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



package org.openspcoop2.testsuite.clients;

import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;


/**
 * Client per la gestione del profilo di collaborazione Asincrono Simmetrico, in modalita sincrona.
 * La richiesta e la risposta vengono effettuate attraverso una interazione sincrona.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientAsincronoSimmetrico_ModalitaSincrona extends ClientCore{
	
	/** Contatore utilizzato per la costruzione dell'Identificato (proprietario della testsuite) 
	 * inserito nell'header della richiesta 
	 * */
	private static int counter=0;
	
	/** Identificato (proprietario della testsuite) inserito nell'header della richiesta */
	protected String idTestSuiteHeader;
	/** Repository per la gestione del profilo asincrono simmetrico */
	RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche;
	/** Repository per la correlazione delle due istanze asincrone */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincrone;
	
	public ClientAsincronoSimmetrico_ModalitaSincrona(RepositoryConsegnaRisposteAsincroneSimmetriche repos,RepositoryCorrelazioneIstanzeAsincrone invRep,String portaCorrelata) throws TestSuiteException {
		super();
		this.repositoryConsegnaRisposteAsincroneSimmetriche=repos;
		this.repositoryCorrelazioneIstanzeAsincrone=invRep;
		// Ottengo l'id proprietario per la gestione del profilo
		synchronized(this.repositoryCorrelazioneIstanzeAsincrone){
			this.idTestSuiteHeader=Utilities.getHeaderValue(portaCorrelata,counter);
			counter++;
		}
	}
	
	
	
	public void run() throws TestSuiteException, InterruptedException, SOAPException, IOException{
		// imposta la url e l'autenticazione per il soapEngine
	    this.soapEngine.setConnectionParameters();
	    // Imposta l'header utilizzato dalla testsuite
	    this.soapEngine.setIDTestSuiteHeader(this.idTestSuiteHeader);
	    // Effettua una invocazione sincrona
	    invocazioneSincrona();
	    //	Messaggio spedito
	    this.sentMessage=this.soapEngine.getRequestMessage();
	    // Identificativo della richiesta
	    String identificativoRichiestaAsincrona=this.idMessaggio;
      
	    this.log.info("[AsincronoSimmetrico_modalitaSincrona] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");

	    //	Aspetto una risposta asincrona
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
	    
	    this.log.info("[AsincronoSimmetrico_modalitaSincrona] Ricezione risposta correlata a "+identificativoRichiestaAsincrona+" , correlata a "+identificativoRichiestaAsincrona+" effettata.");
	}
}
