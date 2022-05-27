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


package org.openspcoop2.web.ctrlstat.servlet.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.gestori.GestoreConsistenzaDati;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * login
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Login extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo PageData
		PageData pd = generalHelper.initPageData();

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			// Annullo quanto letto sull'auditing
			ControlStationCore.clearAuditManager();
			
			LoginHelper loginHelper = new LoginHelper(request, pd, session);
	
			String login = loginHelper.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
	
			if(GestoreConsistenzaDati.gestoreConsistenzaDatiInEsecuzione){
				
				pd.setMessage(LoginCostanti.MESSAGGIO_INFO_CONTROLLO_CONSISTENZA_DATI_IN_CORSO, Costanti.MESSAGE_TYPE_INFO);
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, LoginCostanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());

			}
			
			// Se login = null, devo visualizzare la pagina per l'inserimento dati
			if (login == null) {
	
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, LoginCostanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
					
			}
	
			// Controlli sui campi immessi
			boolean isOk = loginHelper.loginCheckData(LoginTipologia.WITH_PASSWORD);
			if (!isOk) {
	
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, LoginCostanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());

			}
			
			// controllo scadenza password
			isOk = loginHelper.loginScadenzaPasswordCheckData(LoginTipologia.WITH_PASSWORD);
			if (!isOk) {
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				ServletUtils.setObjectIntoSession(request, session, pd, Costanti.SESSION_ATTRIBUTE_PAGE_DATA_REDIRECT);
				
				return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTE_PASSWORD, ForwardParams.CHANGE());

			}
	
			LoginCore loginCore = new LoginCore();
			
			LoginSessionUtilities.setLoginParametersSession(request, session, loginCore, login);
//			loginHelper.updateTipoInterfaccia();
			
			loginCore.performAuditLogin(login);
			
			// Preparo il menu
			loginHelper.makeMenu();
	
			// Inizializzo parametri di ricerca
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);
			loginHelper.initializeFilter(ricerca);
			
			// Boolean verifico configurazione
			Login.impostaMessaggioEsitoLogin(pd, loginCore);
	
			// Inizializzo di nuovo GeneralData, dopo aver messo
			// in sessione la login dell'utente
			gd = generalHelper.initGeneralData(request);
	
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd, true);
	
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, LoginCostanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					LoginCostanti.OBJECT_NAME_LOGIN, ForwardParams.LOGIN());
		}
	}

	public static void impostaMessaggioEsitoLogin(PageData pd, LoginCore loginCore) {
		StringBuilder verificaConfigurazioneProtocolli = new StringBuilder();
		boolean configurazioneCorretta = loginCore.verificaConfigurazioneProtocolliRispettoSoggettiDefault(verificaConfigurazioneProtocolli); 
		
		if(!configurazioneCorretta) {
			pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO+"<br/><br/><b>Attenzione</b>: il controllo di consistenza tra Profili di Interoperabilità attivati e la configurazione sul Gateway ha rilevato inconsistenze: \n"+verificaConfigurazioneProtocolli.toString(),
					Costanti.MESSAGE_TYPE_ERROR);
		}
		else if(GestoreConsistenzaDati.gestoreConsistenzaDatiEseguitoConErrore){
			pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO+"<br/><br/><b>Attenzione</b>: il controllo sulla consistenza dei dati è terminato con errore; esaminare i log per maggiori dettagli",
					Costanti.MESSAGE_TYPE_INFO);
		}
		else{
			pd.setMessage(LoginCostanti.LABEL_LOGIN_EFFETTUATO_CON_SUCCESSO,Costanti.MESSAGE_TYPE_INFO_SINTETICO);
		}
	}
}
