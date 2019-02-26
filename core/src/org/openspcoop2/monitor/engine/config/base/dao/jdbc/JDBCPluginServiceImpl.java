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
package org.openspcoop2.monitor.engine.config.base.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
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

import org.openspcoop2.monitor.engine.config.base.PluginFiltroCompatibilita;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.PluginServizioAzioneCompatibilita;
import org.openspcoop2.monitor.engine.config.base.PluginServizioCompatibilita;
import org.openspcoop2.monitor.engine.config.base.dao.jdbc.JDBCServiceManager;

/**     
 * JDBCPluginServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCPluginServiceImpl extends JDBCPluginServiceSearchImpl
	implements IJDBCServiceCRUDWithId<Plugin, IdPlugin, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Plugin plugin, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object plugin
		sqlQueryObjectInsert.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		sqlQueryObjectInsert.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().CLASS_NAME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().DESCRIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().LABEL,false),"?");

		// Insert plugin
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getPluginFetch().getKeyGeneratorObject(Plugin.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getTipo(),Plugin.model().TIPO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getClassName(),Plugin.model().CLASS_NAME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getDescrizione(),Plugin.model().DESCRIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getLabel(),Plugin.model().LABEL.getFieldType())
		);
		plugin.setId(id);

		// for plugin
		for (int i = 0; i < plugin.getPluginFiltroCompatibilitaList().size(); i++) {


			// Object plugin.getPluginFiltroCompatibilitaList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_pluginFiltroCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE,false),"?");
			sqlQueryObjectInsert_pluginFiltroCompatibilita.addInsertField("id_plugin","?");

			// Insert plugin.getPluginFiltroCompatibilitaList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_pluginFiltroCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA);
			long id_pluginFiltroCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_pluginFiltroCompatibilita, keyGenerator_pluginFiltroCompatibilita, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getTipoMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getNomeMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getIdportaMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getTipoDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getNomeDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getIdportaDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getTipoServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getNomeServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getVersioneServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginFiltroCompatibilitaList().get(i).getAzione(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			plugin.getPluginFiltroCompatibilitaList().get(i).setId(id_pluginFiltroCompatibilita);
		} // fine for 

		// for plugin
		for (int i = 0; i < plugin.getPluginServizioCompatibilitaList().size(); i++) {


			// Object plugin.getPluginServizioCompatibilitaList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_pluginServizioCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_pluginServizioCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
			sqlQueryObjectInsert_pluginServizioCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO,false),"?");
			sqlQueryObjectInsert_pluginServizioCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO,false),"?");
			sqlQueryObjectInsert_pluginServizioCompatibilita.addInsertField("id_plugin","?");

			// Insert plugin.getPluginServizioCompatibilitaList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_pluginServizioCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA);
			long id_pluginServizioCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_pluginServizioCompatibilita, keyGenerator_pluginServizioCompatibilita, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginServizioCompatibilitaList().get(i).getUriAccordo(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginServizioCompatibilitaList().get(i).getServizio(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			plugin.getPluginServizioCompatibilitaList().get(i).setId(id_pluginServizioCompatibilita);

			// for plugin.getPluginServizioCompatibilitaList().get(i)
			for (int i_pluginServizioCompatibilita = 0; i_pluginServizioCompatibilita < plugin.getPluginServizioCompatibilitaList().get(i).getPluginServizioAzioneCompatibilitaList().size(); i_pluginServizioCompatibilita++) {


				// Object plugin.getPluginServizioCompatibilitaList().get(i).getPluginServizioAzioneCompatibilitaList().get(i_pluginServizioCompatibilita)
				ISQLQueryObject sqlQueryObjectInsert_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
				sqlQueryObjectInsert_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE,false),"?");
				sqlQueryObjectInsert_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField("id_plugin_servizio_comp","?");

				// Insert plugin.getPluginServizioCompatibilitaList().get(i).getPluginServizioAzioneCompatibilitaList().get(i_pluginServizioCompatibilita)
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA);
				long id_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, keyGenerator_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin.getPluginServizioCompatibilitaList().get(i).getPluginServizioAzioneCompatibilitaList().get(i_pluginServizioCompatibilita).getAzione(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_pluginServizioCompatibilita),Long.class)
				);
				plugin.getPluginServizioCompatibilitaList().get(i).getPluginServizioAzioneCompatibilitaList().get(i_pluginServizioCompatibilita).setId(id_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita);
			} // fine for _pluginServizioCompatibilita
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin oldId, Plugin plugin, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = plugin.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: plugin.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			plugin.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, plugin, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Plugin plugin, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object plugin
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		boolean isUpdate_plugin = true;
		java.util.List<JDBCObject> lstObjects_plugin = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().TIPO,false), "?");
		lstObjects_plugin.add(new JDBCObject(plugin.getTipo(), Plugin.model().TIPO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().CLASS_NAME,false), "?");
		lstObjects_plugin.add(new JDBCObject(plugin.getClassName(), Plugin.model().CLASS_NAME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().DESCRIZIONE,false), "?");
		lstObjects_plugin.add(new JDBCObject(plugin.getDescrizione(), Plugin.model().DESCRIZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().LABEL,false), "?");
		lstObjects_plugin.add(new JDBCObject(plugin.getLabel(), Plugin.model().LABEL.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects_plugin.add(new JDBCObject(tableId, Long.class));

		if(isUpdate_plugin) {
			// Update plugin
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects_plugin.toArray(new JDBCObject[]{}));
		}
		// for plugin_pluginServizioCompatibilita

		java.util.List<Long> ids_plugin_pluginServizioCompatibilita_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object plugin_pluginServizioCompatibilita_object : plugin.getPluginServizioCompatibilitaList()) {
			PluginServizioCompatibilita plugin_pluginServizioCompatibilita = (PluginServizioCompatibilita) plugin_pluginServizioCompatibilita_object;
			if(plugin_pluginServizioCompatibilita.getId() == null || plugin_pluginServizioCompatibilita.getId().longValue() <= 0) {

				long id = plugin.getId();			

				// Object plugin_pluginServizioCompatibilita
				ISQLQueryObject sqlQueryObjectInsert_plugin_pluginServizioCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_plugin_pluginServizioCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
				sqlQueryObjectInsert_plugin_pluginServizioCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO,false),"?");
				sqlQueryObjectInsert_plugin_pluginServizioCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginServizioCompatibilita.addInsertField("id_plugin","?");

				// Insert plugin_pluginServizioCompatibilita
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_plugin_pluginServizioCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA);
				long id_plugin_pluginServizioCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_plugin_pluginServizioCompatibilita, keyGenerator_plugin_pluginServizioCompatibilita, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginServizioCompatibilita.getUriAccordo(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginServizioCompatibilita.getServizio(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				plugin_pluginServizioCompatibilita.setId(id_plugin_pluginServizioCompatibilita);

				// for plugin_pluginServizioCompatibilita
				for (int i_plugin_pluginServizioCompatibilita = 0; i_plugin_pluginServizioCompatibilita < plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList().size(); i_plugin_pluginServizioCompatibilita++) {


					// Object plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList().get(i_plugin_pluginServizioCompatibilita)
					ISQLQueryObject sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
					sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
					sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE,false),"?");
					sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField("id_plugin_servizio_comp","?");

					// Insert plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList().get(i_plugin_pluginServizioCompatibilita)
					org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA);
					long id_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, keyGenerator_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, jdbcProperties.isShowSql(),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList().get(i_plugin_pluginServizioCompatibilita).getAzione(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType()),
						new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_plugin_pluginServizioCompatibilita),Long.class)
					);
					plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList().get(i_plugin_pluginServizioCompatibilita).setId(id_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita);
				} // fine for _plugin_pluginServizioCompatibilita

				ids_plugin_pluginServizioCompatibilita_da_non_eliminare.add(plugin_pluginServizioCompatibilita.getId());
			} else {


				// Object plugin_pluginServizioCompatibilita
				ISQLQueryObject sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.setANDLogicOperator(true);
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.addUpdateTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
				boolean isUpdate_plugin_pluginServizioCompatibilita = true;
				java.util.List<JDBCObject> lstObjects_plugin_pluginServizioCompatibilita = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO,false), "?");
				lstObjects_plugin_pluginServizioCompatibilita.add(new JDBCObject(plugin_pluginServizioCompatibilita.getUriAccordo(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.URI_ACCORDO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO,false), "?");
				lstObjects_plugin_pluginServizioCompatibilita.add(new JDBCObject(plugin_pluginServizioCompatibilita.getServizio(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.addWhereCondition("id=?");
				ids_plugin_pluginServizioCompatibilita_da_non_eliminare.add(plugin_pluginServizioCompatibilita.getId());
				lstObjects_plugin_pluginServizioCompatibilita.add(new JDBCObject(Long.valueOf(plugin_pluginServizioCompatibilita.getId()),Long.class));

				if(isUpdate_plugin_pluginServizioCompatibilita) {
					// Update plugin_pluginServizioCompatibilita
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_plugin_pluginServizioCompatibilita.toArray(new JDBCObject[]{}));
				}
				// for plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita

				java.util.List<Long> ids_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_da_non_eliminare = new java.util.ArrayList<Long>();
				for (Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object : plugin_pluginServizioCompatibilita.getPluginServizioAzioneCompatibilitaList()) {
					PluginServizioAzioneCompatibilita plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = (PluginServizioAzioneCompatibilita) plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object;
					if(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId() == null || plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId().longValue() <= 0) {

						long id_plugin_pluginServizioCompatibilita = plugin_pluginServizioCompatibilita.getId();					

						// Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
						ISQLQueryObject sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
						sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
						sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE,false),"?");
						sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addInsertField("id_plugin_servizio_comp","?");

						// Insert plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
						org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA);
						long id_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, keyGenerator_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita, jdbcProperties.isShowSql(),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getAzione(),Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType()),
							new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id_plugin_pluginServizioCompatibilita),Long.class)
						);
						plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.setId(id_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita);

						ids_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_da_non_eliminare.add(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId());
					} else {


						// Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
						ISQLQueryObject sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectUpdate.newSQLQueryObject();
						sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.setANDLogicOperator(true);
						sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addUpdateTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
						boolean isUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = true;
						java.util.List<JDBCObject> lstObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = new java.util.ArrayList<JDBCObject>();
						sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE,false), "?");
						lstObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.add(new JDBCObject(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getAzione(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA.AZIONE.getFieldType()));
						sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addWhereCondition("id=?");
						ids_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_da_non_eliminare.add(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId());
						lstObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.add(new JDBCObject(Long.valueOf(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId()),Long.class));

						if(isUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita) {
							// Update plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
							jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.createSQLUpdate(), jdbcProperties.isShowSql(), 
								lstObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.toArray(new JDBCObject[]{}));
						}
					}
				} // fine for plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita

				// elimino tutte le occorrenze di plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita non presenti nell'update

				ISQLQueryObject sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList.setANDLogicOperator(true);
				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
				java.util.List<JDBCObject> jdbcObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_delete = new java.util.ArrayList<JDBCObject>();

				sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList.addWhereCondition("id_plugin_servizio_comp=?");
				jdbcObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_delete.add(new JDBCObject(plugin_pluginServizioCompatibilita.getId(), Long.class));

				StringBuffer marks_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = new StringBuffer();
				if(ids_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_da_non_eliminare.size() > 0) {
					for(Long ids : ids_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_da_non_eliminare) {
						if(marks_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.length() > 0) {
							marks_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.append(",");
						}
						marks_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.append("?");
						jdbcObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_delete.add(new JDBCObject(ids, Long.class));

					}
					sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList.addWhereCondition("id NOT IN ("+marks_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.toString()+")");
				}

				jdbcUtilities.execute(sqlQueryObjectUpdate_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_delete.toArray(new JDBCObject[]{}));

			}
		} // fine for plugin_pluginServizioCompatibilita

		// elimino tutte le occorrenze di plugin_pluginServizioCompatibilita non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
		java.util.List<JDBCObject> jdbcObjects_plugin_pluginServizioCompatibilita_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList.addWhereCondition("id_plugin=?");
		jdbcObjects_plugin_pluginServizioCompatibilita_delete.add(new JDBCObject(plugin.getId(), Long.class));

		StringBuffer marks_plugin_pluginServizioCompatibilita = new StringBuffer();
		if(ids_plugin_pluginServizioCompatibilita_da_non_eliminare.size() > 0) {
			for(Long ids : ids_plugin_pluginServizioCompatibilita_da_non_eliminare) {
				if(marks_plugin_pluginServizioCompatibilita.length() > 0) {
					marks_plugin_pluginServizioCompatibilita.append(",");
				}
				marks_plugin_pluginServizioCompatibilita.append("?");
				jdbcObjects_plugin_pluginServizioCompatibilita_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList.addWhereCondition("id NOT IN ("+marks_plugin_pluginServizioCompatibilita.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_pluginServizioCompatibilita_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_plugin_pluginServizioCompatibilita_delete.toArray(new JDBCObject[]{}));

		// for plugin_pluginFiltroCompatibilita

		java.util.List<Long> ids_plugin_pluginFiltroCompatibilita_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object plugin_pluginFiltroCompatibilita_object : plugin.getPluginFiltroCompatibilitaList()) {
			PluginFiltroCompatibilita plugin_pluginFiltroCompatibilita = (PluginFiltroCompatibilita) plugin_pluginFiltroCompatibilita_object;
			if(plugin_pluginFiltroCompatibilita.getId() == null || plugin_pluginFiltroCompatibilita.getId().longValue() <= 0) {

				long id = plugin.getId();			

				// Object plugin_pluginFiltroCompatibilita
				ISQLQueryObject sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE,false),"?");
				sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita.addInsertField("id_plugin","?");

				// Insert plugin_pluginFiltroCompatibilita
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_plugin_pluginFiltroCompatibilita = this.getPluginFetch().getKeyGeneratorObject(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA);
				long id_plugin_pluginFiltroCompatibilita = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_plugin_pluginFiltroCompatibilita, keyGenerator_plugin_pluginFiltroCompatibilita, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getTipoMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getNomeMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getIdportaMittente(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getTipoDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getNomeDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getIdportaDestinatario(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getTipoServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getNomeServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getVersioneServizio(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(plugin_pluginFiltroCompatibilita.getAzione(),Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				plugin_pluginFiltroCompatibilita.setId(id_plugin_pluginFiltroCompatibilita);

				ids_plugin_pluginFiltroCompatibilita_da_non_eliminare.add(plugin_pluginFiltroCompatibilita.getId());
			} else {


				// Object plugin_pluginFiltroCompatibilita
				ISQLQueryObject sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.setANDLogicOperator(true);
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
				boolean isUpdate_plugin_pluginFiltroCompatibilita = true;
				java.util.List<JDBCObject> lstObjects_plugin_pluginFiltroCompatibilita = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getTipoMittente(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_MITTENTE.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getNomeMittente(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_MITTENTE.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getIdportaMittente(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_MITTENTE.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getTipoDestinatario(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_DESTINATARIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getNomeDestinatario(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_DESTINATARIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getIdportaDestinatario(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.IDPORTA_DESTINATARIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getTipoServizio(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.TIPO_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getNomeServizio(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.NOME_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getVersioneServizio(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.VERSIONE_SERVIZIO.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addUpdateField(this.getPluginFieldConverter().toColumn(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE,false), "?");
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(plugin_pluginFiltroCompatibilita.getAzione(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA.AZIONE.getFieldType()));
				sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.addWhereCondition("id=?");
				ids_plugin_pluginFiltroCompatibilita_da_non_eliminare.add(plugin_pluginFiltroCompatibilita.getId());
				lstObjects_plugin_pluginFiltroCompatibilita.add(new JDBCObject(Long.valueOf(plugin_pluginFiltroCompatibilita.getId()),Long.class));

				if(isUpdate_plugin_pluginFiltroCompatibilita) {
					// Update plugin_pluginFiltroCompatibilita
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_plugin_pluginFiltroCompatibilita.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_plugin_pluginFiltroCompatibilita.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for plugin_pluginFiltroCompatibilita

		// elimino tutte le occorrenze di plugin_pluginFiltroCompatibilita non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
		java.util.List<JDBCObject> jdbcObjects_plugin_pluginFiltroCompatibilita_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList.addWhereCondition("id_plugin=?");
		jdbcObjects_plugin_pluginFiltroCompatibilita_delete.add(new JDBCObject(plugin.getId(), Long.class));

		StringBuffer marks_plugin_pluginFiltroCompatibilita = new StringBuffer();
		if(ids_plugin_pluginFiltroCompatibilita_da_non_eliminare.size() > 0) {
			for(Long ids : ids_plugin_pluginFiltroCompatibilita_da_non_eliminare) {
				if(marks_plugin_pluginFiltroCompatibilita.length() > 0) {
					marks_plugin_pluginFiltroCompatibilita.append(",");
				}
				marks_plugin_pluginFiltroCompatibilita.append("?");
				jdbcObjects_plugin_pluginFiltroCompatibilita_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList.addWhereCondition("id NOT IN ("+marks_plugin_pluginFiltroCompatibilita.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_pluginFiltroCompatibilita_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_plugin_pluginFiltroCompatibilita_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getPluginFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getPluginFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getPluginFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getPluginFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getPluginFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getPluginFieldConverter().toTable(Plugin.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getPluginFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin oldId, Plugin plugin, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, plugin,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, plugin,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Plugin plugin, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, plugin,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, plugin,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Plugin plugin) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (plugin.getId()!=null) && (plugin.getId()>0) ){
			longId = plugin.getId();
		}
		else{
			IdPlugin idPlugin = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,plugin);
			longId = this.findIdPlugin(jdbcProperties,log,connection,sqlQueryObject,idPlugin,false);
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
		

		//Recupero oggetto _plugin_pluginServizioCompatibilita
		ISQLQueryObject sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_getToDelete.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
		sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_getToDelete.addWhereCondition("id_plugin=?");
		java.util.List<Object> plugin_pluginServizioCompatibilita_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA, this.getPluginFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for plugin_pluginServizioCompatibilita
		for (Object plugin_pluginServizioCompatibilita_object : plugin_pluginServizioCompatibilita_toDelete_list) {
			PluginServizioCompatibilita plugin_pluginServizioCompatibilita = (PluginServizioCompatibilita) plugin_pluginServizioCompatibilita_object;

			//Recupero oggetto _plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
			ISQLQueryObject sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_getToDelete.setANDLogicOperator(true);
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_getToDelete.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_getToDelete.addWhereCondition("id_plugin_servizio_comp=?");
			java.util.List<Object> plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA, this.getPluginFetch(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(plugin_pluginServizioCompatibilita.getId()),Long.class));

			// for plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
			for (Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object : plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_toDelete_list) {
				PluginServizioAzioneCompatibilita plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = (PluginServizioAzioneCompatibilita) plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita_object;

				// Object plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
				ISQLQueryObject sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita = sqlQueryObjectDelete.newSQLQueryObject();
				sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.setANDLogicOperator(true);
				sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA.PLUGIN_SERVIZIO_AZIONE_COMPATIBILITA));
				sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.addWhereCondition("id=?");

				// Delete plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita
				if(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita != null){
					jdbcUtilities.execute(sqlQueryObjectDelete_plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.createSQLDelete(), jdbcProperties.isShowSql(), 
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita.getId()),Long.class));
				}
			} // fine for plugin_pluginServizioCompatibilita_pluginServizioAzioneCompatibilita

			// Object plugin_pluginServizioCompatibilita
			ISQLQueryObject sqlQueryObjectDelete_plugin_pluginServizioCompatibilita = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita.setANDLogicOperator(true);
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_SERVIZIO_COMPATIBILITA));
			sqlQueryObjectDelete_plugin_pluginServizioCompatibilita.addWhereCondition("id=?");

			// Delete plugin_pluginServizioCompatibilita
			if(plugin_pluginServizioCompatibilita != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_plugin_pluginServizioCompatibilita.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(plugin_pluginServizioCompatibilita.getId()),Long.class));
			}
		} // fine for plugin_pluginServizioCompatibilita

		//Recupero oggetto _plugin_pluginFiltroCompatibilita
		ISQLQueryObject sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita_getToDelete.addFromTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
		sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita_getToDelete.addWhereCondition("id_plugin=?");
		java.util.List<Object> plugin_pluginFiltroCompatibilita_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), Plugin.model().PLUGIN_FILTRO_COMPATIBILITA, this.getPluginFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for plugin_pluginFiltroCompatibilita
		for (Object plugin_pluginFiltroCompatibilita_object : plugin_pluginFiltroCompatibilita_toDelete_list) {
			PluginFiltroCompatibilita plugin_pluginFiltroCompatibilita = (PluginFiltroCompatibilita) plugin_pluginFiltroCompatibilita_object;

			// Object plugin_pluginFiltroCompatibilita
			ISQLQueryObject sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita.setANDLogicOperator(true);
			sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model().PLUGIN_FILTRO_COMPATIBILITA));
			sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita.addWhereCondition("id=?");

			// Delete plugin_pluginFiltroCompatibilita
			if(plugin_pluginFiltroCompatibilita != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_plugin_pluginFiltroCompatibilita.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(plugin_pluginFiltroCompatibilita.getId()),Long.class));
			}
		} // fine for plugin_pluginFiltroCompatibilita

		// Object plugin
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getPluginFieldConverter().toTable(Plugin.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete plugin
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdPlugin idPlugin) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdPlugin(jdbcProperties, log, connection, sqlQueryObject, idPlugin, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getPluginFieldConverter()));

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
