package org.openspcoop2.core.config.rs.server.api.impl;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.jaxrs.fault.FaultCode;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.PageData;

public class Environment {
	
	public final String tipo_protocollo;
	
	public final String tipo_soggetto;
	
	public final ProfiloEnum profilo;
	
	public final String userLogin;
	
	public final IdSoggetto idSoggetto;
	
	public final PageData pd = new PageData();
	
	public final ControlStationCore stationCore;
	
	public final IProtocolFactory<?> protocolFactory;
	
	public final SoggettiCore soggettiCore;

	
	public Environment(HttpServletRequest req, ProfiloEnum profilo, String soggetto, ServiceContext ctx) throws Exception {
		
		if (profilo == null)
			profilo = Helper.getProfiloDefault();

		soggetto = Helper.getSoggettoOrDefault(soggetto, profilo);
		
		this.userLogin = ctx.getAuthentication().getName();
		this.profilo = profilo;
		this.tipo_protocollo = Helper.tipoProtocolloFromProfilo.get(profilo);
		
		this.stationCore = new ControlStationCore(true, ServerProperties.getInstance().getConfDirectory() ,this.tipo_protocollo);
		this.soggettiCore = new SoggettiCore(this.stationCore);
		this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.tipo_protocollo);
		this.tipo_soggetto = ProtocolFactoryManager.getInstance().getDefaultOrganizationTypes().get(this.tipo_protocollo);
		
		this.idSoggetto = new IdSoggetto();
		this.idSoggetto.setNome(soggetto);
		this.idSoggetto.setTipo(this.tipo_soggetto);
		this.idSoggetto.setId(-1L);
		
		try {
			this.idSoggetto.setId(this.soggettiCore.getIdSoggetto(soggetto,this.tipo_soggetto));
		} catch (Exception e) {}
		
		if (this.idSoggetto.getId() == -1) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto con nome " + soggetto + " e tipo " + this.tipo_soggetto + " non esiste.");
		}

		
	}

}
