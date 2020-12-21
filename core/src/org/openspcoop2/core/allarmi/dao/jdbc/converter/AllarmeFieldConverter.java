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
package org.openspcoop2.core.allarmi.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.constants.CostantiDB;


/**     
 * AllarmeFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public AllarmeFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public AllarmeFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Allarme.model();
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
		
		if(field.equals(Allarme.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Allarme.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(Allarme.model().TIPO_ALLARME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_allarme";
			}else{
				return "tipo_allarme";
			}
		}
		if(field.equals(Allarme.model().MAIL.ACK_MODE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_ack_mode";
			}else{
				return "mail_ack_mode";
			}
		}
		if(field.equals(Allarme.model().MAIL.INVIA_WARNING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_invia_warning";
			}else{
				return "mail_invia_warning";
			}
		}
		if(field.equals(Allarme.model().MAIL.INVIA_ALERT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_invia_alert";
			}else{
				return "mail_invia_alert";
			}
		}
		if(field.equals(Allarme.model().MAIL.DESTINATARI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_destinatari";
			}else{
				return "mail_destinatari";
			}
		}
		if(field.equals(Allarme.model().MAIL.SUBJECT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_subject";
			}else{
				return "mail_subject";
			}
		}
		if(field.equals(Allarme.model().MAIL.BODY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".mail_body";
			}else{
				return "mail_body";
			}
		}
		if(field.equals(Allarme.model().SCRIPT.ACK_MODE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".script_ack_mode";
			}else{
				return "script_ack_mode";
			}
		}
		if(field.equals(Allarme.model().SCRIPT.INVOCA_WARNING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".script_invoke_warning";
			}else{
				return "script_invoke_warning";
			}
		}
		if(field.equals(Allarme.model().SCRIPT.INVOCA_ALERT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".script_invoke_alert";
			}else{
				return "script_invoke_alert";
			}
		}
		if(field.equals(Allarme.model().SCRIPT.COMMAND)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".script_command";
			}else{
				return "script_command";
			}
		}
		if(field.equals(Allarme.model().SCRIPT.ARGS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".script_args";
			}else{
				return "script_args";
			}
		}
		if(field.equals(Allarme.model().STATO_PRECEDENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato_precedente";
			}else{
				return "stato_precedente";
			}
		}
		if(field.equals(Allarme.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(Allarme.model().DETTAGLIO_STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato_dettaglio";
			}else{
				return "stato_dettaglio";
			}
		}
		if(field.equals(Allarme.model().LASTTIMESTAMP_CREATE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".lasttimestamp_create";
			}else{
				return "lasttimestamp_create";
			}
		}
		if(field.equals(Allarme.model().LASTTIMESTAMP_UPDATE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".lasttimestamp_update";
			}else{
				return "lasttimestamp_update";
			}
		}
		if(field.equals(Allarme.model().ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(Allarme.model().ACKNOWLEDGED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".acknowledged";
			}else{
				return "acknowledged";
			}
		}
		if(field.equals(Allarme.model().TIPO_PERIODO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".periodo_tipo";
			}else{
				return "periodo_tipo";
			}
		}
		if(field.equals(Allarme.model().PERIODO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".periodo";
			}else{
				return "periodo";
			}
		}
		if(field.equals(Allarme.model().FILTRO.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_enabled";
			}else{
				return "filtro_enabled";
			}
		}
		if(field.equals(Allarme.model().FILTRO.PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_protocollo";
			}else{
				return "filtro_protocollo";
			}
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo";
			}else{
				return "filtro_ruolo";
			}
		}
		if(field.equals(Allarme.model().FILTRO.NOME_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_porta";
			}else{
				return "filtro_porta";
			}
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_fruitore";
			}else{
				return "filtro_tipo_fruitore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.NOME_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_fruitore";
			}else{
				return "filtro_nome_fruitore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo_fruitore";
			}else{
				return "filtro_ruolo_fruitore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_sa_fruitore";
			}else{
				return "filtro_sa_fruitore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_erogatore";
			}else{
				return "filtro_tipo_erogatore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.NOME_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_erogatore";
			}else{
				return "filtro_nome_erogatore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_ruolo_erogatore";
			}else{
				return "filtro_ruolo_erogatore";
			}
		}
		if(field.equals(Allarme.model().FILTRO.TAG)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tag";
			}else{
				return "filtro_tag";
			}
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_tipo_servizio";
			}else{
				return "filtro_tipo_servizio";
			}
		}
		if(field.equals(Allarme.model().FILTRO.NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_nome_servizio";
			}else{
				return "filtro_nome_servizio";
			}
		}
		if(field.equals(Allarme.model().FILTRO.VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_versione_servizio";
			}else{
				return "filtro_versione_servizio";
			}
		}
		if(field.equals(Allarme.model().FILTRO.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".filtro_azione";
			}else{
				return "filtro_azione";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_enabled";
			}else{
				return "group_enabled";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.RUOLO_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_ruolo";
			}else{
				return "group_ruolo";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_protocollo";
			}else{
				return "group_protocollo";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_fruitore";
			}else{
				return "group_fruitore";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_sa_fruitore";
			}else{
				return "group_sa_fruitore";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_id_autenticato";
			}else{
				return "group_id_autenticato";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.TOKEN)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_token";
			}else{
				return "group_token";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_erogatore";
			}else{
				return "group_erogatore";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_servizio";
			}else{
				return "group_servizio";
			}
		}
		if(field.equals(Allarme.model().GROUP_BY.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".group_azione";
			}else{
				return "group_azione";
			}
		}
		if(field.equals(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".param_id";
			}else{
				return "param_id";
			}
		}
		if(field.equals(Allarme.model().ALLARME_PARAMETRO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".param_value";
			}else{
				return "param_value";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(Allarme.model().NOME)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().TIPO)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().TIPO_ALLARME)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.ACK_MODE)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.INVIA_WARNING)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.INVIA_ALERT)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.DESTINATARI)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.SUBJECT)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().MAIL.BODY)){
			return this.toTable(Allarme.model().MAIL, returnAlias);
		}
		if(field.equals(Allarme.model().SCRIPT.ACK_MODE)){
			return this.toTable(Allarme.model().SCRIPT, returnAlias);
		}
		if(field.equals(Allarme.model().SCRIPT.INVOCA_WARNING)){
			return this.toTable(Allarme.model().SCRIPT, returnAlias);
		}
		if(field.equals(Allarme.model().SCRIPT.INVOCA_ALERT)){
			return this.toTable(Allarme.model().SCRIPT, returnAlias);
		}
		if(field.equals(Allarme.model().SCRIPT.COMMAND)){
			return this.toTable(Allarme.model().SCRIPT, returnAlias);
		}
		if(field.equals(Allarme.model().SCRIPT.ARGS)){
			return this.toTable(Allarme.model().SCRIPT, returnAlias);
		}
		if(field.equals(Allarme.model().STATO_PRECEDENTE)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().STATO)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().DETTAGLIO_STATO)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().LASTTIMESTAMP_CREATE)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().LASTTIMESTAMP_UPDATE)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().ENABLED)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().ACKNOWLEDGED)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().TIPO_PERIODO)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().PERIODO)){
			return this.toTable(Allarme.model(), returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.ENABLED)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.PROTOCOLLO)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_PORTA)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.NOME_PORTA)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_FRUITORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.NOME_FRUITORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_FRUITORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_EROGATORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.NOME_EROGATORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.RUOLO_EROGATORE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.TAG)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.TIPO_SERVIZIO)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.NOME_SERVIZIO)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.VERSIONE_SERVIZIO)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().FILTRO.AZIONE)){
			return this.toTable(Allarme.model().FILTRO, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.ENABLED)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.RUOLO_PORTA)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.PROTOCOLLO)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.FRUITORE)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.TOKEN)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.EROGATORE)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.SERVIZIO)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().GROUP_BY.AZIONE)){
			return this.toTable(Allarme.model().GROUP_BY, returnAlias);
		}
		if(field.equals(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO)){
			return this.toTable(Allarme.model().ALLARME_PARAMETRO, returnAlias);
		}
		if(field.equals(Allarme.model().ALLARME_PARAMETRO.VALORE)){
			return this.toTable(Allarme.model().ALLARME_PARAMETRO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(Allarme.model())){
			return CostantiDB.ALLARMI;
		}
		if(model.equals(Allarme.model().MAIL)){
			return CostantiDB.ALLARMI;
		}
		if(model.equals(Allarme.model().SCRIPT)){
			return CostantiDB.ALLARMI;
		}
		if(model.equals(Allarme.model().FILTRO)){
			return CostantiDB.ALLARMI;
		}
		if(model.equals(Allarme.model().GROUP_BY)){
			return CostantiDB.ALLARMI;
		}
		if(model.equals(Allarme.model().ALLARME_PARAMETRO)){
			return CostantiDB.ALLARMI_PARAMETRI;
		}


		return super.toTable(model,returnAlias);
		
	}

}
