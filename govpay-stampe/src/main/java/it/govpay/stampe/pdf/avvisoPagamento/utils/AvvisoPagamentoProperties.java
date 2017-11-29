package it.govpay.stampe.pdf.avvisoPagamento.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openspcoop2.utils.Utilities;

import it.govpay.stampe.pdf.MissingConfigurationException;
import it.govpay.stampe.pdf.avvisoPagamento.AvvisoPagamentoCostanti;

public class AvvisoPagamentoProperties {

	private static final String PROPERTIES_FILE = "/avvisoPagamento.properties";
	public static final String DEFAULT_PROPS = "default";

	private static AvvisoPagamentoProperties instance;

	private static final Logger log = LogManager.getLogger(AvvisoPagamentoProperties.class);

	private final String govpayResourceDir;

	private final Map<String, Properties> propMap = new HashMap<>();

	public static AvvisoPagamentoProperties getInstance() {
		return instance;
	}

	public static AvvisoPagamentoProperties newInstance(String govpayResourceDir) throws Exception {
		instance = new AvvisoPagamentoProperties(govpayResourceDir);
		return instance;
	}

	public static InputStream getImmagineEnte(String codDominio) {
		String dir = instance.govpayResourceDir;
		
		Path path =  Paths.get(dir, "logo",codDominio+".png");
		try {
			return Files.newInputStream(path);
		} catch (IOException e) {
			log.warn("Nessun file di logo per:"+codDominio+" utilizzo il default");
			try {
				String encoded =  instance.getPropertiesPerDominio(null).getProperty(AvvisoPagamentoCostanti.LOGO_ENTE);
				return toImage(encoded);
			} catch (UnsupportedEncodingException | MissingConfigurationException e1) {
				throw new IllegalStateException(e);
			}
		}
	}
	public static InputStream toImage(String pagopa_logo) throws UnsupportedEncodingException  {
		return new ByteArrayInputStream(stringToImage(pagopa_logo));
	}

	public static byte[] stringToImage(String pagopa_logo) throws UnsupportedEncodingException  {
		return Base64.decodeBase64(((pagopa_logo.contains(",")) ?pagopa_logo.split(",")[1] : pagopa_logo).getBytes("UTF-8"));
	}
	public AvvisoPagamentoProperties(String govpayResourceDir) {
		this.govpayResourceDir = govpayResourceDir;
		try {

			// Recupero il property all'interno dell'EAR/WAR
			final InputStream is = AvvisoPagamentoProperties.class.getResourceAsStream(PROPERTIES_FILE);
			final Properties props1 = new Properties();
			props1.load(is);

			propMap.put(DEFAULT_PROPS, props1);

			final Properties props0;
			

			// Recupero la configurazione della working dir
			// Se e' configurata, la uso come prioritaria

			try {
				

				if (this.govpayResourceDir != null) {
					File resourceDirFile = new File(this.govpayResourceDir);
					if (!resourceDirFile.isDirectory())
						throw new Exception("Il path passato come paramtero (" + this.govpayResourceDir
								+ ") non esiste o non e' un folder.");

					File gpConfigFile = new File(this.govpayResourceDir + PROPERTIES_FILE);
					if (gpConfigFile.exists()) {
						props0 = new Properties();
						props0.load(new FileInputStream(gpConfigFile));
						propMap.put(DEFAULT_PROPS, props0);
						log.info("Individuata configurazione prioritaria: " + gpConfigFile.getAbsolutePath());
					}
				}
			} catch (IOException e) {
				log.warn("Errore di inizializzazione: " + e.getMessage() + ". Property ignorata.",e);
			}

			// carico tutti i file che definiscono configurazioni diverse di avvisi
			// pagamento
			if (this.govpayResourceDir != null) {
				File resourceDirFile = new File(this.govpayResourceDir);
				for (File f : resourceDirFile.listFiles()) {
					if (!f.getName().startsWith("avvisoPagamento") && !f.getName().endsWith("properties")) {
						// Non e' un file di properties. lo salto
						continue;
					}
					final Properties p = new Properties();
					p.load(new FileInputStream(f));
					String key = f.getName().replaceAll(".properties", "");
					key = key.replaceAll("avvisoPagamento.", "");
					// la configurazione di defaut e' gia'stata caricata
					if (!key.equals("avvisoPagamento")) {
						log.info("Caricata configurazione avviso di pagamento con chiave " + key);
						propMap.put(key, p);
					}
				}
			}
		} catch (Exception e) {
			log.warn("Errore di inizializzazione " + e.getMessage() + ". Impostati valori di default.");
		}
	}

	private static String getProperty(String name, Properties props, boolean required) throws Exception {
		String value = System.getProperty(name);

		if (value == null) {
			if (props != null)
				value = props.getProperty(name);
			if (value == null) {
				if (required)
					throw new Exception("Proprieta [" + name + "] non trovata");
				else
					return null;
			} else {
				log.debug("Letta proprieta di configurazione " + name + ": " + value);
			}
		} else {
			log.debug("Letta proprieta di sistema " + name + ": " + value);
		}

		return value.trim();
	}

	public String getProperty(String idprops, String name, boolean required) throws MissingConfigurationException {
		String value = null;
		Properties p = getProperties(idprops);

		try {
			value = getProperty(name, p, required);
		} catch (Exception e) {
		}
		if (value != null && !value.trim().isEmpty()) {
			return value;
		}

		log.debug("Proprieta " + name + " non trovata in configurazione [" + idprops + "]");

		if (required)
			throw new MissingConfigurationException(
					"Proprieta [" + name + "] non trovata in configurazione [" + idprops + "]");
		else
			return null;
	}

	public String getPropertyEnte(String idprop, String name) throws MissingConfigurationException {
		String property = getProperty(idprop, name, false);
		return property != null ? property : "";
	}

	public Properties getProperties(String id) throws MissingConfigurationException {
		if (id == null) {
			id = DEFAULT_PROPS;
		}
		Properties p = propMap.get(id);

		if (p == null) {
			log.debug("Configurazione [" + id + "] non trovata");
			throw new MissingConfigurationException("Configurazione [" + id + "] non trovata");
		}

		return p;
	}

	public Properties getProperties(Properties props, String prefix) throws Exception {
		Properties toRet = Utilities.readProperties(prefix + ".", props);
		return toRet;
	}

	public TreeMap<String, String> getPropertiesAsMap(Properties props, String prefix) throws Exception {
		TreeMap<String, String> mappaProperties = new TreeMap<String, String>();

		Properties p = getProperties(props, prefix);

		for (Object pKeyObj : p.keySet()) {
			Object pValObj = p.get(pKeyObj);
			mappaProperties.put((String) pKeyObj, (String) pValObj);
		}

		return mappaProperties;
	}

	public Properties getPropertiesPerDominio(String codDominio) throws MissingConfigurationException {
		return getPropertiesPerDominioTributo(codDominio, null);
	}

	/**
	 * 
	 * @param codDominio
	 * @param codTributo
	 * @param log
	 * @return
	 * @throws MissingConfigurationException
	 * @throws Exception
	 */
	public Properties getPropertiesPerDominioTributo(String codDominio, String codTributo)
			throws MissingConfigurationException {
		String key = null;

		// 1. ricerca delle properties per la chiave "codDominio.codTributo";
		if (StringUtils.isNotEmpty(codTributo) && StringUtils.isNotEmpty(codDominio)) {
			key = codDominio + "." + codTributo;

			log.debug("Ricerca delle properties per la chiave [" + key + "]");
			return this.getProperties(key);

		}

		// 2 . ricerca per codDominio
		if (StringUtils.isNotEmpty(codDominio)) {

			key = codDominio;

			log.debug("Ricerca delle properties per la chiave [" + key + "]");
			return this.getProperties(key);

		}

		// utilizzo le properties di default

		log.debug("Ricerca delle properties di default");
		return this.getProperties(DEFAULT_PROPS);

	}

}
