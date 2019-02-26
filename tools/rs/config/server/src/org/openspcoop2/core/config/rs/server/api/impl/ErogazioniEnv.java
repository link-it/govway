package org.openspcoop2.core.config.rs.server.api.impl;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;

public class ErogazioniEnv extends Environment {

	public final ErogazioniHelper apsHelper;

	
	public ErogazioniEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext ctx)
			throws Exception {
		super(req, profilo, soggetto, ctx);
		this.apsHelper = new ErogazioniHelper(this.stationCore, req, this.pd, req.getSession());
	}

}
