/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.core.config.constants.TipoAutenticazione;

/**
 * CredenzialiInvocazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiInvocazione {

	public static CredenzialiInvocazione getAutenticazioneDisabilitata() {
		CredenzialiInvocazione c = new CredenzialiInvocazione();
		c.setAutenticazione(TipoAutenticazione.DISABILITATO);
		return c;
	}
	public static CredenzialiInvocazione getAutenticazioneBasic(String username, String password) {
		CredenzialiInvocazione c = new CredenzialiInvocazione();
		c.setAutenticazione(TipoAutenticazione.BASIC);
		c.setUsername(username);
		c.setPassword(password);
		return c;
	}
	public static CredenzialiInvocazione getAutenticazionePrincipal(String username, String password) {
		CredenzialiInvocazione c = getAutenticazioneBasic(username, password);
		c.setAutenticazione(TipoAutenticazione.PRINCIPAL);
		return c;
	}
	public static CredenzialiInvocazione getAutenticazioneSsl(String pathKeystore, String passwordKeystore, String passwordKey) {
		CredenzialiInvocazione c = new CredenzialiInvocazione();
		c.setAutenticazione(TipoAutenticazione.SSL);
		c.setPathKeystore(pathKeystore);
		c.setPasswordKeystore(passwordKeystore);
		c.setPasswordKey(passwordKey);
		return c;
	}
	
	
	private TipoAutenticazione autenticazione;
	
	private String username;
	private String password;
	
	String pathKeystore;
	String passwordKeystore;
	String passwordKey;
	
	public boolean isCreateSSLContext() {
		return TipoAutenticazione.SSL.equals(this.autenticazione);
	}
	
	public TipoAutenticazione getAutenticazione() {
		return this.autenticazione;
	}
	public void setAutenticazione(TipoAutenticazione autenticazione) {
		this.autenticazione = autenticazione;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPathKeystore() {
		return this.pathKeystore;
	}
	public void setPathKeystore(String pathKeystore) {
		this.pathKeystore = pathKeystore;
	}
	public String getPasswordKeystore() {
		return this.passwordKeystore;
	}
	public void setPasswordKeystore(String passwordKeystore) {
		this.passwordKeystore = passwordKeystore;
	}
	public String getPasswordKey() {
		return this.passwordKey;
	}
	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}
	
	@Override
	public String toString() {
		StringBuffer bf = new StringBuffer("(");
		bf.append(this.autenticazione);
		if(this.username!=null) {
			bf.append(" username:").append(this.username);
		}
		if(this.password!=null) {
			bf.append(" password:").append(this.password);
		}
		if(this.pathKeystore!=null) {
			bf.append(" pathKeystore:").append(this.pathKeystore);
		}
		if(this.passwordKeystore!=null) {
			bf.append(" passwordKeystore:").append(this.passwordKeystore);
		}
		if(this.passwordKey!=null) {
			bf.append(" passwordKey:").append(this.passwordKey);
		}
		return bf.toString();
	}
}
