/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.utenti.UtentiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.mvc.login.FailedAttempts;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * LoginHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginHelper extends ConsoleHelper {

	public LoginHelper(HttpServletRequest request, PageData pd,
			HttpSession session) {
		super(request, pd, session);
	}
	public LoginHelper(ControlStationCore core, HttpServletRequest request, PageData pd,
			HttpSession session) {
		super(core, request, pd, session);
	}

	public boolean loginCheckData(LoginTipologia tipoCheck) throws DriverUsersDBException, DriverControlStationException {
		String login = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
		String password = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_PASSWORD);
		return this.loginCheckData(tipoCheck, login, password);
	}
	public static User loginCheckData(Logger log, UtentiCore utentiCore, LoginTipologia tipoCheck, String login, String password, StringBuilder denyReason) throws DriverUsersDBException {

		// Campi obbligatori
		if (login.equals("")) {
			if(denyReason!=null) {
				denyReason.append("Dati incompleti. &Egrave; necessario indicare un "+LoginCostanti.LABEL_USERNAME);
			}
			return null;
		}
		if (tipoCheck.equals(LoginTipologia.WITH_PASSWORD) &&
			password.equals("")) {
			if(denyReason!=null) {
				denyReason.append("Dati incompleti. &Egrave; necessario indicare una Password");
			}
			return null;
		}

		// Se tipoCheck = pw, controllo che login e password corrispondano
		// Se tipoCheck = nopw, mi basta che l'utente sia registrato
		boolean trovato = utentiCore.existsUser(login);
		User u = null;
		if (trovato && tipoCheck.equals(LoginTipologia.WITH_PASSWORD)) {
			// controllo se l'utenza e' da bloccare
			boolean bloccaUtente = FailedAttempts.getInstance().bloccaUtente(log, login);

			if (bloccaUtente) {
				if(denyReason!=null) {
					denyReason.append("Utenza bloccata, superato il numero di tentativi di accesso massimo!");
				}
				return null;
			}

			// Prendo la pw criptata da DB
			u = utentiCore.getUser(login);
			String pwcrypt = u.getPassword();
			if ((pwcrypt != null) && (!pwcrypt.equals(""))) {
				// Controlla se utente e password corrispondono
				trovato = utentiCore.getUtenzePasswordManager().check(password, pwcrypt);
				if(!trovato && utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
					trovato = utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(password, pwcrypt);
				}
			}
		}
		
		//lettura da db in caso di utenza trovata ma senza controllo password
		if (trovato && tipoCheck.equals(LoginTipologia.WITHOUT_PASSWORD)) {
			u = utentiCore.getUser(login);
		}

		if (!trovato) {
			if (tipoCheck.equals(LoginTipologia.WITHOUT_PASSWORD)) {
				if(denyReason!=null) {
					denyReason.append("Login inesistente!");
				}
			} else {
				FailedAttempts.getInstance().aggiungiTentativoFallitoUtente(log, login);
				if(denyReason!=null) {
					denyReason.append("Login o password errata!");
				}
			}
			return null;
		}
		FailedAttempts.getInstance().resetTentativiUtente(log, login);

		return u;
	}
	public boolean loginCheckData(LoginTipologia tipoCheck, String login, String password) throws DriverUsersDBException {
		try{

			StringBuilder denyReason = new StringBuilder();
			User u = loginCheckData(this.log, this.utentiCore, tipoCheck, login, password, denyReason);
			if(u==null) {
				this.pd.setMessage(denyReason.toString(),MessageType.ERROR_SINTETICO);
				return false;
			}

			// controllo modalita' associate all'utenza
			
			if(this.hasOnlyPermessiDiagnosticaReportistica(this.utentiCore.getUser(login))) {
				this.pd.setMessage(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE,MessageType.ERROR_SINTETICO);
				return false;
			}

			if(!u.isConfigurazioneValidaAbilitazioni()) {
				this.pd.setMessage(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO,MessageType.ERROR_SINTETICO);
				return false;
			}

			// setto l utente in sessione
			ServletUtils.setUserIntoSession(this.request, this.session, u);

			return true;

		} catch (DriverUsersDBException e) {
			ControlStationCore.logError("Exception: " + e.getMessage(), e);
			throw e;
		}
	}

	public boolean loginScadenzaPasswordCheckData() throws DriverUsersDBException, DriverControlStationException {
		String login = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
		String password = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_PASSWORD);
		return this.loginScadenzaPasswordCheckData(login, password);
	}
	public boolean loginScadenzaPasswordCheckData(String login, String password) throws DriverUsersDBException {
		try{

			// elimino attributo che abilita il cambio della password
			ServletUtils.removeObjectFromSession(this.request, this.session, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
			// controllo scadenza password
			PasswordVerifier passwordVerifier = this.utentiCore.getUtenzePasswordVerifier();
			if(this.utentiCore.isCheckPasswordExpire(passwordVerifier)) {
				User u = this.utentiCore.getUser(login);
				if(u.isCheckLastUpdatePassword()) {
					StringBuilder bfMotivazioneErrore = new StringBuilder();
					if(passwordVerifier.isPasswordExpire(u.getLastUpdatePassword(), bfMotivazioneErrore)) {
						// imposto attributo che abilita il cambio della password
						ServletUtils.setObjectIntoSession(this.request, this.session, login, LoginCostanti.ATTRIBUTO_MODALITA_CAMBIA_PWD_SCADUTA);
						ServletUtils.removeUserFromSession(this.request, this.session);
						this.pd.setMessage(bfMotivazioneErrore.toString(),MessageType.ERROR_SINTETICO);
						return false;
					}
				}
			}

			return true;

		} catch (DriverUsersDBException e) {
			ControlStationCore.logError("Exception: " + e.getMessage(), e);
			throw e;
		}
	}
	
	public void impostaMessaggioEsitoLoginDaSessione() throws DriverControlStationException {
		this.impostaMessaggioEsitoLoginDaSessione(false);
	}
	
	public void impostaMessaggioEsitoLoginDaSessione(boolean leggiMessaggioDaParametro) throws DriverControlStationException {
		// lettura parametri errore login dalla sessione
		String messageText = ServletUtils.removeObjectFromSession(this.request, this.session, String.class, Costanti.PRINCIPAL_ERROR_MSG);
		if (messageText == null && leggiMessaggioDaParametro) {
			messageText = this.getParameter(Costanti.PARAMETER_MESSAGE_TEXT);
		}
		
		if(messageText == null) {
			messageText = Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE;
		}
		String messageType = this.getParameter(CostantiControlStation.PARAMETER_MESSAGE_TYPE);
		MessageType mt = MessageType.ERROR;
		if(messageType != null) {
			try {
				mt = MessageType.fromValue(messageType);
				if(mt == null)
					mt = MessageType.ERROR;
			}catch(Exception e) {
				mt= MessageType.ERROR;
			}
		}
		String messageTitle = this.getParameter(CostantiControlStation.PARAMETER_MESSAGE_TITLE);
		String messageBreadcrumbs = this.getParameter(CostantiControlStation.PARAMETER_MESSAGE_BREADCRUMB);

		if(messageBreadcrumbs!= null) {
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<>();
			lstParam.add(new Parameter(messageBreadcrumbs, null));
			ServletUtils.setPageDataTitle(this.pd, lstParam);
		}

		// imposto il messaggio da visualizzare
		this.pd.setMessage(messageText, messageTitle, mt);
	}
}
