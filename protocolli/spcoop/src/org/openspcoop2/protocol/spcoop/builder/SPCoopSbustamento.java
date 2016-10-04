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

package org.openspcoop2.protocol.spcoop.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.validator.SPCoopValidazioneSintattica;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Classe utilizzata per Sbustare un SOAPEnvelope dell'header eGov.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SPCoopSbustamento {

	private Logger log;
	private IProtocolFactory protocolFactory;
	private SPCoopValidazioneSintattica validazioneSintattica = null;
	private AbstractXMLUtils xmlUtils = null;
	private SPCoopProperties spcoopProperties = null;
	
	public SPCoopSbustamento(IProtocolFactory protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		this.log = protocolFactory.getLogger();
		this.spcoopProperties = SPCoopProperties.getInstance(this.log);
		
		this.validazioneSintattica = (SPCoopValidazioneSintattica) this.protocolFactory.createValidazioneSintattica();
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
	}
	
	/**
	 * Effettua lo sbustamento eGov
	 *  
	 * @param msg Messaggio in cui deve essere estratto un header eGov.
	 * 
	 */
	public SOAPHeaderElement sbustamentoEGov(OpenSPCoop2Message msg,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	

		SOAPHeader headerSOAP = null;
		SOAPHeaderElement header = null;
		try{

			// Estraggo header
			this.validazioneSintattica.setMsg((SOAPMessage)msg);
			this.validazioneSintattica.setReadQualifiedAttribute(proprietaManifestAttachments.isReadQualifiedAttribute());
			headerSOAP = msg.getSOAPHeader();
			header = this.validazioneSintattica.getHeaderEGov(headerSOAP);
			if(header == null){
			    throw new Exception ("Header eGov non presente");
			}

			msg.removeHeaderElement(headerSOAP, header);
			
			// Remove Manifest
			if(proprietaManifestAttachments.isGestioneManifest() && msg.countAttachments()>0){
				this.remove_eGovManifest(msg,proprietaManifestAttachments);
			}

			return header;
			
		}catch(Exception e){	
			this.log.error("SbustamentoEGov non riuscito: "+e.getMessage(),e);
			throw new ProtocolException("SbustamentoEGov non riuscito: "+e.getMessage(),e);		
		}
		finally{
			// *** GB ***
			if(this.validazioneSintattica!=null){
				this.validazioneSintattica.setHeaderSOAP(null);
			}
			headerSOAP = null;
			header = null;
			// *** GB ***
		}
	}


	 /**
	 * Metodo che si occupa di rimuovere un Manifest per gli Attachments 
	 * definito nella specifica eGov 
	 * 'http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/'.
	 *
	 * @param msg Messaggio su cui creare il manifesto
	 * 
	 */
	public OpenSPCoop2Message remove_eGovManifest(OpenSPCoop2Message msg,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{ 
		try{
			SOAPBody body = msg.getSOAPBody();
			SOAPElement descrizione = (SOAPElement) msg.getFirstChildElement(body);
			java.util.Iterator<?> it = descrizione.getChildElements();
			String idMsg = null;
			while(it.hasNext()){
				Object element = it.next();
				if(!(element instanceof SOAPElement)){
					continue;
				}
				SOAPElement descrizioneMessaggio = (SOAPElement) element;
				SOAPElement riferimento = (SOAPElement)msg.getFirstChildElement(descrizioneMessaggio);
				if(riferimento.getAttribute("role").equalsIgnoreCase(this.spcoopProperties.getRoleRichiestaManifest())){
					idMsg = riferimento.getAttribute("href");
					break;
				}else if(riferimento.getAttribute("role").equalsIgnoreCase(this.spcoopProperties.getRoleRispostaManifest())){
					idMsg = riferimento.getAttribute("href");
					break;
				}		
			}
			if(idMsg==null){
				throw new Exception("DescrizioneMessaggio con ruolo "+this.spcoopProperties.getRoleRichiestaManifest()
						+" o "+this.spcoopProperties.getRoleRispostaManifest()+" non trovato.");
			}
			
			if(idMsg.startsWith("cid:"))
			    idMsg = idMsg.substring("cid:".length());
			MimeHeaders mhs = new MimeHeaders();
			mhs.addHeader("Content-ID", idMsg);
			AttachmentPart ap = (AttachmentPart) msg.getAttachments(mhs).next();
			msg.removeAttachments(mhs);
			

			
			javax.activation.DataHandler dh= ap.getDataHandler();  
			
			// Ripristino body Originale
			msg.getSOAPBody().removeContents();
			
			// Check eventuale <?xml instruction
			java.io.InputStream inputDH = dh.getInputStream();
			byte[]bytePotenzialiXML = new byte[5]; // <?xml
			int readByte = inputDH.read(bytePotenzialiXML);
			boolean xmlContentPresente = false;
			if( (readByte==5) && 
				(((char)bytePotenzialiXML[0])=='<') && 
				(((char)bytePotenzialiXML[1])=='?') &&
				(((char)bytePotenzialiXML[2])=='x') &&
				(((char)bytePotenzialiXML[3])=='m') &&
				(((char)bytePotenzialiXML[4])=='l') ){
				readByte = inputDH.read();
				while( (((char)readByte)!='>') && (readByte!=-1)  ){
					readByte = inputDH.read();
				}
				if(((char)readByte)=='>'){
					xmlContentPresente = true;
				}
			}
			
			// Se il body allegato e' vuoto mi fermo.
			// Parsare uno stream vuoto da errore con AXIOM.
			if(readByte == -1) return msg;
			
			// Costruzione isBody
			InputStream isBody = null;
			if(xmlContentPresente == false){
				isBody = dh.getInputStream();
			}else{
				isBody = inputDH;
			}
			
			// Puo' darsi che siano piu' elementi nello stream senza un unico rootelement.
			// Questo fa arrabbiare il parser.
			// Aggiungo un WrapperElement per farmi costruire l'elemento e poi ne cerco i figli.
			
			Vector<InputStream> iss = new Vector<InputStream>();
			iss.add(new ByteArrayInputStream("<OpenSPCoopWrapper>".getBytes()));
			iss.add(isBody);
			iss.add(new ByteArrayInputStream("</OpenSPCoopWrapper>".getBytes()));
			InputStream is = new SequenceInputStream(iss.elements());
				
			Document doc = this.xmlUtils.newDocument(is);
			NodeList nl = doc.getDocumentElement().getChildNodes();
			for(int i = 0; i < nl.getLength(); i++) {
				org.w3c.dom.Node n = nl.item(i);
				if(n instanceof Element) {
					Element element = (Element) n;
					if(SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_EMPTY_BODY.equals(element.getLocalName()) && 
							SPCoopCostanti.NAMESPACE_MANIFEST_EGOV_EMPTY_BODY.equals(element.getNamespaceURI())){
						// Body Empty
						//System.out.println("SBUSTAMENTO EMPTY PATCH");
						continue;
					}
					msg.getSOAPBody().addChildElement(SoapUtils.getSoapFactory(SOAPVersion.SOAP11).createElement(element));
				}
				if(n instanceof Text) {
					msg.getSOAPBody().addTextNode(n.getTextContent());
				}

			}
			
			return msg;
			
		} catch(Exception e) {
			this.log.error("Rimozione Manifest degli Attachments non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("Rimozione Manifest degli Attachments non riuscita: "+e.getMessage(),e);		
		}   
	}
	
	
	/**
	 * Metodo che si occupa di eliminare dal SOAPHeader, passato col parametro <var>header</var>
	 * l'header eGov.
	 *
	 * @param header bytes del SOAPHeader, contenente la busta eGov da estrarre.
	 * @return bytes del SOAPHeader, in cui e' stato estratto l'header eGov.
	 * 
	 */
	@Deprecated public byte[] removeSPCoop(byte [] header) throws ProtocolException{

		ByteArrayOutputStream reqByteSbustata = null;
		try{
			String headerSTR = new String(header);

			// Ricerco header EGov
			int endIntestazione = 0;
			int start = 0;
			while(true){
				int indexOfEGov = headerSTR.indexOf(SPCoopCostanti.ACTOR_EGOV,start);
				if(indexOfEGov == -1)
					break;
				StringBuffer rovesciata = new StringBuffer();
				for(int i = indexOfEGov-1; i>0 ; i-- ){
					rovesciata.append(((char)header[i]));
					if( ((char)header[i]) == '<' ){
						start = i;
						break;
					}
				}
				StringBuffer rigaCompleta = new StringBuffer();
				rovesciata.reverse();
				rigaCompleta.append(rovesciata.toString());
				rigaCompleta.append(SPCoopCostanti.ACTOR_EGOV);
				for(int i = (indexOfEGov+SPCoopCostanti.ACTOR_EGOV.length()); i<header.length ; i++ ){
					rigaCompleta.append(((char)header[i]));
					if( ((char)header[i]) == '>' ){
						break;
					}
				}
				if(rigaCompleta.toString().indexOf("Intestazione") != -1){
					endIntestazione = start -1 + rigaCompleta.length();
					break;
				}else{
					start = start -1 + rigaCompleta.length();
				}
			}


			// Elimino Header
			if(start == 0){
				return null;
			}
			int end =  headerSTR.indexOf("Intestazione>",endIntestazione) + "Intestazione>".length();
			if(end == -1){
				return null;
			}
			if(end <= start){
				return null;
			}
			reqByteSbustata = new ByteArrayOutputStream();
			for(int i=0; i<start ; i++)
				reqByteSbustata.write(header[i]);
			boolean eraserSpaziDopoEGov = false;
			for(int i=end; i<header.length ; i++){
				if( (eraserSpaziDopoEGov==false) && ((char)header[i]=='<' )  ){
					eraserSpaziDopoEGov=true;
					reqByteSbustata.write(header[i]);
				}else if (eraserSpaziDopoEGov)
					reqByteSbustata.write(header[i]);
			}

			byte[] bustaSenzaEGov = reqByteSbustata.toByteArray();
			reqByteSbustata.close();
			return bustaSenzaEGov;

		} catch(Exception e) {
			try{
				reqByteSbustata.close();
			}catch(Exception eis){}
			this.log.error("RimozioneHeaderSPCoop non riuscita: "+e.getMessage(),e);
			throw new ProtocolException("RimozioneHeaderSPCoop non riuscita: "+e.getMessage(),e);		
		}	
	}

	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	public SOAPHeaderElement sbustamento(OpenSPCoop2Message msg,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		return this.sbustamentoEGov(msg, proprietaManifestAttachments);
	}

}





