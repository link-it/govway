/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.builder;

import java.io.ByteArrayInputStream;
import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.builder.CodeDetailsError;
import org.openspcoop2.protocol.basic.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.basic.builder.ErroreApplicativoMessageUtils;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.TipoErroreApplicativo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Element;

import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneBusta;
import it.cnipa.schemas._2003.egovit.exception1_0.EccezioneProcessamento;
import it.cnipa.schemas._2003.egovit.exception1_0.MessaggioDiErroreApplicativo;
import it.cnipa.schemas._2003.egovit.exception1_0.utils.XMLUtils;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopErroreApplicativoBuilder extends ErroreApplicativoBuilder implements org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder {

	
	public SPCoopErroreApplicativoBuilder(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	
	// NAMESPACE
	
	@Override
	public String getNamespaceEccezioneProtocollo(String defaultNamespace){
		return SPCoopCostanti.NAMESPACE_EGOV;
	}
	
	
	
	
	// UTILITY DI RICONOSCIMENTO
	
	@Override
	public boolean isErroreApplicativo(String namespace, String localName){
		if("MessaggioDiErroreApplicativo".equals(localName) && 
				SPCoopCostanti.NAMESPACE_ECCEZIONE_APPLICATIVA_EGOV.equals(namespace) 
			){
			return true;
		}
		return false;
	}
	

	
	
	// BUILDER
	
	
	/** BUILDER UTILITIES */
		
	@Override
	protected Element _buildErroreApplicativo_Element(CodeDetailsError codeDetailsErrorWrapper, EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
	
		MessaggioDiErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
		
		try{
			// il passaggio da XMLUtils forza anche la validazione dell'oggetto
			byte[]xmlErroreApplicativo = XMLUtils.generateErroreApplicativo(erroreApplicativo);
			Element elementErroreApplicativo = this.xmlUtils.newElement(xmlErroreApplicativo);
			ErroreApplicativoMessageUtils.addPrefixToElement(elementErroreApplicativo,"cnipaErrAppl");
			
			return elementErroreApplicativo;
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
	
	@Override
	protected String _buildErroreApplicativo_String(TipoErroreApplicativo tipoErroreApplicativo, boolean omitXMLDeclaration,
			CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
				MessaggioDiErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				return XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo);
			}
			else{
				Element element = this._buildErroreApplicativo_Element(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				return this.xmlUtils.toString(element, omitXMLDeclaration);
			}
		
		}catch(Exception e){
			throw new ProtocolException("toString failed: "+e.getMessage());
		}
	}
	
	@Override
	protected byte[] _buildErroreApplicativo_ByteArray(TipoErroreApplicativo tipoErroreApplicativo, boolean omitXMLDeclaration,
			CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		try{
			if(TipoErroreApplicativo.JSON.equals(tipoErroreApplicativo)){
				MessaggioDiErroreApplicativo erroreApplicativo = this._buildErroreApplicativo_engine(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				return XMLUtils.generateErroreApplicativoAsJson(erroreApplicativo).getBytes();
			}
			else{
				Element element = this._buildErroreApplicativo_Element(codeDetailsErrorWrapper, eccezioneProtocollo, eccezioneIntegrazione);
				return this.xmlUtils.toByteArray(element, omitXMLDeclaration);
			}
		
		}catch(Exception e){
			throw new ProtocolException("toByteArray failed: "+e.getMessage());
		}
	}
	
	private MessaggioDiErroreApplicativo _buildErroreApplicativo_engine(CodeDetailsError codeDetailsErrorWrapper,
			EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		try{
			ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
			
			MessaggioDiErroreApplicativo erroreApplicativo = new MessaggioDiErroreApplicativo();
			
			String idPorta = null;
			String idFunzione = null;
			String codiceEccezione = null;
			String descrizioneEccezione = null;
			Date oraRegistrazione = null;
			
			IntegrationFunctionError functionError = null;
			boolean genericDetails = true;
			
			if(eccezioneProtocollo!=null){
				idPorta = eccezioneProtocollo.getDominioPorta().getCodicePorta();
				idFunzione = eccezioneProtocollo.getIdFunzione();
				codiceEccezione = super.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
						eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
				descrizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.protocolFactory);
				oraRegistrazione = eccezioneProtocollo.getOraRegistrazione();
				
				functionError = eccezioneProtocollo.getFunctionError();
				if(eccezioneProtocollo.getReturnConfig()!=null) {
					genericDetails = eccezioneProtocollo.getReturnConfig().isGenericDetails();
				}
				
				codeDetailsErrorWrapper.setPrefixCode(org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL);
				codeDetailsErrorWrapper.setCode(codiceEccezione);
				
				codeDetailsErrorWrapper.setDetails(descrizioneEccezione);
				
			}else{
				idPorta = eccezioneIntegrazione.getDominioPorta().getCodicePorta();
				idFunzione = eccezioneIntegrazione.getIdFunzione();
				codiceEccezione = this.traduttore.toCodiceErroreIntegrazioneAsString(eccezioneIntegrazione.getErroreIntegrazione(),
						eccezioneIntegrazione.getProprieta().getFaultPrefixCode(),
						eccezioneIntegrazione.getProprieta().isFaultAsGenericCode());
				descrizioneEccezione = eccezioneIntegrazione.getProprieta().transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.protocolFactory);
				oraRegistrazione = eccezioneIntegrazione.getOraRegistrazione();
				
				functionError = eccezioneIntegrazione.getFunctionError();
				if(eccezioneIntegrazione.getReturnConfig()!=null) {
					genericDetails = eccezioneIntegrazione.getReturnConfig().isGenericDetails();
				}
				
				codeDetailsErrorWrapper.setPrefixCode(org.openspcoop2.protocol.basic.Costanti.PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION);
				codeDetailsErrorWrapper.setCode(codiceEccezione);
				
				codeDetailsErrorWrapper.setDetails(descrizioneEccezione);
			}
			
			if(!genericDetails && erroriProperties.isForceGenericDetails(functionError)) {
				genericDetails = true;
			}
			if (Costanti.TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS) {
				genericDetails = false;
			}
			
			if(oraRegistrazione==null){
				oraRegistrazione = DateManager.getDate();
			}
			erroreApplicativo.setOraRegistrazione(oraRegistrazione);
			
			erroreApplicativo.setIdentificativoPorta(idPorta);
			
			erroreApplicativo.setIdentificativoFunzione(idFunzione);
			
			it.cnipa.schemas._2003.egovit.exception1_0.Eccezione eccezione = new it.cnipa.schemas._2003.egovit.exception1_0.Eccezione();
			if(eccezioneProtocollo!=null){
				EccezioneBusta eccezioneBusta = new EccezioneBusta();
				
				//if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO) {
				// Il pacchetto CNIPA prevede un codice egov sempre
				eccezioneBusta.setCodiceEccezione(codiceEccezione);
				/*}
				else {
					String govwayType = erroriProperties.getErrorType(functionError);
					eccezioneBusta.setCodiceEccezione(govwayType);
				}*/
				
				if(!genericDetails) {
					eccezioneBusta.setDescrizioneEccezione(descrizioneEccezione);
				}
				else {
					eccezioneBusta.setDescrizioneEccezione(erroriProperties.getGenericDetails(functionError));
				}
				
				eccezione.setEccezioneBusta(eccezioneBusta);
			}
			else{
				EccezioneProcessamento eccezioneProcessamento = new EccezioneProcessamento();
				
				if(Costanti.TRANSACTION_ERROR_STATUS_ABILITATO) {
					eccezioneProcessamento.setCodiceEccezione(codiceEccezione);
				}
				else {
					String govwayType = erroriProperties.getErrorType(functionError);
					eccezioneProcessamento.setCodiceEccezione(govwayType);
				}
				
				if(!genericDetails) {
					eccezioneProcessamento.setDescrizioneEccezione(descrizioneEccezione);
				}
				else {
					eccezioneProcessamento.setDescrizioneEccezione(erroriProperties.getGenericDetails(functionError));
				}
				
				eccezione.setEccezioneProcessamento(eccezioneProcessamento);
			}
			erroreApplicativo.setEccezione(eccezione);
			
			return erroreApplicativo;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	// PARSER
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(TipoErroreApplicativo tipoErroreApplicativo, byte[] erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		MessaggioDiErroreApplicativo erroreApplicativoObject = null;
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
	
	private AbstractEccezioneBuilderParameter _parseErroreApplicativo(MessaggioDiErroreApplicativo erroreApplicativo,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
			
			String identificativoPorta = erroreApplicativo.getIdentificativoPorta();
			String identificativoFunzione = erroreApplicativo.getIdentificativoFunzione();
			
			AbstractEccezioneBuilderParameter eccezione = null;
			if( erroreApplicativo.getEccezione().getEccezioneBusta()!=null ){
				eccezione = new EccezioneProtocolloBuilderParameters();
				
				CodiceErroreCooperazione codice = this.protocolFactory.createTraduttore().toCodiceErroreCooperazione(erroreApplicativo.getEccezione().getEccezioneBusta().getCodiceEccezione());
				ErroreCooperazione erroreCooperazione = new ErroreCooperazione(erroreApplicativo.getEccezione().getEccezioneBusta().getDescrizioneEccezione(), codice);
				org.openspcoop2.protocol.sdk.Eccezione eccezioneProtocollo = 
						new org.openspcoop2.protocol.sdk.Eccezione(erroreCooperazione,true,identificativoFunzione,this.protocolFactory);
				((EccezioneProtocolloBuilderParameters)eccezione).setEccezioneProtocollo(eccezioneProtocollo);
			}
			else{
				eccezione = new EccezioneIntegrazioneBuilderParameters();
				CodiceErroreIntegrazione codice = this.protocolFactory.createTraduttore().toCodiceErroreIntegrazione(erroreApplicativo.getEccezione().getEccezioneProcessamento().getCodiceEccezione(),prefixCodiceErroreApplicativoIntegrazione);
				ErroreIntegrazione erroreIntegrazione = new ErroreIntegrazione(erroreApplicativo.getEccezione().getEccezioneProcessamento().getDescrizioneEccezione(), codice);
				((EccezioneIntegrazioneBuilderParameters)eccezione).setErroreIntegrazione(erroreIntegrazione);
			}
			
			IDSoggetto dominio = new IDSoggetto();
			dominio.setCodicePorta(identificativoPorta);
			eccezione.setDominioPorta(dominio);
			eccezione.setOraRegistrazione(erroreApplicativo.getOraRegistrazione());
			eccezione.setIdFunzione(identificativoFunzione);
			
			return eccezione;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	


}