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
package org.openspcoop2.utils.id.serial;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.id.ApacheGeneratorConfiguration;
import org.openspcoop2.utils.id.ApacheIdentifierGenerator;
import org.openspcoop2.utils.id.apache.serial.EnumTypeGenerator;
import org.openspcoop2.utils.id.apache.serial.MaxReachedException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * IDSerialGenerator_alphanumeric
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDSerialGenerator_alphanumeric {
	
	public static String generate(Connection conDB,TipiDatabase tipoDatabase,
			IDSerialGeneratorParameter param,Logger log,InfoStatistics infoStatistics) throws UtilsException{
		
		ApacheGeneratorConfiguration generatorConfig = new ApacheGeneratorConfiguration();
		generatorConfig.setEnableLowerCaseLetter(true);
		generatorConfig.setEnableUpperCaseLetter(true);
		generatorConfig.setWrap(param.isWrap());
		generatorConfig.setType(EnumTypeGenerator.ALPHANUMERIC);
		
		
		long attesaAttivaJDBC = param.getSerializableTimeWaitMs();
		int checkIntervalloJDBC = param.getSerializableNextIntervalTimeMs();
		String protocollo = param.getProtocollo();
		
		String progressivoTmp = null;
		
		boolean idBuildOK = false;

		long scadenzaWhile = DateManager.getTimeMillis()
				+ attesaAttivaJDBC;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		
		int iteration = 0;
		
		List<String> messageException = new ArrayList<String>();
		
		String table = param.getTableName();
		if(table==null){
			if(param.getInformazioneAssociataAlProgressivo()!=null){
				table = Constants.TABELLA_ID_RELATIVO_AS_STRING;
			}
			else{
				table = Constants.TABELLA_ID_AS_STRING;
			}
		}
		
		String columnInfoAssociata = param.getColumnRelativeInfo();
		if(columnInfoAssociata==null){
			columnInfoAssociata = Constants.TABELLA_ID_COLONNA_INFO_ASSOCIATA;
		}
		
		String columnPrg = param.getColumnPrg();
		if(columnPrg==null){
			columnPrg = Constants.TABELLA_ID_COLONNA_PROGRESSIVO;
		}
		
		String columnProtocollo = param.getColumnProtocol();
		if(columnProtocollo==null){
			columnProtocollo = Constants.TABELLA_ID_COLONNA_PROTOCOLLO;
		}
		
		
//		String columnCondition = "";
//		String columnValueCondition = "";
//		String condition = "";
//		if(param.getInformazioneAssociataAlProgressivo()!=null){
//			condition = " AND "+columnInfoAssociata+"=?";
//			columnCondition = ","+columnInfoAssociata;
//			columnValueCondition = ",?";
//		}
		
		boolean maxValueAndWrapDisabled = false;
		
		List<String> valuesGenerated = new ArrayList<String>();
		
		while(maxValueAndWrapDisabled==false && idBuildOK==false && DateManager.getTimeMillis() < scadenzaWhile){

			valuesGenerated = new ArrayList<String>();
			
			iteration++;
			
			// Prima provo ad utilizzare il buffer (può darsi che un altro thread l'abbia riempito)
			if(param.getSizeBuffer()>1){
				String valueFromBuffer = IDSerialGeneratorBuffer.nextValue(IDSerialGenerator_alphanumeric.class,param.getInformazioneAssociataAlProgressivo());
				if(valueFromBuffer!=null){
					//System.out.println("GET ["+valueFromBuffer+"] FROM BUFFER");
					return valueFromBuffer;
				}
			}
			
			//log.info("ancoraImbustamento interval["+checkInterval+"]   secondi "+(scadenzaWhile-DateManager.getTimeMillis())/1000);

			progressivoTmp = null;
			PreparedStatement pstmt = null;
			PreparedStatement pstmtInsert = null;
			ResultSet rs = null;
			try{
				// Lettura attuale valore
				ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlGet.addSelectField(columnPrg);
				sqlGet.addFromTable(table);
				sqlGet.setANDLogicOperator(true);
				sqlGet.addWhereCondition(columnProtocollo+"=?");
				if(param.getInformazioneAssociataAlProgressivo()!=null){
					sqlGet.addWhereCondition(columnInfoAssociata+"=?");
				}
				sqlGet.setSelectForUpdate(true);
				
				StringBuffer query = new StringBuffer();
//				query.append("SELECT "+columnPrg+" FROM ");
//				query.append(table);
//				query.append(" WHERE "+columnProtocollo+"=?");
//				query.append(condition);
//				query.append(" FOR UPDATE");
				query.append(sqlGet.createSQLQuery());
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
					throw new UtilsException("Creazione serial non riuscita: ResultSet is null?");		
				}
				boolean exist = rs.next();

				// incremento se esiste
				if(exist){
					progressivoTmp = rs.getString(columnPrg);
					for (int i = 0; i < param.getSizeBuffer(); i++) {
						try{
							ApacheIdentifierGenerator generator = new ApacheIdentifierGenerator();
							generatorConfig.setInitialStringValue(progressivoTmp);
							generator.initialize(generatorConfig);
							progressivoTmp = generator.newID().getAsString();
							valuesGenerated.add(progressivoTmp);
						}catch(Exception eMaxRaggiunto){
							if(valuesGenerated.size()<=0){
								throw eMaxRaggiunto;
							}
							else{
								break; // utilizzo gli identificativi che ho generato fino ad ora.
							}
						}
					}
				}		
				rs.close();
				pstmt.close();

				if(exist==false){
					// First Value
					ApacheIdentifierGenerator generator = new ApacheIdentifierGenerator();
					generatorConfig.setSize(param.getSize());
					generator.initialize(generatorConfig);
					progressivoTmp = generator.newID().getAsString();
					// CREO PRIMO COUNT!
					StringBuffer queryInsert = new StringBuffer();
					ISQLQueryObject sqlInsert = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
					sqlInsert.addInsertTable(table);
					sqlInsert.addInsertField(columnPrg, "?");
					sqlInsert.addInsertField(columnProtocollo, "?");
					if(param.getInformazioneAssociataAlProgressivo()!=null){
						sqlInsert.addInsertField(columnInfoAssociata, "?");
					}
//					queryInsert.append("INSERT INTO "+table+" ("+columnPrg+","+columnProtocollo+columnCondition+") ");
//					queryInsert.append(" VALUES ( ? , ? "+columnValueCondition+")");
					queryInsert.append(sqlInsert.createSQLInsert());
					//System.out.println("INSERT ["+queryInsert.toString()+"]");
					pstmtInsert = conDB.prepareStatement(queryInsert
							.toString());
					pstmtInsert.setString(1, progressivoTmp);
					pstmtInsert.setString(2, protocollo);
					if(param.getInformazioneAssociataAlProgressivo()!=null){
						pstmtInsert.setString(3, param.getInformazioneAssociataAlProgressivo());
					}
					pstmtInsert.execute();
					pstmtInsert.close();
					
					valuesGenerated.add(progressivoTmp);
					
				}else{
					// Incremento!
					StringBuffer queryUpdate = new StringBuffer();
					ISQLQueryObject sqlUpdate = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
					sqlUpdate.addUpdateTable(table);
					sqlUpdate.addUpdateField(columnPrg, "?");
					sqlUpdate.setANDLogicOperator(true);
					sqlUpdate.addWhereCondition(columnProtocollo+"=?");
					if(param.getInformazioneAssociataAlProgressivo()!=null){
						sqlUpdate.addWhereCondition(columnInfoAssociata+"=?");
					}
//					queryUpdate.append("UPDATE ");
//					queryUpdate.append(table);
//					queryUpdate.append(" SET "+columnPrg+" = ? WHERE "+columnProtocollo+"=?"+condition);
					queryUpdate.append(sqlUpdate.createSQLUpdate());
					//System.out.println("UPDATE ["+queryInsert.toString()+"]");
					pstmtInsert = conDB.prepareStatement(queryUpdate
							.toString());
					pstmtInsert.setString(1, progressivoTmp);
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

			} catch(Throwable e) {
				ps.append("********* Exception Iteration ["+iteration+"] **********\n");
				String msg = e.getMessage(); // per evitare out of memory
				if(msg==null){
					msg = "NULL-MESSAGE";
				}
				if(messageException.contains(msg)){
					ps.append("Message already occurs: "+msg);
				}
				else{
					e.printStackTrace(ps);
					messageException.add(msg);
				}
				ps.append("\n\n");
				
				if(infoStatistics!=null){
					infoStatistics.addErrorSerializableAccess(e);
				}
				
				if(e!=null && Utilities.existsInnerException(e, MaxReachedException.class)){
					maxValueAndWrapDisabled = true;
				}
				
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
					int intervalloDestro = checkIntervalloJDBC;
					if(param.isSerializableNextIntervalTimeMsIncrementMode()){
						intervalloDestro = intervalloDestro + (iteration*param.getSerializableNextIntervalTimeMsIncrement());
						if(intervalloDestro>param.getMaxSerializableNextIntervalTimeMs()){
							intervalloDestro = param.getMaxSerializableNextIntervalTimeMs();
						}
					}
					
					int sleep = (new java.util.Random()).nextInt(intervalloDestro);
					//System.out.println("Sleep: "+sleep);
					Thread.sleep(sleep); // random
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
		
		if(maxValueAndWrapDisabled){
			String msgError = "Max Value of identifier has been reached";
			log.error(msgError+": "+out.toString()); // in out è presente l'intero stackTrace
			throw new UtilsException(msgError);	
		}
		
		if(idBuildOK==false || progressivoTmp==null){
			String msgError = "Creazione serial non riuscita: l'accesso serializable non ha permesso la creazione del numero sequenziale";
			log.error(msgError+": "+out.toString()); // in out è presente l'intero stackTrace
			throw new UtilsException(msgError);	
		}

		String vRet = valuesGenerated.remove(0);
		
		if(valuesGenerated.size()>0){
			IDSerialGeneratorBuffer.putAll(valuesGenerated,IDSerialGenerator_alphanumeric.class,param.getInformazioneAssociataAlProgressivo());
		}
		
		//System.out.println("GET ["+vRet+"] AND SET BUFFER AT SIZE ["+valuesGenerated.size()+"]");
		
		return vRet;
		
	}
	
}
