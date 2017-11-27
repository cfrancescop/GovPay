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
package it.govpay.web.rs.v1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.sun.istack.Nullable;

import it.govpay.bd.BasicBD;
import it.govpay.bd.anagrafica.AnagraficaManager;
import it.govpay.core.business.model.LeggiIncassoDTO;
import it.govpay.core.business.model.LeggiIncassoDTOResponse;
import it.govpay.core.business.model.ListaIncassiDTO;
import it.govpay.core.business.model.ListaIncassiDTOResponse;
import it.govpay.core.business.model.RichiestaIncassoDTO;
import it.govpay.core.business.model.RichiestaIncassoDTOResponse;
import it.govpay.core.exceptions.IncassiException;
import it.govpay.core.exceptions.NotAuthorizedException;
import it.govpay.core.utils.GpContext;
import it.govpay.core.utils.GpThreadLocal;
import it.govpay.model.Applicazione;
import it.govpay.web.rs.BaseRsService;
import it.govpay.web.rs.v1.beans.Errore;
import it.govpay.web.rs.v1.beans.Incasso;
import it.govpay.web.rs.v1.beans.IncassoExt;

@Path("/v1/incassi")
public class Incassi extends BaseRsServiceV1 {
	
	public static final String NOME_SERVIZIO = "incassi";
	
	@POST
	@Path("/")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response inserisciIncasso(InputStream is, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders){
		String methodName = "inserisciIncasso"; 
		
		BasicBD bd = null;
		GpContext ctx = null; 
		ByteArrayOutputStream baos = null, baosResponse = null;
		ByteArrayInputStream bais = null;
		
		try{
			baos = new ByteArrayOutputStream();
			BaseRsService.copy(is, baos);

			this.logRequest(uriInfo, httpHeaders, methodName, baos);

			bd = BasicBD.newInstance(GpThreadLocal.get().getTransactionId());
			ctx =  GpThreadLocal.get();
			
			Incasso incasso = Incasso.parse(baos.toString());
			RichiestaIncassoDTO richiestaIncassoDTO = incasso.toRichiestaIncassoDTO();
			Applicazione applicazione = AnagraficaManager.getApplicazioneByPrincipal(bd, getPrincipal());
			richiestaIncassoDTO.setApplicazione(applicazione);
			
			it.govpay.core.business.Incassi incassi = new it.govpay.core.business.Incassi(bd);
			RichiestaIncassoDTOResponse richiestaIncassoDTOResponse = incassi.richiestaIncasso(richiestaIncassoDTO);
			
			IncassoExt incassoExt = new IncassoExt(richiestaIncassoDTOResponse.getIncasso(), bd);
			
			this.logResponse(uriInfo, httpHeaders, methodName, incassoExt);

			if(richiestaIncassoDTOResponse.isCreato())
				return Response.status(Status.CREATED).entity(incassoExt).build();
			else 
				return Response.status(Status.OK).entity(incassoExt).build();
		} catch (NotAuthorizedException e) {
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0],401);
			return Response.status(Status.UNAUTHORIZED).build();
		} catch (IncassiException e) {
			Errore errore = new Errore(e);
			try { this.logResponse(uriInfo, httpHeaders, methodName, errore); } catch (Exception e2) { log.error(e2);}
			return Response.status(422).entity(errore).build();
		} catch (Exception e) {
			log.error("Errore interno durante il processo di incasso", e);
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0], 500);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if(bd != null) bd.closeConnection();
			if(ctx != null) ctx.log();
			if(bais != null) try { bais.close();} catch (IOException e) {}
			if(baos != null) try { baos.flush(); baos.close();} catch (IOException e) {}
			if(baosResponse != null) try { baosResponse.flush(); baosResponse.close();} catch (IOException e) {}
		}
	}
	
	
	@GET
	@Path("/")
	@Produces({MediaType.APPLICATION_JSON})
	public Response cercaIncassi(InputStream is, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders,
			@QueryParam(value="data_inizio") @Nullable Date inizio,
			@QueryParam(value="data_fine") @Nullable Date fine,
			@QueryParam(value="offset") @DefaultValue(value="0") int offset,
			@QueryParam(value="limit") @DefaultValue(value="25") int limit) {
		
		String methodName = "cercaIncassi"; 
		if(limit > 25) limit = 25;
		
		BasicBD bd = null;
		GpContext ctx = null; 
		
		try{

			this.logRequest(uriInfo, httpHeaders, methodName, new ByteArrayOutputStream());
			bd = BasicBD.newInstance(GpThreadLocal.get().getTransactionId());
			ctx =  GpThreadLocal.get();
			
			ListaIncassiDTO listaIncassoDTO = new ListaIncassiDTO();
			listaIncassoDTO.setInizio(inizio);
			listaIncassoDTO.setFine(fine);
			listaIncassoDTO.setOffset(offset);
			listaIncassoDTO.setLimit(limit);
			listaIncassoDTO.setPrincipal(getPrincipal());
			
			it.govpay.core.business.Incassi incassi = new it.govpay.core.business.Incassi(bd);
			ListaIncassiDTOResponse listaIncassiDTOResponse = incassi.listaIncassi(listaIncassoDTO);
			
			List<Incasso> listaIncassi = new ArrayList<Incasso>();
			for(it.govpay.bd.model.Incasso i : listaIncassiDTOResponse.getIncassi()) {
				listaIncassi.add(new Incasso(i));
			}
			
				return Response.status(Status.OK).entity(listaIncassi).build();
		} catch (NotAuthorizedException e) {
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0],401);
			return Response.status(Status.UNAUTHORIZED).build();
		} catch (Exception e) {
			log.error("Errore interno durante il processo di incasso", e);
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0], 500);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if(bd != null) bd.closeConnection();
			if(ctx != null) ctx.log();
		}
	}
	
	@GET
	@Path("/{trn}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response leggiIncasso(InputStream is, @Context UriInfo uriInfo, @Context HttpHeaders httpHeaders,
			@PathParam(value="trn") String trn) {
		
		String methodName = "leggiIncasso"; 
		BasicBD bd = null;
		GpContext ctx = null; 
		
		try{
			this.logRequest(uriInfo, httpHeaders, methodName, new ByteArrayOutputStream());
			bd = BasicBD.newInstance(GpThreadLocal.get().getTransactionId());
			ctx =  GpThreadLocal.get();
			
			LeggiIncassoDTO leggiIncassoDTO = new LeggiIncassoDTO();
			leggiIncassoDTO.setTrn(trn);
			leggiIncassoDTO.setPrincipal(getPrincipal());
			
			it.govpay.core.business.Incassi incassi = new it.govpay.core.business.Incassi(bd);
			LeggiIncassoDTOResponse leggiIncassoDTOResponse = incassi.leggiIncasso(leggiIncassoDTO);
			
			return Response.status(Status.OK).entity(new IncassoExt(leggiIncassoDTOResponse.getIncasso(), bd)).build();
		} catch (NotAuthorizedException e) {
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0],401);
			return Response.status(Status.UNAUTHORIZED).build();
		} catch (Exception e) {
			log.error("Errore interno durante il processo di incasso", e);
			this.logResponse(uriInfo, httpHeaders, methodName, new byte[0], 500);
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if(bd != null) bd.closeConnection();
			if(ctx != null) ctx.log();
		}
	}
}
