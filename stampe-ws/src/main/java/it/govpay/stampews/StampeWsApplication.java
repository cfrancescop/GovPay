package it.govpay.stampews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.govpay.model.RicevutaPagamento;
import it.govpay.model.avvisi.AvvisoPagamentoInput;
import it.govpay.stampe.pdf.avvisoPagamento.AvvisoPagamentoPdf;
import it.govpay.stampe.pdf.rt.IRicevutaPagamento;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoProperties;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoUtils;

@SpringBootConfiguration
@ComponentScan(basePackageClasses=StampeWsApplication.class)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,FreeMarkerAutoConfiguration.class})
//@SpringBootApplication
@RestController

public class StampeWsApplication {
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(StampeWsApplication.class);
	@Autowired AvvisoPagamentoPdf avvisoPagamentoPdf;
	@Autowired
	IRicevutaPagamento iRicevutaPagamento;
	@Autowired
	RicevutaPagamentoProperties ricevutaPagamentoProperties;
	@Autowired
	QuietanzaReport quietanzaReport;
	public static void main(String[] args) {
		try {
			SpringApplication.run(StampeWsApplication.class, args);
		} catch (RuntimeException e) {
			log.error("Errror during start application", e);
		}

	}

	@Autowired(required = false)
	private DiscoveryClient discoveryClient;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@PostMapping(path = "/avviso/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] printPdfAvviso(@RequestBody PrintAvviso request) throws IOException, Exception {
		log.info("Print avviso di pagamento");
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		AvvisoPagamentoInput input = PrintAvviso.toAvviso(request);
		avvisoPagamentoPdf.exportToStream(input, request.dominioCreditore.codDominio, outputStream);
		return outputStream.toByteArray();
	}

	@PostMapping(path = "/rt/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] printPdfRt(@RequestBody RicevutaPagamento ricevuta) throws IOException, Exception {
		log.info("Print RT");
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		Properties propertiesAvvisoPagamentoDominioTributo = RicevutaPagamentoUtils.getRicevutaPagamentoPropertiesPerDominioTributo(ricevutaPagamentoProperties, ricevuta.getCodDominio(), log);
		iRicevutaPagamento.getPdfRicevutaPagamento(ricevuta, propertiesAvvisoPagamentoDominioTributo, os, log);
		return os.toByteArray();
	}
	
	@PostMapping(path = "/quietanza/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] printPdfQuietanza(@RequestBody StampaQuietanza quietanza) throws IOException, Exception {
		log.info("Print Quietanza");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		quietanzaReport.exportToStream(quietanza, os);
		return os.toByteArray();
	}

	
}
