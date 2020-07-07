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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.InterfaceType;
import org.openspcoop2.web.lib.users.dao.Permessi;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * suAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtentiAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {
			UtentiCore utentiCore = new UtentiCore();
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
	
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String pwsu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PASSWORD);
			String confpwsu = null; //utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CONFERMA_PASSWORD);
			String tipoGui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);
			
			InterfaceType interfaceType = null;
			if(tipoGui==null) {
				//interfaceType = utentiHelper.getTipoInterfaccia();
				interfaceType = InterfaceType.STANDARD;
			}
			else {
				interfaceType = InterfaceType.convert(tipoGui, true);
			}
			
			String isServizi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
			String isDiagnostica = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
			String isReportistica = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_REPORTISTICA);
			String isSistema = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
			String isMessaggi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
			String isUtenti = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
			String isAuditing = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
			String isAccordiCooperazione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);
			
			String isSoggettiAll = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL);
			String isServiziAll = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SERVIZI_ALL);
			
			Boolean singlePdD = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD);
			
			List<String> protocolliRegistratiConsole = utentiCore.getProtocolli();
		
			String [] modalitaScelte = new String[protocolliRegistratiConsole.size()]; 
			for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
				String protocolloName = protocolliRegistratiConsole.get(i);
				modalitaScelte[i] = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX + protocolloName);
			}
						
			// Preparo il menu
			utentiHelper.makeMenu();
	
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(utentiHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletAdd(pd, UtentiCostanti.LABEL_UTENTI);
				
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						ServletUtils.getParameterAggiungi());
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				if(nomesu==null){
					nomesu = "";
				}
				if(pwsu==null){
					pwsu = "";
				}
//				if(confpwsu==null){
//					confpwsu = "";
//				}
				
				utentiHelper.addUtentiToDati(dati, TipoOperazione.ADD, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isReportistica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						null,modalitaScelte, isSoggettiAll, isServiziAll, null);
				
				pd.setDati(dati);
		
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTI,ForwardParams.ADD());

			}
	
			// Controlli sui campi immessi
			boolean isOk = utentiHelper.utentiCheckData(TipoOperazione.ADD,singlePdD,null,false);
			if (!isOk) {
				
				// setto la barra del titolo
//				ServletUtils.setPageDataTitle_ServletAdd(pd, UtentiCostanti.LABEL_UTENTI);
				
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						ServletUtils.getParameterAggiungi());
	
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
				utentiHelper.addUtentiToDati(dati, TipoOperazione.ADD, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isReportistica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						null,modalitaScelte, isSoggettiAll, isServiziAll, null);
				
				pd.setDati(dati);
	
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
			}
	
			// Cripto la password
			pwsu = utentiCore.getUtenzePasswordManager().crypt(pwsu);
	
			// Inserisco l'utente nel db
			User newU = new User();
			newU.setLogin(nomesu);
			newU.setPassword(pwsu);
			newU.setInterfaceType(InterfaceType.valueOf(tipoGui));
			String puString = "";
			if (ServletUtils.isCheckBoxEnabled(isServizi))
				puString = Permessi.SERVIZI.toString();
			if (ServletUtils.isCheckBoxEnabled(isDiagnostica)) {
				if (puString.equals(""))
					puString = Permessi.DIAGNOSTICA.toString();
				else
					puString = puString+","+Permessi.DIAGNOSTICA.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isReportistica)) {
				if (puString.equals(""))
					puString = Permessi.REPORTISTICA.toString();
				else
					puString = puString+","+Permessi.REPORTISTICA.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isSistema)) {
				if (puString.equals(""))
					puString = Permessi.SISTEMA.toString();
				else
					puString = puString+","+Permessi.SISTEMA.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isMessaggi)) {
				if (puString.equals(""))
					puString = Permessi.CODE_MESSAGGI.toString();
				else
					puString = puString+","+Permessi.CODE_MESSAGGI.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isUtenti)) {
				if (puString.equals(""))
					puString = Permessi.UTENTI.toString();
				else
					puString = puString+","+Permessi.UTENTI.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isAuditing)) {
				if (puString.equals(""))
					puString = Permessi.AUDITING.toString();
				else
					puString = puString+","+Permessi.AUDITING.toString();
			}
			if (ServletUtils.isCheckBoxEnabled(isAccordiCooperazione)) {
				if (puString.equals(""))
					puString = Permessi.ACCORDI_COOPERAZIONE.toString();
				else
					puString = puString+","+Permessi.ACCORDI_COOPERAZIONE.toString();
			}
			newU.setPermessi(PermessiUtente.toPermessiUtente(puString));
			
			newU.clearProtocolliSupportati();
			// modalita gateway
			for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
				String protocolloName = protocolliRegistratiConsole.get(i);
				if(ServletUtils.isCheckBoxEnabled(modalitaScelte[i])) {
					newU.addProtocolloSupportato(protocolloName);
				} 
			}
			
			if (ServletUtils.isCheckBoxEnabled(isDiagnostica) || ServletUtils.isCheckBoxEnabled(isReportistica)) {
				if(utentiCore.isMultitenant()) {
					newU.setPermitAllSoggetti(ServletUtils.isCheckBoxEnabled(isSoggettiAll));
				}
				else {
					newU.setPermitAllSoggetti(true);
				}
				newU.setPermitAllServizi(ServletUtils.isCheckBoxEnabled(isServiziAll));
			}
			
			
			utentiCore.performCreateOperation(userLogin, utentiHelper.smista(), newU);
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	
			int idLista = Liste.SU;
	
			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
	
			List<User> lista = utentiCore.userList(ricerca);
	
			utentiHelper.prepareUtentiList(ricerca, lista, singlePdD);
	
			if(newU.isConfigurazioneValidaSoggettiAbilitati()==false) {
				pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_CREATE_NOTE, MessageType.INFO);
			}
			else if(newU.isConfigurazioneValidaServiziAbilitati()==false) {
				pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_CREATE_NOTE, MessageType.INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.ADD());
		} 
	}
}
