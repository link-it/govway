/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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



package org.openspcoop2.testsuite.units.utils;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contiene utility per i test effettuati.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoUtilities {

	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS;
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS = OpenSPCoopDetailsUtilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS;
	

	
	public static boolean existsErroreApplicativo(AxisFault error){
		Element [] details = error.getFaultDetails();
		return existsErroreApplicativo(details);
	}
	
	public static boolean existsErroreApplicativo(Element [] details){
		// XML Applicativo
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma ill'errore applicativo
				boolean result = org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.isErroreApplicativo(detail);
				if(result==false){
					String debugDetail = null;
					try{
						debugDetail = org.openspcoop2.utils.xml.XMLUtils.getInstance().toString(detail);
					}catch(Exception e){}
					System.out.println("Detail["+detail.getLocalName()+"] non e' un ErroreApplicativo ["+debugDetail+"]");
					Reporter.log("Detail["+detail.getLocalName()+"] non e' un ErroreApplicativo ["+debugDetail+"]");
				}
				return result;
			}
		}
		
		return false;
	}
	
	public static boolean existsErroreApplicativo(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma ill'errore applicativo
				return org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.isErroreApplicativo(detail);
			}
		}
		
		return false;
	}
	
	public static Node getErroreApplicativo(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma ill'errore applicativo
				if(org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.isErroreApplicativo(detail)){
					return detail;
				}
			}
		}
		
		return null;
	}
	
	public static void verificaFaultErroreApplicativo(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			CodiceErroreIntegrazione codiceErroreIntegrazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		String[] identificativiFunzioneAttesi = new String[1];
		identificativiFunzioneAttesi[0] = identificativoModuloAtteso;
		verificaFaultErroreApplicativo(getErroreApplicativo(error), dominioAtteso, tipoPdDAtteso, identificativiFunzioneAttesi, 
				null, codiceErroreIntegrazione, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	public static void verificaFaultErroreApplicativo(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			CodiceErroreCooperazione codiceErroreCooperazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		String[] identificativiFunzioneAttesi = new String[1];
		identificativiFunzioneAttesi[0] = identificativoModuloAtteso;
		verificaFaultErroreApplicativo(getErroreApplicativo(error), dominioAtteso, tipoPdDAtteso, identificativiFunzioneAttesi, 
				codiceErroreCooperazione, null, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	
	public static void verificaFaultErroreApplicativo(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			CodiceErroreIntegrazione codiceErroreIntegrazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaFaultErroreApplicativo(getErroreApplicativo(error), dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				null, codiceErroreIntegrazione, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	public static void verificaFaultErroreApplicativo(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			CodiceErroreCooperazione codiceErroreCooperazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaFaultErroreApplicativo(getErroreApplicativo(error), dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				codiceErroreCooperazione, null, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	
	private static Node getErroreApplicativo(AxisFault error){
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element dettaglioOpenSPCoop = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma ill'errore applicativo
				if(org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils.isErroreApplicativo(detail)){
					dettaglioOpenSPCoop = detail;
				}
				break;
			}
		}
		return dettaglioOpenSPCoop;
	}
	
	public static void verificaFaultErroreApplicativo(Node erroreApplicativoNode,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			CodiceErroreIntegrazione codiceErroreIntegrazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaFaultErroreApplicativo(erroreApplicativoNode, dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				null, codiceErroreIntegrazione, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	public static void verificaFaultErroreApplicativo(Node erroreApplicativoNode,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			CodiceErroreCooperazione codiceErroreCooperazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		verificaFaultErroreApplicativo(erroreApplicativoNode, dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				codiceErroreCooperazione, null, descrizione, checkDescrizioneTramiteMatchEsatto);
	}
	private static void verificaFaultErroreApplicativo(Node erroreApplicativoNode,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			CodiceErroreCooperazione codiceErroreCooperazione, CodiceErroreIntegrazione codiceErroreIntegrazione, String descrizione, boolean checkDescrizioneTramiteMatchEsatto) throws Exception{
		
		String xml = null;
		
		try{

			Assert.assertTrue(erroreApplicativoNode!=null);
			xml = XMLUtils.getInstance().toString(erroreApplicativoNode);
			Reporter.log("Dettaglio OpenSPCoop ("+erroreApplicativoNode.getNamespaceURI()+"): "+xml);
			Assert.assertTrue(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(erroreApplicativoNode.getNamespaceURI()));
			
			Reporter.log("Validazione xsd");
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(ErroreApplicativoUtilities.class),
					xsdResourceResolver,ErroreApplicativoUtilities.class.getResourceAsStream("/openspcoopErroreApplicativo.xsd"));
			validatoreXSD.valida(erroreApplicativoNode);
			
			Reporter.log("Navigazione...");
			Assert.assertTrue(erroreApplicativoNode.hasChildNodes());
			NodeList listDettaglioOpenSPCoop = erroreApplicativoNode.getChildNodes();
			Assert.assertTrue(listDettaglioOpenSPCoop!=null);
			Assert.assertTrue(listDettaglioOpenSPCoop.getLength()>0);
			boolean dominioOk = false;
			boolean dominioSoggettoOk = false;
			boolean dominioSoggettoTipoOk = false;
			boolean dominioSoggettoNomeOk = false;
			boolean oraRegistrazioneOk = false;
			boolean identificativoPortaOk = false;
			boolean identificativoFunzioneOk = false;
			boolean identificativoModuloOk = false;
			boolean eccezioneOk = false;
			for(int i=0; i<listDettaglioOpenSPCoop.getLength(); i++){
				Node n = listDettaglioOpenSPCoop.item(i);
				Reporter.log("ErroreApplicativo, elemento ["+n.getLocalName()+"] ["+n.getNamespaceURI()+"]");
				if("timestamp".equals(n.getLocalName())){
					if(oraRegistrazioneOk){
						throw new Exception("Elemento timestamp presente piu' di una volta all'interno dell'errore applicativo");
					}
					oraRegistrazioneOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					String ora = n.getTextContent();
					Assert.assertTrue(ora!=null);
					Reporter.log("ora: "+ora);
				}
				else if("domain".equals(n.getLocalName())){
					if(dominioOk){
						throw new Exception("Elemento domain presente piu' di una volta all'interno dell'errore applicativo");
					}
					dominioOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					
					Assert.assertTrue(n.hasChildNodes());
					
					NamedNodeMap attributi = n.getAttributes();
					Assert.assertTrue(attributi!=null);
					Assert.assertTrue(attributi.getLength()==2);
					
					// modulo
					Attr modulo = (Attr) attributi.getNamedItem("module");
					Assert.assertTrue(modulo!=null);
					if(identificativoModuloOk){
						throw new Exception("Attributo module presente piu' di una volta all'interno del fault.domain di OpenSPCoop");
					}
					identificativoModuloOk = true;
					String idModuloValue = modulo.getTextContent();
					boolean match=false;
					for(int h=0;h<identificativoModuloAtteso.length;h++){
						Reporter.log("Controllo module presente["+idModuloValue+"] atteso("+h+")["+identificativoModuloAtteso[h]+"]");
						if(idModuloValue.equals(identificativoModuloAtteso[h])){ 
							match = true;
							break;
						}
					}
					Assert.assertTrue(match);
					
					// funzione
					Attr funzione = (Attr) attributi.getNamedItem("role");
					Assert.assertTrue(funzione!=null);
					if(identificativoFunzioneOk){
						throw new Exception("Attributo role presente piu' di una volta all'interno del fault.domain di OpenSPCoop");
					}
					identificativoFunzioneOk = true;
					String idFunzioneValue = funzione.getTextContent();
					String identificativoFunzioneAtteso = null;
					if(tipoPdDAtteso.equals(TipoPdD.DELEGATA)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.OUTBOUND_PROXY.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.APPLICATIVA)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INBOUND_PROXY.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.INTEGRATION_MANAGER)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.INTEGRATION_MANAGER.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.ROUTER)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoPdD.ROUTER.getValue();
					}
					Reporter.log("Controllo role presente["+idFunzioneValue+"] atteso["+identificativoFunzioneAtteso+"]");
					Assert.assertTrue(idFunzioneValue.equals(identificativoFunzioneAtteso));
					
					// child-node
					NodeList listDominio = n.getChildNodes();
					Assert.assertTrue(listDominio!=null);
					Assert.assertTrue(listDominio.getLength()==2);
					for(int j=0; j<listDominio.getLength(); j++){
						Node nDominio = listDominio.item(j);
						Reporter.log("DettaglioOpenSPCoop.dominio, elemento ["+nDominio.getLocalName()+"] ["+nDominio.getNamespaceURI()+"]");
						
						if("id".equals(nDominio.getLocalName())){
							if(identificativoPortaOk){
								throw new Exception("Elemento id presente piu' di una volta all'interno del fault.domain di OpenSPCoop");
							}
							identificativoPortaOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							String idPortaValue = nDominio.getTextContent();
							String identificativoPortaAtteso = dominioAtteso.getCodicePorta();
							Reporter.log("Controllo id presente["+idPortaValue+"] atteso["+identificativoPortaAtteso+"]");
							Assert.assertTrue(idPortaValue.equals(identificativoPortaAtteso));
						}
						
						if("organization".equals(nDominio.getLocalName())){
							if(dominioSoggettoOk){
								throw new Exception("Elemento organization presente piu' di una volta all'interno del fault.domain di OpenSPCoop");
							}
							dominioSoggettoOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							
							// tipo
							NamedNodeMap attributiSoggetto = nDominio.getAttributes();
							Assert.assertTrue(attributiSoggetto!=null);
							Assert.assertTrue(attributiSoggetto.getLength()==1);
							Attr tipoSoggetto = (Attr) attributiSoggetto.getNamedItem("type");
							Assert.assertTrue(tipoSoggetto!=null);
							if(dominioSoggettoTipoOk){
								throw new Exception("Attributo tipo presente piu' di una volta all'interno del fault.domain.organization di OpenSPCoop");
							}
							dominioSoggettoTipoOk = true;
							String tipoValue = tipoSoggetto.getTextContent();
							String tipoAtteso = dominioAtteso.getTipo();
							Reporter.log("Controllo type presente["+tipoValue+"] atteso["+tipoAtteso+"]");
							Assert.assertTrue(tipoValue.equals(tipoAtteso));
							
							// nome
							if(dominioSoggettoNomeOk){
								throw new Exception("Elemento organization presente piu' di una volta all'interno del fault.domain di OpenSPCoop");
							}
							dominioSoggettoNomeOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							String nomeValue = nDominio.getTextContent();
							String nomeAtteso = dominioAtteso.getNome();
							Reporter.log("Controllo nome presente["+nomeValue+"] atteso["+nomeAtteso+"]");
							Assert.assertTrue(nomeValue.equals(nomeAtteso));
						}
					}
					
				}
				else if("service".equals(n.getLocalName())){
					// per ora non sono implementati controlli
				}
				else if("exception".equals(n.getLocalName())){
					eccezioneOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					Assert.assertTrue(n.hasChildNodes()==true);
					NodeList listTipoEccezione = n.getChildNodes();
					Assert.assertTrue(listTipoEccezione!=null);
					Assert.assertTrue(listTipoEccezione.getLength()>0);
					
					// Controllo attributi				
					Assert.assertTrue(n.hasChildNodes());
					NamedNodeMap attributi = n.getAttributes();
					Assert.assertTrue(attributi!=null);
					Assert.assertTrue(attributi.getLength()==1);
					
					Attr tipoEccezione = (Attr) attributi.getNamedItem("type");
					Assert.assertTrue(tipoEccezione!=null);
					String valoreTipoEccezione = tipoEccezione.getTextContent();
					Assert.assertTrue(valoreTipoEccezione!=null);
					if(codiceErroreIntegrazione!=null) {
						Reporter.log("Controllo tipo eccezione presente["+valoreTipoEccezione+"] atteso-integrazione["+org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione.INTEGRATION+"]");
						Assert.assertTrue(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione.INTEGRATION.equals(valoreTipoEccezione));
					}
					else {
						Reporter.log("Controllo tipo eccezione presente["+valoreTipoEccezione+"] atteso-integrazione["+org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione.PROTOCOL+"]");
						Assert.assertTrue(org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione.PROTOCOL.equals(valoreTipoEccezione));
					}
					
					
					boolean findCodice = false;
					boolean findDescrizione = false;
					for(int j=0; j<listTipoEccezione.getLength(); j++){
						Node tmp = listTipoEccezione.item(j);
						Reporter.log("Eccezione, ["+tmp.getLocalName()+"]");
						
						if("code".equals(tmp.getLocalName())){
							
							if(findCodice){
								throw new Exception("Elemento code presente piu' di una volta all'interno del fault.exception di OpenSPCoop");
							}
							
							findCodice = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(tmp.getNamespaceURI()) );
							
							// Controllo attributi				
							Assert.assertTrue(tmp.hasChildNodes());
							NamedNodeMap attributiCodice = tmp.getAttributes();
							Assert.assertTrue(attributiCodice!=null);
							Assert.assertTrue(attributiCodice.getLength()==1);
							
							Attr tipoCodice = (Attr) attributiCodice.getNamedItem("type");
							Assert.assertTrue(tipoCodice!=null);
							String valoreTipoCodice = tipoCodice.getTextContent();
							Assert.assertTrue(valoreTipoCodice!=null);
							if(codiceErroreIntegrazione!=null) {
								Reporter.log("Controllo code presente["+valoreTipoCodice+"] atteso-integrazione["+codiceErroreIntegrazione.getCodice()+"]");
								Assert.assertTrue(valoreTipoCodice.equals(codiceErroreIntegrazione.getCodice()+""));
							}
							else {
								Reporter.log("Controllo code presente["+valoreTipoCodice+"] atteso-cooperazione["+codiceErroreCooperazione.getCodice()+"]");
								Assert.assertTrue(valoreTipoCodice.equals(codiceErroreCooperazione.getCodice()+""));
							}
							
							String value = tmp.getTextContent();
							String atteso = null;
							if(codiceErroreIntegrazione!=null) {
								atteso = toString(codiceErroreIntegrazione);
							}
							else {
								atteso = toString(codiceErroreCooperazione);
							}
							Reporter.log("Controllo codice descrittivo presente["+value+"] atteso["+atteso+"]");
							Assert.assertTrue(value.equals(atteso));
						}
						else if("description".equals(tmp.getLocalName())){
							
							if(findDescrizione){
								throw new Exception("Elemento description presente piu' di una volta all'interno del fault.exception di OpenSPCoop");
							}
							
							findDescrizione = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE.equals(tmp.getNamespaceURI()) );
							
							String value = tmp.getTextContent();
							String atteso = descrizione;
							Reporter.log("Controllo description presente["+value+"] atteso["+atteso+"] checkDescrizioneTramiteMatchEsatto["+checkDescrizioneTramiteMatchEsatto+"]");
							if(checkDescrizioneTramiteMatchEsatto) {
								Assert.assertTrue(value.equals(atteso));
							}
							else {
								Assert.assertTrue(value.contains(atteso));
							}
						}

					}
					Assert.assertTrue(findCodice);
					Assert.assertTrue(findDescrizione);
				
				}
				else if(n.getLocalName()!=null){
					throw new Exception("Elemento ["+n.getLocalName()+"] presente all'interno dell'errore applicativo non atteso");
				}
			}
			Assert.assertTrue(dominioOk);
			Assert.assertTrue(dominioSoggettoOk);
			Assert.assertTrue(dominioSoggettoTipoOk);
			Assert.assertTrue(dominioSoggettoNomeOk);
			Assert.assertTrue(oraRegistrazioneOk);
			Assert.assertTrue(identificativoPortaOk);
			Assert.assertTrue(identificativoModuloOk);
			Assert.assertTrue(identificativoFunzioneOk);
			Assert.assertTrue(eccezioneOk);
		}catch(Exception e){
			if(xml!=null){
				System.out.println("----------------------");
				System.out.println(xml);
				System.out.println(e.getMessage());
			}
			throw e;
		}
	}
	
	public static String toString(CodiceErroreIntegrazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+codiceErrore.getCodice();
	}
	public static String toString(CodiceErroreCooperazione codiceErrore) throws ProtocolException{
		return org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE+codiceErrore.getCodice();
	}
}


