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

package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

	public static void addToHiddenDati(TipoOperazione tipoOperazione,Vector<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException{
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
	
	public static List<Parameter> getTitle(Object object, ConsoleHelper consoleHelper) throws Exception {
		
//		PortaApplicativa pa = (PortaApplicativa) object;
//		String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(pa.getTipoSoggettoProprietario());
//		String tmpTitle = consoleHelper.getLabelNomeSoggetto(protocollo, pa.getTipoSoggettoProprietario() , pa.getNomeSoggettoProprietario());
		
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idAsps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		if(idAsps == null) 
			idAsps = "";
		
		PorteApplicativeHelper paHelper = new PorteApplicativeHelper(consoleHelper.getCore(), consoleHelper.getRequest(), consoleHelper.getPd(), consoleHelper.getSession());
		
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, consoleHelper.getSession(), consoleHelper.getRequest());
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		
		List<Parameter> list = paHelper.getTitoloPA(parentPA, idsogg, idAsps);
		
		/*
		List<Parameter> list = new ArrayList<Parameter>();
		
		switch (parentPA) {
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE:
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(consoleHelper.getCore());
			// Prendo il nome e il tipo del servizio
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = consoleHelper.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
			
			list.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			list.add(new Parameter(servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, pIdServizio,pNomeServizio, pTipoServizio));
			list.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_APPLICATIVE, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_LIST ,pIdServizio,pNomeServizio, pTipoServizio, pIdsoggErogatore));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_SOGGETTO:
			list.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			list.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)));
			break;
		case PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE:
		default:
			list.add(new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST));
			break;
		}
		*/
		return list;
		
	}
	
	public static Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		
		String idPorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
		String idsogg = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
		String idasps = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
		String nomePorta = consoleHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
		
		List<Parameter> list = new ArrayList<Parameter>();
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
