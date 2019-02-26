/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.commons.search.Operation;


/**     
 * OperationFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public OperationFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public OperationFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Operation.model();
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
		
		if(field.equals(Operation.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(Operation.model().NOME)){
			return this.toTable(Operation.model(), returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.NOME)){
			return this.toTable(Operation.model().ID_PORT_TYPE, returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(Operation.model())){
			return "port_type_azioni";
		}
		if(model.equals(Operation.model().ID_PORT_TYPE)){
			return "port_type";
		}
		if(model.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(Operation.model().ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}


		return super.toTable(model,returnAlias);
		
	}

}
