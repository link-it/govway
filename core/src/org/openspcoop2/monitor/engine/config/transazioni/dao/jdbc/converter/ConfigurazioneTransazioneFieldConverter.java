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
package org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;


/**     
 * ConfigurazioneTransazioneFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneTransazioneFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public ConfigurazioneTransazioneFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public ConfigurazioneTransazioneFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return ConfigurazioneTransazione.model();
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
		
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".accordo";
			}else{
				return "accordo";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto_referente";
			}else{
				return "tipo_soggetto_referente";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto_referente";
			}else{
				return "nome_soggetto_referente";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione";
			}else{
				return "versione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio";
			}else{
				return "servizio";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_conf_trans_plugin";
			}else{
				return "id_conf_trans_plugin";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.CLASS_NAME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".class_name";
			}else{
				return "class_name";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.DESCRIZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".descrizione";
			}else{
				return "descrizione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.LABEL)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".label";
			}else{
				return "label";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_controllo";
			}else{
				return "tipo_controllo";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_messaggio";
			}else{
				return "tipo_messaggio";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".xpath";
			}else{
				return "xpath";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".abilita_anonimizzazione";
			}else{
				return "abilita_anonimizzazione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".abilita_compressione";
			}else{
				return "abilita_compressione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_compressione";
			}else{
				return "tipo_compressione";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".carattere_maschera";
			}else{
				return "carattere_maschera";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".num_char_maschera";
			}else{
				return "num_char_maschera";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".posizionamento_maschera";
			}else{
				return "posizionamento_maschera";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_mascheramento";
			}else{
				return "tipo_mascheramento";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".enabled";
			}else{
				return "enabled";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_messaggio";
			}else{
				return "tipo_messaggio";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".xpath";
			}else{
				return "xpath";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stat_enabled";
			}else{
				return "stat_enabled";
			}
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO.STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.ACCORDO)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.TIPO_SOGGETTO_REFERENTE)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.NOME_SOGGETTO_REFERENTE)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.VERSIONE)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO.SERVIZIO)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.AZIONE)){
			return this.toTable(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().ENABLED)){
			return this.toTable(ConfigurazioneTransazione.model(), returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.TIPO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.CLASS_NAME)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.DESCRIZIONE)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN.LABEL)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, returnAlias);
		}
		if(field.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO.STATO)){
			return this.toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(ConfigurazioneTransazione.model())){
			return "config_transazioni";
		}
		if(model.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE)){
			return "plugins_conf_azioni";
		}
		if(model.equals(ConfigurazioneTransazione.model().ID_CONFIGURAZIONE_SERVIZIO_AZIONE.ID_CONFIGURAZIONE_SERVIZIO)){
			return "plugins_conf_servizi";
		}
		if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN)){
			return "config_tran_plugins";
		}
		if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.PLUGIN)){
			return "plugins";
		}
		if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO)){
			return "configurazione_stati";
		}
		if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO)){
			return "conf_risorse_contenuti";
		}
		if(model.equals(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ID_CONFIGURAZIONE_TRANSAZIONE_STATO)){
			return "configurazione_stati";
		}


		return super.toTable(model,returnAlias);
		
	}

}
