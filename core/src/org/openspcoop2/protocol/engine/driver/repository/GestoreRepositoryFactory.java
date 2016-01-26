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

import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.TipiDatabase;

/**
 * Factory degli oggetti SQLQueryObject
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreRepositoryFactory {

	public static IGestoreRepository createRepositoryBuste(TipiDatabase tipoDatabase) throws ProtocolException {
		return createRepositoryBuste(tipoDatabase.toString());
	}
	public static IGestoreRepository createRepositoryBuste(String tipoDatabase) throws ProtocolException {
		IGestoreRepository repositoryBuste = null;
		
		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryBytewise();
		}else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryBytewise();
		}else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryOracle();
		}else if (TipiDatabase.HSQL.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryBitOrAndFunction();
		}else if (TipiDatabase.SQLSERVER.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryBytewise();
		}else if (TipiDatabase.DB2.equals(tipoDatabase)) {
			repositoryBuste = new GestoreRepositoryBitOrAndFunction();
		}else{
			throw new ProtocolException("Tipo database non gestito ["+tipoDatabase+"]");
		}
	
		return repositoryBuste;
	}
	
	public static String getTipoRepositoryBuste(TipiDatabase tipoDatabase) throws ProtocolException {
		return getTipoRepositoryBuste(tipoDatabase.toString());
	}
	public static String getTipoRepositoryBuste(String tipoDatabase) throws ProtocolException {
		String repositoryBuste = null;
		
		if (TipiDatabase.POSTGRESQL.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE;
		}else if (TipiDatabase.MYSQL.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE;
		}else if (TipiDatabase.ORACLE.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE_ORACLE;
		}else if (TipiDatabase.HSQL.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BIT_OR_AND_FUNCTION;
		}else if (TipiDatabase.SQLSERVER.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BYTEWISE;
		}else if (TipiDatabase.DB2.equals(tipoDatabase)) {
			repositoryBuste = CostantiConfigurazione.REPOSITORY_BUSTE_BIT_OR_AND_FUNCTION;
		}else{
			throw new ProtocolException("Tipo database non gestito ["+tipoDatabase+"]");
		}

		return repositoryBuste;
	}

}
