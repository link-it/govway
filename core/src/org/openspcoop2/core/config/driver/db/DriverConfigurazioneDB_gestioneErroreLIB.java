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
package org.openspcoop2.core.config.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_gestioneErroreLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_gestioneErroreLIB {


	
	public static GestioneErrore getGestioneErrore(long idGestioneErrore,Connection con) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		GestioneErrore gestioneErrore = null;
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			
			// Get gestione errore generale
			sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
			sqlQueryObject.addSelectField("comportamento_default");
			sqlQueryObject.addSelectField("cadenza_rispedizione");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("id=?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idGestioneErrore);
			rs = stm.executeQuery();

			if (rs.next()) {
				gestioneErrore = new GestioneErrore();
				gestioneErrore.setId(idGestioneErrore);
				gestioneErrore.setComportamento(GestioneErroreComportamento.toEnumConstant(rs.getString("comportamento_default")));
				gestioneErrore.setCadenzaRispedizione(rs.getString("cadenza_rispedizione"));
				gestioneErrore.setNome(rs.getString("nome"));

				//trasporto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_gestione_errore = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, gestioneErrore.getId());
				rs1 = stm1.executeQuery();
				while(rs1.next())
				{
					GestioneErroreCodiceTrasporto trasporto = new GestioneErroreCodiceTrasporto();
					trasporto.setComportamento(GestioneErroreComportamento.toEnumConstant(rs1.getString("comportamento")));
					trasporto.setCadenzaRispedizione(rs1.getString("cadenza_rispedizione"));
					if(rs1.getLong("valore_massimo")>0){
						String maxVal = ""+rs1.getLong("valore_massimo");
						trasporto.setValoreMassimo(!maxVal.equals("") ? Integer.valueOf(maxVal) : null);
					}
					if(rs1.getLong("valore_minimo")>0){
						String minVal = ""+rs1.getLong("valore_minimo");
						trasporto.setValoreMinimo(!minVal.equals("") ? Integer.valueOf(minVal) : null);
					}
					gestioneErrore.addCodiceTrasporto(trasporto);
				}
				rs1.close();
				stm1.close();

				//soap
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE_SOAP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_gestione_errore = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, gestioneErrore.getId());
				rs1 = stm1.executeQuery();
				while(rs1.next())
				{
					GestioneErroreSoapFault soap = new GestioneErroreSoapFault();
					soap.setComportamento(GestioneErroreComportamento.toEnumConstant(rs1.getString("comportamento")));
					soap.setCadenzaRispedizione(rs1.getString("cadenza_rispedizione"));
					soap.setFaultActor(rs1.getString("fault_actor"));
					soap.setFaultCode(rs1.getString("fault_code"));
					soap.setFaultString(rs1.getString("fault_string"));
					gestioneErrore.addSoapFault(soap);
				}
				rs1.close();
				stm1.close();

			} else {
				throw new DriverConfigurazioneNotFound("Gestione errore con id["+idGestioneErrore+"] non presente");
			}
			rs.close();
			stm.close();

			return gestioneErrore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException(" SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException(" Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	public static long getIdGestioneErrore(String nomeGestioneErrore,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idGestioneErrore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setString(1, nomeGestioneErrore);
			rs=stm.executeQuery();

			if(rs.next()){
				idGestioneErrore = rs.getLong("id");
			}

			return idGestioneErrore;

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

		}
	}
	
	
	
	public static long CRUDGestioneErroreServizioApplicativo(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			long idSoggettoProprietario,long idServizioApplicativo,boolean invocazioneServizio,
			Connection con)throws DriverConfigurazioneException{
		if(invocazioneServizio)
			return CRUDGestioneErrore(type, gestioneErrore, idSoggettoProprietario, idServizioApplicativo,1,con);
		else
			return CRUDGestioneErrore(type, gestioneErrore, idSoggettoProprietario, idServizioApplicativo,2,con);
	}
	public static long CRUDGestioneErroreComponenteCooperazione(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			Connection con) throws DriverConfigurazioneException{
		return CRUDGestioneErrore(type, gestioneErrore, -1,-1,3,con) ;
	}
	public static long CRUDGestioneErroreComponenteIntegrazione(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			Connection con) throws DriverConfigurazioneException{
		return CRUDGestioneErrore(type, gestioneErrore,-1,-1,4,con) ;
	}
	
	private static long CRUDGestioneErrore(int type, org.openspcoop2.core.config.GestioneErrore gestioneErrore, 
			long idSoggettoProprietario,long idServizioApplicativo,
			int tipoCRUD,Connection con) throws DriverConfigurazioneException {

		if (gestioneErrore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Parametro non valido.");
		
		String nomeGestioneErrore = null; // Costruito obbligatoriamente dal driver
		if(tipoCRUD==1 || tipoCRUD==2){
			// crud servizioApplicativo invocazioneServizio(1) o rispostaAsincrona(2)
			if(idSoggettoProprietario<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Soggetto proprietario non fornito.");
			}
			if(idServizioApplicativo<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Servizio applicativo non fornito.");
			}
			nomeGestioneErrore = "Soggetto("+idSoggettoProprietario+")_SA("+idServizioApplicativo+")_";
			if(tipoCRUD==1)
				nomeGestioneErrore = nomeGestioneErrore + "INV";
			else
				nomeGestioneErrore = nomeGestioneErrore + "RISP";
		}else if(tipoCRUD==3){
			// componente cooperazione
			nomeGestioneErrore = "componenteCooperazioneGestioneErroreDefaultPdD";
		}else if(tipoCRUD==4){
			// componente integrazione
			nomeGestioneErrore = "componenteIntegrazioneGestioneErroreDefaultPdD";
		}

		// updateNome
		gestioneErrore.setNome(nomeGestioneErrore);

		// Type
		int tipoOperazione = type;
		// Recupero id gestione errore se presente
		long idGestioneErroreChange = -1;
		if (type == CostantiDB.UPDATE){
			try{
				idGestioneErroreChange = getIdGestioneErrore(nomeGestioneErrore, con);
			}catch (Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			if(idGestioneErroreChange<=0){
				tipoOperazione = CostantiDB.CREATE;
			}
		}
		
		
		PreparedStatement updateStmt = null;
		PreparedStatement selectStmt = null;
		String updateQuery = "";
		String selectQuery = "";
		ResultSet selectRS = null;
		int n = 0;
		try {

			// preparo lo statement in base al tipo di operazione
			switch (tipoOperazione) {
			case CREATE:
				// CREATE
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addInsertField("comportamento_default", "?");
				sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
				sqlQueryObject.addInsertField("nome", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setString(1, DriverConfigurazioneDB_LIB.getValue(gestioneErrore.getComportamento()));
				updateStmt.setString(2, gestioneErrore.getCadenzaRispedizione());
				updateStmt.setString(3, gestioneErrore.getNome());
				// eseguo lo statement
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
						gestioneErrore.getComportamento(),gestioneErrore.getCadenzaRispedizione(),gestioneErrore.getNome()));
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore type = " + type + " row affected =" + n);

				// Recupero id
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("nome = ?");
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setString(1, gestioneErrore.getNome());
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {
					gestioneErrore.setId(selectRS.getLong("id"));
				}else{
					throw new Exception("id gestione errore non trovato dopo inserimento con nome ["+gestioneErrore.getNome()+"]");
				}
				selectRS.close();
				selectStmt.close();
				
				// Insert gestione errore trasporto
				for(int i=0; i<gestioneErrore.sizeCodiceTrasportoList(); i++){
					GestioneErroreCodiceTrasporto tr = gestioneErrore.getCodiceTrasporto(i);
					
					int valoreMassimo = -1;
					int valoreMinimo = -1;
					if(tr.getValoreMassimo()!=null){
						valoreMassimo = tr.getValoreMassimo().intValue();
					}
					if(tr.getValoreMinimo()!=null){
						valoreMinimo = tr.getValoreMinimo().intValue();
					}
					
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					if(valoreMassimo>=0)
						sqlQueryObject.addInsertField("valore_massimo", "?");
					if(valoreMinimo>=0)
						sqlQueryObject.addInsertField("valore_minimo", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					int index = 1;
					updateStmt.setLong(index, gestioneErrore.getId()); index++;
					if(valoreMassimo>=0){
						updateStmt.setInt(index, valoreMassimo); index++;
					}
					if(valoreMinimo>=0){
						updateStmt.setInt(index, valoreMinimo); index++;
					}
					updateStmt.setString(index, DriverConfigurazioneDB_LIB.getValue(tr.getComportamento())); index++;
					updateStmt.setString(index, tr.getCadenzaRispedizione()); index++;

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					if(valoreMassimo>=0 && valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMassimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else{
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),tr.getComportamento(),tr.getCadenzaRispedizione()));
					}
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") type = " + type + " row affected =" + n);
				}
				
				// Insert gestione errore SOAP FAULT
				for(int i=0; i<gestioneErrore.sizeSoapFaultList(); i++){
					GestioneErroreSoapFault sf = gestioneErrore.getSoapFault(i);
										
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					sqlQueryObject.addInsertField("fault_actor", "?");
					sqlQueryObject.addInsertField("fault_code", "?");
					sqlQueryObject.addInsertField("fault_string", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, gestioneErrore.getId()); 
					updateStmt.setString(2, sf.getFaultActor());
					updateStmt.setString(3,sf.getFaultCode());
					updateStmt.setString(4,sf.getFaultString());
					updateStmt.setString(5,DriverConfigurazioneDB_LIB.getValue(sf.getComportamento()));
					updateStmt.setString(6, sf.getCadenzaRispedizione());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),sf.getFaultActor(),sf.getFaultCode(),sf.getFaultString(),sf.getComportamento(),sf.getCadenzaRispedizione()));
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") type = " + type + " row affected =" + n);
				}
				
				break;

			case UPDATE:
			
				// UPDATE (ci entro solo se prima ho trovato un gestore errore inserito (idGestioneErroreChange) )
					
				// Set idGestionErrore
				gestioneErrore.setId(idGestioneErroreChange);
				
				// Update gestion errore
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.GESTIONE_ERRORE);
				sqlQueryObject.addUpdateField("comportamento_default", "?");
				sqlQueryObject.addUpdateField("cadenza_rispedizione", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				
				updateStmt.setString(1, DriverConfigurazioneDB_LIB.getValue(gestioneErrore.getComportamento()));
				updateStmt.setString(2, gestioneErrore.getCadenzaRispedizione());
				updateStmt.setLong(3, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
			
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						gestioneErrore.getComportamento(),gestioneErrore.getCadenzaRispedizione(),idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore type = " + type + " row affected =" + n);
				
				// Delete vecchie gestione errore trasporto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
				sqlQueryObject.addWhereCondition("id_gestione_errore=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete old trasporto) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete old trasporto) type = " + type + " row affected =" + n);
				
				// Delete vecchie gestione errore soap fault
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_SOAP);
				sqlQueryObject.addWhereCondition("id_gestione_errore=?");
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idGestioneErroreChange);
				n = updateStmt.executeUpdate();
				updateStmt.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete old soap fault) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
						idGestioneErroreChange));
				DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete old soap fault) type = " + type + " row affected =" + n);
				
				//  Insert gestione errore trasporto
				for(int i=0; i<gestioneErrore.sizeCodiceTrasportoList(); i++){
					GestioneErroreCodiceTrasporto tr = gestioneErrore.getCodiceTrasporto(i);
					
					int valoreMassimo = -1;
					int valoreMinimo = -1;
					if(tr.getValoreMassimo()!=null){
						valoreMassimo = tr.getValoreMassimo().intValue();
					}
					if(tr.getValoreMinimo()!=null){
						valoreMinimo = tr.getValoreMinimo().intValue();
					}
					
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					if(valoreMassimo>=0)
						sqlQueryObject.addInsertField("valore_massimo", "?");
					if(valoreMinimo>=0)
						sqlQueryObject.addInsertField("valore_minimo", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					int index = 1;
					updateStmt.setLong(index, gestioneErrore.getId()); index++;
					if(valoreMassimo>=0){
						updateStmt.setInt(index, valoreMassimo); index++;
					}
					if(valoreMinimo>=0){
						updateStmt.setInt(index, valoreMinimo); index++;
					}
					updateStmt.setString(index, DriverConfigurazioneDB_LIB.getValue(tr.getComportamento())); index++;
					updateStmt.setString(index, tr.getCadenzaRispedizione()); index++;

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					if(valoreMassimo>=0 && valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMassimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMassimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else if(valoreMinimo>=0){
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),valoreMinimo,tr.getComportamento(),tr.getCadenzaRispedizione()));
					}else{
						DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),tr.getComportamento(),tr.getCadenzaRispedizione()));
					}
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_Trasporto("+i+") type = " + type + " row affected =" + n);
				}
				
				// Insert gestione errore SOAP FAULT
				for(int i=0; i<gestioneErrore.sizeSoapFaultList(); i++){
					GestioneErroreSoapFault sf = gestioneErrore.getSoapFault(i);
										
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addInsertField("id_gestione_errore", "?");
					sqlQueryObject.addInsertField("fault_actor", "?");
					sqlQueryObject.addInsertField("fault_code", "?");
					sqlQueryObject.addInsertField("fault_string", "?");
					sqlQueryObject.addInsertField("comportamento", "?");
					sqlQueryObject.addInsertField("cadenza_rispedizione", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);

					updateStmt.setLong(1, gestioneErrore.getId()); 
					updateStmt.setString(2, sf.getFaultActor());
					updateStmt.setString(3,sf.getFaultCode());
					updateStmt.setString(4,sf.getFaultString());
					updateStmt.setString(5,DriverConfigurazioneDB_LIB.getValue(sf.getComportamento()));
					updateStmt.setString(6, sf.getCadenzaRispedizione());

					// eseguo lo statement
					n = updateStmt.executeUpdate();
					updateStmt.close();
					
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") CREATE : \n" + DBUtils.formatSQLString(updateQuery, 
								gestioneErrore.getId(),sf.getFaultActor(),sf.getFaultCode(),sf.getFaultString(),sf.getComportamento(),sf.getCadenzaRispedizione()));
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore_SoapFault("+i+") type = " + type + " row affected =" + n);
				}
				
				break;

			case DELETE:
				// DELETE
				
				long idGestioneErrore = getIdGestioneErrore(nomeGestioneErrore, con);
				
				gestioneErrore.setId(idGestioneErrore);
				
				if(idGestioneErrore>0){
					
					// Delete gestione errore trasporto
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
					sqlQueryObject.addWhereCondition("id_gestione_errore=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete trasporto) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete trasporto) type = " + type + " row affected =" + n);
					
					// Delete gestione errore soap fault
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_SOAP);
					sqlQueryObject.addWhereCondition("id_gestione_errore=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore (Delete soap fault) UPDATE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					DriverConfigurazioneDB_LIB.log.debug("CRUGestioneErrore (Delete soap fault) type = " + type + " row affected =" + n);

					// Delete gestione errore
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE);
					sqlQueryObject.addWhereCondition("id=?");
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt = con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idGestioneErrore);
					n = updateStmt.executeUpdate();
					updateStmt.close();
	
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore type = " + type + " row affected =" + n);
					DriverConfigurazioneDB_LIB.log.debug("CRUDGestioneErrore DELETE : \n" + DBUtils.formatSQLString(updateQuery, 
							idGestioneErrore));
					
				}
				break;
			}


			return n;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] SQLException [" + se.getMessage() + "].",se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDGestioneErrore] Exception [" + se.getMessage() + "].",se);
		} finally {			
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}

	
}
