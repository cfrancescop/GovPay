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
package it.govpay.web.rs.dars.statistiche.transazioni;

import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.govpay.model.Acl.Servizio;
import it.govpay.web.rs.dars.base.StatisticaDarsService;
import it.govpay.web.rs.dars.handler.IStatisticaDarsHandler;

@Path("/dars/statisticheTransazioni/distribuzioneEsiti")
public class DistribuzioneEsiti extends StatisticaDarsService {

	public DistribuzioneEsiti() {
		super();
	}
	
	Logger log = LogManager.getLogger();
	
	@Override
	public String getNomeServizio() {
		return "distribuzioneEsiti";
	}

	@Override
	public IStatisticaDarsHandler<?> getDarsHandler() {
		return new DistribuzioneEsitiHandler(this.log, this);
	}
	
	@Override
	public String getPathServizio() {
		return "/dars/statisticheTransazioni/" + this.getNomeServizio();
	}
	
	@Override
	public Servizio getFunzionalita() {
		return Servizio.Statistiche;
	}
}