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

import org.openspcoop2.utils.TipiDatabase;

/**
 * Contiene la definizione di un KeyGenerator per Oracle
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OracleKeyGenerator extends AbstractNoReturnGeneratedKeyGenerator {

	public OracleKeyGenerator(Connection connection,IKeyGeneratorObject type) throws KeyGeneratorException {
		super(connection,type);
	}
	

	@Override
	public String getSequenceQuery() throws KeyGeneratorException {
		String sequence = null;
		switch (this.type.getType()) {
		case DEFAULT:
			sequence = ((DefaultKeyGeneratorObject)this.type).getPrefix()+"_sequence";
			break;
		case CUSTOM:
			CustomKeyGeneratorObject c = (CustomKeyGeneratorObject)this.type; 
			if(c.existsCustomTableObjectsForDatabaseType(TipiDatabase.ORACLE)){
				sequence = c.getCustomTableObjectsForDatabaseType(TipiDatabase.ORACLE).getSequenceName();
			}else{
				sequence = c.getDefaultTableObjects().getSequenceName();
			}
			break;
		default:
			throw new KeyGeneratorException("Tipo di KeyGeneratorObjects non gestito: "+this.type);
		}
		return "select "+sequence+".nextval as nextval from dual";
	}
	
}
