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



package org.openspcoop2.protocol.registry;

import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.ValidazioneSemantica;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;

/**
 * Classe utilizzata per ottenere informazioni che interessano ad OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lezza Aldo (lezza@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroServiziReader {

	/* istanza di RegistroServiziReader */
	private static  RegistroServiziReader registroServiziReader;
	/* informazione sull'inizializzazione dell'istanza */
	private static boolean initialize = false;

	/* Registro dei Servizi */
	private RegistroServizi registroServizi;

	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

	/* --------------- Reset Cache --------------------*/
	public static void resetCache() throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.resetCache();
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Reset della cache di accesso ai registri dei servizi non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				return registroServiziReader.registroServizi.printStatsCache(separator);
			}
			else{
				throw new Exception("RegistroServizi Non disponibile");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Visualizzazione Statistiche riguardante la cache del registro dei servizi non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.abilitaCache();
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Abilitazione cache di accesso ai registri dei servizi non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond);
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Abilitazione cache di accesso ai registri dei servizi non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.disabilitaCache();
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Disabilitazione cache di accesso ai registri dei servizi non riuscita: "+e.getMessage(),e);
		}
	}	
	public static String listKeysCache(String separator) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				return registroServiziReader.registroServizi.listKeysCache(separator);
			}
			else{
				throw new Exception("RegistroServizi Non disponibile");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Visualizzazione chiavi presenti nella cache del RegistroServizi non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				return registroServiziReader.registroServizi.getObjectCache(key);
			}
			else{
				throw new Exception("RegistroServizi Non disponibile");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Visualizzazione oggetto presente nella cache del RegistroServizi non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.removeObjectCache(key);
			}
			else{
				throw new Exception("RegistroServizi Non disponibile");
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Rimozione oggetto presente nella cache del RegistroServizi non riuscita: "+e.getMessage(),e);
		}
	}


	/*   -------------- Metodi di inizializzazione -----------------  */

	/**
	 * Si occupa di inizializzare l'engine che permette di effettuare
	 * query al registro dei servizi.
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var> di registro :
	 * <ul>
	 * <li> {@link org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI}, interroga un registro dei servizi UDDI.
	 * <li> {@link org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML}, interroga un registro dei servizi realizzato tramite un file xml.
	 * <li> {@link org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB}, interroga un registro dei servizi realizzato come un WEB Server.
	 * <li> {@link org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB}, interroga un registro dei servizi realizzato come un Database relazionale.
	 * </ul>
	 *
	 * @param accessoRegistro Informazioni per accedere al registro Servizi.
	 * @return true se l'inizializzazione ha successo, false altrimenti.
	 */
	public static boolean initialize(AccessoRegistro accessoRegistro,Logger aLog,Logger aLogconsole,
			boolean raggiungibilitaTotale, boolean readObjectStatoBozza, 
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX){

		try {
			RegistroServiziReader.registroServiziReader = 
				new RegistroServiziReader(accessoRegistro,aLog,aLogconsole,raggiungibilitaTotale,readObjectStatoBozza,
						jndiNameDatasourcePdD,useOp2UtilsDatasource,bindJMX);	
			return RegistroServiziReader.initialize;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna lo stato dell'engine che permette di effettuare
	 * query al registro dei servizi. 
	 *
	 * @return true se l'inizializzazione all'engine e' stata precedentemente effettuata, false altrimenti.
	 */
	public static boolean isInitialize(){
		return RegistroServiziReader.initialize;
	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza del Reader del Registro
	 */
	protected static RegistroServiziReader getInstance(){
		return RegistroServiziReader.registroServiziReader;
	}












	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Inizializza il reader
	 *
	 * @param accessoRegistro Informazioni per accedere al registro Servizi.
	 */
	public RegistroServiziReader(AccessoRegistro accessoRegistro,Logger aLog,
			Logger aLogconsole,boolean raggiungibilitaTotale, boolean readObjectStatoBozza, 
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX)throws DriverRegistroServiziException{
		try{
			if(aLog!=null)
				this.log = aLog;
			else
				this.log = LoggerWrapperFactory.getLogger(RegistroServiziReader.class);
			this.registroServizi = new RegistroServizi(accessoRegistro,this.log,aLogconsole,raggiungibilitaTotale,readObjectStatoBozza,
					jndiNameDatasourcePdD, useOp2UtilsDatasource, bindJMX);
			RegistroServiziReader.initialize = true;
		}catch(Exception e){
			RegistroServiziReader.initialize = false;
		}
	}






	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws CoreException eccezione che contiene il motivo della mancata connessione
	 */
	protected void isAlive(boolean controlloTotale) throws CoreException{
		if(controlloTotale){
			for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				try{
					IMonitoraggioRisorsa monitorDriver = (IMonitoraggioRisorsa) this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
					monitorDriver.isAlive();
				}catch(Exception e){
					throw new CoreException("[Registro "+nomeRegInLista+"] "+e.getMessage(),e);
				}
			}
		}else{
			boolean registroRaggiungibile = false;
			StringBuffer eccezioni = new StringBuffer();
			for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				try{
					IMonitoraggioRisorsa monitorDriver = (IMonitoraggioRisorsa) this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
					monitorDriver.isAlive();
					registroRaggiungibile = true;
					break;
				}catch(Exception e){
					this.log.debug("Registro non accessibile [Registro "+nomeRegInLista+"]",e);
					eccezioni.append("\n[Registro "+nomeRegInLista+"] "+e.getMessage());
				}
			}
			if(registroRaggiungibile == false){
				throw new CoreException(eccezioni.toString());
			}
		}
	}
	
	
	/**
	 * Validazione semantica dei registri servizi
	 * 
	 * @throws CoreException eccezione che contiene il motivo della validazione semantica errata
	 */
	protected void validazioneSemantica(boolean controlloTotale,boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziValidi, String[] tipiConnettoriValidi,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltriRegistri,
			Logger logConsole) throws CoreException{
		if(controlloTotale){
			for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				try{
					Object o = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
					boolean validazione = false;
					if(o instanceof DriverRegistroServiziXML){
						validazione = validazioneSemanticaAbilitataXML;
					}else{
						validazione = validazioneSemanticaAbilitataAltriRegistri;
					}
					if(validazione){
						BeanUtilities driverRegistro = (BeanUtilities) o;
						org.openspcoop2.core.registry.RegistroServizi registroServizi = driverRegistro.getImmagineCompletaRegistroServizi();
						ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(registroServizi,verificaURI,
								tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziValidi);
						validazioneSemantica.validazioneSemantica(false);
						if(logConsole!=null){
							logConsole.info("Validazione semantica del registro dei servizi ["+nomeRegInLista+"] effettuata.");
						}
					}
				}catch(Exception e){
					throw new CoreException("[Registro "+nomeRegInLista+"] "+e.getMessage(),e);
				}
			}
		}else{
			boolean registroRaggiungibile = false;
			StringBuffer eccezioni = new StringBuffer();
			for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
				String nomeRegInLista= (String) en.nextElement();
				try{
					Object o = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
					boolean validazione = false;
					if(o instanceof DriverRegistroServiziXML){
						validazione = validazioneSemanticaAbilitataXML;
					}else{
						validazione = validazioneSemanticaAbilitataAltriRegistri;
					}
					if(validazione){
						BeanUtilities driverRegistro = (BeanUtilities) o;
						org.openspcoop2.core.registry.RegistroServizi registroServizi = driverRegistro.getImmagineCompletaRegistroServizi();
						ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(registroServizi,verificaURI,
								tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziValidi);
						validazioneSemantica.validazioneSemantica(false);
						if(logConsole!=null){
							logConsole.info("Validazione semantica del registro dei servizi ["+nomeRegInLista+"] effettuata.");
						}
						registroRaggiungibile = true;
						break;
					}
				}catch(Exception e){
					this.log.debug("Registro non accessibile [Registro "+nomeRegInLista+"]",e);
					eccezioni.append("\n[Registro "+nomeRegInLista+"] "+e.getMessage());
				}
			}
			if(registroRaggiungibile == false){
				throw new CoreException(eccezioni.toString());
			}
		}
	}
	
	
	
	protected void setValidazioneSemanticaModificaRegistroServiziXML(boolean verificaURI, 
			String[] tipiSoggettiValidi,String [] tipiServiziValidi, String[] tipiConnettoriValidi) throws CoreException{
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			try{
				Object o = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
				if(o instanceof DriverRegistroServiziXML){
					DriverRegistroServiziXML driver = (DriverRegistroServiziXML) o;
					driver.abilitazioneValidazioneSemanticaDuranteModificaXML(verificaURI, 
							tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziValidi);
				}
			}catch(Exception e){
				throw new CoreException("[Registro "+nomeRegInLista+"] "+e.getMessage(),e);
			}
		}
	}
	
	
	protected void verificaConsistenzaRegistroServizi() throws DriverRegistroServiziException {
		Object o = this.registroServizi.getDriverRegistroServizi();
		if(o instanceof DriverRegistroServiziXML){
			DriverRegistroServiziXML driver = (DriverRegistroServiziXML) o;
			driver.refreshRegistroServiziXML();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* ********  P R O F I L O   D I   G E S T I O N E  ******** */ 
	
	protected String getProfiloGestioneFruizioneServizio(Connection connectionPdD,IDServizio idServizio,String nomeRegistro) 
		throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		String profilo = null;
		
		// ricerca servizio richiesto
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		try{
			servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idServizio);
		}catch(DriverRegistroServiziNotFound e){}
		if(servizio != null){
			// Profilo di gestione
			profilo = servizio.getVersioneProtocollo();
		} 
		if(profilo == null){
			// vedo se il soggetto erogatore ha un profilo
			Soggetto soggettoErogatore =  this.registroServizi.getSoggetto(connectionPdD,nomeRegistro, idServizio.getSoggettoErogatore());
			if (soggettoErogatore == null){
				throw new DriverRegistroServiziNotFound("getProfiloGestioneFruizioneServizio, soggettoErogatore ["+idServizio.getSoggettoErogatore()+"] non definito (o non registrato)");
			}
			profilo = soggettoErogatore.getVersioneProtocollo();
		}
		return profilo;
	}
	
	protected String getProfiloGestioneErogazioneServizio(Connection connectionPdD,IDSoggetto idFruitore,IDServizio idServizio,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		String profilo = null;
		
		// ricerca servizio richiesto
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		try{
			servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idServizio);
		}catch(DriverRegistroServiziNotFound e){}
		if(servizio != null){
			// ricerco fruitore nel servizio
			Fruitore fruitore = null;
			if(idFruitore==null || idFruitore.getTipo()==null || idFruitore.getNome()==null){
				throw new DriverRegistroServiziException("getProfiloGestioneErogazioneServizio, soggetto fruitore non definito");
			}
			for(int i=0; i<servizio.sizeFruitoreList();i++){
				Fruitore tmp = servizio.getFruitore(i);
				if( (idFruitore.getTipo().equals(tmp.getTipo()))  && (idFruitore.getNome().equals(tmp.getNome())) ){
					fruitore = tmp;
					break;
				}
			}
			
			// Profilo di gestione
			if(fruitore!=null){
				profilo = fruitore.getVersioneProtocollo();
			}
		}

		if(profilo==null){
			// vedo se il soggetto fruitore ha un profilo
			Soggetto soggettoFruitore =  this.registroServizi.getSoggetto(connectionPdD,nomeRegistro, idFruitore);
			if (soggettoFruitore == null){
				throw new DriverRegistroServiziNotFound("getProfiloGestioneErogazioneServizio, soggettoFruitore ["+idFruitore+"] non definito (o non registrato)");
			}
			profilo = soggettoFruitore.getVersioneProtocollo();
		}
		
		return profilo;
	}
	
	protected String getProfiloGestioneSoggetto(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		// Profilo di gestione
		String profilo = null;
		
		// vedo se il soggetto fruitore ha un profilo
		Soggetto soggetto =  this.registroServizi.getSoggetto(connectionPdD, nomeRegistro, idSoggetto);
		if (idSoggetto == null){
			throw new DriverRegistroServiziNotFound("getProfiloGestioneSoggetto, Soggetto ["+idSoggetto+"] non definito (o non registrato)");
		}
		profilo = soggetto.getVersioneProtocollo();
		
		return profilo;
	}
	
	
	
	
	
	
	
	/* ********  R I C E R C A   I N F O     S E R V I Z I  ******** */ 
	/**
	 * Si occupa di ritornare le informazioni sulle funzionalita' associate
	 * al servizio registrato nell'indice dei servizi di openspcoop. Il servizio, viene identificato
	 * grazie ai fields 'tipoDestinatario','Destinatario','Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 * <p>
	 * Il metodo si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Servizio} 
	 * impostando in esso le funzionalita' che sono riscontrate nel registro dei servizi
	 * al servizio identificato da <var>idService</var>.
	 *
	 * @param idSoggetto Soggetto richiedente del servizio
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.id.IDServizio}
	 */
	protected Servizio getInfoServizio(Connection connectionPdD,IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,false,false,idSoggetto,idService,nomeRegistro,verificaEsistenzaServizioAzioneCorrelato);
	}

	/**
	 * Si occupa di ritornare le informazioni sulle funzionalita' associate
	 * ad un servizio correlato registrato nell'indice dei servizi di openspcoop. Il servizio, viene identificato
	 * grazie ai fields 'tipoDestinatario','Destinatario','Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 * <p>
	 * Il metodo si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Servizio} 
	 * impostando in esso le funzionalita' che sono riscontrate nel registro dei servizi
	 * al servizio identificato da <var>idService</var>.
	 *
	 * @param idSoggetto Soggetto richiedente del servizio
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return l'oggetto di tipo {@link org.openspcoop2.protocol.sdk.Servizio} 
	 */
	protected Servizio getInfoServizioCorrelato(Connection connectionPdD,IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,true,false,idSoggetto,idService,nomeRegistro,false);
	}
	
	/**
	 * Ritorna l'informazione su di un servizio, in cui l'azione chiamata e' correlata.
	 * 
	 * @param idSoggetto
	 * @param idService
	 * @param nomeRegistro
	 * @return Servizio
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 * @throws DriverRegistroServiziAzioneNotFound
	 */
	protected Servizio getInfoServizioAzioneCorrelata(Connection connectionPdD,IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
		throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,false,true,idSoggetto,idService,nomeRegistro,false);
	}

	/**
	 * Si occupa di ritornare le informazioni sulle funzionalita' associate
	 * al servizio registrato nell'indice dei servizi di openspcoop. Il servizio, viene identificato
	 * grazie ai fields 'tipoDestinatario','Destinatario','Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 * <p>
	 * Il metodo si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Servizio} 
	 * impostando in esso le funzionalita' che sono riscontrate nel registro dei servizi
	 * al servizio identificato da <var>idService</var>.
	 *
	 * @param servizioCorrelato true se il servizio e' un servizio correlato
	 * @param idSoggetto Soggetto richiedente del servizio
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.id.IDServizio}
	 */
	private Servizio getInfoServizio(Connection connectionPdD,boolean servizioCorrelato,boolean azioneCorrelata,IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{

		Servizio infoServizio = new Servizio();

		// ricerca servizio richiesto
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idService);
		if(servizio == null){
			throw new DriverRegistroServiziNotFound("getInfoServizio, servizio non definito");
		}
		TipologiaServizio tipologiaServizio = servizio.getServizio().getTipologiaServizio();
		if(servizioCorrelato){
			if(!TipologiaServizio.CORRELATO.equals(tipologiaServizio)){
				throw new DriverRegistroServiziNotFound("getInfoServizio, servizio ["+idService.toString()+"] (tipologiaServizio:"+tipologiaServizio+") non e' di tipologia correlata");
			}
		}else{
			if(!TipologiaServizio.NORMALE.equals(tipologiaServizio)){
				throw new DriverRegistroServiziNotFound("getInfoServizio, servizio ["+idService.toString()+"] (tipologiaServizio:"+tipologiaServizio+") e' di tipologia normale");
			}
		}
		idService.setVersioneServizio(servizio.getVersione());

		String azione = idService.getAzione();

		if(azioneCorrelata && (azione==null) ){
			throw new DriverRegistroServiziException("getInfoServizio, azione e' obbligatoria in questa modalita' di ricerca");
		}

		String uriAccordo = servizio.getAccordoServizioParteComune();
		if(uriAccordo == null){
			throw new DriverRegistroServiziException("URIAccordo del servizio is null");
		}
		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
		org.openspcoop2.core.registry.AccordoServizioParteComune as = this.registroServizi.getAccordoServizioParteComune(connectionPdD,nomeRegistro,idAccordo);
		if (as == null){
			throw new DriverRegistroServiziNotFound("getInfoServizio, accordo di servizio ["+uriAccordo+"] non definito (o non registrato)");
		}
		infoServizio.setIdAccordo(idAccordo);
		
		org.openspcoop2.core.registry.PortType pt = null;
		// search port type
		if (servizio.getPortType()!=null){
			for(int i=0; i<as.sizePortTypeList();i++){
				if(servizio.getPortType().equals(as.getPortType(i).getNome())){
					pt = as.getPortType(i);
				}
			}
			if(pt==null){
				throw new DriverRegistroServiziPortTypeNotFound("Port-Type ["+servizio.getPortType()+"] associato al servizio non definito nell'accordo di servizio "+uriAccordo);
			}
		}
		
		org.openspcoop2.core.registry.Azione az = null;
		org.openspcoop2.core.registry.Operation ptAz = null;
		if(azione==null){
			// controllo possibilita di utilizzare il servizio senza azione
			if(pt!=null){
				// se e' definito un port-type non ha senso che si possa invocare il servizio (port-type) senza azione (operation).
				throw new DriverRegistroServiziAzioneNotFound("invocazione senza la definizione di una azione non permessa per il port-type "+pt.getNome() +" dell'accordo di servizio "+uriAccordo);
			}else{
				if(as.getUtilizzoSenzaAzione()==false){
					throw new DriverRegistroServiziAzioneNotFound("invocazione senza la definizione di una azione non permessa per l'accordo di servizio "+uriAccordo);
				}
			}
		}else{
			// Controllo esistenza azione
			boolean find = false;
			if(pt!=null){
				// search in port-type
				for(int i=0; i<pt.sizeAzioneList(); i++){
					if(azione.equals(pt.getAzione(i).getNome())){
						ptAz = pt.getAzione(i);
						find = true;
						break;
					}
				}
				if(find==false){
					throw new DriverRegistroServiziAzioneNotFound("azione ["+azione+"] non trovata nel port-type ["+pt.getNome()+"] dell'accordo di servizio "+uriAccordo);
				}
			}else{
				// search in accordo
				for(int i=0; i<as.sizeAzioneList(); i++){
					if(azione.equals(as.getAzione(i).getNome())){
						az = as.getAzione(i);
						find = true;
						break;
					}
				}
				if(find==false){
					throw new DriverRegistroServiziAzioneNotFound("azione ["+azione+"] non trovata nell'accordo di servizio "+uriAccordo);
				}
			}
		}

		// popolamento oggetto Servizio
		// 1. Accordo di Servizio
		// 2. overwrite con Port-type
		// 3. overwrite con azione dell'accordo di servizio o del port-type (se definito)
		// 4. overwrite con servizio
		// 5. overwrite con fruitore

		
		// ----------- 1. Accordo di Servizio ------------------
		infoServizio.setIDServizio(idService);

		// profilo di collaborazione (default: oneway)
		if(as.getProfiloCollaborazione() == null)
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY);
		else if(as.getProfiloCollaborazione().equals(CostantiRegistroServizi.ONEWAY))
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY); 
		else if(as.getProfiloCollaborazione().equals(CostantiRegistroServizi.SINCRONO))
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO);
		else if(as.getProfiloCollaborazione().equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO))
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		else if(as.getProfiloCollaborazione().equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
		else
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY);

		// ID-Collaborazione (default: false)
		if(as.getIdCollaborazione() == null)
			infoServizio.setCollaborazione(false);
		else if(as.getIdCollaborazione().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setCollaborazione(false);
		else if(as.getIdCollaborazione().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setCollaborazione(true);
		else
			infoServizio.setCollaborazione(false);

		// Consegna in Ordine (default: false)
		if(as.getConsegnaInOrdine() == null)
			infoServizio.setOrdineConsegna(false);
		else if(as.getConsegnaInOrdine().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setOrdineConsegna(false);
		else if(as.getConsegnaInOrdine().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setOrdineConsegna(true);
		else
			infoServizio.setOrdineConsegna(false);

		// ConfermaRicezione (default: false)
		if(as.getConfermaRicezione() == null)
			infoServizio.setConfermaRicezione(false);
		else if(as.getConfermaRicezione().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setConfermaRicezione(false);
		else if(as.getConfermaRicezione().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setConfermaRicezione(true);
		else
			infoServizio.setConfermaRicezione(false);

		// Filtro Duplicati (default: false)
		if(as.getFiltroDuplicati() == null)
			infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
		else if(as.getFiltroDuplicati().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
		else if(as.getFiltroDuplicati().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
		else
			infoServizio.setInoltro(Inoltro.CON_DUPLICATI);

		// Costruzione scadenza
		if(as.getScadenza() != null){
			try{
				long minuti = Long.parseLong(as.getScadenza());
				Date nowDate = DateManager.getDate();
				long now = nowDate.getTime();
				now = now + (minuti*60*1000);
				nowDate.setTime(now);
				infoServizio.setScadenza(nowDate);
				infoServizio.setScadenzaMinuti(minuti);
			}catch(Exception e){}
		}


		
		
		// 2. ------------------ overwrite con Port-type --------------------
		if(pt!=null){

			boolean ridefinisci = true;
			if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(pt.getProfiloPT())){
				ridefinisci = false;
			}
			
			if(ridefinisci){
			
				// Profilo di Collaborazione
				if(pt.getProfiloCollaborazione()!=null){
					if(pt.getProfiloCollaborazione().equals(CostantiRegistroServizi.ONEWAY))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY); 
					else if(pt.getProfiloCollaborazione().equals(CostantiRegistroServizi.SINCRONO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO);
					else if(pt.getProfiloCollaborazione().equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
					else if(pt.getProfiloCollaborazione().equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
				}
	
				// ID-Collaborazione (default: false)
				if(pt.getIdCollaborazione() != null){
					if(pt.getIdCollaborazione().equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setCollaborazione(false);
					else if(pt.getIdCollaborazione().equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setCollaborazione(true);
				}
	
				// Consegna in Ordine (default: false)
				if(pt.getConsegnaInOrdine() != null){
					if(pt.getConsegnaInOrdine().equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setOrdineConsegna(false);
					else if(pt.getConsegnaInOrdine().equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setOrdineConsegna(true);
				}
	
				// ConfermaRicezione (default: false)
				if(pt.getConfermaRicezione() != null){
					if(pt.getConfermaRicezione().equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setConfermaRicezione(false);
					else if(pt.getConfermaRicezione().equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setConfermaRicezione(true);
				}
	
				// Filtro Duplicati (default: false)
				if(pt.getFiltroDuplicati() != null){
					if(pt.getFiltroDuplicati().equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
					else if(pt.getFiltroDuplicati().equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
				}
	
				// Costruzione scadenza
				if(pt.getScadenza() != null){
					try{
						long minuti = Long.parseLong(pt.getScadenza());
						Date nowDate = DateManager.getDate();
						long now = nowDate.getTime();
						now = now + (minuti*60*1000);
						nowDate.setTime(now);
						infoServizio.setScadenza(nowDate);
						infoServizio.setScadenzaMinuti(minuti);
					}catch(Exception e){}
				}
				
			}
		}
		
		
		


		// ---------- 3. overwrite con azione dell'accordo di servizio o del port-type (se definito) -----------------
		if(az!=null || ptAz!=null){

			boolean ridefinisci = true;
			if(az!=null){
				if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(az.getProfAzione())){
					ridefinisci = false;
				}
			}
			else{
				if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(ptAz.getProfAzione())){
					ridefinisci = false;
				}
			}
			
			if(ridefinisci){
			
				// Profilo di Collaborazione
				ProfiloCollaborazione profilo = null;
				if(az!=null)
					profilo = az.getProfiloCollaborazione();
				else
					profilo = ptAz.getProfiloCollaborazione();
				if(profilo!=null){
					if(profilo.equals(CostantiRegistroServizi.ONEWAY))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY); 
					else if(profilo.equals(CostantiRegistroServizi.SINCRONO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO);
					else if(profilo.equals(CostantiRegistroServizi.ASINCRONO_SIMMETRICO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
					else if(profilo.equals(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO))
						infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);
				}
	
				// ID-Collaborazione (default: false)
				StatoFunzionalita idCollaborazione = null;
				if(az!=null)
					idCollaborazione = az.getIdCollaborazione();
				else
					idCollaborazione = ptAz.getIdCollaborazione();
				if(idCollaborazione != null){
					if(idCollaborazione.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setCollaborazione(false);
					else if(idCollaborazione.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setCollaborazione(true);
				}
	
				// Consegna in Ordine (default: false)
				StatoFunzionalita consegnaInOrdine = null;
				if(az!=null)
					consegnaInOrdine = az.getConsegnaInOrdine();
				else
					consegnaInOrdine = ptAz.getConsegnaInOrdine();
				if(consegnaInOrdine != null){
					if(consegnaInOrdine.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setOrdineConsegna(false);
					else if(consegnaInOrdine.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setOrdineConsegna(true);
				}
	
				// ConfermaRicezione (default: false)
				StatoFunzionalita confermaRicezione = null;
				if(az!=null)
					confermaRicezione = az.getConfermaRicezione();
				else
					confermaRicezione = ptAz.getConfermaRicezione();
				if(confermaRicezione != null){
					if(confermaRicezione.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setConfermaRicezione(false);
					else if(confermaRicezione.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setConfermaRicezione(true);
				}
	
				// Filtro Duplicati (default: false)
				StatoFunzionalita filtroDuplicati = null;
				if(az!=null)
					filtroDuplicati = az.getFiltroDuplicati();
				else
					filtroDuplicati = ptAz.getFiltroDuplicati();
				if(filtroDuplicati != null){
					if(filtroDuplicati.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
					else if(filtroDuplicati.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
				}
	
				// Costruzione scadenza
				String scadenza = null;
				if(az!=null)
					scadenza = az.getScadenza();
				else
					scadenza = ptAz.getScadenza();
				if(scadenza != null){
					try{
						long minuti = Long.parseLong(scadenza);
						Date nowDate = DateManager.getDate();
						long now = nowDate.getTime();
						now = now + (minuti*60*1000);
						nowDate.setTime(now);
						infoServizio.setScadenza(nowDate);
						infoServizio.setScadenzaMinuti(minuti);
					}catch(Exception e){}
				}
				
			}
		}




		// ------------- 4. overwrite con servizio ------------------
		// SERVIZIO (Sovrascrivo caratteristiche sia delle azioni che dell'accordo...)
		// ID-Collaborazione (default: false)
		if(servizio.getIdCollaborazione() != null){
			if(servizio.getIdCollaborazione().equals(CostantiRegistroServizi.DISABILITATO))
				infoServizio.setCollaborazione(false);
			else if(servizio.getIdCollaborazione().equals(CostantiRegistroServizi.ABILITATO))
				infoServizio.setCollaborazione(true);
		}	
		// Consegna in Ordine (default: false)
		if(servizio.getConsegnaInOrdine() != null){
			if(servizio.getConsegnaInOrdine().equals(CostantiRegistroServizi.DISABILITATO))
				infoServizio.setOrdineConsegna(false);
			else if(servizio.getConsegnaInOrdine().equals(CostantiRegistroServizi.ABILITATO))
				infoServizio.setOrdineConsegna(true);
		}	
		// ConfermaRicezione (default: false)
		if(servizio.getConfermaRicezione() != null){
			if(servizio.getConfermaRicezione().equals(CostantiRegistroServizi.DISABILITATO))
				infoServizio.setConfermaRicezione(false);
			else if(servizio.getConfermaRicezione().equals(CostantiRegistroServizi.ABILITATO))
				infoServizio.setConfermaRicezione(true);
		}	
		// Filtro Duplicati (default: false)
		if(servizio.getFiltroDuplicati() != null){
			if(servizio.getFiltroDuplicati().equals(CostantiRegistroServizi.DISABILITATO))
				infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
			else if(servizio.getFiltroDuplicati().equals(CostantiRegistroServizi.ABILITATO))
				infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
		}	
		// Costruzione scadenza
		if(servizio.getScadenza() != null){
			try{
				long minuti = Long.parseLong(servizio.getScadenza());
				Date nowDate = DateManager.getDate();
				long now = nowDate.getTime();
				now = now + (minuti*60*1000);
				nowDate.setTime(now);
				infoServizio.setScadenza(nowDate);
				infoServizio.setScadenzaMinuti(minuti);
			}catch(Exception e){}
		}




		// 5. ---------------------- overwrite con fruitore ----------------------------------
		// FRUITORE (Sovrascrivo caratteristiche sia del servizio che delle azioni che dell'accordo...)
		//Cerco il connettore nel servizio fruitore
		org.openspcoop2.core.registry.Fruitore fruitore = null;
		if(idSoggetto!=null){
			String nomeFruitore = idSoggetto.getNome();
			String tipoFruitore = idSoggetto.getTipo();
			for(int i=0; i<servizio.sizeFruitoreList(); i++){
				org.openspcoop2.core.registry.Fruitore f = servizio.getFruitore(i);
				if( (f.getTipo() != null) && 
						(f.getNome() != null) ){
					if( (f.getTipo().equals(tipoFruitore)) && 
							(f.getNome().equals(nomeFruitore)) ){
						fruitore = f;
						break;
					}
				}
			}
		}
		if(fruitore!=null){
			// ID-Collaborazione (default: false)
			if(fruitore.getIdCollaborazione() != null){
				if(fruitore.getIdCollaborazione().equals(CostantiRegistroServizi.DISABILITATO))
					infoServizio.setCollaborazione(false);
				else if(fruitore.getIdCollaborazione().equals(CostantiRegistroServizi.ABILITATO))
					infoServizio.setCollaborazione(true);
			}	
			// Consegna in Ordine (default: false)
			if(fruitore.getConsegnaInOrdine() != null){
				if(fruitore.getConsegnaInOrdine().equals(CostantiRegistroServizi.DISABILITATO))
					infoServizio.setOrdineConsegna(false);
				else if(fruitore.getConsegnaInOrdine().equals(CostantiRegistroServizi.ABILITATO))
					infoServizio.setOrdineConsegna(true);
			}	
			// ConfermaRicezione (default: false)
			if(fruitore.getConfermaRicezione() != null){
				if(fruitore.getConfermaRicezione().equals(CostantiRegistroServizi.DISABILITATO))
					infoServizio.setConfermaRicezione(false);
				else if(fruitore.getConfermaRicezione().equals(CostantiRegistroServizi.ABILITATO))
					infoServizio.setConfermaRicezione(true);
			}	
			// Filtro Duplicati (default: false)
			if(fruitore.getFiltroDuplicati() != null){
				if(fruitore.getFiltroDuplicati().equals(CostantiRegistroServizi.DISABILITATO))
					infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
				else if(fruitore.getFiltroDuplicati().equals(CostantiRegistroServizi.ABILITATO))
					infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
			}	
			// Costruzione scadenza
			if(fruitore.getScadenza() != null){
				try{
					long minuti = Long.parseLong(fruitore.getScadenza());
					Date nowDate = DateManager.getDate();
					long now = nowDate.getTime();
					now = now + (minuti*60*1000);
					nowDate.setTime(now);
					infoServizio.setScadenza(nowDate);
					infoServizio.setScadenzaMinuti(minuti);
				}catch(Exception e){}
			}
		}


		
		if(verificaEsistenzaServizioAzioneCorrelato){
			
			// Profilo Asincrono Simmetrico e Servizio Correlato da inserire nella richiesta:
			//   Si cerca un servizio correlato del soggetto fruitore (parametro idSoggetto)
			//   che contenga il riferimento all'accordo di servizio 'nomeAccordo'.
			if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
				
				// verifica
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioC = null;
				try{
					if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null){
						throw new Exception("Identita soggetto fruitore non fornita");
					}
					servizioC = this.registroServizi.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro,idSoggetto,idAccordo);
				}catch(Exception e){
					throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, servizio correlato da associare al servizio asincrono simmetrico non trovato: "+e.getMessage());
				}
				if(servizioC==null){
					throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, servizio correlato da associare al servizio asincrono simmetrico non trovato");
				}
				if(servizioC.getServizio().getNome()==null || servizioC.getServizio().getTipo()==null){
					throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, servizio correlato al servizio asincrono simmetrico non configurato correttamente (tipo e/o nome is null?)");
				}
				
				// ritorno valori per farli inserire nella richiesta
				infoServizio.setServizioCorrelato(servizioC.getServizio().getNome());
				infoServizio.setTipoServizioCorrelato(servizioC.getServizio().getTipo());
			}
		
			// Profilo Asincrono Asimmetrico
			// Il servizio correlato puo' essere un altro servizio correlato oppure un'azione correlata dello stesso servizio.
			if(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
				
				// verifica
				// 1. cerco come servizio correlato
				DriverRegistroServiziCorrelatoNotFound eFound = null;
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioC = null;
				try{
					servizioC = this.registroServizi.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro,idService.getSoggettoErogatore(),idAccordo);
				}catch(Exception e){
					eFound = new DriverRegistroServiziCorrelatoNotFound("servizio correlato da associare al servizio asincrono asimmetrico non trovato: "+e.getMessage());
				}
				if(eFound==null){
					if(servizioC==null){
						eFound = new DriverRegistroServiziCorrelatoNotFound("servizio correlato da associare al servizio asincrono asimmetrico non trovato");
					}else{
						if(servizioC.getServizio().getNome()==null || servizioC.getServizio().getTipo()==null){
							throw new DriverRegistroServiziCorrelatoNotFound("servizio correlato al servizio asincrono asimmetrico non configurato correttamente (tipo e/o nome is null?)");
						}
					}
				}
				if(eFound!=null){
					// 2. cerco come azione correlata
					
					if(az!=null){
						boolean find = false;
						for(int i=0; i<as.sizeAzioneList();i++){
							Azione azCheck = as.getAzione(i);
							if(azCheck.getNome().equals(az.getNome())){
								continue;
							}
							if(azCheck.getCorrelata()!=null && azCheck.getCorrelata().equals(az.getNome())){
								find = true;
								break;
							}
						}
						if(!find){
							throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, azione correlata o "+eFound.getMessage());
						}
					}
					else if(ptAz!=null && pt!=null){
						boolean find = false;
						for(int i=0; i<pt.sizeAzioneList();i++){
							Operation azCheck = pt.getAzione(i);
							if(azCheck.getNome().equals(ptAz.getNome())){
								continue;
							}
							if(azCheck.getCorrelata()!=null && azCheck.getCorrelata().equals(ptAz.getNome())){
								if(azCheck.getCorrelataServizio()==null || "".equals(azCheck.getCorrelataServizio()) || azCheck.getCorrelataServizio().equals(pt.getNome())){
									find = true;
									break;
								}
							} 
									
						}
						if(!find){
							throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, azione correlata o "+eFound.getMessage());
						}
					}
					else{
						throw eFound;
					}
				}
			}
		}

		
		// Controllo in caso di azione correlato su un servizio 
		// che l'azione dell'accordo sia effettivamente correlata
		if( (!servizioCorrelato) && (azioneCorrelata) && ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(infoServizio.getProfiloDiCollaborazione())){
			String azionePT = null;
			String correlazione = null;
			if(ptAz!=null){
				azionePT = ptAz.getNome();
				correlazione = ptAz.getCorrelata();
			}else{
				azionePT = az.getNome();
				correlazione = az.getCorrelata();
			}
			
			if(correlazione==null){
				throw new DriverRegistroServiziNotFound("getInfoServizio, l'azione ["+azionePT+"] invocata con il servizio ["+idService.getTipoServizio()+idService.getServizio()+"] non e' correlata ad un'altra azione dell'accordo");
			}else{
				// check azione correlata esista
				boolean find = false;
				if(pt!=null){
					for(int i=0; i<pt.sizeAzioneList();i++){
						if(correlazione.equals(pt.getAzione(i).getNome())){
							find = true;
							break;
						}
					}
				}else{
					for(int i=0; i<as.sizeAzioneList();i++){
						if(correlazione.equals(as.getAzione(i).getNome())){
							find = true;
							break;
						}
					}
				}
				
				if(!find){
					if(pt!=null){
						throw new DriverRegistroServiziNotFound("getInfoServizio, l'operation ["+correlazione+"] definita come correlata nell'operation ["+azionePT+"] non esiste ( port type["+pt.getNome()+"], servizio["+idService.getTipoServizio()+idService.getServizio()+"] accordo di servizio["+uriAccordo+"]");
					}else{
						throw new DriverRegistroServiziNotFound("getInfoServizio, l'azione ["+correlazione+"] definita come correlata nell'azione ["+azionePT+"] non esiste ( servizio["+idService.getTipoServizio()+idService.getServizio()+"] accordo di servizio["+uriAccordo+"]");
					}
				}
			}
		}
		
		return infoServizio;
	}

	
	
	
	
	protected  Allegati getAllegati(Connection connectionPdD, IDServizio idASPS)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
	
		Allegati allegati = new Allegati();
		
		AccordoServizioParteSpecifica asps = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD, null, idASPS, true);
		allegati.setAllegatiASParteSpecifica(asps.getAllegatoList());
		allegati.setSpecificheSemiformaliASParteSpecifica(asps.getSpecificaSemiformaleList());
		allegati.setSpecificheSicurezzaASParteSpecifica(asps.getSpecificaSicurezzaList());
		allegati.setSpecificheLivelloServizioASParteSpecifica(asps.getSpecificaLivelloServizioList());
		
		AccordoServizioParteComune aspc = this.registroServizi.getAccordoServizioParteComune(connectionPdD, null, this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()),true);
		allegati.setAllegatiASParteComune(aspc.getAllegatoList());
		allegati.setSpecificheSemiformaliASParteComune(aspc.getSpecificaSemiformaleList());
		
		return allegati;
		
	}
	







	/**
	 * Si occupa di ritornare le informazioni sui wsdl di un servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	protected AccordoServizioWrapper getWsdlAccordoServizio(Connection connectionPdD,IDServizio idService,InformationWsdlSource infoWsdlSource,boolean buildSchemaXSD)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		return this.registroServizi.getWsdlAccordoServizio(connectionPdD, null, idService,infoWsdlSource,buildSchemaXSD);
	}


	/**
	 * Si occupa di verificare se un fruitore e' autorizzato ad invocare un servizio
	 * 
	 * @param pdd Subject/Username della Porta di Dominio
	 * @param servizioApplicativo Servizio Applicativo che richiede il processo
	 * @param soggetto Soggetto con cui viene mappato il servizio applicativo
	 * @param servizio Servizio invocato
	 * @return true in caso di autorizzazione con successo, false altrimenti.
	 * @throws DriverRegistroServiziServizioNotFound 
	 */
	protected EsitoAutorizzazioneRegistro isFruitoreServizioAutorizzato(Connection connectionPdD,String pdd,String servizioApplicativo,IDSoggetto soggetto,IDServizio servizio)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound, DriverRegistroServiziServizioNotFound{
		try{

			EsitoAutorizzazioneRegistro esitoAutorizzazione = new EsitoAutorizzazioneRegistro();
			
			//  ricerca servizio richiesto
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioRicercato = null;
			try{
				servizioRicercato = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,null,servizio);
			}catch(DriverRegistroServiziNotFound e){}
			if(servizioRicercato == null){
				throw new DriverRegistroServiziServizioNotFound("isFruitoreServizioAutorizzato, servizio ["+servizio.toString()+"] non trovato nel registro dei servizi");
			}

			// check fruizione
			for(int i=0; i<servizioRicercato.sizeFruitoreList(); i++){
				Fruitore fruitore = servizioRicercato.getFruitore(i);
				if(fruitore.getTipo().equals(soggetto.getTipo()) &&
						fruitore.getNome().equals(soggetto.getNome())	){
					
					// fruitore realmente esistente nella lista dei fruitori del servizio
					// client-auth
					if(fruitore.getClientAuth()==null){
						// client-auth soggetto
						Soggetto fruitoreSoggetto = this.registroServizi.getSoggetto(connectionPdD,null, soggetto);
						if(fruitoreSoggetto.getPortaDominio()!=null){
							PortaDominio portaDominio = this.registroServizi.getPortaDominio(connectionPdD,null, fruitoreSoggetto.getPortaDominio());
							StatoFunzionalita authMode = portaDominio.getClientAuth();
							if(authMode==null)
								authMode = CostantiRegistroServizi.DISABILITATO;
							if(CostantiRegistroServizi.ABILITATO.equals(authMode)){
								if(pdd==null){
									String error = "subject della porta di dominio che ha inviato la busta non presente (https attivo?, client-auth attivo?)";
									this.log.error("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") fallita: "+error);
									esitoAutorizzazione.setServizioAutorizzato(false);
									esitoAutorizzazione.setDetails(error);
									return esitoAutorizzazione;
								}
								if(Utilities.sslVerify(portaDominio.getSubject(), pdd)==false){
								//if(pdd.equals(portaDominio.getSubject())==false){
									String error = "subject estratto dal certificato client ["+pdd+"] diverso da quello registrato per la porta di dominio "+portaDominio.getNome()+" del mittente ["+portaDominio.getSubject()+"]";
									this.log.error("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") fallita: "+error);
									esitoAutorizzazione.setServizioAutorizzato(false);
									esitoAutorizzazione.setDetails(error);
									return esitoAutorizzazione;
								}else{
									this.log.info("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata: subject corrispondono");
									esitoAutorizzazione.setServizioAutorizzato(true);
									return esitoAutorizzazione;
								}
							}else if(CostantiRegistroServizi.DISABILITATO.equals(authMode)){
								// filtro anti spam per default disabilitato
								this.log.debug("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata: client-auth disabilitato nella porta di dominio "+portaDominio.getNome());
								esitoAutorizzazione.setServizioAutorizzato(true);
								esitoAutorizzazione.setDetails("client-auth disabilitato nella porta di dominio "+portaDominio.getNome());
								return esitoAutorizzazione;
							}else{
								throw new Exception("Valore di client-auth presente nella porta di dominio "+portaDominio.getNome()+" non valido: "+authMode);
							}
						}else{
							// filtro anti spam per default disabilitato
							this.log.debug("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata: client-auth non presente ne nella definizione del fruitore ne nel soggetto");
							esitoAutorizzazione.setServizioAutorizzato(true);
							esitoAutorizzazione.setDetails("client-auth disabilitato");
							return esitoAutorizzazione;
						}
					}
					else if(CostantiRegistroServizi.DISABILITATO.equals(fruitore.getClientAuth()) ){
						this.log.debug("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata: client-auth disabilitato nella fruizione");
						esitoAutorizzazione.setDetails("client-auth disabilitato nella fruizione");
						esitoAutorizzazione.setServizioAutorizzato(true);
						return esitoAutorizzazione;
					}
					else if(CostantiRegistroServizi.ABILITATO.equals(fruitore.getClientAuth()) ){
						Soggetto fruitoreSoggetto = this.registroServizi.getSoggetto(connectionPdD,null, soggetto);
						PortaDominio portaDominio = this.registroServizi.getPortaDominio(connectionPdD,null, fruitoreSoggetto.getPortaDominio());
						if(pdd==null){
							String error = "subject della porta di dominio che ha inviato la busta non presente (https attivo?, client-auth attivo?)";
							this.log.error("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") fallita (FR): "+error);
							esitoAutorizzazione.setServizioAutorizzato(false);
							esitoAutorizzazione.setDetails(error);
							return esitoAutorizzazione;
						}
						if(Utilities.sslVerify(portaDominio.getSubject(), pdd)==false){
						//if(pdd.equals(portaDominio.getSubject())==false){
							String error = "subject estratto dal certificato client ["+pdd+"] diverso da quello registrato per la porta di dominio "+portaDominio.getNome()+" del mittente ["+portaDominio.getSubject()+"]";
							this.log.error("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") fallita (FR): "+error);
							esitoAutorizzazione.setServizioAutorizzato(false);
							esitoAutorizzazione.setDetails(error);
							return esitoAutorizzazione;
						}else{
							this.log.debug("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata (FR): subject corrispondono");
							esitoAutorizzazione.setServizioAutorizzato(true);
							return esitoAutorizzazione;
						}
					}
					else{
						throw new Exception("Valore di client-auth presente nel fruitore "+soggetto.toString()+" non valido: "+fruitore.getClientAuth());
					}
					
				}
			}
			esitoAutorizzazione.setServizioAutorizzato(false);
			return esitoAutorizzazione;

		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziServizioNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante il controllo della fruizione di un servizio: "+e.getMessage(),e);
		}
	}









	/* ********  C O N N E T T O R I  ******** */ 

	/**
	 * Si occupa di ritornare il connettore <var>nomeConnettore</var> associato al soggetto identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}. 
	 * Il connettore viene ricercato come definizione esterna, al soggetto (xml nel registro direttamente)
	 *
	 * @param nomeConnettore Connettore richiesto.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.config.Connettore} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	private org.openspcoop2.core.config.Connettore getConnettore(IDSoggetto idSoggetto, String nomeConnettore,String nomeRegistro) {

		org.openspcoop2.core.registry.RegistroServizi [] registri = this.registroServizi.getRegistriServiziXML();
		if(registri!=null){
			for(int l=0; l<registri.length; l++){
				for(int i=0; i< registri[l].sizeConnettoreList(); i++){
					if(nomeConnettore.equals(registri[l].getConnettore(i).getNome())){
						org.openspcoop2.core.registry.Connettore conn = registri[l].getConnettore(i);
						if(conn!=null && !CostantiConfigurazione.NONE.equals(conn.getTipo()))
							return conn.mappingIntoConnettoreConfigurazione();
						else
							return null;
					}	
				}
			}
		}
		return null;
	}

	/**
	 * Si occupa di ritornare il connettore <var>nomeConnettore</var> associato al servizio di un soggetto 
	 * identificato grazie al parametro 
	 * <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 * (se non presente viene ritornato quello del soggetto erogatore del servizio)
	 *
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @param nomeConnettore Connettore richiesto.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.config.Connettore} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	private org.openspcoop2.core.config.Connettore getConnettore(IDServizio idService, String nomeConnettore,String nomeRegistro){

		org.openspcoop2.core.registry.RegistroServizi [] registri = this.registroServizi.getRegistriServiziXML();
		if(registri!=null){
			for(int l=0; l<registri.length; l++){
				for(int i=0; i< registri[l].sizeConnettoreList(); i++){
					if(nomeConnettore.equals(registri[l].getConnettore(i).getNome())){
						org.openspcoop2.core.registry.Connettore conn = registri[l].getConnettore(i);
						if(conn!=null && !CostantiConfigurazione.NONE.equals(conn.getTipo()))
							return conn.mappingIntoConnettoreConfigurazione();
						else
							return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Dato un oggetto di tipo {@link org.openspcoop2.core.id.IDServizio}, fornito con il parametro
	 * <var>idService</var> cerca di trovare il connettore 
	 * associato al servizio, effettuando una ricerca gerarchica per:
	 * <ul>
	 * <li> Fruitore di un'Azione di un Servizio
	 * <li> Azione di un Servizio
	 * <li> Soggetto fruitore del Servizio
	 * <li> Servizio
	 * <li> Soggetto erogatore 
	 * </ul>
	 * Quindi la ricerca scorre fintanto che non viene trovato un connettore,
	 * cercandolo prima tra le informazioni associate al servizio, 
	 * fino a trovarlo obbligatoriamente nel soggetto erogatore. 
	 * Le informazioni sul connettore sono inserite in un oggetto di tipo {@link org.openspcoop2.core.config.Connettore}
	 *
	 * @param idSoggetto Soggetto richiedente del servizio
	 * @param idService Identificativo del Servizio.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return il connettore ({@link org.openspcoop2.core.config.Connettore}) associato al servizio.
	 */
	protected org.openspcoop2.core.config.Connettore getConnettore(Connection connectionPdD,IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if(idService == null)
			throw new DriverRegistroServiziException("getConnettore error: Servizio non definito");
		// ricerca servizio richiesto (Prima come servizio normale)
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		try{
			servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idService);
		}catch(DriverRegistroServiziNotFound e){}
		if(servizio == null)
			throw new DriverRegistroServiziNotFound("getConnettore, Servizio ["+idService.getTipoServizio()+idService.getServizio()+" erogato da "+idService.getSoggettoErogatore().getTipo()+idService.getSoggettoErogatore().getNome()+"] non definito nel registro");
		org.openspcoop2.core.config.Connettore connector = null;

		String azione = idService.getAzione();
		String nomeFruitore = idSoggetto.getNome();
		String tipoFruitore = idSoggetto.getTipo();

		if(azione != null){
			for(int i=0; i<servizio.getServizio().sizeParametriAzioneList();i++){
				if(azione.equals(servizio.getServizio().getParametriAzione(i).getNome())){
					org.openspcoop2.core.registry.ServizioAzione azSPC = 
						servizio.getServizio().getParametriAzione(i);

					// Cerco il connettore nel fruitore di un azione
					for(int j=0; j<azSPC.sizeParametriFruitoreList();j++){
						if( (azSPC.getParametriFruitore(i).getTipo() != null) && 
								(azSPC.getParametriFruitore(i).getNome() != null) ){
							if( (azSPC.getParametriFruitore(i).getTipo().equals(tipoFruitore)) && 
									(azSPC.getParametriFruitore(i).getNome().equals(nomeFruitore)) ){
								if(azSPC.getParametriFruitore(i).getConnettore()!=null){
									if (azSPC.getParametriFruitore(i).getConnettore().getTipo() != null)
										connector = azSPC.getParametriFruitore(i).getConnettore().mappingIntoConnettoreConfigurazione();
									else
										connector = this.getConnettore(idService,azSPC.getParametriFruitore(i).getConnettore().getNome(),nomeRegistro);
								}
								break;
							}
						}
					}
					if(connector!=null && !CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo()))
						break;

					//	Uso il connettore dell'azione
					if(azSPC.getConnettore()!=null){
						if (azSPC.getConnettore().getTipo() != null)
							connector = azSPC.getConnettore().mappingIntoConnettoreConfigurazione();
						else
							connector = getConnettore(idService,connector.getNome(),nomeRegistro);
					}
					break;
				}
			}
		}

		//Cerco il connettore nel soggetto fruitore
		if(connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())){
			for(int i=0; i<servizio.sizeFruitoreList(); i++){
				org.openspcoop2.core.registry.Fruitore f = servizio.getFruitore(i);
				if( (f.getTipo() != null) && 
						(f.getNome() != null) ){
					if( (f.getTipo().equals(tipoFruitore)) && 
							(f.getNome().equals(nomeFruitore)) ){
						if(f.getConnettore()!=null){
							if (f.getConnettore().getTipo() != null)
								connector = f.getConnettore().mappingIntoConnettoreConfigurazione();
							else
								connector = getConnettore(idService,connector.getNome(),nomeRegistro);
						}
						break;
					}
				}
			}
		}

		//Cerco il connettore nel servizio
		if (connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())) {
			if(servizio.getServizio().getConnettore()!=null){
				if (servizio.getServizio().getConnettore().getTipo() != null)
					connector = servizio.getServizio().getConnettore().mappingIntoConnettoreConfigurazione();
				else
					connector = getConnettore(idService,connector.getNome(),nomeRegistro);
			}
		}

		//Cerco il connettore nel soggetto erogatore
		if (connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())) {
			org.openspcoop2.core.registry.Soggetto soggettoErogatore = this.registroServizi.getSoggetto(connectionPdD,nomeRegistro,idService.getSoggettoErogatore());
			if(soggettoErogatore.getConnettore()!=null){
				if(soggettoErogatore.getConnettore().getTipo()!=null)
					connector = soggettoErogatore.getConnettore().mappingIntoConnettoreConfigurazione();
				else
					connector = getConnettore(idService,soggettoErogatore.getConnettore().getNome(),nomeRegistro);
			}
		}

		if (connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo()))
			throw new DriverRegistroServiziNotFound("Connettore non trovato per il Servizio ["+idService.toString()+"]");

		// imposto proprieta'
		connector.setNomeDestinatarioTrasmissioneBusta(idService.getSoggettoErogatore().getNome());
		connector.setTipoDestinatarioTrasmissioneBusta(idService.getSoggettoErogatore().getTipo());
		
		return connector;
	}


	/**
	 * Dato un oggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}, fornito con il parametro
	 * <var>idSoggetto</var> cerca di trovare il connettore 
	 * associato al soggetto.
	 * Le informazioni sul connettore sono inserite in un oggetto di tipo {@link org.openspcoop2.core.config.Connettore}
	 *
	 * @param idSoggetto Identificativo del Soggetto.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return il connettore ({@link org.openspcoop2.core.config.Connettore}) associato al servizio.
	 */
	protected org.openspcoop2.core.config.Connettore getConnettore(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if(idSoggetto == null)
			throw new DriverRegistroServiziException("getConnettore error: Soggetto destinatario non definito");

		org.openspcoop2.core.config.Connettore connector = null;

		// utilizzo quello del soggetto
		org.openspcoop2.core.registry.Soggetto soggetto = this.registroServizi.getSoggetto(connectionPdD,nomeRegistro,idSoggetto);
		if(soggetto.getConnettore()!=null){
			if(soggetto.getConnettore().getTipo()!=null)
				connector = soggetto.getConnettore().mappingIntoConnettoreConfigurazione(); // Connettore definito all'interno del servizio
			else
				connector = getConnettore(idSoggetto,soggetto.getConnettore().getNome(),nomeRegistro);
		}

		if(connector==null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo()))
			throw new DriverRegistroServiziNotFound("Connettore non trovato per il Soggetto ["+idSoggetto.toString()+"]");

		// raccolgo proprieta'
		connector.setNomeDestinatarioTrasmissioneBusta(idSoggetto.getNome());
		connector.setTipoDestinatarioTrasmissioneBusta(idSoggetto.getTipo());
		return connector;
	}
		










	/* ********  VALIDAZIONE  ******** */ 

	/**
	 * Si occupa di ritornare il dominio associato ad un soggetto  
	 * identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * Se il soggetto non e' registrato nel registro dei servizi ritorna null.
	 *
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return il dominio associato al soggetto se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	protected String getDominio(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro,IProtocolFactory protocolFactory) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if(idSoggetto == null)
			throw new DriverRegistroServiziException("getDominio error: soggetto non definito");

		org.openspcoop2.core.registry.Soggetto soggetto = this.registroServizi.getSoggetto(connectionPdD,nomeRegistro,idSoggetto);

		if(soggetto==null)
			throw new DriverRegistroServiziNotFound("getDominio, soggetto non definito nel registro");
		else{
			if(soggetto.getIdentificativoPorta() != null){
				return soggetto.getIdentificativoPorta();
			}else{
				try{
					return protocolFactory.createTraduttore().getIdentificativoPortaDefault(idSoggetto);
				}catch(Exception e){
					throw new DriverRegistroServiziException(e.getMessage(),e);
				}
			}
		}
	}
	
	/**
	 * Si occupa di ritornare l'implementazione associata ad un soggetto  
	 * identificata grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * Se il soggetto non e' registrato nel registro dei servizi ritorna l'indicazione di una gestione 'Standard'.
	 *
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return il dominio associato al soggetto se la ricerca nel registro ha successo,
	 *         'standard' altrimenti.
	 */
	protected String getImplementazionePdD(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException{

		try{
			PortaDominio pdd = this.getPortaDominio(connectionPdD, idSoggetto, nomeRegistro, "getImplementazionePdD");
			if(pdd!=null){
				if(pdd.getImplementazione()!=null)
					return pdd.getImplementazione();
				else
					return CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD;
			}else{
				return CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD;
			}
		}catch(DriverRegistroServiziNotFound e){
			return CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD;
		}

	}
	
	protected String getIdPortaDominio(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro) 
			throws DriverRegistroServiziException{

		try{
			PortaDominio pdd = this.getPortaDominio(connectionPdD, idSoggetto, nomeRegistro, "getImplementazionePdD");
			if(pdd!=null){
				return pdd.getNome();
			}else{
				return null; // significa che non è associato alcuna porta di dominio al soggetto
			}
		}catch(DriverRegistroServiziNotFound e){
			return null; // significa che non è associato alcuna porta di dominio al soggetto
		}

	}
	
	private PortaDominio getPortaDominio(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro,String nomeMetodo) 
			throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		if(idSoggetto == null)
			throw new DriverRegistroServiziException(nomeMetodo+" error: soggetto non definito");

		org.openspcoop2.core.registry.Soggetto soggetto = null;
		try{
			soggetto = this.registroServizi.getSoggetto(connectionPdD,nomeRegistro,idSoggetto);
			if(soggetto == null){
				throw new DriverRegistroServiziNotFound("Soggetto non esistente (null)");
			}
		}catch(DriverRegistroServiziNotFound e){
			this.log.debug(nomeMetodo+", soggetto ["+idSoggetto.toString()+"] non trovato: "+e.getMessage());
			throw e;
		}catch(DriverRegistroServiziException ex){
			this.log.error(nomeMetodo+", soggetto ["+idSoggetto.toString()+"] ricerca con errore: "+ex.getMessage(),ex);
			throw ex;
		}catch(Exception ex){
			this.log.error(nomeMetodo+", soggetto ["+idSoggetto.toString()+"] ricerca con errore generale: "+ex.getMessage(),ex);
			throw new DriverRegistroServiziException(nomeMetodo+", soggetto ["+idSoggetto.toString()+"] ricerca con errore generale: "+ex.getMessage(),ex);
		}
		
		
		if(soggetto.getPortaDominio()!=null){
			PortaDominio pdd = null;
			try{
				pdd = this.registroServizi.getPortaDominio(connectionPdD,nomeRegistro, soggetto.getPortaDominio());
				if(pdd == null){
					throw new DriverRegistroServiziNotFound("PdD non esistente (null)");
				}
			}catch(DriverRegistroServiziNotFound e){
				this.log.debug(nomeMetodo+", porta di domino ["+soggetto.getPortaDominio()+"] associata al soggetto ["+idSoggetto.toString()+"] non trovata: "+e.getMessage());
				throw new DriverRegistroServiziNotFound("Il soggetto ["+idSoggetto.toString()+"] è associato ad una Porta di Dominio ["+soggetto.getPortaDominio()+"] non registrata",e);
			}catch(DriverRegistroServiziException ex){
				this.log.error(nomeMetodo+", porta di domino ["+soggetto.getPortaDominio()+"] associata al soggetto ["+idSoggetto.toString()+"]  ricerca con errore: "+ex.getMessage(),ex);
				throw ex;
			}catch(Exception ex){
				this.log.error(nomeMetodo+", porta di domino ["+soggetto.getPortaDominio()+"] associata al soggetto ["+idSoggetto.toString()+"]  ricerca con errore generale: "+ex.getMessage(),ex);
				throw new DriverRegistroServiziException(nomeMetodo+", porta di domino ["+soggetto.getPortaDominio()+"] associata al soggetto ["+idSoggetto.toString()+"]  ricerca con errore generale: "+ex.getMessage(),ex);
			}
			return pdd;
		}else{
			throw new DriverRegistroServiziNotFound("Il soggetto ["+idSoggetto.toString()+"] non è associato a nessuna Porta di Dominio");
		}
	}


	/**
	 * Si occupa di ritornare il risultato della validazione di un servizio
	 *
	 * @param soggettoFruitore Fruitore del Servizio
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @param nomeRegistro nome del registro su cui effettuare la ricerca (null per effettuare una ricerca su tutti i registri)
	 * @return Risultato della Validazione
	 */
	protected RisultatoValidazione validaServizio(Connection connectionPdD,IDSoggetto soggettoFruitore,IDServizio idService,String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziPortTypeNotFound{



		// 1. Check di esistenza del servizio
		RisultatoValidazione risultato = new RisultatoValidazione();

		if(idService == null){
			risultato.setServizioRegistrato(false);
			return risultato;
		}



		// 2. Ricerca Servizio e Check di correlazione
		boolean correlato = false;
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		try{
			servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idService);
		}catch(DriverRegistroServiziNotFound e){}
		if(servizio==null){
			risultato.setServizioRegistrato(false);
			return risultato;
		}
		else{
			correlato = TipologiaServizio.CORRELATO.equals(servizio.getServizio().getTipologiaServizio());
		}
		risultato.setIsServizioCorrelato(correlato);

		
			
		
		// 3. Search accordo e portType
		String uriAccordo = servizio.getAccordoServizioParteComune();
		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
		org.openspcoop2.core.registry.AccordoServizioParteComune as = null;
		try{
			as = this.registroServizi.getAccordoServizioParteComune(connectionPdD,nomeRegistro,idAccordo);
		}catch(DriverRegistroServiziNotFound e){}
		if (as == null){
			risultato.setServizioRegistrato(false);
			return risultato;
		}
		
		org.openspcoop2.core.registry.PortType pt = null;
		// search port type
		if (servizio.getPortType()!=null){
			for(int i=0; i<as.sizePortTypeList();i++){
				if(servizio.getPortType().equals(as.getPortType(i).getNome())){
					pt = as.getPortType(i);
				}
			}
			if(pt==null){
				throw new DriverRegistroServiziPortTypeNotFound("Port-Type ["+servizio.getPortType()+"] associato al servizio non definito nell'accordo di servizio "+uriAccordo);
			}
		}
		
		
		
		// 4. Check di invocazione senza azione
		// controllo possibilita di utilizzare il servizio senza azione
		if(pt!=null){
			// se e' definito un port-type non ha senso che si possa invocare il servizio (port-type) senza azione (operation).
			risultato.setAccessoSenzaAzione(false);
		}else{
			risultato.setAccessoSenzaAzione(as.getUtilizzoSenzaAzione());
		}


		
		// 5. Ricerca nome del servizio correlato	
		if(correlato == false && soggettoFruitore!=null){
			
			// Profilo di collaborazione
			ProfiloCollaborazione profiloCollaborazione = as.getProfiloCollaborazione();
			
			if(idService.getAzione()!=null){
				if(pt!=null){
					if(pt.getProfiloCollaborazione()!=null)
						profiloCollaborazione = pt.getProfiloCollaborazione();
					for(int k=0; k<pt.sizeAzioneList(); k++){
						if(idService.getAzione().equals(pt.getAzione(k).getNome())){
							if(pt.getAzione(k).getProfiloCollaborazione()!=null){
								profiloCollaborazione = pt.getAzione(k).getProfiloCollaborazione();
							}
							break;
						}
					}
				}else{
					for(int k=0; k<as.sizeAzioneList(); k++){
						if(idService.getAzione().equals(as.getAzione(k).getNome())){
							if(as.getAzione(k).getProfiloCollaborazione()!=null){
								profiloCollaborazione = as.getAzione(k).getProfiloCollaborazione();
							}
							break;
						}
					}
				}
			}
			
			//	Profilo Asincrono Simmetrico:
			//  Si cerca un servizio correlato del soggetto fruitore
			//  che contenga il riferimento all'accordo di servizio 'nomeAccordo'.
			if(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profiloCollaborazione)){
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioC = null;
				try{
					servizioC = this.registroServizi.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro,soggettoFruitore,idAccordo);
				}catch(DriverRegistroServiziNotFound e){}
				if(servizioC!=null){
					if(servizioC.getServizio().getNome()!=null && servizioC.getServizio().getTipo()!=null){
						risultato.setTipoServizioCorrelato(servizioC.getServizio().getTipo());
						risultato.setServizioCorrelato(servizioC.getServizio().getNome());
					}
				}
			}
			// Profilo Asincrono Asimmetrico:
			// Viene prima cercata nell'accordo, se presente, un'azione correlata all'azione della richiesta.
			// Se non presente, o se l'azione della richiesta e' null allora si cerca un servizio correlato del soggetto erogatore (parametro idService.getSoggettoErogatore)
			// che contenga il riferimento all'accordo di servizio 'nomeAccordo' di questo servizio.
			else if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazione)){
				// Azione
				String azioneRichiesta = idService.getAzione();
				String azioneCorrelata = null;
				if(azioneRichiesta!=null){
					if(pt!=null){
						for(int i=0; i<pt.sizeAzioneList(); i++){
							if( azioneRichiesta.equals(pt.getAzione(i).getCorrelata()) ){
								azioneCorrelata = pt.getAzione(i).getCorrelata();
								break;
							}
						}
					}else{
						for(int i=0; i<as.sizeAzioneList(); i++){
							if( azioneRichiesta.equals(as.getAzione(i).getCorrelata()) ){
								azioneCorrelata = as.getAzione(i).getCorrelata();
								break;
							}
						}
					}
				}
				
				if(azioneCorrelata!=null){
					risultato.setTipoServizioCorrelato(idService.getTipoServizio());
					risultato.setServizioCorrelato(idService.getServizio());
					risultato.setAzioneCorrelata(azioneCorrelata);
				}else{
					org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioC = null;
					try{
						servizioC = this.registroServizi.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro,idService.getSoggettoErogatore(),idAccordo);
					}catch(DriverRegistroServiziNotFound e){}
					if(servizioC!=null){
						if(servizioC.getServizio().getNome()!=null && servizioC.getServizio().getTipo()!=null){
							risultato.setTipoServizioCorrelato(servizioC.getServizio().getTipo());
							risultato.setServizioCorrelato(servizioC.getServizio().getNome());
						}
					}
				}
			}
		}


		// 6. Azioni
		if(pt!=null){
			for(int i=0; i<pt.sizeAzioneList(); i++) {
				risultato.addAzione(pt.getAzione(i).getNome());
			}
		}else{
			for(int i=0; i<as.sizeAzioneList(); i++) {
				risultato.addAzione(as.getAzione(i).getNome());
			}
		}
		
		
		
		// 7. Tipologia di porta del soggetto fruitore
		/*
		Soggetto soggettoFruitore = null;
		try{
			soggettoFruitore = this.registroServizi.getSoggetto(nomeRegistro, soggettoFruitore);
		}catch(DriverRegistroServiziNotFound e){}
		if (soggettoFruitore == null){
			this.log.debug("validaServizio, soggetto frutore ["+soggettoFruitore.toString()+"] non definito (o non registrato)");
			risultato.setServizioRegistrato(false);
			return risultato;
		}
		if(soggettoFruitore.getPortaDominio()!=null){
			PortaDominio pdd = null;
			try{
				pdd = this.registroServizi.getPortaDominio(nomeRegistro, soggettoFruitore.getPortaDominio());
			}catch(DriverRegistroServiziNotFound e){}
			if (pdd == null){
				this.log.debug("validaServizio, porta di domino ["+soggettoFruitore.getPortaDominio()+"] associata al soggetto fruitore ["+soggettoFruitore.toString()+"] non definita (o non registrata)");
				risultato.setServizioRegistrato(false);
				return risultato;
			}
			if(pdd.getImplementazione()==null){
				risultato.setImplementazionePdDSoggettoFruitore(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			}else{
				risultato.setImplementazionePdDSoggettoFruitore(pdd.getImplementazione());
			}
		}else{
			risultato.setImplementazionePdDSoggettoFruitore(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		}
		*/
		
		
		
		// 8. Tipologia di porta del soggetto erogatore
		/*
		Soggetto soggettoErogatore = null;
		try{
			soggettoErogatore = this.registroServizi.getSoggetto(nomeRegistro, idService.getSoggettoErogatore());
		}catch(DriverRegistroServiziNotFound e){}
		if (soggettoErogatore == null){
			this.log.debug("validaServizio, soggetto erogatore ["+idService.getSoggettoErogatore().toString()+"] non definito (o non registrato)");
			risultato.setServizioRegistrato(false);
			return risultato;
		}
		if(soggettoErogatore.getPortaDominio()!=null){
			PortaDominio pdd = null;
			try{
				pdd = this.registroServizi.getPortaDominio(nomeRegistro, soggettoErogatore.getPortaDominio());
			}catch(DriverRegistroServiziNotFound e){}
			if (pdd == null){
				this.log.debug("validaServizio, porta di domino ["+soggettoErogatore.getPortaDominio()+"] associata al soggetto erogatore ["+idService.getSoggettoErogatore().toString()+"] non definita (o non registrata)");
				risultato.setServizioRegistrato(false);
				return risultato;
			}
			if(pdd.getImplementazione()==null){
				risultato.setImplementazionePdDSoggettoErogatore(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
			}else{
				risultato.setImplementazionePdDSoggettoErogatore(pdd.getImplementazione());
			}
		}else{
			risultato.setImplementazionePdDSoggettoErogatore(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
		}
		*/
		
		

		risultato.setServizioRegistrato(true);
		return risultato;
	}

	
	
	
	
	
	
	
	/* ********  R I C E R C A  E L E M E N T I   P R I M I T I V I  ******** */
	
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,IDAccordo idAccordo,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, readContenutiAllegati);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,IDServizio idServizio,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, readContenutiAllegati);
	}
	
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,IDAccordoCooperazione idAccordoCooperazione,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoCooperazione(connectionPdD, nomeRegistro, idAccordoCooperazione, readContenutiAllegati);
	}
}
