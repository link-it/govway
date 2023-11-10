/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * LoginHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoginHelper extends ConsoleHelper {

	public LoginHelper(HttpServletRequest request, PageData pd,
			HttpSession session) throws Exception {
		super(request, pd, session);
	}
	public LoginHelper(ControlStationCore core, HttpServletRequest request, PageData pd,
			HttpSession session) throws Exception {
		super(core, request, pd, session);
	}
	
	public boolean loginCheckData(LoginTipologia tipoCheck) throws Exception {
		String login = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
		String password = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_PASSWORD);
		return this.loginCheckData(tipoCheck, login, password);
	}
	public boolean loginCheckData(LoginTipologia tipoCheck, String login, String password) throws Exception {
		try{
				
			// Campi obbligatori
			if (login.equals("")) {
				this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare un "+LoginCostanti.LABEL_USERNAME,MessageType.ERROR_SINTETICO);
				return false;
			}
			if (tipoCheck.equals(LoginTipologia.WITH_PASSWORD)) {
				if (password.equals("")) {
					this.pd.setMessage("Dati incompleti. &Egrave; necessario indicare una Password",MessageType.ERROR_SINTETICO);
					return false;
				}
			}

			// Se tipoCheck = pw, controllo che login e password corrispondano
			// Se tipoCheck = nopw, mi basta che l'utente sia registrato
			boolean trovato = this.utentiCore.existsUser(login);
			User u = null;
			if (trovato && tipoCheck.equals(LoginTipologia.WITH_PASSWORD)) {
				// Prendo la pw criptata da DB
				u = this.utentiCore.getUser(login);
				String pwcrypt = u.getPassword();
				if ((pwcrypt != null) && (!pwcrypt.equals(""))) {
					// Controlla se utente e password corrispondono
					trovato = this.utentiCore.getUtenzePasswordManager().check(password, pwcrypt);
					if(!trovato && this.utentiCore.getUtenzePasswordManagerBackwardCompatibility()!=null) {
						trovato = this.utentiCore.getUtenzePasswordManagerBackwardCompatibility().check(password, pwcrypt);
					}
				}
			}

			if (!trovato) {
				if (tipoCheck.equals(LoginTipologia.WITHOUT_PASSWORD)) {
					this.pd.setMessage("Login inesistente!",MessageType.ERROR_SINTETICO);
				} else {
					this.pd.setMessage("Login o password errata!",MessageType.ERROR_SINTETICO);
				}
				return false;
			}
			
			// controllo modalita' associate all'utenza
			if(trovato) {
				
				User uCheck = u;
				if(uCheck == null) {
					uCheck = this.utentiCore.getUser(login);
				}
				
				if(this.hasOnlyPermessiDiagnosticaReportistica(this.utentiCore.getUser(login))) {
					this.pd.setMessage(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE,MessageType.ERROR_SINTETICO);
					return false;
				}
				
				if(!uCheck.isConfigurazioneValidaAbilitazioni()) {
					this.pd.setMessage(LoginCostanti.MESSAGGIO_ERRORE_UTENTE_NON_ABILITATO_UTILIZZO_CONSOLE_CONFIGURAZIONE_NON_CORRETTO,MessageType.ERROR_SINTETICO);
					return false;
				}
			}

			// setto l utente in sessione
			ServletUtils.setUserIntoSession(this.request, this.session, u);
			
			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}

	public boolean loginScadenzaPasswordCheckData(LoginTipologia tipoCheck) throws Exception {
		String login = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
		String password = this.getParameter(LoginCostanti.PARAMETRO_LOGIN_PASSWORD);
		return this.loginScadenzaPasswordCheckData(tipoCheck, login, password);
	}
	public boolean loginScadenzaPasswordCheckData(LoginTipologia tipoCheck, String login, String password) throws Exception {
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

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
