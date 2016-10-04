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




package org.openspcoop2.core.registry.driver;

import java.util.List;
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
 * Interfaccia per la ricerca informazioni di soggetti registrati in un
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

public interface IDriverRegistroServiziGet extends IBeanUtilities{

	/* Accordi di Cooperazione */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoCooperazione}, 
	 * identificato grazie al parametro 
	 * <var>nomeAccordo</var> 
	 *
	 * @param idAccordo Identificativo dell'accordo di Cooperazione
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.AccordoCooperazione}.
	 * 
	 */
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	/**
	 * Ritorna gli identificatori degli accordi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(
			FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
	
	
	
	
	
	
	
	/* Accordi di Servizio Parte Comune */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteComune}, 
	 * identificato grazie al parametro 
	 * <var>nomeAccordo</var> 
	 *
	 * @param idAccordo Identificativo dell'accordo di Servizio
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteComune}.
	 * 
	 */
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	/**
	 * Ritorna gli identificatori degli accordi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDAccordo> getAllIdAccordiServizioParteComune(
			FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
	
	
	
	
	
	
	
	
	/* Porte di Dominio */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.PortaDominio}, 
	 * identificato grazie al parametro 
	 * <var>nomePdD</var> 
	 *
	 * @param nomePdD Nome della Porta di Dominio
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.PortaDominio}.
	 * 
	 */
	public PortaDominio getPortaDominio(
			String nomePdD) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	/**
	 * Ritorna gli identificatori delle PdD che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<String> getAllIdPorteDominio(
			FiltroRicerca filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
	
	
	
	
	
	
	/* Soggetti */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Soggetto}, 
	 * identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}. 
	 *
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.Soggetto} .
	 * 
	 */
	public Soggetto getSoggetto(
			IDSoggetto idSoggetto) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	
	/**
	 *  Ritorna gli identificatori dei soggetti che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei soggetti trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDSoggetto> getAllIdSoggetti(
			FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
	
	
	
	
	/* Accordi di Servizio Parte Specifica */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  identificato grazie ai fields Soggetto,
	 * 'Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idServizio</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 *
	 * @param idServizio Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}.
	 * 
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita'  associate
	 * al servizio  identificato grazie ai fields Soggetto,
	 * 'Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idAccordo</var> di tipo {@link org.openspcoop2.core.id.IDAccordo}. 
	 *
	 * @param idAccordo Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDAccordo}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}.
	 * 
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  correlato identificato grazie ai fields Soggetto
	 * e nomeAccordo
	 * 
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordoServizioParteComune ID dell'accordo che deve implementare il servizio correlato
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}.
	 * 
	 */
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(
			IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;

	/**
	 *  Ritorna gli identificatori dei servizi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei servizi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(
			FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
		
	/**
	 *  Ritorna gli identificatori dei servizi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei servizi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDServizio> getAllIdServizi(
			FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound;
	
}
