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
package org.openspcoop2.core.controllo_traffico.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;


/**     
 * ConfigurazioneGeneraleFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGeneraleFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazioneGeneraleFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazioneGeneraleFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazioneGenerale.model();
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
		
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".max_threads_enabled";
			}else{
				return "max_threads_enabled";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".max_threads_warning_only";
			}else{
				return "max_threads_warning_only";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".max_threads";
			}else{
				return "max_threads";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".max_threads_tipo_errore";
			}else{
				return "max_threads_tipo_errore";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".max_threads_includi_errore";
			}else{
				return "max_threads_includi_errore";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cc_enabled";
			}else{
				return "cc_enabled";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cc_threshold";
			}else{
				return "cc_threshold";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pd_connection_timeout";
			}else{
				return "pd_connection_timeout";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pd_read_timeout";
			}else{
				return "pd_read_timeout";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pd_avg_time";
			}else{
				return "pd_avg_time";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pa_connection_timeout";
			}else{
				return "pa_connection_timeout";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pa_read_timeout";
			}else{
				return "pa_read_timeout";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pa_avg_time";
			}else{
				return "pa_avg_time";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_tipo_errore";
			}else{
				return "rt_tipo_errore";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_includi_errore";
			}else{
				return "rt_includi_errore";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.CACHE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cache";
			}else{
				return "cache";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.SIZE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cache_size";
			}else{
				return "cache_size";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.ALGORITHM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cache_algorithm";
			}else{
				return "cache_algorithm";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.IDLE_TIME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cache_idle_time";
			}else{
				return "cache_idle_time";
			}
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.LIFE_TIME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cache_life_time";
			}else{
				return "cache_life_time";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_ENABLED)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_WARNING_ONLY)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_SOGLIA)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_MAX_THREADS_TIPO_ERRORE_INCLUDI_DESCRIZIONE)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_ENABLED)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO.CONTROLLO_CONGESTIONE_THRESHOLD)){
			return this.toTable(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.CONNECTION_TIMEOUT)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.READ_TIMEOUT)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE.TEMPO_MEDIO_RISPOSTA)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.CONNECTION_TIMEOUT)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.READ_TIMEOUT)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE.TEMPO_MEDIO_RISPOSTA)){
			return this.toTable(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE)){
			return this.toTable(ConfigurazioneGenerale.model().RATE_LIMITING, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().RATE_LIMITING.TIPO_ERRORE_INCLUDI_DESCRIZIONE)){
			return this.toTable(ConfigurazioneGenerale.model().RATE_LIMITING, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.CACHE)){
			return this.toTable(ConfigurazioneGenerale.model().CACHE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.SIZE)){
			return this.toTable(ConfigurazioneGenerale.model().CACHE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.ALGORITHM)){
			return this.toTable(ConfigurazioneGenerale.model().CACHE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.IDLE_TIME)){
			return this.toTable(ConfigurazioneGenerale.model().CACHE, returnAlias);
		}
		if(field.equals(ConfigurazioneGenerale.model().CACHE.LIFE_TIME)){
			return this.toTable(ConfigurazioneGenerale.model().CACHE, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazioneGenerale.model())){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}
		if(model.equals(ConfigurazioneGenerale.model().CONTROLLO_TRAFFICO)){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}
		if(model.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_FRUIZIONE)){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}
		if(model.equals(ConfigurazioneGenerale.model().TEMPI_RISPOSTA_EROGAZIONE)){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}
		if(model.equals(ConfigurazioneGenerale.model().RATE_LIMITING)){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}
		if(model.equals(ConfigurazioneGenerale.model().CACHE)){
			return CostantiDB.CONTROLLO_TRAFFICO_CONFIG;
		}


		return super.toTable(model,returnAlias);
		
	}

}
