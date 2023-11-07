/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.timers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.rmi.PortableRemoteObject;

import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.DBStatisticheManager;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.semaphore.Semaphore;
import org.openspcoop2.utils.semaphore.SemaphoreConfiguration;
import org.openspcoop2.utils.semaphore.SemaphoreMapping;
import org.slf4j.Logger;

/**
 * Libreria contenente metodi utili per lo smistamento delle buste 
 * all'interno dei moduli di openspcoop realizzati tramite servizi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TimerUtils {
    
  
   

    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Riscontri Scaduti
     *
     * @return un EJB utile alla gestione dei riscontri scaduti {@link org.openspcoop2.pdd.timers.TimerGestoreBusteNonRiscontrate}.
     * 
     */
    public static TimerGestoreBusteNonRiscontrate createTimerGestoreBusteNonRiscontrate() throws Exception {
        
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
	    
        	String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreBusteNonRiscontrate.ID_MODULO);
        	Object objref = jndi.lookup(nomeJNDI);
        	TimerGestoreBusteNonRiscontrateHome timerHome = 
        		(TimerGestoreBusteNonRiscontrateHome) PortableRemoteObject.narrow(objref,TimerGestoreBusteNonRiscontrateHome.class);
        	TimerGestoreBusteNonRiscontrate timerDiServizio = timerHome.create();	
        
            return timerDiServizio;
	    
        
    }




    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Messaggi
     *
     * @return un EJB utile alla gestione dei messaggi {@link org.openspcoop2.pdd.timers.TimerGestoreMessaggi}.
     * 
     */
    public static TimerGestoreMessaggi createTimerGestoreMessaggi() throws Exception {
       
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	
	    String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreMessaggi.ID_MODULO);
	    Object objref = jndi.lookup(nomeJNDI);
	    TimerGestoreMessaggiHome timerHome = 
		(TimerGestoreMessaggiHome) PortableRemoteObject.narrow(objref,TimerGestoreMessaggiHome.class);
	    TimerGestoreMessaggi timerDiServizio = timerHome.create();	
 
            return timerDiServizio;
	    
       
    }
    
    
    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione dei Messaggi anomali
     *
     * @return un EJB utile alla gestione dei messaggi {@link org.openspcoop2.pdd.timers.TimerGestorePuliziaMessaggiAnomali}.
     * 
     */
    public static TimerGestorePuliziaMessaggiAnomali createTimerGestorePuliziaMessaggiAnomali() throws Exception {
       
    	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
    	GestoreJNDI jndi = null;
    	if(properties.getJNDIContext_TimerEJB()==null)
    		jndi = new GestoreJNDI();
    	else
    		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	
	    String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestorePuliziaMessaggiAnomali.ID_MODULO);
	    Object objref = jndi.lookup(nomeJNDI);
	    TimerGestorePuliziaMessaggiAnomaliHome timerHome = 
		(TimerGestorePuliziaMessaggiAnomaliHome) PortableRemoteObject.narrow(objref,TimerGestorePuliziaMessaggiAnomaliHome.class);
	    TimerGestorePuliziaMessaggiAnomali timerDiServizio = timerHome.create();	
 
	    return timerDiServizio;
	    
       
    }

    
    /**
     * Metodo che si occupa di ottenere il EJB utile alla gestione delle buste
     *
     * @return un EJB utile alla gestione delle buste {@link org.openspcoop2.pdd.timers.TimerGestoreRepositoryBuste}.
     * 
     */
    public static TimerGestoreRepositoryBuste createTimerGestoreRepositoryBuste() throws Exception {
       
        	OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
        	GestoreJNDI jndi = null;
        	if(properties.getJNDIContext_TimerEJB()==null)
        		jndi = new GestoreJNDI();
        	else
        		jndi = new GestoreJNDI(properties.getJNDIContext_TimerEJB());
        	String nomeJNDI = properties.getJNDITimerEJBName().get(TimerGestoreRepositoryBuste.ID_MODULO);
        	Object objref = jndi.lookup(nomeJNDI);
        	TimerGestoreRepositoryBusteHome timerHome = 
        		(TimerGestoreRepositoryBusteHome) PortableRemoteObject.narrow(objref,TimerGestoreRepositoryBusteHome.class);
        	TimerGestoreRepositoryBuste timerDiServizio = timerHome.create();	
 
        	return timerDiServizio;
	    
    }
    

    public static boolean createEmptyLockTimers(OpenSPCoop2Properties propertiesReader, String ID_MODULO, Logger logCore, boolean logErrorConnection) {
    	return _lockTimers(propertiesReader, ID_MODULO, logCore, logErrorConnection, false);
    }
    public static void relaseLockTimers(OpenSPCoop2Properties propertiesReader, String ID_MODULO, Logger logCore, boolean logErrorConnection) {
    	_lockTimers(propertiesReader, ID_MODULO, logCore, logErrorConnection, true);
    }
    public static boolean _lockTimers(OpenSPCoop2Properties propertiesReader, String ID_MODULO, Logger logCore, boolean logErrorConnection, boolean release) {
		
    	boolean ok = true;
    	
    	if(propertiesReader!=null && propertiesReader.isTimerLockByDatabase() && propertiesReader.getDatabaseType()!=null) {
			
			// RILASCIO LOCK SUL RUNTIME
			
			List<TipoLock> tipiStatistiche = new ArrayList<TipoLock>();
			tipiStatistiche.add(TipoLock.GENERAZIONE_STATISTICHE_ORARIE);
			tipiStatistiche.add(TipoLock.GENERAZIONE_STATISTICHE_GIORNALIERE);
			tipiStatistiche.add(TipoLock.GENERAZIONE_STATISTICHE_SETTIMANALI);
			tipiStatistiche.add(TipoLock.GENERAZIONE_STATISTICHE_MENSILI);
			
			List<TipoLock> tipiRuntime = new ArrayList<TipoLock>();
			tipiRuntime.add(TipoLock.GESTIONE_BUSTE_NON_RISCONTRATE);
			tipiRuntime.add(TipoLock.GESTIONE_CORRELAZIONE_APPLICATIVA);
			tipiRuntime.add(TipoLock.GESTIONE_PULIZIA_MESSAGGI_ANOMALI);
			if(propertiesReader.isMsgGiaInProcessamentoUseLock()) {
				tipiRuntime.add(TipoLock._getLockGestioneRepositoryMessaggi());
			}
			else {
				tipiRuntime.add(TipoLock.GESTIONE_PULIZIA_REPOSITORY_MESSAGGI);
				tipiRuntime.add(TipoLock.GESTIONE_PULIZIA_REPOSITORY_BUSTE);
			}
			if(propertiesReader.isStatisticheGenerazioneEnabled()){
				if(propertiesReader.isStatisticheUsePddRuntimeDatasource()) {
					tipiRuntime.addAll(tipiStatistiche);
				}
			}

			if(tipiRuntime!=null && tipiRuntime.size()>0) {
				Resource resource = null;
				DBManager dbManager = DBManager.getInstance();
				try {
					resource = dbManager.getResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null, false);
					
					for (TipoLock tipoLock : tipiRuntime) {
						_lock(propertiesReader, (Connection)resource.getResource(), tipoLock, logCore, release);
					}
					
					if(propertiesReader.isServerJ2EE()!=null && propertiesReader.isServerJ2EE()==false){
						if(propertiesReader.isTimerConsegnaContenutiApplicativiAbilitato()){
							List<String> code = propertiesReader.getTimerConsegnaContenutiApplicativiCode();
							if(code!=null && !code.isEmpty()) {
								for (String coda : code) {
									boolean result = _lock(propertiesReader, (Connection)resource.getResource(), TimerLock.getIdLockConsegnaNotifica(coda), logCore, release);
									if(!result) {
										ok = false; // l'errore è registrato all'interno del metodo _lock
									}
								}
							}
						}
					}
					
				}catch(Exception e){
					ok = false;
					if(logErrorConnection) {
						logCore.error("Riscontrato errore durante il "+(release?"il rilascio":"la creazione")+" dei lock: "+e.getMessage(),e);
					}
					else {
						logCore.debug("Riscontrato errore durante il "+(release?"il rilascio":"la creazione")+" dei lock, in fase di shutdown: "+e.getMessage(),e);
					}
				}
				finally {
					if(dbManager!=null) {
						dbManager.releaseResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, resource, false);
					}
				}
			}
			
			// RILASCIO LOCK STATISTICHE SE NON SONO SUL RUNTIME
			if(propertiesReader.isStatisticheGenerazioneEnabled() && !propertiesReader.isStatisticheUsePddRuntimeDatasource()){
				
				if(tipiStatistiche!=null && tipiStatistiche.size()>0) {
					Resource resource = null;
					DBStatisticheManager dbStatisticheManager = null;
					DBTransazioniManager dbTransazioniManager = null;
					if(propertiesReader.isStatisticheUseTransazioniDatasource()) {
						dbTransazioniManager = DBTransazioniManager.getInstance();
					}
					else {
						dbStatisticheManager = DBStatisticheManager.getInstance();
					}
					try {
						if(dbTransazioniManager!=null) {
							resource = dbTransazioniManager.getResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null, false);
						}
						else if(dbStatisticheManager!=null){
							resource = dbStatisticheManager.getResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null, false);
						}
						
						for (TipoLock tipoLock : tipiStatistiche) {
							if(resource!=null) {
								boolean result = _lock(propertiesReader, (Connection)resource.getResource(), tipoLock, logCore, release);
								if(!result) {
									ok = false; // l'errore è registrato all'interno del metodo _lock
								}
							}
							else {
								ok = false;
								logCore.error("Timer Resource is null?");
							}
						}
					}catch(Exception e){
						ok = false;
						if(logErrorConnection) {
							logCore.error("Riscontrato errore durante il "+(release?"il rilascio":"la creazione")+" dei lock: "+e.getMessage(),e);
						}
						else {
							logCore.debug("Riscontrato errore durante il "+(release?"il rilascio":"la creazione")+" dei lock, in fase di shutdown: "+e.getMessage(),e);
						}
					}
					finally {
						if(dbTransazioniManager!=null) {
							dbTransazioniManager.releaseResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, resource, false);
						}
						else if(dbStatisticheManager!=null){
							dbStatisticheManager.releaseResource(propertiesReader.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, resource, false);
						}
					}
				}
				
			}
		}
    	
    	return ok;
    }
    
    private static boolean _lock(OpenSPCoop2Properties propertiesReader, Connection connection, TipoLock tipoLock, Logger logCore, boolean release) {
    	return _lock(propertiesReader,connection,tipoLock.getTipo(),logCore, release);
    }
	private static boolean _lock(OpenSPCoop2Properties propertiesReader, Connection connection, String idLock, Logger logCore, boolean release) {
		boolean ok = true; 
		try{
			InfoStatistics semaphore_statistics = new InfoStatistics();
			SemaphoreConfiguration config = GestoreMessaggi.newSemaphoreConfiguration(propertiesReader.getTimerConsegnaContenutiApplicativi_lockMaxLife(), 
					propertiesReader.getTimerConsegnaContenutiApplicativi_lockIdleTime());
			//config.setSerializableLevel(true); // in modo da non avere il lock esclusivo sulla tabella e far funzionare anche se l'entry non esiste proprio
			TipiDatabase databaseType = TipiDatabase.toEnumConstant(propertiesReader.getDatabaseType());
			Semaphore semaphore = new Semaphore(semaphore_statistics, SemaphoreMapping.newInstance(idLock), 
					config, databaseType, logCore);
			if(release) {
				boolean unlock = semaphore.releaseLock(connection, "Riavvio application server");
				logCore.debug("Lock di tipo '"+idLock+"' "+(unlock?"rilasciato" :"non rilasciato"));
			}
			else {
				boolean created = semaphore.createEmptyLock(connection, false);
				logCore.debug("Lock di tipo '"+idLock+"' "+(created?"creato" :"non creato"));
			}
		}catch(Exception e){
			ok = false;
			logCore.error("Riscontrato errore durante "+(release?"il rilascio":"la creazione")+" del lock di tipo '"+idLock+"': "+e.getMessage(),e);
		}
		return ok;
	}
}
