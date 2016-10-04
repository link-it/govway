/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.reference.Reference;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.IMessageSecurityReceiver;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityUtilities;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.authorization.IMessageSecurityAuthorization;
import org.openspcoop2.security.message.constants.SecurityConstants;



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
			if(message.getSOAPBody().hasFault()){
				
				if(MessageSecurityUtilities.processSOAPFault(this.messageSecurityContext.getIncomingProperties()) == false){
					return true; // non devo applicare la sicurezza.
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
			
			
			
			
			// ** Raccolgo elementi "toccati" da Security per pulirli ** /
			List<Reference> elementsToClean = receiverInterface.getDirtyElements(this.messageSecurityContext, message);
			
			
			
			
			// ** Verifica cifratura/firma elementi richiesti dalla configurazione ** /
			List<SubErrorCodeSecurity> listaErroriRiscontrati = new ArrayList<SubErrorCodeSecurity>();
			Map<QName, QName> notResolved = receiverInterface.checkEncryptSignatureParts(this.messageSecurityContext,elementsToClean, message.countAttachments(),listaErroriRiscontrati);
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
			receiverInterface.checkEncryptionPartElements(notResolved, message, listaErroriInElementi);

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
				if (!authorize(authClass, this.subject , busta)) {
					this.msgErrore =  "Mittente della busta ["+busta.getTipoMittente()+busta.getMittente()+
					"] non autorizzato ad invocare il servizio ["+busta.getServizio()+"] erogato dal soggetto ["+busta.getTipoDestinatario()+busta.getDestinatario()+"]";
					this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA;
					return false;
				}
			}
            if (authClass != null) {
                try {
            		IMessageSecurityAuthorization auth = (IMessageSecurityAuthorization)org.openspcoop2.utils.resources.Loader.getInstance().newInstance(authClass);
                    boolean status = auth.authorize(this.subject,busta);
                    if(!status){
                        if(auth.getMessaggioErrore()!=null){
                                this.msgErrore = auth.getMessaggioErrore();
                        }else{
                                this.msgErrore =  "Mittente della busta ["+busta.getTipoMittente()+busta.getMittente()+
                                        "] (subject:"+this.subject+") non autorizzato ad invocare il servizio ["+busta.getServizio()+"] erogato dal soggetto ["+busta.getTipoDestinatario()+busta.getDestinatario()+"]";
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
				receiverInterface.cleanDirtyElements(this.messageSecurityContext, message, elementsToClean);
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
