/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Contiene la definizione di un KeyGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractNoReturnGeneratedKeyGenerator implements IKeyGenerator {

	protected Connection connection;
	protected IKeyGeneratorObject type;
	protected long idTable;
	
	public AbstractNoReturnGeneratedKeyGenerator(Connection connection,IKeyGeneratorObject type) throws KeyGeneratorException {
		this.connection = connection;
		this.type = type;
	}
	
	@Override
	public boolean isReturnGeneratedKeySupported(){
		return false;
	}
	
	@Override
	public boolean useReturnGeneratedKeyColumnNameInResultSet() {
		return false;
	}
	
	@Override
	public String getColunmKeyName() throws KeyGeneratorException {
		switch (this.type.getType()) {
			case DEFAULT:
				return "id";
			case CUSTOM:
				return ((CustomKeyGeneratorObject)this.type).getColumnNameId();
			default:
				throw new KeyGeneratorException("Tipo di KeyGeneratorObjects non gestito: "+this.type);
		}
	}
	
	public abstract String getSequenceQuery() throws KeyGeneratorException;
	
	@Override
	public long generateKey() throws KeyGeneratorException{
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			String sequenceQuery =  getSequenceQuery();
			stmt = this.connection.prepareStatement(sequenceQuery);
			rs = stmt.executeQuery();
			if(rs.next()==false){
				throw new Exception("ID autoincrementale non ottenuto via Sequence");
			}
			this.idTable = rs.getLong("nextval");
			if(this.idTable<=0){
				throw new Exception("ID autoincrementale non ottenuto: is null?");
			}
			return this.idTable;
		}catch(Exception e){
			throw new KeyGeneratorException("Errore durante la generazione della chiave: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				stmt.close();
			}catch(Exception eClose){
				// close
			}
		}
	}
	
	@Override
	public long getReturnGeneratedKey(PreparedStatement stmt) throws KeyGeneratorException{
		return this.idTable;
	}
	
}
