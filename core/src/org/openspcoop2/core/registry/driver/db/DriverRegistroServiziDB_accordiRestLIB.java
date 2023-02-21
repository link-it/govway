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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
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
public class DriverRegistroServiziDB_accordiRestLIB {
	
	
	public static int CRUDResource(int type, AccordoServizioParteComune as,Resource resource, Connection con, long idAccordo) throws DriverRegistroServiziException {
		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		int n = 0;
		if (idAccordo <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDResource] ID Accordo non valido.");

		try {
			switch (type) {
			case CREATE:
				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("http_method", "?");
				sqlQueryObject.addInsertField("path", "?");
				sqlQueryObject.addInsertField("message_type", "?");
				sqlQueryObject.addInsertField("message_type_request", "?");
				sqlQueryObject.addInsertField("message_type_response", "?");
				sqlQueryObject.addInsertField("profilo_azione", "?");
				sqlQueryObject.addInsertField("filtro_duplicati", "?");
				sqlQueryObject.addInsertField("conferma_ricezione", "?");
				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
				sqlQueryObject.addInsertField("id_riferimento_richiesta", "?");
				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
				sqlQueryObject.addInsertField("scadenza", "?");
				updateQuery = sqlQueryObject.createSQLInsert();
				updateStmt = con.prepareStatement(updateQuery);
				int index = 1;
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, resource.getNome());
				updateStmt.setString(index++, resource.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMethod()));
				if(resource.getPath()==null) {
					updateStmt.setString(index++, CostantiDB.API_RESOURCE_PATH_ALL_VALUE);
				}
				else {
					updateStmt.setString(index++, resource.getPath());
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getRequestMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getResponseMessageType()));
				
				DriverRegistroServiziDB_LIB.log.debug("Aggiungo resource ["+resource.getNome()+"] con profilo ["+resource.getProfAzione()+"]");

				updateStmt.setString(index++, resource.getProfAzione());
				
				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(resource.getProfAzione())){
					DriverRegistroServiziDB_LIB.log.debug("ridefinizione...");
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getConsegnaInOrdine()));
					updateStmt.setString(index++, resource.getScadenza());
				}else{
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(index++, as.getScadenza());
				}

				// log.debug("CRUDAzione CREATE :
				// \n"+formatSQLString(updateQuery,idAccordo,idSoggettoFruitore,idConnettore,wsdlImplementativoErogatore,wsdlImplementativoFruitore));
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);

				break;

			case UPDATE:
				// update
				//
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("http_method", "?");
				sqlQueryObject.addUpdateField("path", "?");
				sqlQueryObject.addUpdateField("message_type", "?");
				sqlQueryObject.addUpdateField("message_type_request", "?");
				sqlQueryObject.addUpdateField("message_type_response", "?");
				sqlQueryObject.addUpdateField("profilo_azione", "?");
				sqlQueryObject.addUpdateField("filtro_duplicati", "?");
				sqlQueryObject.addUpdateField("conferma_ricezione", "?");
				sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
				sqlQueryObject.addUpdateField("id_riferimento_richiesta", "?");
				sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
				sqlQueryObject.addUpdateField("scadenza", "?");
				sqlQueryObject.addWhereCondition("id_accordo=?");
				sqlQueryObject.addWhereCondition("nome=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLUpdate();
				updateStmt = con.prepareStatement(updateQuery);
				index = 1;
				updateStmt.setString(index++, resource.getDescrizione());
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMethod()));
				if(resource.getPath()==null) {
					updateStmt.setString(index++, CostantiDB.API_RESOURCE_PATH_ALL_VALUE);
				}
				else {
					updateStmt.setString(index++, resource.getPath());
				}
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getRequestMessageType()));
				updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getResponseMessageType()));
				
				updateStmt.setString(index++, resource.getProfAzione());
				
				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(resource.getProfAzione())){
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(resource.getConsegnaInOrdine()));
					updateStmt.setString(index++, resource.getScadenza());
				}else{
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getIdRiferimentoRichiesta()));
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
					updateStmt.setString(index++, as.getScadenza());
				}
				
				updateStmt.setLong(index++, idAccordo);
				updateStmt.setString(index++, resource.getNome());
				n = updateStmt.executeUpdate();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione UPDATE :
				// \n"+formatSQLString(updateQuery,wsdlImplementativoErogatore,wsdlImplementativoFruitore,
				// idServizio,idSoggettoFruitore,idConnettore));

				break;

			case DELETE:
				// delete

				Long idResource = 0l;
				if(resource.getId()==null || resource.getId()<=0){
					idResource = DBUtils.getIdResource(idAccordo, resource.getNome(), con);
					if(idResource==null || idResource<=0)
						throw new Exception("ID della risorsa ["+resource.getNome()+"] idAccordo["+idAccordo+"] non trovato");
				}
				else {
					idResource = resource.getId();
				}
				
				// gestione request
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addWhereCondition("id_resource_media=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource );
				updateStmt.executeUpdate();
				updateStmt.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addWhereCondition("id_resource_parameter=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// gestione response
				List<Long> idResourceResponse = new ArrayList<Long>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_resource=?");
				updateQuery = sqlQueryObject.createSQLQuery();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				selectRS=updateStmt.executeQuery();
				while(selectRS.next()){
					idResourceResponse.add(selectRS.getLong("id"));
				}
				selectRS.close();
				updateStmt.close();
	
				while(idResourceResponse.size()>0){
					
					long idRR = idResourceResponse.remove(0);
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
					sqlQueryObject.addWhereCondition("id_resource_response_media=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idRR );
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
					sqlQueryObject.addWhereCondition("id_resource_response_par=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idRR);
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addWhereCondition("id_resource=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				updateStmt.executeUpdate();
				updateStmt.close();
				
				// elimino risorsa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				updateQuery = sqlQueryObject.createSQLDelete();
				updateStmt=con.prepareStatement(updateQuery);
				updateStmt.setLong(1, idResource);
				n = updateStmt.executeUpdate();
				updateStmt.close();

				DriverRegistroServiziDB_LIB.log.debug("CRUDResource type = " + type + " row affected =" + n);
				// log.debug("CRUDAzione DELETE :
				// \n"+formatSQLString(updateQuery,idServizio,idSoggettoFruitore,idConnettore));

				break;
			}



			if ( (CostantiDB.CREATE == type) || (CostantiDB.UPDATE == type)) {
				Long idResource = DBUtils.getIdResource(idAccordo, resource.getNome(), con);
				if(idResource==null || idResource<=0)
					throw new Exception("ID della risorsa ["+resource.getNome()+"] idAccordo["+idAccordo+"] non trovato");

				DriverRegistroServiziDB_LIB.log.debug("ID risorsa: "+idResource);

				if ( CostantiDB.UPDATE == type ){
					
					if(resource.getRequest()!=null) {
						DriverRegistroServiziDB_accordiRestLIB.CRUDResourceRequest(CostantiDB.DELETE, as, resource, resource.getRequest(), con, idResource);
						DriverRegistroServiziDB_LIB.log.info("Cancellato dettagli di richiesta della risorsa ["+idResource+"] associata all'accordo "+idAccordo);
					}
					
					n = 0;
					for(int i=0;i<resource.sizeResponseList();i++){
						DriverRegistroServiziDB_accordiRestLIB.CRUDResourceResponse(CostantiDB.DELETE, as, resource, resource.getResponse(i), con, idResource);
					}
					DriverRegistroServiziDB_LIB.log.info("Cancellate "+n+" dettagli di risposta della risorsa ["+idResource+"] associata all'accordo "+idAccordo);
				}

				if(resource.getRequest()!=null) {
					DriverRegistroServiziDB_accordiRestLIB.CRUDResourceRequest(CostantiDB.CREATE, as, resource, resource.getRequest(), con, idResource);
				}
				
				for(int i=0;i<resource.sizeResponseList();i++){
					DriverRegistroServiziDB_accordiRestLIB.CRUDResourceResponse(CostantiDB.CREATE, as, resource, resource.getResponse(i), con, idResource);
				}					
				DriverRegistroServiziDB_LIB.log.debug("inserite " + resource.sizeResponseList() + " dettagli di risposta relative alla risorsa ["+resource.getNome()+"] id-risorsa["+resource.getId()+"] dell'accordo :" + IDAccordoFactory.getInstance().getUriFromAccordo(as) + " id-accordo :" + idAccordo);
				
				
				// ProtocolProperties
				DriverRegistroServiziDB_LIB.CRUDProtocolProperty(type, resource.getProtocolPropertyList(), 
						idResource, ProprietariProtocolProperty.RESOURCE, con, DriverRegistroServiziDB_LIB.tipoDB);
			}


			return n;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDResource] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDResource] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
				if(selectStmt!=null)selectStmt.close();
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	
	public static void CRUDResourceRequest(int type, AccordoServizioParteComune as,Resource resource,ResourceRequest resourceRequest, Connection con, long idResource) throws DriverRegistroServiziException {
		_CRUDResourceRequestResponse(type, as, resource, resourceRequest, null, con, idResource);
	}
	public static void CRUDResourceResponse(int type, AccordoServizioParteComune as,Resource resource,ResourceResponse resourceResponse, Connection con, long idResource) throws DriverRegistroServiziException {
		_CRUDResourceRequestResponse(type, as, resource, null, resourceResponse, con, idResource);
	}
	
	private static void _CRUDResourceRequestResponse(int type, AccordoServizioParteComune as,Resource resource,
			ResourceRequest resourceRequest,ResourceResponse resourceResponse, Connection con, long idResource) throws DriverRegistroServiziException {

		PreparedStatement updateStmt = null;
		String updateQuery;
		PreparedStatement selectStmt = null;
		String selectQuery = "";
		ResultSet selectRS = null;
		long n = 0;
		if (idResource <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] ID Risorda non valido.");

		try {
			switch (type) {
			case CREATE:
				
				long idFK = -1;
				if(resourceRequest!=null) {
					idFK = idResource;
				}
				else {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addInsertField("id_resource", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("status", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idResource);
					updateStmt.setString(index++, resourceResponse.getDescrizione());
					updateStmt.setInt(index++, resourceResponse.getStatus());
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (RESPONSE) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					updateStmt.close();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (RESPONSE) type = " + type + " row affected =" + n);
					
					index = 1;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_resource = ?");
					sqlQueryObject.addWhereCondition("status = ?");
					sqlQueryObject.setANDLogicOperator(true);
					selectQuery = sqlQueryObject.createSQLQuery();
					selectStmt = con.prepareStatement(selectQuery);
					selectStmt.setLong(index++, idResource);
					selectStmt.setInt(index++, resourceResponse.getStatus());
					selectRS = selectStmt.executeQuery();
					if (selectRS.next()) {
						idFK = selectRS.getLong("id");
					}
					else {
						throw new Exception("Recupero dell'id della tabella '"+CostantiDB.API_RESOURCES_RESPONSE+"' con id_resource='"+idResource+"' e status='"+resourceResponse.getStatus()+"' non riuscito");
					}
				}
				if(idFK<=0) {
					throw new Exception("Recupero dell'id della tabella padre non riuscito");
				}
				if(resourceRequest!=null) {
					resourceRequest.setIdResource(idFK);
				}
				else {
					resourceResponse.setIdResource(idFK);
				}
				
				List<ResourceRepresentation> lRR = null;
				if(resourceRequest!=null) {
					lRR = resourceRequest.getRepresentationList();
				}
				else {
					lRR = resourceResponse.getRepresentationList();
				}
				for (ResourceRepresentation rr : lRR) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_MEDIA);
					if(resourceRequest!=null) {
						sqlQueryObject.addInsertField("id_resource_media", "?");
					}
					else {
						sqlQueryObject.addInsertField("id_resource_response_media", "?");
					}
					sqlQueryObject.addInsertField("media_type", "?");
					sqlQueryObject.addInsertField("message_type", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("xml_tipo", "?");
					sqlQueryObject.addInsertField("xml_name", "?");
					sqlQueryObject.addInsertField("xml_namespace", "?");
					sqlQueryObject.addInsertField("json_type", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idFK);
					updateStmt.setString(index++, rr.getMediaType());
					updateStmt.setString(index++,  DriverRegistroServiziDB_LIB.getValue(rr.getMessageType()));
					updateStmt.setString(index++, rr.getNome());
					updateStmt.setString(index++, rr.getDescrizione());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(rr.getRepresentationType()));
					updateStmt.setString(index++, rr.getXml()!=null ? DriverRegistroServiziDB_LIB.getValue(rr.getXml().getXmlType()) : null);
					updateStmt.setString(index++, rr.getXml()!=null ? rr.getXml().getNome() : null);
					updateStmt.setString(index++, rr.getXml()!=null ? rr.getXml().getNamespace() : null);
					updateStmt.setString(index++, rr.getJson()!=null ? rr.getJson().getTipo() : null);					
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (MEDIA) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (MEDIA) type = " + type + " row affected =" + n);
					updateStmt.close();
					
				}
				
				List<ResourceParameter> lRP = null;
				if(resourceRequest!=null) {
					lRP = resourceRequest.getParameterList();
				}
				else {
					lRP = resourceResponse.getParameterList();
				}
				for (ResourceParameter rp : lRP) {
					
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.API_RESOURCES_PARAMETER);
					if(resourceRequest!=null) {
						sqlQueryObject.addInsertField("id_resource_parameter", "?");
					}
					else {
						sqlQueryObject.addInsertField("id_resource_response_par", "?");
					}
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("descrizione", "?");
					sqlQueryObject.addInsertField("tipo_parametro", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQueryObject.addInsertField("tipo", "?");
					sqlQueryObject.addInsertField("restrizioni", "?");
					updateQuery = sqlQueryObject.createSQLInsert();
					updateStmt = con.prepareStatement(updateQuery);
					int index = 1;
					updateStmt.setLong(index++, idFK);
					updateStmt.setString(index++, rp.getNome());
					updateStmt.setString(index++, rp.getDescrizione());
					updateStmt.setString(index++, DriverRegistroServiziDB_LIB.getValue(rp.getParameterType()));
					updateStmt.setBoolean(index++, rp.isRequired());
					updateStmt.setString(index++, rp.getTipo());			
					updateStmt.setString(index++, rp.getRestrizioni());
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (PARAMETER) CREATE :\n"+updateQuery);
					n = updateStmt.executeUpdate();
					DriverRegistroServiziDB_LIB.log.debug("_CRUDResourceRequestResponse (PARAMETER) type = " + type + " row affected =" + n);
					updateStmt.close();
					
				}
				
				break;

			case UPDATE:
				throw new Exception("Not Implemented");

				//break;

			case DELETE:
				// delete

				
				// gestione request
				if(resourceRequest!=null) {
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
					sqlQueryObject.addWhereCondition("id_resource_media=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource );
					updateStmt.executeUpdate();
					updateStmt.close();
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
					sqlQueryObject.addWhereCondition("id_resource_parameter=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					updateStmt.executeUpdate();
					updateStmt.close();
				}
				else {
				
					// gestione response
					List<Long> idResourceResponse = new ArrayList<Long>();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addWhereCondition("id_resource=?");
					updateQuery = sqlQueryObject.createSQLQuery();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					selectRS=updateStmt.executeQuery();
					while(selectRS.next()){
						idResourceResponse.add(selectRS.getLong("id"));
					}
					selectRS.close();
					updateStmt.close();
		
					while(idResourceResponse.size()>0){
						
						long idRR = idResourceResponse.remove(0);
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_MEDIA);
						sqlQueryObject.addWhereCondition("id_resource_response_media=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt=con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idRR );
						updateStmt.executeUpdate();
						updateStmt.close();
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_PARAMETER);
						sqlQueryObject.addWhereCondition("id_resource_response_par=?");
						sqlQueryObject.setANDLogicOperator(true);
						updateQuery = sqlQueryObject.createSQLDelete();
						updateStmt=con.prepareStatement(updateQuery);
						updateStmt.setLong(1, idRR);
						updateStmt.executeUpdate();
						updateStmt.close();
					}
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.API_RESOURCES_RESPONSE);
					sqlQueryObject.addWhereCondition("id_resource=?");
					sqlQueryObject.setANDLogicOperator(true);
					updateQuery = sqlQueryObject.createSQLDelete();
					updateStmt=con.prepareStatement(updateQuery);
					updateStmt.setLong(1, idResource);
					updateStmt.executeUpdate();
					updateStmt.close();
				
				}
				
				break;
			}


		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::_CRUDResourceRequestResponse] Exception : " + se.getMessage(),se);
		} finally {
			try {
				if(selectRS!=null)selectRS.close();
			} catch (Exception e) {
			}
			try {
				if(selectStmt!=null)selectStmt.close();
			} catch (Exception e) {
			}
			try {
				if(updateStmt!=null)updateStmt.close();
			} catch (Exception e) {
			}
		}
	}
	
}
