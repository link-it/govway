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




package org.openspcoop2.core.registry.driver;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;


/**
 * Interfaccia per la gestione CRUD di soggetti registrati in un
 * registro dei servizi. I driver che implementano l'interfaccia 
 * sono attualmente:
 * <ul>
 * <li> {@link org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI}, interroga un registro dei servizi UDDI.
 * <li> {@link org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB}, interroga un registro dei servizi Relazionale.
 * <li> {@link org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB}, interroga un registro dei servizi Web.
 * <li> {@link org.openspcoop2.core.registry.driver.ws.DriverRegistroServiziWS}, interroga un registro dei servizi WS.
 * <li> {@link org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML}, interroga un registro dei servizi XML.
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDriverRegistroServiziCRUD {

	
	
	/**
	 * Crea un nuovo AccordoCooperazione
	 * 
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di un accordo registrato.
     *
     * @param idAccordo dell'accordo da verificare
     * @return true se l'accordo esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException;
	
	/**
	 * Aggiorna l'AccordoCooperazione con i nuovi valori.
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	public void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException;
	
	/**
	 * Elimina un AccordoCooperazione 
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	public void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException;
	
	
	
	
	
	
	
	/**
	 * Crea un nuovo AccordoServizioParteComune 
	 * 
	 * @param accordoServizioParteComune
	 * @throws DriverRegistroServiziException
	 */
	public void createAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di un accordo registrato.
     *
     * @param idAccordo dell'accordo da verificare
     * @return true se l'accordo esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException;
	
	/**
	 * Aggiorna l'AccordoServizioParteComune con i nuovi valori.
	 *  
	 * @param accordoServizioParteComune
	 * @throws DriverRegistroServiziException
	 */
	public void updateAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException;
	
	/**
	 * Elimina un AccordoServizioParteComune 
	 *  
	 * @param accordoServizioParteComune
	 * @throws DriverRegistroServiziException
	 */
	public void deleteAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune) throws DriverRegistroServiziException;
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Crea una nuova Porta di Dominio 
	 * 
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	public void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di una Porta di Dominio.
     *
     * @param nome della porta di dominio da verificare
     * @return true se la porta di dominio esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsPortaDominio(String nome) throws DriverRegistroServiziException;
	
	/**
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException;
	
	/**
	 * Elimina una Porta di Dominio 
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	public void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException;
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	public void createSoggetto(Soggetto soggetto) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di un soggetto registrato.
     *
     * @param idSoggetto Identificativo del soggetto
     * @return true se il soggetto esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException;
    
	/**
	 * Aggiorna un Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	public void updateSoggetto(Soggetto soggetto) throws DriverRegistroServiziException;
	
	/**
	 * Cancella un Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	public void deleteSoggetto(Soggetto soggetto) throws DriverRegistroServiziException;
	
	
	
	
	
	
	
	/**
	 * Crea un AccordoServizioParteSpecifica
	 * 
	 * @param accordoServizioParteSpecifica
	 * @throws DriverRegistroServiziException
	 */
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di un accordoServizioParteSpecifica registrato.
     *
	 * @param idAccordo Identificativo dell'accordo di cooperazione
     * @return true se il servizio esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException;
	
	/**
     * Verifica l'esistenza di un accordoServizioParteSpecifica registrato.
     *
	 * @param idServizio Identificativo del servizio
     * @return true se il servizio esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
     */    
    public boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException;
    
	/**
	 * Aggiorna un accordoServizioParteSpecifica
	 * 
	 * @param accordoServizioParteSpecifica
	 * @throws DriverRegistroServiziException
	 */
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException;
	
	/**
	 * Cancella un AccordoServizioParteSpecifica
	 * 
	 * @param accordoServizioParteSpecifica
	 * @throws DriverRegistroServiziException
	 */
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException;
	
	
	
	
	//RESET
	public void reset() throws DriverRegistroServiziException;
	
}
