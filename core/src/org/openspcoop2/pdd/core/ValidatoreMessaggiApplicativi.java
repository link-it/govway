/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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




package org.openspcoop2.pdd.core;

import javax.xml.soap.SOAPEnvelope;

import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.WSDLValidator;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.slf4j.Logger;
import org.w3c.dom.Element;


/**
 * Classe utilizzata per validare i messaggi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidatoreMessaggiApplicativi {

	/** Registro dei Servizi */
	private RegistroServiziManager registroServiziManager;
	/** Identificativo del Servizio */
	private IDServizio idServizio;
	/** OpenSPCoop2Message */
	private OpenSPCoop2Message message;
	/** SOAPEnvelope */
	private Element element;
	/** WSDL Associato al servizio */
	private AccordoServizioWrapper accordoServizioWrapper = null;
	/** Logger */
	private Logger logger = null;
	/** XMLUtils */
	private org.openspcoop2.message.xml.XMLUtils xmlUtils = null;
	/** WSDLValidator */
	private WSDLValidator wsdlValidator = null;
	
	
	
	
	/* ------ Utility ------- */
	
	public static String getTipo(ValidazioneContenutiApplicativi validazioneContenutoApplicativoApplicativo){
		String tipo = validazioneContenutoApplicativoApplicativo.getTipo().getValue();
		if(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage()!=null){
			if(StatoFunzionalita.ABILITATO.equals(validazioneContenutoApplicativoApplicativo.getAcceptMtomMessage())){
				tipo = tipo + CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR + 
						CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_CON_MTOM;
			}
			if (CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(validazioneContenutoApplicativoApplicativo.getStato())){
				tipo = tipo + CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR + 
						CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_IN_WARNING_MODE;
			}
		}
		return tipo;
	}
	
	public boolean isServizioCorrelato(){
		try{
			return TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio());
		}catch(Exception e){
			return false;
		}
	}
	
	
	
	
	
	/* ------ Costruttore -------------- */
	public ValidatoreMessaggiApplicativi(RegistroServiziManager registro,IDServizio idServizio,
			OpenSPCoop2Message message,boolean readWSDLAccordoServizio, boolean gestioneXsiType_rpcLiteral)throws ValidatoreMessaggiApplicativiException{
		
		if(registro==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Reader del Registro dei Servizi non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
		
		if(idServizio==null || idServizio.getSoggettoErogatore()==null || idServizio.getTipo()==null || idServizio.getNome()==null || idServizio.getVersione()==null ||
				idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("ID del servizio da validare, non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
		
		this.message = message;
		try{
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = this.message.castAsSoap();
				try{
					this.element = soapMessage.getSOAPPart().getEnvelope();
					SOAPEnvelope envelope = (SOAPEnvelope) this.element;
					if(envelope.getBody()==null || (envelope.getBody().hasChildNodes()==false)){
						ValidatoreMessaggiApplicativiException ex 
							= new ValidatoreMessaggiApplicativiException("SOAPBody non esistente");
						ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
						throw ex;
					}
				}
				catch(ValidatoreMessaggiApplicativiException e){
					throw e;
				}
				catch(Exception e){
					this.logger.error("Read failed: "+e.getMessage(),e);
					ValidatoreMessaggiApplicativiException ex 
						= new ValidatoreMessaggiApplicativiException("Lettura SOAPEnvelope fallita");
					ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
					throw ex;
				}
			}
			else{
				if(MessageType.XML.equals(this.message.getMessageType())){
					OpenSPCoop2RestXmlMessage xml = this.message.castAsRestXml();
					this.element = xml.getContent();	
				}
				else{
					throw new Exception("Funzionalità non supportata con ServiceBinding REST e tipologia di messaggio ["+this.message.getMessageType()+"]");
				}
			}
		}
		catch(ValidatoreMessaggiApplicativiException e){
			throw e;
		}
		catch(Exception e){
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
		if(this.element==null){
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("Contenuto da validare non esistente");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}

		this.registroServiziManager = registro;
		this.idServizio = idServizio;
		
		this.logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
		
		try{
			if(readWSDLAccordoServizio){
				this.accordoServizioWrapper = this.registroServiziManager.getWsdlAccordoServizio(idServizio,InformationApiSource.SPECIFIC,true);
			}else{
				this.accordoServizioWrapper = this.registroServiziManager.getWsdlAccordoServizio(idServizio,InformationApiSource.REGISTRY,true);
			}
		}catch(DriverRegistroServiziNotFound e){
			this.logger.error("Riscontrato errore durante la ricerca dei wsdl/xsd che definiscono l'accordo di servizio: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Riscontrato errore durante la ricerca del Wsdl: "+e.getMessage());
			ex.setErrore(ErroriIntegrazione.ERRORE_405_SERVIZIO_NON_TROVATO.getErroreIntegrazione());
			throw ex;
		}catch(DriverRegistroServiziException e){
			this.logger.error("Riscontrato errore durante l'inizializzazione: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Riscontrato errore durante l'inizializzazione: "+e.getMessage());
			if(e.getMessage()!=null && e.getMessage().startsWith("[SchemaXSD]")){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD));
				throw ex;
			}else if(e.getMessage()!=null && e.getMessage().startsWith("[WSDL-FRUITORE]")){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(CostantiPdD.WSDL_FRUITORE));
				throw ex;
			}else if(e.getMessage()!=null && e.getMessage().startsWith("[WSDL-EROGATORE]")){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(CostantiPdD.WSDL_EROGATORE));
				throw ex;
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_530_COSTRUZIONE_INTERFACCIA_FALLITA));
				throw ex;
			}
		}
		
		try{
			this.wsdlValidator = new WSDLValidator(this.message, this.xmlUtils, this.accordoServizioWrapper, this.logger, gestioneXsiType_rpcLiteral);
		}catch(Exception e){
			this.logger.error("WSDLValidator initialized failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_TRAMITE_INTERFACCIA_FALLITA));
			throw ex;
		}
	}
	
	
	
	
	
	
	
	/* ----------- INTERFACCE ---------------- */
	public AccordoServizioWrapper getAccordoServizioWrapper() {
		return this.accordoServizioWrapper;
	}
	
	
	
	
	
	
	
	
	
	/* -------------- VALIDAZIONE XSD --------------------- */
	
	/**
	 * Validazione xsd
	 * 
	 * @param isRichiesta Indicazione sul tipo di messaggio applicativo da gestire
	 * @throws ValidatoreMessaggiApplicativiException (contiene codice e msg di errore)
	 */
	public void validateWithWsdlDefinitorio(boolean isRichiesta) throws ValidatoreMessaggiApplicativiException{
		
		try{
			
			this.wsdlValidator.validateAgainstXSDSchema(isRichiesta, this.idServizio.getAzione());
			
		}catch(org.openspcoop2.utils.wsdl.WSDLException e){
			this.logger.error("WSDL validate failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException(e.getMessage());
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_TRAMITE_INTERFACCIA_FALLITA.getErrore417_CostruzioneValidatoreTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD));
			throw ex;
		}catch(Exception e){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException(e.getMessage());
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.SCHEMA_XSD));
			}
			throw ex;
		}
		
	}

	
	
	
	
	/* -------------- VALIDAZIONE WSDL --------------------- */
	
	/**
	 * Validazione WSDL
	 * 
	 * @param isRichiesta Indicazione sul tipo di messaggio applicativo da gestire
	 * 
	 */
	public void validateWithWsdlLogicoImplementativo(boolean isRichiesta, boolean checkSoapAction) throws ValidatoreMessaggiApplicativiException {
		
		try{
			if(ServiceBinding.SOAP.equals(this.message.getServiceBinding())==false){
				throw new Exception("Tipo di validazione non supportata con Service Binding REST");
			}
			
			this.wsdlValidator.wsdlConformanceCheck(isRichiesta, this.message.castAsSoap().getSoapAction(), this.idServizio.getAzione(),
					checkSoapAction, false);
		}catch(Exception e ){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			if(isRichiesta){
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.WSDL_EROGATORE));
			}else{
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.WSDL_EROGATORE));
			}
			throw ex;
		}
		
	}
	
	public void restoreOriginalDocument(boolean isRichiesta) throws ValidatoreMessaggiApplicativiException {
		
		try{
			this.wsdlValidator.wsdlConformanceCheck_restoreOriginalDocument();
		}catch(Exception e ){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			if(isRichiesta){
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_RICHIESTA_TRAMITE_INTERFACCIA_FALLITA.getErrore418_ValidazioneRichiestaTramiteInterfacciaFallita(CostantiPdD.WSDL_EROGATORE));
			}else{
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_RISPOSTA_TRAMITE_INTERFACCIA_FALLITA.getErrore419_ValidazioneRispostaTramiteInterfacciaFallita(CostantiPdD.WSDL_EROGATORE));
			}
			throw ex;
		}
		
	}
	
	
}
