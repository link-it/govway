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
package org.openspcoop2.core.commons.search.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;


/**     
 * AccordoServizioParteSpecificaFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecificaFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public AccordoServizioParteSpecificaFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public AccordoServizioParteSpecificaFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return AccordoServizioParteSpecifica.model();
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
		
		if(field.equals(AccordoServizioParteSpecifica.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio";
			}else{
				return "tipo_servizio";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio";
			}else{
				return "nome_servizio";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().PORT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".port_type";
			}else{
				return "port_type";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(AccordoServizioParteSpecifica.model().TIPO)){
			return this.toTable(AccordoServizioParteSpecifica.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().NOME)){
			return this.toTable(AccordoServizioParteSpecifica.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().PORT_TYPE)){
			return this.toTable(AccordoServizioParteSpecifica.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.TIPO)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_EROGATORE, returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE.NOME)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_EROGATORE, returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(AccordoServizioParteSpecifica.model())){
			return "servizi";
		}
		if(model.equals(AccordoServizioParteSpecifica.model().ID_EROGATORE)){
			if(returnAlias){
				return "sogerog";
			}else{
				return "soggetti sogerog";
			}
		}
		if(model.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteSpecifica.model().ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			if(returnAlias){
				return "sogref";
			}else{
				return "soggetti sogref";
			}
		}


		return super.toTable(model,returnAlias);
		
	}

}
