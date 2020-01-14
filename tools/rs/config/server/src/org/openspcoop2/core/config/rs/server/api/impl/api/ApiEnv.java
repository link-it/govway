/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;

/**
 * ApiEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
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
		this.apcHelper = new AccordiServizioParteComuneHelper(this.stationCore, this.requestWrapper, this.pd, ctx.getServletRequest().getSession());
		this.archiviCore = new ArchiviCore(this.stationCore);
		
		this.gestisciSoggettoReferente =  this.apcCore.isSupportatoSoggettoReferente(this.tipo_protocollo);
		
	}

}
