/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.registry;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziAzioneNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziCorrelatoNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziPortTypeNotFound;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziServizioNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ValidazioneSemantica;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

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
	public static void prefillCache(CryptConfig cryptConfigSoggetti) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.prefillCache(null, registroServiziReader.log, cryptConfigSoggetti);
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Prefill della cache di accesso ai registri dei servizi non riuscita: "+e.getMessage(),e);
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
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, CryptConfig cryptConfigSoggetti) throws DriverRegistroServiziException{
		try{
			RegistroServiziReader registroServiziReader = org.openspcoop2.protocol.registry.RegistroServiziReader.getInstance();
			if(registroServiziReader!=null && registroServiziReader.registroServizi!=null){
				registroServiziReader.registroServizi.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond, cryptConfigSoggetti);
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
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig cryptConfigSoggetti){

		try {
			RegistroServiziReader.registroServiziReader = 
				new RegistroServiziReader(accessoRegistro,aLog,aLogconsole,raggiungibilitaTotale,readObjectStatoBozza,
						jndiNameDatasourcePdD,useOp2UtilsDatasource,bindJMX, 
						prefillCache, cryptConfigSoggetti);	
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

	public static java.util.Hashtable<String, IDriverRegistroServiziGet> getDriverRegistroServizi() {
		return RegistroServiziReader.registroServiziReader.registroServizi.getDriverRegistroServizi();
	}










	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Inizializza il reader
	 *
	 * @param accessoRegistro Informazioni per accedere al registro Servizi.
	 */
	public RegistroServiziReader(AccessoRegistro accessoRegistro,Logger aLog,
			Logger aLogconsole,boolean raggiungibilitaTotale, boolean readObjectStatoBozza, 
			String jndiNameDatasourcePdD, boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig cryptConfigSoggetti)throws DriverRegistroServiziException{
		try{
			if(aLog!=null)
				this.log = aLog;
			else
				this.log = LoggerWrapperFactory.getLogger(RegistroServiziReader.class);
			this.registroServizi = new RegistroServizi(accessoRegistro,this.log,aLogconsole,raggiungibilitaTotale,readObjectStatoBozza,
					jndiNameDatasourcePdD, useOp2UtilsDatasource, bindJMX, 
					prefillCache, cryptConfigSoggetti);
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
			StringBuilder eccezioni = new StringBuilder();
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
			String[] tipiSoggettiValidi,String [] tipiServiziSoapValidi, String [] tipiServiziRestValidi, String[] tipiConnettoriValidi,
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
								tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziSoapValidi,tipiServiziRestValidi);
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
			StringBuilder eccezioni = new StringBuilder();
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
								tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziSoapValidi,tipiServiziRestValidi);
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
			String[] tipiSoggettiValidi,String [] tipiServiziSoapValidi, String [] tipiServiziRestValidi, String[] tipiConnettoriValidi) throws CoreException{
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			try{
				Object o = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
				if(o instanceof DriverRegistroServiziXML){
					DriverRegistroServiziXML driver = (DriverRegistroServiziXML) o;
					driver.abilitazioneValidazioneSemanticaDuranteModificaXML(verificaURI, 
							tipiConnettoriValidi,tipiSoggettiValidi,tipiServiziSoapValidi,tipiServiziRestValidi);
				}
			}catch(Exception e){
				throw new CoreException("[Registro "+nomeRegInLista+"] "+e.getMessage(),e);
			}
		}
	}
	
	
	protected void verificaConsistenzaRegistroServizi() throws DriverRegistroServiziException {
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			Object o = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			if(o instanceof DriverRegistroServiziXML){
				DriverRegistroServiziXML driver = (DriverRegistroServiziXML) o;
				driver.refreshRegistroServiziXML();
			}	
		}
	}
	
	
	
	/* ********  G E T   O G G E T T I   PRIMITIVI   NO CACHE ******** */ 
	
	public PortaDominio getPortaDominio_noCache(String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		PortaDominio r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getPortaDominio(nome);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public Ruolo getRuolo_noCache(String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Ruolo r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				IDRuolo idRuolo = new IDRuolo(nome);
				r = driver.getRuolo(idRuolo);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public Soggetto getSoggetto_noCache(IDSoggetto idSoggetto,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Soggetto r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getSoggetto(idSoggetto);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune_noCache(IDAccordo idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		AccordoServizioParteComune r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				if(driver instanceof DriverRegistroServiziDB){
					r = ((DriverRegistroServiziDB)driver).getAccordoServizioParteComune(idAccordo,readContenutiAllegati);
				}
				else{
					r = driver.getAccordoServizioParteComune(idAccordo);
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_noCache(IDServizio idServizio,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		AccordoServizioParteSpecifica r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				if(driver instanceof DriverRegistroServiziDB){
					r = ((DriverRegistroServiziDB)driver).getAccordoServizioParteSpecifica(idServizio,readContenutiAllegati);
				}
				else{
					r = driver.getAccordoServizioParteSpecifica(idServizio);
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public AccordoCooperazione getAccordoCooperazione_noCache(IDAccordoCooperazione idAccordo,String nomeRegistro,Boolean readContenutiAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		AccordoCooperazione r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				if(driver instanceof DriverRegistroServiziDB){
					r = ((DriverRegistroServiziDB)driver).getAccordoCooperazione(idAccordo,readContenutiAllegati);
				}
				else{
					r = driver.getAccordoCooperazione(idAccordo);
				}
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	
	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  NO CACHE  ******** */
	
	public List<String> getAllIdPorteDominio_noCache(FiltroRicerca filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<String> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdPorteDominio(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDRuolo> getAllIdRuoli_noCache(FiltroRicercaRuoli filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDRuolo> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdRuoli(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDSoggetto> getAllIdSoggetti_noCache(FiltroRicercaSoggetti filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDSoggetto> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdSoggetti(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione_noCache(FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDAccordoCooperazione> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdAccordiCooperazione(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDAccordo> getAllIdAccordiServizioParteComune_noCache(FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDAccordo> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdAccordiServizioParteComune(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDPortType> getAllIdPortType_noCache(FiltroRicercaPortTypes filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		List<IDPortType> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdPortType(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDPortTypeAzione> getAllIdAzionePortType_noCache(FiltroRicercaOperations filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		List<IDPortTypeAzione> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdAzionePortType(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDAccordoAzione> getAllIdAzioneAccordo_noCache(FiltroRicercaAzioni filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		List<IDAccordoAzione> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdAzioneAccordo(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDServizio> getAllIdServizi_noCache(FiltroRicercaServizi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDServizio> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdServizi(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
	}
	
	public List<IDFruizione> getAllIdFruizioniServizio_noCache(FiltroRicercaFruizioniServizio filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		List<IDFruizione> r = null;
		for (Enumeration<?> en = this.registroServizi.getDriverRegistroServizi().keys() ; en.hasMoreElements() ;) {
			String nomeRegInLista= (String) en.nextElement();
			if(nomeRegistro!=null && !nomeRegistro.equals(nomeRegInLista)){
				continue;
			}
			IDriverRegistroServiziGet driver = this.registroServizi.getDriverRegistroServizi().get(nomeRegInLista);
			try{
				r = driver.getAllIdFruizioniServizio(filtroRicerca);
			}catch(DriverRegistroServiziNotFound notFound){}
			if(r!=null || nomeRegistro!=null){
				break;
			}
		}
		if(r==null){
			throw new DriverRegistroServiziNotFound();
		}
		return r;
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
		
		// vedo se il soggetto fruitore ha un profilo
		Soggetto soggettoFruitore =  this.registroServizi.getSoggetto(connectionPdD,nomeRegistro, idFruitore);
		if (soggettoFruitore == null){
			throw new DriverRegistroServiziNotFound("getProfiloGestioneErogazioneServizio, soggettoFruitore ["+idFruitore+"] non definito (o non registrato)");
		}
		profilo = soggettoFruitore.getVersioneProtocollo();
		
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
	protected Servizio getInfoServizio(Connection connectionPdD,IDSoggetto idSoggetto, IDServizio idService,String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato, boolean throwAzioneNotFound)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,false,false,idSoggetto,idService,nomeRegistro,verificaEsistenzaServizioAzioneCorrelato, throwAzioneNotFound);
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
	protected Servizio getInfoServizioCorrelato(Connection connectionPdD,IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound) 
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,true,false,idSoggetto,idService,nomeRegistro,false, throwAzioneNotFound);
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
	protected Servizio getInfoServizioAzioneCorrelata(Connection connectionPdD,IDSoggetto idSoggetto,IDServizio idService,String nomeRegistro, boolean throwAzioneNotFound) 
		throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{
		return getInfoServizio(connectionPdD,false,true,idSoggetto,idService,nomeRegistro,false, throwAzioneNotFound);
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
	private Servizio getInfoServizio(Connection connectionPdD,boolean servizioCorrelato,boolean azioneCorrelata,IDSoggetto idSoggetto, IDServizio idService,
			String nomeRegistro, boolean verificaEsistenzaServizioAzioneCorrelato, boolean throwAzioneNotFound)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound,DriverRegistroServiziAzioneNotFound,DriverRegistroServiziPortTypeNotFound,DriverRegistroServiziCorrelatoNotFound{

		Servizio infoServizio = new Servizio();

		// ricerca servizio richiesto
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio = null;
		servizio = this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD,nomeRegistro,idService);
		if(servizio == null){
			throw new DriverRegistroServiziNotFound("Servizio non definito");
		}
		TipologiaServizio tipologiaServizio = servizio.getTipologiaServizio();
		if(servizioCorrelato){
			if(!TipologiaServizio.CORRELATO.equals(tipologiaServizio)){
				throw new DriverRegistroServiziNotFound("Servizio ["+idService.toString()+"] (tipologia:"+tipologiaServizio+") non è di tipologia correlata");
			}
		}else{
			if(!TipologiaServizio.NORMALE.equals(tipologiaServizio)){
				throw new DriverRegistroServiziNotFound("Servizio ["+idService.toString()+"] (tipologia:"+tipologiaServizio+") è di tipologia normale");
			}
		}

		String azione = idService.getAzione();

		if(azioneCorrelata && (azione==null) ){
			throw new DriverRegistroServiziException("Azione obbligatoria in questa modalità di ricerca");
		}

		String uriAccordo = servizio.getAccordoServizioParteComune();
		if(uriAccordo == null){
			throw new DriverRegistroServiziException("Identificativo dell'API non definito nel servizio");
		}
		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
		org.openspcoop2.core.registry.AccordoServizioParteComune as = this.registroServizi.getAccordoServizioParteComune(connectionPdD,nomeRegistro,idAccordo);
		if (as == null){
			throw new DriverRegistroServiziNotFound("API '"+uriAccordo+"' non trovata");
		}
		infoServizio.setIdAccordo(idAccordo);
		ServiceBinding serviceBinding = as.getServiceBinding();
		infoServizio.setServiceBinding(RegistroServiziUtils.convertToMessage(serviceBinding));
		
		infoServizio.setIDServizio(idService);
		
		if(ServiceBinding.SOAP.equals(serviceBinding)) {
			return this._getInfoServizioSOAP(idService, servizio, 
					idAccordo, as, uriAccordo, 
					azione, infoServizio, idSoggetto, 
					verificaEsistenzaServizioAzioneCorrelato, servizioCorrelato, azioneCorrelata, nomeRegistro, connectionPdD, throwAzioneNotFound);
		}
		else {
			return this._getInfoServizioREST(as, uriAccordo, azione, infoServizio, throwAzioneNotFound);
		}
		
	}
	
	private Servizio _getInfoServizioREST(org.openspcoop2.core.registry.AccordoServizioParteComune as, String uriAccordo,
			String azione, Servizio infoServizio, boolean throwAzioneNotFound) throws DriverRegistroServiziAzioneNotFound {
		
		org.openspcoop2.core.registry.Resource resource = null;
		if(azione==null){
			if(throwAzioneNotFound) {
				throw new DriverRegistroServiziAzioneNotFound("La richiesta effettuata non è associabile a nessuna risorsa definita nell'API "+uriAccordo);
			}
		}else{
			// Controllo esistenza azione
			boolean find = false;
			// search in accordo
			for(int i=0; i<as.sizeResourceList(); i++){
				if(azione.equals(as.getResource(i).getNome())){
					resource = as.getResource(i);
					find = true;
					break;
				}
			}
			if(find==false){
				if(throwAzioneNotFound) {
					throw new DriverRegistroServiziAzioneNotFound("Risorsa '"+azione+"' non trovata nell'API "+uriAccordo);
				}
			}
		}
		
		
		infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
		infoServizio.setCollaborazione(false);
		infoServizio.setConfermaRicezione(false);
		infoServizio.setOrdineConsegna(false);
		infoServizio.setCorrelato(false);
		
		

		
		// ----------- 1. Accordo di Servizio ------------------

		// profilo di collaborazione (default: oneway)
		if(as.getProfiloCollaborazione()!=null) {
			if(org.openspcoop2.core.registry.constants.ProfiloCollaborazione.ONEWAY.equals(as.getProfiloCollaborazione())) {
				infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.ONEWAY);
			}
			else {
				infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO);
			}
		}else {
			infoServizio.setProfiloDiCollaborazione(ProfiloDiCollaborazione.SINCRONO);
		}

		// ID-Collaborazione (default: false)
		if(as.getIdCollaborazione() == null)
			infoServizio.setCollaborazione(false);
		else if(as.getIdCollaborazione().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setCollaborazione(false);
		else if(as.getIdCollaborazione().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setCollaborazione(true);
		else
			infoServizio.setCollaborazione(false);

		// ID-RiferimentoRichiesta (default: false)
		if(as.getIdRiferimentoRichiesta() == null)
			infoServizio.setIdRiferimentoRichiesta(false);
		else if(as.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setIdRiferimentoRichiesta(false);
		else if(as.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setIdRiferimentoRichiesta(true);
		else
			infoServizio.setIdRiferimentoRichiesta(false);
		
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


		
	


		// ---------- 2. overwrite con risorsa dell'accordo di servizio o del port-type (se definito) -----------------
		if(resource!=null){

			boolean ridefinisci = true;
			if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(resource.getProfAzione())){
				ridefinisci = false;
			}
						
			if(ridefinisci){
	
				// ID-Collaborazione (default: false)
				StatoFunzionalita idCollaborazione = resource.getIdCollaborazione();
				if(idCollaborazione != null){
					if(idCollaborazione.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setCollaborazione(false);
					else if(idCollaborazione.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setCollaborazione(true);
				}
				
				// ID-RiferimentoRichiesta (default: false)
				StatoFunzionalita idRiferimentoRichiesta = resource.getIdRiferimentoRichiesta();
				if(idRiferimentoRichiesta != null){
					if(idRiferimentoRichiesta.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setIdRiferimentoRichiesta(false);
					else if(idRiferimentoRichiesta.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setIdRiferimentoRichiesta(true);
				}
	
				// Consegna in Ordine (default: false)
				StatoFunzionalita consegnaInOrdine = resource.getConsegnaInOrdine();
				if(consegnaInOrdine != null){
					if(consegnaInOrdine.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setOrdineConsegna(false);
					else if(consegnaInOrdine.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setOrdineConsegna(true);
				}
	
				// ConfermaRicezione (default: false)
				StatoFunzionalita confermaRicezione = resource.getConfermaRicezione();
				if(confermaRicezione != null){
					if(confermaRicezione.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setConfermaRicezione(false);
					else if(confermaRicezione.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setConfermaRicezione(true);
				}
	
				// Filtro Duplicati (default: false)
				StatoFunzionalita filtroDuplicati = resource.getFiltroDuplicati();
				if(filtroDuplicati != null){
					if(filtroDuplicati.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setInoltro(Inoltro.CON_DUPLICATI);
					else if(filtroDuplicati.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setInoltro(Inoltro.SENZA_DUPLICATI);
				}
	
				// Costruzione scadenza
				String scadenza = resource.getScadenza();
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

			
		return infoServizio;
	}
	
	private Servizio _getInfoServizioSOAP(IDServizio idService, org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizio,
			IDAccordo idAccordo, org.openspcoop2.core.registry.AccordoServizioParteComune as, String uriAccordo,
			String azione, Servizio infoServizio, IDSoggetto idSoggetto,
			boolean verificaEsistenzaServizioAzioneCorrelato, boolean servizioCorrelato,boolean azioneCorrelata, String nomeRegistro, 
			Connection connectionPdD, boolean throwAzioneNotFound) throws DriverRegistroServiziPortTypeNotFound, DriverRegistroServiziAzioneNotFound, DriverRegistroServiziCorrelatoNotFound, DriverRegistroServiziNotFound {
		
		org.openspcoop2.core.registry.PortType pt = null;
		// search port type
		if (servizio.getPortType()!=null){
			for(int i=0; i<as.sizePortTypeList();i++){
				if(servizio.getPortType().equals(as.getPortType(i).getNome())){
					pt = as.getPortType(i);
				}
			}
			if(pt==null){
				throw new DriverRegistroServiziPortTypeNotFound("Servizio '"+servizio.getPortType()+"' non definito nell'API "+uriAccordo);
			}
		}
		
		org.openspcoop2.core.registry.Azione az = null;
		org.openspcoop2.core.registry.Operation ptAz = null;
		if(azione==null){
			// controllo possibilita di utilizzare il servizio senza azione
			if(pt!=null){
				// se e' definito un port-type non ha senso che si possa invocare il servizio (port-type) senza azione (operation).
				if(throwAzioneNotFound) {
					throw new DriverRegistroServiziAzioneNotFound("La richiesta effettuata non è associabile a nessuna azione (servizio: "+pt.getNome() +") dell'API "+uriAccordo);
				}
			}else{
				if(as.getUtilizzoSenzaAzione()==false){
					if(throwAzioneNotFound) {
						throw new DriverRegistroServiziAzioneNotFound("La richiesta effettuata non è associabile a nessuna azione dell'API "+uriAccordo+ " (invocazione senza la definizione di una azione non permessa)");
					}
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
					if(throwAzioneNotFound) {
						throw new DriverRegistroServiziAzioneNotFound("Azione '"+azione+"' non trovata per il servizio ["+pt.getNome()+"] dell'API "+uriAccordo);
					}
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
					if(throwAzioneNotFound) {
						throw new DriverRegistroServiziAzioneNotFound("Azione '"+azione+"' non trovata nell'API "+uriAccordo);
					}
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

		// ID-RiferimentoRichiesta (default: false)
		if(as.getIdRiferimentoRichiesta() == null)
			infoServizio.setIdRiferimentoRichiesta(false);
		else if(as.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.DISABILITATO))
			infoServizio.setIdRiferimentoRichiesta(false);
		else if(as.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.ABILITATO))
			infoServizio.setIdRiferimentoRichiesta(true);
		else
			infoServizio.setIdRiferimentoRichiesta(false);
		
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
				
				// ID-RiferimentoRichiesta (default: false)
				if(pt.getIdRiferimentoRichiesta() != null){
					if(pt.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setIdRiferimentoRichiesta(false);
					else if(pt.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setIdRiferimentoRichiesta(true);
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
				
				// ID-RiferimentoRichiesta (default: false)
				StatoFunzionalita idRiferimentoRichiesta = null;
				if(az!=null)
					idRiferimentoRichiesta = az.getIdRiferimentoRichiesta();
				else
					idRiferimentoRichiesta = ptAz.getIdRiferimentoRichiesta();
				if(idRiferimentoRichiesta != null){
					if(idRiferimentoRichiesta.equals(CostantiRegistroServizi.DISABILITATO))
						infoServizio.setIdRiferimentoRichiesta(false);
					else if(idRiferimentoRichiesta.equals(CostantiRegistroServizi.ABILITATO))
						infoServizio.setIdRiferimentoRichiesta(true);
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
		// ID-RiferimentoRichiesta (default: false)
		if(servizio.getIdRiferimentoRichiesta() != null){
			if(servizio.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.DISABILITATO))
				infoServizio.setIdRiferimentoRichiesta(false);
			else if(servizio.getIdRiferimentoRichiesta().equals(CostantiRegistroServizi.ABILITATO))
				infoServizio.setIdRiferimentoRichiesta(true);
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
				if(servizioC.getNome()==null || servizioC.getTipo()==null || servizioC.getVersione()==null){
					throw new DriverRegistroServiziCorrelatoNotFound("getInfoServizio, servizio correlato al servizio asincrono simmetrico non configurato correttamente (tipo e/o nome is null?)");
				}
				
				// ritorno valori per farli inserire nella richiesta
				infoServizio.setServizioCorrelato(servizioC.getNome());
				infoServizio.setTipoServizioCorrelato(servizioC.getTipo());
				infoServizio.setVersioneServizioCorrelato(servizioC.getVersione());
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
						if(servizioC.getNome()==null || servizioC.getTipo()==null || servizioC.getVersione()==null){
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
			
			String uriServizio = null;
			try{
				uriServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idService);
			}catch(Exception e){
				uriServizio = idService.toString(false);
			}
			
			if(correlazione==null){
				throw new DriverRegistroServiziNotFound("getInfoServizio, l'azione ["+azionePT+"] invocata con il servizio ["+uriServizio+"] non e' correlata ad un'altra azione dell'accordo");
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
						throw new DriverRegistroServiziNotFound("getInfoServizio, l'operation ["+correlazione+"] definita come correlata nell'operation ["+azionePT+"] non esiste ( port type["+pt.getNome()+"], servizio["+uriServizio+"] accordo di servizio["+uriAccordo+"]");
					}else{
						throw new DriverRegistroServiziNotFound("getInfoServizio, l'azione ["+correlazione+"] definita come correlata nell'azione ["+azionePT+"] non esiste ( servizio["+uriServizio+"] accordo di servizio["+uriAccordo+"]");
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
	protected org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper getWsdlAccordoServizio(Connection connectionPdD,IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		return this.registroServizi.getWsdlAccordoServizio(connectionPdD, null, idService,infoWsdlSource,buildSchemaXSD);
	}
	
	/**
	 * Si occupa di ritornare le informazioni REST di un servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	protected org.openspcoop2.core.registry.rest.AccordoServizioWrapper getRestAccordoServizio(Connection connectionPdD,IDServizio idService,InformationApiSource infoWsdlSource,boolean buildSchemaXSD, boolean processIncludeForOpenApi)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		return this.registroServizi.getRestAccordoServizio(connectionPdD, null, idService,infoWsdlSource,buildSchemaXSD, processIncludeForOpenApi);
	}
	
	/**
	 * Si occupa di ritornare il tipo di service binding del servizio
	 * 
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.constants.ServiceBinding} se la ricerca nel registro ha successo,
	 *         null altrimenti.
	 */
	protected org.openspcoop2.core.registry.constants.ServiceBinding getServiceBinding(Connection connectionPdD,IDServizio idService)
			throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		return this.registroServizi.getServiceBinding(connectionPdD, null, idService);
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
			
			if(soggetto==null){
				String error = "identita del chiamante non fornita (autenticazione non attiva?)";
				this.log.error("Identita del chiamante non fornita");
				esitoAutorizzazione.setServizioAutorizzato(false);
				esitoAutorizzazione.setDetails(error);
				return esitoAutorizzazione;
			}
			
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
					if(CertificateUtils.sslVerify(portaDominio.getSubject(), pdd, PrincipalType.subject, this.log)==false){
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
				this.log.debug("Autorizzazione ("+soggetto.toString()+" -> "+servizio.toString()+") effettuata: client-auth non effettuata; il soggetto fruitore non è associato ad una porta di dominio");
				esitoAutorizzazione.setServizioAutorizzato(true);
				esitoAutorizzazione.setDetails("client-auth disabilitato");
				return esitoAutorizzazione;
			}

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
		String uriServizio = null;
		try{
			uriServizio = IDServizioFactory.getInstance().getUriFromIDServizio(idService);
		}catch(Exception e){
			uriServizio = idService.toString(false);
		}
		if(servizio == null)
			throw new DriverRegistroServiziNotFound("getConnettore, Servizio ["+uriServizio+"] non definito nel registro");
		org.openspcoop2.core.config.Connettore connector = null;

		String azione = idService.getAzione();
		String nomeFruitore = idSoggetto.getNome();
		String tipoFruitore = idSoggetto.getTipo();

		//Cerco il connettore nel soggetto fruitore (e nelle azioni)
		if(connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())){
			for(int i=0; i<servizio.sizeFruitoreList(); i++){
				org.openspcoop2.core.registry.Fruitore f = servizio.getFruitore(i);
				if( (f.getTipo() != null) && 
						(f.getNome() != null) ){
					if( (f.getTipo().equals(tipoFruitore)) && 
							(f.getNome().equals(nomeFruitore)) ){
						
						for(int j=0; j<f.sizeConfigurazioneAzioneList();j++){
							boolean findAzione = false;
							ConfigurazioneServizioAzione conf = f.getConfigurazioneAzione(j);
							if(conf!=null && conf.sizeAzioneList()>0) {
								for (String azioneCheck : conf.getAzioneList()) {
									if(azione.equals(azioneCheck)){
										findAzione = true;
									}
								}
							}
							if(findAzione){
								//	Uso il connettore dell'azione del fruitore
								if(conf.getConnettore()!=null){
									if (conf.getConnettore().getTipo() != null)
										connector = conf.getConnettore().mappingIntoConnettoreConfigurazione();
									else
										connector = getConnettore(idService,conf.getConnettore().getNome(),nomeRegistro);
								}
								break;
							}
						}
						
						if(connector!=null && !CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo()))
							break;
						
						if(f.getConnettore()!=null){
							if (f.getConnettore().getTipo() != null)
								connector = f.getConnettore().mappingIntoConnettoreConfigurazione();
							else
								connector = getConnettore(idService,f.getConnettore().getNome(),nomeRegistro);
						}
						break;
					}
				}
			}
		}

		//Cerco il connettore nell'azione del servizio
		if(connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())){
			if(azione != null){
				if(servizio.getConfigurazioneServizio()!=null){
					for(int i=0; i<servizio.getConfigurazioneServizio().sizeConfigurazioneAzioneList();i++){
						boolean findAzione = false;
						ConfigurazioneServizioAzione conf = servizio.getConfigurazioneServizio().getConfigurazioneAzione(i);
						if(conf!=null && conf.sizeAzioneList()>0) {
							for (String azioneCheck : conf.getAzioneList()) {
								if(azione.equals(azioneCheck)){
									findAzione = true;
								}
							}
						}
						if(findAzione){
							//	Uso il connettore dell'azione
							if(conf.getConnettore()!=null){
								if (conf.getConnettore().getTipo() != null)
									connector = conf.getConnettore().mappingIntoConnettoreConfigurazione();
								else
									connector = getConnettore(idService,conf.getConnettore().getNome(),nomeRegistro);
							}
							break;
						}
					}
				}
			}
		}
		
		//Cerco il connettore nel servizio
		if (connector == null || CostantiRegistroServizi.DISABILITATO.equals(connector.getTipo())) {
			if(servizio.getConfigurazioneServizio()!=null && servizio.getConfigurazioneServizio().getConnettore()!=null){
				if (servizio.getConfigurazioneServizio().getConnettore().getTipo() != null)
					connector = servizio.getConfigurazioneServizio().getConnettore().mappingIntoConnettoreConfigurazione();
				else
					connector = getConnettore(idService,servizio.getConfigurazioneServizio().getConnettore().getNome(),nomeRegistro);
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
	protected String getDominio(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro,IProtocolFactory<?> protocolFactory) 
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
			correlato = TipologiaServizio.CORRELATO.equals(servizio.getTipologiaServizio());
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
		ServiceBinding serviceBinding = as.getServiceBinding();
		
		if(ServiceBinding.REST.equals(serviceBinding)) {
		
			risultato.setAccessoSenzaAzione(false);
			
			for(int i=0; i<as.sizeResourceList(); i++) {
				risultato.addAzione(as.getResource(i).getNome());
			}
			
			risultato.setServizioRegistrato(true);
			return risultato;
			
		}
		else {
		
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
						if(servizioC.getNome()!=null && servizioC.getTipo()!=null && servizioC.getVersione()!=null){
							risultato.setTipoServizioCorrelato(servizioC.getTipo());
							risultato.setServizioCorrelato(servizioC.getNome());
							risultato.setVersioneServizioCorrelato(servizioC.getVersione());
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
						risultato.setTipoServizioCorrelato(idService.getTipo());
						risultato.setServizioCorrelato(idService.getNome());
						risultato.setVersioneServizioCorrelato(idService.getVersione());
						risultato.setAzioneCorrelata(azioneCorrelata);
					}else{
						org.openspcoop2.core.registry.AccordoServizioParteSpecifica servizioC = null;
						try{
							servizioC = this.registroServizi.getAccordoServizioParteSpecifica_ServizioCorrelato(connectionPdD,nomeRegistro,idService.getSoggettoErogatore(),idAccordo);
						}catch(DriverRegistroServiziNotFound e){}
						if(servizioC!=null){
							if(servizioC.getNome()!=null && servizioC.getTipo()!=null && servizioC.getVersione()!=null){
								risultato.setTipoServizioCorrelato(servizioC.getTipo());
								risultato.setServizioCorrelato(servizioC.getNome());
								risultato.setVersioneServizioCorrelato(servizioC.getVersione());
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
			
		}
		
		

		risultato.setServizioRegistrato(true);
		return risultato;
	}

	
	
	
	
	
	
	/* ********  A U T E N T I C A Z I O N E   S O G G E T T I  ******** */ 
	
	public Soggetto getSoggettoByCredenzialiBasic(Connection connectionPdD,String username, String password, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggettoByCredenzialiBasic(connectionPdD, nomeRegistro, username, password, cryptConfig);
	}
	
	public Soggetto getSoggettoByCredenzialiApiKey(Connection connectionPdD,String username, String password, boolean appId, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggettoByCredenzialiApiKey(connectionPdD, nomeRegistro, username, password, appId, cryptConfig);
	}
	
	public Soggetto getSoggettoByCredenzialiSsl(Connection connectionPdD,String subject, String issuer, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, subject, issuer);
	}
	
	public Soggetto getSoggettoByCredenzialiSsl(Connection connectionPdD,CertificateInfo certificate, boolean strictVerifier, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, certificate, strictVerifier);
	}
	
	public Soggetto getSoggettoByCredenzialiPrincipal(Connection connectionPdD,String principal, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggettoByCredenzialiPrincipal(connectionPdD, nomeRegistro, principal);
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiBasic(Connection connectionPdD,String username, String password, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return convertToId(this.registroServizi.getSoggettoByCredenzialiBasic(connectionPdD, nomeRegistro, username, password, cryptConfig));
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiApiKey(Connection connectionPdD,String username, String password,  boolean appId, CryptConfig cryptConfig, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return convertToId(this.registroServizi.getSoggettoByCredenzialiApiKey(connectionPdD, nomeRegistro, username, password, appId, cryptConfig));
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiSsl(Connection connectionPdD,String subject, String issuer, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return convertToId(this.registroServizi.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, subject, issuer));
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiSsl(Connection connectionPdD,CertificateInfo certificate, boolean strictVerifier, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return convertToId(this.registroServizi.getSoggettoByCredenzialiSsl(connectionPdD, nomeRegistro, certificate, strictVerifier));
	}
	
	public IDSoggetto getIdSoggettoByCredenzialiPrincipal(Connection connectionPdD,String principal, String nomeRegistro)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return convertToId(this.registroServizi.getSoggettoByCredenzialiPrincipal(connectionPdD, nomeRegistro, principal));
	}
		
	private IDSoggetto convertToId(Soggetto s){
		IDSoggetto id = new IDSoggetto(s.getTipo(), s.getNome(), s.getIdentificativoPorta());
		return id;
	}
	
	
	
	
	
	
	
	
	/* ********  R I C E R C A  E L E M E N T I   P R I M I T I V I  ******** */
	
	public PortaDominio getPortaDominio(Connection connectionPdD,String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getPortaDominio(connectionPdD, nomeRegistro, nome);
	}
	
	public Ruolo getRuolo(Connection connectionPdD,String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getRuolo(connectionPdD, nomeRegistro, nome);
	}
	
	public Scope getScope(Connection connectionPdD,String nome,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getScope(connectionPdD, nomeRegistro, nome);
	}

	public Soggetto getSoggetto(Connection connectionPdD,IDSoggetto idSoggetto,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getSoggetto(connectionPdD, nomeRegistro, idSoggetto);
	}
	
	public AccordoServizioParteComune getAccordoServizioParteComune(Connection connectionPdD,IDAccordo idAccordo,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoServizioParteComune(connectionPdD, nomeRegistro, idAccordo, readContenutiAllegati);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(Connection connectionPdD,IDServizio idServizio,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoServizioParteSpecifica(connectionPdD, nomeRegistro, idServizio, readContenutiAllegati);
	}
	
	public AccordoCooperazione getAccordoCooperazione(Connection connectionPdD,IDAccordoCooperazione idAccordoCooperazione,Boolean readContenutiAllegati,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAccordoCooperazione(connectionPdD, nomeRegistro, idAccordoCooperazione, readContenutiAllegati);
	}

	
	
	
	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */
	
	public List<String> getAllIdPorteDominio(Connection connectionPdD,FiltroRicerca filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdPorteDominio(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDRuolo> getAllIdRuoli(Connection connectionPdD,FiltroRicercaRuoli filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdRuoli(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDScope> getAllIdScope(Connection connectionPdD,FiltroRicercaScope filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdScope(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDSoggetto> getAllIdSoggetti(Connection connectionPdD,FiltroRicercaSoggetti filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdSoggetti(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(Connection connectionPdD,FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdAccordiCooperazione(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDAccordo> getAllIdAccordiServizioParteComune(Connection connectionPdD,FiltroRicercaAccordi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdAccordiServizioParteComune(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDPortType> getAllIdPortType(Connection connectionPdD,FiltroRicercaPortTypes filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdPortType(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDPortTypeAzione> getAllIdAzionePortType(Connection connectionPdD,FiltroRicercaOperations filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdAzionePortType(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDAccordoAzione> getAllIdAzioneAccordo(Connection connectionPdD,FiltroRicercaAzioni filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdAzioneAccordo(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDResource> getAllIdResource(Connection connectionPdD,FiltroRicercaResources filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdResource(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDServizio> getAllIdServizi(Connection connectionPdD,FiltroRicercaServizi filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdServizi(connectionPdD, nomeRegistro, filtroRicerca);
	}
	
	public List<IDFruizione> getAllIdFruizioniServizio(Connection connectionPdD,FiltroRicercaFruizioniServizio filtroRicerca,String nomeRegistro) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.registroServizi.getAllIdFruizioniServizio(connectionPdD, nomeRegistro, filtroRicerca);
	}

	
	
	
	
	
	/* ********  R E P O S I T O R Y    O G G E T T I   G E N E R I C I  ******** */
	
	public Serializable getGenericObject(String keyObject) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.getGenericObject(keyObject);
	}
	
	public Serializable pushGenericObject(String keyObject, Serializable object) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.registroServizi.pushGenericObject(keyObject, object);
	}

}
