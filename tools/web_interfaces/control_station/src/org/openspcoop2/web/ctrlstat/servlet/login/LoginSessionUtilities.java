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
package org.openspcoop2.web.ctrlstat.servlet.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.TipologiaConnettori;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * LoginSessionUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginSessionUtilities {

	public static void setLoginParametersSession(HttpServletRequest request, HttpSession session, ControlStationCore core,String login) throws Exception{
				
		UtentiCore utentiCore = new UtentiCore(core);
		
		Boolean contaListe = core.isShowCountElementInLinkList();
		
		ServletUtils.setUserLoginIntoSession(session, login);
		
		ServletUtils.setContaListeIntoSession(session, contaListe);
				
		User user = utentiCore.getUser(login);
		
		ServletUtils.setUserIntoSession(request, session, user);
	
		setLoginParametersSession(request, session, core, user);
	}
	public static void setLoginParametersSession(HttpServletRequest request, HttpSession session, ControlStationCore core, User user) throws Exception{
			
		Boolean showAccordiAzioni = core.isShowAccordiColonnaAzioni();
		Boolean gestioneInfoProtocol = core.isShowAccordiInformazioniProtocollo();
		Boolean soggVirt = core.isShowGestioneSoggettiVirtuali();
		Boolean confPers = core.isShowConfigurazioniPersonalizzate();
		
		Boolean singlePdD = core.isSinglePdD();
		
		Boolean sameDBWebUI = core.isTracceSameDBWebUI();
		
		Boolean showAccordiCooperazione = user.getPermessi().isAccordiCooperazione();
		
		InterfaceType gui = user.getInterfaceType();
		switch (gui) {
			case AVANZATA:
			case COMPLETA:
				ServletUtils.setObjectIntoSession(request, session, gestioneInfoProtocol, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);
				ServletUtils.setObjectIntoSession(request, session, showAccordiAzioni, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI);
				Utilities.readTipologiaConnettori(core);
				ServletUtils.setObjectIntoSession(request, session, soggVirt, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				break;
			case STANDARD:
				ServletUtils.setObjectIntoSession(request, session, false, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);
				ServletUtils.setObjectIntoSession(request, session, false, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI);
				Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
				ServletUtils.setObjectIntoSession(request, session, false, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
				break;
		}
		ServletUtils.setObjectIntoSession(request, session, gui.toString(), CostantiControlStation.SESSION_PARAMETRO_MODALITA_INTERFACCIA);
		
		ServletUtils.setObjectIntoSession(request, session, singlePdD, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
		ServletUtils.setObjectIntoSession(request, session, showAccordiCooperazione, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
		ServletUtils.setObjectIntoSession(request, session, confPers, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		ServletUtils.setObjectIntoSession(request, session, sameDBWebUI, CostantiControlStation.SESSION_PARAMETRO_SAME_DB_WEBUI);
	}
	
	
	public static void cleanLoginParametersSession(HttpServletRequest request, HttpSession session) throws Exception{
		
		ServletUtils.removeUserLoginFromSession(session);
		
		ServletUtils.removeContaListeFromSession(session);
				
		ServletUtils.removeUserFromSession(request, session);
				
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_MODALITA_INTERFACCIA);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_SAME_DB_WEBUI);
		ServletUtils.removeObjectFromSession(request, session, CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

	}
	
}
