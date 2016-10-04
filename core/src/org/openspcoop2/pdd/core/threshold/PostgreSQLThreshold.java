/*
 * OpenSPCoop - Customizable API Gateway 
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

import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**
 * Implementazione che definisce un meccanismo di Soglia sullo spazio libero rimasto
 * nelle risorse utilizzate dalla PdD, per un database MySQL. 
 *  
 * @author Andrea Manca (manca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class PostgreSQLThreshold implements IThreshold {

	public static OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
	public static String ID_MODULO = "PostgreSQLThreshold";
	private static final String Query="SELECT SUM(pg_database_size(pg_database.datname)) as size FROM pg_database JOIN pg_shadow ON pg_database.datdba = pg_shadow.usesysid;";
	
	
	/**
	 * @param parametri puo essere del tipo 
	 * 	"valore=<valore di soglia> [datasource=<nome datasource>]"
	 *  se nome datasource non e' indicato verra' utilizzata la connessione
	 *  verso il database openspcoop
	 *  
	 *  Il valore di soglia e' interpretato in byte a meno che non sia esplici
	 *  tamente indicata l'unita di misura. 
	 *  NOTA: la query per il controllo dello spazio necessita dei diritti
	 *  di superutente per essere eseguita
	 */
	@Override
	public boolean check(Properties parametri) throws ThresholdException {
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		long Threshold,size=0L,factor=1L;
		boolean result=false;
		Statement s=null;
		ResultSet rs=null;
		
		String valoreSoglia = parametri.getProperty("valore");
		if(valoreSoglia==null){
			throw new ThresholdException("Parametro ["+valoreSoglia+"] non presente");
		}
		else{
			valoreSoglia = valoreSoglia.trim();
		}
		
		String datasource = parametri.getProperty("datasource");
		if(datasource!=null){
			datasource = datasource.trim();
		}
		
		String Soglia=valoreSoglia.toLowerCase();
		StringBuffer valore=new StringBuffer();
		char ch=Soglia.charAt(0);
		int i=1;
		while (Character.isDigit(ch))
		{
			valore.append(ch);
			if(i<Soglia.length())
                ch=Soglia.charAt(i++);
			else
                ch='f';
		}
		if ( Soglia.endsWith("kb") || Soglia.endsWith("k") )
			factor=1024L;
		else if ( Soglia.endsWith("mb") || Soglia.endsWith("m") )
			factor=1024L * 1024L;
		else if ( Soglia.endsWith("gb") || Soglia.endsWith("g") )
			factor=1024L * 1024L * 1024L;
		//else
			//Utilizzo del fattore di default 1
		
		MsgDiagnostico msgDiag = new MsgDiagnostico(PostgreSQLThreshold.ID_MODULO);
		Connection connection =null;
		try{
			if(datasource==null) {
				try{
					resource = dbManager.getResource(PostgreSQLThreshold.properties.getIdentitaPortaDefault(null), PostgreSQLThreshold.ID_MODULO,null);
				}catch(Exception e){
					throw new Exception("Impossibile ottenere una Risorsa dal DBManager",e);
				}
				if(resource==null)
					throw new Exception("Risorsa is null");
				if(resource.getResource() == null)
					throw new Exception("Connessione is null");
				connection = (Connection) resource.getResource();
			}
			else
			{
                Context c = new InitialContext();
                DataSource ds= (DataSource)c.lookup(datasource);
                c.close();	
                connection = ds.getConnection();
			}

			
			
			// controllo validita parametro
			Threshold=Long.parseLong(valore.toString())*factor;
			if(Threshold<0L)
				throw new Exception("Valore di soglia negativo");
			
			//Interrogazione del database
			s=connection.createStatement();
			if(!s.execute(PostgreSQLThreshold.Query))
				throw new Exception("Impossibile verficare lo spazio occupato");
			
			rs=s.getResultSet();
			
			if(rs==null)
				throw new Exception("Nessun risultato disponibile per la verifica di soglia");
			if(!rs.next())
				throw new Exception("Nessun risultato disponibile per la verifica di soglia");
			
			size=rs.getLong(1);
			if(size==0)
				throw new Exception("La quantita' di spazio occupata dai DB e' NULL");

						
			result=(size<Threshold);
			
		}catch(Exception e){
			throw new ThresholdException("PostgreSQLThreshold error: "+e.getMessage(),e);
		}finally{
			try {
					if(rs!=null) rs.close();
					if(s!=null) s.close();
					rs=null;s=null;
					if(datasource==null)
						dbManager.releaseResource(PostgreSQLThreshold.properties.getIdentitaPortaDefault(null), PostgreSQLThreshold.ID_MODULO, resource);
					else {
						if ((connection != null) && (connection.isClosed()==false))
							connection.close();
					}
			}catch(SQLException ex){}		
		}
		msgDiag.highDebug("Spazio occupato da tutti i databse: "+size+"; Spazio entro la soglia: "+result);
		return result;
		
	}

}
