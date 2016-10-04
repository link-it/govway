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
import org.apache.axis.AxisFault;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.Utilities;

/**
 * Client per la gestione del profilo di collaborazione OneWay.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ClientOneWay extends ClientCore{
	  
	public ClientOneWay(Repository rep)throws TestSuiteException{
		super();
		this.repository=rep;
	}
	

    public void run()throws TestSuiteException,AxisFault{
    	// imposta la url e l'autenticazione per il soapEngine
	    this.soapEngine.setConnectionParameters();
	    // Effettua una invocazione asincrona
	    invocazioneAsincrona();
	    // Messaggio spedito
	    this.sentMessage=this.soapEngine.getRequestMessage();
	    // Check
	    if(this.idMessaggio==null)
	    	throw new TestSuiteException("ID messaggio non presenta nell'header del trasporto della risposta.");
	    // Preleva l'id dal body della risposta
	    try{
	    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
		    if(this.receivedMessage.getSOAPBody()!=null && this.receivedMessage.getSOAPBody().hasChildNodes()){
			    String idBody=Utilities.getIDFromOpenSPCoopOKMessage(this.log,this.receivedMessage);
			    // Controlla che sia uguale a quello ritornato nell'header della risposta
			    if(idBody==null)
			    	throw new TestSuiteException("ID messaggio non presenta nella risposta OpenSPCoopOK.");
			    if(this.idMessaggio.equals(idBody)==false)
			    	throw new TestSuiteException("ID messaggio presente nell'header del trasporto della risposta differisce dall'id presente nel messaggio OpenSPCoopOK della risposta.");
				
		    }
	    }catch(Exception e){
	    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
	    }
	   // Aggiungo nel repository l'id del messaggio raccolto   
	    this.repository.add(this.idMessaggio);
	}
}



