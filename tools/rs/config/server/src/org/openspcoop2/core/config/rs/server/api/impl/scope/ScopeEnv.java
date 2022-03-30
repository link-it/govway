/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.scope;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeCore;
import org.openspcoop2.web.ctrlstat.servlet.scope.ScopeHelper;

/**
 * ScopeEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ScopeEnv extends Environment {

	public final ScopeCore scopeCore;
	public final ScopeHelper scopeHelper; 
	
	public ScopeEnv(HttpServletRequest req, IContext context) throws UtilsException, Exception {
		super(req,null,null,context);

		this.scopeCore = new ScopeCore(this.stationCore);
		this.scopeHelper = new ScopeHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
	}
}
