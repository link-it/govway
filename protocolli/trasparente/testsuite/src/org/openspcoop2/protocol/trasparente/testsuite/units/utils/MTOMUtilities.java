package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.io.input.ReaderInputStream;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExample;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP11Service;
import org.openspcoop2.example.server.mtom.ws.MTOMServiceExampleSOAP12Service;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.testsuite.core.FatalTestSuiteException;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.db.DatiServizioAzione;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MTOMUtilities {

	/** Gestore della Collaborazione di Base */
	private static CooperazioneBaseInformazioni info = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	
	
	
	private static boolean init = false;
	private static XMLDiff xmlDiff = null;
	private static synchronized void init() throws XMLException{
		if(init==false){
			xmlDiff = new XMLDiff();
			xmlDiff.initialize(XMLDiffImplType.XML_UNIT, new XMLDiffOptions());
			init = true;
		}
	}
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	public static void testMTOM(boolean soap11, boolean portaDelegata, String url, String username, String password, boolean addIDUnivoco) throws Exception{
		
        init();
		
        String servizio = null;
        
		MTOMServiceExample port = null;
		if(soap11){
			MTOMServiceExampleSOAP11Service ss = new MTOMServiceExampleSOAP11Service();
			port = ss.getMTOMServiceExampleSOAP11InterfaceEndpoint();  
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP11;
		}
		else{
			MTOMServiceExampleSOAP12Service ss = new MTOMServiceExampleSOAP12Service();
			port = ss.getMTOMServiceExampleSOAP12InterfaceEndpoint();
			servizio = CostantiTestSuite.PROXY_NOME_SERVIZIO_MTOM_SOAP12;
		}
		 
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,  url);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
    	((BindingProvider)port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		   	
    	String file = org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getXmlSenzaSoap();
    	byte [] xml = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(file);
    	Document d = XMLUtils.getInstance().newDocument(xml);
    	if(addIDUnivoco){
    		Element elem = d.createElementNS("http://www.openspcoop.org", "test:idUnivoco");
    		String idUnivoco = "ID-"+SOAPEngine.getIDUnivoco();
    		elem.setNodeValue(idUnivoco);
    		d.getDocumentElement().appendChild(elem);
    	}
    	javax.xml.transform.Source _echo_imageData = new DOMSource(d.getDocumentElement());
    	
    	java.util.List<javax.activation.DataHandler> _echo_other = new ArrayList<javax.activation.DataHandler>();
    	File f_other1 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getAttachmentsFilePDF());
    	if(f_other1!=null){
    		FileDataSource fDS = new FileDataSource(f_other1);
    		javax.activation.DataHandler dh = new DataHandler(fDS);
    		_echo_other.add(dh);
    	}
    	File f_other2 = new File(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getAttachmentsFileZIP());
    	if(f_other2!=null){
    		FileDataSource fDS = new FileDataSource(f_other2);
    		javax.activation.DataHandler dh = new DataHandler(fDS);
    		_echo_other.add(dh);
    	}
    	
        javax.xml.ws.Holder<java.lang.String> _echo_risposta = new javax.xml.ws.Holder<java.lang.String>();
        javax.xml.ws.Holder<javax.xml.transform.Source> _echo_imageDataResponse = new javax.xml.ws.Holder<javax.xml.transform.Source>();
        javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>> _echo_otherResponse = new javax.xml.ws.Holder<java.util.List<javax.activation.DataHandler>>();
        
        port.echo(file, _echo_imageData, _echo_other,
        		_echo_risposta, _echo_imageDataResponse, _echo_otherResponse);

        Reporter.log("echo._echo_risposta=" + _echo_risposta.value);
        Assert.assertEquals(file, _echo_risposta.value); 
        Assert.assertTrue(_echo_imageDataResponse.value instanceof javax.xml.transform.stream.StreamSource);
        javax.xml.transform.stream.StreamSource ssi = (javax.xml.transform.stream.StreamSource) _echo_imageDataResponse.value;
        ReaderInputStream ris = new ReaderInputStream(ssi.getReader());
        Document dResponse = XMLUtils.getInstance().newDocument(ris);

        boolean diff = xmlDiff.diff(d, dResponse);
		if(!diff){
			System.out.println("Request ["+XMLUtils.getInstance().toString(d)+"]");
			System.out.println("Response ["+XMLUtils.getInstance().toString(dResponse)+"]");
			System.out.println("Diff: "+xmlDiff.getDifferenceDetails());
		}
		Assert.assertTrue(diff);
		
		Reporter.log("echo._echo_other.size=" +_echo_otherResponse.value.size());
		Assert.assertEquals(_echo_other.size(),_echo_otherResponse.value.size());
		
		java.util.List<javax.activation.DataHandler> other = _echo_otherResponse.value;
		for (int i = 0; i < other.size(); i++) {
    		javax.activation.DataHandler dh = other.get(i);
    		//System.out.println("richiesta.other.size[i] received: "+dh.getContent().getClass().getName());
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
    		InputStream is = dh.getInputStream();
    		int letti = 0;
    		byte [] buffer = new byte[1024];
    		while((letti=is.read(buffer))!=-1){
    			bout.write(buffer, 0, letti);
    		}
    		bout.flush();
    		bout.close();
    		
    		byte[] received = bout.toByteArray();
    		byte [] src = null;
    		String nomeFile = null;
    		if(i==0){
    			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other1);
    			nomeFile = f_other1.getAbsolutePath();
    		}
    		else{
    			src = org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(f_other2);
    			nomeFile = f_other2.getAbsolutePath();
    		}

			boolean check = Arrays.equals(src,received);
			if(!check){
				File f = File.createTempFile("test", ".bin");
				org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(f, received);
				System.out.println("Contenuto ricevuto (salvato in "+f.getAbsolutePath()+") differente dal contenuto atteso inviato presente nel file "+nomeFile);
			}
			Assert.assertTrue(check);
		}
		
		@SuppressWarnings("unchecked")
		Map<String,java.util.List<String>> headers = (Map<String,java.util.List<String>>)((BindingProvider)port).getResponseContext().
				get(MessageContext.HTTP_RESPONSE_HEADERS);
		String id = headers.get("X-OpenSPCoop2-IdMessaggio").get(0);
	
		
		DatabaseComponent data = null;
		try{
			boolean checkServizioApplicativo = false;
			int numeroAttachments = 3;
			data = DatabaseProperties.getDatabaseComponentErogatore();
			testSincrono(data, id,
					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
					CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
					CostantiTestSuite.PROXY_TIPO_SERVIZIO, servizio,CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
					false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI,
					checkServizioApplicativo, null, null, null, false, numeroAttachments, false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}

	}
	
	private static void testSincrono(DatabaseComponent data,String id,
			IDSoggetto mittente, IDSoggetto destinatario,
			String tipoServizio,String servizio,String azione,
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdk,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,
			boolean manifestAbilitato, Integer numeroAttachments, boolean responseIsFault) throws FatalTestSuiteException{
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, mittente, null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio ["+servizio+"] Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro, inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachments);
		if(checkServizioApplicativo){
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("----------------------------------------------------------");

		Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, destinatario, null));
		Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro,inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}
		
		if(!responseIsFault)
			checkCountAttachmentsResponse(data, id, null, manifestAbilitato,  numeroAttachments);
	}
	private static void checkCountAttachmentsRequest(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
		_checkAttachments(true, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	private static void checkCountAttachmentsResponse(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
		_checkAttachments(false, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	private static void _checkAttachments(boolean isRequest,DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
			
		try{
			Thread.sleep(1250);
		}catch(Exception e){}
		
		try{
			
			int countAttachments = numeroAttachments;
			if(withManifest){
				countAttachments = countAttachments+1; //manifest comporta che il body originale finisca come allegato
			}
			
			if(isRequest){
				if(datiServizioAzione!=null){
					long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id, datiServizioAzione);
					Reporter.log("Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
				else{
					long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id);
					Reporter.log("Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
			}
			else{
				if(datiServizioAzione!=null){
					long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id, datiServizioAzione);
					Reporter.log("Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
				else{
					long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id);
					Reporter.log("Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
					Assert.assertTrue(count==countAttachments);
				}
			}
		}catch(Exception e){
			throw new  FatalTestSuiteException(e.getMessage());
		}
	}

}
