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
package org.openspcoop2.monitor.engine.status;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.monitor.engine.constants.SondaStatus;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.HttpUtilsException;
import org.slf4j.Logger;

/**
 * StatusUtilities
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatusUtilities {

	public static List<IStatus> updateStato( List<IStatus> listaStatus, Logger log ) throws Exception {

		for (IStatus status : listaStatus) {
			updateStato(status, log);
		}

		return listaStatus;
	}
	
	public static void updateStato( IStatus status, Logger log ) throws Exception {

		try{
			_check(((GatewayStatus) status));
			status.setStato(SondaStatus.OK);
			status.setDescrizione(null);
		}catch(Exception e){
			if(e instanceof HttpUtilsException) {
				HttpUtilsException http = (HttpUtilsException) e;
				if(http.getReturnCode()>=200 && http.getReturnCode()<=299) {
					status.setStato(SondaStatus.WARNING);
				}
				else {
					status.setStato(SondaStatus.ERROR);
				}
			}
			else {
				status.setStato(SondaStatus.ERROR);
			}
			status.setDescrizione(e.getMessage());
			log.error("Verifica '"+status.getNome()+"' fallita: "+e.getMessage(),e);
		}

	}
	
	private static void _check(GatewayStatus status) throws Exception {
		boolean https = status.isHttps();
		boolean https_verificaHostName = true;
		boolean https_autenticazioneServer = true;
		String https_truststorePath = null;
		String https_truststoreType = null;
		String https_truststorePassword = null;
		if(https) {
			https_verificaHostName = status.isHttps_verificaHostName();
			https_autenticazioneServer = status.isHttps_autenticazioneServer();
			if(https_autenticazioneServer) {
				https_truststorePath = status.getHttps_autenticazioneServer_truststorePath();
				if(StringUtils.isEmpty(https_truststorePath)) {
					throw new Exception("[alias:"+status.getNome()+"] TLS Truststore path non fornito");
				}
				https_truststoreType = status.getHttps_autenticazioneServer_truststoreType();
				if(StringUtils.isEmpty(https_truststoreType)) {
					throw new Exception("[alias:"+status.getNome()+"] TLS Truststore type non fornito");
				}
				https_truststorePassword = status.getHttps_autenticazioneServer_truststorePassword();
				if(StringUtils.isEmpty(https_truststorePassword)) {
					throw new Exception("[alias:"+status.getNome()+"] TLS Truststore password non fornito");
				}
			}
		}
		
		if(https) {
			HttpRequest httpRequest = new HttpRequest();
			httpRequest.setUrl(status.getUrl());
			httpRequest.setReadTimeout(status.getReadConnectionTimeout());
			httpRequest.setConnectTimeout(status.getConnectionTimeout());
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
			HttpUtilities.check(status.getUrl(), status.getReadConnectionTimeout(), status.getConnectionTimeout());
		}
	}
	
	public static int getTotOk(List<IStatus> listaStatus) throws Exception {
		int totOk = 0;
		try{
			for (IStatus pddBean : listaStatus) {
				if(pddBean.getStato().equals(SondaStatus.OK)) 
					totOk ++;
			}
		}catch(Exception e){

		}
		return totOk;
	}
	
	public static SondaStatus statesProcess(List<IStatus> listaStatus) throws Exception {
		int tot_ok = getTotOk(listaStatus);
		// Tutti in errore
		if(tot_ok == 0){
			boolean findWarning = false;
			try{
				for (IStatus pddBean : listaStatus) {
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
			if(tot_ok< listaStatus.size()){
				return  SondaStatus.WARNING;
			}else {
				return SondaStatus.OK;
			}
	}
	
	public static String getDetailStatesProcess(List<IStatus> listaStatus) throws Exception {
		int tot_ok = getTotOk(listaStatus);
		// Tutti in errore
		if(tot_ok == 0){
			if(listaStatus.size()==1)
				return "Il Gateway non è funzionante";
			else
				return "Nessuno dei "+listaStatus.size()+" nodi del Gateway è funzionante";
		}else {
			// parzialmente in errore
			if(tot_ok< listaStatus.size()){
				return  (listaStatus.size() - tot_ok) + " su " +listaStatus.size()+ " nodi del Gateway non sono funzionanti";
			}else {
				// tutti ok
				if(listaStatus.size()==1)
					return "Il Gateway è funzionante";
				else
					return "Tutti i "+listaStatus.size()+" nodi del Gateway sono funzionanti";
			}
		}
	}
}
