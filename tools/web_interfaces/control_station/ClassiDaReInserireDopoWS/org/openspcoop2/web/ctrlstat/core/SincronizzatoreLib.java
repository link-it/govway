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


package org.openspcoop2.web.ctrlstat.core;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.openspcoop2.utils.log.LogUtilities;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.gestoreeventi.GestoreEventiCRUDService;
import org.openspcoop2.gestoreeventi.types.EventoSPCoop;
import org.openspcoop2.gestoreeventi.types.ServizioSPCoop;
import org.openspcoop2.gestoreeventi.types.SoggettoSPCoop;
import org.openspcoop2.gestoreeventi.types.Sottoscrittore;
import org.openspcoop2.gestoreeventi.types.Sottoscrizione;
import org.openspcoop2.management.stub.config.crud.ConfigurazioneService;
import org.openspcoop2.management.stub.config.crud.DriverConfigurazioneException_Exception;
import org.openspcoop2.management.stub.regserv.crud.RegistroServizi_Service;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.IRepositoryAutorizzazioniDriverCRUD;

/**
 * Libreria per la Sincronizzazione
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SincronizzatoreLib {

	/* Variabili */
	// Informazioni per la PDD
	private String pddWebServiceSuffix = "";
	// Informazioni per il Registro Servizi
	private String ws_registry_url = null;
	// Informazioni per RepositoryAutorizzazioni
	private String repositoryAutorizzazioni_className = "";
	// Abilitazione Engine
	private boolean enginePDD = false;
	private boolean engineRegistro = false;
	private boolean engineRepositoryAutorizzazioni = false;

	// Driver
	// private static DriverRegistroServiziUDDI driver_uddi;
	private static ControlStationCore cs_core;

	// Web Service
	private static org.openspcoop2.management.stub.config.crud.ConfigurazioneService locator;
	private static org.openspcoop2.management.stub.config.crud.Configurazione ws_core;
	// ws registro
	private static org.openspcoop2.management.stub.regserv.crud.RegistroServizi_Service locatorRegistry;
	private static org.openspcoop2.management.stub.regserv.crud.RegistroServizi ws_registry;

	// ws Gestore Eventi
	private static org.openspcoop2.gestoreeventi.GestoreEventiCRUDService locatorGE = null;
	private static org.openspcoop2.gestoreeventi.GestoreEventiCRUD ws_gestore_eventi = null;

	// Gestore RepositoryAutorizzazioni
	private static IRepositoryAutorizzazioniDriverCRUD gestoreRepositoryAutorizzazioni;

	// Logger utilizzato per debug
	private static Logger log = null;

	private DBManager dbm = null;
	private String wsurlGE;
	private boolean engineGE;
	private String tipo_soggetto;
	private String nome_soggetto;
	private String nome_servizio_applicativo;
	private String tipoDatabase = null;

	public SincronizzatoreLib() throws Exception {
		SincronizzatoreLib.log = LogUtilities.getLogger("sincronizzatore");

		this.init();
	}

	/**
	 * Inizilizza il Sincronizzatore leggendo i parametri dai file di
	 * configurazione
	 */
	public void init() throws Exception {
		// Connessione al DB
		SincronizzatoreLib.log.debug("Connessione al DB");
		String jndiName = "";
		Properties jndiProp = new Properties();
		Properties prop = new Properties();
		InputStream inProp = null;
		try {
			inProp = SincronizzatoreLib.class.getResourceAsStream("/console.datasource.properties");
			prop.load(inProp);
			Enumeration<?> en = prop.propertyNames();
			while (en.hasMoreElements()) {
				String property = (String) en.nextElement();
				if (property.equals("dataSource")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						jndiName = value;
					}
				}
				if (property.startsWith("dataSource.property.")) {
					String key = (property.substring("dataSource.property.".length()));
					if (key != null) {
						key = key.trim();
					}
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
					}
					if ((key != null) && (value != null)) {
						jndiProp.setProperty(key, value);
					}
				}
			}
		} catch (java.lang.Exception e) {
			SincronizzatoreLib.log.error("Impossibile leggere i dati dal file console.datasource.properties: " + e.getMessage());
			return;
		} finally {
			try {
				if (inProp != null) {
					inProp.close();
				}
			} catch (Exception e) {
			}
		}

		if (!DBManager.initialize(jndiName, jndiProp)) {
			SincronizzatoreLib.log.error("Impossibile inizializzare il DBManager");
			return;
		}

		// Leggo le informazioni da infoGeneral.cfg
		SincronizzatoreLib.log.debug("Lettura informazioni da infoGeneral.cfg");

		prop = new Properties();
		try {
			inProp = SincronizzatoreLib.class.getResourceAsStream("/infoGeneral.cfg");
			prop.load(inProp);
			Enumeration<?> en = prop.propertyNames();
			while (en.hasMoreElements()) {
				// PDD Property
				String property = (String) en.nextElement();
				if (property.equals("UrlWebServiceConfigurazione")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.pddWebServiceSuffix = value;
					}
				}

				// WebService Registro
				if (property.equals("UrlWebServiceRegistro")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.ws_registry_url = value;
					}
				}

				// RepositoryAutorizzazioni Property
				if (property.equals("RepositoryAutorizzazioniClassName")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.repositoryAutorizzazioni_className = value;
					}
				}
				// Engine activation
				if (property.equals("sincronizzazionePdd")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						try {
							this.enginePDD = Boolean.parseBoolean(value);
						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante la lettura della proprieta': sincronizzazionePdd");
						}
					}
				}
				if (property.equals("sincronizzazioneRepositoryAutorizzazioni")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						try {
							this.engineRepositoryAutorizzazioni = Boolean.parseBoolean(value);
						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante la lettura della proprieta': sincronizzazioneRepositoryAutorizzazioni");
						}
					}
				}
				if (property.equals("sincronizzazioneRegistro")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						try {
							this.engineRegistro = Boolean.parseBoolean(value);
						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante la lettura della proprieta': sincronizzazioneRegistro");
						}
					}
				}

				if (property.equals("UrlWebServiceGestoreEventi")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.wsurlGE = value;
					}
				}
				if (property.equals("GestoreEventiQueue")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
					}
				}
				// Engine activation
				if (property.equals("sincronizzazioneGE")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						try {
							this.engineGE = Boolean.parseBoolean(value);
						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante la lettura della proprieta': sincronizzazioneGE");
						}
					}
				}

				// Parametri evento
				if (property.equals("tipo_soggetto")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.tipo_soggetto = value;
					}
				}

				if (property.equals("nome_soggetto")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.nome_soggetto = value;
					}
				}

				if (property.equals("nome_servizio_applicativo")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.nome_servizio_applicativo = value;
					}
				}

				if (property.equals("tipoDatabase")) {
					String value = prop.getProperty(property);
					if (value != null) {
						value = value.trim();
						this.tipoDatabase = value;
					}
				}
			}
		} catch (java.lang.Exception e) {
			SincronizzatoreLib.log.error("Impossibile leggere i dati dal file infoGeneral.cfg: " + e.getMessage());
			return;
		} finally {
			try {
				inProp.close();
			} catch (Exception e) {
			}
		}

		// ISQLQueryObject
		try {
			SincronizzatoreLib.log.info("Inizializzo ISQLQueryObject...");
			createISQLQueryObj();
			SincronizzatoreLib.log.info("Inizializzo ISQLQueryObject [" + this.tipoDatabase + "] terminata.");

		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante inizializzazione SQLQueryObject...", e);
			throw new Exception("Errore durante inizializzazione SQLQueryObject...", e);
		}

		// inizializzazione Core

		try {
			SincronizzatoreLib.log.debug("Inizializzazione ControlStationCore...");
			SincronizzatoreLib.cs_core = new ControlStationCore();
			SincronizzatoreLib.log.debug("Inizializzazione ControlStationCore, COMPLETATA!");

		} catch (Exception e) {
			SincronizzatoreLib.log.error("Impossibile inizializzare il ControlStationCore, Termino!");
			return;
		}

		// configuro il gestore per il registro dei servizi
		if (this.engineRegistro) {
			try {
				SincronizzatoreLib.log.info("Configurazione WebService registro servizi");

				SincronizzatoreLib.locatorRegistry = new RegistroServizi_Service();
				
				SincronizzatoreLib.ws_registry = SincronizzatoreLib.locatorRegistry.getRegistroServizi();
				
				BindingProvider provider = (BindingProvider) SincronizzatoreLib.ws_registry;
				provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.ws_registry_url);
				/*if(username !=null && password!=null){
					// to use Basic HTTP Authentication: 
					provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
					provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				}*/

				// driver_uddi = new
				// DriverRegistroServiziUDDI(uddi_inquiryURL,uddi_publishURL,uddi_user,uddi_password,repositoryXML_urlPrefix,repositoryXML_pathPrefix);
			} catch (Exception e) {
				SincronizzatoreLib.log.error("Inizializzazione WebService Registro Servizi non effettuata: " + e.getMessage());
				return;
			}
		} else {
			SincronizzatoreLib.log.info("Inizializzatore Registro non attivato");
		}

		// configuro il gestore repositoryAutorizzazioni
		if (this.engineRepositoryAutorizzazioni) {
			try {
				SincronizzatoreLib.log.info("Configurazione RepositoryAutorizzazioni Manager");
				SincronizzatoreLib.gestoreRepositoryAutorizzazioni = (IRepositoryAutorizzazioniDriverCRUD) Class.forName(this.repositoryAutorizzazioni_className).newInstance();
			} catch (Exception e) {
				SincronizzatoreLib.log.error("GestoreRepositoryAutorizzazioni: Impossibile inizializzare l'istanza RepositoryAutorizzazioniClassName[" + this.repositoryAutorizzazioni_className + "] :" + e.getMessage());
				return;
			}
		} else {
			SincronizzatoreLib.log.info("Inizializzatore RepositoryAutorizzazioni non attivato");
		}

		// configuro Gestore Eventi
		if (this.engineGE) {
			try {
				String pattern = "Configurazione Gestore Eventi : \nUrlWebServiceGestoreEventi: {0} " + "\nTipoSoggetto: {1}" + "\nNomeSoggetto: {2}" + "\nNomeServizioApplicativo: {3}";
				SincronizzatoreLib.log.info(MessageFormat.format(pattern, this.wsurlGE, this.tipo_soggetto, this.nome_soggetto, this.nome_servizio_applicativo));

				SincronizzatoreLib.locatorGE = new GestoreEventiCRUDService();
				SincronizzatoreLib.ws_gestore_eventi = SincronizzatoreLib.locatorGE.getGestoreEventiCRUDPort();
				((BindingProvider) SincronizzatoreLib.ws_gestore_eventi).getRequestContext().put(
				        BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				        this.wsurlGE);
				SincronizzatoreLib.log.info("Configurazione Gestore Eventi Completata!!");
			} catch (Exception e) {
				SincronizzatoreLib.log.error("GestoreEventi: Impossibile inizializzare il Gestore Eventi", e);
				return;
			}
		} else {
			SincronizzatoreLib.log.info("Inizializzatore Gestore Eventi non attivato");
		}

		this.dbm = DBManager.getInstance();
	}

	/**
	 * Esegue il reset della/delle pdd
	 * 
	 * @param pdds
	 * @throws Exception
	 */
	public void resetPdD(PdDControlStation... pdds) throws Exception {
		/**
		 * RESET PDD
		 */
		ArrayList<PdDControlStation> listaPddNonResettate = new ArrayList<PdDControlStation>();
		if (this.enginePDD) {
			// Per prima cosa, chiedo una reset (per i pdd)
			// queryString = "SELECT * FROM "+CostantiDB.PDD;
			// stmt = con.prepareStatement(queryString);
			// risultato = stmt.executeQuery();
			for (PdDControlStation apdd : pdds) {
				String pdd = apdd.getNome();
				String ipPdd = apdd.getIpGestione();
				String tipoPdd = apdd.getTipo();
				String protocollo = apdd.getProtocollo();
				int porta = apdd.getPortaGestione();
				if (tipoPdd.equals("operativo")) {
					String url = protocollo + "://" + ipPdd + ":" + porta + "/" + this.pddWebServiceSuffix;
					
					SincronizzatoreLib.locator = new ConfigurazioneService();
					
					SincronizzatoreLib.ws_core = SincronizzatoreLib.locator.getConfigurazione();
					
					BindingProvider provider = (BindingProvider) SincronizzatoreLib.ws_core;
					provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
					/*if(username !=null && password!=null){
						// to use Basic HTTP Authentication: 
						provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
						provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
					}*/

					try {						
						SincronizzatoreLib.ws_core.reset();
					}catch (DriverConfigurazioneException_Exception e) {
						//non esco ma continuo con le altre pdd, aggiungo questa pdd alla lista delle pdd non resettate
						SincronizzatoreLib.log.error("Riscontrato errore durante la connessione al WebService: " + url, e);
						listaPddNonResettate.add(apdd);
						continue;
					}catch (Exception e) {
						//non esco ma continuo con le altre pdd, aggiungo questa pdd alla lista delle pdd non resettate
						SincronizzatoreLib.log.error("Riscontrato errore durante la reset su " + pdd, e);
						listaPddNonResettate.add(apdd);
						continue;
					}

					SincronizzatoreLib.log.debug("Reset per il pdd " + pdd + " effettuata.");
				}
			}
			if(listaPddNonResettate.size()>0){
				SincronizzatoreLib.log.warn("ALCUNE PdD NON SONO STATE RESETTATE A CAUSA DI ERRORI.");
				SincronizzatoreLib.log.warn("Lista PdD non resettate:");
				for (PdDControlStation pdDControlStation : listaPddNonResettate) {
					SincronizzatoreLib.log.warn(pdDControlStation.getNome()+"@"+pdDControlStation.getProtocollo() + "://" + pdDControlStation.getIpGestione() + ":" + pdDControlStation.getPortaGestione() + "/" + this.pddWebServiceSuffix);
				}
			}
		} else {
			SincronizzatoreLib.log.info("Engine Porta di Dominio non Attivo.");
		}
	}

	/**
	 * Esegue il reset del Registro Servizi
	 * 
	 * @throws Exception
	 */
	public void resetRegistroServizi() throws Exception {
		if (this.engineRegistro) {
			try {
				BindingProvider provider = (BindingProvider) SincronizzatoreLib.ws_registry;
				
				SincronizzatoreLib.log.debug("Avvio Reset Registro Servizio @["+provider.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY)+"]");
				SincronizzatoreLib.ws_registry.reset();
				SincronizzatoreLib.log.debug("Reset Registro Servizi Completato.");

			} catch (Exception e) {
				SincronizzatoreLib.log.error("Impossibile effettuare Reset Registro Servizi.", e);
				throw e;
			}

		} else {
			SincronizzatoreLib.log.info("Engine Registro Servizio non Attivo.");
		}
	}

	/**
	 * Esegue il Reset di RepositoryAutorizzazioni
	 * 
	 * @throws Exception
	 */
	public void resetRepositoryAutorizzazioni() throws Exception {
		if (this.engineRepositoryAutorizzazioni) {
			try {
				SincronizzatoreLib.log.debug("Avvio Reset RepositoryAutorizzazioni");

				SincronizzatoreLib.gestoreRepositoryAutorizzazioni.reset();

				SincronizzatoreLib.log.debug("Reset RepositoryAutorizzazioni completato.");
			} catch (Exception e) {
				SincronizzatoreLib.log.debug("Impossibile effettuare reset RepositoryAutorizzazioni.", e);
				throw e;
			}
		} else {
			SincronizzatoreLib.log.info("Engine RepositoryAutorizzazioni non abilitato.");
		}
	}

	/**
	 * Reset del Gestore Eventi
	 * 
	 * @throws Exception
	 */
	public void resetGestoreEventi() throws Exception {
		if (this.engineGE) {
			try {
				SincronizzatoreLib.log.debug("Avvio Reset Gestore Eventi");

				SincronizzatoreLib.ws_gestore_eventi.reset();

				SincronizzatoreLib.log.debug("Reset Gestore Eventi completato.");
			} catch (Exception e) {
				SincronizzatoreLib.log.debug("Impossibile effettuare reset Gestore Eventi.", e);
				throw e;
			}
		} else {
			SincronizzatoreLib.log.info("Engine Gestore Eventi non abilitato.");
		}
	}

	/**
	 * Sincronizza una Porta di Dominio inviando l'oggetto da sincronizzare
	 * 
	 * @param <T>
	 * @param aPdd
	 *            La Porta di Dominio su cui effettuare le operazioni di
	 *            sincronizzazione
	 * @param oggetto
	 *            L'oggetto da sincronizzare
	 * @throws Exception
	 */
	private boolean syncPdD(PdDControlStation aPdd) throws Exception {
		String tipoPdd = aPdd.getTipo();
		String pdd = aPdd.getNome();
		String ipPdd = aPdd.getIpGestione();
		String protocollo = aPdd.getProtocollo();
		int porta = aPdd.getPortaGestione();

		if (tipoPdd.equals("operativo")) {
			String url = protocollo + "://" + ipPdd + ":" + porta + "/" + this.pddWebServiceSuffix;
			SincronizzatoreLib.locator = new ConfigurazioneService();
			
			try {
				SincronizzatoreLib.ws_core = SincronizzatoreLib.locator.getConfigurazione();
				
				BindingProvider provider = (BindingProvider) SincronizzatoreLib.ws_core;
				provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
				/*if(username !=null && password!=null){
					// to use Basic HTTP Authentication: 
					provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
					provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				}*/
				
			} catch (Exception e) {
				SincronizzatoreLib.log.error("Riscontrato errore durante syncPdD su PdD [" + pdd + "]", e);
				throw new Exception(e);
			}
			return true;
			
		} else {
			SincronizzatoreLib.log.debug("Sincronizzazione PdD [" + pdd + "] non effettuata causa tipo [" + tipoPdd + "]");
			return false;
		}
	}
	private void syncSoggettoSPCoop(PdDControlStation aPdd,org.openspcoop2.core.config.Soggetto soggetto,boolean delete) throws Exception {
		if(syncPdD(aPdd)){
			
			String url = (String) ((BindingProvider) SincronizzatoreLib.ws_core).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
			if(delete){
				SincronizzatoreLib.log.debug("Delete config.Soggetto @["+url+"]");
				SincronizzatoreLib.ws_core.deleteSoggetto(soggetto);
			}
			else{
				IDSoggetto idSO = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
				if(SincronizzatoreLib.ws_core.existsSoggetto(idSO)==false){
					SincronizzatoreLib.log.debug("Create config.Soggetto @["+url+"]");
					SincronizzatoreLib.ws_core.createSoggetto(soggetto);
				} else {
					SincronizzatoreLib.log.debug("Update config.Soggetto @["+url+"]");
					SincronizzatoreLib.ws_core.updateSoggetto(soggetto);
				}
			}
		}
	}
	private void syncServizioApplicativo(PdDControlStation aPdd,ServizioApplicativo sa,boolean delete) throws Exception {
		if(syncPdD(aPdd)){
			
			String url = (String) ((BindingProvider) SincronizzatoreLib.ws_core).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
			if(delete){
				SincronizzatoreLib.log.debug("Delete Servizio Applicativo @["+url+"]");
				SincronizzatoreLib.ws_core.deleteServizioApplicativo(sa);
			}
			else{
				
				IDSoggetto idSO = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idSO);
				idSA.setNome(sa.getNome());
				
				if(SincronizzatoreLib.ws_core.existsServizioApplicativo(idSA)==false){
					SincronizzatoreLib.log.debug("Create Servizio Applicativo @["+url+"]");
					SincronizzatoreLib.ws_core.createServizioApplicativo(sa);
				} else {
					SincronizzatoreLib.log.debug("Update Servizio Applicativo @["+url+"]");
					SincronizzatoreLib.ws_core.updateServizioApplicativo(sa);
				}
			}
		}
	}
	private void syncPortaDelegata(PdDControlStation aPdd,PortaDelegata pd,boolean delete) throws Exception {
		if(syncPdD(aPdd)){
			
			String url = (String) ((BindingProvider) SincronizzatoreLib.ws_core).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
			if(delete){
				SincronizzatoreLib.log.debug("Delete PortaDelegata @["+url+"]");
				SincronizzatoreLib.ws_core.deletePortaDelegata(pd);
			}
			else{
				
				IDSoggetto idSO = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setLocationPD(pd.getNome());
				idPD.setSoggettoFruitore(idSO);
				
				if(SincronizzatoreLib.ws_core.existsPortaDelegata(idPD)==false){
					SincronizzatoreLib.log.debug("Create PortaDelegata @["+url+"]");
					SincronizzatoreLib.ws_core.createPortaDelegata(pd);
				} else {
					SincronizzatoreLib.log.debug("Update PortaDelegata @["+url+"]");
					SincronizzatoreLib.ws_core.updatePortaDelegata(pd);
				}
			}
		}
	}
	private void syncPortaApplicativa(PdDControlStation aPdd,PortaApplicativa pa,boolean delete) throws Exception {
		if(syncPdD(aPdd)){
			
			String url = (String) ((BindingProvider) SincronizzatoreLib.ws_core).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
			if(delete){
				SincronizzatoreLib.log.debug("Delete PortaApplicativa @["+url+"]");
				SincronizzatoreLib.ws_core.deletePortaApplicativa(pa);
			}
			else{
				
				IDSoggetto idSO = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
				
				if(SincronizzatoreLib.ws_core.existsPortaApplicativaProprietario(pa.getNome(), idSO)==false){
					SincronizzatoreLib.log.debug("Create PortaApplicativa @["+url+"]");
					SincronizzatoreLib.ws_core.createPortaApplicativa(pa);
				} else {
					SincronizzatoreLib.log.debug("Update PortaApplicativa @["+url+"]");
					SincronizzatoreLib.ws_core.updatePortaApplicativa(pa);
				}
			}
		}
	}
	
	/**
	 * Esegue la sincronizzazione con filtro
	 * 
	 * 
	 * @throws Exception
	 */
	public void syncByFilter(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {

		//porte di dominio
		this.syncPorteDominio(pdds, filtro);
		// soggetti
		this.syncSoggetti(pdds, filtro);
		// accordi
		this.syncAccordi(filtro);
		// servizi
		this.syncServizi(pdds, filtro);
		// sa
		this.syncServiziApplicativi(pdds, filtro);
		// pa
		this.syncPorteApplicative(pdds, filtro);
		// pd
		this.syncPorteDelegate(pdds, filtro);

	}

	/**
	 * Effettua La sincronizzazione dei Soggetti delle Porte di Dominio passate
	 * come parametri La sincronizzazione viene fatta in base agli engine attivi
	 * (enginePdD, engineRegistro, engineRepositoryAutorizzazioni)
	 * 
	 * @throws Exception
	 */
	public void syncSoggetti(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		// String pattern = "";
		try {
			con = this.dbm.getConnection();

			ISQLQueryObject sqlObj = createISQLQueryObj();

			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.PDD);

			sqlObj.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "id as idSoggetto");

			sqlObj.setANDLogicOperator(true);

			// query generale tutti i soggetti
			sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".server=" + CostantiDB.PDD + ".nome");
			sqlObj.addWhereCondition(CostantiDB.PDD + ".nome=?");

			if (filtro != null) {
				IDSoggetto soggetto = filtro.getSoggetto();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}
			}

			// tutti i soggetti
			// pattern = "SELECT *, {0}.id as idSoggetto FROM {0}, {1} WHERE
			// {0}.server={1}.nome AND {1}.nome=?";
			// queryString = MessageFormat.format(pattern, CostantiDB.SOGGETTI,
			// CostantiDB.PDD);

			queryString = sqlObj.createSQLQuery();
			SincronizzatoreLib.log.debug("eseguo query : " + queryString);

			stmt = con.prepareStatement(queryString);
			for (PdDControlStation aPdd : pdds) {
				stmt.setString(1, aPdd.getNome());

				risultato = stmt.executeQuery();
				while (risultato.next()) {
					int id = risultato.getInt("idSoggetto");
					String tipoprov = risultato.getString("tipo_soggetto");
					String nomeprov = risultato.getString("nome_soggetto");

					SincronizzatoreLib.log.info("---------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SOGGETTO ----------");
					SincronizzatoreLib.log.info("---------------------------------------");
					SincronizzatoreLib.log.info("tipo:" + tipoprov);
					SincronizzatoreLib.log.info("nome:" + nomeprov);
					SincronizzatoreLib.log.info("server:" + aPdd.getNome());
					SincronizzatoreLib.log.info("id:" + id);
					SincronizzatoreLib.log.info("---------------------------------------\n\n");

					// Ottengo nuova immagine del soggetto
					SoggettoCtrlStat soggetto = null;
					try {
						// soggetto = backEndConnector.getDatiSoggetto(id);
						soggetto = SincronizzatoreLib.cs_core.getSoggettoCtrlStat(id);
					} catch (Exception e) {
						SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
						soggetto = null;
					}
					if (soggetto == null) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + aPdd.getNome());
						continue;
					}

					// Operazione per il pdd
					if (this.enginePDD) {

						try {
							SincronizzatoreLib.log.info("Sincronizzazione Soggetto su PdD :" + aPdd.getNome());
							this.syncSoggettoSPCoop(aPdd, soggetto.getSoggettoConf(), false);
							SincronizzatoreLib.log.info("Sincronizzazione Soggetto su PdD :" + aPdd.getNome() + " Completata.");
						} catch (Exception e) {
							SincronizzatoreLib.log.error(e, e);
							continue;
						}
					}

					// Operazione per il registro
					if (this.engineRegistro) {
						// IDSoggetto idSogg = new
						// IDSoggetto(tipoprov,nomeprov);
						// TODO controllare esistenza soggetto (aggiungendo
						// metodo in webservice)
						// per adesso provo prima a creare l'oggetto e se ho un
						// eccezione allora provo a fare l'update
						Soggetto soggReg = null;
						try {
							soggReg = soggetto.getSoggettoReg();

							this.syncSoggettoRegistro(soggReg, false);

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante update di soggetto tramite WebService Registro", e);
							SincronizzatoreLib.log.error("impossibile creare o aggiornare il soggetto " + soggReg.getTipo() + "/" + soggReg.getNome());
							continue;

						}
					}

					// Operazione per RepositoryAutorizzazioni
					if (this.engineRepositoryAutorizzazioni) {

						try {
							Soggetto soggDAO = soggetto.getSoggettoReg();
							String patternDAO = "Soggetto per RepositoryAutorizzazioni :\n\t Nome [{0}] \n\tDescr[{1}] \n\tTipo[{2}] \n\tServer[{3}]";
							String info = MessageFormat.format(patternDAO, soggDAO.getNome(), soggDAO.getDescrizione(), soggDAO.getTipo(), soggDAO.getPortaDominio());
							SincronizzatoreLib.log.info(info);
							SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createSoggetto(soggDAO);
							SincronizzatoreLib.log.debug("Add di " + tipoprov + "/" + nomeprov + "nel repository RepositoryAutorizzazioni effettuata.");

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Riscontrato errore durante l'inserimento del soggetto nel RepositoryAutorizzazioni: " + e.getMessage());
							continue;
						}
					}

					if (this.engineGE) {
						try {
							SoggettoSPCoop sog = new SoggettoSPCoop();
							sog.setNome(soggetto.getNome());
							sog.setTipo(soggetto.getTipo());

							// Effettuo operazione sul soggetto
							if (SincronizzatoreLib.ws_gestore_eventi.esisteSoggetto(sog)) {
								SincronizzatoreLib.ws_gestore_eventi.aggiornaSoggetto(sog, sog);
							} else {
								SincronizzatoreLib.ws_gestore_eventi.registraSoggetto(sog);
							}

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante creazione soggetto tramite WebService Gestore Eventi", e);
							continue;
						}
					}

					SincronizzatoreLib.log.info("-------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SOGGETTO TERMINATA ----------");
					SincronizzatoreLib.log.info("-------------------------------------------------\n\n\n");
				}
			}// chiudo for
			risultato.close();
			stmt.close();

		} catch (Exception e) {
			SincronizzatoreLib.log.error(e, e);
			throw new Exception(e);
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione Accordi Servizio Utilizza come filtro (se impostato) l'
	 * IDService - Se impostato IDService viene utilizzato -IDService tipo e/o
	 * nome del servizio -IDSoggetto tipo e/o nome del soggetto fornitore del
	 * servizio
	 * 
	 * @throws Exception
	 */
	public void syncAccordi(FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		try {
			con = this.dbm.getConnection();

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.ACCORDI);
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.SERVIZI);

			// select
			sqlObj.setSelectDistinct(true);
			sqlObj.setANDLogicOperator(true);
			sqlObj.addSelectField(CostantiDB.ACCORDI, "id as idAccordo");
			sqlObj.addSelectField(CostantiDB.ACCORDI, "nome");
			sqlObj.addSelectField(CostantiDB.ACCORDI, "versione");
			sqlObj.addSelectField(CostantiDB.ACCORDI, "id_referente");
			// where di join
			sqlObj.addWhereCondition(CostantiDB.ACCORDI + ".id=" + CostantiDB.SERVIZI + ".id_accordo");
			sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtri
			if (filtro != null && filtro.getServizio() != null) {
				// se filtro impostato
				IDServizio servizio = filtro.getServizio();
				// filtro su tipo e/o nome del servizio
				if (servizio != null) {
					if (servizio.getTipoServizio() != null && !"".equals(servizio.getTipoServizio())) {
						sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".tipo_servizio='" + servizio.getTipoServizio() + "'");

					}
					if (servizio.getServizio() != null && !"".equals(servizio.getServizio())) {
						sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".nome_servizio='" + servizio.getTipoServizio() + "'");

					}
				}
				// filtro su tipo e/o nome del soggetto erogatore
				IDSoggetto soggetto = servizio.getSoggettoErogatore();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}

			}

			/**
			 * ACCORDI
			 */
			// queryString = "SELECT * FROM " + CostantiDB.ACCORDI;
			queryString = sqlObj.createSQLQuery();
			SincronizzatoreLib.log.debug("eseguo query : " + queryString);
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				int id = risultato.getInt("idAccordo");
				String nome = risultato.getString("nome");
				String versione = risultato.getString("versione");
				long id_referente = risultato.getLong("id_referente");
				String soggettoReferente = null;
				if(id_referente>0){
					Soggetto soggettoReferenteSPCoop = SincronizzatoreLib.cs_core.getSoggettoRegistro(id_referente);
					if(soggettoReferenteSPCoop==null){
						SincronizzatoreLib.log.error("Riscontrato errore durante la lettura del soggetto referente per l'accordo ["+nome+"] (versione ["+versione+"]) con id " + id_referente);
						continue;
					}
					soggettoReferente =soggettoReferenteSPCoop.getTipo()+soggettoReferenteSPCoop.getNome();
				}
			

				SincronizzatoreLib.log.info("----------------------------------------------");
				SincronizzatoreLib.log.info("---------- GESTIONE ACCORDO SERVIZIO ---------");
				SincronizzatoreLib.log.info("----------------------------------------------");
				SincronizzatoreLib.log.info("nome:" + nome);
				SincronizzatoreLib.log.info("soggetto-referente:" + soggettoReferente);
				SincronizzatoreLib.log.info("versione:" + versione);
				SincronizzatoreLib.log.info("id:" + id);
				SincronizzatoreLib.log.info("----------------------------------------------\n\n");

				// Ottengo nuova immagine dell'accordo
				AccordoServizio accserv = null;
				try {
					// accserv = backEndConnector.getDatiAccordoServizio(id);
					accserv = SincronizzatoreLib.cs_core.getAccordoServizio(id);
				} catch (Exception e) {
					accserv = null;
				}
				if (accserv == null) {
					SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine dell'accordo " + nome);
					continue;
				}

				// Operazione per il registro
				if (this.engineRegistro) {

					try {
						this.syncAccordoServizio(accserv, false); 
					} catch (Exception e) {
						SincronizzatoreLib.log.error("Errore durante update di AccordoServizio tramite WebService Registro", e);
						SincronizzatoreLib.log.error("impossibile creare o aggiornare AccordoServizio " + IDAccordo.getUriFromAccordo(accserv));
						continue;
					}
				} else {
					SincronizzatoreLib.log.info("Engine Registro Disabilitato.");
				}

				if (this.engineRepositoryAutorizzazioni) {

					String pattern = "Creao Ruolo RepositoryAutorizzazioni: Nome: {0} Descrizione:{1}";
					String descr = null;

					// creo ruolo normale
					SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createRuolo(nome, accserv.getDescrizione());
					descr = accserv.getDescrizione();
					SincronizzatoreLib.log.debug(MessageFormat.format(pattern, nome, descr));

					// creo ruolo normale
					SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createRuolo(nome + "Correlato", accserv.getDescrizione() + " [Correlato]");
					descr = accserv.getDescrizione() + " [Correlato]";
					SincronizzatoreLib.log.debug(MessageFormat.format(pattern, nome, descr));

				} else {
					SincronizzatoreLib.log.info("Engine RepositoryAutorizzazioni Disabilitato.");
				}

				SincronizzatoreLib.log.info("---------------------------------------------------------");
				SincronizzatoreLib.log.info("---------- GESTIONE ACCORDO SERVIZIO TERMINATA ----------");
				SincronizzatoreLib.log.info("---------------------------------------------------------\n\n\n");
			}
			risultato.close();
			stmt.close();

		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione ServiziSpCoop
	 * 
	 * @throws Exception
	 */
	public void syncServizi(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet risultato = null;
		ResultSet risultato1 = null;
		// String pattern = null;
		try {
			con = this.dbm.getConnection();

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.PDD);
			sqlObj.addFromTable(CostantiDB.SERVIZI);

			sqlObj.addSelectField(CostantiDB.SERVIZI, "id as idServizio");
			sqlObj.addSelectField(CostantiDB.SERVIZI, "tipo_servizio");
			sqlObj.addSelectField(CostantiDB.SERVIZI, "nome_servizio");
			sqlObj.addSelectField(CostantiDB.SERVIZI, "id_soggetto");

			sqlObj.setANDLogicOperator(true);
			// where
			sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".server=" + CostantiDB.PDD + ".nome");
			sqlObj.addWhereCondition(CostantiDB.PDD + ".nome=?");
			sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtro su tipo/nome soggetto
			if (filtro != null) {
				IDSoggetto soggetto = filtro.getSoggetto();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}
			}

			/**
			 * SERVIZI Selezione tutti i servizi appartenenti ai soggetti dei
			 * nal passati da input
			 */
			// pattern = "SELECT *, {2}.id as idServizio FROM {0}, {1}, {2} " +
			// "WHERE {0}.server={1}.nome " + "AND {1}.nome=? " + "AND
			// {2}.id_soggetto={0}.id";
			// queryString = MessageFormat.format(pattern, CostantiDB.SOGGETTI,
			// CostantiDB.PDD, CostantiDB.SERVIZI);
			queryString = sqlObj.createSQLQuery();
			SincronizzatoreLib.log.debug("eseguo query : " + queryString);
			stmt = con.prepareStatement(queryString);
			for (PdDControlStation aPdd : pdds) {
				stmt.setString(1, aPdd.getNome());

				risultato = stmt.executeQuery();
				while (risultato.next()) {
					int id = risultato.getInt("idServizio");
					String tiposervizio = risultato.getString("tipo_servizio");
					String nomeservizio = risultato.getString("nome_servizio");
					String correlato = risultato.getString("servizio_correlato");
					boolean isCorrelato =  CostantiDB.ABILITATO.equals(correlato);
					int idProv = risultato.getInt("id_soggetto");

					String nomeprov = "", tipoprov = "";
					sqlObj = createISQLQueryObj();
					sqlObj.addFromTable(CostantiDB.SOGGETTI);
					sqlObj.addSelectField("*");
					sqlObj.addWhereCondition("id = ?");
					queryString = sqlObj.createSQLQuery();
					SincronizzatoreLib.log.debug("eseguo query : " + queryString);
					stmt1 = con.prepareStatement(queryString);
					stmt1.setInt(1, idProv);
					risultato1 = stmt1.executeQuery();
					if (risultato1.next()) {
						nomeprov = risultato1.getString("nome_soggetto");
						tipoprov = risultato1.getString("tipo_soggetto");
					}
					risultato1.close();
					stmt1.close();

					SincronizzatoreLib.log.info("---------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SERVIZIO SPCOOP ---------");
					SincronizzatoreLib.log.info("---------------------------------------------");
					SincronizzatoreLib.log.info("nome servizio:" + nomeservizio);
					SincronizzatoreLib.log.info("tipo servizio:" + tiposervizio);
					SincronizzatoreLib.log.info("nome soggetto erogatore:" + nomeprov);
					SincronizzatoreLib.log.info("tipo soggetto erogatore:" + tipoprov);
					SincronizzatoreLib.log.info("id soggetto erogatore:" + idProv);
					SincronizzatoreLib.log.info("correlato:" + isCorrelato);
					SincronizzatoreLib.log.info("id:" + id);
					SincronizzatoreLib.log.info("----------------------------------------------\n\n");

					// Ottengo nuova immagine del servizio
					ServizioSpcoop servizio = null;
					try {
						// ervizio = backEndConnector.getDatiServizio(id);
						servizio = SincronizzatoreLib.cs_core.getServizioSpcoop(id);
					} catch (Exception e) {
						SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
						servizio = null;
					}
					if (servizio == null) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del servizio " + tiposervizio + "/" + nomeservizio + " associato al soggetto " + tipoprov + "/" + nomeprov);
						continue;
					}

					// Ottengo immagine accordo di servizio del servizio
					AccordoServizio as = null;
					try {
						// as=backEndConnector.getDatiAccordoServizio(servizio.getAccordoServizio());
						as = SincronizzatoreLib.cs_core.getAccordoServizio(IDAccordo.getIDAccordoFromUri(servizio.getAccordoServizio()));
					} catch (Exception e) {
						SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
						as = null;
					}
					if (as == null) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine dell'accordo di servizio " + servizio.getAccordoServizio() + " associato al servizio " + tiposervizio + "/" + nomeservizio + " associato al soggetto " + tipoprov + "/" + nomeprov);
						continue;
					}

					// Operazione per il registro
					if (this.engineRegistro) {

						try {
							// Prima di effettuare la sincronizzazione del
							// servizio
							// devo verificare che i fruitori del servizio
							// esistano in remoto
							// se nn esistono devo crearli
							if (servizio.sizeFruitoreList() > 0) {
								checkFruitori(servizio.getFruitoreList());
							}

							// il metodo si occupa di controllare l'esistenza
							// dell'oggetto prima di effettuare
							// la creazione in caso di oggetto gia' presente
							// effettua un update
							if(isCorrelato){
								this.syncServizioSPCoopCorrelato(servizio, false);
							}else{
								this.syncServizioSPCoop(servizio, false);
							}

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante sincronizzazione servizio tramite WebService Registro", e);
							SincronizzatoreLib.log.error("Impossibile creare o aggiornare il servizio " + servizio.getTipo() + "/" + servizio.getNome());
							continue;
						}
					}

					// Operazione per RepositoryAutorizzazioni
					if (this.engineRepositoryAutorizzazioni) {
						try {
							ServizioSpcoop servDAO = new ServizioSpcoop();
							AccordoServizio accServDAO = new AccordoServizio();
							servDAO.setNome(servizio.getNome());
							servDAO.setTipo(servizio.getTipo());
							servDAO.setAccordoServizio(servizio.getAccordoServizio());
							servDAO.setNomeSoggettoErogatore(servizio.getNomeSoggettoErogatore());
							servDAO.setTipoSoggettoErogatore(servizio.getTipoSoggettoErogatore());
							servDAO.setServizioCorrelato(servizio.isServizioCorrelato());
							servDAO.setOldNomeForUpdate(servizio.getNome());
							servDAO.setOldTipoForUpdate(servizio.getTipo());
							servDAO.setOldNomeSoggettoErogatoreForUpdate(servizio.getNomeSoggettoErogatore());// questo
							// non
							// viene
							// modificato
							// nel
							// servizio
							servDAO.setOldTipoSoggettoErogatoreForUpdate(servizio.getTipoSoggettoErogatore());// quindi
							// va
							// bene
							// utilizzare
							// gli
							// stessi
							// nome
							// e
							// tipo
							// sogg
							// erogatore

							String patternDAO = "Servizio per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] " + "\n\t Tipo[{1}] " + "\n\t AccordoServizio[{2}] " + "\n\t NomeSoggettoErogatore[{3}]" + "\n\t TipoSoggettoErogatore[{4}]" + "\n\t ServizioCorrelato[{5}]";
							String info = MessageFormat.format(patternDAO, servDAO.getNome(), servDAO.getTipo(), servDAO.getAccordoServizio(), servDAO.getNomeSoggettoErogatore(), servDAO.getTipoSoggettoErogatore(), servDAO.isServizioCorrelato());
							SincronizzatoreLib.log.info(info);

							SincronizzatoreLib.log.info("\n\tAggiungo [" + as.sizeAzioneList() + "] Azioni...");
							// aggiungo azioni
							for (int i = 0; i < as.sizeAzioneList(); i++) {
								patternDAO = "\n\tAzione [{0}] --> Nome: {1}";

								org.openspcoop2.core.registry.Azione azDAO = new org.openspcoop2.core.registry.Azione();
								azDAO.setNome(as.getAzione(i).getNome());
								accServDAO.addAzione(azDAO);

								info = MessageFormat.format(patternDAO, i + "", azDAO.getNome());
								SincronizzatoreLib.log.info(info);
							}

							SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createServizio(accServDAO, servDAO);
							SincronizzatoreLib.log.info("Add di " + tiposervizio + "/" + nomeservizio + " associato al soggetto " + tipoprov + "/" + nomeprov + " nel Repository RepositoryAutorizzazioni effettuata.");

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Riscontrato errore durante l'inserimento del servizio nel RepositoryAutorizzazioni: " + e.getMessage());
							continue;
						}
					}

					SincronizzatoreLib.log.info("---------------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SERVIZIO SPCOOP TERMINATA -----------");
					SincronizzatoreLib.log.info("---------------------------------------------------------\n\n\n");
				}
			}// chiudo for
			risultato.close();
			stmt.close();
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (risultato1 != null) {
					risultato1.close();
				}
				if (stmt1 != null) {
					stmt1.close();
				}
			} catch (Exception e) {
				// ignore
			}

			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione Servizi Applicativi
	 * 
	 * @throws Exception
	 */
	public void syncServiziApplicativi(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			con = this.dbm.getConnection();

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.PDD);
			sqlObj.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);

			sqlObj.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "id as idSA");
			sqlObj.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "nome");
			sqlObj.addSelectField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto");

			sqlObj.setANDLogicOperator(true);

			// where
			sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".server=" + CostantiDB.PDD + ".nome");
			sqlObj.addWhereCondition(CostantiDB.PDD + ".nome=?");
			sqlObj.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtro su tipo/nome soggetto
			if (filtro != null) {
				IDSoggetto soggetto = filtro.getSoggetto();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}
			}

			/**
			 * SERVIZI APPLICATIVI Selezione i servizi applicativi dei soggetti
			 * appartenenti ai nal passati
			 */
			// pattern = "SELECT *, {2}.id as idSA FROM {0}, {1}, {2} " + "WHERE
			// {0}.server={1}.nome " + "AND {1}.nome=? " + "AND
			// {2}.id_soggetto={0}.id";
			// queryString = MessageFormat.format(pattern, CostantiDB.SOGGETTI,
			// CostantiDB.PDD, CostantiDB.SERVIZI_APPLICATIVI);
			queryString = sqlObj.createSQLQuery();
			SincronizzatoreLib.log.debug("eseguo query : " + queryString);
			stmt = con.prepareStatement(queryString);
			for (PdDControlStation aPdd : pdds) {
				stmt.setString(1, aPdd.getNome());

				risultato = stmt.executeQuery();
				while (risultato.next()) {
					int id = risultato.getInt("idSA");
					String nome = risultato.getString("nome");
					int idProv = risultato.getInt("id_soggetto");

					SincronizzatoreLib.log.info("--------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SERVIZIO APPLICATIVO ---------");
					SincronizzatoreLib.log.info("--------------------------------------------------");
					SincronizzatoreLib.log.info("nome servizio applicativo:" + nome);
					SincronizzatoreLib.log.info("Porta di Dominio di appartenenza:" + aPdd.getIpGestione());
					SincronizzatoreLib.log.info("Tipo Porta di Dominio di appartenenza:" + aPdd.getTipo());
					SincronizzatoreLib.log.info("id Porta di Dominio di appartenenza:" + aPdd.getNome());
					SincronizzatoreLib.log.info("id soggetto appartenenza:" + idProv);
					SincronizzatoreLib.log.info("id:" + id);
					SincronizzatoreLib.log.info("--------------------------------------------------\n\n");

					// Ottengo nuova immagine del servizioApplicativo
					ServizioApplicativo s = null;
					try {
						// s = backEndConnector.getDatiServizioApplicativo(id);
						s = SincronizzatoreLib.cs_core.getServizioApplicativo(id);
					} catch (Exception e) {
						SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
						s = null;
					}
					if (s == null) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del servizioApplicativo " + nome + " sul pdd " + aPdd.getNome());
						continue;
					}

					// Operazione per il pdd
					if (this.enginePDD) {

						try {
							SincronizzatoreLib.log.info("Sincronizzazione Servizio Applicativo su PdD :" + aPdd.getNome());
							this.syncServizioApplicativo(aPdd, s, false);
							SincronizzatoreLib.log.info("Sincronizzazione Servizio Applicativo su PdD :" + aPdd.getNome() + " Completata.");
						} catch (Exception e) {
							SincronizzatoreLib.log.error(e);
							continue;
						}
					}

					// Operazione per RepositoryAutorizzazioni
					if (this.engineRepositoryAutorizzazioni) {
						try {
							org.openspcoop2.core.config.ServizioApplicativo saDAO = new org.openspcoop2.core.config.ServizioApplicativo();
							saDAO.setNome(s.getNome());
							saDAO.setDescrizione("ServizioApplicativo [" + s.getNome() + "] di [" + s.getTipoSoggettoProprietario() + s.getNomeSoggettoProprietario() + "]");// la
							// descrizione
							// e'
							// vuota
							saDAO.setNomeSoggettoProprietario(s.getNomeSoggettoProprietario());
							saDAO.setTipoSoggettoProprietario(s.getTipoSoggettoProprietario());
							// setto ruoli
							saDAO.setRuoloList(s.getRuoloList());
							String patternDAO = "ServizioApplicativo per RepositoryAutorizzazioni :" + "\n\t Nome [{0}] " + "\n\t Descrizione [{1}] " + "\n\t NomeSoggetto [{2}]" + "\n\t TipoSoggetto [{3}] \n\t TotRuoli [{4}]";
							String info = MessageFormat.format(patternDAO, saDAO.getNome(), saDAO.getDescrizione(), saDAO.getNomeSoggettoProprietario(), saDAO.getTipoSoggettoProprietario(), saDAO.sizeRuoloList());
							SincronizzatoreLib.log.info(info);

							SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createServizioApplicativo(saDAO);
							SincronizzatoreLib.log.debug("Add di " + nome + " sul repository RepositoryAutorizzazioni effettuata.");

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Riscontrato errore durante l'inserimento del servizio nel RepositoryAutorizzazioni: " + e.getMessage());
							continue;
						}
					}

					if (this.engineGE) {
						try {
							String nomeSoggetto = s.getNomeSoggettoProprietario();
							String tipoSoggetto = s.getTipoSoggettoProprietario();
							String nomeSA = s.getNome();
							long idFruitore = DBUtils.getIdSoggetto(nomeSoggetto, tipoSoggetto, con, this.tipoDatabase);

							if (this.tipo_soggetto.equals(tipoSoggetto) && this.nome_soggetto.equals(nomeSoggetto) && this.nome_servizio_applicativo.equals(nomeSA)) {
								sqlObj = createISQLQueryObj();
								sqlObj.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
								sqlObj.addFromTable(CostantiDB.SERVIZI);
								sqlObj.addSelectField(CostantiDB.SERVIZI + ".id");
								sqlObj.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio_applicativo = ?");
								sqlObj.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_fruitore = ?");
								sqlObj.addWhereCondition(CostantiDB.POLITICHE_SICUREZZA + ".id_servizio = " + CostantiDB.SERVIZI + ".id");
								sqlObj.setANDLogicOperator(true);
								String sqlQuery = sqlObj.createSQLQuery();
								PreparedStatement stm1 = con.prepareStatement(sqlQuery);
								stm1.setLong(1, s.getId());
								stm1.setLong(2, idFruitore);
								ResultSet rs1 = stm1.executeQuery();
								// se trovo servizi, vuol dire che devo generare
								// l'evento per ogni servizio
								ArrayList<Long> idServizi = new ArrayList<Long>();
								while (rs1.next()) {
									long idServizio = rs1.getLong("id");
									idServizi.add(idServizio);
								}
								rs1.close();
								stm1.close();

								for (Long idServizio : idServizi) {

									ServizioSpcoop servizio = SincronizzatoreLib.cs_core.getServizioSpcoop(idServizio);

									// servizio
									ServizioSPCoop serv = new ServizioSPCoop();
									serv.setTipo(servizio.getTipo());
									serv.setNome(servizio.getNome());
									// evento
									EventoSPCoop evento = new EventoSPCoop();
									evento.setServizio(serv);
									evento.setDescrizione("Evento " + serv.getTipo() + serv.getNome());
									// sottoscrittore
									Sottoscrittore sottoscrittore = new Sottoscrittore();
									sottoscrittore.setId(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());
									sottoscrittore.setServizio(serv);
									SoggettoSPCoop soggettoGestoreEventi = new SoggettoSPCoop();
									soggettoGestoreEventi.setTipo(servizio.getTipoSoggettoErogatore());
									soggettoGestoreEventi.setNome(servizio.getNomeSoggettoErogatore());
									sottoscrittore.setSoggetto(soggettoGestoreEventi);
									sottoscrittore.setTipoConsegna("CONSEGNA");
									sottoscrittore.setDescrizione("Sottoscrittore [" + servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore() + "] del Servizio [" + serv.getTipo() + serv.getNome() + "]");
									// sottoscrizione
									Sottoscrizione sottoscrizione = new Sottoscrizione();
									sottoscrizione.setEvento(evento);
									sottoscrizione.setIdSottoscrittore(serv.getTipo() + "/" + serv.getNome() + "@" + servizio.getTipoSoggettoErogatore() + "/" + servizio.getNomeSoggettoErogatore());

									try {
										if (!SincronizzatoreLib.ws_gestore_eventi.esisteServizio(serv)) {
											SincronizzatoreLib.log.debug("Gestore Eventi: Registo Servizio " + serv.toString());
											SincronizzatoreLib.ws_gestore_eventi.registraServizio(serv);
										} else {
											SincronizzatoreLib.log.debug("Gestore Eventi: Aggiorno Servizio " + serv.toString());
											SincronizzatoreLib.ws_gestore_eventi.aggiornaServizio(serv, serv);
										}

										if (!SincronizzatoreLib.ws_gestore_eventi.esisteEvento(evento)) {
											SincronizzatoreLib.log.debug("Gestore Eventi: Registo Evento " + evento.toString());
											SincronizzatoreLib.ws_gestore_eventi.registraEvento(evento);
										} else {
											SincronizzatoreLib.log.debug("Gestore Eventi: Aggiorno Evento " + evento.toString());
											SincronizzatoreLib.ws_gestore_eventi.aggiornaEvento(evento, evento);
										}

										if (!SincronizzatoreLib.ws_gestore_eventi.esisteSottoscrittore(sottoscrittore)) {
											SincronizzatoreLib.log.debug("Gestore Eventi: Registo Sottoscrittore " + sottoscrittore.toString());
											SincronizzatoreLib.ws_gestore_eventi.registraSottoscrittore(sottoscrittore);
										} else {
											SincronizzatoreLib.log.debug("Gestore Eventi: Aggiorno Sottoscrittore " + sottoscrittore.toString());
											SincronizzatoreLib.ws_gestore_eventi.aggiornaSottoscrittore(sottoscrittore);
										}

										if (!SincronizzatoreLib.ws_gestore_eventi.esisteSottoscrizione(sottoscrizione)) {
											SincronizzatoreLib.log.debug("Gestore Eventi: Registo Sottoscrizione " + sottoscrizione.toString());
											SincronizzatoreLib.ws_gestore_eventi.registraSottoscrizione(sottoscrizione);
										} else {
											SincronizzatoreLib.log.debug("Gestore Eventi: Aggiorno Sottoscrizione " + sottoscrizione.toString());
											SincronizzatoreLib.ws_gestore_eventi.aggiornaSottoscrizione(sottoscrizione);
										}
									} catch (Exception e) {
										SincronizzatoreLib.log.error("Gestore Eventi: Errore durante richiesta al WebService: ", e);
										continue;
									}

								}// for
							}// while

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Gestore Eventi: Errore durante la Gestione di un Evento.", e);
							continue;
						}
					}

					SincronizzatoreLib.log.info("--------------------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE SERVIZIO APPLICATIVO TERMINATA -----------");
					SincronizzatoreLib.log.info("--------------------------------------------------------------\n\n\n");
				}
			}// chiudo for
			risultato.close();
			stmt.close();
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione Fruitori Utilizza come filtro (se impostato) l'
	 * IDService - Se impostato IDService viene utilizzato -IDService tipo e/o
	 * nome del servizio -IDSoggetto tipo e/o nome del soggetto fornitore del
	 * servizio
	 * 
	 * @throws Exception
	 */
	public void syncFruitori(FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet risultato = null;
		ResultSet risultato1 = null;

		try {
			con = this.dbm.getConnection();

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.SERVIZI);

			sqlObj.setANDLogicOperator(true);
			// select
			sqlObj.setSelectDistinct(true);
			sqlObj.addSelectField(CostantiDB.SERVIZI_FRUITORI, "id");
			sqlObj.addSelectField(CostantiDB.SERVIZI_FRUITORI, "id_soggetto");
			sqlObj.addSelectField(CostantiDB.SERVIZI_FRUITORI, "id_servizio");
			// where di join
			// FRUITORE OR EROGATORE
			sqlObj.addWhereCondition(false, CostantiDB.SERVIZI_FRUITORI + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id", CostantiDB.SERVIZI_FRUITORI + ".id_servizio=" + CostantiDB.SERVIZI + ".id" + " AND " + CostantiDB.SERVIZI + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtri
			if (filtro != null && filtro.getServizio() != null) {
				// se filtro impostato
				IDServizio servizio = filtro.getServizio();
				// filtro su tipo e/o nome del servizio
				if (servizio != null) {
					if (servizio.getTipoServizio() != null && !"".equals(servizio.getTipoServizio())) {
						sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".tipo_servizio='" + servizio.getTipoServizio() + "'");

					}
					if (servizio.getServizio() != null && !"".equals(servizio.getServizio())) {
						sqlObj.addWhereCondition(CostantiDB.SERVIZI + ".nome_servizio='" + servizio.getTipoServizio() + "'");

					}
				}
				// filtro su tipo e/o nome del soggetto erogatore
				IDSoggetto soggetto = servizio.getSoggettoErogatore();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}

			}

			/**
			 * FRUITORI
			 */
			// queryString = "SELECT * FROM " + CostantiDB.SERVIZI_FRUITORI;
			queryString = sqlObj.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				int id = risultato.getInt("id");
				int idServizio = risultato.getInt("id_servizio");
				int idFruitore = risultato.getInt("id_soggetto");

				String nomeFruitore = "", tipoFruitore = "";
				sqlObj = createISQLQueryObj();
				sqlObj.addFromTable(CostantiDB.SOGGETTI);
				sqlObj.addSelectField("*");
				sqlObj.addWhereCondition("id = ?");
				queryString = sqlObj.createSQLQuery();
				stmt1 = con.prepareStatement(queryString);
				stmt1.setInt(1, idFruitore);
				risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					nomeFruitore = risultato1.getString("nome_soggetto");
					tipoFruitore = risultato1.getString("tipo_soggetto");
				}
				risultato1.close();
				stmt1.close();

				String nomeServizio = "", tipoServizio = "";
				String nomeSoggErogatore = "", tipoSoggErogatore = "";
				int idSoggettoErogatore = -1;

				sqlObj = createISQLQueryObj();
				sqlObj.addFromTable(CostantiDB.SERVIZI);
				sqlObj.addSelectField("*");
				sqlObj.addWhereCondition("id = ?");
				queryString = sqlObj.createSQLQuery();
				stmt1 = con.prepareStatement(queryString);
				stmt1.setInt(1, idServizio);
				risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					nomeServizio = risultato1.getString("nome_servizio");
					tipoServizio = risultato1.getString("tipo_servizio");
					idSoggettoErogatore = risultato1.getInt("id_soggetto");
				}
				risultato1.close();
				stmt1.close();

				sqlObj = createISQLQueryObj();
				sqlObj.addFromTable(CostantiDB.SOGGETTI);
				sqlObj.addSelectField("*");
				sqlObj.addWhereCondition("id = ?");
				queryString = sqlObj.createSQLQuery();
				stmt1 = con.prepareStatement(queryString);
				stmt1.setInt(1, idSoggettoErogatore);
				risultato1 = stmt1.executeQuery();
				if (risultato1.next()) {
					nomeSoggErogatore = risultato1.getString("nome_soggetto");
					tipoSoggErogatore = risultato1.getString("tipo_soggetto");
				}
				risultato1.close();
				stmt1.close();

				// procedo con la sincronizzazione del fruitore

				SincronizzatoreLib.log.info("----------------------------------------------");
				SincronizzatoreLib.log.info("---------- GESTIONE FRUITORE SPCOOP  ---------");
				SincronizzatoreLib.log.info("----------------------------------------------");
				SincronizzatoreLib.log.info("Nome fruitore:" + nomeFruitore);
				SincronizzatoreLib.log.info("Tipo fruitore:" + tipoFruitore);
				SincronizzatoreLib.log.info("Nome soggetto erogatore:" + nomeSoggErogatore);
				SincronizzatoreLib.log.info("Tipo soggetto erogatore:" + tipoSoggErogatore);
				SincronizzatoreLib.log.info("Nome servizio:" + nomeServizio);
				SincronizzatoreLib.log.info("Tipo servizio:" + tipoServizio);
				SincronizzatoreLib.log.info("idServizio:" + idServizio);
				SincronizzatoreLib.log.info("idSoggettoErogatore:" + idSoggettoErogatore);
				SincronizzatoreLib.log.info("idFruitore:" + idFruitore);
				SincronizzatoreLib.log.info("id:" + id);
				SincronizzatoreLib.log.info("----------------------------------------------\n\n");

				// Ottengo nuova immagine del servizio
				ServizioSpcoop servizio = null;
				try {
					// servizio = backEndConnector.getDatiServizio(idServizio);
					servizio = SincronizzatoreLib.cs_core.getServizioSpcoop(idServizio);
				} catch (Exception e) {
					SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
					servizio = null;
				}
				if (servizio == null) {
					SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del servizio " + tipoServizio + "/" + nomeServizio + " associato al soggetto " + tipoSoggErogatore + "/" + nomeSoggErogatore);
					continue;
				}

				// Operazione per il registro
				if (this.engineRegistro) {

					// devo fare solo una update

					try {

						// SincronizzatoreLib.oos.writeObject(servizio);
						// SincronizzatoreLib.oos.flush();
						//
						// SincronizzatoreLib.ws_registry.performUpdateOperation(SincronizzatoreLib.baos.toByteArray(),
						// servizio.getClass().getName());
						if(servizio.isServizioCorrelato()){
							this.syncServizioSPCoopCorrelato(servizio, false);
						}else{
							this.syncServizioSPCoop(servizio, false);
						}
					} catch (Exception e) {

						SincronizzatoreLib.log.error("Errore durante update di un servizio tramite WebService Registro", e);
						continue;
					}

				}

				// Operazione per RepositoryAutorizzazioni
				if (this.engineRepositoryAutorizzazioni) {
					try {
						org.openspcoop2.core.registry.ServizioSpcoop servDAO = new org.openspcoop2.core.registry.ServizioSpcoop();
						servDAO.setNome(servizio.getNome());
						servDAO.setTipo(servizio.getTipo());
						servDAO.setAccordoServizio(servizio.getAccordoServizio());
						servDAO.setNomeSoggettoErogatore(servizio.getNomeSoggettoErogatore());
						servDAO.setTipoSoggettoErogatore(servizio.getTipoSoggettoErogatore());
						servDAO.setServizioCorrelato(servizio.isServizioCorrelato());

						// recupero il fruitore che mi interessa (e solamente
						// quello)
						// Fruitore fru = null;
						Soggetto soggFru = null;
						try {
							// fru=backEndConnector.getFruitore(idServizio,
							// idFruitore);
							soggFru = SincronizzatoreLib.cs_core.getSoggettoRegistro(idFruitore);

						} catch (Exception e) {
							SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
							SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del fruitore " + tipoFruitore + "/" + nomeFruitore + " del servizio " + tipoServizio + "/" + nomeServizio + " associato al soggetto " + tipoSoggErogatore + "/" + nomeSoggErogatore);
							continue;
						}

						Fruitore fruitoreDAO = new Fruitore();
						fruitoreDAO.setNome(soggFru.getNome());
						fruitoreDAO.setTipo(soggFru.getTipo());
						fruitoreDAO.setId(soggFru.getId());

						// Cerco i servizi applicativi associati al fruitore
						sqlObj = createISQLQueryObj();
						sqlObj.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
						sqlObj.addSelectField("*");
						sqlObj.addWhereCondition("id_fruitore = ?");
						sqlObj.addWhereCondition("id_servizio = ?");
						sqlObj.setANDLogicOperator(true);
						queryString = sqlObj.createSQLQuery();
						stmt1 = con.prepareStatement(queryString);
						stmt1.setInt(1, idFruitore);
						stmt1.setInt(2, idServizio);
						risultato1 = stmt1.executeQuery();
						Vector<Integer> idServiziApplicativFruitore = new Vector<Integer>();
						while (risultato1.next()) {
							idServiziApplicativFruitore.add(risultato1.getInt("id_servizio_applicativo"));
						}
						risultato1.close();
						stmt1.close();

						// recupero i dati del servizio applicativo e li
						// inserisce nel fruitore
						while (idServiziApplicativFruitore.size() > 0) {
							ServizioApplicativo sa = null;
							int idSA = idServiziApplicativFruitore.remove(0);
							try {
								// sa =
								// backEndConnector.getDatiServizioApplicativo(idSA);
								sa = SincronizzatoreLib.cs_core.getServizioApplicativo(idSA);
							} catch (Exception e) {
								SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
								SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine del servizio applicativo con id " + idSA + " del fruitore " + tipoFruitore + "/" + nomeFruitore + " del servizio " + tipoServizio + "/" + nomeServizio + " associato al soggetto " + tipoSoggErogatore + "/" + nomeSoggErogatore);
								continue;
							}
							fruitoreDAO.addServizioApplicativo(sa.getNome());
						}

						// aggiungo il fruitore al ServizioSPcoop
						servDAO.addFruitore(fruitoreDAO);

						String patternDAO = "PoliticheSicurezza per RepositoryAutorizzazioni :" + "\n\t[SERVIZIO]" + "\n\t Nome [{0}] " + "\n\t Tipo [{1}] " + "\n\t AccordoServizio [{2}]" + "\n\t NomeSoggettoErogatore [{3}]" + "\n\t TipoSoggettoErogatore [{4}]" + "\n\t ServizioCorrelato [{5}]" + "\n\n\t[FRUITORE]" + "\n\tNome [{6}]" + "\n\tTipo [{7}]";
						String info = MessageFormat.format(patternDAO, servDAO.getNome(), servDAO.getTipo(), servDAO.getAccordoServizio(), servDAO.getNomeSoggettoErogatore(), servDAO.getTipoSoggettoErogatore(), servDAO.isServizioCorrelato(), fruitoreDAO.getNome(), fruitoreDAO.getTipo());
						SincronizzatoreLib.log.info(info);

						try {
							List<String> serviziApplicativiList = servDAO.getFruitore(0).getServizioApplicativoList();
							SincronizzatoreLib.log.info("\nInserisco " + serviziApplicativiList.size() + " ServiziApplicativi:");
							for (int j = 0; j < serviziApplicativiList.size(); j++) {
								SincronizzatoreLib.log.info("\tServizioApplicativo [" + serviziApplicativiList.get(j)+ "]");

							}
						} catch (Exception e) {
							SincronizzatoreLib.log.info("Servizi Applicativi non presenti");
						}
						SincronizzatoreLib.gestoreRepositoryAutorizzazioni.createFruitore(servDAO);
						SincronizzatoreLib.log.info(" Add del Fruitore " + tipoFruitore + "/" + nomeFruitore + " al servizio di " + tipoServizio + "/" + nomeServizio + " associato al soggetto " + tipoSoggErogatore + "/" + nomeSoggErogatore + ", nel Repository RepositoryAutorizzazioni effettuata.");
					} catch (Exception e) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la createFruitore " + tipoFruitore + "/" + nomeFruitore + " al servizio di " + tipoServizio + "/" + nomeServizio + " associato al soggetto " + tipoSoggErogatore + "/" + nomeSoggErogatore + " nel Repository RepositoryAutorizzazioni: " + e.getMessage(), e);
						continue;
					}
				}

				SincronizzatoreLib.log.info("---------------------------------------------------------");
				SincronizzatoreLib.log.info("---------- GESTIONE FRUITORE SPCOOP TERMINATA -----------");
				SincronizzatoreLib.log.info("---------------------------------------------------------\n\n\n");
			}
			risultato.close();
			stmt.close();
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {

			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (risultato1 != null) {
					risultato1.close();
				}
				if (stmt1 != null) {
					stmt1.close();
				}
			} catch (Exception e) {
				// ignore
			}

			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione Porte Applicative
	 * 
	 * @throws Exception
	 */
	public void syncPorteApplicative(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		// PreparedStatement stmt1 = null;
		ResultSet risultato = null;
		// ResultSet risultato1 = null;
		// String pattern = null;
		try {
			con = this.dbm.getConnection();
			/**
			 * PORTE APPLICATIVE
			 */

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.PDD);
			sqlObj.addFromTable(CostantiDB.PORTE_APPLICATIVE);

			sqlObj.addSelectField(CostantiDB.PORTE_APPLICATIVE, "id as idPA");
			sqlObj.addSelectField(CostantiDB.PORTE_APPLICATIVE, "nome_porta");
			sqlObj.addSelectField(CostantiDB.PORTE_APPLICATIVE, "id_soggetto");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");

			sqlObj.setANDLogicOperator(true);
			// where
			sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".server=" + CostantiDB.PDD + ".nome");
			sqlObj.addWhereCondition(CostantiDB.PDD + ".nome=?");
			sqlObj.addWhereCondition(CostantiDB.PORTE_APPLICATIVE + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtro su tipo/nome soggetto
			if (filtro != null) {
				IDSoggetto soggetto = filtro.getSoggetto();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}
			}

			// pattern = "SELECT *,{2}.id as idPA FROM {0}, {1}, {2} " + "WHERE
			// {0}.server={1}.nome " + "AND {1}.nome=? " + "AND
			// {2}.id_soggetto={0}.id";
			// queryString = MessageFormat.format(pattern, CostantiDB.SOGGETTI,
			// CostantiDB.PDD, CostantiDB.PORTE_APPLICATIVE);
			queryString = sqlObj.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			for (PdDControlStation aPdd : pdds) {
				stmt.setString(1, aPdd.getNome());

				risultato = stmt.executeQuery();
				while (risultato.next()) {
					int id = risultato.getInt("idPA");
					String idporta = risultato.getString("nome_porta");
					int idProv = risultato.getInt("id_soggetto");

					String tipoprov = risultato.getString("tipo_soggetto");
					String nomeprov = risultato.getString("nome_soggetto");
					String pdd = aPdd.getNome();

					SincronizzatoreLib.log.info("------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA APPLICATIVA  ---------");
					SincronizzatoreLib.log.info("------------------------------------------------");
					SincronizzatoreLib.log.info("Nome soggetto proprietario:" + nomeprov);
					SincronizzatoreLib.log.info("Tipo soggetto proprietario:" + tipoprov);
					SincronizzatoreLib.log.info("id Porta di Dominio proprietaria:" + aPdd.getNome());
					SincronizzatoreLib.log.info("Porta di Dominio proprietaria:" + aPdd.getIpGestione());
					SincronizzatoreLib.log.info("Tipo Porta di Dominio proprietaria:" + aPdd.getTipo());
					SincronizzatoreLib.log.info("idSoggetto proprietario:" + idProv);
					SincronizzatoreLib.log.info("idPorta:" + idporta);
					SincronizzatoreLib.log.info("id:" + id);
					SincronizzatoreLib.log.info("------------------------------------------------\n\n");

					// Ottengo nuova immagine dela porta applicativa
					PortaApplicativa pa = null;
					try {
						// pa = backEndConnector.getDatiPA(id);
						pa = SincronizzatoreLib.cs_core.getPortaApplicativa(id);
						if (pa == null) {
							throw new Exception("Nessuna Porta Applicativa trovata.");
						}
					} catch (Exception e) {
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine della porta applicativa " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd);
						continue;
					}

					// Operazione per il pdd
					if (this.enginePDD) {

						try {
							SincronizzatoreLib.log.info("Sincronizzazione Porta Applicativa su PdD :" + aPdd.getNome());
							this.syncPortaApplicativa(aPdd, pa, false);
							SincronizzatoreLib.log.info("Sincronizzazione Porta Applicativa su PdD :" + aPdd.getNome() + " Completata.");

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Riscontrato errore durante la create PA (add) di " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd);
							continue;
						}

						SincronizzatoreLib.log.info("Add di " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd + " effettuata.");
					}

					SincronizzatoreLib.log.info("-----------------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA APPLICATIVA TERMINATA -----------");
					SincronizzatoreLib.log.info("-----------------------------------------------------------\n\n\n");
				}// chiudo while
			}// chiudo for
			risultato.close();
			stmt.close();
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizzazione Porte Delegate
	 * 
	 * @throws Exception
	 */
	public void syncPorteDelegate(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
		Connection con = null;
		String queryString = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		// String pattern = null;
		try {
			con = this.dbm.getConnection();
			/**
			 * PORTE DELEGATE
			 */

			ISQLQueryObject sqlObj = createISQLQueryObj();

			// from
			sqlObj.addFromTable(CostantiDB.SOGGETTI);
			sqlObj.addFromTable(CostantiDB.PDD);
			sqlObj.addFromTable(CostantiDB.PORTE_DELEGATE);

			sqlObj.addSelectField(CostantiDB.PORTE_DELEGATE, "id as idPD");
			sqlObj.addSelectField(CostantiDB.PORTE_DELEGATE, "id_soggetto");
			sqlObj.addSelectField(CostantiDB.PORTE_DELEGATE, "nome_porta");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlObj.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");

			sqlObj.setANDLogicOperator(true);
			// where
			sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".server=" + CostantiDB.PDD + ".nome");
			sqlObj.addWhereCondition(CostantiDB.PDD + ".nome=?");
			sqlObj.addWhereCondition(CostantiDB.PORTE_DELEGATE + ".id_soggetto=" + CostantiDB.SOGGETTI + ".id");

			// filtro su tipo/nome soggetto
			if (filtro != null) {
				IDSoggetto soggetto = filtro.getSoggetto();
				if (soggetto != null) {
					// tipo soggetto
					if (soggetto.getTipo() != null && !"".equals(soggetto.getTipo())) {
						SincronizzatoreLib.log.debug("applico filtro su tipo soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".tipo_soggetto='" + soggetto.getTipo() + "'");

					}
					// nome soggetto
					if (soggetto.getNome() != null && !"".equals(soggetto.getNome())) {
						SincronizzatoreLib.log.debug("applico filtro su nome soggetto");
						sqlObj.addWhereCondition(CostantiDB.SOGGETTI + ".nome_soggetto='" + soggetto.getNome() + "'");
					}
				}
			}
			// pattern = "SELECT *,{2}.id as idPD FROM {0}, {1}, {2} " + "WHERE
			// {0}.server={1}.nome " + "AND {1}.nome=? " + "AND
			// {2}.id_soggetto={0}.id";
			// queryString = MessageFormat.format(pattern, CostantiDB.SOGGETTI,
			// CostantiDB.PDD, CostantiDB.PORTE_DELEGATE);
			queryString = sqlObj.createSQLQuery();
			SincronizzatoreLib.log.debug("eseguo query : " + queryString);
			stmt = con.prepareStatement(queryString);
			for (PdDControlStation aPdd : pdds) {
				stmt.setString(1, aPdd.getNome());

				risultato = stmt.executeQuery();
				while (risultato.next()) {
					int id = risultato.getInt("idPD");
					String idporta = risultato.getString("nome_porta");
					int idProv = risultato.getInt("id_soggetto");

					String tipoprov = risultato.getString("tipo_soggetto");
					String nomeprov = risultato.getString("nome_soggetto");
					String pdd = aPdd.getNome();

					SincronizzatoreLib.log.info("---------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA DELEGATA  ---------");
					SincronizzatoreLib.log.info("---------------------------------------------");
					SincronizzatoreLib.log.info("Nome soggetto proprietario:" + nomeprov);
					SincronizzatoreLib.log.info("Tipo soggetto proprietario:" + tipoprov);
					SincronizzatoreLib.log.info("id Porta di Dominio proprietaria:" + aPdd.getNome());
					SincronizzatoreLib.log.info("Porta di Dominio proprietaria:" + aPdd.getIpGestione());
					SincronizzatoreLib.log.info("Tipo Porta di Dominio proprietaria:" + aPdd.getTipo());
					SincronizzatoreLib.log.info("idSoggetto proprietario:" + idProv);
					SincronizzatoreLib.log.info("idPorta:" + idporta);
					SincronizzatoreLib.log.info("id:" + id);
					SincronizzatoreLib.log.info("----------------------------------------------\n\n");

					// Ottengo nuova immagine dela porta delegata
					PortaDelegata pd = null;
					try {
						// pd = backEndConnector.getDatiPD(id);
						pd = SincronizzatoreLib.cs_core.getPortaDelegata(id);
						if (pd == null) {
							throw new Exception("Nessuna Porta Delegata trovata.");
						}
					} catch (Exception e) {
						SincronizzatoreLib.log.error("ControlStationCore ERROR: " + e.getMessage(), e);
						SincronizzatoreLib.log.error("Riscontrato errore durante la get dell'immagine della porta delegata " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd);
						continue;
					}

					// Operazione per il pdd
					if (this.enginePDD) {

						try {

							SincronizzatoreLib.log.info("Sincronizzazione Porta Delegata su PdD : " + aPdd.getNome());
							this.syncPortaDelegata(aPdd, pd,false);
							SincronizzatoreLib.log.info("Sincronizzazione Porta Delegata su PdD : " + aPdd.getNome() + " Completata");

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Riscontrato errore durante la create PD (add) di " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd);
							continue;
						}

						SincronizzatoreLib.log.info("Add di " + idporta + " sul soggetto " + tipoprov + "/" + nomeprov + " sul pdd " + pdd + " effettuata.");

					}

					SincronizzatoreLib.log.info("----------------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA DELEGATA TERMINATA -------------");
					SincronizzatoreLib.log.info("----------------------------------------------------------\n\n\n");
				}
			}
			risultato.close();
			stmt.close();
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {

			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}

			this.dbm.releaseConnection(con);
		}
	}

	/**
	 * Sincronizza tutte le tabelle delle Porte di Dominio registrate nella
	 * ControlStation Effettua gli aggiornamenti in base agli engine abilitati
	 */
	public void syncAll() throws Exception {
		/**
		 * Le pdd da sincronizzare sono tutte il filtro sul tipo (operativo,
		 * non-operativo, esterno) viene effettuato dal metodo specifico di
		 * sincronizzazione
		 */
		List<PdDControlStation> pdds = SincronizzatoreLib.cs_core.pddList("", new Search(true));
		this.syncAll(pdds.toArray(new PdDControlStation[pdds.size()]));

	}

	/**
	 * Sincronizza tutte le tabelle delle Porte di Dominio passate come
	 * parametro Effettua gli aggiornamenti in base agli engine abilitati
	 * 
	 * @param pdds
	 * @throws Exception
	 */
	public void syncAll(PdDControlStation... pdds) throws Exception {

		try {
			SincronizzatoreLib.log.info("Sincronizzazione Totale...");

			String engineAbilitati = "Engine Abilitati : ";
			if (this.enginePDD) {
				engineAbilitati += " PdD";
			}
			if (this.engineRegistro) {
				engineAbilitati += " RegistroServizi";
			}
			if (this.engineRepositoryAutorizzazioni) {
				engineAbilitati += " RepositoryAutorizzazioni";
			}
			if (this.engineGE) {
				engineAbilitati += " GestoreEventi";
			}

			SincronizzatoreLib.log.info(engineAbilitati);

			ArrayList<String> pddsName = new ArrayList<String>();
			ArrayList<PdDControlStation> lista = new ArrayList<PdDControlStation>();

			for (PdDControlStation pdd : pdds) {
				lista.add(pdd);
				pddsName.add(pdd.getNome() + "[" + pdd.getIpGestione() + "]-[" + pdd.getTipo() + "]");
			}
			SincronizzatoreLib.log.info("Avvio la sincronizzazione delle Porte di Dominio : \n" + pddsName.toString());

			this.syncPorteDominio(lista, null);
			this.syncSoggetti(lista, null);
			this.syncAccordi(null);
			this.syncServizi(lista, null);
			this.syncServiziApplicativi(lista, null);
			this.syncFruitori(null);
			this.syncPorteApplicative(lista, null);
			this.syncPorteDelegate(lista, null);

			SincronizzatoreLib.log.info("Sincronizzazione Totale Completata.");

		} catch (Exception e) {
			SincronizzatoreLib.log.error(e);
			throw new Exception(e);
		}

	}

	public void setEnginePDD(boolean enginePDD) {
		this.enginePDD = enginePDD;
	}

	public void setEngineRegistro(boolean engineRegistro) {
		this.engineRegistro = engineRegistro;
	}

	public void setEngineRepositoryAutorizzazioni(boolean engineRepositoryAutorizzazioni) {
		this.engineRepositoryAutorizzazioni = engineRepositoryAutorizzazioni;
	}

	public void setEngineGE(boolean engineGE) {
		this.engineGE = engineGE;
	}

	private void syncSoggettoRegistro(Soggetto soggetto,boolean delete) throws Exception {
		
		String url = (String) ((BindingProvider) SincronizzatoreLib.ws_registry).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
		if(delete){
			SincronizzatoreLib.log.debug("Delete Soggetto @["+url+"]");
			SincronizzatoreLib.ws_registry.deleteSoggetto(soggetto);
		}else{
			IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			if(SincronizzatoreLib.ws_registry.existsSoggetto(idSoggetto)==false){
				SincronizzatoreLib.log.debug("Create Soggetto registro @["+url+"]");
				SincronizzatoreLib.ws_registry.createSoggetto(soggetto);
			} else {
				SincronizzatoreLib.log.debug("Update Soggetto registro @["+url+"]");
				SincronizzatoreLib.ws_registry.updateSoggetto(soggetto);
			}
		}
	}
	
	private void syncPortaDominio(PortaDominio pdd,boolean delete) throws Exception {
		
		String url = (String) ((BindingProvider) SincronizzatoreLib.ws_registry).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
		if(delete){
			SincronizzatoreLib.log.debug("Delete PortaDominio @["+url+"]");
			SincronizzatoreLib.ws_registry.deletePortaDominio(pdd);
		}else{
			if(SincronizzatoreLib.ws_registry.existsPortaDominio(pdd.getNome())==false){
				SincronizzatoreLib.log.debug("Create PortaDominio @["+url+"]");
				SincronizzatoreLib.ws_registry.createPortaDominio(pdd);
			} else {
				SincronizzatoreLib.log.debug("Update PortaDominio @["+url+"]");
				SincronizzatoreLib.ws_registry.updatePortaDominio(pdd);
			}
		}
	}
	
	private void syncAccordoServizio(AccordoServizio accordo,boolean delete) throws Exception {
		
		String url = (String) ((BindingProvider) SincronizzatoreLib.ws_registry).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
		if(delete){
			SincronizzatoreLib.log.debug("Delete AccordoServizio @["+url+"]");
			SincronizzatoreLib.ws_registry.deleteAccordoServizio(accordo);
		}else{
			IDAccordo idAccordo = IDAccordo.getIDAccordoFromAccordo(accordo);
			if(SincronizzatoreLib.ws_registry.existsAccordoServizio(idAccordo)==false){
				SincronizzatoreLib.log.debug("Create AccordoServizio @["+url+"]");
				SincronizzatoreLib.ws_registry.createAccordoServizio(accordo);
			} else {
				SincronizzatoreLib.log.debug("Update AccordoServizio @["+url+"]");
				SincronizzatoreLib.ws_registry.updateAccordoServizio(accordo);
			}
		}
	}
	
	private void syncServizioSPCoop(ServizioSpcoop servizio,boolean delete) throws Exception {
		
		String url = (String) ((BindingProvider) SincronizzatoreLib.ws_registry).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
		if(delete){
			SincronizzatoreLib.log.debug("Delete ServizioSPCoop @["+url+"]");
			SincronizzatoreLib.ws_registry.deleteServizioSpcoop(servizio);
		}else{
			IDServizio idServizio = new IDServizio(new IDSoggetto(servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore()), 
					servizio.getTipo(), servizio.getNome());
			idServizio.setCorrelato(false);
			if(SincronizzatoreLib.ws_registry.existsServizioSpcoop(idServizio)==false){
				SincronizzatoreLib.log.debug("Create ServizioSPCoop @["+url+"]");
				SincronizzatoreLib.ws_registry.createServizioSpcoop(servizio);
			} else {
				SincronizzatoreLib.log.debug("Update ServizioSPCoop @["+url+"]");
				SincronizzatoreLib.ws_registry.updateServizioSpcoop(servizio);
			}
		}
	}
	
	private void syncServizioSPCoopCorrelato(ServizioSpcoop servizio,boolean delete) throws Exception {
		
		String url = (String) ((BindingProvider) SincronizzatoreLib.ws_registry).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
			
		if(delete){
			SincronizzatoreLib.log.debug("Delete ServizioSPCoop @["+url+"]");
			SincronizzatoreLib.ws_registry.deleteServizioSpcoop(servizio);
		}else{
			IDServizio idServizio = new IDServizio(new IDSoggetto(servizio.getTipoSoggettoErogatore(), servizio.getNomeSoggettoErogatore()), 
					servizio.getTipo(), servizio.getNome());
			idServizio.setCorrelato(true);
			if(SincronizzatoreLib.ws_registry.existsServizioSpcoopCorrelato(idServizio)==false){
				SincronizzatoreLib.log.debug("Create ServizioSPCoopCorrelato @["+url+"]");
				SincronizzatoreLib.ws_registry.createServizioSpcoopCorrelato(servizio);
			} else {
				SincronizzatoreLib.log.debug("Update ServizioSPCoopCorrelato @["+url+"]");
				SincronizzatoreLib.ws_registry.updateServizioSpcoopCorrelato(servizio);
			}
		}
	}

	/**
	 * Controlla se i fruitori passati come parametro sono presenti nel registro
	 * servizi Se un fruitore non e' presente viene creato
	 * 
	 * @param fruitori
	 * @throws Exception
	 */
	private void checkFruitori(List<Fruitore> fruitori) throws Exception {
		if (fruitori == null)
			return;
		// Stream
		try {

			for (int i = 0; i < fruitori.size(); i++) {
				Fruitore fruitore = fruitori.get(i);
				IDSoggetto idSO = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());

				SincronizzatoreLib.log.info("Check Fruitore [" + idSO.toString() + "]");

				boolean exist = SincronizzatoreLib.ws_registry.existsSoggetto(idSO);
				if (!exist) {
					SincronizzatoreLib.log.info("Il Fruitore [" + idSO.toString() + "] non e' presente in remoto, effettuo sincronizzazione del Fruitore");
					// recupero soggetto fruitore dal db
					Soggetto soggetto = SincronizzatoreLib.cs_core.getSoggettoRegistro(idSO);
					String server = soggetto.getPortaDominio();
					PdDControlStation pdd = SincronizzatoreLib.cs_core.getPdDControlStation(server);
					ArrayList<PdDControlStation> pdds = new ArrayList<PdDControlStation>();
					pdds.add(pdd);
					// Filtro su tipo/nome soggetto
					FiltroSincronizzazione filtro = new FiltroSincronizzazione();
					filtro.setSoggetto(idSO);
					// sincronizzo il soggetto
					this.syncSoggetti(pdds, filtro);
					SincronizzatoreLib.log.info("Sincronizzazione Fruitore [" + idSO.toString() + "] Completata");
				}

			}

		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	private ISQLQueryObject createISQLQueryObj() throws SQLQueryObjectException {

		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);

		return sqlQueryObject;
	}

	public void syncPorteDominio(ArrayList<PdDControlStation> pdds, FiltroSincronizzazione filtro) throws Exception {
	
		try {

			for (PdDControlStation aPdd : pdds) {
				
				PortaDominio pd = new PortaDominio();
				pd.setClientAuth(aPdd.getClientAuth());
				pd.setDescrizione(aPdd.getDescrizione());
				pd.setImplementazione(aPdd.getImplementazione());
				pd.setNome(aPdd.getNome());
				pd.setOraRegistrazione(aPdd.getOraRegistrazione());
				pd.setSubject(aPdd.getSubject());
				pd.setId(aPdd.getId());
				pd.setSuperUser(aPdd.getSuperUser());
				
					SincronizzatoreLib.log.info("-------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA DOMINIO ---------");
					SincronizzatoreLib.log.info("-------------------------------------------");
					SincronizzatoreLib.log.info("nome porta:" + pd.getNome());
					SincronizzatoreLib.log.info("descrizione:" + pd.getDescrizione());
					SincronizzatoreLib.log.info("implementazione:" + pd.getImplementazione());
					SincronizzatoreLib.log.info("subject:" + pd.getSubject());
					SincronizzatoreLib.log.info("ora registrazione:" + pd.getOraRegistrazione());
					SincronizzatoreLib.log.info("superuser:" + pd.getSuperUser());
					SincronizzatoreLib.log.info("id:" + pd.getId());
					SincronizzatoreLib.log.info("----------------------------------------------\n\n");

					// Operazione per il registro
					if (this.engineRegistro) {

						try {
							// il metodo si occupa di controllare l'esistenza
							// dell'oggetto prima di effettuare
							// la creazione in caso di oggetto gia' presente
							// effettua un update
							this.syncPortaDominio(pd,false);

						} catch (Exception e) {
							SincronizzatoreLib.log.error("Errore durante sincronizzazione porta dominio tramite WebService Registro", e);
							SincronizzatoreLib.log.error("Impossibile creare o aggiornare la porta di dominio " + pd.getNome());
							continue;
						}
					}

					SincronizzatoreLib.log.info("-------------------------------------------------------");
					SincronizzatoreLib.log.info("---------- GESTIONE PORTA DOMINIO TERMINATA -----------");
					SincronizzatoreLib.log.info("-------------------------------------------------------\n\n\n");
				}
			
		} catch (Exception e) {
			SincronizzatoreLib.log.error("Errore durante sincronizzazione: ", e);
			throw e;
		} finally {
			//this.dbm.releaseConnection(con);
		}
	}
	
}
