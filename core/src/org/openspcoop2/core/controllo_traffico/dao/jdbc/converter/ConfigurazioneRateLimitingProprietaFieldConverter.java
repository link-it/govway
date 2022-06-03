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
package org.openspcoop2.core.controllo_traffico.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta;


/**     
 * ConfigurazioneRateLimitingProprietaFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRateLimitingProprietaFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazioneRateLimitingProprietaFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazioneRateLimitingProprietaFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazioneRateLimitingProprieta.model();
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
		
		if(field.equals(ConfigurazioneRateLimitingProprieta.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_prop_name";
			}else{
				return "rt_prop_name";
			}
		}
		if(field.equals(ConfigurazioneRateLimitingProprieta.model().VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_prop_value";
			}else{
				return "rt_prop_value";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazioneRateLimitingProprieta.model().NOME)){
			return this.toTable(ConfigurazioneRateLimitingProprieta.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneRateLimitingProprieta.model().VALORE)){
			return this.toTable(ConfigurazioneRateLimitingProprieta.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazioneRateLimitingProprieta.model())){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG_RATE_LIMITING_PROPERTIES;
		}


		return super.toTable(model,returnAlias);
		
	}

}
