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


package org.openspcoop2.core.config.driver;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;

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
	 * Restituisce Il soggetto che include la porta delegata identificata da <var>location</var>
	 *
	 * @param location Location che identifica una porta delegata
	 * @return Il Soggetto che include la porta delegata fornita come parametro.
	 */
	public Soggetto getSoggetto(String location) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	
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
	public HashSet<String> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei servizi associati a soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	public HashSet<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
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
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, viene ricercata
	 * una Porta Applicativa che non possegga l'azione
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA,
			boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	/** Restituisce una porta applicativa in base al nome della porta e il soggetto proprietario della porta*/
	
	public PortaApplicativa getPortaApplicativa(
			IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	public PortaApplicativa getPortaApplicativa(
			String nomePorta, 
			IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * @param idPA
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale,boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa) 
	 * che possiedono il soggetto SoggettoVirtuale identificato da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa con soggetto Virtuale
	 * @return una porta applicativa
	 * 
	 */
	public 
		Hashtable<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce la lista delle porte applicative con il nome fornito da parametro.
	 * Possono esistere piu' porte applicative con medesimo nome, che appartengono a soggetti differenti.
	 * Se indicati i parametri sui soggetti vengono utilizzati come filtro per localizzare in maniera piu' precisa la PA
	 * 
	 * @param nomePA Nome di una Porta Applicativa
	 * @param tipoSoggettoProprietario Tipo del Soggetto Proprietario di una Porta Applicativa
	 * @param nomeSoggettoProprietario Nome del Soggetto Proprietario di una Porta Applicativa
	 * @return La lista di porte applicative
	 * 
	 */
	public List<PortaApplicativa> getPorteApplicative(
			String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce la lista degli identificativi delle porte applicative
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte applicative
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	public List<IDPortaApplicativaByNome> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
	
	
	
	
	// SERVIZIO APPLICATIVO
	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta delegata <var>location</var>.
	 * Se nella porta delegata non vi e' viene cercato 
	 * poi in un specifico soggetto se specificato con <var>aSoggetto</var>, altrimenti in ogni soggetto. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	public ServizioApplicativo getServizioApplicativo(IDPortaDelegata idPD,String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta applicativa <var>location</var>
	 * e poi nel soggetto <var>aSoggetto</var>. 
	 *
	 * @param idPA Identificatore della porta applicativa.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	public ServizioApplicativo getServizioApplicativo(IDPortaApplicativa idPA,String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aUser User utilizzato nell'header HTTP Authentication.
	 * @param aPassword Password utilizzato nell'header HTTP Authentication.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aUser,String aPassword) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	public ServizioApplicativo getServizioApplicativoAutenticato(String aUser,String aPassword) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;

	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aSubject Subject utilizzato nella connessione HTTPS.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	public ServizioApplicativo getServizioApplicativoAutenticato(String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound;
	
	/**
     * Verifica l'esistenza di un servizio applicativo.
     *
     * @param idSoggetto id del soggetto proprietario
     * @param nomeServizioApplicativo nome del servizio applicativo
     * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
     */    
	public ServizioApplicativo getServizioApplicativo(IDSoggetto idSoggetto,String nomeServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
    /**
     * Verifica l'esistenza di un servizio applicativo.
     *
     * @param idServizioApplicativo id del servizio applicativo
     * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
     */    
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException, DriverConfigurazioneNotFound;
	
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
	 * Restituisce la configurazione generale della Porta di Dominio 
	 *
	 * @return Configurazione
	 * 
	 */
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException, DriverConfigurazioneNotFound;

}
