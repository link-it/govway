/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.sdk.builder;

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;



/**
 * Classe utilizzata per raccogliere le informazioni sul tipo di errore applicativo desiderato.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class ProprietaErroreApplicativo implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione se il msg di Errore Applicativo deve essere costruito come un SOAPFault(false)
	 *                    o come un msg di errore applicativo (true). */
	private boolean faultAsXML;
	/** Eventuale FaultActor. */
	private String faultActor;
	/** Dominio del soggetto che ha effettuato la richiesta */
	private String dominio;
	/** Identificativo del Modulo che genera il msg di errore Applicativo */
	private String idModulo;
	/** tipo di fault ritornato dall'applicazione: generic code o specifico codice di errore */
	private boolean faultAsGenericCode;
	/** tipo di fault ritornato dall'applicazione: prefix code */
	private String faultPrefixCode;
	/** Insert dell'errore applicativo come details */
	private boolean insertAsDetails;
	/** Descrizione se il details di OpenSPCoop deve possedere informazioni generiche o specifiche */
	private Boolean informazioniGenericheDetailsOpenSPCoop;
	/** Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale */
	private boolean aggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	/** Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale */
	private boolean aggiungiDetailErroreApplicativo_SoapFaultPdD;




	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public ProprietaErroreApplicativo(){
		// Costruttore di default.
	}






	/**
	 * Restituisce il fault code che segue determinate caratteristiche di prefix e code.
	 * !!Nota: da eseguire prima di un transformFaultCode!!
	 *
	 * @param errore Errore Integrazione
	 * @param protocolFactory Protocol Factory
	 * @return Restituisce il msg che segue determinate caratteristiche di generalita'.
	 * @throws ProtocolException 
	 * 
	 */
	public String transformFaultMsg(ErroreIntegrazione errore,IProtocolFactory protocolFactory) throws ProtocolException{

		CodiceErroreIntegrazione code = errore.getCodiceErrore();
		if(this.faultAsGenericCode){
			if( code.getCodice() >= 450 && 
					code.getCodice() != CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE.getCodice() &&
					code.getCodice() != CodiceErroreIntegrazione.CODICE_517_RISPOSTA_RICHIESTA_NON_RITORNATA.getCodice() && 
					code.getCodice() != CodiceErroreIntegrazione.CODICE_518_RISPOSTA_RICHIESTA_RITORNATA_COME_FAULT.getCodice() && 
					code.getCodice() != CodiceErroreIntegrazione.CODICE_559_RICEVUTA_RISPOSTA_CON_ERRORE_TRASPORTO.getCodice() && 
					code.getCodice() != CodiceErroreIntegrazione.CODICE_543_HANDLER_OUT_REQUEST.getCodice() &&  
					code.getCodice() != CodiceErroreIntegrazione.CODICE_544_HANDLER_IN_RESPONSE.getCodice() &&  
					code.getCodice() != CodiceErroreIntegrazione.CODICE_558_HANDLER_IN_PROTOCOL_REQUEST.getCodice()  
					) {
				return CostantiProtocollo.SISTEMA_NON_DISPONIBILE;
			}
		}

		return new String(errore.getDescrizione(protocolFactory));

	}






	/* ********  S E T T E R   ******** */
	/**
	 * Indicazione se il msg di Errore Applicativo deve essere costruito come un SOAPFault(false)
	 *                    o come un msg di errore applicativo (true). 
	 *
	 * @param faultAsXML Indicazione se il msg di Errore Applicativo deve essere costruito come un SOAPFault(false)
	 *                    o come un msg di errore applicativo (true). 
	 * 
	 */
	public void setFaultAsXML(boolean faultAsXML) {
		this.faultAsXML = faultAsXML;
	}
	/**
	 * Eventuale FaultActor
	 *
	 * @param faultActor Eventuale FaultActor
	 * 
	 */
	public void setFaultActor(String faultActor) {
		this.faultActor = faultActor;
	}
	/**
	 * Dominio del soggetto che ha effettuato la richiesta
	 *
	 * @param dominio Dominio del soggetto che ha effettuato la richiesta
	 * 
	 */
	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	/**
	 * Identificativo del Modulo che genera il msg di errore Applicativo
	 *
	 * @param idModulo Identificativo del Modulo che genera il msg di errore Applicativo
	 * 
	 */
	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
	/**
	 * Tipo di fault ritornato dall'applicazione: codice di errore generico (true) o specifico (false)
	 * @param faultAsGenericCode tipo di codice ritornato
	 */
	public void setFaultAsGenericCode(boolean faultAsGenericCode) {
		this.faultAsGenericCode = faultAsGenericCode;
	}
	/**
	 * Tipo di fault ritornato dall'applicazione: prefisso del FaultCode
	 * @param faultPrefixCode prefisso del FaultCode
	 */
	public void setFaultPrefixCode(String faultPrefixCode) {
		this.faultPrefixCode = faultPrefixCode;
	}

	public void setInformazioniGenericheDetailsOpenSPCoop(
			Boolean informazioniGenericheDetailsOpenSPCoop) {
		this.informazioniGenericheDetailsOpenSPCoop = informazioniGenericheDetailsOpenSPCoop;
	}

	public void setAggiungiDetailErroreApplicativo_SoapFaultApplicativo(
			boolean aggiungiDetailErroreApplicativo_SoapFaultApplicativo) {
		this.aggiungiDetailErroreApplicativo_SoapFaultApplicativo = aggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}

	public void setAggiungiDetailErroreApplicativo_SoapFaultPdD(boolean aggiungiDetailErroreApplicativo_SoapFaultPdD) {
		this.aggiungiDetailErroreApplicativo_SoapFaultPdD = aggiungiDetailErroreApplicativo_SoapFaultPdD;
	}




	/* ********  G E T T E R   ******** */
	/**
	 * Indicazione se il msg di Errore Applicativo deve essere costruito come un SOAPFault(false)
	 *                    o come un msg di errore applicativo (true). 
	 *
	 * @return Indicazione se il msg di Errore Applicativo deve essere costruito come un SOAPFault(false)
	 *                    o come un msg di errore applicativo (true). 
	 * 
	 */
	public boolean isFaultAsXML() {
		return this.faultAsXML;
	}
	/**
	 * Eventuale FaultActor
	 *
	 * @return Eventuale FaultActor
	 * 
	 */
	public String getFaultActor() {
		return this.faultActor;
	}
	/**
	 * Dominio del soggetto che ha effettuato la richiesta
	 *
	 * @return dominio Dominio del soggetto che ha effettuato la richiesta
	 * 
	 */
	public String getDominio() {
		return this.dominio;
	}
	/**
	 * Identificativo del Modulo che genera il msg di errore Applicativo
	 *
	 * @return Identificativo del Modulo che genera il msg di errore Applicativo
	 * 
	 */
	public String getIdModulo() {
		return this.idModulo;
	}
	/**
	 * Tipo di fault ritornato dall'applicazione: prefisso del FaultCode
	 * @return prefisso del FaultCode
	 */
	public String getFaultPrefixCode() {
		return this.faultPrefixCode;
	}
	/**
	 * Tipo di fault ritornato dall'applicazione: codice di errore generico (true) o specifico (false)
	 * @return tipo di codice ritornato
	 */
	public boolean isFaultAsGenericCode() {
		return this.faultAsGenericCode;
	}

	public boolean isInsertAsDetails() {
		return this.insertAsDetails;
	}

	public void setInsertAsDetails(boolean insertAsDetails) {
		this.insertAsDetails = insertAsDetails;
	}

	public boolean isInformazioniGenericheDetailsOpenSPCoop() {
		if(this.informazioniGenericheDetailsOpenSPCoop!=null)
			return this.informazioniGenericheDetailsOpenSPCoop;
		else
			return this.faultAsGenericCode;
	}

	public boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo() {
		return this.aggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}

	public boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD() {
		return this.aggiungiDetailErroreApplicativo_SoapFaultPdD;
	}



}
