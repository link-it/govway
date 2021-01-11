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

package org.openspcoop2.core.transazioni.utils;

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

	public static final String ALIAS_PREFIX = "op2T";
	
	public static List<String> addFromTable(IExpression expression, ISQLQueryObject sqlQueryObject,ISQLFieldConverter fieldConverter) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException{
		List<IField> iFields = expression.getFields(true);
		List<String> tabelleAggiunteAlias = new ArrayList<String>();
		if(iFields!=null && iFields.size()>0){
			for (IField iField : iFields) {
				if(iField instanceof IAliasTableField){
					IAliasTableField af = (IAliasTableField) iField;
					//System.out.println("CHECK ALIAS ["+af.getAliasTable()+"] ["+af.getFieldName()+"] ...");
					if(org.openspcoop2.core.transazioni.Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME.equals(af.getField())){
						String aliasTabella = af.getAliasTable();
						//System.out.println("CHECK ALIAS DENTRO ["+af.getAliasTable()+"] ["+af.getFieldName()+"] ...");
						if(tabelleAggiunteAlias.contains(aliasTabella)==false){
							//Lo fa gia in automatico la gestione dell'Expression
							//sqlQueryObject.addFromTable(fieldConverter.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO), aliasTabella);
							//System.out.println("CHECK ALIAS ADD ["+af.getAliasTable()+"] ["+af.getFieldName()+"]");
							tabelleAggiunteAlias.add(aliasTabella);
						}
					}
				}
			}
		}
		return tabelleAggiunteAlias;
	}
	
}
