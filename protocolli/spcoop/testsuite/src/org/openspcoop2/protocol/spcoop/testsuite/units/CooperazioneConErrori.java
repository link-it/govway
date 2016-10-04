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




package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.db.DatiServizioAzione;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Gestione degli errori
 *  
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CooperazioneConErrori {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "CooperazioneConErrori";

	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	private ProfiloDiCollaborazione toProfiloCollaborazioneSdk(String profiloCollaborazione){
		if(profiloCollaborazione==null){
			return null;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY.equals(profiloCollaborazione)){
			return ProfiloDiCollaborazione.ONEWAY;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO.equals(profiloCollaborazione)){
			return ProfiloDiCollaborazione.SINCRONO;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profiloCollaborazione)){
			return ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		}
		else if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profiloCollaborazione)){
			return ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		}
		else{
			return ProfiloDiCollaborazione.UNKNOWN;
		}
	}
	
	private void invocaServizio(Repository repository,String portaDelegata,
			String username,String password,
			ParametriCooperazioneConErrori param) throws Exception{
		invocaServizio(repository, portaDelegata, username, password, param, null);
	}
	
	private void invocaServizio(Repository repository,String portaDelegata,
			String username,String password,
			ParametriCooperazioneConErrori param,
			Integer timeout) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		java.io.FileInputStream fin = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			if(username!=null && password!=null){
				client.setAutenticazione(username, password);
			}
			// AttesaTerminazioneMessaggi
			if(param.isModalitaAsincrona()){
				if(param.isEliminaMessaggioFruitore() || param.isVerificaRollbackNonEffettuato()){
					dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				}
				if(param.isEliminaMessaggioErogatore() || param.isVerificaRollbackNonEffettuato()){
					dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
				}
			}else{
				if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
					dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
					dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
					
					client.setAttesaTerminazioneMessaggi(true);
					client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
					client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				}
			}
			String id = null;
			try {
				if(timeout!=null){
					client.setConnectTimedOut(timeout);
					client.setConnectionReadTimeout(timeout);
				}else{
					// cmq tengo il parametro parecchio alto
					client.setConnectTimedOut(6000);
					client.setConnectionReadTimeout(6000);
				}
				client.run();
				if(param.isInvocazioneOK()==false){
					Reporter.log("Invocazione PD non ha causato errori.");
					throw new TestSuiteException("Invocazione PD non ha causato errori.");
				}else{
					if(param.isModalitaAsincrona()){
						
						try{
							Thread.sleep(3000);
						}catch(Exception e){}
						
						id = repository.getNext();
						int i=0;
						while(i<param.getIndexIdMessaggioDaEliminare()){
							id = repository.getNext();
							i++;
						}
												
						if(param.getMotivoErroreProcessamento()!=null){
							String motivoErroreProcessamento = null;
							if(param.isEliminaMessaggioErogatore()){
								motivoErroreProcessamento = dbComponentErogatore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "INBOX");
							}else{
								motivoErroreProcessamento = dbComponentFruitore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "OUTBOX");
							}
							try{
								if(motivoErroreProcessamento==null)
									throw new Exception("Il motivo dell'errore di processamento ["+motivoErroreProcessamento+"] non combacia con quello atteso ["+param.getMotivoErroreProcessamento_toString()+"]");
								if(param.matchMotivoErroreProcessamento(motivoErroreProcessamento) == false ){
									throw new Exception("Il motivo dell'errore di processamento ["+motivoErroreProcessamento+"] non combacia con quello atteso ["+param.getMotivoErroreProcessamento_toString()+"]");
								}
							}catch(Exception e){
								if(param.getMotivoErroreProcessamento_alternativoConfigurazioneDB()!=null){
									if(motivoErroreProcessamento==null){
										throw new Exception("Il motivo dell'errore di processamento ["+motivoErroreProcessamento+"] non combacia con quello atteso ["+param.getMotivoErroreProcessamento_toString()+"] ne con quello alternativo ["+param.getMotivoErroreProcessamento_alternativoConfigurazioneDB_toString()+"]");
									}
									if(param.matchMotivoErroreProcessamento_alternativoConfigurazioneDB(motivoErroreProcessamento) == false ){
										throw new Exception("Il motivo dell'errore di processamento ["+motivoErroreProcessamento+"] non combacia con quello atteso ["+param.getMotivoErroreProcessamento_toString()+"] ne con quello alternativo ["+param.getMotivoErroreProcessamento_alternativoConfigurazioneDB_toString()+"]");
									}
								}else{
									throw e;
								}
							}
						}
						
						
						if(param.isVerificaRollbackProcessamento()){
							
							String motivoErroreProcessamento = null;
							if(param.isEliminaMessaggioErogatore()){
								motivoErroreProcessamento = dbComponentErogatore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "INBOX");
							}else{
								motivoErroreProcessamento = dbComponentFruitore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "OUTBOX");
							}
							
							if(motivoErroreProcessamento==null || motivoErroreProcessamento.startsWith("[spedizione n.1]")==false){
								// NOTA: in caso di errore verifica di avere impostato a 1 la cadenza di rispedizione in configurazione
								throw new Exception("Messaggio per motivo di rollback messaggio non conforme a quello atteso (Spedizione N.1)");
							}
							
							int attesa = 0;
							while(attesa<=180000){
								System.out.println("Attendo rispedizione (secondi di attesa attuali:"+(attesa/1000)+") (Timeout 3 minuti) (PD:"+portaDelegata+")...");
								try{
									Thread.sleep(5000);
								}catch(Exception e){}
								attesa=attesa+5000;
								
								if(param.isEliminaMessaggioErogatore()){
									motivoErroreProcessamento = dbComponentErogatore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "INBOX");
								}else{
									motivoErroreProcessamento = dbComponentFruitore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "OUTBOX");
								}
								
								if(motivoErroreProcessamento!=null && motivoErroreProcessamento.startsWith("[spedizione n.2]"))
									break;
							}
								
							if(motivoErroreProcessamento==null || motivoErroreProcessamento.startsWith("[spedizione n.2]")==false){
								// NOTA: in caso di errore verifica di avere impostato a 1 la cadenza di rispedizione in configurazione
								throw new Exception("Messaggio per motivo di rollback messaggio non conforme a quello atteso (Spedizione N.2)");
							}
							
						}
						
						if(param.isVerificaRollbackNonEffettuato()){
							if(dbComponentFruitore.getVerificatoreMessaggi().existsMessaggioInProcessamento(id, "OUTBOX")){
								throw new Exception("Messaggio in processamento nella PdD Fruitore");
							}
							if(dbComponentErogatore.getVerificatoreMessaggi().existsMessaggioInProcessamento(id, "INBOX")){
								throw new Exception("Messaggio in processamento nella PdD Fruitore");
							}
						}
						
						if(param.isEliminaMessaggioFruitore()){
							dbComponentFruitore.getVerificatoreMessaggi().deleteMessage(id, "OUTBOX", Utilities.testSuiteProperties.isUseTransazioni());
						}
						if(param.isEliminaMessaggioErogatore()){
							dbComponentErogatore.getVerificatoreMessaggi().deleteMessage(id, "INBOX", Utilities.testSuiteProperties.isUseTransazioni());
						}
						
						repository.setIndex(0);
					}
				}
			} catch (AxisFault error) {
				if(param.isInvocazioneOK()){
					throw error;
				}else{
					Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
					Reporter.log("Controllo fault actor ["+param.getActorClientAtteso()+"]");
					if(param.getActorClientAtteso()!=null)
						Assert.assertTrue(param.getActorClientAtteso().equals(error.getFaultActor()));
					else
						Assert.assertTrue(error.getFaultActor()==null);
					Reporter.log("Controllo fault code ["+param.getFaultCodeAtteso()+"]");
					if(param.getFaultCodeAtteso()!=null)
						Assert.assertTrue(param.getFaultCodeAtteso().equals(error.getFaultCode().getLocalPart().trim()));
					else
						Assert.assertTrue(error.getFaultCode()==null);
					Reporter.log("Controllo fault string ["+param.getFaultString()+"] con ["+error.getFaultString()+"]");
					boolean faultStringOK = error.getFaultString().indexOf(param.getFaultString())>=0;
					if(!faultStringOK){
						if(param.getFaultString_alternativoConfigurazioneDB()!=null){
							Reporter.log("Controllo fault string alternativo ["+param.getFaultString_alternativoConfigurazioneDB()+"] con ["+error.getFaultString()+"]");
							faultStringOK = error.getFaultString().indexOf(param.getFaultString_alternativoConfigurazioneDB())>=0;
						}
					}
					Assert.assertTrue(faultStringOK);
					
					try{
						if(id!=null){
							if(param.isEliminaMessaggioFruitore()){
								dbComponentFruitore.getVerificatoreMessaggi().deleteMessage(id, "OUTBOX", Utilities.testSuiteProperties.isUseTransazioni());
							}
							if(param.isEliminaMessaggioErogatore()){
								dbComponentErogatore.getVerificatoreMessaggi().deleteMessage(id, "INBOX", Utilities.testSuiteProperties.isUseTransazioni());
							}
							repository.setIndex(0);
						}
					}catch(Exception e){}
				}
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}		
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				fin.close();
			}catch(Exception e){}
		}
	}
	
	public void controllaTracciamentoRichiesta(String id,DatabaseComponent data,CooperazioneBase cooperazione,
			String tipoServizio,String servizio,String azione,String collaborazione,String profiloCollaborazione,boolean existsListaEccezioni){
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, cooperazione.getMittente(), null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, cooperazione.getDestinatario(), null));
		Reporter.log("Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, profiloCollaborazione,toProfiloCollaborazioneSdk(profiloCollaborazione)));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, cooperazione.isConfermaRicezione(),cooperazione.getInoltro(),cooperazione.getInoltroSdk()));
		Reporter.log("Controllo che la busta abbia=("+existsListaEccezioni+") generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==existsListaEccezioni);
		Reporter.log("Controllo lista trasmissione con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
				cooperazione.getMittente(), null,  
				cooperazione.getDestinatario() , null));
		//if(checkServizioApplicativo){
		//	Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta()isArrivedCount(id));
		//	Assert.assertTrue(data.getVerificatoreTracciaRichiesta()isArrivedCount(id)==1);
		//}
	}
	public void controllaTracciamentoRisposta(String id,DatabaseComponent data,CooperazioneBase cooperazione,
			String tipoServizio,String servizio,String azione,String collaborazione,String profiloCollaborazione,
			String codiceEccezione){
		controllaTracciamentoRisposta(id, data, cooperazione, tipoServizio, servizio, azione, collaborazione, profiloCollaborazione, codiceEccezione, null);
	}
	public void controllaTracciamentoRisposta(String id,DatabaseComponent data,CooperazioneBase cooperazione,
			String tipoServizio,String servizio,String azione,String collaborazione,String profiloCollaborazione,
			String codiceEccezione,IDSoggetto [] mittenteRisposta){
		Reporter.log("Controllo ricevuta richiesta asincrona simmetrica con riferimento messaggio: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("Controllo valore Mittente Busta con riferimento messaggio: " +id);
		if(mittenteRisposta==null)
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, cooperazione.getDestinatario(), null));
		else{
			//System.out.println("************** Verifico **********");
			boolean test = false;
			for(int i=0; i<mittenteRisposta.length; i++){
				//IDSoggetto idSoggetto = new IDSoggetto(cooperazione.getDestinatario().getTipo(), mittenteRisposta[i], cooperazione.getDestinatario().getCodicePorta());
				//System.out.println("Verifico per mittente["+mittenteRisposta[i].toString()+"]");
				if(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, mittenteRisposta[i] ,null)){
					//System.out.println("OK");
					test = true;
					break;
				}
			}
			Assert.assertTrue(test);
		}
		Reporter.log("Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, cooperazione.getMittente(), null));
		Reporter.log("Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, profiloCollaborazione, toProfiloCollaborazioneSdk(profiloCollaborazione)));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, cooperazione.isConfermaRicezione(),cooperazione.getInoltro(), cooperazione.getInoltroSdk()));
		Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
		
		if(mittenteRisposta==null)
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione,
					cooperazione.getDestinatario(), null,
					cooperazione.getMittente(), null));
		else{
			boolean test = false;
			for(int i=0; i<mittenteRisposta.length; i++){
				//IDSoggetto idSoggetto = new IDSoggetto(cooperazione.getDestinatario().getTipo(), mittenteRisposta[i], cooperazione.getDestinatario().getCodicePorta());
				if(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione,
						mittenteRisposta[i], null,
						cooperazione.getMittente(), null)){
					test = true;
					break;
				}
			}
			Assert.assertTrue(test);
		}
		
		if(codiceEccezione==null){
			Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}
		else{
			
			Reporter.log("Controllo che la busta abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==true);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + codiceEccezione);
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, codiceEccezione);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num>0);
		}
	}
	
	
	
	
	
	
	
	
	
	

	/* ------------ Connettore Errato ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoConnettoreErrato = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_SOGGETTO_CONNETTORE_ERRATO,
				false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseConnettoreErrato = 
			new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoConnettoreErrato, 
					org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	
	Repository repositoryConnettoreErrato=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".CONNETTORE_ERRATO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void connettoreErrato() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		Date dataInizioTest = DateManager.getDate();
		
		// Test connettore errato per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP:"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY,
				null,null,param);
		
		// Test connettore errato per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO,
				null,null,param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP:"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(4);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP:"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
				null,null,param);
		
		// Test connettore errato per profilo oneway in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY_STATELESS,
				null,null,param);
	
		// Test connettore errato per profilo sincrono in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS,
				null,null,param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(8);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);
				
		// Test connettore errato per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(9);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(10);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);
				
		// Test connettore errato per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(11);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE));
		param.setFaultString("Porta di Dominio del soggetto SPCSoggettoConnettoreErrato non disponibile");
		invocaServizio(this.repositoryConnettoreErrato,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
	
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="ConnettoreErrato")
	public Object[][]testConnettoreErrato()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryConnettoreErrato.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false}//,
				//La busta non arriva alla PdD destinataria {DatabaseProperties.getDatabaseComponentErogatore(),ids,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".CONNETTORE_ERRATO"},dataProvider="ConnettoreErrato",dependsOnMethods="connettoreErrato")
	public void testConnettoreErrato(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Oneway, siccome il messaggio è in rollback, non viene tracciato.
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
				}
				
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==3){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==5){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==6){
					// Test Oneway modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==7){
					// Test Sincrono in modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErrato, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ SOAPFault PdD Destinazione ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoSOAPFaultPdDDestinazione = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_SOAP_FAULT_PDD_DEST,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseSOAPFaultPdDDestinazione = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoSOAPFaultPdDDestinazione, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	Repository repositorySOAPFaultPdDDestinazione=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".SOAP_FAULT_PDD_DEST"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void SOAPFaultPdDDestinazione() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		
		// Test connettore errato per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY,
				null,null,param);
		
		// Test connettore errato per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO,
				null,null,param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(4);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
				null,null,param);
		
		// Test connettore errato per profilo oneway in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY_STATELESS,
				null,null,param);
		
		// Test connettore errato per profilo sincrono in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO_STATELESS,
				null,null,param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(8);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);
				
		// Test connettore errato per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(9);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test connettore errato per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(10);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);
				
		// Test connettore errato per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(11);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultPdDDestinazione,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
	}
	@DataProvider (name="SOAPFaultPdDDestinazione")
	public Object[][]testSOAPFaultPdDDestinazione()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositorySOAPFaultPdDDestinazione.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false}//,
				//La busta non arriva alla PdD destinataria {DatabaseProperties.getDatabaseComponentErogatore(),ids,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".SOAP_FAULT_PDD_DEST"},dataProvider="SOAPFaultPdDDestinazione",dependsOnMethods="SOAPFaultPdDDestinazione")
	public void testSOAPFaultPdDDestinazione(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Oneway, siccome il messaggio è in rollback, non viene tracciato.
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
				}
				
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==3){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==5){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==6){
					// Test Oneway modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==7){
					// Test Sincrono in modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ ConnettoreErrato Servizio Applicativo ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoConnettoreErratoServizioApplicativo = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoConnettoreErratoServizioApplicativo, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	Repository repositoryConnettoreErratoServizioApplicativo=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".CONNETTORE_ERRATO_SA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void ConnettoreErratoServizioApplicativo() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		Date dataInizioTest = DateManager.getDate();
	
		// Test soap fault applicativo per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ONEWAY,
				null,null,param);
		
		// Test soap fault applicativo per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(4);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
				null,null,param);
			
		// Test soap fault applicativo per profilo oneway in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ONEWAY_STATELESS,
				null,null,param);

		// Test soap fault applicativo per profilo sincrono in modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Servizio Applicativo non disponibile");
		invocaServizio(this.repositoryConnettoreErratoServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
				
		try{
			Thread.sleep(3000);
		}catch(Exception e){}

				
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="ConnettoreErratoServizioApplicativo")
	public Object[][]testConnettoreErratoServizioApplicativo()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryConnettoreErratoServizioApplicativo.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,false,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".CONNETTORE_ERRATO_SA"},dataProvider="ConnettoreErratoServizioApplicativo",dependsOnMethods="ConnettoreErratoServizioApplicativo")
	public void testConnettoreErratoServizioApplicativo(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo,boolean isPA) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Oneway
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null);
				}
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==3){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==5){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==6){
					// Test Oneway in modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,null);
				}
				
				else if(i==7){
					// Test Sincrono in modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null);
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_CONNETTORE_ERRATO_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA==false)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ SOAPFault Servizio Applicativo ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoSOAPFaultServizioApplicativo = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseSOAPFaultServizioApplicativo = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.infoSOAPFaultServizioApplicativo, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	Repository repositorySOAPFaultServizioApplicativo=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".SOAP_FAULT_SA"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void SOAPFaultServizioApplicativo() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
	
		// Test soap fault applicativo per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ONEWAY,
				null,null,param);
		
		// Test soap fault applicativo per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
	
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
				null,null,param);
		
		// Test soap fault applicativo per profilo oneway stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ONEWAY_STATELESS,
				null,null,param);
		
		// Test soap fault applicativo per profilo sincrono stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO_STATELESS,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(null);
		param.setFaultCodeAtteso("Server.faultExample");
		param.setFaultString("Fault ritornato dalla servlet di esempio di OpenSPCoop");
		invocaServizio(this.repositorySOAPFaultServizioApplicativo,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
	}
	@DataProvider (name="SOAPFaultServizioApplicativo")
	public Object[][]testSOAPFaultServizioApplicativo()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositorySOAPFaultServizioApplicativo.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,false,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".SOAP_FAULT_SA"},dataProvider="SOAPFaultServizioApplicativo",dependsOnMethods="SOAPFaultServizioApplicativo")
	public void testSOAPFaultServizioApplicativo(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo,boolean isPA) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Oneway
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null);
				}
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==3){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				else if(i==5){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==6){
					// Test Oneway modalita stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				
				else if(i==7){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null);
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_SINCRONA_AZIONE_SOAP_FAULT_SA_STATELESS, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					if(isPA)
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					else
						data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ Errore Processamento ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoErroreProcessamento = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_ERRORE_PROCESSAMENTO,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseErroreProcessamento = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoErroreProcessamento, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	Repository repositoryErroreProcessamento=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".ERRORE_PROCESSAMENTO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void ErroreProcessamento() throws TestSuiteException, IOException, Exception{

		int indexMessage = 0;
		
		ParametriCooperazioneConErrori param = null;
	
		// Test errore processamento per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Eccezione (EccezioneProtocollo) GRAVE con codice [EGOV_IT_300] - ErroreProcessamentoMessaggioSPCoop, descrizione errore: Errore nel processamento del messaggio SPCoop"});
			// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
			//param.setMotivoErroreProcessamento_alternativoConfigurazioneDB(new String[]{"[spedizione n.1] Consegna [http] con errore: Eccezione GRAVE con codice [EGOV_IT_300] - Sbustamento_ErroreProcessamentoMessaggioSPCoop, descrizione errore: La porta applicativa richiesta dalla busta eGov non esiste"});
		}
		param.setIndexIdMessaggioDaEliminare(indexMessage);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ONEWAY,
				null,null,param);
		indexMessage++;
		
		// Test errore processamento per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_SINCRONO,
				null,null,param);
		indexMessage++;

		// Test errore processamento per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(indexMessage);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		indexMessage++;
		
		// Test errore processamento per profilo asincrono simmetrico modalita sincrona
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param = new ParametriCooperazioneConErrori();
			param.setInvocazioneOK(false);
			param.setEliminaMessaggioFruitore(false);
			param.setEliminaMessaggioErogatore(false);
			param.setModalitaAsincrona(false);
			param.setVerificaRollbackProcessamento(false);
			param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
			param.setFaultCodeAtteso("EGOV_IT_300");
			param.setFaultString("Errore nel processamento del messaggio SPCoop");
			// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
			//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
			invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
					"profiloAsincrono_richiestaSincrona","123456",param);
		}else{
			this.repositoryErroreProcessamento.add("skip");
		}
		indexMessage++;
			
		// Test errore processamento per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(indexMessage);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		indexMessage++;
	
		// Test errore processamento per profilo asincrono asimmetrico modalita sincrona
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param = new ParametriCooperazioneConErrori();
			param.setInvocazioneOK(false);
			param.setEliminaMessaggioFruitore(false);
			param.setEliminaMessaggioErogatore(false);
			param.setModalitaAsincrona(false);
			param.setVerificaRollbackProcessamento(false);
			param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
			param.setFaultCodeAtteso("EGOV_IT_300");
			param.setFaultString("Errore nel processamento del messaggio SPCoop");
			// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
			//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
			invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
					null,null,param,7000);
		}else{
			this.repositoryErroreProcessamento.add("skip");
		}
		indexMessage++;
		
		// Test errore processamento per profilo oneway modalita stateless
		param = new ParametriCooperazioneConErrori();
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param.setInvocazioneOK(false);
		}else{
			param.setInvocazioneOK(true);
		}
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(indexMessage);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ONEWAY_STATELESS,
				null,null,param);
		indexMessage++;
		
		// Test errore processamento per profilo sincrono modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(indexMessage);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_SINCRONO_STATELESS,
				null,null,param);
		indexMessage++;
		
		// Test errore processamento per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test errore processamento per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test errore processamento per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);
		
		// Test errore processamento per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_300");
		param.setFaultString("Errore nel processamento del messaggio SPCoop");
		// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
		//param.setFaultString_alternativoConfigurazioneDB("La porta applicativa richiesta dalla busta eGov non esiste");
		invocaServizio(this.repositoryErroreProcessamento,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
	}
	@DataProvider (name="ErroreProcessamento")
	public Object[][]testErroreProcessamento()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryErroreProcessamento.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,false,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".ERRORE_PROCESSAMENTO"},dataProvider="ErroreProcessamento",dependsOnMethods="ErroreProcessamento")
	public void testErroreProcessamento(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo,boolean isPA) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				IDSoggetto soggettoPdDDefault = new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(), Utilities.testSuiteProperties.getIdentitaDefault_nome(), Utilities.testSuiteProperties.getIdentitaDefault_dominio());
				
				if(i==0){
					// Test Oneway
					if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
						this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,null,
								new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
					}else{
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
					}
				}
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
					/*}else{
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
								CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
								Costanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					}*/
				}
				else if(i==3 && !"skip".equals(id)){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
					/*}else{
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
								CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
								Costanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					}*/
				}
				else if(i==5 && !"skip".equals(id)){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				
				else if(i==6){
					// Test Oneway
					if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
						this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,null,
								new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
					}else{
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
					}
				}
			
				else if(i==7){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null,
							new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ Errore Validazione ---------------- */
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoErroreValidazione = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseErroreValidazione = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoErroreValidazione, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	
	Repository repositoryErroreValidazione=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".ERRORE_VALIDAZIONE"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void ErroreValidazione() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
	
		// Test errore validazione per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ONEWAY,
				null,null,param);
		
		// Test errore validazione per profilo sincrono
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_SINCRONO,
				null,null,param);

		// Test errore validazione per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test errore validazione per profilo asincrono simmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test errore validazione per profilo asincrono asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setIndexIdMessaggioDaEliminare(4);
		param.setVerificaRollbackProcessamento(false);
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
	
		// Test errore validazione per profilo asincrono asimmetrico modalita sincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA,
				null,null,param);
		
		// Test errore validazione per profilo oneway modalita stateless
		param = new ParametriCooperazioneConErrori();
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param.setInvocazioneOK(false);
		}else{
			param.setInvocazioneOK(true);
		}
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ONEWAY_STATELESS,
				null,null,param);
		
		// Test errore validazione per profilo sincrono modalita stateless
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setVerificaRollbackNonEffettuato(true);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_SINCRONO_STATELESS,
				null,null,param);
		
		// Test errore validazione per profilo asincrono simmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				"profiloAsincrono_richiestaAsincrona","123456",param);

		// Test errore validazione per profilo asincrono simmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_STATELESS,
				"profiloAsincrono_richiestaSincrona","123456",param);
		
		// Test errore validazione per profilo asincrono asimmetrico modalita asincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_STATELESS,
				null,null,param);

		// Test errore validazione per profilo asincrono asimmetrico modalita sincrona (Stateless)
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(false);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(false);
		param.setVerificaRollbackProcessamento(false);
		param.setActorClientAtteso(CostantiPdD.OPENSPCOOP2);
		param.setFaultCodeAtteso("EGOV_IT_001");
		param.setFaultString("[EGOV_IT_113] ProfiloTrasmissione/inoltro");
		invocaServizio(this.repositoryErroreValidazione,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_STATELESS,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
	}
	@DataProvider (name="ErroreValidazione")
	public Object[][]testErroreValidazione()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryErroreValidazione.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,false,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".ERRORE_VALIDAZIONE"},dataProvider="ErroreValidazione",dependsOnMethods="ErroreValidazione")
	public void testErroreValidazione(DatabaseComponent data,String [] ids,boolean checkServizioApplicativo,boolean isPA) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Oneway
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,"EGOV_IT_113");
				}
				else if(i==1){
					// Test Sincrono
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,"EGOV_IT_113");
				}
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,"EGOV_IT_113");
				}
				else if(i==3){
					// Test Asincrono Simmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,"EGOV_IT_113");
				}
				else if(i==4){
					// Test Asincrono Asimmetrico modalita asincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,"EGOV_IT_113");
				}
				else if(i==5){
					// Test Asincrono Asimmetrico modalita sincrona
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,"EGOV_IT_113");
				}
				
				else if(i==6){
					// Test Oneway stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,"EGOV_IT_113");
				}
				
				else if(i==7){
					// Test Sincrono stateless
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,"EGOV_IT_113");
				}
				
				else if(i==8){
					// Test Asincrono Simmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,"EGOV_IT_113");
				}
				
				else if(i==9){
					// Test Asincrono Simmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,"EGOV_IT_113");
				}
				
				else if(i==10){
					// Test Asincrono Asimmetrico modalita asincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,"EGOV_IT_113");
				}
				
				else if(i==11){
					// Test Asincrono Asimmetrico modalita sincrona (Stateless)
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,"EGOV_IT_113");
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ Test rispedizioni per profilo OneWay (Controllo Timer) ---------------- */
	
	Repository repositoryRispedizioniOneWay=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ONEWAY"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void rispedizioniOneWay() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		Date dataInizioTest = DateManager.getDate();
		
		// ONEWAY
		
		// Test connettore errato PdD destinazione per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY,
				null,null,param);
		
		// Test connettore errato Servizio Applicativo per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(1);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ONEWAY,
				null,null,param);
		
		// Test soap fault PdD Destinazione per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY,
				null,null,param);
		
		// Test soap faul servizio applicativo per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(3);
		param.setVerificaRollbackNonEffettuato(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ONEWAY,
				null,null,param);
		
		// Test Busta Errore Processamento SPCoop per profilo oneway
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(4);
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Eccezione (EccezioneProtocollo) GRAVE con codice [EGOV_IT_300] - ErroreProcessamentoMessaggioSPCoop, descrizione errore: Errore nel processamento del messaggio SPCoop"});
			// Visto che ora il motivo generico e' abilitato, dovrebbe sempre bastare il motivo sopra.
			//param.setMotivoErroreProcessamento_alternativoConfigurazioneDB(new String[]{"[spedizione n.1] Consegna [http] con errore: Eccezione GRAVE con codice [EGOV_IT_300] - Sbustamento_ErroreProcessamentoMessaggioSPCoop, descrizione errore: La porta applicativa richiesta dalla busta eGov non esiste"});
			param.setVerificaRollbackProcessamento(true);
		}
		param.setModalitaAsincrona(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_PROCESSAMENTO_ONEWAY,
				null,null,param);
		
		// Test Busta Errore Validazione SPCoop per profilo oneway non funzioni!
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(false);
		param.setIndexIdMessaggioDaEliminare(5);
		param.setVerificaRollbackNonEffettuato(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_ERRORE_SPCOOP_VALIDAZIONE_ONEWAY,
				null,null,param);
		
		
		// Test connettore errato PdD per oneway con riscontri
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(6);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ONEWAY_RISCONTRI,
				null,null,param);
		
		
		// Test soap fault PdD destinazione per oneway con riscontri
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(7);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniOneWay,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ONEWAY_RISCONTRI,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);

	}
	@DataProvider (name="rispedizioniOneWay")
	public Object[][]testRispedizioniOneWay()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryRispedizioniOneWay.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ONEWAY"},dataProvider="rispedizioniOneWay",dependsOnMethods="rispedizioniOneWay")
	public void testRispedizioniOneWay(DatabaseComponent data,String [] ids,boolean pddDestinazione) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				IDSoggetto soggettoPdDDefault = new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(), Utilities.testSuiteProperties.getIdentitaDefault_nome(), Utilities.testSuiteProperties.getIdentitaDefault_dominio());
								
				if(i==0){
					// Test Oneway, connettore errato PdD Destinazione
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==1){
					// Test Oneway con connettore errato per servizio applicativo
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==2){
					// Test Oneway, con soap fault della PdD Destinazione
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==3){
					// Test Oneway, con soap fault per Servizio Applicativo
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
				
				else if(i==4){
					// Test Oneway, con busta SPCoop errore Processamento
					if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
						this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,null,
								new IDSoggetto[] {soggettoPdDDefault,this.collaborazioneSPCoopBaseErroreProcessamento.getDestinatario()}); // getDestinatario serve per configurazione su database
					}else{
						this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreProcessamento, 
								CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
								CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
								null, null,
								SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
					}
				}
				
				else if(i==5){
					// Test Oneway, con busta SPCoop errore Validazione
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,true);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseErroreValidazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							null, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,"EGOV_IT_113");
				}
				
				else if(i==6){
					// Test Oneway con riscontri, connettore errato PdD Destinazione
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==7){
					// Test Oneway con riscontri, con soap fault della PdD Destinazione
					CooperazioneBaseInformazioni infoTEST7 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
							CostantiTestSuite.SPCOOP_SOGGETTO_SOAP_FAULT_PDD_DEST,
							true,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
					CooperazioneBase collaborazioneSPCoopBaseTEST7 = 
						new CooperazioneBase(false,SOAPVersion.SOAP11,  infoTEST7, 
								org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
								DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
					this.controllaTracciamentoRichiesta(id,data,collaborazioneSPCoopBaseTEST7, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,false);
				}
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* ------------ Test rispedizioni per profilo Asincrono Simmetrico (Controllo Timer) ---------------- */
	
	Repository repositoryRispedizioniAsincronoSimmetrico=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ASINCRONO_SIMMETRICO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void rispedizioniAsincronoSimmetrico() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		Date dataInizioTest = DateManager.getDate();

		// AsincronoSimmetrico
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoSimmetrico,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test connettore errato per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(1);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoSimmetrico,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);

		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoSimmetrico,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		// Test soap fault applicativo per profilo asincrono simmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setIndexIdMessaggioDaEliminare(3);
		param.setVerificaRollbackProcessamento(false);
		param.setVerificaRollbackNonEffettuato(true);
		invocaServizio(this.repositoryRispedizioniAsincronoSimmetrico,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA,
				"profiloAsincrono_richiestaAsincrona","123456",param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="rispedizioniAsincronoSimmetrico")
	public Object[][]testRispedizioniAsincronoSimmetrico()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryRispedizioniAsincronoSimmetrico.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ASINCRONO_SIMMETRICO"},dataProvider="rispedizioniAsincronoSimmetrico",dependsOnMethods="rispedizioniAsincronoSimmetrico")
	public void testRispedizioniAsincronoSimmetrico(DatabaseComponent data,String [] ids,boolean pddDestinazione) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Asincrono Simmetrico modalita Asincrona connettore errato PdD
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==1){
					// Test Asincrono Simmetrico modalita asincrona, soap fault pdd dest
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
				}
				
				else if(i==2){
					// Test Asincrono Simmetrico modalita asincrona connettore errato sa
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==3){
					// Test Asincrono Simmetrico modalita asincrona soap fault sa
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,null);
					
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------ Test rispedizioni per profilo Asincrono Asimmetrico (Controllo Timer) ---------------- */
	
	Repository repositoryRispedizioniAsincronoAsimmetrico=new Repository();
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ASINCRONO_ASIMMETRICO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void rispedizioniAsincronoAsimmetrico() throws TestSuiteException, IOException, Exception{

		ParametriCooperazioneConErrori param = null;
		Date dataInizioTest = DateManager.getDate();

		// AsincronoAsimmetrico
		
		// Test connettore errato per profilo asincrono Asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(0);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoAsimmetrico,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		// Test connettore errato per profilo asincrono Asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(true);
		param.setEliminaMessaggioErogatore(false);
		param.setIndexIdMessaggioDaEliminare(1);
		param.setMotivoErroreProcessamento(
				new String[]{"[spedizione n.1] Consegna [http] con errore: errore di trasporto, codice 500",
						"Server.faultExample",
						"Fault ritornato dalla servlet di esempio di OpenSPCoop"});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoAsimmetrico,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);

		// Test soap fault applicativo per profilo asincrono Asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(true);
		param.setIndexIdMessaggioDaEliminare(2);
		param.setMotivoErroreProcessamento(new String[]{"[spedizione n.1] Consegna [http] con errore: Errore avvenuto durante la consegna HTTP: "});
		param.setModalitaAsincrona(true);
		param.setVerificaRollbackProcessamento(true);
		invocaServizio(this.repositoryRispedizioniAsincronoAsimmetrico,CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		// Test soap fault applicativo per profilo asincrono Asimmetrico modalita asincrona
		param = new ParametriCooperazioneConErrori();
		param.setInvocazioneOK(true);
		param.setEliminaMessaggioFruitore(false);
		param.setEliminaMessaggioErogatore(false);
		param.setModalitaAsincrona(true);
		param.setIndexIdMessaggioDaEliminare(3);
		param.setVerificaRollbackProcessamento(false);
		param.setVerificaRollbackNonEffettuato(true);
		invocaServizio(this.repositoryRispedizioniAsincronoAsimmetrico,CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA,
				null,null,param);
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="rispedizioniAsincronoAsimmetrico")
	public Object[][]testRispedizioniAsincronoAsimmetrico()throws Exception{
		String [] ids = new String[1];
		Vector<String> idsV = new Vector<String>();
		String id = null;
		while( (id=this.repositoryRispedizioniAsincronoAsimmetrico.getNext()) != null){
			idsV.add(id);
		}
		ids = idsV.toArray(ids);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),ids,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),ids,true}
		};
	}
	@Test(groups={CooperazioneConErrori.ID_GRUPPO,
			CooperazioneConErrori.ID_GRUPPO+".RISPEDIZIONI_ASINCRONO_ASIMMETRICO"},dataProvider="rispedizioniAsincronoAsimmetrico",dependsOnMethods="rispedizioniAsincronoAsimmetrico")
	public void testRispedizioniAsincronoAsimmetrico(DatabaseComponent data,String [] ids,boolean pddDestinazione) throws Exception{
		try{
			for(int i=0; i<ids.length; i++){
				String id = ids[i];
			
				if(i==0){
					// Test Asincrono Asimmetrico modalita Asincrona connettore errato PdD
					Reporter.log("Controllo tracciamento richiesta non sia effettuata con id: " +id);
					Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				}
				
				else if(i==1){
					// Test Asincrono Asimmetrico modalita asincrona, soap fault pdd dest
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultPdDDestinazione, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
				}
				
				else if(i==2){
					// Test Asincrono Asimmetrico modalita asincrona connettore errato sa
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseConnettoreErratoServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_CONNETTORE_ERRATO_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
				else if(i==3){
					// Test Asincrono Asimmetrico modalita asincrona soap fault sa
					this.controllaTracciamentoRichiesta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,false);
					this.controllaTracciamentoRisposta(id,data,this.collaborazioneSPCoopBaseSOAPFaultServizioApplicativo, 
							CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, 
							CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_MOD_ASINCRONA_AZIONE_SOAP_FAULT_SA, null,
							SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,null);
					
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.OUTBOX, Utilities.testSuiteProperties.isUseTransazioni());
					data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
				}
				
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
}


class ParametriCooperazioneConErrori {

	private boolean invocazioneOK;
	private boolean modalitaAsincrona;
	private boolean eliminaMessaggioFruitore;
	private boolean eliminaMessaggioErogatore;
	private int indexIdMessaggioDaEliminare;
    private boolean verificaRollbackProcessamento;
    private boolean verificaRollbackNonEffettuato = false;
    private String actorClientAtteso;
    private String faultCodeAtteso;
    private String faultString;
    private String faultString_alternativoConfigurazioneDB;
	private String [] motivoErroreProcessamento;
    private String [] motivoErroreProcessamento_alternativoConfigurazioneDB;
    
	public boolean isEliminaMessaggioErogatore() {
		return this.eliminaMessaggioErogatore;
	}
	public void setEliminaMessaggioErogatore(boolean eliminaMessaggioErogatore) {
		this.eliminaMessaggioErogatore = eliminaMessaggioErogatore;
	}
	public boolean isEliminaMessaggioFruitore() {
		return this.eliminaMessaggioFruitore;
	}
	public void setEliminaMessaggioFruitore(boolean eliminaMessaggioFruitore) {
		this.eliminaMessaggioFruitore = eliminaMessaggioFruitore;
	}
	public boolean isInvocazioneOK() {
		return this.invocazioneOK;
	}
	public void setInvocazioneOK(boolean invocazioneOK) {
		this.invocazioneOK = invocazioneOK;
	}
	public boolean isModalitaAsincrona() {
		return this.modalitaAsincrona;
	}
	public void setModalitaAsincrona(boolean modalitaAsincrona) {
		this.modalitaAsincrona = modalitaAsincrona;
	}
	public boolean isVerificaRollbackProcessamento() {
		return this.verificaRollbackProcessamento;
	}
	public void setVerificaRollbackProcessamento(
			boolean verificaRollbackProcessamento) {
		this.verificaRollbackProcessamento = verificaRollbackProcessamento;
	}
	public String getActorClientAtteso() {
		return this.actorClientAtteso;
	}
	public void setActorClientAtteso(String actorClientAtteso) {
		this.actorClientAtteso = actorClientAtteso;
	}
	public String getFaultCodeAtteso() {
		return this.faultCodeAtteso;
	}
	public void setFaultCodeAtteso(String faultCodeAtteso) {
		this.faultCodeAtteso = faultCodeAtteso;
	}
	public String getFaultString() {
		return this.faultString;
	}
	public void setFaultString(String faultString) {
		this.faultString = faultString;
	}
	public String getFaultString_alternativoConfigurazioneDB() {
		return this.faultString_alternativoConfigurazioneDB;
	}
	public void setFaultString_alternativoConfigurazioneDB(
			String faultStringAlternativoConfigurazioneDB) {
		this.faultString_alternativoConfigurazioneDB = faultStringAlternativoConfigurazioneDB;
	}
	public int getIndexIdMessaggioDaEliminare() {
		return this.indexIdMessaggioDaEliminare;
	}
	public void setIndexIdMessaggioDaEliminare(int indexIdMessaggioDaEliminare) {
		this.indexIdMessaggioDaEliminare = indexIdMessaggioDaEliminare;
	}
	public String[] getMotivoErroreProcessamento() {
		return this.motivoErroreProcessamento;
	}
	public String getMotivoErroreProcessamento_toString(){
		if(this.motivoErroreProcessamento!=null && this.motivoErroreProcessamento.length>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i< this.motivoErroreProcessamento.length;i++){
				bf.append((i+1)+". "+this.motivoErroreProcessamento[i]+"\n");
			}
			return bf.toString();
		}else{
			return null;
		}
	}
	public boolean matchMotivoErroreProcessamento(String match){
		if(this.motivoErroreProcessamento==null || this.motivoErroreProcessamento.length==0){
			return (match==null);
		}
		if(match==null){
			return false;
		}
		for(int i=0; i< this.motivoErroreProcessamento.length;i++){
			String tmp = this.motivoErroreProcessamento[i];
			//System.out.println("Verifico presenza ["+tmp+"] in ["+match+"]... ");
			//System.out.println("RESULT ["+(match.indexOf(tmp))+"]");
			if(match.indexOf(tmp)<0){
				return false;
			}
		}
		return true;
	}
	public void setMotivoErroreProcessamento(String[] motivoErroreProcessamento) {
		this.motivoErroreProcessamento = motivoErroreProcessamento;
	}
	
	
	public void setMotivoErroreProcessamento_alternativoConfigurazioneDB(String[] motivoErroreProcessamento) {
		this.motivoErroreProcessamento_alternativoConfigurazioneDB = motivoErroreProcessamento;
	}
	public String[] getMotivoErroreProcessamento_alternativoConfigurazioneDB() {
		return this.motivoErroreProcessamento_alternativoConfigurazioneDB;
	}
	public String getMotivoErroreProcessamento_alternativoConfigurazioneDB_toString(){
		if(this.motivoErroreProcessamento_alternativoConfigurazioneDB!=null && this.motivoErroreProcessamento_alternativoConfigurazioneDB.length>0){
			StringBuffer bf = new StringBuffer();
			for(int i=0; i< this.motivoErroreProcessamento_alternativoConfigurazioneDB.length;i++){
				bf.append((i+1)+". "+this.motivoErroreProcessamento_alternativoConfigurazioneDB[i]+"\n");
			}
			return bf.toString();
		}else{
			return null;
		}
	}
	public boolean matchMotivoErroreProcessamento_alternativoConfigurazioneDB(String match){
		if(this.motivoErroreProcessamento_alternativoConfigurazioneDB==null || this.motivoErroreProcessamento_alternativoConfigurazioneDB.length==0){
			return (match==null);
		}
		if(match==null){
			return false;
		}
		for(int i=0; i< this.motivoErroreProcessamento_alternativoConfigurazioneDB.length;i++){
			String tmp = this.motivoErroreProcessamento_alternativoConfigurazioneDB[i];
			//System.out.println("Verifico presenza ["+tmp+"] in ["+match+"]... ");
			//System.out.println("RESULT ["+(match.indexOf(tmp))+"]");
			if(match.indexOf(tmp)<0){
				return false;
			}
		}
		return true;
	}

	
	public boolean isVerificaRollbackNonEffettuato() {
		return this.verificaRollbackNonEffettuato;
	}
	public void setVerificaRollbackNonEffettuato(
			boolean verificaRollbackNonEffettuato) {
		this.verificaRollbackNonEffettuato = verificaRollbackNonEffettuato;
	}
}
