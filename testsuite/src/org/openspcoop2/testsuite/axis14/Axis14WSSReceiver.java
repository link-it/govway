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


package org.openspcoop2.testsuite.axis14;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;


import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.ws.axis.security.WSDoAllReceiver;
import org.apache.ws.security.SOAP11Constants;
import org.apache.ws.security.SOAPConstants;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.apache.ws.security.util.WSSecurityUtil;

import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.utils.resources.Loader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Classe per la gestione della WS-Security (WSDoAllReceiver).
 *
 * @author Spadafora Marcello <Ma.Spadafora@finsiel.it>
 * @author Montebove Luciano <L.Montebove@finsiel.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Axis14WSSReceiver {

	/** Eventuale messaggio di errore avvenuto durante il processo di validazione */
	private String msgErrore;
	/** Eventuale codice di errore avvenuto durante il processo di validazione  */
	private CodiceErroreCooperazione codiceErrore;
	/** Contiene il message Context */
	private Axis14WSSBaseUtils baseUtilsWSS;
	/** Certificato del client */
	private String subject;

	public Axis14WSSReceiver(Hashtable<?,?> wssProperties, Axis14WSSBaseUtils baseWSS) {
		this.baseUtilsWSS = baseWSS;
		this.baseUtilsWSS.setMessageContext(wssProperties);
	}


	public boolean process(Message axisMessage,Busta busta) {
		try{
//			java.text.SimpleDateFormat dt;
			//dt = new java.text.SimpleDateFormat("yy-MM-dd HH-mm-ss SSS");
			if (this.baseUtilsWSS.getMessageContext() != null) {
				this.baseUtilsWSS.getMessageContext().setCurrentMessage(axisMessage);
				// Authentication class from propertiesWSS
				String authClass = (String)this.baseUtilsWSS.getMessageContext().getProperty("authorizationClass");
				// Actor from propertiesWSS
				String actor = (String)this.baseUtilsWSS.getMessageContext().getProperty(WSHandlerConstants.ACTOR);
				// MustUnderstand from propertiesWSS
				String mustUnderstand = (String)this.baseUtilsWSS.getMessageContext().getProperty(WSHandlerConstants.MUST_UNDERSTAND);
				boolean mustUnderstandValue = Boolean.parseBoolean(mustUnderstand);
				//System.out.println("Letto proprieta actor["+actor+"] mustUnderstand["+mustUnderstand+"]");

				String actionOrderCheck= (String)this.baseUtilsWSS.getMessageContext().getProperty("actionOrderCheck");
				WSDoAllReceiver recever = null;
				if (actionOrderCheck!=null && actionOrderCheck.equalsIgnoreCase("true") ){    
					// Implementazione 'normale' di WSS
					recever = new WSDoAllReceiver();
				}else{
					//	Gestione messaggio (caso speciale: mustUnderstand=true e actor non definito)
					recever = new Axis14WSDoAllReceiverNoActionOrderCheck();
				}
				// Impostazione actor
				if (actor != null)
					recever.setOption(WSHandlerConstants.ACTOR, actor);

				// Recupero elementi che dovranno poi essere puliti
				List<WSSReference> elementsToClean = getWSSDirtyElements(axisMessage, actor, mustUnderstandValue);
				
				// Invocazione engine di WSSecurity
				recever.invoke(this.baseUtilsWSS.getMessageContext());

				// Lettura subject certificato
				try{
					this.subject=getPrincipal(actor);
				}catch(Exception e){}

				// Autorizzzazione WSS
				if (authClass != null) {
					try {
						Axis14Authorization auth = (Axis14Authorization)Loader.getInstance().newInstance(authClass);
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
						this.baseUtilsWSS.getLog().error("Errore di Processamento durante l'autorizzazione: " + e.getMessage(),e);
						throw new AxisFault("Errore di Processamento durante l'autorizzazione: " + e.getMessage());
					}
				}

				// Pulizia header WSSecurity
				//SOAPEnvelope envelope = axisMessage.getSOAPEnvelope();
				//System.out.println("Invocazione clean actor["+actor+"] mustUnderstand["+mustUnderstandValue+"]");
				// L'actor = "" serve a forzare il comportamento di interpretare l'header WSS senza actor, 
				// anche con mustUnderstand = true
				String actorDaPulire = actor;
				if("".equals(actor))
					actorDaPulire = null;
				
				// DEPRECATO il clien sottostante: non ripuliva gli elementi interni al body
				//clean(envelope,actorDaPulire,mustUnderstandValue);
				try{
					cleanWSSDirtyElements(axisMessage, actorDaPulire, mustUnderstandValue, elementsToClean);
				} catch (SOAPException e) {
					throw new Exception("Errore durante la cleanWSSDirtyElements: " + e.getMessage());
				}

				//axisMessage.saveChanges();
			}
		}catch (AxisFault af) {
			//af.printStackTrace(System.out);
			this.msgErrore =  "Generatosi errore durante il processamento WS-Security(Receiver) [code: "
				+af.getFaultCode()+"]\n"+af.getFaultString();
			if(af.getFaultString()!=null && af.getFaultString().contains("The signature or decryption was invalid")){
				this.codiceErrore = CodiceErroreCooperazione.SICUREZZA_FIRMA_INTESTAZIONE_NON_PRESENTE;
			}else{
				this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
			}
			this.baseUtilsWSS.getLog().error(this.msgErrore,af);
			return false;
		} catch (Exception e) {
			//e.printStackTrace(System.out);
			this.msgErrore =  "Generatosi errore durante il processamento WS-Security(Receiver): "+e.getMessage();
			this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
			this.baseUtilsWSS.getLog().error(this.msgErrore,e);
			return false;
		}

		return true;
	}

	/**
	 * In caso di avvenuto errore durante il processo di validazione, 
	 * questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto).
	 * 
	 */
	public String getMsgErrore(){
		return this.msgErrore;
	}

	/**
	 * In caso di avvenuto errore, questo metodo ritorna il codice dell'errore.
	 *
	 * @return codice dell'errore (se avvenuto).
	 * 
	 */
	public CodiceErroreCooperazione getCodiceErrore(){
		return this.codiceErrore;
	}
	
	public String getActor() {
		return this.baseUtilsWSS.getActor();
	}
    
	@Deprecated
	public void clean(SOAPEnvelope envelope, String actor, boolean mustUnderstand) throws AxisFault {
		try {
			//elimina header WSS per specifico attore se indicato od in generale
			SOAPHeader header = (SOAPHeader)envelope.getHeader();
			java.util.Iterator<?> it = header.getChildElements();
			SOAPHeaderElement elementWSS = null;
			int countWSSHeader = 0;
			while(it.hasNext()){
				SOAPHeaderElement elementInVerifica = 
					(SOAPHeaderElement) it.next();
				//System.out.println("Elemento "+elementInVerifica.getLocalName()+" con actor ["+elementInVerifica.getActor()+"] e mustUnderstand ["+elementInVerifica.getMustUnderstand()+"]");
				if("Security".equals(elementInVerifica.getLocalName()) && 
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd".equals(elementInVerifica.getNamespaceURI())){

					// trovato header WSS
					countWSSHeader++;

					// check header attualmente in gestione in OpenSPCoop
					if(actor==null){
						if(elementInVerifica.getActor()==null && mustUnderstand==elementInVerifica.getMustUnderstand()){
							elementWSS = elementInVerifica;
							//break;
						}
					}else{
						if(actor.equals(elementInVerifica.getActor()) && mustUnderstand==elementInVerifica.getMustUnderstand()){
							elementWSS = elementInVerifica;
							//break;
						}
					}
				}
			}

			// Rimozione header WSS gestito
			if(elementWSS==null){
				throw new AxisFault("cleanWSS: header WSS gestito non trovato.");
			}else{
				header.removeChild(elementWSS);
			}

			// Pulizia body (se non esistono altri header WSS)
			if(countWSSHeader==1){
				SOAPBody body = (SOAPBody)envelope.getBody();
				body.removeAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", 
				"Id");
				body.removeNamespaceDeclaration("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
			}

		}catch (Exception e) {
			e.printStackTrace();
			throw new AxisFault("Errore di Processamento: " + e.getMessage());
		}

	}


	public String getPrincipal(String actor) {
		MessageContext msgContext = this.baseUtilsWSS.getMessageContext();
		String principal = null;
		Vector<?> results = 
			(Vector<?>)msgContext.getProperty(WSHandlerConstants.RECV_RESULTS);
		//System.out.println("Potential number of usernames: " + results.size());
		for (int i = 0; results != null && i < results.size(); i++) {
			WSHandlerResult hResult = (WSHandlerResult)results.get(i);
			if (actor != null) {
				//prendo solo i risultati dell'actor in config.xml
				if (hResult.getActor().compareTo(actor) == 0) {
					Vector<?> hResults = hResult.getResults();
					for (int j = 0; j < hResults.size(); j++) {
						WSSecurityEngineResult eResult = 
							(WSSecurityEngineResult)hResults.get(j);
						// An encryption or timestamp action does not have an associated principal,
						// only Signature and UsernameToken actions return a principal
						int actionGet = ((java.lang.Integer)  eResult.get(WSSecurityEngineResult.TAG_ACTION)).intValue();
						if ((actionGet == WSConstants.SIGN) || 
								(actionGet == WSConstants.UT)) {
							principal =  ((Principal) eResult.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();
							// Signature and UsernameToken actions return a principal
							//System.out.println("Principal's name: " + principal);
						}
					}
				}
			} else {
				Vector<?> hResults = hResult.getResults();
				for (int j = 0; j < hResults.size(); j++) {
					WSSecurityEngineResult eResult = 
						(WSSecurityEngineResult)hResults.get(j);
					// An encryption or timestamp action does not have an associated principal,
					// only Signature and UsernameToken actions return a principal
					int actionGet = ((java.lang.Integer)  eResult.get(WSSecurityEngineResult.TAG_ACTION)).intValue();
					if ((actionGet == WSConstants.SIGN) || 
							(actionGet == WSConstants.UT)) {
						principal =  ((Principal) eResult.get(WSSecurityEngineResult.TAG_PRINCIPAL)).getName();
						// Signature and UsernameToken actions return a principal
						//System.out.println("Principal's name: " + principal);
					}
				}
			}
		}
		return principal;
	}


	public String getSubject() {
		return this.subject;
	}

	
	/* **** Pulizia messaggio **** */
	
	public List<WSSReference> getWSSDirtyElements(Message message, String actor, boolean mustUnderstand) throws SOAPException {
		List<WSSReference> references = new ArrayList<WSSReference>();
		
		SOAPPart soapPart = message.getSOAPPart();
		
		// Prendo il security Header di mia competenza
        SOAPElement security = (SOAPElement) getSecurityHeader(soapPart, actor, new SOAP11Constants());
       
        //TODO verificare se actor==null && mustUnderstand==false?
        
        // Prendo i riferimenti agli elementi cifrati
        saveWSSDirtyEncryptElements(security, soapPart, references);
        
        // Prendo i riferimenti agli elementi firmati
        saveWSSDirtySignatureElements(security, soapPart, references);
        
        return references;
	}
	
	public void saveWSSDirtyEncryptElements(SOAPElement security, SOAPPart soapPart, List<WSSReference> references) throws SOAPException {
		 // Prendo i riferimenti agli elementi cifrati
        Iterator<?> it = security.getChildElements(new PrefixedQName(new QName(WSConstants.ENC_NS, WSConstants.ENC_KEY_LN)));
        if(it.hasNext()){ 
        	SOAPElement encryptedKey = (SOAPElement) it.next();
        	SOAPElement referenceList = (SOAPElement) encryptedKey.getChildElements(new PrefixedQName(new QName(WSConstants.ENC_NS, WSConstants.REF_LIST_LN))).next();
        	Iterator<?> referenceListIterator = referenceList.getChildElements();
        	while (referenceListIterator.hasNext()) {
        		String referenceWithSharp = ((SOAPElement) referenceListIterator.next()).getAttributeValue(new PrefixedQName(new QName("URI")));
        		// Il riferimento presenta un # prima dell'identificativo
        		String reference = referenceWithSharp.substring(1);
        		// Vado a vedere se ' cifrato {Content} o {Element}
        		SOAPElement encryptedElement = (SOAPElement) WSSecurityUtil.findElementById(soapPart.getEnvelope(), reference, null);
        		if(encryptedElement==null){
        			throw new SOAPException("Multiple elements with the same 'Id' attribute value ("+referenceWithSharp+") [EncryptSearch]?");
        		}
        		if(encryptedElement.getAttributeNS(null, "Type").equals(WSConstants.ENC_NS + "Content"))
        			references.add(new WSSReference (encryptedElement.getParentElement(), WSSReference.TYPE_ENCRYPT_CONTENT, reference));
        		else
        			references.add(new WSSReference (encryptedElement.getParentElement(), WSSReference.TYPE_ENCRYPT_ELEMENT, reference));
        	}
        }
	}
	
	public void saveWSSDirtySignatureElements(SOAPElement security, SOAPPart soapPart, List<WSSReference> references) throws SOAPException {
        // Prendo i riferimenti agli elementi firmati
		Iterator<?> it = security.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, WSConstants.SIG_LN)));
        if(it.hasNext()){ 
        	SOAPElement signature = (SOAPElement) it.next();
        	SOAPElement signatureInfo = (SOAPElement) signature.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, "SignedInfo"))).next();
        	Iterator<?> referenceIterator = signatureInfo.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, "Reference")));
        	while (referenceIterator.hasNext()) {
        		String referenceWithSharp = ((SOAPElement) referenceIterator.next()).getAttributeValue(new PrefixedQName(new QName("URI")));
        		// Il riferimento presenta un # prima dell'identificativo
        		String reference = referenceWithSharp.substring(1);
        		
        		SOAPElement signedElement = (SOAPElement) WSSecurityUtil.findElementById(soapPart.getEnvelope(), reference, WSConstants.WSU_NS);
        		if(signedElement==null){
        			throw new SOAPException("Multiple elements with the same 'Id' attribute value ("+referenceWithSharp+") [SignatureSearch]?");
        		}
       			references.add(new WSSReference (signedElement, WSSReference.TYPE_SIGNATURE, reference));
        	}
        }
	}
	
	// Fix rispetto all'originale metodo di WSSecurityUtil. Con l'implementazione axis14 di Document, tale metodo non funziona.
    public static Element getSecurityHeader(Document doc, String actor, SOAPConstants sc) {
        Element soapHeaderElement = 
            (Element) WSSecurityUtil.getDirectChild(
                doc.getDocumentElement(), 
                sc.getHeaderQName().getLocalPart(), 
                sc.getEnvelopeURI()
            );
        if (soapHeaderElement == null) { // no SOAP header at all
            return null;
        }

        // get all wsse:Security nodes
        NodeList list = 
            soapHeaderElement.getElementsByTagNameNS(WSConstants.WSSE_NS, WSConstants.WSSE_LN);
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.getLength(); i++) {
            Element elem = (Element) list.item(i);
            if(elem instanceof org.apache.axis.message.SOAPHeaderElement){
            	org.apache.axis.message.SOAPHeaderElement h = 
            		(org.apache.axis.message.SOAPHeaderElement) elem;
            	String hActor = h.getActor();
            	if (WSSecurityUtil.isActorEqual(actor, hActor)) {
            		return elem;
            	}
            } 
        }
        return null;
    }
	

	public void cleanWSSDirtyElements(Message message, String actor, boolean mustUnderstand, List<WSSReference> references) throws SOAPException {
		
		SOAPPart soapPart = message.getSOAPPart();
		javax.xml.soap.SOAPHeader soapHeader = message.getSOAPHeader();
		
		// Prendo il security Header di mia competenza
        SOAPElement security = (SOAPElement) getSecurityHeader(soapPart, actor, new SOAP11Constants());
        
        // Aggiungo gli elementi di signature anche dopo averli processati con WSS.
        // poiche' Axis14 ricostruisce il messaggio
        saveWSSDirtySignatureElements(security, soapPart, references);
        
        // Rimuovo l'header Security
        security.detachNode();
        
        boolean found;
        
        // Pulisco i nodi sporchi
		for(int i=0; i<references.size(); i++){
			WSSReference reference = references.get(i);
			SOAPElement elementToClean = reference.getElement();
			switch (reference.getType()) {
			case WSSReference.TYPE_SIGNATURE:
				// Devo vedere se altri hanno firmato l'elemento ed in tal caso lasciar fare l'id ed il namespace
				found = false;
				NodeList securities = soapHeader.getElementsByTagNameNS(WSConstants.WSSE_NS, WSConstants.WSSE_LN);
				for(int s=0; s<securities.getLength(); s++){
					security = (SOAPElement) securities.item(s);
					// Prendo i riferimenti agli elementi firmati
			        Iterator<?>  it = security.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, WSConstants.SIG_LN)));
			        if(it.hasNext()){ 
			        	SOAPElement signature = (SOAPElement) it.next();
			        	SOAPElement signatureInfo = (SOAPElement) signature.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, "SignedInfo"))).next();
			        	Iterator<?> referenceIterator = signatureInfo.getChildElements(new PrefixedQName(new QName(WSConstants.SIG_NS, "Reference")));
			        	while (referenceIterator.hasNext()) {
			        		String referenceWithSharp = ((SOAPElement) referenceIterator.next()).getAttributeValue(new PrefixedQName(new QName("URI")));
			        		if(reference.getReference().equals(referenceWithSharp.substring(1))) {
			        			found = true;
			        		}
			        	}
			        }
				}
				if(!found) {
					elementToClean.removeAttributeNS(WSConstants.WSU_NS, "Id");
					
					Iterator<?> prefixes = elementToClean.getNamespacePrefixes();
					while(prefixes.hasNext()){
						String prefix = (String) prefixes.next();
						String namespace = elementToClean.getNamespaceURI(prefix);
						//System.out.println("OLD SIGNATURE ["+prefix+"] ["+namespace+"]");
						if(namespace.equals(WSConstants.WSU_NS)) {
							elementToClean.removeNamespaceDeclaration(prefix);
						}
					}
					
					prefixes = elementToClean.getAllAttributes();
					while(prefixes.hasNext()){
						Object o = prefixes.next();
						org.apache.axis.message.PrefixedQName attr = 
							(org.apache.axis.message.PrefixedQName) o;
						String prefix = attr.getPrefix();
						String namespace = elementToClean.getNamespaceURI(prefix);
						//System.out.println("NUOVO SIGNATURE ["+prefix+"] ["+namespace+"]");
						if(namespace.equals(WSConstants.WSU_NS)) {
							elementToClean.removeNamespaceDeclaration(prefix);
						}
					}
				}
				break;

			case WSSReference.TYPE_ENCRYPT_CONTENT:
				found = false;
				Iterator<?> childrenToClean =  elementToClean.getChildElements();
				while(childrenToClean.hasNext() && !found) {
					Object next = childrenToClean.next();
					if(next instanceof SOAPElement) {
						SOAPElement childToClean = (SOAPElement) next;
						List<String> prefixesToRemove = new ArrayList<String>();
						
						Iterator<?> prefixes = childToClean.getNamespacePrefixes();
						while(prefixes.hasNext()){
							String prefix = (String) prefixes.next();
							String namespace = childToClean.getNamespaceURI(prefix);
							//System.out.println("OLD ENCRYPT ["+prefix+"] ["+namespace+"]");
							if(namespace.equals(WSConstants.ENC_NS) ||  namespace.equals(elementToClean.getNamespaceURI(prefix))){
								prefixesToRemove.add(prefix);
							}
						}
						
						prefixes = elementToClean.getAllAttributes();
						while(prefixes.hasNext()){
							Object o = prefixes.next();
							org.apache.axis.message.PrefixedQName attr = 
								(org.apache.axis.message.PrefixedQName) o;
							String prefix = attr.getPrefix();
							String namespace = childToClean.getNamespaceURI(prefix);
							//System.out.println("NUOVO ENCRYPT ["+prefix+"] ["+namespace+"]");
							if(namespace.equals(WSConstants.ENC_NS) ||  namespace.equals(elementToClean.getNamespaceURI(prefix))){
								prefixesToRemove.add(prefix);
							}
						}
						
						for(int y=0; y<prefixesToRemove.size(); y++){
							childToClean.removeNamespaceDeclaration(prefixesToRemove.get(y));
						}
						
						found = true;
					}
				}
				break;
				
			case WSSReference.TYPE_ENCRYPT_ELEMENT:
				// Non ci sono elementi sporchi in questo caso.
				break;
				
			default:
				break;
			}
			
		}
	}
	
	private class WSSReference {
		private String reference;
		private int type;
		private SOAPElement element;
		
		public static final int TYPE_ENCRYPT_CONTENT = 1;
		public static final int TYPE_ENCRYPT_ELEMENT = 2;
		public static final int TYPE_SIGNATURE = 3;
		
		public WSSReference(SOAPElement element, int type, String reference) {
			this.element = element;
			this.reference = reference;
			this.type = type;
		}
		
		public String getReference(){
			return this.reference;
		}
		
		public int getType(){
			return this.type;
		}
		
		public SOAPElement getElement(){
			return this.element;
		}
	}
}
