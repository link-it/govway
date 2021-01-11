/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.ruoli;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;

/**
 * RuoliEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class RuoliEnv extends Environment {
	
	public final RuoliCore ruoliCore;	
	public final RuoliHelper ruoliHelper;
	public final UtentiCore utentiCore;
	
	public RuoliEnv(HttpServletRequest req, IContext context) throws Exception {
		super(req, null, null, context);
		
		this.ruoliCore = new RuoliCore(this.stationCore);
		this.utentiCore = new UtentiCore(this.stationCore);
		this.ruoliHelper = new RuoliHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
	}

}
