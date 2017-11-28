package it.govpay.stampews;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;

import javax.validation.constraints.NotNull;

import it.govpay.model.AvvisoPagamento;

public class PrintAvviso {
	public String getHexQrCode() {
		return hexQrCode;
	}

	public void setHexQrCode(String hexQrCode) {
		this.hexQrCode = hexQrCode;
	}

	public String getHexBarCode() {
		return hexBarCode;
	}

	public void setHexBarCode(String hexBarCode) {
		this.hexBarCode = hexBarCode;
	}

	public Dominio getDominioCreditore() {
		return dominioCreditore;
	}

	public void setDominioCreditore(Dominio dominioCreditore) {
		this.dominioCreditore = dominioCreditore;
	}

	public Anagrafica getAnagraficaCreditore() {
		return anagraficaCreditore;
	}

	public void setAnagraficaCreditore(Anagrafica anagraficaCreditore) {
		this.anagraficaCreditore = anagraficaCreditore;
	}

	public String getCodiceAvviso() {
		return codiceAvviso;
	}

	public void setCodiceAvviso(String codiceAvviso) {
		this.codiceAvviso = codiceAvviso;
	}

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public String getIuv() {
		return iuv;
	}

	public void setIuv(String iuv) {
		this.iuv = iuv;
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public Anagrafica getAnagraficaDebitore() {
		return anagraficaDebitore;
	}

	public void setAnagraficaDebitore(Anagrafica anagraficaDebitore) {
		this.anagraficaDebitore = anagraficaDebitore;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}

	public String getCodVersamento() {
		return codVersamento;
	}

	public void setCodVersamento(String codVersamento) {
		this.codVersamento = codVersamento;
	}

	public static class Dominio {
		public String getCodDominio() {
			return codDominio;
		}
		public void setCodDominio(String codDominio) {
			this.codDominio = codDominio;
		}
		
		
		@NotNull
		String codDominio;
		@NotNull 
		String ragioneSociale;
		public String getRagioneSociale() {
			return ragioneSociale;
		}
		public void setRagioneSociale(String ragioneSociale) {
			this.ragioneSociale = ragioneSociale;
		}
	}

	public static class Anagrafica {
		@NotNull
		String codUnivoco;
		public String getCodUnivoco() {
			return codUnivoco;
		}
		public void setCodUnivoco(String codUnivoco) {
			this.codUnivoco = codUnivoco;
		}
		public String getRagioneSociale() {
			return ragioneSociale;
		}
		public void setRagioneSociale(String ragioneSociale) {
			this.ragioneSociale = ragioneSociale;
		}
		public String getIndirizzo() {
			return indirizzo;
		}
		public void setIndirizzo(String indirizzo) {
			this.indirizzo = indirizzo;
		}
		public String getCap() {
			return cap;
		}
		public void setCap(String cap) {
			this.cap = cap;
		}
		public String getLocalita() {
			return localita;
		}
		public void setLocalita(String localita) {
			this.localita = localita;
		}
		@NotNull
		String ragioneSociale;
		String indirizzo;
		String cap;
		String localita;
	}
	@NotNull
	String hexQrCode;
	@NotNull
	String hexBarCode;
	@NotNull
	Dominio dominioCreditore;
	@NotNull
	Anagrafica anagraficaCreditore;
	String codiceAvviso;
	Date dataScadenza;
	@NotNull
	String iuv;
	@NotNull
	BigDecimal importo;
	@NotNull
	Anagrafica anagraficaDebitore;
	String causale;
	String codVersamento;

	public static AvvisoPagamento toAvviso(PrintAvviso r) {
		AvvisoPagamento a = new AvvisoPagamento();
		a.setAnagraficaCreditore(toAnagrafica(r.anagraficaCreditore));
		a.setImporto(r.importo);
		a.setBarCode(Base64.getDecoder().decode(r.hexBarCode));
		a.setQrCode(Base64.getDecoder().decode(r.hexQrCode));
		a.setAnagraficaDebitore(toAnagrafica(r.anagraficaDebitore));
		a.setDominioCreditore(toDominio(r.dominioCreditore));
		a.setDataScadenza(r.dataScadenza);
		a.setIuv(r.iuv);
		a.setCodiceAvviso(r.codiceAvviso);
		a.setCodVersamento(r.codVersamento);
		a.setCausale(r.causale);
		return a;
	}

	private static it.govpay.model.Dominio toDominio(Dominio d2) {
		it.govpay.model.Dominio d = new it.govpay.model.Dominio();
		d.setCodDominio(d2.codDominio);
		d.setRagioneSociale(d2.getRagioneSociale());
//		d.setLogo(Base64.getDecoder().decode(d2.hexLogo));
		return d;
	}

	private static it.govpay.model.Anagrafica toAnagrafica(Anagrafica a2) {
		it.govpay.model.Anagrafica a = new it.govpay.model.Anagrafica();
		a.setCodUnivoco(a2.codUnivoco);
		a.setIndirizzo(a2.indirizzo);
		a.setRagioneSociale(a2.ragioneSociale);
		a.setCap(a2.cap);
		a.setLocalita(a2.localita);
		return a;
	}
}
