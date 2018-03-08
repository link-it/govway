/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
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

	public static void addToHiddenDati(TipoOperazione tipoOperazione,Vector<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException{
		try {
			String id = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String idsogg = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			PorteDelegateHelper porteDelegateHelper = (PorteDelegateHelper)consoleHelper;
			porteDelegateHelper.addHiddenFieldsToDati(tipoOperazione, id, idsogg, null, dati);
		}catch(Exception e) {
			throw new ExtendedException(e.getMessage(),e);
		}
	}
	
	public static Object getObject(ConsoleHelper consoleHelper) throws Exception {
		PorteDelegateCore porteDelegateCore = (PorteDelegateCore) consoleHelper.getCore();
		String id = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		int idInt = Integer.parseInt(id);
		return porteDelegateCore.getPortaDelegata(idInt);
	}
	
	public static List<Parameter> getTitle(Object object, ConsoleHelper consoleHelper) throws Exception {
		// PortaDelegata pd = (PortaDelegata) object;
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, consoleHelper.getSession());
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
		List<Parameter> lstParam = new ArrayList<>();
		
		String idSoggettoFruitore = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

		String idAsps = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
		if(idAsps == null)
			idAsps = "";
		
		String idFruizione = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
		if(idFruizione == null)
			idFruizione = "";
		
		SoggettiCore soggettiCore = new SoggettiCore(consoleHelper.getCore());
		String tipoSoggettoFruitore = null;
		String nomeSoggettoFruitore = null;
		if(soggettiCore.isRegistroServiziLocale()){
			org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoFruitore));
			tipoSoggettoFruitore = soggettoFruitore.getTipo();
			nomeSoggettoFruitore = soggettoFruitore.getNome();
		}else{
			org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggettoFruitore));
			tipoSoggettoFruitore = soggettoFruitore.getTipo();
			nomeSoggettoFruitore = soggettoFruitore.getNome();
		}
		
		String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoFruitore);
		
		switch (parentPD) {
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE:
			// Prendo il nome e il tipo del servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(consoleHelper.getCore()); 
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			String servizioTmpTile = consoleHelper.getLabelIdServizio(asps);
			Parameter pIdServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId()+ "");
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, asps.getIdSoggetto()+"");
			Parameter pIdFruizione = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idFruizione+ "");
			Parameter pIdSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO, idSoggettoFruitore);
			Parameter pIdProviderSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE, idSoggettoFruitore);
			
			String fruizioneTmpTile = consoleHelper.getLabelNomeSoggetto(protocollo, tipoSoggettoFruitore,nomeSoggettoFruitore);
			
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + servizioTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST , pIdServizio,pIdSoggettoErogatore));
			lstParam.add(new Parameter(fruizioneTmpTile, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, pIdServizio,pIdFruizione,pIdProviderSoggettoFruitore));
			lstParam.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_PORTE_DELEGATE, 
					AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_PORTE_DELEGATE_LIST ,pIdFruizione,pIdServizio,pIdSoggettoFruitore));
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_SOGGETTO:
			String soggettoTitle =   consoleHelper.getLabelNomeSoggetto(protocollo, tipoSoggettoFruitore,nomeSoggettoFruitore);
			lstParam.add(new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + soggettoTitle, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST ,
					new Parameter( PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idSoggettoFruitore)));
			break;
		case PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE:
		default:
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST));
			break;
		}
		return lstParam;
		
	}
	
	public static Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception {
		
		String id = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		
		Parameter[] par = new Parameter[2];
		par[0] = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
		par[1] = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
		return par;
		
	}
	
	public static UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception {
		String id = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id));
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_CHANGE);
		return urlExtended;
	}
	
	public static UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper) throws Exception {
		String id = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
		String idsogg = consoleHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
		UrlParameters urlExtended = new UrlParameters();
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id));
		urlExtended.addParameter(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg));
		urlExtended.setUrl(PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_EXTENDED_LIST);
		return urlExtended;
	}
}
