/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedFormServlet;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletNewWindowChangeExtended;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;

/**
 * routing
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneExtended extends AbstractServletNewWindowChangeExtended {

	@Override
	protected ConsoleHelper getConsoleHelper(HttpServletRequest request,
			PageData pd, HttpSession session) throws Exception {
		return new ConfigurazioneHelper(request, pd, session);
	}

	@Override
	protected ControlStationCore getConsoleCore() throws Exception {
		return new ConfigurazioneCore();
	}

	@Override
	protected IExtendedFormServlet getExtendedServlet(ControlStationCore core, String uniqueId) throws Exception{
		List<IExtendedFormServlet> list = core.getExtendedServletConfigurazione();
		for (IExtendedFormServlet iExtendedFormServlet : list) {
			if(uniqueId.equals(iExtendedFormServlet.getUniqueID())){
				return iExtendedFormServlet;
			}
		}
		return null;
	}
	
	@Override
	protected List<Parameter> getTitle(Object object,HttpServletRequest request, HttpSession session) throws Exception {
		List<Parameter> lstParam = new ArrayList<Parameter>();
		lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
		return lstParam;
	}

	@Override
	protected Object getObject(ControlStationCore core,HttpServletRequest request) throws Exception {
		ConfigurazioneCore c = (ConfigurazioneCore) core;
		return c.getConfigurazioneGenerale();
	}

	@Override
	protected String getObjectName() throws Exception {
		return ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE_EXTENDED;
	}

	@Override
	protected ForwardParams getForwardParams() throws Exception {
		return ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE_EXTENDED;
	}


}
