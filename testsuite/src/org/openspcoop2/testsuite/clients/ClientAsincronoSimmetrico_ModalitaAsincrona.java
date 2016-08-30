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



package org.openspcoop2.testsuite.clients;



import javax.xml.soap.SOAPException;
import org.apache.axis.AxisFault;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;

/**
 * Client per la gestione del profilo di collaborazione Asincrono Simmetrico, in modalita asincrona.
 * La richiesta e la risposta vengono effettuate attraverso una interazione asincrona.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientAsincronoSimmetrico_ModalitaAsincrona extends ClientCore{
	
	/** Contatore utilizzato per la costruzione dell'Identificato (proprietario della testsuite) 
	 * inserito nell'header della richiesta 
	 * */
	private static int counter=0;
	
	/** Identificato (proprietario della testsuite) inserito nell'header della richiesta */
	protected String idTestSuiteHeader;
	/** Repository per la gestione del profilo asincrono simmetrico */
	private RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche;
	/** Repository per la correlazione delle due istanze asincrone */
	private RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincrone;
	
	public ClientAsincronoSimmetrico_ModalitaAsincrona(RepositoryConsegnaRisposteAsincroneSimmetriche repos,RepositoryCorrelazioneIstanzeAsincrone rep,String portaCorrelata) throws TestSuiteException{
		super();
		this.repositoryConsegnaRisposteAsincroneSimmetriche=repos;
		this.repositoryCorrelazioneIstanzeAsincrone=rep;
		// Ottengo l'id proprietario per la gestione del profilo
		synchronized(this.repositoryCorrelazioneIstanzeAsincrone){
			this.idTestSuiteHeader=Utilities.getHeaderValue(portaCorrelata,counter);
			counter++;
		}
	}

	public void run() throws TestSuiteException, AxisFault, SOAPException, InterruptedException{
		// imposta la url e l'autenticazione per il soapEngine
	    this.soapEngine.setConnectionParameters();
		// Imposta l'header utilizzato dalla testsuite
	    this.soapEngine.setIDTestSuiteHeader(this.idTestSuiteHeader);
	    // Effettua una invocazione asincrona
	    invocazioneAsincrona();
	    //  Messaggio spedito
	    this.sentMessage=this.soapEngine.getRequestMessage();
	    // Check id
	    if(this.idMessaggio==null)
	    	throw new TestSuiteException("ID Messaggio non presenta nell'header del trasporto della ricevuta.");
	    // Preleva l'id dal body della risposta
	    try{
	    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
		    if(this.receivedMessage.getSOAPBody()!=null && this.receivedMessage.getSOAPBody().hasChildNodes()){
			    String idBody=Utilities.getIDFromOpenSPCoopOKMessage(this.log,this.receivedMessage);
			    // Controlla che sia uguale a quello ritornato nell'header della risposta
			    if(idBody==null)
			    	throw new TestSuiteException("ID messaggio non presenta nella ricevuta OpenSPCoopOK.");
			    if(this.idMessaggio.equals(idBody)==false)
			    	throw new TestSuiteException("ID messaggio presente nell'header del trasporto della risposta differisce dall'id presente nel messaggio OpenSPCoopOK della ricevuta.");
				
		    }
	    }catch(Exception e){
	    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
	    }
	  	// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=this.idMessaggio;
      
	    this.log.info("[AsincronoSimmetrico_modalitaAsincrona] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");

	    
	    // Aspetto una risposta asincrona
	    // Ritira il messagggio dal ServerRicezioneRispostaAsincronaSimmetrica
	    this.receivedMessage=this.repositoryConsegnaRisposteAsincroneSimmetriche.get(this.idTestSuiteHeader); 
	    // Raccolgo identificativo della risposta asincrona
	    String identificativoRispostaAsincrona= Utilities.getValueFromHeaders(this.receivedMessage, this.testsuiteProperties.getIdMessaggioTrasporto());
	    // Raccolgo riferimentoAsincrono della risposta asincrona
	    String riferimentoAsincrono= null;
	    try{
	    	riferimentoAsincrono = Utilities.getValueFromHeaders(this.receivedMessage, this.testsuiteProperties.getRiferimentoAsincronoTrasporto());
	    }catch(Exception e){
	    	// Nelle linee guida il riferimento messaggio non è presente.
	    	riferimentoAsincrono = Utilities.getValueFromHeaders(this.receivedMessage, this.testsuiteProperties.getCollaborazioneTrasporto());
	    }
	    // Check riferimentoAsincrono uguale a identificativo richiesta
	    if(identificativoRichiestaAsincrona.equals(riferimentoAsincrono)==false){
	    	throw new TestSuiteException("RiferimentoAsincrono ritornato con la risposta ["+riferimentoAsincrono+"] non e' uguale all'identificativo di correlazione atteso ["+identificativoRichiestaAsincrona+"]");
	    }
	    
	    // Attesa terminazione messaggi
	    if(this.attesaTerminazioneMessaggi){
			if(this.dbAttesaTerminazioneMessaggiFruitore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
					try{
					Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
			if(this.dbAttesaTerminazioneMessaggiErogatore!=null){
				long countTimeout = System.currentTimeMillis() + (1000*this.testsuiteProperties.getTimeoutProcessamentoMessaggiOpenSPCoop());
				while(this.dbAttesaTerminazioneMessaggiErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
					try{
						Thread.sleep(this.testsuiteProperties.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
					}catch(Exception e){}
				}
			}
		}
	    
	    // Metto i due id nel repository
	    this.repositoryCorrelazioneIstanzeAsincrone.add(identificativoRichiestaAsincrona,identificativoRispostaAsincrona);
	    
	    
	    this.log.info("[AsincronoSimmetrico_modalitaAsincrona] Ricezione risposta correlata a "+identificativoRispostaAsincrona+" , correlata a "+identificativoRichiestaAsincrona+" effettata.");
	}


}
