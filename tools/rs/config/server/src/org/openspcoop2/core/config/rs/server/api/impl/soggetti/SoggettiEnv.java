package org.openspcoop2.core.config.rs.server.api.impl.soggetti;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;

public class SoggettiEnv extends Environment {
	

	public final UtentiCore utentiCore;
	public final PddCore 		pddCore;
	public final SoggettiHelper soggettiHelper;
	public final ConsoleInterfaceType consoleInterfaceType;

	public final boolean multitenant;
	

	public SoggettiEnv(HttpServletRequest req, ProfiloEnum profilo, IContext ctx) throws Exception {
		super(req,profilo,null,ctx);

		this.multitenant = ServerProperties.getInstance().isMultitenant();
		
		this.pddCore 	= new PddCore(this.soggettiCore);
		this.utentiCore = new UtentiCore(this.soggettiCore);
		this.soggettiHelper = new SoggettiHelper(this.stationCore, req, this.pd, req.getSession());	
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(this.soggettiHelper);
	}
}
