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

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.basic.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopErroreApplicativoBuilder extends ErroreApplicativoBuilder implements org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder {

	
	public SPCoopErroreApplicativoBuilder(IProtocolFactory factory) throws ProtocolException{
		super(factory);
	}

	@Override
	public boolean isErroreApplicativo(String namespace, String localName){
		if("MessaggioDiErroreApplicativo".equals(localName) && 
				SPCoopCostanti.NAMESPACE_ECCEZIONE_APPLICATIVA_EGOV.equals(namespace) 
			){
			return true;
		}
		return false;
	}
	
	@Override
	public String getNamespaceEccezioneProtocollo(){
		return SPCoopCostanti.NAMESPACE_EGOV;
	}
	
	@Override
	protected SOAPElement buildErroreApplicativoElement_engine(EccezioneProtocolloBuilderParameters eccezioneProtocollo,
			EccezioneIntegrazioneBuilderParameters eccezioneIntegrazione)throws ProtocolException{
		try{
			
			String idPorta = null;
			String idFunzione = null;
			String tipo = null;
			String codiceEccezione = null;
			String descrizioneEccezione = null;
			if(eccezioneProtocollo!=null){
				idPorta = eccezioneProtocollo.getDominioPorta().getCodicePorta();
				idFunzione = eccezioneProtocollo.getIdFunzione();
				tipo = SPCoopCostanti.ECCEZIONE_VALIDAZIONE_BUSTA_SPCOOP;
				codiceEccezione = super.traduttore.toString(eccezioneProtocollo.getEccezioneProtocollo().getCodiceEccezione(),
						eccezioneProtocollo.getEccezioneProtocollo().getSubCodiceEccezione());
				descrizioneEccezione = eccezioneProtocollo.getEccezioneProtocollo().getDescrizione(this.factory);
			}else{
				idPorta = eccezioneIntegrazione.getDominioPorta().getCodicePorta();
				idFunzione = eccezioneIntegrazione.getIdFunzione();
				tipo = SPCoopCostanti.ECCEZIONE_PROCESSAMENTO_SPCOOP;
				codiceEccezione = this.traduttore.toString(eccezioneIntegrazione.getErroreIntegrazione().getCodiceErrore(),
						eccezioneIntegrazione.getProprieta().getFaultPrefixCode(),
						eccezioneIntegrazione.getProprieta().isFaultAsGenericCode());
				descrizioneEccezione = eccezioneIntegrazione.getProprieta().transformFaultMsg(eccezioneIntegrazione.getErroreIntegrazione(),this.factory);
			}
			
			Document doc = super.xmlUtils.newDocument();
			Element eccezione = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:MessaggioDiErroreApplicativo");
			
			Element oraRec = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:OraRegistrazione");
			oraRec.setTextContent(SPCoopUtils.getDate_eGovFormat());
			eccezione.appendChild(oraRec);

			Element identificativoPorta = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:IdentificativoPorta");
			identificativoPorta.setTextContent(idPorta);
			eccezione.appendChild(identificativoPorta);
			
			Element identificativoFunzione = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:IdentificativoFunzione");
			identificativoFunzione.setTextContent(idFunzione);
			eccezione.appendChild(identificativoFunzione);

			Element EccNode = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:Eccezione");
			eccezione.appendChild(EccNode);

			Element EccNodeInterno = doc.createElementNS("http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/", "eGov_IT_Ecc:"+tipo);
			EccNodeInterno.setAttribute("codiceEccezione",codiceEccezione);
			EccNodeInterno.setAttribute("descrizioneEccezione",descrizioneEccezione);
			EccNode.appendChild(EccNodeInterno);

			// Aggiunta per rispettare l'interfaccia.
			
			OpenSPCoop2Message responseSOAPMessageError = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11);
			SOAPBody soapBody = responseSOAPMessageError.getSOAPBody();
			soapBody.appendChild(soapBody.getOwnerDocument().importNode(eccezione, true));
			return (SOAPElement) soapBody.getFirstChild();

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("toElement failed: "+e.getMessage(),e);
		}
	}
	
	
	
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(byte[] xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
			Node nXml = this.xmlUtils.newDocument(xml);
			return readErroreApplicativo(nXml,prefixCodiceErroreApplicativoIntegrazione);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}
	
	@Override
	public AbstractEccezioneBuilderParameter readErroreApplicativo(Node xml,String prefixCodiceErroreApplicativoIntegrazione) throws ProtocolException{
		try{
			
			org.w3c.dom.NodeList ndList = xml.getChildNodes();
			String oraRegistrazione = null;
			String identificativoPorta = null;
			String identificativoFunzione = null;
			boolean eccezioneBusta = false;
			String codiceEccezione = null;
			String descrizioneEccezione = null;
			for(int i=0; i<ndList.getLength(); i++){
				//System.out.println("NOME:"+ndList.item(i).getNodeName()+" VALORE:"+ndList.item(i).getTextContent());
				if(ndList.item(i).getNodeName().endsWith("OraRegistrazione"))
					oraRegistrazione = ((org.w3c.dom.Element) ndList.item(i)).getFirstChild().getTextContent();
				else if(ndList.item(i).getNodeName().endsWith("IdentificativoPorta"))
					identificativoPorta = ((org.w3c.dom.Element) ndList.item(i)).getFirstChild().getTextContent();
				else if(ndList.item(i).getNodeName().endsWith("IdentificativoFunzione"))
					identificativoFunzione = ((org.w3c.dom.Element) ndList.item(i)).getFirstChild().getTextContent();
				else if(ndList.item(i).getNodeName().endsWith("Eccezione")){
					org.w3c.dom.NodeList ndEcc = ndList.item(i).getChildNodes();
					if(ndEcc.item(0).getNodeName().endsWith("EccezioneBusta")){
						eccezioneBusta = true;
					}else if(ndEcc.item(0).getNodeName().endsWith("EccezioneProcessamento")){
						eccezioneBusta = false;
					}
					org.w3c.dom.NamedNodeMap mapAtt = ndEcc.item(0).getAttributes();
					codiceEccezione = mapAtt.getNamedItem("codiceEccezione").getNodeValue();
					descrizioneEccezione = mapAtt.getNamedItem("descrizioneEccezione").getNodeValue();
				}		
			}

			if(oraRegistrazione==null)
				throw new ProtocolException("OraRegistrazione non definita?");
			if(identificativoFunzione==null)
				throw new ProtocolException("IdentificativoFunzione non definito?");
			if(identificativoPorta==null)
				throw new ProtocolException("IdentificativoPorta non definito?");
			if(codiceEccezione==null)
				throw new ProtocolException("CodiceEccezione non definita?");
			if(descrizioneEccezione==null)
				throw new ProtocolException("DescrizioneEccezione non definita?");
			
			
			AbstractEccezioneBuilderParameter eccezione = null;
			if( eccezioneBusta ){
				eccezione = new EccezioneProtocolloBuilderParameters();
				
				CodiceErroreCooperazione codice = this.factory.createTraduttore().toCodiceErroreCooperazione(codiceEccezione);
				ErroreCooperazione erroreCooperazione = new ErroreCooperazione(descrizioneEccezione, codice);
				org.openspcoop2.protocol.sdk.Eccezione eccezioneProtocollo = 
						new org.openspcoop2.protocol.sdk.Eccezione(erroreCooperazione,true,identificativoFunzione,this.factory);
				((EccezioneProtocolloBuilderParameters)eccezione).setEccezioneProtocollo(eccezioneProtocollo);
			}
			else{
				eccezione = new EccezioneIntegrazioneBuilderParameters();
				CodiceErroreIntegrazione codice = this.factory.createTraduttore().toCodiceErroreIntegrazione(codiceEccezione,prefixCodiceErroreApplicativoIntegrazione);
				ErroreIntegrazione erroreIntegrazione = new ErroreIntegrazione(descrizioneEccezione, codice);
				((EccezioneIntegrazioneBuilderParameters)eccezione).setErroreIntegrazione(erroreIntegrazione);
			}
			
			IDSoggetto dominio = new IDSoggetto();
			dominio.setCodicePorta(identificativoPorta);
			eccezione.setDominioPorta(dominio);
			eccezione.setOraRegistrazione(SPCoopUtils.getDate_eGovFormat(oraRegistrazione));
			eccezione.setIdFunzione(identificativoFunzione);
			
			return eccezione;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(), e);
		}
	}

}