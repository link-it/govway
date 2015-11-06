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

package org.openspcoop2.utils.id.serial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * ClientTest
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientTest {

	public static void main(String[] args) throws Exception {
		
		TipiDatabase tipoDatabase = null;
		if(args.length>0){
			if(!"${tipoDatabase}".equals(args[0].trim())){
				tipoDatabase = TipiDatabase.toEnumConstant(args[0].trim());
			}
		}

		String url = null;
		String driver = null;
		String userName = null;
		String password = null;

		switch (tipoDatabase) {
		case POSTGRESQL:
			url = "jdbc:postgresql://localhost/prova";
			driver = "org.postgresql.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case MYSQL:
			url = "jdbc:mysql://localhost/prova";
			driver = "com.mysql.jdbc.Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case ORACLE:
			url = "jdbc:oracle:thin:@localhost:1521:XE";
			driver = "oracle.jdbc.OracleDriver";
			userName = "prova";
			password = "prova";
			break;
		case HSQL:
			url = "jdbc:hsqldb:hsql://localhost:9001/";
			driver = "org.hsqldb.jdbcDriver";
			userName = "sa";
			password = "";
			break;
		case SQLSERVER:
			url = "jdbc:sqlserver://localhost:1433;databaseName=prova";
			driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		case DB2:
			url = "jdbc:db2://127.0.0.1:50000/prova";
			driver = "com.ibm.db2.jcc.DB2Driver";
			userName = "openspcoop2";
			password = "openspcoop2";
			break;
		default:
			break;
		}
		if(args.length>1){
			String urlCustom = args[1].trim();
			if(!"${url}".equals(urlCustom)){
				url = urlCustom;
			}
		}
		if(args.length>2){
			String usernameCustom = args[2].trim();
			if(!"${username}".equals(usernameCustom)){
				userName = usernameCustom;
			}
		}
		if(args.length>3){
			String passwordCustom = args[3].trim();
			if(!"${password}".equals(passwordCustom)){
				password = passwordCustom;
			}
		}


		Class.forName(driver).newInstance();
		Connection con = null;
		Statement stmtDelete = null;
		PreparedStatement stmtInsert = null;
		try{
			con = DriverManager.getConnection(url, userName, password);


			String delete = "delete from id_messaggio_relativo";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();
			
			delete = "delete from id_messaggio";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();
			
			delete = "delete from id_messaggio_prg";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();
			
			delete = "delete from id_messaggio_relativo_prg";
			stmtDelete = con.createStatement();
			stmtDelete.execute(delete);
			stmtDelete.close();
			
			
			Logger log = Logger.getLogger(ClientTest.class);
			
			IDSerialGenerator serialGenerator = new IDSerialGenerator();
			
			IDSerialGeneratorParameter serialGeneratorParameter = new IDSerialGeneratorParameter("ApplicationContext");
			
			// Tempo di attesa jdbc
			//serialGeneratorParameter.setSerializableTimeWaitMs(60000);
			//serialGeneratorParameter.setSerializableNextIntervalTimeMs(100);
			
				
			
			/** TEST N.1 PROGRESSIVO */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(false);			
			System.out.println("\n\n==========================================");
			System.out.println("Test 1. Progressivo numerico");
			test(serialGenerator, serialGeneratorParameter, con, log, true);
			
			
			/** TEST N.2 PROGRESSIVO con MAX */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(10l);
			System.out.println("\n\n==========================================");
			System.out.println("Test 2. Progressivo numerico con max 10");
			boolean foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, con, log, true);
			}catch(Exception e){
				foundError = true;
				System.out.println("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			
			/** TEST N.3 PROGRESSIVO con MAX e Wrap */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setMaxValue(10l);
			System.out.println("\n\n==========================================");
			System.out.println("Test 3. Progressivo numerico con max 10 e wrap");
			test(serialGenerator, serialGeneratorParameter, con, log, true);
			
			
			
			
			/** TEST N.4 PROGRESSIVO (InfoAssociata) */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(Long.MAX_VALUE);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			System.out.println("\n\n==========================================");
			System.out.println("Test 4. Progressivo numerico (InfoAssociata)");
			test(serialGenerator, serialGeneratorParameter, con, log, true);
			
			
			/** TEST N.5 PROGRESSIVO con MAX (InfoAssociata) */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setMaxValue(10l);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			System.out.println("\n\n==========================================");
			System.out.println("Test 5. Progressivo numerico con max 10 (InfoAssociata)");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, con, log, true);
			}catch(Exception e){
				foundError = true;
				System.out.println("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			
			/** TEST N.6 PROGRESSIVO con MAX e Wrap (InfoAssociata)  */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.NUMERIC);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setMaxValue(10l);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("Associata");
			System.out.println("\n\n==========================================");
			System.out.println("Test 6. Progressivo numerico con max 10 e wrap");
			test(serialGenerator, serialGeneratorParameter, con, log, true);
			
			
			
			
			
			/** TEST N.7 PROGRESSIVO con MAX */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo(null); // annullo il precedente assegnamento
			System.out.println("\n\n==========================================");
			System.out.println("Test 8. Progressivo alfanumerico con size 2");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, con, log, false);
			}catch(Exception e){
				foundError = true;
				System.out.println("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			
			/** TEST N.8 PROGRESSIVO con MAX e Wrap */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setSize(1);
			System.out.println("\n\n==========================================");
			System.out.println("Test 9. Progressivo numerico con size 2 e wrap");
			test(serialGenerator, serialGeneratorParameter, con, log, false);
			
			
			
			/** TEST N.9 PROGRESSIVO con MAX (InfoAssociata) */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGeneratorParameter.setWrap(false);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("InfoAssociata");
			System.out.println("\n\n==========================================");
			System.out.println("Test 9. Progressivo alfanumerico con size 2 (InfoAssociata)");
			foundError = false;
			try{
				test(serialGenerator, serialGeneratorParameter, con, log, false);
			}catch(Exception e){
				foundError = true;
				System.out.println("Errore Atteso: "+e.getMessage());
			}
			if(foundError==false){
				throw new Exception("Atteso errore di max value, errore non rilevato");
			}
			
			/** TEST N.10 PROGRESSIVO con MAX e Wrap (InfoAssociata) */
			
			serialGeneratorParameter.setTipo(IDSerialGeneratorType.ALFANUMERICO);
			serialGeneratorParameter.setWrap(true);
			serialGeneratorParameter.setSize(1);
			serialGeneratorParameter.setInformazioneAssociataAlProgressivo("InfoAssociata");
			System.out.println("\n\n==========================================");
			System.out.println("Test 10. Progressivo numerico con size 2 e wrap (InfoAssociata)");
			test(serialGenerator, serialGeneratorParameter, con, log, false);

			
			
		}finally{
			try{
				stmtDelete.close();
			}catch(Exception eClose){}
			try{
				stmtInsert.close();
			}catch(Exception eClose){}
			try{
				con.close();
			}catch(Exception eClose){}
		}


	}


	
	public static void test(IDSerialGenerator serialGenerator, IDSerialGeneratorParameter param,
			Connection con, Logger log, boolean isNumber) throws Exception{
		List<String> check = new ArrayList<String>();
		
		String v = null;
		int i = 0;
		boolean rilevatiValoriDuplicati = false;
		try{
			int limit = 100;
			for (; i < limit; i++) {
				v = null;
				if(isNumber){
					long l = serialGenerator.buildIDAsNumber(param, con, log);
					v = l + "";
				}
				else{
					v = serialGenerator.buildID(param, con, log);
					if(!RegularExpressionEngine.isMatch((v+""),"^[a-zA-Z0-9]*$")){
						throw new UtilsException("Deve essere fornito [a-zA-Z0-9] trovato ["+v+"]");
					}
				}
				if(!check.contains(v)){
					check.add(v);
				}else{
					if(param.isWrap()){
						rilevatiValoriDuplicati = true;
					}else{
						throw new Exception("Valore ["+v+"] gia generato");
					}
				}
				if(i<20){
					System.out.println("VALORE ["+i+"]: "+v);
				}else if(i%50 == 0 )
					System.out.println("VALORE ["+i+"]: "+v);
			}
		}finally{
			System.out.println("VALORE LAST ["+i+"]: "+v);
			System.out.println("Duplicati: "+rilevatiValoriDuplicati);
		}
	}
	
}
