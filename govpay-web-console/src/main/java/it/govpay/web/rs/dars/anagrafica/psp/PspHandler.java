/*
 * GovPay - Porta di Accesso al Nodo dei Pagamenti SPC 
 * http://www.gov4j.it/govpay
 * 
 * Copyright (c) 2014-2017 Link.it srl (http://www.link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.govpay.web.rs.dars.anagrafica.psp;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.openspcoop2.generic_project.expression.SortOrder;

import it.govpay.bd.BasicBD;
import it.govpay.bd.FilterSortWrapper;
import it.govpay.bd.anagrafica.filters.PspFilter;
import it.govpay.bd.model.Psp;
import it.govpay.web.rs.dars.base.DarsHandler;
import it.govpay.web.rs.dars.base.DarsService;
import it.govpay.web.rs.dars.exception.ConsoleException;
import it.govpay.web.rs.dars.exception.DeleteException;
import it.govpay.web.rs.dars.exception.DuplicatedEntryException;
import it.govpay.web.rs.dars.exception.ExportException;
import it.govpay.web.rs.dars.exception.ValidationException;
import it.govpay.web.rs.dars.handler.IDarsHandler;
import it.govpay.web.rs.dars.model.Dettaglio;
import it.govpay.web.rs.dars.model.Elemento;
import it.govpay.web.rs.dars.model.Elenco;
import it.govpay.web.rs.dars.model.InfoForm;
import it.govpay.web.rs.dars.model.InfoForm.Sezione;
import it.govpay.web.rs.dars.model.RawParamValue;
import it.govpay.web.rs.dars.model.Voce;
import it.govpay.web.rs.dars.model.input.ParamField;
import it.govpay.web.rs.dars.model.input.base.CheckButton;
import it.govpay.web.rs.dars.model.input.base.InputText;
import it.govpay.web.utils.Utils;

public class PspHandler extends DarsHandler<it.govpay.bd.model.Psp> implements IDarsHandler<it.govpay.bd.model.Psp>{

	public PspHandler(Logger log, DarsService darsService) {
		super(log,darsService);
	}

	@Override
	public Elenco getElenco(UriInfo uriInfo,BasicBD bd) throws WebApplicationException,ConsoleException {
		String methodName = "getElenco " + this.titoloServizio;

		try{		
			Integer offset = this.getOffset(uriInfo);
			Integer limit = this.getLimit(uriInfo);

			this.log.info("Esecuzione " + methodName + " in corso..."); 
			// Operazione consentita solo agli utenti che hanno almeno un ruolo consentito per la funzionalita'
			this.darsService.checkDirittiServizio(bd, this.funzionalita);

			boolean simpleSearch = this.containsParameter(uriInfo, DarsService.SIMPLE_SEARCH_PARAMETER_ID);

			it.govpay.bd.anagrafica.PspBD pspBD = new it.govpay.bd.anagrafica.PspBD(bd);
			PspFilter filter = pspBD.newFilter(simpleSearch);
			filter.setOffset(offset);
			filter.setLimit(limit);
			FilterSortWrapper fsw = new FilterSortWrapper();
			fsw.setField(it.govpay.orm.Psp.model().RAGIONE_SOCIALE);
			fsw.setSortOrder(SortOrder.ASC);
			filter.getFilterSortList().add(fsw);
			
			Map<String, String> params = new HashMap<String, String>();
			
			String abilitatoId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".abilitato.id");
			String abilitato = this.getParameter(uriInfo, abilitatoId, String.class);
			
			filter.setSearchAbilitato(this.getMostraDisabilitato(abilitato)); 
			
			if(simpleSearch){
				// simplesearch
				String simpleSearchString = this.getParameter(uriInfo, DarsService.SIMPLE_SEARCH_PARAMETER_ID, String.class);
				params.put(DarsService.SIMPLE_SEARCH_PARAMETER_ID, simpleSearchString);
				
				if(StringUtils.isNotEmpty(simpleSearchString)) {
					filter.setSimpleSearchString(simpleSearchString);
				}
			}else{
				String ragioneSocialeId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.id");
				String ragioneSociale = this.getParameter(uriInfo, ragioneSocialeId, String.class);
				String codPspId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.id");
				String codPsp = this.getParameter(uriInfo, codPspId, String.class);

				if(StringUtils.isNotEmpty(codPsp)){
					params.put(codPspId, codPsp);
					filter.setCodPsp(codPsp);
				}

				if(StringUtils.isNotEmpty(ragioneSociale)){
					params.put(ragioneSocialeId, ragioneSociale);
					filter.setRagioneSociale(ragioneSociale);
				}
			}

			long count = pspBD.count(filter);

			String simpleSearchPlaceholder = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio+".simpleSearch.placeholder");
			Elenco elenco = new Elenco(this.titoloServizio, this.getInfoRicerca(uriInfo, bd,params),
					this.getInfoCreazione(uriInfo, bd),
					count, this.getInfoEsportazione(uriInfo, bd), this.getInfoCancellazione(uriInfo, bd),simpleSearchPlaceholder); 

			List<it.govpay.bd.model.Psp> findAll = pspBD.findAll(filter);

			// Indico la visualizzazione custom
			String formatter = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio+".elenco.formatter");
			if(findAll != null && findAll.size() > 0){
				for (it.govpay.bd.model.Psp entry : findAll) {
					Elemento elemento = this.getElemento(entry, entry.getId(), this.pathServizio,bd);
					elemento.setFormatter(formatter);
					elenco.getElenco().add(elemento);
				}
			}

			this.log.info("Esecuzione " + methodName + " completata.");

			return elenco;
		}catch(WebApplicationException e){
			throw e;
		}catch(Exception e){
			throw new ConsoleException(e);
		}
	}

	@Override
	public InfoForm getInfoRicerca(UriInfo uriInfo, BasicBD bd, boolean visualizzaRicerca, Map<String,String> parameters) throws ConsoleException {
		URI ricerca = this.getUriRicerca(uriInfo, bd,parameters);
		InfoForm infoRicerca = new InfoForm(ricerca);

		if(visualizzaRicerca) {
			String codPspId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.id");
			String ragioneSocialeId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.id");
			String abilitatoId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".abilitato.id");
			
			if(this.infoRicercaMap == null){
				this.initInfoRicerca(uriInfo, bd);

			}

			Sezione sezioneRoot = infoRicerca.getSezioneRoot();

			InputText codPsp = (InputText) this.infoRicercaMap.get(codPspId);
			codPsp.setDefaultValue(null);
			sezioneRoot.addField(codPsp);

			InputText ragioneSociale = (InputText) this.infoRicercaMap.get(ragioneSocialeId);
			ragioneSociale.setDefaultValue(null);
			sezioneRoot.addField(ragioneSociale);
			
			CheckButton abilitato = (CheckButton) this.infoRicercaMap.get(abilitatoId);
			abilitato.setDefaultValue(false);
			sezioneRoot.addField(abilitato);
		}

		return infoRicerca;
	}

	private void initInfoRicerca(UriInfo uriInfo, BasicBD bd) throws ConsoleException{
		if(this.infoRicercaMap == null){
			this.infoRicercaMap = new HashMap<String, ParamField<?>>();

			String codPspId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.id");
			String ragioneSocialeId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.id");
			String abilitatoId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".abilitato.id");

			// codPsp
			String codPspLabel = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.label");
			InputText codPsp = new InputText(codPspId, codPspLabel, null, false, false, true, 1, 255);
			this.infoRicercaMap.put(codPspId, codPsp);

			// nome
			String ragioneSocialeLabel = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.label");
			InputText ragioneSociale = new InputText(ragioneSocialeId, ragioneSocialeLabel, null, false, false, true, 1, 255);
			this.infoRicercaMap.put(ragioneSocialeId, ragioneSociale);
			
			CheckButton abilitato = this.creaCheckButtonSearchMostraDisabilitato(abilitatoId);
			this.infoRicercaMap.put(abilitatoId, abilitato);	
		}
	}

	@Override
	public InfoForm getInfoCreazione(UriInfo uriInfo, BasicBD bd) throws ConsoleException {
		InfoForm infoCreazione = null; 
		return infoCreazione;
	}

	@Override
	public InfoForm getInfoModifica(UriInfo uriInfo, BasicBD bd, it.govpay.bd.model.Psp entry) throws ConsoleException {
		InfoForm infoModifica = null; 
		return infoModifica;
	}

	@Override
	public InfoForm getInfoCancellazione(UriInfo uriInfo, BasicBD bd, Map<String, String> parameters) throws ConsoleException { return null;}

	@Override
	public InfoForm getInfoCancellazioneDettaglio(UriInfo uriInfo, BasicBD bd, Psp entry) throws ConsoleException {
		return null;
	}
	
	@Override
	public InfoForm getInfoEsportazione(UriInfo uriInfo, BasicBD bd, Map<String, String> parameters) throws ConsoleException { return null; }
	
	@Override
	public InfoForm getInfoEsportazioneDettaglio(UriInfo uriInfo, BasicBD bd, Psp entry)	throws ConsoleException {	return null;	}

	@Override
	public Object getField(UriInfo uriInfo,List<RawParamValue>values, String fieldId,BasicBD bd) throws ConsoleException {
		return null;
	}
	
	@Override
	public Object getSearchField(UriInfo uriInfo, List<RawParamValue> values, String fieldId, BasicBD bd)
			throws WebApplicationException, ConsoleException {
		return null;
	}
	
	@Override
	public Object getDeleteField(UriInfo uriInfo, List<RawParamValue> values, String fieldId, BasicBD bd) throws WebApplicationException, ConsoleException { return null; }
	
	@Override
	public Object getExportField(UriInfo uriInfo, List<RawParamValue> values, String fieldId, BasicBD bd) throws WebApplicationException, ConsoleException { return null; }
	

	@Override
	public Dettaglio getDettaglio(long id, UriInfo uriInfo, BasicBD bd) throws WebApplicationException,ConsoleException {
		String methodName = "dettaglio " + this.titoloServizio + "."+ id;
		try{
			this.log.info("Esecuzione " + methodName + " in corso...");
			// Operazione consentita solo ai ruoli con diritto di lettura
			this.darsService.checkDirittiServizioLettura(bd, this.funzionalita);

			// recupero oggetto
			it.govpay.bd.anagrafica.PspBD pspBD = new it.govpay.bd.anagrafica.PspBD(bd);
			it.govpay.bd.model.Psp psp = pspBD.getPsp(id);

			InfoForm infoModifica = this.getInfoModifica(uriInfo, bd,psp);
			InfoForm infoCancellazione = this.getInfoCancellazioneDettaglio(uriInfo, bd, psp);
			InfoForm infoEsportazione = null;

			Dettaglio dettaglio = new Dettaglio(this.getTitolo(psp,bd), infoEsportazione, infoCancellazione, infoModifica);

			it.govpay.web.rs.dars.model.Sezione root = dettaglio.getSezioneRoot(); 

			// dati del psp
			if(StringUtils.isNotEmpty(psp.getCodPsp()))
				root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.label"), psp.getCodPsp());
			if(StringUtils.isNotEmpty(psp.getRagioneSociale()))
				root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.label"), psp.getRagioneSociale());
			if(StringUtils.isNotEmpty(psp.getCodFlusso()))
				root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codFlusso.label"), psp.getCodFlusso());
			if(StringUtils.isNotEmpty(psp.getUrlInfo()))
				root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".urlInfo.label"), psp.getUrlInfo());
			root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".abilitato.label"), Utils.getSiNoAsLabel(psp.isAbilitato()));
			root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".bolloGestito.label"), Utils.getAbilitatoAsLabel(psp.isBolloGestito()));
			root.addVoce(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stornoGestito.label"), Utils.getAbilitatoAsLabel(psp.isStornoGestito()));

			// Elementi correlati
			String etichettaCanali = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".elementoCorrelato.canali.titolo");
			String codPspId = Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.id");

			Canali canaliDars = new Canali();
			Map<String, String> params = new HashMap<String, String>();
			params.put(codPspId, psp.getCodPsp());
			URI uriDettaglio = Utils.creaUriConParametri(canaliDars.getPathServizio(), params);
			dettaglio.addElementoCorrelato(etichettaCanali, uriDettaglio);

			this.log.info("Esecuzione " + methodName + " completata.");

			return dettaglio;
		}catch(WebApplicationException e){
			throw e;
		}catch(Exception e){
			throw new ConsoleException(e);
		}
	}

	@Override
	public Dettaglio insert(InputStream is, UriInfo uriInfo, BasicBD bd) throws WebApplicationException, ConsoleException,ValidationException,DuplicatedEntryException {
		return null;
	}

	@Override
	public it.govpay.bd.model.Psp creaEntry(InputStream is, UriInfo uriInfo, BasicBD bd) throws WebApplicationException, ConsoleException {
		return null;
	}

	@Override
	public void checkEntry(Psp entry, Psp oldEntry) throws ValidationException {}

	@Override
	public Dettaglio update(InputStream is, UriInfo uriInfo, BasicBD bd) throws WebApplicationException, ConsoleException, ValidationException {
		return null;
	}

	@Override
	public Elenco delete(List<Long> idsToDelete, List<RawParamValue> rawValues, UriInfo uriInfo, BasicBD bd) throws WebApplicationException, ConsoleException, DeleteException {	return null; 	}

	@Override
	public String getTitolo(it.govpay.bd.model.Psp entry, BasicBD bd) {
		StringBuilder sb = new StringBuilder();

		sb.append(entry.getRagioneSociale());
		sb.append(" (").append(entry.getCodPsp()).append(")");
		return sb.toString();
	}

	@Override
	public String getSottotitolo(it.govpay.bd.model.Psp entry, BasicBD bd) {
		StringBuilder sb = new StringBuilder();

		sb.append(Utils.getAbilitatoAsLabel(entry.isAbilitato()));
		sb.append(", Bollo ").append(Utils.getAbilitatoAsLabel(entry.isBolloGestito()));
		sb.append(", Storno ").append(Utils.getAbilitatoAsLabel(entry.isStornoGestito()));

		return sb.toString();
	}

	@Override
	public Map<String, Voce<String>> getVoci(Psp entry, BasicBD bd) throws ConsoleException {
		Map<String, Voce<String>> valori = new HashMap<String, Voce<String>>();

		if(StringUtils.isNotEmpty(entry.getRagioneSociale()))
			valori.put(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.id"),
				new Voce<String>(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".ragioneSociale.label"),entry.getRagioneSociale()));

		if(StringUtils.isNotEmpty(entry.getCodPsp()))
			valori.put(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.id"),
				new Voce<String>(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".codPsp.label"),entry.getCodPsp()));

		// stato del dominio
		// 
		if(!entry.isAbilitato()) {
			valori.put(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.id"),
					new Voce<String>(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.disabilitato.label"),
							Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.disabilitato")));
		} else {
			valori.put(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.id"),
					new Voce<String>(Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.abilitato.label"),
							Utils.getInstance(this.getLanguage()).getMessageFromResourceBundle(this.nomeServizio + ".stato.abilitato")));
		}

		return valori;
	}

	@Override
	public String esporta(List<Long> idsToExport, List<RawParamValue> rawValues, UriInfo uriInfo, BasicBD bd, ZipOutputStream zout)
			throws WebApplicationException, ConsoleException,ExportException {
		return null;
	}

	@Override
	public String esporta(Long idToExport, List<RawParamValue> rawValues, UriInfo uriInfo, BasicBD bd, ZipOutputStream zout)	throws WebApplicationException, ConsoleException ,ExportException{
		return null;
	}


	@Override
	public Object uplaod(MultipartFormDataInput input, UriInfo uriInfo, BasicBD bd)	throws WebApplicationException, ConsoleException, ValidationException { return null;}
}
