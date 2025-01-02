/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.constants.ConnettoriHttpImpl;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.OCSPManager;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
	
	private ConnettoreUtils() {}

	public static String formatLocation(HttpRequestMethod httpMethod, String location){
		if(httpMethod!=null)
			return location+" http-method:"+httpMethod;
		else
			return location;
	}
	
	public static String getPrefixRedirect(int codice, String redirectLocation) {
		return "Gestione redirect (code:"+codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") ";
	} 
	
	private static String getErrorMessageBuildLocation(Exception e) {
		return "Errore durante la costruzione della location: "+e.getMessage();
	}

	public static String getAndReplaceLocationWithBustaValues(IConnettore connector, ConnettoreMsg connettoreMsg,Busta busta,PdDContext pddContext,IProtocolFactory<?> protocolFactory,Logger log) {
		
		if(protocolFactory!=null) {
			// nop
		}
		
		boolean dynamicLocation = false;
		
		String location = null;
		if(TipiConnettore.NULL.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreNULL.LOCATION;
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreNULLEcho.LOCATION;
		}
		else if(TipiConnettore.STATUS.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreStatus.LOCATION;
		}
		else if(ConnettoreStresstest.ENDPOINT_TYPE.equals(connettoreMsg.getTipoConnettore())){
			location = ConnettoreStresstest.LOCATION;
		}
		else if(TipiConnettore.FILE.getNome().equals(connettoreMsg.getTipoConnettore())){
			location = buildLocationFile(connector,  connettoreMsg, log);
			//dynamicLocation = true; la dinamicita viene gia gestita nel metodo buildLocation
		}
		else if(ConnettoreRicezioneBusteDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore()) ||
				ConnettoreRicezioneContenutiApplicativiDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore()) ||
				ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			location = buildLocationDirectVM(connector, connettoreMsg, log);
			if(location!=null) {
				dynamicLocation = true;
			}
		}
		else{
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION)!=null){
				location = connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
				dynamicLocation = true; // http, jms, ...
			}
		}
		
		if(location==null) {
			location = "N.D.";
		}
		
		return getAndReplaceLocationWithBustaValues(connector, connettoreMsg, location, busta, pddContext, log, dynamicLocation);
	}
	private static String buildLocationFile(IConnettore connector, ConnettoreMsg connettoreMsg,Logger log) {
		String l = null;
		try{
			l = ((ConnettoreFILE)connector).buildLocation(connettoreMsg);
			//dynamicLocation = true; la dinamicita viene gia gestita nel metodo buildLocation
		}catch(Exception e){
			log.error(getErrorMessageBuildLocation(e),e);
		}
		return l;
	}
	private static String buildLocationDirectVM(IConnettore connector, ConnettoreMsg connettoreMsg,Logger log) {
		String l = null;
		if(ConnettoreRicezioneBusteDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())) {
			l = buildLocationRicezioneBusteDirectVM(connector, connettoreMsg, log);
		}
		else if(ConnettoreRicezioneContenutiApplicativiDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			l = buildLocationRicezioneContenutiApplicativiDirectVM(connector, connettoreMsg, log);
		}
		else {
			l = buildLocationRicezioneContenutiApplicativiHTTPtoSOAPDirectVM(connector, connettoreMsg, log);
		}
		return l;
	}
	private static String buildLocationRicezioneBusteDirectVM(IConnettore connector, ConnettoreMsg connettoreMsg,Logger log) {
		String l = null;
		try{
			((ConnettoreRicezioneBusteDirectVM)connector).validate(connettoreMsg);
			((ConnettoreRicezioneBusteDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
			l = connector.getLocation();
		}catch(Exception e){
			log.error(getErrorMessageBuildLocation(e),e);
		}
		return l;
	}
	private static String buildLocationRicezioneContenutiApplicativiDirectVM(IConnettore connector, ConnettoreMsg connettoreMsg,Logger log) {
		String l = null;
		try{
			((ConnettoreRicezioneContenutiApplicativiDirectVM)connector).validate(connettoreMsg);
			((ConnettoreRicezioneContenutiApplicativiDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
			l = connector.getLocation();
		}catch(Exception e){
			log.error(getErrorMessageBuildLocation(e),e);
		}
		return l;
	}
	private static String buildLocationRicezioneContenutiApplicativiHTTPtoSOAPDirectVM(IConnettore connector, ConnettoreMsg connettoreMsg,Logger log) {
		String l = null;
		try{
			((ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM)connector).validate(connettoreMsg);
			((ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM)connector).buildLocation(connettoreMsg.getConnectorProperties(),false);
			l = connector.getLocation();
		}catch(Exception e){
			log.error(getErrorMessageBuildLocation(e),e);
		}
		return l;
	}
	private static String getAndReplaceLocationWithBustaValues(IConnettore connector, ConnettoreMsg connettoreMsg,String location, Busta busta,PdDContext pddContext,Logger log,boolean dynamicLocation) {
		if(location !=null && (!location.equals("")) ){
			
			// Keyword old
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO,busta.getTipoServizio());
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO,busta.getServizio());
			if(busta.getAzione()!=null){
				location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE,busta.getAzione());
			}
			
			// Dynamic
			// Costruisco Mappa per dynamic name
			if(dynamicLocation) {
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
				TipiConnettore.HTTPS.toString().equals(tipoConnettore)||
				ConnettoreHTTP.ENDPOINT_TYPE.equals(tipoConnettore) ||
				ConnettoreHTTPS.ENDPOINT_TYPE.equals(tipoConnettore) ||
				org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
				org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPSCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
				org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
				org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPSCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
				ConnettoreSAAJ.ENDPOINT_TYPE.equals(tipoConnettore) ||
				ConnettoreStresstest.ENDPOINT_TYPE.equals(tipoConnettore)){
	
			try{
			
				OpenSPCoop2MessageProperties forwardParameter = null;
				if(ServiceBinding.REST.equals(msg.getServiceBinding())) {
					forwardParameter = msg.getForwardUrlProperties(OpenSPCoop2Properties.getInstance().getRESTServicesUrlParametersForwardConfig());
				}
				else {
					forwardParameter = msg.getForwardUrlProperties(OpenSPCoop2Properties.getInstance().getSOAPServicesUrlParametersForwardConfig());
				}
				
				Map<String, List<String>>  p = buildMapLocationWithURLBasedParameter(propertiesURLBased, forwardParameter);
				
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
	private static Map<String, List<String>> buildMapLocationWithURLBasedParameter(Map<String, List<String>> propertiesURLBased, OpenSPCoop2MessageProperties forwardParameter) {
		Map<String, List<String>>  p = propertiesURLBased;
		if(forwardParameter!=null && forwardParameter.size()>0){
			if(p==null){
				p = new HashMap<> ();
			}
			Iterator<String> keys = forwardParameter.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				List<String> values = forwardParameter.getPropertyValues(key);
				if(values!=null && !values.isEmpty()){
					if(p.containsKey(key)){
						p.remove(key);
					}
					p.put(key, values);
				}
			}
		}
		return p;
	}

	public static String normalizeInterfaceName(OpenSPCoop2Message msg, String idModulo, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
		String nomePortaInvocata = null;
		if(nomePortaInvocataObject instanceof String nomePortaInvocataS) {
			nomePortaInvocata = nomePortaInvocataS;
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
		if( 
				(
						TipiConnettore.HTTP.toString().equals(tipoConnettore) 
						|| 
						TipiConnettore.HTTPS.toString().equals(tipoConnettore)
				)
				&& 
				properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null
			){
			String proxyHostname = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			String proxyPort = properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			return location+" [proxy: "+proxyHostname+":"+proxyPort+"]";
		}
		return location;
	}
	
	public static String addGovWayProxyInfoToLocationForHTTPConnector(ForwardProxy forwardProxy, IConnettore connectorSender, String location) throws ConnettoreException {
		if(forwardProxy!=null && connectorSender instanceof ConnettoreBaseHTTP http) {
			http.updateForwardProxy(forwardProxy);
			if(http.updateLocationForwardProxy(location)) {
				return http.getLocation();
			}
		}
		return location;
	}
	
	public static String formatTipoConnettore(OpenSPCoop2Properties props, String tipoConnector, ConnettoreMsg connettoreMsg, IAsyncResponseCallback asyncResponseCallback) {
		if(tipoConnector==null) {
			return null;
		}
		
		TipiConnettore t = TipiConnettore.toEnumFromName(tipoConnector); 
		
		if(!TipiConnettore.CUSTOM.equals(t) &&
				!TipiConnettore.HTTP.equals(t) &&
				!TipiConnettore.HTTPS.equals(t)) {
			return tipoConnector;
		}
		
		String newTipoConnettore = formatTipoConnettore(props, tipoConnector, t, connettoreMsg, asyncResponseCallback);
		if(!tipoConnector.equals(newTipoConnettore)) {
			connettoreMsg.setTipoConnettore(newTipoConnettore);
		}
		return newTipoConnettore;
	}
	private static String formatTipoConnettore(OpenSPCoop2Properties props, String tipoConnector, TipiConnettore t, ConnettoreMsg connettoreMsg, IAsyncResponseCallback asyncResponseCallback) {
		if(connettoreMsg.getConnectorProperties()!=null && !connettoreMsg.getConnectorProperties().isEmpty()) {
			String impl = connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_HTTP_IMPL);
			ConnettoriHttpImpl cImpl = ConnettoriHttpImpl.getConnettoreHttpImplSafe(impl);
			if(cImpl!=null) {
				return formatTipoConnettore(tipoConnector, t, cImpl, asyncResponseCallback);
			}
		}
		
		boolean nio = asyncResponseCallback!=null;
		String libreriaHttpDefault = nio ? props.getConnettoreNIOLibreriaHttpDefault() : props.getConnettoreLibreriaHttpDefault();
		String libreriaHttpsDefault = nio ? props.getConnettoreNIOLibreriaHttpsDefault() : props.getConnettoreLibreriaHttpsDefault();
		return formatTipoConnettore(tipoConnector, t, 
				nio,
				libreriaHttpDefault,
				libreriaHttpsDefault);
	}
	private static final String NIO_SUFFIX_TIPO_CONNETTORE = "-nio";
	private static String formatTipoConnettore(String tipoConnector, TipiConnettore t, 
			boolean nio,
			String libreriaHttpDefault,
			String libreriaHttpsDefault) {
		
		if(isHttp(t, tipoConnector) &&
				libreriaHttpDefault!=null && StringUtils.isNotEmpty(libreriaHttpDefault)) {
			return formatTipoConnettore(libreriaHttpDefault, 
					nio?
							TipiConnettore.HTTP.getNome()+NIO_SUFFIX_TIPO_CONNETTORE // per far finire nei log 'http-nio'
							:
							TipiConnettore.HTTP.getNome() // per far finire nei log 'http'
					);
		}
		else if(isHttps(t, tipoConnector) &&
				libreriaHttpsDefault!=null && StringUtils.isNotEmpty(libreriaHttpsDefault)) {
			return formatTipoConnettore(libreriaHttpsDefault, 
					nio?
							TipiConnettore.HTTPS.getNome()+NIO_SUFFIX_TIPO_CONNETTORE // per far finire nei log 'https-nio'
							:
							TipiConnettore.HTTPS.getNome() // per far finire nei log 'https'
					);
		}
		else {
			return tipoConnector;
		}
	}
	private static String formatTipoConnettore(String libreriaDefault, String tipoDefault) {
		String tipo = libreriaDefault;
		String classTipo = ClassNameProperties.getInstance().getConnettore(tipo);
		String defaultHttp = ClassNameProperties.getInstance().getConnettore(tipoDefault);
		if(classTipo.equals(defaultHttp)) {
			return tipoDefault;
		}
		else {
			return tipo;
		}
	}
	private static boolean forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting = true;
	public static boolean isForceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting() {
		return forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting;
	}
	public static void setForceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting(
			boolean forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting) {
		ConnettoreUtils.forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting = forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting;
	}
	private static String formatTipoConnettore(String tipoConnector, TipiConnettore t, ConnettoriHttpImpl cImpl, IAsyncResponseCallback asyncResponseCallback) {
		switch (cImpl) {
		case HTTP_CORE5: {
			return formatTipoConnettoreHttpCore5Library(tipoConnector, t, asyncResponseCallback);
		}
		case HTTP_URL_CONNECTION: {
			return formatTipoConnettoreHttpUrlConnectionLibrary(tipoConnector, t, asyncResponseCallback);
		}
		default: {
			break;
		}
		}
		return null;
	}	
	private static String formatTipoConnettoreHttpCore5Library(String tipoConnector, TipiConnettore t, IAsyncResponseCallback asyncResponseCallback) {
		if(isHttp(t, tipoConnector)) {
			return asyncResponseCallback!=null ?  
					org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCORE.ENDPOINT_TYPE :  org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE.ENDPOINT_TYPE;
		}
		else if(isHttps(t, tipoConnector)) {
			return asyncResponseCallback!=null ?  
					org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPSCORE.ENDPOINT_TYPE : org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPSCORE.ENDPOINT_TYPE;
		}
		return null;
	}
	private static String formatTipoConnettoreHttpUrlConnectionLibrary(String tipoConnector, TipiConnettore t, IAsyncResponseCallback asyncResponseCallback) {
		if(isHttp(t, tipoConnector)) {
			if(asyncResponseCallback!=null && forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting) {
				return org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCORE.ENDPOINT_TYPE;
			}
			return ConnettoreHTTP.ENDPOINT_TYPE;
		}
		else if(isHttps(t, tipoConnector)) {
			if(asyncResponseCallback!=null && forceUseHttpCore5NioInAsyncChannelWithHttpUrlConnectionLibrarySetting) {
				return org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPSCORE.ENDPOINT_TYPE;
			}
			return ConnettoreHTTPS.ENDPOINT_TYPE;
		}
		return null;
	}
	public static boolean isHttp(TipiConnettore t, String tipoConnector) {
		return (t!=null &&
			(
					TipiConnettore.HTTP.equals(t) 
					||
					(
							TipiConnettore.CUSTOM.equals(t) &&
							(
									org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnector) 
									||
									org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnector) 
									|| 
									ConnettoreHTTP.ENDPOINT_TYPE.equals(tipoConnector)
							)
					)
			)
		);
	}
	public static boolean isHttps(TipiConnettore t, String tipoConnector) {
		return (t!=null &&
			(
					TipiConnettore.HTTPS.equals(t) 
					||
					(
							TipiConnettore.CUSTOM.equals(t) &&
							(
									org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPSCORE.ENDPOINT_TYPE.equals(tipoConnector) 
									|| 
									org.openspcoop2.pdd.core.connettori.httpcore5.nio.ConnettoreHTTPSCORE.ENDPOINT_TYPE.equals(tipoConnector) 
									|| 
									ConnettoreHTTPS.ENDPOINT_TYPE.equals(tipoConnector)
							)
					)
			)
		);
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
			/**endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULL.LOCATION;*/
		}
		else if(TipiConnettore.NULLECHO.getNome().equals(connettore.getTipo())){
			/**endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreNULLEcho.LOCATION;*/
		}
		else if(TipiConnettore.STATUS.getNome().equals(connettore.getTipo())){
			/**endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreStatus.LOCATION;*/
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
				long l = Long.parseLong(connectionTimeout);
				if(l>0) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
					sb.append(separator);
					sb.append(Utilities.convertSystemTimeIntoStringMillisecondi(l, true, false, " "," ",""));
				}
			}catch(Exception t) {
				// ignore
			}
		}
		String readConnectionTimeout = getProperty(CostantiConnettori.CONNETTORE_READ_CONNECTION_TIMEOUT, connettore.getPropertyList());
		if(readConnectionTimeout!=null) {
			try {
				long l = Long.parseLong(readConnectionTimeout);
				if(l>0) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
					sb.append(separator);
					sb.append(Utilities.convertSystemTimeIntoStringMillisecondi(l, true, false, " "," ",""));
				}
			}catch(Exception t) {
				// ignore
			}
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
			
			String apiKey = getProperty(CostantiConnettori.CONNETTORE_APIKEY, connettore.getPropertyList());
			if(apiKey!=null && StringUtils.isNotEmpty(apiKey)){
				
				String apiKeyHeader = getProperty(CostantiConnettori.CONNETTORE_APIKEY_HEADER, connettore.getPropertyList());
				if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
					apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
				}
				
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_API_KEY).append(" '").append(apiKeyHeader).append("'");
				/**sb.append(separator);
				sb.append(apiKey); Informazione sensibile*/
				
				String appId = getProperty(CostantiConnettori.CONNETTORE_APIKEY_APPID, connettore.getPropertyList());
				if(appId!=null && StringUtils.isNotEmpty(appId)){
					
					String appIdHeader = getProperty(CostantiConnettori.CONNETTORE_APIKEY_APPID_HEADER, connettore.getPropertyList());
					if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
						appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
					}
					
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_API_KEY_APP_ID).append(" '").append(appIdHeader).append("'");
					sb.append(separator);
					sb.append(appId);
					
				}
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
				
				String trustCRL = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_CRLS, connettore.getPropertyList());
				if(trustCRL!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_CRLS);
					sb.append(separator);
					sb.append(trustCRL);
				}
				
				String trustOCSP = getProperty(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY, connettore.getPropertyList());
				if(trustOCSP!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_TRUSTSTORE_OCSP_POLICY);
					sb.append(separator);
					try {
						String label = OCSPManager.getInstance().getOCSPConfig(trustOCSP).getLabel();
						sb.append((label!=null && StringUtils.isNotEmpty(label)) ? label : trustOCSP);
					}catch(Exception t) {
						sb.append(trustOCSP);	
					}
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
				
				String keyBYOK = getProperty(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY, connettore.getPropertyList());
				if(keyBYOK!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_VERIFICA_CONNETTORE_DETAILS_HTTPS_KEYSTORE_BYOK_POLICY);
					sb.append(separator);
					try {
						String label = BYOKManager.getInstance().getKSMConfigByType(keyBYOK).getLabel();
						sb.append((label!=null && StringUtils.isNotEmpty(label)) ? label : keyBYOK);
					}catch(Exception t) {
						sb.append(keyBYOK);	
					}
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
		
		if(TipiConnettore.HTTP.getNome().equals(connettore.getTipo()) || TipiConnettore.HTTPS.getNome().equals(connettore.getTipo())){
			
			String dataTransferMode = getProperty(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE, connettore.getPropertyList());
			if(dataTransferMode!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
				sb.append(separator);
				sb.append(dataTransferMode);
				
				String dataTransferChunked = getProperty(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE, connettore.getPropertyList());
				if(dataTransferChunked!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
					sb.append(separator);
					sb.append(dataTransferChunked);
				}
			}
			
			String redirect = getProperty(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW, connettore.getPropertyList());
			if(redirect!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
				sb.append(separator);
				sb.append(redirect);
				
				String redirectHop = getProperty(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_MAX_HOP, connettore.getPropertyList());
				if(redirectHop!=null) {
					sb.append(newLine);
					sb.append(CostantiLabel.LABEL_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
					sb.append(separator);
					sb.append(redirectHop);
				}
			}
			
			String impl = getProperty(CostantiConnettori.CONNETTORE_HTTP_IMPL, connettore.getPropertyList());
			if(impl!=null) {
				sb.append(newLine);
				sb.append(CostantiLabel.LABEL_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
				sb.append(separator);
				sb.append(impl);
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
		
		/**TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
		if(tipo==null) {
			tipo = TipiConnettore.CUSTOM;
		}*/
		
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
		else if(TipiConnettore.STATUS.getNome().equals(connettore.getTipo())){
			endpoint = org.openspcoop2.pdd.core.connettori.ConnettoreStatus.LOCATION;
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
		if(list!=null && !list.isEmpty()){
			for (org.openspcoop2.core.config.Property property : list) {
				if(property.getNome().equals(nome)){
					return property.getValore();
				}
			}
		}
		return null;
	}
	
	
	public static String getNomeConnettori(Context context) {
		String connettoriMultipli = null;
		if(context.containsKey(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI_BY_ID)) {
			Object oConnettori = context.getObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_CONNETTORI_BY_ID );
			if (oConnettori instanceof List){
				List<?> l = (List<?>) oConnettori;
				String s = getNomeConnettori(l);
				if(s!=null && s.length()>0) {
					return s;
				}
			}
		}
		return connettoriMultipli;
	}
	private static String getNomeConnettori(List<?> l) {
		if(!l.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object object : l) {
				if(object instanceof String s) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(s);
				}
			}
			if(sb.length()>0) {
				return sb.toString();
			}
		}
		return null;
	}
}
