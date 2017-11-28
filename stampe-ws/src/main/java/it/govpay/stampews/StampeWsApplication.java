package it.govpay.stampews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.gov.digitpa.schemas._2011.pagamenti.CtRicevutaTelematica;
import it.govpay.model.AvvisoPagamento;
import it.govpay.model.Versamento.Causale;
import it.govpay.stampe.pdf.avvisoPagamento.AvvisoPagamentoPdf;
import it.govpay.stampe.pdf.rt.utils.RicevutaPagamentoUtils;

@SpringBootApplication
@RestController
public class StampeWsApplication {
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(StampeWsApplication.class);

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
		AvvisoPagamentoPdf avvisoPagamentoPdf = new AvvisoPagamentoPdf();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		AvvisoPagamento avviso = PrintAvviso.toAvviso(request);
		String result = avvisoPagamentoPdf.getPdfAvvisoPagamento("logo", avviso, null, os);
		log.info(result);
		return os.toByteArray();
	}

	@PostMapping(path = "/rt/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] printPdfRt(@RequestBody PrintRtRequest r) throws IOException, Exception {
		log.info("Print RT");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CtRicevutaTelematica rt = r.getRt();

		RicevutaPagamentoUtils.getPdfRicevutaPagamento(Base64.decode(r.getLogoHex()), new CausaleSimple(r.getCausale()),
				rt, r.getAuxDigit(), r.getApplicationCode(), out, log);
		return out.toByteArray();
	}

	public static class CausaleSimple implements Causale {
		private final String causale;

		public CausaleSimple(String causale) {
			super();
			this.causale = causale;
		}

		@Override
		public String encode() throws UnsupportedEncodingException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSimple() throws UnsupportedEncodingException {
			// TODO Auto-generated method stub
			return causale;
		}

	}
}
