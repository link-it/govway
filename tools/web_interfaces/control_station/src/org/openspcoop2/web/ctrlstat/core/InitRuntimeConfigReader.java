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

package org.openspcoop2.web.ctrlstat.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeBYOKRemoteConfig;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.core.byok.BYOKMapProperties;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.logger.filetrace.FileTraceGovWayState;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.threads.BaseThread;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;

/**
 * InitRuntimeConfigReader
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class InitRuntimeConfigReader extends BaseThread {

	private ConsoleProperties consoleProperties;
	private boolean reInitSecretMaps = false;
	
	private boolean first = true;
	
	public InitRuntimeConfigReader(ConsoleProperties consoleProperties, boolean reInitSecretMaps) {
		this.consoleProperties = consoleProperties;
		this.reInitSecretMaps = reInitSecretMaps;
	}
	
	@Override
	protected void process() {
		
		// provo ad iterare fino a che un nodo runtime non risulta disponibile
		
		boolean finish = false;
		
		while(!finish) {
		
			try{
				ConfigurazioneNodiRuntime configurazioneNodiRuntime = this.consoleProperties.getConfigurazioneNodiRuntime();
				InvokerNodiRuntime invoker = new InvokerNodiRuntime(InitListener.log, configurazioneNodiRuntime);
				List<String> aliases = configurazioneNodiRuntime.getAliases();
				if(aliases!=null && !aliases.isEmpty()) {
					for (String alias : aliases) {
						
						boolean continueCheck = true;
						
						ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig = new ConfigurazioneNodiRuntimeBYOKRemoteConfig();
						if(!configurazioneNodiRuntime.isActiveNode(InitListener.log, 
								alias, remoteConfig)) {
							continueCheck = false;
						}
						
						if(continueCheck && this.reInitSecretMaps &&
								!reInitSecretMaps(this.consoleProperties, 
								configurazioneNodiRuntime, remoteConfig)) {
							continueCheck = false;
						}
						
						if(continueCheck) {
							analizeFileTraceGovWayState(invoker, alias, this.consoleProperties);
							if(InitListener.getFileTraceGovWayState()!=null) {
								finish = true;
								break; // non itero su altri nodi
							}
						}
					}
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della configurazione (file trace, secrets): " + e.getMessage();
				InitListener.logError(
						msgErrore,e);
				//throw new UtilsRuntimeException(msgErrore,e); non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
			}
		
			if(!finish) {
				String msg = "Non è stato possibile ottenere informazioni sulla configurazione (file trace, secrets) da nessun nodo runtime (prossimo controllo tra 30 secondi)";
				if(this.first) {
					InitListener.logDebug(msg);
					this.first = false;
				}
				else {
					InitListener.logError(msg);
				}
				Utilities.sleep(30000); // riprovo dopo 10 secondi
			}
		}
		
		this.setStop(true);
		
	}
	
	private void analizeFileTraceGovWayState(InvokerNodiRuntime invoker, String alias, ConsoleProperties consoleProperties) {
		try{
			String tmp = invoker.invokeJMXMethod(alias, consoleProperties.getJmxPdDConfigurazioneSistemaType(alias),
					consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias), 
					consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(alias));
			InitListener.setFileTraceGovWayState(FileTraceGovWayState.toConfig(tmp,true));	
		} catch (Exception e) {
			InitListener.logDebug(e.getMessage(),e);
			// provo su tutti i nodi, non voglio mettere un vincolo che serve per forza un nodo acceso
		}
	}
	
	private boolean reInitSecretMaps(ConsoleProperties consoleProperties, 
			ConfigurazioneNodiRuntime configurazioneNodiRuntime, ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig) {
		try{
			Map<String, Object> dynamicMap = new HashMap<>();
			DynamicInfo dynamicInfo = new  DynamicInfo();
			DynamicUtils.fillDynamicMap(InitListener.log, dynamicMap, dynamicInfo);
			configurazioneNodiRuntime.initBYOKDynamicMapRemoteGovWayNode(InitListener.log,dynamicMap, false, true, remoteConfig);
					
			String secretsConfig = consoleProperties.getBYOKEnvSecretsConfig();
			String securityPolicy = consoleProperties.getBYOKInternalConfigSecurityEngine();
			String securityRemotePolicy = consoleProperties.getBYOKInternalConfigRemoteSecurityEngine();
			
			BYOKMapProperties.initialize(InitListener.log, secretsConfig, consoleProperties.isBYOKEnvSecretsConfigRequired(), 
					securityPolicy, securityRemotePolicy, 
					dynamicMap, true);
			BYOKMapProperties secretsProperties = BYOKMapProperties.getInstance();
			secretsProperties.initEnvironment();
			String msgInit = "Environment re-inizializzato con i secrets definiti nel file '"+secretsConfig+"'"+
					"\n\tJavaProperties: "+secretsProperties.getJavaMap().keys()+
					"\n\tEnvProperties: "+secretsProperties.getEnvMap().keys()+
					"\n\tObfuscateMode: "+secretsProperties.getObfuscateModeDescription();
			InitListener.log.info(msgInit);
			return true;
		} catch (Exception e) {
			InitListener.logDebug(e.getMessage(),e);
			// provo su tutti i nodi, non voglio mettere un vincolo che serve per forza un nodo acceso
			return false;
		}
	}
	
}
