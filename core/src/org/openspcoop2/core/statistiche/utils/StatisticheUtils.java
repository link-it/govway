/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.statistiche.utils;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.statistiche.model.StatisticaModel;
import org.openspcoop2.generic_project.beans.FunctionField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;

/**     
 * StatisticheUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticheUtils {

	public static void selezionaRecordValidi(IExpression expr, StatisticaModel model) throws ExpressionNotImplementedException, ExpressionException{
		expr.greaterEquals(model.STATO_RECORD, CostantiDB.STATISTICHE_STATO_RECORD_VALIDO);
	}
	
	public static FunctionField calcolaMedia(ISQLFieldConverter fieldConverter, IField latenza, IField numeroRichieste, String alias) throws ExpressionException{
		// (SUM(latenza_servizio*richieste))/SUM(richieste)
		String columnLatenza = fieldConverter.toColumn(latenza, false);
		String columnRichieste = fieldConverter.toColumn(numeroRichieste, false);
		String  sbFunctionValue = getSqlCalcolaMedia(columnLatenza, columnRichieste);
		Class<?> functionValueType = Long.class;
		
		return new FunctionField(sbFunctionValue.toString(),functionValueType,"","",alias);
	}
	
	public static String getSqlCalcolaMedia(String columnLatenza, String columnRichieste) {
		StringBuilder sbFunctionValue = new StringBuilder("(");
		sbFunctionValue.append("SUM(").append(columnLatenza).append("*").append(columnRichieste).append(")");
		sbFunctionValue.append("/");
		sbFunctionValue.append("SUM(").append(columnRichieste).append(")");
		sbFunctionValue.append(")");
		return sbFunctionValue.toString();
	}
}
