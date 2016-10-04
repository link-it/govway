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
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.testsuite.core.TestSuiteException;

/**
 * Gestione del Database di Tracciamento
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DatabaseComponent {





	/** PRIVATE FIELD **/

	private Connection connectionTracciamento;
	private Connection connectionPdD;
	private String dataSourceTracciamento;
	private String dataSourcePdD;
	public String getDataSourcePdD() {
		return this.dataSourcePdD;
	}
	public String getDataSourceTracciamento() {
		return this.dataSourceTracciamento;
	}
	
	private VerificatoreTracciaRichiesta verificatoreTracciaRichiesta;
	private VerificatoreTracciaRisposta verificatoreTracciaRisposta;
	private VerificatoreMessaggi verificatoreMessaggi;
	@SuppressWarnings("unused")
	private String protocollo;

	/** ******************* Costruttore********************************* */
	public DatabaseComponent(String dataSourceTracciamento, Properties contextJNDITracciamento,String protocolloSdk) {
		this.setConnection(dataSourceTracciamento,contextJNDITracciamento,true);
		this.setConnection(dataSourceTracciamento,contextJNDITracciamento,false);
		this.dataSourceTracciamento = dataSourceTracciamento;
		this.dataSourcePdD = dataSourceTracciamento;
		initVerificatori(protocolloSdk);
	}
	public DatabaseComponent(String dataSourceTracciamento, Properties contextJNDITracciamento,
			String dataSourcePdD, Properties contextJNDIPdD,String protocolloSdk) {
		this.setConnection(dataSourceTracciamento,contextJNDITracciamento,true);
		this.setConnection(dataSourcePdD,contextJNDIPdD,false);
		this.dataSourceTracciamento = dataSourceTracciamento;
		this.dataSourcePdD = dataSourcePdD;
		initVerificatori(protocolloSdk);
	}
	public DatabaseComponent(String driverJDBC,String connectionUrlTracciamento,String usernameTracciamento,String passwordTracciamento,String protocolloSdk) {
		this.setConnection(driverJDBC,connectionUrlTracciamento,usernameTracciamento,passwordTracciamento,true);
		this.setConnection(driverJDBC,connectionUrlTracciamento,usernameTracciamento,passwordTracciamento,false);
		initVerificatori(protocolloSdk);
	}
	public DatabaseComponent(String driverJDBC,String connectionUrlTracciamento,String usernameTracciamento,String passwordTracciamento,
			String connectionUrlPdD,String usernamePdD,String passwordPdD,String protocolloSdk) {
		this.setConnection(driverJDBC,connectionUrlTracciamento,usernameTracciamento,passwordTracciamento,true);
		this.setConnection(driverJDBC,connectionUrlPdD,usernamePdD,passwordPdD,false);
		initVerificatori(protocolloSdk);
	}

	private void initVerificatori(String protocollo){
		this.verificatoreTracciaRichiesta = new VerificatoreTracciaRichiesta(this.connectionTracciamento, protocollo);
		this.verificatoreTracciaRisposta = new VerificatoreTracciaRisposta(this.connectionTracciamento, protocollo);
		this.verificatoreMessaggi = new VerificatoreMessaggi(this.connectionPdD, protocollo);
		this.protocollo = protocollo;
	}
	

	public void close() throws TestSuiteException{
		try{
			String error = null;
			try{
				if(this.connectionTracciamento!=null && this.connectionTracciamento.isClosed()==false){
					this.connectionTracciamento.close();
				}
			}catch(Exception e){
				error = "TracciamentoConnection close error: "+e.getMessage()+"\n"; 
			}

			try{
				if(this.connectionPdD!=null && this.connectionPdD.isClosed()==false){
					this.connectionPdD.close();
				}
			}catch(Exception e){
				error = "PdDConnection close error: "+e.getMessage(); 
			}

			if(error!=null)
				throw new Exception(error);
		}catch(Exception e){
			System.out.println("Errore durante la chiusura delle connessione: "+e.getMessage());
		}
	}




	/** ********************Crea una connessione****************************** */

	private void setConnection(String dataSource,Properties propJNDI,boolean tracciamento) throws TestSuiteException {
		InitialContext ctx = null;
		try {
			ctx = new InitialContext(propJNDI);
			DataSource ds = (DataSource) ctx.lookup(dataSource); 
			if(ds==null)
				throw new Exception("dataSource is null");

			if(tracciamento)
				this.connectionTracciamento = ds.getConnection();
			else
				this.connectionPdD = ds.getConnection();

		} catch (Exception e) {
			throw new TestSuiteException("Impossibile instanziare la connessione al database ("+dataSource+")("+tracciamento+"): "+e.getMessage());
		}finally{
			try{
				if(ctx!=null)
					ctx.close();
			} catch (Exception e) {}
		}
	}

	private void setConnection(String driverJDBC,String connectionUrl,String username,String password,boolean tracciamento) throws TestSuiteException {
		try {

			// Carico driver JDBC
			Class.forName(driverJDBC); 

			if(tracciamento)
				this.connectionTracciamento = DriverManager.getConnection(connectionUrl, username, password); 
			else
				this.connectionPdD = DriverManager.getConnection(connectionUrl, username, password); 

		} catch (Exception e) {
			throw new TestSuiteException("Impossibile instanziare la connessione al database ("+connectionUrl+")("+tracciamento+"): "+e.getMessage());
		}
	}








	/** ******************** Verificatori ****************************** */

	public VerificatoreTracciaRichiesta getVerificatoreTracciaRichiesta() {
		return this.verificatoreTracciaRichiesta;
	}
	public VerificatoreTracciaRisposta getVerificatoreTracciaRisposta() {
		return this.verificatoreTracciaRisposta;
	}
	public VerificatoreTracciaRichiesta getVerificatoreTracciaComune() {
		return this.verificatoreTracciaRichiesta; // il metodo esiste solo per maggiore leggibilita' del codice di test. E' ininflunte usare richiesta o risposta
	}

	public VerificatoreMessaggi getVerificatoreMessaggi() {
		return this.verificatoreMessaggi;
	}
	

}


