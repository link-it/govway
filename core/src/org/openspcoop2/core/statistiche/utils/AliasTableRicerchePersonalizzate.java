/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.statistiche.model.StatisticaContenutiModel;
import org.openspcoop2.core.statistiche.model.StatisticaModel;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**     
 * AliasTableRicerchePersonalizzate
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AliasTableRicerchePersonalizzate {

	public static final String ALIAS_PREFIX = "op2S";
	
	private static List<String> addFromTable(IExpression expression, ISQLQueryObject sqlQueryObject,
			ISQLFieldConverter fieldConverter,StatisticaContenutiModel contenutiModel) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException{
		List<IField> iFields = expression.getFields(true);
		List<String> tabelleAggiunteAlias = new ArrayList<String>();
		if(iFields!=null && iFields.size()>0){
			for (IField iField : iFields) {
				if(iField instanceof IAliasTableField){
					IAliasTableField af = (IAliasTableField) iField;
					String aliasTabella = null;
					
					if(contenutiModel.FILTRO_NOME_1.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_2.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_3.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_4.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_5.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_6.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_7.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_8.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_9.equals(af.getField()) ||
							contenutiModel.FILTRO_NOME_10.equals(af.getField()) ){
						aliasTabella = af.getAliasTable();
					}
					
					if(aliasTabella!=null){
						if(tabelleAggiunteAlias.contains(aliasTabella)==false){
							//sqlQueryObject.addFromTable(fieldConverter.toTable(contenutiModel), aliasTabella);
							//Lo fa gia in automatico la gestione dell'Expression
							tabelleAggiunteAlias.add(aliasTabella);
						}
					}
					
				}
			}
		}
		return tabelleAggiunteAlias;
	}
	
	
	public static void join(IExpression expression, ISQLQueryObject sqlQueryObject, StatisticaModel model,StatisticaContenutiModel modelContenuti,
			ISQLFieldConverter sqlFieldConverter) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException{
		List<String> tableWithAlias = AliasTableRicerchePersonalizzate.addFromTable(expression, sqlQueryObject, sqlFieldConverter,modelContenuti);
		if(tableWithAlias!=null && tableWithAlias.size()>0){
			String tableName2 = sqlFieldConverter.toTable(model);
			for (String aliasTable : tableWithAlias) {
				sqlQueryObject.addWhereCondition(aliasTable+".id_stat="+tableName2+".id");	
			}
		}
		//else{
		if(expression.inUseModel(modelContenuti,false)){
			String tableName1 = sqlFieldConverter.toTable(modelContenuti);
			String tableName2 = sqlFieldConverter.toTable(model);
			sqlQueryObject.addWhereCondition(tableName1+".id_stat="+tableName2+".id");
		}
		//}
        
	}
}
