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

package org.openspcoop2.pdd.core.autenticazione;

import java.util.List;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.utils.crypt.PasswordGenerator;
import org.openspcoop2.utils.io.Base64Utilities;

/**
 * ApiKeyUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiKeyUtilities {

	public static String getKey(boolean apiKey,
			boolean header, boolean cookie, boolean queryParameter, 
			String nomeHeader, String nomeCookie, String nomeQueryParameter,
			InfoConnettoreIngresso infoConnettore, PdDContext pddContext, boolean throwException) throws AutenticazioneException {
		
		String tipo = apiKey ? "ApiKey" : "AppId";
		
		String key = null;
		
		// viene usato l'ordine della specifica
		
		if(queryParameter) {
			if(nomeQueryParameter==null && throwException) {
				throw new AutenticazioneException("Nome del parametro della query, da cui estrarre l'"+tipo+", non indicato");
			}
			if(nomeQueryParameter!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				key = infoConnettore.getUrlProtocolContext().getParameterFormBased(nomeQueryParameter);
				if(key!=null && "".equals(key.trim())) {
					key = null;
				}
			}
		}
		if(key!=null) {
			return key;
		}
		
		if(header) {
			if(nomeHeader==null && throwException) {
				throw new AutenticazioneException("Nome dell'header, da cui estrarre l'"+tipo+", non indicato");
			}
			if(nomeHeader!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				key = infoConnettore.getUrlProtocolContext().getParameterTrasporto(nomeHeader);
				if(key!=null && "".equals(key.trim())) {
					key = null;
				}
			}
		}
		if(key!=null) {
			return key;
		}
		
		if(cookie) {
			if(nomeCookie==null && throwException) {
				throw new AutenticazioneException("Nome del cookie, da cui estrarre l'"+tipo+", non indicato");
			}
			if(nomeCookie!=null && infoConnettore!=null && infoConnettore.getUrlProtocolContext()!=null) {
				key = infoConnettore.getUrlProtocolContext().getCookieValue(nomeCookie);
				if(key!=null && "".equals(key.trim())) {
					key = null;
				}
			}
		}
		if(key!=null) {
			return key;
		}
		
		if(throwException) {
			throw new AutenticazioneException(tipo+" non presente nella richiesta");
		}
			
		return null;
	}
	
	private static final String SOGGETTO_SEPARATOR = ".";	
	public static final String APPLICATIVO_SOGGETTO_SEPARATOR = "@";
	private static final int MAX_ALREADY_EXISTS = 5000;
	private static final String SEPARATOR_ALREADY_EXISTS = ".";
	
	public static String toAppId(String protocollo, IDSoggetto idSoggetto, boolean multipleApiKeys, DriverRegistroServiziDB driver) throws Exception {
		String appId = _toAppId(protocollo, idSoggetto);
		if(!_existsAppId(appId, multipleApiKeys, idSoggetto, driver)) {
			return appId;
		}
		for (int i = 2; i < MAX_ALREADY_EXISTS; i++) {
			IDSoggetto idSoggettoAlreadyExists = new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome()+SEPARATOR_ALREADY_EXISTS+i);
			String appIdAlreadyExists = _toAppId(protocollo, idSoggettoAlreadyExists);
			if(!_existsAppId(appIdAlreadyExists, multipleApiKeys, idSoggetto, driver)) {
				return appIdAlreadyExists;
			}
		}
		throw new Exception("Generazione appId univoco non riuscita dopo "+MAX_ALREADY_EXISTS+" tentativi");
	}
	private static String _toAppId(String protocollo, IDSoggetto idSoggetto) throws Exception {
		// non va bene perchè due nomi uguali di due protocolli differenti, vengono identici.
		//return org.openspcoop2.protocol.engine.utils.NamingUtils.getLabelSoggetto(protocollo, idSoggetto.getTipo(), idSoggetto.getNome());	
		if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
			throw new Exception("Identificativo soggetto non definito");
		}
		return idSoggetto.getNome()+SOGGETTO_SEPARATOR+idSoggetto.getTipo();
	}
	private static boolean _existsAppId(String appId, boolean multipleApiKeys, IDSoggetto idSoggetto, DriverRegistroServiziDB driver) throws Exception {
		Soggetto soggetto = driver.soggettoWithCredenzialiApiKey(appId, multipleApiKeys);
		if(soggetto==null) {
			return false;
		}
		boolean isSame = false;
		if(soggetto.getTipo().equals(idSoggetto.getTipo()) &&
				soggetto.getNome().equals(idSoggetto.getNome()) ) {
			isSame = true;
		}
		return !isSame;
	}
	
	public static String toAppId(String protocollo, IDServizioApplicativo idSA, boolean multipleApiKeys, DriverConfigurazioneDB driver) throws Exception {
		String appId = _toAppId(protocollo, idSA.getNome(), idSA.getIdSoggettoProprietario());
		if(!_existsAppId(appId, multipleApiKeys, idSA, driver)) {
			return appId;
		}
		for (int i = 2; i < MAX_ALREADY_EXISTS; i++) {
			String appIdAlreadyExists = _toAppId(protocollo, idSA.getNome()+SEPARATOR_ALREADY_EXISTS+i, idSA.getIdSoggettoProprietario());
			if(!_existsAppId(appIdAlreadyExists, multipleApiKeys, idSA, driver)) {
				return appIdAlreadyExists;
			}
		}
		throw new Exception("Generazione appId univoco non riuscita dopo "+MAX_ALREADY_EXISTS+" tentativi");
	}
	private static String _toAppId(String protocollo, String nomeSA, IDSoggetto idSoggetto) throws Exception {
		return nomeSA+APPLICATIVO_SOGGETTO_SEPARATOR+ _toAppId(protocollo, idSoggetto);
	}
	private static boolean _existsAppId(String appId, boolean multipleApiKeys,  IDServizioApplicativo idSA, DriverConfigurazioneDB driver) throws Exception {
		List<ServizioApplicativo> saList = driver.servizioApplicativoWithCredenzialiApiKeyList(appId, multipleApiKeys);
		if (saList==null || saList.isEmpty()) {
			return false;
		}
		boolean isSame = false;
		for (ServizioApplicativo servizioApplicativo : saList) {
			if(!servizioApplicativo.getNome().equals(idSA.getNome())) {
				continue;
			}
			if(!servizioApplicativo.getTipoSoggettoProprietario().equals(idSA.getIdSoggettoProprietario().getTipo())) {
				continue;
			}
			if(!servizioApplicativo.getNomeSoggettoProprietario().equals(idSA.getIdSoggettoProprietario().getNome())) {
				continue;
			}
			isSame = true;
			break;
		}
		return !isSame;
	}
			
	private static final String PREFIX_SEPARATOR_API_KEY = ".";
	private static final String PREFIX_SEPARATOR_API_KEY_REGEXP = "\\.";
	
	private static String getPrefixForApiKey(String protocollo, IDSoggetto idSoggetto, DriverRegistroServiziDB driver) throws Exception {
		boolean multipleApiKeys = false;
		String appId = toAppId(protocollo, idSoggetto, multipleApiKeys, driver);
		return Base64Utilities.encodeAsString(appId.getBytes())+PREFIX_SEPARATOR_API_KEY;
	}
	public static ApiKey newApiKey(String protocollo, IDSoggetto idSoggetto, int length, DriverRegistroServiziDB driver) throws Exception {
		ApiKey apiKey = new ApiKey();
		String password = getPassword(length);
		String apiKeyToken = getPrefixForApiKey(protocollo, idSoggetto, driver)+Base64Utilities.encodeAsString(password.getBytes());
		apiKey.setApiKey(apiKeyToken);
		apiKey.setPassword(password);
		return apiKey;
	}
	
	private static String getPrefixForApiKey(String protocollo, IDServizioApplicativo idSA, DriverConfigurazioneDB driver) throws Exception {
		boolean multipleApiKeys = false;
		String appId = toAppId(protocollo, idSA, multipleApiKeys, driver);
		return Base64Utilities.encodeAsString(appId.getBytes())+PREFIX_SEPARATOR_API_KEY;
	}
	public static ApiKey newApiKey(String protocollo, IDServizioApplicativo idSA, int length, DriverConfigurazioneDB driver) throws Exception {
		ApiKey apiKey = new ApiKey();
		String password = getPassword(length);
		String apiKeyToken = getPrefixForApiKey(protocollo, idSA, driver)+Base64Utilities.encodeAsString(password.getBytes());
		apiKey.setApiKey(apiKeyToken);
		apiKey.setPassword(password);
		return apiKey;
	}
	
	public static String encodeApiKey(String appId, String password) throws Exception {
		return Base64Utilities.encodeAsString(appId.getBytes())+
			PREFIX_SEPARATOR_API_KEY+
			Base64Utilities.encodeAsString(password.getBytes());
	}
	
	public static String[] decodeApiKey(String apiKeyBase64) throws Exception {
		if(!apiKeyBase64.contains(PREFIX_SEPARATOR_API_KEY)) {
			throw new Exception("Formato non corretto");
		}
		String [] tmp = apiKeyBase64.split(PREFIX_SEPARATOR_API_KEY_REGEXP);
		if(tmp==null || tmp.length!=2){
			throw new Exception("Formato non corretto (.)");
		}
		try {
			tmp[0] = new String(Base64Utilities.decode(tmp[0]));
		}catch(Exception e) {
			throw new Exception("Formato non corretto (appId)",e);
		}
		try {
			tmp[1] = new String(Base64Utilities.decode(tmp[1]));
		}catch(Exception e) {
			throw new Exception("Formato non corretto (appKey)",e);
		}
		return tmp;
	}
	
	
	public static ApiKey newMultipleApiKey(int length) throws Exception {
		ApiKey apiKey = new ApiKey();
		String password = getPassword(length);
		String apiKeyToken = encodeMultipleApiKey(password);
		apiKey.setApiKey(apiKeyToken);
		apiKey.setPassword(password);
		return apiKey;
	}
	
	public static String decodeMultipleApiKey(String apiKeyBase64) throws Exception {
		return new String(Base64Utilities.decode(apiKeyBase64));
	}
	public static String encodeMultipleApiKey(String password) throws Exception {
		return Base64Utilities.encodeAsString(password.getBytes());
	}
	
	private static String getPassword(int length) throws Exception {
		PasswordGenerator pwdGenerator = PasswordGenerator.DEFAULT;
		return pwdGenerator.generate(length);
	}
}
