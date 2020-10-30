/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.as4.config;

import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.StatusRequest;
import org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.MessageStatus;
import org.openspcoop2.protocol.basic.config.BasicManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.slf4j.Logger;
import org.w3c.dom.Node;

import backend.ecodex.org._1_1.SubmitResponse;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ProtocolManager extends BasicManager {
	
	protected AS4Properties as4Properties = null;
	protected Logger logger = null;
	public AS4ProtocolManager(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.logger = this.getProtocolFactory().getLogger();
		this.as4Properties = AS4Properties.getInstance();
	}
	
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo() {
		return this.as4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD() {
		return this.as4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
	}

	@Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext,
    		IRegistryReader registryReader,
    		boolean integration) throws ProtocolException{
		try {
			if(busta!=null && msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())) {
				OpenSPCoop2SoapMessage soapMsg = (OpenSPCoop2SoapMessage) msg.castAsSoap();
				
				OpenSPCoop2MessageFactory messageFactory = msg!=null ? msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
				
				if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()==false) {
					
					List<Node> list = SoapUtils.getNotEmptyChildNodes(messageFactory, soapMsg.getSOAPBody(), false);
					if(list.size()==1) {
						Node n = list.get(0);
						// provo a vedere se si tratta di una risposta
						if(backend.ecodex.org._1_1.utils.ProjectInfo.getInstance().getProjectNamespace().equals(n.getNamespaceURI()) &&
								"submitResponse".equals(n.getLocalName())) {
							
							// recupero id
							backend.ecodex.org._1_1.utils.serializer.JaxbDeserializer deserializer = new backend.ecodex.org._1_1.utils.serializer.JaxbDeserializer();
							byte[]b=XMLUtils.getInstance(messageFactory).toByteArray(n);
							SubmitResponse submitResponse = deserializer.readSubmitResponse(b);
							String responseId = submitResponse.getMessageIDList().get(0);
							busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID, responseId);
							
							// recupero stato
							Connettore connettore = this.protocolFactory.createProtocolVersionManager(this.protocolFactory.createProtocolConfiguration().getVersioneDefault()).
								getStaticRoute(new IDSoggetto(busta.getTipoMittente(), busta.getMittente()),
										IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
												new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario()), 
												busta.getVersioneServizio()), 
										registryReader);
							String url = null;
							for (Property p : connettore.getPropertyList()) {
								if(CostantiConnettori.CONNETTORE_LOCATION.equals(p.getNome())) {
									url = p.getValore();
									break;
								}
							}
							org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendInterface domibusPort = null;
							org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendService11 domibusService = null;
							domibusService = new org.openspcoop2.protocol.as4.stub.backend_ecodex.v1_1.BackendService11(new URL(url+"?wsdl"));
							domibusPort = domibusService.getBACKENDPORT();
							BindingProvider imProviderMessageBox = (BindingProvider)domibusPort;
							imProviderMessageBox.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);

							StatusRequest statusReq = new StatusRequest();
							statusReq.setMessageID(responseId);
							MessageStatus stat = domibusPort.getStatus(statusReq);
							int index = 10;
							while( (stat==null) && index<10) {
								Utilities.sleep(1000);
								stat = domibusPort.getStatus(statusReq);
							}
							if(stat==null) {
								ProtocolException pe = new ProtocolException("Fallito recupero da Domibus dello stato del messaggio spedito con id '"+responseId+"'");
								pe.setForceTrace(true);
								throw pe;
							}
							if(MessageStatus.SEND_IN_PROGRESS.equals(stat)) {
								this.log.debug("Stato del messaggio con id '"+responseId+"' risulta in spedizione");
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS, stat.name());
							}
							else if(MessageStatus.SEND_ENQUEUED.equals(stat)) {
								this.log.debug("Stato del messaggio con id '"+responseId+"' risulta salvato su domibus, in attesa di essere spedito");
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS, stat.name());
							}
							else if(MessageStatus.ACKNOWLEDGED.equals(stat) || MessageStatus.ACKNOWLEDGED_WITH_WARNING.equals(stat)) {
								this.log.debug("Stato del messaggio con id '"+responseId+"': "+stat);
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS, stat.name());
							}
							else {
								this.log.error("Stato del messaggio con id '"+responseId+"': "+stat);
								busta.addProperty(AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS, stat.name());
								ProtocolException pe = new ProtocolException("Domibus non è riuscito a gestire la spedizione del messaggio con id '"+responseId+"'; stato ritornato: "+stat);
								pe.setForceTrace(true);
								throw pe;
							}
							
							// risposta non prevista, profili oneway
							return null;
						}
					}
				}
			}
			return super.updateOpenSPCoop2MessageResponse(msg, busta, notifierInputStreamParams, transportRequestContext, transportResponseContext, registryReader, integration);
		}
		catch(ProtocolException pe) {
			throw pe;
		}
		catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	@Override
	public boolean isStaticRoute() throws ProtocolException {
		return true;
	}
	
	@Override
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException{
		try {
			boolean registry = this.as4Properties.isDomibusGatewayRegistry();
			if(registry) {
				
				String tipo = this.protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
				
				IDSoggetto idSoggettoGateway = null;
				String nome = this.as4Properties.getDomibusGatewayRegistrySoggettoCustom(idSoggettoMittente.getNome());
				if(nome!=null && !"".equals(nome)) {
					idSoggettoGateway = new IDSoggetto(tipo, nome);
				}
				else {
					idSoggettoGateway = new IDSoggetto(tipo, this.as4Properties.getDomibusGatewayRegistrySoggettoDefault());
				}
				
				Soggetto s = null;
				try {
					s = registryReader.getSoggetto(idSoggettoGateway);
					if(s==null) {
						throw new RegistryNotFound();
					}
				}catch(RegistryNotFound notFound) {
					throw new Exception("Soggetto Gateway ["+idSoggettoGateway+"], indicato nella configurazione, non risulta esistere nel registro",notFound);
				}
				if(s.getConnettore()==null || TipiConnettore.DISABILITATO.getNome().equals(s.getConnettore().getTipo())) {
					throw new Exception("Soggetto Gateway ["+idSoggettoGateway+"], indicato nella configurazione, non contiene la definizione di un connettore");
				}
				return s.getConnettore();
			}
			else {
				String url = this.as4Properties.getDomibusGatewayConfigCustomUrl(idSoggettoMittente.getNome());
				if(url==null || "".equals(url)) {
					url = this.as4Properties.getDomibusGatewayConfigDefaultUrl();
				}
								
				String tipoConnettore = TipiConnettore.HTTP.getNome();
				Properties pConnettore = null;
				Boolean https = this.as4Properties.isDomibusGatewayConfigCustomHttpsEnabled(idSoggettoMittente.getNome());
				if(https==null) {
					https = this.as4Properties.isDomibusGatewayConfigDefaultHttpsEnabled();
				}
				if(https) {
					tipoConnettore = TipiConnettore.HTTPS.getNome();
					pConnettore = this.as4Properties.getDomibusGatewayConfigCustomHttpsProperties(idSoggettoMittente.getNome());
					if(pConnettore==null || pConnettore.size()<=0) {
						pConnettore = this.as4Properties.getDomibusGatewayConfigDefaultHttpsProperties();
					}
				}
				
				
				org.openspcoop2.core.registry.Connettore c = new org.openspcoop2.core.registry.Connettore();
				c.setNome("DomibusGateway");
				c.setTipo(tipoConnettore);
				Property pUrl = new Property();
				pUrl.setNome(CostantiConnettori.CONNETTORE_LOCATION);
				pUrl.setValore(url);
				c.getPropertyList().add(pUrl);
				if(pConnettore!=null && pConnettore.size()>0) {
					Enumeration<?> names = pConnettore.keys();
					while (names.hasMoreElements()) {
						Object object = (Object) names.nextElement();
						if(object instanceof String) {
							String key = (String) object;
							String value = pConnettore.getProperty(key);
							Property p = new Property();
							p.setNome(key);
							p.setValore(value);
							c.getPropertyList().add(p);
						}
					}
				}
				return c;
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

}
