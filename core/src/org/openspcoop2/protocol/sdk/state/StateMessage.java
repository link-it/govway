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

package org.openspcoop2.protocol.sdk.state;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
/**
 * Oggetto che rappresenta lo stato di una busta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StateMessage implements IState {

	/** Connessione database */
	private Connection connectionDB;

	/** Tabella Hash contenente le PreparedStatement da eseguire */
	private StateMap tablePstmt;
	
	/** Logger */
	private Logger log = null;
	
	public StateMessage(){
		
	}
	
	
	public StateMessage(Connection con, Logger log) {
		this.connectionDB = con;
		this.log = log;
		this.tablePstmt = new StateMap(log);
	}
	

	public StateMessage(Connection con, Logger log,	StateMap preparedStatement) {
		this.connectionDB = con;
		this.log = log;
		this.tablePstmt = preparedStatement;
	}


	public Logger getLog() {
		return this.log;
	}


	public void setLog(Logger log) {
		this.log = log;
	}


	public Connection getConnectionDB() {
		return this.connectionDB;
	}

	public void setConnectionDB(Connection connectionDB) {
		this.connectionDB = connectionDB;
	}

	/**
	 * Imposta la connessione.
	 *
	 * @param con Connessione al Database
	 * 
	 */
	public void updateConnection(Connection con){
		this.connectionDB = con;
	}
	
	/**
	 * Ritorna una tabella Hash contenente le preparedStatement da eseguire.
	 *
	 * @return Tabella Hash contenente le PreparedStatement da eseguire.
	 * 
	 */
	public StateMap getPreparedStatement(){
		return this.tablePstmt;
	}
	
	public void setPreparedStatement(Hashtable<String,PreparedStatement> pstm){
		this.tablePstmt.setPreparedStatement(pstm);
	}
	public void setPreparedStatement(StateMap pstm){
		this.tablePstmt = pstm;
	}
	
	
	
	/**
	 * Chiude tutte le PreparedStatement.
	 *
	 * 
	 */
	public void closePreparedStatement(){
		JDBCUtilities.closePreparedStatement(this.tablePstmt.getPreparedStatement(),this.log);
	}


	/**
	 * Esegue e Chiude tutte le PreparedStatement.
	 *
	 * 
	 */
	public void executePreparedStatement() throws UtilsException{
		JDBCUtilities.executePreparedStatement(this.tablePstmt.getPreparedStatement());
	}
	
	/**
	 * Aggiunge le pstmt alla tabella delle pstmt.
	 *
	 * @param pstmt Tabella Hash contenente le PreparedStatement da eseguire.
	 * 
	 */
	public void addPreparedStatement(Hashtable<String,PreparedStatement> pstmt)throws UtilsException{
		JDBCUtilities.addPreparedStatement(pstmt,this.tablePstmt.getPreparedStatement(),this.log);
	}
	
}
