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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeExtendedUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeExtendedUtilities {

	public static void addToHiddenDati(TipoOperazione tipoOperazione,Vector<DataElement> dati,ConsoleHelper consoleHelper,HttpServletRequest request) throws ExtendedException{
		String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		PorteApplicativeHelper porteApplicativeHelper = (PorteApplicativeHelper)consoleHelper;
		porteApplicativeHelper.addHiddenFieldsToDati(tipoOperazione, idPorta, idsogg, idPorta, dati);
	}
	
	public static Object getObject(ControlStationCore core,
			HttpServletRequest request) throws Exception {
		PorteApplicativeCore PorteApplicativeCore = (PorteApplicativeCore) core;
		String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		int idInt = Integer.parseInt(idPorta);
		return PorteApplicativeCore.getPortaApplicativa(idInt);
	}
	
	public static List<Parameter> getTitle(Object object, HttpServletRequest request, HttpSession session) throws Exception {
		
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, session);
		
		PortaApplicativa pa = (PortaApplicativa) object;
		String tmpTitle = pa.getTipoSoggettoProprietario() + "/" + pa.getNomeSoggettoProprietario();
		
		String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		
		List<Parameter> list = new ArrayList<Parameter>();
		
		if(useIdSogg){
			list.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null));
			list.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			list.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)
					));
		}else{
			list.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null));
			list.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
		}
		return list;
		
	}
	
	public static Parameter[] getParameterList(HttpServletRequest request,
			HttpSession session) throws Exception {
		
		String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		
		Parameter[] par = new Parameter[2];
		par[0] = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta);
		par[1] = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
		return par;
		
	}
	
	public static UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper,HttpServletRequest request) throws Exception {
		String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,idPorta));
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_CHANGE);
		return urlExtended;
	}
	
	public static UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper,
			HttpServletRequest request) throws Exception {
		String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,idPorta));
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST);
		return urlExtended;
	}
}
