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
package org.openspcoop2.core.commons.search.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.commons.search.SoggettoRuolo;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * SoggettoRuoloFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoRuoloFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public SoggettoRuoloFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public SoggettoRuoloFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return SoggettoRuolo.model();
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
		
		if(field.equals(SoggettoRuolo.model().ID_RUOLO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(SoggettoRuolo.model().ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(SoggettoRuolo.model().ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(SoggettoRuolo.model().ID_RUOLO.NOME)){
			return this.toTable(SoggettoRuolo.model().ID_RUOLO, returnAlias);
		}
		if(field.equals(SoggettoRuolo.model().ID_SOGGETTO.TIPO)){
			return this.toTable(SoggettoRuolo.model().ID_SOGGETTO, returnAlias);
		}
		if(field.equals(SoggettoRuolo.model().ID_SOGGETTO.NOME)){
			return this.toTable(SoggettoRuolo.model().ID_SOGGETTO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(SoggettoRuolo.model())){
			return CostantiDB.SOGGETTI_RUOLI;
		}
		if(model.equals(SoggettoRuolo.model().ID_RUOLO)){
			return CostantiDB.RUOLI;
		}
		if(model.equals(SoggettoRuolo.model().ID_SOGGETTO)){
			return CostantiDB.SOGGETTI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
