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



package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.utils.BooleanNullable;

/**
 * ParametriAutenticazioneApiKey
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParametriAutenticazioneApiKey extends ParametriAutenticazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_HEADER_API_KEY = "X-API-KEY";
	public static final String DEFAULT_COOKIE_API_KEY = "X-API-KEY";
	public static final String DEFAULT_QUERY_PARAMETER_API_KEY = "api_key";
	
	public static final String DEFAULT_HEADER_APP_ID = "X-APP-ID";
	public static final String DEFAULT_COOKIE_APP_ID = "X-APP-ID";
	public static final String DEFAULT_QUERY_PARAMETER_APP_ID = "app_id";
	
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String HEADER = "header";
	public static final String HEADER_TRUE = TRUE;
	public static final String HEADER_FALSE = FALSE;
	
	public static final String COOKIE = "cookie";
	public static final String COOKIE_TRUE = TRUE;
	public static final String COOKIE_FALSE = FALSE;
	
	public static final String QUERY_PARAMETER = "queryParameter";
	public static final String QUERY_PARAMETER_TRUE = TRUE;
	public static final String QUERY_PARAMETER_FALSE = FALSE;
	
	public static final String USE_OAS3_NAMES = "useOAS3Names";
	public static final String USE_OAS3_NAMES_TRUE = TRUE;
	public static final String USE_OAS3_NAMES_FALSE = FALSE;
	
	public static final String NOME_HEADER_API_KEY = "headerApiKey";
	public static final String NOME_COOKIE_API_KEY = "cookieApiKey";
	public static final String NOME_QUERY_PARAMETER_API_KEY = "queryParameterApiKey";
	
	public static final String APP_ID = "appId";
	public static final String APP_ID_TRUE = TRUE;
	public static final String APP_ID_FALSE = FALSE;

	public static final String NOME_HEADER_APP_ID = "headerAppId";
	public static final String NOME_COOKIE_APP_ID = "cookieAppId";
	public static final String NOME_QUERY_PARAMETER_APP_ID = "queryParameterAppId";
	
	public static final String CLEAN_API_KEY = "cleanApiKey";
	public static final String CLEAN_API_KEY_TRUE = TRUE;
	public static final String CLEAN_API_KEY_FALSE = FALSE;
	
	public static final String CLEAN_APP_ID = "cleanAppId";
	public static final String CLEAN_APP_ID_TRUE = TRUE;
	public static final String CLEAN_APP_ID_FALSE = FALSE;
	
	
	public ParametriAutenticazioneApiKey(ParametriAutenticazione parametri) {
		super(parametri);
	}
	
	public BooleanNullable getHeader() {
		return _get(HEADER);
	}
	public BooleanNullable getCookie() {
		return _get(COOKIE);
	}
	public BooleanNullable getQueryParameter() {
		return _get(QUERY_PARAMETER);
	}
	
	public String getNomeHeaderApiKey() {
		return _getNome(DEFAULT_HEADER_API_KEY, NOME_HEADER_API_KEY);
	}
	public String getNomeCookieApiKey() {
		return _getNome(DEFAULT_COOKIE_API_KEY, NOME_COOKIE_API_KEY);
	}
	public String getNomeQueryParameterApiKey() {
		return _getNome(DEFAULT_QUERY_PARAMETER_API_KEY, NOME_QUERY_PARAMETER_API_KEY);
	}
	
	public BooleanNullable getAppId() {
		return _get(APP_ID);
	}
	
	public String getNomeHeaderAppId() {
		return _getNome(DEFAULT_HEADER_APP_ID, NOME_HEADER_APP_ID);
	}
	public String getNomeCookieAppId() {
		return _getNome(DEFAULT_COOKIE_APP_ID, NOME_COOKIE_APP_ID);
	}
	public String getNomeQueryParameterAppId() {
		return _getNome(DEFAULT_QUERY_PARAMETER_APP_ID, NOME_QUERY_PARAMETER_APP_ID);
	}
	
	public BooleanNullable getCleanApiKey() {
		return _get(CLEAN_API_KEY);
	}
	public BooleanNullable getCleanAppId() {
		return _get(CLEAN_APP_ID);
	}
	
	private BooleanNullable _get(String name) {
		String valore = this.get(name);
		if(valore==null || "".equals(valore)) {
			return BooleanNullable.NULL();
		}
		if(FALSE.equalsIgnoreCase(valore)) {
			return BooleanNullable.FALSE();
		}
		else if(TRUE.equalsIgnoreCase(valore)) {
			return BooleanNullable.TRUE();
		}
		return BooleanNullable.NULL();
	}
	
	private String _getNome(String defaultName, String nome) {
		
		BooleanNullable useOAS3NamesNullable = _get(USE_OAS3_NAMES);
		if(useOAS3NamesNullable!=null && useOAS3NamesNullable.getValue()!=null && useOAS3NamesNullable.getValue()) {
			return defaultName;
		}
		
		String nomeCustom = this.get(nome);
		if(nomeCustom==null || "".equals(nomeCustom)) {
			return defaultName;
		}
		
		return nomeCustom;
	}
}
