/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.util.List;

import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;

/**
 * Interfaccia di ricerca dei messaggi diagnostici
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDiagnosticDriver extends IComponentFactory {

	
	
	/* *********** ACCESSI TRAMITE RICERCHE (DIAGNOSTICI) ******* */
	
	/**
	 * Si occupa di ritornare il numero di diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	public int countMessaggiDiagnostici(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException;
	
	/**
	 * Si occupa di ritornare i diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	public List<MsgDiagnostico> getMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filtro)  
		throws DriverMsgDiagnosticiException, DriverMsgDiagnosticiNotFoundException;
	
	
	/**
	 * Si occupa di eliminare i diagnostici che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di diagnostici eliminati
	 * @throws DriverTracciamentoException
	 */
	public int deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter) throws DriverMsgDiagnosticiException;
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	public void close() throws DriverMsgDiagnosticiException;
}


