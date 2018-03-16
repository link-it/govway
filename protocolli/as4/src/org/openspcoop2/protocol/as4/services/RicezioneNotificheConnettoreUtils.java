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

package org.openspcoop2.protocol.as4.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * RicezioneBusteConnettoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13732 $, $Date: 2018-03-14 18:03:37 +0100 (Wed, 14 Mar 2018) $
 */
public class RicezioneNotificheConnettoreUtils extends BaseConnettoreUtils {

	private AS4Properties properties;
	
	public RicezioneNotificheConnettoreUtils(RunnableLogger log, AS4Properties properties) {
		super(log);
		this.properties = properties;
	}
	
	
	public void updateTraccia(TextMessage textMsg) throws Exception {
		
		String idNotifica = this.getPropertyJms(textMsg, AS4Costanti.JMS_NOTIFICA_MESSAGE_ID, true);
		String messageStatus = this.getPropertyJms(textMsg, AS4Costanti.JMS_NOTIFICA_NOTIFICATION_TYPE, true);
		
		String datasource = this.properties.getAckTraceDatasource();
		Properties datasourceJndiContext = this.properties.getAckTraceDatasource_jndiContext();
		String tipoDatabase = this.properties.getAckTraceTipoDatabase();
		if(tipoDatabase==null) {
			tipoDatabase = OpenSPCoop2Properties.getInstance().getDatabaseType();
		}
		
		GestoreJNDI gestoreJNDI = new GestoreJNDI(datasourceJndiContext);
		DataSource ds = (DataSource) gestoreJNDI.lookup(datasource);
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			
			ISQLQueryObject sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectCore.addFromTable(CostantiDB.TRACCE_EXT_INFO);
			sqlQueryObjectCore.addSelectField(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA);
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME+"=?");
			sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE+"=?");
			sqlQueryObjectCore.setANDLogicOperator(true);
			String query = sqlQueryObjectCore.createSQLQuery();
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID);
			pstmt.setString(2, idNotifica);
			rs = pstmt.executeQuery();
			List<Long> ids = new ArrayList<>();
			while(rs.next()) {
				ids.add(rs.getLong(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA));
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
			
			if(ids.size()<=0) {
				this.log.error("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_ID+"] e valore["+idNotifica+"] non trovata");
				return;
			}
			
			for (Long id : ids) { // dovrebbe essere solo una
				sqlQueryObjectCore = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObjectCore.addUpdateTable(CostantiDB.TRACCE_EXT_INFO);
				sqlQueryObjectCore.addUpdateField(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_VALUE, "?");
				sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_ID_TRACCIA+"=?");
				sqlQueryObjectCore.addWhereCondition(CostantiDB.TRACCE_EXT_PROTOCOL_INFO_COLUMN_NAME+"=?");
				sqlQueryObjectCore.setANDLogicOperator(true);
				String update = sqlQueryObjectCore.createSQLUpdate();
				pstmt = connection.prepareStatement(update);
				pstmt.setString(1, messageStatus);
				pstmt.setLong(2, id);
				pstmt.setString(3, AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS);
				int n = pstmt.executeUpdate();
				if(n<=0) {
					this.log.error("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS+"] e idtraccia["+id+"] non trovata");
				}
				else {
					this.log.debug("Traccia con proprietà 'as4' con nome ["+AS4Costanti.AS4_BUSTA_SERVIZIO_MESSAGE_INFO_SEND_STATUS+"] e idtraccia["+id+"] aggiornata allo stato ["+messageStatus+"]");
				}
				pstmt.close();
				pstmt = null;
			}
			
			
		}finally {
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception e) {}
			try {
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e) {}
			try {
				if(connection!=null)
					connection.close();
			}catch(Exception e) {}
		}
		
		
	}
}
