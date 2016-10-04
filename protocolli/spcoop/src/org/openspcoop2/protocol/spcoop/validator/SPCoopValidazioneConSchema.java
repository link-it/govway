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



package org.openspcoop2.protocol.spcoop.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.xml.sax.SAXException;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema}
 * Utilizzata per effettuare una validazione con schema xsd di SPCoop. 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopValidazioneConSchema implements IValidazioneConSchema {

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Validatore della busta SPCoop */
	private static ValidatoreXSD validatoreBustaSPCoop = null;

	/** Errori di validazione riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriProcessamento;

	private IProtocolFactory protocolFactory;
	private AbstractXMLUtils xmlUtils;
	
	
	
	/**
	 * Costruttore.
	 *
	 * @param protocolFactory ProtocolFactory
	 * @throws ProtocolException 
	 * 
	 */
	public SPCoopValidazioneConSchema(IProtocolFactory protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		if(protocolFactory.getLogger()!=null)
			this.log = protocolFactory.getLogger();
		else
			this.log = LoggerWrapperFactory.getLogger("ValidazioneConSchemaSPCoop");
		
		this.xmlUtils = XMLUtils.getInstance();
	}

	@Override
	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta SPCoop.   
	 *
	 * @return Eccezioni riscontrate nella busta SPCoop.
	 * 
	 */
	@Override
	public java.util.Vector<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta SPCoop.   
	 *
	 * @return Eccezioni riscontrate nella busta SPCoop.
	 * 
	 */
	@Override
	public java.util.Vector<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}

	/**
	 * Metodo che si occupa di inizializzare lo schema della busta SPCoop utilizzato per la validazione.
	 *
	 * 
	 */
	public static synchronized boolean initializeSchema(Logger log){
		if(SPCoopValidazioneConSchema.validatoreBustaSPCoop!=null)
			return true; // schema gia' inizializzato.

		if(log==null)
			log = LoggerWrapperFactory.getLogger("ValidazioneConSchemaSPCoop");
		
		try{
			SPCoopProperties spcoopProperties = SPCoopProperties.getInstance(log);
			
			log.info("Inizializzazione dello schema per il protocollo spcoop (possono essere necessari alcuni minuti...)");
			String[] schemiXSDDaImportare = spcoopProperties.getSchemiXSDImportatiValidazioneXSDBusta();
			String schemaXSD = spcoopProperties.getSchemaXSDValidazioneXSDBusta();
			SPCoopValidazioneConSchema.validatoreBustaSPCoop = 	SPCoopValidazioneConSchema.createSchemaValidator(log,schemiXSDDaImportare, schemaXSD);
			log.info("Inizializzazione dello schema per il protocollo spcoop terminata.");
			return true;
		}catch (Exception e) {
			log.error("Riscontrato errore durante l'inizializzazione dello schema per il protocollo spcoop: "+e.getMessage(),e);
			SPCoopValidazioneConSchema.validatoreBustaSPCoop = null;
			return false;
		}
	}

	private static ValidatoreXSD createSchemaValidator(Logger log,String[] schemiXSDDaImportare,String schemaXSD) throws Exception{
		// ** Schemi importati **
		XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
		if(schemiXSDDaImportare!=null){
			for(int i=0;i<schemiXSDDaImportare.length;i++){
				File fXsd = new File(schemiXSDDaImportare[i]);
				InputStream is = null;
				try{
					if(fXsd.exists()){
						is = new FileInputStream(fXsd);
					}else{
						is = SPCoopValidazioneConSchema.class.getResourceAsStream("/"+schemiXSDDaImportare[i]);
					}
					xsdResourceResolver.addResource(fXsd.getName(), is);
				}finally{
					try{
						if(is!=null){
							is.close();
						}
					}catch(Exception e){} //?????? Close effettuato dentro xsdResolver ??? : riabilitato il close.
				}
			}
		}
		
		// ** Schema principale **
		File fXsd = new File(schemaXSD);
		InputStream is = null;
		try{
			if(fXsd.exists()){
				is = new FileInputStream(fXsd);
			}else{
				is = SPCoopValidazioneConSchema.class.getResourceAsStream("/"+schemaXSD);
			}
			// ** Creo Validatore **
			return new ValidatoreXSD(log,xsdResourceResolver,is);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception e){} //?????? Close effettuato dentro xsdResolver ???? : riabilitato il close.
		}
		
	}
	
	/**
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * 
	 */
	@Override
	public void valida(OpenSPCoop2Message message, SOAPElement header, SOAPBody soapBody, boolean isSPCoopErroreProcessamento, boolean isSPCoopErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException{

		this.erroriValidazione = new java.util.Vector<Eccezione>();
		this.erroriProcessamento = new java.util.Vector<Eccezione>();

		if(SPCoopValidazioneConSchema.validatoreBustaSPCoop == null){
			throw new ProtocolException("Validatore con schema XSD non inizializzato");
		}	  
		
		// Validazione eGov
		try {

			if(isSPCoopErroreProcessamento==false && isSPCoopErroreIntestazione==false){
				
				// VALIDAZIONE
				SPCoopValidazioneConSchema.validatoreBustaSPCoop.valida(
						this.xmlUtils.newDocument((OpenSPCoop2MessageFactory.getMessageFactory()).createMessage(SOAPVersion.SOAP11).getAsByte(header,false)));
				
			}
			else{
				
				// Validazione Lista Eccezioni.
				Iterator<?> itChilds = header.getChildElements();
				SOAPElement listaEccezioni = null;
				while(itChilds.hasNext()){
					Object element = itChilds.next();
					if(!(element instanceof SOAPElement)){
						continue;
					}
					
					SOAPElement elemInterno = (SOAPElement) element;
					if("ListaEccezioni".equals(elemInterno.getLocalName())){
						listaEccezioni = elemInterno;
						break;
					}
				}
				if(listaEccezioni==null){
					if(isSPCoopErroreIntestazione)
						throw new SAXException("ListaEccezioni non presente in un messaggio SPCoopErrore");
				}
				
				// VALIDAZIONE
				if(listaEccezioni!=null){
					SPCoopValidazioneConSchema.validatoreBustaSPCoop.valida(this.xmlUtils.newDocument((OpenSPCoop2MessageFactory.getMessageFactory()).createMessage(SOAPVersion.SOAP11).getAsByte(listaEccezioni,false)));
				}
			}
					
		} catch (SAXException e) {
			// instance document is invalid!
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(e.getMessage());
			this.erroriValidazione.add(ecc);
		}catch (Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Validazione con schema xsd dell'header egov non riuscita: errore di processamento");
			this.log.error("Validazione con schema xsd dell'header egov non riuscita non riuscita",e);
			this.erroriProcessamento.add(ecc);
		}

		// Validazione ManifestAttachments
		if(soapBody!=null && isMessaggioConAttachments && validazioneManifestAttachments){
			try {	
				// Validazione
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11);
				SPCoopValidazioneConSchema.validatoreBustaSPCoop.valida(this.xmlUtils.newDocument(msg.getAsByte(msg.getFirstChildElement(soapBody),false)));  
			} catch (SAXException e) {
				// instance document is invalid!
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("ManifestAttachments: "+e.getMessage());
				this.erroriValidazione.add(ecc);
			}catch (Exception e) {
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Validazione con schema xsd del manifest degli attachments non riuscita: errore di processamento");
				this.log.error("Validazione con schema xsd del manifest degli attachments non riuscita",e);
				this.erroriProcessamento.add(ecc);
			}
		}

	}

	@Override
	public boolean initialize() {
		return SPCoopValidazioneConSchema.initializeSchema(this.log);
	}

}
