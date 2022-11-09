/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.eventi.dao.IEventoService;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.controllo_traffico.NotificatoreEventi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.GestorePolicyAttive;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.threads.BaseThread;
import org.slf4j.Logger;


/**     
 * TimerEventiThread
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerEventiThread extends BaseThread{

	public static TimerState STATE = TimerState.OFF; // abilitato in OpenSPCoop2Startup al momento dell'avvio
	
	public static final String ID_MODULO = "TimerEventi";
		
	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** Indicazione se deve essere effettuato il log delle query */
	private boolean debug = false;	
	
	/** Database */
//	private DataSource dsRuntime = null;
//	private String datasourceRuntime = null;
	private String tipoDatabaseRuntime = null; //tipoDatabase
	private DAOFactory daoFactory = null;
    private Logger daoFactoryLogger = null;
	private ServiceManagerProperties daoFactoryServiceManagerPropertiesPlugins = null;
	
	private OpenSPCoop2Properties properties;
	
	/** NotificatoreEventi */
	private NotificatoreEventi notificatoreEventi = null;
	
	/** LastInterval */
	private Date lastInterval;
	
	/** Immagine */
	private boolean forceCheckPrimoAvvio = false;
	
	
	
	/** Costruttore */
	@SuppressWarnings("deprecation")
	public TimerEventiThread(Logger log) throws Exception{
	
		this.log = log;
	
		this.properties = OpenSPCoop2Properties.getInstance();
		this.setTimeout(this.properties.getEventiTimerIntervalSeconds());
		
		// Eventi per Controllo Traffico
		if(this.properties.isControlloTrafficoEnabled()){
			this.notificatoreEventi = NotificatoreEventi.getInstance();
			
			boolean inizializzazioneAttiva = false;
			// Il meccanismo di ripristino dell'immagine degli eventi non sembra funzionare
			// Lascio comunque il codice se in futuro si desidera approfindire la questione
			if(inizializzazioneAttiva) {
				List<PolicyGroupByActiveThreadsType> tipiGestorePolicyRateLimiting = null;
				try{
					tipiGestorePolicyRateLimiting = GestorePolicyAttive.getTipiGestoriAttivi();
				}catch(Throwable e){
					this.log.error("Errore durante l'inizializzazione dell'immagine degli eventi per il Controllo del Traffico: "+e.getMessage(),e);
				}
				if(tipiGestorePolicyRateLimiting!=null && !tipiGestorePolicyRateLimiting.isEmpty()) {
					for (PolicyGroupByActiveThreadsType type : tipiGestorePolicyRateLimiting) {
						File fDati = null;
						try{
							File fRepository = this.properties.getControlloTrafficoGestorePolicyFileSystemRecoveryRepository();
							if(fRepository!=null){
								if(fRepository.exists()==false){
									throw new Exception("Directory ["+fRepository.getAbsolutePath()+"] not exists");
								}
								if(fRepository.isDirectory()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] is not directory");
								}
								if(fRepository.canRead()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot read");
								}
								if(fRepository.canWrite()==false){
									throw new Exception("File ["+fRepository.getAbsolutePath()+"] cannot write");
								}
								fDati = new File(fRepository, GestorePolicyAttive.getControlloTrafficoEventiImage(type));
								if(fDati.exists() && fDati.canRead() && fDati.length()>0){
									FileInputStream fin = new FileInputStream(fDati);
									this.notificatoreEventi.initialize(fin);
									fDati.delete();
									this.forceCheckPrimoAvvio = true;
								}
							}
						}catch(Exception e){
							String img = null;
							if(fDati!=null){
								img = fDati.getAbsolutePath();
							}
							throw new HandlerException("Inizializzazione dell'immagine degli eventi ["+img+"] per il Controllo del Traffico non riuscita: "+e.getMessage(),e);
						}
					}
				}
			}
		}
		
		this.lastInterval = DateManager.getDate();
		
		this.debug = this.properties.isEventiDebug();
		
		try{
			
			this.tipoDatabaseRuntime = this.properties.getDatabaseType();		
			if(this.tipoDatabaseRuntime==null){
				throw new Exception("Tipo Database non definito");
			}
			
			// DAOFactory
			DAOFactoryProperties daoFactoryProperties = null;
			this.daoFactoryLogger = this.log;
			this.daoFactory = DAOFactory.getInstance(this.daoFactoryLogger);
			daoFactoryProperties = DAOFactoryProperties.getInstance(this.daoFactoryLogger);
			
			this.daoFactoryServiceManagerPropertiesPlugins = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance());
			this.daoFactoryServiceManagerPropertiesPlugins.setShowSql(this.debug);	
			this.daoFactoryServiceManagerPropertiesPlugins.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
			
		}catch(Exception e){
			throw new Exception("Errore durante l'inizializzazione del datasource: "+e.getMessage(),e);
		}
	}
	
	
	@Override
	public void process(){
	
		if(TimerState.ENABLED.equals(STATE)) {
		
			DBTransazioniManager dbManager = null;
	    	Resource r = null;
	    	try{
	    		dbManager = DBTransazioniManager.getInstance();
				r = dbManager.getResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, null);
				if(r==null){
					throw new Exception("Risorsa al database non disponibile");
				}
				Connection con = (Connection) r.getResource();
				if(con == null)
					throw new Exception("Connessione non disponibile");	
	
				org.openspcoop2.core.eventi.dao.IServiceManager pluginsSM = 
						(org.openspcoop2.core.eventi.dao.IServiceManager) this.daoFactory.getServiceManager(org.openspcoop2.core.eventi.utils.ProjectInfo.getInstance(), con,
								this.daoFactoryServiceManagerPropertiesPlugins, this.daoFactoryLogger);
				IEventoService eventoService = pluginsSM.getEventoService();
				
				if(this.properties.isControlloTrafficoEnabled()){
					this.lastInterval = this.notificatoreEventi.process(this.log,eventoService, this.getTimeout(), this.lastInterval, con, this.debug, this.forceCheckPrimoAvvio);
				}
				
				// Aggiungere in futuro altre gestione degli eventi
				
			}catch(Exception e){
				this.log.error("Errore durante la generazione degli eventi: "+e.getMessage(),e);
			}finally{
				try{
					if(r!=null)
						dbManager.releaseResource(this.properties.getIdentitaPortaDefaultWithoutProtocol(), ID_MODULO, r);
				}catch(Exception eClose){
					// ignore
				}
			}
	    	
		}
		else {
			this.log.info("Timer "+ID_MODULO+" disabilitato");
		}
				
	}
	
	@Override
	public void close(){
		this.log.info("Thread per la generazione degli eventi terminato");
	}
		
}
