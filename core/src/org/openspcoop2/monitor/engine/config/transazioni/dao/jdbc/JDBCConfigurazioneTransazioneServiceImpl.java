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
package org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCConfigurazioneServizioAzioneBaseLib;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCPluginsBaseLib;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneTransazione;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;

import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;

import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;

import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCConfigurazioneTransazioneServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCConfigurazioneTransazioneServiceImpl extends JDBCConfigurazioneTransazioneServiceSearchImpl
	implements IJDBCServiceCRUDWithId<ConfigurazioneTransazione, IdConfigurazioneTransazione, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneTransazione configurazioneTransazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				

		// Object _configurazioneServizioAzione
		if(configurazioneTransazione.getIdConfigurazioneServizioAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione is null");
		}
		if(configurazioneTransazione.getIdConfigurazioneServizioAzione().getAzione()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.azione is null");
		}
		if(configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio()==null){
			throw new ServiceException("IdConfigurazioneServizioAzione.idConfigurazioneServizio is null");
		}



		// Object _configurazioneServizioAzione
		Long id_configurazioneServizioAzione = 
				JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getAzione(),
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getAccordo(), 
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getVersione(), 
						configurazioneTransazione.getIdConfigurazioneServizioAzione().getIdConfigurazioneServizio().getServizio(), 
						true);


		// Object configurazioneTransazione
		sqlQueryObjectInsert.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		sqlQueryObjectInsert.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField("id_conf_servizio_azione","?");

		// Insert configurazioneTransazione
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getEnabled(),ConfigurazioneTransazione.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneServizioAzione,Long.class)
		);
		configurazioneTransazione.setId(id);

		// for configurazioneTransazione
		for (int i = 0; i < configurazioneTransazione.getConfigurazioneTransazionePluginList().size(); i++) {

			ConfigurazioneTransazionePlugin configTransazionePlugin = configurazioneTransazione.getConfigurazioneTransazionePlugin(i);
			
			// Object _plugin
			if(configTransazionePlugin.getPlugin()==null || configTransazionePlugin.getPlugin().getClassName()==null){
				throw new ServiceException("ClassName del plugin non fornito");
			}
			Long id_configurazioneTransazionePlugin_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.TRANSAZIONE, configTransazionePlugin.getPlugin().getClassName(), true);

			// Object configurazioneTransazione.getConfigurazioneTransazionePluginList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazionePlugin = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_configurazioneTransazionePlugin.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
			sqlQueryObjectInsert_configurazioneTransazionePlugin.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN,false),"?");
			sqlQueryObjectInsert_configurazioneTransazionePlugin.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED,false),"?");
			sqlQueryObjectInsert_configurazioneTransazionePlugin.addInsertField("id_plugin","?");
			sqlQueryObjectInsert_configurazioneTransazionePlugin.addInsertField("id_configurazione_transazione","?");

			// Insert configurazioneTransazione.getConfigurazioneTransazionePluginList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazionePlugin = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN);
			long id_configurazioneTransazionePlugin = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazionePlugin, keyGenerator_configurazioneTransazionePlugin, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazionePluginList().get(i).getIdConfigurazioneTransazionePlugin(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazionePluginList().get(i).getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneTransazionePlugin_plugin,Long.class),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			configurazioneTransazione.getConfigurazioneTransazionePluginList().get(i).setId(id_configurazioneTransazionePlugin);
		} // fine for 

		// for configurazioneTransazione
		for (int i = 0; i < configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().size(); i++) {

			// Object _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
			ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addSelectField("id");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.setANDLogicOperator(true);
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,true)+"=?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition("id_configurazione_transazione=?");

			// Recupero _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
			org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] {
					new JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getIdConfigurazioneTransazioneStato().getStato(), String.class),
					new JDBCObject(configurazioneTransazione.getId(),Long.class)
			};
			Long id_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
			try{
				id_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = (Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.createSQLQuery(), jdbcProperties.isShowSql(),
						Long.class, searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato);
			}catch(NotFoundException e){
				// risorsa con stato == null o '*'
				id_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
			}

			// Object configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED,false),"?");
//			if(id_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato != null)
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField("id_configurazione_stato","?");
			sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto.addInsertField("id_conf_transazione","?");

			// Insert configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazioneRisorsaContenuto = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO);
			long id_configurazioneTransazioneRisorsaContenuto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto, keyGenerator_configurazioneTransazioneRisorsaContenuto, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getAbilitaAnonimizzazione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getAbilitaCompressione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getTipoCompressione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getCarattereMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getNumeroCaratteriMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getPosizionamentoMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getTipoMascheramento(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getNome(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getTipoMessaggio(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getXpath(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).getStatEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato,Long.class),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList().get(i).setId(id_configurazioneTransazioneRisorsaContenuto);
		} // fine for 

		// for configurazioneTransazione
		for (int i = 0; i < configurazioneTransazione.getConfigurazioneTransazioneStatoList().size(); i++) {


			// Object configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazioneStato = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH,false),"?");
			sqlQueryObjectInsert_configurazioneTransazioneStato.addInsertField("id_configurazione_transazione","?");

			// Insert configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazioneStato = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO);
			long id_configurazioneTransazioneStato = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazioneStato, keyGenerator_configurazioneTransazioneStato, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getNome(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getTipoControllo(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getTipoMessaggio(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getValore(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).getXpath(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			configurazioneTransazione.getConfigurazioneTransazioneStatoList().get(i).setId(id_configurazioneTransazioneStato);
		} // fine for 


	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione oldId, ConfigurazioneTransazione configurazioneTransazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = configurazioneTransazione.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: configurazioneTransazione.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			configurazioneTransazione.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneTransazione, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneTransazione configurazioneTransazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		
		@SuppressWarnings("unused")
		boolean setIdMappingResolutionBehaviour = 
			(idMappingResolutionBehaviour==null) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour) ||
			org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour);
			

		// Object _configurazioneTransazione_configurazioneServizioAzione
		Long id_configurazioneTransazione_configurazioneServizioAzione = null;
		org.openspcoop2.monitor.engine.config.transazioni.IdConfigurazioneServizioAzione idLogic_configurazioneTransazione_configurazioneServizioAzione = null;
		idLogic_configurazioneTransazione_configurazioneServizioAzione = configurazioneTransazione.getIdConfigurazioneServizioAzione();
		if(idLogic_configurazioneTransazione_configurazioneServizioAzione!=null){
			if(idMappingResolutionBehaviour==null ||
				(org.openspcoop2.generic_project.beans.IDMappingBehaviour.ENABLED.equals(idMappingResolutionBehaviour))){
				id_configurazioneTransazione_configurazioneServizioAzione = 
						JDBCConfigurazioneServizioAzioneBaseLib.getIdConfigurazioneServizioAzione(connection, jdbcProperties, log, 
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getAzione(),
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getIdConfigurazioneServizio().getAccordo(), 
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getIdConfigurazioneServizio().getTipoSoggettoReferente(), 
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getIdConfigurazioneServizio().getNomeSoggettoReferente(), 
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getIdConfigurazioneServizio().getVersione(), 
								idLogic_configurazioneTransazione_configurazioneServizioAzione.getIdConfigurazioneServizio().getServizio(), 
								true);
			}
			else if(org.openspcoop2.generic_project.beans.IDMappingBehaviour.USE_TABLE_ID.equals(idMappingResolutionBehaviour)){
				id_configurazioneTransazione_configurazioneServizioAzione = idLogic_configurazioneTransazione_configurazioneServizioAzione.getId();
				if(id_configurazioneTransazione_configurazioneServizioAzione==null || id_configurazioneTransazione_configurazioneServizioAzione<=0){
					throw new Exception("Logic id not contains table id");
				}
			}
		}


		// Object configurazioneTransazione
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		boolean isUpdate_configurazioneTransazione = true;
		java.util.List<JDBCObject> lstObjects_configurazioneTransazione = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().ENABLED,false), "?");
		lstObjects_configurazioneTransazione.add(new JDBCObject(configurazioneTransazione.getEnabled(), ConfigurazioneTransazione.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_configurazioneTransazione.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_configurazioneTransazione) {
			// Update configurazioneTransazione
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_configurazioneTransazione.toArray(new JDBCObject[]{}));
		}
		// for configurazioneTransazione_configurazioneTransazionePlugin

		java.util.List<Long> ids_configurazioneTransazione_configurazioneTransazionePlugin_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object configurazioneTransazione_configurazioneTransazionePlugin_object : configurazioneTransazione.getConfigurazioneTransazionePluginList()) {
			ConfigurazioneTransazionePlugin configurazioneTransazione_configurazioneTransazionePlugin = (ConfigurazioneTransazionePlugin) configurazioneTransazione_configurazioneTransazionePlugin_object;
			
			// Object _plugin
			if(configurazioneTransazione_configurazioneTransazionePlugin.getPlugin()==null || configurazioneTransazione_configurazioneTransazionePlugin.getPlugin().getClassName()==null){
				throw new ServiceException("ClassName del plugin non fornito");
			}
			Long id_configurazioneTransazione_configurazioneTransazionePlugin_plugin = JDBCPluginsBaseLib.getIdPlugin(connection, jdbcProperties, log, TipoPlugin.TRANSAZIONE, configurazioneTransazione_configurazioneTransazionePlugin.getPlugin().getClassName(), true);
			
			if(configurazioneTransazione_configurazioneTransazionePlugin.getId() == null || configurazioneTransazione_configurazioneTransazionePlugin.getId().longValue() <= 0) {

				long id = configurazioneTransazione.getId();		
				
				// Object configurazioneTransazione_configurazioneTransazionePlugin
				ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin.addInsertField("id_plugin","?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin.addInsertField("id_configurazione_transazione","?");

				// Insert configurazioneTransazione_configurazioneTransazionePlugin
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazione_configurazioneTransazionePlugin = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN);
				long id_configurazioneTransazione_configurazioneTransazionePlugin = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazionePlugin, keyGenerator_configurazioneTransazione_configurazioneTransazionePlugin, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazionePlugin.getIdConfigurazioneTransazionePlugin(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazionePlugin.getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneTransazione_configurazioneTransazionePlugin_plugin,Long.class),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				configurazioneTransazione_configurazioneTransazionePlugin.setId(id_configurazioneTransazione_configurazioneTransazionePlugin);

				ids_configurazioneTransazione_configurazioneTransazionePlugin_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazionePlugin.getId());
			} else {

				// Object configurazioneTransazione_configurazioneTransazionePlugin
				ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.setANDLogicOperator(true);
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.addUpdateTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
				boolean isUpdate_configurazioneTransazione_configurazioneTransazionePlugin = true;
				java.util.List<JDBCObject> lstObjects_configurazioneTransazione_configurazioneTransazionePlugin = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazionePlugin.add(new JDBCObject(configurazioneTransazione_configurazioneTransazionePlugin.getIdConfigurazioneTransazionePlugin(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ID_CONFIGURAZIONE_TRANSAZIONE_PLUGIN.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazionePlugin.add(new JDBCObject(configurazioneTransazione_configurazioneTransazionePlugin.getEnabled(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN.ENABLED.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.addUpdateField("id_plugin", "?");
				lstObjects_configurazioneTransazione_configurazioneTransazionePlugin.add(new JDBCObject(id_configurazioneTransazione_configurazioneTransazionePlugin_plugin, Long.class));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.addWhereCondition("id=?");
				ids_configurazioneTransazione_configurazioneTransazionePlugin_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazionePlugin.getId());
				lstObjects_configurazioneTransazione_configurazioneTransazionePlugin.add(new JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazionePlugin.getId()),Long.class));

				if(isUpdate_configurazioneTransazione_configurazioneTransazionePlugin) {
					// Update configurazioneTransazione_configurazioneTransazionePlugin
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazionePlugin.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_configurazioneTransazione_configurazioneTransazionePlugin.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for configurazioneTransazione_configurazioneTransazionePlugin

		// elimino tutte le occorrenze di configurazioneTransazione_configurazioneTransazionePlugin non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
		java.util.List<JDBCObject> jdbcObjects_configurazioneTransazione_configurazioneTransazionePlugin_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList.addWhereCondition("id_configurazione_transazione=?");
		jdbcObjects_configurazioneTransazione_configurazioneTransazionePlugin_delete.add(new JDBCObject(configurazioneTransazione.getId(), Long.class));

		StringBuilder marks_configurazioneTransazione_configurazioneTransazionePlugin = new StringBuilder();
		if(ids_configurazioneTransazione_configurazioneTransazionePlugin_da_non_eliminare.size() > 0) {
			for(Long ids : ids_configurazioneTransazione_configurazioneTransazionePlugin_da_non_eliminare) {
				if(marks_configurazioneTransazione_configurazioneTransazionePlugin.length() > 0) {
					marks_configurazioneTransazione_configurazioneTransazionePlugin.append(",");
				}
				marks_configurazioneTransazione_configurazioneTransazionePlugin.append("?");
				jdbcObjects_configurazioneTransazione_configurazioneTransazionePlugin_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList.addWhereCondition("id NOT IN ("+marks_configurazioneTransazione_configurazioneTransazionePlugin.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_configurazioneTransazionePlugin_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_configurazioneTransazione_configurazioneTransazionePlugin_delete.toArray(new JDBCObject[]{}));

		// for configurazioneTransazione_configurazioneTransazioneStato

		java.util.List<Long> ids_configurazioneTransazione_configurazioneTransazioneStato_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object configurazioneTransazione_configurazioneTransazioneStato_object : configurazioneTransazione.getConfigurazioneTransazioneStatoList()) {
			ConfigurazioneTransazioneStato configurazioneTransazione_configurazioneTransazioneStato = (ConfigurazioneTransazioneStato) configurazioneTransazione_configurazioneTransazioneStato_object;
			if(configurazioneTransazione_configurazioneTransazioneStato.getId() == null || configurazioneTransazione_configurazioneTransazioneStato.getId().longValue() <= 0) {

				long id = configurazioneTransazione.getId();			

				// Object configurazioneTransazione_configurazioneTransazioneStato
				ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato.addInsertField("id_configurazione_transazione","?");

				// Insert configurazioneTransazione_configurazioneTransazioneStato
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazione_configurazioneTransazioneStato = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO);
				long id_configurazioneTransazione_configurazioneTransazioneStato = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneStato, keyGenerator_configurazioneTransazione_configurazioneTransazioneStato, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getNome(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getTipoControllo(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getTipoMessaggio(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getValore(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getXpath(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				configurazioneTransazione_configurazioneTransazioneStato.setId(id_configurazioneTransazione_configurazioneTransazioneStato);

				ids_configurazioneTransazione_configurazioneTransazioneStato_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazioneStato.getId());
			} else {


				// Object configurazioneTransazione_configurazioneTransazioneStato
				ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.setANDLogicOperator(true);
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
				boolean isUpdate_configurazioneTransazione_configurazioneTransazioneStato = true;
				java.util.List<JDBCObject> lstObjects_configurazioneTransazione_configurazioneTransazioneStato = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getEnabled(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.ENABLED.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getNome(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getTipoControllo(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_CONTROLLO.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getTipoMessaggio(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.TIPO_MESSAGGIO.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getValore(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.VALORE.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneStato.getXpath(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.XPATH.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.addWhereCondition("id=?");
				ids_configurazioneTransazione_configurazioneTransazioneStato_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazioneStato.getId());
				lstObjects_configurazioneTransazione_configurazioneTransazioneStato.add(new JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazioneStato.getId()),Long.class));

				if(isUpdate_configurazioneTransazione_configurazioneTransazioneStato) {
					// Update configurazioneTransazione_configurazioneTransazioneStato
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneStato.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_configurazioneTransazione_configurazioneTransazioneStato.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for configurazioneTransazione_configurazioneTransazioneStato

		// elimino tutte le occorrenze di configurazioneTransazione_configurazioneTransazioneStato non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
		java.util.List<JDBCObject> jdbcObjects_configurazioneTransazione_configurazioneTransazioneStato_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList.addWhereCondition("id_configurazione_transazione=?");
		jdbcObjects_configurazioneTransazione_configurazioneTransazioneStato_delete.add(new JDBCObject(configurazioneTransazione.getId(), Long.class));

		StringBuilder marks_configurazioneTransazione_configurazioneTransazioneStato = new StringBuilder();
		if(ids_configurazioneTransazione_configurazioneTransazioneStato_da_non_eliminare.size() > 0) {
			for(Long ids : ids_configurazioneTransazione_configurazioneTransazioneStato_da_non_eliminare) {
				if(marks_configurazioneTransazione_configurazioneTransazioneStato.length() > 0) {
					marks_configurazioneTransazione_configurazioneTransazioneStato.append(",");
				}
				marks_configurazioneTransazione_configurazioneTransazioneStato.append("?");
				jdbcObjects_configurazioneTransazione_configurazioneTransazioneStato_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList.addWhereCondition("id NOT IN ("+marks_configurazioneTransazione_configurazioneTransazioneStato.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_configurazioneTransazioneStato_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_configurazioneTransazione_configurazioneTransazioneStato_delete.toArray(new JDBCObject[]{}));

		// for configurazioneTransazione_configurazioneTransazioneRisorsaContenuto

		java.util.List<Long> ids_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object : configurazioneTransazione.getConfigurazioneTransazioneRisorsaContenutoList()) {
			ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = (ConfigurazioneTransazioneRisorsaContenuto) configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object;
			if(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId() == null || configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId().longValue() <= 0) {

				long id = configurazioneTransazione.getId();	

				// Object _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
				ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addSelectField("id");
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.setANDLogicOperator(true);
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,true)+"=?");
				sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition("id_configurazione_transazione=?");

				// Recupero _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
				org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] {
						new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato(), String.class),
						new JDBCObject(configurazioneTransazione.getId(),Long.class)
				};
				Long id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
				try{	
					id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato=	(Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.createSQLQuery(), jdbcProperties.isShowSql(),
								Long.class, searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato);
				}catch(NotFoundException e){
					// risorsa con stato == null o '*'
					id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
				}


				// Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
				ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED,false),"?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField("id_configurazione_stato","?");
				sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addInsertField("id_conf_transazione","?");

				// Insert configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = this.getConfigurazioneTransazioneFetch().getKeyGeneratorObject(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO);
				long id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto, keyGenerator_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoCompressione(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getCarattereMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getNumeroCaratteriMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getPosizionamentoMaschera(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoMascheramento(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getNome(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoMessaggio(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getXpath(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getStatEnabled(),ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato,Long.class),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setId(id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto);

				ids_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId());
			} else {

				// Object _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
				Long id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
				if(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato()!=null){
					ISQLQueryObject sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = sqlQueryObjectInsert.newSQLQueryObject();
					sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
					sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addSelectField("id");
					sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.setANDLogicOperator(true);
					sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO.NOME,true)+"=?");
					sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.addWhereCondition("id_configurazione_transazione=?");
	
					// Recupero _configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato
					org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject [] {
							new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato(), String.class),
							new JDBCObject(configurazioneTransazione.getId(),Long.class)
					};
					try{	
						id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato=	(Long) jdbcUtilities.executeQuerySingleResult(sqlQueryObjectInsert_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato.createSQLQuery(), jdbcProperties.isShowSql(),
									Long.class, searchParams_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato);
					}catch(NotFoundException e){
						// risorsa con stato == null o '*'
						id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato = null;
					}
				}
				
				// Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
				ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setANDLogicOperator(true);
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
				boolean isUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = true;
				java.util.List<JDBCObject> lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_ANONIMIZZAZIONE.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ABILITA_COMPRESSIONE.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoCompressione(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_COMPRESSIONE.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getCarattereMaschera(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.CARATTERE_MASCHERA.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getNumeroCaratteriMaschera(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NUMERO_CARATTERI_MASCHERA.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getPosizionamentoMaschera(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.POSIZIONAMENTO_MASCHERA.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoMascheramento(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MASCHERAMENTO.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getEnabled(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.ENABLED.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getNome(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.NOME.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getTipoMessaggio(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.TIPO_MESSAGGIO.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getXpath(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.XPATH.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField(this.getConfigurazioneTransazioneFieldConverter().toColumn(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED,false), "?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getStatEnabled(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO.STAT_ENABLED.getFieldType()));
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addUpdateField("id_configurazione_stato","?");
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(id_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_configurazioneTransazioneStato,Long.class));
				
				sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addWhereCondition("id=?");
				ids_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_da_non_eliminare.add(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId());
				lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.add(new JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId()),Long.class));

				if(isUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto) {
					// Update configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for configurazioneTransazione_configurazioneTransazioneRisorsaContenuto

		// elimino tutte le occorrenze di configurazioneTransazione_configurazioneTransazioneRisorsaContenuto non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
		java.util.List<JDBCObject> jdbcObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList.addWhereCondition("id_conf_transazione=?");
		jdbcObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_delete.add(new JDBCObject(configurazioneTransazione.getId(), Long.class));

		StringBuilder marks_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = new StringBuilder();
		if(ids_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_da_non_eliminare.size() > 0) {
			for(Long ids : ids_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_da_non_eliminare) {
				if(marks_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.length() > 0) {
					marks_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.append(",");
				}
				marks_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.append("?");
				jdbcObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList.addWhereCondition("id NOT IN ("+marks_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_configurazioneTransazioneRisorsaContenuto_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_delete.toArray(new JDBCObject[]{}));


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneTransazioneFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneTransazioneFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getConfigurazioneTransazioneFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneTransazioneFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneTransazioneFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getConfigurazioneTransazioneFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione oldId, ConfigurazioneTransazione configurazioneTransazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, configurazioneTransazione,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneTransazione,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, ConfigurazioneTransazione configurazioneTransazione, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, configurazioneTransazione,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, configurazioneTransazione,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, ConfigurazioneTransazione configurazioneTransazione) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (configurazioneTransazione.getId()!=null) && (configurazioneTransazione.getId()>0) ){
			longId = configurazioneTransazione.getId();
		}
		else{
			IdConfigurazioneTransazione idConfigurazioneTransazione = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,configurazioneTransazione);
			longId = this.findIdConfigurazioneTransazione(jdbcProperties,log,connection,sqlQueryObject,idConfigurazioneTransazione,false);
			if(longId == null){
				return; // entry not exists
			}
		}		
		
		this._delete(jdbcProperties, log, connection, sqlQueryObject, longId);
		
	}

	private void _delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Long id) throws NotImplementedException,ServiceException,Exception {
	
		if(id!=null && id.longValue()<=0){
			throw new ServiceException("Id is less equals 0");
		}
		
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObject.newSQLQueryObject();
		

		//Recupero oggetto _configurazioneTransazione_configurazioneTransazionePlugin
		ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin_getToDelete.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin_getToDelete.addWhereCondition("id_configurazione_transazione=?");
		java.util.List<Object> configurazioneTransazione_configurazioneTransazionePlugin_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN, this.getConfigurazioneTransazioneFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for configurazioneTransazione_configurazioneTransazionePlugin
		for (Object configurazioneTransazione_configurazioneTransazionePlugin_object : configurazioneTransazione_configurazioneTransazionePlugin_toDelete_list) {
			ConfigurazioneTransazionePlugin configurazioneTransazione_configurazioneTransazionePlugin = (ConfigurazioneTransazionePlugin) configurazioneTransazione_configurazioneTransazionePlugin_object;

			// Object configurazioneTransazione_configurazioneTransazionePlugin
			ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin.setANDLogicOperator(true);
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_PLUGIN));
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin.addWhereCondition("id=?");

			// Delete configurazioneTransazione_configurazioneTransazionePlugin
			if(configurazioneTransazione_configurazioneTransazionePlugin != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazionePlugin.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazionePlugin.getId()),Long.class));
			}
		} // fine for configurazioneTransazione_configurazioneTransazionePlugin

		//Recupero oggetto _configurazioneTransazione_configurazioneTransazioneStato
		ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato_getToDelete.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato_getToDelete.addWhereCondition("id_configurazione_transazione=?");
		java.util.List<Object> configurazioneTransazione_configurazioneTransazioneStato_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO, this.getConfigurazioneTransazioneFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for configurazioneTransazione_configurazioneTransazioneStato
		for (Object configurazioneTransazione_configurazioneTransazioneStato_object : configurazioneTransazione_configurazioneTransazioneStato_toDelete_list) {
			ConfigurazioneTransazioneStato configurazioneTransazione_configurazioneTransazioneStato = (ConfigurazioneTransazioneStato) configurazioneTransazione_configurazioneTransazioneStato_object;

			// Object configurazioneTransazione_configurazioneTransazioneStato
			ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato.setANDLogicOperator(true);
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_STATO));
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato.addWhereCondition("id=?");

			// Delete configurazioneTransazione_configurazioneTransazioneStato
			if(configurazioneTransazione_configurazioneTransazioneStato != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneStato.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazioneStato.getId()),Long.class));
			}
		} // fine for configurazioneTransazione_configurazioneTransazioneStato

		//Recupero oggetto _configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
		ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_getToDelete.addFromTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
		sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_getToDelete.addWhereCondition("id_conf_transazione=?");
		java.util.List<Object> configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO, this.getConfigurazioneTransazioneFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
		for (Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object : configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_toDelete_list) {
			ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = (ConfigurazioneTransazioneRisorsaContenuto) configurazioneTransazione_configurazioneTransazioneRisorsaContenuto_object;

			// Object configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
			ISQLQueryObject sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.setANDLogicOperator(true);
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model().CONFIGURAZIONE_TRANSAZIONE_RISORSA_CONTENUTO));
			sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.addWhereCondition("id=?");

			// Delete configurazioneTransazione_configurazioneTransazioneRisorsaContenuto
			if(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(configurazioneTransazione_configurazioneTransazioneRisorsaContenuto.getId()),Long.class));
			}
		} // fine for configurazioneTransazione_configurazioneTransazioneRisorsaContenuto

		// Object configurazioneTransazione
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getConfigurazioneTransazioneFieldConverter().toTable(ConfigurazioneTransazione.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete configurazioneTransazione
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdConfigurazioneTransazione idConfigurazioneTransazione) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdConfigurazioneTransazione(jdbcProperties, log, connection, sqlQueryObject, idConfigurazioneTransazione, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getConfigurazioneTransazioneFieldConverter()));

	}

	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, JDBCExpression expression) throws NotImplementedException, ServiceException,Exception {

		java.util.List<Long> lst = this.findAllTableIds(jdbcProperties, log, connection, sqlQueryObject, new JDBCPaginatedExpression(expression));
		
		for(Long id : lst) {
			this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		}
		
		return new NonNegativeNumber(lst.size());
	
	}



	// -- DB
	
	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId) throws ServiceException, NotImplementedException, Exception {
		this._delete(jdbcProperties, log, connection, sqlQueryObject, Long.valueOf(tableId));
	}
}
