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



package org.openspcoop2.protocol.sdk.tracciamento;

import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;



/**
 * Interfaccia Tracciamento
 *
 * @author Stefano Corallo <corallo@link.it>
* @author $Author$
* @version $Rev$, $Date$
 */
public interface IDriverTracciamento {

	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
		
	public IProtocolFactory getProtocolFactory();

	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE ******* */
	
	/**
	 * Si occupa di ritornare il numero di tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di tracce che rispettano il filtro di ricerca
	 * 
	 */
	public int countTracce(FiltroRicercaTracce filtro) throws DriverTracciamentoException;
	
	/**
	 * Si occupa di ritornare le tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return tracce che rispettano il filtro di ricerca
	 * 
	 */
	public List<Traccia> getTracce(FiltroRicercaTracceConPaginazione filtro)  throws DriverTracciamentoException, DriverTracciamentoNotFoundException;
	
	
	/**
	 * Si occupa di eliminare le tracce che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di tracce eliminate
	 * @throws DriverTracciamentoException
	 */
	public int deleteTracce(FiltroRicercaTracce filter) throws DriverTracciamentoException;
	
	
	
	
	
	
	
	
	/* ******* ACCESSI PUNTUALI ********** */
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta) throws DriverTracciamentoException, DriverTracciamentoNotFoundException; 
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta,boolean ricercaIdBustaComeRiferimentoMessaggio) throws DriverTracciamentoException, DriverTracciamentoNotFoundException; 
	
	/**
	 * Recupera la traccia in base ad una serie di properties
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	public Traccia getTraccia(TipoTraccia tipoTraccia,Hashtable<String, String> propertiesRicerca) throws DriverTracciamentoException, DriverTracciamentoNotFoundException; 
	
	
	

	
	
	/* ******* RISORSE INTERNE ********** */
	
	public void close() throws DriverTracciamentoException;
}


