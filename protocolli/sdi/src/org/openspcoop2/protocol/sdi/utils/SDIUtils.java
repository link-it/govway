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
package org.openspcoop2.protocol.sdi.utils;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.mtom.MtomXomReference;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdi.validator.SDIValidatoreNomeFile;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.utils.IDSerialGenerator;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorParameter;
import org.openspcoop2.utils.id.serial.IDSerialGeneratorType;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SDIUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIUtils {

	public static SOAPElement readHeader(OpenSPCoop2Message msg) throws ProtocolException{

		List<MtomXomReference> xomReference = null;
		try{
			SOAPBody soapBody = msg.getSOAPBody();
			if(soapBody==null || soapBody.hasChildNodes()==false){
				return null;
			}
			if(soapBody.hasFault()){
				return null;
			}
			Element element = SoapUtils.getNotEmptyFirstChildSOAPElement(soapBody);
			
			// Il codice sottostante imposta un valore 'ContenutoBase64' al posto del vero contenuto Base64

			// **** Servizio 'RicezioneFatture', operazione 'RiceviFatture' ******
			if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ROOT_ELEMENT.equals(element.getLocalName()) &&
					SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE.equals(element.getNamespaceURI())){
			
				xomReference = msg.mtomFastUnpackagingForXSDConformance();
				element = (Element)element.cloneNode(true);
				
				Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(element, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
						child.setTextContent("ContenutoBase64");
					}
					else if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI.equals(child.getLocalName())){
						child.setTextContent("ContenutoBase64");
					}
				}
			}
			
			// **** Servizio 'RicezioneFatture', operazione 'NotificaDecorrenzaTermini' ******
			else if(SDICostantiServizioRicezioneFatture.NOTIFICA_DECORRENZA_TERMINI_RICHIESTA_ROOT_ELEMENT.equals(element.getLocalName()) &&
					SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE.equals(element.getNamespaceURI())){
			
				xomReference = msg.mtomFastUnpackagingForXSDConformance();
				element = (Element)element.cloneNode(true);
				
				Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(element, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
						child.setTextContent("ContenutoBase64");
					}
				}
			}
			
			
			// **** Servizio 'RiceviNotifica', operazione 'NotificaEsito', !!richiesta!! ******
			else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT.equals(element.getLocalName()) &&
					SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE.equals(element.getNamespaceURI())){
			
				xomReference = msg.mtomFastUnpackagingForXSDConformance();
				element = (Element)element.cloneNode(true);
				
				Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(element, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
						child.setTextContent("ContenutoBase64");
					}
				}
			}
			// **** Servizio 'RiceviNotifica', operazione 'NotificaEsito', !!risposta!! (se presento lo scarto dell'esito) ******
			else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ROOT_ELEMENT.equals(element.getLocalName()) &&
					SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE.equals(element.getNamespaceURI())){
			
				xomReference = msg.mtomFastUnpackagingForXSDConformance();
				element = (Element)element.cloneNode(true);
				
				Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(element, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO.equals(child.getLocalName())){
						Vector<Node> childsScarto = SoapUtils.getNotEmptyChildNodes(child, false);
						for (int j = 0; j < childsScarto.size(); j++) {
							Node childScarto = childsScarto.get(j);
							if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE.equals(childScarto.getLocalName())){
								childScarto.setTextContent("ContenutoBase64");
							}
						}
					}
				}
			}
			
			// **** 
			//  Servizio 'TrasmissioneFatture', operazione '*' (tutte e 6)
			//  Servizio 'RiceviFile', operazione 'RiceviFile'  (Possiedono lo stesso namespace e la stessa struttura)
			// ******
			else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_NAMESPACE.equals(element.getNamespaceURI())){
			
				xomReference = msg.mtomFastUnpackagingForXSDConformance();
				element = (Element)element.cloneNode(true);
				
				Vector<Node> childs = SoapUtils.getNotEmptyChildNodes(element, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
						child.setTextContent("ContenutoBase64");
					}
				}
			}
			
			return SoapUtils.getSoapFactory(msg.getVersioneSoap()).createElement(element);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				msg.mtomRestoreAfterXSDConformance(xomReference);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
	}

	
	public static String getNomeFileMessaggi(IProtocolFactory factory, IState state, String nomeFileFattura,TipiMessaggi tipo) throws ProtocolException{
		// <NomeFilaFatturaRicevutoSenzaEstensione>_<TipoMessaggio>_<ProgressivoUnivoco>
		
		// Progressivo fileName: è relativo al file inviato
		// *  - Il Progressivo univoco deve essere una stringa alfanumerica di lunghezza massima 3 caratteri e 
		// *  con valori ammessi [a-z], [A-Z], [0-9] che identifica univocamente ogni notifica / ricevuta relativa al file inviato.
		
		ConfigurazionePdD config = factory.getConfigurazionePdD();
		
		IDSerialGenerator serialGenerator = new IDSerialGenerator(config.getLog(), state, config.getTipoDatabase());
		
		IDSerialGeneratorParameter serialGeneratorParameter = new IDSerialGeneratorParameter(factory.getProtocol());
		serialGeneratorParameter.setSerializableTimeWaitMs(config.getAttesaAttivaJDBC());
		serialGeneratorParameter.setSerializableNextIntervalTimeMs(config.getCheckIntervalJDBC());
		serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
		serialGeneratorParameter.setWrap(false);
		serialGeneratorParameter.setSize(3);
		serialGeneratorParameter.setInformazioneAssociataAlProgressivo(SDIValidatoreNomeFile.getNomeFileFatturaSenzaEstensione(nomeFileFattura));
		
		String ext = "."+SDICostanti.SDI_FATTURA_ESTENSIONE_XML;
		
		return SDIValidatoreNomeFile.getNomeFileFatturaPerMessaggi(nomeFileFattura)+"_"+tipo.name()+"_"+serialGenerator.buildID(serialGeneratorParameter)+ext;
	}
	
	public static String getNomeFileFattura(IProtocolFactory factory, IState state, String idPaeseTrasmittente, String idCodiceTrasmittente, String formatoInvio) throws ProtocolException{
		
		// <codice Paese>< identificativo univoco del soggetto trasmittente >_<progressivoUnicoFile>
		
		// *  Progressivo fileName: è relativo al trasmittente
		// *  - il progressivo univoco del file è rappresentato da una stringa alfanumerica di lunghezza massima di 5 caratteri e con valori ammessi [a-z], [A-Z], [0-9].
		
		if(idPaeseTrasmittente==null){
			throw new ProtocolException("IdPaeseTrasmittente non fornito (richiesto per la creazione del nome di file)");
		}
		if(idCodiceTrasmittente==null){
			throw new ProtocolException("IdCodiceTrasmittente non fornito (richiesto per la creazione del nome di file)");
		}
		String idPaeseIdCodice = idPaeseTrasmittente+idCodiceTrasmittente;
		
		ConfigurazionePdD config = factory.getConfigurazionePdD();
		
		IDSerialGenerator serialGenerator = new IDSerialGenerator(config.getLog(),state, config.getTipoDatabase());
		
		IDSerialGeneratorParameter serialGeneratorParameter = new IDSerialGeneratorParameter(factory.getProtocol());
		serialGeneratorParameter.setSerializableTimeWaitMs(config.getAttesaAttivaJDBC());
		serialGeneratorParameter.setSerializableNextIntervalTimeMs(config.getCheckIntervalJDBC());
		serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
		serialGeneratorParameter.setWrap(false);
		serialGeneratorParameter.setSize(5);
		serialGeneratorParameter.setInformazioneAssociataAlProgressivo(idPaeseIdCodice);
		
		String ext = "."+SDICostanti.SDI_FATTURA_ESTENSIONE_XML;
		if(SDICostanti.SDI_TIPO_FATTURA_ZIP.equals(formatoInvio)){
			ext = "."+SDICostanti.SDI_FATTURA_ESTENSIONE_ZIP;
		}else if(SDICostanti.SDI_TIPO_FATTURA_P7M.equals(formatoInvio)){
			ext = "."+SDICostanti.SDI_FATTURA_ESTENSIONE_P7M;
		}
		
		return idPaeseIdCodice+"_"+serialGenerator.buildID(serialGeneratorParameter)+ext;
	}
	
	public static List<byte[]> splitLotto(byte[] lotto) throws Exception {
		
		List<byte[]> risultato = new ArrayList<byte[]>();
		
		AbstractXMLUtils xmlUtils = XMLUtils.getInstance();
		XPathExpressionEngine xpathEngine = new XPathExpressionEngine();
		
		Element lottoElement = null;
		DynamicNamespaceContext dnc = null;
		try{
			lottoElement = xmlUtils.newElement(lotto);
			dnc = new DynamicNamespaceContext();
			dnc.findPrefixNamespace(lottoElement);
		}catch(Exception e){
			throw new Exception("Lettura lotto di fattura non riuscita: "+e.getMessage(),e);
		}
		
		NodeList nl = null;
		try{
			nl = (NodeList) xpathEngine.getMatchPattern(lottoElement, dnc, "//FatturaElettronicaBody", XPathReturnType.NODESET);
		}catch(XPathNotFoundException notFound){
			throw new Exception("La fattura non contiene alcun body?: "+notFound.getMessage(),notFound);
		}catch(Exception e){
			throw new Exception("Estrazione body dal lotto di fattura non riuscita: "+e.getMessage(),e);
		}
		if(nl==null){
			throw new Exception("La fattura non contiene alcun body? (null)");
		}
		if(nl.getLength()<1){
			throw new Exception("La fattura non contiene alcun body? (size): "+nl.getLength());
		}
		
		if(nl.getLength()==1){
			// non si tratta di un lotto, ma di una fattura con un body solamente
			risultato.add(lotto);
			return risultato;
		}
		
		List<Node> listBody = new ArrayList<Node>();
		try{
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				listBody.add(n);
				lottoElement.removeChild(n);
			}
		}catch(Exception e){
			throw new Exception("Elaborazione lotto di fattura (step1) non riuscita: "+e.getMessage(),e);
		}
		int i = 0;
		try{
			for (; i < listBody.size(); i++) {
				Node n = listBody.get(i);
				lottoElement.appendChild(n);
				risultato.add(xmlUtils.toByteArray(lottoElement));
				lottoElement.removeChild(n);
			}
		}catch(Exception e){
			throw new Exception("Elaborazione lotto di fattura (step2-"+i+") non riuscita: "+e.getMessage(),e);
		}
		
		return risultato;
	}
	
}
