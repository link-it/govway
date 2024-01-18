/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
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
	
	private PorteApplicativeExtendedUtilities() {}

	public static void addToHiddenDati(TipoOperazione tipoOperazione,List<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException{
		try {
			String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idasps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			PorteApplicativeHelper porteApplicativeHelper = (PorteApplicativeHelper)consoleHelper;
			porteApplicativeHelper.addHiddenFieldsToDati(tipoOperazione, idPorta, idsogg, idPorta, idasps, dati);
		}catch(Exception e) {
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
	public static Object getObject(ConsoleHelper consoleHelper) throws Exception {
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(consoleHelper.getCore());
		String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		int idInt = Integer.parseInt(idPorta);
		return porteApplicativeCore.getPortaApplicativa(idInt);
	}
	
	public static List<Parameter> getTitle(ConsoleHelper consoleHelper) throws Exception {
				
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idAsps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		if(idAsps == null) 
			idAsps = "";
		
		PorteApplicativeHelper paHelper = new PorteApplicativeHelper(consoleHelper.getCore(), consoleHelper.getRequest(), consoleHelper.getPd(), consoleHelper.getSession());
		
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, consoleHelper.getSession(), consoleHelper.getRequest());
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		
		return paHelper.getTitoloPA(parentPA, idsogg, idAsps);
		
	}
	
	public static Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		
		String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idasps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		String nomePorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		
		List<Parameter> list = new ArrayList<>();
		list.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
		list.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
		if(StringUtils.isNotEmpty(idasps)) {
			list.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idasps));
		}
		if(StringUtils.isNotEmpty(nomePorta)) {
			list.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA,nomePorta));
		}
		return list.toArray(new Parameter[1]);
		
	}
	
	public static UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception {
		String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idasps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		String nomePorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,idPorta));
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg));
		if(StringUtils.isNotEmpty(idasps)) {
			urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idasps));
		}
		if(StringUtils.isNotEmpty(nomePorta)) {
			urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA,nomePorta));
		}
		urlExtended.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_CHANGE);
		return urlExtended;
	}
	
	public static UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper) throws Exception {
		String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idasps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		String nomePorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,idPorta));
		urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg));
		if(StringUtils.isNotEmpty(idasps)) {
			urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idasps));
		}
		if(StringUtils.isNotEmpty(nomePorta)) {
			urlExtended.addParameter(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA,nomePorta));
		}
		urlExtended.setUrl(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_EXTENDED_LIST);
		return urlExtended;
	}
}
