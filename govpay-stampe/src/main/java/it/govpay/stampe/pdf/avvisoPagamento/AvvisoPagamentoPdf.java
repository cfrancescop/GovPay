package it.govpay.stampe.pdf.avvisoPagamento;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import it.govpay.model.avvisi.AvvisoPagamento;
import it.govpay.model.avvisi.AvvisoPagamentoInput;
import it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import static it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties.toImage;

public class AvvisoPagamentoPdf {

	private static AvvisoPagamentoPdf _instance = null;
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AvvisoPagamentoPdf.class);

	public static AvvisoPagamentoPdf getInstance() {
		if (_instance == null)
			init();

		return _instance;
	}

	public static synchronized void init() {
		if (_instance == null)
			_instance = new AvvisoPagamentoPdf();
	}

	private AvvisoPagamentoPdf() {
		// puo essere inizializzato solo tramite init()
	}

	public void exportToStream(AvvisoPagamentoInput input, String codDominio, OutputStream outputStream)
			throws Exception {
		InputStream jasperTemplateInputStream = this.getClass().getResourceAsStream("/AvvisoPagamento.jasper");// new
																												// FileInputStream("/home/pintori/Downloads/Jasper_1/AvvisoPagamento.jasper");
		

		// converte l'oggetto in xml
		JRDataSource dataSource = creaXmlDataSource(input);
		final Map<String, Object> parameters = new HashMap<>();
		BufferedImage bufferedImage = getQrCode(input);
		parameters.put("qr_code", bufferedImage);
		// caricaLoghiAvvisoFromProps(parameters, propertiesAvvisoPerDominio);
		parameters.put("ente_logo", AvvisoPagamentoProperties.getImmagineEnte(codDominio)); // CARICA IL LOGO dalla cartella
																					// /var/govpay/logo/{codDominio}.png
																					// se non esiste
		// inserisce il logo di defaul(Rep. Italiana)
		JasperPrint jasperPrint = creaJasperPrintAvviso(jasperTemplateInputStream, dataSource, parameters);
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}

	private BufferedImage getQrCode(AvvisoPagamentoInput input) throws WriterException {
		if (input.getAvvisoQrcode() == null) {
			throw new IllegalArgumentException("QR must not be null");
		}
		Map<EncodeHintType, Object> hints = new HashMap<>();

		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

		hints.put(EncodeHintType.MARGIN, Integer.valueOf(3));

		BitMatrix matrix = new com.google.zxing.qrcode.QRCodeWriter().encode(input.getAvvisoQrcode(),
				com.google.zxing.BarcodeFormat.QR_CODE, 300, 300, hints);
		BufferedImage bufferedImage = com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(matrix);
		return bufferedImage;
	}

	public JasperPrint creaJasperPrintAvviso(InputStream jasperTemplateInputStream, JRDataSource dataSource,
			Map<String, Object> parameters) throws Exception {
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperTemplateInputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		return jasperPrint;
	}

	public AvvisoPagamento creaAvviso(Logger log, AvvisoPagamentoInput input, AvvisoPagamento avvisoPagamento,
			AvvisoPagamentoProperties avProperties) throws Exception {
		// cerco file di properties esterni per configurazioni specifiche per dominio
		// String codDominio = avvisoPagamento.getCodDominio();
		Properties propertiesAvvisoPerDominio = avProperties.getPropertiesPerDominio(null);

		// leggo il template file jasper da inizializzare
		String jasperTemplateFilename = propertiesAvvisoPerDominio
				.getProperty(AvvisoPagamentoCostanti.AVVISO_PAGAMENTO_TEMPLATE_JASPER);

		if (!jasperTemplateFilename.startsWith("/"))
			jasperTemplateFilename = "/" + jasperTemplateFilename;

		InputStream is = AvvisoPagamentoPdf.class.getResourceAsStream(jasperTemplateFilename);
		final Map<String, Object> parameters = new HashMap<>();
		BufferedImage bufferedImage = getQrCode(input);
		parameters.put("qr_code", bufferedImage);
		// caricaLoghiAvvisoFromProps(parameters, propertiesAvvisoPerDominio);
		parameters.put("ente_logo", AvvisoPagamentoProperties.getImmagineEnte(avvisoPagamento.getCodDominio())); 
		
		// caricaLoghiAvvisoFromProps(parameters, propertiesAvvisoPerDominio);
		JRDataSource dataSource = creaXmlDataSource(input);
		JasperPrint jasperPrint = creaJasperPrintAvviso(is, dataSource, parameters);

		byte[] reportToPdf = JasperExportManager.exportReportToPdf(jasperPrint);
		avvisoPagamento.setPdf(reportToPdf);
		return avvisoPagamento;
	}

	/**
	 * 
	 * @param log
	 * @param input
	 * @return
	 * @throws UtilsException
	 * @throws JRException
	 */
	public JRDataSource creaXmlDataSource(AvvisoPagamentoInput input) throws UtilsException, JRException {
		WriteToSerializerType serType = WriteToSerializerType.JAXB;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		input.writeTo(os, serType);
		byte[] byteArray = os.toByteArray();
		return new JRXmlDataSource(new ByteArrayInputStream(byteArray),
				AvvisoPagamentoCostanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME);
		// return dataSource;
	}

	public JRDataSource creaCustomDataSource(AvvisoPagamentoInput input) throws UtilsException, JRException {
		List<AvvisoPagamentoInput> listaAvvisi = new ArrayList<AvvisoPagamentoInput>();
		listaAvvisi.add(input);
		JRDataSource dataSource = new AvvisoPagamentoDatasource(listaAvvisi, log);
		return dataSource;
	}

	/**
	 * TODO convertire ad immagini caricare da resource ed inserite nei parametri
	 * 
	 * @param input
	 * @param propertiesAvvisoPerDominio
	 * @throws UnsupportedEncodingException
	 */
	protected void caricaLoghiAvvisoFromProps(Map<String, Object> parameters, Properties propertiesAvvisoPerDominio)
			throws UnsupportedEncodingException {
		// valorizzo la sezione loghi
		// LOGO COMUNE/REGIONE/STATO
		//
		parameters.put("ente_logo", toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_ENTE)));
		// LOGO DI AGID
		parameters.put("agid_logo", toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_AGID)));
		// LOGO PAGOPA
		String pagopa_logo = propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PAGOPA);

		parameters.put("pagopa_logo", toImage(pagopa_logo));
		//
		parameters.put("pagopa90_logo",
				toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PAGOPA_90)));
		//
		parameters.put("app_logo", toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_APP)));

		parameters.put("place_logo",
				toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PLACE)));
		// CALCOLATRICE
		parameters.put("importo_logo",
				toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_IMPORTO)));
		// CALENDARIO
		parameters.put("scadenza_logo",
				toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_SCADENZA)));
		// FORBICI
		parameters.put("taglio", toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_TAGLIO)));
		parameters.put("taglio1",
				toImage(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_TAGLIO1)));
	}

}
