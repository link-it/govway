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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceRepresentationJson;
import org.openspcoop2.core.registry.ResourceRepresentationXml;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiRestDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiRestDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_accordiRestDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	

	protected void readResources(AccordoServizioParteComune as,Connection conParam, boolean readDatiRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo resource

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readResources");
			else
				con = this.driver.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("path");
			sqlQueryObject.addOrderBy("http_method");
			sqlQueryObject.setSortType(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			while (rs.next()) {

				Resource resource = new Resource();

				String tmp = rs.getString("nome");
				resource.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				resource.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("http_method");
				resource.setMethod(DriverRegistroServiziDB_LIB.getEnumHttpMethod(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("path");
				if(tmp!=null) {
					if(CostantiDB.API_RESOURCE_PATH_ALL_VALUE.equals(tmp.trim())==false) {
						resource.setPath(tmp);
					}
				}
				
				tmp = rs.getString("message_type");
				resource.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("message_type_request");
				resource.setRequestMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("message_type_response");
				resource.setResponseMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				
				tmp = rs.getString("conferma_ricezione");
				resource.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				resource.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				resource.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				resource.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("id_riferimento_richiesta");
				resource.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("scadenza");
				resource.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_azione");
				if (tmp == null || tmp.equals(""))
					resource.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					resource.setProfAzione(tmp);
							
				
				resource.setIdAccordo(as.getId());

				long idResource = rs.getLong("id");
				resource.setId(idResource);

				if(readDatiRegistro) {
					// Aggiungo dettagli della richiesta
					this.readResourcesDetails(resource,true,con);
					
					// Aggiungo dettagli della risposta
					this.readResourcesDetails(resource,false,con);
				}

				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idResource, ProprietariProtocolProperty.RESOURCE, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							resource.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				
				as.addResource(resource);

			}
			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readResources] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	private void readResourcesDetails(Resource resource,boolean request, Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo resource

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readResourcesDetails(request:"+request+")");
			else
				con = this.driver.globalConnection;

			if(resource.getId()==null || resource.getId()<=0)
				throw new Exception("Resource id non definito");

			if(request) {
				resource.setRequest(new ResourceRequest());
				resource.getRequest().setIdResource(resource.getId());
				
				List<ResourceRepresentation> l = this.readResourcesMedia(resource.getId(), true, con);
				if(l!=null && l.size() > 0) {
					resource.getRequest().getRepresentationList().addAll(l);
				}
				
				List<ResourceParameter> lp = this.readResourcesParameters(resource.getId(), true, con);
				if(lp!=null && lp.size() > 0) {
					resource.getRequest().getParameterList().addAll(lp);
				}
			}
			else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_resource = ?");
				sqlQueryObject.addOrderBy("status");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, resource.getId());
				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, resource.getId()));
				rs = stm.executeQuery();
				while (rs.next()) {
					
					ResourceResponse rr = new ResourceResponse();
					
					rr.setIdResource(resource.getId());
					
					String tmp = rs.getString("descrizione");
					rr.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
					
					int status = rs.getInt("status");
					rr.setStatus(status);
					
					long idRR = rs.getLong("id");
					rr.setId(idRR);
										
					List<ResourceRepresentation> l = this.readResourcesMedia(idRR, false, con);
					if(l!=null && l.size() > 0) {
						rr.getRepresentationList().addAll(l);
					}
					
					List<ResourceParameter> lp = this.readResourcesParameters(idRR, false, con);
					if(lp!=null && lp.size() > 0) {
						rr.getParameterList().addAll(lp);
					}
					
					resource.addResponse(rr);
				}
				rs.close();
				stm.close();
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readResourcesDetails] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	private List<ResourceRepresentation> readResourcesMedia(long idResourceDetail,boolean request,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo resource

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readResourcesMedia("+idResourceDetail+")");
			else
				con = this.driver.globalConnection;

			if(idResourceDetail<=0)
				throw new Exception("ResourceDetail id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
			sqlQueryObject.addSelectField("*");
			if(request) {
				sqlQueryObject.addWhereCondition("id_resource_media = ?");
			}
			else {
				sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
			}
			sqlQueryObject.addOrderBy("media_type");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idResourceDetail);


			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idResourceDetail));
			rs = stm.executeQuery();
			
			List<ResourceRepresentation> list = new ArrayList<ResourceRepresentation>();
			
			while(rs.next()) {
				
				ResourceRepresentation rr = new ResourceRepresentation();
								
				String tmp = rs.getString("media_type");
				rr.setMediaType(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("message_type");
				rr.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("nome");
				rr.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("descrizione");
				rr.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("tipo");
				rr.setRepresentationType(DriverRegistroServiziDB_LIB.getEnumRepresentationType((tmp == null || tmp.equals("")) ? null : tmp));
				
				if(rr.getRepresentationType()!=null) {
					switch (rr.getRepresentationType()) {
					case XML:
						
						ResourceRepresentationXml xml = new ResourceRepresentationXml();
						
						tmp = rs.getString("xml_tipo");
						xml.setXmlType(DriverRegistroServiziDB_LIB.getEnumRepresentationXmlType((tmp == null || tmp.equals("")) ? null : tmp));
						
						tmp = rs.getString("xml_name");
						xml.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
						
						tmp = rs.getString("xml_namespace");
						xml.setNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
						
						rr.setXml(xml);
						
						break;
	
					case JSON:
						
						ResourceRepresentationJson json = new ResourceRepresentationJson();
						
						tmp = rs.getString("json_type");
						json.setTipo(((tmp == null || tmp.equals("")) ? null : tmp));
						
						rr.setJson(json);
						
						break;
					}
				}
				
				long idRR = rs.getLong("id");
				rr.setId(idRR);
				
				list.add(rr);
			
			}
			rs.close();
			stm.close();
			
			return list;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readResourcesMedia] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	private List<ResourceParameter> readResourcesParameters(long idResourceDetail,boolean request,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo resource

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readResourcesMedia("+idResourceDetail+")");
			else
				con = this.driver.globalConnection;

			if(idResourceDetail<=0)
				throw new Exception("ResourceDetail id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
			sqlQueryObject.addSelectField("*");
			if(request) {
				sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
			}
			else {
				sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
			}
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idResourceDetail);


			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idResourceDetail));
			rs = stm.executeQuery();
			
			List<ResourceParameter> list = new ArrayList<ResourceParameter>();
			
			while(rs.next()) {
				
				ResourceParameter rr = new ResourceParameter();
								
				String tmp = rs.getString("nome");
				rr.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("descrizione");
				rr.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("tipo_parametro");
				rr.setParameterType(DriverRegistroServiziDB_LIB.getEnumParameterType((tmp == null || tmp.equals("")) ? null : tmp));
				
				boolean req = rs.getBoolean("required");
				rr.setRequired(req);
				
				tmp = rs.getString("tipo");
				rr.setTipo(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = rs.getString("restrizioni");
				rr.setRestrizioni(((tmp == null || tmp.equals("")) ? null : tmp));
				
				long idRR = rs.getLong("id");
				rr.setId(idRR);
				
				list.add(rr);
			
			}
			rs.close();
			stm.close();
			
			return list;
			
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readResourcesParameters] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}
	
	protected List<Resource> accordiResourceList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiResourceList";
		int idLista = Liste.ACCORDI_API_RESOURCES;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		String filterHttpMethod = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_HTTP_METHOD);
		org.openspcoop2.core.registry.constants.HttpMethod httpMethod = null;
		if(filterHttpMethod!=null) {
			httpMethod = org.openspcoop2.core.registry.constants.HttpMethod.toEnumConstant(filterHttpMethod);
		}
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterHttpMethod : " + filterHttpMethod);
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Resource> lista = new ArrayList<Resource>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiResourceList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("http_method", search, true, true),
						sqlQueryObject.getWhereLikeCondition("path", search, true, true));
				if(httpMethod!=null) {
					sqlQueryObject.addWhereCondition("http_method = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				if(httpMethod!=null) {
					sqlQueryObject.addWhereCondition("http_method = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setLong(index++,idAccordo);
			if(httpMethod!=null) {
				stmt.setString(index++,httpMethod.name());
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("http_method");
				sqlQueryObject.addSelectField("path");
				sqlQueryObject.addSelectField("message_type");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("http_method", search, true, true),
						sqlQueryObject.getWhereLikeCondition("path", search, true, true));
				if(httpMethod!=null) {
					sqlQueryObject.addWhereCondition("http_method = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("path");
				sqlQueryObject.addOrderBy("http_method");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("http_method");
				sqlQueryObject.addSelectField("path");
				sqlQueryObject.addSelectField("message_type");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("id_riferimento_richiesta");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				if(httpMethod!=null) {
					sqlQueryObject.addWhereCondition("http_method = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("path");
				sqlQueryObject.addOrderBy("http_method");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			stmt.setLong(index++, idAccordo);
			if(httpMethod!=null) {
				stmt.setString(index++,httpMethod.name());
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				
				Resource resource = new Resource();

				String tmp = risultato.getString("nome");
				resource.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = risultato.getString("descrizione");
				resource.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = risultato.getString("http_method");
				resource.setMethod(DriverRegistroServiziDB_LIB.getEnumHttpMethod(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = risultato.getString("path");
				if(tmp!=null) {
					if(CostantiDB.API_RESOURCE_PATH_ALL_VALUE.equals(tmp.trim())==false) {
						resource.setPath(tmp);
					}
				}
				
				resource.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("conferma_ricezione")));
				resource.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("consegna_in_ordine")));
				resource.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("filtro_duplicati")));
				resource.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("identificativo_collaborazione")));
				resource.setIdRiferimentoRichiesta(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("id_riferimento_richiesta")));
				resource.setScadenza(risultato.getString("scadenza"));
				resource.setProfAzione(risultato.getString("profilo_azione"));
				
				tmp = risultato.getString("message_type");
				resource.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				resource.setIdAccordo(risultato.getLong("id_accordo"));

				long idResource = risultato.getLong("id");
				resource.setId(idResource);
				
				lista.add(resource);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	protected List<ResourceResponse> accordiResourceResponseList(long idRisorsa, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiResourceResponseList";
		int idLista = Liste.ACCORDI_API_RESOURCES_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<ResourceResponse> lista = new ArrayList<ResourceResponse>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiResourceList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_resource = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("descrizione", search, true, true),
						"status = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_resource = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idRisorsa);
			if (!search.equals("")) {
				try {
					stmt.setInt(2,Integer.parseInt(search));
				}catch(Exception e) {
					stmt.setInt(2,-1);	
				}
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectField("id_resource");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("status");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_resource = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("descrizione", search, true, true),
						"status = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("status");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_RESPONSE);
				sqlQueryObject.addSelectField("id_resource");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("status");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_resource = ?");
				sqlQueryObject.addOrderBy("status");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idRisorsa);
			if (!search.equals("")) {
				try {
					stmt.setInt(2,Integer.parseInt(search));
				}catch(Exception e) {
					stmt.setInt(2,-1);	
				}
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				
				ResourceResponse resource = new ResourceResponse();

				resource.setStatus(risultato.getInt("status"));

				String tmp = risultato.getString("descrizione");
				resource.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				resource.setIdResource(risultato.getLong("id_resource"));

				long idResource = risultato.getLong("id");
				resource.setId(idResource);
				
				lista.add(resource);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	protected List<ResourceRepresentation> accordiResourceRepresentationsList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca)  throws DriverRegistroServiziException {
		String nomeMetodo = "accordiResourceRepresentationsList";
		int idLista = Liste.ACCORDI_API_RESOURCES_REPRESENTATION_REQUEST;
		if(!isRequest)
			idLista = Liste.ACCORDI_API_RESOURCES_REPRESENTATION_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<ResourceRepresentation> lista = new ArrayList<ResourceRepresentation>();
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeOperationMessagePartList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_media = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
				sqlQueryObject.addWhereLikeCondition("media_type", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_media = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			
			if(isRequest)
				stmt.setLong(1,idRisorsa);
			else 
				stmt.setLong(1,idRisposta);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addSelectField("id_resource_media");
				sqlQueryObject.addSelectField("id_resource_response_media");
				sqlQueryObject.addSelectField("media_type");
				sqlQueryObject.addSelectField("message_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("xml_tipo");
				sqlQueryObject.addSelectField("xml_name");
				sqlQueryObject.addSelectField("xml_namespace");
				sqlQueryObject.addSelectField("json_type");
				sqlQueryObject.addSelectField("id");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_media = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
				sqlQueryObject.addWhereLikeCondition("media_type", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_MEDIA);
				sqlQueryObject.addSelectField("id_resource_media");
				sqlQueryObject.addSelectField("id_resource_response_media");
				sqlQueryObject.addSelectField("media_type");
				sqlQueryObject.addSelectField("message_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("xml_tipo");
				sqlQueryObject.addSelectField("xml_name");
				sqlQueryObject.addSelectField("xml_namespace");
				sqlQueryObject.addSelectField("json_type");
				sqlQueryObject.addSelectField("id");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_media = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_media = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(isRequest)
				stmt.setLong(1,idRisorsa);
			else 
				stmt.setLong(1,idRisposta);
			risultato = stmt.executeQuery();
			
			while (risultato.next()) {
				ResourceRepresentation rr = new ResourceRepresentation();
				
				String tmp = risultato.getString("media_type");
				rr.setMediaType(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("message_type");
				rr.setMessageType(DriverRegistroServiziDB_LIB.getEnumMessageType((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("nome");
				rr.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("descrizione");
				rr.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("tipo");
				rr.setRepresentationType(DriverRegistroServiziDB_LIB.getEnumRepresentationType((tmp == null || tmp.equals("")) ? null : tmp));
				
				if(rr.getRepresentationType()!=null) {
					switch (rr.getRepresentationType()) {
					case XML:
						
						ResourceRepresentationXml xml = new ResourceRepresentationXml();
						
						tmp = risultato.getString("xml_tipo");
						xml.setXmlType(DriverRegistroServiziDB_LIB.getEnumRepresentationXmlType((tmp == null || tmp.equals("")) ? null : tmp));
						
						tmp = risultato.getString("xml_name");
						xml.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
						
						tmp = risultato.getString("xml_namespace");
						xml.setNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
						
						rr.setXml(xml);
						
						break;
	
					case JSON:
						
						ResourceRepresentationJson json = new ResourceRepresentationJson();
						
						tmp = risultato.getString("json_type");
						json.setTipo(((tmp == null || tmp.equals("")) ? null : tmp));
						
						rr.setJson(json);
						
						break;
					}
				}
				
				long idRR = risultato.getLong("id");
				rr.setId(idRR);
				
				lista.add(rr);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	protected List<ResourceParameter> accordiResourceParametersList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca)  throws DriverRegistroServiziException {
		String nomeMetodo = "accordiResourceParametersList";
		int idLista = Liste.ACCORDI_API_RESOURCES_PARAMETERS_REQUEST;
		if(!isRequest)
			idLista = Liste.ACCORDI_API_RESOURCES_PARAMETERS_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<ResourceParameter> lista = new ArrayList<ResourceParameter>();
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiPorttypeOperationMessagePartList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			
			if(isRequest)
				stmt.setLong(1,idRisorsa);
			else 
				stmt.setLong(1,idRisposta);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addSelectField("id_resource_parameter");
				sqlQueryObject.addSelectField("id_resource_response_par");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("tipo_parametro");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("restrizioni");
				sqlQueryObject.addSelectField("id");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_parametro");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES_PARAMETER);
				sqlQueryObject.addSelectField("id_resource_parameter");
				sqlQueryObject.addSelectField("id_resource_response_par");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("tipo_parametro");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("restrizioni");
				sqlQueryObject.addSelectField("id");
				if(isRequest)
					sqlQueryObject.addWhereCondition("id_resource_parameter = ?");
				else
					sqlQueryObject.addWhereCondition("id_resource_response_par = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_parametro");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(isRequest)
				stmt.setLong(1,idRisorsa);
			else 
				stmt.setLong(1,idRisposta);
			risultato = stmt.executeQuery();
			
			while (risultato.next()) {
				ResourceParameter rr = new ResourceParameter();
				
				String tmp = risultato.getString("nome");
				rr.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("descrizione");
				rr.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("tipo_parametro");
				rr.setParameterType(DriverRegistroServiziDB_LIB.getEnumParameterType((tmp == null || tmp.equals("")) ? null : tmp));
				
				boolean req = risultato.getBoolean("required");
				rr.setRequired(req);
				
				tmp = risultato.getString("tipo");
				rr.setTipo(((tmp == null || tmp.equals("")) ? null : tmp));
				
				tmp = risultato.getString("restrizioni");
				rr.setRestrizioni(((tmp == null || tmp.equals("")) ? null : tmp));
				
				long idRR = risultato.getLong("id");
				rr.setId(idRR);
				
				lista.add(rr);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
}
