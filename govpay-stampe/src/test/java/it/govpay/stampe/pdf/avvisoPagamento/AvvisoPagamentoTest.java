package it.govpay.stampe.pdf.avvisoPagamento;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.zxing.common.BitMatrix;

import it.govpay.model.Anagrafica;
import it.govpay.model.avvisi.AvvisoPagamento;
import it.govpay.model.avvisi.AvvisoPagamentoInput;
import it.govpay.model.Dominio;
import it.govpay.model.avvisi.AvvisoPagamentoInput;
import it.govpay.stampe.pdf.avvisoPagamento.utils.AvvisoPagamentoProperties;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

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
	public void testStampa() throws Exception {
		try (FileOutputStream outputStream = new FileOutputStream("/tmp/tmp2.pdf")) {
			instance.exportToStream(createInput(), "83000390019", outputStream);
		}

	}

	@Test
	public void test() throws Exception {

		AvvisoPagamento av = new AvvisoPagamento();
		av.setCodDominio("83000390019");

		InputStream jasperTemplateInputStream = this.getClass().getResourceAsStream("/AvvisoPagamento.jasper");// new
																												// FileInputStream("/home/pintori/Downloads/Jasper_1/AvvisoPagamento.jasper");
		final Map<String, Object> parameters = new HashMap<>();
		AvvisoPagamentoInput input = createInput();
		Properties propertiesAvvisoPerDominio;
		propertiesAvvisoPerDominio = avProperties.getPropertiesPerDominio(av.getCodDominio(), log);
		instance.caricaLoghiAvviso(input, propertiesAvvisoPerDominio);
		JRDataSource dataSource = instance.creaXmlDataSource(input);
		BitMatrix matrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
				input.getAvvisoQrcode(), com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);
		BufferedImage bufferedImage = com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(matrix);
		parameters.put("qr_code", bufferedImage);
		// System.out.println(bufferedImage);
		JasperPrint jasperPrint = instance.creaJasperPrintAvviso(jasperTemplateInputStream, dataSource, parameters);
		// JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
		JasperExportManager.exportReportToPdfFile(jasperPrint, "/tmp/tmp.pdf");

		// System.out.println(input.toXml_Jaxb());
		System.out.println("FINE");
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
		input.setAvvisoImporto(9999999.99);
		input.setAvvisoScadenza("31/12/2020");
		input.setAvvisoNumero("399000012345678900");
		input.setAvvisoIuv("99000012345678900");
		input.setAvvisoBarcode("415808888880094580203990000123456789003902222250");
		input.setAvvisoQrcode("PAGOPA|002|399000012345678900|83000390019|222250");
		return input;
	}
}
