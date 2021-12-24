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
package org.openspcoop2.web.monitor.core.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
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

	public final static String GATEWAY_DEFAULT = "Gateway";
	public final static String ALIAS_DEFAULT = ConfigurazioneNodiRuntime.ALIAS_DEFAULT;
	
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

			List<String> listaPdDMonitorate_StatusPdD = this.getListaPdDMonitorate_StatusPdD();

			ConfigurazioneNodiRuntime config = pddMonitorProperties.getConfigurazioneNodiRuntime();
			
			
			for (String nomeSonda : listaPdDMonitorate_StatusPdD) {
				
				String nomeNodoRuntime = nomeSonda;
				if(listaPdDMonitorate_StatusPdD.size()==1 && SondaPddStatus.GATEWAY_DEFAULT.equals(nomeSonda)) {
					if(!config.containsNode(nomeSonda) && config.containsNode(SondaPddStatus.ALIAS_DEFAULT)) {
						nomeNodoRuntime = SondaPddStatus.ALIAS_DEFAULT;
					}
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
					throw new Exception("Alternative Properties ["+p1+"] or [configurazioni.risorseJmxPdd.remoteAccess.url] not found");
				}
				
				boolean https = false;
				boolean https_verificaHostName = true;
				boolean https_autenticazioneServer = true;
				String https_truststorePath = null;
				String https_truststoreType = null;
				String https_truststorePassword = null;
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
					
					String httpsP_verificaHostName = this.propertiesSonda.getProperty(nomeSonda+".https.verificaHostName");
					if(httpsP_verificaHostName!=null) {
						https_verificaHostName = "false".equals(httpsP_verificaHostName.trim()); // default true
					}
					else {
						https_verificaHostName = config.isHttps_verificaHostName(nomeNodoRuntime);
					}
					
					String httpsP_autenticazioneServer = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer");
					if(httpsP_autenticazioneServer!=null) {
						https_autenticazioneServer = "false".equals(httpsP_autenticazioneServer.trim()); // default true
					}
					else {
						https_autenticazioneServer = config.isHttps_autenticazioneServer(nomeNodoRuntime);
					}
				
					if(https_autenticazioneServer) {
						
						https_truststorePath = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststorePath");
						if(https_truststorePath==null) {
							https_truststorePath = config.getHttps_autenticazioneServer_truststorePath(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(https_truststorePath)) {
							throw new Exception("[gateway:"+nomeSonda+"] TLS Truststore path non fornito");
						}
						
						https_truststoreType = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststoreType");
						if(https_truststoreType==null) {
							https_truststoreType = config.getHttps_autenticazioneServer_truststoreType(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(https_truststoreType)) {
							throw new Exception("[gateway:"+nomeSonda+"] TLS Truststore type non fornito");
						}
						
						https_truststorePassword = this.propertiesSonda.getProperty(nomeSonda+".https.autenticazioneServer.truststorePassword");
						if(https_truststorePassword==null) {
							https_truststorePassword = config.getHttps_autenticazioneServer_truststorePassword(nomeNodoRuntime);
						}
						if(StringUtils.isEmpty(https_truststorePassword)) {
							throw new Exception("[gateway:"+nomeSonda+"] TLS Truststore password non fornito");
						}
					}
				}
				
				PddStatus pddStat = new PddStatus();
				//pddStat.setNome(nomeSonda);
				String descrizione = null;
				if(config.containsNode(nomeNodoRuntime)) {
					descrizione = config.getDescrizione(nomeNodoRuntime);
				}
				pddStat.setNome(descrizione!=null ? descrizione : nomeSonda);
				pddStat.setUrl(url);
				pddStat.setHttps(https);
				pddStat.setHttps_verificaHostName(https_verificaHostName);
				pddStat.setHttps_autenticazioneServer(https_autenticazioneServer);
				pddStat.setHttps_autenticazioneServer_truststorePath(https_truststorePath);
				pddStat.setHttps_autenticazioneServer_truststoreType(https_truststoreType);
				pddStat.setHttps_autenticazioneServer_truststorePassword(https_truststorePassword);
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
			this.log.error("Si e' verificato un errore durante l'Init Sonda Pdd Status: " + e.getMessage(),e); 
			throw e;
		}
	}



	@Override
	public List<IStatus> updateStato() throws Exception {

		for (IStatus pddBean : this.listaStatus) {

			try{
				_check(((PddStatus) pddBean));
				pddBean.setStato(SondaStatus.OK);
				pddBean.setDescrizione(null);
			}catch(Exception e){
				if(e instanceof HttpUtilsException) {
					HttpUtilsException http = (HttpUtilsException) e;
					if(http.getReturnCode()>=200 && http.getReturnCode()<=299) {
						pddBean.setStato(SondaStatus.WARNING);
					}
					else {
						pddBean.setStato(SondaStatus.ERROR);
					}
				}
				else {
					pddBean.setStato(SondaStatus.ERROR);
				}
				pddBean.setDescrizione(e.getMessage());
				this.log.error("Sonda '"+pddBean.getNome()+"' ha rilevato un errore: "+e.getMessage(),e);
			}

		}

		return this.listaStatus;
	}

	private void _check(PddStatus pdd) throws Exception {
		boolean https = pdd.isHttps();
		boolean https_verificaHostName = true;
		boolean https_autenticazioneServer = true;
		String https_truststorePath = null;
		String https_truststoreType = null;
		String https_truststorePassword = null;
		if(https) {
			https_verificaHostName = pdd.isHttps_verificaHostName();
			https_autenticazioneServer = pdd.isHttps_autenticazioneServer();
			if(https_autenticazioneServer) {
				https_truststorePath = pdd.getHttps_autenticazioneServer_truststorePath();
				if(StringUtils.isEmpty(https_truststorePath)) {
					throw new Exception("[alias:"+pdd.getNome()+"] TLS Truststore path non fornito");
				}
				https_truststoreType = pdd.getHttps_autenticazioneServer_truststoreType();
				if(StringUtils.isEmpty(https_truststoreType)) {
					throw new Exception("[alias:"+pdd.getNome()+"] TLS Truststore type non fornito");
				}
				https_truststorePassword = pdd.getHttps_autenticazioneServer_truststorePassword();
				if(StringUtils.isEmpty(https_truststorePassword)) {
					throw new Exception("[alias:"+pdd.getNome()+"] TLS Truststore password non fornito");
				}
			}
		}
		
		if(https) {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setUrl(pdd.getUrl());
			httpRequest.setReadTimeout(pdd.getReadConnectionTimeout());
			httpRequest.setConnectTimeout(pdd.getConnectionTimeout());
			httpRequest.setMethod(HttpRequestMethod.GET);
			httpRequest.setHostnameVerifier(https_verificaHostName);
			if(https_autenticazioneServer) {
				httpRequest.setTrustStorePath(https_truststorePath);
				httpRequest.setTrustStoreType(https_truststoreType);
				httpRequest.setTrustStorePassword(https_truststorePassword);
			}
			else {
				httpRequest.setTrustAllCerts(true);
			}
			HttpUtilities.check(httpRequest);
		}
		else {
			HttpUtilities.check(pdd.getUrl(), pdd.getReadConnectionTimeout(), pdd.getConnectionTimeout());
		}
	}
	
	public List<String> getListaPdDMonitorate_StatusPdD() throws Exception{
		return PddMonitorProperties.getInstance(this.log).getListaPdDMonitorate_StatusPdD();
	}

	@Override
	public String getMessaggioStatoSondaPdd() throws Exception {
		int tot_ok = this.getTotOk();
		// Tutti in errore
		if(tot_ok == 0){
			if(this.listaStatus.size()==1)
				return "Il Gateway non è funzionante";
			else
				return "Nessuno dei "+this.listaStatus.size()+" nodi del Gateway è funzionante";
		}else 
			// parzialmente in errore
			if(tot_ok< this.listaStatus.size()){
				return  (this.listaStatus.size() - tot_ok) + " su " +this.listaStatus.size()+ " nodi del Gateway non sono funzionanti";
			}else {
				// tutti ok
				if(this.listaStatus.size()==1)
					return "Il Gateway è funzionante";
				else
					return "Tutti i "+this.listaStatus.size()+" nodi del Gateway sono funzionanti";
			}
	}


	public int getTotOk() throws Exception {
		int totOk = 0;
		try{
			for (IStatus pddBean : this.listaStatus) {
				if(pddBean.getStato().equals(SondaStatus.OK)) 
					totOk ++;
			}
		}catch(Exception e){

		}
		return totOk;
	}

	@Override
	public SondaStatus getStatoSondaPdd() throws Exception {
		int tot_ok = this.getTotOk();
		// Tutti in errore
		if(tot_ok == 0){
			boolean findWarning = false;
			try{
				for (IStatus pddBean : this.listaStatus) {
					if(pddBean.getStato().equals(SondaStatus.WARNING)) { 
						findWarning = true;
						break;
					}
				}
				if(findWarning) {
					return SondaStatus.WARNING;
				}
				else {
					return SondaStatus.ERROR;
				}
			}catch(Exception e){
				return SondaStatus.ERROR;
			}
		}else
			// parzialmente in errore
			if(tot_ok< this.listaStatus.size()){
				return  SondaStatus.WARNING;
			}else {
				return SondaStatus.OK;
			}
	}

}
