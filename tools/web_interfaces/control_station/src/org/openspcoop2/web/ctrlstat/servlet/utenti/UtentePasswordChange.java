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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.gestori.GestoreConsistenzaDati;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginSessionUtilities;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserPassword;

/**
 * UtentePasswordChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtentePasswordChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);

			String newpw = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_NUOVA_PASSWORD);
			String first = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_FIRST);

			PageData oldPD = ServletUtils.getPageDataFromSession(session);
			
			UtentiCore utentiCore = new UtentiCore();
			
			String userToUpdate = ServletUtils.getObjectFromSession(request, session, String.class, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
			
			User user = utentiCore.getUser(userToUpdate);
			
			if(utentiHelper.isEditModeInProgress()){
				
				pd.setIncludiMenuLateraleSx(false);
				
				if(first == null) {
					pd.setMessage(oldPD.getMessage(), MessageType.fromValue(oldPD.getMessageType()));
				}
				
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				utentiHelper.addUtenteChangePasswordScadutaToDati(dati, TipoOperazione.CHANGE);
				
				pd.setDati(dati);
		
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTE_PASSWORD,ForwardParams.CHANGE());
			}
			
			
			// controllo della password inserita
			boolean isOk = utentiHelper.changePwScadutaCheckData();
			if (!isOk) {
				
				pd.setIncludiMenuLateraleSx(false);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtenteChangePasswordScadutaToDati(dati, TipoOperazione.CHANGE);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTE_PASSWORD,ForwardParams.CHANGE());

			}
			
			User myS = null;
			//tutto ok modifico pwd
			// Cripto la nuova password
			newpw = utentiCore.getUtenzePasswordManager().crypt(newpw);

			// Modifico i dati della pw nel db
			myS = utentiCore.getUser(userToUpdate);
			
			
			PasswordVerifier passwordVerifier = utentiCore.getUtenzePasswordVerifier();
			
			// aggiornamento della password nello storico e nella data 
			if(passwordVerifier.isHistory()) {
				List<UserPassword> precedentiPassword = myS.getPrecedentiPassword();
				
				UserPassword userPassword = new UserPassword();
				userPassword.setDatePassword(user.getLastUpdatePassword());
				userPassword.setPassword(user.getPassword());
				precedentiPassword.add(userPassword );
			}
			
			myS.setLastUpdatePassword(new Date());
			myS.setPassword(newpw);

			utentiCore.performUpdateOperation(userToUpdate, utentiHelper.smista(), myS);
			
			
			// eliminare stato password scaduta dalla sessione
			ServletUtils.removeObjectFromSession(session, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);

			// porto l'utente dove andrebbe dopo il login effettuato con successo.
			LoginSessionUtilities.setLoginParametersSession(session, utentiCore, userToUpdate);
			utentiHelper.updateTipoInterfaccia();
			
			utentiCore.performAuditLogin(userToUpdate);
			
			// Preparo il menu
			utentiHelper.makeMenu();
	
			// Inizializzo parametri di ricerca
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			utentiHelper.initializeFilter(ricerca);
			
			// Boolean verifico configurazione
			StringBuilder verificaConfigurazioneProtocolli = new StringBuilder();
			boolean configurazioneCorretta = utentiCore.verificaConfigurazioneProtocolliRispettoSoggettiDefault(verificaConfigurazioneProtocolli); 
			
			if(!configurazioneCorretta) {
				pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO+"<br/><br/><b>Attenzione</b>: il controllo di consistenza tra Profili di Interoperabilità attivati e la configurazione sul Gateway ha rilevato inconsistenze: \n"+verificaConfigurazioneProtocolli.toString(),
						Costanti.MESSAGE_TYPE_ERROR);
			}
			else if(GestoreConsistenzaDati.gestoreConsistenzaDatiEseguitoConErrore){
				pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO+"<br/><br/><b>Attenzione</b>: il controllo sulla consistenza dei dati è terminato con errore; esaminare i log per maggiori dettagli",
						Costanti.MESSAGE_TYPE_INFO);
			}
			else{
				pd.setMessage(LoginCostanti.LABEL_LOGIN_PASSWORD_AGGIORNATA_CON_SUCCESSO,Costanti.MESSAGE_TYPE_INFO_SINTETICO);
			}

			// Reinit general data per aggiornare lo stato della barra dell'header a dx.
			gd = generalHelper.initGeneralData(request);

			// Refresh Menu' Preparo il menu
			utentiHelper.makeMenu();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTE_PASSWORD, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, UtentiCostanti.OBJECT_NAME_UTENTE_PASSWORD, ForwardParams.CHANGE());
		}
	}
}
