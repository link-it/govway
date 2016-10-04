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

import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
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
import org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils;
import org.openspcoop2.core.eccezione.router_details.Dettaglio;
import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
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
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoBuilder implements org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder {

	protected Logger log;
	protected IProtocolFactory factory;
	protected ITraduttore traduttore;
	protected OpenSPCoop2MessageFactory msgFactory = null;
	protected org.openspcoop2.message.XMLUtils xmlUtils;
	
	public ErroreApplicativoBuilder(IProtocolFactory factory) throws ProtocolException{
		this.log = factory.getLogger();
		this.factory = factory;
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		this.traduttore = factory.createTraduttore();
		this.msgFactory = OpenSPCoop2MessageFactory.getMessageFactory();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}

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
	
	
	@Override
	public OpenSPCoop2Message toMessage(
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return newMessaggioErroreApplicativo_engine(parameters, null);
	}

	@Override
	public OpenSPCoop2Message toMessage(
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return newMessaggioErroreApplicativo_engine(null,parameters);
	}

	
	
	@Override
	public SOAPElement toElement(
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoElement_engine(parameters, null);
	}

	@Override
	public SOAPElement toElement(
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoElement_engine(null,parameters);
	}

	
	@Override
	public String toString(
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoString_engine(parameters, null);
	}

	@Override
	public String toString(
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoString_engine(null,parameters);
	}
	
	
	@Override
	public byte[] toByteArray(
			EccezioneProtocolloBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoByteArray_engine(parameters, null);
	}

	@Override
	public byte[] toByteArray(
			EccezioneIntegrazioneBuilderParameters parameters)
			throws ProtocolException {
		return this.buildErroreApplicativoByteArray_engine(null,parameters);
	}

	
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(String xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		return readErroreApplicativo(xml.getBytes(),prefixCodiceErroreApplicativoIntegrazione);
	}
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(byte[] xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
			if(XMLUtils.isErroreApplicativo(xml)==false){
				throw new ProtocolException("XML fornito non contiene un errore applicativo per il protocollo "+this.getProtocolFactory().getProtocol());
			}
			ErroreApplicativo erroreApplicativo = XMLUtils.getErroreApplicativo(this.log, xml);
			AbstractEccezioneBuilderParameter eccezione = null;
			if( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_ECCEZIONE_PROTOCOLLO.equals(erroreApplicativo.getEccezione().getTipo())){
				eccezione = new EccezioneProtocolloBuilderParameters();
				
				CodiceErroreCooperazione codice = CodiceErroreCooperazione.toCodiceErroreCooperazione(erroreApplicativo.getEccezione().getCodice().getTipo().intValue());
				String descrizione = erroreApplicativo.getEccezione().getCodice().getBase();
				ErroreCooperazione erroreCooperazione = new ErroreCooperazione(descrizione, codice);
				org.openspcoop2.protocol.sdk.Eccezione eccezioneProtocollo = 
						new org.openspcoop2.protocol.sdk.Eccezione(erroreCooperazione,true,erroreApplicativo.getDominio().getFunzione(),this.factory);
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
			if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_PORTA_DELEGATA.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.DELEGATA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_PORTA_APPLICATIVA.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.APPLICATIVA);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_INTEGRATION_MANAGER.equals(erroreApplicativo.getDominio().getFunzione())){
				eccezione.setTipoPorta(TipoPdD.INTEGRATION_MANAGER);
			}
			else if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_ROUTER.equals(erroreApplicativo.getDominio().getFunzione())){
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
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(Node xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
			byte[] xmlBytes = this.xmlUtils.toByteArray(xml, true);
			return readErroreApplicativo(xmlBytes,prefixCodiceErroreApplicativoIntegrazione);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	
	
	
	
	@Override
	public void insertInSOAPFault(
			EccezioneProtocolloBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException {
		this.insertErroreApplicativoIntoSOAPFault_engine(
				this.buildErroreApplicativoElement_engine(parameters, null), msg);
	}

	@Override
	public void insertInSOAPFault(
			EccezioneIntegrazioneBuilderParameters parameters,
			OpenSPCoop2Message msg) throws ProtocolException {
		this.insertErroreApplicativoIntoSOAPFault_engine(
				this.buildErroreApplicativoElement_engine(null,parameters), msg);
	}

	
	

	@Override
	public void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg) throws ProtocolException{
		
		try{
			if(msg==null)
				throw new ProtocolException("Messaggio non presente");
			
			DettaglioRouting dettaglioRouting = new DettaglioRouting();
			
			
			// dominio
			org.openspcoop2.core.eccezione.router_details.Dominio dominio = new org.openspcoop2.core.eccezione.router_details.Dominio();
			org.openspcoop2.core.eccezione.router_details.DominioSoggetto dominioSoggetto = new org.openspcoop2.core.eccezione.router_details.DominioSoggetto();
			dominioSoggetto.setTipo(identitaRouter.getTipo());
			dominioSoggetto.setBase(identitaRouter.getNome());
			dominio.setSoggetto(dominioSoggetto);
			dominio.setIdentificativoPorta(identitaRouter.getCodicePorta());
			dominio.setModulo(idFunzione);
			dettaglioRouting.setDominio(dominio);
			
			// oraRegistrazione
			dettaglioRouting.setOraRegistrazione(DateManager.getDate());
			
			// dettaglio
			Dettaglio dettaglio = new Dettaglio();
			dettaglio.setDescrizione(msgErrore);
			dettaglio.setEsito(org.openspcoop2.core.eccezione.router_details.constants.Costanti.ESITO_ERRORE);
			dettaglioRouting.setDettaglio(dettaglio);
			
			byte[]xmlDettaglioRouting = org.openspcoop2.core.eccezione.router_details.utils.XMLUtils.generateDettaglioRouting(dettaglioRouting);
			Element elementDettaglioRouting = this.xmlUtils.newElement(xmlDettaglioRouting);
			addPrefixToElement(elementDettaglioRouting,"op2RoutingDetail");
			
			SOAPFactory sf = SoapUtils.getSoapFactory(msg.getVersioneSoap());
			SOAPElement dettaglioRoutingElementSOAP =  sf.createElement(elementDettaglioRouting);
			
			this.insertErroreApplicativoIntoSOAPFault_engine(dettaglioRoutingElementSOAP, msg);
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di errore",e);
			throw new ProtocolException("Errore durante la costruzione del messaggio di errore",e);
		}
	}
	
	
	
	/** UTILITIES */
	
	protected SOAPElement buildErroreApplicativoElement_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
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
			BigInteger codiceEccezioneOpenSPCoop = null;
			BigInteger subCodiceEccezioneOpenSPCoop = null;
			String descrizioneEccezione = null;
			String tipoEccezione = null;
			Date oraRegistrazione = null;
			
			if(eccezioneProtocollo!=null){
				idDominio = eccezioneProtocollo.getDominioPorta();
				idModulo = eccezioneProtocollo.getIdFunzione();
				tipoPdD = eccezioneProtocollo.getTipoPorta();
				fruitore = eccezioneProtocollo.getMittente();
				servizio = eccezioneProtocollo.getServizio();
				servizioApplicativo = eccezioneProtocollo.getServizioApplicativo();
				codiceEccezione = this.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
						eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
				codiceEccezioneOpenSPCoop = new BigInteger(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione().getCodice()+"");
				if(eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione()!=null){
					subCodiceEccezioneOpenSPCoop = new BigInteger(eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione().getSubCodice()+"");
				}
				descrizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory);
				tipoEccezione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_ECCEZIONE_PROTOCOLLO;
				oraRegistrazione = eccezioneProtocollo.getOraRegistrazione();
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
				codiceEccezioneOpenSPCoop = new BigInteger(eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore().getCodice()+"");
				descrizioneEccezione = eccezioneIntegrazione.getProprieta().transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.factory);
				tipoEccezione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_ECCEZIONE_INTEGRAZIONE;
				oraRegistrazione = eccezioneIntegrazione.getOraRegistrazione();
			}
			
			String idFunzione = null;
			if(TipoPdD.DELEGATA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_PORTA_DELEGATA;
			}
			else if(TipoPdD.APPLICATIVA.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_PORTA_APPLICATIVA;
			}
			else if(TipoPdD.INTEGRATION_MANAGER.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_INTEGRATION_MANAGER;
			}
			else if(TipoPdD.ROUTER.equals(tipoPdD)){
				idFunzione = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_PDD_ROUTER;
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
					if(servizio.getVersioneServizio()!=null){
						servizioErroreApplicativo.setVersione(new BigInteger(servizio.getVersioneServizio()));
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
			codice.setSottotipo(subCodiceEccezioneOpenSPCoop);
			eccezione.setCodice(codice);
			eccezione.setDescrizione(descrizioneEccezione);
			eccezione.setTipo(tipoEccezione);
			erroreApplicativo.setEccezione(eccezione);
			
			// Aggiunta per rispettare l'interfaccia.
			
//			OpenSPCoop2Message responseSOAPMessageError = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11);
//			SOAPBody soapBody = responseSOAPMessageError.getSOAPBody();
//			soapBody.appendChild(soapBody.getOwnerDocument().importNode(eccezione, true));
//			return (SOAPElement) soapBody.getFirstChild();
			
			byte[]xmlErroreApplicativo = org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.generateErroreApplicativo(erroreApplicativo);
			Element elementErroreApplicativo = this.xmlUtils.newElement(xmlErroreApplicativo);
			addPrefixToElement(elementErroreApplicativo,"op2ErrAppl");
			
			SOAPFactory sf = SoapUtils.getSoapFactory(SOAPVersion.SOAP11);
			SOAPElement erroreApplicativoElementSOAP =  sf.createElement(elementErroreApplicativo);
			
			return erroreApplicativoElementSOAP;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("buildErroreApplicativoElement failed: "+e.getMessage(),e);
		}
	}
		
	private String buildErroreApplicativoString_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		SOAPElement element = this.buildErroreApplicativoElement_engine(eccezioneProtocollo, eccezioneIntegrazione);
		if(element==null){
			throw new ProtocolException("Element non generato");
		}
		try{
			String xml = this.xmlUtils.toString(element, true);
			if(xml==null){
				throw new Exception("Trasformazione in stringa non riuscita");
			}
			return xml;
		}catch(Exception e){
			throw new ProtocolException("toString failed: "+e.getMessage());
		}
	}
	
	private byte[] buildErroreApplicativoByteArray_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		
		SOAPElement element = this.buildErroreApplicativoElement_engine(eccezioneProtocollo, eccezioneIntegrazione);
		if(element==null){
			throw new ProtocolException("Element non generato");
		}
		try{
			byte[] xml = this.xmlUtils.toByteArray(element, true);
			if(xml==null){
				throw new Exception("Trasformazione in byte[] non riuscita");
			}
			return xml;
		}catch(Exception e){
			throw new ProtocolException("toString failed: "+e.getMessage());
		}
	}

	private OpenSPCoop2Message newMessaggioErroreApplicativo_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		try{

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
			SOAPElement rispostaApplicativaElement = this.buildErroreApplicativoElement_engine(eccezioneProtocollo, eccezioneIntegrazione);
			//String xmlRispostaApplicativa = Utilities.toString(this.log, rispostaApplicativaElement); // elimina prefissi axis
			//System.out.println("xml ["+xmlRispostaApplicativa+"]");
			SOAPVersion soapVersion = null;
			if(eccezioneIntegrazione!=null){
				soapVersion = eccezioneIntegrazione.getVersioneSoap();
			}else{
				soapVersion = eccezioneProtocollo.getVersioneSoap();
			}
			OpenSPCoop2Message responseSOAPMessageError = this.msgFactory.createMessage(soapVersion);
			SOAPBody soapBody = responseSOAPMessageError.getSOAPBody();
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
				responseSOAPMessageError.setFaultCode(fault, code, eccezioneName);
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
				responseSOAPMessageError.setParseException(eccezioneProtocollo.getParseException());
			}
			else if(eccezioneIntegrazione!=null){
				responseSOAPMessageError.setParseException(eccezioneIntegrazione.getParseException());
			}
			
			return responseSOAPMessageError;

		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di errore applicativo",e);
			return this.msgFactory.createFaultMessage(SOAPVersion.SOAP11,"ErroreDiProcessamento");
		}
	}
	
	
	private void insertErroreApplicativoIntoSOAPFault_engine(SOAPElement erroreApplicativo,
			OpenSPCoop2Message msg) throws ProtocolException{
		try{
			if(msg==null)
				throw new ProtocolException("Messaggio non presente");
			SOAPBody soapBody = msg.getSOAPBody();
			if(soapBody==null)
				throw new ProtocolException("SOAPBody non presente");
			SOAPFault faultOriginale = null;
			if(soapBody.hasFault()==false)
				throw new ProtocolException("SOAPFault non presente");
			else
				faultOriginale = soapBody.getFault();
			if(faultOriginale==null)
				throw new ProtocolException("SOAPFault is null");
			
			SOAPElement eccezioneDetailApplicativo = msg.cleanXSITypes(erroreApplicativo);
			
			QName nameDetail = null;
			if(msg.getVersioneSoap()!=null && SOAPVersion.SOAP12.equals(msg.getVersioneSoap())){
				nameDetail = new QName(org.openspcoop2.message.Costanti.SOAP12_ENVELOPE_NAMESPACE,"Detail");
			}
			else{
				nameDetail = new QName("detail");
			}
			SOAPElement detailsFaultOriginale = null;
			Iterator<?> itDetailsOriginali = faultOriginale.getChildElements(nameDetail);
			if(itDetailsOriginali!=null && itDetailsOriginali.hasNext()){
				detailsFaultOriginale = (SOAPElement) itDetailsOriginali.next();
			}
					
			String faultActor = faultOriginale.getFaultActor(); // in soap1.2 e' il role
			Name faultCode = faultOriginale.getFaultCodeAsName();
			Iterator<?> faultSubCode = null;
			String faultNode = null;
			if(msg.getVersioneSoap()!=null && SOAPVersion.SOAP12.equals(msg.getVersioneSoap())){
				faultSubCode = faultOriginale.getFaultSubcodes();
				faultNode = faultOriginale.getFaultNode();
			}
			String faultString = faultOriginale.getFaultString();
			
			
			msg.getSOAPBody().removeChild(msg.getSOAPBody().getFault());
			
			//msg.saveChanges();
			
			SOAPFault faultPulito = msg.getSOAPBody().addFault();
			if(faultActor != null)
				faultPulito.setFaultActor(faultActor);
			if(faultCode!=null)
				faultPulito.setFaultCode(faultCode);
			if(faultSubCode!=null){
				while (faultSubCode.hasNext()) {
					QName faultSubCodeQname = (QName) faultSubCode.next();
					faultPulito.appendFaultSubcode(faultSubCodeQname);
				}
			}
			if(faultNode!=null){
				faultPulito.setFaultNode(faultNode);
			}
			if(faultString!=null)
				faultPulito.setFaultString(faultString);
			Detail detailFaultPulito = faultPulito.addDetail();
			detailFaultPulito = faultPulito.getDetail();
		
			if(detailsFaultOriginale!=null){
	            Iterator<?> it = detailsFaultOriginale.getChildElements();
				while (it.hasNext()) {
					Object o = it.next();
					if(o instanceof SOAPElement){
						SOAPElement elem = (SOAPElement) o;
						detailFaultPulito.addChildElement(elem);
					}
				}
			}
			detailFaultPulito.addChildElement(eccezioneDetailApplicativo);
		
            msg.saveChanges();
			
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di errore applicativo (InsertDetail)",e);
			throw new ProtocolException("Errore durante la costruzione del messaggio di errore (InsertDetail)",e);
		}
	}
	
	private void addPrefixToElement(Element elementErroreApplicativo,String prefix){
		//workAround per ovviare poi al problema axiom: NAMESPACE_ERR: An attempt is made to create or change an object in a way which is incorrect with regard to namespaces.
		// che si verifica in detailFaultPulito.addChildElement(eccezioneDetailApplicativo) del metodo insertErroreApplicativoIntoSOAPFault_engine di questa classe
		//elementErroreApplicativo.setPrefix(prefix); 
	}
}