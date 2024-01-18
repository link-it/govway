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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.ApiKeyUtilities;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;

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
	public static CredenzialiInvocazione getAutenticazioneApiKey(String username, String password) throws Exception {
		CredenzialiInvocazione c = new CredenzialiInvocazione();
		c.setAutenticazione(TipoAutenticazione.APIKEY);
		c.setAppIdEnabled(false);
		c.setApiKey(ApiKeyUtilities.encodeApiKey(username, password));
		return c;
	}
	public static CredenzialiInvocazione getAutenticazioneMultipleApiKey(String username, String password) throws Exception {
		CredenzialiInvocazione c = new CredenzialiInvocazione();
		c.setAutenticazione(TipoAutenticazione.APIKEY);
		c.setAppIdEnabled(true);
		c.setApiKey(ApiKeyUtilities.encodeMultipleApiKey(password));
		c.setAppId(username);
		return c;
	}
	
	
	private TipoAutenticazione autenticazione;
	
	private String username;
	private String password;
	
	String pathKeystore;
	String passwordKeystore;
	String passwordKey;
	
	private String apiKey;
	private String appId;
	private boolean appIdEnabled;
	private PosizioneCredenziale posizioneApiKey = PosizioneCredenziale.HEADER;
	private PosizioneCredenziale posizioneAppId = PosizioneCredenziale.HEADER;
	private String nomePosizioneApiKey = null;
	private String nomePosizioneAppId = null;
	
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

	public String getApiKey() {
		return this.apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getAppId() {
		return this.appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public boolean isAppIdEnabled() {
		return this.appIdEnabled;
	}
	public void setAppIdEnabled(boolean appIdEnabled) {
		this.appIdEnabled = appIdEnabled;
	}
	public PosizioneCredenziale getPosizioneApiKey() {
		return this.posizioneApiKey;
	}
	public void setPosizioneApiKey(PosizioneCredenziale posizioneApiKey) {
		this.posizioneApiKey = posizioneApiKey;
	}
	public PosizioneCredenziale getPosizioneAppId() {
		return this.posizioneAppId;
	}
	public void setPosizioneAppId(PosizioneCredenziale posizioneAppId) {
		this.posizioneAppId = posizioneAppId;
	}
	public String getNomePosizioneApiKey() {
		if(this.nomePosizioneApiKey!=null) {
			return this.nomePosizioneApiKey;
		}
		else {
			switch (this.posizioneApiKey) {
			case QUERY:
				return ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_API_KEY;
			case HEADER:
				return ParametriAutenticazioneApiKey.DEFAULT_HEADER_API_KEY;
			case COOKIE:
				return ParametriAutenticazioneApiKey.DEFAULT_COOKIE_API_KEY;
			}
		}
		return null;
	}
	public void setNomePosizioneApiKey(String nomePosizioneApiKey) {
		this.nomePosizioneApiKey = nomePosizioneApiKey;
	}
	public String getNomePosizioneAppId() {
		if(this.nomePosizioneAppId!=null) {
			return this.nomePosizioneAppId;
		}
		else {
			switch (this.posizioneAppId) {
			case QUERY:
				return ParametriAutenticazioneApiKey.DEFAULT_QUERY_PARAMETER_APP_ID;
			case HEADER:
				return ParametriAutenticazioneApiKey.DEFAULT_HEADER_APP_ID;
			case COOKIE:
				return ParametriAutenticazioneApiKey.DEFAULT_COOKIE_APP_ID;
			}
		}
		return null;
	}
	public void setNomePosizioneAppId(String nomePosizioneAppId) {
		this.nomePosizioneAppId = nomePosizioneAppId;
	}
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder("(");
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
		if(this.apiKey!=null) {
			bf.append(" apiKey:").append(this.apiKey);
			bf.append(" posizioneApiKey:").append(this.posizioneApiKey);
		}
		if(this.appIdEnabled && this.appId!=null) {
			bf.append(" appId:").append(this.appId);
			bf.append(" posizioneAppId:").append(this.posizioneAppId);
		}
		return bf.toString();
	}
}
