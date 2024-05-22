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

package org.openspcoop2.pdd.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;

/**
 * InitRuntimeConfigReader
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneNodiRuntimeInit extends BaseThread {

	protected Logger log;
	protected ConfigurazioneNodiRuntime configurazioneNodiRuntime = null;
	
	private boolean reInitSecretMaps = false;
	private String secretsConfig;
	private boolean secretsConfigRequired;
	private String securityPolicy;
	private String securityRemotePolicy;
	
	private boolean first = true;
	
	public ConfigurazioneNodiRuntimeInit(Logger log, ConfigurazioneNodiRuntime configurazioneNodiRuntime, 
			boolean reInitSecretMaps, String secretsConfig, boolean secretsConfigRequired, String securityPolicy, String securityRemotePolicy) {
		this.log = log;
		this.configurazioneNodiRuntime = configurazioneNodiRuntime;
		this.reInitSecretMaps = reInitSecretMaps;
		this.secretsConfig = secretsConfig;
		this.secretsConfigRequired = secretsConfigRequired;
		this.securityPolicy = securityPolicy;
		this.securityRemotePolicy = securityRemotePolicy;
	}
	
	@Override
	protected void process() {
		
		// provo ad iterare fino a che un nodo runtime non risulta disponibile
		
		boolean finish = false;
		
		while(!finish) {
		
			try{
				finish = analyze();
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della configurazione (file trace, secrets): " + e.getMessage();
				this.log.error(msgErrore,e);
				//throw new UtilsRuntimeException(msgErrore,e); non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
			}
		
			if(!finish) {
				String msg = "Non è stato possibile ottenere informazioni sulla configurazione (file trace, secrets) da nessun nodo runtime (prossimo controllo tra 30 secondi)";
				if(this.first) {
					this.log.debug(msg);
					this.first = false;
				}
				else {
					this.log.error(msg);
				}
				Utilities.sleep(30000); // riprovo dopo 10 secondi
			}
		}
		
		this.setStop(true);
		
	}
	
	private boolean analyze() {
		boolean finish = false;
		List<String> aliases = this.configurazioneNodiRuntime.getAliases();
		if(aliases!=null && !aliases.isEmpty()) {
			for (String alias : aliases) {
				
				boolean continueCheck = true;
				
				ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig = new ConfigurazioneNodiRuntimeBYOKRemoteConfig();
				if(!this.configurazioneNodiRuntime.isActiveNode(this.log, 
						alias, remoteConfig)) {
					continueCheck = false;
				}
				
				if(continueCheck && this.reInitSecretMaps &&
						!reInitSecretMaps(this.configurazioneNodiRuntime, remoteConfig)) {
					continueCheck = false;
				}
				
				if(continueCheck &&
					this.isCompleted(alias)) {
					finish = true;
					break; // non itero su altri nodi
				}
			}
		}
		return finish;
	}
	
	protected boolean isCompleted(String alias) {
		// nop
		return true;
	}
	
	private boolean reInitSecretMaps(ConfigurazioneNodiRuntime configurazioneNodiRuntime, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig) {
		try{
			Map<String, Object> dynamicMap = new HashMap<>();
			DynamicInfo dynamicInfo = new  DynamicInfo();
			DynamicUtils.fillDynamicMap(this.log, dynamicMap, dynamicInfo);
			configurazioneNodiRuntime.initBYOKDynamicMapRemoteGovWayNode(this.log,dynamicMap, false, true, remoteConfig);
					
			BYOKMapProperties.initialize(this.log, this.secretsConfig, this.secretsConfigRequired, 
					this.securityPolicy, this.securityRemotePolicy, 
					dynamicMap, true);
			BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
			secretsProperties.initEnvironment();
			String msgInit = "Environment re-inizializzato con i secrets definiti nel file '"+this.secretsConfig+"'"+
					"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
					"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
					"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
			this.log.info(msgInit);
			return true;
		} catch (Exception e) {
			this.log.debug(e.getMessage(),e);
			// provo su tutti i nodi, non voglio mettere un vincolo che serve per forza un nodo acceso
			return false;
		}
	}
	
}
