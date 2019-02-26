package org.openspcoop2.core.config.rs.server.api.impl.api;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;

public class ApiEnv extends Environment {
	
	public final AccordiServizioParteComuneCore apcCore;
	public final AccordiServizioParteComuneHelper apcHelper;
	public final ArchiviCore archiviCore;
	public final IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	
	public final boolean gestisciSoggettoReferente;
	
	ApiEnv(ProfiloEnum profilo, String soggetto, IContext ctx) throws Exception {
		super(ctx.getServletRequest(), profilo, soggetto, ctx);
		
		if (profilo == null)
			profilo = Helper.getProfiloDefault();
		
		soggetto = Helper.getSoggettoOrDefault(soggetto, profilo);		
		
		this.apcCore = new AccordiServizioParteComuneCore(this.stationCore);
		this.apcHelper = new AccordiServizioParteComuneHelper(this.stationCore, ctx.getServletRequest(), this.pd, ctx.getServletRequest().getSession());
		this.archiviCore = new ArchiviCore(this.stationCore);
		
		this.gestisciSoggettoReferente =  this.apcCore.isSupportatoSoggettoReferente(this.tipo_protocollo);
		
	}

}
