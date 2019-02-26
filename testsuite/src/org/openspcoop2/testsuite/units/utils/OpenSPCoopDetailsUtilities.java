/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.List;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.message.xml.XMLUtils;
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

public class OpenSPCoopDetailsUtilities {

	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS = true;
	public static final boolean CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS = false;
	

	
	public static boolean existsOpenSPCoopDetails(AxisFault error){
		Element [] details = error.getFaultDetails();
		return existsOpenSPCoopDetails(details);
	}
	
	public static boolean existsOpenSPCoopDetails(Element [] details){
		// XML Applicativo
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault-details".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				boolean result = org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail);
				if(result==false){
					String debugDetail = null;
					try{
						debugDetail = org.openspcoop2.utils.xml.XMLUtils.getInstance().toString(detail);
					}catch(Exception e){}
					System.out.println("Detail["+detail.getLocalName()+"] non e' un DettagliEccezione ["+debugDetail+"]");
					Reporter.log("Detail["+detail.getLocalName()+"] non e' un DettagliEccezione ["+debugDetail+"]");
				}
				return result;
			}
		}
		
		return false;
	}
	
	public static boolean existsOpenSPCoopDetails(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault-details".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				return org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail);
			}
		}
		
		return false;
	}
	
	public static Node getOpenSPCoopDetails(Element xml){
		// XML Applicativo
		Assert.assertTrue(xml!=null);
		Assert.assertTrue(xml.getChildNodes()!=null);
		Assert.assertTrue(xml.getChildNodes().getLength()>0);
		Reporter.log("Details presenti: "+xml.getChildNodes().getLength());
		for(int i=0; i<xml.getChildNodes().getLength(); i++){
			Node detail = xml.getChildNodes().item(i);
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault-details".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				if(org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail)){
					return detail;
				}
			}
		}
		
		return null;
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli) throws Exception{
		verificaFaultOpenSPCoopDetail(error, dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, eccezioni, dettagli, true);
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		String[] identificativiFunzioneAttesi = new String[1];
		identificativiFunzioneAttesi[0] = identificativoModuloAtteso;
		verificaFaultOpenSPCoopDetail(getDettaglioOpenSPCoop(error), dominioAtteso, tipoPdDAtteso, identificativiFunzioneAttesi, 
				eccezioni, dettagli, verificatutto);
	}
	
	public static void verificaFaultOpenSPCoopDetail(AxisFault error,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		verificaFaultOpenSPCoopDetail(getDettaglioOpenSPCoop(error), dominioAtteso, tipoPdDAtteso, identificativoModuloAtteso, 
				eccezioni, dettagli, verificatutto);
	}
	
	private static Node getDettaglioOpenSPCoop(AxisFault error){
		// XML Applicativo
		Element [] details = error.getFaultDetails();
		Assert.assertTrue(details!=null);
		Assert.assertTrue(details.length>0);
		Element dettaglioOpenSPCoop = null;
		Reporter.log("Details presenti: "+details.length);
		for(int i=0; i<details.length; i++){
			Element detail = details[i];
			Reporter.log("Detail["+detail.getLocalName()+"]");
			if("fault-details".equals(detail.getLocalName())){
				// Details, contiene l'xml che forma il dettaglio di OpenSPCoop
				if(org.openspcoop2.core.eccezione.details.utils.XMLUtils.isDettaglioEccezione(detail)){
					dettaglioOpenSPCoop = detail;
				}
				break;
			}
		}
		return dettaglioOpenSPCoop;
	}
	
	public static void verificaFaultOpenSPCoopDetail(Node dettaglioOpenSPCoop,IDSoggetto dominioAtteso,TipoPdD tipoPdDAtteso,String[] identificativoModuloAtteso,
			List<OpenSPCoopDetail> eccezioni,List<OpenSPCoopDetail> dettagli, boolean verificatutto) throws Exception{
		
		String xml = null;
		
		try{

			Assert.assertTrue(dettaglioOpenSPCoop!=null);
			xml = XMLUtils.getInstance().toString(dettaglioOpenSPCoop);
			Reporter.log("Dettaglio OpenSPCoop ("+dettaglioOpenSPCoop.getNamespaceURI()+"): "+xml);
			Assert.assertTrue(org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(dettaglioOpenSPCoop.getNamespaceURI()));
			
			Reporter.log("Validazione xsd");
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(LoggerWrapperFactory.getLogger(OpenSPCoopDetailsUtilities.class),
					xsdResourceResolver,OpenSPCoopDetailsUtilities.class.getResourceAsStream("/openspcoopDetail.xsd"));
			validatoreXSD.valida(dettaglioOpenSPCoop);
			
			Reporter.log("Navigazione...");
			Assert.assertTrue(dettaglioOpenSPCoop.hasChildNodes());
			NodeList listDettaglioOpenSPCoop = dettaglioOpenSPCoop.getChildNodes();
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
				Reporter.log("DettaglioOpenSPCoop, elemento ["+n.getLocalName()+"] ["+n.getNamespaceURI()+"]");
				if("timestamp".equals(n.getLocalName())){
					if(oraRegistrazioneOk){
						throw new Exception("Elemento timestamp presente piu' di una volta all'interno del dettaglio di OpenSPCoop");
					}
					oraRegistrazioneOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					String ora = n.getTextContent();
					Assert.assertTrue(ora!=null);
					Reporter.log("ora: "+ora);
				}else if("domain".equals(n.getLocalName())){
					if(dominioOk){
						throw new Exception("Elemento domain presente piu' di una volta all'interno del dettaglio di OpenSPCoop");
					}
					dominioOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					
					Assert.assertTrue(n.hasChildNodes());
					
					NamedNodeMap attributi = n.getAttributes();
					Assert.assertTrue(attributi!=null);
					Assert.assertTrue(attributi.getLength()==2);
					
					// modulo
					Attr modulo = (Attr) attributi.getNamedItem("module");
					Assert.assertTrue(modulo!=null);
					if(identificativoModuloOk){
						throw new Exception("Attributo module presente piu' di una volta all'interno del fault-details.domain di OpenSPCoop");
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
						throw new Exception("Attributo role presente piu' di una volta all'interno del fault-details.domain di OpenSPCoop");
					}
					identificativoFunzioneOk = true;
					String idFunzioneValue = funzione.getTextContent();
					String identificativoFunzioneAtteso = null;
					if(tipoPdDAtteso.equals(TipoPdD.DELEGATA)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.details.constants.TipoPdD.OUTBOUND_PROXY.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.APPLICATIVA)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.details.constants.TipoPdD.INBOUND_PROXY.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.INTEGRATION_MANAGER)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.details.constants.TipoPdD.INTEGRATION_MANAGER.getValue();
					}
					else if(tipoPdDAtteso.equals(TipoPdD.ROUTER)){
						identificativoFunzioneAtteso = org.openspcoop2.core.eccezione.details.constants.TipoPdD.ROUTER.getValue();
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
								throw new Exception("Elemento id presente piu' di una volta all'interno del fault-details.domain di OpenSPCoop");
							}
							identificativoPortaOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							String idPortaValue = nDominio.getTextContent();
							String identificativoPortaAtteso = dominioAtteso.getCodicePorta();
							Reporter.log("Controllo identificativoPorta presente["+idPortaValue+"] atteso["+identificativoPortaAtteso+"]");
							Assert.assertTrue(idPortaValue.equals(identificativoPortaAtteso));
						}
						
						if("organization".equals(nDominio.getLocalName())){
							if(dominioSoggettoOk){
								throw new Exception("Elemento organization presente piu' di una volta all'interno del fault-details.domain di OpenSPCoop");
							}
							dominioSoggettoOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							
							// type
							NamedNodeMap attributiSoggetto = nDominio.getAttributes();
							Assert.assertTrue(attributiSoggetto!=null);
							Assert.assertTrue(attributiSoggetto.getLength()==1);
							Attr tipoSoggetto = (Attr) attributiSoggetto.getNamedItem("type");
							Assert.assertTrue(tipoSoggetto!=null);
							if(dominioSoggettoTipoOk){
								throw new Exception("Attributo type presente piu' di una volta all'interno del fault-details.domain.organization di OpenSPCoop");
							}
							dominioSoggettoTipoOk = true;
							String tipoValue = tipoSoggetto.getTextContent();
							String tipoAtteso = dominioAtteso.getTipo();
							Reporter.log("Controllo tipo presente["+tipoValue+"] atteso["+tipoAtteso+"]");
							Assert.assertTrue(tipoValue.equals(tipoAtteso));
							
							// nome
							if(dominioSoggettoNomeOk){
								throw new Exception("Elemento soggetto presente piu' di una volta all'interno del fault-details.domain di OpenSPCoop");
							}
							dominioSoggettoNomeOk = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(nDominio.getNamespaceURI()) );
							String nomeValue = nDominio.getTextContent();
							String nomeAtteso = dominioAtteso.getNome();
							Reporter.log("Controllo nome presente["+nomeValue+"] atteso["+nomeAtteso+"]");
							Assert.assertTrue(nomeValue.equals(nomeAtteso));
						}
					}
					
				}
				else if("exceptions".equals(n.getLocalName())){
					eccezioneOk = true;
					Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					Assert.assertTrue(n.hasChildNodes()==true);
					NodeList listTipoEccezione = n.getChildNodes();
					Assert.assertTrue(listTipoEccezione!=null);
					Assert.assertTrue(listTipoEccezione.getLength()>0);
					boolean findEccezione = false;
					for(int j=0; j<listTipoEccezione.getLength(); j++){
						Node tmp = listTipoEccezione.item(j);
						Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"]");
						if("exception".equals(tmp.getLocalName())){
							findEccezione = true;
							Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(tmp.getNamespaceURI()) );
						}
						
						// Controllo attributi				
						Assert.assertTrue(tmp.hasChildNodes()==false);
						NamedNodeMap attributi = tmp.getAttributes();
						Assert.assertTrue(attributi!=null);
						Assert.assertTrue(attributi.getLength()==3 || attributi.getLength()==5);
						
						Attr codiceEccezione = (Attr) attributi.getNamedItem("code");
						Assert.assertTrue(codiceEccezione!=null);
						String namespaceCodiceEccezione = codiceEccezione.getNamespaceURI();
						String valoreCodiceEccezione = codiceEccezione.getTextContent();
						
						Attr descrizioneEccezione = (Attr) attributi.getNamedItem("description");
						Assert.assertTrue(descrizioneEccezione!=null);
						String valoreDescrizioneEccezione = descrizioneEccezione.getTextContent();
						
						Attr tipoEccezione = (Attr) attributi.getNamedItem("type");
						Assert.assertTrue(tipoEccezione!=null);
						String valoreTipoEccezione = tipoEccezione.getTextContent();
						
						// Check eccezione attesa
						boolean findEccezioneAttesa = false;
						
						Reporter.log("Verifico code["+valoreCodiceEccezione+"] description["+valoreDescrizioneEccezione+"] type["+valoreTipoEccezione+"]");
						
						for (int k = 0; k < eccezioni.size(); k++) {
														
							Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"] Controllo presenza code["+eccezioni.get(k).getCodice()
									+"] description["+eccezioni.get(k).getDescrizione()+"] matchEsatto["+eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()+"] ...");
							
							// check codice
							if(eccezioni.get(k).getCodice().equals(valoreCodiceEccezione)){
								
								// check tipo
								boolean okTipo = false;
								// Non invertire altrimenti non funziona poi per i codici personalizzabili sull'integrazione
								if(
										valoreCodiceEccezione.startsWith("EGOV_IT_") // SPCoopCostanti.ECCEZIONE_PREFIX_CODE) non e' possibile usare la classe per non aggiungere una dipendenza di compilazione 
										|| 
										(valoreCodiceEccezione.startsWith(org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_PREFIX_CODE) && 
												org.openspcoop2.protocol.basic.Costanti.ERRORE_PROTOCOLLO_NAMESPACE.equals(namespaceCodiceEccezione))
									){
									if(org.openspcoop2.core.eccezione.details.constants.TipoEccezione.PROTOCOL.equals(valoreTipoEccezione)){
										okTipo = true;
									}
								}
								else{
									if(org.openspcoop2.core.eccezione.details.constants.TipoEccezione.INTEGRATION.equals(valoreTipoEccezione)){
										okTipo = true;
									}
								}
								
								if(okTipo){
								// check descrizione
									if(eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()){
										if(eccezioni.get(k).getDescrizione().equals(valoreDescrizioneEccezione)){
											findEccezioneAttesa = true;
										}
									} else if(valoreDescrizioneEccezione.contains(eccezioni.get(k).getDescrizione())){
										findEccezioneAttesa = true;
									}
								}
								
							}
							
							if(findEccezioneAttesa){
								Reporter.log("Eccezioni, eccezione ["+tmp.getLocalName()+"] Controllo presenza code["+eccezioni.get(k).getCodice()
										+"] description["+eccezioni.get(k).getDescrizione()+"] matchEsatto["+eccezioni.get(k).isCheckDescrizioneTramiteMatchEsatto()+"] FIND");
								eccezioni.remove(k);
								break;
							}
							
						}
						if(!findEccezioneAttesa){
							Reporter.log("Eccezione ("+valoreTipoEccezione+")("+valoreCodiceEccezione+")("+valoreDescrizioneEccezione+") non trovato tra quelli attesi");
						}
						Assert.assertTrue(findEccezioneAttesa);
					}
					Assert.assertTrue(findEccezione);
				
					// Verifico che tutte le eccezioni attese siano state riscontrate
					if(verificatutto) Assert.assertTrue( eccezioni.size() == 0);
				}
				else if("details".equals(n.getLocalName())){
					Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(n.getNamespaceURI()) );
					if(dettagli.size()>0){
						Assert.assertTrue(n.hasChildNodes()==true);
						NodeList listDettagli = n.getChildNodes();
						Assert.assertTrue(listDettagli!=null);
						Assert.assertTrue(listDettagli.getLength()>0);
						boolean findDettaglio = false;
						for(int j=0; j<listDettagli.getLength(); j++){
							Node tmp = listDettagli.item(j);
							Reporter.log("Dettagli, dettaglio ["+tmp.getLocalName()+"]");
							if("detail".equals(tmp.getLocalName())){
								findDettaglio = true;
								Assert.assertTrue( org.openspcoop2.core.eccezione.details.constants.Costanti.TARGET_NAMESPACE.equals(tmp.getNamespaceURI()) );
							}
							
							// Controllo attributi				
							Assert.assertTrue(tmp.hasChildNodes());
							NamedNodeMap attributi = tmp.getAttributes();
							Assert.assertTrue(attributi!=null);
							Assert.assertTrue(attributi.getLength()==1);
							
							Attr tipoDettaglio = (Attr) attributi.getNamedItem("type");
							Assert.assertTrue(tipoDettaglio!=null);
							String valoreTipo = tipoDettaglio.getTextContent();
							
							String valoreDettaglio = tmp.getTextContent();
							Assert.assertTrue(valoreDettaglio!=null);
							
							// Check eccezione attesa
							boolean findDettaglioAtteso = false;
							for (int k = 0; k < dettagli.size(); k++) {
														
								if(dettagli.get(k).getCodice().equals(valoreTipo)){
									if(dettagli.get(k).isCheckDescrizioneTramiteMatchEsatto()){
										if(dettagli.get(k).getDescrizione().equals(valoreDettaglio)){
											findDettaglioAtteso = true;
										}else if(valoreDettaglio.contains(dettagli.get(k).getDescrizione())){
											findDettaglioAtteso = true;
										}
									}
								}
								
								if(findDettaglioAtteso){
									dettagli.remove(k);
									break;
								}
								
							}
							if(!findDettaglioAtteso){
								Reporter.log("Detail ("+valoreTipo+")("+valoreDettaglio+") non trovato tra quelli attesi");
							}
							Assert.assertTrue(findDettaglioAtteso);
						}
						Assert.assertTrue(findDettaglio);
					
						// Verifico che tutte le dettagli attese siano state riscontrate
						Assert.assertTrue( dettagli.size() == 0);
					}
				}
				else if(n.getLocalName()!=null){
					throw new Exception("Elemento ["+n.getLocalName()+"] presente all'interno del dettaglio di OpenSPCoop non atteso");
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
	
	
}


