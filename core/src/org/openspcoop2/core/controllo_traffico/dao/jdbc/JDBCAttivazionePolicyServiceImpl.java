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
package org.openspcoop2.core.controllo_traffico.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.sql.ISQLQueryObject;

import org.slf4j.Logger;

import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
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
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;

/**     
 * JDBCAttivazionePolicyServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAttivazionePolicyServiceImpl extends JDBCAttivazionePolicyServiceSearchImpl
	implements IJDBCServiceCRUDWithId<AttivazionePolicy, IdActivePolicy, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AttivazionePolicy attivazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		
		attivazionePolicy.setUpdateTime(DateManager.getDate());

		// Object attivazionePolicy
		sqlQueryObjectInsert.addInsertTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ALIAS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().UPDATE_TIME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().POSIZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().CONTINUA_VALUTAZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_POLICY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().WARNING_ONLY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().RIDEFINISCI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().VALORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TAG,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.TOKEN,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME,false),"?");

		// Insert attivazionePolicy
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getAttivazionePolicyFetch().getKeyGeneratorObject(AttivazionePolicy.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getIdActivePolicy(),AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getAlias(),AttivazionePolicy.model().ALIAS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getUpdateTime(),AttivazionePolicy.model().UPDATE_TIME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getPosizione(),AttivazionePolicy.model().POSIZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getContinuaValutazione(),AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getIdPolicy(),AttivazionePolicy.model().ID_POLICY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getEnabled(),AttivazionePolicy.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getWarningOnly(),AttivazionePolicy.model().WARNING_ONLY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getRidefinisci(),AttivazionePolicy.model().RIDEFINISCI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getValore(),AttivazionePolicy.model().VALORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getEnabled(),AttivazionePolicy.model().FILTRO.ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getProtocollo(),AttivazionePolicy.model().FILTRO.PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getRuoloPorta(),AttivazionePolicy.model().FILTRO.RUOLO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getNomePorta(),AttivazionePolicy.model().FILTRO.NOME_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getTipoFruitore(),AttivazionePolicy.model().FILTRO.TIPO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getNomeFruitore(),AttivazionePolicy.model().FILTRO.NOME_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getRuoloFruitore(),AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getServizioApplicativoFruitore(),AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getTipoErogatore(),AttivazionePolicy.model().FILTRO.TIPO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getNomeErogatore(),AttivazionePolicy.model().FILTRO.NOME_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getRuoloErogatore(),AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getServizioApplicativoErogatore(),AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getTag(),AttivazionePolicy.model().FILTRO.TAG.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getTipoServizio(),AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getNomeServizio(),AttivazionePolicy.model().FILTRO.NOME_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getVersioneServizio(),AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getAzione(),AttivazionePolicy.model().FILTRO.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getInformazioneApplicativaEnabled(),AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getInformazioneApplicativaTipo(),AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getInformazioneApplicativaNome(),AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getFiltro().getInformazioneApplicativaValore(),AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getEnabled(),AttivazionePolicy.model().GROUP_BY.ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getRuoloPorta(),AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getProtocollo(),AttivazionePolicy.model().GROUP_BY.PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getFruitore(),AttivazionePolicy.model().GROUP_BY.FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getServizioApplicativoFruitore(),AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getIdentificativoAutenticato(),AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getToken(),AttivazionePolicy.model().GROUP_BY.TOKEN.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getErogatore(),AttivazionePolicy.model().GROUP_BY.EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getServizioApplicativoErogatore(),AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getServizio(),AttivazionePolicy.model().GROUP_BY.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getAzione(),AttivazionePolicy.model().GROUP_BY.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getInformazioneApplicativaEnabled(),AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getInformazioneApplicativaTipo(),AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(attivazionePolicy.getGroupBy().getInformazioneApplicativaNome(),AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME.getFieldType())
		);
		attivazionePolicy.setId(id);

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy oldId, AttivazionePolicy attivazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = attivazionePolicy.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: attivazionePolicy.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			attivazionePolicy.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, attivazionePolicy, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AttivazionePolicy attivazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		

		// Check se è avvenuto effettivamente un aggiornamento
		AttivazionePolicy attivazioneLettoDaDB = this.get(jdbcProperties, log, connection, sqlQueryObjectUpdate, tableId, idMappingResolutionBehaviour);
		if(attivazioneLettoDaDB.equals(attivazionePolicy)){
			log.debug("Non sono state apportate differenze");
			return;
		}
		else{
			StringBuilder bf = new StringBuilder();
			attivazioneLettoDaDB.diff(attivazionePolicy, bf);
			log.debug("Esistono differenze: "+bf.toString());
			attivazionePolicy.setUpdateTime(DateManager.getDate());
		}
		
		// Object attivazionePolicy
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_ACTIVE_POLICY,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getIdActivePolicy(), AttivazionePolicy.model().ID_ACTIVE_POLICY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ALIAS,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getAlias(), AttivazionePolicy.model().ALIAS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().UPDATE_TIME,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getUpdateTime(), AttivazionePolicy.model().UPDATE_TIME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().POSIZIONE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getPosizione(), AttivazionePolicy.model().POSIZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().CONTINUA_VALUTAZIONE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getContinuaValutazione(), AttivazionePolicy.model().CONTINUA_VALUTAZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ID_POLICY,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getIdPolicy(), AttivazionePolicy.model().ID_POLICY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().ENABLED,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getEnabled(), AttivazionePolicy.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().WARNING_ONLY,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getWarningOnly(), AttivazionePolicy.model().WARNING_ONLY.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().RIDEFINISCI,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getRidefinisci(), AttivazionePolicy.model().RIDEFINISCI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().VALORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy.getValore(), AttivazionePolicy.model().VALORE.getFieldType()));
		AttivazionePolicyFiltro attivazionePolicy_filtro = attivazionePolicy.getFiltro();
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.ENABLED,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getEnabled(), AttivazionePolicy.model().FILTRO.ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.PROTOCOLLO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getProtocollo(), AttivazionePolicy.model().FILTRO.PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getRuoloPorta(), AttivazionePolicy.model().FILTRO.RUOLO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_PORTA,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getNomePorta(), AttivazionePolicy.model().FILTRO.NOME_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getTipoFruitore(), AttivazionePolicy.model().FILTRO.TIPO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getNomeFruitore(), AttivazionePolicy.model().FILTRO.NOME_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getRuoloFruitore(), AttivazionePolicy.model().FILTRO.RUOLO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getServizioApplicativoFruitore(), AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getTipoErogatore(), AttivazionePolicy.model().FILTRO.TIPO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getNomeErogatore(), AttivazionePolicy.model().FILTRO.NOME_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getRuoloErogatore(), AttivazionePolicy.model().FILTRO.RUOLO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getServizioApplicativoErogatore(), AttivazionePolicy.model().FILTRO.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TAG,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getTag(), AttivazionePolicy.model().FILTRO.TAG.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getTipoServizio(), AttivazionePolicy.model().FILTRO.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.NOME_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getNomeServizio(), AttivazionePolicy.model().FILTRO.NOME_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getVersioneServizio(), AttivazionePolicy.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getAzione(), AttivazionePolicy.model().FILTRO.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getInformazioneApplicativaEnabled(), AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getInformazioneApplicativaTipo(), AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getInformazioneApplicativaNome(), AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_filtro.getInformazioneApplicativaValore(), AttivazionePolicy.model().FILTRO.INFORMAZIONE_APPLICATIVA_VALORE.getFieldType()));
		AttivazionePolicyRaggruppamento attivazionePolicy_groupBy = attivazionePolicy.getGroupBy();
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.ENABLED,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getEnabled(), AttivazionePolicy.model().GROUP_BY.ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getRuoloPorta(), AttivazionePolicy.model().GROUP_BY.RUOLO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.PROTOCOLLO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getProtocollo(), AttivazionePolicy.model().GROUP_BY.PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getFruitore(), AttivazionePolicy.model().GROUP_BY.FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getServizioApplicativoFruitore(), AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getIdentificativoAutenticato(), AttivazionePolicy.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.TOKEN,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getToken(), AttivazionePolicy.model().GROUP_BY.TOKEN.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getErogatore(), AttivazionePolicy.model().GROUP_BY.EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getServizioApplicativoErogatore(), AttivazionePolicy.model().GROUP_BY.SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getServizio(), AttivazionePolicy.model().GROUP_BY.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getAzione(), AttivazionePolicy.model().GROUP_BY.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getInformazioneApplicativaEnabled(), AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getInformazioneApplicativaTipo(), AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_TIPO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAttivazionePolicyFieldConverter().toColumn(AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME,false), "?");
		lstObjects.add(new JDBCObject(attivazionePolicy_groupBy.getInformazioneApplicativaNome(), AttivazionePolicy.model().GROUP_BY.INFORMAZIONE_APPLICATIVA_NOME.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update attivazionePolicy
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}


	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAttivazionePolicyFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAttivazionePolicyFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAttivazionePolicyFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAttivazionePolicyFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAttivazionePolicyFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAttivazionePolicyFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy oldId, AttivazionePolicy attivazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, attivazionePolicy,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, attivazionePolicy,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, AttivazionePolicy attivazionePolicy, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, attivazionePolicy,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, attivazionePolicy,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, AttivazionePolicy attivazionePolicy) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (attivazionePolicy.getId()!=null) && (attivazionePolicy.getId()>0) ){
			longId = attivazionePolicy.getId();
		}
		else{
			IdActivePolicy idAttivazionePolicy = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,attivazionePolicy);
			longId = this.findIdAttivazionePolicy(jdbcProperties,log,connection,sqlQueryObject,idAttivazionePolicy,false);
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
		

		// Object attivazionePolicy
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getAttivazionePolicyFieldConverter().toTable(AttivazionePolicy.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete attivazionePolicy
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdActivePolicy idAttivazionePolicy) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdAttivazionePolicy(jdbcProperties, log, connection, sqlQueryObject, idAttivazionePolicy, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getAttivazionePolicyFieldConverter()));

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
