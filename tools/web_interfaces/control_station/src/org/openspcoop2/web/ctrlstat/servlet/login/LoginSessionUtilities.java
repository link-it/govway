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
package org.openspcoop2.web.ctrlstat.servlet.login;

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

	public static void setLoginParametersSession(HttpSession session,ControlStationCore core,String login) throws Exception{
				
		UtentiCore utentiCore = new UtentiCore(core);
		
		Boolean contaListe = core.isShowCountElementInLinkList();
		Boolean showAccordiAzioni = core.isShowAccordiColonnaAzioni();
		Boolean showAccordiPortTypes = core.isShowAccordiColonnaServizi();
		Boolean gestioneInfoProtocol = core.isShowAccordiInformazioniProtocollo();
		Boolean soggVirt = core.isShowGestioneSoggettiVirtuali();
		Boolean confPers = core.isShowConfigurazioniPersonalizzate();
		
		Boolean singlePdD = core.isSinglePdD();
		Boolean generazioneAutomaticaPD = core.isGenerazioneAutomaticaPorteDelegate();
		
		Boolean sameDBWebUI = core.isTracce_sameDBWebUI();
		
		ServletUtils.setUserLoginIntoSession(session, login);
		
		ServletUtils.setContaListeIntoSession(session, contaListe);
				
		User user = utentiCore.getUser(login);
		
		Boolean showAccordiCooperazione = user.getPermessi().isAccordiCooperazione();
		
		ServletUtils.setUserIntoSession(session, user);
				
		InterfaceType gui = user.getInterfaceType();
		switch (gui) {
			case AVANZATA:
			case COMPLETA:
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO, gestioneInfoProtocol);
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI, showAccordiAzioni);
				Utilities.readTipologiaConnettori(core);
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI, soggVirt);
				break;
			case STANDARD:
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO, false);
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI, false);
				Utilities.setTipologiaConnettori(TipologiaConnettori.TIPOLOGIA_CONNETTORI_HTTP);
				session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI, false);
				break;
		}
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_MODALITA_INTERFACCIA, gui.toString() );

		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_PORT_TYPES, showAccordiPortTypes);
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD, singlePdD);
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GENERAZIONE_AUTOMATICA_PORTE_DELEGATE, generazioneAutomaticaPD);
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE, showAccordiCooperazione);
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE, confPers);
		session.setAttribute(CostantiControlStation.SESSION_PARAMETRO_SAME_DB_WEBUI, sameDBWebUI);
	}
	
	
	public static void cleanLoginParametersSession(HttpSession session) throws Exception{
		
		ServletUtils.removeUserLoginFromSession(session);
		
		ServletUtils.removeContaListeFromSession(session);
				
		ServletUtils.removeUserFromSession(session);
				
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_MODALITA_INTERFACCIA);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_PORT_TYPES);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_GENERAZIONE_AUTOMATICA_PORTE_DELEGATE);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_SAME_DB_WEBUI);
		session.removeAttribute(CostantiControlStation.SESSION_PARAMETRO_TIPO_DB);

	}
	
}
