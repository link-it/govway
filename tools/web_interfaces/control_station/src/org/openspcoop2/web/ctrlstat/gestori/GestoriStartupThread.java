/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.web.ctrlstat.gestori;

import org.slf4j.Logger;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;

/**
*
* GestoriStartupThread
* 
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public class GestoriStartupThread implements Runnable {
	
     private Logger log = null;

     // Gestori
     private GestorePdDInitThread pddInit;
     private GestoreRegistroThread registro;
     private SmistatoreThread smistatore;
     //private GestoreEventi gestoreEventi;

     public GestoriStartupThread() {
    	 this.log = ControlStationLogger.getPddConsoleCoreLogger();
     }

     @Override
	public void run() {
    	 
    	 try{
    	 
	    	 // Controllo inizializzazione risorse
	    	 // L'inizializzazione del core attende anche che venga inizializzato il datasource
	    	 new ControlStationCore();
	    	 
	         // Adesso posso avviare i Gestori
	         this.log.info("Inizializzazione Gestori....");
	         
	         // Gestore PDD
	         this.pddInit = new GestorePdDInitThread();
	         this.log.info("Avvio " + this.pddInit.getClass().getName());
	         new Thread(this.pddInit).start();
	
	         // Registro
	         this.registro = new GestoreRegistroThread();
	         this.log.info("Avvio " + this.registro.getClass().getName());
	         new Thread(this.registro).start();
	
	         // Smistatore
	         this.smistatore = new SmistatoreThread();
	         this.log.info("Avvio " + this.smistatore.getClass().getName());
	         new Thread(this.smistatore).start();
	
	         // Gestore Eventi
	         /*
	         this.gestoreEventi = new GestoreEventi();
	         this.log.info("Avvio " + this.gestoreEventi.getClass().getName());
	         new Thread(this.gestoreEventi).start();
	         */
	
	         this.log.info("Inizializzazione Gestori effettuata con successo.");
	         
    	 }catch(Exception e){
    		 this.log.error(e.getMessage(),e);
    		 throw new RuntimeException(e.getMessage(),e);
    	 }
    	 
     }
     
     public void stopGestori() {
         this.log.info("Terminazione Gestori in corso...");

         this.pddInit.stopGestore();
         this.registro.stopGestore();
         this.smistatore.stopGestore();
         //this.gestoreEventi.stopGestore();

         this.log.info("Terminazione gestori effettuata con successo.");
     }
}
