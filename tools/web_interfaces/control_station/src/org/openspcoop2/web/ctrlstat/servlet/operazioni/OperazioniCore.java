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
package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.queue.ClassQueue;
import org.openspcoop2.web.lib.queue.ClassQueueException;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.Operation;

/**
 * OperazioniCore
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniCore extends ControlStationCore {

	public OperazioniCore() throws Exception {
		super();
	}
	public OperazioniCore(ControlStationCore core) throws Exception {
		super(core);
	}

	public Operation getOperation(long idOperation) throws ClassQueueException {
		Connection con = null;
		String nomeMetodo = "getOperation";
		ClassQueue driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new ClassQueue(con, this.tipoDB);
			return driver.getOperation(idOperation);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new ClassQueueException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public int updateOperation(Operation operation) throws ClassQueueException {
		Connection con = null;
		String nomeMetodo = "updateOperation";
		ClassQueue driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new ClassQueue(con, this.tipoDB);
			return driver.updateOperation(operation);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new ClassQueueException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<Operation> operationsList(Search ricerca, 
			OperazioniFormBean formBean, String logAdm
			) throws ClassQueueException {
		Connection con = null;
		String nomeMetodo = "operationsList";
		ClassQueue driver = null;

		OperationStatus os = formBean.getTipoOperationStatus();
		Vector<String> utenti = formBean.getUtenti();
		ricerca.setFilter(this.getIdLista(formBean),formBean.getUtente());
		String hostname = formBean.getHostname() != null ? formBean.getHostname() : "";
		String pezzoAny = formBean.getPezzoAny()!= null ? formBean.getPezzoAny() : "";
		String daSql = formBean.getDataInizio()!= null ? formBean.getDataInizio() : "";
		String aSql = formBean.getDataFine()!= null ? formBean.getDataFine() : "";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new ClassQueue(con, this.tipoDB);
			return driver.operationsList(ricerca, os, utenti, logAdm, hostname, pezzoAny, daSql, aSql);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new ClassQueueException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}	
	public Vector<String> hostnameList() throws ClassQueueException {
		Connection con = null;
		String nomeMetodo = "hostnameList";
		ClassQueue driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new ClassQueue(con, this.tipoDB);
			return driver.hostnameList();
		} catch (Exception e) {
			ControlStationCore.log.error("[OperationsCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new ClassQueueException("[OperationsCore::" + nomeMetodo + "] Error :" + e.getMessage());
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public int getIdLista(OperazioniFormBean formBean){
		return getIdLista(formBean.getTipoOperationStatus());
	}
	
	public int getIdLista(OperationStatus os){
		int idLista = -1;
		
		if(os == null)
			return idLista;
		
		if (os.toString().equals("SUCCESS"))
			idLista = Liste.OPERATIONS_ESEGUITE;
		if (os.toString().equals("ERROR"))
			idLista = Liste.OPERATIONS_FALLITE;
		if (os.toString().equals("INVALID"))
			idLista = Liste.OPERATIONS_INVALIDE;
		if (os.toString().equals("NOT_SET"))
			idLista = Liste.OPERATIONS_CODA;
		if (os.toString().equals("WAIT"))
			idLista = Liste.OPERATIONS_WAITING;
		
		return idLista;
	}
}
