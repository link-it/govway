/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
	public final static String ALIAS_DEFAULT = "pdd";
	
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

			for (String namePdD : this.getListaPdDMonitorate_StatusPdD()) {
				
				String url = this.propertiesSonda.getProperty(namePdD+".url"); //"pdd."+
				if(url==null) {
					url = pddMonitorProperties.getJmxPdD_remoteAccess_url(namePdD);
				}
				if(url==null) {
					String p1 = "statoPdD.sonde.standard."+namePdD+".url";
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
				
				String httpsP = this.propertiesSonda.getProperty(namePdD+".https");
				if(httpsP!=null) {
					https = "true".equals(httpsP.trim()); // default false
				}
				else {
					https = pddMonitorProperties.isJmxPdD_remoteAccess_https(namePdD);
				}
				
				String connectionTimeoutS = this.propertiesSonda.getProperty(namePdD+".connectionTimeout");
				if(connectionTimeoutS==null) {
					connectionTimeoutS = pddMonitorProperties.getJmxPdD_remoteAccess_connectionTimeout(namePdD);
				}					
				if(connectionTimeoutS!=null) {
					connectionTimeout = Integer.valueOf(connectionTimeoutS);
				}
				
				String readConnectionTimeoutS = this.propertiesSonda.getProperty(namePdD+".readConnectionTimeout");
				if(readConnectionTimeoutS==null) {
					readConnectionTimeoutS = pddMonitorProperties.getJmxPdD_remoteAccess_readConnectionTimeout(namePdD);
				}
				if(readConnectionTimeoutS!=null) {
					readConnectionTimeout = Integer.valueOf(readConnectionTimeoutS);
				}
								
				if(https) {
					
					String httpsP_verificaHostName = this.propertiesSonda.getProperty(namePdD+".https.verificaHostName");
					if(httpsP_verificaHostName!=null) {
						https_verificaHostName = "false".equals(httpsP_verificaHostName.trim()); // default true
					}
					else {
						https_verificaHostName = pddMonitorProperties.isJmxPdD_remoteAccess_https_verificaHostName(namePdD);
					}
					
					String httpsP_autenticazioneServer = this.propertiesSonda.getProperty(namePdD+".https.autenticazioneServer");
					if(httpsP_autenticazioneServer!=null) {
						https_autenticazioneServer = "false".equals(httpsP_autenticazioneServer.trim()); // default true
					}
					else {
						https_autenticazioneServer = pddMonitorProperties.isJmxPdD_remoteAccess_https_autenticazioneServer(namePdD);
					}
				
					if(https_autenticazioneServer) {
						
						https_truststorePath = this.propertiesSonda.getProperty(namePdD+".https.autenticazioneServer.truststorePath");
						if(https_truststorePath==null) {
							https_truststorePath = pddMonitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststorePath(namePdD);
						}
						if(StringUtils.isEmpty(https_truststorePath)) {
							throw new Exception("[gateway:"+namePdD+"] TLS Truststore path non fornito");
						}
						
						https_truststoreType = this.propertiesSonda.getProperty(namePdD+".https.autenticazioneServer.truststoreType");
						if(https_truststoreType==null) {
							https_truststoreType = pddMonitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststoreType(namePdD);
						}
						if(StringUtils.isEmpty(https_truststoreType)) {
							throw new Exception("[gateway:"+namePdD+"] TLS Truststore type non fornito");
						}
						
						https_truststorePassword = this.propertiesSonda.getProperty(namePdD+".https.autenticazioneServer.truststorePassword");
						if(https_truststorePassword==null) {
							https_truststorePassword = pddMonitorProperties.getJmxPdD_remoteAccess_https_autenticazioneServer_truststorePassword(namePdD);
						}
						if(StringUtils.isEmpty(https_truststorePassword)) {
							throw new Exception("[gateway:"+namePdD+"] TLS Truststore password non fornito");
						}
					}
				}
				
				PddStatus pddStat = new PddStatus();
				pddStat.setNome(namePdD);
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
