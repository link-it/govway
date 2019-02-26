package org.openspcoop2.core.config.rs.server.api.impl.scope;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeHelper;

public class ScopeEnv extends Environment {

	public final ScopeCore scopeCore;
	public final ScopeHelper scopeHelper; 
	
	public ScopeEnv(HttpServletRequest req, IContext context) throws UtilsException, Exception {
		super(req,null,null,context);

		this.scopeCore = new ScopeCore(this.stationCore);
		this.scopeHelper = new ScopeHelper(this.stationCore, req, this.pd, req.getSession());
	}
}
