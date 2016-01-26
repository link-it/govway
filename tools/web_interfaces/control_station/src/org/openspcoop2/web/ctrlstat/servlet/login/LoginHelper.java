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
package org.openspcoop2.web.ctrlstat.servlet.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
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
	
	public boolean loginCheckData(LoginTipologia tipoCheck) throws Exception {
		try{
			String login = this.request.getParameter(LoginCostanti.PARAMETRO_LOGIN_LOGIN);
			String password = this.request.getParameter(LoginCostanti.PARAMETRO_LOGIN_PASSWORD);

			// Campi obbligatori
			if (login.equals("")) {
				this.pd.setMessage("Dati incompleti. E' necessario indicare una Login");
				return false;
			}
			if (tipoCheck.equals(LoginTipologia.WITH_PASSWORD)) {
				if (password.equals("")) {
					this.pd.setMessage("Dati incompleti. E' necessario indicare una Password");
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
					trovato = this.passwordManager.checkPw(password, pwcrypt);
				}
			}

			if (!trovato) {
				if (tipoCheck.equals(LoginTipologia.WITHOUT_PASSWORD)) {
					this.pd.setMessage("Login inesistente!");
				} else {
					this.pd.setMessage("Login o password errata!");
				}
				return false;
			}

			// setto l utente in sessione
			ServletUtils.setUserIntoSession(this.session, u);

			return true;

		} catch (Exception e) {
			this.log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
}
