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

package it.gov.spcoop.sica.dao.driver;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Message;
import javax.wsdl.Service;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopUtilities;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import it.cnipa.collprofiles.EgovDecllElement;
import it.cnipa.collprofiles.OperationListType;
import it.cnipa.collprofiles.OperationType;
import it.cnipa.collprofiles.driver.TipiProfiliCollaborazione;
import it.gov.spcoop.sica.dao.AccordoCooperazione;
import it.gov.spcoop.sica.dao.AccordoServizioComposto;
import it.gov.spcoop.sica.dao.AccordoServizioParteComune;
import it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.dao.Costanti;
import it.gov.spcoop.sica.dao.Documento;
import it.gov.spcoop.sica.manifest.AccordoServizio;
import it.gov.spcoop.sica.manifest.DocumentoConversazione;
import it.gov.spcoop.sica.manifest.DocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.DocumentoInterfaccia;
import it.gov.spcoop.sica.manifest.DocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.DocumentoSemiformale;
import it.gov.spcoop.sica.manifest.DocumentoSicurezza;
import it.gov.spcoop.sica.manifest.ElencoAllegati;
import it.gov.spcoop.sica.manifest.ElencoPartecipanti;
import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;
import it.gov.spcoop.sica.manifest.ElencoServiziComposti;
import it.gov.spcoop.sica.manifest.ServizioComposto;
import it.gov.spcoop.sica.manifest.SpecificaConversazione;
import it.gov.spcoop.sica.manifest.SpecificaCoordinamento;
import it.gov.spcoop.sica.manifest.SpecificaInterfaccia;
import it.gov.spcoop.sica.manifest.SpecificaLivelliServizio;
import it.gov.spcoop.sica.manifest.SpecificaPortiAccesso;
import it.gov.spcoop.sica.manifest.SpecificaSemiformale;
import it.gov.spcoop.sica.manifest.SpecificaSicurezza;
import it.gov.spcoop.sica.manifest.driver.TipiAdesione;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoConversazione;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoCoordinamento;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoInterfaccia;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoLivelloServizio;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoSemiformale;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoSicurezza;
import it.gov.spcoop.sica.wscp.ProfiloCollaborazioneEGOV;


/**
 * ClientTest
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	
	public final static String wsdlImplementativo = "<wsdl:definitions targetNamespace=\"http://openspcoop.org/Example/service\" \n" +
			"xmlns:apachesoap=\"http://xml.apache.org/xml-soap\" xmlns:service=\"http://openspcoop.org/Example/service\"\n" +
			" xmlns:types=\"http://openspcoop.org/Example/types\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n" +
			" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" >\n" +
			"   \n" +
			"	<!-- Example KEYWORD -->  \n" +
			"	\n" +
			"		<wsdl:import namespace=\"http://openspcoop.org/Example/service\" location=\"logicoKEYWORD.wsdl\" />\n" +
			"  "+
			"		 <wsdl:binding name=\"ExampleSoapBinding\" type=\"service:Example\">\n" +
			"			<wsdlsoap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
			"			<wsdl:operation name=\"movOneWayRequest\">\n" +
			"				<wsdlsoap:operation soapAction=\"Example\"/>\n" +
			"				<wsdl:input name=\"movOneWayRequest\">\n" +
			"						<wsdlsoap:body use=\"literal\"/>\n" +
			"				</wsdl:input>\n" +
			"			</wsdl:operation>\n" +
			"		</wsdl:binding>\n" +
			"\n" +
			"		<wsdl:service name=\"ExampleService\">\n" +
			"			<wsdl:port binding=\"service:ExampleSoapBinding\" name=\"Example\">\n" +
			"				<wsdlsoap:address location=\"http://localhost:8080/exampleKEYWORD/service\"/>\n" +
			"			</wsdl:port>\n" +
			"		</wsdl:service>\n" +
			"</wsdl:definitions>";
	
	
	
	public static SICAtoOpenSPCoopContext getContextSICAToOpenSPCoop(boolean invertiOpzioni,boolean invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione,
			boolean sicaClientCompatibility) throws Exception{
		
		SICAtoOpenSPCoopContext context = new SICAtoOpenSPCoopContext();
		
		// Se richiesto inversione proprieta lo effettuo
		if(invertiOpzioni){
			context.setSICAClient_generaProject(!context.isSICAClient_generaProject());
			if(invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione){
				context.setSICAClient_includiInfoRegistroGenerale(!context.isSICAClient_includiInfoRegistroGenerale());
			}
			context.setInformazioniEGov_specificaSemiformale(!context.isInformazioniEGov_specificaSemiformale());
			context.setInformazioniEGov_wscp(!context.isInformazioniEGov_wscp());
			context.setInformazioniEGov_wscpDisabled_namespaceCnipa(!context.isInformazioniEGov_wscpDisabled_namespaceCnipa());
			context.setInformazioniEGov_wscpDisabled_childUnqualified(!context.isInformazioniEGov_wscpDisabled_childUnqualified());
			context.setInformazioniEGov_wscpEnabled_childUnqualified(!context.isInformazioniEGov_wscpEnabled_childUnqualified());
			context.setInformazioniEGov_nomiSPCoop_qualified(!context.isInformazioniEGov_nomiSPCoop_qualified());
			context.setWSDL_XSD_prettyDocuments(!context.isWSDL_XSD_prettyDocuments());
			if(invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione){
				context.setWSDL_XSD_allineaImportInclude(!context.isWSDL_XSD_allineaImportInclude());
				context.setWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune(!context.isWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune());
			}
			context.setWSDL_XSD_accordiParteSpecifica_gestioneParteComune(!context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune());
			context.setWSDL_XSD_accordiParteSpecifica_wsdlEmpty(!context.isWSDL_XSD_accordiParteSpecifica_wsdlEmpty());
			context.setWSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune(!context.isWSDL_XSD_accordiParteSpecifica_sicaToOpenspcoop_aggiuntaImportParteComune());
		}
		
		// Compatibilita SICA Client
		context.setSICAClientCompatibility(sicaClientCompatibility);
		
		return context;
	}
	
	
	/**
	 * 
	 * For run:
	 * java 
	 *    it.gov.spcoop.sica.dao.driver.ClientTest example/registroServizi/registroServiziXML [0/1/2/SICA]
	 *    
	 *    Dove invertiComportamentoDefinitoOpzioni
	 *    - 0: comportamento normale
	 *    - 1: comportamento completamente invertito
	 *    - 2: comportamento invertito tranne opzioni fondamentali per la validazione WSDL e per la ritrasformazione in oggetti openspcoop: verificaCorreggiWSDLImportInclude e generaInformazioniRegistroSICAGenerale
	 * 	  - SICA: package SICA Client compatibility
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			
			if(args.length<1){
				System.out.println("Error, java use:");
				System.out.println("\t java -classpath CLASSPATH it.gov.spcoop.sica.dao.driver.ClientTest \"example/registroServizi/registroServiziXML\" [0/1/2/SICA]");
				return;
			}
			
			boolean invertiOpzioni = false;
			boolean invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione = false;
			boolean sicaClientCompatibility = false;
			if(args!=null && args.length>1){
				if("0".equals(args[1].trim())){
					invertiOpzioni = false;
				}
				else if("1".equals(args[1].trim())){
					invertiOpzioni = true;
					invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione = true;
				}
				else if("2".equals(args[1].trim())){
					invertiOpzioni = true;
					invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione = false;
				}
				else if("SICA".equalsIgnoreCase(args[1].trim())){
					sicaClientCompatibility = true;
				}
				else{
					throw new Exception("Proprieta' ["+args[1].trim()+"] non gestita. (Forse volevi usare la proprieta' '0/1/2'?) ");
				}
			}
			
			SICAtoOpenSPCoopContext context = ClientTest.getContextSICAToOpenSPCoop(invertiOpzioni, invertireOpzioniCheCausanoFallimentoPerValidazioneETrasformazione, sicaClientCompatibility);
			
			String dir = args[0].trim();
			File dirRegistroServizi = new File(dir);
			if(dirRegistroServizi.exists()==false){
				throw new Exception("Directory ["+dirRegistroServizi.getAbsolutePath()+"] non esistente");
			}
			if(dirRegistroServizi.canRead()==false){
				throw new Exception("Directory ["+dirRegistroServizi.getAbsolutePath()+"] non accessibile");
			}
			
			
			java.util.Properties loggerProperties = new java.util.Properties();
			loggerProperties.load(RegistroServizi.class.getResourceAsStream("/openspcoop2.log4j2.properties"));
			LoggerWrapperFactory.setLogConfiguration(loggerProperties);
			Logger log = LoggerWrapperFactory.getLogger("openspcoop2.core");
			
			XMLUtils xmlSICAUtilities = new XMLUtils(context,log);
			
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
			
			
			System.out.println("********* TEST PER FUNZIONAMENTO DRIVER **************** \n\n");
			
			
			
			
			/* ---- TEST Accordo di Servizio parte comune  ----- */
			
			// 1. Crezione oggetto Java
			
			AccordoServizioParteComune asPC = new AccordoServizioParteComune();
			// Manifest
			AccordoServizio manAsPC = ClientTest.getManifestoAS_ParteComune(context.isInformazioniEGov_specificaSemiformale(),context.isInformazioniEGov_wscp());
			asPC.setManifesto(manAsPC);
			// Allegato
			Documento dAllegato1 = new Documento("Allegato1.doc","DOC","PROVA".getBytes());
			Documento dAllegato2 = new Documento("Allegato2.doc","DOC","PROVA2".getBytes());
			asPC.addAllegato(dAllegato1);
			asPC.addAllegato(dAllegato2);
			// SpecificaSemiformale
			Documento dSS1 = new Documento("Collaborazione.doc",TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString(),"SS".getBytes());
			Documento dSS2 = new Documento("Schemi.xml",TipiDocumentoSemiformale.XML.toString(),"<test/>".getBytes());
			asPC.addSpecificaSemiformale(dSS1);
			asPC.addSpecificaSemiformale(dSS2);
			// Specifica Conversazioni
			Documento conversazioneConcettuale = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>concettuale</wsbl>".getBytes());
			asPC.setConversazioneConcettuale(conversazioneConcettuale);
			Documento conversazioneLogicaErogatore = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>erogatore</wsbl>".getBytes());
			asPC.setConversazioneLogicaErogatore(conversazioneLogicaErogatore);
			Documento conversazioneLogicaFruitore = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>fruitore</wsbl>".getBytes());
			asPC.setConversazioneLogicaFruitore(conversazioneLogicaFruitore);
			// Specifica Interfacce
			Documento interfacciaConcettuale = new Documento(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>concettuale</wsdl>".getBytes());
			asPC.setInterfacciaConcettuale(interfacciaConcettuale);
			Documento interfacciaLogicaErogatore = new Documento(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>erogatore</wsdl>".getBytes());
			asPC.setInterfacciaLogicaLatoErogatore(interfacciaLogicaErogatore);
			Documento interfacciaLogicaFruitore = new Documento(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>fruitore</wsdl>".getBytes());
			asPC.setInterfacciaLogicaLatoFruitore(interfacciaLogicaFruitore);
			// ModalitaEsplicitaCNIPA per info egov di tipo
			byte[] egovBytes = null;
			String nomeFileInfoEGov = null;
			if(context.isInformazioniEGov_wscp()){
				ProfiloCollaborazioneEGOV dichiarazioneEGov = ClientTest.getDichiarazioneEGovFormatoClientSICA(manAsPC.getNome());
				egovBytes = it.gov.spcoop.sica.wscp.driver.XMLUtils.generateDichiarazioneEGov(dichiarazioneEGov,context.isInformazioniEGov_wscpEnabled_childUnqualified());
				nomeFileInfoEGov = it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV;
			}else{
				EgovDecllElement dichiarazioneEGov = ClientTest.getDichiarazioneEGov(manAsPC.getNome());
				egovBytes = it.cnipa.collprofiles.driver.XMLUtils.generateDichiarazioneEGov(dichiarazioneEGov,context.isInformazioniEGov_wscpDisabled_namespaceCnipa());
				nomeFileInfoEGov = it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV;
			}
			Documento docModalitaEsplicitaCNIPA = new Documento(nomeFileInfoEGov,TipiDocumentoSemiformale.XML.toString(),egovBytes);
			if(context.isInformazioniEGov_specificaSemiformale())
				asPC.addSpecificaSemiformale(docModalitaEsplicitaCNIPA);
			else
				asPC.addAllegato(docModalitaEsplicitaCNIPA);
			// Definitorio.xsd
			Documento dAllegatoDEFINITORIO = new Documento(Costanti.ALLEGATO_DEFINITORIO_XSD,"XSD","<xsd>DEFINITORIO</xsd>".getBytes());
			asPC.addAllegato(dAllegatoDEFINITORIO);
			
			// 2. Trasformazione in package
			String fileName = "package.apc";
			xmlSICAUtilities.generateAccordoServizioParteComune(asPC, fileName);
			
			// 3. Ritrasformazione in oggetto java
			AccordoServizioParteComune asParteComune = xmlSICAUtilities.getAccordoServizioParteComune(fileName);
			org.openspcoop2.core.registry.AccordoServizioParteComune asParteComuneOpenSPCoop = 
				SICAtoOpenSPCoopUtilities.accordoServizioParteComune_sicaToOpenspcoop(asParteComune,context,log);
			ClientTest.printAccordoServizioParteComune(log,asParteComuneOpenSPCoop,false,false);
			
			
			
			
			
			
			
			/* ---- TEST Accordo di Servizio parte specifica  ----- */
			
			// 1. Crezione oggetto Java
			
			AccordoServizioParteSpecifica asPS = new AccordoServizioParteSpecifica();
			// Manifest
			AccordoServizio manAsPS = ClientTest.getManifestoAS_ParteSpecifica();
			asPS.setManifesto(manAsPS);
			// Allegato
			dAllegato1 = new Documento("Allegato1.doc","DOC","PROVA".getBytes());
			dAllegato2 = new Documento("Allegato2.doc","DOC","PROVA2".getBytes());
			asPS.addAllegato(dAllegato1);
			asPS.addAllegato(dAllegato2);
			// SpecificaSemiformale
			dSS1 = new Documento("Collaborazione.doc",TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString(),"SS".getBytes());
			dSS2 = new Documento("Schemi.xml",TipiDocumentoSemiformale.XML.toString(),"<test/>".getBytes());
			asPS.addSpecificaSemiformale(dSS1);
			asPS.addSpecificaSemiformale(dSS2);
			// Specifica Porti Accesso
			Documento portiAccessoErogatore = new Documento(Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),ClientTest.wsdlImplementativo.replaceAll("KEYWORD", "EROGATORE").getBytes());
			asPS.setPortiAccessoErogatore(portiAccessoErogatore);
			Documento portiAccessoFruitore = new Documento(Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),ClientTest.wsdlImplementativo.replaceAll("KEYWORD", "FRUITORE").getBytes());
			asPS.setPortiAccessoFruitore(portiAccessoFruitore);
			// SpecificaLivelliServizio
			Documento servizioMinimo = new Documento("LivelloServizioMinimo.wsla",
					TipiDocumentoLivelloServizio.WSLA.toString(),"<sla>minimo</sla>".getBytes());
			asPS.addSpecificaLivelloServizio(servizioMinimo);
			Documento servizioMax = new Documento("LivelloServizioOttimale.wsla",
					TipiDocumentoLivelloServizio.WSLA.toString(),"<sla>max</sla>".getBytes());
			asPS.addSpecificaLivelloServizio(servizioMax);
			// Specifica Sicurezza
			Documento sicurezza1 = new Documento("SicurezzaDelCanale.wspolicy",
					TipiDocumentoSicurezza.WSPOLICY.toString(),"<sec>wss</sec>".getBytes());
			asPS.addSpecificaSicurezza(sicurezza1);
			Documento sicurezza2 = new Documento("LineeGuida.doc",
					TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString(),"LINEE GUIDA".getBytes());
			asPS.addSpecificaSicurezza(sicurezza2);
			
			// 2. Trasformazione in package
			fileName = "package.aps";
			xmlSICAUtilities.generateAccordoServizioParteSpecifica(asPS, fileName);
			
			// 3. Ritrasformazione in oggetto java
			AccordoServizioParteSpecifica asParteSpecifica = xmlSICAUtilities.getAccordoServizioParteSpecifica(fileName);
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asParteSpecificaOpenSPCoop = 
				SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(asParteSpecifica,context,log);
			ClientTest.printAccordoServizioParteSpecifica(log,asParteSpecificaOpenSPCoop,false);
			
			// 4. Trasformazione in package (package.aps) per il servizio composto
			fileName = "package2.aps";
			asPS.getManifesto().setNome(asPS.getManifesto().getNome()+"2");
			xmlSICAUtilities.generateAccordoServizioParteSpecifica(asPS, fileName);
			
			
			
			
			
			
			
			
			
			/* ---- TEST Accordo di Cooperazione  ----- */
			
			// 1. Crezione oggetto Java
			
			AccordoCooperazione ac = new AccordoCooperazione();
			// Manifest
			ac.setManifesto(ClientTest.getManifestoAC());
			// Allegato
			dAllegato1 = new Documento("Allegato1.doc","DOC","PROVA".getBytes());
			dAllegato2 = new Documento("Allegato2.doc","DOC","PROVA2".getBytes());
			ac.addAllegato(dAllegato1);
			ac.addAllegato(dAllegato2);
			// SpecificaSemiformale
			dSS1 = new Documento("Collaborazione.doc",TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString(),"SS".getBytes());
			dSS2 = new Documento("Schemi.xml",TipiDocumentoSemiformale.XML.toString(),"<test/>".getBytes());
			ac.addSpecificaSemiformale(dSS1);
			ac.addSpecificaSemiformale(dSS2);
			
			// 2. Trasformazione in package
			fileName = "package.adc";
			xmlSICAUtilities.generateAccordoCooperazione(ac, fileName);
			
			// 3. Ritrasformazione in oggetto java
			AccordoCooperazione acSICA = xmlSICAUtilities.getAccordoCooperazione(fileName);
			org.openspcoop2.core.registry.AccordoCooperazione acOpenSPCoop = SICAtoOpenSPCoopUtilities.accordoCooperazione_sicaToOpenspcoop(acSICA,context,log);
			ClientTest.printAccordoCooperazione(acOpenSPCoop);
			
			
			
			
			
			
			
			
			
			/* ---- TEST Accordo di Servizio composto  ----- */
			
			// 1. Crezione oggetto Java
			
			AccordoServizioComposto aSC = new AccordoServizioComposto();
			// Manifest
			ServizioComposto manASC = ClientTest.getManifestoASComposto(context.isInformazioniEGov_specificaSemiformale(),context.isInformazioniEGov_wscp());
			aSC.setManifesto(manASC);
			// Allegato
			dAllegato1 = new Documento("Allegato1.doc","DOC","PROVA".getBytes());
			dAllegato2 = new Documento("Allegato2.doc","DOC","PROVA2".getBytes());
			aSC.addAllegato(dAllegato1);
			aSC.addAllegato(dAllegato2);
			// SpecificaSemiformale
			dSS1 = new Documento("Collaborazione.doc",TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString(),"SS".getBytes());
			dSS2 = new Documento("Schemi.xml",TipiDocumentoSemiformale.XML.toString(),"<test/>".getBytes());
			aSC.addSpecificaSemiformale(dSS1);
			aSC.addSpecificaSemiformale(dSS2);
			// Specifica Conversazioni
			conversazioneConcettuale = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_CONCETTUALE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>concettuale</wsbl>".getBytes());
			aSC.setConversazioneConcettuale(conversazioneConcettuale);
			conversazioneLogicaErogatore = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_EROGATORE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>erogatore</wsbl>".getBytes());
			aSC.setConversazioneLogicaErogatore(conversazioneLogicaErogatore);
			conversazioneLogicaFruitore = new Documento(Costanti.SPECIFICA_CONVERSAZIONE_LOGICA_LATO_FRUITORE_WSBL,
					TipiDocumentoConversazione.WSBL.toString(),"<wsbl>fruitore</wsbl>".getBytes());
			aSC.setConversazioneLogicaFruitore(conversazioneLogicaFruitore);
			// Specifica Interfacce
			interfacciaConcettuale = new Documento(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>concettuale</wsdl>".getBytes());
			aSC.setInterfacciaConcettuale(interfacciaConcettuale);
			interfacciaLogicaErogatore = new Documento(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>erogatore</wsdl>".getBytes());
			aSC.setInterfacciaLogicaLatoErogatore(interfacciaLogicaErogatore);
			interfacciaLogicaFruitore = new Documento(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL,
					TipiDocumentoInterfaccia.WSDL.toString(),"<wsdl>fruitore</wsdl>".getBytes());
			aSC.setInterfacciaLogicaLatoFruitore(interfacciaLogicaFruitore);
			// Specifica Coordinamento
			Documento specificaCoordinamento = new Documento("Generica Orchestrazione.bpel",
					TipiDocumentoCoordinamento.BPEL.toString(),"<bpel>coordinamento</bpel>".getBytes());
			aSC.addSpecificaCoordinamento(specificaCoordinamento);
			Documento specificaCoordinamento2 = new Documento("Generica Orchestrazione.wscdl",
					TipiDocumentoCoordinamento.WSCDL.toString(),"<wscdl>coordinamento</wscdl>".getBytes());
			aSC.addSpecificaCoordinamento(specificaCoordinamento2);
			// ModalitaEsplicitaCNIPA per info egov
			byte[] egovBytesServizioComposto = null;
			String nomeFileInfoEGovServizioComposto = null;
			if(context.isInformazioniEGov_wscp()){
				ProfiloCollaborazioneEGOV dichiarazioneEGov = ClientTest.getDichiarazioneEGovFormatoClientSICA(manAsPC.getNome());
				egovBytesServizioComposto = it.gov.spcoop.sica.wscp.driver.XMLUtils.generateDichiarazioneEGov(dichiarazioneEGov,context.isInformazioniEGov_wscpEnabled_childUnqualified());
				nomeFileInfoEGovServizioComposto = it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV;
			}else{
				EgovDecllElement dichiarazioneEGov = ClientTest.getDichiarazioneEGov(manAsPC.getNome());
				egovBytesServizioComposto = it.cnipa.collprofiles.driver.XMLUtils.generateDichiarazioneEGov(dichiarazioneEGov,context.isInformazioniEGov_wscpDisabled_namespaceCnipa());
				nomeFileInfoEGovServizioComposto = it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV;
			}
			Documento docModalitaEsplicitaCNIPAServizioComposto = new Documento(nomeFileInfoEGovServizioComposto,TipiDocumentoSemiformale.XML.toString(),egovBytesServizioComposto);
			if(context.isInformazioniEGov_specificaSemiformale())
				aSC.addSpecificaSemiformale(docModalitaEsplicitaCNIPAServizioComposto);
			else
				aSC.addAllegato(docModalitaEsplicitaCNIPAServizioComposto);
			// Definitorio.xsd
			dAllegatoDEFINITORIO = new Documento(Costanti.ALLEGATO_DEFINITORIO_XSD,"XSD","<xsd>DEFINITORIO</xsd>".getBytes());
			aSC.addAllegato(dAllegatoDEFINITORIO);

			
			// 2. Trasformazione in package
			fileName = "package.asc";
			xmlSICAUtilities.generateAccordoServizioComposto(aSC, fileName);
			
			// 3. Ritrasformazione in oggetto java
			AccordoServizioComposto asComposto = xmlSICAUtilities.getAccordoServizioComposto(fileName);
			// aggiungo mapping dei servizi SPCoop to uriAPS
			context.addMappingServizioToUriAPS(new IDServizio("SPC", "SoggettoEsempio", "SPC", "ASParteSpecifica"), 
					idAccordoFactory.getIDAccordoFromValues("ASParteSpecifica", new IDSoggetto("SPC", "SoggettoEsempio"), "2"));
			context.addMappingServizioToUriAPS(new IDServizio("SPC", "SoggettoEsempio", "SPC", "ASParteSpecifica2"), 
					idAccordoFactory.getIDAccordoFromValues("ASParteSpecifica2", new IDSoggetto("SPC", "SoggettoEsempio"), "2"));
			org.openspcoop2.core.registry.AccordoServizioParteComune asCompostoOpenSPCoop = 
				SICAtoOpenSPCoopUtilities.accordoServizioComposto_sicaToOpenspcoop(asComposto,context,log);
			ClientTest.printAccordoServizioParteComune(log,asCompostoOpenSPCoop,false,true);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			System.out.println("\n\n\n********* SCENARIO REALE AS PARTE COMUNE E SPECIFICA **************** \n\n");
			
			
			
			
			
			/* ---- TEST Scenario Accordo di Servizio ParteComune e partiSpecifiche  ----- */
			
			DriverRegistroServiziXML driverRegistroServiziASParteComuneESpecificaXML = new DriverRegistroServiziXML(dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiServizio"+File.separatorChar+"registroServizi.xml",null);
			if(driverRegistroServiziASParteComuneESpecificaXML.create==false){
				throw new Exception("RegistroServiziXML non istanziato");
			}
			String DIR_ESEMPI_AS = "ESEMPI_PACKAGE_AS";
			File fDIR_ESEMPI_AS = new File(DIR_ESEMPI_AS);
			if(fDIR_ESEMPI_AS.exists()==false){
				if(fDIR_ESEMPI_AS.mkdir()==false){
					throw new Exception("Creazione directory "+fDIR_ESEMPI_AS.getAbsolutePath()+" non riuscita");
				}
			}else{
				FileSystemUtilities.deleteDir(fDIR_ESEMPI_AS.getAbsolutePath());
				if(fDIR_ESEMPI_AS.mkdir()==false){
					throw new Exception("Creazione directory "+fDIR_ESEMPI_AS.getAbsolutePath()+" non riuscita");
				}
			}
			
			List<IDAccordo> idAccordi = driverRegistroServiziASParteComuneESpecificaXML.getAllIdAccordiServizioParteComune(null);
			if(idAccordi!=null){
				for(int i=0; i<idAccordi.size(); i++){
					org.openspcoop2.core.registry.AccordoServizioParteComune as = driverRegistroServiziASParteComuneESpecificaXML.getAccordoServizioParteComune(idAccordi.get(i));
					String nomeFile = idAccordoFactory.getUriFromIDAccordo(idAccordi.get(i)).replace(":", "_")+"."+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE;
					nomeFile = nomeFile.replace("/", "");
					nomeFile = DIR_ESEMPI_AS+File.separatorChar+nomeFile;
					String dirFiles = dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiServizio"+File.separatorChar;
					boolean servizioComposto = false;
					
					// traduzione in package CNIPA
					ClientTest.normalizzaPackageCNIPA(as,dirFiles,servizioComposto);
					AccordoServizioParteComune tmp = 
						SICAtoOpenSPCoopUtilities.accordoServizioParteComune_openspcoopToSica(as,context,log);
					if(tmp==null){
						throw new Exception("AccordoServizioParteComune ["+idAccordi.get(i)+"] non generato in formato CNIPA?");
					}
					xmlSICAUtilities.generateAccordoServizioParteComune(tmp, nomeFile);
					
					// Verifica package CNIPA
					if(context.isWSDL_XSD_allineaImportInclude()){
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio ["+idAccordoFactory.getUriFromAccordo(as)+"] verifica WSDL");
						ClientTest.verificaAccordoServizioParteComune(nomeFile,as);
						System.out.println("------------------------------------------------------------------------------------------------");
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio ["+idAccordoFactory.getUriFromAccordo(as)+"] verifica WSDL non attuata poiche' non vi e' abilitata l'opzione 'WSDL_XSD.allineaImportInclude'");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
					
					// Ritrasformazione in oggetto java
					if(context.isSICAClient_includiInfoRegistroGenerale()){
						AccordoServizioParteComune tmpRitrasformato = xmlSICAUtilities.getAccordoServizioParteComune(nomeFile);
						org.openspcoop2.core.registry.AccordoServizioParteComune asRitrasformatopenSPCoop = 
							SICAtoOpenSPCoopUtilities.accordoServizioParteComune_sicaToOpenspcoop(tmpRitrasformato,context,log);
						ClientTest.printAccordoServizioParteComune(log,asRitrasformatopenSPCoop,true,false);
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio ["+idAccordoFactory.getUriFromAccordo(as)+"] non ritrasformato in oggetto openspcoop, poiche' mancano le informazioni del registro SICA Generale (es. Soggetto Referente)");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
				}
			}	
			
			
			List<IDServizio> idServizio = driverRegistroServiziASParteComuneESpecificaXML.getAllIdServizi(null);
			if(idServizio!=null){
				for(int i=0; i<idServizio.size(); i++){
					org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = 
						driverRegistroServiziASParteComuneESpecificaXML.getAccordoServizioParteSpecifica(idServizio.get(i));
					Servizio s = asps.getServizio();
					String nomeServizio = s.getTipoSoggettoErogatore()+s.getNomeSoggettoErogatore()+"_"+
						s.getTipo()+s.getNome();
					String nomeFile = nomeServizio+"."+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA;
					nomeFile = nomeFile.replace("/", "");
					nomeFile = DIR_ESEMPI_AS+File.separatorChar+nomeFile;
					String dirFiles = dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiServizio"+File.separatorChar;
					boolean implementazioneServizioComposto = false;
					org.openspcoop2.core.registry.AccordoServizioParteComune asParteComuneDaIncludere = null;
					if(context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune()){
						asParteComuneDaIncludere = driverRegistroServiziASParteComuneESpecificaXML.getAccordoServizioParteComune(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
						if(asParteComuneDaIncludere==null){
							throw new Exception("Accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"] per il Servizio SPCoop ["+nomeServizio+"] non trovata sul Registro dei Servizi");
						}
					}
					Soggetto soggettoErogatore = driverRegistroServiziASParteComuneESpecificaXML.getSoggetto(new IDSoggetto(s.getTipoSoggettoErogatore(),s.getNomeSoggettoErogatore()));
					if(soggettoErogatore==null){
						throw new Exception("Soggetto erogatore ["+s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore()+"] per il Servizio SPCoop ["+nomeServizio+"] non trovato sul Registro dei Servizi");
					}
					
					// traduzione in package CNIPA
					ClientTest.normalizzaPackageCNIPA(asps,dirFiles,soggettoErogatore.getConnettore());
					AccordoServizioParteSpecifica tmp = 
						SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_openspcoopToSica(asps,implementazioneServizioComposto,asParteComuneDaIncludere,context,log);
					if(tmp==null){
						throw new Exception("AccordoServizioParteSpecifica ["+idAccordi.get(i)+"] non generato in formato CNIPA?");
					}
					xmlSICAUtilities.generateAccordoServizioParteSpecifica(tmp, nomeFile);

					// Verifica package CNIPA
					if(context.isWSDL_XSD_allineaImportInclude() && ( (!context.isWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune()) || context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune()) ){
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL");
						ClientTest.verificaAccordoServizioParteSpecifica(nomeFile,asps,context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune(),DIR_ESEMPI_AS,false);
						System.out.println("------------------------------------------------------------------------------------------------");
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						if(!context.isWSDL_XSD_allineaImportInclude())
							System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL non attuata poiche' non vi e' abilitata l'opzione 'WSDL_XSD.allineaImportInclude'");
						else {
							System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL non attuata poiche'  vi e' sia abilitata l'opzione 'WSDL_XSD.accordiParteSpecifica.openspcoopToSica.eliminazioneImportParteComune' che disabilitata l'opzione 'WSDL_XSD.accordiParteSpecifica.gestioneParteComune'");
						}
						System.out.println("------------------------------------------------------------------------------------------------");
					}
					
					// Ritrasformazione in oggetto java
					if(context.isSICAClient_includiInfoRegistroGenerale()){
						AccordoServizioParteSpecifica tmpRitrasformato = xmlSICAUtilities.getAccordoServizioParteSpecifica(nomeFile);
						org.openspcoop2.core.registry.AccordoServizioParteSpecifica asRitrasformatopenSPCoop = 
							SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(tmpRitrasformato,context,log);
						ClientTest.printAccordoServizioParteSpecifica(log,asRitrasformatopenSPCoop,true);
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Servizio SPCoop ["+nomeServizio+"] non ritrasformato in oggetto openspcoop, poiche' mancano le informazioni del registro SICA Generale (es. Soggetto Referente)");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
				}
			}
			
			
			

			
			
			
			
			
			
			
			
			
			
			System.out.println("\n\n\n********* SCENARIO REALE AS SERVIZI COMPOSTI E ACCORDI DI COOPERAZIONE **************** \n\n");
			
			
			
			
			
			/* ---- TEST Scenario Accordo di Cooperazione e servizi composti  ----- */
			
			DriverRegistroServiziXML driverRegistroServiziASCompostiEACooperazioneXML = new DriverRegistroServiziXML(dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiCooperazione"+File.separatorChar+"registroServizi.xml",null);
			if(driverRegistroServiziASCompostiEACooperazioneXML.create==false){
				throw new Exception("RegistroServiziXML non istanziato");
			}
			String DIR_ESEMPI_AC = "ESEMPI_PACKAGE_AC";
			File fDIR_ESEMPI_AC = new File(DIR_ESEMPI_AC);
			if(fDIR_ESEMPI_AC.exists()==false){
				if(fDIR_ESEMPI_AC.mkdir()==false){
					throw new Exception("Creazione directory "+fDIR_ESEMPI_AC.getAbsolutePath()+" non riuscita");
				}
			}else{
				FileSystemUtilities.deleteDir(fDIR_ESEMPI_AC.getAbsolutePath());
				if(fDIR_ESEMPI_AC.mkdir()==false){
					throw new Exception("Creazione directory "+fDIR_ESEMPI_AC.getAbsolutePath()+" non riuscita");
				}
			}
			
			// Lo scenario di accordi di cooperazione richiede anche il servizio sincrono precedentemente generato.
			String nomeFileTMP = "SPCMinisteroReferente_EsempioASParteComune_1."+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE;
			String nomeFileAS = DIR_ESEMPI_AS+File.separatorChar+nomeFileTMP;
			String nomeFileAC = DIR_ESEMPI_AC+File.separatorChar+nomeFileTMP;
			FileSystemUtilities.copy(nomeFileAS, nomeFileAC);
			
			
			
			List<IDAccordoCooperazione> idAccordiCooperazione = driverRegistroServiziASCompostiEACooperazioneXML.getAllIdAccordiCooperazione(null);
			if(idAccordiCooperazione!=null){
				for(int i=0; i<idAccordiCooperazione.size(); i++){
					org.openspcoop2.core.registry.AccordoCooperazione acoop = driverRegistroServiziASCompostiEACooperazioneXML.getAccordoCooperazione(idAccordiCooperazione.get(i));
					String nomeFile = idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordiCooperazione.get(i)).replace(":", "_")+"."+Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE;
					nomeFile = nomeFile.replace("/", "");
					nomeFile = DIR_ESEMPI_AC+File.separatorChar+nomeFile;
					String dirFiles = dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiCooperazione"+File.separatorChar;
					
					// traduzione in package CNIPA
					ClientTest.normalizzaPackageCNIPA(acoop,dirFiles);
					AccordoCooperazione tmp = 
						SICAtoOpenSPCoopUtilities.accordoCooperazione_openspcoopToSica(acoop,context,log);
					if(tmp==null){
						throw new Exception("AccordoCooperazione ["+idAccordiCooperazione.get(i)+"] non generato in formato CNIPA?");
					}
					xmlSICAUtilities.generateAccordoCooperazione(tmp, nomeFile);
					
					// Verifica package CNIPA non esistente, non vi sono WSDL, e' solo un documento istitutivo
					/*if(context.isWSDL_XSD_allineaImportInclude()){
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Cooperazione ["+IDAccordoCooperazione.getUriFromAccordo(acoop)+"] verifica WSDL");
						verificaAccordoServizioParteComune(nomeFile,ac);
						System.out.println("------------------------------------------------------------------------------------------------");
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Cooperazione ["+IDAccordoCooperazione.getUriFromAccordo(acoop)+"] verifica WSDL non attuata poiche' non vi e' abilitata l'opzione 'WSDL_XSD.allineaImportInclude'");
						System.out.println("------------------------------------------------------------------------------------------------");
					}*/
					
					// Ritrasformazione in oggetto java
					if(context.isSICAClient_includiInfoRegistroGenerale()){
						AccordoCooperazione tmpRitrasformato = xmlSICAUtilities.getAccordoCooperazione(nomeFile);
						org.openspcoop2.core.registry.AccordoCooperazione acRitrasformatopenSPCoop = 
							SICAtoOpenSPCoopUtilities.accordoCooperazione_sicaToOpenspcoop(tmpRitrasformato,context,log);
						ClientTest.printAccordoCooperazione(acRitrasformatopenSPCoop);
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Cooperazione ["+idAccordoCooperazioneFactory.getUriFromAccordo(acoop)+"] non ritrasformato in oggetto openspcoop, poiche' mancano le informazioni del registro SICA Generale (es. Soggetto Referente)");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
				}
			}	
			
			
			
			
			
			
			List<IDAccordo> idAccordiServiziComposti = driverRegistroServiziASCompostiEACooperazioneXML.getAllIdAccordiServizioParteComune(null);
			if(idAccordiServiziComposti!=null){
				for(int i=0; i<idAccordiServiziComposti.size(); i++){
					org.openspcoop2.core.registry.AccordoServizioParteComune as = driverRegistroServiziASCompostiEACooperazioneXML.getAccordoServizioParteComune(idAccordiServiziComposti.get(i));
					if(as.getServizioComposto()==null){
						throw new Exception("Accordo di servizio Composto ["+idAccordiServiziComposti.get(i).toString()+"] non e' un accordo di servizio composto");
					}
					String nomeFile = idAccordoFactory.getUriFromIDAccordo(idAccordiServiziComposti.get(i)).replace(":", "_")+"."+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO;
					nomeFile = nomeFile.replace("/", "");
					nomeFile = DIR_ESEMPI_AC+File.separatorChar+nomeFile;
					String dirFiles = dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiCooperazione"+File.separatorChar;
					boolean servizioComposto = true;
					
					// addMapping tra uriAPS e IDServizio SPCoop
					if(as.getServizioComposto()!=null){
						for(int j=0;j<as.getServizioComposto().sizeServizioComponenteList();j++){
							AccordoServizioParteComuneServizioCompostoServizioComponente sc = as.getServizioComposto().getServizioComponente(j);
							IDServizio idServizioComponente = new IDServizio(sc.getTipoSoggetto(), sc.getNomeSoggetto(), 
									sc.getTipo(), sc.getNome());
							org.openspcoop2.core.registry.AccordoServizioParteSpecifica servComponente = 
								driverRegistroServiziASCompostiEACooperazioneXML.getAccordoServizioParteSpecifica(idServizioComponente);
							IDAccordo idAccordoParteSpecifica = idAccordoFactory.getIDAccordoFromValues(servComponente.getNome(),
									idServizioComponente.getSoggettoErogatore(),
									servComponente.getVersione());
							context.addMappingServizioToUriAPS(idServizioComponente, idAccordoParteSpecifica);
						}
					}
					
					// traduzione in package CNIPA
					ClientTest.normalizzaPackageCNIPA(as,dirFiles,servizioComposto);
					AccordoServizioComposto tmp = 
						SICAtoOpenSPCoopUtilities.accordoServizioComposto_openspcoopToSica(as,context,log);
					if(tmp==null){
						throw new Exception("AccordoServizioComposto ["+idAccordiServiziComposti.get(i)+"] non generato in formato CNIPA?");
					}
					xmlSICAUtilities.generateAccordoServizioComposto(tmp, nomeFile);
					
					// Verifica package CNIPA
					if(context.isWSDL_XSD_allineaImportInclude()){
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio Composto  ["+idAccordoFactory.getUriFromAccordo(as)+"] verifica WSDL");
						ClientTest.verificaAccordoServizioParteComune(nomeFile,as);
						System.out.println("------------------------------------------------------------------------------------------------");
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio Composto ["+idAccordoFactory.getUriFromAccordo(as)+"] verifica WSDL non attuata poiche' non vi e' abilitata l'opzione 'WSDL_XSD.allineaImportInclude'");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
					
					// Ritrasformazione in oggetto java
					if(context.isSICAClient_includiInfoRegistroGenerale()){
						AccordoServizioComposto tmpRitrasformato = xmlSICAUtilities.getAccordoServizioComposto(nomeFile);
						org.openspcoop2.core.registry.AccordoServizioParteComune asRitrasformatopenSPCoop = 
							SICAtoOpenSPCoopUtilities.accordoServizioComposto_sicaToOpenspcoop(tmpRitrasformato,context,log);
						ClientTest.printAccordoServizioParteComune(log,asRitrasformatopenSPCoop,true,true);
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Accordo di Servizio ["+idAccordoFactory.getUriFromAccordo(as)+"] non ritrasformato in oggetto openspcoop, poiche' mancano le informazioni del registro SICA Generale (es. Soggetto Referente)");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
				}
			}	
			
			
			
			
			List<IDServizio> idServiziComponenti = driverRegistroServiziASCompostiEACooperazioneXML.getAllIdServizi(null);
			if(idServiziComponenti!=null){
				for(int i=0; i<idServiziComponenti.size(); i++){
					org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = 
						driverRegistroServiziASCompostiEACooperazioneXML.getAccordoServizioParteSpecifica(idServiziComponenti.get(i));
					Servizio s = asps.getServizio();
					String nomeServizio = s.getTipoSoggettoErogatore()+s.getNomeSoggettoErogatore()+"_"+
						s.getTipo()+s.getNome();
					String nomeFile = nomeServizio+"."+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA;
					nomeFile = nomeFile.replace("/", "");
					nomeFile = DIR_ESEMPI_AC+File.separatorChar+nomeFile;
					String dirFiles = dirRegistroServizi.getAbsolutePath()+File.separatorChar+"accordiCooperazione"+File.separatorChar;
					boolean implementazioneServizioComposto = "EsempioServizioComposto".equals(s.getNome());
					org.openspcoop2.core.registry.AccordoServizioParteComune asParteComuneDaIncludere = null;
					if(context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune()){
						if("EsempioServizioComposto".equals(s.getNome())){
							asParteComuneDaIncludere = driverRegistroServiziASCompostiEACooperazioneXML.getAccordoServizioParteComune(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
						}else{
							asParteComuneDaIncludere = driverRegistroServiziASParteComuneESpecificaXML.getAccordoServizioParteComune(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
						}
						if(asParteComuneDaIncludere==null){
							throw new Exception("Accordo di servizio parte comune ["+asps.getAccordoServizioParteComune()+"] per il Servizio SPCoop ["+nomeServizio+"] non trovata sul Registro dei Servizi");
						}
					}
					Soggetto soggettoErogatore = driverRegistroServiziASCompostiEACooperazioneXML.getSoggetto(new IDSoggetto(s.getTipoSoggettoErogatore(),s.getNomeSoggettoErogatore()));
					if(soggettoErogatore==null){
						throw new Exception("Soggetto erogatore ["+s.getTipoSoggettoErogatore()+"/"+s.getNomeSoggettoErogatore()+"] per il Servizio SPCoop ["+nomeServizio+"] non trovato sul Registro dei Servizi");
					}
					
					// traduzione in package CNIPA
					ClientTest.normalizzaPackageCNIPA(asps,dirFiles,soggettoErogatore.getConnettore());
					AccordoServizioParteSpecifica tmp = 
						SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_openspcoopToSica(asps,implementazioneServizioComposto,asParteComuneDaIncludere,context,log);
					if(tmp==null){
						throw new Exception("AccordoServizioParteSpecifica ["+idAccordi.get(i)+"] non generato in formato CNIPA?");
					}
					xmlSICAUtilities.generateAccordoServizioParteSpecifica(tmp, nomeFile);

					// Verifica package CNIPA
					if(context.isWSDL_XSD_allineaImportInclude() && ( (!context.isWSDL_XSD_accordiParteSpecifica_openspcoopToSica_eliminazioneImportParteComune()) || context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune()) ){
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL");
						ClientTest.verificaAccordoServizioParteSpecifica(nomeFile,asps,context.isWSDL_XSD_accordiParteSpecifica_gestioneParteComune(),DIR_ESEMPI_AC,implementazioneServizioComposto);
						System.out.println("------------------------------------------------------------------------------------------------");
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						if(!context.isWSDL_XSD_allineaImportInclude())
							System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL non attuata poiche' non vi e' abilitata l'opzione 'WSDL_XSD.allineaImportInclude'");
						else {
							System.out.println("Servizio SPCoop ["+nomeServizio+"] verifica WSDL non attuata poiche'  vi e' sia abilitata l'opzione 'WSDL_XSD.accordiParteSpecifica.openspcoopToSica.eliminazioneImportParteComune' che disabilitata l'opzione 'WSDL_XSD.accordiParteSpecifica.gestioneParteComune'");
						}
						System.out.println("------------------------------------------------------------------------------------------------");
					}
					
					// Ritrasformazione in oggetto java
					if(context.isSICAClient_includiInfoRegistroGenerale()){
						AccordoServizioParteSpecifica tmpRitrasformato = xmlSICAUtilities.getAccordoServizioParteSpecifica(nomeFile);
						org.openspcoop2.core.registry.AccordoServizioParteSpecifica asRitrasformatopenSPCoop = 
							SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(tmpRitrasformato,context,log);
						ClientTest.printAccordoServizioParteSpecifica(log,asRitrasformatopenSPCoop,true);
					}else{
						System.out.println("------------------------------------------------------------------------------------------------");
						System.out.println("Servizio SPCoop ["+nomeServizio+"] non ritrasformato in oggetto openspcoop, poiche' mancano le informazioni del registro SICA Generale (es. Soggetto Referente)");
						System.out.println("------------------------------------------------------------------------------------------------");
					}
				}
			}
			
			
			
			
			
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		
	}

	private static void printImportFromWSDL(Logger log,byte[] doc)throws Exception{
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		if(xmlUtils.isDocument(doc)){
			Document d = xmlUtils.newDocument(doc);
			List<Node> imports = wsdlUtilities.readImports(d);
			for(int i=0; i<imports.size(); i++){
				Node n = imports.get(i);
				String namespaceImport = null;
				try{
					namespaceImport = wsdlUtilities.getImportNamespace(n);
				}catch(Exception e){}
				String location = null;
				try{
					location = wsdlUtilities.getImportLocation(n);
				}catch(Exception e){}
				System.out.println("		  Import namespace=\""+namespaceImport+"\" location=\""+location+"\"");
			}
		}
	}
	private static void printImportIntoTypesFromWSDL(Logger log,byte[] doc)throws Exception{
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		XSDUtils xsdUtils = new XSDUtils(xmlUtils);
		if(xmlUtils.isDocument(doc)){
			Document d = xmlUtils.newDocument(doc);
			List<Node> imports = wsdlUtilities.readImportsSchemaIntoTypes(d);
			for(int i=0; i<imports.size(); i++){
				Node n = imports.get(i);
				String namespaceImport = null;
				try{
					namespaceImport = xsdUtils.getImportNamespace(n);
				}catch(Exception e){}
				String location = null;
				try{
					location = xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				System.out.println("		  Import (types.schema) namespace=\""+namespaceImport+"\" schemaLocation=\""+location+"\"");
			}
		}
	}
	private static void printIncludeIntoTypesFromWSDL(Logger log,byte[] doc)throws Exception{
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();	
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		XSDUtils xsdUtils = new XSDUtils(xmlUtils);
		if(xmlUtils.isDocument(doc)){
			Document d = xmlUtils.newDocument(doc);
			List<Node> include = wsdlUtilities.readIncludesSchemaIntoTypes(d);
			for(int i=0; i<include.size(); i++){
				Node n = include.get(i);
				String location = null;
				try{
					location = xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				System.out.println("		  Include (types.schema) schemaLocation=\""+location+"\"");
			}
		}
	}
	private static void printImportFromXSD(Logger log,byte[] doc)throws Exception{
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		XSDUtils xsdUtils = new XSDUtils(xmlUtils);
		if(xmlUtils.isDocument(doc)){
			Document d = xmlUtils.newDocument(doc);
			List<Node> imports = xsdUtils.readImports(d);
			for(int i=0; i<imports.size(); i++){
				Node n = imports.get(i);
				String namespaceImport = null;
				try{
					namespaceImport = xsdUtils.getImportNamespace(n);
				}catch(Exception e){}
				String location = null;
				try{
					location = xsdUtils.getImportSchemaLocation(n);
				}catch(Exception e){}
				System.out.println("		  Import namespace=\""+namespaceImport+"\" schemaLocation=\""+location+"\"");
			}
		}
	}
	private static void printIncludeFromXSD(Logger log,byte[] doc)throws Exception{
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		XSDUtils xsdUtils = new XSDUtils(xmlUtils);
		if(xmlUtils.isDocument(doc)){
			Document d = xmlUtils.newDocument(doc);
			List<Node> includes = xsdUtils.readIncludes(d);
			for(int i=0; i<includes.size(); i++){
				Node n = includes.get(i);
				String location = null;
				try{
					location = xsdUtils.getIncludeSchemaLocation(n);
				}catch(Exception e){}
				System.out.println("		  Include schemaLocation=\""+location+"\"");
			}
		}
	}
	private static void printElementIntoWSDL(Logger log,byte[] doc)throws Exception{
	
		AbstractXMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();		
		WSDLUtilities wsdlUtilities = WSDLUtilities.getInstance(xmlUtils);
		Document d = xmlUtils.newDocument(doc);
		wsdlUtilities.removeTypes(d);
		wsdlUtilities.removeImports(d);
				
		DefinitionWrapper wsdl = new DefinitionWrapper(d,xmlUtils);
		
		//System.out.println("PRESENTI "+wsdl.toString());
		
		// messages.
		Map<?, ?> messages = wsdl.getMessages();
		if(messages!=null && messages.size()>0){
			Iterator<?> it = messages.keySet().iterator();
			while(it.hasNext()){
				javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
				Message msg = (Message) messages.get(key);
				System.out.println("		  Message="+msg.getQName().toString());
			}
		}
		
		// port types
		Map<?, ?> porttypes = wsdl.getAllPortTypes();
		if(porttypes!=null && porttypes.size()>0){
			Iterator<?> it = porttypes.keySet().iterator();
			while(it.hasNext()){
				javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
				javax.wsdl.PortType pt = (javax.wsdl.PortType) porttypes.get(key);
				System.out.println("		  PortType="+pt.getQName().toString()+" ("+pt.getOperations().size()+" operations)");
				for(int i=0; i<pt.getOperations().size();i++){
					javax.wsdl.Operation op = (javax.wsdl.Operation) pt.getOperations().get(i);
					String tipo="InputOutput";
					if(op.getOutput()==null){
						tipo="InputOnly";
					}
					System.out.println("		  		  Operation="+op.getName()+" ("+tipo+")" );
				}
			}
		}
		
		// binding
		Map<?, ?> bindings = wsdl.getAllBindings();
		if(bindings!=null && bindings.size()>0){
			Iterator<?> it = bindings.keySet().iterator();
			while(it.hasNext()){
				javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
				Binding binding = (Binding) bindings.get(key);
				System.out.println("		  Binding="+binding.getQName().toString());
			}
		}
		
		// service
		Map<?, ?> services = wsdl.getAllServices();
		if(services!=null && services.size()>0){
			Iterator<?> it = services.keySet().iterator();
			while(it.hasNext()){
				javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
				Service service = (Service) services.get(key);
				System.out.println("		  Service="+service.getQName().toString());
			}
		}
	}
	
	
	private static void printAccordoServizioParteComune(Logger log,org.openspcoop2.core.registry.AccordoServizioParteComune asParteComuneOpenSPCoop,boolean readWSDL_XSD,boolean servizioComposto) throws Exception{
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		System.out.println("------------------------------------------------------------------------------------------------");
		System.out.println("Accordo di Servizio ["+idAccordoFactory.getUriFromAccordo(asParteComuneOpenSPCoop)+"] gestito correttamente");
		if(asParteComuneOpenSPCoop.getByteWsdlDefinitorio()!=null){
			System.out.println("- WSDL Definitorio");
			if(readWSDL_XSD){
				ClientTest.printImportFromXSD(log,asParteComuneOpenSPCoop.getByteWsdlDefinitorio());
				ClientTest.printIncludeFromXSD(log,asParteComuneOpenSPCoop.getByteWsdlDefinitorio());
			}
		}
		System.out.println("- Allegati: "+asParteComuneOpenSPCoop.sizeAllegatoList());
		for(int i=0; i<asParteComuneOpenSPCoop.sizeAllegatoList(); i++){
			org.openspcoop2.core.registry.Documento doc = asParteComuneOpenSPCoop.getAllegato(i);
			System.out.println("	- ("+doc.getTipo()+") "+doc.getFile());
			if(readWSDL_XSD){
				ClientTest.printImportFromXSD(log,doc.getByteContenuto());
				ClientTest.printIncludeFromXSD(log,doc.getByteContenuto());
			}
		}
		System.out.println("- SpecificheSemiformali: "+asParteComuneOpenSPCoop.sizeSpecificaSemiformaleList());
		for(int i=0; i<asParteComuneOpenSPCoop.sizeSpecificaSemiformaleList(); i++){
			org.openspcoop2.core.registry.Documento doc = asParteComuneOpenSPCoop.getSpecificaSemiformale(i);
			System.out.println("	- ("+doc.getTipo()+") "+doc.getFile());
			if(readWSDL_XSD){
				ClientTest.printImportFromXSD(log,doc.getByteContenuto());
				ClientTest.printIncludeFromXSD(log,doc.getByteContenuto());
			}
		}
		if(servizioComposto){
			AccordoServizioParteComuneServizioComposto asComposto = asParteComuneOpenSPCoop.getServizioComposto();
			System.out.println("- SpecificheCoordinamento: "+asComposto.sizeSpecificaCoordinamentoList());
			for(int i=0; i<asComposto.sizeSpecificaCoordinamentoList(); i++){
				org.openspcoop2.core.registry.Documento doc = asComposto.getSpecificaCoordinamento(i);
				System.out.println("	- ("+doc.getTipo()+") "+doc.getFile());
			}
			System.out.println("- ServiziComponenti: "+asComposto.sizeServizioComponenteList());
			for(int i=0; i<asComposto.sizeServizioComponenteList(); i++){
				AccordoServizioParteComuneServizioCompostoServizioComponente sComponente = asComposto.getServizioComponente(i);
				String s = "	- "+sComponente.getTipoSoggetto()+"/"+sComponente.getNomeSoggetto()+"_"+
					sComponente.getTipo()+"/"+sComponente.getNome();
				if(sComponente.getAzione()!=null){
					s = s+"_"+sComponente.getAzione();
				}
				System.out.println(s);
			}
		}
		if(asParteComuneOpenSPCoop.getByteWsdlConcettuale()!=null){
			System.out.println("- WSDL Concettuale");
			if(readWSDL_XSD){
				ClientTest.printImportFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlConcettuale());
				ClientTest.printImportIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlConcettuale());
				ClientTest.printIncludeIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlConcettuale());
				ClientTest.printElementIntoWSDL(log,asParteComuneOpenSPCoop.getByteWsdlConcettuale());
			}
		}
		if(asParteComuneOpenSPCoop.getByteWsdlLogicoErogatore()!=null){
			System.out.println("- WSDL Logico Erogatore");
			if(readWSDL_XSD){
				ClientTest.printImportFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoErogatore());
				ClientTest.printImportIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoErogatore());
				ClientTest.printIncludeIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoErogatore());
				ClientTest.printElementIntoWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoErogatore());
			}
		}
		if(asParteComuneOpenSPCoop.getByteWsdlLogicoFruitore()!=null){
			System.out.println("- WSDL Logico Fruitore");
			if(readWSDL_XSD){
				ClientTest.printImportFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoFruitore());
				ClientTest.printImportIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoFruitore());
				ClientTest.printIncludeIntoTypesFromWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoFruitore());
				ClientTest.printElementIntoWSDL(log,asParteComuneOpenSPCoop.getByteWsdlLogicoFruitore());
			}
		}
		System.out.println("- PortTypes: "+asParteComuneOpenSPCoop.sizePortTypeList());
		for(int i=0; i<asParteComuneOpenSPCoop.sizePortTypeList(); i++){
			PortType pt = asParteComuneOpenSPCoop.getPortType(i);
			System.out.println("\tPortType["+i+"]="+pt.getNome()+" (sizeAzioni:"+pt.sizeAzioneList()+") (ProfiloCollaborazione:"+pt.getProfiloCollaborazione()+")");
			for(int j=0; j<pt.sizeAzioneList(); j++){
				Operation op = pt.getAzione(j);
				if(op.getCorrelata()!=null || op.getCorrelataServizio()!=null)
					System.out.println("\t\tOperation["+j+"]="+op.getNome()+" (ProfiloCollaborazione:"+op.getProfiloCollaborazione()+") (servizioCorrelato:"+op.getCorrelataServizio()+") (correlata:"+op.getCorrelata()+")");
				else
					System.out.println("\t\tOperation["+j+"]="+op.getNome()+" (ProfiloCollaborazione:"+op.getProfiloCollaborazione()+")");
			}
		}
		System.out.println("------------------------------------------------------------------------------------------------");
	}
	private static void printAccordoServizioParteSpecifica(Logger log,org.openspcoop2.core.registry.AccordoServizioParteSpecifica aspsOpenSPCoop, boolean readWSDL) throws Exception {
		
		Servizio servizioOpenSPCoop = aspsOpenSPCoop.getServizio();
		
		System.out.println("------------------------------------------------------------------------------------------------");
		System.out.println("Servizio SPCoop (tipologia:"+servizioOpenSPCoop.getTipologiaServizio()+") ["+servizioOpenSPCoop.getTipo()+"/"+servizioOpenSPCoop.getNome()+"] (erogatore:"+
				servizioOpenSPCoop.getTipoSoggettoErogatore()+"/"+servizioOpenSPCoop.getNomeSoggettoErogatore()+") gestito correttamente");
		System.out.println("- Allegati: "+aspsOpenSPCoop.sizeAllegatoList());
		System.out.println("- SpecificheSemiformali: "+aspsOpenSPCoop.sizeSpecificaSemiformaleList());
		System.out.println("- SpecificheLivelliServizio: "+aspsOpenSPCoop.sizeSpecificaLivelloServizioList());
		System.out.println("- SpecificheSicurezza: "+aspsOpenSPCoop.sizeSpecificaSicurezzaList());
		System.out.println("- Connettore servizio: "+servizioOpenSPCoop.getConnettore().getProperty(0).getValore());
		if(aspsOpenSPCoop.getByteWsdlImplementativoErogatore()!=null){
			System.out.println("- WSDL Implementativo Erogatore");
			if(readWSDL){
				ClientTest.printImportFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoErogatore());
				ClientTest.printImportIntoTypesFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoErogatore());
				ClientTest.printIncludeIntoTypesFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoErogatore());
				ClientTest.printElementIntoWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoErogatore());
			}
		}
		if(aspsOpenSPCoop.getByteWsdlImplementativoFruitore()!=null){
			System.out.println("- WSDL Implementativo Fruitore");
			if(readWSDL){
				ClientTest.printImportFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoFruitore());
				ClientTest.printImportIntoTypesFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoFruitore());
				ClientTest.printIncludeIntoTypesFromWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoFruitore());
				ClientTest.printElementIntoWSDL(log,aspsOpenSPCoop.getByteWsdlImplementativoFruitore());
			}
		}
		System.out.println("------------------------------------------------------------------------------------------------");
	}
	private static void printAccordoCooperazione(org.openspcoop2.core.registry.AccordoCooperazione acOpenSPCoop) throws Exception {
		System.out.println("------------------------------------------------------------------------------------------------");
		System.out.println("Accordo di Cooperazione ["+IDAccordoCooperazioneFactory.getInstance().getUriFromAccordo(acOpenSPCoop)+"] gestito correttamente");
		System.out.println("- Allegati: "+acOpenSPCoop.sizeAllegatoList());
		System.out.println("- SpecificheSemiformali: "+acOpenSPCoop.sizeSpecificaSemiformaleList());
		if(acOpenSPCoop.getElencoPartecipanti()!=null){
			AccordoCooperazionePartecipanti acp = acOpenSPCoop.getElencoPartecipanti();
			System.out.println("- Partecipanti: "+acp.sizeSoggettoPartecipanteList());
			for(int i=0; i<acp.sizeSoggettoPartecipanteList(); i++){
				System.out.println("		  - "+acp.getSoggettoPartecipante(i).getTipo()+"/"+acp.getSoggettoPartecipante(i).getNome());
			}
		}else{
			System.out.println("- Partecipanti: non presenti");
		}
		System.out.println("- URIServiziComposti: "+acOpenSPCoop.sizeUriServiziCompostiList());
		for(int i=0; i<acOpenSPCoop.sizeUriServiziCompostiList(); i++){
			System.out.println("		  - "+acOpenSPCoop.getUriServiziComposti(i));
		}
		System.out.println("------------------------------------------------------------------------------------------------");
	}	

	private static void normalizzaPackageCNIPA(org.openspcoop2.core.registry.AccordoServizioParteComune as,String dirFiles, boolean servizioComposto) throws Exception{
		// imposto campi necessari per la traduzione in package CNIPA
		as.setOraRegistrazione(new Date());
		
		// WSDL
		if(as.getWsdlDefinitorio()!=null){
			String path = dirFiles + as.getWsdlDefinitorio();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			as.setByteWsdlDefinitorio(contenuto);
		}
		if(as.getWsdlConcettuale()!=null){
			String path = dirFiles + as.getWsdlConcettuale();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			as.setByteWsdlConcettuale(contenuto);
		}
		if(as.getWsdlLogicoErogatore()!=null){
			String path = dirFiles + as.getWsdlLogicoErogatore();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			as.setByteWsdlLogicoErogatore(contenuto);
		}
		if(as.getWsdlLogicoFruitore()!=null){
			String path = dirFiles + as.getWsdlLogicoFruitore();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			as.setByteWsdlLogicoFruitore(contenuto);
		}
		
		// ALLEGATI
		for(int i=0; i<as.sizeAllegatoList(); i++){
			org.openspcoop2.core.registry.Documento doc = as.getAllegato(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		// SPECIFICHE SEMIFORMALI
		for(int i=0; i<as.sizeSpecificaSemiformaleList(); i++){
			org.openspcoop2.core.registry.Documento doc = as.getSpecificaSemiformale(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		if(servizioComposto){
			if(as.getServizioComposto()==null){
				throw new Exception("ServizioComposto non e' presente");
			}else{
				for(int j=0 ; j< as.getServizioComposto().sizeSpecificaCoordinamentoList(); j++){
					org.openspcoop2.core.registry.Documento doc = as.getServizioComposto().getSpecificaCoordinamento(j);
					String path = dirFiles + doc.getFile();
					byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
					doc.setByteContenuto(contenuto);
					File f = new File(path);
					doc.setFile(f.getName());
				}
			}
		}
	}
	
	
	
	private static void normalizzaPackageCNIPA(org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,String dirFiles,Connettore connettoreSoggetto) throws Exception{
		// imposto campi necessari per la traduzione in package CNIPA
		asps.setOraRegistrazione(new Date());
		// Adesione automatica
		//asps.setTipoAdesione(TipiAdesione.AUTOMATICA.toString());
		
		Servizio servizio = asps.getServizio();
		
		// Connettore
		String url = connettoreSoggetto.getProperty(0).getValore();
		//System.out.println("URL "+url+"/TEST_"+servizio.getTipo()+servizio.getNome());
		Connettore con = new Connettore();
		con.setTipo("http");
		con.setNome("Connettore per servizio "+servizio.toString());
		Property cp = new Property();
		cp.setNome("location");
		cp.setValore(url+"/TEST_"+servizio.getTipo()+servizio.getNome());
		con.addProperty(cp);
		servizio.setConnettore(con);

		
		// WSDL
		if(asps.getWsdlImplementativoErogatore()!=null){
			String path = dirFiles + asps.getWsdlImplementativoErogatore();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			asps.setByteWsdlImplementativoErogatore(contenuto);
		}
		if(asps.getWsdlImplementativoFruitore()!=null){
			String path = dirFiles + asps.getWsdlImplementativoFruitore();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			asps.setByteWsdlImplementativoFruitore(contenuto);
		}
		
		// ALLEGATI
		for(int i=0; i<asps.sizeAllegatoList(); i++){
			org.openspcoop2.core.registry.Documento doc = asps.getAllegato(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		// SPECIFICHE SEMIFORMALI
		for(int i=0; i<asps.sizeSpecificaSemiformaleList(); i++){
			org.openspcoop2.core.registry.Documento doc = asps.getSpecificaSemiformale(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		// SPECIFICHE LIVELLI SERVIZIO
		for(int i=0; i<asps.sizeSpecificaLivelloServizioList(); i++){
			org.openspcoop2.core.registry.Documento doc = asps.getSpecificaLivelloServizio(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		// SPECIFICHE SICUREZZA
		for(int i=0; i<asps.sizeSpecificaSicurezzaList(); i++){
			org.openspcoop2.core.registry.Documento doc = asps.getSpecificaSicurezza(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
	}
	
	
	
	private static void normalizzaPackageCNIPA(org.openspcoop2.core.registry.AccordoCooperazione ac,String dirFiles) throws Exception{
		// imposto campi necessari per la traduzione in package CNIPA
		ac.setOraRegistrazione(new Date());
		
		// ALLEGATI
		for(int i=0; i<ac.sizeAllegatoList(); i++){
			org.openspcoop2.core.registry.Documento doc = ac.getAllegato(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}
		
		// SPECIFICHE SEMIFORMALI
		for(int i=0; i<ac.sizeSpecificaSemiformaleList(); i++){
			org.openspcoop2.core.registry.Documento doc = ac.getSpecificaSemiformale(i);
			String path = dirFiles + doc.getFile();
			byte [] contenuto = FileSystemUtilities.readBytesFromFile(path);
			doc.setByteContenuto(contenuto);
			File f = new File(path);
			doc.setFile(f.getName());
		}

	}
	
	
	
	
	
	private static void verificaAccordoServizioParteComune(String nomeFile,org.openspcoop2.core.registry.AccordoServizioParteComune as) throws Exception{
		
				
		File f = new File(nomeFile);
		if(f.exists()==false){
			throw new Exception("Accordo ["+nomeFile+"] non esistente");
		}
		if(f.canRead()==false){
			throw new Exception("Accordo ["+nomeFile+"] non leggibile");
		}
			
		// Interfaccia Concettuale
		String dir = "DIR_CONCETTUALE_"+nomeFile.replace("/", "");
		FileSystemUtilities.deleteDir(dir); // per essere sicuri di partire da una situazione pulita
		ZipUtilities.unzipFile(nomeFile, dir);
		System.out.println(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL +" in corso di verifica (sintassi/import/include) ...");
		if(as.getServizioComposto()==null)
			ClientTest.verificaAccordoServizioParteComune(dir, as.getNome(),Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL,true,true);
		else
			ClientTest.verificaAccordoServizioComposto(dir, as.getNome(),Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL);
		System.out.println(Costanti.SPECIFICA_INTERFACCIA_CONCETTUALE_WSDL +" correttamente formato");
		FileSystemUtilities.deleteDir(dir);
		
		// Interfaccia Logica Erogatore
		dir = "DIR_LOGICA_EROGATORE_"+nomeFile.replace("/", "");
		FileSystemUtilities.deleteDir(dir); // per essere sicuri di partire da una situazione pulita
		ZipUtilities.unzipFile(nomeFile, dir);
		System.out.println(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL +" in corso di verifica (sintassi/import/include) ...");
		if(as.getServizioComposto()==null)
			ClientTest.verificaAccordoServizioParteComune(dir, as.getNome(),Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL,true,false);
		else
			ClientTest.verificaAccordoServizioComposto(dir, as.getNome(),Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL);
		System.out.println(Costanti.SPECIFICA_INTERFACCIA_LOGICA_EROGATORE_WSDL +" correttamente formato");
		FileSystemUtilities.deleteDir(dir);
		
		// Interfaccia Logica Fruitore
		if(as.getServizioComposto()==null){
			dir = "DIR_LOGICA_FRUITORE_"+nomeFile.replace("/", "");
			FileSystemUtilities.deleteDir(dir); // per essere sicuri di partire da una situazione pulita
			ZipUtilities.unzipFile(nomeFile, dir);
			System.out.println(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL +" in corso di verifica (sintassi/import/include) ...");
			ClientTest.verificaAccordoServizioParteComune(dir, as.getNome(),Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL,false,true);
			System.out.println(Costanti.SPECIFICA_INTERFACCIA_LOGICA_FRUITORE_WSDL +" correttamente formato");
			FileSystemUtilities.deleteDir(dir);
		}
		
	}
	private static void verificaAccordoServizioParteComune(String dirName,String nomeAccordoServizioParteComune,String fileWSDL,boolean logicoErogatore,boolean logicoFruitore) throws Exception{
		
		WSDL2JAVA wsdl2java = new WSDL2JAVA();
		boolean todo = true;
		if(todo){
			throw new Exception("Implementare WSDL2JAVA indipendente dal framwework soap");
		}
				
		// Genero STUB e SKELETON PER VERIFICARE CORRETTEZZA DEI WSDL
		String [] args = new String[5];
		args[0] = "-S";
		args[1] = "true";
		args[2] = "-o";
		args[3] = "STUB_SKELETON_"+dirName;
		String dirWSDL = dirName + File.separatorChar + nomeAccordoServizioParteComune + File.separatorChar + Costanti.SPECIFICA_INTERFACCIA_DIR +  File.separatorChar;
		
		// Delete directory dove vengono prodotte le classi prima di produrre il test.
		FileSystemUtilities.deleteDir(args[3]);
		
		// Interfaccia Concettuale
		args[4] = dirWSDL + fileWSDL;
		wsdl2java.run(args);
		if(wsdl2java.getError()!=null){
			throw wsdl2java.getError();
		}
		String baseDir = args[3]+File.separatorChar+"org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
		// file comuni
		ClientTest.isFileExists(baseDir+"deploy.wsdd");
		ClientTest.isFileExists(baseDir+"Esito.java");
		ClientTest.isFileExists(baseDir+"PresaInCarico.java");
		ClientTest.isFileExists(baseDir+"IdentificativoRichiestaAsincrona.java");
		ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneRequest.java");
		ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneResponse.java");
		ClientTest.isFileExists(baseDir+"undeploy.wsdd");
		// file per logico erogatore
		if(logicoErogatore){
			ClientTest.isFileExists(baseDir+"AggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"AggiornamentoResponse.java");
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"EsempioAllegatoInclude1.java");
			ClientTest.isFileExists(baseDir+"EsempioAllegatoInclude2.java");
			ClientTest.isFileExists(baseDir+"EsempioSpecificaSemiformaleInclude1.java");
			ClientTest.isFileExists(baseDir+"EsempioSpecificaSemiformaleInclude2.java");
			ClientTest.isFileExists(baseDir+"NotificaRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaAggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaAggiornamentoResponse.java");
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneResponse.java");
			// Allegati
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport1.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport2.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"allegato"+File.separatorChar+"interno"+File.separatorChar+"EsempioAllegatoInterno.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"import2"+File.separatorChar+"AltroOggettoImportato.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"importwsdl"+File.separatorChar+"AllegatoImportatoDirettamenteInWSDL.java");
			// SpecificaSemiformale
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport1.java");
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport2.java");
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"specificasemiformale"+File.separatorChar+"interno"+File.separatorChar+"EsempioSpecificaSemiformaleInterno.java");
			//isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"importwsdl"+File.separatorChar+"SpecificaSemiformaleImportatoDirettamenteInWSDL.java");
		}
		// file per logico fruitore
		if(logicoFruitore){
			ClientTest.isFileExists(baseDir+"EsitoAggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"EsitoAggiornamentoResponse.java");
		}
		
		// Delete directory dove vengono prodotte le classi.
		FileSystemUtilities.deleteDir(args[3]);
		
	}
	private static void verificaAccordoServizioComposto(String dirName,String nomeAccordoServizioParteComune,String fileWSDL) throws Exception{
		
		WSDL2JAVA wsdl2java = new WSDL2JAVA();
				
		// Genero STUB e SKELETON PER VERIFICARE CORRETTEZZA DEI WSDL
		String [] args = new String[5];
		args[0] = "-S";
		args[1] = "true";
		args[2] = "-o";
		args[3] = "STUB_SKELETON_"+dirName;
		String dirWSDL = dirName + File.separatorChar + nomeAccordoServizioParteComune + File.separatorChar + Costanti.SPECIFICA_INTERFACCIA_DIR +  File.separatorChar;
		
		// Delete directory dove vengono prodotte le classi prima di produrre il test.
		FileSystemUtilities.deleteDir(args[3]);
		
		// Interfaccia Concettuale
		args[4] = dirWSDL + fileWSDL;
		wsdl2java.run(args);
		if(wsdl2java.getError()!=null){
			throw wsdl2java.getError();
		}
		String baseDir = args[3]+File.separatorChar+"org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
		// file comuni
		ClientTest.isFileExists(baseDir+"deploy.wsdd");
		ClientTest.isFileExists(baseDir+"Esito.java");
		ClientTest.isFileExists(baseDir+"Dati.java");
		ClientTest.isFileExists(baseDir+"ServizioCompostoRequest.java");
		ClientTest.isFileExists(baseDir+"ServizioCompostoResponse.java");
		ClientTest.isFileExists(baseDir+"undeploy.wsdd");
		
		// Delete directory dove vengono prodotte le classi.
		FileSystemUtilities.deleteDir(args[3]);
		
	}
	private static void verificaAccordoServizioParteSpecifica(String nomeFile,org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			boolean parteSpecificaContieneParteComune,String DIR_ESEMPI_AS,
			boolean implementazioneServizioComposto) throws Exception{
		
		
		File f = new File(nomeFile);
		if(f.exists()==false){
			throw new Exception("Accordo ["+nomeFile+"] non esistente");
		}
		if(f.canRead()==false){
			throw new Exception("Accordo ["+nomeFile+"] non leggibile");
		}
			
		Servizio servizio = asps.getServizio();
		
		String dir = null;
		if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
			dir = "DIR_IMPL_FRUITORE_"+nomeFile.replace("/", "");
		}else{
			dir = "DIR_IMPL_EROGATORE_"+nomeFile.replace("/", "");
		}
		FileSystemUtilities.deleteDir(dir); // per essere sicuri di partire da una situazione pulita
		
		
		if(parteSpecificaContieneParteComune==false){
			// Serve unzippato anche la parte comune
			String fPC = asps.getAccordoServizioParteComune().replace(":", "_")+".";
			if(implementazioneServizioComposto){
				fPC = fPC+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO;
			}else{
				fPC = fPC+Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE;
			}
			fPC = fPC.replace("/", "");
			fPC = DIR_ESEMPI_AS+File.separatorChar+fPC;
			File parteComune = new File(fPC);
			if(parteComune.exists()==false){
				throw new Exception("Accordo Parte Comune ["+parteComune.getAbsolutePath()+"] non esistente");
			}
			if(parteComune.canRead()==false){
				throw new Exception("Accordo Parte Comune ["+parteComune.getAbsolutePath()+"] non leggibile");
			}
			ZipUtilities.unzipFile(parteComune.getAbsolutePath(), dir);
		}
		
		
		ZipUtilities.unzipFile(nomeFile, dir);
		if(TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio())){
			System.out.println(Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL +" in corso di verifica (sintassi/import/include) ...");
			if(implementazioneServizioComposto){
				ClientTest.verificaAccordoServizioCompostoParteSpecifica(dir, asps.getNome(),servizio.getNome(),Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL);
			}else{
				ClientTest.verificaAccordoServizioParteSpecifica(dir, asps.getNome(),servizio.getNome(),Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL);
			}
			System.out.println(Costanti.SPECIFICA_PORTI_ACCESSO_FRUITORE_WSDL +" correttamente formato");
		}else{
			System.out.println(Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL +" in corso di verifica (sintassi/import/include) ...");
			if(implementazioneServizioComposto){
				ClientTest.verificaAccordoServizioCompostoParteSpecifica(dir, asps.getNome(),servizio.getNome(),Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL);
			}else{
				ClientTest.verificaAccordoServizioParteSpecifica(dir, asps.getNome(),servizio.getNome(),Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL);
			}
			System.out.println(Costanti.SPECIFICA_PORTI_ACCESSO_EROGATORE_WSDL +" correttamente formato");
		}
		
		FileSystemUtilities.deleteDir(dir);
		
	}
	private static void verificaAccordoServizioParteSpecifica(String dirName,String nomeAccordoServizioParteSpecifica,String nomeServizio,String fileWSDL) throws Exception{
		
		WSDL2JAVA wsdl2java = new WSDL2JAVA();
				
		// Genero STUB e SKELETON PER VERIFICARE CORRETTEZZA DEI WSDL
		String [] args = new String[5];
		args[0] = "-S";
		args[1] = "true";
		args[2] = "-o";
		args[3] = "STUB_SKELETON_"+dirName;

		String dirWSDL = dirName + File.separatorChar + nomeAccordoServizioParteSpecifica + File.separatorChar + Costanti.SPECIFICA_PORTI_ACCESSO_DIR +  File.separatorChar;
		
		// Delete directory dove vengono prodotte le classi prima di produrre il test.
		FileSystemUtilities.deleteDir(args[3]);
		
		// Interfaccia Concettuale
		args[4] = dirWSDL + fileWSDL;
		wsdl2java.run(args);
		if(wsdl2java.getError()!=null){
			throw wsdl2java.getError();
		}
		String baseDir = args[3]+File.separatorChar+"org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
		// file comuni
		ClientTest.isFileExists(baseDir+"deploy.wsdd");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingImpl.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingSkeleton.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingStub.java");
		ClientTest.isFileExists(baseDir+nomeServizio+".java");
		ClientTest.isFileExists(baseDir+nomeServizio+"Service.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"ServiceLocator.java");
		ClientTest.isFileExists(baseDir+"undeploy.wsdd");
		// File specifici per il servizio oneway
		if("Oneway".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"EsempioAllegatoInclude1.java");
			ClientTest.isFileExists(baseDir+"EsempioAllegatoInclude2.java");
			ClientTest.isFileExists(baseDir+"EsempioSpecificaSemiformaleInclude1.java");
			ClientTest.isFileExists(baseDir+"EsempioSpecificaSemiformaleInclude2.java");
			ClientTest.isFileExists(baseDir+"NotificaRequest.java");
			// Allegati
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport1.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"EsempioAllegatoImport2.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"_import"+File.separatorChar+"allegato"+File.separatorChar+"interno"+File.separatorChar+"EsempioAllegatoInterno.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"import2"+File.separatorChar+"AltroOggettoImportato.java");
			ClientTest.isFileExists(baseDir+"allegato"+File.separatorChar+"importwsdl"+File.separatorChar+"AllegatoImportatoDirettamenteInWSDL.java");
			// SpecificaSemiformale
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport1.java");
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"EsempioSpecificaSemiformaleImport2.java");
			ClientTest.isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"_import"+File.separatorChar+"specificasemiformale"+File.separatorChar+"interno"+File.separatorChar+"EsempioSpecificaSemiformaleInterno.java");
			//isFileExists(baseDir+"specificasemiformale"+File.separatorChar+"importwsdl"+File.separatorChar+"SpecificaSemiformaleImportatoDirettamenteInWSDL.java");
		}
		else if("Sincrono".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"AggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"AggiornamentoResponse.java");
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"Esito.java");
		}
		else if("AsincronoSimmetricoRichiesta".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"RichiestaAggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaAggiornamentoResponse.java");
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"PresaInCarico.java");
		}
		else if("AsincronoSimmetricoRisposta".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"EsitoAggiornamentoRequest.java");
			ClientTest.isFileExists(baseDir+"EsitoAggiornamentoResponse.java");
			ClientTest.isFileExists(baseDir+"Esito.java");
			ClientTest.isFileExists(baseDir+"PresaInCarico.java");
		}
		else if("AsincronoAsimmetrico".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneResponse.java");
			ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneResponse.java");
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"Esito.java");
			ClientTest.isFileExists(baseDir+"IdentificativoRichiestaAsincrona.java");
			ClientTest.isFileExists(baseDir+"PresaInCarico.java");
		}
		else if("AsincronoAsimmetricoRichiesta".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaRegistrazioneResponse.java");
			ClientTest.isFileExists(baseDir+"Dati.java");
			ClientTest.isFileExists(baseDir+"PresaInCarico.java");
		}
		else if("AsincronoAsimmetricoRisposta".equals(nomeServizio)){
			ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneRequest.java");
			ClientTest.isFileExists(baseDir+"RichiestaStatoRegistrazioneResponse.java");
			ClientTest.isFileExists(baseDir+"Esito.java");
			ClientTest.isFileExists(baseDir+"IdentificativoRichiestaAsincrona.java");
		}
		else{
			throw new Exception("Servizio ["+nomeServizio+"] non gestito ?");
		}
		
		// Delete directory dove vengono prodotte le classi.
		FileSystemUtilities.deleteDir(args[3]);
		
	}
	private static void verificaAccordoServizioCompostoParteSpecifica(String dirName,String nomeAccordoServizioParteSpecifica,String nomeServizio,String fileWSDL) throws Exception{
		
		WSDL2JAVA wsdl2java = new WSDL2JAVA();
				
		// Genero STUB e SKELETON PER VERIFICARE CORRETTEZZA DEI WSDL
		String [] args = new String[5];
		args[0] = "-S";
		args[1] = "true";
		args[2] = "-o";
		args[3] = "STUB_SKELETON_"+dirName;

		String dirWSDL = dirName + File.separatorChar + nomeAccordoServizioParteSpecifica + File.separatorChar + Costanti.SPECIFICA_PORTI_ACCESSO_DIR +  File.separatorChar;
		
		// Delete directory dove vengono prodotte le classi prima di produrre il test.
		FileSystemUtilities.deleteDir(args[3]);
		
		// Interfaccia Concettuale
		args[4] = dirWSDL + fileWSDL;
		wsdl2java.run(args);
		if(wsdl2java.getError()!=null){
			throw wsdl2java.getError();
		}
		String baseDir = args[3]+File.separatorChar+"org"+File.separatorChar+"openspcoop"+File.separatorChar+"www"+File.separatorChar+"example"+File.separatorChar;
		// file comuni
		ClientTest.isFileExists(baseDir+"deploy.wsdd");
		ClientTest.isFileExists(baseDir+"Dati.java");
		ClientTest.isFileExists(baseDir+"Esito.java");
		ClientTest.isFileExists(baseDir+"ServizioCompostoRequest.java");
		ClientTest.isFileExists(baseDir+"ServizioCompostoResponse.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingImpl.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingSkeleton.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"BindingStub.java");
		ClientTest.isFileExists(baseDir+nomeServizio+".java");
		ClientTest.isFileExists(baseDir+nomeServizio+"Service.java");
		ClientTest.isFileExists(baseDir+nomeServizio+"ServiceLocator.java");
		ClientTest.isFileExists(baseDir+"undeploy.wsdd");
		
		// Delete directory dove vengono prodotte le classi.
		FileSystemUtilities.deleteDir(args[3]);
		
	}
	
	private static AccordoServizio getManifestoAS_ParteComune(boolean generaInfoEGovComeSpecificaSemiformale,boolean generaInfoEGovFormatoClientSICA) throws Exception{
		// Manifesto
		AccordoServizio manifest = new AccordoServizio();
		manifest.setDataCreazione(new Date());
		manifest.setDataPubblicazione(new Date());
		manifest.setDescrizione("Descrizione di esempio");
		manifest.setFirmato(false);
		manifest.setNome("ASParteComune");
		manifest.setRiservato(true);
		manifest.setVersione("2");
		
		// Parte comune manifest
		it.gov.spcoop.sica.manifest.AccordoServizioParteComune parteComune = new it.gov.spcoop.sica.manifest.AccordoServizioParteComune();
		parteComune.setPubblicatore(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", true));
		// -- SpecificaConversazione
		SpecificaConversazione conversazione = new SpecificaConversazione();
		DocumentoConversazione doc = new DocumentoConversazione();
		doc.setTipo("WSBL");
		doc.setBase("ConversazioneConcettuale.wsbl");
		conversazione.setConversazioneConcettuale(doc);
		DocumentoConversazione docErogatore = new DocumentoConversazione();
		docErogatore.setTipo("WSBL");
		docErogatore.setBase("ConversazioneLogicaLatoErogatore.wsbl");
		conversazione.setConversazioneLogicaLatoErogatore(docErogatore);
		DocumentoConversazione docFruitore = new DocumentoConversazione();
		docFruitore.setTipo("WSBL");
		docFruitore.setBase("ConversazioneLogicaLatoFruitore.wsbl");
		conversazione.setConversazioneLogicaLatoFruitore(docFruitore);
		parteComune.setSpecificaConversazione(conversazione);
		// -- SpecificaInterfaccia
		SpecificaInterfaccia interfaccia = new SpecificaInterfaccia();
		DocumentoInterfaccia docI = new DocumentoInterfaccia();
		docI.setTipo("WSDL");
		docI.setBase("InterfacciaConcettuale.wsdl");
		interfaccia.setInterfacciaConcettuale(docI);
		DocumentoInterfaccia docIErogatore = new DocumentoInterfaccia();
		docIErogatore.setTipo("WSDL");
		docIErogatore.setBase("InterfacciaLogicaErogatore.wsdl");
		interfaccia.setInterfacciaLogicaLatoErogatore(docIErogatore);
		DocumentoInterfaccia docIFruitore = new DocumentoInterfaccia();
		docIFruitore.setTipo("WSDL");
		docIFruitore.setBase("InterfacciaLogicaFruitore.wsdl");
		interfaccia.setInterfacciaLogicaLatoFruitore(docIFruitore);
		parteComune.setSpecificaInterfaccia(interfaccia);
		
			
		manifest.setParteComune(parteComune);
		
		// Allegati
		ElencoAllegati allegati = new ElencoAllegati();
		allegati.addGenericoDocumento("Allegato1.doc");
		allegati.addGenericoDocumento("Allegato2.doc");
		allegati.addGenericoDocumento(Costanti.ALLEGATO_DEFINITORIO_XSD); // DefinitorioXSD
		manifest.setAllegati(allegati);
		
		// SpecificheSemiformali
		SpecificaSemiformale specifiche = new SpecificaSemiformale();
		
		DocumentoSemiformale docS1 = new DocumentoSemiformale();
		docS1.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString());
		docS1.setBase("Collaborazione.doc");
		specifiche.addDocumentoSemiformale(docS1);

		DocumentoSemiformale docS2 = new DocumentoSemiformale();
		docS2.setTipo(TipiDocumentoSemiformale.XML.toString());
		docS2.setBase("Schemi.xml");
		specifiche.addDocumentoSemiformale(docS2);
		
		manifest.setSpecificaSemiformale(specifiche);
				
		// ModalitaEsplicitaCNIPA per info egov
		if(generaInfoEGovComeSpecificaSemiformale){
			DocumentoSemiformale docSpecificaEGOV = new DocumentoSemiformale();
			docSpecificaEGOV.setTipo(TipiDocumentoSemiformale.XML.toString());
			if(generaInfoEGovFormatoClientSICA)
				docSpecificaEGOV.setBase(it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			else
				docSpecificaEGOV.setBase(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			specifiche.addDocumentoSemiformale(docSpecificaEGOV);
			manifest.setSpecificaSemiformale(specifiche);
		}
		else{
			if(generaInfoEGovFormatoClientSICA)
				allegati.addGenericoDocumento(it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			else
				allegati.addGenericoDocumento(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			manifest.setAllegati(allegati);
		}
		
		
		
		return manifest;
	}
	
	
	private static AccordoServizio getManifestoAS_ParteSpecifica() throws Exception{
		// Manifesto
		AccordoServizio manifest = new AccordoServizio();
		manifest.setDataCreazione(new Date());
		manifest.setDataPubblicazione(new Date());
		manifest.setDescrizione("Descrizione di esempio");
		manifest.setFirmato(false);
		manifest.setNome("ASParteSpecifica");
		manifest.setRiservato(true);
		manifest.setVersione("2");
		
		// Parte specifica manifest
		it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica parteSpecifica = new it.gov.spcoop.sica.manifest.AccordoServizioParteSpecifica();
		parteSpecifica.setRiferimentoParteComune(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_COMUNE, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", false), "ASParteComune", "2"));
		parteSpecifica.setAdesione(TipiAdesione.AUTOMATICA.toString());
		parteSpecifica.setErogatore(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", true));
		// -- SpecificaPortiAccesso
		SpecificaPortiAccesso portiAccesso = new SpecificaPortiAccesso();
		DocumentoInterfaccia docErogatore = new DocumentoInterfaccia();
		docErogatore.setTipo("WSDL");
		docErogatore.setBase("PortiAccessoErogatore.wsdl");
		portiAccesso.setPortiAccessoErogatore(docErogatore);
		DocumentoInterfaccia docFruitore = new DocumentoInterfaccia();
		docFruitore.setTipo("WSDL");
		docFruitore.setBase("PortiAccessoFruitore.wsdl");
		portiAccesso.setPortiAccessoFruitore(docFruitore);
		parteSpecifica.setSpecificaPortiAccesso(portiAccesso);
		// -- SpecificheLivelliServizio
		SpecificaLivelliServizio sLivServizio = new SpecificaLivelliServizio();
		DocumentoLivelloServizio dsLivServ1 = new DocumentoLivelloServizio();
		dsLivServ1.setTipo(TipiDocumentoLivelloServizio.WSLA.toString());
		dsLivServ1.setBase("LivelloServizioMinimo.wsla");
		sLivServizio.addDocumentoLivelloServizio(dsLivServ1);
		DocumentoLivelloServizio dsLivServ2 = new DocumentoLivelloServizio();
		dsLivServ2.setTipo(TipiDocumentoLivelloServizio.WSLA.toString());
		dsLivServ2.setBase("LivelloServizioOttimale.wsla");
		sLivServizio.addDocumentoLivelloServizio(dsLivServ2);
		parteSpecifica.setSpecificaLivelliServizio(sLivServizio);
		// -- SpecificheSicurezza
		SpecificaSicurezza sSicurezza = new SpecificaSicurezza();
		DocumentoSicurezza dsSicurezza1 = new DocumentoSicurezza();
		dsSicurezza1.setTipo(TipiDocumentoSicurezza.WSPOLICY.toString());
		dsSicurezza1.setBase("SicurezzaDelCanale.wspolicy");
		sSicurezza.addDocumentoSicurezza(dsSicurezza1);
		DocumentoSicurezza dsSicurezza2 = new DocumentoSicurezza();
		dsSicurezza2.setTipo(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString());
		dsSicurezza2.setBase("LineeGuida.doc");
		sSicurezza.addDocumentoSicurezza(dsSicurezza2);
		parteSpecifica.setSpecificaSicurezza(sSicurezza);
		
		manifest.setParteSpecifica(parteSpecifica);
		
		// Allegati
		ElencoAllegati elenco = new ElencoAllegati();
		elenco.addGenericoDocumento("Allegato1.doc");
		elenco.addGenericoDocumento("Allegato2.doc");
		manifest.setAllegati(elenco);
		
		// SpecificheSemiformali
		SpecificaSemiformale specifiche = new SpecificaSemiformale();
		
		DocumentoSemiformale docS1 = new DocumentoSemiformale();
		docS1.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString());
		docS1.setBase("Collaborazione.doc");
		specifiche.addDocumentoSemiformale(docS1);
		
		DocumentoSemiformale docS2 = new DocumentoSemiformale();
		docS2.setTipo(TipiDocumentoSemiformale.XML.toString());
		docS2.setBase("Schemi.xml");
		specifiche.addDocumentoSemiformale(docS2);
		
		manifest.setSpecificaSemiformale(specifiche);
		
		return manifest;
	}
	
	
	private static it.gov.spcoop.sica.manifest.AccordoCooperazione getManifestoAC() throws Exception{
		// Manifesto
		it.gov.spcoop.sica.manifest.AccordoCooperazione manifest = new it.gov.spcoop.sica.manifest.AccordoCooperazione();
		manifest.setDataCreazione(new Date());
		manifest.setDataPubblicazione(new Date());
		manifest.setDescrizione("Descrizione di esempio");
		manifest.setFirmato(false);
		manifest.setNome("AC");
		manifest.setRiservato(true);
		manifest.setVersione("2");
		
		manifest.setCoordinatore(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempioCoordinatore", true));
				
		// Allegati
		ElencoAllegati elenco = new ElencoAllegati();
		elenco.addGenericoDocumento("Allegato1.doc");
		elenco.addGenericoDocumento("Allegato2.doc");
		manifest.setAllegati(elenco);
		
		// SpecificheSemiformali
		SpecificaSemiformale specifiche = new SpecificaSemiformale();
		
		DocumentoSemiformale docS1 = new DocumentoSemiformale();
		docS1.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString());
		docS1.setBase("Collaborazione.doc");
		specifiche.addDocumentoSemiformale(docS1);
		
		DocumentoSemiformale docS2 = new DocumentoSemiformale();
		docS2.setTipo(TipiDocumentoSemiformale.XML.toString());
		docS2.setBase("Schemi.xml");
		specifiche.addDocumentoSemiformale(docS2);
		
		manifest.setSpecificaSemiformale(specifiche);
		
		// Elenco Partecipanti
		ElencoPartecipanti ePartecipanti = new ElencoPartecipanti();
		ePartecipanti.addPartecipante(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoPartecipante1", true));
		ePartecipanti.addPartecipante(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoPartecipante2", true));
		manifest.setElencoPartecipanti(ePartecipanti);
		
		// ServiziComposti
		ElencoServiziComposti eSC = new ElencoServiziComposti();
		eSC.addServizioComposto(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoErogatoreServizioComposto1", false), "ASServizioComposto1", "2"));
		eSC.addServizioComposto(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoErogatoreServizioComposto2", false), "ASServizioComposto2", "2"));
		eSC.addServizioComposto(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_COMPOSTO, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoErogatoreServizioComposto3", false), "ASServizioComposto3", "2"));
		manifest.setServiziComposti(eSC);
		
		return manifest;
	}
	
	
	
	private static ServizioComposto getManifestoASComposto(boolean generaInfoEGovComeSpecificaSemiformale,boolean generaInfoEGovFormatoClientSICA) throws Exception{
		// Manifesto
		ServizioComposto manifest = new ServizioComposto();
		manifest.setDataCreazione(new Date());
		manifest.setDataPubblicazione(new Date());
		manifest.setDescrizione("Descrizione di esempio");
		manifest.setFirmato(false);
		manifest.setNome("ASServizioComposto");
		manifest.setRiservato(true);
		manifest.setVersione("2");
		
		manifest.setPubblicatore(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", true));
		manifest.setRiferimentoAccordoCooperazione(SICAtoOpenSPCoopUtilities.buildIDAccordoCooperazioneSica("AC", "2"));
		
		// -- SpecificaConversazione
		SpecificaConversazione conversazione = new SpecificaConversazione();
		DocumentoConversazione doc = new DocumentoConversazione();
		doc.setTipo("WSBL");
		doc.setBase("ConversazioneConcettuale.wsbl");
		conversazione.setConversazioneConcettuale(doc);
		DocumentoConversazione docErogatore = new DocumentoConversazione();
		docErogatore.setTipo("WSBL");
		docErogatore.setBase("ConversazioneLogicaLatoErogatore.wsbl");
		conversazione.setConversazioneLogicaLatoErogatore(docErogatore);
		DocumentoConversazione docFruitore = new DocumentoConversazione();
		docFruitore.setTipo("WSBL");
		docFruitore.setBase("ConversazioneLogicaLatoFruitore.wsbl");
		conversazione.setConversazioneLogicaLatoFruitore(docFruitore);
		manifest.setSpecificaConversazione(conversazione);
		
		// -- SpecificaInterfaccia
		SpecificaInterfaccia interfaccia = new SpecificaInterfaccia();
		DocumentoInterfaccia docI = new DocumentoInterfaccia();
		docI.setTipo("WSDL");
		docI.setBase("InterfacciaConcettuale.wsdl");
		interfaccia.setInterfacciaConcettuale(docI);
		DocumentoInterfaccia docIErogatore = new DocumentoInterfaccia();
		docIErogatore.setTipo("WSDL");
		docIErogatore.setBase("InterfacciaLogicaErogatore.wsdl");
		interfaccia.setInterfacciaLogicaLatoErogatore(docIErogatore);
		DocumentoInterfaccia docIFruitore = new DocumentoInterfaccia();
		docIFruitore.setTipo("WSDL");
		docIFruitore.setBase("InterfacciaLogicaFruitore.wsdl");
		interfaccia.setInterfacciaLogicaLatoFruitore(docIFruitore);
		manifest.setSpecificaInterfaccia(interfaccia);
		
		// Specifica Coordinamento
		SpecificaCoordinamento speCorr = new SpecificaCoordinamento();
		DocumentoCoordinamento docCoor = new DocumentoCoordinamento();
		docCoor.setTipo(TipiDocumentoCoordinamento.BPEL.toString());
		docCoor.setBase("Generica Orchestrazione.bpel");
		speCorr.addDocumentoCoordinamento(docCoor);
		DocumentoCoordinamento docCoor2 = new DocumentoCoordinamento();
		docCoor2.setTipo(TipiDocumentoCoordinamento.WSCDL.toString());
		docCoor2.setBase("Generica Orchestrazione.wscdl");
		speCorr.addDocumentoCoordinamento(docCoor2);
		manifest.setSpecificaCoordinamento(speCorr);
		
		// Servizi Componente
		ElencoServiziComponenti componenti = new ElencoServiziComponenti();
		componenti.addServizioComponente(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", false), "ASParteSpecifica", "2"));
		componenti.addServizioComponente(SICAtoOpenSPCoopUtilities.buildIDAccordoSica(Costanti.TIPO_ACCORDO_SERVIZIO_PARTE_SPECIFICA, 
				SICAtoOpenSPCoopUtilities.buildIDSoggettoSica("SoggettoEsempio", false), "ASParteSpecifica2", "2"));
		manifest.setServiziComponenti(componenti);
		
		// Allegati
		ElencoAllegati allegati = new ElencoAllegati();
		allegati.addGenericoDocumento("Allegato1.doc");
		allegati.addGenericoDocumento("Allegato2.doc");
		allegati.addGenericoDocumento(Costanti.ALLEGATO_DEFINITORIO_XSD); // DefinitorioXSD
		manifest.setAllegati(allegati);
		
		// SpecificheSemiformali
		SpecificaSemiformale specifiche = new SpecificaSemiformale();
		
		DocumentoSemiformale docS1 = new DocumentoSemiformale();
		docS1.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString());
		docS1.setBase("Collaborazione.doc");
		specifiche.addDocumentoSemiformale(docS1);
		
		DocumentoSemiformale docS2 = new DocumentoSemiformale();
		docS2.setTipo(TipiDocumentoSemiformale.XML.toString());
		docS2.setBase("Schemi.xml");
		specifiche.addDocumentoSemiformale(docS2);
		
		manifest.setSpecificaSemiformale(specifiche);
		
		// ModalitaEsplicitaCNIPA per info egov
		if(generaInfoEGovComeSpecificaSemiformale){
			DocumentoSemiformale docSpecificaEGOV = new DocumentoSemiformale();
			docSpecificaEGOV.setTipo(TipiDocumentoSemiformale.XML.toString());
			if(generaInfoEGovFormatoClientSICA)
				docSpecificaEGOV.setBase(it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			else
				docSpecificaEGOV.setBase(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			specifiche.addDocumentoSemiformale(docSpecificaEGOV);
			manifest.setSpecificaSemiformale(specifiche);
		}
		else{
			if(generaInfoEGovFormatoClientSICA)
				allegati.addGenericoDocumento(it.gov.spcoop.sica.wscp.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			else
				allegati.addGenericoDocumento(it.cnipa.collprofiles.driver.Costanti.SPECIFICA_SEMIFORMALE_INFORMAZIONI_EGOV);
			manifest.setAllegati(allegati);
		}
		
		return manifest;
	}
	
	
	private static EgovDecllElement getDichiarazioneEGov(String nomeAccordo){
		
		EgovDecllElement egov = new EgovDecllElement();
		egov.setEGovVersion(it.cnipa.collprofiles.driver.Costanti.VERSIONE_BUSTA);
		egov.setRifDefinizioneInterfaccia(nomeAccordo);
		
		OperationListType operations = new OperationListType();
		
		OperationType tipoOneWay = new OperationType();
		tipoOneWay.setOperazione("nomeOperationOneWay"); 
		tipoOneWay.setProfiloDiCollaborazione(TipiProfiliCollaborazione.OneWay.name());
		tipoOneWay.setServizio("nomeServizioOneWay");
		operations.addOperation(tipoOneWay);
		
		OperationType tipoSincrono = new OperationType();
		tipoSincrono.setOperazione("nomeOperationSincrono"); 
		tipoSincrono.setProfiloDiCollaborazione(TipiProfiliCollaborazione.Sincrono.name());
		tipoSincrono.setServizio("nomeServizioSincrono");
		operations.addOperation(tipoSincrono);
		
		OperationType tipoAsincronoSimmetrico = new OperationType();
		tipoAsincronoSimmetrico.setOperazione("nomeOperationAsincronoSimmetricoRichiesta"); 
		tipoAsincronoSimmetrico.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoSimmetrico.name());
		tipoAsincronoSimmetrico.setServizio("nomeServizioAsincronoSimmetrico");
		tipoAsincronoSimmetrico.setOperazioneCorrelata("nomeOperationAsincronoSimmetricoRisposta");
		tipoAsincronoSimmetrico.setServizioCorrelato("nomeServizioCorrelatoAsincronoSimmetrico");
		operations.addOperation(tipoAsincronoSimmetrico);
				
		OperationType tipoAsincronoAsimmetrico = new OperationType();
		tipoAsincronoAsimmetrico.setOperazione("nomeOperationAsincronoAsimmetricoRichiesta"); 
		tipoAsincronoAsimmetrico.setProfiloDiCollaborazione(TipiProfiliCollaborazione.AsincronoAsimmetrico.name());
		tipoAsincronoAsimmetrico.setServizio("nomeServizioAsincronoAsimmetrico");
		tipoAsincronoAsimmetrico.setOperazioneCorrelata("nomeOperationAsincronoAsimmetricoRichiestaStato");
		tipoAsincronoAsimmetrico.setServizioCorrelato("nomeServizioAsincronoAsimmetrico");
		operations.addOperation(tipoAsincronoAsimmetrico);
		
		egov.setOperationList(operations);
		
		return egov;
	}
	
	private static ProfiloCollaborazioneEGOV getDichiarazioneEGovFormatoClientSICA(String nomeAccordo){
		
		ProfiloCollaborazioneEGOV egov = new ProfiloCollaborazioneEGOV();
		egov.setVersioneEGOV(it.gov.spcoop.sica.wscp.driver.Costanti.VERSIONE_BUSTA);
		egov.setRiferimentoDefinizioneInterfaccia(nomeAccordo);
		
		it.gov.spcoop.sica.wscp.OperationListType operations = new it.gov.spcoop.sica.wscp.OperationListType();
		
		it.gov.spcoop.sica.wscp.OperationType tipoOneWay = new it.gov.spcoop.sica.wscp.OperationType();
		tipoOneWay.setOperazione("nomeOperationOneWay"); 
		tipoOneWay.setProfiloDiCollaborazione(it.gov.spcoop.sica.wscp.driver.TipiProfiliCollaborazione.EGOV_IT_MessaggioSingoloOneWay.name());
		tipoOneWay.setServizio("nomeServizioOneWay");
		operations.addCollaborazione(tipoOneWay);
		
		it.gov.spcoop.sica.wscp.OperationType tipoSincrono = new it.gov.spcoop.sica.wscp.OperationType();
		tipoSincrono.setOperazione("nomeOperationSincrono"); 
		tipoSincrono.setProfiloDiCollaborazione(it.gov.spcoop.sica.wscp.driver.TipiProfiliCollaborazione.EGOV_IT_ServizioSincrono.name());
		tipoSincrono.setServizio("nomeServizioSincrono");
		operations.addCollaborazione(tipoSincrono);
		
		it.gov.spcoop.sica.wscp.OperationType tipoAsincronoSimmetrico = new it.gov.spcoop.sica.wscp.OperationType();
		tipoAsincronoSimmetrico.setOperazione("nomeOperationAsincronoSimmetricoRichiesta"); 
		tipoAsincronoSimmetrico.setProfiloDiCollaborazione(it.gov.spcoop.sica.wscp.driver.TipiProfiliCollaborazione.EGOV_IT_ServizioAsincronoSimmetrico.name());
		tipoAsincronoSimmetrico.setServizio("nomeServizioAsincronoSimmetrico");
		tipoAsincronoSimmetrico.setOperazioneCorrelata("nomeOperationAsincronoSimmetricoRisposta");
		tipoAsincronoSimmetrico.setServizioCorrelato("nomeServizioCorrelatoAsincronoSimmetrico");
		operations.addCollaborazione(tipoAsincronoSimmetrico);
				
		it.gov.spcoop.sica.wscp.OperationType tipoAsincronoAsimmetrico = new it.gov.spcoop.sica.wscp.OperationType();
		tipoAsincronoAsimmetrico.setOperazione("nomeOperationAsincronoAsimmetricoRichiesta"); 
		tipoAsincronoAsimmetrico.setProfiloDiCollaborazione(it.gov.spcoop.sica.wscp.driver.TipiProfiliCollaborazione.EGOV_IT_ServizioAsincronoAsimmetrico.name());
		tipoAsincronoAsimmetrico.setServizio("nomeServizioAsincronoAsimmetrico");
		tipoAsincronoAsimmetrico.setOperazioneCorrelata("nomeOperationAsincronoAsimmetricoRichiestaStato");
		tipoAsincronoAsimmetrico.setServizioCorrelato("nomeServizioAsincronoAsimmetrico");
		operations.addCollaborazione(tipoAsincronoAsimmetrico);
		
		egov.setListaCollaborazioni(operations);
		
		return egov;
	}	
	
	public static void isFileExists(String file) throws Exception{
		if ((new File(file)).exists()==false){
			throw new Exception(file+" non esistente? Wsdl2Java non ha prodotto l'output atteso");
		}
	}
	
	/*
	protected static class WSDL2JAVA extends WSDL2Java {
		
		private Exception error;
		
		public Exception getError() {
			return this.error;
		}

		@SuppressWarnings("unqualified-field-access")
		@Override
		public void run(String[] args) {

	        // Parse the arguments
	        CLArgsParser argsParser = new CLArgsParser(args, options);

	        // Print parser errors, if any
	        if (null != argsParser.getErrorString()) {
	        	printUsage();
	        	this.error = new Exception(Messages.getMessage("error01", argsParser.getErrorString()));
	        	return;
	        }

	        // Get a list of parsed options
	        List<?> clOptions = argsParser.getArguments();
	        int size = clOptions.size();

	        try {

	            // Parse the options and configure the emitter as appropriate.
	            for (int i = 0; i < size; i++) {
	                parseOption((CLOption) clOptions.get(i));
	            }

	            // validate argument combinations
	            // 
	            validateOptions();
	            parser.run(wsdlURI);

	        } catch (Throwable t) {
	        	this.error = new Exception("Generazione Stub/Skeleton tramite WSDL2Java fallita: "+t.getMessage(),t);
	        }
	    }   
	}
	*/
	protected static class WSDL2JAVA{
		private Exception error;
		
		public Exception getError() {
			return this.error;
		}
		public void run(String[] args) {
			
		}
	}
}
