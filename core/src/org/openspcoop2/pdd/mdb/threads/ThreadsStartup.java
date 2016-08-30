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

package org.openspcoop2.pdd.mdb.threads;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Metodi per funzionalita' alternative all MDB 
 * 
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ThreadsStartup implements ServletContextListener {

	/** Logger utilizzato per segnalazione di errori. */
	private static Logger log = LoggerWrapperFactory.getLogger(ThreadsStartup.class);
	
	/** Indicazione su una corretta inizializzazione */
	public static boolean initialize = false;
	
	/** Gestore InoltroBuste */
	private InoltroBuste inoltroBuste = null;
	
	/** Gestore Sbustamento */
	private SbustamentoMain sbustamento = null;
	
	/** Gestore SbustamentoRisposte */
	private SbustamentoRisposteMain sbustamentoRisposte = null;
	
	/** Gestore Imbustamento */
	private ImbustamentoMain imbustamento = null;
	
	/** Gestore ImbustamentoRisposte */
	private ImbustamentoRisposteMain imbustamentoRisposte = null;
	
	/** Gestore InoltroRisposte */
	private InoltroRisposteMain inoltroRisposte = null;
	
	/** Gestore ConsegnaContenutiApplicativi */
	private ConsegnaContenutiApplicativiMain consegnaContenutiApplicativi = null;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
		fermaModuloAlternativo();	
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		avviaModuloAlternativo();
	}
	
	
	void avviaModuloAlternativo() {
	
		/** *********** Thread Alternativi all MDB ************************ */
		//Inizializzazione Thread InoltroBuste
		ThreadsStartup.log.info("Inizializzo il Thread InoltroBuste");
		this.inoltroBuste = new InoltroBuste();
		this.inoltroBuste.start();
		
		//Inizializzazione Thread InoltroRisposte
		ThreadsStartup.log.info("Inizializzo il Thread InoltroRisposte");
		this.inoltroRisposte = new InoltroRisposteMain();
		this.inoltroRisposte.start();
		
		//Inizializzazione Thread Imbustamento
		ThreadsStartup.log.info("Inizializzo il Thread Imbustamento");
		this.imbustamento = new ImbustamentoMain();
		this.imbustamento.start();
		
		//Inizializzazione Thread ImbustamentoRisposte
		ThreadsStartup.log.info("Inizializzo il Thread ImbustamentoRisposte");
		this.imbustamentoRisposte = new ImbustamentoRisposteMain();		
		this.imbustamentoRisposte.start();
		
		//Inizializzazione Thread Sbustamento
		ThreadsStartup.log.info("Inizializzo il Thread Sbustamento");
		this.sbustamento = new SbustamentoMain();
		this.sbustamento.start();
		
		//Inizializzazione Thread SbustamentoRisposte
		ThreadsStartup.log.info("Inizializzo il Thread SbustamentoRisposte");
		this.sbustamentoRisposte = new SbustamentoRisposteMain();
		this.sbustamentoRisposte.start();
		
		//Inizializzazione Thread ConsegnaContenutiApplicativi
		ThreadsStartup.log.info("Inizializzo il Thread ConsegnaContenutiApplicativi");
		this.consegnaContenutiApplicativi = new ConsegnaContenutiApplicativiMain();
		this.consegnaContenutiApplicativi.start();
		
	}
	
	void fermaModuloAlternativo(){
		//fermo il Thread InoltroBuste
		if(this.inoltroBuste!=null){
			this.inoltroBuste.setStop(true);
		}
		
		//fermo il Thread InoltroRisposte
		if(this.inoltroRisposte!=null){
			this.inoltroRisposte.setStop(true);
		}
	
		//fermo il Thread Imbustamento
		if(this.imbustamento!=null){
			this.imbustamento.setStop(true);
		}
		
		//fermo il Thread ImbustamentoRisposte
		if(this.imbustamentoRisposte!=null){
			this.imbustamentoRisposte.setStop(true);
		}
		
		//fermo il Thread Imbustamento
		if(this.sbustamento!=null){
			this.sbustamento.setStop(true);
		}
		
		//fermo il Thread ImbustamentoRisposte
		if(this.sbustamentoRisposte!=null){
			this.sbustamentoRisposte.setStop(true);
		}
		
		//fermo il Thread consegnaContenutiApplicativi
		if(this.consegnaContenutiApplicativi!=null){
			this.consegnaContenutiApplicativi.setStop(true);
		}
	}
		
}