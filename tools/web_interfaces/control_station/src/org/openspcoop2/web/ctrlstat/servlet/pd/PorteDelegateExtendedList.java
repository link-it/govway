/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletListExtendedList;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * porteDelegateCorrAppList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateExtendedList extends AbstractServletListExtendedList {

	
	@Override
	protected ConsoleHelper getConsoleHelper(HttpServletRequest request,
			PageData pd, HttpSession session) throws Exception {
		PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
		
		String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
		if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
			ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
		}
		
		return porteDelegateHelper;
	}

	@Override
	protected ControlStationCore getConsoleCore() throws Exception {
		return new PorteDelegateCore();
	}

	@Override
	protected IExtendedListServlet getExtendedServlet(ConsoleHelper consoleHelper)
			throws Exception {
		return consoleHelper.getCore().getExtendedServletPortaDelegata();
	}

	@Override
	protected Object getObject(ConsoleHelper consoleHelper) throws Exception {
		return PorteDelegateExtendedUtilities.getObject(consoleHelper);
	}

	@Override
	protected List<Parameter> getTitle(Object object,ConsoleHelper consoleHelper) throws Exception {
		return PorteDelegateExtendedUtilities.getTitle(consoleHelper);
	}

	@Override
	protected int getIdList() throws Exception {
		return Liste.PORTE_DELEGATE_EXTENDED;
	}

	@Override
	protected Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		return PorteDelegateExtendedUtilities.getParameterList(consoleHelper);
	}

	@Override
	protected String getObjectName() throws Exception {
		return PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_EXTENDED;
	}

	@Override
	protected UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception {
		return PorteDelegateExtendedUtilities.getUrlExtendedChange(consoleHelper);
	}
	
	@Override
	protected UrlParameters getUrlExtendedFather(ConsoleHelper consoleHelper) throws Exception {
		return PorteDelegateExtendedUtilities.getUrlExtendedList(consoleHelper);
	}
}
