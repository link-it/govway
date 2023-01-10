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
package org.openspcoop2.core.commons.search.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.commons.search.PortaDelegata;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * PortaDelegataFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public PortaDelegataFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public PortaDelegataFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return PortaDelegata.model();
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
		
		if(field.equals(PortaDelegata.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_porta";
			}else{
				return "nome_porta";
			}
		}
		if(field.equals(PortaDelegata.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(PortaDelegata.model().ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(PortaDelegata.model().ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto_erogatore";
			}else{
				return "tipo_soggetto_erogatore";
			}
		}
		if(field.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto_erogatore";
			}else{
				return "nome_soggetto_erogatore";
			}
		}
		if(field.equals(PortaDelegata.model().TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio";
			}else{
				return "tipo_servizio";
			}
		}
		if(field.equals(PortaDelegata.model().NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio";
			}else{
				return "nome_servizio";
			}
		}
		if(field.equals(PortaDelegata.model().VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione_servizio";
			}else{
				return "versione_servizio";
			}
		}
		if(field.equals(PortaDelegata.model().MODE_AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mode_azione";
			}else{
				return "mode_azione";
			}
		}
		if(field.equals(PortaDelegata.model().NOME_AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_azione";
			}else{
				return "nome_azione";
			}
		}
		if(field.equals(PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_porta_delegante_azione";
			}else{
				return "nome_porta_delegante_azione";
			}
		}
		if(field.equals(PortaDelegata.model().CANALE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".canale";
			}else{
				return "canale";
			}
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto";
			}else{
				return "tipo_soggetto";
			}
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto";
			}else{
				return "nome_soggetto";
			}
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME)){
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
		
		if(field.equals(PortaDelegata.model().NOME)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().STATO)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().ID_SOGGETTO.TIPO)){
			return this.toTable(PortaDelegata.model().ID_SOGGETTO, returnAlias);
		}
		if(field.equals(PortaDelegata.model().ID_SOGGETTO.NOME)){
			return this.toTable(PortaDelegata.model().ID_SOGGETTO, returnAlias);
		}
		if(field.equals(PortaDelegata.model().TIPO_SOGGETTO_EROGATORE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().NOME_SOGGETTO_EROGATORE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().TIPO_SERVIZIO)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().NOME_SERVIZIO)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().VERSIONE_SERVIZIO)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().MODE_AZIONE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().NOME_AZIONE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().NOME_PORTA_DELEGANTE_AZIONE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().CANALE)){
			return this.toTable(PortaDelegata.model(), returnAlias);
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.NOME)){
			return this.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO, returnAlias);
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO.TIPO)){
			return this.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO.NOME)){
			return this.toTable(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO, returnAlias);
		}
		if(field.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE.NOME)){
			return this.toTable(PortaDelegata.model().PORTA_DELEGATA_AZIONE, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(PortaDelegata.model())){
			return CostantiDB.PORTE_DELEGATE;
		}
		if(model.equals(PortaDelegata.model().ID_SOGGETTO)){
			return CostantiDB.SOGGETTI;
		}
		if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO)){
			return CostantiDB.PORTE_DELEGATE_SA;
		}
		if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO)){
			return CostantiDB.SERVIZI_APPLICATIVI;
		}
		if(model.equals(PortaDelegata.model().PORTA_DELEGATA_SERVIZIO_APPLICATIVO.ID_SERVIZIO_APPLICATIVO.ID_SOGGETTO)){
			return CostantiDB.SOGGETTI;
		}
		if(model.equals(PortaDelegata.model().PORTA_DELEGATA_AZIONE)){
			return CostantiDB.PORTE_DELEGATE_AZIONI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
