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
package org.openspcoop2.core.commons.search.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.commons.search.ServizioApplicativo;


/**     
 * ServizioApplicativoFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ServizioApplicativoFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ServizioApplicativoFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ServizioApplicativo.model();
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
		
		if(field.equals(ServizioApplicativo.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipologia_fruizione";
			}else{
				return "tipologia_fruizione";
			}
		}
		if(field.equals(ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipologia_erogazione";
			}else{
				return "tipologia_erogazione";
			}
		}
		if(field.equals(ServizioApplicativo.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(ServizioApplicativo.model().AS_CLIENT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".as_client";
			}else{
				return "as_client";
			}
		}
		if(field.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(ServizioApplicativo.model().ID_SOGGETTO.NOME)){
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
		
		if(field.equals(ServizioApplicativo.model().NOME)){
			return this.toTable(ServizioApplicativo.model(), returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().TIPOLOGIA_FRUIZIONE)){
			return this.toTable(ServizioApplicativo.model(), returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().TIPOLOGIA_EROGAZIONE)){
			return this.toTable(ServizioApplicativo.model(), returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().TIPO)){
			return this.toTable(ServizioApplicativo.model(), returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().AS_CLIENT)){
			return this.toTable(ServizioApplicativo.model(), returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().ID_SOGGETTO.TIPO)){
			return this.toTable(ServizioApplicativo.model().ID_SOGGETTO, returnAlias);
		}
		if(field.equals(ServizioApplicativo.model().ID_SOGGETTO.NOME)){
			return this.toTable(ServizioApplicativo.model().ID_SOGGETTO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ServizioApplicativo.model())){
			return "servizi_applicativi";
		}
		if(model.equals(ServizioApplicativo.model().ID_SOGGETTO)){
			return "soggetti";
		}


		return super.toTable(model,returnAlias);
		
	}

}
