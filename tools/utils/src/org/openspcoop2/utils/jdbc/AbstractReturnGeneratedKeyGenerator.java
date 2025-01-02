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

public abstract class AbstractReturnGeneratedKeyGenerator implements IKeyGenerator {

	protected Connection connection;
	protected IKeyGeneratorObject type;
	protected long idTraccia;
	
	public AbstractReturnGeneratedKeyGenerator(Connection connection,IKeyGeneratorObject type) throws KeyGeneratorException {
		this.connection = connection;
		this.type = type;
	}
	
	@Override
	public boolean isReturnGeneratedKeySupported(){
		return true;
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
	
	@Override
	public long generateKey() throws KeyGeneratorException{
		throw new KeyGeneratorException("ReturnGeneratedKey supported");
	}
	
	@Override
	public long getReturnGeneratedKey(PreparedStatement stmt) throws KeyGeneratorException{
		ResultSet rs = null;
		try{
			rs = stmt.getGeneratedKeys();
			if(rs.next()==false){
				throw new Exception("ID autoincrementale non ottenuto via JDBC3.0");
			}
			if(this.useReturnGeneratedKeyColumnNameInResultSet()) {
				this.idTraccia = rs.getLong(this.getColunmKeyName());
			}
			else {
				this.idTraccia = rs.getLong(1);
			}
			if(this.idTraccia<=0){
				throw new Exception("ID autoincrementale non ottenuto: is null?");
			}
			return this.idTraccia;
		}catch(Exception e){
			throw new KeyGeneratorException("Errore durante la generazione della chiave via JDBC3.0: "+e.getMessage(),e);
		}finally{
			try{
				if(rs!=null) {
					rs.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
	}
	
}
