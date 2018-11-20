package org.openspcoop2.core.config.rs.server.api.impl;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.PageData;

public class RuoliEnv {
	
	public final String tipo_protocollo = ApplicativiApiHelper.getProtocolloOrDefault(null);
	public final PageData pd = new PageData();
	public final RuoliCore ruoliCore;
	public final UtentiCore utentiCore;
	public final ControlStationCore stationCore;
	public final RuoliHelper ruoliHelper;
	
	public RuoliEnv(HttpServletRequest req) throws Exception {	 
		
		ServerProperties serverProperties = ServerProperties.getInstance();
		this.stationCore = new ControlStationCore(true, serverProperties.getConfDirectory(),this.tipo_protocollo);
		this.ruoliCore = new RuoliCore(this.stationCore);

		//RuoliWrapperServlet wrap = new RuoliWrapperServlet(req);

		this.utentiCore = new UtentiCore(this.stationCore);
		this.ruoliHelper = new RuoliHelper(this.stationCore, req, this.pd, req.getSession());
	}

}
