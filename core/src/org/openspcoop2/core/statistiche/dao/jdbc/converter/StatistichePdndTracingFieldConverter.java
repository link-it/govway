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

package org.openspcoop2.core.statistiche.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.statistiche.StatistichePdndTracing;


/**     
 * StatistichePdndTracingFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatistichePdndTracingFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public StatistichePdndTracingFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public StatistichePdndTracingFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return StatistichePdndTracing.model();
	}
	
	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return this.databaseType;
	}
	


	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		// In the case of columns with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the column containing the alias
		
		if(field.equals(StatistichePdndTracing.model().DATA_TRACCIAMENTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_tracciamento";
			}else{
				return "data_tracciamento";
			}
		}
		if(field.equals(StatistichePdndTracing.model().DATA_REGISTRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_registrazione";
			}else{
				return "data_registrazione";
			}
		}
		if(field.equals(StatistichePdndTracing.model().PDD_CODICE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pdd_codice";
			}else{
				return "pdd_codice";
			}
		}
		if(field.equals(StatistichePdndTracing.model().CSV)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".csv";
			}else{
				return "csv";
			}
		}
		if(field.equals(StatistichePdndTracing.model().METHOD)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".method";
			}else{
				return "method";
			}
		}
		if(field.equals(StatistichePdndTracing.model().STATO_PDND)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato_pdnd";
			}else{
				return "stato_pdnd";
			}
		}
		if(field.equals(StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tentativi_pubblicazione";
			}else{
				return "tentativi_pubblicazione";
			}
		}
		if(field.equals(StatistichePdndTracing.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(StatistichePdndTracing.model().TRACING_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tracing_id";
			}else{
				return "tracing_id";
			}
		}
		if(field.equals(StatistichePdndTracing.model().ERROR_DETAILS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".error_details";
			}else{
				return "error_details";
			}
		}
		if(field.equals(StatistichePdndTracing.model().HISTORY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".history";
			}else{
				return "history";
			}
		}
		if (field.getFieldName().equals("id") && field.getClassType().equals(StatistichePdndTracing.class)) {
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id";
			}else{
				return "id";
			}
		}

		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(StatistichePdndTracing.model().DATA_TRACCIAMENTO)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().DATA_REGISTRAZIONE)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().PDD_CODICE)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().CSV)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().METHOD)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().STATO_PDND)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().TENTATIVI_PUBBLICAZIONE)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().STATO)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().TRACING_ID)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().ERROR_DETAILS)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if(field.equals(StatistichePdndTracing.model().HISTORY)){
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}
		if (field.getFieldName().equals("id") && field.getClassType().equals(StatistichePdndTracing.class)) {
			return this.toTable(StatistichePdndTracing.model(), returnAlias);
		}

		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(StatistichePdndTracing.model())){
			return CostantiDB.STATISTICHE_PDND_TRACING;
		}


		return super.toTable(model,returnAlias);
		
	}

}
