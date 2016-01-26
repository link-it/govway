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



package org.openspcoop2.protocol.engine.driver.repository;

import org.openspcoop2.protocol.sdk.ProtocolException;



/**
 * Classe utilizzata per accedere ai flag di accesso al repository da parte di:
 *  HISTORY: Busta usata per funzionalita di confermaRicezione(OUTBOX)/FiltroDuplicati(INBOX)
 *  PROFILI: Busta usata per funzionalita di profili di collaborazione
 *  PDD:     Busta usata eventualmente da un PdD
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRepositoryDefault implements IGestoreRepository{

	
	/**
	 * Imposta la modalita' di accesso per l'history
	 * 
	 * @param value
	 */
	@Override
	public String createSQLSet_History(boolean value) throws ProtocolException{
		if(value)
			return "HISTORY=1";
		else
			return "HISTORY=0";
	}
	
	/**
	 * Imposta la modalita' di accesso per i profili di collaborazione
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "PROFILO=1";
		else
			return "PROFILO=0";
	}
	
	/**
	 * Imposta la modalita' di accesso per una pdd
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_PdD(boolean value) throws ProtocolException{
		if(value)
			return "PDD=1";
		else
			return "PDD=0";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_History(boolean value) throws ProtocolException{
		if(value)
			return "HISTORY=1";
		else
			return "HISTORY=0";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "PROFILO=1";
		else
			return "PROFILO=0";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_PdD(boolean value) throws ProtocolException{
		if(value)
			return "PDD=1";
		else
			return "PDD=0";
	}
	

	@Override
	public String createSQLCondition_enableOnlyHistory() throws ProtocolException{
		return "( HISTORY=1 AND PROFILO=0 AND PDD=0 )";
	}
	

	@Override
	public String createSQLCondition_enableOnlyPdd() throws ProtocolException{
		return "( HISTORY=0 AND PROFILO=0 AND PDD=1 )";
	}
       

	@Override
	public String createSQLCondition_enableOnlyProfilo() throws ProtocolException{
		return "( HISTORY=0 AND PROFILO=1 AND PDD=0 )";
	}
	

	@Override
	public String createSQLCondition_enableOnlyPddAndProfilo() throws ProtocolException{
		return "( HISTORY=0 AND PROFILO=1 AND PDD=1 )";
	}
	

	@Override
	public String createSQLCondition_disabledAll() throws ProtocolException{
		return "( HISTORY=0 AND PROFILO=0 AND PDD=0 )";
	}

	
	/**
	 * Ritorna il valore da associare al field che gestisce l'History
	 * 
	 * @return SQLField Value
	 * @throws ProtocolException
	 */
	@Override
	public String getSQLValueHistory(boolean history) throws ProtocolException{
		if(history)
			return "1";
		else
			return "0";
	}
	
	/**
	 * Ritorna i field che gestiscono la modalita di accesso al Repository
	 * 
	 * @return SQLField
	 * @throws ProtocolException
	 */
	@Override
	public String createSQLFields() throws ProtocolException{
		return "HISTORY,PROFILO,PDD";
	}
	
	/**
	 * Ritorna il field che gestisce la modalita di accesso all'History flag
	 * 
	 * @return SQL Field History
	 * @throws ProtocolException
	 */
	@Override
	public String createSQLFieldHistory() throws ProtocolException{
		return "HISTORY";
	}
}
