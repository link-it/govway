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


package org.openspcoop2.ValidazioneContenutiWS.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.utils.LoggerWrapperFactory;

/**
* Gestione del Database di Tracciamento
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DatabaseComponent {

	

	
	
	/** PRIVATE FIELD **/

	private Connection connectionTracciamento;
	private String dataSourceTracciamento;
	public String getDataSourceTracciamento() {
		return this.dataSourceTracciamento;
	}
	
	/** ******************* Costruttore********************************* */
	public DatabaseComponent(String dataSourceTracciamento, Properties contextJNDITracciamento) throws Exception {
		InitialContext ctx = null;
		try {
			ctx = new InitialContext(contextJNDITracciamento);
			DataSource ds = (DataSource) ctx.lookup(dataSourceTracciamento); 
			if(ds==null)
				throw new Exception("dataSource is null");
			
			this.connectionTracciamento = ds.getConnection();
			
		} catch (Exception e) {
			throw new Exception("Impossibile instanziare la connessione al database tracciamento("+dataSourceTracciamento+"): "+e.getMessage());
		}finally{
			try{
				if(ctx!=null)
					ctx.close();
			} catch (Exception e) {}
		}
		this.dataSourceTracciamento = dataSourceTracciamento;
		
	}

	
	
	public void close() throws Exception{
		try{
			String error = null;
			try{
				if(this.connectionTracciamento!=null && this.connectionTracciamento.isClosed()==false){
					this.connectionTracciamento.close();
				}
			}catch(Exception e){
				error = "TracciamentoConnection close error: "+e.getMessage()+"\n"; 
			}
		
			if(error!=null)
				throw new Exception(error);
		}catch(Exception e){
			System.out.println("Errore durante la chiusura delle connessione: "+e.getMessage());
		}
	}
	
	

		
	
	/* ------------------------- IS ARRIVED ------------------------ */
	
	private static final String TRACCE = "tracce";
	private final static String TRACCE_COLUMN_ID = "id";
	private static final String TRACCE_COLUMN_GDO = "gdo";
	private static final String TRACCE_COLUMN_PDD_CODICE = "pdd_codice";
	private static final String TRACCE_COLUMN_ID_MESSAGGIO = "id_messaggio";
	private static final String TRACCE_COLUMN_IS_ARRIVED = "is_arrived";
	
	/** 
	 * Metodo cheva a scrivere il numero di volte 
	 * che viene ricevuto un messaggio
	 * 
	 * @param id Identi
	 * */
	public void tracciaIsArrivedIntoDatabase(String id,String destinatario) throws Exception {

		// Raccolata tempo minimo
		PreparedStatement state=null;
		ResultSet res = null;
		java.sql.Timestamp minDate = null;
		int giro = 0;
		int giriMax = 300;
		String query = "select "+TRACCE+"."+TRACCE_COLUMN_GDO+" from "+TRACCE+" where "+TRACCE+"."+TRACCE_COLUMN_ID_MESSAGGIO+"='"+id+"' AND "+
			 	TRACCE_COLUMN_IS_ARRIVED+"='0' AND "+TRACCE_COLUMN_PDD_CODICE+" LIKE '"+destinatario+"%"+"' ORDER BY "+TRACCE+"."+TRACCE_COLUMN_GDO+"";
		LoggerWrapperFactory.getLogger("openspcoop2.testsuite").debug("Query in corso...:  "+query);
		while(giro<giriMax){
			try {
				//System.out.println("SELECT [select tracce.gdo from tracce where tracce.id_egov="+id+" AND "+CostantiDatabase.IS_ARRIVED+"=0 AND "+CostantiDatabase.ID_PORTA+" LIKE "+destinatario+"%"+" ORDER BY tracce.gdo]");
				state=this.connectionTracciamento.prepareStatement("select "+TRACCE+"."+TRACCE_COLUMN_GDO+" from "+TRACCE+" where "+TRACCE+"."+TRACCE_COLUMN_ID_MESSAGGIO+"=? AND "+
																	TRACCE_COLUMN_IS_ARRIVED+"=? AND "+TRACCE_COLUMN_PDD_CODICE+" LIKE ? ORDER BY "+TRACCE+"."+TRACCE_COLUMN_GDO+"");
				state.setString(1,id);
				state.setInt(2,0);
				state.setString(3, destinatario+"%");
				res = state.executeQuery();
				if(res.next()){
					minDate = res.getTimestamp(TRACCE_COLUMN_GDO);
				}else{
					throw new Exception("Data minima per traccia non trovata."); 
				}
				res.close();
				state.close();
			} catch (Exception e) {
				if(giro==(giriMax-1))
					throw new Exception("Impostazione isArrived non riuscita (select Data) ["+query+"]: "+e.getMessage(),e);
			}
			finally{
				try{
					res.close();
				}catch(Exception sql){}
				try{
					state.close();
				}catch(Exception sql){}
			}
			
			if(minDate!=null)
				break;
			
			giro++;
			if(giro==giriMax)
				throw new Exception("Impostazione isArrived non riuscita (select Data) aspettato 1 minuto.");
			
			try{
				Thread.sleep(100);
			}catch(Exception e){}
		}
		
		// Raccolata id
		int idTraccia = -1;
		try {
			state=this.connectionTracciamento.prepareStatement("select "+TRACCE+"."+TRACCE_COLUMN_ID+" from "+TRACCE+" where "+TRACCE+"."+TRACCE_COLUMN_ID_MESSAGGIO+"=? AND "+TRACCE+"."+TRACCE_COLUMN_IS_ARRIVED+"=? AND "+TRACCE+"."+TRACCE_COLUMN_PDD_CODICE+" LIKE ? AND "+TRACCE+"."+TRACCE_COLUMN_GDO+"=?");
			state.setString(1,id);
			state.setInt(2,0);
			state.setString(3, destinatario+"%");
			state.setTimestamp(4,minDate);
			res = state.executeQuery();
			if(res.next()){
				idTraccia = res.getInt(TRACCE_COLUMN_ID);
			}else{
				throw new Exception("Identificatore traccia non trovato."); 
			}
			res.close();
			state.close();
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new Exception("Impostazione isArrived non riuscita (select id): "+e.getMessage(),e);
		}
		finally{
			try{
				res.close();
			}catch(Exception sql){}
			try{
				state.close();
			}catch(Exception sql){}
		}
		

		try {
			//System.out.println("UPDATE [UPDATE tracce set "+CostantiDatabase.IS_ARRIVED+"=1 where id="+idTraccia+"]");
			state=this.connectionTracciamento.prepareStatement("UPDATE "+TRACCE+" set "+TRACCE_COLUMN_IS_ARRIVED+"=? where "+TRACCE_COLUMN_ID+"=?");
			state.setInt(1,1);
			state.setInt(2,idTraccia);
			state.execute();
			state.close();
		} catch (Exception e) {
			System.out.println("-----------ERRORE SQL ----------------");e.printStackTrace(System.out);
			throw new Exception("Impostazione isArrived non riuscita: "+e.getMessage(),e);
		}
		finally{
			try{
				state.close();
			}catch(Exception sql){}
		}

	}
	
	
	
	
}


