/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.engine.driver.repository;

import org.openspcoop2.protocol.sdk.ProtocolException;


/**
 * Classe utilizzata per accedere ai flag di accesso al repository da parte di:
 *  HISTORY: Busta usata per funzionalita di confermaRicezione(OUTBOX)/FiltroDuplicati(INBOX)
 *  PROFILI: Busta usata per funzionalita di profili di collaborazione
 *  PDD:     Busta usata eventualmente da un PdD
 * 
 *
 * @author Manca Andrea (manca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreRepositoryBitOrAndFunction  implements IGestoreRepository{
	// Bit 2^0 = PDD
	// Bit 2^1 = PROFILO
	// Bit 2^2 = HISTORY
	
	private static final int OR_MASK_PDD = 1; // mask-bit 001 da utilizzare con OR
	private static final int OR_MASK_PROFILO = 2; // mask-bit 010 da utilizzare con OR
	private static final int OR_MASK_HISTORY = 4; // mask-bit 100 da utilizzare con OR
	private static final int AND_MASK_PDD = 6; // mask-bit 110 da utilizzare con AND
	private static final int AND_MASK_PROFILO = 5; // mask-bit 1.4 da utilizzare con AND
	private static final int AND_MASK_HISTORY = 3; // mask-bit 011 da utilizzare con AND
	
	// Tabella dei valori
	private static final int HISTORY_PROFILO_PDD = 7;  // 111
	private static final int HISTORY_PROFILO = 6;  // 110
	private static final int HISTORY_PDD = 5;  // 101
	private static final int PROFILO_PDD = 3;  // 011
	private static final int HISTORY = 4; // 100
	private static final int PROFILO = 2; // 010
	private static final int PDD = 1; // 001
	private static final int NONE = 0; // 000
	
	/**
	 * Imposta la modalita' di accesso per l'history
	 * 
	 * @param value
	 */
	@Override
	public String createSQLSet_History(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (bitor(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.OR_MASK_HISTORY+"))";
		else
			return "REPOSITORY_ACCESS = (bitand(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.AND_MASK_HISTORY+"))";
	}
	
	/**
	 * Imposta la modalita' di accesso per i profili di collaborazione
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (bitor(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.OR_MASK_PROFILO+"))";
		else
			return "REPOSITORY_ACCESS = (bitand(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.AND_MASK_PROFILO+"))";
	}
	
	/**
	 * Imposta la modalita' di accesso per una pdd
	 * 
	  * @param value
	 */
	@Override
	public String createSQLSet_PdD(boolean value) throws ProtocolException{
		if(value)
			return "REPOSITORY_ACCESS = (bitor(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.OR_MASK_PDD+"))";
		else
			return "REPOSITORY_ACCESS = (bitand(REPOSITORY_ACCESS,"+GestoreRepositoryBitOrAndFunction.AND_MASK_PDD+"))";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_History(boolean value) throws ProtocolException{
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY+")";
		
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.NONE+")";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_ProfiloCollaborazione(boolean value) throws ProtocolException{
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO+")";
		
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PDD+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.NONE+")";
	}
	
	/**
	 * @param value Indicazione sull'utilizzo
	 */
	@Override
	public String createSQLCondition_PdD(boolean value) throws ProtocolException{
		if(value)
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PDD+
				   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PDD+")";
		
		else
			return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY_PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY+
			   " OR REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.NONE+")";
	}


	@Override
	public String createSQLCondition_enableOnlyHistory() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.HISTORY+")";
	}
      

	@Override
	public String createSQLCondition_enableOnlyPdd() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PDD+")";
	}
	

	@Override
	public String createSQLCondition_enableOnlyProfilo() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO+")";
	}
	

	@Override
	public String createSQLCondition_enableOnlyPddAndProfilo() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.PROFILO_PDD+")";
	}
	

	@Override
	public String createSQLCondition_disabledAll() throws ProtocolException{
		return "(REPOSITORY_ACCESS="+GestoreRepositoryBitOrAndFunction.NONE+")";
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
			return GestoreRepositoryBitOrAndFunction.HISTORY+"";
		else
			return GestoreRepositoryBitOrAndFunction.NONE+"";
	}
	
	/**
	 * Ritorna i field che gestiscono la modalita di accesso al Repository
	 * 
	 * @return SQL Field
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

