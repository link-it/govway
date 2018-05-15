/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro;


/**     
 * ConfigurazioneFiltroFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneFiltroFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazioneFiltroFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazioneFiltroFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazioneFiltro.model();
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
		
		if(field.equals(ConfigurazioneFiltro.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".descrizione";
			}else{
				return "descrizione";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_mittente";
			}else{
				return "tipo_mittente";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_mittente";
			}else{
				return "nome_mittente";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().IDPORTA_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_mittente";
			}else{
				return "idporta_mittente";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_destinatario";
			}else{
				return "tipo_destinatario";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_destinatario";
			}else{
				return "nome_destinatario";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_destinatario";
			}else{
				return "idporta_destinatario";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio";
			}else{
				return "tipo_servizio";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio";
			}else{
				return "nome_servizio";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione_servizio";
			}else{
				return "versione_servizio";
			}
		}
		if(field.equals(ConfigurazioneFiltro.model().AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazioneFiltro.model().NOME)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().DESCRIZIONE)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_MITTENTE)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_MITTENTE)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().IDPORTA_MITTENTE)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_DESTINATARIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_DESTINATARIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().IDPORTA_DESTINATARIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().TIPO_SERVIZIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().NOME_SERVIZIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().VERSIONE_SERVIZIO)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneFiltro.model().AZIONE)){
			return this.toTable(ConfigurazioneFiltro.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazioneFiltro.model())){
			return "plugins_conf_filtri";
		}


		return super.toTable(model,returnAlias);
		
	}

}
