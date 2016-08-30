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


package org.openspcoop2.web.ctrlstat.gestori;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationLogger;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddTipologia;

/**
 * GestorePdDInitThread
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class GestorePdDInitThread extends Thread {

	private ConsoleProperties consoleProperties;
	
	/** Logger utilizzato per debug. */
	private static Logger log = null;
	/** run */
	// public static boolean stop = false;
	private static Hashtable<String, IGestore> gestoriPdd = new Hashtable<String, IGestore>();

	private boolean singlePdD = false;
	private boolean enginePDD = false;
	
	/** Costruttore 
	 * @throws OpenSPCoop2ConfigurationException */
	public GestorePdDInitThread() throws OpenSPCoop2ConfigurationException {
		
		GestorePdDInitThread.log = ControlStationLogger.getGestorePddLogger();

		this.consoleProperties = ConsoleProperties.getInstance();
		
		// start();
	}

	/**
	 * Metodo che fa partire il Thread.
	 * 
	 */
	@Override
	public void run() {

		try {
			this.initGestore();

			if (this.singlePdD) {
				log.warn("GestorePdDInitThread non avviato: pddConsole avviata in singlePdD mode.");
				return;
			}

		} catch (GestoreNonAttivoException e) {
			log.warn("Inizializzazione GestorePdDInitThread non effettuata : " + e.getMessage());
			return;
		} catch (Exception e) {
			log.error("Inizializzazione Gestore PdD Init Thread Fallita : " + e.getMessage(), e);
			return;
		}
		
		try {

			// Controllo se dbmanager inizializzato
			// Il DBManager viene inizializzato nell'InitListener
			if (!DBManager.isInitialized()) {
				GestorePdDInitThread.log.info("Inizializzazione di " + this.getClass().getSimpleName() + " non riuscito perche' DBManager non INIZIALIZZATO");
				GestorePdDInitThread.log.info("Gestore Pdd non avviato!");
				return;
			}

			DBManager dbm = DBManager.getInstance();
			Connection con = dbm.getConnection();

			PddCore core = new PddCore();
			if(core.isSinglePdD()==false){
				List<String> pddList = core.getAllIdPorteDominio(null);
				if(pddList!=null && pddList.size()>0)
					GestorePdDInitThread.log.info("Trovate "+pddList.size()+" pdd su cui avviare il thread di gestione");
				else
					GestorePdDInitThread.log.info("Non sono state trovate pdd da gestire");
				for (int i = 0; i < pddList.size(); i++) {
					PdDControlStation pdd = null;
					try{
						pdd = core.getPdDControlStation(pddList.get(i));
					}catch(DriverControlStationNotFound dNot){
						GestorePdDInitThread.log.error("Errore durante la lettura dei dati della pdd ["+pddList.get(i)+"]: "+dNot.getMessage());
					}
					addGestore(pdd);				
				}
			}

			// Chiudo la connessione al DB
			dbm.releaseConnection(con);
		} catch (org.openspcoop2.web.ctrlstat.core.ControlStationCoreException csce) {
			GestorePdDInitThread.log.error("ControlStationCoreException: " + csce.getMessage());
		} catch (org.openspcoop2.web.ctrlstat.driver.DriverControlStationException dcse) {
			GestorePdDInitThread.log.error("DriverControlStationException: " + dcse.getMessage());
		} catch (Exception dcse) {
			GestorePdDInitThread.log.error("DriverControlStationException: " + dcse.getMessage());
		}
	}

	/**
	 * Aggiunge una porta di dominio alla lista di porte di dominio e avvia il
	 * thread di gestione della PdD
	 * 
	 * @param nomeCoda
	 * @throws OpenSPCoop2ConfigurationException 
	 */
	public static void startPdD(String nomeCoda) throws OpenSPCoop2ConfigurationException {
		GestorePdDThread pdd = new GestorePdDThread(nomeCoda);
		GestorePdDInitThread.gestoriPdd.put(nomeCoda,pdd);
		new Thread(pdd).start();
	}

	public void stopGestore() {
		// Stoppo tutti i thread gestori delle pdd
		Enumeration<String> pdds = gestoriPdd.keys();
		while (pdds.hasMoreElements()) {
			String pdd = (String) pdds.nextElement();
			deleteGestore(pdd);
		}

	}

	
	public void initGestore() throws Exception {

		this.enginePDD = this.consoleProperties.isGestioneCentralizzata_SincronizzazionePdd();
		this.singlePdD = this.consoleProperties.isSinglePdD();

		if (this.enginePDD == false) {
			//this.log.info("Motore di sincronizzazione verso le Porte di Dominio non attivo.");
			throw new GestoreNonAttivoException("Motore di sincronizzazione verso le Porte di Dominio non attivo.");
		}

	}
	
	
	public static void addGestore(PdDControlStation pdd) throws Exception {
		if(gestoriPdd.containsKey(pdd.getNome())==false){
			try{
				String tipoPdd = pdd.getTipo();
				if (tipoPdd != null && PddTipologia.OPERATIVO.toString().equals(tipoPdd)) {
					GestorePdDInitThread.log.info("Avvio thread di gestione per la porta di dominio ["+pdd.getNome()+"] ...");
					String nomeCoda = pdd.getNome();
					GestorePdDInitThread.startPdD(nomeCoda);
					GestorePdDInitThread.log.info("Avviato thread di gestione per la porta di dominio ["+pdd.getNome()+"]");
				}else{
					GestorePdDInitThread.log.info("Thread di gestione per la porta di dominio ["+pdd.getNome()+"] non avviato poiche' la porta di dominio non possiede tipo "+PddTipologia.OPERATIVO.toString()+" (tipo:"+pdd.getTipo()+")");
				}
			}catch(Exception e){
				GestorePdDInitThread.log.error("Thread di gestione per la porta di dominio ["+pdd.getNome()+"] non avviato: "+e.getMessage());
			}
		}
		else{
			GestorePdDInitThread.log.debug("Thread di gestione per la porta di dominio ["+pdd.getNome()+"] gia' in esecuzione");
		}
	}
	
	public static void deleteGestore(String pdd)  {
		if(gestoriPdd.containsKey(pdd)==false){
			GestorePdDInitThread.log.debug("Thread di gestione per la porta di dominio ["+pdd+"] non risulta in esecuzione");
		}
		else{
			GestorePdDInitThread.log.debug("Fermo il thread di gestione per la porta di dominio ["+pdd+"] ...");
			GestorePdDThread gestore = (GestorePdDThread) gestoriPdd.get(pdd);
			gestore.stopGestore();
			int timeout = 60;
			for (int i = 0; i < timeout; i++) {
				if(gestore.isRunning()){
					try{
						Thread.sleep(1000);
					}catch(Exception e){}
				}
				else{
					break;
				}
			}
			GestorePdDInitThread.log.debug("Thread di gestione per la porta di dominio ["+pdd+"] terminato");
			gestoriPdd.remove(pdd);
		}
	}
}
