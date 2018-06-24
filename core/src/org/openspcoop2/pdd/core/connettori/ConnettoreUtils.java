/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageProperties;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
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
	
	public static String getAndReplaceLocationWithBustaValues(ConnettoreMsg connettoreMsg,Busta busta,PdDContext pddContext,IProtocolFactory<?> protocolFactory,Logger log) throws ConnettoreException{
		
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
			ConnettoreFILE c = new ConnettoreFILE();
			try{
				c.init(pddContext, protocolFactory);
				location = c.buildLocation(connettoreMsg);
			}catch(Exception e){
				log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
			}
		}
		else if(ConnettoreRicezioneBusteDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			ConnettoreRicezioneBusteDirectVM c = new ConnettoreRicezioneBusteDirectVM();
			try{
				c.validate(connettoreMsg);
				c.buildLocation(connettoreMsg.getConnectorProperties(),false);
			}catch(Exception e){
				log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
			}
			location = c.getLocation();
		}
		else if(ConnettoreRicezioneContenutiApplicativiDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			ConnettoreRicezioneContenutiApplicativiDirectVM c = new ConnettoreRicezioneContenutiApplicativiDirectVM();
			try{
				c.validate(connettoreMsg);
				c.buildLocation(connettoreMsg.getConnectorProperties(),false);
			}catch(Exception e){
				log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
			}
			location = c.getLocation();
		}
		else if(ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM.TIPO.equals(connettoreMsg.getTipoConnettore())){
			ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM c = new ConnettoreRicezioneContenutiApplicativiHTTPtoSOAPDirectVM();
			try{
				c.validate(connettoreMsg);
				c.buildLocation(connettoreMsg.getConnectorProperties(),false);
			}catch(Exception e){
				log.error("Errore durante la costruzione della location: "+e.getMessage(),e);
			}
			location = c.getLocation();
		}
		else{
			if(connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION)!=null){
				location = connettoreMsg.getConnectorProperties().get(CostantiConnettori.CONNETTORE_LOCATION);
			}
		}
		
		if(location !=null && (location.equals("")==false) ){
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_TIPO_SERVIZIO,busta.getTipoServizio());
			location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_NOME_SERVIZIO,busta.getServizio());
			if(busta.getAzione()!=null){
				location = location.replace(CostantiConnettori.CONNETTORE_JMS_LOCATION_REPLACE_TOKEN_AZIONE,busta.getAzione());
			}
			connettoreMsg.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION,location);
		}
		
		return location;
	}
	
	public static String buildLocationWithURLBasedParameter(OpenSPCoop2Message msg, String tipoConnettore, Properties propertiesURLBased, String locationParam,
			IProtocolFactory<?> protocolFactory, String idModulo) throws ConnettoreException{
		
		if(TipiConnettore.HTTP.toString().equals(tipoConnettore) || 
				TipiConnettore.HTTPS.toString().equals(tipoConnettore) ||
				ConnettoreHTTPCORE.ENDPOINT_TYPE.equals(tipoConnettore) ||
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
				
				Properties p = propertiesURLBased;
				if(forwardParameter!=null && forwardParameter.size()>0){
					if(p==null){
						p = new Properties();
					}
					Enumeration<?> keys = forwardParameter.getKeys();
					while (keys.hasMoreElements()) {
						String key = (String) keys.nextElement();
						String value = forwardParameter.getProperty(key);
						if(value!=null){
							if(p.containsKey(key)){
								p.remove(key);
							}
							p.setProperty(key, value);
						}
					}
				}
				
				String location = locationParam;
				if(ServiceBinding.REST.equals(msg.getServiceBinding())){
					String normalizedInterfaceName = null;
					if(msg.getTransportRequestContext()!=null && msg.getTransportRequestContext().getInterfaceName()!=null) {
						PorteNamingUtils namingUtils = new PorteNamingUtils(protocolFactory);
						if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idModulo)){
							normalizedInterfaceName = namingUtils.normalizePA(msg.getTransportRequestContext().getInterfaceName());
						}
						else {
							normalizedInterfaceName = namingUtils.normalizePD(msg.getTransportRequestContext().getInterfaceName());
						}
					}
					return RestUtilities.buildUrl(location, p, msg.getTransportRequestContext(),
							normalizedInterfaceName);
				}
				else{
					return TransportUtils.buildLocationWithURLBasedParameter(p, location, OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
				}
				
			}catch(Exception e){
				throw new ConnettoreException(e.getMessage(),e);
			}
		}
		return locationParam;
	}

	public static String limitLocation255Character(String location){
		return TransportUtils.limitLocation255Character(location);
	}
	
	public static String addProxyInfoToLocationForHTTPConnector(String tipoConnettore, Hashtable<String, String> properties, String location){
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
}
