/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.threshold;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;


//import org.openspcoop.pdd.logger.MsgDiagnostico;

/**
 * Implementazione che definisce un meccanismo di Soglia sullo spazio libero rimasto
 * nelle risorse utilizzate dalla PdD, per un database MySQL. 
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MySQLThreshold implements IThreshold {

	public static OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
	public static String ID_MODULO = "MySQLThreshold";
	private static final String Query="SHOW TABLE STATUS LIKE 'MESSAGGI'";
	
	/**
	 * Controlla lo spazio libero sul tablespace di mysql
	 *  
	 * @param parametri Parametri
	 * @return true se vi e' ancora spazio libero che rispetti la soglia
	 */
	@Override
	public boolean check(Properties parametri) throws ThresholdException{
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		long Threshold,FreeTableSpace;
		boolean result=false;
		Statement s=null;
		ResultSet rs=null;
	
		//MsgDiagnostico msgDiag = new MsgDiagnostico(ID_MODULO);
		try{
			try{
				resource = dbManager.getResource(MySQLThreshold.properties.getIdentitaPortaDefault(null), MySQLThreshold.ID_MODULO,null);
			}catch(Exception e){
				throw new Exception("Impossibile ottenere una Risorsa dal DBManager",e);
			}
			if(resource==null)
				throw new Exception("Risorsa is null");
			if(resource.getResource() == null)
				throw new Exception("Connessione is null");
			Connection connection = (Connection) resource.getResource();
			
			// controllo validita parametro
			String valore = parametri.getProperty("valore");
			if(valore==null){
				throw new ThresholdException("Parametro ["+valore+"] non presente");
			}
			else{
				valore = valore.trim();
			}
			Threshold=Long.parseLong(valore);
			if(Threshold<0L)
				throw new Exception("Valore di soglia negativo");
			
			//Interrogazione del database
			s=connection.createStatement();
			if(!s.execute(MySQLThreshold.Query))
				throw new Exception("Impossibile verficare lo spazio rimanente sul tablespace");
			
			rs=s.getResultSet();
			
			if(rs==null)
				throw new Exception("Nessun risultato disponibile per la verifica di soglia");
			if(!rs.next())
				throw new Exception("Nessun risultato disponibile per la verifica di soglia");
			
			String Comment = rs.getString("Comment");
			if(Comment==null)
				throw new Exception("La quantita' di spazio disponibile sul DB e' NULL");

			// Parse della stringa ricevuta da MySql
			// Il contentuo di questa colonna tipicamente e'
			//	InnoDB free: <spazio rimanente nel tablespace> kB
			// vanno eliminati i primi 13 caratteri e gli ultimi 3
		
			FreeTableSpace=Long.parseLong(Comment.substring(13, Comment.length()-3 ));
			
			result=(FreeTableSpace>Threshold);
			
		}catch(Exception e){
			throw new ThresholdException("MySQLThreshold error: "+e.getMessage(),e);
		}finally{
			try {
				if(rs!=null) rs.close();
				if(s!=null) s.close();
			}catch(SQLException ex){}
			rs=null;s=null;
			dbManager.releaseResource(MySQLThreshold.properties.getIdentitaPortaDefault(null), MySQLThreshold.ID_MODULO, resource);
		}
		//msgDiag.highDebug("Spazio libero sul TableSpace: "+FreeTableSpace+" Spazio entro la soglia: "+result);
		return result;
	}
	
}

