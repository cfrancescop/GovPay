package it.govpay.stampe.pdf.avvisoPagamento;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import it.govpay.model.avvisi.AvvisoPagamentoInput;
import it.govpay.stampe.pdf.MissingConfigurationException;
import it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties;

public class AvvisoPagamentoTest {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger();
	static AvvisoPagamentoProperties avProperties;

	static AvvisoPagamentoPdf instance;

	@BeforeClass
	public static void setup() throws Exception {
		avProperties = AvvisoPagamentoProperties.newInstance("/var/govpay");

		instance = AvvisoPagamentoPdf.getInstance();
	}

	@Test
	public void testStampaAvviso() throws Exception {
		try (FileOutputStream outputStream = new FileOutputStream("/tmp/avvisoPagoPA.pdf")) {
			instance.exportToStream(createInput(), "83000390019", outputStream);
		}

	}

	@Test
	public void testLogiImage() throws UnsupportedEncodingException, MissingConfigurationException {
		Map<String,Object> parameters = new HashMap<>();
		
		instance.caricaLoghiAvvisoFromProps(parameters, avProperties.getPropertiesPerDominio(null));
		parameters.entrySet().forEach(entry -> {
			InputStream is = (InputStream) entry.getValue();
			String name = entry.getKey();
			try {
				Files.copy(is, Paths.get("/tmp",name+".png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	

	private AvvisoPagamentoInput createInput() {
		AvvisoPagamentoInput input = new AvvisoPagamentoInput();
		// AvvisoPagamentoPdf.getInstance().caricaLoghiAvviso(input,
		// propertiesAvvisoPerDominio);

		input.setEnteDenominazione("Comune di San Valentino in Abruzzo Citeriore");
		input.setEnteArea("Area di sviluppo per le politiche agricole e forestali");
		input.setEnteIdentificativo("83000390019");
		input.setEnteCbill("AAAAAAA");
		input.setEnteUrl("www.comune.sanciprianopicentino.sa.it/");
		input.setEntePeo("info@comune.sancipriano.sa.it");
		input.setEntePec("protocollo@pec.comune.sanciprianopicentino.sa.it");
		input.setEntePartner("Link.it Srl");
		input.setIntestatarioDenominazione("Lorenzo Nardi");
		input.setIntestatarioIdentificativo("NRDLNA80P19D612M");
		input.setIntestatarioIndirizzo1("Via di Corniola 119A");
		input.setIntestatarioIndirizzo2("50053 Empoli (FI)");
		input.setAvvisoCausale(
				"Pagamento diritti di segreteria per il rilascio in duplice copia della documentazione richiesta.");
		input.setAvvisoImporto(new BigDecimal("9999.99"));
		input.setAvvisoScadenza("31/12/2020");
		input.setAvvisoNumero("399000012345678900");
		input.setAvvisoIuv("99000012345678900");
		input.setAvvisoBarcode("415808888880094580203990000123456789003902222250");
		input.setAvvisoQrcode("PAGOPA|002|399000012345678900|83000390019|222250");
		return input;
	}
}
