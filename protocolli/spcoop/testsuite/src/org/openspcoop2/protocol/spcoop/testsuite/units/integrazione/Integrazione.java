/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.integrazione;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.List;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Test sulle informazioni di integrazione
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Integrazione extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "Integrazione";
	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	protected Integrazione() {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	
	//private static final String MSG_ERRORE = "Riscontrato errore durante l'identificazione del servizio associato alla porta delegata, utilizzando il pattern specificato nella configurazione";
	//private static final String MSG_ERRORE_SERVIZIO="Servizio richiesto con la porta delegata non trovato nel Registro dei Servizi";
	private static final String MSG_ERRORE_IDENTIFICAZIONE_AZIONE="Identificazione dinamica dell'azione associata alla porta delegata fallita";
	private static final String MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2="Identificazione dinamica dell'operazione fallita";
	//private static final String MSG_ERRORE_IDENTIFICAZIONE_SERVIZIO="Riscontrato errore durante l'identificazione del servizio associato alla porta delegata, utilizzando il pattern specificato nella configurazione";
	private static final String MSG_ERRORE_SERVIZIO_ERRATO = "Azione richiesta non corretta: (azione:@AZIONE@) Azione '@AZIONE@' non trovata nell'API @ACCORDO_SERVIZIO@";
	
	
	public static void checkHttpRisposta(CooperazioneBase collaborazioneSPCoopBase,Properties risposta,String tipoServizio,String servizio,String azione,String idEGov)throws Exception{
		if(risposta==null){
			throw new Exception("Risposta is null");
		}
		if(risposta.size()<=0){
			throw new Exception("Risposta, header http vuoto");
		}
		Enumeration<?> keys = risposta.keys();
		boolean findTipoMittente = false;
		boolean findMittente = false;
		boolean findTipoDestinatario = false;
		boolean findDestinatario = false;
		boolean findTipoServizio = false;
		boolean findServizio = false;
		boolean findVersioneServizio = false;
		boolean findAzione = (azione==null);
		boolean findID = false;
		boolean findIDTransazione = false;
		TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
		while(keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			String value = risposta.getProperty(key);
			Reporter.log("Elemento nell'header http key["+key+"] value["+value+"]");
			if(testsuiteProperties.getTipoMittenteTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(collaborazioneSPCoopBase.getMittente().getTipo())==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getMittente().getTipo()+"]");
				}
				findTipoMittente = true;
			}
			else if(testsuiteProperties.getMittenteTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(collaborazioneSPCoopBase.getMittente().getNome())==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getMittente().getNome()+"]");
				}
				findMittente = true;
			}
			if(testsuiteProperties.getTipoDestinatarioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(collaborazioneSPCoopBase.getDestinatario().getTipo())==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getDestinatario().getTipo()+"]");
				}
				findTipoDestinatario = true;
			}
			else if(testsuiteProperties.getDestinatarioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(collaborazioneSPCoopBase.getDestinatario().getNome())==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getDestinatario().getNome()+"]");
				}
				findDestinatario = true;
			}
			if(testsuiteProperties.getTipoServizioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(tipoServizio)==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+tipoServizio+"]");
				}
				findTipoServizio = true;
			}
			else if(testsuiteProperties.getNomeServizioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(servizio)==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+servizio+"]");
				}
				findServizio = true;
			}
			else if(testsuiteProperties.getVersioneServizioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals("1")==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+"1"+"]");
				}
				findVersioneServizio = true;
			}
			else if(testsuiteProperties.getAzioneTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(azione)==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+azione+"]");
				}
				findAzione = true;
			}
			else if(testsuiteProperties.getIdMessaggioTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if(value.equals(idEGov)==false){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"] diverso da quello atteso ["+idEGov+"]");
				}
				findID = true;
			}
			else if(testsuiteProperties.getIDTransazioneTrasporto().equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				findIDTransazione = true;
			}
		}
		if(findTipoMittente==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getTipoMittenteTrasporto()+" non trovato");
		}
		if(findMittente==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getMittenteTrasporto()+" non trovato");
		}
		if(findTipoDestinatario==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getTipoDestinatarioTrasporto()+" non trovato");
		}
		if(findDestinatario==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getDestinatarioTrasporto()+" non trovato");
		}
		if(findTipoServizio==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getTipoServizioTrasporto()+" non trovato");
		}
		if(findServizio==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getNomeServizioTrasporto()+" non trovato");
		}
		if(findVersioneServizio==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getVersioneServizioTrasporto()+" non trovato");
		}
		if(findAzione==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getAzioneTrasporto()+" non trovato");
		}
		if(findID==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getIdMessaggioTrasporto()+" non trovato");
		}
		if(findIDTransazione==false){
			throw new Exception("HTTP Header "+testsuiteProperties.getIDTransazioneTrasporto()+" non trovato");
		}

		checkHttpRispostaPddVersioneDetails(risposta);
	}
	
	public static void checkHttpRispostaPddVersioneDetails(Properties risposta)throws Exception{
		if(risposta==null){
			throw new Exception("Risposta is null");
		}
		if(risposta.size()<=0){
			throw new Exception("Risposta, header http vuoto");
		}
		Enumeration<?> keys = risposta.keys();
		boolean findProductVersion = false;
		boolean findProductDetails = false;
		while(keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			String value = risposta.getProperty(key);
			Reporter.log("Elemento nell'header http key["+key+"] value["+value+"]");
			if(CostantiPdD.HEADER_HTTP_X_PDD.equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if("".equals(value)){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"]");
				}
				findProductVersion = true;
			}
			else if(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS.equalsIgnoreCase(key)){
				if(value==null){
					throw new Exception("Attributo ["+key+"] con valore null");
				}
				if("".equals(value)){
					throw new Exception("Attributo ["+key+"] con valore ["+value+"]");
				}
				findProductDetails = true;
			}
		}
		if(findProductVersion==false){
			throw new Exception("HTTP Header "+CostantiPdD.HEADER_HTTP_X_PDD+" non trovato");
		}
		if(findProductDetails==false){
			throw new Exception("HTTP Header "+CostantiPdD.HEADER_HTTP_X_PDD_DETAILS+" non trovato");
		}
	}
	
	public static void checkMessaggioRisposta(CooperazioneBase collaborazioneSPCoopBase,Message risposta,String tipoServizio,String servizio,String azione,String idEGov)throws Exception{
		if(risposta==null){
			throw new Exception("Risposta is null");
		}
		if(risposta.getSOAPHeader()==null){
			throw new Exception("Risposta, soap header is null");
		}
		if(risposta.getSOAPHeader().hasChildNodes()==false){
			throw new Exception("Risposta, soap header vuoto");
		}
		Iterator<?> elements = risposta.getSOAPHeader().getChildElements();
		boolean find = false;
		boolean findTipoMittente = false;
		boolean findMittente = false;
		boolean findTipoDestinatario = false;
		boolean findDestinatario = false;
		boolean findTipoServizio = false;
		boolean findServizio = false;
		boolean findVersioneServizio = false;
		boolean findAzione = (azione==null);
		boolean findID = false;
		boolean findIDTransazione = false;
		boolean findProductVersion = false;
		boolean findProductDetails = false;
		TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
		while(elements.hasNext()){
			MessageElement elem = (MessageElement) elements.next();
			Reporter.log("Elemento nell'header name["+elem.getLocalName()+"] prefix["+elem.getPrefix()+"] namespace["+elem.getNamespaceURI()+"]");
			if(org.openspcoop2.core.integrazione.constants.Costanti.ROOT_LOCAL_NAME_INTEGRATION.equals(elem.getLocalName()) && "gw".equals(elem.getPrefix()) && org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE.equals(elem.getNamespaceURI())){
				find = true;
				Iterator<?> attributes = elem.getAllAttributes();
				while(attributes.hasNext()){
					org.apache.axis.message.PrefixedQName attr = (org.apache.axis.message.PrefixedQName) attributes.next();
					if("mustUnderstand".equals(attr.getLocalName())){
						continue;
					}else if("actor".equals(attr.getLocalName())){
						continue;
					}else if(testsuiteProperties.getTipoMittenteSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(collaborazioneSPCoopBase.getMittente().getTipo())==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getMittente().getTipo()+"]");
						}
						findTipoMittente = true;
					}else if(testsuiteProperties.getMittenteSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(collaborazioneSPCoopBase.getMittente().getNome())==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getMittente().getNome()+"]");
						}
						findMittente = true;
					}else if(testsuiteProperties.getTipoDestinatarioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(collaborazioneSPCoopBase.getDestinatario().getTipo())==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getDestinatario().getTipo()+"]");
						}
						findTipoDestinatario = true;
					}else if(testsuiteProperties.getDestinatarioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(collaborazioneSPCoopBase.getDestinatario().getNome())==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+collaborazioneSPCoopBase.getDestinatario().getNome()+"]");
						}
						findDestinatario = true;
					}else if(testsuiteProperties.getTipoServizioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(tipoServizio)==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+tipoServizio+"]");
						}
						findTipoServizio = true;
					}else if(testsuiteProperties.getNomeServizioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(servizio)==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+servizio+"]");
						}
						findServizio = true;
					}else if(testsuiteProperties.getVersioneServizioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals("1")==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso [1]");
						}
						findVersioneServizio = true;
					}else if(testsuiteProperties.getAzioneSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(azione)==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+azione+"]");
						}
						findAzione = true;
					}else if(testsuiteProperties.getIdMessaggioSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if(v.equals(idEGov)==false){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"] diverso da quello atteso ["+idEGov+"]");
						}
						findID = true;
					}
					else if(testsuiteProperties.getIDTransazioneSoap().equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						findIDTransazione = true;
					}
					else if(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_VERSION.equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if("".equals(v)){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"]");
						}
						findProductVersion = true;
					}
					else if(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS.equals(attr.getLocalName())){
						String v = elem.getAttributeValue(attr.getLocalName());
						if(v==null){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore null");
						}
						if("".equals(v)){
							throw new Exception("Attributo ["+attr.getLocalName()+"] con valore ["+v+"]");
						}
						findProductDetails = true;
					}
					Reporter.log("Attributo name["+attr.getLocalName()+"] prefix["+attr.getPrefix()+"]");
				}
			}
		}
		if(find==false){
			throw new Exception("Integrazione non trovata");
		}
		if(findTipoMittente==false){
			throw new Exception("Integrazione.tipoMittente non trovata");
		}
		if(findMittente==false){
			throw new Exception("Integrazione.mittente non trovata");
		}
		if(findTipoDestinatario==false){
			throw new Exception("Integrazione.tipoDestinatario non trovata");
		}
		if(findDestinatario==false){
			throw new Exception("Integrazione.destinatario non trovata");
		}
		if(findTipoServizio==false){
			throw new Exception("Integrazione.tipoServizio non trovata");
		}
		if(findServizio==false){
			throw new Exception("Integrazione.servizio non trovata");
		}
		if(findVersioneServizio==false){
			throw new Exception("Integrazione.versioneServizio non trovata");
		}
		if(findAzione==false){
			throw new Exception("Integrazione.azione non trovata");
		}
		if(findID==false){
			throw new Exception("Integrazione.id non trovata");
		}
		if(findIDTransazione==false){
			throw new Exception("Integrazione.idTransazione non trovata");
		}
		if(findProductVersion==false){
			throw new Exception("Integrazione."+CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_VERSION+" non trovata");
		}
		if(findProductDetails==false){
			throw new Exception("Integrazione."+CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS+" non trovata");
		}
	}
	
	public static void checkMessaggioRispostaWSAddressing(CooperazioneBase collaborazioneSPCoopBase,Message risposta,String tipoServizio,String nomeServizio,Integer versione, String azione,String idEGov)throws Exception{
		if(risposta==null){
			throw new Exception("Risposta is null");
		}
		if(risposta.getSOAPHeader()==null){
			throw new Exception("Risposta, soap header is null");
		}
		if(risposta.getSOAPHeader().hasChildNodes()==false){
			throw new Exception("Risposta, soap header vuoto");
		}
		Iterator<?> elements = risposta.getSOAPHeader().getChildElements();
		boolean findMittenteWSA = false;
		boolean findDestinatarioWSA = false;
		boolean findAzioneWSA = (azione==null);
		boolean findIDWSA = false;
		while(elements.hasNext()){
			SOAPHeaderElement elem = (SOAPHeaderElement) elements.next();
			Reporter.log("Elemento nell'header name["+elem.getLocalName()+"] prefix["+elem.getPrefix()+"] namespace["+elem.getNamespaceURI()+"]");
			
			if("To".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
					org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
				String v=elem.getValue();
				Reporter.log("trovato nell'header WSA(to): "+v);
				if(v==null){
					throw new Exception("Valore dell'elemento WSA-To non definito");
				}else{
					String test = "http://"+collaborazioneSPCoopBase.getDestinatario().getTipo()+"_"+collaborazioneSPCoopBase.getDestinatario().getNome()+"" +
							".govway.org/services/"+tipoServizio+"_"+nomeServizio+"/"+versione;
					if(test.equals(v)==false){
						throw new Exception("WSATo con valore ["+v+"] diverso da quello atteso ["+test+"]");
					}
					findDestinatarioWSA=true;
				}
			}
			else if("From".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
					org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
				Iterator<?> itFROM = elem.getChildElements();
				String v=null;
				while (itFROM.hasNext()) {
					Object o = itFROM.next();
					if(o!=null && (o instanceof SOAPElement) ){
						SOAPElement s = (SOAPElement) o;
						if("Address".equals(s.getLocalName())){
							Reporter.log("trovato header address all'interno di WSA(from)");
							v = s.getValue();
						}
					}
				}
				Reporter.log("trovato nell'header WSA(from): "+v);
				if(v==null){
					throw new Exception("Valore dell'elemento WSA-From non definito");
				}else{
					String test = "http://"+collaborazioneSPCoopBase.getMittente().getTipo()+"_"+collaborazioneSPCoopBase.getMittente().getNome()+".govway.org";
					if(test.equals(v)==false){
						throw new Exception("WSAFrom con valore ["+v+"] diverso da quello atteso ["+test+"]");
					}
					findMittenteWSA=true;
				}
			}
			else if("Action".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
					org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
				String v=elem.getValue();
				Reporter.log("trovato nell'header WSA(action): "+v);
				if(v==null){
					throw new Exception("Valore dell'elemento WSA-Action non definito");
				}else{
					String test = "http://"+collaborazioneSPCoopBase.getDestinatario().getTipo()+"_"+collaborazioneSPCoopBase.getDestinatario().getNome()+
							".govway.org/services/"+tipoServizio+"_"+nomeServizio+"/"+versione+"/"+azione;
					if(test.equals(v)==false){
						throw new Exception("WSAAction con valore ["+v+"] diverso da quello atteso ["+test+"]");
					}
					findAzioneWSA=true;
				}
			}
			else if("MessageID".equals(elem.getLocalName()) && "wsa".equals(elem.getPrefix()) && "http://www.w3.org/2005/08/addressing".equals(elem.getNamespaceURI()) &&
					org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA.equals(elem.getActor())){
				String v=elem.getValue();
				Reporter.log("trovato nell'header WSA(id): "+v);
				if(v==null){
					throw new Exception("Valore dell'elemento WSA-MessageID non definito");
				}else{
					String test = "uuid:"+idEGov;
					if(test.equals(v)==false){
						throw new Exception("WSAMessageID con valore ["+v+"] diverso da quello atteso ["+test+"]");
					}
					findIDWSA=true;
				}
			}
			
		}
		if(findMittenteWSA==false){
			throw new Exception("Integrazione.wsa.from non trovata");
		}
		if(findDestinatarioWSA==false){
			throw new Exception("Integrazione.wsa.to non trovata");
		}
		if(findAzioneWSA==false){
			throw new Exception("Integrazione.wsa.action non trovata");
		}
		if(findIDWSA==false){
			throw new Exception("Integrazione.wsa.messageId non trovata");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ****************** VERIFICA HEADER PDD VERSIONE E DETAILS TRA PORTE DI DOMINIO ******************** */
	
	Repository repositoryTestTraPdd=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".TRASPORTO_TRA_PDD"})
	public void trasportoTraPdd()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryTestTraPdd.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,null);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTestTraPdd);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();				
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
			
			// VERIFICA RESPONSE
			checkHttpRispostaPddVersioneDetails(client.getPropertiesTrasportoRisposta());
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}

	}
	@DataProvider (name="testTrasportoTraPdd")
	public Object[][]testTrasportoTraPdd()throws Exception{
		String id=this.repositoryTestTraPdd.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".TRASPORTO_TRA_PDD"},
			dataProvider="testTrasportoTraPdd",
			dependsOnMethods="trasportoTraPdd")
	public void testTrasportoTraPdd(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio ));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	/* ***************** CONNETTORE HTTP ************************* */


	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWay=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY"})
	public void oneWay() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="OneWay")
	public Object[][]testOneWay() throws Exception{
		String id=this.repositoryOneWay.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
	public void testOneWay(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWayWithWSA=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_WSADDRESSING"})
	public void oneWayWithWSA() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithWSA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 1,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="OneWayWithWSA")
	public Object[][]testOneWayWithWSA() throws Exception{
		String id=this.repositoryOneWayWithWSA.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_WSADDRESSING"},dataProvider="OneWayWithWSA",dependsOnMethods={"oneWayWithWSA"})
	public void testOneWayWithWSA(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try {
			for (int i = 0; i < 10; i++) {
				try{
					this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,null);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincrono=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincrono() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincrono);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			 // Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="Sincrono")
	public Object[][]testSincrono()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono",dependsOnMethods={"sincrono"})
	public void testSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try {
			for (int i = 0; i < 10; i++) {
				try{
					this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
							null);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoWithWSA=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_WSADDRESSING"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSA() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoWithWSA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,  1,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWithWSA")
	public Object[][]testSincronoWithWSA()throws Exception{
		String id=this.repositorySincronoWithWSA.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_WSADDRESSING"},dataProvider="SincronoWithWSA",dependsOnMethods={"sincronoWithWSA"})
	public void testSincronoWithWSA(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorysincronoWithWSAVerificaClientSincrono=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSAVerificaClientSincrono() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorysincronoWithWSAVerificaClientSincrono);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING);
			client.connectToSoapEngine();
			client.setMessage(msg);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,  1,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="sincronoWithWSAVerificaClientSincrono")
	public Object[][]testsincronoWithWSAVerificaClientSincrono()throws Exception{
		String id=this.repositorysincronoWithWSAVerificaClientSincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO"},
			dataProvider="sincronoWithWSAVerificaClientSincrono",
			dependsOnMethods={"sincronoWithWSAVerificaClientSincrono"})
	public void testsincronoWithWSAVerificaClientSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWayWithAttachments
	 */
	Repository repositoryOneWayWithAttachments=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_ATTACHMENTS"})
	public void oneWayWithAttachments() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachments);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="OneWayWithAttachments")
	public Object[][]testOneWayWithAttachments() throws Exception{
		String id=this.repositoryOneWayWithAttachments.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_ATTACHMENTS"},dataProvider="OneWayWithAttachments",dependsOnMethods={"oneWayWithAttachments"})
	public void testOneWayWithAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWayWithAttachments
	 */
	Repository repositoryOneWayWithAttachmentsAndWSAddressing=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING"})
	public void oneWayWithAttachmentsAndWSAddressing() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachmentsAndWSAddressing);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
				
		    // Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
		    
			// Check header proprietario OpenSPCoop
		    checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,   CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,  1,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="OneWayWithAttachmentsAndWSAddressing")
	public Object[][]testOneWayWithAttachmentsAndWSAddressing() throws Exception{
		String id=this.repositoryOneWayWithAttachmentsAndWSAddressing.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING"},
			dataProvider="OneWayWithAttachmentsAndWSAddressing",dependsOnMethods={"oneWayWithAttachmentsAndWSAddressing"})
	public void testOneWayWithAttachmentsAndWSAddressing(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try {
			for (int i = 0; i < 10; i++) {
				try{
					this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,null);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione SincronoWithAttachments
	 */
	Repository repositorySincronoWithAttachments=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_ATTACHMENTS"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachments() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachments);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.run();

			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null)
					dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWithAttachments")
	public Object[][]testSincronoWithAttachments()throws Exception{
		String id=this.repositorySincronoWithAttachments.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_ATTACHMENTS"},dataProvider="SincronoWithAttachments",dependsOnMethods={"sincronoWithAttachments"})
	public void testSincronoWithAttachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione SincronoWithAttachments
	 */
	Repository repositorySincronoWithAttachmentsAndWSAddressing=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachmentsAndWSAddressing() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachmentsAndWSAddressing);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.run();

			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
		    checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());
			
			// Check header WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING,client.getIdMessaggio());		
			
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null)
					dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWithAttachmentsAndWSAddressing")
	public Object[][]testSincronoWithAttachmentsAndWSAddressing()throws Exception{
		String id=this.repositorySincronoWithAttachmentsAndWSAddressing.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING"},
			dataProvider="SincronoWithAttachmentsAndWSAddressing",dependsOnMethods={"sincronoWithAttachmentsAndWSAddressing"})
	public void testSincronoWithAttachmentsAndWSAddressing(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ************************ URL BASED ******************************** 
	
	
	/***
	 * Test URL Based
	 */
	Repository repositorySincronoURLBased=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoUrlBased() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoURLBased);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_URL_BASED+"/"+
					this.collaborazioneSPCoopBase.getDestinatario().getNome()+"/"+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO+"/"+
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoUrlBased")
	public Object[][]testSincronoUrlBased()throws Exception{
		String id=this.repositorySincronoURLBased.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED"},dataProvider="SincronoUrlBased",dependsOnMethods={"sincronoUrlBased"})
	public void testSincronoUrlBased(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/***
	 * Test URL Based in stile FormBased
	 */
	Repository repositorySincronoUrlFormBased=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_FORM_BASED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoUrlFormBased() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoUrlFormBased);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_URL_FORM_BASED+"?soggetto="+
					this.collaborazioneSPCoopBase.getDestinatario().getNome()+"&servizio="+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO+"&azione="+
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoUrlFormBased")
	public Object[][]testSincronoUrlFormBased()throws Exception{
		String id=this.repositorySincronoUrlFormBased.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_FORM_BASED"},dataProvider="SincronoUrlFormBased",dependsOnMethods={"sincronoUrlFormBased"})
	public void testSincronoUrlFormBased(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test URL Based, input mancanti
	 */
	Repository repositorySincronoURLBasedInputMancanti=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED_DATI_MANCANTI"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoUrlBasedInputMancanti_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoUrlBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED_DATI_MANCANTI"},
		description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoUrlBasedInputMancanti_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoUrlBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _sincronoUrlBasedInputMancanti(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoURLBasedInputMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_URL_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403] non si e' verificato...");
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String msgErrore = MSG_ERRORE_IDENTIFICAZIONE_AZIONE;
				String msgErrore2 = MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2;
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						msgErrore2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"] faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"] o ["+msgErrore2+"]");
				Assert.assertTrue(msgErrore2.equals(error.getFaultString()) || msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoUrlBasedInputMancanti")
	public Object[][]testSincronoUrlBasedInputMancanti()throws Exception{
		String id=this.repositorySincronoURLBasedInputMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED_DATI_MANCANTI"},dataProvider="SincronoUrlBasedInputMancanti",dependsOnMethods={"sincronoUrlBasedInputMancanti_genericCode"})
	public void testSincronoUrlBasedInputMancanti_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".URL_BASED_DATI_MANCANTI"},dataProvider="SincronoUrlBasedInputMancanti",dependsOnMethods={"sincronoUrlBasedInputMancanti_specificCode"})
	public void testSincronoUrlBasedInputMancanti_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ************************ CONTENT BASED ******************************** 
	
	
	/***
	 * Test content based 1
	 */
	Repository repositorySincronoContentBased=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_1"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBased() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased1FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBased);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED1);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBased")
	public Object[][]testSincronoContentBased()throws Exception{
		String id=this.repositorySincronoContentBased.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_1"},dataProvider="SincronoContentBased",dependsOnMethods={"sincronoContentBased"})
	public void testSincronoContentBased(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test content based 2
	 */
	Repository repositorySincronoContentBased2=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBased2() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased2FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBased2);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED2);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBased2")
	public Object[][]testSincronoContentBased2()throws Exception{
		String id=this.repositorySincronoContentBased2.getNext();
		org.openspcoop2.utils.Utilities.sleep(10000);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_2"},dataProvider="SincronoContentBased2",dependsOnMethods={"sincronoContentBased2"})
	public void testSincronoContentBased2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		
		try{
			for (int i = 0; i < 10; i++) {
				try{
					this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
							null);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Content Based, input mancanti
	 */
	Repository repositorySincronoContentBasedInputMancanti=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_DATI_MANCANTI"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedInputMancanti_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_DATI_MANCANTI"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedInputMancanti_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _sincronoContentBasedInputMancanti(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBasedInputMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED1);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403] non si e' verificato...");
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String msgErrore = MSG_ERRORE_IDENTIFICAZIONE_AZIONE;
				String msgErrore2 = MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2;
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						msgErrore2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"] faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"] o ["+msgErrore2+"]");
				Assert.assertTrue(msgErrore2.equals(error.getFaultString()) || msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBasedInputMancanti")
	public Object[][]testSincronoContentBasedInputMancanti()throws Exception{
		String id=this.repositorySincronoContentBasedInputMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_DATI_MANCANTI"},dataProvider="SincronoContentBasedInputMancanti",dependsOnMethods={"sincronoContentBasedInputMancanti_genericCode"})
	public void testSincronoContentBasedInputMancanti_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_DATI_MANCANTI"},dataProvider="SincronoContentBasedInputMancanti",dependsOnMethods={"sincronoContentBasedInputMancanti_specificCode"})
	public void testSincronoContentBasedInputMancanti_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ************************ CONTENT BASED (CONCAT) ******************************** 
	
	
	/***
	 * Test content based funzione concat
	 */
	Repository repositorySincronoContentBasedConcat=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcat() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased2FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBasedConcat);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_CONCAT,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBasedConcat")
	public Object[][]testSincronoContentBasedConcat()throws Exception{
		String id=this.repositorySincronoContentBasedConcat.getNext();
		org.openspcoop2.utils.Utilities.sleep(10000);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT"},dataProvider="SincronoContentBasedConcat",dependsOnMethods={"sincronoContentBasedConcat"})
	public void testSincronoContentBasedConcat(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{

		try {
			for (int i = 0; i < 10; i++) {
				try{
					this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_CONCAT, checkServizioApplicativo,
							null);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test content based funzione concat, se fornisco xml che non permette di estrarre l'azione, la funziona concat non si arrabbia e quindi si deve sollevare una eccezione
	 * ServizioNotFound invece di correlazioneApplicativaNonRiuscita
	 */
	Repository repositorySincronoContentBasedConcatErroreIdentificazione=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcatErroreIdentificazione_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedConcatErroreIdentificazione(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcatErroreIdentificazione_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedConcatErroreIdentificazione(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _sincronoContentBasedConcatErroreIdentificazione(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased2_servizioNonIdentificabileFileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBasedConcatErroreIdentificazione);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
			}
			try {
				client.run();
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA);
				String msgErrore = "Azione richiesta non corretta: (azione:BEGIN-ID__END-ID) Azione 'BEGIN-ID__END-ID' non trovata nell'API ASRichiestaStatoAvanzamento:1";
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				return;
			}finally{
				dbComponentFruitore.close();
			}
			
			throw new Exception("Attesa eccezione "+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"405 che non e' avvenuta");
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBasedConcatErroreIdentificazione")
	public Object[][]testSincronoContentBasedConcatErroreIdentificazione()throws Exception{
		String id=this.repositorySincronoContentBasedConcatErroreIdentificazione.getNext();
		org.openspcoop2.utils.Utilities.sleep(10000);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE"},
			dataProvider="SincronoContentBasedConcatErroreIdentificazione",dependsOnMethods={"sincronoContentBasedConcatErroreIdentificazione_genericCode"})
	public void testSincronoContentBasedConcatErroreIdentificazione_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_ERRORE_IDENTIFICAZIONE"},
			dataProvider="SincronoContentBasedConcatErroreIdentificazione",dependsOnMethods={"sincronoContentBasedConcatErroreIdentificazione_specificCode"})
	public void testSincronoContentBasedConcatErroreIdentificazione_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		
		try {
			for (int i = 0; i < 10; i++) {
				try{
					Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}catch(Throwable e){
					if(i==9) {
						throw new Exception("Attesa("+i+"); "+e.getMessage(),e);
					}
					else {
						org.openspcoop2.utils.Utilities.sleep(2000+(i*1000));
						continue;
					}
				}
			}
		}finally{
			data.close();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test content based funzione concat_openspcoop
	 */
	Repository repositorySincronoContentBasedConcatOpenSPCoop=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcatOpenSPCoop() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased2FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBasedConcatOpenSPCoop);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_OPENSPCOOP);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_CONCAT,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBasedConcatOpenSPCoop")
	public Object[][]testSincronoContentBasedConcatOpenSPCoop()throws Exception{
		String id=this.repositorySincronoContentBasedConcatOpenSPCoop.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP"},
			dataProvider="SincronoContentBasedConcatOpenSPCoop",dependsOnMethods={"sincronoContentBasedConcatOpenSPCoop"})
	public void testSincronoContentBasedConcatOpenSPCoop(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_CONCAT, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test content based funzione concat_openspcoop, se fornisco xml che non permette di estrarre l'azione, la funziona concat_openspcoop si arrabbia e 
	 * quindi si deve sollevare una eccezione di IdentificazioneServizioNonRiuscita
	 */
	Repository repositorySincronoContentBasedConcatOpenSPCoopErroreIdentificazione=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcatOpenSPCoopErroreIdentificazione_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedConcatOpenSPCoopErroreIdentificazione(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoContentBasedConcatOpenSPCoopErroreIdentificazione_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoContentBasedConcatOpenSPCoopErroreIdentificazione(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
		
	private void _sincronoContentBasedConcatOpenSPCoopErroreIdentificazione(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapContentBased2_servizioNonIdentificabileFileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoContentBasedConcatErroreIdentificazione);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
			}
			try {
				client.run();
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String msgErrore = MSG_ERRORE_IDENTIFICAZIONE_AZIONE;
				String msgErrore2 = MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2;
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						msgErrore2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]:  faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"] o ["+msgErrore2+"]");
				Assert.assertTrue(msgErrore2.equals(error.getFaultString()) || msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				return;
			}finally{
				dbComponentFruitore.close();
			}
			
			throw new Exception("Attesa eccezione "+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"405 che non e' avvenuta");
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoContentBasedConcatOpenSPCoopErroreIdentificazione")
	public Object[][]testSincronoContentBasedConcatOpenSPCoopErroreIdentificazione()throws Exception{
		String id=this.repositorySincronoContentBasedConcatOpenSPCoopErroreIdentificazione.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE"},
			dataProvider="SincronoContentBasedConcatOpenSPCoopErroreIdentificazione",dependsOnMethods={"sincronoContentBasedConcatOpenSPCoopErroreIdentificazione_genericCode"})
	public void testSincronoContentBasedConcatOpenSPCoopErroreIdentificazione_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".CONTENT_BASED_CONCAT_OPENSPCOOP_ERRORE_IDENTIFICAZIONE"},
			dataProvider="SincronoContentBasedConcatOpenSPCoopErroreIdentificazione",dependsOnMethods={"sincronoContentBasedConcatOpenSPCoopErroreIdentificazione_specificCode"})
	public void testSincronoContentBasedConcatOpenSPCoopErroreIdentificazione_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// ************************ INPUT BASED ******************************** 
	
	
	/***
	 * Test input based tramite url
	 */
	Repository repositorySincronoInputBasedURL=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_URL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedURL() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoInputBasedURL);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED+"?"+testsuiteProperties.getDestinatarioUrlBased()+"="+
					this.collaborazioneSPCoopBase.getDestinatario().getNome()+"&"+testsuiteProperties.getNomeServizioUrlBased()+"="+
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO+"&"+testsuiteProperties.getAzioneUrlBased()+"="+
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoInputBasedURL")
	public Object[][]testSincronoInputBasedURL()throws Exception{
		String id=this.repositorySincronoInputBasedURL.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_URL"},dataProvider="SincronoInputBasedURL",dependsOnMethods={"sincronoInputBasedURL"})
	public void testSincronoInputBasedURL(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test input based tramite trasporto
	 */
	Repository repositorySincronoInputBasedTrasporto=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_TRASPORTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedTrasporto() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoInputBasedTrasporto);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED);
			client.connectToSoapEngine();
			client.setProperty(testsuiteProperties.getDestinatarioTrasporto(), this.collaborazioneSPCoopBase.getDestinatario().getNome());
			client.setProperty(testsuiteProperties.getNomeServizioTrasporto(), CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
			client.setProperty(testsuiteProperties.getAzioneTrasporto(), CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoInputBasedTrasporto")
	public Object[][]testSincronoInputBasedTrasporto()throws Exception{
		String id=this.repositorySincronoInputBasedTrasporto.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_TRASPORTO"},dataProvider="SincronoInputBasedTrasporto",dependsOnMethods={"sincronoInputBasedTrasporto"})
	public void testSincronoInputBasedTrasporto(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test input based tramite soap header
	 */
	Repository repositorySincronoInputBasedSOAPHeader=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_SOAP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedSOAPHeader() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();

			TestSuiteProperties testsuiteProperties = TestSuiteProperties.getInstance();
			
			Name name = new PrefixedQName(org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE,org.openspcoop2.core.integrazione.constants.Costanti.ROOT_LOCAL_NAME_INTEGRATION,"gw");
			org.apache.axis.message.SOAPHeaderElement header = 
				new org.apache.axis.message.SOAPHeaderElement(name);
			header.setActor(org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE);
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

			header.setAttribute(testsuiteProperties.getDestinatarioSoap(), this.collaborazioneSPCoopBase.getDestinatario().getNome());
			header.setAttribute(testsuiteProperties.getNomeServizioSoap(), CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
			header.setAttribute(testsuiteProperties.getAzioneSoap(), CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			msg.getSOAPHeader().addChildElement(header);
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoInputBasedSOAPHeader);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoInputBasedSOAPHeader")
	public Object[][]testSincronoInputBasedSOAPHeader()throws Exception{
		String id=this.repositorySincronoInputBasedSOAPHeader.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_SOAP"},dataProvider="SincronoInputBasedSOAPHeader",dependsOnMethods={"sincronoInputBasedSOAPHeader"})
	public void testSincronoInputBasedSOAPHeader(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	/***
	 * Test input based tramite soap header
	 */
	Repository repositorySincronoInputBasedWSAddressing=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_WSADDRESSING"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedWSAddressing() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			Name nameTo = new PrefixedQName("http://www.w3.org/2005/08/addressing","To","wsa");
			Name nameAction = new PrefixedQName("http://www.w3.org/2005/08/addressing","Action","wsa");
			
			org.apache.axis.message.SOAPHeaderElement headerTo = 
				new org.apache.axis.message.SOAPHeaderElement(nameTo);
			org.apache.axis.message.SOAPHeaderElement headerAction = 
				new org.apache.axis.message.SOAPHeaderElement(nameAction);
			
			headerTo.setActor(org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA);
			headerTo.setMustUnderstand(false);
			headerTo.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
			headerAction.setActor(org.openspcoop2.core.integrazione.constants.Costanti.TARGET_NAMESPACE_WSA);
			headerAction.setMustUnderstand(false);
			headerAction.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");
			
			headerTo.setValue("http://"+this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"_"+this.collaborazioneSPCoopBase.getDestinatario().getNome()+
					".govway.org/services/"+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"_"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO+"/1");
			headerAction.setValue("http://"+this.collaborazioneSPCoopBase.getDestinatario().getTipo()+"_"+this.collaborazioneSPCoopBase.getDestinatario().getNome()+
					".govway.org/services/"+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO+"_"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO+"/1/"+
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			
			if(msg.getSOAPHeader()==null)
				msg.getSOAPEnvelope().addHeader();
			
			byte [] headerToByte = Axis14SoapUtils.msgElementoToByte(headerTo);
			ByteArrayInputStream inputTo = new ByteArrayInputStream(headerToByte);
			Document documentTo = org.apache.axis.utils.XMLUtils.newDocument(inputTo);
			SOAPHeaderElement elementToSenzaXSITypes = new SOAPHeaderElement(documentTo.getDocumentElement());
			msg.getSOAPHeader().addChildElement(elementToSenzaXSITypes);
			
			byte [] headerActionByte = Axis14SoapUtils.msgElementoToByte(headerAction);
			ByteArrayInputStream inputAction = new ByteArrayInputStream(headerActionByte);
			Document documentAction = org.apache.axis.utils.XMLUtils.newDocument(inputAction);
			SOAPHeaderElement elementActionSenzaXSITypes = new SOAPHeaderElement(documentAction.getDocumentElement());
			msg.getSOAPHeader().addChildElement(elementActionSenzaXSITypes);
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoInputBasedWSAddressing);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Check header proprietario OpenSPCoop
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
			// Check header proprietario WSAddressing
			checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoInputBasedWSAddressing")
	public Object[][]testSincronoInputBasedWSAddressing()throws Exception{
		String id=this.repositorySincronoInputBasedWSAddressing.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_WSADDRESSING"},dataProvider="SincronoInputBasedWSAddressing",dependsOnMethods={"sincronoInputBasedWSAddressing"})
	public void testSincronoInputBasedWSAddressing(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Input Based, input mancanti
	 */
	Repository repositorySincronoInputBasedInputMancanti=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_DATI_MANCANTI"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedInputMancanti_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoInputBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_DATI_MANCANTI"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoInputBasedInputMancanti_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoInputBasedInputMancanti(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _sincronoInputBasedInputMancanti(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoInputBasedInputMancanti);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_INPUT_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403] non si e' verificato...");
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String msgErrore = MSG_ERRORE_IDENTIFICAZIONE_AZIONE;
				String msgErrore2 = MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2;
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						msgErrore2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"] faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"] o ["+msgErrore2+"]");
				Assert.assertTrue(msgErrore2.equals(error.getFaultString()) || msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoInputBasedInputMancanti")
	public Object[][]testSincronoInputBasedInputMancanti()throws Exception{
		String id=this.repositorySincronoInputBasedInputMancanti.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_DATI_MANCANTI"},dataProvider="SincronoInputBasedInputMancanti",dependsOnMethods={"sincronoInputBasedInputMancanti_genericCode"})
	public void testSincronoInputBasedInputMancanti_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".INPUT_BASED_DATI_MANCANTI"},dataProvider="SincronoInputBasedInputMancanti",dependsOnMethods={"sincronoInputBasedInputMancanti_specificCode"})
	public void testSincronoInputBasedInputMancanti_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ************************ SOAP ACTION BASED ******************************** 
	
	
	/***
	 * Test SOAP Action Based
	 */
	Repository repositorySincronoSoapActionBased=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBased() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBased);
			client.setSoapAction("\""+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE+"\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBased")
	public Object[][]testSincronoSoapActionBased()throws Exception{
		String id=this.repositorySincronoSoapActionBased.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED"},dataProvider="SincronoSoapActionBased",dependsOnMethods={"sincronoSoapActionBased"})
	public void testSincronoSoapActionBased(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	/***
	 * Test SOAP Action Based 2
	 */
	Repository repositorySincronoSoapActionBased2=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBased2() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBased2);
			String soapAction = "http://govway.org/testSincrono";
			client.setSoapAction("\""+soapAction+"\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED_2);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_PORT_TYPE_AZIONE_TEST,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBased2")
	public Object[][]testSincronoSoapActionBased2()throws Exception{
		String id=this.repositorySincronoSoapActionBased2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED2"},dataProvider="SincronoSoapActionBased2",dependsOnMethods={"sincronoSoapActionBased2"})
	public void testSincronoSoapActionBased2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_PORT_TYPE_AZIONE_TEST, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}	
	
	
	
	
	
	
	
	/***
	 * Test SOAP Action Based con action non quotata
	 */
	// TEST Utile solo a fini di verifica per funzionalita' durante lo sviluppo.
	// Durante l'installazione della testsuite, nel documento di installazione, si indica che la PdD deve essere configurata in modo da attivare
	// il controllo di soap action non quotato. SoapAction non quotate non vengono accettate dalla PdD.
	// Quindi il test seguente fallisce e non ha senso eseguirlo in questo contesto.
	// Non e' stato cancellato, poiche' puo' essere utile per test di sviluppo.
	/*
	Repository repositorySincronoSoapActionBasedNonQuotata=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_SA_NON_QUOTATA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedNonQuotata() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapFileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBasedNonQuotata);
			client.setSoapAction(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			checkMessaggioRisposta(client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBasedNonQuotata")
	public Object[][]testSincronoSoapActionBasedNonQuotata()throws Exception{
		String id=this.repositorySincronoSoapActionBasedNonQuotata.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_SA_NON_QUOTATA"},dataProvider="SincronoSoapActionBasedNonQuotata",dependsOnMethods={"sincronoSoapActionBasedNonQuotata"})
	public void testSincronoSoapActionBasedNonQuotata(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	*/
	

		
	
	
	
	/***
	 * Test URL Based, input mancanti: soap action con valore http://...
	 */
	Repository repositorySincronoSoapActionBasedInputMancanti_1=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_1"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedInputMancanti_1_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoSoapActionBasedInputMancanti_1(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_1"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedInputMancanti_1_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoSoapActionBasedInputMancanti_1(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _sincronoSoapActionBasedInputMancanti_1(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		String soapActionTest = "http://soapActionWSBasicProfile2";
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBasedInputMancanti_1);
			client.setSoapAction("\""+soapActionTest+"\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"423] non si e' verificato...");
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_423_SERVIZIO_CON_AZIONE_SCORRETTA);
				String msgErrore = MSG_ERRORE_SERVIZIO_ERRATO;
				// Ci sono 2 occorrenze di azione
				msgErrore = msgErrore.replace("@AZIONE@", soapActionTest);
				msgErrore = msgErrore.replace("@AZIONE@", soapActionTest);
				msgErrore = msgErrore.replace("@ACCORDO_SERVIZIO@", "ASRichiestaStatoAvanzamento:1");
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"] faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo faultString ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBasedInputMancanti_1")
	public Object[][]testSincronoSoapActionBasedInputMancanti_1()throws Exception{
		String id=this.repositorySincronoSoapActionBasedInputMancanti_1.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_1"},dataProvider="SincronoSoapActionBasedInputMancanti_1",dependsOnMethods={"sincronoSoapActionBasedInputMancanti_1_genericCode"})
	public void testSincronoSoapActionBasedInputMancanti_1_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_1"},dataProvider="SincronoSoapActionBasedInputMancanti_1",dependsOnMethods={"sincronoSoapActionBasedInputMancanti_1_specificCode"})
	public void testSincronoSoapActionBasedInputMancanti_1_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test URL Based, input mancanti: soap action ""
	 */
	// TEST Utile solo a fini di verifica per funzionalita' durante lo sviluppo.
	// Durante l'installazione della testsuite, nel documento di installazione, si indica che la PdD deve essere configurata in modo da attivare
	// il controllo di soap action non quotato. SoapAction non quotate non vengono accettate dalla PdD.
	// Quindi il test seguente fallisce e non ha senso eseguirlo in questo contesto.
	// Non e' stato cancellato, poiche' puo' essere utile per test di sviluppo.
	/*
	Repository repositorySincronoSoapActionBasedInputMancanti_2=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_2"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedInputMancanti_2() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoapFileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBasedInputMancanti_2);
			client.setSoapAction("");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403] non si e' verificato...");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_403_PD_PATTERN_NON_VALIDO).equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+MSG_ERRORE+"]");
				Assert.assertTrue(MSG_ERRORE.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBasedInputMancanti_2")
	public Object[][]testSincronoSoapActionBasedInputMancanti_2()throws Exception{
		String id=this.repositorySincronoSoapActionBasedInputMancanti_2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_2"},dataProvider="SincronoSoapActionBasedInputMancanti_2",dependsOnMethods={"sincronoSoapActionBasedInputMancanti_2"})
	public void testSincronoSoapActionBasedInputMancanti_2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test URL Based, input mancanti: soap action "\"\""
	 */
	Repository repositorySincronoSoapActionBasedInputMancanti_3=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_3"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedInputMancanti_3_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoSoapActionBasedInputMancanti_3(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_3"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSoapActionBasedInputMancanti_3_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		try {
			super.lockForCode(genericCode, false);
			
			_sincronoSoapActionBasedInputMancanti_3(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _sincronoSoapActionBasedInputMancanti_3(boolean genericCode) throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSoapActionBasedInputMancanti_3);
			client.setSoapAction("\"\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SOAP_ACTION_BASED);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Errore atteso ["+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"403] non si e' verificato...");
			} catch (AxisFault error) {
				
				String codiceEccezione = Utilities.toString(CodiceErroreIntegrazione.CODICE_403_AZIONE_NON_IDENTIFICATA);
				String msgErrore = MSG_ERRORE_IDENTIFICAZIONE_AZIONE;
				String msgErrore2 = MSG_ERRORE_IDENTIFICAZIONE_AZIONE_2;
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceEccezione = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
						msgErrore2 = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"] faultString["+error.getFaultString()+"]");
				Reporter.log("Controllo fault code ["+codiceEccezione+"]");
				Assert.assertTrue(codiceEccezione.equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+msgErrore+"] o ["+msgErrore2+"]");
				Assert.assertTrue(msgErrore2.equals(error.getFaultString()) || msgErrore.equals(error.getFaultString()));
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoSoapActionBasedInputMancanti_3")
	public Object[][]testSincronoSoapActionBasedInputMancanti_3()throws Exception{
		String id=this.repositorySincronoSoapActionBasedInputMancanti_3.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_3"},dataProvider="SincronoSoapActionBasedInputMancanti_3",dependsOnMethods={"sincronoSoapActionBasedInputMancanti_3_genericCode"})
	public void testSincronoSoapActionBasedInputMancanti_3_genericCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,Integrazione.ID_GRUPPO,Integrazione.ID_GRUPPO+".SOAP_ACTION_BASED_DATI_MANCANTI_3"},dataProvider="SincronoSoapActionBasedInputMancanti_3",dependsOnMethods={"sincronoSoapActionBasedInputMancanti_3_specificCode"})
	public void testSincronoSoapActionBasedInputMancanti_3_specificCode(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta non effettuato con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}
