package it.govpay.stampews;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
@Component
public class QuietanzaReport {
	@Autowired
	ObjectMapper json;
	public void exportToStream(StampaQuietanza input, OutputStream outputStream)
			throws Exception {
		InputStream jasperTemplateInputStream = this.getClass().getResourceAsStream("/Quietanza.jasper");// new
																												// FileInputStream("/home/pintori/Downloads/Jasper_1/AvvisoPagamento.jasper");
		

		// converte l'oggetto in xml
		JRDataSource dataSource = createDataSource(input);
		final Map<String, Object> parameters = new HashMap<>();
		
		// caricaLoghiAvvisoFromProps(parameters, propertiesAvvisoPerDominio);
		parameters.put("ente_logo", AvvisoPagamentoProperties.getImmagineEnte(input.getEnteIndentificativo()));
		parameters.put("ente_area",StringUtils.trimToEmpty( input.getEnteArea()));
		parameters.put("ente_denominazione",StringUtils.trimToEmpty( input.getEnteDenominazione()));
		parameters.put("totale", input.getTotale() != null ? input.getTotale() : BigDecimal.ZERO);
		
		parameters.put("ente_identificativo", StringUtils.trimToEmpty(input.getEnteIndentificativo()));
		parameters.put("intestatario_denominazione",StringUtils.trimToEmpty( input.getIntestatarioDenominazione()));
		parameters.put("intestatario_identificativo",StringUtils.trimToEmpty( input.getIntestatarioIdentificativo()));
		parameters.put("intestatario_indirizzo_1",StringUtils.trimToEmpty( input.getIntestatarioIndirizzo()));
		parameters.put("intestatario_indirizzo_2",StringUtils.trimToEmpty(  input.getIntestatarioArea()));
		// inserisce il logo di defaul(Rep. Italiana)
		JasperPrint jasperPrint = creaJasperPrintAvviso(jasperTemplateInputStream, dataSource, parameters);
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}
	
	public JasperPrint creaJasperPrintAvviso(InputStream jasperTemplateInputStream, JRDataSource dataSource,
			Map<String, Object> parameters) throws Exception {
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperTemplateInputStream);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
		return jasperPrint;
	}
	
	protected JRDataSource createDataSource(StampaQuietanza input) throws JRException, JsonProcessingException {
		String bytes = json.writeValueAsString(input);
		ByteArrayInputStream io = new ByteArrayInputStream(bytes.getBytes());
		
		JsonDataSource dataSource=  new JsonDataSource(io, "causali");
		return dataSource;
	}
}
