package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jaxrs.impl.ServiceContext;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;

public class ApplicativiEnv extends Environment {
	
	public final ServiziApplicativiCore saCore;
	public final PorteDelegateCore porteDelegateCore;
	public final ServiziApplicativiHelper saHelper;
	
	ApplicativiEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, ServiceContext ctx) throws UtilsException, Exception {
		super(req,profilo,soggetto,ctx);
		 
		this.saCore = new ServiziApplicativiCore(this.stationCore);
		this.porteDelegateCore = new PorteDelegateCore(this.stationCore);
		this.saHelper = new ServiziApplicativiHelper(this.stationCore, req, this.pd, req.getSession());
	}
	
	
}