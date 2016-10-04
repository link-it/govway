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


package org.openspcoop2.pdd.mdb.threads;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.threshold.ThreadsUtils;
import org.openspcoop2.pdd.mdb.GenericLibException;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModuloAlternativoMain extends Thread {

	protected DBManager dbManager;
	protected Logger log;
	protected OpenSPCoop2Properties oSPCpropertiesReader;
	protected ThreadsImplProperties propertiesReader;

	
	protected String ID_MODULO;
	protected ExecutorService pool;
	protected BlockingQueue <IWorker> coda;
	protected IProducer produttore;
	
	
	private boolean inizializzato = false;
	
	public ModuloAlternativoMain(String IdModulo){
	
		this.ID_MODULO = IdModulo;
			
		
	}

	private boolean inizializza(){
		/* ------------ Controllo inizializzazione OpenSPCoop -------------------- */
		while( OpenSPCoop2Startup.initialize == false ){
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		try {	
			initLogger(); 
			initLettoreProperties(); 
			this.dbManager = initDBManager();
			initCoda(); 
			initPool();
			return true;
		} catch (GenericLibException e) {
			System.out.println(this.ID_MODULO + " Errore in fase di init");
			return false;
		}
	}
	
	/*private boolean checkInit(){
		if (this.propertiesReader == null){
			System.out.println("Lettore ThreadProp null"); return false; }
		if (this.oSPCpropertiesReader == null){
				System.out.println("Lettore null");		return false;}
		if (this.dbManager == null ) {
			System.out.println("DBManager null"); return false;}
		if (this.coda == null){
			System.out.println("coda null");		return false;}
		if (this.pool == null){
			System.out.println("pool null");		return false; }
		return true;
	}*/
	
	@Override
	public void run() {
		System.out.println(this.ID_MODULO + " Main attivato");
		
		if (this.inizializzato==false) inizializza();
		
		try {
			this.produttore = creaProduttore();
			new Thread(this.produttore).start();
		}catch (Exception e) {
			this.log.error(this.ID_MODULO + ": Impossibile creare i consumatori");
		}

		while (!this.stop) {
			consuma();
		}
		

		this.produttore.setStop(true);
		
		this.pool.shutdownNow();
		System.out.println(this.ID_MODULO + " Main disattivato");
	}
	
	

	protected IProducer creaProduttore() throws Exception {
		// impl nelle sottoclassi
		return null;
	}

	public void consuma(){
		if (this.stop == false) {
			Runnable r = this.coda.poll();
			if (r!=null) { this.pool.execute(r); }
			else ThreadsUtils.attesa(50);
		}
	}
	
	/** Estrae un Thread di gestione messaggio dalla coda e lo manda in esecuzione */
	private void initLettoreProperties() throws GenericLibException{
		try{
			this.propertiesReader = ThreadsImplProperties.getInstance();
			this.oSPCpropertiesReader = OpenSPCoop2Properties.getInstance();
		}catch (Exception e) {
			throw new GenericLibException("Riscontrato Errore durante l'inizializzazione del Reader della Configurazione");
		}
	}

	/* **************** Metodi di init ********************** */
	private void initLogger() throws GenericLibException {
		try {
			this.log = LoggerWrapperFactory.getLogger(ModuloAlternativoMain.class);
		} catch (Exception e) {
			if(this.log==null)
				System.out.println("Riscontrato errore durante l'inizializzazione del Logger: "
						+ e.getMessage());
			throw new GenericLibException("Riscontrato Errore durante l'inizializzazione del Logger per il Thread "+ this.ID_MODULO);
		}
	}
	
	private DBManager initDBManager() {
		return DBManager.getInstance();
	}

	private void initPool() {
		String tipoPool = this.propertiesReader.getPoolType();
		if (tipoPool.equals("fixed")){
			this.pool = Executors.newFixedThreadPool(this.propertiesReader.getPoolDepth());
		}
		else if (tipoPool.equals("cached")){
			this.pool = Executors.newCachedThreadPool();
		}
	}
	
	private void initCoda() {
		String tipoCoda = this.propertiesReader.getCodaType();
		if (tipoCoda.equals("array")){
			this.coda = new ArrayBlockingQueue <IWorker> (this.propertiesReader.getCodaDepth());
		}
		else if (tipoCoda.equals("linked")){
			this.coda = new LinkedBlockingQueue <IWorker> ();
		}		
	}
	
	/** Stop */
	protected boolean stop = false;
	public void setStop(boolean stop){	this.stop = stop; }
	public boolean isStop(){	return this.stop;	}

}
