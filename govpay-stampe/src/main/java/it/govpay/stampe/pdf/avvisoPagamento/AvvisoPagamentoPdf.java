package it.govpay.stampe.pdf.avvisoPagamento;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.beans.WriteToSerializerType;

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

public class AvvisoPagamentoPdf {

	private static AvvisoPagamentoPdf _instance = null;

	public static AvvisoPagamentoPdf getInstance() {
		if(_instance == null)
			init();

		return _instance;
	}

	public static synchronized void init() {
		if(_instance == null)
			_instance = new AvvisoPagamentoPdf();
	}

	public AvvisoPagamentoPdf() {

	}
	
	
	public JasperPrint creaJasperPrintAvviso(InputStream jasperTemplateInputStream,
			JRDataSource dataSource,Map<String, Object> parameters) throws Exception {
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperTemplateInputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, dataSource);
		return jasperPrint;
	}
	
	public AvvisoPagamento creaAvviso(Logger log, AvvisoPagamentoInput input, AvvisoPagamento avvisoPagamento, AvvisoPagamentoProperties avProperties) throws Exception {
		// cerco file di properties esterni per configurazioni specifiche per dominio
		String codDominio = avvisoPagamento.getCodDominio();
		Properties propertiesAvvisoPerDominio = avProperties.getPropertiesPerDominio(codDominio, log);
		
		caricaLoghiAvviso(input, propertiesAvvisoPerDominio);

		// leggo il template file jasper da inizializzare
		String jasperTemplateFilename = propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.AVVISO_PAGAMENTO_TEMPLATE_JASPER);

		if(!jasperTemplateFilename.startsWith("/"))
			jasperTemplateFilename = "/" + jasperTemplateFilename; 

		InputStream is = AvvisoPagamentoPdf.class.getResourceAsStream(jasperTemplateFilename);
		Map<String, Object> parameters = new HashMap<>();
		JRDataSource dataSource = creaXmlDataSource(log,input);
		JasperPrint jasperPrint = creaJasperPrintAvviso( is, dataSource,parameters);

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
	public JRDataSource creaXmlDataSource(Logger log,AvvisoPagamentoInput input) throws UtilsException, JRException {
		WriteToSerializerType serType = WriteToSerializerType.JAXB;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		input.writeTo(os, serType);
		Object[] params = {input};
		//return new net.sf.jasperreports.engine.data.JRBeanArrayDataSource(params);
		byte[] byteArray = os.toByteArray();
		System.out.println(new String(byteArray));
		return new JRXmlDataSource(new ByteArrayInputStream(byteArray),AvvisoPagamentoCostanti.AVVISO_PAGAMENTO_ROOT_ELEMENT_NAME);
		//return dataSource;
	}
	
	public JRDataSource creaCustomDataSource(Logger log,AvvisoPagamentoInput input) throws UtilsException, JRException {
		List<AvvisoPagamentoInput> listaAvvisi = new ArrayList<AvvisoPagamentoInput>();
		listaAvvisi.add(input);
		JRDataSource dataSource = new AvvisoPagamentoDatasource(listaAvvisi,log);
		return dataSource;
	}
	
	public void caricaLoghiAvviso(AvvisoPagamentoInput input, Properties propertiesAvvisoPerDominio) {
		// valorizzo la sezione loghi
		input.setEnteLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_ENTE));
		input.setAgidLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_AGID));
		input.setPagopaLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PAGOPA));
		input.setPagopa90Logo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PAGOPA_90));
		input.setAppLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_APP));
		input.setPlaceLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_PLACE));
		input.setImportoLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_IMPORTO));
		input.setScadenzaLogo(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_SCADENZA));
		input.setTaglio(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_TAGLIO));
		input.setTaglio1(propertiesAvvisoPerDominio.getProperty(AvvisoPagamentoCostanti.LOGO_TAGLIO1));
	}


	
}
