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



package org.openspcoop2.protocol.basic.diagnostica;

//import java.io.ByteArrayInputStream;
import java.io.PrintStream;
/*import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.naming.Context;
import javax.sql.DataSource;
*/
import org.apache.log4j.AppenderSkeleton;
//import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
/*import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.utils.GestoreJNDI;
import org.openspcoop2.utils.Loader;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
*/
//NOTA: Se lo script viene interrotto a meta', il file con le tracce che hanno dato errore non viene generato e nel file delle tracce restano tutte le tracce. In ogni caso, al giro successivo dello script, le tracce gia' inserite non verranno prese in considerazione, mentre le altre vengono tentate nuovamente.
//Attenzione: attualmente non e' prevista una chiave per identificare ogni messaggio, quindi i messaggi verranno inseriti tutti.

/**
 * Log4JAppender personalizzato per la gestione dei messaggi diagnostici
 * 
 * @author Andrea Manca (manca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MsgDiagnosticoLog4JAppender extends AppenderSkeleton {
	
	//private final static String MSG_DIAGNOSTICI = "msgdiagnostici";
	
	
	//private IUnmarshallingContext uctx = null;
	private PrintStream ps = null;
	/** DataSource dove attingere connessioni */
    //private DataSource ds = null;

	private String dburl = "", dbuser = "", dbpw = "";
	private String filerej = "msgdiagnostici.rejected";
	private String dbdriver = "";
	private String dataSource = null;
	private String provider = null;
	
	

	//DB Url
	public void setDBUrl(String dburl) {
		this.dburl = dburl;
	}
	public String getDBUrl() {
		return this.dburl;
	}

	//DB User
	public void setDBUser(String dbuser) {
		this.dbuser = dbuser;
	}
	public String getDBUser() {
		return this.dbuser;
	}

	//DB Password
	public void setDBPwd(String dbpw) {
		this.dbpw = dbpw;
	}
	public String getDBPwd() {
		return this.dbpw;
	}
	
	//	DB Driver
	public void setDBDriver(String dbdriver) {
		this.dbdriver= dbdriver;
	}
	public String getDBDriver() {
		return this.dbdriver;
	}
	
	//	DataSource
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getDataSource() {
		return this.dataSource;
	}
	
	//	ProviderJNDI
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getProvider() {
		return this.provider;
	}

	//File dove inserire le tracce che non si riesce ad inserire nel DB
	public void setFileRejected(String filerej) {
		this.filerej = filerej;
	}
	public String getFileRejected() {
		return this.filerej;
	}

	
	@Override
	public boolean requiresLayout() {
		return true;
	}

	@Override public void activateOptions() {
		/* ---- Inizializzazione del contesto di unmarshall ---- */
		// TODO
		/*try {
			IBindingFactory bfact = BindingDirectory.getFactory(MessaggioDiagnostico.class);
			this.uctx = bfact.createUnmarshallingContext();
		} catch(org.jibx.runtime.JiBXException e) {
			System.err.println("[MessaggioDiagnosticoAppender] Riscontrato errore durante la creazione del contesto di unmarshall:\n"+e.getMessage());
		}*/

		/* ---- Apertura file per fallimenti ---- */
		try {
			this.ps = new PrintStream(this.filerej);
		} catch (java.io.IOException ioe) {
			System.err.println("[MessaggioDiagnosticoAppender] IOException: "+ioe.getMessage());
		}
		
		/* --- Impostazione Datasource/ConnessioneSQL ---- */
		/*if(this.dataSource!=null){
			try {
				java.util.Properties context = new java.util.Properties();
				if(this.provider!=null){
					context.put(Context.PROVIDER_URL, this.provider);
				}
				GestoreJNDI jndi = new GestoreJNDI(context);
				this.ds = (DataSource) jndi.lookup(this.dataSource);
			} catch (Exception e) {
				System.err.println("[MessaggioDiagnosticoAppender] Lookup DataSource Exception: "+e.getMessage());
			}
		}else{
			try {
				Loader.getInstance().newInstance(this.dbdriver);
			} catch (Exception ex) {
				System.err.println ("[MessaggioDiagnosticoAppender] ClassNotFoundException: "+ex.getMessage());
			}
		}*/
	}

	@Override public void append(LoggingEvent event) {

		throw new RuntimeException("NotImplemented");
		
		/*
		
		Logger log = OpenSPCoopLogger.getLoggerOpenSPCoopCore();
		String singleTrace = (String) event.getMessage();

		boolean procedi = true;
		if (singleTrace != null) {
			// ---- Unmarshall del file di configurazione ---- 
			
			ByteArrayInputStream sbis = null;
			MessaggioDiagnostico msgDiagXML = null;
			try {  
				sbis = new ByteArrayInputStream(singleTrace.getBytes());
				msgDiagXML = (MessaggioDiagnostico) this.uctx.unmarshalDocument(sbis, null);
				try {
					sbis.close();
				} catch (java.io.IOException ioe) {
				}
			} catch(Exception e) {
				try {
					sbis.close();
				} catch (java.io.IOException ioe) {
				}
				if ((singleTrace != null) && !singleTrace.equals("") && !singleTrace.equals(" ")) {
					log.error("[MessaggioDiagnosticoAppender] Riscontrato errore durante l'unmarshall del file di configurazione:\n"+e.getMessage(),e);
					this.ps.println(singleTrace);
				}
				procedi = false;
			}
			
			Connection con = null;
			if (procedi) {
				//Connessione al DB
				try {
					if(this.ds != null){
						con = this.ds.getConnection();
					}else{	
						con = DriverManager.getConnection (this.dburl, this.dbuser, this.dbpw);
					}
					
					if(con==null)
						throw new Exception("Connection is null");
					
				} catch (Exception ex) {
					log.error("[MessaggioDiagnosticoAppender] GetConnection, SQLException: "+ex.getMessage(),ex);
					this.ps.println(singleTrace);
					procedi = false;
				}
			}
				
			if (procedi) {
				PreparedStatement stmt = null;
				try {
					con.setAutoCommit(false);

					Date gdo = null;
					BigInteger severita = BigInteger.ZERO;
					String idporta = "", idfunzione = "", messaggio = "";

					gdo = msgDiagXML.getOraRegistrazione();
					idporta = msgDiagXML.getIdentificativoPorta();
					idfunzione = msgDiagXML.getIdentificativoFunzione();
					severita = msgDiagXML.getLivelloDiSeverita();
					messaggio = msgDiagXML.getTestoDiagnostico();
					
					//Inserimento della traccia nel DB   
					String updateString = "INSERT INTO "+MsgDiagnosticoLog4JAppender.MSG_DIAGNOSTICI+" (gdo, idporta, idfunzione, severita, messaggio) VALUES (?, ?, ?, ?, ?)";
					stmt = con.prepareStatement(updateString);
					if(gdo!=null){
						// FIX BUG: dall'ora ritornata dal gdo letto tramite unmarshall, devo eliminare due ore.
						stmt.setTimestamp(1, new java.sql.Timestamp(gdo.getTime()-(2*60*60*1000)));
					}else
						stmt.setTimestamp(1,null);
					JDBCUtilities.setSQLStringValue(stmt,2, idporta);
					JDBCUtilities.setSQLStringValue(stmt,3, idfunzione);
					stmt.setInt(4, severita.intValue());
					JDBCUtilities.setSQLStringValue(stmt,5, messaggio);
					stmt.executeUpdate();
					stmt.close();

					con.commit();
					con.setAutoCommit(true);
				} catch (Exception ex) {
					log.error("[MessaggioDiagnosticoAppender] Registrazione non riuscita: " + ex.getMessage(),ex);
					this.ps.println(singleTrace);
				}finally{
					try{
						if(stmt!=null)
							stmt.close();
					}catch(Exception e){}
				}

				//Chiusura della connessione al DB
				try{
						con.close();
				} catch (java.sql.SQLException ex) {
					log.error("[MessaggioDiagnosticoAppender] Exception closing connection to DB "+ex.getMessage(),ex);
				}
				
			}
			 
		}
		*/
	}

	@Override
	public void close() {
		/* ---- Chiusura file msgdiagnostici.rejected ---- */
		this.ps.close();
	}
}
