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
package org.openspcoop2.core.plugins.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;


/**     
 * ConfigurazioneServizioAzioneFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneServizioAzioneFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazioneServizioAzioneFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazioneServizioAzioneFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazioneServizioAzione.model();
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
		
		if(field.equals(ConfigurazioneServizioAzione.model().AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".accordo";
			}else{
				return "accordo";
			}
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto_referente";
			}else{
				return "tipo_soggetto_referente";
			}
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto_referente";
			}else{
				return "nome_soggetto_referente";
			}
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio";
			}else{
				return "servizio";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazioneServizioAzione.model().AZIONE)){
			return this.toTable(ConfigurazioneServizioAzione.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.ACCORDO)){
			return this.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE)){
			return this.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE)){
			return this.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.VERSIONE)){
			return this.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO)){
			return this.toTable(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazioneServizioAzione.model())){
			return "plugins_conf_azioni";
		}
		if(model.equals(ConfigurazioneServizioAzione.model().ID_CONFIGURAZIONE_SERVIZIO)){
			return "plugins_conf_servizi";
		}


		return super.toTable(model,returnAlias);
		
	}

}
