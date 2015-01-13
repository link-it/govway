/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.protocol.utils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;

import org.openspcoop2.core.constants.CostantiDB;

/**
 * IDSerialGenerator_mysql
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator_mysql {

	public static String generate(Connection conDB,IDSerialGeneratorParameter param){
		
		Statement stmtUpdate = null;
		String identificativoUnivoco = null;
		try{
			// Incremento
			stmtUpdate= conDB.createStatement();
			
			String table = CostantiDB.TABELLA_ID_AS_LONG;
			if(param.getInformazioneAssociataAlProgressivo()!=null){
				table = CostantiDB.TABELLA_ID_RELATIVO_AS_LONG;
			}
			
			String condition = " WHERE "+CostantiDB.TABELLA_ID_COLONNA_PROTOCOLLO+"='"+param.getProtocolFactory().getProtocol()+"'";
			if(param.getInformazioneAssociataAlProgressivo()!=null){
				condition = " AND "+CostantiDB.TABELLA_ID_COLONNA_INFO_ASSOCIATA+"='"+param.getInformazioneAssociataAlProgressivo()+"'";
			}
			
			stmtUpdate.executeUpdate("UPDATE " + table  + " SET "+CostantiDB.TABELLA_ID_COLONNA_COUNTER+
					" = LAST_INSERT_ID("+CostantiDB.TABELLA_ID_COLONNA_COUNTER+"+1)"+condition );
							
			/*			
			if (stmtUpdate instanceof com.mysql.jdbc.Statement){
			com.mysql.jdbc.Statement temp=(com.mysql.jdbc.Statement) stmtUpdate;
			//counter = ((com.mysql.jdbc.Statement) stmtUpdate).getLastInsertID();
			counter=temp.getLastInsertID();
			} else {
			org.jboss.resource.adapter.jdbc.WrappedStatement jbossStatement=(org.jboss.resource.adapter.jdbc.WrappedStatement)stmtUpdate;
			com.mysql.jdbc.Statement temp=(com.mysql.jdbc.Statement)jbossStatement.getUnderlyingStatement();
			counter=temp.getLastInsertID();
			}
			 */
		
			// TODO REFLECTION VERIFY
			Class<?> cMysqlStatement = Class.forName("com.mysql.jdbc.Statement");
			Method m = cMysqlStatement.getClass().getMethod("getLastInsertID");
			if(stmtUpdate.getClass().isAssignableFrom(cMysqlStatement)){
				Object oReturn = m.invoke(stmtUpdate);
				if(oReturn!=null){
					Long counterAsLong = (Long) oReturn;
					identificativoUnivoco = counterAsLong.longValue()+"";
				}
			}
			else{
				Method mUnder = cMysqlStatement.getClass().getMethod("getUnderlyingStatement");
				Object stmtUnder = mUnder.invoke(stmtUpdate);
				Object oReturn = m.invoke(stmtUnder);
				if(oReturn!=null){
					Long counterAsLong = (Long) oReturn;
					identificativoUnivoco = counterAsLong.longValue()+"";
				}
			}
			
			stmtUpdate.close();	
		} catch(Exception e) {
			try{
				if( stmtUpdate != null )
					stmtUpdate.close();
			} catch(Exception er) {}
			try{
				conDB.rollback();
			} catch(Exception er) {}
		}
		
		return identificativoUnivoco;
	}
	
}
