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


package org.openspcoop2.core.config.driver;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * Interfaccia per la ricerca informazioni di oggetti presenti in una
 * configurazione di OpenSPCoop. I driver che implementano l'interfaccia 
 * sono attualmente:
 * <ul>
 * <li> {@link org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB}, interroga un registro dei servizi Relazionale.
 * <li> {@link org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML}, interroga un registro dei servizi realizzato tramite un file xml.
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDriverConfigurazioneGet extends IBeanUtilities {

	// SOGGETTO 
	/**
	 * Restituisce Il soggetto identificato da <var>idSoggetto</var>
	 *
	 * @param aSoggetto Identificatore di un soggetto
	 * @return Il Soggetto identificato dal parametro.
	 */
	public Soggetto getSoggetto(IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 * 
	 * @return il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 */
	public Soggetto getRouter() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	public List<IDSoggetto> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei servizi associati a soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	public List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la lista degli identificativi dei soggetti
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei soggetti
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<IDSoggetto> getAllIdSoggetti(
			FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	
	
	// PORTA DELEGATA
	
	/**
	 * Restituisce l'identificativo della PortaDelegata identificata da <var>nome</var>.
	 * Tale struttura contiene gli identificativi del fruitore e del servizio indirizzato dalla porta.
	 *
	 * @param nome Nome che identifica una porta delegata
	 * @return Identificativi del fruitore e del servizio indirizzato dalla porta.
	 */
	public IDPortaDelegata getIDPortaDelegata(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la porta delegata identificato da <var>idPD</var>
	 *
	 * @param idPD Identificatore della Porta Delegata
	 * @return La porta delegata.
	 * 
	 */
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound; 
	
	/**
	 * Restituisce la lista degli identificativi delle porte delegate
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte delegate
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<IDPortaDelegata> getAllIdPorteDelegate(
			FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	
	
	// PORTA APPLICATIVA
	
	/**
	 * Restituisce l'identificativo della PortaApplicativa identificata da <var>nome</var>.
	 * Tale struttura contiene l'identificativo del servizio indirizzato dalla porta.
	 *
	 * @param nome Nome che identifica una porta applicativa
	 * @return Identificativo del servizio indirizzato dalla porta.
	 */
	public IDPortaApplicativa getIDPortaApplicativa(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le porta applicative che indirizzano il servizio indicato in <var>idServizio</var>
	 * Nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idServizio
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le porta applicative che indirizzano il servizio indicato in <var>idServizio</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * Nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param soggettoVirtuale
	 * @param idServizio
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa) 
	 * che erogano il servizio indicato dal parametro <var>idServizio</var> attraverso 
	 * il SoggettoVirtuale indicato come soggettoErogatore sempre nel parametro <var>idServizio</var>.
	 *
	 * @param idServizio Identificatore del servizio
	 * @return una porta applicativa
	 * 
	 */
	public Map<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce la lista degli identificativi delle porte applicative
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte applicative
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<IDPortaApplicativa> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	
	
	
	
	// SERVIZIO APPLICATIVO
	/**
	 * Restituisce il servizio applicativo
	 *
	 * @param idServizioApplicativo Identificativo del servizio applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param aUser User utilizzato nell'header HTTP Authentication.
	 * @param aPassword Password utilizzato nell'header HTTP Authentication.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param aUser User utilizzato come appId o all'interno del token
	 * @param aPassword Password presente all'interno del token
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param subject Subject utilizzato nella connessione HTTPS.
	 * @param issuer Issuer utilizzato nella connessione HTTPS.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String subject, String issuer) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 * @param certificate certificato utilizzato nella connessione HTTPS.
	 * @param strictVerifier indicazione se deve essere effettuata una ricerca stringente su tutti i parametri del certificato
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 */
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param principal User Principal
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce la lista degli identificativi dei servizi applicativi
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei servizi applicativi
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(
			FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	
	
	
	
	
	
	// CONFIGURAZIONE
	/**
	 * Restituisce la RoutingTable definita nella Porta di Dominio 
	 *
	 * @return RoutingTable
	 * 
	 */
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException;
	
	/**
	 * Restituisce l'accesso al registro definito nella Porta di Dominio 
	 *
	 * @return AccessoRegistro
	 * 
	 */
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;

	/**
	 * Restituisce l'accesso alla configurazione definito nella Porta di Dominio 
	 *
	 * @return AccessoConfigurazione
	 * 
	 */
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce l'accesso ai dati di autorizzazione definiti nella Porta di Dominio 
	 *
	 * @return AccessoDatiAutorizzazione
	 * 
	 */
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce l'accesso ai dati di autenticazione definiti nella Porta di Dominio 
	 *
	 * @return AccessoDatiAutenticazione
	 * 
	 */
	public AccessoDatiAutenticazione getAccessoDatiAutenticazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce l'accesso ai dati di gestione token
	 *
	 * @return AccessoDatiGestioneToken
	 * 
	 */
	public AccessoDatiGestioneToken getAccessoDatiGestioneToken() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce l'accesso ai dati per la gestione dei keystore
	 *
	 * @return AccessoDatiKeystore
	 * 
	 */
	public AccessoDatiKeystore getAccessoDatiKeystore() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;

	/**
	 * Restituisce l'accesso ai dati per la gestione della consegna agli applicativi
	 *
	 * @return AccessoDatiKeystore
	 * 
	 */
	public AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la servizio sui ServiziAttivi definiti nella Porta di Dominio 
	 *
	 * @return ServiziAttivi
	 * 
	 */
	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le proprieta' di sistema utilizzate dalla PdD
	 *
	 * @return proprieta' di sistema
	 * 
	 */
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	public List<GenericProperties> getGenericProperties() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	public GenericProperties getGenericProperties(String tipologia, String name) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la configurazione generale della Porta di Dominio 
	 *
	 * @return Configurazione
	 * 
	 */
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;

}
