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
package org.openspcoop2.core.controllo_traffico.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;


/**     
 * AttivazionePolicyFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttivazionePolicyFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public AttivazionePolicyFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public AttivazionePolicyFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return AttivazionePolicy.model();
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
		
		if(field.equals(AttivazionePolicy.model().ID_ACTIVE_POLICY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".active_policy_id";
			}else{
				return "active_policy_id";
			}
		}
		if(field.equals(AttivazionePolicy.model().ALIAS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_alias";
			}else{
				return "policy_alias";
			}
		}
		if(field.equals(AttivazionePolicy.model().UPDATE_TIME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_update_time";
			}else{
				return "policy_update_time";
			}
		}
		if(field.equals(AttivazionePolicy.model().POSIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_posizione";
			}else{
				return "policy_posizione";
			}
		}
		if(field.equals(AttivazionePolicy.model().CONTINUA_VALUTAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_continue";
			}else{
				return "policy_continue";
			}
		}
		if(field.equals(AttivazionePolicy.model().ID_POLICY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_id";
			}else{
				return "policy_id";
			}
		}
		if(field.equals(AttivazionePolicy.model().ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_enabled";
			}else{
				return "policy_enabled";
			}
		}
		if(field.equals(AttivazionePolicy.model().WARNING_ONLY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_warning";
			}else{
				return "policy_warning";
			}
		}
		if(field.equals(AttivazionePolicy.model().RIDEFINISCI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_redefined";
			}else{
				return "policy_redefined";
			}
		}
		if(field.equals(AttivazionePolicy.model().VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".policy_valore";
			}else{
				return "policy_valore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_enabled";
			}else{
				return "filtro_enabled";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_protocollo";
			}else{
				return "filtro_protocollo";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo";
			}else{
				return "filtro_ruolo";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_porta";
			}else{
				return "filtro_porta";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_fruitore";
			}else{
				return "filtro_tipo_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_fruitore";
			}else{
				return "filtro_nome_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo_fruitore";
			}else{
				return "filtro_ruolo_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_sa_fruitore";
			}else{
				return "filtro_sa_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_erogatore";
			}else{
				return "filtro_tipo_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_erogatore";
			}else{
				return "filtro_nome_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo_erogatore";
			}else{
				return "filtro_ruolo_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_sa_erogatore";
			}else{
				return "filtro_sa_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TAG)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tag";
			}else{
				return "filtro_tag";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_servizio";
			}else{
				return "filtro_tipo_servizio";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_servizio";
			}else{
				return "filtro_nome_servizio";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_versione_servizio";
			}else{
				return "filtro_versione_servizio";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_azione";
			}else{
				return "filtro_azione";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_key_enabled";
			}else{
				return "filtro_key_enabled";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_key_type";
			}else{
				return "filtro_key_type";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_key_name";
			}else{
				return "filtro_key_name";
			}
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_key_value";
			}else{
				return "filtro_key_value";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_enabled";
			}else{
				return "group_enabled";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_ruolo";
			}else{
				return "group_ruolo";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_protocollo";
			}else{
				return "group_protocollo";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_fruitore";
			}else{
				return "group_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_sa_fruitore";
			}else{
				return "group_sa_fruitore";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_id_autenticato";
			}else{
				return "group_id_autenticato";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.TOKEN)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_token";
			}else{
				return "group_token";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_erogatore";
			}else{
				return "group_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_sa_erogatore";
			}else{
				return "group_sa_erogatore";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_servizio";
			}else{
				return "group_servizio";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_azione";
			}else{
				return "group_azione";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_key_enabled";
			}else{
				return "group_key_enabled";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_key_type";
			}else{
				return "group_key_type";
			}
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_key_name";
			}else{
				return "group_key_name";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(AttivazionePolicy.model().ID_ACTIVE_POLICY)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().ALIAS)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().UPDATE_TIME)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().POSIZIONE)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().CONTINUA_VALUTAZIONE)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().ID_POLICY)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().ENABLED)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().WARNING_ONLY)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().RIDEFINISCI)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().VALORE)){
			return this.toTable(AttivazionePolicy.model(), returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.ENABLED)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.PROTOCOLLO)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_PORTA)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_PORTA)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_FRUITORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_EROGATORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TAG)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.AZIONE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE)){
			return this.toTable(AttivazionePolicy.model().FILTRO, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.ENABLED)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.FRUITORE)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.TOKEN)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.EROGATORE)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.SERVIZIO)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.AZIONE)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}
		if(field.equals(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME)){
			return this.toTable(AttivazionePolicy.model().GROUP_BY, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(AttivazionePolicy.model())){
			return CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY;
		}
		if(model.equals(AttivazionePolicy.model().FILTRO)){
			return CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY;
		}
		if(model.equals(AttivazionePolicy.model().GROUP_BY)){
			return CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY;
		}


		return super.toTable(model,returnAlias);
		
	}

}
