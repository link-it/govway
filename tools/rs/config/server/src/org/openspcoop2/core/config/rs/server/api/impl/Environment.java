/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
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
	
	public final PddCore pddCore;
	
	public final ProtocolFactoryManager protocolFactoryMgr;
	
	public final boolean delete_404;
	
	public final boolean findall_404;

	public final boolean multitenant;

	public final boolean gestioneCanali;
	public String canaleDefault;
	public List<String> canali = new ArrayList<>();
	
	public Environment(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext ctx) throws Exception {
		
		if (profilo == null)
			profilo = Helper.getProfiloDefault();

		soggetto = Helper.getSoggettoOrDefault(soggetto, profilo);
		
		this.userLogin = ctx.getAuthentication().getName();
		this.profilo = profilo;
		this.tipo_protocollo = BaseHelper.tipoProtocolloFromProfilo.get(profilo);
		this.requestWrapper = new HttpRequestWrapper(req);
		
		this.stationCore = new ControlStationCore(true, ServerProperties.getInstance().getConfDirectory() ,this.tipo_protocollo);
		this.pddCore = new PddCore(this.stationCore);
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
		
		CanaliConfigurazione canali = confCore.getCanaliConfigurazione(false);
		this.gestioneCanali = canali!=null && StatoFunzionalita.ABILITATO.equals(canali.getStato());
		if(this.gestioneCanali && canali.getCanaleList()!=null && !canali.getCanaleList().isEmpty()) {
			for (CanaleConfigurazione canale : canali.getCanaleList()) {
				if(canale.isCanaleDefault()) {
					this.canaleDefault = canale.getNome();
				}
				this.canali.add(canale.getNome());
			}
		}
	}
	
	public boolean isDominioInterno(IDSoggetto idSoggetto) {
		try {
			Soggetto soggetto = this.soggettiCore.getSoggettoRegistro(idSoggetto);
			return !this.pddCore.isPddEsterna(soggetto.getPortaDominio());
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean isProfiloModi() {
		return this.isProfiloModi(this.profilo);
	}
	public boolean isProfiloModi(ProfiloEnum profilo) {
		return profilo!=null && (ProfiloEnum.MODI.equals(profilo) || ProfiloEnum.MODIPA.equals(profilo));
	}
	
	public boolean isProfiloSPCoop() {
		return this.isProfiloSPCoop(this.profilo);
	}
	public boolean isProfiloSPCoop(ProfiloEnum profilo) {
		return profilo!=null && ProfiloEnum.SPCOOP.equals(profilo);
	}

}
