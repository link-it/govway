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



package org.openspcoop2.pdd.monitor.driver;

import java.util.List;

import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.StatoPdd;


/**
 * Interfaccia per il monitoraggio della porta di dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IDriverMonitoraggio {

	/**
	 * Ritorna lo stato delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return stato delle richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	public StatoPdd getStatoRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException;
    
    /**
     * Ritorna il numero delle richieste pendenti che matchano il criterio di filtro.
     * 
     * @param search criterio di filtro
     * @return numero di richieste pendenti
     * @throws DriverMonitoraggioException
     */
    public long countListaRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException;
	
    /**
     * Ritorna I dettagli dei messaggi delle richieste pendenti che matchano il criterio di filtro.
     * 
     * @param search criterio di filtro
     * @return dettagli dei messaggi delle richieste pendenti
     * @throws DriverMonitoraggioException
     */
    public List<Messaggio> getListaRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException;
    
	/**
     * Ritorna e cancella i dettagli dei messaggi delle richieste pendenti che matchano il criterio di filtro.
     * 
     * @param search criterio di filtro
     * @return numero dei messaggi delle richieste pendenti eliminate
     * @throws DriverMonitoraggioException
     */
    public long deleteRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException;
	
}
