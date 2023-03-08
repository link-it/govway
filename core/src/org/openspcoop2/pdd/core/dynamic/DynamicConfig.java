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

package org.openspcoop2.pdd.core.dynamic;

import java.util.Map;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * PurposeId
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicConfig {
	
	private Logger log;
	private Map<String, Object> dynamicMap;
	private RequestInfo requestInfo;
	
	private Busta busta;
	
	
	public DynamicConfig(Logger log, Map<String, Object> dynamicMap, RequestInfo requestInfo, Busta busta) {
		this.log = log;
		this.dynamicMap = dynamicMap;
		this.requestInfo = requestInfo;
		this.busta = busta;
	}
	
	
	private IDServizio configId = null;
	private IDServizio getConfigId(){
		if(this.configId!=null) {
			return this.configId;
		}
		if(this.requestInfo!=null && this.requestInfo.getIdServizio()!=null) {
			this.configId = this.requestInfo.getIdServizio();
		}
		else if(this.busta==null && this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_BUSTA_OBJECT);
			if(o!=null && o instanceof Busta) {
				this.busta = (Busta) o;
			}
		}
		
		if(this.configId==null && this.busta!=null) {
			try {
				this.configId = IDServizioFactory.getInstance().getIDServizioFromValues(this.busta.getTipoServizio(), this.busta.getServizio(), 
						this.busta.getTipoDestinatario(), this.busta.getDestinatario(), 
						this.busta.getVersioneServizio());
			}
			catch(Exception e) {
				this.log.error("Creazione IDServizio per dynamic config fallita: "+e.getMessage(),e);
			}
		}
		
		return this.configId;
	}
	
	private Map<String, String> mapConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapConfig(){
		if(this.mapConfig!=null) {
			return this.mapConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_API_IMPL_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapConfig = (Map<String, String>) o;
			}
		}
		return this.mapConfig;
	}
	

	
	private IDSoggetto providerOrganizationId;
	@SuppressWarnings("unused")
	private IDSoggetto getProviderOrganizationId(){
		if(this.providerOrganizationId!=null) {
			return this.providerOrganizationId;
		}
		if(this.requestInfo!=null && this.requestInfo.getIdServizio()!=null && this.requestInfo.getIdServizio().getSoggettoErogatore()!=null) {
			this.providerOrganizationId = this.requestInfo.getIdServizio().getSoggettoErogatore();
		}
		else if(this.busta==null && this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_BUSTA_OBJECT);
			if(o!=null && o instanceof Busta) {
				this.busta = (Busta) o;
			}
		}
		if(this.providerOrganizationId==null && this.busta!=null) {
			try {
				this.providerOrganizationId = new IDSoggetto(this.busta.getTipoDestinatario(), this.busta.getDestinatario());
			}
			catch(Exception e) {
				this.log.error("Creazione IDSoggetto provider per dynamic config fallita: "+e.getMessage(),e);
			}
		}
		return this.providerOrganizationId;
	}
	private Map<String, String> mapProviderOrganizationConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapProviderOrganizationConfig(){
		if(this.mapProviderOrganizationConfig!=null) {
			return this.mapProviderOrganizationConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_SOGGETTO_EROGATORE_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapProviderOrganizationConfig = (Map<String, String>) o;
			}
		}
		return this.mapProviderOrganizationConfig;
	}
	
	
	
	
	private IDSoggetto clientOrganizationId;
	private IDSoggetto getClientOrganizationId(){
		if(this.clientOrganizationId!=null) {
			return this.clientOrganizationId;
		}
		if(this.requestInfo!=null && this.requestInfo.getFruitore()!=null) {
			this.clientOrganizationId = this.requestInfo.getFruitore();
		}
		else if(this.busta==null && this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_BUSTA_OBJECT);
			if(o!=null && o instanceof Busta) {
				this.busta = (Busta) o;
			}
		}
		
		if(this.clientOrganizationId==null && this.busta!=null) {
			this.clientOrganizationId = new IDSoggetto(this.busta.getTipoMittente(), this.busta.getMittente());
		}
		
		return this.clientOrganizationId;
	}
	private Map<String, String> mapClientOrganizationConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapClientOrganizationConfig(){
		if(this.mapClientOrganizationConfig!=null) {
			return this.mapClientOrganizationConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_SOGGETTO_FRUITORE_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapClientOrganizationConfig = (Map<String, String>) o;
			}
		}
		return this.mapClientOrganizationConfig;
	}
	
	private IDServizioApplicativo clientApplicationId; 
	private IDServizioApplicativo getClientApplicationId(){
		if(this.clientApplicationId!=null) {
			return this.clientApplicationId;
		}
		
		if(this.busta==null && this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_BUSTA_OBJECT);
			if(o!=null && o instanceof Busta) {
				this.busta = (Busta) o;
			}
		}
		
		if(this.clientOrganizationId==null && this.busta!=null && this.busta.getServizioApplicativoFruitore()!=null) {
			IDSoggetto organization = getClientOrganizationId();
			this.clientApplicationId = new IDServizioApplicativo();
			this.clientApplicationId.setIdSoggettoProprietario(organization);
			this.clientApplicationId.setNome(this.busta.getServizioApplicativoFruitore());
		}
		
		return this.clientApplicationId;
	}
	private Map<String, String> mapClientApplicationConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapClientApplicationConfig(){
		if(this.mapClientApplicationConfig!=null) {
			return this.mapClientApplicationConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_APPLICATIVO_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapClientApplicationConfig = (Map<String, String>) o;
			}
		}
		return this.mapClientApplicationConfig;
	}
	
	
	
	
	
	
	private IDSoggetto tokenTokenClientOrganizationId;
	@SuppressWarnings("unused")
	private IDSoggetto getTokenClientOrganizationId(){
		if(this.tokenTokenClientOrganizationId!=null) {
			return this.tokenTokenClientOrganizationId;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_APPLICATIVO_TOKEN);
			if(o!=null && o instanceof IDServizioApplicativo) {
				this.tokenTokenClientApplicationId = (IDServizioApplicativo) o;
				this.tokenTokenClientOrganizationId = this.tokenTokenClientApplicationId.getIdSoggettoProprietario();
			}
		}
		
		return this.tokenTokenClientOrganizationId;
	}
	private Map<String, String> mapTokenClientOrganizationConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapTokenClientOrganizationConfig(){
		if(this.mapTokenClientOrganizationConfig!=null) {
			return this.mapTokenClientOrganizationConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapTokenClientOrganizationConfig = (Map<String, String>) o;
			}
		}
		return this.mapTokenClientOrganizationConfig;
	}
	
	private IDServizioApplicativo tokenTokenClientApplicationId; 
	private IDServizioApplicativo getTokenClientApplicationId(){
		if(this.tokenTokenClientApplicationId!=null) {
			return this.tokenTokenClientApplicationId;
		}
		
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_APPLICATIVO_TOKEN);
			if(o!=null && o instanceof IDServizioApplicativo) {
				this.tokenTokenClientApplicationId = (IDServizioApplicativo) o;
				this.tokenTokenClientOrganizationId = this.tokenTokenClientApplicationId.getIdSoggettoProprietario();
			}
		}
		
		return this.tokenTokenClientApplicationId;
	}
	private Map<String, String> mapTokenClientApplicationConfig = null;
	@SuppressWarnings("unchecked")
	private Map<String, String> getMapTokenClientApplicationConfig(){
		if(this.mapTokenClientApplicationConfig!=null) {
			return this.mapTokenClientApplicationConfig;
		}
		if(this.dynamicMap!=null && !this.dynamicMap.isEmpty()) {
			Object o = this.dynamicMap.get(Costanti.MAP_APPLICATIVO_TOKEN_CONFIG_PROPERTY);
			if(o!=null && o instanceof Map<?, ?>) {
				this.mapTokenClientApplicationConfig = (Map<String, String>) o;
			}
		}
		return this.mapTokenClientApplicationConfig;
	}
	
	
	
	
	
	
	// metodi diretti
	
	public String getApi(String pName) throws DynamicException {
		return this.getValue(getMapConfig(), pName);
	}
	
	public String getProviderOrganization(String pName) throws DynamicException {
		return this.getValue(getMapProviderOrganizationConfig(), pName);
	}
	
	public String getClientApplication(String pName) throws DynamicException {
		return this.getValue(getMapClientApplicationConfig(), pName);
	}
	
	public String getClientOrganization(String pName) throws DynamicException {
		return this.getValue(getMapClientOrganizationConfig(), pName);
	}
	
	public String getTokenClientApplication(String pName) throws DynamicException {
		return this.getValue(getMapTokenClientApplicationConfig(), pName);
	}
	
	public String getTokenClientOrganization(String pName) throws DynamicException {
		return this.getValue(getMapTokenClientOrganizationConfig(), pName);
	}
	
	
	// ricerche
	
	public String apiSearchByClientApplication(String pNameParam) throws DynamicException {
		
		IDServizioApplicativo clientApplicationId = getClientApplicationId();
		Map<String, String> mapConfig = getMapConfig();
		
		// 1. Cerco nell'api con nome '<clientOrganizationName>.<clientApplicationName>.<pName>'
		if(clientApplicationId!=null && clientApplicationId.getNome()!=null && 
				clientApplicationId.getIdSoggettoProprietario()!=null && clientApplicationId.getIdSoggettoProprietario().getNome()!=null) {
			String pName = clientApplicationId.getIdSoggettoProprietario().getNome()+"."+clientApplicationId.getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 2. Cerco nella fruizione con nome '<clientApplicationName>.<pName>'
		if(clientApplicationId!=null && clientApplicationId.getNome()!=null) {
			String pName = clientApplicationId.getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 3. Cerco nella fruizione con nome '<clientOrganizationName>.<pName>'
		if(clientApplicationId!=null && 
				clientApplicationId.getIdSoggettoProprietario()!=null && clientApplicationId.getIdSoggettoProprietario().getNome()!=null) {
			String pName = clientApplicationId.getIdSoggettoProprietario().getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
				
		// 4. Proprietà di default
		return this.getValue(mapConfig, pNameParam);
	}
	
	public String clientApplicationSearch(String pNameParam) throws DynamicException {
		
		IDServizio configId = this.getConfigId();
		Map<String, String> mapClientApplicationConfig = getMapClientApplicationConfig();
		
		// 1. Cerco nell'applicativo con nome '<nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.purposeId'
		if(configId!=null && configId.getNome()!=null && configId.getVersione()!=null && 
				configId.getSoggettoErogatore()!=null && configId.getSoggettoErogatore().getNome()!=null) {
			String pName = configId.getSoggettoErogatore().getNome() +"." + configId.getNome() + ".v"+configId.getVersione()+"."+pNameParam;
			String v = this.getValue(mapClientApplicationConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 2. Cerco nell'applicativo con nome '<nomeApiImpl>.v<nomeApiImpl>.purposeId'
		if(configId!=null && configId.getNome()!=null && configId.getVersione()!=null) {
			String pName = configId.getNome() + ".v"+configId.getVersione()+"."+pNameParam;
			String v = this.getValue(mapClientApplicationConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 3. Proprietà di default
		return this.getValue(mapClientApplicationConfig, pNameParam);
	}
	
	
	
	
	
	public String apiSearchByTokenClientApplication(String pNameParam) throws DynamicException {
		
		IDServizioApplicativo tokenClientApplicationId = getTokenClientApplicationId();
		Map<String, String> mapConfig = getMapConfig();
		
		// 1. Cerco nell'api con nome '<tokenClientOrganizationName>.<tokenClientApplicationName>.<pName>'
		if(tokenClientApplicationId!=null && tokenClientApplicationId.getNome()!=null && 
				tokenClientApplicationId.getIdSoggettoProprietario()!=null && tokenClientApplicationId.getIdSoggettoProprietario().getNome()!=null) {
			String pName = tokenClientApplicationId.getIdSoggettoProprietario().getNome()+"."+tokenClientApplicationId.getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 2. Cerco nella fruizione con nome '<tokenClientApplicationName>.<pName>'
		if(tokenClientApplicationId!=null && tokenClientApplicationId.getNome()!=null) {
			String pName = tokenClientApplicationId.getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 3. Cerco nella fruizione con nome '<tokenClientOrganizationName>.<pName>'
		if(tokenClientApplicationId!=null && 
				tokenClientApplicationId.getIdSoggettoProprietario()!=null && tokenClientApplicationId.getIdSoggettoProprietario().getNome()!=null) {
			String pName = tokenClientApplicationId.getIdSoggettoProprietario().getNome()+"."+pNameParam;
			String v = this.getValue(mapConfig, pName);
			if(v!=null) {
				return v;
			}
		}
				
		// 4. Proprietà di default
		return this.getValue(mapConfig, pNameParam);
	}
	
	public String tokenClientApplicationSearch(String pNameParam) throws DynamicException {
		
		IDServizio configId = this.getConfigId();
		Map<String, String> mapTokenClientApplicationConfig = getMapTokenClientApplicationConfig();
		
		// 1. Cerco nell'applicativo con nome '<nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.purposeId'
		if(configId!=null && configId.getNome()!=null && configId.getVersione()!=null && 
				configId.getSoggettoErogatore()!=null && configId.getSoggettoErogatore().getNome()!=null) {
			String pName = configId.getSoggettoErogatore().getNome() +"." + configId.getNome() + ".v"+configId.getVersione()+"."+pNameParam;
			String v = this.getValue(mapTokenClientApplicationConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 2. Cerco nell'applicativo con nome '<nomeApiImpl>.v<nomeApiImpl>.purposeId'
		if(configId!=null && configId.getNome()!=null && configId.getVersione()!=null) {
			String pName = configId.getNome() + ".v"+configId.getVersione()+"."+pNameParam;
			String v = this.getValue(mapTokenClientApplicationConfig, pName);
			if(v!=null) {
				return v;
			}
		}
		
		// 3. Proprietà di default
		return this.getValue(mapTokenClientApplicationConfig, pNameParam);
	}
	
	
	
	
	private String getValue(Map<String, String> map, String pName) {
		if(map!=null && !map.isEmpty()) {
			for (String name : map.keySet()) {
				if(name.equals(pName)) {
					return map.get(name);
				}
			}
		}
		return null;
	}
}
