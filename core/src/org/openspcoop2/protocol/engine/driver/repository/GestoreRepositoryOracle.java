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
public class GestoreRepositoryOracle implements IGestoreRepository{

	// Nota: con Oracle viene utilizzato il 2,3 e 4 bit, quindi i valori 2, 4 e 8
	// Bit 2^1 = PDD
	// Bit 2^2 = PROFILO
	// Bit 2^3 = HISTORY
	
	private static final String OR_MASK_PDD = "'C103'"; // number:2, mask-bit 001 da utilizzare con OR
	private static final String OR_MASK_PROFILO = "'C105'"; // number:4, mask-bit 010 da utilizzare con OR
	private static final String OR_MASK_HISTORY = "'C109'"; // number:8, mask-bit 100 da utilizzare con OR
	private static final String AND_MASK_PDD = "'C10D'"; // number:12, mask-bit 110 da utilizzare con AND
	private static final String AND_MASK_PROFILO = "'C10B'"; // number:10, mask-bit 1.4 da utilizzare con AND
	private static final String AND_MASK_HISTORY = "'C107'"; // number:6, mask-bit 011 da utilizzare con AND
	
	// Tabella dei valori
	private static final String HISTORY_PROFILO_PDD = "'C10F'";  // number:14, bit: 111
	private static final String HISTORY_PROFILO = "'C10D'";  // number:12, bit: 110
	private static final String HISTORY_PDD = "'C10B'";  // number:10, bit: 101
	private static final String PROFILO_PDD = "'C107'";  // number:6, bit: 011
	private static final String HISTORY = "'C109'"; // number:8, bit: 100
	private static final String PROFILO = "'C105'"; // number:4, bit: 010
	private static final String PDD = "'C103'"; // number:2, bit: 001
	private static final String NONE = "'C101'"; // number:0, bit: 000
	
	/** Query di utility:
	 * 
	 * CREATE TABLE TEST
	 * (
	 *     TIPO VARCHAR(255) NOT NULL,
	 *     T1 RAW(8) DEFAULT 'C101' NOT NULL
	 * );
	 * 
	 * INSERT INTO TEST (TIPO) VALUES ('TIPO');
	 * 
	 * select TIPO,T1,utl_raw.CAST_TO_NUMBER(T1) from TEST;
	 * 
	 * select TIPO,T1,utl_raw.CAST_TO_NUMBER(T1) from TEST WHERE T1 ='C101';
	 * 
	 * UPDATE TEST SET t1 = (utl_raw.bit_or(t1,utl_raw.CAST_FROM_NUMBER(2)))
	 * 
	 * UPDATE TEST SET t1 = 'C101';
	 * 
	 * */
	
	/**
	 * Imposta la modalita' di accesso per l'history
	 * 
	 * @param value
	 */
	@Override
	public String createSQLSet_History(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (utl_raw.bit_or(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.OR_MASK_HISTORY+"))";
		else
			return "REPOSITORY_ACCESS = (utl_raw.bit_and(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.AND_MASK_HISTORY+"))";
	}
	
	/**
	 * Imposta la modalita' di accesso per i profili di collaborazione
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (utl_raw.bit_or(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.OR_MASK_PROFILO+"))";
		else
			return "REPOSITORY_ACCESS = (utl_raw.bit_and(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.AND_MASK_PROFILO+"))";
	}
	
	/**
	 * Imposta la modalita' di accesso per una pdd
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_PdD(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (utl_raw.bit_or(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.OR_MASK_PDD+"))";
		else
			return "REPOSITORY_ACCESS = (utl_raw.bit_and(REPOSITORY_ACCESS,"+GestoreRepositoryOracle.AND_MASK_PDD+"))";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_History(boolean value) throws ProtocolException{
		
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY+" )";
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.NONE+" )";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO+" )";
		
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.NONE+" )";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_PdD(boolean value) throws ProtocolException{
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PDD+" )";
		
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY_PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryOracle.NONE+" )";
	}


	@Override
	public String createSQLCondition_enableOnlyHistory() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.HISTORY+")";
	}
       

	@Override
	public String createSQLCondition_enableOnlyPdd() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.PDD+")";
	}
	

	@Override
	public String createSQLCondition_enableOnlyProfilo() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO+")";
	}
	

	@Override
	public String createSQLCondition_enableOnlyPddAndProfilo() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.PROFILO_PDD+")";
	}
	

	@Override
	public String createSQLCondition_disabledAll() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryOracle.NONE+")";
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
			return GestoreRepositoryOracle.HISTORY+"";
		else
			return GestoreRepositoryOracle.NONE+"";
	}
	
	/**
	 * Ritorna i field che gestiscono la modalita di accesso al Repository
	 * 
	 * @return SQLField
	 * @throws ProtocolException
	 */
	@Override
	public String createSQLFields() throws ProtocolException{
		return "REPOSITORY_ACCESS";
	}
	
	/**
	 * Ritorna il field che gestisce la modalita di accesso all'History flag
	 * 
	 * @return SQL Field History
	 * @throws ProtocolException
	 */
	@Override
	public String createSQLFieldHistory() throws ProtocolException{
		return "REPOSITORY_ACCESS";
	}
}
