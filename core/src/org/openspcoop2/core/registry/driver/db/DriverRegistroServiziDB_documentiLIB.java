/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Classe utilizzata per effettuare query ad un registro dei servizi openspcoop
 * formato db.
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_documentiLIB {
		
	
	public static void CRUDDocumento(int type, List<Documento> documenti, long idProprietario,
			ProprietariDocumento tipologiaProprietarioDocumento, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException {
		
		// NOTA: l'update dei documenti, essendo mega di documenti non puo' essere implementata come delete + create
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] id proprietario non definito");
		
		IJDBCAdapter jdbcAdapter = null;
		
		try {

			jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			
			switch (type) {
			case CREATE:

				if((documenti == null)) 
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] L'oggetto documenti non puo essere null");
				
				for(int i=0; i<documenti.size(); i++){
				
					Documento doc = documenti.get(i);
					if(doc.getFile()==null || "".equals(doc.getFile()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Nome non definito per documento ["+i+"]");
					if(doc.getRuolo()==null || "".equals(doc.getRuolo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Ruolo non definito per documento ["+doc.getFile()+"]");
					if(doc.getTipo()==null || "".equals(doc.getTipo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Tipo non definito per documento ["+doc.getFile()+"]");
					if(doc.getByteContenuto()==null || doc.getByteContenuto().length<=0)
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Contenuto non definito per documento ["+doc.getFile()+"]");
					BeanUtilities.validateTipoRuolo(doc.getTipo(),doc.getRuolo());
					
					byte[] contenuto = doc.getByteContenuto();
					if(contenuto.length<3){
						String test = new String(contenuto);
						if("".equals(test.trim().replaceAll("\n", ""))){
							// eliminare \n\n
							contenuto = null;	
						}
					}
					
					
					// create
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.DOCUMENTI);
					sqlQueryObject.addInsertField("ruolo", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("id_proprietario", "?");
					sqlQueryObject.addInsertField("tipo_proprietario", "?");
					if(contenuto!=null)
						sqlQueryObject.addInsertField("contenuto", "?");
					if(doc.getOraRegistrazione()!=null)
						sqlQueryObject.addInsertField("ora_registrazione", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = connection.prepareStatement(sqlQuery);
					stm.setString(1, doc.getRuolo());
					stm.setString(2, doc.getTipo());
					stm.setString(3, doc.getFile());
					stm.setLong(4, idProprietario);
					stm.setString(5, tipologiaProprietarioDocumento.toString());
					int index = 6;
					if(contenuto!=null){
						jdbcAdapter.setBinaryData(stm,index,contenuto);
						index++;
					}
					if(doc.getOraRegistrazione()!=null){
						stm.setTimestamp(index, new Timestamp(doc.getOraRegistrazione().getTime()));
						index++;
					}
					
					DriverRegistroServiziDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
	
					int n = stm.executeUpdate();
					stm.close();
					DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
		
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addSelectField("ora_registrazione");
					sqlQueryObject.addWhereCondition("ruolo = ?");
					sqlQueryObject.addWhereCondition("tipo = ?");
					sqlQueryObject.addWhereCondition("nome = ?");
					sqlQueryObject.addWhereCondition("id_proprietario = ?");
					sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = connection.prepareStatement(sqlQuery);
					stm.setString(1, doc.getRuolo());
					stm.setString(2, doc.getTipo());
					stm.setString(3, doc.getFile());
					stm.setLong(4, idProprietario);
					stm.setString(5, tipologiaProprietarioDocumento.toString());
	
					DriverRegistroServiziDB_LIB.log.debug("Recupero idConnettore e oraRegistrazione inserito : \n" + DBUtils.formatSQLString(sqlQuery,doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
	
					rs = stm.executeQuery();
	
					if (rs.next()) {
						documenti.get(i).setId(rs.getLong("id"));
						documenti.get(i).setOraRegistrazione(rs.getTimestamp("ora_registrazione"));
					} else {
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
					}
	
					rs.close();
					stm.close();
					
				}
				break;

			case UPDATE:
				
				if((documenti == null)) 
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] L'oggetto documenti non puo essere null");
				
				// Prelevo vecchia lista
				List<Documento> oldLista = null;
				try{
					oldLista = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(idProprietario,tipologiaProprietarioDocumento, false, connection, tipoDatabase);
				}catch(DriverRegistroServiziNotFound dNotFound){
					oldLista = new ArrayList<Documento>();
				}
				
				// Gestico la nuova immagine
				for(int i=0; i<documenti.size(); i++){
					
					Documento doc = documenti.get(i);
					if(doc.getFile()==null || "".equals(doc.getFile()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Nome non definito per documento ["+i+"]");
					if(doc.getRuolo()==null || "".equals(doc.getRuolo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Ruolo non definito per documento ["+doc.getFile()+"]");
					if(doc.getTipo()==null || "".equals(doc.getTipo()))
						throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Tipo non definito per documento ["+doc.getFile()+"]");
					BeanUtilities.validateTipoRuolo(doc.getTipo(),doc.getRuolo());
					
					//if(doc.getId()<=0){
					// Rileggo sempre id, puo' essere diverso (es. importato tramite sincronizzazioni)
					String tipologiaProprietarioDocumentoString = null;
					if(tipologiaProprietarioDocumento!=null){
						tipologiaProprietarioDocumentoString = tipologiaProprietarioDocumento.toString();
					}
					doc.setId(DBUtils.getIdDocumento(doc.getFile(), doc.getTipo(), doc.getRuolo(), idProprietario, connection, 
							DriverRegistroServiziDB_LIB.tipoDB, tipologiaProprietarioDocumentoString));
					//}
					// L'ora di registrazione deve essere impostata solo se non presente, in tale caso siamo in un caso di creazione del nuovo documento
					if(doc.getOraRegistrazione()==null){
						doc.setOraRegistrazione(new Date());
					}
					
					// Assegno corretto idProprietario se id e' diverso (es. importato tramite sincronizzazioni)
					doc.setIdProprietarioDocumento(idProprietario);
					
					boolean documentoGiaPresente = false;
					boolean documentoDaAggiornare = false;
					if(doc.getId()>0){
						for(int j=0; j<oldLista.size(); j++){
							Documento old = oldLista.get(j);
							/*System.out.println("OLD["+old.getRuolo()+"]==ATTUALE["+doc.getRuolo()+"] ("+doc.getRuolo().equals(old.getRuolo())+")");
							System.out.println("OLD["+old.getTipo()+"]==ATTUALE["+doc.getTipo()+"] ("+doc.getTipo().equals(old.getTipo())+")");
							System.out.println("OLD["+old.getFile()+"]==ATTUALE["+doc.getFile()+"] ("+doc.getFile().equals(old.getFile())+")");
							System.out.println("OLD["+old.getIdProprietarioDocumento().longValue()+"]==ATTUALE["+doc.getIdProprietarioDocumento().longValue()+"] ("+(doc.getIdProprietarioDocumento().longValue() == old.getIdProprietarioDocumento().longValue())+")");
							System.out.println("OLD["+old.getTipoProprietarioDocumento()+"]==ATTUALE["+tipologiaProprietarioDocumento+"] ("+tipologiaProprietarioDocumento.equals(old.getTipoProprietarioDocumento())+")");
							if(doc.getRuolo().equals(old.getRuolo()) &&
									doc.getTipo().equals(old.getTipo()) &&
									doc.getFile().equals(old.getFile()) &&
									( (doc.getIdProprietarioDocumento().longValue()) == (old.getIdProprietarioDocumento().longValue()) ) &&
									tipologiaProprietarioDocumento.equals(old.getTipoProprietarioDocumento()) ){*/
							
							//System.out.println("OLD["+old.getId().longValue()+"]==ATTUALE["+doc.getId().longValue()+"] ("+((doc.getId().longValue() == old.getId().longValue()))+")");
							if(doc.getId().longValue() == old.getId().longValue()){		
									documentoGiaPresente = true; // non devo fare una insert, ma una update...
									
									if(doc.getOraRegistrazione()==null)
										throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] OraRegistrazione non definita per documento, precedentemente gia inserito, ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");
											
									// rimuovo la vecchia immagine del documento dalla lista dei doc vecchi
									oldLista.remove(j);
									
									// Il documento e' lo stesso, rimane da verificare che sia un documento successivo di modifica.
									if(doc.getOraRegistrazione().after(old.getOraRegistrazione())){
										documentoDaAggiornare = true;
									}
							}
						}
					}

					byte[] contenuto = null;
					if( (documentoGiaPresente == false) || (documentoDaAggiornare) ){
						// Il contenuto ci deve essere
						if(doc.getByteContenuto()==null || doc.getByteContenuto().length<=0)
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Contenuto non definito per documento ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");

						contenuto = doc.getByteContenuto();
						if(contenuto.length<3){
							String test = new String(contenuto);
							if("".equals(test.trim().replaceAll("\n", ""))){
								// eliminare \n\n
								contenuto = null;	
							}
						}
					}
					
					if(documentoGiaPresente){
						if(documentoDaAggiornare){
							// update
							long idDocumento = doc.getId();
							if(idDocumento<=0){
								throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] ID non definito per documento da aggiorare ["+doc.getFile()+"] ["+doc.getTipo()+"] ["+tipologiaProprietarioDocumento+"] ["+idProprietario+"]");
							}
							ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
							sqlQueryObject.addUpdateTable(CostantiDB.DOCUMENTI);
							sqlQueryObject.addUpdateField("tipo", "?");
							sqlQueryObject.addUpdateField("nome", "?");
							sqlQueryObject.addUpdateField("contenuto", "?");
							sqlQueryObject.addUpdateField("ora_registrazione", "?");
							sqlQueryObject.addWhereCondition("id=?");
							sqlQuery = sqlQueryObject.createSQLUpdate();
							stm = connection.prepareStatement(sqlQuery);
							stm.setString(1, doc.getTipo());
							stm.setString(2, doc.getFile());
							jdbcAdapter.setBinaryData(stm,3,contenuto);
							stm.setTimestamp(4, new Timestamp(doc.getOraRegistrazione().getTime()));
							stm.setLong(5, idDocumento);
							stm.executeUpdate();
							stm.close();
						}
					}else{
						// insert
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.DOCUMENTI);
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQueryObject.addInsertField("tipo", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("id_proprietario", "?");
						sqlQueryObject.addInsertField("tipo_proprietario", "?");
						if(contenuto!=null)
							sqlQueryObject.addInsertField("contenuto", "?");
						if(doc.getOraRegistrazione()!=null)
							sqlQueryObject.addInsertField("ora_registrazione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, doc.getRuolo());
						stm.setString(2, doc.getTipo());
						stm.setString(3, doc.getFile());
						stm.setLong(4, idProprietario);
						stm.setString(5, tipologiaProprietarioDocumento.toString());
						int index = 6;
						if(contenuto!=null){
							jdbcAdapter.setBinaryData(stm,index,contenuto);
							index++;
						}
						if(doc.getOraRegistrazione()!=null){
							stm.setTimestamp(index, new Timestamp(doc.getOraRegistrazione().getTime()));
							index++;
						}
						
						DriverRegistroServiziDB_LIB.log.debug("CRUDDocumento UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario, tipologiaProprietarioDocumento.toString()));
		
						int n = stm.executeUpdate();
						stm.close();
						DriverRegistroServiziDB_LIB.log.debug("Inserted " + n + " row(s)");
			
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("ruolo = ?");
						sqlQueryObject.addWhereCondition("tipo = ?");
						sqlQueryObject.addWhereCondition("nome = ?");
						sqlQueryObject.addWhereCondition("id_proprietario = ?");
						sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, doc.getRuolo());
						stm.setString(2, doc.getTipo());
						stm.setString(3, doc.getFile());
						stm.setLong(4, idProprietario);
						stm.setString(5, tipologiaProprietarioDocumento.toString());
		
						DriverRegistroServiziDB_LIB.log.debug("Recupero idConnettore inserito : \n" + DBUtils.formatSQLString(sqlQuery,doc.getRuolo(), doc.getTipo(), doc.getFile(), idProprietario,tipologiaProprietarioDocumento.toString()));
		
						rs = stm.executeQuery();
		
						if (rs.next()) {
							documenti.get(i).setId(rs.getLong("id"));
						} else {
							throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
						}
		
						rs.close();
						stm.close();
					}
					
				}
				
				if(oldLista.size()>0){
					// Qualche documento e' stato cancellato.
					// Non e' piu' presente.
					for(int j=0; j<oldLista.size(); j++){
						Documento old = oldLista.get(j);
						
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
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
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addWhereCondition("id_proprietario=?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idProprietario);
				stm.setString(2, tipologiaProprietarioDocumento.toString());
				stm.executeUpdate();
				stm.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDDocumento DELETE : \n" + DBUtils.formatSQLString(sqlQuery, idProprietario));

				break;
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDDocumento] Exception : " + se.getMessage(),se);
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
	
	
	public static List<Documento> getListaDocumenti(long idProprietario, ProprietariDocumento tipologiaProprietarioDocumento, boolean readBytes, 
			Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return DriverRegistroServiziDB_documentiLIB.getListaDocumenti(null,idProprietario,tipologiaProprietarioDocumento,readBytes,connection,tipoDatabase);
	}
		
	public static List<Documento> getListaDocumenti(String ruoloDocumenti, long idProprietario, 
			ProprietariDocumento tipologiaProprietarioDocumento, boolean readBytes, Connection connection,
			String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(idProprietario <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] id proprietario non definito");
		
		try {
		
			List<Documento> documenti = new ArrayList<Documento>();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.setANDLogicOperator(true);
			if(ruoloDocumenti!=null)
				sqlQueryObject.addWhereCondition("ruolo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idProprietario);
			stm.setString(2, tipologiaProprietarioDocumento.toString());
			if(ruoloDocumenti!=null)
				stm.setString(3, ruoloDocumenti);
			rs = stm.executeQuery();
			
			while(rs.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(rs.getLong("id"),readBytes, connection,tipoDatabase); 
				documenti.add(doc);
			}
			
			if(documenti.size()<=0)
				throw new DriverRegistroServiziNotFound("Documenti con ruolo["+ruoloDocumenti+"] e idProprietario["+idProprietario+"] non trovati");
			
			return documenti;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getListaDocumenti] Exception : " + se.getMessage(),se);
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
	
	public static Documento getDocumento(long id, boolean readBytes, Connection connection, String tipoDatabase) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(id <= 0 ) 
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] id non definito");
		
		try {
		
			IJDBCAdapter jdbcAdapter = null;
			if(readBytes){
				// jdbcAdapter = org.openspcoop.utils.jdbc.JDBCAdapterFactory.createJDBCAdapter(DriverRegistroServiziDB_LIB.tipoDB);
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(tipoDatabase);
			}
				
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("tipo");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			sqlQueryObject.addSelectField("ora_registrazione");
			sqlQueryObject.addSelectField("id");
			if(readBytes)
				sqlQueryObject.addSelectField("contenuto");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, id);
			rs = stm.executeQuery();
			
			Documento doc = null;
			if(rs.next()){
				doc = new Documento();
				doc.setRuolo(rs.getString("ruolo"));
				doc.setTipo(rs.getString("tipo"));
				doc.setFile(rs.getString("nome"));
				doc.setIdProprietarioDocumento(rs.getLong("id_proprietario"));
				doc.setTipoProprietarioDocumento(rs.getString("tipo_proprietario"));
				doc.setOraRegistrazione(rs.getTimestamp("ora_registrazione"));
				doc.setId(rs.getLong("id"));
				if(readBytes){
					doc.setByteContenuto(jdbcAdapter.getBinaryData(rs,8));
				}
			}
			
			if(doc==null)
				throw new DriverRegistroServiziNotFound("Documento con id["+id+"] non trovato");
			
			return doc;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] SQLException : " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound dnf) {
			throw dnf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::getDocumento] Exception : " + se.getMessage(),se);
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
	
}
