/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.security.message.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityReceiver;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityUtilities;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.authorization.IMessageSecurityAuthorization;
import org.openspcoop2.security.message.authorization.MessageSecurityAuthorizationRequest;
import org.openspcoop2.security.message.authorization.MessageSecurityAuthorizationResult;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.saml.SAMLBuilderConfigConstants;



/**
 * Classe per la gestione della Sicurezza  (role:Receiver)
 *
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MessageSecurityReceiver_impl extends MessageSecurityReceiver{


	protected MessageSecurityReceiver_impl(MessageSecurityContext messageSecurityContext) {
		super(messageSecurityContext);
	}

	@Override
	protected boolean process(OpenSPCoop2Message message,Busta busta) {
		try{
			
			
			IMessageSecurityReceiver receiverInterface = this.messageSecurityContext.getMessageSecurityReceiver();
			
			
			
			
			// ** Fix per SOAPFault (quando ci sono le encryptionParts o le signatureParts, la Sicurezza fallisce se c'e' un SOAPFault) **
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				if(message.castAsSoap().getSOAPBody().hasFault()){
					
					if(MessageSecurityUtilities.processSOAPFault(this.messageSecurityContext.getIncomingProperties()) == false){
						return true; // non devo applicare la sicurezza.
					}
					
				}	
			}
			
			String action = (String) this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.ACTION);
			if(action==null || "".equals(action.trim())){
				return true; // nessuna action: non devo applicare la sicurezza.
			}
			
			
			
			
			// ** Inizializzazione parametri **
			
			// Authentication class from properties
			String authClass = (String) this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.AUTHORIZATION_CLASS);
			// MustUnderstand from properties
//			boolean mustUnderstandValue = false;
//			Object mustUnderstand = this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.MUST_UNDERSTAND);
//			if(mustUnderstand!=null){
//				mustUnderstandValue = Boolean.parseBoolean((String)mustUnderstand);
//			}
			String actor = this.messageSecurityContext.getActor();
			if("".equals(this.messageSecurityContext.getActor()))
				actor = null;
			
			
			
			
			// ** Verifico presenza header sicurezza ** /
			if(receiverInterface.checkExistsWSSecurityHeader()) {
				if(this.messageSecurityContext.existsSecurityHeader(message, actor)==false){
					this.msgErrore =  "Header Message Security, richiesto dalla configurazione (action:"+action+"), non riscontrato nella SOAPEnvelope ricevuta";
					if(action.contains(SecurityConstants.ACTION_SIGNATURE)){
						this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE;
					}
					else if(action.contains(SecurityConstants.ACTION_ENCRYPT)){
						this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_PRESENTE;
					}
					else{
						this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE;
					}
					return false;
				}
				
				// se e' richiesta la verifica SAML la faccio.
				if(action.contains(SecurityConstants.ACTION_SAML_TOKEN_SIGNED) || action.contains(SecurityConstants.ACTION_SAML_TOKEN_UNSIGNED)) {
					String samlVersion = (String) this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.SAML_VERSION_XMLCONFIG_ID);
					if(samlVersion!=null && !"".equals(samlVersion)) {
						
						SOAPHeaderElement securityHeader = this.messageSecurityContext.getSecurityHeader(message, actor); // verificato prima
						SOAPElement samlToken = this.messageSecurityContext.getSAMLTokenInSecurityHeader(securityHeader, samlVersion);
						if(samlToken==null) {
							this.msgErrore =  "SAMLToken (versione:"+samlVersion+"), richiesto dalla configurazione (action:"+action+"), non riscontrato nell'header Message Security presente all'interno della SOAPEnvelope ricevuta";
							this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_PRESENTE;
							return false;
						}
						
						String samlConfirmationType = (String) this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID);
						if(samlConfirmationType!=null && !"".equals(samlConfirmationType)) {
							String confirmationMethod = this.messageSecurityContext.getSAMLTokenSubjectConfirmationMethodInSecurityHeader(samlToken, samlVersion);
							if(confirmationMethod==null) {
								this.msgErrore =  "SAMLToken (versione:"+samlVersion+"), richiesto dalla configurazione (action:"+action+"), non possiede un metodo di subject confirmation";
								this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO;
								return false;
							}
							
							String atteso = null;
							if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_HOLDER_OF_KEY.equals(samlConfirmationType)) {
								atteso = SecurityConstants.SAML_VERSION_XMLCONFIG_ID_VALUE_20.equals(samlVersion) ? 
										SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_20 :
											SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_HOLDER_OF_KEY_SAML_10;
							}
							else if(SecurityConstants.SAML_SUBJECT_CONFIRMATION_VALIDATION_METHOD_XMLCONFIG_ID_SENDER_VOUCHES.equals(samlConfirmationType)) {
								atteso = SecurityConstants.SAML_VERSION_XMLCONFIG_ID_VALUE_20.equals(samlVersion) ? 
										SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_20 :
											SAMLBuilderConfigConstants.SAML_CONFIG_BUILDER_SUBJECT_CONFIRMATION_METHOD_SENDER_VOUCHES_SAML_10;
							}
							if(atteso!=null) {
								if(atteso.equals(confirmationMethod)==false) {
									this.msgErrore =  "SAMLToken (versione:"+samlVersion+"), richiesto dalla configurazione (action:"+action+"), possiede un metodo di subject confirmation ("+confirmationMethod+") diverso da quello atteso ("+atteso+")";
									this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_TOKEN_NON_VALIDO;
									return false;
								}
							}
						}
						
					}
				}
			}
			
			
			
			
			// ** Raccolgo elementi "toccati" da Security per pulirli ** /
			List<Reference> elementsToClean = null;
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				elementsToClean = receiverInterface.getDirtyElements(this.messageSecurityContext, message.castAsSoap());
			}
			
			
			
			
			// ** Verifica cifratura/firma elementi richiesti dalla configurazione ** /
			List<SubErrorCodeSecurity> listaErroriRiscontrati = new ArrayList<SubErrorCodeSecurity>();
			Map<QName, QName> notResolved = null;
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				notResolved = receiverInterface.checkEncryptSignatureParts(this.messageSecurityContext,elementsToClean, message.castAsSoap(),listaErroriRiscontrati);
			}
			if(listaErroriRiscontrati.size()>0){
				StringBuffer bf = new StringBuffer();
				for (Iterator<?> iterator = listaErroriRiscontrati.iterator(); iterator.hasNext();) {
					SubErrorCodeSecurity subCodiceErroreSicurezza = (SubErrorCodeSecurity) iterator.next();
					if(bf.length()>0){
						bf.append(" ; ");
					}
					bf.append(subCodiceErroreSicurezza.getMsgErrore());
				}
				this.messageSecurityContext.getLog().error(bf.toString());
				this.msgErrore =  bf.toString();
				this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE;
				this.listaSubCodiceErrore.addAll(listaErroriRiscontrati);
				return false;
			}
			
			
			
			
			// ** Applico sicurezza tramite engine **/
			receiverInterface.process(this.messageSecurityContext, message, busta);
			
			
			
			
			
			// ** Verifica elementi cifrati come element, possibile solo dopo la decifratura ** //
			List<SubErrorCodeSecurity> listaErroriInElementi = new ArrayList<SubErrorCodeSecurity>();
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				receiverInterface.checkEncryptionPartElements(notResolved, message.castAsSoap(), listaErroriInElementi);
			}
			if(listaErroriInElementi.size()>0){
				StringBuffer bf = new StringBuffer();
				for (Iterator<?> iterator = listaErroriInElementi.iterator(); iterator.hasNext();) {
					SubErrorCodeSecurity subCodiceErroreSicurezza = (SubErrorCodeSecurity) iterator.next();
					if(bf.length()>0){
						bf.append(" ; ");
					}
					bf.append(subCodiceErroreSicurezza.getMsgErrore());
				}
				this.messageSecurityContext.getLog().error(bf.toString());
				this.msgErrore =  bf.toString();
				this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_NON_PRESENTE;
				this.listaSubCodiceErrore.addAll(listaErroriInElementi);
				return false;
			}
			
			
			
			
			// ** Lettura Subject Certificato **/
			try{
				this.subject=receiverInterface.getCertificate();
			}catch(Exception e){
				this.messageSecurityContext.getLog().error("GetPrincipal Message-Security(Receiver) error: "+e.getMessage(),e);
			}

			
			
			
			// ** Autorizzazione Message Security **/
            if (authClass != null) {
                try {
            		IMessageSecurityAuthorization auth = (IMessageSecurityAuthorization)org.openspcoop2.utils.resources.Loader.getInstance().newInstance(authClass);
            		MessageSecurityAuthorizationRequest req = new MessageSecurityAuthorizationRequest();
            		req.setBusta(busta);
            		req.setSecurityPrincipal(this.subject);
            		req.setMessageSecurityContext(this.messageSecurityContext);
            		req.setMessage(message);
                    MessageSecurityAuthorizationResult result = auth.authorize(req);
                    if(!result.isAuthorized()){
                        this.msgErrore =  "Mittente della busta ["+busta.getTipoMittente()+busta.getMittente()+
                                "] (subject:"+this.subject+") non autorizzato ad invocare il servizio ["+busta.getServizio()+"] erogato dal soggetto ["+busta.getTipoDestinatario()+busta.getDestinatario()+"]";
                        if(result.getErrorMessage()!=null){
                                this.msgErrore = this.msgErrore + "(" +result.getErrorMessage()+")";
                        }
                        if(result.getException()!=null){
                        	this.messageSecurityContext.getLog().error(this.msgErrore,result.getException());
                        }
                        this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA;
                        return false;
                    }
                }
                catch (Exception e) {
                    throw new Exception("Errore di Processamento durante l'autorizzazione: " + e.getMessage());
                }
            }
			
            
            
            
            
            // ** Pulizia elementi "toccati" da MessageSecurity per pulirli ** /
			// NOTA: Clean dipendente dall'implementazione
			try{
				boolean detachValue = true; // per default l'header WSS viene eliminato
				Object detach = this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.DETACH_HEADER_WSS); // per backward compatibility
				if(detach!=null){
					detachValue = Boolean.parseBoolean((String)detach);
				}
				if(detach==null) {
					detach = this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.DETACH_SECURITY_INFO);
					if(detach!=null){
						detachValue = Boolean.parseBoolean((String)detach);
					}
				}
				
				boolean removeAllIdRefValue = this.messageSecurityContext.isRemoveAllWsuIdRef();
				Object removeAllIdRef = this.messageSecurityContext.getIncomingProperties().get(SecurityConstants.REMOVE_ALL_WSU_ID_REF);
				if(removeAllIdRef!=null){
					removeAllIdRefValue = Boolean.parseBoolean((String)removeAllIdRef);
				}
				
				if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
					receiverInterface.cleanDirtyElements(this.messageSecurityContext, message.castAsSoap(), elementsToClean, detachValue, removeAllIdRefValue);
				}
				else {
					if(detachValue) {
						receiverInterface.detachSecurity(this.messageSecurityContext, message.castAsRest());
					}
				}

			} catch (SecurityException e) {
				this.messageSecurityContext.getLog().error("Errore durante il clean del messaggio: " + e.getMessage(), e);
				throw new Exception("Errore durante la cleanMessage: " + e.getMessage());
			}
			
			
			
		} catch (Exception e) {
			
			String prefix = "Generatosi errore durante il processamento Message-Security(Receiver): ";
			
			this.messageSecurityContext.getLog().error(prefix+e.getMessage(),e);
			
			this.msgErrore =  prefix+"Generatosi errore durante il processamento Message-Security(Receiver): "+e.getMessage();
			this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
			
			if(e instanceof SecurityException){
				SecurityException securityException = (SecurityException) e;
				if(securityException.getMsgErrore()!=null){
					this.msgErrore = prefix+securityException.getMsgErrore();
				}
				if(securityException.getCodiceErrore()!=null){
					this.codiceErrore = securityException.getCodiceErrore();
				}
			} 
			
			return false;
		}
		return true;
	}

	
}
