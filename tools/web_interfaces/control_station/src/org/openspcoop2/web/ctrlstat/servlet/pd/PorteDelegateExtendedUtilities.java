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

import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateExtendedUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateExtendedUtilities {
	
	private PorteDelegateExtendedUtilities() {}

	public static void addToHiddenDati(TipoOperazione tipoOperazione,List<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException{
		try {
			String id = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			PorteDelegateHelper porteDelegateHelper = (PorteDelegateHelper)consoleHelper;
			porteDelegateHelper.addHiddenFieldsToDati(tipoOperazione, id, idsogg, null, dati);
		}catch(Exception e) {
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
	public static Object getObject(ConsoleHelper consoleHelper) throws Exception {
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(consoleHelper.getCore());
		String id = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		int idInt = Integer.parseInt(id);
		return porteDelegateCore.getPortaDelegata(idInt);
	}
	
	public static List<Parameter> getTitle(ConsoleHelper consoleHelper) throws Exception {
		
		String idSoggettoFruitore = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

		String idAsps = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
		if(idAsps == null)
			idAsps = "";
		
		String idFruizione = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
		if(idFruizione == null)
			idFruizione = "";
		
		PorteDelegateHelper pdHelper = new PorteDelegateHelper(consoleHelper.getCore(), consoleHelper.getRequest(), consoleHelper.getPd(), consoleHelper.getSession());
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, consoleHelper.getSession(), consoleHelper.getRequest());
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
		
		return pdHelper.getTitoloPD(parentPD, idSoggettoFruitore, idAsps, idFruizione); 
		
	}
	
	public static Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		
		String id = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		
		Parameter[] par = new Parameter[2];
		par[0] = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
		par[1] = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
		return par;
		
	}
	
	public static UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception {
		String id = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id));
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_CHANGE);
		return urlExtended;
	}
	
	public static UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper) throws Exception {
		String id = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id));
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST);
		return urlExtended;
	}
}
