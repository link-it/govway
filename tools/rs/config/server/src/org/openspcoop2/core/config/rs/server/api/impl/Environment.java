/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.PageData;

/**
 * Environment
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Environment {
	
	public final String tipo_protocollo;
	
	public final String tipo_soggetto;
	
	public final ProfiloEnum profilo;
	
	public final String userLogin;
	
	public final IdSoggetto idSoggetto;
	
	public final HttpRequestWrapper requestWrapper;
	
	public final PageData pd = new PageData();
	
	public final ControlStationCore stationCore;
	
	public final IProtocolFactory<?> protocolFactory;
	
	public final SoggettiCore soggettiCore;
	
	public final ProtocolFactoryManager protocolFactoryMgr;
	
	public final boolean delete_404;
	
	public final boolean findall_404;

	public final boolean multitenant;

	
	public Environment(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext ctx) throws Exception {
		
		if (profilo == null)
			profilo = Helper.getProfiloDefault();

		soggetto = Helper.getSoggettoOrDefault(soggetto, profilo);
		
		this.userLogin = ctx.getAuthentication().getName();
		this.profilo = profilo;
		this.tipo_protocollo = Helper.tipoProtocolloFromProfilo.get(profilo);
		this.requestWrapper = new HttpRequestWrapper(req);
		
		this.stationCore = new ControlStationCore(true, ServerProperties.getInstance().getConfDirectory() ,this.tipo_protocollo);
		this.soggettiCore = new SoggettiCore(this.stationCore);
		this.protocolFactoryMgr = ProtocolFactoryManager.getInstance();
		
		this.protocolFactory = this.protocolFactoryMgr.getProtocolFactoryByName(this.tipo_protocollo);
		this.tipo_soggetto = this.protocolFactoryMgr.getDefaultOrganizationTypes().get(this.tipo_protocollo);
		
		this.idSoggetto = new IdSoggetto();
		this.idSoggetto.setNome(soggetto);
		this.idSoggetto.setTipo(this.tipo_soggetto);
		this.idSoggetto.setId(-1L);
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(this.stationCore);
		this.multitenant = confCore.getConfigurazioneGenerale().getMultitenant().getStato() == StatoFunzionalita.ABILITATO;
		
		try {
			this.idSoggetto.setId(this.soggettiCore.getIdSoggetto(soggetto,this.tipo_soggetto));
		} catch (Exception e) {}
		
		if (this.idSoggetto.getId() == -1) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto con nome " + soggetto + " e tipo " + this.tipo_soggetto + " non esiste.");
		}
		
		this.delete_404 = ServerProperties.getInstance().isDelete404();
		this.findall_404 = ServerProperties.getInstance().isFindall404();
	}

}
