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


package org.openspcoop2.testsuite.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Utilities;

/**
 * Gestione del Database per i messaggi diagnostici
 * 
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatabaseMsgDiagnosticiComponent {





	/** PRIVATE FIELD **/

	private Connection connectionMsgDiagnostici;
	private String dataSourceMsgDiagnostici;
	private String protocollo;

	/** ******************* Costruttore********************************* */
	public DatabaseMsgDiagnosticiComponent(String dataSourceMsgDiagnostico, Properties contextJNDIMsgDiagnostico,String protocolloSdk) {
		this.protocollo = protocolloSdk;
		this.setConnection(dataSourceMsgDiagnostico,contextJNDIMsgDiagnostico);
		this.dataSourceMsgDiagnostici = dataSourceMsgDiagnostico;
	}
	public DatabaseMsgDiagnosticiComponent(String driverJDBC,String connectionUrl,String username,String password,String protocolloSdk) {
		this.protocollo = protocolloSdk;
		this.setConnection(driverJDBC,connectionUrl,username,password);
	}

	public void close() throws TestSuiteException{
		try{
			String error = null;
			try{
				if(this.connectionMsgDiagnostici!=null && this.connectionMsgDiagnostici.isClosed()==false){
					this.connectionMsgDiagnostici.close();
					//System.out.println("closeConnection MSG DIAGNOSTICO");
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




	/** ********************Crea una connessione****************************** */

	private void setConnection(String dataSource,Properties propJNDI) throws TestSuiteException {
		InitialContext ctx = null;
		try {
			ctx = new InitialContext(propJNDI);
			DataSource ds = (DataSource) ctx.lookup(dataSource); 
			if(ds==null)
				throw new Exception("dataSource is null");

			this.connectionMsgDiagnostici = ds.getConnection();

		} catch (Exception e) {
			throw new TestSuiteException("Impossibile instanziare la connessione al database dei msg diagnostici("+dataSource+"): "+e.getMessage());
		}finally{
			try{
				if(ctx!=null)
					ctx.close();
			} catch (Exception e) {}
		}
	}
	private void setConnection(String driverJDBC,String connectionurl,String username,String password) throws TestSuiteException {
		try {
			// Carico driver JDBC
			Class.forName(driverJDBC); 

			this.connectionMsgDiagnostici = DriverManager.getConnection(connectionurl, username, password); 

		} catch (Exception e) {
			throw new TestSuiteException("Impossibile instanziare la connessione al database dei msg diagnostici("+connectionurl+"): "+e.getMessage());
		}
	}

	public String getDataSourceMsgDiagnostici() {
		return this.dataSourceMsgDiagnostici;
	}

	public void setDataSourceMsgDiagnostici(String dataSourceMsgDiagnostici) {
		this.dataSourceMsgDiagnostici = dataSourceMsgDiagnostici;
	}

	public long count(String id)throws TestSuiteException{
		return countSeveritaLessEquals(id, null);
	}
	public long countSeveritaLessEquals(String id, Integer severita)throws TestSuiteException{
		if(id==null)throw new TestSuiteException("Il parametro id vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			String sql = "select count(id) as cont from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=?";
			if(severita!=null){
				sql = sql + " AND "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA+"<=?";
			}
					
			prep = this.connectionMsgDiagnostici
					.prepareStatement(sql);
			prep.setString(1, id);
			if(severita!=null){
				prep.setInt(2, severita);
			}

			res = prep.executeQuery();
			if(res.next()){
				return res.getLong("cont");
			}else{
				return -1;
			}
 
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	
	
	

	public boolean isTracedMessaggio(String id,CodiceErroreCooperazione codice, String rilevanza, String contesto, ErroreCooperazione errore)throws TestSuiteException, ProtocolException{
		return isTracedMessaggio(id,Utilities.toString(codice, this.protocollo),rilevanza,contesto,Utilities.toString(errore, this.protocollo));
	}
	public boolean isTracedMessaggio(String id,CodiceErroreCooperazione codice, String rilevanza, String contesto, String posizione)throws TestSuiteException, ProtocolException{
		return isTracedMessaggio(id,Utilities.toString(codice, this.protocollo),rilevanza,contesto,posizione);
	}
	public boolean isTracedMessaggio(String id,String codice, String rilevanza, String contesto, ErroreCooperazione errore)throws TestSuiteException, ProtocolException{
		return isTracedMessaggio(id,codice,rilevanza,contesto,Utilities.toString(errore, this.protocollo));
	}
	public boolean isTracedMessaggio(String id,String codice, String rilevanza, String contesto, String posizione)throws TestSuiteException{
		if(codice==null)throw new TestSuiteException("Il codice eccezione vale null");
		if(rilevanza==null)throw new TestSuiteException("La rilevanza eccezione vale null");
		if(contesto==null)throw new TestSuiteException("Il contesto eccezione vale null");
		if(posizione==null)throw new TestSuiteException("La posizione eccezione vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE ? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE ? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE ? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE ?");
			prep.setString(1, id);
			prep.setString(2, "%"+codice+"%");
			prep.setString(3, "%"+contesto+"%");
			prep.setString(4, "%"+posizione+"%");
			prep.setString(5, "%"+rilevanza+"%");

			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggio(String id,String messaggio)throws TestSuiteException{
		if(messaggio==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+"=? ");
			/*System.out.println("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"='"+id+"' AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSSAGGIO+"='"+messaggio+"' ");*/
			prep.setString(1, id);
			prep.setString(2, messaggio);

			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			
			if(e.getMessage().contains("CLOB")){
				
				try {
					
					try{
						res.close();
					}catch(Exception eClose){}
					try{
						prep.close();
					}catch(Exception eClose){}
					
					prep = this.connectionMsgDiagnostici
					.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND TO_CHAR("+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+")=? ");
					/*System.out.println("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"='"+id+"' AND "+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSSAGGIO+"='"+messaggio+"' ");*/
					prep.setString(1, id);
					prep.setString(2, messaggio);
					
					res = prep.executeQuery();
					return res.next();
				} catch (SQLException eClob) {
					
					throw new TestSuiteException("Errore nel database: "+eClob.getMessage(),
					"nella fase DBC.getResult (CLOB)");
				}
				
			}
			
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedCodice(String id,String codice)throws TestSuiteException{
		if(id==null)throw new TestSuiteException("Id vale null");
		if(codice==null)throw new TestSuiteException("Codice vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"=?");
			prep.setString(1, id);
			prep.setString(2, codice);
			//System.out.println("ESEGUO:[select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
			//		CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSSAGGIO+"='"+messaggio+"']");
			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedCodice(Date date,String codice)throws Exception{
		if(codice==null)throw new TestSuiteException("Codice vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+">=? AND "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"=?");
			prep.setTimestamp(1, new Timestamp(date.getTime()));
			prep.setString(2, codice);

			/*java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println("QUERY [select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+">='"+dateformat.format(date)+"' AND "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"='"+codice+"'");*/
			
			res = prep.executeQuery();
			boolean ris = res.next();
			//System.out.println("RESULT: "+ris);
			//if(ris)
			//	System.out.println("TROVATO ID ["+res.getLong("id")+"]");
			return ris;
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggio(String messaggio)throws TestSuiteException{
		if(messaggio==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+"=? ");
			prep.setString(1, messaggio);
			//System.out.println("ESEGUO:[select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
			//		CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSSAGGIO+"='"+messaggio+"']");
			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			
			if(e.getMessage().contains("CLOB")){
				
				try {
					
					try{
						res.close();
					}catch(Exception eClose){}
					try{
						prep.close();
					}catch(Exception eClose){}
					
					prep = this.connectionMsgDiagnostici
					.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where TO_CHAR("+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+")=? ");
					prep.setString(1, messaggio);
					
					res = prep.executeQuery();
					return res.next();
				} catch (SQLException eClob) {
			
					// Se si ottiene: ORA-22835 Buffer too small for CLOB to CHAR or BLOB to RAW conversion (actual: 4907, maximum: 4000)
					// Eliminare tutti i diagnostici e riprovare
					
					throw new TestSuiteException("Errore nel database: "+eClob.getMessage(),
					"nella fase DBC.getResult (CLOB)");
				}
				
			}
			
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggioWithLike(Date date,String messaggio)throws Exception{
		if(messaggio==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+">=? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(messaggio)+"%' ");
			prep.setTimestamp(1, new Timestamp(date.getTime()));

			/*java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat ("yyyy-MM-dd HH:mm:ss.SSS");
			System.out.println("QUERY [select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO+">='"+dateformat.format(date)+"' AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(messaggio)+"%']");*/
			
			res = prep.executeQuery();
			boolean ris = res.next();
			//System.out.println("RESULT: "+ris);
			//if(ris)
			//	System.out.println("TROVATO ID ["+res.getLong("id")+"]");
			return ris;
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggioWithLike(String id,String messaggio)throws Exception{
		if(messaggio==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(messaggio)+"%' ");
			prep.setString(1, id);

			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggioWithLike(String messaggio)throws Exception{
		if(messaggio==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(messaggio)+"%' ");

			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggio(String ... valoriContenutiMsg)throws TestSuiteException{
		if(valoriContenutiMsg==null)throw new TestSuiteException("Parametri non passati");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where ");
			for(int i=0; i<valoriContenutiMsg.length;i++){
				if(i>0)
					sql.append(" AND ");
				String msgFiltro = org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(valoriContenutiMsg[i]);
				sql.append("("+CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+msgFiltro+"%')");
			}
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());

			res = prep.executeQuery();
			return res.next();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggio(String id, boolean richiesta, String ... valoriContenutiMsg)throws TestSuiteException{
		if(valoriContenutiMsg==null)throw new TestSuiteException("Parametri non passati");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			StringBuffer sql = new StringBuffer();
			String idMsgColumn = richiesta ? CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO : CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO_RISPOSTA;
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where " + idMsgColumn+" = ? AND ");
			for(int i=0; i<valoriContenutiMsg.length;i++){
				if(i>0)
					sql.append(" AND ");
				String msgFiltro = org.openspcoop2.utils.sql.SQLQueryObjectCore.getEscapeStringValue(valoriContenutiMsg[i]);
				sql.append("("+CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO+" LIKE '%"+msgFiltro+"%')");
			}
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());

			prep.setString(1, id);
			res = prep.executeQuery();
			return res.next();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedErrorMsg(String id)throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+
					CostantiDB.MSG_DIAGNOSTICI_COLUMN_SEVERITA+"<=2 ");
			prep.setString(1, id);

			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase JDBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean isTracedMessaggiWithCode(String id,String ... code)throws TestSuiteException{

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			
			prep = this.connectionMsgDiagnostici
					.prepareStatement("select count(*) as totale from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=?");
			prep.setString(1, id);
			res = prep.executeQuery();
			if(res.next()){
				long count = res.getLong("totale");
				if(count!=code.length){
					//throw new TestSuiteException("Trovati "+count+" diagnostici");
					return false;
				}
			}else{
				//throw new TestSuiteException("Diagnostico con codice ["+code[i]+"] non trovato");
				return false;
			}
			res.close();
			prep.close();
			
			for (int i = 0; i < code.length; i++) {
				
				prep = this.connectionMsgDiagnostici
						.prepareStatement("select count(*) as totale from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
								CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? AND "+
								CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"=? ");
				prep.setString(1, id);
				prep.setString(2, code[i]);
				res = prep.executeQuery();
				if(res.next()){
					long count = res.getLong("totale");
					int sommaCodiciDaTrovareUguali = 0;
					for (int j = 0; j < code.length; j++) {
						if(code[j].equals(code[i])){
							sommaCodiciDaTrovareUguali++;
						}
					}
					if(count!=sommaCodiciDaTrovareUguali){
						//throw new TestSuiteException("Trovati "+count+" diagnostici con codice ["+code[i]+"]");
						return false;
					}
				}else{
					//throw new TestSuiteException("Diagnostico con codice ["+code[i]+"] non trovato");
					return false;
				}
				res.close();
				prep.close();
			}
			
			prep = this.connectionMsgDiagnostici
					.prepareStatement("select "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE+"  from "+CostantiDB.MSG_DIAGNOSTICI+" where "+
							CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO+"=? order by "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_GDO);
			prep.setString(1, id);
			res = prep.executeQuery();
			int pos = 0;
			while(res.next()){
				String codiceTrovato = res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE);
				if(code[pos].equals(codiceTrovato)==false){
					System.out.println("Atteso codice["+code[pos]+"], trovato["+codiceTrovato+"] posizione["+pos+"]");
					return false;
				}
				pos++;
			}
			
			return true;
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase JDBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	
	public Vector<String> getMessaggiNonTrasformatiCorrettamente()throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement prep = null;
		Vector<String> resultsVector = new Vector<String>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO +" LIKE '%@%@%'");
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());
			res = prep.executeQuery();
			while(res.next()){
				
				// Verifico che non siano due email o tre email del caso di test
				String messaggio = res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
				int index1 = messaggio.indexOf("apoli@link.it");
				int index2 = messaggio.indexOf("apoli@link.it",index1+"apoli@link.it".length());
				boolean casoSpecialeEmail = false;
				if(index2>0 && index1>=0){
					casoSpecialeEmail = true;
				}
				
				if(!casoSpecialeEmail){
					if(messaggio.contains("EMAILADDRESS=info@openspcoop.org")){
						String tmp = new String(messaggio);
						tmp = tmp.replace("EMAILADDRESS=info@openspcoop.org", "");
						casoSpecialeEmail = tmp.contains("@")==false;
					}
				}
				
				if(!casoSpecialeEmail){
					if(messaggio.contains("EMAILADDRESS=apoli@link.it")){
						String tmp = new String(messaggio);
						tmp = tmp.replace("EMAILADDRESS=apoli@link.it", "");
						casoSpecialeEmail = tmp.contains("@")==false;
					}
				}
				
				if(casoSpecialeEmail==false){
					resultsVector.add(CostantiDB.MSG_DIAGNOSTICI+"."+res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO)+
							": "+res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO));
				}/*else{
					System.out.println("CASO SPECIALE EMAIL");
				}*/
			}
			res.close();
			prep.close();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
		
		return resultsVector;
	}
	
	public Vector<String> getMessaggiCheSegnalanoNullPointer()throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement prep = null;
		Vector<String> resultsVector = new Vector<String>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO +" LIKE '%NullPointer%'");
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());
			res = prep.executeQuery();
			while(res.next()){
				
				// Verifico che non siano due email o tre email del caso di test
				String messaggio = res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
				resultsVector.add(CostantiDB.MSG_DIAGNOSTICI+"."+res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO)+
						": "+messaggio);

			}
			res.close();
			prep.close();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
		
		return resultsVector;
	}
	
	public Vector<String> getMessaggiSenzaCodici()throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement prep = null;
		Vector<String> resultsVector = new Vector<String>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE +" is null OR "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE +"=''");
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());
			res = prep.executeQuery();
			while(res.next()){
				
				// Verifico che non siano due email o tre email del caso di test
				String messaggio = res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
				resultsVector.add(CostantiDB.MSG_DIAGNOSTICI+"."+res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO)+
						": "+messaggio);

			}
			res.close();
			prep.close();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
		
		return resultsVector;
	}
	
	public Vector<String> getTracciamentoNonRiuscito()throws TestSuiteException{
		ResultSet res = null;
		PreparedStatement prep = null;
		Vector<String> resultsVector = new Vector<String>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from "+CostantiDB.MSG_DIAGNOSTICI+" where "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE +" is not null AND "+CostantiDB.MSG_DIAGNOSTICI_COLUMN_CODICE +" LIKE '009%'");
			prep = this.connectionMsgDiagnostici.prepareStatement(sql.toString());
			res = prep.executeQuery();
			while(res.next()){
				
				// Verifico che non siano due email o tre email del caso di test
				String messaggio = res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
				resultsVector.add(CostantiDB.MSG_DIAGNOSTICI+"."+res.getString(CostantiDB.MSG_DIAGNOSTICI_COLUMN_IDMESSAGGIO)+
						": "+messaggio);

			}
			res.close();
			prep.close();
		} catch (Exception e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
		
		return resultsVector;
	}

	public boolean isTracedCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(String value) throws TestSuiteException{
		return this.isTracedIntoMsgDiagnosticiCorrelazione(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RICHIESTA,value);
	} 
	public boolean isTracedCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(String value) throws TestSuiteException{
		return this.isTracedIntoMsgDiagnosticiCorrelazione(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RISPOSTA,value);
	} 
	private boolean isTracedIntoMsgDiagnosticiCorrelazione(String field,String value)throws TestSuiteException{
		if(value==null)throw new TestSuiteException("Messaggio vale null");

		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" where "+
					field+"=?");
			prep.setString(1, value);
			
			res = prep.executeQuery();
			return res.next();
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	
	public boolean existsCorrelazioneApplicativaRichiestaIntoMsgDiagnosticiCorrelazione(String idMessaggio,boolean delegata) throws TestSuiteException{
		return this.existsCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idMessaggio, delegata, true);
	} 
	public boolean existsCorrelazioneApplicativaRispostaIntoMsgDiagnosticiCorrelazione(String idMessaggio,boolean delegata) throws TestSuiteException{
		return this.existsCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(idMessaggio, delegata, false);
	} 
	private boolean existsCorrelazioneApplicativaIntoMsgDiagnosticiCorrelazione(String idMessaggio,boolean delegata,boolean richiesta)throws TestSuiteException{
		
		ResultSet res = null;
		PreparedStatement prep = null;
		try {
			prep = this.connectionMsgDiagnostici
			.prepareStatement("select * from "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+" where "+
					CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_IDMESSAGGIO+"=? AND "+CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_DELEGATA+"=?");
			prep.setString(1, idMessaggio);
			if(delegata)
				prep.setInt(2, 1);
			else
				prep.setInt(2, 0);
			res = prep.executeQuery();
			res.next();
			
			String idCorrelazione = null;
			if(richiesta){
				idCorrelazione = res.getString(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RICHIESTA);
			}else{
				idCorrelazione = res.getString(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_COLUMN_ID_CORRELAZIONE_APPLICATIVA_RISPOSTA);
			}

			if(idCorrelazione!=null && !"".equals(idCorrelazione)){
				return true;
			}
			else{
				return false;
			}
			
		} catch (SQLException e) {
			throw new TestSuiteException("Errore nel database: "+e.getMessage(),
			"nella fase DBC.getResult");
		} finally{
			try{
				if(res!=null)
					res.close();
			}catch(Exception e){}
			try{
				if(prep!=null)
					prep.close();
			}catch(Exception e){}
		}
	}
	


}


