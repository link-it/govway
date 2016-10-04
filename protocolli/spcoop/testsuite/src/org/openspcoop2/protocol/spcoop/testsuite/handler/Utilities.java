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


package org.openspcoop2.protocol.spcoop.testsuite.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.pdd.core.handlers.HandlerException;

/**
 * Libreria per handler testsuite
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	public static void verificaMessaggioRichiesta(OpenSPCoop2Message msg) throws HandlerException{
		if(msg==null){
			throw new HandlerException("Messaggio non fornito nel contesto");
		}
	}
	public static void verificaMessaggioRisposta(OpenSPCoop2Message msg,boolean rispostaVuota) throws HandlerException{
		if(rispostaVuota){
			if(msg!=null){
				try{
					File tmp = File.createTempFile("TEST-HANDLER", "tmp");
					FileOutputStream fout = null;
					try{
						fout = new FileOutputStream(tmp);
						msg.writeTo(fout,false);
						fout.flush();
						fout.close();
					}finally{
						try{
							fout.close();
						}catch(Exception eClose){}
					}
					throw new HandlerException("Messaggio fornito nel contesto non atteso nella testsuite (messaggio salvato in ["+tmp.getAbsolutePath()+"])");
				}catch(HandlerException e){
					throw e;
				}catch(Exception e){
					throw new HandlerException("Messaggio fornito nel contesto non atteso nella testsuite e salvataggio messaggio non riuscito: "+e.getMessage(),e);	
				}
			}
		}
		else{
			if(msg==null){
				throw new HandlerException("Messaggio non fornito nel contesto");
			}
		}
	}
	
	public static void verificaLunghezza(Long length,String tipoMessaggio) throws HandlerException{
		if(length==null){
			throw new HandlerException("Lunghezza ["+tipoMessaggio+"] non fornita nel contesto");
		}
		if(length.longValue()<=10){
			throw new HandlerException("Lunghezza ["+tipoMessaggio+"] fornita nel contesto minore di 10 bytes ?? (lunghezza:"+length.longValue()+")");
		}
	}
	public static void verificaLunghezza(Long length,String tipoMessaggio,boolean lunghezzaRichiesta) throws HandlerException{
		if(lunghezzaRichiesta){
			if(length==null){
				throw new HandlerException("Lunghezza ["+tipoMessaggio+"] non fornita nel contesto");
			}
			if(length.longValue()<=10){
				throw new HandlerException("Lunghezza ["+tipoMessaggio+"] fornita nel contesto minore di 10 bytes ?? (lunghezza:"+length.longValue()+")");
			}
		}else{
			if(length!=null){
				throw new HandlerException("Lunghezza ["+tipoMessaggio+"] fornita nel contesto ("+length.longValue()+") ma non attesa nel test");
			}
		}
	}
	
	public static void verificaLocationTipoConnettore(TipoPdD tipoPdD,String locationPD,String locationPA,
			String tipoConnettorePD,String tipoConnettorePA,
			InfoConnettoreUscita infoConnettoreUscita)throws HandlerException{
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			if(locationPD!=null){
				if(infoConnettoreUscita==null){
					throw new HandlerException("InfoConnettoreUscita non fornito dalla PdD e richiesto dal test (verifica location)");
				}
				if(!locationPD.equals(infoConnettoreUscita.getLocation())){
					throw new HandlerException("Location fornita dalla Porta di Dominio ["+infoConnettoreUscita.getLocation()
							+"] diversa da quella attesa per il test ["+locationPD+"]");
				}
			}
			if(tipoConnettorePD!=null){
				if(infoConnettoreUscita==null){
					throw new HandlerException("InfoConnettoreUscita non fornito dalla PdD e richiesto dal test (verifica tipo connettore)");
				}
				if(!tipoConnettorePD.equals(infoConnettoreUscita.getTipoConnettore())){
					throw new HandlerException("TipoConnettore fornito dalla Porta di Dominio ["+infoConnettoreUscita.getTipoConnettore()
							+"] diverso da quello atteso per il test ["+tipoConnettorePD+"]");
				}
			}
		}else{
			if(locationPA!=null){
				if(infoConnettoreUscita==null){
					throw new HandlerException("InfoConnettoreUscita non fornito dalla PdD e richiesto dal test (verifica location)");
				}
				if(!locationPA.equals(infoConnettoreUscita.getLocation())){
					throw new HandlerException("Location fornita dalla Porta di Dominio ["+infoConnettoreUscita.getLocation()
							+"] diversa da quella attesa per il test ["+locationPA+"]");
				}
			}
			if(tipoConnettorePA!=null){
				if(infoConnettoreUscita==null){
					throw new HandlerException("InfoConnettoreUscita non fornito dalla PdD e richiesto dal test (verifica tipo connettore)");
				}
				if(!tipoConnettorePA.equals(infoConnettoreUscita.getTipoConnettore())){
					throw new HandlerException("TipoConnettore fornito dalla Porta di Dominio ["+infoConnettoreUscita.getTipoConnettore()
							+"] diverso da quello atteso per il test ["+tipoConnettorePA+"]");
				}
			}
		}
	}
	
	public static void verificaData(Date dataTest,Date dataElaborazioneMsg)throws HandlerException{
		// Data Elaborazione
		if(dataElaborazioneMsg==null){
			throw new HandlerException("Data elaborazione messaggio non presente nel contesti");
		}
		if(dataTest!=null){
			
			SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
			String dataElaborata = dateformat.format(dataElaborazioneMsg);
			Date d = null;
			try{
				d = dateformat.parse(dataElaborata);
			}catch(Exception e){}
			
			if(d.before(dataTest)){
				
				throw new HandlerException("Data elaborazione messaggio presente nel contesto ["+dataElaborata
						+"] precedente alla data di esecuzione del test ["+dataTest+"]");
			}
		}
	}
	
	public static void verificaIntegrationContext(TestContext test, IntegrationContext integrationContext, TipoPdD tipoPdD) throws HandlerException{
		
		// integrationContext
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			if(test.getCorrelazioneApplicativaPDReq()!=null){
				if(integrationContext==null){
					throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica correlazione applicativa)");
				}
				if(!test.getCorrelazioneApplicativaPDReq().equals(integrationContext.getIdCorrelazioneApplicativa())){
					throw new HandlerException("CorrelazioneApplicativa ritornata dalla Porta di Dominio ["+integrationContext.getIdCorrelazioneApplicativa()
							+"] diversa da quella attesa per il test ["+test.getCorrelazioneApplicativaPDReq()+"]");
				}
			}
		}else{
			if(test.getCorrelazioneApplicativaPAReq()!=null){
				if(integrationContext==null){
					throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica correlazione applicativa)");
				}
				if(!test.getCorrelazioneApplicativaPAReq().equals(integrationContext.getIdCorrelazioneApplicativa())){
					throw new HandlerException("CorrelazioneApplicativa ritornata dalla Porta di Dominio ["+integrationContext.getIdCorrelazioneApplicativa()
							+"] diversa da quella attesa per il test ["+test.getCorrelazioneApplicativaPAReq()+"]");
				}
			}
		}
		if(test.getServizioApplicativoFruitore()!=null && TipoPdD.DELEGATA.equals(tipoPdD)){
			if(integrationContext==null){
				throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica servizio applicativo fruitore)");
			}
			if(Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO.equals(test.getServizioApplicativoFruitore())){
				if(integrationContext.getServizioApplicativoFruitore()!=null){
					throw new HandlerException("Servizio applicativo fruitore ritornato dalla Porta di Dominio ["+integrationContext.getServizioApplicativoFruitore()
						+"] diverso da quello atteso per il test [S.A. Anonimo]");
				}
			}else{
				if(!test.getServizioApplicativoFruitore().equals(integrationContext.getServizioApplicativoFruitore())){
					throw new HandlerException("Servizio applicativo fruitore ritornato dalla Porta di Dominio ["+integrationContext.getServizioApplicativoFruitore()
							+"] diverso da quello atteso per il test ["+test.getServizioApplicativoFruitore()+"]");
				}
			}
		}
		if(test.getServizioApplicativoErogatore()!=null && TipoPdD.APPLICATIVA.equals(tipoPdD)){
			if(integrationContext==null){
				throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica servizio applicativo erogatore)");
			}
			if(integrationContext.sizeServiziApplicativiErogatori()<=0){
				throw new HandlerException("Servizi applicativi erogatori non presenti nel contesto ritornato dalla PdD");
			}
			if(integrationContext.sizeServiziApplicativiErogatori()>1){
				throw new HandlerException("Servizi applicativi erogatori presenti in un numero maggiori di 1 nel contesto ritornato dalla PdD");
			}
			if(!test.getServizioApplicativoErogatore().equals(integrationContext.getServizioApplicativoErogatore(0))){
				throw new HandlerException("Servizio applicativo erogatore ritornato dalla Porta di Dominio ["+integrationContext.getServizioApplicativoErogatore(0)
						+"] diverso da quello atteso per il test ["+test.getServizioApplicativoErogatore()+"]");
			}
		}
		if(test.getStatelessPD()!=null){
			if(integrationContext==null){
				throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica stateless)");
			}
			if(integrationContext.isGestioneStateless()==null){
				throw new HandlerException("Informazione sulla gestione stateless all'interno dell'IntegrationContext non fornito dalla PdD e richiesto dal test (verifica stateless)");
			}
			if(test.getStatelessPD().booleanValue() != integrationContext.isGestioneStateless().booleanValue() ){
				throw new HandlerException("Gestione stateless ritornata dalla Porta di Dominio ["+integrationContext.isGestioneStateless().booleanValue()
						+"] diversa da quella attesa per il test ["+test.getStatelessPD().booleanValue()+"]");
			}
		}
		if(test.getStatelessPA()!=null){
			if(integrationContext==null){
				throw new HandlerException("IntegrationContext non fornito dalla PdD e richiesto dal test (verifica stateless)");
			}
			if(integrationContext.isGestioneStateless()==null){
				throw new HandlerException("Informazione sulla gestione stateless all'interno dell'IntegrationContext non fornito dalla PdD e richiesto dal test (verifica stateless)");
			}
			if(test.getStatelessPA().booleanValue() != integrationContext.isGestioneStateless().booleanValue() ){
				throw new HandlerException("Gestione stateless ritornata dalla Porta di Dominio ["+integrationContext.isGestioneStateless().booleanValue()
						+"] diversa da quella attesa per il test ["+test.getStatelessPA().booleanValue()+"]");
			}
		}
	}
	
	public static void verificaEGovContext(ProtocolContext contextPdD, ProtocolContext contextTest,boolean checkIDEgovRisposta,TipoPdD tipoPdD) throws HandlerException{
		
		if(contextPdD==null){
			throw new HandlerException("Contesto non ritornato dalla porta di dominio, ma atteso dalla testsuite");
		}
		
		if(TipoPdD.DELEGATA.equals(tipoPdD)){
			if(contextPdD.getFruitore()!=null){
				if(contextPdD.getDominio()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta");
				}
				
				if(contextPdD.getDominio().getTipo()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (tipo)");
				}
				if(!contextPdD.getDominio().getTipo().equals(contextPdD.getFruitore().getTipo())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (tipo) ["+contextPdD.getDominio().getTipo()
							+"] diverso da quello atteso ["+contextPdD.getFruitore().getTipo()+"]");
				}
				
				if(contextPdD.getDominio().getNome()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (nome)");
				}
				if(!contextPdD.getDominio().getNome().equals(contextPdD.getFruitore().getNome())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (nome) ["+contextPdD.getDominio().getNome()
							+"] diverso da quello atteso ["+contextPdD.getFruitore().getNome()+"]");
				}
				
				if(contextPdD.getDominio().getCodicePorta()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (codice-porta)");
				}
				if(!contextPdD.getDominio().getCodicePorta().equals(contextPdD.getFruitore().getCodicePorta())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (codice-porta) ["+contextPdD.getDominio().getCodicePorta()
							+"] diverso da quello atteso ["+contextPdD.getFruitore().getCodicePorta()+"]");
				}
			}
		}else{
			if(contextPdD.getErogatore()!=null && contextPdD.getErogatore().getNome()!=null){
				if(contextPdD.getDominio()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta");
				}
				
				if(contextPdD.getDominio().getTipo()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (tipo)");
				}
				if(!contextPdD.getDominio().getTipo().equals(contextPdD.getErogatore().getTipo())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (tipo) ["+contextPdD.getDominio().getTipo()
							+"] diverso da quello atteso ["+contextPdD.getErogatore().getTipo()+"]");
				}
				
				if(contextPdD.getDominio().getNome()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (nome)");
				}
				if(!contextPdD.getDominio().getNome().equals(contextPdD.getErogatore().getNome())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (nome) ["+contextPdD.getDominio().getNome()
							+"] diverso da quello atteso ["+contextPdD.getErogatore().getNome()+"]");
				}
				
				if(contextPdD.getDominio().getCodicePorta()==null){
					throw new HandlerException("Contesto ritornato dalla porta di dominio non contiene l'identificativo porta (codice-porta)");
				}
				if(!contextPdD.getDominio().getCodicePorta().equals(contextPdD.getErogatore().getCodicePorta())){
					throw new HandlerException("Contesto ritornato dalla porta di dominio contiene un identificativo porta (codice-porta) ["+contextPdD.getDominio().getCodicePorta()
							+"] diverso da quello atteso ["+contextPdD.getErogatore().getCodicePorta()+"]");
				}
				
			}
		}
		
		// Fruitore
		if(contextTest.getFruitore()!=null){
			
			if(contextTest.getFruitore().getTipo()!=null){
				
				if(contextPdD.getFruitore()==null){
					throw new HandlerException("Informazioni sul fruitore non presenti nel Contesto ritornato dalla porta di dominio, ma attesi dalla testsuite");
				}
				if(!contextTest.getFruitore().getTipo().equals(contextPdD.getFruitore().getTipo())){
					throw new HandlerException("Tipo del fruitore presente nel Contesto della Porta di Dominio ["+contextPdD.getFruitore().getTipo()
							+"] differente da quello atteso per il test ["+contextTest.getFruitore().getTipo()+"]");
				}
				
			}
			if(contextTest.getFruitore().getNome()!=null){
				
				if(contextPdD.getFruitore()==null){
					throw new HandlerException("Informazioni sul fruitore non presenti nel Contesto ritornato dalla porta di dominio, ma attesi dalla testsuite");
				}
				if(!contextTest.getFruitore().getNome().equals(contextPdD.getFruitore().getNome())){
					throw new HandlerException("Nome del fruitore presente nel Contesto della Porta di Dominio ["+contextPdD.getFruitore().getNome()
							+"] differente da quello atteso per il test ["+contextTest.getFruitore().getNome()+"]");
				}
				
			}
			
		}
		
		// Erogatore
		if(contextTest.getErogatore()!=null){
			
			if(contextTest.getErogatore().getTipo()!=null){
				
				if(contextPdD.getErogatore()==null){
					throw new HandlerException("Informazioni sull'erogatore non presenti nel Contesto ritornato dalla porta di dominio, ma attesi dalla testsuite");
				}
				if(!contextTest.getErogatore().getTipo().equals(contextPdD.getErogatore().getTipo())){
					throw new HandlerException("Tipo dell'erogatore presente nel Contesto della Porta di Dominio ["+contextPdD.getErogatore().getTipo()
							+"] differente da quello atteso per il test ["+contextTest.getErogatore().getTipo()+"]");
				}
				
			}
			if(contextTest.getErogatore().getNome()!=null){
				
				if(contextPdD.getErogatore()==null){
					throw new HandlerException("Informazioni sull'erogatore non presenti nel Contesto ritornato dalla porta di dominio, ma attesi dalla testsuite");
				}
				if(!contextTest.getErogatore().getNome().equals(contextPdD.getErogatore().getNome())){
					throw new HandlerException("Nome dell'erogatore presente nel Contesto della Porta di Dominio ["+contextPdD.getErogatore().getNome()
							+"] differente da quello atteso per il test ["+contextTest.getErogatore().getNome()+"]");
				}
				
			}
			
		}
		
		// ID EGov Richiesta
		if(contextTest.getIdRichiesta()!=null){
			if(Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_NULL.equals(contextTest.getIdRichiesta())){
				if(contextPdD.getIdRichiesta()!=null){
					throw new HandlerException("Informazione sull'id egov della richiesta presente nel Contesto ritornato dalla porta di dominio ("+contextPdD.getIdRichiesta()+"), ma non atteso dalla testsuite");
				}
			}else if(Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO.equals(contextTest.getIdRichiesta())){
				if(contextPdD.getIdRichiesta()==null){
					throw new HandlerException("Informazione sull'id egov della richiesta non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
				}
				if(contextTest.getFruitore()==null){
					throw new HandlerException("Verifica dell'id egov della richiesta non effettuabile se non si definisce il fruitore nel contesto del test");
				}
				if(contextTest.getFruitore().getNome()==null){
					throw new HandlerException("Verifica dell'id egov della richiesta non effettuabile se non si definisce il nome del fruitore nel contesto del test");
				}
				if(!contextPdD.getIdRichiesta().startsWith(contextTest.getFruitore().getNome()+"_"+contextTest.getFruitore().getNome()+"SPCoopIT_")){
					throw new HandlerException("Informazione sull'id egov della richiesta presente nel Contesto ritornato dalla porta di dominio differente da quello atteso dalla testsuite: prefisso non contiene il nome del fruitore");
				}
			}else{
				if(contextPdD.getIdRichiesta()==null){
					throw new HandlerException("Informazione sull'id egov della richiesta non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
				}
				if(!contextTest.getIdRichiesta().equals(contextPdD.getIdRichiesta())){
					throw new HandlerException("ID egov della richiesta presente nel Contesto della Porta di Dominio ["+contextPdD.getIdRichiesta()
							+"] differente da quello atteso per il test ["+contextTest.getIdRichiesta()+"]");
				}
			}
		}
		
		if(checkIDEgovRisposta){
			// ID EGov Risposta
			if(contextTest.getIdRisposta()!=null){
				if(Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_NULL.equals(contextTest.getIdRisposta())){
					if(contextPdD.getIdRisposta()!=null){
						throw new HandlerException("Informazione sull'id egov della risposta presente nel Contesto ritornato dalla porta di dominio ("+contextPdD.getIdRisposta()+"), ma non atteso dalla testsuite");
					}
				}else if(Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO.equals(contextTest.getIdRisposta())){
					if(contextPdD.getIdRisposta()==null){
						throw new HandlerException("Informazione sull'id egov della risposta non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
					}
					if(contextTest.getErogatore()==null){
						throw new HandlerException("Verifica dell'id egov della risposta non effettuabile se non si definisce il erogatore nel contesto del test");
					}
					if(contextTest.getErogatore().getNome()==null){
						throw new HandlerException("Verifica dell'id egov della risposta non effettuabile se non si definisce il nome del erogatore nel contesto del test");
					}
					if(!contextPdD.getIdRisposta().startsWith(contextTest.getErogatore().getNome()+"_"+contextTest.getErogatore().getNome()+"SPCoopIT_")){
						throw new HandlerException("Informazione sull'id egov della risposta presente nel Contesto ritornato dalla porta di dominio differente da quello atteso dalla testsuite: prefisso non contiene il nome del erogatore");
					}
				}else{
					if(contextPdD.getIdRisposta()==null){
						throw new HandlerException("Informazione sull'id egov della risposta non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
					}
					if(!contextTest.getIdRisposta().equals(contextPdD.getIdRisposta())){
						throw new HandlerException("ID egov della risposta presente nel Contesto della Porta di Dominio ["+contextPdD.getIdRisposta()
								+"] differente da quello atteso per il test ["+contextTest.getIdRisposta()+"]");
					}
				}
			}
		}
		
		// Profilo di Collaborazione
		if(contextTest.getProfiloCollaborazione()!=null){
			if(contextPdD.getProfiloCollaborazione()==null){
				throw new HandlerException("Informazione sul profilo di collaborazione non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
			}
			if(!contextTest.getProfiloCollaborazione().equals(contextPdD.getProfiloCollaborazione())){
				throw new HandlerException("Profilo di collaborazione presente nel Contesto della Porta di Dominio ["+contextPdD.getProfiloCollaborazione()
						+"] differente da quello atteso per il test ["+contextTest.getProfiloCollaborazione()+"]");
			}
		}
		
		// Collaborazione
		if(contextTest.getCollaborazione()!=null){
			if(Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL.equals(contextTest.getCollaborazione())){
				if(contextPdD.getCollaborazione()!=null){
					throw new HandlerException("Informazione sulla collaborazione presente nel Contesto ritornato dalla porta di dominio ("+contextPdD.getCollaborazione()+"), ma non atteso dalla testsuite");
				}
			}
			else{
				if(contextPdD.getCollaborazione()==null){
					throw new HandlerException("Informazione sulla collaborazione non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
				}
				if(!contextTest.getCollaborazione().equals(contextPdD.getCollaborazione())){
					throw new HandlerException("Collaborazione presente nel Contesto della Porta di Dominio ["+contextPdD.getCollaborazione()
							+"] differente da quello atteso per il test ["+contextTest.getCollaborazione()+"]");
				}
			}
		}
		
		// Scenario
		if(contextTest.getScenarioCooperazione()!=null){
			if(contextPdD.getScenarioCooperazione()==null){
				throw new HandlerException("Informazione sullo scenario di cooperazione non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
			}
			if(!contextTest.getScenarioCooperazione().equals(contextPdD.getScenarioCooperazione())){
				throw new HandlerException("Scenario di cooperazione presente nel Contesto della Porta di Dominio ["+contextPdD.getScenarioCooperazione()
						+"] differente da quello atteso per il test ["+contextTest.getScenarioCooperazione()+"]");
			}
		}
		
		// TipoServizio
		if(contextTest.getTipoServizio()!=null){
			if(contextPdD.getTipoServizio()==null){
				throw new HandlerException("Informazione sul tipo di servizio non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
			}
			if(!contextTest.getTipoServizio().equals(contextPdD.getTipoServizio())){
				throw new HandlerException("Tipo del servizio presente nel Contesto della Porta di Dominio ["+contextPdD.getTipoServizio()
						+"] differente da quello atteso per il test ["+contextTest.getTipoServizio()+"]");
			}
		}
		
		// Servizio
		if(contextTest.getServizio()!=null){
			if(contextPdD.getServizio()==null){
				throw new HandlerException("Informazione sul servizio non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
			}
			if(!contextTest.getServizio().equals(contextPdD.getServizio())){
				throw new HandlerException("Servizio presente nel Contesto della Porta di Dominio ["+contextPdD.getTipoServizio()
						+"] differente da quello atteso per il test ["+contextTest.getTipoServizio()+"]");
			}
		}
		
		// Azione
		if(contextTest.getAzione()!=null){
			if(Costanti.TEST_CONTEXT_EGOV_AZIONE_VERIFICA_NULL.equals(contextTest.getAzione())){
				if(contextPdD.getAzione()!=null){
					throw new HandlerException("Informazione sull'azione presente nel Contesto ritornato dalla porta di dominio ("+contextPdD.getAzione()+"), ma non atteso dalla testsuite");
				}
			}
			else{
				if(contextPdD.getAzione()==null){
					throw new HandlerException("Informazione sull'azione non presente nel Contesto ritornato dalla porta di dominio, ma attesa dalla testsuite");
				}
				if(!contextTest.getAzione().equals(contextPdD.getAzione())){
					throw new HandlerException("Azione presente nel Contesto della Porta di Dominio ["+contextPdD.getAzione()
							+"] differente da quello atteso per il test ["+contextTest.getAzione()+"]");
				}
			}
		}
	}
	
}
