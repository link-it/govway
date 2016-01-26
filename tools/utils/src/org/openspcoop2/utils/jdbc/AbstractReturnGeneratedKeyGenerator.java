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
	public String getColunmKeyName() throws KeyGeneratorException {
		throw new KeyGeneratorException("ReturnGeneratedKey supported");
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
			this.idTraccia = rs.getLong(1);
			if(this.idTraccia<=0){
				throw new Exception("ID autoincrementale non ottenuto: is null?");
			}
			return this.idTraccia;
		}catch(Exception e){
			throw new KeyGeneratorException("Errore durante la generazione della chiave via JDBC3.0: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
		}
	}
	
}
