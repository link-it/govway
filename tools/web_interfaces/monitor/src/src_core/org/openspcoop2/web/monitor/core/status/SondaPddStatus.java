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
package org.openspcoop2.web.monitor.core.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.monitor.engine.constants.SondaStatus;
import org.openspcoop2.monitor.engine.status.GatewayStatus;
import org.openspcoop2.monitor.engine.status.IStatus;
import org.openspcoop2.monitor.engine.status.StatusUtilities;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.slf4j.Logger;

/**
 * SondaPddStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SondaPddStatus extends BaseSondaPdd implements ISondaPdd{

	public static final String GATEWAY_DEFAULT = "Gateway";
	public static final String ALIAS_DEFAULT = ConfigurazioneNodiRuntime.ALIAS_DEFAULT;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	public SondaPddStatus(String identificativo, Logger log, Properties prop) throws Exception{
		super(identificativo, log, prop);
	}

	@Override
	protected void init() throws Exception {
		try{
			this.log.debug("Init Sonda Pdd Status in corso...");
			this.listaStatus = new ArrayList<IStatus>();

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(this.log);

			List<String> listaPdDMonitorateStatusPdD = this.getListaPdDMonitorate_StatusPdD();

			ConfigurazioneNodiRuntime config = pddMonitorProperties.getConfigurazioneNodiRuntime();
			
			
			for (String nomeSonda : listaPdDMonitorateStatusPdD) {
				
				String nomeNodoRuntime = nomeSonda;
				if(listaPdDMonitorateStatusPdD.size()==1 && SondaPddStatus.GATEWAY_DEFAULT.equals(nomeSonda) &&
					!config.containsNode(nomeSonda) && config.containsNode(SondaPddStatus.ALIAS_DEFAULT)) {
					nomeNodoRuntime = SondaPddStatus.ALIAS_DEFAULT;
				}
				
				String url = config.getCheckStatusUrl(nomeNodoRuntime); // leggo prima il file unico dei nodi, se presente (la configurazione backward non avrà questa impostata, poichè si tratta di una nuova proprietà)
				if(url==null) {
					url = this.propertiesSonda.getProperty(nomeSonda+".url"); //"pdd."+
				}
				if(url==null) {
					url = config.getResourceUrl(nomeNodoRuntime);
				}
				if(url==null) {
					String p1 = "statoPdD.sonde.standard."+nomeSonda+".url";
					throw new CoreException("Alternative Properties ["+p1+"] or [remoteAccess.url] not found");
				}
				
				boolean https = false;
				boolean httpsVerificaHostName = true;
				boolean httpsAutenticazioneServer = true;
				String httpsTruststorePath = null;
				String httpsTruststoreType = null;
				String httpsTruststorePassword = null;
				Integer connectionTimeout = null;
				Integer readConnectionTimeout = null;
				
				String httpsP = this.propertiesSonda.getProperty(nomeSonda+".https");
				if(httpsP!=null) {
					https = "true".equals(httpsP.trim()); // default false
				}
				else {
					https = config.isHttps(nomeNodoRuntime);
				}
				
				String connectionTimeoutS = this.propertiesSonda.getProperty(nomeSonda+".connectionTimeout");
				if(connectionTimeoutS==null) {
					connectionTimeoutS = config.getConnectionTimeout(nomeNodoRuntime);
				}					
				if(connectionTimeoutS!=null) {
					connectionTimeout = Integer.valueOf(connectionTimeoutS);
				}
				
				String readConnectionTimeoutS = this.propertiesSonda.getProperty(nomeSonda+".readConnectionTimeout");
				if(readConnectionTimeoutS==null) {
					readConnectionTimeoutS = config.getReadConnectionTimeout(nomeNodoRuntime);
				}
				if(readConnectionTimeoutS!=null) {
					readConnectionTimeout = Integer.valueOf(readConnectionTimeoutS);
				}
								
				if(https) {
					
					String httpsPverificaHostName = this.propertiesSonda.getProperty(nomeSonda+".https.verificaHostName");
					if(httpsPverificaHostName!=null) {
						httpsVerificaHostName = "false".equals(httpsPverificaHostName.trim()); // default true
					}
					else {
						httpsVerificaHostName = config.isHttps_verificaHostName(nomeNodoRuntime);
					}
					
					String httpsPautenticazioneServer = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer");
					if(httpsPautenticazioneServer!=null) {
						httpsAutenticazioneServer = "false".equals(httpsPautenticazioneServer.trim()); // default true
					}
					else {
						httpsAutenticazioneServer = config.isHttps_autenticazioneServer(nomeNodoRuntime);
					}
				
					if(httpsAutenticazioneServer) {
						
						httpsTruststorePath = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststorePath");
						if(httpsTruststorePath==null) {
							httpsTruststorePath = config.getHttps_autenticazioneServer_truststorePath(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(httpsTruststorePath)) {
							throw new CoreException("[gateway:"+nomeSonda+"] TLS Truststore path non fornito");
						}
						
						httpsTruststoreType = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststoreType");
						if(httpsTruststoreType==null) {
							httpsTruststoreType = config.getHttps_autenticazioneServer_truststoreType(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(httpsTruststoreType)) {
							throw new CoreException("[gateway:"+nomeSonda+"] TLS Truststore type non fornito");
						}
						
						httpsTruststorePassword = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststorePassword");
						if(httpsTruststorePassword==null) {
							httpsTruststorePassword = config.getHttps_autenticazioneServer_truststorePassword(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(httpsTruststorePassword)) {
							throw new CoreException("[gateway:"+nomeSonda+"] TLS Truststore password non fornito");
						}
					}
				}
				
				GatewayStatus pddStat = new GatewayStatus();
				/**pddStat.setNome(nomeSonda);*/
				String descrizione = null;
				if(config.containsNode(nomeNodoRuntime)) {
					descrizione = config.getDescrizione(nomeNodoRuntime);
				}
				pddStat.setNome(descrizione!=null ? descrizione : nomeSonda);
				pddStat.setUrl(url);
				pddStat.setHttps(https);
				pddStat.setHttps_verificaHostName(httpsVerificaHostName);
				pddStat.setHttps_autenticazioneServer(httpsAutenticazioneServer);
				pddStat.setHttps_autenticazioneServer_truststorePath(httpsTruststorePath);
				pddStat.setHttps_autenticazioneServer_truststoreType(httpsTruststoreType);
				pddStat.setHttps_autenticazioneServer_truststorePassword(httpsTruststorePassword);
				if(connectionTimeout!=null) {
					pddStat.setConnectionTimeout(connectionTimeout);
				}
				if(readConnectionTimeout!=null) {
					pddStat.setReadConnectionTimeout(readConnectionTimeout);
				}

				this.listaStatus.add(pddStat);
			}

			this.log.debug("Init Sonda Pdd Status completato.");

		}catch(Exception e){
			this.logError("Si e' verificato un errore durante l'Init Sonda Pdd Status: " + e.getMessage(),e); 
			throw e;
		}
	}



	@Override
	public List<IStatus> updateStato() throws Exception {

		return StatusUtilities.updateStato(this.listaStatus, this.log);
		
	}

	
	
	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		return PddMonitorProperties.getInstance(this.log).getListaPdDMonitorate_StatusPdD();
	}

	@Override
	public String getMessaggioStatoSondaPdd() throws Exception {
		return StatusUtilities.getDetailStatesProcess(this.listaStatus);
	}


	@Override
	public SondaStatus getStatoSondaPdd() throws Exception {
		return StatusUtilities.statesProcess(this.listaStatus);
	}

}
