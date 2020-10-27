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

import org.openspcoop2.core.commons.search.AccordoServizioParteComune;


/**     
 * AccordoServizioParteComuneFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public AccordoServizioParteComuneFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public AccordoServizioParteComuneFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return AccordoServizioParteComune.model();
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
		
		if(field.equals(AccordoServizioParteComune.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().CANALE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".canale";
			}else{
				return "canale";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".http_method";
			}else{
				return "http_method";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.PATH)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".path";
			}else{
				return "path";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".service_binding";
			}else{
				return "service_binding";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
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
		
		if(field.equals(AccordoServizioParteComune.model().NOME)){
			return this.toTable(AccordoServizioParteComune.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ID_REFERENTE.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().ID_REFERENTE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ID_REFERENTE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ID_REFERENTE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().CANALE)){
			return this.toTable(AccordoServizioParteComune.model(), returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.HTTP_METHOD)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.PATH)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.TIPO)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO.NOME)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.VERSIONE)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}
		if(field.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.SERVICE_BINDING)){
			return this.toTable(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(AccordoServizioParteComune.model())){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().ID_REFERENTE)){
			return "soggetti";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE)){
			return "accordi_azioni";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE)){
			return "port_type";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION)){
			return "port_type_azioni";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE)){
			return "port_type";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.OPERATION.ID_PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().PORT_TYPE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}
		if(model.equals(AccordoServizioParteComune.model().RESOURCE)){
			return "api_resources";
		}
		if(model.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().RESOURCE.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO)){
			return "accordi_gruppi";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_GRUPPO)){
			return "gruppi";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE)){
			return "accordi";
		}
		if(model.equals(AccordoServizioParteComune.model().ACCORDO_SERVIZIO_PARTE_COMUNE_GRUPPO.ID_ACCORDO_SERVIZIO_PARTE_COMUNE.ID_SOGGETTO)){
			return "soggetti";
		}


		return super.toTable(model,returnAlias);
		
	}

}
