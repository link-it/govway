/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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

package org.openspcoop2.core.mapping;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCParameterUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * Funzioni di utilita utilizzate dai driver
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DBProtocolPropertiesUtils {
	
	private DBProtocolPropertiesUtils() {}

	private static void logDebug(Logger log, String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	
    private static List<String> protocolPropertiesConfidentials = new ArrayList<>();
    public static void addConfidentialProtocolProperty(String nome){
    	if(!protocolPropertiesConfidentials.contains(nome)) {
    		protocolPropertiesConfidentials.add(nome);
    	}
    }
    public static boolean isConfidentialProtocolProperty(String nome) {
    	return protocolPropertiesConfidentials.contains(nome);
    }
	
	public static void crudRegistryProtocolProperty(Logger log, int type, List<org.openspcoop2.core.registry.ProtocolProperty> listPP, long idProprietario,
			ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		List<ProtocolProperty> list = null;
		if(listPP!=null) {
			list = new ArrayList<>();
			if(!listPP.isEmpty()) {
				for (org.openspcoop2.core.registry.ProtocolProperty pp : listPP) {
					list.add(new ProtocolProperty(pp));
				}
			}
		}
		crudProtocolProperty(log, type, list, idProprietario, tipologiaProprietarioProtocolProperty, connection, tipoDatabase, driverBYOK);
	}
	public static void crudConfigProtocolProperty(Logger log, int type, List<org.openspcoop2.core.config.ProtocolProperty> listPP, long idProprietario,
			ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		List<ProtocolProperty> list = null;
		if(listPP!=null) {
			list = new ArrayList<>();
			if(!listPP.isEmpty()) {
				for (org.openspcoop2.core.config.ProtocolProperty pp : listPP) {
					list.add(new ProtocolProperty(pp));
				}
			}
		}
		crudProtocolProperty(log, type, list, idProprietario, tipologiaProprietarioProtocolProperty, connection, tipoDatabase, driverBYOK);
	}

	private static void crudProtocolProperty(Logger log, int type, List<ProtocolProperty> listPP, long idProprietario,
			ProprietariProtocolProperty tipologiaProprietarioProtocolProperty, Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		
		// NOTA: l'update dei documenti, essendo mega di documenti non puo' essere implementata come delete + create
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if((listPP == null) && (type!= CostantiDB.DELETE)) 
			throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] L'oggetto listPP non puo essere null");
		if(idProprietario <= 0 ) 
			throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] id proprietario non definito");
		
		IJDBCAdapter jdbcAdapter = null;
		
		try {

			jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			switch (type) {
			case CREATE:

				if(listPP!=null) {
					for(int i=0; i<listPP.size(); i++){
					
						ProtocolProperty protocolProperty = listPP.get(i);
						if(protocolProperty.getName()==null || "".equals(protocolProperty.getName()))
							throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Nome non definito per protocolProperty ["+i+"]");
						
						// Aggiorno proprietari
						protocolProperty.setIdProprietario(idProprietario);
						protocolProperty.setTipoProprietario(tipologiaProprietarioProtocolProperty.name());
						
						int contenutiDefiniti = 0;
						
						boolean stringValue = protocolProperty.getValue()!=null && !"".equals(protocolProperty.getValue());
						String plainContenutoString = null;
						String encContenutoString = null;
						if(stringValue){ 
							contenutiDefiniti++;
							plainContenutoString = protocolProperty.getValue();
							if(driverBYOK!=null && isConfidentialProtocolProperty(protocolProperty.getName())) {
								BYOKWrappedValue byokValue = driverBYOK.wrap(protocolProperty.getValue());
								if(byokValue!=null) {
									encContenutoString = byokValue.getWrappedValue();
									plainContenutoString = byokValue.getWrappedPlainValue();
								}
							}
						}
						
						boolean numberValue = protocolProperty.getNumberValue()!=null;
						Long contenutoNumber = null;
						if(numberValue){
							contenutiDefiniti++;
							contenutoNumber = protocolProperty.getNumberValue();
						}
						
						boolean booleanValue = protocolProperty.getBooleanValue()!=null;
						Boolean contenutoBoolean = null;
						if(booleanValue){
							contenutiDefiniti++;
							contenutoBoolean = protocolProperty.getBooleanValue();
						}
						
						boolean binaryValue = protocolProperty.getByteFile()!=null && protocolProperty.getByteFile().length>0;
						byte[] contenutoBinario = null; 
						String contenutoBinarioFileName = null;
						if(binaryValue){
							contenutiDefiniti++;
							contenutoBinario = protocolProperty.getByteFile();
							if(contenutoBinario.length<3){
								String test = new String(contenutoBinario);
								if("".equals(test.trim().replace("\n", ""))){
									// eliminare \n\n
									contenutoBinario = null;	
									binaryValue = false;
									contenutiDefiniti--;
								}
							}
							if(binaryValue){
								contenutoBinarioFileName = protocolProperty.getFile();
							}
						}
						
						/**if(!stringValue && !numberValue && !binaryValue && !booleanValue){
							throw new DriverRegistroServiziException("[DBProtocolProperties::CRUDProtocolProperty] Contenuto non definito per protocolProperty ["+protocolProperty.getName()+"]");
						}*/
						// Per fare i filtri con is null e' necessario registrarlo!
						if(contenutiDefiniti>1){
							throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Contenuto definito con più tipologie per protocolProperty ["+protocolProperty.getName()+
									"] (string:"+stringValue+" number:"+numberValue+" binary:"+binaryValue+")");
						}
						
						
						// create
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
						sqlQueryObject.addInsertTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, "?");
						sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, "?");
						sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, "?");
						if(stringValue){
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, "?");
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, "?");
						}
						if(numberValue){
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER, "?");
						}
						if(booleanValue){
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN, "?");
						}
						if(binaryValue){
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, "?");
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_FILENAME, "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = connection.prepareStatement(sqlQuery);
						int index = 1;
						stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
						stm.setLong(index++, idProprietario);
						stm.setString(index++, protocolProperty.getName());
						String debug = null;
						if(stringValue){
							stm.setString(index++, plainContenutoString);
							debug = plainContenutoString;
							stm.setString(index++, encContenutoString);
						}
						if(numberValue){
							stm.setLong(index++, contenutoNumber);
							debug = contenutoNumber+"";
						}
						if(booleanValue){
							if(contenutoBoolean!=null && contenutoBoolean.booleanValue()){
								stm.setInt(index++,CostantiDB.TRUE);
								debug = CostantiDB.TRUE+"";
							}
							else{
								stm.setInt(index++,CostantiDB.FALSE);
								debug = CostantiDB.FALSE+"";
							}
						}
						if(binaryValue){
							jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
							debug = "BinaryData";
							stm.setString(index++, contenutoBinarioFileName);
							debug = debug + "," + contenutoBinarioFileName;
						}
						
						logDebug(log,"CRUDProtocolProperty CREATE : \n" + DBUtils.
								formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName(), debug));
		
						int n = stm.executeUpdate();
						stm.close();
						logDebug(log,"Inserted " + n + " row(s)");
			
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
						sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
						sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+" = ?");
						sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+" = ?");
						sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+" = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = connection.prepareStatement(sqlQuery);
						index = 1;
						stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
						stm.setLong(index++, idProprietario);
						stm.setString(index++, protocolProperty.getName());
		
						logDebug(log,"Recupero id inserito : \n" + DBUtils.
								formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName()));
		
						rs = stm.executeQuery();
		
						if (rs.next()) {
							listPP.get(i).setId(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID));
						} else {
							throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Errore avvenuto durante il recupero dell'id dopo una create");
						}
		
						rs.close();
						stm.close();
						
					}
				}
				break;

			case UPDATE:
				
				// Prelevo vecchia lista
				List<ProtocolProperty> oldLista = getListaProtocolPropertySafe(idProprietario,tipologiaProprietarioProtocolProperty, connection, tipoDatabase, 
						null); //driverBYOK);  // serve solo a verificare se devo aggiornare o meno. Non serve quindi decodificare
				
				// Gestico la nuova immagine
				if(listPP!=null) {
					for(int i=0; i<listPP.size(); i++){
						
						ProtocolProperty protocolProperty = listPP.get(i);
						if(protocolProperty.getName()==null || "".equals(protocolProperty.getName()))
							throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Nome non definito per protocolProperty ["+i+"]");
						
						// Aggiorno proprietari
						protocolProperty.setIdProprietario(idProprietario);
						protocolProperty.setTipoProprietario(tipologiaProprietarioProtocolProperty.name());
						
						int contenutiDefiniti = 0;
						
						boolean stringValue = protocolProperty.getValue()!=null && !"".equals(protocolProperty.getValue());
						String plainContenutoString = null;
						String encContenutoString = null;
						if(stringValue){ 
							contenutiDefiniti++;
							plainContenutoString = protocolProperty.getValue();
							if(driverBYOK!=null && isConfidentialProtocolProperty(protocolProperty.getName())) {
								BYOKWrappedValue byokValue = driverBYOK.wrap(protocolProperty.getValue());
								if(byokValue!=null) {
									encContenutoString = byokValue.getWrappedValue();
									plainContenutoString = byokValue.getWrappedPlainValue();
								}
							}
						}
						
						boolean numberValue = protocolProperty.getNumberValue()!=null;
						Long contenutoNumber = null;
						if(numberValue){
							contenutiDefiniti++;
							contenutoNumber = protocolProperty.getNumberValue();
						}
						
						boolean booleanValue = protocolProperty.getBooleanValue()!=null;
						Boolean contenutoBoolean = null;
						if(booleanValue){
							contenutiDefiniti++;
							contenutoBoolean = protocolProperty.getBooleanValue();
						}
						
						boolean binaryValue = protocolProperty.getByteFile()!=null && protocolProperty.getByteFile().length>0;
						byte[] contenutoBinario = null; 
						String contenutoBinarioFileName = null;
						if(binaryValue){
							contenutiDefiniti++;
							contenutoBinario = protocolProperty.getByteFile();
							if(contenutoBinario.length<3){
								String test = new String(contenutoBinario);
								if("".equals(test.trim().replace("\n", ""))){
									// eliminare \n\n
									contenutoBinario = null;	
									binaryValue = false;
									contenutiDefiniti--;
								}
							}
							if(binaryValue){
								contenutoBinarioFileName = protocolProperty.getFile();
							}
						}
						
						/**if(!stringValue && !numberValue && !binaryValue && !booleanValue){
							throw new DriverRegistroServiziException("[DBProtocolProperties::CRUDProtocolProperty] Contenuto non definito per protocolProperty ["+protocolProperty.getName()+"]");
						}*/
						// Per fare i filtri con is null e' necessario registrarlo!
						if(contenutiDefiniti>1){
							throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Contenuto definito con più tipologie per protocolProperty ["+protocolProperty.getName()+
									"] (string:"+stringValue+" number:"+numberValue+" binary:"+binaryValue+")");
						}
						
										
						/**if(doc.getId()<=0){*/
						// Rileggo sempre id, puo' essere diverso (es. importato tramite sincronizzazioni)
						protocolProperty.setId(DBUtils.getIdProtocolProperty(protocolProperty.getTipoProprietario(), idProprietario,protocolProperty.getName(), 
								connection, 
								tipoDatabase));
											
						boolean ppGiaPresente = false;
						boolean ppDaAggiornare = false;
						if(protocolProperty.getId()>0){
							for(int j=0; j<oldLista.size(); j++){
								ProtocolProperty old = oldLista.get(j);
			
								/**System.out.println("OLD["+old.getId().longValue()+"]==ATTUALE["+doc.getId().longValue()+"] ("+((doc.getId().longValue() == old.getId().longValue()))+")");*/
								if(protocolProperty.getId().longValue() == old.getId().longValue()){		
									ppGiaPresente = true; // non devo fare una insert, ma una update...
											
									// rimuovo la vecchia immagine del documento dalla lista dei doc vecchi
									oldLista.remove(j);
										
									ppDaAggiornare = true;
									break;
								}
							}
						}
	
						if(ppGiaPresente){
							if(ppDaAggiornare){
								// update
								long idPP = protocolProperty.getId();
								if(idPP<=0){
									throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] ID non definito per documento da aggiorare ["+protocolProperty.getName()+"]");
								}
								ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
								sqlQueryObject.addUpdateTable(CostantiDB.PROTOCOL_PROPERTIES);
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, "?");
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, "?");
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER, "?");
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN, "?");
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, "?");
								sqlQueryObject.addUpdateField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_FILENAME, "?");
								sqlQueryObject.addWhereCondition("id=?");
								sqlQuery = sqlQueryObject.createSQLUpdate();
								stm = connection.prepareStatement(sqlQuery);
								int index = 1;
								
								stm.setString(index++, plainContenutoString);
								stm.setString(index++, encContenutoString);
								
								if(numberValue){
									stm.setLong(index++, contenutoNumber);
								}
								else{
									stm.setNull(index++, java.sql.Types.BIGINT);
								}
								
								if(booleanValue){
									if(contenutoBoolean!=null && contenutoBoolean.booleanValue()){
										stm.setInt(index++,CostantiDB.TRUE);
									}
									else{
										stm.setInt(index++,CostantiDB.FALSE);
									}
								}
								else{
									stm.setNull(index++, java.sql.Types.INTEGER);
								}
								
								jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
								stm.setString(index++, contenutoBinarioFileName);
								
								stm.setLong(index++, idPP);
								stm.executeUpdate();
								stm.close();
							}
						}else{
							// create
							ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
							sqlQueryObject.addInsertTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, "?");
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, "?");
							sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, "?");
							if(stringValue){
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, "?");
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, "?");
							}
							if(numberValue){
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER, "?");
							}
							if(booleanValue){
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN, "?");
							}
							if(binaryValue){
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, "?");
								sqlQueryObject.addInsertField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_FILENAME, "?");
							}
							sqlQuery = sqlQueryObject.createSQLInsert();
							stm = connection.prepareStatement(sqlQuery);
							int index = 1;
							stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
							stm.setLong(index++, idProprietario);
							stm.setString(index++, protocolProperty.getName());
							String debug = null;
							if(stringValue){
								stm.setString(index++, plainContenutoString);
								debug = plainContenutoString;
								stm.setString(index++, encContenutoString);
							}
							if(numberValue){
								stm.setLong(index++, contenutoNumber);
								debug = contenutoNumber+"";
							}
							if(booleanValue){
								if(contenutoBoolean!=null && contenutoBoolean.booleanValue()){
									stm.setInt(index++,CostantiDB.TRUE);
									debug = CostantiDB.TRUE+"";
								}
								else{
									stm.setInt(index++,CostantiDB.FALSE);
									debug = CostantiDB.FALSE+"";
								}
							}
							if(binaryValue){
								jdbcAdapter.setBinaryData(stm,index++,contenutoBinario);
								debug = "BinaryData";
								stm.setString(index++, contenutoBinarioFileName);
								debug = debug + "," + contenutoBinarioFileName;
							}
							
							logDebug(log,"CRUDProtocolProperty CREATE : \n" + DBUtils.
									formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName(), debug));
			
							int n = stm.executeUpdate();
							stm.close();
							logDebug(log,"Inserted " + n + " row(s)");
				
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
							sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
							sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
							sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+" = ?");
							sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+" = ?");
							sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+" = ?");
							sqlQueryObject.setANDLogicOperator(true);
							sqlQuery = sqlQueryObject.createSQLQuery();
							stm = connection.prepareStatement(sqlQuery);
							index = 1;
							stm.setString(index++, tipologiaProprietarioProtocolProperty.name());
							stm.setLong(index++, idProprietario);
							stm.setString(index++, protocolProperty.getName());
			
							logDebug(log,"Recupero id inserito : \n" + DBUtils.
									formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario, protocolProperty.getName()));
			
							rs = stm.executeQuery();
			
							if (rs.next()) {
								listPP.get(i).setId(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID));
							} else {
								throw new CoreException("[DBProtocolProperties::CRUDProtocolProperty] Errore avvenuto durante il recupero dell'id dopo una create");
							}
			
							rs.close();
							stm.close();
							
						}
						
					}
				}
				
				if(!oldLista.isEmpty()){
					// Qualche documento e' stato cancellato.
					// Non e' piu' presente.
					for(int j=0; j<oldLista.size(); j++){
						ProtocolProperty old = oldLista.get(j);
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
						sqlQueryObject.addDeleteTable(CostantiDB.PROTOCOL_PROPERTIES);
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = connection.prepareStatement(sqlQuery);
						stm.setLong(1, old.getId());
						stm.executeUpdate();
						stm.close();
					}
				}
				
				break;

			case DELETE:
				// delete
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
				sqlQueryObject.addDeleteTable(CostantiDB.PROTOCOL_PROPERTIES);
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+" = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+"=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setString(1, tipologiaProprietarioProtocolProperty.name());
				stm.setLong(2, idProprietario);
				stm.executeUpdate();
				stm.close();

				logDebug(log,"CRUDDocumento DELETE : \n" + DBUtils.formatSQLString(sqlQuery, tipologiaProprietarioProtocolProperty.name(), idProprietario));

				break;
				
			default:
				// nop
			}

		} catch (SQLException se) {
			throw new CoreException("[DBProtocolProperties::CRUDDocumento] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new CoreException("[DBProtocolProperties::CRUDDocumento] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) 
					rs.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(stm!=null) 
					stm.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	public static List<org.openspcoop2.core.registry.ProtocolProperty> getListaProtocolPropertyRegistry(long idProprietario, ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		List<ProtocolProperty> l = getListaProtocolProperty(idProprietario, tipologiaProprietario, connection, tipoDatabase, driverBYOK);
		List<org.openspcoop2.core.registry.ProtocolProperty> lPP = null;
		if(l==null) {
			return lPP;
		}
		lPP = new ArrayList<>();
		if(!l.isEmpty()) {
			for (ProtocolProperty protocolProperty : l) {
				lPP.add(protocolProperty.toRegistry());
			}
		}
		return lPP;
	}
	public static List<org.openspcoop2.core.config.ProtocolProperty> getListaProtocolPropertyConfig(long idProprietario, ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		List<ProtocolProperty> l = getListaProtocolProperty(idProprietario, tipologiaProprietario, connection, tipoDatabase, driverBYOK);
		List<org.openspcoop2.core.config.ProtocolProperty> lPP = null;
		if(l==null) {
			return lPP;
		}
		lPP = new ArrayList<>();
		if(!l.isEmpty()) {
			for (ProtocolProperty protocolProperty : l) {
				lPP.add(protocolProperty.toConfig());
			}
		}
		return lPP;
	}
	
	public static List<ProtocolProperty> getListaProtocolPropertySafe(long idProprietario, ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException  {
		List<ProtocolProperty> l = null;
		try{
			l = getListaProtocolProperty(idProprietario,tipologiaProprietario, connection, tipoDatabase, driverBYOK);
		}catch(NotFoundException dNotFound){
			l = new ArrayList<>();
		}
		return l;
	}
	public static List<ProtocolProperty> getListaProtocolProperty(long idProprietario, ProprietariProtocolProperty tipologiaProprietario, 
			Connection connection,
			String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(idProprietario <= 0 ) 
			throw new CoreException("[DBProtocolProperties::getListaProtocolProperty] id proprietario non definito");
		
		try {
		
			List<ProtocolProperty> listPP = new ArrayList<>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+" = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+" = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setString(1, tipologiaProprietario.name());
			stm.setLong(2, idProprietario);
			rs = stm.executeQuery();
			
			while(rs.next()){
				ProtocolProperty pp = getProtocolProperty(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID), connection,tipoDatabase, driverBYOK); 
				listPP.add(pp);
			}
			
			if(listPP.isEmpty())
				throw new NotFoundException("ProtocolProperty con tipologiaProprietario["+tipologiaProprietario.name()+
						"] e idProprietario["+idProprietario+"] non trovati");
			
			return listPP;

		} catch (SQLException se) {
			throw new CoreException("[DBProtocolProperties::getListaProtocolProperty] SQLException : " + se.getMessage(),se);
		}catch (NotFoundException dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new CoreException("[DBProtocolProperties::getListaProtocolProperty] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) 
					rs.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(stm!=null) 
					stm.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	public static org.openspcoop2.core.registry.ProtocolProperty getProtocolPropertyRegistry(long id, Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		ProtocolProperty pp = getProtocolProperty(id, connection, tipoDatabase, driverBYOK);
		if(pp==null) {
			return null;
		}
		return pp.toRegistry();
	}
	public static org.openspcoop2.core.config.ProtocolProperty getProtocolPropertyConfig(long id, Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		ProtocolProperty pp = getProtocolProperty(id, connection, tipoDatabase, driverBYOK);
		if(pp==null) {
			return null;
		}
		return pp.toConfig();
	}
	public static ProtocolProperty getProtocolProperty(long id, Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException,NotFoundException {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(id <= 0 ) 
			throw new CoreException("[DBProtocolProperties::getProtocolProperty] id non definito");
		
		try {
		
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
				
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_FILENAME);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID+" = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			
			ProtocolProperty pp = null;
			if(rs.next()){
				pp = new ProtocolProperty();
				pp.setTipoProprietario(rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO));
				pp.setIdProprietario(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO));
				pp.setName(rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME));
				
				String plainStringValue = rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING);
				String encStringValue = rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING);
				if(encStringValue!=null && StringUtils.isNotEmpty(encStringValue)) {
					if(driverBYOK!=null) {
						pp.setValue(driverBYOK.unwrapAsString(encStringValue));
					}
					else {
						pp.setValue(encStringValue);
					}
				}
				else {
					pp.setValue(plainStringValue);
				}
				
				pp.setNumberValue(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER));
				if(rs.wasNull()){
					pp.setNumberValue(null);
				}
				int value = rs.getInt(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN);
				pp.setBooleanValue(value == CostantiDB.TRUE);
				if(rs.wasNull()){
					pp.setBooleanValue(null);
				}
				pp.setByteFile(jdbcAdapter.getBinaryData(rs,CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY));
				pp.setFile(rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_FILENAME));
				pp.setId(rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID));
			}
			
			if(pp==null)
				throw new NotFoundException("ProtocolProperty con id["+id+"] non trovato");
			
			return pp;

		} catch (SQLException se) {
			throw new CoreException("[DBProtocolProperties::getProtocolProperty] SQLException : " + se.getMessage(),se);
		}catch (NotFoundException dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new CoreException("[DBProtocolProperties::getProtocolProperty] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(rs!=null) 
					rs.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(stm!=null) 
					stm.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}
	
	
	
	
	
	public static boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome,
			Connection connection, String tipoDatabase) throws CoreException {

		boolean exist = false;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addSelectField(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+" = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+" = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+" = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setString(1, proprietarioProtocolProperty.name());
			stm.setLong(2, idProprietario);
			stm.setString(3, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new CoreException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try {
				if(rs!=null) 
					rs.close();
			} catch (Exception e) {
				// ignore
			}
			try {
				if(stm!=null) 
					stm.close();
			} catch (Exception e) {
				// ignore
			}

		}

		return exist;
	}

	public static org.openspcoop2.core.registry.ProtocolProperty getProtocolPropertyRegistry(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome,
			Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		ProtocolProperty pp = getProtocolProperty(proprietarioProtocolProperty, idProprietario, nome, connection, tipoDatabase, driverBYOK);
		if(pp==null) {
			return null;
		}
		return pp.toRegistry();
	}
	public static org.openspcoop2.core.config.ProtocolProperty getProtocolPropertyConfig(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome,
			Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		ProtocolProperty pp = getProtocolProperty(proprietarioProtocolProperty, idProprietario, nome, connection, tipoDatabase, driverBYOK);
		if(pp==null) {
			return null;
		}
		return pp.toConfig();
	}
	public static ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome,
			Connection connection, String tipoDatabase, IDriverBYOK driverBYOK) throws CoreException {
		String nomeMetodo = "getProtocolProperty";
		try {
			long idPP = DBUtils.getIdProtocolProperty(proprietarioProtocolProperty.name(),idProprietario,nome,connection,tipoDatabase);
			return getProtocolProperty(idPP, connection, tipoDatabase, driverBYOK);

		} catch (Exception se) {
			throw new CoreException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		}
	}
	
	
	private static final String AND_SEPARATOR = " AND ";
	private static final String IS_NULL_CONDITION = "is null";
	public static void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, List<FiltroRicercaProtocolProperty> list, String tabella) throws SQLQueryObjectException{
		if(list!=null && !list.isEmpty()){
			String [] conditions = new String[list.size()];
			for (int i = 0; i < conditions.length; i++) {
				String aliasTabella = "pp"+i+tabella;
				sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES, aliasTabella);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+"=?");
				sqlQueryObject.addWhereCondition(aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+"="+tabella+".id");
				FiltroRicercaProtocolProperty f = list.get(i);
								
				if(f.getName()!=null){
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + AND_SEPARATOR;
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+"=?";
				}
				
				if(f.getValueAsString()!=null){
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + AND_SEPARATOR;
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING+"=?";
				}
				else if(f.getValueAsLong()!=null){
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + AND_SEPARATOR;
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER+"=?";
				}
				else if(f.getValueAsBoolean()!=null){
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + AND_SEPARATOR;
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN+"=?";
				}
				else {
					if(conditions[i]!=null){
						conditions[i] = conditions[i] + AND_SEPARATOR;
					}
					else {
						conditions[i] = "";
					}
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING+" "+IS_NULL_CONDITION;
					conditions[i] = conditions[i] + AND_SEPARATOR;
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_NUMBER+" "+IS_NULL_CONDITION;
					conditions[i] = conditions[i] + AND_SEPARATOR;
					conditions[i] = conditions[i] + " " + aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BOOLEAN+" "+IS_NULL_CONDITION;
				}
				
				// casoSpecialeValoreNull
				ISQLQueryObject sqlQueryObjectPropertyNotExists = null;
				// in un caso dove il valore non e' definito nel database ci possono essere due casistiche:
				// 1) Passando via govwayConsole, la proprieta' esiste con il nome ('name') ed e' valorizzata null in tutte le colonne (value_string,value_number,value_boolean)
				// 2) Passando via govwayLoader, in una configurazione xml, non si definisce la proprietà senza il valore, quindi la riga con il nome non esistera proprio nel db.
				if(f.getValueAsString()==null && f.getValueAsLong()==null && f.getValueAsBoolean()==null){
					
					ISQLQueryObject sqlQueryObjectPropertyNotExistsInternal = sqlQueryObject.newSQLQueryObject();
					String aliasTabellaNotExists =  "not_exists_"+aliasTabella;
					sqlQueryObjectPropertyNotExistsInternal.addFromTable(CostantiDB.PROTOCOL_PROPERTIES, aliasTabellaNotExists);
					sqlQueryObjectPropertyNotExistsInternal.addSelectField(aliasTabellaNotExists, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
					sqlQueryObjectPropertyNotExistsInternal.addWhereCondition(aliasTabellaNotExists+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO+"="+aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO);
					sqlQueryObjectPropertyNotExistsInternal.addWhereCondition(aliasTabellaNotExists+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO+"="+aliasTabella+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO);
					sqlQueryObjectPropertyNotExistsInternal.addWhereCondition(aliasTabellaNotExists+"."+CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+"=?");
					sqlQueryObjectPropertyNotExistsInternal.setANDLogicOperator(true);
					
					sqlQueryObjectPropertyNotExists = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectPropertyNotExists.addWhereExistsCondition(true, sqlQueryObjectPropertyNotExistsInternal);

					conditions[i] = "( " + conditions[i] + " ) OR ( " + sqlQueryObjectPropertyNotExists.createSQLConditions() + " )";
				}
			}
			sqlQueryObject.addWhereCondition(true, conditions);
		}
	}
	
	public static void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, 
			List<FiltroRicercaProtocolProperty> list, ProprietariProtocolProperty proprietario,
			String tipoDatabase, Logger log) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		
		JDBCParameterUtilities jdbcParameterUtilities = new JDBCParameterUtilities(TipiDatabase.toEnumConstant(tipoDatabase));
		
		if(list!=null && !list.isEmpty()){
			for (int i = 0; i < list.size(); i++) {
				
				logDebug(log,"FiltroRicercaProtocolProperty size:"+list.size()+" ["+i+"] Proprietario stmt.setString("+proprietario.name()+")");
				stmt.setString(index++, proprietario.name());
				
			}
			for (int i = 0; i < list.size(); i++) {
				
				String prefix = "FiltroRicercaProtocolProperty["+i+"] ";
				
				FiltroRicercaProtocolProperty f = list.get(i);
				if(f.getName()!=null){
					logDebug(log,prefix+"Name stmt.setString("+f.getName()+")");
					stmt.setString(index++, f.getName());
				}
				
				if(f.getValueAsString()!=null){
					logDebug(log,prefix+"ValueAsString stmt.setString("+f.getValueAsString()+")");
					jdbcParameterUtilities.setParameter(stmt, index++, f.getValueAsString(), String.class);
				}
				else if(f.getValueAsLong()!=null){
					logDebug(log,prefix+"ValueAsLong stmt.setLong("+f.getValueAsLong()+")");
					jdbcParameterUtilities.setParameter(stmt, index++, f.getValueAsLong(), Long.class);
				}
				else if(f.getValueAsBoolean()!=null){
					int value = f.getValueAsBoolean()!=null && f.getValueAsBoolean().booleanValue() ? CostantiDB.TRUE : CostantiDB.FALSE;
					logDebug(log,prefix+"ValueAsBoolean stmt.setInt("+value+")");
					jdbcParameterUtilities.setParameter(stmt, index++, value, Integer.class);
				}
				
				// casoSpecialeValoreNull
				// in un caso dove il valore non e' definito nel database ci possono essere due casistiche:
				// 1) Passando via govwayConsole, la proprieta' esiste con il nome ('name') ed e' valorizzata null in tutte le colonne (value_string,value_number,value_boolean)
				// 2) Passando via govwayLoader, in una configurazione xml, non si definisce la proprietà senza il valore, quindi la riga con il nome non esistera proprio nel db.
				if(f.getValueAsString()==null && f.getValueAsLong()==null && f.getValueAsBoolean()==null){
					logDebug(log,prefix+"Name stmt.setString("+f.getName()+")");
					stmt.setString(index++, f.getName());
				}
			}
		}
	}
}
