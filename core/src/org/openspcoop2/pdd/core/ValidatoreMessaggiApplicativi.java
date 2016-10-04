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




package org.openspcoop2.pdd.core;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.WSDLValidator;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.InformationWsdlSource;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;


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
	RegistroServiziManager registroServiziManager;
	/** Identificativo del Servizio */
	IDServizio idServizio;
	/** SOAPEnvelope */
	SOAPVersion soapVersion;
	SOAPEnvelope envelope;
	SOAPBody body;
	/** WSDL Associato al servizio */
	AccordoServizioWrapper accordoServizioWrapper = null;
	/** Logger */
	Logger logger = null;
	/** XMLUtils */
	org.openspcoop2.message.XMLUtils xmlUtils = null;
	/** WSDLValidator */
	WSDLValidator wsdlValidator = null;
	
	
	
	
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
			SOAPVersion soapVersion,SOAPEnvelope envelope,boolean readWSDLAccordoServizio)throws ValidatoreMessaggiApplicativiException{
		
		if(registro==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("Reader del Registro dei Servizi non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
			throw ex;
		}
		
		if(idServizio==null || idServizio.getSoggettoErogatore()==null || idServizio.getTipoServizio()==null || idServizio.getServizio()==null ||
				idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null){
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException("ID del servizio da validare, non fornito");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
			throw ex;
		}
		
		if(envelope==null){
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("SOAPEnvelope non esistente");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
			throw ex;
		}
		this.soapVersion = soapVersion;
		this.envelope = envelope;
		try{
			this.body = this.envelope.getBody();
		}catch(Exception e){
			this.logger.error("SOAPEnvelope.getBody failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("SOAPEnvelope senza body");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
			throw ex;
		}
		
		if(this.body==null || (this.body.hasChildNodes()==false)){
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException("SOAPBody non esistente");
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
			throw ex;
		}
		this.registroServiziManager = registro;
		this.idServizio = idServizio;
		
		this.logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		
		try{
			if(readWSDLAccordoServizio){
				this.accordoServizioWrapper = this.registroServiziManager.getWsdlAccordoServizio(idServizio,InformationWsdlSource.WSDL,true);
			}else{
				this.accordoServizioWrapper = this.registroServiziManager.getWsdlAccordoServizio(idServizio,InformationWsdlSource.REGISTRY,true);
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
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.getErrore417_CostruzioneValidatoreWSDLFallita(CostantiPdD.WSDL_DEFINITORIO));
				throw ex;
			}else if(e.getMessage()!=null && e.getMessage().startsWith("[WSDL-FRUITORE]")){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.getErrore417_CostruzioneValidatoreWSDLFallita(CostantiPdD.WSDL_FRUITORE));
				throw ex;
			}else if(e.getMessage()!=null && e.getMessage().startsWith("[WSDL-EROGATORE]")){
				ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.getErrore417_CostruzioneValidatoreWSDLFallita(CostantiPdD.WSDL_EROGATORE));
				throw ex;
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_530_COSTRUZIONE_WSDL_FALLITA));
				throw ex;
			}
		}
		
		try{
			this.wsdlValidator = new WSDLValidator(this.soapVersion,this.envelope, this.xmlUtils, this.accordoServizioWrapper, this.logger);
		}catch(Exception e){
			this.logger.error("WSDLValidator initialized failed: "+e.getMessage(),e);
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			ex.setErrore(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_531_VALIDAZIONE_WSDL_FALLITA));
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
			ex.setErrore(ErroriIntegrazione.ERRORE_417_COSTRUZIONE_VALIDATORE_WSDL_FALLITA.getErrore417_CostruzioneValidatoreWSDLFallita(CostantiPdD.WSDL_DEFINITORIO));
			throw ex;
		}catch(Exception e){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
			= new ValidatoreMessaggiApplicativiException(e.getMessage());
			if(isRichiesta){
				ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA.getErrore418_ValidazioneWSDLRichiesta(CostantiPdD.WSDL_DEFINITORIO));
			}else{
				ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA.getErrore419_ValidazioneWSDLRisposta(CostantiPdD.WSDL_DEFINITORIO));
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
	public void validateWithWsdlLogicoImplementativo(boolean isRichiesta,String soapAction) throws ValidatoreMessaggiApplicativiException {
		
		try{
			this.wsdlValidator.wsdlConformanceCheck(isRichiesta, soapAction, this.idServizio.getAzione());
		}catch(Exception e ){ // WSDLValidatorException
			ValidatoreMessaggiApplicativiException ex 
				= new ValidatoreMessaggiApplicativiException(e.getMessage());
			if(isRichiesta){
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA.getErrore418_ValidazioneWSDLRichiesta(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_418_VALIDAZIONE_WSDL_RICHIESTA_FALLITA.getErrore418_ValidazioneWSDLRichiesta(CostantiPdD.WSDL_EROGATORE));
			}else{
				if(TipologiaServizio.CORRELATO.equals(this.accordoServizioWrapper.getTipologiaServizio()))
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA.getErrore419_ValidazioneWSDLRisposta(CostantiPdD.WSDL_FRUITORE));
				else
					ex.setErrore(ErroriIntegrazione.ERRORE_419_VALIDAZIONE_WSDL_RISPOSTA_FALLITA.getErrore419_ValidazioneWSDLRisposta(CostantiPdD.WSDL_EROGATORE));
			}
			throw ex;
		}
		
	}
	
	
}
