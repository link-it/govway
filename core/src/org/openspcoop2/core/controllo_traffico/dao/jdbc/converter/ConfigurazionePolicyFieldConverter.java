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
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;


/**     
 * ConfigurazionePolicyFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePolicyFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazionePolicyFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazionePolicyFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazionePolicy.model();
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
		
		if(field.equals(ConfigurazionePolicy.model().ID_POLICY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_id";
			}else{
				return "policy_id";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_descrizione";
			}else{
				return "rt_descrizione";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().RISORSA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_risorsa";
			}else{
				return "rt_risorsa";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().SIMULTANEE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_simultanee";
			}else{
				return "rt_simultanee";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_valore";
			}else{
				return "rt_valore";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE_TIPO_BANDA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_bytes_type";
			}else{
				return "rt_bytes_type";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE_TIPO_LATENZA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_latency_type";
			}else{
				return "rt_latency_type";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().MODALITA_CONTROLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_modalita_controllo";
			}else{
				return "rt_modalita_controllo";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_interval_type_real";
			}else{
				return "rt_interval_type_real";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_interval_type_stat";
			}else{
				return "rt_interval_type_stat";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_interval";
			}else{
				return "rt_interval";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_finestra";
			}else{
				return "rt_finestra";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_APPLICABILITA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_applicabilita";
			}else{
				return "rt_applicabilita";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_applicabilita_con_cc";
			}else{
				return "rt_applicabilita_con_cc";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_applicabilita_degrado";
			}else{
				return "rt_applicabilita_degrado";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrato_modalita_controllo";
			}else{
				return "degrato_modalita_controllo";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrado_avg_interval_type_real";
			}else{
				return "degrado_avg_interval_type_real";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrado_avg_interval_type_stat";
			}else{
				return "degrado_avg_interval_type_stat";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrado_avg_interval";
			}else{
				return "degrado_avg_interval";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrado_avg_finestra";
			}else{
				return "degrado_avg_finestra";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".degrado_avg_latency_type";
			}else{
				return "degrado_avg_latency_type";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".rt_applicabilita_allarme";
			}else{
				return "rt_applicabilita_allarme";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".allarme_nome";
			}else{
				return "allarme_nome";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".allarme_stato";
			}else{
				return "allarme_stato";
			}
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_NOT_STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".allarme_not_stato";
			}else{
				return "allarme_not_stato";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazionePolicy.model().ID_POLICY)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DESCRIZIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().RISORSA)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().SIMULTANEE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE_TIPO_BANDA)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().VALORE_TIPO_LATENZA)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().MODALITA_CONTROLLO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_REALTIME)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().INTERVALLO_OSSERVAZIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().FINESTRA_OSSERVAZIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().TIPO_APPLICABILITA)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_CON_CONGESTIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_DEGRADO_PRESTAZIONALE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_MODALITA_CONTROLLO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_REALTIME)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_INTERVALLO_OSSERVAZIONE_STATISTICO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_INTERVALLO_OSSERVAZIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_FINESTRA_OSSERVAZIONE)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().DEGRADO_AVG_TIME_TIPO_LATENZA)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().APPLICABILITA_STATO_ALLARME)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_NOME)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_STATO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}
		if(field.equals(ConfigurazionePolicy.model().ALLARME_NOT_STATO)){
			return this.toTable(ConfigurazionePolicy.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazionePolicy.model())){
			return CostantiDB.CONGESTIONE_CONFIG_POLICY;
		}


		return super.toTable(model,returnAlias);
		
	}

}
