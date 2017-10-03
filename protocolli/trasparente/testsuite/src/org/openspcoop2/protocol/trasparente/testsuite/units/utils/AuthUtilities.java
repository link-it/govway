/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.services.connector.RicezioneBusteConnector;
import org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiConnector;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.utils.ErroreApplicativoUtilities;
import org.openspcoop2.testsuite.units.utils.OpenSPCoopDetailsUtilities;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * AuthUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthUtilities {

	/** Gestore della Collaborazione di Base */
	private static CooperazioneBaseInformazioni infoPortaDelegata = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
//	private static CooperazioneBaseInformazioni infoPortaApplicativa = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
//			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
//				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	
	
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, boolean addIDUnivoco,
			String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		test(true, nomePorta, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, false);
	}
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, boolean addIDUnivoco,
			String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		test(true, nomePorta, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, checkOpenSPCoopDetail);
	}
	
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, boolean addIDUnivoco,
			String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		test(false, nomePorta, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, false);
	}
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, boolean addIDUnivoco,
			String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		test(false, nomePorta, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, checkOpenSPCoopDetail);
	}
	
	private static void test(boolean portaDelegata, String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, boolean addIDUnivoco,
			String erroreAtteso, CodiceErroreIntegrazione codiceErroreIntegrazione, CodiceErroreCooperazione codiceErroreCooperazone, boolean ricercaEsatta, Date dataInizioTest,
			IDSoggetto fruitore,IDSoggetto erogatore, int returnCodeAtteso, 
			boolean checkOpenSPCoopDetail // presente solamente in caso di errore di processamento
			) throws Exception{
		
		java.io.FileInputStream fin = null;
		Repository repository=new Repository();
		int stato = -1;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			if(credenzialiInvocazione!=null && credenzialiInvocazione.isCreateSSLContext()){
				Reporter.log("Creo contesto SSL");
				sslContext = new Hashtable<String, String>();
				if(credenzialiInvocazione.getPathKeystore()!=null){
					sslContext.put("trustStoreLocation", credenzialiInvocazione.getPathKeystore());
					sslContext.put("keyStoreLocation", credenzialiInvocazione.getPathKeystore());
				}
				else {
					throw new Exception("Keystore path undefined");
				}
				if(credenzialiInvocazione.getPasswordKeystore()!=null){
					sslContext.put("trustStorePassword", credenzialiInvocazione.getPasswordKeystore());
					sslContext.put("keyStorePassword", credenzialiInvocazione.getPasswordKeystore());
				}
				else {
					throw new Exception("Keystore password undefined");
				}
				if(credenzialiInvocazione.getPasswordKey()!=null){
					sslContext.put("keyPassword", credenzialiInvocazione.getPasswordKey());
				}
				else {
					throw new Exception("Key password undefined");
				}
				sslContext.put("hostnameVerifier", "false");
			}
	
			ClientHttpGenerico client=new ClientHttpGenerico(repository,sslContext);
			client.setSoapAction("\"TEST\"");
			if(portaDelegata) {
				if(credenzialiInvocazione!=null && TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_openspcoop2Sec()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_openspcoop2Sec());
				}
				else if(sslContext!=null){
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
				}else{
					//System.out.println("NoLocation ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
				}
			}
			else {
				if(credenzialiInvocazione!=null && TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_openspcoop2Sec()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_openspcoop2Sec());
				}
				else if(sslContext!=null){
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient());
				}else{
					//System.out.println("NoLocation ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
				}
			}
			client.setPortaDelegata(nomePorta);
			client.connectToSoapEngine();
			if(credenzialiInvocazione.getUsername()!=null) {
				client.setUsername(credenzialiInvocazione.getUsername());
			}
			if(credenzialiInvocazione.getPassword()!=null) {
				client.setPassword(credenzialiInvocazione.getPassword());
			}
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			client.setAttesaTerminazioneMessaggi(false);
			try {
				Reporter.log("Invoco...");
				client.run();
				stato = client.getCodiceStatoHTTP();
				if(erroreAtteso!=null) {
					if(stato==200 && returnCodeAtteso!=200) {
						throw new Exception("Atteso errore, ritornato ["+stato+"]");
					}
				}
				if(stato!=200) {
					// ho un codice differente da 200 ok e non è stato generato un SOAPFault
					// Si tratta di una terminazione http effettuata dal container, non ha senso effettuare ulteriori controlli
					Reporter.log("ReturnCode ritornato["+stato+"] atteso["+returnCodeAtteso+"]");
					Assert.assertTrue(stato==returnCodeAtteso);
					System.out.println("Richiesta bloccata dal container: "+stato);
					return;
				}
				Reporter.log("Invocazione terminata");
			} catch (AxisFault error) {
				stato = client.getCodiceStatoHTTP();
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				TipoPdD tipoPdD = null;
				String modulo = null;
				
				if(portaDelegata) {
					
					tipoPdD = TipoPdD.DELEGATA;
					modulo = RicezioneContenutiApplicativiConnector.ID_MODULO;
					Reporter.log("Modulo ["+modulo+"]");
					
					Reporter.log("Controllo actor code ["+CostantiPdD.OPENSPCOOP2+"]");
					Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
					
					Reporter.log("Controllo fault code ["+Utilities.toString(codiceErroreIntegrazione)+"]");
					Assert.assertTrue(Utilities.toString(codiceErroreIntegrazione).equals(error.getFaultCode().getLocalPart()));
					
					Reporter.log("Controllo fault string ["+erroreAtteso+"]");
					if(ricercaEsatta) {
						Assert.assertTrue(erroreAtteso.equals(error.getFaultString()));
					}
					else {
						Assert.assertTrue(error.getFaultString().contains(erroreAtteso));
					}
					
					
					// errore applicativo
					Assert.assertTrue(ErroreApplicativoUtilities.existsErroreApplicativo(error)); // vengono generati in caso di 5XX
					
					List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
							new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
						org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
						ecc.setCodice(Utilities.toString(codiceErroreIntegrazione));
						ecc.setDescrizione(erroreAtteso);
						ecc.setCheckDescrizioneTramiteMatchEsatto(ricercaEsatta);
						eccezioni.add(ecc);
					
						ErroreApplicativoUtilities.verificaFaultErroreApplicativo(error, 
							fruitore,tipoPdD,modulo, 
							codiceErroreIntegrazione, erroreAtteso, ricercaEsatta);
					
					
				}
				else {
					
					tipoPdD = TipoPdD.APPLICATIVA;
					modulo = RicezioneBusteConnector.ID_MODULO;
					Reporter.log("Modulo ["+modulo+"]");
					
					Reporter.log("Controllo actor code is null");
					Assert.assertTrue(error.getFaultActor()==null);
					
					Reporter.log("Controllo fault code [Client]");
					Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
					
					Reporter.log("Controllo fault string ["+MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString()+"]");
					Assert.assertTrue(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString().equals(error.getFaultString()));
					
					
					// errore applicativo
					Assert.assertTrue(ErroreApplicativoUtilities.existsErroreApplicativo(error)); // vengono generati in caso di 5XX
					
					List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
							new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
						org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
						ecc.setCodice(Utilities.toString(codiceErroreCooperazone));
						ecc.setDescrizione(erroreAtteso);
						ecc.setCheckDescrizioneTramiteMatchEsatto(ricercaEsatta);
						eccezioni.add(ecc);
					
						ErroreApplicativoUtilities.verificaFaultErroreApplicativo(error, 
							erogatore,tipoPdD,
							"PortaApplicativa",//modulo, 
							codiceErroreCooperazone, erroreAtteso, ricercaEsatta);
					
					
					if(checkOpenSPCoopDetail){
						
						// openspcoop detail
						
						Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
						
						List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioniOD = 
								new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
							org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail eccOD = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
							eccOD.setCodice(Utilities.toString(codiceErroreCooperazone));
							eccOD.setDescrizione(erroreAtteso);
							eccOD.setCheckDescrizioneTramiteMatchEsatto(ricercaEsatta);
							eccezioniOD.add(eccOD);
						
						OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
								erogatore,TipoPdD.APPLICATIVA,modulo, 
								eccezioniOD, null);
					}

				}	

			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
		
		Reporter.log("ReturnCode ritornato["+stato+"] atteso["+returnCodeAtteso+"]");
		Assert.assertTrue(stato==returnCodeAtteso);

		DatabaseComponent data = null;
		String id = repository.getNext();
		Reporter.log("ID Messaggio["+id+"]");
		if(erroreAtteso==null) {
			Assert.assertTrue(id!=null);
		}
		boolean checkDB = (id!=null);
		if(portaDelegata) {
			checkDB = (stato==200);
		}
		if(checkDB) {
			try{
				boolean checkServizioApplicativo = false;
				if(portaDelegata)
					data = DatabaseProperties.getDatabaseComponentFruitore();
				else
					data = DatabaseProperties.getDatabaseComponentErogatore();
				testSincrono(data, id,
						fruitore, erogatore,
						CostantiTestSuite.SOAP_TIPO_SERVIZIO, CostantiTestSuite.SOAP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO,
						false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI,
						checkServizioApplicativo, null, null, null);
			}catch(Exception e){
				throw e;
			}finally{
				data.close();
			}
		}
		

		DatabaseMsgDiagnosticiComponent dataMsg = null;
		if(erroreAtteso!=null) {
			try{
				if(portaDelegata)
					dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
				else
					dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
				if(id!=null){
					if(returnCodeAtteso!=200) {
						Assert.assertTrue(dataMsg.isTracedErrorMsg(id));
					}
					// il messaggio di errore eventuale viene comunque registrato con livello info, se siamo in un contesto opzionale o di warning
	    			if(ricercaEsatta){
	    				Assert.assertTrue(dataMsg.isTracedMessaggio(id, erroreAtteso));
	    			}
	    			else{
	    				if(returnCodeAtteso==200 && portaDelegata) {
	    					boolean withId = dataMsg.isTracedMessaggio(id, true, erroreAtteso);
	    					boolean withoutId = dataMsg.isTracedMessaggioWithLike(dataInizioTest, erroreAtteso);
	    					Assert.assertTrue(withId || withoutId);
	    				}
	    				else {
	    					Assert.assertTrue(dataMsg.isTracedMessaggio(id, true, erroreAtteso));
	    				}
	    			}
				}
				else{
					Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, erroreAtteso));
				}
	
			}catch(Exception eInternal){
				throw eInternal;
			}finally{
				dataMsg.close();
			}
		}
			
	}
	
	private static void testSincrono(DatabaseComponent data,String id,
			IDSoggetto mittente, IDSoggetto destinatario,
			String tipoServizio,String servizio,String azione,
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdk,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws Exception{
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id+ " atteso:["+mittente+"] ");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, mittente, null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id+ " atteso:["+destinatario+"] ");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id+ " atteso:["+tipoTempoAtteso+"] sdk["+tipoTempoAttesoSdk+"]");
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio ["+servizio+"] Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, infoPortaDelegata.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta con id: " +id+ " atteso:["+azione+"] ");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id+ " atteso:["+infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono()+"] SINCRONO");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono(), 
				ProfiloDiCollaborazione.SINCRONO));
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
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
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

	}
}
