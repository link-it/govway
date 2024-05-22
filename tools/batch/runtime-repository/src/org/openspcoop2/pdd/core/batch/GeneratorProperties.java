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


package org.openspcoop2.pdd.core.batch;

import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;

/**
* GeneratorProperties
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class GeneratorProperties {
	
	private static GeneratorProperties staticInstance = null;
	private static synchronized void init() throws UtilsException{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.staticInstance = new GeneratorProperties();
		}
	}
	public static GeneratorProperties getInstance() throws UtilsException{
		if(GeneratorProperties.staticInstance == null){
			GeneratorProperties.init();
		}
		return GeneratorProperties.staticInstance;
	}
	
	
	
	
	private static final String PROPERTIES_FILE = "/batch-runtime-repository.properties";
	
	private String protocolloDefault;
	
	private int refreshConnessione;
	
	private int scadenzaMessaggiMinuti;

	private boolean messaggiLogQuery;
	private int messaggiFinestraSecondi;
	
	private String repositoryBuste;
	private boolean useDataRegistrazione;
	
	private PropertiesReader props;

	public GeneratorProperties() throws UtilsException {

		Properties p = new Properties();
		try {
			InputStream is = GeneratorProperties.class.getResourceAsStream(GeneratorProperties.PROPERTIES_FILE);
			p.load(is);
		} catch(Exception e) {
			throw new UtilsException("Errore durante l'init delle properties", e);
		}
		this.props = new PropertiesReader(p, true);
		
	}
	
	public void initProperties() throws UtilsException {
		
		// PROPERTIES
				
		this.protocolloDefault = this.getProperty("protocolloDefault", true);
	
		this.refreshConnessione = this.getIntProperty("connectionRefresh.secondi", true);
		this.scadenzaMessaggiMinuti = this.getIntProperty("repository.scadenzaMessaggio.minuti", false);
		
		this.messaggiLogQuery = this.getBooleanProperty("repository.logQuery", true);
		this.messaggiFinestraSecondi = this.getIntProperty("repository.finestra.secondi", true);

		this.repositoryBuste = this.getProperty("repository.gestoreBuste", true);
		this.useDataRegistrazione = this.getBooleanProperty("repository.gestoreBuste.dataRegistrazione", true);
		
	}
	
	private String getPropertyPrefix(String name) {
		return "Property '"+name+"' ";
	} 
	
	private String getProperty(String name,boolean required) throws UtilsException{
		String tmp = this.props.getValue_convertEnvProperties(name);
		if(tmp==null){
			if(required){
				throw new UtilsException(getPropertyPrefix(name)+"not found");
			}
			else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	public String readProperty(boolean required,String property) throws UtilsException{
		return getProperty(property, required);
	}
	
	private boolean getBooleanProperty(String name,boolean required) throws UtilsException{
		String tmp = this.getProperty(name, required);
		if(tmp!=null){
			try{
				return Boolean.parseBoolean(tmp);
			}catch(Exception e){
				throw new UtilsException(getPropertyPrefix(name)+"wrong int format: "+e.getMessage());
			}
		}
		else{
			return false;
		}
	}
	private BooleanNullable readBooleanProperty(boolean required,String property) throws UtilsException{
		String tmp = this.getProperty(property, required);
		if(tmp==null && !required) {
			return BooleanNullable.NULL(); // se e' required viene sollevata una eccezione dal metodo readProperty
		}
		if(!"true".equalsIgnoreCase(tmp) && !"false".equalsIgnoreCase(tmp)){
			throw new UtilsException("Property ["+property+"] with uncorrect value ["+tmp+"] (true/value expected)");
		}
		return Boolean.parseBoolean(tmp) ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
	}
	private boolean parse(BooleanNullable b, boolean defaultValue) {
		return (b!=null && b.getValue()!=null) ? b.getValue() : defaultValue;
	}
	
	private int getIntProperty(String name,boolean required) throws UtilsException{
		String tmp = this.getProperty(name, required);
		if(tmp!=null){
			try{
				return Integer.valueOf(tmp);
			}catch(Exception e){
				throw new UtilsException(getPropertyPrefix(name)+"wrong int format: "+e.getMessage());
			}
		}
		else{
			return -1;
		}
	}
	
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	public int getRefreshConnessione() {
		return this.refreshConnessione;
	}
	
	public int getScadenzaMessaggiMinuti() {
		return this.scadenzaMessaggiMinuti;
	}
	
	public boolean isMessaggiDebug() throws UtilsException {
		return this.getBooleanProperty("repository.debug", true);
	}
	public boolean isMessaggiLogQuery() {
		return this.messaggiLogQuery;
	}
	public int getMessaggiFinestraSecondi() {
		return this.messaggiFinestraSecondi;
	}
	
	public String getRepositoryBuste() {
		return this.repositoryBuste;
	}
	public boolean isUseDataRegistrazione() {
		return this.useDataRegistrazione;
	}
	
	public boolean isSecurityLoadBouncyCastleProvider() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "security.addBouncyCastleProvider");
		return parse(b, false);
	}
	
	
	public String getEnvMapConfig() throws UtilsException{
		return this.readProperty(false, "env.map.config");
	}
	public boolean isEnvMapConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "env.map.required");
		return this.parse(b, false);
	}
	
	
	public String getHSMConfigurazione() throws UtilsException {
		return this.readProperty(false, "hsm.config");
	}
	public boolean isHSMRequired() throws UtilsException {
		return Boolean.parseBoolean(this.readProperty(true, "hsm.required"));
	}
	public boolean isHSMKeyPasswordConfigurable() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "hsm.keyPassword");
		return this.parse(b, false);
	}
	
	
	
	public String getBYOKConfigurazione() throws UtilsException{
		return this.readProperty(false, "byok.config");
	}
	public boolean isBYOKRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.required");
		return parse(b, false);
	}
	public String getBYOKInternalConfigSecurityEngine() throws UtilsException{
		return this.readProperty(false, "byok.internalConfig.securityEngine");
	}
	public String getBYOKInternalConfigRemoteSecurityEngine() throws UtilsException{
		return this.readProperty(false, "byok.internalConfig.securityEngine.remote");
	}
	public String getBYOKEnvSecretsConfig() throws UtilsException{
		return this.readProperty(false, "byok.env.secrets.config");
	}
	public boolean isBYOKEnvSecretsConfigRequired() throws UtilsException{
		BooleanNullable b = this.readBooleanProperty(false, "byok.env.secrets.required");
		return this.parse(b, false);
	}
	
	
	public String getConfigurazioneNodiRuntime() throws UtilsException{
		return this.readProperty(false, "configurazioni.configurazioneNodiRun");
	}
}
