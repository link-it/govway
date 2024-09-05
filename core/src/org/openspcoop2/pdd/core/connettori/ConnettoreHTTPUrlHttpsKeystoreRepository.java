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

package org.openspcoop2.pdd.core.connettori;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.SSLConfigProps;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**     
 * ConnettoreHTTP_urlHttps_keystoreRepository
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPUrlHttpsKeystoreRepository {

	protected static boolean isEnabled(String idModulo, List<Proprieta> proprietaPorta) {
		boolean urlHttpsOverrideJvmConfiguration = false;
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)){
			urlHttpsOverrideJvmConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttpUrlHttpsOverrideDefaultConfigurationConsegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			urlHttpsOverrideJvmConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttpUrlHttpsOverrideDefaultConfigurationInoltroBuste();
		}
		
		if(proprietaPorta!=null && !proprietaPorta.isEmpty()) {
			urlHttpsOverrideJvmConfiguration = CostantiProprieta.isConnettoriHttpsEndpointJvmConfigOverrideEnabled(proprietaPorta, urlHttpsOverrideJvmConfiguration);
		}
		
		return urlHttpsOverrideJvmConfiguration;
	}
	
	private boolean debug;
	private ConnettoreLogger logger;
	private boolean fruizioni;
	private OpenSPCoop2Properties op2Properties;
	
	private File config;
	
	public ConnettoreHTTPUrlHttpsKeystoreRepository(boolean debug, ConnettoreLogger logger, String idModulo) throws ConnettoreException {
		
		try {
		
			this.debug = debug;
			this.logger = logger;
			this.fruizioni = !ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo);
			this.op2Properties = OpenSPCoop2Properties.getInstance();
						
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		
	}
	
	public void init(List<Proprieta> proprietaPorta, Busta busta,
			Map<String, Object> dynamicMap, PdDContext pddContext) throws ConnettoreException {
		
		try {
		
			// Repository https
			File repoDefault = this.fruizioni ? this.op2Properties.getConnettoreHttpUrlHttpsRepositoryInoltroBuste() :
				this.op2Properties.getConnettoreHttpUrlHttpsRepositoryConsegnaContenutiApplicativi();
			String repositoryHttps = CostantiProprieta.getConnettoriHttpsEndpointJvmConfigOverrideRepository(proprietaPorta, 
					repoDefault!=null ? repoDefault.getAbsolutePath() : null);
			if(repositoryHttps!=null) {
				repositoryHttps = convertDynamicPropertyValue("repository", repositoryHttps, dynamicMap, pddContext);
			}
			if(repositoryHttps==null) {
				throw new ConnettoreException("Repository undefined");
			}
			File repo = new File(repositoryHttps);
			
			// ConfigFile
			String configFileDefault = buildDefaultConfigFile(this.debug, this.logger, busta, this.fruizioni);
			String configFile = CostantiProprieta.getConnettoriHttpsEndpointJvmConfigOverrideConfig(proprietaPorta, configFileDefault);
			if(configFile!=null) {
				configFile = convertDynamicPropertyValue("configFile", configFile, dynamicMap, pddContext);
			}
			if(configFile==null) {
				throw new ConnettoreException("Configuration file undefined");
			}
			this.config = new File(repo, configFile);
			
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		
	}
	
	private static String convertDynamicPropertyValue(String oggettoDebug, String valore, Map<String, Object> dynamicMap, PdDContext pddContext) throws ConnettoreException {
		try {
			return DynamicUtils.convertDynamicPropertyValue(oggettoDebug, valore, dynamicMap, pddContext, false);
		}catch(Exception e){
			throw new ConnettoreException("Errore durante la lettura della proprietà '"+oggettoDebug+"' per l'overriding della configurazione jvm (dynamic): "+e.getMessage(),e);
		}
	}
	
	private static String buildDefaultConfigFile(boolean debug, ConnettoreLogger logger, Busta busta, boolean fruizioni) {
		if(busta==null){
			if(debug){
				logger.debug("Dati busta non presenti");
			}
			return null;
		}
		String tipoNomeSoggetto = null;
		if(fruizioni) {
			tipoNomeSoggetto = buildDefaultConfigFileFruizioni(debug, logger, busta);
		}
		else {
			tipoNomeSoggetto = buildDefaultConfigFileErogazioni(debug, logger, busta);
		}
		return tipoNomeSoggetto;
	}
	private static String buildDefaultConfigFileFruizioni(boolean debug, ConnettoreLogger logger, Busta busta) {
		if(busta.getTipoMittente()==null){
			if(debug){
				logger.debug("Dati busta tipo-mittente non presente");
			}
			return null;
		}
		if(busta.getMittente()==null){
			if(debug){
				logger.debug("Dati busta nome-mittente non presente");
			}
			return null;
		}
		return busta.getTipoMittente()+busta.getMittente()+".properties";
	}
	private static String buildDefaultConfigFileErogazioni(boolean debug, ConnettoreLogger logger, Busta busta) {
		if(busta.getTipoDestinatario()==null){
			if(debug){
				logger.debug("Dati busta tipo-destinatario non presente");
			}
			return null;
		}
		if(busta.getDestinatario()==null){
			if(debug){
				logger.debug("Dati busta nome-destinatario non presente");
			}
			return null;
		}
		return busta.getTipoDestinatario()+busta.getDestinatario()+".properties";
	}
	
	
	
	public SSLConfig readSSLContext(RequestInfo requestInfo) throws ConnettoreException {
		
		try {
			SSLConfigProps sslConfigProps = GestoreKeystoreCache.getSSLConfigProps(requestInfo, this.config.getAbsolutePath(), false);
			if(sslConfigProps!=null) {
				return sslConfigProps.getSslconfig();
			}
			return null;
		}
		catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
	}	
	
}
