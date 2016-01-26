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

import org.openspcoop2.utils.TipiDatabase;

/**
 * Contiene la definizione di un KeyGenerator per Postgresql
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PostgreSQLKeyGenerator extends AbstractNoReturnGeneratedKeyGenerator {

	public PostgreSQLKeyGenerator(Connection connection,IKeyGeneratorObject type) throws KeyGeneratorException {
		super(connection,type);
	}
	

	@Override
	public String getSequenceQuery() throws KeyGeneratorException {
		String sequence = null;
		switch (this.type.getType()) {
		case DEFAULT:
			sequence = ((DefaultKeyGeneratorObject)this.type).getPrefix()+"_id_seq";
			break;
		case CUSTOM:
			CustomKeyGeneratorObject c = (CustomKeyGeneratorObject)this.type; 
			if(c.existsCustomTableObjectsForDatabaseType(TipiDatabase.POSTGRESQL)){
				sequence = c.getCustomTableObjectsForDatabaseType(TipiDatabase.POSTGRESQL).getSequenceName();
			}else{
				sequence = c.getDefaultTableObjects().getSequenceName();
			}
			break;
		default:
			throw new KeyGeneratorException("Tipo di KeyGeneratorObjects non gestito: "+this.type);
		}
		return "select nextval('"+sequence+"'::text) as nextval";
	}
	
}
