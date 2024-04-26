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



package org.openspcoop2.core.registry.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
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
public class DriverRegistroServiziDB_accordiSoapLIB {
	
	

	public static long CRUDAzione(int type, AccordoServizioParteComune as,Azione azione, Connection con, long idAccordo, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idAccordo <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzione] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("profilo_azione", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("id_riferimento_richiesta", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("correlata", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, azione.getNome());
				updateStmt.setString(index++, azione.getProfAzione());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo azione ["+azione.getNome()+"] con profilo ["+azione.getProfAzione()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(index++, azione.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
				}else{
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(index++, as.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++, azione.getCorrelata());

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione CREATE :\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,idAccordo,azione.getNome(),azione.getProfAzione()));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idAccordo);
				selectStmt.setString(2, azione.getNome());

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addUpdateField("profilo_azione", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("id_riferimento_richiesta", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("correlata", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(1, azione.getProfAzione());

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(index++, azione.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
				}else{
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(index++, as.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}

				updateStmt.setString(index++, azione.getCorrelata());

				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						azione.getId(), ProprietariProtocolProperty.AZIONE_ACCORDO, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(sqlQuery);
				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, azione.getNome());
				n=updateStmt.executeUpdate();
				updateStmt.close();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if (CostantiDB.CREATE == type) {
				if(selectStmt!=null) {
					selectRS = selectStmt.executeQuery();
					if (selectRS.next()) {
	
						azione.setId(selectRS.getLong("id"));
						
						// ProtocolProperties
						DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, azione.getProtocolPropertyList(), 
								azione.getId(), ProprietariProtocolProperty.AZIONE_ACCORDO, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
						
						return azione.getId();
	
					}
				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzione] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzione] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}

	public static long CRUDPortType(int type, AccordoServizioParteComune as,PortType pt, Connection con, long idAccordo, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		long n = 0;
		if (idAccordo <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortType] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("message_type", "?");
				sqlQueryObject.addInsertField("profilo_pt", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("id_riferimento_richiesta", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("soap_style", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, pt.getNome());
				updateStmt.setString(index++, pt.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getMessageType()));
				updateStmt.setString(index++, pt.getProfiloPT());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo port-type ["+pt.getNome()+"] con profilo ["+pt.getProfiloPT()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(index++, pt.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					if(pt.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++,DriverRegistroServiziDB_LIB.getValue(pt.getStyle()));
				// log.debug("CRUDAzione CREATE :
				// \n"+formatSQLString(updateQuery,idAccordo,idSoggettoFruitore,idConnettore,wsdlImplementativoErogatore,wsdlImplementativoFruitore));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("message_type", "?");
				sqlQueryObject.addUpdateField("profilo_pt", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("id_riferimento_richiesta", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("soap_style", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(index++, pt.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getMessageType()));
				updateStmt.setString(index++, pt.getProfiloPT());


				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					updateStmt.setString(index++, pt.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
				}else{
					if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					if(pt.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());
					if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++,DriverRegistroServiziDB_LIB.getValue(pt.getStyle()));
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, pt.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				Long idPT = 0l;
				if(pt.getId()==null || pt.getId()<=0){
					idPT = DBUtils.getIdPortType(idAccordo, pt.getNome(), con);
					if(idPT==null || idPT<=0)
						throw new Exception("ID del porttype["+pt.getNome()+"] idAccordo["+idAccordo+"] non trovato");
				}
				else {
					idPT = pt.getId();
				}

				// Operations
				for(int i=0;i<pt.sizeAzioneList();i++){
					DriverRegistroServiziDB_accordiSoapLIB.CRUDAzionePortType(CostantiDB.DELETE, as, pt, pt.getAzione(i), con, idPT, driverBYOK);
				}

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						idPT, ProprietariProtocolProperty.PORT_TYPE, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, idAccordo);
				updateStmt.setString(2, pt.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if ( (CostantiDB.CREATE == type) || (CostantiDB.UPDATE == type)) {
				Long idPT = DBUtils.getIdPortType(idAccordo, pt.getNome(), con);
				if(idPT==null || idPT<=0)
					throw new Exception("ID del porttype["+pt.getNome()+"] idAccordo["+idAccordo+"] non trovato");

				DriverRegistroServiziDB_LIB.log.debug("ID port type: "+idPT);

				if ( CostantiDB.UPDATE == type ){
					n = 0;
					for(int i=0;i<pt.sizeAzioneList();i++){
						n += DriverRegistroServiziDB_accordiSoapLIB.CRUDAzionePortType(CostantiDB.DELETE, as, pt, pt.getAzione(i), con, idPT, driverBYOK);
					}
					
//					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
//					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
//					sqlQueryObject.addWhereCondition("id_port_type=?");
//					sqlQueryObject.setANDLogicOperator(true);
//					String sqlQuery = sqlQueryObject.createSQLDelete();
//					updateStmt=con.prepareStatement(sqlQuery);
//					updateStmt.setLong(1, idPT);
//					n=updateStmt.executeUpdate();
//					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.info("Cancellate "+n+" azioni del port type ["+idPT+"] associate all'accordo "+idAccordo);
				}

				// Azioni PortType
				Operation azione = null;
				for (int i = 0; i < pt.sizeAzioneList(); i++) {
					azione = pt.getAzione(i);
					DriverRegistroServiziDB_accordiSoapLIB.CRUDAzionePortType(CostantiDB.CREATE,as,pt, azione, con, idPT, driverBYOK);
				}
				DriverRegistroServiziDB_LIB.log.debug("inserite " + pt.sizeAzioneList() + " azioni relative al port type["+pt.getNome()+"] id-porttype["+pt.getId()+"] dell'accordo :" + IDAccordoFactory.getInstance().getUriFromAccordo(as) + " id-accordo :" + idAccordo);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, pt.getProtocolPropertyList(), 
						idPT, ProprietariProtocolProperty.PORT_TYPE, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
			}


			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDPortType] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}

	public static long CRUDAzionePortType(int type, AccordoServizioParteComune as,PortType pt,Operation azione, Connection con, long idPortType, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idPortType <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] ID Port Type non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("profilo_pt_azione", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("id_riferimento_richiesta", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
				sqlQueryObject.addInsertField("soap_style", "?");
				sqlQueryObject.addInsertField("soap_action", "?");
				sqlQueryObject.addInsertField("soap_use_msg_input", "?");
				sqlQueryObject.addInsertField("soap_namespace_msg_input", "?");
				sqlQueryObject.addInsertField("soap_use_msg_output", "?");
				sqlQueryObject.addInsertField("soap_namespace_msg_output", "?");
				sqlQueryObject.addInsertField("correlata_servizio", "?");
				sqlQueryObject.addInsertField("correlata", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idPortType);
				updateStmt.setString(index++, azione.getNome());
				updateStmt.setString(index++, azione.getProfAzione());

				DriverRegistroServiziDB_LIB.log.debug("Aggiungo azione ["+azione.getNome()+"] pt ["+pt.getNome()+"] con profilo ["+azione.getProfAzione()+"]");

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(index++, azione.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
				}else{
					if(azione.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					else if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));

					if(azione.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					else if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));

					if(azione.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					else if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					
					if(azione.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					else if(pt.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));

					if(azione.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					else if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));

					if(azione.getScadenza()!=null)
						updateStmt.setString(index++, azione.getScadenza());
					else if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());

					if(azione.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
					else if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getStyle()));
				updateStmt.setString(index++, azione.getSoapAction());
				if(azione.getMessageInput()!=null){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getMessageInput().getUse()));
					updateStmt.setString(index++, azione.getMessageInput().getSoapNamespace());
				}else{
					updateStmt.setString(index++, null);
					updateStmt.setString(index++, null);
				}
				if(azione.getMessageOutput()!=null){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getMessageOutput().getUse()));
					updateStmt.setString(index++, azione.getMessageOutput().getSoapNamespace());
				}else{
					updateStmt.setString(index++, null);
					updateStmt.setString(index++, null);
				}

				updateStmt.setString(index++, azione.getCorrelataServizio());
				updateStmt.setString(index++, azione.getCorrelata());

				DriverRegistroServiziDB_LIB.log.debug("CRUDPortTypeAzione CREATE :\n"+updateQuery);
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.setANDLogicOperator(true);
				selectQuery = sqlQueryObject.createSQLQuery();
				selectStmt = con.prepareStatement(selectQuery);
				selectStmt.setLong(1, idPortType);
				selectStmt.setString(2, azione.getNome());

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addUpdateField("profilo_azione", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("id_riferimento_richiesta", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
				sqlQueryObject.addUpdateField("soap_style", "?");
				sqlQueryObject.addUpdateField("soap_action", "?");
				sqlQueryObject.addUpdateField("soap_use_msg_input", "?");
				sqlQueryObject.addUpdateField("soap_namespace_msg_input", "?");
				sqlQueryObject.addUpdateField("soap_use_msg_output", "?");
				sqlQueryObject.addUpdateField("soap_namespace_msg_output", "?");
				sqlQueryObject.addUpdateField("correlata_servizio", "?");
				sqlQueryObject.addUpdateField("correlata", "?");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(1, azione.getProfAzione());

				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					updateStmt.setString(index++, azione.getScadenza());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
				}else{
					if(azione.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
					else if(pt.getFiltroDuplicati()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getFiltroDuplicati()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));

					if(azione.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
					else if(pt.getConfermaRicezione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConfermaRicezione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));

					if(azione.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
					else if(pt.getIdCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					
					if(azione.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getIdRiferimentoRichiesta()));
					else if(pt.getIdRiferimentoRichiesta()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getIdRiferimentoRichiesta()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));

					if(azione.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
					else if(pt.getConsegnaInOrdine()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getConsegnaInOrdine()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));

					if(azione.getScadenza()!=null)
						updateStmt.setString(index++, azione.getScadenza());
					else if(pt.getScadenza()!=null)
						updateStmt.setString(index++, pt.getScadenza());
					else
						updateStmt.setString(index++, as.getScadenza());

					if(azione.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
					else if(pt.getProfiloCollaborazione()!=null)
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(pt.getProfiloCollaborazione()));
					else
						updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getStyle()));
				updateStmt.setString(index++, azione.getSoapAction());
				if(azione.getMessageInput()!=null){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getMessageInput().getUse()));
					updateStmt.setString(index++, azione.getMessageInput().getSoapNamespace());
				}else{
					updateStmt.setString(index++, null);
					updateStmt.setString(index++, null);
				}
				if(azione.getMessageOutput()!=null){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(azione.getMessageOutput().getUse()));
					updateStmt.setString(index++, azione.getMessageOutput().getSoapNamespace());
				}else{
					updateStmt.setString(index++, null);
					updateStmt.setString(index++, null);
				}

				updateStmt.setString(index++, azione.getCorrelataServizio());
				updateStmt.setString(index++, azione.getCorrelata());

				updateStmt.setLong(index++, idPortType);
				updateStmt.setString(index++, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				// message-part
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addWhereCondition("id_port_type_azione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione DELETE MESSAGES:\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,azione.getId()));
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, azione.getId());
				n = updateStmt.executeUpdate();
				updateStmt.close();

				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(CostantiDB.DELETE, null, 
						azione.getId(), ProprietariProtocolProperty.OPERATION, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
				
				// azioni
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				DriverRegistroServiziDB_LIB.log.debug("CRUDAzione DELETE AZIONI:\n"+DriverRegistroServiziDB_LIB.formatSQLString(updateQuery,idPortType,azione.getNome()));
				updateStmt = con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idPortType);
				updateStmt.setString(2, azione.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDAzionePortType type = " + type + " row affected =" + n);

				break;
			}



			if (CostantiDB.CREATE == type) {
				selectRS = selectStmt.executeQuery();
				if (selectRS.next()) {

					azione.setId(selectRS.getLong("id"));
					azione.setIdPortType(idPortType);

					// creo message-part
					DriverRegistroServiziDB_accordiSoapLIB.CRUDMessageAzionePortType(CostantiDB.CREATE, azione, con);

					// ProtocolProperties
					DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, azione.getProtocolPropertyList(), 
							azione.getId(), ProprietariProtocolProperty.OPERATION, con, DriverRegistroServiziDB_LIB.tipoDB, driverBYOK);
					
					return azione.getId();

				}
			}

			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDAzionePortType] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}


	public static void CRUDMessageAzionePortType(int type, Operation azione, Connection con) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		if (azione.getId() <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] ID Operation Port Type non valida.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addInsertField("id_port_type_azione", "?");
				sqlQueryObject.addInsertField("input_message", "?");
				sqlQueryObject.addInsertField("name", "?");
				sqlQueryObject.addInsertField("element_name", "?");
				sqlQueryObject.addInsertField("element_namespace", "?");
				sqlQueryObject.addInsertField("type_name", "?");
				sqlQueryObject.addInsertField("type_namespace", "?");
				updateQuery = sqlQueryObject.createSQLInsert();


				// message input
				if(azione.getMessageInput()!=null){
					for(int i=0; i<azione.getMessageInput().sizePartList(); i++){
						updateStmt = con.prepareStatement(updateQuery);
						MessagePart part = azione.getMessageInput().getPart(i);

						updateStmt.setLong(1, azione.getId());
						updateStmt.setInt(2, 1);
						updateStmt.setString(3, part.getName());
						updateStmt.setString(4, part.getElementName());
						updateStmt.setString(5, part.getElementNamespace());
						updateStmt.setString(6, part.getTypeName());
						updateStmt.setString(7, part.getTypeNamespace());

						DriverRegistroServiziDB_LIB.log.debug("Aggiungo part element input  ["+part.getName()+"] per azione ["+azione.getNome()+"]");

						DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType CREATE :\n"+updateQuery);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				// message output
				if(azione.getMessageOutput()!=null){
					for(int i=0; i<azione.getMessageOutput().sizePartList(); i++){
						updateStmt = con.prepareStatement(updateQuery);
						MessagePart part = azione.getMessageOutput().getPart(i);

						updateStmt.setLong(1, azione.getId());
						updateStmt.setInt(2, 0);
						updateStmt.setString(3, part.getName());
						updateStmt.setString(4, part.getElementName());
						updateStmt.setString(5, part.getElementNamespace());
						updateStmt.setString(6, part.getTypeName());
						updateStmt.setString(7, part.getTypeNamespace());

						DriverRegistroServiziDB_LIB.log.debug("Aggiungo part element output ["+part.getName()+"] per azione ["+azione.getNome()+"]");

						DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType CREATE :\n"+updateQuery);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
				}

				break;

			case UPDATE:
				// update
				//
				throw new Exception("Non implementato");

			case DELETE:
				// delete

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addWhereCondition("id_port_type_azione=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt = con.prepareStatement(updateQuery);

				updateStmt.setLong(1, azione.getId());
				long n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDMessageAzionePortType type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}


		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDMessageAzionePortType] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
			JDBCUtilities.closeResources(updateStmt);
		}
	}

	
}
