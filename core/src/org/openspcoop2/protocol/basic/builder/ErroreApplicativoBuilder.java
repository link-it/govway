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

package org.openspcoop2.protocol.basic.builder;

import java.io.ByteArrayInputStream;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione;
import org.openspcoop2.core.eccezione.errore_applicativo.Dominio;
import org.openspcoop2.core.eccezione.errore_applicativo.DominioSoggetto;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.Servizio;
import org.openspcoop2.core.eccezione.errore_applicativo.Soggetto;
import org.openspcoop2.core.eccezione.errore_applicativo.SoggettoIdentificativo;
import org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoErroreApplicativo;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ErroreApplicativoBuilder
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoBuilder implements org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder {

	protected Logger log;
	protected IProtocolFactory factory;
	protected ITraduttore traduttore;
	protected OpenSPCoop2MessageFactory msgFactory = null;
	protected org.openspcoop2.message.xml.XMLUtils xmlUtils;
	
	public ErroreApplicativoBuilder(IProtocolFactory factory) throws ProtocolException{
		this.log = factory.getLogger();
		this.factory = factory;
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
		this.traduttore = factory.createTraduttore();
		this.msgFactory = OpenSPCoop2MessageFactory.getMessageFactory();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}

	
	// NAMESPACE
	
	@Override
	public String getNamespaceEccezioneProtocollo(){
		return Costanti.ERRORE_PROTOCOLLO_NAMESPACE;
	}

	@Override
	public QName getQNameEccezioneProtocollo(String codice){
		return new QName(this.getNamespaceEccezioneProtocollo(),
				codice, 
				Costanti.ERRORE_PROTOCOLLO_PREFIX);
	}
	
	@Override
	public String getNamespaceEccezioneIntegrazione(){
		return Costanti.ERRORE_INTEGRAZIONE_NAMESPACE;
	}
	
	@Override
	public QName getQNameEccezioneIntegrazione(String codice){
		return new QName(getNamespaceEccezioneIntegrazione(),
				codice, 
				Costanti.ERRORE_INTEGRAZIONE_PREFIX);
	}
	
	
	
	
	
	// UTILITY DI RICONOSCIMENTO
	
	@Override
	public boolean isErroreApplicativo(Node node){
		if(node==null){
			return false;
		}
		return this.isErroreApplicativo(node.getNamespaceURI(), node.getLocalName());
	}

	@Override
	public boolean isErroreApplicativo(String namespace, String localName){
		if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.ROOT_LOCAL_NAME_ERRORE_APPLICATIVO.equals(localName) && 
				org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(namespace) 
			){
			return true;
		}
		return false;
	}
	
	
	
	// BUILDER
	
	@Override
	public OpenSPCoop2Message toMessage(
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return _buildErroreApplicativo_OpenSPCoop2Message(parameters, null);
	}
	@Override
	public OpenSPCoop2Message toMessage(
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return _buildErroreApplicativo_OpenSPCoop2Message(null,parameters);
	}


	@Override
	public SOAPElement toSoapElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException{
		return this._buildErroreApplicativo_SoapElement(parameters, null);
	}
	@Override
	public SOAPElement toSoapElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException{
		return this._buildErroreApplicativo_SoapElement(null,parameters);
	}
	

	@Override
	public Element toElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException{
		return this._buildErroreApplicativo_Element(parameters, null);
	}
	@Override
	public Element toElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException{
		return this._buildErroreApplicativo_Element(null,parameters);
	}
	
	
	@Override
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return this._buildErroreApplicativo_String(tipoErroreApplicativo, parameters, null);
	}
	@Override
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return this._buildErroreApplicativo_String(tipoErroreApplicativo, null,parameters);
	}
	
	
	@Override
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return this._buildErroreApplicativo_ByteArray(tipoErroreApplicativo, parameters, null);
	}
	@Override
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return this._buildErroreApplicativo_ByteArray(tipoErroreApplicativo, null,parameters);
	}


	
	
	
	// PARSER
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(TipoErroreApplicativo tipoErroreApplicativo, String erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		return readErroreApplicativo(tipoErroreApplicativo,erroreApplicativo.getBytes(),prefixCodiceErroreApplicativoIntegrazione);
	}
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(TipoErroreApplicativo tipoErroreApplicativo, byte[] erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		ErroreApplicativo erroreApplicativoObject = null;
		switch (tipoErroreApplicativo) {
		case JSON:
			try{
				erroreApplicativoObject = XMLUtils.getErroreApplicativoFromJson(this.log, new ByteArrayInputStream(erroreApplicativo));
			}catch(Exception e){
				throw new ProtocolException("JSon fornito non contiene un errore applicativo per il protocollo "+this.getProtocolFactory().getProtocol()+": "+e.getMessage(),e);
			}
			break;
		default:
			if(XMLUtils.isErroreApplicativo(erroreApplicativo)==false){
				throw new ProtocolException("XML fornito non contiene un errore applicativo per il protocollo "+this.getProtocolFactory().getProtocol());
			}
			try{
				erroreApplicativoObject = XMLUtils.getErroreApplicativo(this.log, erroreApplicativo);
			}catch(Exception e){
				throw new ProtocolException("Xml fornito non contiene un errore applicativo per il protocollo "+this.getProtocolFactory().getProtocol()+": "+e.getMessage(),e);
			}
			break;
		}
		
		return _parseErroreApplicativo(erroreApplicativoObject, prefixCodiceErroreApplicativoIntegrazione);
		
	}
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(Node erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		if(XMLUtils.isErroreApplicativo(erroreApplicativo)==false){
			throw new ProtocolException("Node fornito non contiene un errore applicativo per il protocollo "+this.getProtocolFactory().getProtocol());
		}
		try{
			byte[] xmlBytes = this.xmlUtils.toByteArray(erroreApplicativo, true);
			return readErroreApplicativo(TipoErroreApplicativo.XML, xmlBytes,prefixCodiceErroreApplicativoIntegrazione);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	
	
	
	
	// INSERT INTO SOAP FAULT
	
	@Override
	public void insertInSOAPFault(
			EccezioneProtocolloBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException {
		ErroreApplicativoMessageUtils.insertErroreApplicativoIntoSOAPFault(
				this.toSoapElement(parameters), msg, this.log);
	}

	@Override
	public void insertInSOAPFault(
			EccezioneIntegrazioneBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException {
		ErroreApplicativoMessageUtils.insertErroreApplicativoIntoSOAPFault(
				this.toSoapElement(parameters), msg, this.log);
	}

	@Override
	public void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg) throws ProtocolException{
		
		ErroreApplicativoMessageUtils.insertRoutingErrorInSOAPFault(identitaRouter, idFunzione, msgErrore, msg, this.log, this.xmlUtils);
		
	}
	
	
	
	
	
	
	/** BUILDER UTILITIES */
	
	protected SOAPElement _buildErroreApplicativo_SoapElement(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		Element elementErroreApplicativo = this._buildErroreApplicativo_Element(eccezioneProtocollo, eccezioneIntegrazione);
		
		try{
		
			MessageType messageType = null;
			if(eccezioneProtocollo!=null){
				messageType = eccezioneProtocollo.getMessageType();
			}
			else{
				messageType = eccezioneIntegrazione.getMessageType();
			}
			
			SOAPFactory sf = SoapUtils.getSoapFactory(messageType);
			SOAPElement erroreApplicativoElementSOAP =  sf.createElement(elementErroreApplicativo);
			
			return erroreApplicativoElementSOAP;
		
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
			
	}
	
	protected Element _buildErroreApplicativo_Element(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
	
		ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(eccezioneProtocollo, eccezioneIntegrazione);
		
		try{
			// il passaggio da XMLUtils forza anche la validazione dell'oggetto
			byte[]xmlErroreApplicativo = org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativo(erroreApplicativo);
			Element elementErroreApplicativo = this.xmlUtils.newElement(xmlErroreApplicativo);
			ErroreApplicativoMessageUtils.addPrefixToElement(elementErroreApplicativo,"op2ErrAppl");
			
			return elementErroreApplicativo;
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
	
	protected String _buildErroreApplicativo_String(TipoErroreApplicativo tipoErroreApplicativo,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
				ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(eccezioneProtocollo, eccezioneIntegrazione);
				return org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo);
			}
			else{
				Element element = this._buildErroreApplicativo_Element(eccezioneProtocollo, eccezioneIntegrazione);
				return this.xmlUtils.toString(element, true);
			}
		
		}catch(Exception e){
			throw new ProtocolException("toString failed: "+e.getMessage());
		}
	}
	
	protected byte[] _buildErroreApplicativo_ByteArray(TipoErroreApplicativo tipoErroreApplicativo,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
				ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(eccezioneProtocollo, eccezioneIntegrazione);
				return org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo).getBytes();
			}
			else{
				Element element = this._buildErroreApplicativo_Element(eccezioneProtocollo, eccezioneIntegrazione);
				return this.xmlUtils.toByteArray(element, true);
			}
		
		}catch(Exception e){
			throw new ProtocolException("toByteArray failed: "+e.getMessage());
		}
	}
	
	private ErroreApplicativo _buildErroreApplicativo_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		try{

			ErroreApplicativo erroreApplicativo = new ErroreApplicativo();
			
			IDSoggetto idDominio = null;
			String idModulo = null;
			TipoPdD tipoPdD = null;
			IDSoggetto fruitore = null;
			IDServizio servizio = null;
			String servizioApplicativo = null;
			String codiceEccezione = null;
			Integer codiceEccezioneOpenSPCoop = null;
			Integer subCodiceEccezioneOpenSPCoop = null;
			String descrizioneEccezione = null;
			TipoEccezione tipoEccezione = null;
			Date oraRegistrazione = null;
			@SuppressWarnings("unused")
			MessageType messageType = null;
			
			if(eccezioneProtocollo!=null){
				idDominio = eccezioneProtocollo.getDominioPorta();
				idModulo = eccezioneProtocollo.getIdFunzione();
				tipoPdD = eccezioneProtocollo.getTipoPorta();
				fruitore = eccezioneProtocollo.getMittente();
				servizio = eccezioneProtocollo.getServizio();
				servizioApplicativo = eccezioneProtocollo.getServizioApplicativo();
				codiceEccezione = this.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
						eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
				codiceEccezioneOpenSPCoop = eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione().getCodice();
				if(eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione()!=null){
					subCodiceEccezioneOpenSPCoop = eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione().getSubCodice();
				}
				descrizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory);
				tipoEccezione = TipoEccezione.ECCEZIONE_PROTOCOLLO;
				oraRegistrazione = eccezioneProtocollo.getOraRegistrazione();
				messageType = eccezioneProtocollo.getMessageType();
			}
			else{
				idDominio = eccezioneIntegrazione.getDominioPorta();
				idModulo = eccezioneIntegrazione.getIdFunzione();
				tipoPdD = eccezioneIntegrazione.getTipoPorta();
				fruitore = eccezioneIntegrazione.getMittente();
				servizio = eccezioneIntegrazione.getServizio();
				servizioApplicativo = eccezioneIntegrazione.getServizioApplicativo();
				codiceEccezione = this.traduttore.toString(eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore(),
						eccezioneIntegrazione.getProprieta().getFaultPrefixCode(),eccezioneIntegrazione.getProprieta().isFaultAsGenericCode());
				codiceEccezioneOpenSPCoop = eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore().getCodice();
				descrizioneEccezione = eccezioneIntegrazione.getProprieta().transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.factory);
				tipoEccezione = TipoEccezione.ECCEZIONE_INTEGRAZIONE;
				oraRegistrazione = eccezioneIntegrazione.getOraRegistrazione();
				messageType = eccezioneIntegrazione.getMessageType();
			}
			
			org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD idFunzione = null;
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.PORTA_DELEGATA;
			}
			else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.PORTA_APPLICATIVA;
			}
			else if(TipoPdD.INTEGRATION_MANAGER.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INTEGRATION_MANAGER;
			}
			else if(TipoPdD.ROUTER.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.ROUTER;
			}
			
			
			// dominio
			Dominio dominio = new Dominio();
			DominioSoggetto dominioSoggetto = new DominioSoggetto();
			dominioSoggetto.setTipo(idDominio.getTipo());
			dominioSoggetto.setBase(idDominio.getNome());
			dominio.setSoggetto(dominioSoggetto);
			dominio.setIdentificativoPorta(idDominio.getCodicePorta());
			dominio.setFunzione(idFunzione);
			dominio.setModulo(idModulo);
			erroreApplicativo.setDominio(dominio);
			
			// oraRegistrazione
			if(oraRegistrazione==null){
				oraRegistrazione = DateManager.getDate();
			}
			erroreApplicativo.setOraRegistrazione(oraRegistrazione);
			
			// dati-coopeazione
			if(fruitore!=null || servizio!=null || servizioApplicativo!=null){
				DatiCooperazione datiCooperazione = new DatiCooperazione();
				
				if(fruitore!=null){
					Soggetto fruitoreErroreApplicativo = new Soggetto();
					SoggettoIdentificativo fruitoreIdentificativoErroreApplicativo = new SoggettoIdentificativo();
					fruitoreIdentificativoErroreApplicativo.setTipo(fruitore.getTipo());
					fruitoreIdentificativoErroreApplicativo.setBase(fruitore.getNome());
					fruitoreErroreApplicativo.setIdentificativo(fruitoreIdentificativoErroreApplicativo);
					fruitoreErroreApplicativo.setIdentificativoPorta(fruitore.getCodicePorta());
					datiCooperazione.setFruitore(fruitoreErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getSoggettoErogatore()!=null){
					IDSoggetto erogatore = servizio.getSoggettoErogatore();
					Soggetto erogatoreErroreApplicativo = new Soggetto();
					SoggettoIdentificativo erogatoreIdentificativoErroreApplicativo = new SoggettoIdentificativo();
					erogatoreIdentificativoErroreApplicativo.setTipo(erogatore.getTipo());
					erogatoreIdentificativoErroreApplicativo.setBase(erogatore.getNome());
					erogatoreErroreApplicativo.setIdentificativo(erogatoreIdentificativoErroreApplicativo);
					erogatoreErroreApplicativo.setIdentificativoPorta(erogatore.getCodicePorta());
					datiCooperazione.setErogatore(erogatoreErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getTipoServizio()!=null && servizio.getServizio()!=null){
					Servizio servizioErroreApplicativo = new Servizio();
					servizioErroreApplicativo.setBase(servizio.getServizio());
					servizioErroreApplicativo.setTipo(servizio.getTipoServizio());
					if(servizio.getVersioneServizio()!=null && !"".equals(servizio.getVersioneServizio())){
						servizioErroreApplicativo.setVersione(Integer.parseInt(servizio.getVersioneServizio()));
					}
					datiCooperazione.setServizio(servizioErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getAzione()!=null){
					datiCooperazione.setAzione(servizio.getAzione());
				}
				
				datiCooperazione.setServizioApplicativo(servizioApplicativo);
				
				erroreApplicativo.setDatiCooperazione(datiCooperazione);
			}
			
			// eccezioni
			Eccezione eccezione = new Eccezione();
			CodiceEccezione codice = new CodiceEccezione();
			codice.setBase(codiceEccezione);
			codice.setTipo(codiceEccezioneOpenSPCoop);
			if(subCodiceEccezioneOpenSPCoop!=null)
				codice.setSottotipo(subCodiceEccezioneOpenSPCoop);
			eccezione.setCodice(codice);
			eccezione.setDescrizione(descrizioneEccezione);
			eccezione.setTipo(tipoEccezione);
			erroreApplicativo.setEccezione(eccezione);
			
			return erroreApplicativo; 

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
		
	
	
	
	
	// UTILS - BUILD OPENSPCOOP2 MESSAGE	

	private OpenSPCoop2Message _buildErroreApplicativo_OpenSPCoop2Message(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		MessageType messageType = null;
		try{

			// MESSAGE TYPE
			if(eccezioneIntegrazione!=null){
				messageType = eccezioneIntegrazione.getMessageType();
			}else{
				messageType = eccezioneProtocollo.getMessageType();
			}
			
			
			switch (messageType) {
			
				case XML:
				
					byte[] bytes = this._buildErroreApplicativo_ByteArray(TipoErroreApplicativo.XML, eccezioneProtocollo, eccezioneIntegrazione);
					OpenSPCoop2MessageParseResult pr = this.msgFactory.createMessage(messageType, MessageRole.FAULT, HttpConstants.CONTENT_TYPE_XML, 
							new ByteArrayInputStream(bytes), null, 
							false, null, null);
					return pr.getMessage_throwParseException();

				case JSON:
				
					bytes = this._buildErroreApplicativo_ByteArray(TipoErroreApplicativo.JSON, eccezioneProtocollo, eccezioneIntegrazione);
					pr = this.msgFactory.createMessage(messageType, MessageRole.FAULT, HttpConstants.CONTENT_TYPE_JSON, 
							new ByteArrayInputStream(bytes), null, 
							false, null, null);
					return pr.getMessage_throwParseException();

				default:
				
					// PROPRIETA ERRORE APPLICATIVO
					ProprietaErroreApplicativo proprieta = null;
					if(eccezioneIntegrazione!=null){
						proprieta = eccezioneIntegrazione.getProprieta();
					}else{
						proprieta = eccezioneProtocollo.getProprieta();
					}
				
					// PERSONALIZZAZIONE MESSAGGI
					String codiceEccezione = null;
					String posizioneEccezione = null;
					if(eccezioneProtocollo!=null){
						
						// cambio il msg nell'eccezione, aggiungendo il soggetto che l'ha prodotta
						// A meno di porta di dominio non disponibile
						String msgPortaDiDominioNonDisponibile = 
							CostantiProtocollo.PDD_NON_DISPONIBILE.
							replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE,
									eccezioneProtocollo.getSoggettoProduceEccezione().getTipo()+
									eccezioneProtocollo.getSoggettoProduceEccezione().getNome());
						if(eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory).indexOf(msgPortaDiDominioNonDisponibile)==-1)
							eccezioneProtocollo.getEccezioneProtocollo().
							setDescrizione(eccezioneProtocollo.getSoggettoProduceEccezione().toString() +" ha rilevato le seguenti eccezioni:\n"+eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory));
						
						// Raccolgo codice e messaggio
						codiceEccezione = 
							this.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
									eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
						posizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory);
						
					}
					else{
					
						codiceEccezione = this.traduttore.toString(eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore(),
								proprieta.getFaultPrefixCode(),proprieta.isFaultAsGenericCode());
						posizioneEccezione = proprieta.transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.factory);
					
					}
										
					// ELEMENT RISPOSTA APPLICATIVA ERRORE			
					SOAPElement rispostaApplicativaElement = this._buildErroreApplicativo_SoapElement(eccezioneProtocollo, eccezioneIntegrazione);

					OpenSPCoop2Message responseMessageError = this.msgFactory.createMessage(messageType,MessageRole.FAULT);
					OpenSPCoop2SoapMessage soapMessageError = responseMessageError.castAsSoap();
					SOAPBody soapBody = soapMessageError.getSOAPBody();
					SOAPFaultCode code = null;
					
					// ECCEZIONE CODE
					QName eccezioneName = null;
					if(eccezioneIntegrazione!=null){
						eccezioneName = this.getQNameEccezioneIntegrazione(codiceEccezione);
						code = eccezioneIntegrazione.getSoapFaultCode();
					}else{
						eccezioneName = this.getQNameEccezioneProtocollo(codiceEccezione);
						code = eccezioneProtocollo.getSoapFaultCode();
					}
					
					// Genero FAULT O ERRORE XML
					if(proprieta.isFaultAsXML()){
						soapBody.appendChild(soapBody.getOwnerDocument().importNode(rispostaApplicativaElement,true));
			
						//NOTA: in caso il servizio applicativo voglia un errore XML non deve essere aggiunto il Details di OpenSPCoop
						// Altrimenti l'xml ritornato non e' piu' compatibile con quello definito da XSD
					}
					else{
						soapBody.addFault();
						SOAPFault fault = soapBody.getFault();
						soapMessageError.setFaultCode(fault, code, eccezioneName);
						fault.setFaultActor(proprieta.getFaultActor());
						if(proprieta.isInsertAsDetails()){
							fault.setFaultString(posizioneEccezione);
						
							Detail d = fault.getDetail();
							if(d==null){
								d = fault.addDetail();
								d = fault.getDetail();
							}
							
							d.appendChild(d.getOwnerDocument().importNode(rispostaApplicativaElement, true));
							
						}else{
							fault.setFaultString(Utilities.toString(this.log, rispostaApplicativaElement));
						}
						
						// DettaglioEccezione
						DettaglioEccezione dettaglioEccezione = null;
						if(eccezioneIntegrazione!=null){
							dettaglioEccezione = eccezioneIntegrazione.getDettaglioEccezionePdD();
						}else{
							dettaglioEccezione = eccezioneProtocollo.getDettaglioEccezionePdD();
						}
						if(dettaglioEccezione!=null){
							Detail d = fault.getDetail();
							if(d==null){
								d = fault.addDetail();
								d = fault.getDetail();
							}
							byte[] dettaglioEccezioneBytes = org.openspcoop2.core.eccezione.details.utils.XMLUtils.generateDettaglioEccezione(dettaglioEccezione);
							d.appendChild(d.getOwnerDocument().importNode(this.xmlUtils.newDocument(dettaglioEccezioneBytes).getDocumentElement(), true));
						}
					}
					
					if(eccezioneProtocollo!=null){
						responseMessageError.setParseException(eccezioneProtocollo.getParseException());
					}
					else if(eccezioneIntegrazione!=null){
						responseMessageError.setParseException(eccezioneIntegrazione.getParseException());
					}
					
					return responseMessageError;
					
			}

		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di errore applicativo",e);
			return this.msgFactory.createFaultMessage(messageType,"ErroreDiProcessamento");
		}
	}
	
	
	
	
	
	// UTILS - PARSING
	
	private AbstractEccezioneBuilderParameter _parseErroreApplicativo(ErroreApplicativo erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
						
			AbstractEccezioneBuilderParameter eccezione = null;
			if( TipoEccezione.ECCEZIONE_PROTOCOLLO.equals(erroreApplicativo.getEccezione().getTipo())){
				eccezione = new EccezioneProtocolloBuilderParameters();
				
				CodiceErroreCooperazione codice = CodiceErroreCooperazione.toCodiceErroreCooperazione(erroreApplicativo.getEccezione().getCodice().getTipo().intValue());
				String descrizione = erroreApplicativo.getEccezione().getCodice().getBase();
				ErroreCooperazione erroreCooperazione = new ErroreCooperazione(descrizione, codice);
				org.openspcoop2.protocol.sdk.Eccezione eccezioneProtocollo = 
						new org.openspcoop2.protocol.sdk.Eccezione(erroreCooperazione,true,erroreApplicativo.getDominio().getFunzione().getValue(),this.factory);
				if(erroreApplicativo.getEccezione().getCodice().getSottotipo()!=null){
					SubCodiceErrore sub = new SubCodiceErrore();
					sub.setSubCodice(erroreApplicativo.getEccezione().getCodice().getSottotipo().intValue());
					eccezioneProtocollo.setSubCodiceEccezione(sub);
				}
				((EccezioneProtocolloBuilderParameters)eccezione).setEccezioneProtocollo(eccezioneProtocollo);
			}
			else{
				eccezione = new EccezioneIntegrazioneBuilderParameters();
				CodiceErroreIntegrazione codice = CodiceErroreIntegrazione.toCodiceErroreIntegrazione(erroreApplicativo.getEccezione().getCodice().getTipo().intValue());
				String descrizione = erroreApplicativo.getEccezione().getCodice().getBase();
				ErroreIntegrazione erroreIntegrazione = new ErroreIntegrazione(descrizione, codice);
				((EccezioneIntegrazioneBuilderParameters)eccezione).setErroreIntegrazione(erroreIntegrazione);
			}
				
			// dominio
			eccezione.setDominioPorta(new IDSoggetto(erroreApplicativo.getDominio().getSoggetto().getTipo(), 
					erroreApplicativo.getDominio().getSoggetto().getBase(),
					erroreApplicativo.getDominio().getIdentificativoPorta()));
			eccezione.setIdFunzione(erroreApplicativo.getDominio().getModulo());
			if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.PORTA_DELEGATA.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.DELEGATA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.PORTA_APPLICATIVA.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.APPLICATIVA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INTEGRATION_MANAGER.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.INTEGRATION_MANAGER);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.ROUTER.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.ROUTER);
			}
				
			// oraRegistrazione
			eccezione.setOraRegistrazione(erroreApplicativo.getOraRegistrazione());
			
			// dati cooperazione
			if(erroreApplicativo.getDatiCooperazione()!=null){
				DatiCooperazione datiCooperazione = erroreApplicativo.getDatiCooperazione();
				
				if(datiCooperazione.getFruitore()!=null){
					eccezione.setMittente(new IDSoggetto(datiCooperazione.getFruitore().getIdentificativo().getTipo(), 
							datiCooperazione.getFruitore().getIdentificativo().getBase(), 
							datiCooperazione.getFruitore().getIdentificativoPorta()));
				}
				
				IDServizio idServizio = null;
				if(datiCooperazione.getErogatore()!=null){
					if(idServizio==null){
						idServizio = new IDServizio();
					}
					idServizio.setSoggettoErogatore(new IDSoggetto(datiCooperazione.getErogatore().getIdentificativo().getTipo(), 
							datiCooperazione.getErogatore().getIdentificativo().getBase(), 
							datiCooperazione.getErogatore().getIdentificativoPorta()));
				}
				if(datiCooperazione.getServizio()!=null){
					idServizio.setTipoServizio(datiCooperazione.getServizio().getTipo());
					idServizio.setServizio(datiCooperazione.getServizio().getBase());
					if(datiCooperazione.getServizio().getVersione()!=null){
						idServizio.setVersioneServizio(datiCooperazione.getServizio().getVersione().toString());
					}
				}
				idServizio.setAzione(datiCooperazione.getAzione());
				if(idServizio!=null){
					eccezione.setServizio(idServizio);
				}
				
				eccezione.setServizioApplicativo(datiCooperazione.getServizioApplicativo());
			}
				
			return eccezione;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	
	
	
	
	
	
	
	
}