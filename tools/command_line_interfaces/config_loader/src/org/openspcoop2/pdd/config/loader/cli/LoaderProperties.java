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


package org.openspcoop2.pdd.config.loader.cli;

import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.core.commons.CoreException;

/**
* LoaderProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class LoaderProperties {
	
	private static LoaderProperties staticInstance = null;
	private static synchronized void init() throws CoreException{
		if(LoaderProperties.staticInstance == null){
			LoaderProperties.staticInstance = new LoaderProperties();
		}
	}
	public static LoaderProperties getInstance() throws CoreException{
		if(LoaderProperties.staticInstance == null){
			LoaderProperties.init();
		}
		return LoaderProperties.staticInstance;
	}
	
	private static String getPropertyPrefix(String name) {
		return "Property '"+name+"'"; 
	}
	
	
	
	private static final String PROPERTIES_FILE = "/config_loader.cli.properties";
	
	private String protocolloDefault = null;
	
	private boolean policyEnable = false;
	private boolean pluginEnable = false;
	private int pluginSeconds = 60;
	private boolean pluginCheckReferences = false;
	private boolean configurazioneGeneraleEnable = false;
	
	private String nomePddOperativa = null;
	private String tipoPddArchivio = null;
	
	private boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto;
	private boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto;
	
	private boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials;
	private boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials;
	private boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials;

	private String utente = null;
		
	private String utenzePassword = null;

	private String applicativiPassword = null;
	private int applicativiApiKeyPasswordGeneratedLength = -1;
	private boolean applicativiBasicPasswordEnableConstraints = false;
	
	private String soggettiPassword = null;
	private int soggettiApiKeyPasswordGeneratedLength = -1;
	private boolean soggettiBasicPasswordEnableConstraints = false;
	
	private boolean securityLoadBouncyCastleProvider = false;
	
	private String envMapConfig = null;
	private boolean envMapConfigRequired = false;
	
	private String hsmConfig = null;
	private boolean hsmRequired = false;
	private boolean hsmKeyPasswordConfigurable = false;
	
	private String byokConfigurazione = null;
	private boolean byokRequired = false;
	private String byokEnvSecretsConfig = null;
	private boolean byokEnvSecretsConfigRequired = false;
	
	public LoaderProperties() throws CoreException {

		Properties props = new Properties();
		try {
			InputStream is = LoaderProperties.class.getResourceAsStream(LoaderProperties.PROPERTIES_FILE);
			props.load(is);
		} catch(Exception e) {
			throw new CoreException("Errore durante l'init delle properties", e);
		}
		
		// PROPERTIES
				
		this.protocolloDefault = this.getProperty(props, "protocolloDefault", true);
		
		this.policyEnable = this.getBooleanProperty(props, "policy.enable", true);
		this.pluginEnable = this.getBooleanProperty(props, "plugin.enable", true);
		this.pluginCheckReferences = this.getBooleanProperty(props, "plugin.checkReferences", true);
		this.pluginSeconds = this.getIntProperty(props, "plugin.seconds", true);
		this.configurazioneGeneraleEnable = this.getBooleanProperty(props, "configurazioneGenerale.enable", true);
		
		this.nomePddOperativa = this.getProperty(props, "nomePddOperativa", false);
		this.tipoPddArchivio = this.getProperty(props, "tipoPddArchivio", true);
		
		this.utente = this.getProperty(props, "utente", true);
		
		this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = this.getBooleanProperty(props, "accordi.implementazioneUnicaPerSoggetto", true);
		this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = this.getBooleanProperty(props, "accordi.portType.implementazioneUnicaPerSoggetto", true);
		
		this.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials = this.getBooleanProperty(props, "soggettiApplicativi.credenzialiBasic.permitSameCredentials", true);
		this.isSoggettiApplicativiCredenzialiSslPermitSameCredentials = this.getBooleanProperty(props, "soggettiApplicativi.credenzialiSsl.permitSameCredentials", true);
		this.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials = this.getBooleanProperty(props, "soggettiApplicativi.credenzialiPrincipal.permitSameCredentials", true);
		
		this.utenzePassword = this.getProperty(props, "utenze.password", true);

		this.applicativiPassword = this.getProperty(props, "applicativi.password", true);
		this.applicativiApiKeyPasswordGeneratedLength = this.getIntProperty(props, "applicativi.api_key.passwordGenerated.length", true);
		this.applicativiBasicPasswordEnableConstraints = this.getBooleanProperty(props, "applicativi.basic.password.enableConstraints", true);
		
		this.soggettiPassword = this.getProperty(props, "soggetti.password", true);
		this.soggettiApiKeyPasswordGeneratedLength = this.getIntProperty(props, "soggetti.api_key.passwordGenerated.length", true);
		this.soggettiBasicPasswordEnableConstraints = this.getBooleanProperty(props, "soggetti.basic.password.enableConstraints", true);

		this.securityLoadBouncyCastleProvider = this.getBooleanProperty(props, "security.addBouncyCastleProvider", false);
		
		this.envMapConfig = this.getProperty(props, "env.map.config", false);
		this.envMapConfigRequired = this.getBooleanProperty(props, "env.map.required", false);
		
		this.hsmConfig = this.getProperty(props, "hsm.config", false);
		this.hsmRequired = this.getBooleanProperty(props, "hsm.required", false);
		this.hsmKeyPasswordConfigurable = this.getBooleanProperty(props, "hsm.keyPassword", false);
		
		this.byokConfigurazione = this.getProperty(props, "byok.config", false);
		this.byokRequired = this.getBooleanProperty(props, "byok.required", false);
		this.byokEnvSecretsConfig = this.getProperty(props, "byok.env.secrets.config", false);
		this.byokEnvSecretsConfigRequired = this.getBooleanProperty(props, "byok.env.secrets.required", false);
		
	}
	
	private String getProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = props.getProperty(name);
		if(tmp==null){
			if(required){
				throw new CoreException(getPropertyPrefix(name)+" not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	private boolean getBooleanProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new CoreException(getPropertyPrefix(name)+" wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	private int getIntProperty(Properties props,String name,boolean required) throws CoreException{
		String tmp = this.getProperty(props, name, required);
		if(tmp!=null){
			try{
				return Integer.valueOf(tmp);
			}catch(Exception e){
				throw new CoreException(getPropertyPrefix(name)+" wrong int format: "+e.getMessage());
			}
		}
		else{
			return -1;
		}
	}
	
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	public boolean isPolicyEnable() {
		return this.policyEnable;
	}
	public boolean isPluginEnable() {
		return this.pluginEnable;
	}
	public boolean isPluginCheckReferences() {
		return this.pluginCheckReferences;
	}
	public int getPluginSeconds() {
		return this.pluginSeconds;
	}
	public boolean isConfigurazioneGeneraleEnable() {
		return this.configurazioneGeneraleEnable;
	}
	
	public String getNomePddOperativa() {
		return this.nomePddOperativa;
	}
	public String getTipoPddArchivio() {
		return this.tipoPddArchivio;
	}
	
	public boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() {
		return this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto;
	}
	public boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() {
		return this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto;
	}
	
	public boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials() {
		return this.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials;
	}
	public boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials() {
		return this.isSoggettiApplicativiCredenzialiSslPermitSameCredentials;
	}
	public boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials() {
		return this.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials;
	}
	
	public String getUtente() {
		return this.utente;
	}
	
	public String getUtenzePassword() {
		return this.utenzePassword;
	}
	public String getApplicativiPassword() {
		return this.applicativiPassword;
	}
	public int getApplicativiApiKeyPasswordGeneratedLength() {
		return this.applicativiApiKeyPasswordGeneratedLength;
	}
	public boolean isApplicativiBasicPasswordEnableConstraints() {
		return this.applicativiBasicPasswordEnableConstraints;
	}
	public String getSoggettiPassword() {
		return this.soggettiPassword;
	}
	public int getSoggettiApiKeyPasswordGeneratedLength() {
		return this.soggettiApiKeyPasswordGeneratedLength;
	}
	public boolean isSoggettiBasicPasswordEnableConstraints() {
		return this.soggettiBasicPasswordEnableConstraints;
	}

	public boolean isSecurityLoadBouncyCastleProvider() {
		return this.securityLoadBouncyCastleProvider;
	}
	
	public String getEnvMapConfig() {
		return this.envMapConfig;
	}
	public boolean isEnvMapConfigRequired(){
		return this.envMapConfigRequired;
	}
	
	public String getHSMConfigurazione() {
		return this.hsmConfig;
	}
	public boolean isHSMRequired() {
		return this.hsmRequired;
	}
	public boolean isHSMKeyPasswordConfigurable() {
		return this.hsmKeyPasswordConfigurable;
	}
	
	public String getBYOKConfigurazione() {
		return this.byokConfigurazione;
	}
	public boolean isBYOKRequired() {
		return this.byokRequired;
	}
	public String getBYOKEnvSecretsConfig() {
		return this.byokEnvSecretsConfig;
	}
	public boolean isBYOKEnvSecretsConfigRequired() {
		return this.byokEnvSecretsConfigRequired;
	}
}
