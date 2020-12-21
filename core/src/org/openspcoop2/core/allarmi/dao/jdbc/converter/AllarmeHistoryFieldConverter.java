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
package org.openspcoop2.core.allarmi.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * AllarmeHistoryFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeHistoryFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public AllarmeHistoryFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public AllarmeHistoryFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return AllarmeHistory.model();
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
		
		if(field.equals(AllarmeHistory.model().ID_ALLARME.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AllarmeHistory.model().ID_ALLARME.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(AllarmeHistory.model().ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(AllarmeHistory.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(AllarmeHistory.model().DETTAGLIO_STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato_dettaglio";
			}else{
				return "stato_dettaglio";
			}
		}
		if(field.equals(AllarmeHistory.model().ACKNOWLEDGED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".acknowledged";
			}else{
				return "acknowledged";
			}
		}
		if(field.equals(AllarmeHistory.model().TIMESTAMP_UPDATE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".timestamp_update";
			}else{
				return "timestamp_update";
			}
		}
		if(field.equals(AllarmeHistory.model().UTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".utente";
			}else{
				return "utente";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(AllarmeHistory.model().ID_ALLARME.NOME)){
			return this.toTable(AllarmeHistory.model().ID_ALLARME, returnAlias);
		}
		if(field.equals(AllarmeHistory.model().ID_ALLARME.TIPO)){
			return this.toTable(AllarmeHistory.model().ID_ALLARME, returnAlias);
		}
		if(field.equals(AllarmeHistory.model().ENABLED)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}
		if(field.equals(AllarmeHistory.model().STATO)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}
		if(field.equals(AllarmeHistory.model().DETTAGLIO_STATO)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}
		if(field.equals(AllarmeHistory.model().ACKNOWLEDGED)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}
		if(field.equals(AllarmeHistory.model().TIMESTAMP_UPDATE)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}
		if(field.equals(AllarmeHistory.model().UTENTE)){
			return this.toTable(AllarmeHistory.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(AllarmeHistory.model())){
			return CostantiDB.ALLARMI_HISTORY;
		}
		if(model.equals(AllarmeHistory.model().ID_ALLARME)){
			return CostantiDB.ALLARMI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
