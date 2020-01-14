/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.utils.TipiDatabase;

/**
 * Contiene la definizione di un KeyGenerator per Derby
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DerbyKeyGenerator extends AbstractNoReturnGeneratedKeyGenerator {

	public DerbyKeyGenerator(Connection connection,IKeyGeneratorObject type) throws KeyGeneratorException {
		super(connection,type);
	}
	

	@Override
	public String getSequenceQuery() throws KeyGeneratorException {
		String sequence = null;
		String table = null;
		switch (this.type.getType()) {
		case DEFAULT:
			sequence = ((DefaultKeyGeneratorObject)this.type).getPrefix()+"_id_seq";
			table = ((DefaultKeyGeneratorObject)this.type).getTable()+"_init_seq"; // per audit prefix e table sono diverse!
			break;
		case CUSTOM:
			CustomKeyGeneratorObject c = (CustomKeyGeneratorObject)this.type; 
			if(c.existsCustomTableObjectsForDatabaseType(TipiDatabase.DERBY)){
				sequence = c.getCustomTableObjectsForDatabaseType(TipiDatabase.DERBY).getSequenceName();
				table = c.getCustomTableObjectsForDatabaseType(TipiDatabase.DERBY).getTableNameForInitSequence();
			}
			// else verifico eventualmente anche se esiste per hsql
			else if(c.existsCustomTableObjectsForDatabaseType(TipiDatabase.HSQL)){
				sequence = c.getCustomTableObjectsForDatabaseType(TipiDatabase.HSQL).getSequenceName();
				table = c.getCustomTableObjectsForDatabaseType(TipiDatabase.HSQL).getTableNameForInitSequence();
			}else{
				sequence = c.getDefaultTableObjects().getSequenceName();
				table = c.getDefaultTableObjects().getTableNameForInitSequence();
			}
			break;
		default:
			throw new KeyGeneratorException("Tipo di KeyGeneratorObjects non gestito: "+this.type);
		}
		return "SELECT NEXT VALUE FOR  "+sequence+"  as nextval from "+table;
	}
	
}
