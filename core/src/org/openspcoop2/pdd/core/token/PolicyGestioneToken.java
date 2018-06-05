package org.openspcoop2.pdd.core.token;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;

public class PolicyGestioneToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String descrizione;
	private Map<String, Properties> properties;
	
	private boolean validazioneJWT;
	private boolean validazioneJWT_warningOnly;
	
	private boolean introspection;
	private boolean introspection_warningOnly;
	
	private boolean userInfo;
	private boolean userInfo_warningOnly;
	
	private boolean forwardToken;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public Map<String, Properties> getProperties() {
		return this.properties;
	}
	public void setProperties(Map<String, Properties> properties) {
		this.properties = properties;
	}
	public boolean isValidazioneJWT() {
		return this.validazioneJWT;
	}
	public void setValidazioneJWT(boolean validazioneJWT) {
		this.validazioneJWT = validazioneJWT;
	}
	public boolean isValidazioneJWT_warningOnly() {
		return this.validazioneJWT_warningOnly;
	}
	public void setValidazioneJWT_warningOnly(boolean validazioneJWT_warningOnly) {
		this.validazioneJWT_warningOnly = validazioneJWT_warningOnly;
	}
	public boolean isIntrospection() {
		return this.introspection;
	}
	public void setIntrospection(boolean introspection) {
		this.introspection = introspection;
	}
	public boolean isIntrospection_warningOnly() {
		return this.introspection_warningOnly;
	}
	public void setIntrospection_warningOnly(boolean introspection_warningOnly) {
		this.introspection_warningOnly = introspection_warningOnly;
	}
	public boolean isUserInfo() {
		return this.userInfo;
	}
	public void setUserInfo(boolean userInfo) {
		this.userInfo = userInfo;
	}
	public boolean isUserInfo_warningOnly() {
		return this.userInfo_warningOnly;
	}
	public void setUserInfo_warningOnly(boolean userInfo_warningOnly) {
		this.userInfo_warningOnly = userInfo_warningOnly;
	}
	public boolean isForwardToken() {
		return this.forwardToken;
	}
	public void setForwardToken(boolean forwardToken) {
		this.forwardToken = forwardToken;
	}
	
	public Properties getDefaultProperties() throws ProviderException, ProviderValidationException {
		return TokenUtilities.getDefaultProperties(this.properties);
	}
	
}
