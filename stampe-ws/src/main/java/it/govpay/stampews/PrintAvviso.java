package it.govpay.stampews;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import it.govpay.model.avvisi.AvvisoPagamentoInput;

public class PrintAvviso {
	@NotNull
	String barCode;
	@NotNull
	String qrCode;
	@NotNull
	Dominio dominioCreditore;
	@NotNull
	Anagrafica anagraficaCreditore;
	String codiceAvviso;
	String dataScadenza;
	@NotNull
	String iuv;
	@NotNull
	BigDecimal importo;
	@NotNull
	Anagrafica anagraficaDebitore;
	String causale;
	String codVersamento;
	String numeroAvviso;

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getNumeroAvviso() {
		return numeroAvviso;
	}

	public void setNumeroAvviso(String numeroAvviso) {
		this.numeroAvviso = numeroAvviso;
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

	public String getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(String dataScadenza) {
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

	public static AvvisoPagamentoInput toAvviso(PrintAvviso r) {
		AvvisoPagamentoInput a = new AvvisoPagamentoInput();
		// Comune di San Valentino in Abruzzo Citeriore
		a.setEnteDenominazione(r.getDominioCreditore().ragioneSociale);
		// Area di sviluppo per le politiche agricole e forestali
		a.setEnteArea(r.anagraficaCreditore.ragioneSociale);
		// 83000390019
		a.setEnteIdentificativo(r.dominioCreditore.codDominio);
		// TODO
		a.setEnteCbill(r.anagraficaCreditore.cbill);
		// TODO
		a.setEnteUrl(r.anagraficaCreditore.url);
		// TODO
		a.setEntePeo(r.anagraficaCreditore.email);
		// TODO
		a.setEntePec(r.anagraficaCreditore.pec);
		// TODO
		a.setEntePartner(r.anagraficaCreditore.partner);
		// Lorenzo Nardi
		a.setIntestatarioDenominazione(r.anagraficaDebitore.ragioneSociale);
		// CODICE FISCALE
		a.setIntestatarioIdentificativo(r.anagraficaDebitore.codUnivoco);
		a.setIntestatarioIndirizzo1(r.anagraficaDebitore.indirizzo);
		Anagrafica deb =  r.anagraficaDebitore;
		String indirizzo = "";
		if(deb.cap != null) {
			indirizzo += deb.cap+' ';
		}
		if(deb.localita!=null) {
			indirizzo += deb.localita +' ';
		}
		if(deb.provincia != null) {
			indirizzo += '('+deb.provincia+')';
		}
		a.setIntestatarioIndirizzo2(indirizzo);
		a.setAvvisoCausale(r.causale);
		a.setAvvisoImporto(r.importo);
		a.setAvvisoScadenza(r.dataScadenza);
		a.setAvvisoNumero(r.numeroAvviso);
		a.setAvvisoIuv(r.iuv);
		a.setAvvisoQrcode(r.qrCode);
		a.setAvvisoBarcode(r.barCode);
		return a;
	}

	public static class Dominio {

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

		public String getCodDominio() {
			return codDominio;
		}

		public void setCodDominio(String codDominio) {
			this.codDominio = codDominio;
		}
	}

	public static class Anagrafica {
		@NotNull
		String codUnivoco;

		@NotNull
		String ragioneSociale;
		String indirizzo;
		String cap;
		String localita;
		String url;
		String pec;
		String email;
		String partner;
		String cbill;
		String provincia;
		public String getProvincia() {
			return provincia;
		}

		public void setProvincia(String provincia) {
			this.provincia = provincia;
		}

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
	}

}
