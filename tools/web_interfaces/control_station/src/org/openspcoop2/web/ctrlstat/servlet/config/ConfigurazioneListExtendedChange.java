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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletListExtendedChange;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;

/**
 * porteDelegateCorrAppAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneListExtendedChange extends AbstractServletListExtendedChange {
	
	@Override
	public void addToHiddenDati(Vector<DataElement> dati,ConsoleHelper consoleHelper,HttpServletRequest request) throws ExtendedException{
		
	}
	
	
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
	protected IExtendedListServlet getExtendedServlet(ConsoleHelper consoleHelper,ControlStationCore core)
			throws Exception {
		return core.getExtendedServletConfigurazioneList(consoleHelper);
	}

	@Override
	protected Object getObject(ControlStationCore core,
			HttpServletRequest request) throws Exception {
		ConfigurazioneCore c = (ConfigurazioneCore) core;
		return c.getConfigurazioneGenerale();
	}

	@Override
	protected List<Parameter> getTitle(Object object, HttpServletRequest request, HttpSession session) throws Exception {
		List<Parameter> lstParam = new ArrayList<Parameter>();
		lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
		return lstParam;
	}

	@Override
	protected int getIdList() throws Exception {
		return Liste.CONFIGURAZIONE_EXTENDED;
	}

	@Override
	protected Parameter[] getParameterList(HttpServletRequest request, HttpSession session) throws Exception {
		return null;
	}

	@Override
	protected String getObjectName() throws Exception {
		return ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE_LIST_EXTENDED;
	}

	@Override
	protected UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper,HttpServletRequest request) throws Exception {
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_LIST_EXTENDED_CHANGE);
		return urlExtended;
	}


	@Override
	protected UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper,
			HttpServletRequest request) throws Exception {
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_LIST_EXTENDED_LIST);
		return urlExtended;
	}
	
	@Override
	protected UrlParameters getUrlExtendedFather(ConsoleHelper consoleHelper,
			HttpServletRequest request) throws Exception {
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.setUrl(ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
		return urlExtended;
	}
	

}
