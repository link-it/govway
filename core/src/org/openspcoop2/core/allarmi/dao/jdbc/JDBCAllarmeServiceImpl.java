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
package org.openspcoop2.core.allarmi.dao.jdbc;

import java.sql.Connection;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeFiltro;
import org.openspcoop2.core.allarmi.AllarmeMail;
import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.AllarmeRaggruppamento;
import org.openspcoop2.core.allarmi.AllarmeScript;
import org.openspcoop2.core.allarmi.IdAllarme;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.beans.UpdateModel;
import org.openspcoop2.generic_project.dao.jdbc.IJDBCServiceCRUDWithId;
import org.openspcoop2.generic_project.dao.jdbc.JDBCExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCPaginatedExpression;
import org.openspcoop2.generic_project.dao.jdbc.JDBCServiceManagerProperties;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCUtilities;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**     
 * JDBCAllarmeServiceImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JDBCAllarmeServiceImpl extends JDBCAllarmeServiceSearchImpl
	implements IJDBCServiceCRUDWithId<Allarme, IdAllarme, JDBCServiceManager> {

	@Override
	public void create(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Allarme allarme, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {

		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
				


		// Object allarme
		sqlQueryObjectInsert.addInsertTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_ALLARME,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.ACK_MODE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_WARNING,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_ALERT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.DESTINATARI,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.SUBJECT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.BODY,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ACK_MODE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_WARNING,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_ALERT,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.COMMAND,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ARGS,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO_PRECEDENTE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().DETTAGLIO_STATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_CREATE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_UPDATE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ACKNOWLEDGED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_PERIODO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().PERIODO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TAG,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.VERSIONE_SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.AZIONE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.ENABLED,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.RUOLO_PORTA,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.PROTOCOLLO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.TOKEN,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.EROGATORE,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO,false),"?");
		sqlQueryObjectInsert.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.AZIONE,false),"?");

		// Insert allarme
		org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator = this.getAllarmeFetch().getKeyGeneratorObject(Allarme.model());
		long id = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert, keyGenerator, jdbcProperties.isShowSql(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getNome(),Allarme.model().NOME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getTipo(),Allarme.model().TIPO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getTipoAllarme(),Allarme.model().TIPO_ALLARME.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getAckMode(),Allarme.model().MAIL.ACK_MODE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getInviaWarning(),Allarme.model().MAIL.INVIA_WARNING.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getInviaAlert(),Allarme.model().MAIL.INVIA_ALERT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getDestinatari(),Allarme.model().MAIL.DESTINATARI.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getSubject(),Allarme.model().MAIL.SUBJECT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getMail().getBody(),Allarme.model().MAIL.BODY.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getScript().getAckMode(),Allarme.model().SCRIPT.ACK_MODE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getScript().getInvocaWarning(),Allarme.model().SCRIPT.INVOCA_WARNING.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getScript().getInvocaAlert(),Allarme.model().SCRIPT.INVOCA_ALERT.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getScript().getCommand(),Allarme.model().SCRIPT.COMMAND.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getScript().getArgs(),Allarme.model().SCRIPT.ARGS.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getStatoPrecedente(),Allarme.model().STATO_PRECEDENTE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getStato(),Allarme.model().STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getDettaglioStato(),Allarme.model().DETTAGLIO_STATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getLasttimestampCreate(),Allarme.model().LASTTIMESTAMP_CREATE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getLasttimestampUpdate(),Allarme.model().LASTTIMESTAMP_UPDATE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getEnabled(),Allarme.model().ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getAcknowledged(),Allarme.model().ACKNOWLEDGED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getTipoPeriodo(),Allarme.model().TIPO_PERIODO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getPeriodo(),Allarme.model().PERIODO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getEnabled(),Allarme.model().FILTRO.ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getProtocollo(),Allarme.model().FILTRO.PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getRuoloPorta(),Allarme.model().FILTRO.RUOLO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getNomePorta(),Allarme.model().FILTRO.NOME_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getTipoFruitore(),Allarme.model().FILTRO.TIPO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getNomeFruitore(),Allarme.model().FILTRO.NOME_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getRuoloFruitore(),Allarme.model().FILTRO.RUOLO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getServizioApplicativoFruitore(),Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getTipoErogatore(),Allarme.model().FILTRO.TIPO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getNomeErogatore(),Allarme.model().FILTRO.NOME_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getRuoloErogatore(),Allarme.model().FILTRO.RUOLO_EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getTag(),Allarme.model().FILTRO.TAG.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getTipoServizio(),Allarme.model().FILTRO.TIPO_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getNomeServizio(),Allarme.model().FILTRO.NOME_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getVersioneServizio(),Allarme.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getFiltro().getAzione(),Allarme.model().FILTRO.AZIONE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getEnabled(),Allarme.model().GROUP_BY.ENABLED.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getRuoloPorta(),Allarme.model().GROUP_BY.RUOLO_PORTA.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getProtocollo(),Allarme.model().GROUP_BY.PROTOCOLLO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getFruitore(),Allarme.model().GROUP_BY.FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getServizioApplicativoFruitore(),Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getIdentificativoAutenticato(),Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getToken(),Allarme.model().GROUP_BY.TOKEN.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getErogatore(),Allarme.model().GROUP_BY.EROGATORE.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getServizio(),Allarme.model().GROUP_BY.SERVIZIO.getFieldType()),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getGroupBy().getAzione(),Allarme.model().GROUP_BY.AZIONE.getFieldType())
		);
		allarme.setId(id);

		// for allarme
		for (int i = 0; i < allarme.getAllarmeParametroList().size(); i++) {


			// Object allarme.getAllarmeParametroList().get(i)
			ISQLQueryObject sqlQueryObjectInsert_allarmeParametro = sqlQueryObjectInsert.newSQLQueryObject();
			sqlQueryObjectInsert_allarmeParametro.addInsertTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
			sqlQueryObjectInsert_allarmeParametro.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,false),"?");
			sqlQueryObjectInsert_allarmeParametro.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.VALORE,false),"?");
			sqlQueryObjectInsert_allarmeParametro.addInsertField("id_allarme","?");

			// Insert allarme.getAllarmeParametroList().get(i)
			org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_allarmeParametro = this.getAllarmeFetch().getKeyGeneratorObject(Allarme.model().ALLARME_PARAMETRO);
			long id_allarmeParametro = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_allarmeParametro, keyGenerator_allarmeParametro, jdbcProperties.isShowSql(),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getAllarmeParametroList().get(i).getIdParametro(),Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme.getAllarmeParametroList().get(i).getValore(),Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType()),
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
			);
			allarme.getAllarmeParametroList().get(i).setId(id_allarmeParametro);
		} // fine for 

		
	}

	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme oldId, Allarme allarme, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObject.newSQLQueryObject();
		Long longIdByLogicId = this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObjectUpdate.newSQLQueryObject(), oldId, true);
		Long tableId = allarme.getId();
		if(tableId != null && tableId.longValue() > 0) {
			if(tableId.longValue() != longIdByLogicId.longValue()) {
				throw new Exception("Ambiguous parameter: allarme.id ["+tableId+"] does not match logic id ["+longIdByLogicId+"]");
			}
		} else {
			tableId = longIdByLogicId;
			allarme.setId(tableId);
		}
		if(tableId==null || tableId<=0){
			throw new Exception("Retrieve tableId failed");
		}

		this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarme, idMappingResolutionBehaviour);
	}
	@Override
	public void update(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Allarme allarme, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotFoundException, NotImplementedException, ServiceException, Exception {
	
		org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities jdbcUtilities = 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCPreparedStatementUtilities(sqlQueryObject.getTipoDatabaseOpenSPCoop2(), log, connection);
		
		ISQLQueryObject sqlQueryObjectInsert = sqlQueryObject.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectDelete = sqlQueryObjectInsert.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectGet = sqlQueryObjectDelete.newSQLQueryObject();
		ISQLQueryObject sqlQueryObjectUpdate = sqlQueryObjectGet.newSQLQueryObject();
		


		// Object allarme
		sqlQueryObjectUpdate.setANDLogicOperator(true);
		sqlQueryObjectUpdate.addUpdateTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		boolean isUpdate = true;
		java.util.List<JDBCObject> lstObjects = new java.util.ArrayList<JDBCObject>();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().NOME,false), "?");
		lstObjects.add(new JDBCObject(allarme.getNome(), Allarme.model().NOME.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO,false), "?");
		lstObjects.add(new JDBCObject(allarme.getTipo(), Allarme.model().TIPO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_ALLARME,false), "?");
		lstObjects.add(new JDBCObject(allarme.getTipoAllarme(), Allarme.model().TIPO_ALLARME.getFieldType()));
		AllarmeMail allarme_mail = allarme.getMail();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.ACK_MODE,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getAckMode(), Allarme.model().MAIL.ACK_MODE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_WARNING,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getInviaWarning(), Allarme.model().MAIL.INVIA_WARNING.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.INVIA_ALERT,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getInviaAlert(), Allarme.model().MAIL.INVIA_ALERT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.DESTINATARI,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getDestinatari(), Allarme.model().MAIL.DESTINATARI.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.SUBJECT,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getSubject(), Allarme.model().MAIL.SUBJECT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().MAIL.BODY,false), "?");
		lstObjects.add(new JDBCObject(allarme_mail.getBody(), Allarme.model().MAIL.BODY.getFieldType()));
		AllarmeScript allarme_script = allarme.getScript();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ACK_MODE,false), "?");
		lstObjects.add(new JDBCObject(allarme_script.getAckMode(), Allarme.model().SCRIPT.ACK_MODE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_WARNING,false), "?");
		lstObjects.add(new JDBCObject(allarme_script.getInvocaWarning(), Allarme.model().SCRIPT.INVOCA_WARNING.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.INVOCA_ALERT,false), "?");
		lstObjects.add(new JDBCObject(allarme_script.getInvocaAlert(), Allarme.model().SCRIPT.INVOCA_ALERT.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.COMMAND,false), "?");
		lstObjects.add(new JDBCObject(allarme_script.getCommand(), Allarme.model().SCRIPT.COMMAND.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().SCRIPT.ARGS,false), "?");
		lstObjects.add(new JDBCObject(allarme_script.getArgs(), Allarme.model().SCRIPT.ARGS.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO_PRECEDENTE,false), "?");
		lstObjects.add(new JDBCObject(allarme.getStatoPrecedente(), Allarme.model().STATO_PRECEDENTE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().STATO,false), "?");
		lstObjects.add(new JDBCObject(allarme.getStato(), Allarme.model().STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().DETTAGLIO_STATO,false), "?");
		lstObjects.add(new JDBCObject(allarme.getDettaglioStato(), Allarme.model().DETTAGLIO_STATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_CREATE,false), "?");
		lstObjects.add(new JDBCObject(allarme.getLasttimestampCreate(), Allarme.model().LASTTIMESTAMP_CREATE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().LASTTIMESTAMP_UPDATE,false), "?");
		lstObjects.add(new JDBCObject(allarme.getLasttimestampUpdate(), Allarme.model().LASTTIMESTAMP_UPDATE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ENABLED,false), "?");
		lstObjects.add(new JDBCObject(allarme.getEnabled(), Allarme.model().ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ACKNOWLEDGED,false), "?");
		lstObjects.add(new JDBCObject(allarme.getAcknowledged(), Allarme.model().ACKNOWLEDGED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().TIPO_PERIODO,false), "?");
		lstObjects.add(new JDBCObject(allarme.getTipoPeriodo(), Allarme.model().TIPO_PERIODO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().PERIODO,false), "?");
		lstObjects.add(new JDBCObject(allarme.getPeriodo(), Allarme.model().PERIODO.getFieldType()));
		AllarmeFiltro allarme_filtro = allarme.getFiltro();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.ENABLED,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getEnabled(), Allarme.model().FILTRO.ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.PROTOCOLLO,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getProtocollo(), Allarme.model().FILTRO.PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getRuoloPorta(), Allarme.model().FILTRO.RUOLO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_PORTA,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getNomePorta(), Allarme.model().FILTRO.NOME_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getTipoFruitore(), Allarme.model().FILTRO.TIPO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getNomeFruitore(), Allarme.model().FILTRO.NOME_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getRuoloFruitore(), Allarme.model().FILTRO.RUOLO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getServizioApplicativoFruitore(), Allarme.model().FILTRO.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getTipoErogatore(), Allarme.model().FILTRO.TIPO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getNomeErogatore(), Allarme.model().FILTRO.NOME_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.RUOLO_EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getRuoloErogatore(), Allarme.model().FILTRO.RUOLO_EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TAG,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getTag(), Allarme.model().FILTRO.TAG.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.TIPO_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getTipoServizio(), Allarme.model().FILTRO.TIPO_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.NOME_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getNomeServizio(), Allarme.model().FILTRO.NOME_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.VERSIONE_SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getVersioneServizio(), Allarme.model().FILTRO.VERSIONE_SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().FILTRO.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(allarme_filtro.getAzione(), Allarme.model().FILTRO.AZIONE.getFieldType()));
		AllarmeRaggruppamento allarme_groupBy = allarme.getGroupBy();
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.ENABLED,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getEnabled(), Allarme.model().GROUP_BY.ENABLED.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.RUOLO_PORTA,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getRuoloPorta(), Allarme.model().GROUP_BY.RUOLO_PORTA.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.PROTOCOLLO,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getProtocollo(), Allarme.model().GROUP_BY.PROTOCOLLO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getFruitore(), Allarme.model().GROUP_BY.FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getServizioApplicativoFruitore(), Allarme.model().GROUP_BY.SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getIdentificativoAutenticato(), Allarme.model().GROUP_BY.IDENTIFICATIVO_AUTENTICATO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.TOKEN,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getToken(), Allarme.model().GROUP_BY.TOKEN.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.EROGATORE,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getErogatore(), Allarme.model().GROUP_BY.EROGATORE.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.SERVIZIO,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getServizio(), Allarme.model().GROUP_BY.SERVIZIO.getFieldType()));
		sqlQueryObjectUpdate.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().GROUP_BY.AZIONE,false), "?");
		lstObjects.add(new JDBCObject(allarme_groupBy.getAzione(), Allarme.model().GROUP_BY.AZIONE.getFieldType()));
		sqlQueryObjectUpdate.addWhereCondition("id=?");
		lstObjects.add(new JDBCObject(tableId, Long.class));

		if(isUpdate) {
			// Update allarme
			jdbcUtilities.executeUpdate(sqlQueryObjectUpdate.createSQLUpdate(), jdbcProperties.isShowSql(), 
				lstObjects.toArray(new JDBCObject[]{}));
		}
		// for allarme_allarmeParametro

		java.util.List<Long> ids_allarmeParametro_da_non_eliminare = new java.util.ArrayList<Long>();
		for (Object allarme_allarmeParametro_object : allarme.getAllarmeParametroList()) {
			AllarmeParametro allarme_allarmeParametro = (AllarmeParametro) allarme_allarmeParametro_object;
			if(allarme_allarmeParametro.getId() == null || allarme_allarmeParametro.getId().longValue() <= 0) {

				long id = allarme.getId();			

				// Object allarme_allarmeParametro
				ISQLQueryObject sqlQueryObjectInsert_allarmeParametro = sqlQueryObjectInsert.newSQLQueryObject();
				sqlQueryObjectInsert_allarmeParametro.addInsertTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
				sqlQueryObjectInsert_allarmeParametro.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,false),"?");
				sqlQueryObjectInsert_allarmeParametro.addInsertField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.VALORE,false),"?");
				sqlQueryObjectInsert_allarmeParametro.addInsertField("id_allarme","?");

				// Insert allarme_allarmeParametro
				org.openspcoop2.utils.jdbc.IKeyGeneratorObject keyGenerator_allarmeParametro = this.getAllarmeFetch().getKeyGeneratorObject(Allarme.model().ALLARME_PARAMETRO);
				long id_allarmeParametro = jdbcUtilities.insertAndReturnGeneratedKey(sqlQueryObjectInsert_allarmeParametro, keyGenerator_allarmeParametro, jdbcProperties.isShowSql(),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme_allarmeParametro.getIdParametro(),Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(allarme_allarmeParametro.getValore(),Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType()),
					new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class)
				);
				allarme_allarmeParametro.setId(id_allarmeParametro);

				ids_allarmeParametro_da_non_eliminare.add(allarme_allarmeParametro.getId());
			} else {


				// Object allarme_allarmeParametro
				ISQLQueryObject sqlQueryObjectUpdate_allarmeParametro = sqlQueryObjectUpdate.newSQLQueryObject();
				sqlQueryObjectUpdate_allarmeParametro.setANDLogicOperator(true);
				sqlQueryObjectUpdate_allarmeParametro.addUpdateTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
				boolean isUpdate_allarmeParametro = true;
				java.util.List<JDBCObject> lstObjects_allarmeParametro = new java.util.ArrayList<JDBCObject>();
				sqlQueryObjectUpdate_allarmeParametro.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO,false), "?");
				lstObjects_allarmeParametro.add(new JDBCObject(allarme_allarmeParametro.getIdParametro(), Allarme.model().ALLARME_PARAMETRO.ID_PARAMETRO.getFieldType()));
				sqlQueryObjectUpdate_allarmeParametro.addUpdateField(this.getAllarmeFieldConverter().toColumn(Allarme.model().ALLARME_PARAMETRO.VALORE,false), "?");
				lstObjects_allarmeParametro.add(new JDBCObject(allarme_allarmeParametro.getValore(), Allarme.model().ALLARME_PARAMETRO.VALORE.getFieldType()));
				sqlQueryObjectUpdate_allarmeParametro.addWhereCondition("chk_param_id=?");
				ids_allarmeParametro_da_non_eliminare.add(allarme_allarmeParametro.getId());
				lstObjects_allarmeParametro.add(new JDBCObject(Long.valueOf(allarme_allarmeParametro.getId()),Long.class));

				if(isUpdate_allarmeParametro) {
					// Update allarme_allarmeParametro
					jdbcUtilities.executeUpdate(sqlQueryObjectUpdate_allarmeParametro.createSQLUpdate(), jdbcProperties.isShowSql(), 
						lstObjects_allarmeParametro.toArray(new JDBCObject[]{}));
				}
			}
		} // fine for allarme_allarmeParametro

		// elimino tutte le occorrenze di allarme_allarmeParametro non presenti nell'update

		ISQLQueryObject sqlQueryObjectUpdate_allarmeParametro_deleteList = sqlQueryObjectUpdate.newSQLQueryObject();
		sqlQueryObjectUpdate_allarmeParametro_deleteList.setANDLogicOperator(true);
		sqlQueryObjectUpdate_allarmeParametro_deleteList.addDeleteTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
		java.util.List<JDBCObject> jdbcObjects_allarmeParametro_delete = new java.util.ArrayList<JDBCObject>();

		sqlQueryObjectUpdate_allarmeParametro_deleteList.addWhereCondition("id_allarme=?");
		jdbcObjects_allarmeParametro_delete.add(new JDBCObject(allarme.getId(), Long.class));

		StringBuilder marks_allarmeParametro = new StringBuilder();
		if(ids_allarmeParametro_da_non_eliminare.size() > 0) {
			for(Long ids : ids_allarmeParametro_da_non_eliminare) {
				if(marks_allarmeParametro.length() > 0) {
					marks_allarmeParametro.append(",");
				}
				marks_allarmeParametro.append("?");
				jdbcObjects_allarmeParametro_delete.add(new JDBCObject(ids, Long.class));

			}
			sqlQueryObjectUpdate_allarmeParametro_deleteList.addWhereCondition("chk_param_id NOT IN ("+marks_allarmeParametro.toString()+")");
		}

		jdbcUtilities.execute(sqlQueryObjectUpdate_allarmeParametro_deleteList.createSQLDelete(), jdbcProperties.isShowSql(), jdbcObjects_allarmeParametro_delete.toArray(new JDBCObject[]{}));



	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAllarmeFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAllarmeFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme id, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				this._getRootTablePrimaryKeyValues(jdbcProperties, log, connection, sqlQueryObject, id),
				this.getAllarmeFieldConverter(), this, updateModels);
	}	
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeFieldConverter(), this, null, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, IExpression condition, UpdateField ... updateFields) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeFieldConverter(), this, condition, updateFields);
	}
	
	@Override
	public void updateFields(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, UpdateModel ... updateModels) throws NotFoundException, NotImplementedException, ServiceException, Exception {
		java.util.List<Object> ids = new java.util.ArrayList<Object>();
		ids.add(tableId);
		JDBCUtilities.updateFields(jdbcProperties, log, connection, sqlQueryObject, 
				this.getAllarmeFieldConverter().toTable(Allarme.model()), 
				this._getMapTableToPKColumn(), 
				ids,
				this.getAllarmeFieldConverter(), this, updateModels);
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme oldId, Allarme allarme, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
	
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, oldId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, oldId, allarme,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarme,idMappingResolutionBehaviour);
		}
		
	}
	
	@Override
	public void updateOrCreate(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, long tableId, Allarme allarme, org.openspcoop2.generic_project.beans.IDMappingBehaviour idMappingResolutionBehaviour) throws NotImplementedException,ServiceException,Exception {
		if(this.exists(jdbcProperties, log, connection, sqlQueryObject, tableId)) {
			this.update(jdbcProperties, log, connection, sqlQueryObject, tableId, allarme,idMappingResolutionBehaviour);
		} else {
			this.create(jdbcProperties, log, connection, sqlQueryObject, allarme,idMappingResolutionBehaviour);
		}
	}
	
	@Override
	public void delete(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, Allarme allarme) throws NotImplementedException,ServiceException,Exception {
		
		
		Long longId = null;
		if( (allarme.getId()!=null) && (allarme.getId()>0) ){
			longId = allarme.getId();
		}
		else{
			IdAllarme idAllarme = this.convertToId(jdbcProperties,log,connection,sqlQueryObject,allarme);
			longId = this.findIdAllarme(jdbcProperties,log,connection,sqlQueryObject,idAllarme,false);
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
		

		//Recupero oggetto _allarmeParametro
		ISQLQueryObject sqlQueryObjectDelete_allarmeParametro_getToDelete = sqlQueryObjectDelete.newSQLQueryObject();
		sqlQueryObjectDelete_allarmeParametro_getToDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete_allarmeParametro_getToDelete.addFromTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
		sqlQueryObjectDelete_allarmeParametro_getToDelete.addWhereCondition("id_allarme=?");
		java.util.List<Object> allarme_allarmeParametro_toDelete_list = (java.util.List<Object>) jdbcUtilities.executeQuery(sqlQueryObjectDelete_allarmeParametro_getToDelete.createSQLQuery(), jdbcProperties.isShowSql(), Allarme.model().ALLARME_PARAMETRO, this.getAllarmeFetch(),
			new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(id),Long.class));

		// for allarme_allarmeParametro
		for (Object allarme_allarmeParametro_object : allarme_allarmeParametro_toDelete_list) {
			AllarmeParametro allarme_allarmeParametro = (AllarmeParametro) allarme_allarmeParametro_object;

			// Object allarme_allarmeParametro
			ISQLQueryObject sqlQueryObjectDelete_allarmeParametro = sqlQueryObjectDelete.newSQLQueryObject();
			sqlQueryObjectDelete_allarmeParametro.setANDLogicOperator(true);
			sqlQueryObjectDelete_allarmeParametro.addDeleteTable(this.getAllarmeFieldConverter().toTable(Allarme.model().ALLARME_PARAMETRO));
			sqlQueryObjectDelete_allarmeParametro.addWhereCondition("chk_param_id=?");

			// Delete allarme_allarmeParametro
			if(allarme_allarmeParametro != null){
				jdbcUtilities.execute(sqlQueryObjectDelete_allarmeParametro.createSQLDelete(), jdbcProperties.isShowSql(), 
				new org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject(Long.valueOf(allarme_allarmeParametro.getId()),Long.class));
			}
		} // fine for allarme_allarmeParametro

		// Object allarme
		sqlQueryObjectDelete.setANDLogicOperator(true);
		sqlQueryObjectDelete.addDeleteTable(this.getAllarmeFieldConverter().toTable(Allarme.model()));
		if(id != null)
			sqlQueryObjectDelete.addWhereCondition("id=?");

		// Delete allarme
		jdbcUtilities.execute(sqlQueryObjectDelete.createSQLDelete(), jdbcProperties.isShowSql(), 
			new JDBCObject(id,Long.class));

	}

	@Override
	public void deleteById(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject, IdAllarme idAllarme) throws NotImplementedException,ServiceException,Exception {

		Long id = null;
		try{
			id = this.findIdAllarme(jdbcProperties, log, connection, sqlQueryObject, idAllarme, true);
		}catch(NotFoundException notFound){
			return;
		}
		this._delete(jdbcProperties, log, connection, sqlQueryObject, id);
		
	}
	
	@Override
	public NonNegativeNumber deleteAll(JDBCServiceManagerProperties jdbcProperties, Logger log, Connection connection, ISQLQueryObject sqlQueryObject) throws NotImplementedException,ServiceException,Exception {
		
		return this.deleteAll(jdbcProperties, log, connection, sqlQueryObject, new JDBCExpression(this.getAllarmeFieldConverter()));

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
