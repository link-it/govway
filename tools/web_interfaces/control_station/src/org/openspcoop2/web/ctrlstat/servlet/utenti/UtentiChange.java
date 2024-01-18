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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.OggettoDialogEnum;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.login.LoginSessionUtilities;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
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
import org.openspcoop2.web.lib.users.dao.Stato;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserObjects;

/**
 * suChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtentiChange extends Action {

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
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
			
			// Preparo il menu
			utentiHelper.makeMenu();

			UtentiCore utentiCore = new UtentiCore();
			SoggettiCore soggettiCore = new SoggettiCore(utentiCore);
			
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String pwsu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_PW);
			String confpwsu = null; 
			String tipoGui = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_TIPO_GUI);

			String isServizi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SERVIZI);
			String isDiagnostica = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_DIAGNOSTICA);
			String isReportistica = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_REPORTISTICA);
			String isSistema = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_SISTEMA);
			String isMessaggi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_MESSAGGI);
			String isUtenti = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_UTENTI);
			String isAuditing = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_AUDITING);
			String isAccordiCooperazione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_IS_ACCORDI_COOPERAZIONE);

			String singleSuServizi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			String singleSuAccordiCooperazione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
			String changepwd = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_CHANGE_PW);

			String isSoggettiAll = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SOGGETTI_ALL);
			String isServiziAll = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_ABILITAZIONI_SERVIZI_ALL);
			
			String tipoModalitaConsoleGestione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA);
			String idSoggettoConsoleGestione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO);
			
			String tipoModalitaConsoleMonitoraggio = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR);
			String idSoggettoConsoleMonitoraggio = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTE_ID_SOGGETTO_MONITOR);
			
			String homePageMonitoraggio = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO);
			String intervalloTemporaleHomePageConsoleMonitoraggio = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO);
			
			String scadenza = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SCADENZA);
			
			Boolean singlePdD = ServletUtils.getBooleanAttributeFromSession(CostantiControlStation.SESSION_PARAMETRO_SINGLE_PDD, session, request).getValue();
			
			List<String> protocolliRegistratiConsole = utentiCore.getProtocolli();
			
			String [] modalitaScelte = new String[protocolliRegistratiConsole.size()]; 
			for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
				String protocolloName = protocolliRegistratiConsole.get(i);
				modalitaScelte[i] = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX + protocolloName);
			}
			
			// Prendo l'utente
			User user = utentiCore.getUser(nomesu);
			Date dataUltimoAggiornamentoPassword = user.getLastUpdatePassword();
			boolean oldScadenza = user.isCheckLastUpdatePassword();
			//Prendo i vecchi dati dell'utente
			
			// Check multitenant
						
			List<String> oldProtocolliSupportati = user.getProtocolliSupportati();
			boolean oldHasOnlyPermessiUtenti = user.hasOnlyPermessiUtenti();
			List<String> protocolliSupportati = null;
			boolean first = false;
			if(tipoGui == null) {
				tipoGui = user.getInterfaceType().toString();
				
				if(!oldHasOnlyPermessiUtenti) {
					oldProtocolliSupportati = user.getProtocolliSupportati();
					// dal db ho letto un utente che non ha associato nessun protocollo, glieli associo tutti di default
					if(oldProtocolliSupportati == null) {
						oldProtocolliSupportati = new ArrayList<>();
						for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
							String protocolloName = protocolliRegistratiConsole.get(i);
							oldProtocolliSupportati.add(protocolloName);
							modalitaScelte[i] = Costanti.CHECK_BOX_ENABLED;
						}
					}
				} else {
					// utente configurato solo per gestire gli utenti
					oldProtocolliSupportati = new ArrayList<>();
				}
				protocolliSupportati  = oldProtocolliSupportati;
				first = true;
				
				if(tipoModalitaConsoleGestione == null) {
					tipoModalitaConsoleGestione =  user.getProtocolloSelezionatoPddConsole();
				}
				
				// nessun profilo selezionato imposto all
				if(tipoModalitaConsoleGestione == null) {
					tipoModalitaConsoleGestione = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				
				if(idSoggettoConsoleGestione == null) {
					idSoggettoConsoleGestione = user.getSoggettoSelezionatoPddConsole();
				}
				
				// nessun soggetto selezionato imposto all
				if(idSoggettoConsoleGestione == null) {
					idSoggettoConsoleGestione = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				if(tipoModalitaConsoleMonitoraggio == null) {
					tipoModalitaConsoleMonitoraggio =  user.getProtocolloSelezionatoPddMonitor();
				}
				
				// nessun profilo selezionato imposto all
				if(tipoModalitaConsoleMonitoraggio == null) {
					tipoModalitaConsoleMonitoraggio = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				
				if(idSoggettoConsoleMonitoraggio == null) {
					idSoggettoConsoleMonitoraggio = user.getSoggettoSelezionatoPddMonitor();
				}
				
				// nessun soggetto selezionato imposto all
				if(idSoggettoConsoleMonitoraggio == null) {
					idSoggettoConsoleMonitoraggio = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				List<Stato> stati = user.getStati();
				
				if(homePageMonitoraggio == null) {
					for (Stato stato : stati) {
						if(stato.getOggetto().equals(UtentiCostanti.OGGETTO_STATO_UTENTE_HOME_PAGE)) {
							homePageMonitoraggio = utentiHelper.extractValoreStato(stato.getStato());
							break;
						}
					}
				}
				
				// nessuna home page selezionata imposto transazioni
				if(homePageMonitoraggio == null) {
					homePageMonitoraggio = UtentiCostanti.VALUE_PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO_TRANSAZIONI;
				}
				
				if(intervalloTemporaleHomePageConsoleMonitoraggio == null) {
					for (Stato stato : stati) {
						if(stato.getOggetto().equals(UtentiCostanti.OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE)) {
							intervalloTemporaleHomePageConsoleMonitoraggio = utentiHelper.extractValoreStato(stato.getStato());
							break;
						}
					}
				}
				
				// nessuna home page selezionata imposto transazioni
				if(intervalloTemporaleHomePageConsoleMonitoraggio == null) {
					intervalloTemporaleHomePageConsoleMonitoraggio = UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI;
				}
			}
			
			InterfaceType interfaceType = InterfaceType.convert(tipoGui, true);
			
			String postBackElementName = utentiHelper.getPostBackElementName();
			
			if (postBackElementName != null) {
				
				// selezione modalita'
				if(postBackElementName.startsWith(UtentiCostanti.PARAMETRO_UTENTI_MODALITA_PREFIX)) {
					tipoModalitaConsoleGestione = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
					idSoggettoConsoleGestione = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
					tipoModalitaConsoleMonitoraggio = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
					idSoggettoConsoleMonitoraggio = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				// cambio del profilo, reset del valore del soggetto
				if(postBackElementName.equals(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA)) {
					idSoggettoConsoleGestione = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				// cambio del profilo, reset del valore del soggetto
				if(postBackElementName.equals(UtentiCostanti.PARAMETRO_UTENTE_TIPO_MODALITA_MONITOR)) {
					idSoggettoConsoleMonitoraggio = UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL;
				}
				
				// cambio della home page, reset del valore del grafico
				if(postBackElementName.equals(UtentiCostanti.PARAMETRO_UTENTI_HOME_PAGE_MONITORAGGIO)) {
					intervalloTemporaleHomePageConsoleMonitoraggio = UtentiCostanti.VALUE_PARAMETRO_UTENTI_INTERVALLO_TEMPORALE_HOME_PAGE_MONITORAGGIO_ULTIMI_7_GIORNI;
				}
			}

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento dati
			if(utentiHelper.isEditModeInProgress()){

				// setto la barra del titolo

				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(nomesu,null));


				PermessiUtente pu = user.getPermessi();

				isServizi = (isServizi==null) ? (pu.isServizi() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isServizi;
				isDiagnostica = (isDiagnostica==null) ? (pu.isDiagnostica() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isDiagnostica;
				isReportistica = (isReportistica==null) ? (pu.isReportistica() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isReportistica;
				isSistema = (isSistema==null) ? (pu.isSistema() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isSistema;
				isMessaggi = (isMessaggi==null) ? (pu.isCodeMessaggi() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isMessaggi;
				isUtenti = (isUtenti==null) ? (pu.isUtenti() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isUtenti;
				isAuditing = (isAuditing==null) ? (pu.isAuditing() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isAuditing;
				isAccordiCooperazione = (isAccordiCooperazione==null) ? (pu.isAccordiCooperazione() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isAccordiCooperazione;
				
				isSoggettiAll = (isSoggettiAll==null) ? (user.isPermitAllSoggetti() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isSoggettiAll;
				isServiziAll = (isServiziAll==null) ? (user.isPermitAllServizi() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : isServiziAll;
								
				scadenza = (scadenza == null) ? (user.isCheckLastUpdatePassword() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED) : scadenza;
				
				if(first) {
					for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
						String protocolloName = protocolliRegistratiConsole.get(i);
						if(modalitaScelte[i] == null &&
							protocolliSupportati.contains(protocolloName)) {
							modalitaScelte[i] = Costanti.CHECK_BOX_ENABLED;
						} 
					}
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtentiToDati(dati, TipoOperazione.CHANGE, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isReportistica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						changepwd,modalitaScelte, isSoggettiAll, isServiziAll, user, scadenza, dataUltimoAggiornamentoPassword, oldScadenza, 
						tipoModalitaConsoleGestione, idSoggettoConsoleGestione, tipoModalitaConsoleMonitoraggio, idSoggettoConsoleMonitoraggio,
						homePageMonitoraggio, intervalloTemporaleHomePageConsoleMonitoraggio);

				pd.setDati(dati);

				if(first &&
					!user.isConfigurazioneValidaAbilitazioni()) {
					if(!user.isConfigurazioneValidaSoggettiAbilitati()) {
						pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_UPDATE_NOTE,MessageType.INFO);
					}
					else if(!user.isConfigurazioneValidaServiziAbilitati()) {
						pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_UPDATE_NOTE,MessageType.INFO);
					}
				}
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = utentiHelper.utentiCheckData(TipoOperazione.CHANGE,singlePdD,oldProtocolliSupportati,oldHasOnlyPermessiUtenti);
			if (!isOk) {

				// setto la barra del titolo

				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI, UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(nomesu,null));

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addUtentiToDati(dati, TipoOperazione.CHANGE, singlePdD,
						nomesu,pwsu,confpwsu,interfaceType,
						isServizi,isDiagnostica,isReportistica,isSistema,isMessaggi,isUtenti,isAuditing,isAccordiCooperazione,
						changepwd,modalitaScelte, isSoggettiAll, isServiziAll, user, scadenza, dataUltimoAggiornamentoPassword, oldScadenza, 
						tipoModalitaConsoleGestione, idSoggettoConsoleGestione, tipoModalitaConsoleMonitoraggio, idSoggettoConsoleMonitoraggio,
						homePageMonitoraggio, intervalloTemporaleHomePageConsoleMonitoraggio);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
			}
			

			// Se singleSuServizi != null, controllo che il superutente non sia
			// quello che sto modificando
			// Se singleSu = null, controllo se l'utente aveva permessi S
			List<String> usersWithS = utentiCore.getUsersWithType(Permessi.SERVIZI.toString());

			String[] uws = null;
			if (usersWithS != null && !usersWithS.isEmpty()) {
				List<String> usersWithPermessoS = new ArrayList<>();
				Iterator<String> itUWS = usersWithS.iterator();
				while (itUWS.hasNext()) {
					String singleUWS = itUWS.next();
					if (!nomesu.equals(singleUWS)) {
						usersWithPermessoS.add(singleUWS);
					}
				}
				if(!usersWithPermessoS.isEmpty()){
					uws = new String[1];
					uws = usersWithPermessoS.toArray(uws);
				}
			}

			// Se singleSuAccordi = null, controllo se l'utente aveva permessi P
			List<String> usersWithP = utentiCore.getUsersWithType(Permessi.ACCORDI_COOPERAZIONE.toString());

			String[] uwp = null;
			if (usersWithP != null && !usersWithP.isEmpty()) {
				List<String> usersWithPermessoP = new ArrayList<>();
				Iterator<String> itUWS = usersWithP.iterator();
				while (itUWS.hasNext()) {
					String singleUWP = itUWS.next();
					if (!nomesu.equals(singleUWP)) {
						usersWithPermessoP.add(singleUWP);
					}
				}
				if(!usersWithPermessoP.isEmpty()){
					uwp = new String[1];
					uwp = usersWithPermessoP.toArray(uwp);
				}
			}

			// check input relativo ai servizi
			String msgServizi = "";
			boolean paginaSuServizi = false;

			if(!utentiCore.isVisioneOggettiGlobaleIndipendenteUtente()) {
				if (singleSuServizi != null) {
					if (nomesu.equals(singleSuServizi)) {
						paginaSuServizi = true;
						msgServizi = "Scegliere un utente con il permesso 'Servizi' che non sia quello che sto modificando<br>";
					}
				} else {
					if ((isServizi == null || !isServizi.equals("yes")) &&
							usersWithS.contains(nomesu)) {
						if(uws==null){
	
							List<DataElement> dati = new ArrayList<>();
	
							dati.add(ServletUtils.getDataElementForEditModeFinished());
	
							pd.disableEditMode();
	
							pd.setDati(dati);
	
							// Preparo il menu
							pd.setMessage("Non è possibile eliminare il permesso 'Servizi', poichè non esistono altri utenti con tale permesso");
	
							ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
	
							return ServletUtils.getStrutsForwardGeneralError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());	
	
						}else{
							paginaSuServizi = true;
							msgServizi = "Scegliere un utente a cui verranno assegnati tutti gli oggetti appartenenti all'utente '"+nomesu+
									"'.<br>L'operazione si è resa necessaria in seguito all'eliminazione del permesso di gestione 'Servizi' (S).<br>";
						}
					}
				}
			}

			String msgAccordi = "";
			boolean paginaSuAccordi = false;
			boolean checkOggettiAccordi = false;
			if(!utentiCore.isVisioneOggettiGlobaleIndipendenteUtente()) {
				if (singleSuAccordiCooperazione != null) {
					if (nomesu.equals(singleSuAccordiCooperazione)) {
						paginaSuAccordi = true;
						msgAccordi = "Scegliere un utente con il permesso 'Accordi Cooperazione' che non sia quello che sto modificando<br>";
					}
				} else {
					if ((isAccordiCooperazione == null || !isAccordiCooperazione.equals("yes")) &&
							usersWithP.contains(nomesu)) {
						if(uwp==null){
							// controllare che l'utente possieda degli oggetti
							checkOggettiAccordi = true;
						}else{
							paginaSuAccordi = true;
							msgAccordi = (!msgServizi.contains("Scegliere un utente")
									? ("Scegliere un utente a cui verranno assegnati tutti gli oggetti appartenenti all'utente '"+nomesu+	"'.<br>") : "")
									+"L'operazione si è resa necessaria in seguito all'eliminazione del permesso di gestione 'Accordi Cooperazione' (P).<br>";
						}
					}
				}
			}


			boolean paginaSu = paginaSuAccordi || paginaSuServizi;
			// CHECK
			String msg = msgServizi + msgAccordi;

			// Se paginaSu = true, propongo una pagina in cui faccio scegliere
			// un superutente con permessi S e/o P a cui assegnare gli oggetti
			// dell'utente che sto modificando
			// In caso contrario, modifico l'utente, assegnando gli eventuali
			// oggetti a singleSu
			if (paginaSu) {
				// Faccio scegliere il superutente a cui assegnare gli oggetti

				// setto la barra del titolo

				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI, UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(nomesu,null));

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				utentiHelper.addChangeUtenteInfoToDati(dati, nomesu, changepwd, pwsu, confpwsu, interfaceType, 
						isServizi, isDiagnostica, isReportistica, isSistema, isMessaggi, isUtenti, isAuditing,isAccordiCooperazione,paginaSuServizi, 
						uws, paginaSuAccordi, uwp,modalitaScelte, tipoModalitaConsoleGestione, idSoggettoConsoleGestione, tipoModalitaConsoleMonitoraggio, idSoggettoConsoleMonitoraggio,
						homePageMonitoraggio, intervalloTemporaleHomePageConsoleMonitoraggio);

				pd.setDati(dati);
				pd.setMessage(msg,Costanti.MESSAGE_TYPE_INFO);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE(),UtentiCostanti.STRUTS_FORWARD_INFO);	

			} else {

				boolean secret = false;
				String secretPassword  = pwsu;
				String secretUser = nomesu;
				boolean secretAppId = false;
				
				// Modifico l'utente
				PasswordVerifier passwordVerifier = utentiCore.getUtenzePasswordVerifier();
				if(utentiCore.isCheckPasswordExpire(passwordVerifier)) {
					user.setCheckLastUpdatePassword(ServletUtils.isCheckBoxEnabled(scadenza));
				} else {
					user.setCheckLastUpdatePassword(false);
				}
				
				// Cripto la password
				boolean cpwd = ServletUtils.isCheckBoxEnabled(changepwd);
				if(cpwd && !"".equals(pwsu)){
					if(utentiCore.isUtenzePasswordEncryptEnabled()) {
						secret = true;
						pwsu = utentiCore.getUtenzePasswordManager().crypt(pwsu);
					}
					
					user.setLastUpdatePassword(new Date());
					user.setPassword(pwsu);
				}

				// Modifico i dati dell'utente
				user.setInterfaceType(InterfaceType.valueOf(tipoGui));
				user.setProtocolloSelezionatoPddConsole(!tipoModalitaConsoleGestione.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? tipoModalitaConsoleGestione : null);
				user.setSoggettoSelezionatoPddConsole(!idSoggettoConsoleGestione.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? idSoggettoConsoleGestione : null);
				user.setProtocolloSelezionatoPddMonitor(!tipoModalitaConsoleMonitoraggio.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? tipoModalitaConsoleMonitoraggio : null);
				user.setSoggettoSelezionatoPddMonitor(!idSoggettoConsoleMonitoraggio.equals(UtentiCostanti.VALORE_PARAMETRO_MODALITA_ALL) ? idSoggettoConsoleMonitoraggio : null);
					
				String puString = "";

				if (isServizi != null && ServletUtils.isCheckBoxEnabled(isServizi))
					puString = Permessi.SERVIZI.toString();

				if (isDiagnostica != null && ServletUtils.isCheckBoxEnabled(isDiagnostica)) {
					if (puString.equals(""))
						puString = Permessi.DIAGNOSTICA.toString();
					else
						puString = puString+","+Permessi.DIAGNOSTICA.toString();
				}
				
				if (isReportistica != null && ServletUtils.isCheckBoxEnabled(isReportistica)) {
					if (puString.equals(""))
						puString = Permessi.REPORTISTICA.toString();
					else
						puString = puString+","+Permessi.REPORTISTICA.toString();
				}

				if (isSistema != null && ServletUtils.isCheckBoxEnabled(isSistema)) {
					if (puString.equals(""))
						puString = Permessi.SISTEMA.toString();
					else
						puString = puString+","+Permessi.SISTEMA.toString();
				}

				if (isMessaggi != null && ServletUtils.isCheckBoxEnabled(isMessaggi)) {
					if (puString.equals(""))
						puString = Permessi.CODE_MESSAGGI.toString();
					else
						puString = puString+","+Permessi.CODE_MESSAGGI.toString();
				}

				if (isUtenti != null && ServletUtils.isCheckBoxEnabled(isUtenti)) {
					if (puString.equals(""))
						puString = Permessi.UTENTI.toString();
					else
						puString = puString+","+Permessi.UTENTI.toString();
				}

				if (isAuditing != null && ServletUtils.isCheckBoxEnabled(isAuditing)) {
					if (puString.equals(""))
						puString = Permessi.AUDITING.toString();
					else
						puString = puString+","+Permessi.AUDITING.toString();
				}
				if (isAccordiCooperazione != null && ServletUtils.isCheckBoxEnabled(isAccordiCooperazione)) {
					if (puString.equals(""))
						puString = Permessi.ACCORDI_COOPERAZIONE.toString();
					else
						puString = puString+","+Permessi.ACCORDI_COOPERAZIONE.toString();
				}

				user.setPermessi(PermessiUtente.toPermessiUtente(puString));
				
				user.clearProtocolliSupportati();
				if(user.hasOnlyPermessiUtenti()) {
					user.setProtocolloSelezionatoPddConsole(null); 
					user.setInterfaceType(InterfaceType.STANDARD);
				}
				else {
					// modalita gateway
					for (int i = 0; i < protocolliRegistratiConsole.size() ; i++) {
						String protocolloName = protocolliRegistratiConsole.get(i);
						if(ServletUtils.isCheckBoxEnabled(modalitaScelte[i])) {
							user.addProtocolloSupportato(protocolloName);
						} 
					}
					if(user.getProtocolloSelezionatoPddConsole() != null &&
						!user.getProtocolliSupportati().contains(user.getProtocolloSelezionatoPddConsole())) {
						user.setProtocolloSelezionatoPddConsole(null); 
					}
				}
				
				if (ServletUtils.isCheckBoxEnabled(isDiagnostica) || ServletUtils.isCheckBoxEnabled(isReportistica)) {
					user.setPermitAllSoggetti(ServletUtils.isCheckBoxEnabled(isSoggettiAll));
					// se seleziono il permitall devo cancellare i soggetti selezionati
					if(user.isPermitAllSoggetti() &&
						user.getSoggetti() != null && !user.getSoggetti().isEmpty()) {
						user.getSoggetti().clear();
					}
					user.setPermitAllServizi(ServletUtils.isCheckBoxEnabled(isServiziAll));
					// se seleziono il permitall devo cancellare i servizi selezionati
					if(user.isPermitAllServizi() &&
						user.getServizi() != null && !user.getServizi().isEmpty()) {
						user.getServizi().clear();
					}
					
					// salvataggio homepage e grafico della console di monitoraggio
					boolean homePageFound = false;
					for (Stato stato : user.getStati()) {
						if(stato.getOggetto().equals(UtentiCostanti.OGGETTO_STATO_UTENTE_HOME_PAGE)) {
							stato.setStato(utentiHelper.incapsulaValoreStato(homePageMonitoraggio));
							homePageFound = true;
							break;
						}
					}
					
					if(!homePageFound) {
						Stato statoHomePage = new Stato();
						statoHomePage.setOggetto(UtentiCostanti.OGGETTO_STATO_UTENTE_HOME_PAGE);
						statoHomePage.setStato(utentiHelper.incapsulaValoreStato(homePageMonitoraggio));
						
						user.getStati().add(statoHomePage);
					}
					
					boolean statoIntevalloTemporaleHomePageFound = false;
					
					for (Stato stato : user.getStati()) {
						if(stato.getOggetto().equals(UtentiCostanti.OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE)) {
							stato.setStato(utentiHelper.incapsulaValoreStato(intervalloTemporaleHomePageConsoleMonitoraggio));
							statoIntevalloTemporaleHomePageFound = true;
							break;
						}
					}
					
					if(!statoIntevalloTemporaleHomePageFound) {
						Stato statoIntevalloTemporaleHomePage = new Stato();
						statoIntevalloTemporaleHomePage.setOggetto(UtentiCostanti.OGGETTO_STATO_UTENTE_INTERVALLO_TEMPORALE_HOME_PAGE);
						statoIntevalloTemporaleHomePage.setStato(utentiHelper.incapsulaValoreStato(intervalloTemporaleHomePageConsoleMonitoraggio));
						
						user.getStati().add(statoIntevalloTemporaleHomePage);
					}
				}

				// Se singleSu != null, devo recuperare gli oggetti
				// dell'utente ed assegnarli a singleSu
				List<Object> oggetti = new ArrayList<>();
				List<Integer> tipoModifica = new ArrayList<>();
				if(soggettiCore.isRegistroServiziLocale() &&
					singleSuServizi != null && !singleSuServizi.equals("")) {
					UserObjects results = utentiCore.updateUserServizi(nomesu, singleSuServizi);
					ControlStationCore.logInfo("Modificata utenza ["+nomesu+"]->["+singleSuServizi+"] per permesso relativo ai servizi (L'utenza '"+nomesu+"' verrà modificata per non avere più la gestione dei servizi). Risultati modifica: "+results.toString(false));
				}

				if(soggettiCore.isRegistroServiziLocale() &&
					(singleSuAccordiCooperazione != null && !singleSuAccordiCooperazione.equals("")) || checkOggettiAccordi) {
						
					if(checkOggettiAccordi){
						UserObjects results = utentiCore.countUserCooperazione(nomesu);
						if(results.accordi_accoperazione>0 || results.accordi_parte_comune>0) {
							List<DataElement> dati = new ArrayList<>();
							
							dati.add(ServletUtils.getDataElementForEditModeFinished());

							pd.disableEditMode();

							pd.setDati(dati);

							// Preparo il menu
							pd.setMessage("Non è possibile eliminare il permesso 'Accordi Cooperazione', poichè non esistono altri utenti con tale permesso");

							ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

							return ServletUtils.getStrutsForwardGeneralError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
						}
					}
					
					UserObjects results = utentiCore.updateUserCooperazione(nomesu, singleSuAccordiCooperazione);
					ControlStationCore.logInfo("Modificata utenza ["+nomesu+"]->["+singleSuAccordiCooperazione+"] per permesso relativo agli accordi di cooperazione (L'utenza '"+nomesu+"' verrà modificata per non avere più la gestione degli accordi di cooperazione). Risultati modifica: "+results.toString(true));
				}

				// Alla fine, modifico l'utente
				oggetti.add(user);
				tipoModifica.add(org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation.PERFORM_OPERATION_UPDATE);
				int[] operationTypes = new int[tipoModifica.size()];
				for (int cO = 0; cO < tipoModifica.size(); cO++)
					operationTypes[cO] = tipoModifica.get(cO);
				utentiCore.performOperationMultiTypes(userLogin, utentiHelper.smista(), operationTypes, oggetti.toArray());



				// Se sto modificando l'utente che è anche quello connesso
				if(userLogin.equals(user.getLogin())){

					if(utentiCore.isUtenzeModificaProfiloUtenteDaFormAggiornaSessione()) {
						LoginSessionUtilities.cleanLoginParametersSession(request, session);

						ServletUtils.setUserIntoSession(request, session, user); // update in sessione.
//						utentiHelper.setTipoInterfaccia(user.getInterfaceType()); // update InterfaceType
						LoginSessionUtilities.setLoginParametersSession(request, session, utentiCore, userLogin);
					} else {
						ServletUtils.setUserIntoSession(request, session, user); // update in sessione.
					}
				}

				boolean isLoggedUser = userLogin.equals(user.getLogin());
				
				// Messaggio 'Please Copy'
				if(!isLoggedUser && secret) {
					utentiHelper.setSecretPleaseCopy(secretPassword, secretUser, secretAppId, ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC, OggettoDialogEnum.UTENTE, nomesu);
				}

				// Se ho modificato l'utente loggato non faccio caricare la lista, degli utente ma una
				// pagina di info che indica che l'utente e' stato modificato con successo
				// MOTIVO: Viene effettuato il refresh della colonna a sinistra, se per caso sono cambiati i diritti
				if (isLoggedUser){

					// Reinit general data per aggiornare lo stato della barra dell'header a dx.
					gd = generalHelper.initGeneralData(request);
					
					utentiHelper.makeMenu();

					pd.setMessage("Utente '"+userLogin+"' modificato con successo", Costanti.MESSAGE_TYPE_INFO);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE(),UtentiCostanti.STRUTS_FORWARD_PERMESSI_OK);	

				} else {

					if(!user.isConfigurazioneValidaSoggettiAbilitati()) {
						pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SOGGETTI_DEFINIZIONE_UPDATE_NOTE, MessageType.INFO);
					}
					else if(!user.isConfigurazioneValidaServiziAbilitati()) {
						pd.setMessage(UtentiCostanti.LABEL_ABILITAZIONI_PUNTUALI_SERVIZI_DEFINIZIONE_UPDATE_NOTE, MessageType.INFO);
					}
					
					// Preparo la lista
					int idLista = Liste.SU;

					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class);

					ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);

					List<User> lista = utentiCore.userList(ricerca);

					utentiHelper.prepareUtentiList(ricerca, lista, singlePdD);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
				}
			}
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
		}
	}
}
