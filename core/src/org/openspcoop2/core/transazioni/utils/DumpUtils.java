/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.core.transazioni.utils;

import java.util.List;

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.DumpMessaggioFieldConverter;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.utils.sql.Case;
import org.openspcoop2.utils.sql.CastColumnType;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**     
 * DumpUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpUtils {

	private static int threshold_readInMemory = -1;

	public static int getThreshold_readInMemory() {
		return threshold_readInMemory;
	}

	public static void setThreshold_readInMemory(int threshold_readInMemory) {
		DumpUtils.threshold_readInMemory = threshold_readInMemory;
	}
	
	public static final String ALIAS_BODY = "msgContentBody";
	
	public static void selectContentByThreshold(ISQLQueryObject sqlQueryObjectGet_dumpMessaggio, DumpMessaggioFieldConverter dumpMessaggioFieldConverter,
			List<JDBCObject> listJDBCObject) throws SQLQueryObjectException, ExpressionException {
		
		String columnBody = dumpMessaggioFieldConverter.toColumn(DumpMessaggio.model().BODY,true);
		
		if(threshold_readInMemory>0) {
			String columnLength = dumpMessaggioFieldConverter.toColumn(DumpMessaggio.model().CONTENT_LENGTH,true);
			ISQLQueryObject sqlQueryObjectGet_condizione = sqlQueryObjectGet_dumpMessaggio.newSQLQueryObject();
			sqlQueryObjectGet_condizione.setANDLogicOperator(true);
			sqlQueryObjectGet_condizione.addWhereIsNotNullCondition(columnLength);
			sqlQueryObjectGet_condizione.addWhereCondition(columnLength+"<?");
			
			Case caseValue = new Case(CastColumnType.NONE,"null");
			caseValue.addCase(sqlQueryObjectGet_condizione.createSQLConditions(), columnBody);
			sqlQueryObjectGet_dumpMessaggio.addSelectCaseField(caseValue, ALIAS_BODY);
			
			listJDBCObject.add(new JDBCObject(Long.valueOf(threshold_readInMemory),Long.class));
		}
		else {
			sqlQueryObjectGet_dumpMessaggio.addSelectAliasField(columnBody, ALIAS_BODY);
		}
	}
	
}
