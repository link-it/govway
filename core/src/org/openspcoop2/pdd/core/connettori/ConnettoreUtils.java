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

package org.openspcoop2.pdd.core.connettori;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;


/**
 * ConnettoreUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreUtils {

	public static String formatLocation(HttpRequestMethod httpMethod, String location){
		if(httpMethod!=null)
			return location+" http-method:"+httpMethod;
		else
			return location;
	}
	
	public static String getAndReplaceLocationWithBustaValues(IConnettore connector, ConnettoreMsg connettoreMsg,Busta busta,PdDContext pddContext,IProtocolFactory<?> protocolFactory,Logger log) throws ConnettoreException{
		
		boolean dynamicLocation = false;
		
		String location = null;
		if(TipiConnettore.NULL.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreNULL.LOCATION;
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreNULLEcho.LOCATION;
		}
		else if(ConnettoreStresstest.ENDPOINT_TYPE.equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreStresstest.LOCATION;
		}
		else if(TipiConnettore.FILE.getNome().equals(connettoreMsg.getTipoConnettore())){
			try{
				location = ((ConnettoreFILE)connector).buildLocation(connettoreMsg);
				//dynamicLocation = true; la dinamicita viene gia gestita nel metodo buildLocation
			}catch(Exception e){
				log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
				location = "N.D.";
			}
		}
		else if(ConnettoreRicezioneBusteDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			if(connector!=null) {
				try{
					((ConnettoreRicezioneBusteDirectVM)connector).validate(connettoreMsg);
					((ConnettoreRicezioneBusteDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
					location = connector.getLocation();
					dynamicLocation = true;
				}catch(Exception e){
					log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
					location = "N.D.";
				}
			}
			else {
				log.debug("Connettore non disponibile"); // caso in cui il tipo indicato o la classe non esiste
				location = "undefined";
			}
		}
		else if(ConnettoreRicezioneContenutiApplicativiDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			if(connector!=null) {
				try{
					((ConnettoreRicezioneContenutiApplicativiDirectVM)connector).validate(connettoreMsg);
					((ConnettoreRicezioneContenutiApplicativiDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
					location = connector.getLocation();
					dynamicLocation = true;
				}catch(Exception e){
					log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
					location = "N.D.";
				}
			}
			else {
				log.debug("Connettore non disponibile"); // caso in cui il tipo indicato o la classe non esiste
				location = "undefined";
			}
		}
		else if(ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			if(connector!=null) {
				try{
					((ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM)connector).validate(connettoreMsg);
					((ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
					location = connector.getLocation();
					dynamicLocation = true;
				}catch(Exception e){
					log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
					location = "N.D.";
				}
			}
			else {
				log.debug("Connettore non disponibile"); // caso in cui il tipo indicato o la classe non esiste
				location = "undefined";
			}
		}
		else{
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION)!=null){
				location = connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
				dynamicLocation = true; // http, jms, ...
			}
		}
		
		if(location !=null && (location.equals("")==false) ){
			
			// Keyword old
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO,busta.getTipoServizio());
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO,busta.getServizio());
			if(busta.getAzione()!=null){
				location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE,busta.getAzione());
			}
			
			// Dynamic
			// Costruisco Mappa per dynamic name
			if(dynamicLocation && connector!=null) {
				try {
					Map<String, Object> dynamicMap = ((ConnettoreBase)connector).buildDynamicMap(connettoreMsg);
					location = DynamicUtils.convertDynamicPropertyValue(CostantiConnettori.CONNETTORE_LOCATION, location, dynamicMap, pddContext, false);
				}catch(Exception e){
					log.error("Errore durante la costruzione della location (dynamic): "+e.getMessage(),e);
				}
			}
			
			connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION,location);
		}
		
		return location;
	}
	
	public static String buildLocationWithURLBasedParameter(Logger log, OpenSPCoop2Message msg, String tipoConnettore, Map<String, List<String>> propertiesURLBased, String locationParam,
			IProtocolFactory<?> protocolFactory, String idModulo) throws ConnettoreException{
		
		if(TipiConnettore.HTTP.toString().equals(tipoConnettore) || 
				TipiConnettore.HTTPS.toString().equals(tipoConnettore) ||
				ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
				ConnettoreHTTPCORE5.ENDPOINT_TYPE.equals(tipoConnettore) ||
				ConnettoreSAAJ.ENDPOINT_TYPE.equals(tipoConnettore)  ||
				ConnettoreStresstest.ENDPOINT_TYPE.equals(tipoConnettore)){
	
			try{
			
				OpenSPCoop2MessageProperties forwardParameter = null;
				if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
					forwardParameter = msg.getForwardUrlProperties(OpenSPCoop2Properties.getInstance().getRESTServicesUrlParametersForwardConfig());
				}
				else {
					forwardParameter = msg.getForwardUrlProperties(OpenSPCoop2Properties.getInstance().getSOAPServicesUrlParametersForwardConfig());
				}
				
				Map<String, List<String>>  p = propertiesURLBased;
				if(forwardParameter!=null && forwardParameter.size()>0){
					if(p==null){
						p = new HashMap<String, List<String>> ();
					}
					Iterator<String> keys = forwardParameter.getKeys();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						List<String> values = forwardParameter.getPropertyValues(key);
						if(values!=null && !values.isEmpty()){
							if(p.containsKey(key)){
								p.remove(key);
							}
							p.put(key, values);
						}
					}
				}
				
				String location = locationParam;
				if(ServiceBinding.REST.equals(msg.getServiceBinding())){
					
					String normalizedInterfaceName = normalizeInterfaceName(msg, idModulo, protocolFactory);
					
					return RestUtilities.buildUrl(log, location, p, msg.getTransportRequestContext(),
							normalizedInterfaceName);
				}
				else{
					boolean encodeBaseLocation = true; // la base location può contenere dei parametri
					return TransportUtils.buildUrlWithParameters(p, location, encodeBaseLocation, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
				}
				
			}catch(Exception e){
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
		return locationParam;
	}

	public static String normalizeInterfaceName(OpenSPCoop2Message msg, String idModulo, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
		String nomePortaInvocata = null;
		if(nomePortaInvocataObject!=null && nomePortaInvocataObject instanceof String) {
			nomePortaInvocata = (String) nomePortaInvocataObject;
		}
		else if(msg.getTransportRequestContext()!=null && msg.getTransportRequestContext().getInterfaceName()!=null) {
			nomePortaInvocata = msg.getTransportRequestContext().getInterfaceName();
		}
		
		String normalizedInterfaceName = null;
		if(nomePortaInvocata!=null) {
			PorteNamingUtils namingUtils = new PorteNamingUtils(protocolFactory);
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)){
				normalizedInterfaceName = namingUtils.normalizePA(nomePortaInvocata);
			}
			else {
				normalizedInterfaceName = namingUtils.normalizePD(nomePortaInvocata);
			}
		}
		
		return normalizedInterfaceName;
	}
	
	public static String limitLocation255Character(String location){
		return TransportUtils.limitLocation255Character(location);
	}
	
	public static String addProxyInfoToLocationForHTTPConnector(String tipoConnettore, Map<String, String> properties, String location){
		if(TipiConnettore.HTTP.toString().equals(tipoConnettore) || 
				TipiConnettore.HTTPS.toString().equals(tipoConnettore)){
			if(properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null){
				String proxyHostname = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
				String proxyPort = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
				return location+" [proxy: "+proxyHostname+":"+proxyPort+"]";
			}
		}
		return location;
	}
	
	public static String addGovWayProxyInfoToLocationForHTTPConnector(ForwardProxy forwardProxy, IConnettore connectorSender, String location) throws ConnettoreException {
		if(forwardProxy!=null && connectorSender instanceof ConnettoreBaseHTTP) {
			ConnettoreBaseHTTP http = (ConnettoreBaseHTTP) connectorSender;
			http.updateForwardProxy(forwardProxy);
			if(http.updateLocation_forwardProxy(location)) {
				return http.getLocation();
			}
		}
		return location;
	}
	
	public static String formatTipoConnettore(OpenSPCoop2Properties props, String tipoConnector, boolean async) {
		if(tipoConnector!=null) {
			if(async && props.isNIOConfig_convertToAsyncClient()) {
				return props.convertToAsyncClientConnector(tipoConnector);
			}
		}
		return tipoConnector;
	}

	public static void printDatiConnettore(org.openspcoop2.core.registry.Connettore connettore, String labelTipoConnettore, String labelNomeConnettore,
			boolean connettoreStatic,
			StringBuilder sb,
			String separator, String newLine,
			boolean printIntestazione) {
		printDatiConnettore(connettore.mappingIntoConnettoreConfigurazione(), labelTipoConnettore, labelNomeConnettore,
				null, connettoreStatic,
				sb,
				separator, newLine,
				printIntestazione);
	}
	public static void printDatiConnettore(org.openspcoop2.core.config.Connettore connettore, String labelTipoConnettore, String labelNomeConnettore,
			ServizioApplicativo sa, 
			boolean connettoreStatic,
			StringBuilder sb,
			String separator, String newLine,
			boolean printIntestazione) {

		if(connettoreStatic) {
			return; // non visualizzo nulla
		}
		
		Boolean integrationManager = null;
		InvocazioneCredenziali invCredenziali = null;
		if(sa!=null) {
			InvocazioneServizio is = sa.getInvocazioneServizio();
			integrationManager = is.getGetMessage()!=null && StatoFunzionalita.ABILITATO.equals(is.getGetMessage());
			invCredenziali = is.getCredenziali();
		}
		
		TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
		if(tipo==null) {
			tipo = TipiConnettore.CUSTOM;
		}
		
		if(printIntestazione) {
			sb.append(newLine);
			String label = labelNomeConnettore!=null ? " "+labelNomeConnettore : "";
			sb.append("- "+CostantiLabel.LABEL_CONNETTORE+label+" -");
		}
		
		String tipoConnettore = labelTipoConnettore;
		if(TipiConnettore.DISABILITATO.equals(tipo)) {
			if(integrationManager!=null && integrationManager) {
				tipoConnettore = "MessageBox";
			}
		}
		else {
			if(integrationManager!=null && integrationManager) {
				tipoConnettore = tipoConnettore + " + MessageBox";
			}
		}
		sb.append(newLine);
		sb.append(CostantiLabel.LABEL_TIPO_CONNETTORE);
		sb.append(separator);
		sb.append(tipoConnettore);
		
		String labelEndpoint = CostantiLabel.LABEL_CONNETTORE_ENDPOINT;
		String endpoint = null;
		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			endpoint = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
		}
		else if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			String tipoCoda = getProperty(CostantiConnettori.CONNETTORE_JMS_TIPO, connettore.getPropertyList());
			labelEndpoint = CostantiLabel.LABEL_CONNETTORE_JMS_NOME_CODA+" "+tipoCoda;
			endpoint = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
		}
		else if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			labelEndpoint = CostantiLabel.LABEL_OUTPUT_FILE;
			endpoint = getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, connettore.getPropertyList());
		}
		else if(TipiConnettore.NULL.getNome().equals(connettore.getTipo())){
			//endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULL.LOCATION;
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			//endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho.LOCATION;
		}
		else {
			String endpointV = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
			if(StringUtils.isNotEmpty(endpointV)) {
				endpoint = endpointV;
			}
		}
		if(endpoint!=null) {
			sb.append(newLine);
			sb.append(labelEndpoint);
			sb.append(separator);
			sb.append(endpoint);
		}
		
		if(sa!=null && CostantiConfigurazione.SERVER.equals(sa.getTipo())) {
			sb.append(newLine);
			sb.append(CostantiLabel.LABEL_SERVER);
			sb.append(separator);
			sb.append(sa.getNome());
		}
		
		String connectionTimeout = getProperty(CostantiConnettori.CONNETTORE_CONNECTION_TIMEOUT, connettore.getPropertyList());
		if(connectionTimeout!=null) {
			try {
				long l = Long.valueOf(connectionTimeout);
				if(l>0) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
					sb.append(separator);
					sb.append(Utilities.convertSystemTimeIntoString_millisecondi(l, true, false, " "," ",""));
				}
			}catch(Throwable t) {}
		}
		String readConnectionTimeout = getProperty(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT, connettore.getPropertyList());
		if(readConnectionTimeout!=null) {
			try {
				long l = Long.valueOf(readConnectionTimeout);
				if(l>0) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
					sb.append(separator);
					sb.append(Utilities.convertSystemTimeIntoString_millisecondi(l, true, false, " "," ",""));
				}
			}catch(Throwable t) {}
		}
		
		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			
			String token = getProperty(CostantiConnettori.CONNETTORE_TOKEN_POLICY, connettore.getPropertyList());
			if(token!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_TOKEN).append(" - ").append(CostantiLabel.LABEL_CONNETTORE_TOKEN_POLICY);
				sb.append(separator);
				sb.append(token);
			}
			
			String username = null;
			if(invCredenziali!=null){
				username = invCredenziali.getUser();
			}
			else{
				username = getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
			}
			if(username!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTP).append(" - ").append(CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				sb.append(separator);
				sb.append(username);
			}
			
			String bearerToken = getProperty(CostantiConnettori.CONNETTORE_BEARER_TOKEN, connettore.getPropertyList());
			if(bearerToken!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_BEARER).append(" ").append(CostantiLabel.LABEL_CONNETTORE_BEARER_TOKEN);
				sb.append(separator);
				sb.append(bearerToken);
			}
		}
		
		if(TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			
			boolean trustAllCerts = false;
			String trustAllCertsV = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_ALL_CERTS, connettore.getPropertyList());
			if("true".equalsIgnoreCase(trustAllCertsV)) {
				trustAllCerts = true;
			}
			
			String trustLocation = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_LOCATION, connettore.getPropertyList());
			
			if(trustAllCerts || trustLocation!=null) {
				
				String sslType = getProperty(CostantiConnettori.CONNETTORE_HTTPS_SSL_TYPE, connettore.getPropertyList());
				if(sslType!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS);
					sb.append(separator);
					sb.append(sslType);
				}
				
				String hostnameVerifier = getProperty(CostantiConnettori.CONNETTORE_HTTPS_HOSTNAME_VERIFIER, connettore.getPropertyList());
				if(hostnameVerifier!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_HTTPS_HOST_VERIFY);
					sb.append(separator);
					sb.append(hostnameVerifier);
				}
				
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE);
				sb.append(separator);
				if(trustAllCerts) {
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUST_ALL_CERTS);
				}
				else {
					String trustType = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_TYPE, connettore.getPropertyList());
					boolean hsm = HSMUtils.isKeystoreHSM(trustType);
					if(hsm) {
						trustLocation = CostantiLabel.CONNETTORE_HSM;
					}
					sb.append("(").append(trustType).append(") ").append(trustLocation);
				}
				
				String trustCRL = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLs, connettore.getPropertyList());
				if(trustCRL!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLs);
					sb.append(separator);
					sb.append(hostnameVerifier);
				}
			}
			
			String keyLocation = getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_LOCATION, connettore.getPropertyList());
			if(keyLocation!=null) {
				
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE);
				sb.append(separator);
				
				String keyType = getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_TYPE, connettore.getPropertyList());
				boolean hsm = HSMUtils.isKeystoreHSM(keyType);
				if(hsm) {
					keyLocation = CostantiLabel.CONNETTORE_HSM;
				}
				sb.append("(").append(keyType).append(") ").append(keyLocation);
				
				String keyAlias = getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_ALIAS, connettore.getPropertyList());
				if(keyAlias!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEY_ALIAS);
					sb.append(separator);
					sb.append(keyAlias);
				}
			}
		}
		
		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			String proxyHostname = getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME, connettore.getPropertyList());
			if(proxyHostname!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_PROXY);
				sb.append(separator);
				String proxyPort = getProperty(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT, connettore.getPropertyList());
				if(proxyPort!=null) {
					sb.append(proxyHostname).append(":").append(proxyPort);
				}
			}
		}
		
		if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			 
			String username = null;
			if(invCredenziali!=null){
				username = invCredenziali.getUser();
			}
			else{
				username = getProperty(CostantiConnettori.CONNETTORE_USERNAME, connettore.getPropertyList());
			}
			if(username!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_CONNETTORE_JMS_USERNAME);
				sb.append(separator);
				sb.append(username);
			}
			
		}

		if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			
			String f = getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS, connettore.getPropertyList());
			if(f!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_OUTPUT_FILE_HEADER);
				sb.append(separator);
				sb.append(f);
			}
			
			f = getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE, connettore.getPropertyList());
			if(f!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_INPUT_FILE);
				sb.append(separator);
				sb.append(f);
			}
			
			f = getProperty(CostantiConnettori.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS, connettore.getPropertyList());
			if(f!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_INPUT_FILE_HEADER);
				sb.append(separator);
				sb.append(f);
			}
		}
		
	}
	
	public static String getEndpointConnettore(org.openspcoop2.core.registry.Connettore connettore, boolean connettoreStatic) {
		return getEndpointConnettore(connettore.mappingIntoConnettoreConfigurazione(), connettoreStatic);
	}
	public static String getEndpointConnettore(org.openspcoop2.core.config.Connettore connettore, boolean connettoreStatic) {

		if(connettoreStatic) {
			return null; // non visualizzo nulla
		}
		
		TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
		if(tipo==null) {
			tipo = TipiConnettore.CUSTOM;
		}
		
		String endpoint = null;
		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			endpoint = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
		}
		else if(TipiConnettore.JMS.getNome().equals(connettore.getTipo())){
			endpoint = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
		}
		else if(TipiConnettore.FILE.getNome().equals(connettore.getTipo())){
			endpoint = getProperty(CostantiConnettori.CONNETTORE_FILE_REQUEST_OUTPUT_FILE, connettore.getPropertyList());
		}
		else if(TipiConnettore.NULL.getNome().equals(connettore.getTipo())){
			endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULL.LOCATION;
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho.LOCATION;
		}
		else {
			String endpointV = getProperty(CostantiConnettori.CONNETTORE_LOCATION, connettore.getPropertyList());
			if(StringUtils.isNotEmpty(endpointV)) {
				endpoint = endpointV;
			}
		}
		return endpoint;
	}
	
	public static String getNegoziazioneTokenPolicyConnettore(org.openspcoop2.core.registry.Connettore connettore) {
		return getNegoziazioneTokenPolicyConnettore(connettore.mappingIntoConnettoreConfigurazione());
	}
	public static String getNegoziazioneTokenPolicyConnettore(org.openspcoop2.core.config.Connettore connettore) {
		return getProperty(CostantiConnettori.CONNETTORE_TOKEN_POLICY, connettore.getPropertyList());
	}
	
	public static String getNegoziazioneTokenEndpoint(GenericProperties gp, Logger log) {
		PolicyNegoziazioneToken policy = null;
		try {
			policy = TokenUtilities.convertTo(gp);
			return policy.getEndpoint();
		}catch(Exception e) {
			log.error("Errore durante il reperimento dell'endpoint della policy '"+gp.getNome()+"': "+e.getMessage(),e);
			return null;
		}
	}
	
	private static String getProperty(String nome,List<org.openspcoop2.core.config.Property> list){
		if(list!=null && list.size()>0){
			for (org.openspcoop2.core.config.Property property : list) {
				if(property.getNome().equals(nome)){
					return property.getValore();
				}
			}
		}
		return null;
	}
}
