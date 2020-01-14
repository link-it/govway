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
package org.openspcoop2.core.config.rs.server.api.impl.soggetti;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;

/**
 * SoggettiEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SoggettiEnv extends Environment {
	

	public final UtentiCore utentiCore;
	public final PddCore 		pddCore;
	public final SoggettiHelper soggettiHelper;
	public final ConsoleInterfaceType consoleInterfaceType;

	

	public SoggettiEnv(HttpServletRequest req, ProfiloEnum profilo, IContext ctx) throws Exception {
		super(req,profilo,null,ctx);
		
		this.pddCore 	= new PddCore(this.soggettiCore);
		this.utentiCore = new UtentiCore(this.soggettiCore);
		this.soggettiHelper = new SoggettiHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());	
		this.consoleInterfaceType = ProtocolPropertiesUtilities.getTipoInterfaccia(this.soggettiHelper);
	}
}
