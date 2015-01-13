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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.date.DateManager;

/**
 * IDSerialGenerator_numeric
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator_numeric {

	public static String generate(Connection conDB,IDSerialGeneratorParameter param,ConfigurazionePdD config) throws ProtocolException{
		
		long attesaAttivaJDBC = config.getAttesaAttivaJDBC();
		int checkIntervalloJDBC = config.getCheckIntervalJDBC();
		String protocollo = param.getProtocolFactory().getProtocol();
		Logger log = config.getLog();
		
		long counterTmp = -1;
		
		boolean idBuildOK = false;

		long scadenzaWhile = DateManager.getTimeMillis()
				+ attesaAttivaJDBC;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		
		int iteration = 0;
		
		String table = CostantiDB.TABELLA_ID_AS_LONG;
		if(param.getInformazioneAssociataAlProgressivo()!=null){
			table = CostantiDB.TABELLA_ID_RELATIVO_AS_LONG;
		}
		
		String columnCondition = "";
		String columnValueCondition = "";
		String condition = "";
		if(param.getInformazioneAssociataAlProgressivo()!=null){
			condition = " AND "+CostantiDB.TABELLA_ID_COLONNA_INFO_ASSOCIATA+"=?";
			columnCondition = ","+CostantiDB.TABELLA_ID_COLONNA_INFO_ASSOCIATA;
			columnValueCondition = ",?";
		}
		
		while(idBuildOK==false && DateManager.getTimeMillis() < scadenzaWhile){

			iteration++;
			
			//log.info("ancoraImbustamento interval["+checkInterval+"]   secondi "+(scadenzaWhile-DateManager.getTimeMillis())/1000);

			counterTmp = -1;
			PreparedStatement pstmt = null;
			PreparedStatement pstmtInsert = null;
			ResultSet rs = null;
			try{
				// Lettura attuale valore
				StringBuffer query = new StringBuffer();
				query.append("SELECT "+CostantiDB.TABELLA_ID_COLONNA_COUNTER+" FROM ");
				query.append(table);
				query.append(" WHERE "+CostantiDB.TABELLA_ID_COLONNA_PROTOCOLLO+"=?");
				query.append(condition);
				query.append(" FOR UPDATE");
				//System.out.println("SELECT ["+query.toString()+"]");
				pstmt = conDB.prepareStatement(query.toString());
				pstmt.setString(1, protocollo);
				if(param.getInformazioneAssociataAlProgressivo()!=null){
					pstmt.setString(2, param.getInformazioneAssociataAlProgressivo());
				}
				rs = pstmt.executeQuery();
				if(rs == null) {
					pstmt.close();
					log.error("Creazione serial non riuscita: ResultSet is null?");
					throw new ProtocolException("Creazione serial non riuscita: ResultSet is null?");		
				}
				boolean exist = rs.next();

				// incremento se esiste
				if(exist){
					counterTmp = rs.getLong(CostantiDB.TABELLA_ID_COLONNA_COUNTER);
					if ((counterTmp + 1) > param.getMaxValue()) {
						if(param.isWrap()){
							counterTmp = 0;
						}else{
							throw new Exception("Max Value of identifier has been reached");
						}
					} 
					counterTmp++;
				}		
				rs.close();
				pstmt.close();

				if(exist==false){
					counterTmp = 1;
					// CREO PRIMO COUNT!
					StringBuffer queryInsert = new StringBuffer();
					queryInsert.append("INSERT INTO "+table+" ("+CostantiDB.TABELLA_ID_COLONNA_COUNTER+","+CostantiDB.TABELLA_ID_COLONNA_PROTOCOLLO+columnCondition+") ");
					queryInsert.append(" VALUES ( ? , ? "+columnValueCondition+")");
					//System.out.println("INSERT ["+queryInsert.toString()+"]");
					pstmtInsert = conDB.prepareStatement(queryInsert
							.toString());
					pstmtInsert.setLong(1, 1);
					pstmtInsert.setString(2, protocollo);
					if(param.getInformazioneAssociataAlProgressivo()!=null){
						pstmtInsert.setString(3, param.getInformazioneAssociataAlProgressivo());
					}
					pstmtInsert.execute();
					pstmtInsert.close();
				}else{
					// Incremento!
					StringBuffer queryUpdate = new StringBuffer();
					queryUpdate.append("UPDATE ");
					queryUpdate.append(table);
					queryUpdate.append(" SET "+CostantiDB.TABELLA_ID_COLONNA_COUNTER+" = ? WHERE "+CostantiDB.TABELLA_ID_COLONNA_PROTOCOLLO+"=?"+condition);
					//System.out.println("UPDATE ["+queryInsert.toString()+"]");
					pstmtInsert = conDB.prepareStatement(queryUpdate
							.toString());
					pstmtInsert.setLong(1, counterTmp);
					pstmtInsert.setString(2, protocollo);
					if(param.getInformazioneAssociataAlProgressivo()!=null){
						pstmtInsert.setString(3, param.getInformazioneAssociataAlProgressivo());
					}
					pstmtInsert.execute();
					pstmtInsert.close();
				}

				// Chiusura Transazione
				conDB.commit();

				// ID Costruito
				idBuildOK = true;

			} catch(Exception e) {
				ps.append("********* Exception Iteration ["+iteration+"] **********\n");
				e.printStackTrace(ps);
				ps.append("\n\n");
				
				//System.out.println("ERRORE: "+e.getMessage());
				//log.info("ERROR GET SERIAL SQL ["+e.getMessage()+"]");
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				try{
					if( pstmtInsert != null )
						pstmtInsert.close();
				} catch(Exception er) {}
				try{
					conDB.rollback();
				} catch(Exception er) {}
			}

			if(idBuildOK == false){
				// Per aiutare ad evitare conflitti
				try{
					Thread.sleep((new java.util.Random())
							.nextInt(checkIntervalloJDBC)); // random
				}catch(Exception eRandom){}
			}
		}

		try{
			if( ps != null ){
				ps.flush();
			}
		} catch(Exception er) {}
		try{
			if( out != null ){
				out.flush();
			}
		} catch(Exception er) {}
		try{
			if( ps != null ){
				ps.close();
			}
		} catch(Exception er) {}
		try{
			if( out != null ){
				out.close();
			}
		} catch(Exception er) {}
		
		if(idBuildOK==false || counterTmp<=0){
			String msgError = "Creazione serial non riuscita: l'accesso serializable non ha permesso la creazione del numero sequenziale";
			config.getLog().error(msgError+": "+out.toString()); // in out è presente l'intero stackTrace
			throw new ProtocolException(msgError);	
		}

		return counterTmp+"";
	}
	
}
