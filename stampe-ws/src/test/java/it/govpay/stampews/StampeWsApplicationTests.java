package it.govpay.stampews;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;
import it.govpay.model.RicevutaPagamento;
import it.govpay.stampews.PrintAvviso.Anagrafica;
import it.govpay.stampews.PrintAvviso.Dominio;
import it.govpay.stampews.StampaQuietanza.VoceQuietanza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StampeWsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StampeWsApplicationTests {
	private static JAXBContext jaxbContext;

	private static Schema RPT_RT_schema;

	@BeforeClass
	public static void startEureka() throws JAXBException, SAXException {

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		RPT_RT_schema = schemaFactory.newSchema(
				new StreamSource(CtRicevutaTelematica.class.getResourceAsStream("/xsd/RPT_RT_6_0_2_FR_1_0_4.xsd")));
		jaxbContext = JAXBContext.newInstance(
				"it.gov.digitpa.schemas._2011.pagamenti:it.gov.digitpa.schemas._2011.pagamenti.revoche:it.gov.digitpa.schemas._2011.ws.paa:it.gov.digitpa.schemas._2011.psp:gov.telematici.pagamenti.ws.ppthead:it.govpay.servizi.pa");

	}

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void shouldWritePdfRT() throws InterruptedException, JAXBException, SAXException, FileNotFoundException,
			IOException, URISyntaxException {
		// registration has to take place...
		Thread.sleep(3000);
		RicevutaPagamento ricevuta = new RicevutaPagamento();
		ricevuta.setImportoPagato(BigDecimal.TEN);

		it.govpay.model.Anagrafica cred = new it.govpay.model.Anagrafica();
		cred.setCodUnivoco("Comune di Torino (00514490010)");
		ricevuta.setAnagraficaCreditore(cred);
		it.govpay.model.Anagrafica deb = new it.govpay.model.Anagrafica();
		ricevuta.setAnagraficaDebitore(deb);
		deb.setCodUnivoco("PIRRONE FRANCESCO (CODICE FISCALE)");
		ricevuta.setDataPagamento(new Date());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<RicevutaPagamento> request = new HttpEntity<>(ricevuta, headers);
		ResponseEntity<byte[]> response = this.testRestTemplate
				.postForEntity("http://localhost:" + this.port + "/rt/pdf", request, byte[].class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		try (FileOutputStream out = new FileOutputStream("/tmp/test-rt-2.pdf")) {
			assertNotNull("NULL BODY", response.getBody());
			out.write(response.getBody());
		}
	}

	@Test
	public void shouldWriteAvvisoPdf() throws InterruptedException, JAXBException, SAXException, FileNotFoundException,
			IOException, URISyntaxException {
		// registration has to take place...
		Thread.sleep(3000);
		PrintAvviso print = new PrintAvviso();
		print.setQrCode("1");
		print.setBarCode("2");
		print.setImporto(BigDecimal.TEN);
		print.setIuv("IUVXXX");
		print.setDataScadenza("28/11/2017");
		Anagrafica anagraficaCreditore = new Anagrafica();
		anagraficaCreditore.setLocalita("LOCALITA CREDITORE");
		anagraficaCreditore.setIndirizzo("VIA CREDITORE");
		anagraficaCreditore.setCap("CAP CREDITORE");
		anagraficaCreditore.setRagioneSociale("RAGIONE SOCIALE CRED");
		anagraficaCreditore.setCodUnivoco("CODICE UNIVOCO CRED");
		print.setAnagraficaCreditore(anagraficaCreditore);

		Anagrafica anagraficaDebitore = new Anagrafica();
		anagraficaDebitore.setLocalita("TORINO");
		anagraficaDebitore.setRagioneSociale("RAGIONE SOCIALE DEB");
		anagraficaDebitore.setCodUnivoco("CODICE UNIVOCO DEB");
		anagraficaDebitore.setIndirizzo("Via Alfieri 17");
		anagraficaDebitore.setCap("10121 ");
		print.setAnagraficaDebitore(anagraficaDebitore);

		Dominio dominioCreditore = new Dominio();
		dominioCreditore.setRagioneSociale("Comune di Torino");
		dominioCreditore.setCodDominio("00514490010");

		print.setDominioCreditore(dominioCreditore);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<PrintAvviso> request = new HttpEntity<>(print, headers);
		String url = "http://localhost:" + this.port + "/avviso/pdf";
		ResponseEntity<byte[]> response = this.testRestTemplate.postForEntity(url, request, byte[].class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		try (FileOutputStream out = new FileOutputStream("/tmp/test-avviso.pdf")) {
			assertNotNull("NULL BODY", response.getBody());
			out.write(response.getBody());
		}
	}

	@Test
	public void testShouldWriteQuietanza() throws FileNotFoundException, IOException {
		StampaQuietanza request = new StampaQuietanza();
		
		request.setEnteArea("Comune di Torino");
		request.setEnteDenominazione("Anagrafe Centrale");
		request.setEnteIndentificativo("00514490010");
		request.setTotale(BigDecimal.TEN);
		request.setIntestatarioDenominazione("NOME INTESTATARIO");
		request.setIntestatarioIdentificativo("CODICE_FISCALE");
		request.setIntestatarioIndirizzo("VIA ....");
		VoceQuietanza a = new VoceQuietanza();
		a.setCausale("Causale 1");
		a.setDescrizione("Descrizione Esempio 1");
		a.setImporto(BigDecimal.TEN);
		request.getCausali().add(a);
		request.getCausali().add(a);
		request.getCausali().add(a);
		request.getCausali().add(a);
		ResponseEntity<byte[]> response = this.testRestTemplate
				.postForEntity("http://localhost:" + this.port + "/quietanza/pdf", request, byte[].class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		try (FileOutputStream out = new FileOutputStream("/tmp/test-quietanza.pdf")) {
			assertNotNull("NULL BODY", response.getBody());
			out.write(response.getBody());
		}
	}

}