/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.eventi.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.eventi.Evento;


/**     
 * EventoFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EventoFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public EventoFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public EventoFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Evento.model();
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
		
		if(field.equals(Evento.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(Evento.model().CODICE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice";
			}else{
				return "codice";
			}
		}
		if(field.equals(Evento.model().SEVERITA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".severita";
			}else{
				return "severita";
			}
		}
		if(field.equals(Evento.model().ORA_REGISTRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".ora_registrazione";
			}else{
				return "ora_registrazione";
			}
		}
		if(field.equals(Evento.model().DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".descrizione";
			}else{
				return "descrizione";
			}
		}
		if(field.equals(Evento.model().ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(Evento.model().ID_CONFIGURAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_configurazione";
			}else{
				return "id_configurazione";
			}
		}
		if(field.equals(Evento.model().CONFIGURAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".configurazione";
			}else{
				return "configurazione";
			}
		}
		if(field.equals(Evento.model().CLUSTER_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id";
			}else{
				return "cluster_id";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(Evento.model().TIPO)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().CODICE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().SEVERITA)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().ORA_REGISTRAZIONE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().DESCRIZIONE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().ID_TRANSAZIONE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().ID_CONFIGURAZIONE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().CONFIGURAZIONE)){
			return this.toTable(Evento.model(), returnAlias);
		}
		if(field.equals(Evento.model().CLUSTER_ID)){
			return this.toTable(Evento.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(Evento.model())){
			return CostantiDB.DUMP_EVENTI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
