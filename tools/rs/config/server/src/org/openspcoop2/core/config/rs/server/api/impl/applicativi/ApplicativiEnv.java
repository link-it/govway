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
package org.openspcoop2.core.config.rs.server.api.impl.applicativi;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.config.rs.server.api.impl.Environment;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;

/**
 * ApplicativiEnv
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApplicativiEnv extends Environment {
	
	public final ServiziApplicativiCore saCore;
	public final PorteDelegateCore porteDelegateCore;
	public final ServiziApplicativiHelper saHelper;
	
	public ApplicativiEnv(HttpServletRequest req, ProfiloEnum profilo, String soggetto, IContext ctx) throws UtilsException, Exception {
		super(req,profilo,soggetto,ctx);
		 
		this.saCore = new ServiziApplicativiCore(this.stationCore);
		this.porteDelegateCore = new PorteDelegateCore(this.stationCore);
		this.saHelper = new ServiziApplicativiHelper(this.stationCore, this.requestWrapper, this.pd, req.getSession());
	}
	
}