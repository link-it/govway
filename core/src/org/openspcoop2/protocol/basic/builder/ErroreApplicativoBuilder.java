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

package org.openspcoop2.protocol.basic.builder;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Random;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.apache.commons.lang.StringUtils;
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
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SOAPFaultCode;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.Context;
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
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoErroreApplicativo;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.rest.problem.JsonSerializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807Builder;
import org.openspcoop2.utils.rest.problem.XmlSerializer;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * ErroreApplicativoBuilder
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoBuilder extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder {
	
	protected ITraduttore traduttore;
	protected OpenSPCoop2MessageFactory errorFactory = null;
	protected org.openspcoop2.message.xml.XMLUtils xmlUtils;
	protected boolean omitXMLDeclaration;
	
	public ErroreApplicativoBuilder(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.DEFAULT;
		this.traduttore = factory.createTraduttore();
		this.errorFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		this.omitXMLDeclaration = false;
	}

	public boolean isOmitXMLDeclaration() {
		return this.omitXMLDeclaration;
	}
	public void setOmitXMLDeclaration(boolean omitXMLDeclaration) {
		this.omitXMLDeclaration = omitXMLDeclaration;
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
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_SoapElement(nop, parameters, null);
	}
	@Override
	public SOAPElement toSoapElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException{
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_SoapElement(nop, null,parameters);
	}
	

	@Override
	public Element toElement(EccezioneProtocolloBuilderParameters parameters) throws ProtocolException{
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_Element(nop, parameters, null);
	}
	@Override
	public Element toElement(EccezioneIntegrazioneBuilderParameters parameters) throws ProtocolException{
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_Element(nop, null,parameters);
	}
	
	
	@Override
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_String(tipoErroreApplicativo, this.omitXMLDeclaration, nop, parameters, null);
	}
	@Override
	public String toString(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_String(tipoErroreApplicativo, this.omitXMLDeclaration, nop, null,parameters);
	}
	
	
	@Override
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_ByteArray(tipoErroreApplicativo, this.omitXMLDeclaration, nop, parameters, null);
	}
	@Override
	public byte[] toByteArray(TipoErroreApplicativo tipoErroreApplicativo, 
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		CodeDetailsError nop = new CodeDetailsError();
		return this._buildErroreApplicativo_ByteArray(tipoErroreApplicativo, this.omitXMLDeclaration, nop, null,parameters);
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
		if(this.isErroreApplicativo(erroreApplicativo)==false){
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
		ErroreApplicativoMessageUtils.addErroreApplicativoIntoSOAPFaultDetail(
				this.toSoapElement(parameters), msg, this.log);
	}

	@Override
	public void insertInSOAPFault(
			EccezioneIntegrazioneBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException {
		ErroreApplicativoMessageUtils.addErroreApplicativoIntoSOAPFaultDetail(
				this.toSoapElement(parameters), msg, this.log);
	}

	@Override
	public void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg) throws ProtocolException{
		
		ErroreApplicativoMessageUtils.insertRoutingErrorInSOAPFault(identitaRouter, idFunzione, msgErrore, msg, this.log, this.xmlUtils);
		
	}
	
	
	
	
	
	
	/** BUILDER UTILITIES */
	
	protected SOAPElement _buildErroreApplicativo_SoapElement(CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		Element elementErroreApplicativo = this._buildErroreApplicativo_Element(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
		
		try{
		
			MessageType messageType = null;
			if(eccezioneProtocollo!=null){
				messageType = eccezioneProtocollo.getMessageType();
			}
			else{
				messageType = eccezioneIntegrazione.getMessageType();
			}
			
			SOAPFactory sf = SoapUtils.getSoapFactory(this.errorFactory, messageType);
			SOAPElement erroreApplicativoElementSOAP =  sf.createElement(elementErroreApplicativo);
			
			return erroreApplicativoElementSOAP;
		
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
			
	}
	
	protected Element _buildErroreApplicativo_Element(CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
	
		try{
			ConfigurationRFC7807 rfc7807 = null;
			if(eccezioneIntegrazione!=null){
				rfc7807 = eccezioneIntegrazione.getRfc7807();
			}else{
				rfc7807 = eccezioneProtocollo.getRfc7807();
			}
			if(rfc7807!=null) {
				ProblemRFC7807 problemRFC7807 = this._buildErroreApplicativo_problemRFC7807(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				XmlSerializer xmlSerializer = new XmlSerializer();	
				return xmlSerializer.toNode(problemRFC7807);
			}
			else {
				// il passaggio da XMLUtils forza anche la validazione dell'oggetto
				ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(false, codeDetailsErrorWrapper,
						eccezioneProtocollo, eccezioneIntegrazione);
				byte[]xmlErroreApplicativo = org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativo(erroreApplicativo);
				Element elementErroreApplicativo = this.xmlUtils.newElement(xmlErroreApplicativo);
				ErroreApplicativoMessageUtils.addPrefixToElement(elementErroreApplicativo,"op2ErrAppl");
				
				return elementErroreApplicativo;
			}
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
	
	protected String _buildErroreApplicativo_String(TipoErroreApplicativo tipoErroreApplicativo, boolean omitXMLDeclaration,
			CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			ConfigurationRFC7807 rfc7807 = null;
			if(eccezioneIntegrazione!=null){
				rfc7807 = eccezioneIntegrazione.getRfc7807();
			}else{
				rfc7807 = eccezioneProtocollo.getRfc7807();
			}
			if(rfc7807!=null) {
				ProblemRFC7807 problemRFC7807 = this._buildErroreApplicativo_problemRFC7807(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
					JsonSerializer jsonSerializer = new JsonSerializer();
					return jsonSerializer.toString(problemRFC7807);
				}
				else {
					XmlSerializer xmlSerializer = new XmlSerializer();	
					return xmlSerializer.toString(problemRFC7807, omitXMLDeclaration);
				}
			}
			else {
				if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
					ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(false, codeDetailsErrorWrapper,
							eccezioneProtocollo, eccezioneIntegrazione);
					return org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo);
				}
				else{
					Element element = this._buildErroreApplicativo_Element(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
					return this.xmlUtils.toString(element, omitXMLDeclaration);
				}
			}
		
		}catch(Exception e){
			throw new ProtocolException("toString failed: "+e.getMessage(),e);
		}
	}
	
	protected byte[] _buildErroreApplicativo_ByteArray(TipoErroreApplicativo tipoErroreApplicativo, boolean omitXMLDeclaration,
			CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			ConfigurationRFC7807 rfc7807 = null;
			if(eccezioneIntegrazione!=null){
				rfc7807 = eccezioneIntegrazione.getRfc7807();
			}else{
				rfc7807 = eccezioneProtocollo.getRfc7807();
			}
			if(rfc7807!=null) {
				ProblemRFC7807 problemRFC7807 = this._buildErroreApplicativo_problemRFC7807(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
					JsonSerializer jsonSerializer = new JsonSerializer();
					return jsonSerializer.toByteArray(problemRFC7807);
				}
				else {
					XmlSerializer xmlSerializer = new XmlSerializer();	
					return xmlSerializer.toByteArray(problemRFC7807, omitXMLDeclaration);
				}
			}
			else {
				if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
					ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(false, codeDetailsErrorWrapper,
							eccezioneProtocollo, eccezioneIntegrazione);
					return org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo).getBytes();
				}
				else{
					Element element = this._buildErroreApplicativo_Element(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
					return this.xmlUtils.toByteArray(element, omitXMLDeclaration);
				}
			}
		
		}catch(Exception e){
			throw new ProtocolException("toByteArray failed: "+e.getMessage(),e);
		}
	}
	
	private ProblemRFC7807 _buildErroreApplicativo_problemRFC7807(CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			
			ConfigurationRFC7807 rfc7807 = null;
			int govwayCode; 
			String nomePorta;
			String transactionId;
			IntegrationFunctionError functionError;
			boolean genericDetails = true;
			if(eccezioneIntegrazione!=null){
				rfc7807 = eccezioneIntegrazione.getRfc7807();
				govwayCode = eccezioneIntegrazione.getReturnConfig().getGovwayReturnCode();
				nomePorta = eccezioneIntegrazione.getNomePorta();
				transactionId = eccezioneIntegrazione.getTransactionId();
				functionError = eccezioneIntegrazione.getFunctionError();
				genericDetails = eccezioneIntegrazione.getReturnConfig().isGenericDetails();
			}else{
				rfc7807 = eccezioneProtocollo.getRfc7807();
				govwayCode = eccezioneProtocollo.getReturnConfig().getGovwayReturnCode();
				nomePorta = eccezioneProtocollo.getNomePorta();
				transactionId = eccezioneProtocollo.getTransactionId();
				functionError = eccezioneProtocollo.getFunctionError();
				genericDetails = eccezioneProtocollo.getReturnConfig().isGenericDetails();
			}
			if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
				genericDetails = true;
			}
			if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
				genericDetails = false;
			}
			
			// Problem builder
			ProblemRFC7807Builder rfc7807ProblemBuilder = null;
			String webSite = erroriProperties.getWebSite(functionError);
			if(webSite!=null && !"".equals(webSite)) {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(webSite);
			}
			else if(rfc7807.isType()) {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(rfc7807.getTypeFormat());
			}
			else {
				rfc7807ProblemBuilder = new ProblemRFC7807Builder(false);
			}
			
			// Esamino errore
			ErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(true, null,
					eccezioneProtocollo, eccezioneIntegrazione);
			if(erroreApplicativo.getException()!=null) {
				codeDetailsErrorWrapper.setDetails(erroreApplicativo.getException().getDescription());
			}
			if(erroreApplicativo.getException()!=null &&
					erroreApplicativo.getException().getCode()!=null &&
					erroreApplicativo.getException().getCode().getBase()!=null) {
				String prefixCodeStatus = null;
				if(erroreApplicativo.getException().getType()!=null && 
						org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione.PROTOCOL.equals(erroreApplicativo.getException().getType())) {
					prefixCodeStatus = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL;
				}
				else {
					prefixCodeStatus = org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION;
				}
				String code = erroreApplicativo.getException().getCode().getBase();
				codeDetailsErrorWrapper.setPrefixCode(prefixCodeStatus);
				codeDetailsErrorWrapper.setCode(code);
			}
			
			// Costruisco problem
			ProblemRFC7807 problemRFC7807 = rfc7807ProblemBuilder.buildProblem(govwayCode);
						
			// details
			if(rfc7807.isDetails()) {
				if(codeDetailsErrorWrapper.getDetails()!=null && !genericDetails) {
					problemRFC7807.setDetail(codeDetailsErrorWrapper.getDetails());
				}
				else {
					problemRFC7807.setDetail(erroriProperties.getGenericDetails(functionError));
				}
			}
			
			// govway-type
			if(rfc7807.isGovwayType()) {
				String govwayType = erroriProperties.getErrorType(functionError);
				
				// title
				if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE()) {
					if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE()) {
						problemRFC7807.setTitle(StringUtils.join(
							     StringUtils.splitByCharacterTypeCamelCase(govwayType),
							     ' '));
					}
					else {
						problemRFC7807.setTitle(govwayType);
					}
					
					if(Costanti.isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM()) {
						problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE(), govwayType);
					}
				}
				else {
					problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TYPE(), govwayType);
				}
			}
			
			// govway-status
			if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO && rfc7807.isGovwayStatus()) {
				if(codeDetailsErrorWrapper.getCode()!=null && codeDetailsErrorWrapper.getPrefixCode()!=null) {
					problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_CODE(), 
							codeDetailsErrorWrapper.getPrefixCode()+codeDetailsErrorWrapper.getCode());
				}
			}
			
			// instance
			if(Costanti.TRANSACTION_ERROR_INSTANCE_ID_ABILITATO && rfc7807.isInstance()) {
				problemRFC7807.setInstance(nomePorta);
			}
			
			// govway-transactionId
			if(rfc7807.isGovwayTransactionId()) {
				problemRFC7807.getCustom().put(org.openspcoop2.protocol.basic.Costanti.getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID(), transactionId);
			}
			
			return problemRFC7807;
		}catch(Exception e){
			throw new ProtocolException("toProblemRFC7807 failed: "+e.getMessage());
		}
	}
	
	private ErroreApplicativo _buildErroreApplicativo_engine(boolean ignoreIntegrationFunctionErroreDirective, CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		try{
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
						
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
			
			IntegrationFunctionError functionError = null;
			boolean genericDetails = true;
			int govwayCode = 500; 
			
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
				descrizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.protocolFactory);
				tipoEccezione = TipoEccezione.PROTOCOL;
				oraRegistrazione = eccezioneProtocollo.getOraRegistrazione();
				messageType = eccezioneProtocollo.getMessageType();
				
				if(!ignoreIntegrationFunctionErroreDirective) {
					functionError = eccezioneProtocollo.getFunctionError();
					if(eccezioneProtocollo.getReturnConfig()!=null) {
						genericDetails = eccezioneProtocollo.getReturnConfig().isGenericDetails();
						govwayCode = eccezioneProtocollo.getReturnConfig().getGovwayReturnCode();
					}
					
					codeDetailsErrorWrapper.setPrefixCode(org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL);
					codeDetailsErrorWrapper.setCode(codiceEccezione);
					
					codeDetailsErrorWrapper.setDetails(descrizioneEccezione);
				}
			}
			else{
				idDominio = eccezioneIntegrazione.getDominioPorta();
				idModulo = eccezioneIntegrazione.getIdFunzione();
				tipoPdD = eccezioneIntegrazione.getTipoPorta();
				fruitore = eccezioneIntegrazione.getMittente();
				servizio = eccezioneIntegrazione.getServizio();
				servizioApplicativo = eccezioneIntegrazione.getServizioApplicativo();
				codiceEccezione = this.traduttore.toCodiceErroreIntegrazioneAsString(eccezioneIntegrazione.getErroreIntegrazione(),
						eccezioneIntegrazione.getProprieta().getFaultPrefixCode(),eccezioneIntegrazione.getProprieta().isFaultAsGenericCode());
				codiceEccezioneOpenSPCoop = eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore().getCodice();
				descrizioneEccezione = eccezioneIntegrazione.getProprieta().transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.protocolFactory);
				tipoEccezione = TipoEccezione.INTEGRATION;
				oraRegistrazione = eccezioneIntegrazione.getOraRegistrazione();
				messageType = eccezioneIntegrazione.getMessageType();
				
				if(!ignoreIntegrationFunctionErroreDirective) {
					functionError = eccezioneIntegrazione.getFunctionError();
					if(eccezioneIntegrazione.getReturnConfig()!=null) {
						genericDetails = eccezioneIntegrazione.getReturnConfig().isGenericDetails();
						govwayCode = eccezioneIntegrazione.getReturnConfig().getGovwayReturnCode();
					}
					
					codeDetailsErrorWrapper.setPrefixCode(org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION);
					codeDetailsErrorWrapper.setCode(codiceEccezione);
					
					codeDetailsErrorWrapper.setDetails(descrizioneEccezione);
				}
			}
			
			if(!ignoreIntegrationFunctionErroreDirective) {
				if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
					genericDetails = true;
				}
				if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
					genericDetails = false;
				}
			}
			
			org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD idFunzione = null;
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.OUTBOUND_PROXY;
			}
			else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INBOUND_PROXY;
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
			dominioSoggetto.setType(idDominio.getTipo());
			dominioSoggetto.setBase(idDominio.getNome());
			dominio.setOrganization(dominioSoggetto);
			dominio.setId(idDominio.getCodicePorta());
			dominio.setRole(idFunzione);
			dominio.setModule(idModulo);
			erroreApplicativo.setDomain(dominio);
			
			// oraRegistrazione
			if(oraRegistrazione==null){
				oraRegistrazione = DateManager.getDate();
			}
			erroreApplicativo.setTimestamp(oraRegistrazione);
			
			// dati-coopeazione
			if(fruitore!=null || servizio!=null || servizioApplicativo!=null){
				DatiCooperazione datiCooperazione = new DatiCooperazione();
				
				if(fruitore!=null){
					Soggetto fruitoreErroreApplicativo = new Soggetto();
					SoggettoIdentificativo fruitoreIdentificativoErroreApplicativo = new SoggettoIdentificativo();
					fruitoreIdentificativoErroreApplicativo.setType(fruitore.getTipo());
					fruitoreIdentificativoErroreApplicativo.setBase(fruitore.getNome());
					fruitoreErroreApplicativo.setId(fruitoreIdentificativoErroreApplicativo);
					fruitoreErroreApplicativo.setDomainId(fruitore.getCodicePorta());
					datiCooperazione.setSender(fruitoreErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getSoggettoErogatore()!=null){
					IDSoggetto erogatore = servizio.getSoggettoErogatore();
					Soggetto erogatoreErroreApplicativo = new Soggetto();
					SoggettoIdentificativo erogatoreIdentificativoErroreApplicativo = new SoggettoIdentificativo();
					erogatoreIdentificativoErroreApplicativo.setType(erogatore.getTipo());
					erogatoreIdentificativoErroreApplicativo.setBase(erogatore.getNome());
					erogatoreErroreApplicativo.setId(erogatoreIdentificativoErroreApplicativo);
					erogatoreErroreApplicativo.setDomainId(erogatore.getCodicePorta());
					datiCooperazione.setProvider(erogatoreErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getTipo()!=null && servizio.getNome()!=null && servizio.getVersione()!=null){
					Servizio servizioErroreApplicativo = new Servizio();
					servizioErroreApplicativo.setBase(servizio.getNome());
					servizioErroreApplicativo.setType(servizio.getTipo());
					servizioErroreApplicativo.setVersion(servizio.getVersione());
					datiCooperazione.setService(servizioErroreApplicativo);
				}
				
				if(servizio!=null && servizio.getAzione()!=null){
					datiCooperazione.setAction(servizio.getAzione());
				}
				
				datiCooperazione.setApplication(servizioApplicativo);
				
				erroreApplicativo.setService(datiCooperazione);
			}
			
			// eccezioni
			Eccezione eccezione = new Eccezione();
			CodiceEccezione codice = new CodiceEccezione();
			if(ignoreIntegrationFunctionErroreDirective || Costanti.TRANSACTION_ERROR_STATUS_ABILITATO) {
				codice.setBase(codiceEccezione);
				codice.setType(codiceEccezioneOpenSPCoop);
				if(subCodiceEccezioneOpenSPCoop!=null)
					codice.setSubtype(subCodiceEccezioneOpenSPCoop);
			}
			else {
				String govwayType = erroriProperties.getErrorType(functionError);
				codice.setBase(govwayType);
				codice.setType(govwayCode);
			}
			eccezione.setCode(codice);
			
			if(ignoreIntegrationFunctionErroreDirective || !genericDetails) {
				eccezione.setDescription(descrizioneEccezione);
			}
			else {
				eccezione.setDescription(erroriProperties.getGenericDetails(functionError));
			}
			
			eccezione.setType(tipoEccezione);
			erroreApplicativo.setException(eccezione);
			
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
		ConfigurationRFC7807 rfc7807= null;
		boolean useProblemRFC7807 = false;
		CodeDetailsError codeDetailsErrorWrapper = null;
		OpenSPCoop2Message msg = null;
		boolean addErroreProtocolloInMessaggio = false;
		IntegrationFunctionError functionError = null;
		IntegrationErrorReturnConfiguration returnConfig = null;
		ErroriProperties erroriProperties = null;
		
		try{
			erroriProperties = ErroriProperties.getInstance(this.log);

			Context context = null;
			if(eccezioneIntegrazione!=null){
				context = eccezioneIntegrazione.getContext();
				functionError = eccezioneIntegrazione.getFunctionError();
				returnConfig = eccezioneIntegrazione.getReturnConfig();
			}
			else {
				context = eccezioneProtocollo.getContext();
				functionError = eccezioneProtocollo.getFunctionError();
				returnConfig = eccezioneProtocollo.getReturnConfig();
			}
			
			if(eccezioneProtocollo!=null) { // non aggiungamo anche gli errori di processamento altrimenti vengono catalogati come errore di protocollo se avvengono prima della generazione della busta errore
				if(context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO)) {
					Object o = context.getObject(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO);
					if(o!=null) {
						if(o instanceof String) {
							addErroreProtocolloInMessaggio = "true".equalsIgnoreCase((String)o);
						}
						else if(o instanceof Boolean) {
							addErroreProtocolloInMessaggio = (Boolean) o;
						}
					}
				}
			}
			
			// RFC7807
			if(eccezioneIntegrazione!=null){
				rfc7807 = eccezioneIntegrazione.getRfc7807();
			}else{
				rfc7807 = eccezioneProtocollo.getRfc7807();
			}
			useProblemRFC7807 = rfc7807!=null;
			
			// MESSAGE TYPE
			if(eccezioneIntegrazione!=null){
				messageType = eccezioneIntegrazione.getMessageType();
			}else{
				messageType = eccezioneProtocollo.getMessageType();
			}
						
			boolean omitXMLDeclaration = true;
			
			// 1) Il Messagge Type XML o JSON DEVE ESSERE CAPITO PRIMA
			// 2) In questo momento deve arrivare la configurazione del RFC PROBLEM
			
			// FIX: in caso di XML devo restituire un errore 'XML'
			
			// PROPRIETA ERRORE APPLICATIVO
			ProprietaErroreApplicativo proprieta = null;
			if(eccezioneIntegrazione!=null){
				proprieta = eccezioneIntegrazione.getProprieta();
			}else{
				proprieta = eccezioneProtocollo.getProprieta();
			}
			
			if(proprieta!=null && 
					proprieta.isFaultAsXML()){
				messageType = MessageType.XML;
				returnConfig.setHttpReturnCode(returnConfig.getGovwayReturnCode());
			}
			
			switch (messageType) {
			
				case XML:
				
					codeDetailsErrorWrapper = new CodeDetailsError();
					byte[] bytes = this._buildErroreApplicativo_ByteArray(TipoErroreApplicativo.XML, !omitXMLDeclaration, codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
					OpenSPCoop2MessageParseResult pr = this.errorFactory.createMessage(messageType, MessageRole.FAULT, HttpConstants.CONTENT_TYPE_XML, bytes);
					msg = pr.getMessage_throwParseException();
					if(useProblemRFC7807) {
						msg.setContentType(HttpConstants.CONTENT_TYPE_XML_PROBLEM_DETAILS_RFC_7807);
					}
					else {
						msg.setContentType(HttpConstants.CONTENT_TYPE_XML);
					}
					return msg;

				case JSON:
				
					codeDetailsErrorWrapper = new CodeDetailsError();
					bytes = this._buildErroreApplicativo_ByteArray(TipoErroreApplicativo.JSON, !omitXMLDeclaration, codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
					pr = this.errorFactory.createMessage(messageType, MessageRole.FAULT, HttpConstants.CONTENT_TYPE_JSON, bytes);
					msg = pr.getMessage_throwParseException();
					if(useProblemRFC7807) {
						msg.setContentType(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
					}
					else {
						msg.setContentType(HttpConstants.CONTENT_TYPE_JSON);
					}
					return msg;
					
				case BINARY:
				case MIME_MULTIPART:
					// Viene usato per l'opzione None dove viene ritornato solamente il return code
					msg = this.errorFactory.createEmptyMessage(messageType, MessageRole.FAULT);
					return msg;

				default:
								
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
						if(eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.protocolFactory).indexOf(msgPortaDiDominioNonDisponibile)==-1)
							eccezioneProtocollo.getEccezioneProtocollo().
							setDescrizione(eccezioneProtocollo.getSoggettoProduceEccezione().toString() +" ha rilevato le seguenti eccezioni:\n"+eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.protocolFactory));
						
						// Raccolgo codice e messaggio
						codiceEccezione = 
							this.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
									eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
						posizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.protocolFactory);
						
					}
					else{
					
						codiceEccezione = this.traduttore.toCodiceErroreIntegrazioneAsString(eccezioneIntegrazione.getErroreIntegrazione(),
								proprieta.getFaultPrefixCode(),proprieta.isFaultAsGenericCode());
						posizioneEccezione = proprieta.transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.protocolFactory);
					
					}
										
					// ELEMENT RISPOSTA APPLICATIVA ERRORE			
					CodeDetailsError codeDetailsErrorWrapperNOP = new CodeDetailsError(); // uso quello del fault
					SOAPElement rispostaApplicativaElement = this._buildErroreApplicativo_SoapElement(codeDetailsErrorWrapperNOP, eccezioneProtocollo, eccezioneIntegrazione);

					msg = this.errorFactory.createEmptyMessage(messageType,MessageRole.FAULT);
					OpenSPCoop2SoapMessage soapMessageError = msg.castAsSoap();
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
						
						codeDetailsErrorWrapper = new CodeDetailsError();
						
						// fault code
						codeDetailsErrorWrapper.setPrefixCode(eccezioneName.getPrefix());
						codeDetailsErrorWrapper.setCode(eccezioneName.getLocalPart());
						if(Costanti.TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE) {
							soapMessageError.setFaultCode(fault, code, eccezioneName);
						}
						else {
							String codiceEccezioneGW = Costanti.getTransactionSoapFaultCode(returnConfig.getGovwayReturnCode(),erroriProperties.getErrorType(functionError));
							// aggiorno code con codici govway
							if(returnConfig.getGovwayReturnCode()<=499) {
								code = SOAPFaultCode.Sender;
							}
							else {
								code = SOAPFaultCode.Receiver;
							}
							if(MessageType.SOAP_11.equals(messageType)) {
								codiceEccezioneGW = (SOAPFaultCode.Sender.equals(code) ? 
										org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT :  org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER) +
										org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+codiceEccezioneGW;
							}
							QName eccezioneNameGovway = null;
							if(MessageType.SOAP_11.equals(messageType)) {
								eccezioneNameGovway = new QName(org.openspcoop2.message.constants.Costanti.SOAP_ENVELOPE_NAMESPACE, codiceEccezioneGW, fault.getPrefix());
							}
							else {
								eccezioneNameGovway = this.getQNameEccezioneIntegrazione(codiceEccezioneGW);
							}
							msg.castAsSoap().setFaultCode(fault, code, eccezioneNameGovway);
						}
						
						// fault actor
						fault.setFaultActor(proprieta.getFaultActor());
						
						// fault string
						if(proprieta.isInsertAsDetails()){
							
							codeDetailsErrorWrapper.setDetails(posizioneEccezione);
							
							boolean genericDetails = returnConfig.isGenericDetails();
							if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
								genericDetails = true;
							}
							if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
								genericDetails = false;
							}
							if(codeDetailsErrorWrapper.getDetails()!=null && !genericDetails) {
								SoapUtils.setFaultString(fault, codeDetailsErrorWrapper.getDetails());
							}
							else {
								String errorMsg = erroriProperties.getGenericDetails(functionError);
								SoapUtils.setFaultString(fault, errorMsg);
							}
						
							Detail d = fault.getDetail();
							if(d==null){
								d = fault.addDetail();
								d = fault.getDetail();
							}
							
							d.appendChild(d.getOwnerDocument().importNode(rispostaApplicativaElement, true));
							
						}else{
							SoapUtils.setFaultString(fault, Utilities.toString(this.errorFactory, rispostaApplicativaElement, true));
						}
						
						// DettaglioEccezione
						DettaglioEccezione dettaglioEccezione = null;
						if(Costanti.TRANSACTION_ERROR_SOAP_FAULT_ADD_FAULT_DETAILS_WITH_PROBLEM_RFC7807 || !useProblemRFC7807) {
							if(eccezioneIntegrazione!=null){
								dettaglioEccezione = eccezioneIntegrazione.getDettaglioEccezionePdD();
							}else{
								dettaglioEccezione = eccezioneProtocollo.getDettaglioEccezionePdD();
							}
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
						msg.setParseException(eccezioneProtocollo.getParseException());
					}
					else if(eccezioneIntegrazione!=null){
						msg.setParseException(eccezioneIntegrazione.getParseException());
					}
					
					return msg;
					
			}

		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di errore applicativo",e);
			return this.errorFactory.createFaultMessage(messageType,useProblemRFC7807,"ErroreDiProcessamento");
		}finally {
			if(msg!=null) {
				msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY, useProblemRFC7807 ? 
						org.openspcoop2.message.constants.Costanti.TIPO_RFC7807 : org.openspcoop2.message.constants.Costanti.TIPO_GOVWAY );
				if(codeDetailsErrorWrapper!=null) {
					if(codeDetailsErrorWrapper.getPrefixCode()!=null) {
						String prefixInternalErrorCode = codeDetailsErrorWrapper.getPrefixCode();
						if(prefixInternalErrorCode.endsWith(":")) {
							prefixInternalErrorCode = prefixInternalErrorCode.substring(0, prefixInternalErrorCode.length()-1);
						}
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_PREFIX_CODE, prefixInternalErrorCode);
					}
					if(codeDetailsErrorWrapper.getCode()!=null) {
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_CODE, codeDetailsErrorWrapper.getCode() );
						if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO) {
							String code = codeDetailsErrorWrapper.getCode();
							if(codeDetailsErrorWrapper.getPrefixCode()!=null) {
								if(codeDetailsErrorWrapper.getPrefixCode().endsWith(":")) {
									code = codeDetailsErrorWrapper.getPrefixCode() + code;
								}
								else {
									code = codeDetailsErrorWrapper.getPrefixCode() + ":" +code;
								}
							}
							msg.forceTransportHeader(Costanti._getHTTP_HEADER_GOVWAY_ERROR_STATUS(), code);
						}
					}
					if(codeDetailsErrorWrapper.getDetails()!=null) {
						msg.addContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY_DETAILS, codeDetailsErrorWrapper.getDetails() );
					}
				}
				if(addErroreProtocolloInMessaggio) {
					msg.addContextProperty(org.openspcoop2.core.constants.Costanti.ERRORE_VALIDAZIONE_PROTOCOLLO, org.openspcoop2.core.constants.Costanti.ERRORE_TRUE);
				}
				
				try {
					if(erroriProperties!=null) {
						msg.forceTransportHeader(Costanti._getHTTP_HEADER_GOVWAY_ERROR_TYPE(), erroriProperties.getErrorType(functionError));
					}
				}catch(Exception e) {
					this.log.error("Scrittura header http 'GovWayErrorType' non riuscita: "+e.getMessage(),e);
				}
				
				if(returnConfig.isRetry()) {
					int seconds = returnConfig.getRetryAfterSeconds();
					if(seconds<0) {
						seconds=0;
					}
					if(returnConfig.getRetryRandomBackoffSeconds()>0) {
						seconds = seconds + new Random().nextInt(returnConfig.getRetryRandomBackoffSeconds());
					}
					msg.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
				
				msg.setForcedResponseCode(returnConfig.getHttpReturnCode()+"");
			}
		}
	}
	
	
	
	
	
	// UTILS - PARSING
	
	private AbstractEccezioneBuilderParameter _parseErroreApplicativo(ErroreApplicativo erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
						
			AbstractEccezioneBuilderParameter eccezione = null;
			if( TipoEccezione.PROTOCOL.equals(erroreApplicativo.getException().getType())){
				eccezione = new EccezioneProtocolloBuilderParameters();
				
				CodiceErroreCooperazione codice = CodiceErroreCooperazione.toCodiceErroreCooperazione(erroreApplicativo.getException().getCode().getType().intValue());
				String descrizione = erroreApplicativo.getException().getCode().getBase();
				ErroreCooperazione erroreCooperazione = new ErroreCooperazione(descrizione, codice);
				org.openspcoop2.protocol.sdk.Eccezione eccezioneProtocollo = 
						new org.openspcoop2.protocol.sdk.Eccezione(erroreCooperazione,true,erroreApplicativo.getDomain().getRole().getValue(),this.protocolFactory);
				if(erroreApplicativo.getException().getCode().getSubtype()!=null){
					SubCodiceErrore sub = new SubCodiceErrore();
					sub.setSubCodice(erroreApplicativo.getException().getCode().getSubtype().intValue());
					eccezioneProtocollo.setSubCodiceEccezione(sub);
				}
				((EccezioneProtocolloBuilderParameters)eccezione).setEccezioneProtocollo(eccezioneProtocollo);
			}
			else{
				eccezione = new EccezioneIntegrazioneBuilderParameters();
				CodiceErroreIntegrazione codice = CodiceErroreIntegrazione.toCodiceErroreIntegrazione(erroreApplicativo.getException().getCode().getType().intValue());
				String descrizione = erroreApplicativo.getException().getCode().getBase();
				ErroreIntegrazione erroreIntegrazione = new ErroreIntegrazione(descrizione, codice);
				((EccezioneIntegrazioneBuilderParameters)eccezione).setErroreIntegrazione(erroreIntegrazione);
			}
				
			// dominio
			eccezione.setDominioPorta(new IDSoggetto(erroreApplicativo.getDomain().getOrganization().getType(), 
					erroreApplicativo.getDomain().getOrganization().getBase(),
					erroreApplicativo.getDomain().getId()));
			eccezione.setIdFunzione(erroreApplicativo.getDomain().getModule());
			if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.OUTBOUND_PROXY.equals(erroreApplicativo.getDomain().getRole())){
				eccezione.setTipoPorta(TipoPdD.DELEGATA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INBOUND_PROXY.equals(erroreApplicativo.getDomain().getRole())){
				eccezione.setTipoPorta(TipoPdD.APPLICATIVA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INTEGRATION_MANAGER.equals(erroreApplicativo.getDomain().getRole())){
				eccezione.setTipoPorta(TipoPdD.INTEGRATION_MANAGER);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.ROUTER.equals(erroreApplicativo.getDomain().getRole())){
				eccezione.setTipoPorta(TipoPdD.ROUTER);
			}
				
			// oraRegistrazione
			eccezione.setOraRegistrazione(erroreApplicativo.getTimestamp());
			
			// dati cooperazione
			if(erroreApplicativo.getService()!=null){
				DatiCooperazione datiCooperazione = erroreApplicativo.getService();
				
				if(datiCooperazione.getSender()!=null){
					eccezione.setMittente(new IDSoggetto(datiCooperazione.getSender().getId().getType(), 
							datiCooperazione.getSender().getId().getBase(), 
							datiCooperazione.getSender().getDomainId()));
				}
				
				IDServizio idServizio = null;
				IDSoggetto idSoggettoErogatore = null;
				if(datiCooperazione.getProvider()!=null){
					idSoggettoErogatore = new IDSoggetto(datiCooperazione.getProvider().getId().getType(), 
							datiCooperazione.getProvider().getId().getBase(), 
							datiCooperazione.getProvider().getDomainId());
				}
				if(datiCooperazione.getService()!=null){
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(datiCooperazione.getService().getType(), 
							datiCooperazione.getService().getBase(), 
							idSoggettoErogatore, 
							datiCooperazione.getService().getVersion());
				}
				if(idServizio!=null){
					idServizio.setAzione(datiCooperazione.getAction());
					eccezione.setServizio(idServizio);
				}
				
				eccezione.setServizioApplicativo(datiCooperazione.getApplication());
			}
				
			return eccezione;
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	
	
	
	
	
	
	
	
}