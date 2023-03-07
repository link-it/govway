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
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_connettoriLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_connettoriLIB {


	/**
	 * CRUD oggetto Connettore. In caso di CREATE inserisce nel db il dati del
	 * connettore passato e ritorna l'id dell'oggetto creato Non si occupa di
	 * chiudere la connessione con il db in caso di errore in quanto verra'
	 * gestita dal metodo chiamante
	 * 
	 * @param type
	 *            Tipo operazione {1 (CREATE),2 (UPDATE),3 (DELETE)}
	 * @param connettore
	 * @return id del connettore in caso di type 1 (CREATE)
	 */
	public static long CRUDConnettore(int type, Connettore connettore, Connection connection) throws DriverConfigurazioneException {
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(connettore == null) throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'oggetto Connettore non puo essere null");
		if (type!=CostantiDB.DELETE){
			if(connettore.getNome() == null || connettore.getNome().trim().equals(""))throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore]Il nome Connettore non puo essere null");
		}
		// il tipo di connettore (http, jms, disabilitato o altro)
		String nomeConnettore = connettore.getNome();
		String endpointtype = connettore.getTipo();

		if (endpointtype == null || endpointtype.trim().equals(""))
			endpointtype = TipiConnettore.DISABILITATO.getNome();

		String url = null;// in caso di tipo http
		boolean debug = false;
		String nome = null; // jms
		String tipo = null; // jms
		String utente = null;// jms
		String password = null;// jms
		String initcont = null;// jms
		String urlpkg = null;// jms
		String provurl = null;// jms
		String connectionfactory = null;// jms
		String sendas = null;// jms

		String transfer_mode = null; // in caso di tipo http e https
		Integer transfer_mode_chunk_size = null; // in caso di tipo http e https

		boolean proxy = false;
		String proxy_type = null;
		String proxy_hostname = null;
		String proxy_port = null;
		String proxy_username = null;
		String proxy_password = null;
		
		Integer tempiRisposta_connectionTimeout = null;
		Integer tempiRisposta_readTimeout = null;
		Integer tempiRisposta_avgResponseTime = null;

		String redirect_mode = null; // in caso di tipo http e https
		Integer redirect_max_hop = null; // in caso di tipo http e https
		
		String token_policy = null;
		
		boolean isAbilitato = false;

		Map<String, String> extendedProperties = new HashMap<String, String>();
		
		List<String> propertiesGestiteAttraversoColonneAdHoc = new ArrayList<String>();
		
		// setto i dati, se le property non sono presenti il loro valore rimarra
		// a null e verra settato come tale nel DB
		String nomeProperty = null;
		String valoreProperty = null;
		for (int i = 0; i < connettore.sizePropertyList(); i++) {
			nomeProperty = connettore.getProperty(i).getNome();

			valoreProperty = connettore.getProperty(i).getValore();
			if (valoreProperty != null && valoreProperty.equals(""))
				valoreProperty = null;

			// Debug
			if (nomeProperty.equals(CostantiDB.CONNETTORE_DEBUG)){
				if("true".equals(valoreProperty)){
					debug=true;
				}
			}
			
			// Proxy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_PROXY_TYPE)){
				proxy = true;
				proxy_type = valoreProperty;
				
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				
				// cerco altri valori del proxy
				for (Property propertyCheck: connettore.getPropertyList()) {
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_HOSTNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_hostname = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PORT)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_port = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_USERNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_username = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PASSWORD)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxy_password = propertyCheck.getValore();
					}
				}
			}
			
			// Tempi Risposta
			if (nomeProperty.equals(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_connectionTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_readTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRisposta_avgResponseTime = Integer.parseInt(valoreProperty);
			}
			
			// TransferMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transfer_mode_chunk_size = Integer.parseInt(valoreProperty);
			}
			
			// RedirectMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_mode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirect_max_hop = Integer.parseInt(valoreProperty);
			}
			
			// TokenPolicy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TOKEN_POLICY)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				token_policy = valoreProperty;
			}
			

			if(TipiConnettore.HTTP.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_LOCATION))
					url = valoreProperty;
			}
			else if(TipiConnettore.JMS.getNome().equals(endpointtype)){
				if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_NOME))
					nome = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_TIPO))
					tipo = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_USER))
					utente = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_PWD))
					password = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL))
					initcont = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG))
					urlpkg = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL))
					provurl = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY))
					connectionfactory = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_JMS_SEND_AS))
					sendas = valoreProperty;
			}

			// se endpointype != disabilitato allora lo setto abilitato
			if (!endpointtype.equalsIgnoreCase(TipiConnettore.DISABILITATO.getNome()))
				isAbilitato = true;
			
			// extendedProperties
			if(nomeProperty.startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)){
				extendedProperties.put(nomeProperty, valoreProperty);
			}

		}

		try {

			long idConnettore = 0;
			int n = 0;
			switch (type) {
			case CREATE:

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addInsertField("endpointtype", "?");
				sqlQueryObject.addInsertField("url", "?");
				sqlQueryObject.addInsertField("transfer_mode", "?");
				sqlQueryObject.addInsertField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addInsertField("redirect_mode", "?");
				sqlQueryObject.addInsertField("redirect_max_hop", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("initcont", "?");
				sqlQueryObject.addInsertField("urlpkg", "?");
				sqlQueryObject.addInsertField("provurl", "?");
				sqlQueryObject.addInsertField("connection_factory", "?");
				sqlQueryObject.addInsertField("send_as", "?");
				sqlQueryObject.addInsertField("nome_connettore", "?");
				sqlQueryObject.addInsertField("debug", "?");				
				sqlQueryObject.addInsertField("proxy", "?");		
				sqlQueryObject.addInsertField("proxy_type", "?");		
				sqlQueryObject.addInsertField("proxy_hostname", "?");		
				sqlQueryObject.addInsertField("proxy_port", "?");		
				sqlQueryObject.addInsertField("proxy_username", "?");		
				sqlQueryObject.addInsertField("proxy_password", "?");	
				sqlQueryObject.addInsertField("connection_timeout", "?");		
				sqlQueryObject.addInsertField("read_timeout", "?");		
				sqlQueryObject.addInsertField("avg_response_time", "?");		
				sqlQueryObject.addInsertField("custom", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				int index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, (isAbilitato ? url : null));
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, isAbilitato ? nome : null);
				stm.setString(index++, isAbilitato ? tipo : null);
				stm.setString(index++, (isAbilitato ? utente : null));
				stm.setString(index++, (isAbilitato ? password : null));
				stm.setString(index++, (isAbilitato ? initcont : null));
				stm.setString(index++, (isAbilitato ? urlpkg : null));
				stm.setString(index++, (isAbilitato ? provurl : null));
				stm.setString(index++, (isAbilitato ? connectionfactory : null));
				stm.setString(index++, (isAbilitato ? sendas : null));
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(tempiRisposta_connectionTimeout!=null) {
					stm.setInt(index++, tempiRisposta_connectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_readTimeout!=null) {
					stm.setInt(index++, tempiRisposta_readTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_avgResponseTime!=null) {
					stm.setInt(index++, tempiRisposta_avgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, token_policy);

				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore,debug,
						proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_avgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom())),
						token_policy);

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + n + " row(s)");


				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("endpointtype = ?");
				sqlQueryObject.addWhereCondition("nome_connettore = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, nomeConnettore);

				DriverConfigurazioneDB_LIB.log.debug("Recupero idConnettore inserito : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, nomeConnettore));

				rs = stm.executeQuery();

				if (rs.next()) {
					idConnettore = rs.getLong("id");
					connettore.setId(idConnettore);
				} else {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
				}

				rs.close();
				stm.close();				
				
				// Custom properties
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, connettore.getId());
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeP+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeP);
						stm.setString(2, valoreProperty);
						stm.setLong(3, connettore.getId());
						stm.executeUpdate();
						stm.close();
					}				
				}

				break;

			case UPDATE:
				// update
				idConnettore = connettore.getId();

				if (idConnettore < 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di update.");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addUpdateField("endpointtype", "?");
				sqlQueryObject.addUpdateField("url", "?");
				sqlQueryObject.addUpdateField("transfer_mode", "?");
				sqlQueryObject.addUpdateField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addUpdateField("redirect_mode", "?");
				sqlQueryObject.addUpdateField("redirect_max_hop", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("initcont", "?");
				sqlQueryObject.addUpdateField("urlpkg", "?");
				sqlQueryObject.addUpdateField("provurl", "?");
				sqlQueryObject.addUpdateField("connection_factory", "?");
				sqlQueryObject.addUpdateField("send_as", "?");
				sqlQueryObject.addUpdateField("nome_connettore", "?");
				sqlQueryObject.addUpdateField("debug", "?");
				sqlQueryObject.addUpdateField("proxy", "?");		
				sqlQueryObject.addUpdateField("proxy_type", "?");		
				sqlQueryObject.addUpdateField("proxy_hostname", "?");		
				sqlQueryObject.addUpdateField("proxy_port", "?");		
				sqlQueryObject.addUpdateField("proxy_username", "?");		
				sqlQueryObject.addUpdateField("proxy_password", "?");
				sqlQueryObject.addUpdateField("connection_timeout", "?");		
				sqlQueryObject.addUpdateField("read_timeout", "?");		
				sqlQueryObject.addUpdateField("avg_response_time", "?");
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, url);
				stm.setString(index++, (isAbilitato ? transfer_mode : null));
				if(isAbilitato && transfer_mode_chunk_size!=null){
					stm.setInt(index++, transfer_mode_chunk_size);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirect_mode : null));
				if(isAbilitato && redirect_max_hop!=null){
					stm.setInt(index++, redirect_max_hop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, nome);
				stm.setString(index++, tipo);
				stm.setString(index++, utente);
				stm.setString(index++, password);
				stm.setString(index++, initcont);
				stm.setString(index++, urlpkg);
				stm.setString(index++, provurl);
				stm.setString(index++, connectionfactory);
				stm.setString(index++, sendas);
				stm.setString(index++, nomeConnettore);
				if(debug){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				if(proxy){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, isAbilitato && proxy ? proxy_type : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_hostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_port : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_username : null);
				stm.setString(index++, isAbilitato && proxy ? proxy_password : null);
				if(tempiRisposta_connectionTimeout!=null) {
					stm.setInt(index++, tempiRisposta_connectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_readTimeout!=null) {
					stm.setInt(index++, tempiRisposta_readTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRisposta_avgResponseTime!=null) {
					stm.setInt(index++, tempiRisposta_avgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, token_policy);
				stm.setLong(index++, idConnettore);

				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
						nome, tipo, utente, password, initcont, urlpkg, provurl, connectionfactory, sendas,nomeConnettore, debug,
						proxy, proxy_type, proxy_hostname, proxy_port, proxy_username, proxy_password,
						tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_avgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom()),
						token_policy,
						idConnettore));

				// Custom properties
				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				// Aggiungo attuali
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						nomeProperty = connettore.getProperty(i).getNome();
						if(propertiesGestiteAttraversoColonneAdHoc.contains(nomeProperty)){
							continue;
						}
						valoreProperty = connettore.getProperty(i).getValore();
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeProperty+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new Exception("Property ["+nomeP+"] without value");
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeP);
						stm.setString(2, valoreProperty);
						stm.setLong(3, idConnettore);
						stm.executeUpdate();
						stm.close();
					}			
				}
				
				break;

			case DELETE:
				// delete
				idConnettore = connettore.getId();

				if (idConnettore < 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di delete.");

				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				// Delete connettori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("CRUDConnettore DELETE : \n" + DBUtils.formatSQLString(sqlQuery, idConnettore));

				break;
			}

			// ritorno l id del connettore questo e' utile in caso di create
			return idConnettore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] SQLException : " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] Exception : " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	/**
	 * Ritorna il connettore con idConnettore, null se il connettore non esiste
	 */
	protected static Connettore getConnettore(long idConnettore, Connection connection) throws DriverConfigurazioneException {

		Connettore connettore = null;

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			if (rs.next()) {
				String endpoint = rs.getString("endpointtype");
				if (endpoint == null || endpoint.equals("") || endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
					connettore.setTipo(TipiConnettore.DISABILITATO.getNome());
					connettore.setId(idConnettore);

				} else {
					Property prop = null;
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
					connettore.setTipo(endpoint);
					// l'id del connettore e' quello passato come parametro
					connettore.setId(idConnettore);

					// Debug
					if(rs.getInt("debug")==1){
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_DEBUG);
						prop.setValore("true");
						connettore.addProperty(prop);
					}
					
					// Proxy
					if(rs.getInt("proxy")==1){
						
						String tmp = rs.getString("proxy_type");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_hostname");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_port");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_username");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_password");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
					}
					
					// Tempi Risposta
					int connectionTimeout = rs.getInt("connection_timeout");
					if(connectionTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
						prop.setValore(connectionTimeout+"");
						connettore.addProperty(prop);
						
					}
					int readTimeout = rs.getInt("read_timeout");
					if(readTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
						prop.setValore(readTimeout+"");
						connettore.addProperty(prop);
						
					}
					int avgResponseTime = rs.getInt("avg_response_time");
					if(avgResponseTime>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
						prop.setValore(avgResponseTime+"");
						connettore.addProperty(prop);
						
					}
					
					// transfer_mode
					String transferMode = rs.getString("transfer_mode");
					if(transferMode!=null && !"".equals(transferMode)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
						prop.setValore(transferMode.trim());
						connettore.addProperty(prop);
						
						transferMode = rs.getString("transfer_mode_chunk_size");
						if(transferMode!=null && !"".equals(transferMode)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
							prop.setValore(transferMode.trim());
							connettore.addProperty(prop);
						}
					}
					
					// redirect_mode
					String redirectMode = rs.getString("redirect_mode");
					if(redirectMode!=null && !"".equals(redirectMode)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
						prop.setValore(redirectMode.trim());
						connettore.addProperty(prop);
						
						redirectMode = rs.getString("redirect_max_hop");
						if(redirectMode!=null && !"".equals(redirectMode)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
							prop.setValore(redirectMode.trim());
							connettore.addProperty(prop);
						}
					}
					
					// token policy
					String tokenPolicy = rs.getString("token_policy");
					if(tokenPolicy!=null && !"".equals(tokenPolicy)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
						prop.setValore(tokenPolicy.trim());
						connettore.addProperty(prop);
						
					}


					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {
						//	url
						String value = rs.getString("url");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo http possiede una url non definita");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
						prop.setValore(value);
						connettore.addProperty(prop);
					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){//jms

						// nome coda/topic
						String value = rs.getString("nome");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il nome della coda/topic non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
						prop.setValore(value);
						connettore.addProperty(prop);

						// tipo
						value = rs.getString("tipo");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il tipo della coda non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
						prop.setValore(value);
						connettore.addProperty(prop);

						// connection-factory
						value = rs.getString("connection_factory");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms non possiede la definizione di una Connection Factory");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
						prop.setValore(value);
						connettore.addProperty(prop);

						// send_as
						value = rs.getString("send_as");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverConfigurazioneException("Connettore di tipo jms possiede il tipo dell'oggetto JMS non definito");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
						prop.setValore(value);
						connettore.addProperty(prop);

						// user
						String usr = rs.getString("utente");
						if (usr != null && !usr.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_USER);
							prop.setValore(usr);
							connettore.addProperty(prop);
						}
						// password
						String pwd = rs.getString("password");
						if (pwd != null && !pwd.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PWD);
							prop.setValore(pwd);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.initial
						String initcont = rs.getString("initcont");
						if (initcont != null && !initcont.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
							prop.setValore(initcont);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.url.pkgs
						String urlpkg = rs.getString("urlpkg");
						if (urlpkg != null && !urlpkg.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
							prop.setValore(urlpkg);
							connettore.addProperty(prop);
						}
						// context-java.naming.provider.url
						String provurl = rs.getString("provurl");
						if (provurl != null && !provurl.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
							prop.setValore(provurl);
							connettore.addProperty(prop);
						}

					}else if(endpoint.equals(TipiConnettore.NULL.getNome())){
						//nessuna proprieta per connettore null
					}else if(endpoint.equals(TipiConnettore.NULLECHO.getNome())){
						//nessuna proprieta per connettore nullEcho
					}else if (!endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
						if(rs.getLong("custom")==1){
							// connettore custom
							readPropertiesConnettoreCustom(idConnettore,connettore,connection);
							connettore.setCustom(true);
						}
						else{
							// legge da file properties
							connettore.setPropertyList(ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,DriverConfigurazioneDB_LIB.tipoDB));
						}
					}

				}
			}
			
			// Extended Info
			readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection);
			
			return connettore;
		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::getConnettore] SQLException : " + sqle.getMessage(),sqle);
		}catch (DriverConfigurazioneException e) {
			throw e;
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::getConnettore] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	protected static void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();
			
			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");
				
				if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){ // lo posso aver aggiunto prima
					boolean found = false;
					for (int i = 0; i < connettore.sizePropertyList(); i++) {
						if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())){
							// already exists
							found = true;
							break;
						}
					}
					if(found){
						continue; // è gia stato aggiunto.
					}
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}
			
			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreCustom] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreCustom] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	protected static void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition("name", CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");
				
				// Le proprietà sono già state inserite in caso di connettore custom
				boolean found = false;
				for (int i = 0; i < connettore.sizePropertyList(); i++) {
					if(nome.equals(connettore.getProperty(i).getNome())){
						// already exists
						found = true;
						break;
					}
				}
				if(found){
					continue; // è gia stato aggiunto.
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}
			
			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreExtendedInfo] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::readPropertiesConnettoreExtendedInfo] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	/**
	 * Se il connettore e' null lo considero disabilitato
	 * @param connettore
	 * @return true se il connettore e' abilitato
	 */
	protected static boolean isConnettoreAbilitato(Connettore connettore) {

		//Se connettore null oppure il tipo e' null o "" o DISABILITATO allora connettore disabilitato
		//altrimenti e' abilitato.
		if (connettore == null)
			return false;

		String tipo = connettore.getTipo();
		if(!TipiConnettore.DISABILITATO.getNome().equals(tipo)) return true;
		else return false;
	}

	protected static long getIdConnettore_SA_RISP(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizioApplicativo);
			rs=stm.executeQuery();

			if(rs.next()){
				idConnettore = rs.getLong("id_connettore_risp");
			}

			return idConnettore;

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

	protected static long getIdConnettore_SA_INV(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizioApplicativo);
			rs=stm.executeQuery();

			if(rs.next()){
				idConnettore = rs.getLong("id_connettore_inv");
			}

			return idConnettore;

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs,stm);

		}
	}

	
}
