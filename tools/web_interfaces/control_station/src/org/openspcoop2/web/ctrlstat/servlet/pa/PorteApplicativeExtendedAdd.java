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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletListExtendedAdd;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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
public final class PorteApplicativeExtendedAdd extends AbstractServletListExtendedAdd {

	@Override
	public void addToHiddenDati(Vector<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException{
		PorteApplicativeExtendedUtilities.addToHiddenDati(TipoOperazione.ADD, dati, consoleHelper);
	}
	
	@Override
	protected ConsoleHelper getConsoleHelper(HttpServletRequest request,
			PageData pd, HttpSession session) throws Exception {
		return new PorteApplicativeHelper(request, pd, session);
	}

	@Override
	protected ControlStationCore getConsoleCore() throws Exception {
		return new PorteApplicativeCore();
	}

	@Override
	protected IExtendedListServlet getExtendedServlet(ConsoleHelper consoleHelper)
			throws Exception {
		return consoleHelper.getCore().getExtendedServletPortaApplicativa();
	}

	@Override
	protected Object getObject(ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getObject(consoleHelper);
	}

	@Override
	protected List<Parameter> getTitle(Object object, ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getTitle(object, consoleHelper);
	}

	@Override
	protected int getIdList() throws Exception {
		return Liste.PORTE_APPLICATIVE_EXTENDED;
	}

	@Override
	protected Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getParameterList(consoleHelper);
	}

	@Override
	protected String getObjectName() throws Exception {
		return PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_EXTENDED;
	}

	@Override
	protected UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getUrlExtendedChange(consoleHelper);
	}
	
	@Override
	protected UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getUrlExtendedList(consoleHelper);
	}
	
	@Override
	protected UrlParameters getUrlExtendedFather(ConsoleHelper consoleHelper) throws Exception {
		return PorteApplicativeExtendedUtilities.getUrlExtendedList(consoleHelper);
	}
}
