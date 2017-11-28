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
package it.govpay.core.business;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.utils.csv.Format;
import org.openspcoop2.utils.csv.FormatReader;
import org.openspcoop2.utils.csv.Printer;
import org.openspcoop2.utils.logger.beans.Property;

import it.govpay.bd.BasicBD;
import it.govpay.bd.FilterSortWrapper;
import it.govpay.bd.anagrafica.DominiBD;
import it.govpay.bd.anagrafica.filters.DominioFilter;
import it.govpay.bd.reportistica.EstrattiContoBD;
import it.govpay.core.utils.CSVSerializerProperties;
import it.govpay.core.utils.CSVUtils;
import it.govpay.core.utils.GovpayConfig;
import it.govpay.core.utils.GpContext;
import it.govpay.core.utils.GpThreadLocal;
import it.govpay.model.Anagrafica;
import it.govpay.model.comparator.EstrattoContoComparator;
import it.govpay.model.reportistica.EstrattoContoMetadata;
import it.govpay.model.reportistica.comparator.EstrattoContoMetadataComparator;
import it.govpay.orm.Dominio;
//import it.govpay.stampe.pdf.Costanti;
//import it.govpay.stampe.pdf.estrattoConto.EstrattoContoPdf;

public class EstrattoConto extends BasicBD {

	private SimpleDateFormat f3 = new SimpleDateFormat("yyyy/MM");

	private static final String CSV_SEPARATOR = "|";

	private static Logger log = LogManager.getLogger();

	private static final int LIMIT = 50;

//	public static final String PAGAMENTI_SENZA_RPT = Costanti.PAGAMENTI_SENZA_RPT_KEY;
//	public static final String MARCA_DA_BOLLO = Costanti.MARCA_DA_BOLLO_KEY;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

	private Format formatW= null;

	public EstrattoConto(BasicBD bd) {
		super(bd);

		try{
			// Setto le properties di scrittura
			FormatReader formatWriter = new FormatReader(CSVSerializerProperties.getInstance(log).getProperties());
			this.formatW = formatWriter.getFormat();
		}catch(Exception e){
			log.error("Errore durante l'inizializzazione di EstrattoConto: " + e.getMessage(),e);
		}
	}

	public String creaEstrattiContoSuFileSystem() throws Exception {
		throw new UnsupportedOperationException("Not aviable for this version");
	}

	public List<EstrattoContoMetadata> getListaEstrattoConto(String codDominio, String formatoFile) throws Exception {
		List<EstrattoContoMetadata> response = new ArrayList<EstrattoContoMetadata>();
		GpContext ctx = GpThreadLocal.get();
		int numeroMesi = GovpayConfig.getInstance().getNumeroMesiEstrattoConto();
		ctx.getContext().getRequest().addGenericProperty(new Property("numeroMesi", numeroMesi+""));
		ctx.log("estrattoConto.listaEstrattoContoDominio");

		try {
			//controllo directory base
			String basePath = GovpayConfig.getInstance().getPathEstrattoConto();
			if(StringUtils.isEmpty(basePath)){
				throw new Exception("Directory di export non impostata, impossibile trovare la lista degli Estratto Conto per il dominio ["+codDominio+"].");
			}

			File baseDir = new File(basePath);

			if(!baseDir.exists()){
				throw new Exception("Directory di export non presente, impossibile trovare la lista degli Estratto Conto per il dominio ["+codDominio+"].");
			}

			// controllo esistenza dominio 
			DominiBD dominiBD = new DominiBD(this);
			it.govpay.model.Dominio dominio  = dominiBD.getDominio(codDominio);

			String denominazioneDominio = dominio.getRagioneSociale()+" ("+dominio.getCodDominio()+")";

			// 1 controllo esistenza directory con nome codDominio
			File dominioDir = new File(basePath+File.separator + dominio.getCodDominio());
			if(!dominioDir.exists()){
				log.debug("creo directory per il dominio ["+denominazioneDominio+"]: "+dominioDir.getAbsolutePath());
				dominioDir.mkdir();
			}

			for (File file : dominioDir.listFiles()) {
				EstrattoContoMetadata estrattoContoFromJson = getEstrattoContoFromJson(dominio.getCodDominio(), file.getName());
				// restituisco solo quelli del formato scelto
				if(formatoFile.equals(EstrattoContoMetadata.FORMATO_STAR) || estrattoContoFromJson.getFormato().equals(formatoFile)) 
					response.add(estrattoContoFromJson);
			}

			if(response.isEmpty()) {
				ctx.log("estrattoConto.listaEstrattoContoDominioVuota");
			} else {
				Collections.sort(response, new EstrattoContoMetadataComparator()); 
				ctx.log("estrattoConto.listaEstrattoContoDominioOk");
			}

			return response;
		} catch(NotFoundException e){
			ctx.log("estrattoConto.listaEstrattoContoDominioKo",e.getMessage());
			throw new Exception("Dominio ["+codDominio+"] non registrato in GovPay.");
		}	catch(Exception e) {

			ctx.log("estrattoConto.listaEstrattoContoDominioKo",e.getMessage());
			log.error(e,e);
			throw e;
		} finally {
		}
	}

	public InputStream scaricaEstrattoConto(String codDominio, String nomeFile, String formatoFile) throws Exception {
		InputStream response = null;
		GpContext ctx = GpThreadLocal.get();
		int numeroMesi = GovpayConfig.getInstance().getNumeroMesiEstrattoConto();
		ctx.getContext().getRequest().addGenericProperty(new Property("numeroMesi", numeroMesi+""));
		ctx.log("estrattoConto.scaricaEstrattoContoDominio");

		try {
			//controllo directory base
			String basePath = GovpayConfig.getInstance().getPathEstrattoConto();
			if(StringUtils.isEmpty(basePath)){
				throw new Exception("Directory di export non impostata, impossibile scaricare l'Estratto Conto ["+nomeFile+"] per il dominio ["+codDominio+"].");
			}

			File baseDir = new File(basePath);

			if(!baseDir.exists()){
				throw new Exception("Directory di export non impostata, impossibile scaricare l'Estratto Conto ["+nomeFile+"] per il dominio ["+codDominio+"].");
			}

			// controllo esistenza dominio 
			DominiBD dominiBD = new DominiBD(this);
			it.govpay.model.Dominio dominio  = dominiBD.getDominio(codDominio);

			String denominazioneDominio = dominio.getRagioneSociale()+" ("+dominio.getCodDominio()+")";

			// 1 controllo esistenza directory con nome codDominio
			File dominioDir = new File(basePath+File.separator + dominio.getCodDominio());
			if(!dominioDir.exists()){
				throw new Exception("Directory di contenente gli Estratto Conto per il dominio ["+denominazioneDominio+"] non presente.");
			}

			String fileToDownload = basePath+File.separator + dominio.getCodDominio()+ File.separator + nomeFile;

			// per ora si gestisce solo il formato CSV
			//			String extension = ".csv";

			//			if(formatoFile != null && formatoFile.equals(FORMATO_PDF))
			//				extension = ".pdf";
			//+ extension
			File estrattoConto = new File((fileToDownload ));

			if(!estrattoConto.exists() || estrattoConto.isDirectory()){
				throw new Exception("Il file richiesto ["+nomeFile+"] per il Dominio ["+codDominio+"] non e' disponibile in formato ["+formatoFile+"].");
			}

			response = new FileInputStream(estrattoConto);

			ctx.log("estrattoConto.scaricaEstrattoContoDominioOk");

			return response;
		}catch(NotFoundException e){
			ctx.log("estrattoConto.listaEstrattoContoDominioKo",e.getMessage());
			throw new Exception("Dominio ["+codDominio+"] non registrato in GovPay.");
		}	 catch(Exception e) {
			ctx.log("estrattoConto.scaricaEstrattoContoDominioKo",e.getMessage());
			log.error(e,e);
			throw e;
		} finally {
		}
	}

	private EstrattoContoMetadata getEstrattoContoFromJson(String codDominio, String fileName){
		EstrattoContoMetadata estrattoConto = new EstrattoContoMetadata();
		estrattoConto.setCodDominio(codDominio);
		estrattoConto.setNomeFile(fileName);

		try{
			// decodificare l'estensione del file
			String extension = FilenameUtils.getExtension(fileName);
			estrattoConto.setFormato(extension);
			fileName = FilenameUtils.removeExtension(fileName);

			String[] split = fileName.split("_");

			if(split != null && split.length > 0 ){
				boolean formatoOk = false;
				// formato Nome file codDominio_anno_mese				
				if(split.length == 3){
					estrattoConto.setAnno(Integer.parseInt(split[1]));
					estrattoConto.setMese(Integer.parseInt(split[2]));
					formatoOk = true;
				}

				// formato Nome file codDominio_iban_anno_mese
				if(split.length == 4){
					estrattoConto.setIbanAccredito(split[1]); 
					estrattoConto.setAnno(Integer.parseInt(split[2]));
					estrattoConto.setMese(Integer.parseInt(split[3]));
					formatoOk = true;
				}

				if(formatoOk){
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.MILLISECOND, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.MONTH, estrattoConto.getMese() -1);
					cal.set(Calendar.YEAR, estrattoConto.getAnno());

					estrattoConto.setMeseAnno(this.f3.format(cal.getTime()));

				}else{
					log.debug("Formato nome del file ["+fileName+"] non valido, impossibile identificare mese/anno dell'estratto conto.");
				}
			}
		} catch(Exception e){
			log.error(e.getMessage(),e);
		}

		return estrattoConto;
	}


	public List<it.govpay.core.business.model.EstrattoConto> getEstrattoContoVersamenti(List<it.govpay.core.business.model.EstrattoConto> inputEstrattoConto,String pathLoghi) throws Exception{throw new UnsupportedOperationException("Not aviable for this version");}

	public List<it.govpay.core.business.model.EstrattoConto> getEstrattoContoPagamenti(List<it.govpay.core.business.model.EstrattoConto> inputEstrattoConto,String pathLoghi) throws Exception{throw new UnsupportedOperationException("Not aviable for this version");}

}
