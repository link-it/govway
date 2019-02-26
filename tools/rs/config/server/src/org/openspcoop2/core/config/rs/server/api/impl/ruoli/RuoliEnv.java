package org.openspcoop2.core.config.rs.server.api.impl.ruoli;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;

public class RuoliEnv extends Environment {
	
	public final RuoliCore ruoliCore;	
	public final RuoliHelper ruoliHelper;
	public final UtentiCore utentiCore;
	
	public RuoliEnv(HttpServletRequest req, IContext context) throws Exception {
		super(req, null, null, context);
		
		this.ruoliCore = new RuoliCore(this.stationCore);
		this.utentiCore = new UtentiCore(this.stationCore);
		this.ruoliHelper = new RuoliHelper(this.stationCore, req, this.pd, req.getSession());
	}

}
