/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.servlet.connettori;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * connettorePropList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConnettorePropList extends Action {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);
			
			ConnettoriCore connettoriCore = new ConnettoriCore();
			SoggettiCore soggettiCore = new SoggettiCore(connettoriCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(connettoriCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(connettoriCore);
			
			String servlet = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
//			String nomeprov = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
//			String tipoprov = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
//			String nomeservizio = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
//			String tiposervizio = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
			String myId = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
//			String correlato = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
//			String idSoggErogatore = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
//			String nomeservizioApplicativo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
			String idsil = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);
			
			String tipoAccordo = request.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
	
			// Preparo il menu
			connettoriHelper.makeMenu();
	
			// Preparo la lista
			Connettore connettore = null;
			org.openspcoop2.core.config.Connettore connettoreC = null;
			if (servlet.equals(AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_EROGATORI_FRUITORI_CHANGE)) {
				int myIdInt = Integer.parseInt(myId);
				Fruitore myAccErFru = apsCore.getErogatoreFruitore(myIdInt);
				connettore = myAccErFru.getConnettore();
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica servizio = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				connettore = servizio.getServizio().getConnettore();
			}
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				int idServizioFruitoreInt = Integer.parseInt(myId);
				Fruitore servFru = apsCore.getServizioFruitore(idServizioFruitoreInt);
				connettore = servFru.getConnettore();
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				connettoreC = is.getConnettore();
			}
			if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				connettoreC = ra.getConnettore();
			}
			if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				connettore = ss.getConnettore();
			}
			
			List<Object> lista = new ArrayList<Object>();
			if (connettore != null) {
				for (int i = 0; i<connettore.sizePropertyList(); i++){
					if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false &&
							connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
						lista.add(connettore.getProperty(i));
					}
				}
			}
			if (connettoreC != null) {
				for (int i = 0; i<connettoreC.sizePropertyList(); i++){
					if(CostantiDB.CONNETTORE_DEBUG.equals(connettoreC.getProperty(i).getNome())==false  &&
							connettoreC.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
						lista.add(connettoreC.getProperty(i));
					}
				}
			}

			connettoriHelper.prepareConnettorePropList(lista, new Search(), 0, tipoAccordo);
	
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.LIST());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.LIST());
		} 
	}
}
