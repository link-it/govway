/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.utils.UtilsException;
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
	public static long CRUDConnettore(int type, Connettore connettore, Connection connection, IDriverBYOK driverBYOK) throws DriverConfigurazioneException {
		PreparedStatement stm = null;
		ResultSet rs=null;
		String sqlQuery;

		if(connettore == null) throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore] L'oggetto Connettore non puo essere null");
		if (type!=CostantiDB.DELETE &&
			(connettore.getNome() == null || connettore.getNome().trim().equals(""))
			){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDConnettore]Il nome Connettore non puo essere null");
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

		String transferMode = null; // in caso di tipo http e https
		Integer transferModeChunkSize = null; // in caso di tipo http e https

		boolean proxy = false;
		String proxyType = null;
		String proxyHostname = null;
		String proxyPort = null;
		String proxyUsername = null;
		String proxyPassword = null;
		
		Integer tempiRispostaConnectionTimeout = null;
		Integer tempiRispostaReadTimeout = null;
		Integer tempiRispostaAvgResponseTime = null;

		String redirectMode = null; // in caso di tipo http e https
		Integer redirectMaxHop = null; // in caso di tipo http e https
		
		String httpImpl = null; // in caso di tipo http e https
		
		String tokenPolicy = null;
		
		String apiKey = null;
		String apiKeyHeader = null;
		String appId = null;
		String appIdHeader = null;
		
		boolean isAbilitato = false;

		Map<String, String> extendedProperties = new HashMap<>();
		
		List<String> propertiesGestiteAttraversoColonneAdHoc = new ArrayList<>();
		
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
			if (nomeProperty.equals(CostantiDB.CONNETTORE_DEBUG) &&
				"true".equals(valoreProperty)){
				debug=true;
			}
			
			// Proxy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_PROXY_TYPE)){
				proxy = true;
				proxyType = valoreProperty;
				
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				
				// cerco altri valori del proxy
				for (Property propertyCheck: connettore.getPropertyList()) {
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_HOSTNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxyHostname = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PORT)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxyPort = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_USERNAME)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxyUsername = propertyCheck.getValore();
					}
					if (propertyCheck.getNome().equals(CostantiDB.CONNETTORE_PROXY_PASSWORD)){
						propertiesGestiteAttraversoColonneAdHoc.add(propertyCheck.getNome());
						proxyPassword = propertyCheck.getValore();
					}
				}
			}
			
			// Tempi Risposta
			if (nomeProperty.equals(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRispostaConnectionTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRispostaReadTimeout = Integer.parseInt(valoreProperty);
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tempiRispostaAvgResponseTime = Integer.parseInt(valoreProperty);
			}
			
			// TransferMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transferMode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				transferModeChunkSize = Integer.parseInt(valoreProperty);
			}
			
			// RedirectMode
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirectMode = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				redirectMaxHop = Integer.parseInt(valoreProperty);
			}
			
			// HttImpl
			if (nomeProperty.equals(CostantiDB.CONNETTORE_HTTP_IMPL)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				httpImpl = valoreProperty;
			}
			
			// TokenPolicy
			if (nomeProperty.equals(CostantiDB.CONNETTORE_TOKEN_POLICY)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				tokenPolicy = valoreProperty;
			}
			
			// ApiKey
			if (nomeProperty.equals(CostantiDB.CONNETTORE_APIKEY)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				apiKey = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_APIKEY_HEADER)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				apiKeyHeader = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_APIKEY_APPID)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				appId = valoreProperty;
			}
			if (nomeProperty.equals(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER)){
				propertiesGestiteAttraversoColonneAdHoc.add(nomeProperty);
				appIdHeader = valoreProperty;
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE, "?");
				sqlQueryObject.addInsertField("url", "?");
				sqlQueryObject.addInsertField("transfer_mode", "?");
				sqlQueryObject.addInsertField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addInsertField("redirect_mode", "?");
				sqlQueryObject.addInsertField("redirect_max_hop", "?");
				sqlQueryObject.addInsertField("http_impl", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("tipo", "?");
				sqlQueryObject.addInsertField("utente", "?");
				sqlQueryObject.addInsertField("password", "?");
				sqlQueryObject.addInsertField("enc_password", "?");
				sqlQueryObject.addInsertField("initcont", "?");
				sqlQueryObject.addInsertField("urlpkg", "?");
				sqlQueryObject.addInsertField("provurl", "?");
				sqlQueryObject.addInsertField("connection_factory", "?");
				sqlQueryObject.addInsertField("send_as", "?");
				sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_COLUMN_NOME, "?");
				sqlQueryObject.addInsertField("debug", "?");				
				sqlQueryObject.addInsertField("proxy", "?");		
				sqlQueryObject.addInsertField("proxy_type", "?");		
				sqlQueryObject.addInsertField("proxy_hostname", "?");		
				sqlQueryObject.addInsertField("proxy_port", "?");		
				sqlQueryObject.addInsertField("proxy_username", "?");		
				sqlQueryObject.addInsertField("proxy_password", "?");	
				sqlQueryObject.addInsertField("enc_proxy_password", "?");
				sqlQueryObject.addInsertField("connection_timeout", "?");		
				sqlQueryObject.addInsertField("read_timeout", "?");		
				sqlQueryObject.addInsertField("avg_response_time", "?");		
				sqlQueryObject.addInsertField("custom", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQueryObject.addInsertField("api_key", "?");
				sqlQueryObject.addInsertField("api_key_header", "?");
				sqlQueryObject.addInsertField("app_id", "?");
				sqlQueryObject.addInsertField("app_id_header", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = connection.prepareStatement(sqlQuery);

				int index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, (isAbilitato ? url : null));
				stm.setString(index++, (isAbilitato ? transferMode : null));
				if(isAbilitato && transferModeChunkSize!=null){
					stm.setInt(index++, transferModeChunkSize);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirectMode : null));
				if(isAbilitato && redirectMaxHop!=null){
					stm.setInt(index++, redirectMaxHop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? httpImpl : null));
				stm.setString(index++, isAbilitato ? nome : null);
				stm.setString(index++, isAbilitato ? tipo : null);
				stm.setString(index++, (isAbilitato ? utente : null));
				
				String plainPassword = isAbilitato ? password : null;
				String encPassword = null;
				if(isAbilitato && driverBYOK!=null && plainPassword!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainPassword);
					if(byokValue!=null) {
						encPassword = byokValue.getWrappedValue();
						plainPassword = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainPassword);
				stm.setString(index++, encPassword);
				
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
				stm.setString(index++, isAbilitato && proxy ? proxyType : null);
				stm.setString(index++, isAbilitato && proxy ? proxyHostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxyPort : null);
				stm.setString(index++, isAbilitato && proxy ? proxyUsername : null);

				String plainProxyPassword = isAbilitato ? proxyPassword : null;
				String encProxyPassword = null;
				if(isAbilitato && driverBYOK!=null && plainProxyPassword!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainProxyPassword);
					if(byokValue!=null) {
						encProxyPassword = byokValue.getWrappedValue();
						plainProxyPassword = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainProxyPassword);
				stm.setString(index++, encProxyPassword);
				
				if(tempiRispostaConnectionTimeout!=null) {
					stm.setInt(index++, tempiRispostaConnectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRispostaReadTimeout!=null) {
					stm.setInt(index++, tempiRispostaReadTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRispostaAvgResponseTime!=null) {
					stm.setInt(index++, tempiRispostaAvgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, tokenPolicy);
				
				String apiKeyInsert = isAbilitato ? apiKey : null;
				if(isAbilitato && apiKey!=null && StringUtils.isNotEmpty(apiKey) && driverBYOK!=null && CostantiConnettori.isConfidential(CostantiDB.CONNETTORE_APIKEY)) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(apiKey);
					if(byokValue!=null) {
						apiKeyInsert = byokValue.getWrappedValue();
						stm.setString(index++, byokValue.getWrappedValue());
					}
					else {
						stm.setString(index++, apiKey);
					}
				}
				else {
					stm.setString(index++, apiKey);
				}
				stm.setString(index++, isAbilitato ? apiKeyHeader : null);
				stm.setString(index++, isAbilitato ? appId : null);
				stm.setString(index++, isAbilitato ? appIdHeader : null);

				DriverConfigurazioneDBLib.logDebug("CRUDConnettore CREATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						nome, tipo, utente, plainPassword, encPassword, 
						initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore,debug,
						proxy, proxyType, proxyHostname, proxyPort, proxyUsername, plainProxyPassword, encProxyPassword,
						tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaAvgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom()),
						tokenPolicy,
						apiKeyInsert, apiKeyHeader, appId, appIdHeader));

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("Inserted " + n + " row(s)");


				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("endpointtype = ?");
				sqlQueryObject.addWhereCondition("nome_connettore = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, endpointtype);
				stm.setString(2, nomeConnettore);

				DriverConfigurazioneDBLib.logDebug("Recupero idConnettore inserito : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, nomeConnettore));

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
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, "?");
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
							throw new DriverConfigurazioneException("Property ["+nomeProperty+"] without value");
						}
						
						String plainValue = valoreProperty;
						String encValue = null;
						if(driverBYOK!=null && CostantiConnettori.isConfidential(nomeProperty)) {
							BYOKWrappedValue byokValue = driverBYOK.wrap(valoreProperty);
							if(byokValue!=null) {
								encValue = byokValue.getWrappedValue();
								plainValue = byokValue.getWrappedPlainValue();
							}
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, plainValue);
						stm.setString(3, encValue);
						stm.setLong(4, connettore.getId());
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new DriverConfigurazioneException("Property ["+nomeP+"] without value");
						}
						
						String plainValue = valoreProperty;
						String encValue = null;
						if(driverBYOK!=null && CostantiConnettori.isConfidential(nomeProperty)) {
							BYOKWrappedValue byokValue = driverBYOK.wrap(valoreProperty);
							if(byokValue!=null) {
								encValue = byokValue.getWrappedValue();
								plainValue = byokValue.getWrappedPlainValue();
							}
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeP);
						stm.setString(2, plainValue);
						stm.setString(3, encValue);
						stm.setLong(4, connettore.getId());
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

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addUpdateField(CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE, "?");
				sqlQueryObject.addUpdateField("url", "?");
				sqlQueryObject.addUpdateField("transfer_mode", "?");
				sqlQueryObject.addUpdateField("transfer_mode_chunk_size", "?");
				sqlQueryObject.addUpdateField("redirect_mode", "?");
				sqlQueryObject.addUpdateField("redirect_max_hop", "?");
				sqlQueryObject.addUpdateField("http_impl", "?");
				sqlQueryObject.addUpdateField("nome", "?");
				sqlQueryObject.addUpdateField("tipo", "?");
				sqlQueryObject.addUpdateField("utente", "?");
				sqlQueryObject.addUpdateField("password", "?");
				sqlQueryObject.addUpdateField("enc_password", "?");
				sqlQueryObject.addUpdateField("initcont", "?");
				sqlQueryObject.addUpdateField("urlpkg", "?");
				sqlQueryObject.addUpdateField("provurl", "?");
				sqlQueryObject.addUpdateField("connection_factory", "?");
				sqlQueryObject.addUpdateField("send_as", "?");
				sqlQueryObject.addUpdateField(CostantiDB.CONNETTORI_COLUMN_NOME, "?");
				sqlQueryObject.addUpdateField("debug", "?");
				sqlQueryObject.addUpdateField("proxy", "?");		
				sqlQueryObject.addUpdateField("proxy_type", "?");		
				sqlQueryObject.addUpdateField("proxy_hostname", "?");		
				sqlQueryObject.addUpdateField("proxy_port", "?");		
				sqlQueryObject.addUpdateField("proxy_username", "?");		
				sqlQueryObject.addUpdateField("proxy_password", "?");
				sqlQueryObject.addUpdateField("enc_proxy_password", "?");
				sqlQueryObject.addUpdateField("connection_timeout", "?");		
				sqlQueryObject.addUpdateField("read_timeout", "?");		
				sqlQueryObject.addUpdateField("avg_response_time", "?");
				sqlQueryObject.addUpdateField("custom", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addUpdateField("api_key", "?");
				sqlQueryObject.addUpdateField("api_key_header", "?");
				sqlQueryObject.addUpdateField("app_id", "?");
				sqlQueryObject.addUpdateField("app_id_header", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = connection.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, endpointtype);
				stm.setString(index++, url);
				stm.setString(index++, (isAbilitato ? transferMode : null));
				if(isAbilitato && transferModeChunkSize!=null){
					stm.setInt(index++, transferModeChunkSize);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? redirectMode : null));
				if(isAbilitato && redirectMaxHop!=null){
					stm.setInt(index++, redirectMaxHop);
				}
				else{
					stm.setNull(index++, Types.INTEGER);
				}
				stm.setString(index++, (isAbilitato ? httpImpl : null));
				stm.setString(index++, isAbilitato ? nome : null);
				stm.setString(index++, isAbilitato ? tipo: null);
				stm.setString(index++, (isAbilitato ? utente : null));
				
				plainPassword = isAbilitato ? password : null;
				encPassword = null;
				if(isAbilitato && driverBYOK!=null && plainPassword!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainPassword);
					if(byokValue!=null) {
						encPassword = byokValue.getWrappedValue();
						plainPassword = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainPassword);
				stm.setString(index++, encPassword);
				
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
				stm.setString(index++, isAbilitato && proxy ? proxyType : null);
				stm.setString(index++, isAbilitato && proxy ? proxyHostname : null);
				stm.setString(index++, isAbilitato && proxy ? proxyPort : null);
				stm.setString(index++, isAbilitato && proxy ? proxyUsername : null);

				plainProxyPassword = isAbilitato ? proxyPassword : null;
				encProxyPassword = null;
				if(isAbilitato && driverBYOK!=null && plainProxyPassword!=null) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(plainProxyPassword);
					if(byokValue!=null) {
						encProxyPassword = byokValue.getWrappedValue();
						plainProxyPassword = byokValue.getWrappedPlainValue();
					}
				}
				stm.setString(index++, plainProxyPassword);
				stm.setString(index++, encProxyPassword);
				
				if(tempiRispostaConnectionTimeout!=null) {
					stm.setInt(index++, tempiRispostaConnectionTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRispostaReadTimeout!=null) {
					stm.setInt(index++, tempiRispostaReadTimeout);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(tempiRispostaAvgResponseTime!=null) {
					stm.setInt(index++, tempiRispostaAvgResponseTime);
				}
				else {
					stm.setNull(index++, Types.INTEGER);
				}
				if(connettore.getCustom()!=null && connettore.getCustom()){
					stm.setInt(index++, 1);
				}else{
					stm.setInt(index++, 0);
				}
				stm.setString(index++, tokenPolicy);
				
				apiKeyInsert = isAbilitato ? apiKey : null;
				if(isAbilitato && apiKey!=null && StringUtils.isNotEmpty(apiKey) && driverBYOK!=null && CostantiConnettori.isConfidential(CostantiDB.CONNETTORE_APIKEY)) {
					BYOKWrappedValue byokValue = driverBYOK.wrap(apiKey);
					if(byokValue!=null) {
						apiKeyInsert = byokValue.getWrappedValue();
						stm.setString(index++, byokValue.getWrappedValue());
					}
					else {
						stm.setString(index++, apiKey);
					}
				}
				else {
					stm.setString(index++, apiKey);
				}
				stm.setString(index++, isAbilitato ? apiKeyHeader : null);
				stm.setString(index++, isAbilitato ? appId : null);
				stm.setString(index++, isAbilitato ? appIdHeader : null);
				
				stm.setLong(index++, idConnettore);

				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("CRUDConnettore UPDATE : \n" + DBUtils.formatSQLString(sqlQuery, endpointtype, url, 
						transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						nome, tipo, utente, plainPassword, encPassword, 
						initcont, urlpkg, provurl, connectionfactory, sendas,nomeConnettore, debug,
						proxy, proxyType, proxyHostname, proxyPort, proxyUsername, plainProxyPassword, encProxyPassword,
						tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaAvgResponseTime,
						(connettore.getCustom()!=null && connettore.getCustom()),
						tokenPolicy,
						apiKeyInsert, apiKeyHeader, appId, appIdHeader,
						idConnettore));

				// Custom properties
				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				// Aggiungo attuali
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, "?");
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
							throw new DriverConfigurazioneException("Property ["+nomeProperty+"] without value");
						}
						
						String plainValue = valoreProperty;
						String encValue = null;
						if(driverBYOK!=null && CostantiConnettori.isConfidential(nomeProperty)) {
							BYOKWrappedValue byokValue = driverBYOK.wrap(valoreProperty);
							if(byokValue!=null) {
								encValue = byokValue.getWrappedValue();
								plainValue = byokValue.getWrappedPlainValue();
							}
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeProperty);
						stm.setString(2, plainValue);
						stm.setString(3, encValue);
						stm.setLong(4, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				else if(extendedProperties.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE, "?");
					sqlQueryObject.addInsertField(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new DriverConfigurazioneException("Property ["+nomeP+"] without value");
						}
						
						String plainValue = valoreProperty;
						String encValue = null;
						if(driverBYOK!=null && CostantiConnettori.isConfidential(nomeProperty)) {
							BYOKWrappedValue byokValue = driverBYOK.wrap(valoreProperty);
							if(byokValue!=null) {
								encValue = byokValue.getWrappedValue();
								plainValue = byokValue.getWrappedPlainValue();
							}
						}
						
						stm = connection.prepareStatement(sqlQuery);
						stm.setString(1, nomeP);
						stm.setString(2, plainValue);
						stm.setString(3, encValue);
						stm.setLong(4, idConnettore);
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				// Delete connettori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.logDebug("CRUDConnettore DELETE : \n" + DBUtils.formatSQLString(sqlQuery, idConnettore));

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
	protected static Connettore getConnettore(long idConnettore, Connection connection, IDriverBYOK driverBYOK) throws DriverConfigurazioneException {

		Connettore connettore = null;

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDBLib.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			if (rs.next()) {
				String endpoint = rs.getString(CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE);
				if (endpoint == null || endpoint.equals("") || endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
					connettore = new Connettore();
					connettore.setNome(rs.getString(CostantiDB.CONNETTORI_COLUMN_NOME));
					connettore.setTipo(TipiConnettore.DISABILITATO.getNome());
					connettore.setId(idConnettore);

				} else {
					Property prop = null;
					connettore = new Connettore();
					connettore.setNome(rs.getString(CostantiDB.CONNETTORI_COLUMN_NOME));
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
					readConnettoreProxy(rs, connettore, driverBYOK);
					
					// Tempi Risposta
					readConnettoreTempiRisposta(rs, connettore);
					
					// transfer_mode
					readConnettoreTransferMode(rs, connettore);
					
					// redirect_mode
					readConnettoreRedirectMode(rs, connettore);
					
					// http_impl
					readConnettoreHttpImpl(rs, connettore);
					
					// token policy
					String tokenPolicy = rs.getString("token_policy");
					if(tokenPolicy!=null && !"".equals(tokenPolicy)){
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
						prop.setValore(tokenPolicy.trim());
						connettore.addProperty(prop);
					}

					// api key
					readAutenticazioneApiKey(rs, connettore, driverBYOK);

					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {
						readConnettoreHttp(rs, connettore);
					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){//jms
						readConnettoreJms(rs, connettore, driverBYOK);
					}else if(endpoint.equals(TipiConnettore.NULL.getNome())){
						//nessuna proprieta per connettore null
					}else if(endpoint.equals(TipiConnettore.NULLECHO.getNome())){
						//nessuna proprieta per connettore nullEcho
					}else if (!endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
						if(rs.getLong("custom")==1){
							// connettore custom
							readPropertiesConnettoreCustom(idConnettore,connettore,connection,driverBYOK);
							connettore.setCustom(true);
						}
						else{
							// legge da file properties
							connettore.setPropertyList(ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,DriverConfigurazioneDBLib.tipoDB));
						}
					}

				}
			}
			
			// Extended Info
			readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection,driverBYOK);
			
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
	
	private static void readConnettoreProxy(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		if(rs.getInt("proxy")==1){
			
			String tmp = rs.getString("proxy_type");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			tmp = rs.getString("proxy_hostname");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			tmp = rs.getString("proxy_port");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			readConnettoreProxyCredentials(rs, connettore, driverBYOK);
		}
	}
	private static void readConnettoreProxyCredentials(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String tmp = rs.getString("proxy_username");
		if(tmp!=null && !"".equals(tmp)){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
			prop.setValore(tmp.trim());
			connettore.addProperty(prop);
		}
		
		tmp = rs.getString("proxy_password");
		String encValue = rs.getString("enc_proxy_password");
		if(tmp!=null && !"".equals(tmp)){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
			
			if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
				if(driverBYOK!=null) {
					prop.setValore(driverBYOK.unwrapAsString(encValue));
				}
				else {
					prop.setValore(encValue);
				}
			}
			else {
				prop.setValore(tmp.trim());
			}
			
			connettore.addProperty(prop);
		}
	}
	
	private static void readConnettoreTempiRisposta(ResultSet rs, Connettore connettore) throws SQLException {
		int connectionTimeout = rs.getInt("connection_timeout");
		if(connectionTimeout>0){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
			prop.setValore(connectionTimeout+"");
			connettore.addProperty(prop);
		}
		int readTimeout = rs.getInt("read_timeout");
		if(readTimeout>0){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
			prop.setValore(readTimeout+"");
			connettore.addProperty(prop);
		}
		int avgResponseTime = rs.getInt("avg_response_time");
		if(avgResponseTime>0){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
			prop.setValore(avgResponseTime+"");
			connettore.addProperty(prop);
		}
	}
	
	private static void readConnettoreTransferMode(ResultSet rs, Connettore connettore) throws SQLException {
		String transferMode = rs.getString("transfer_mode");
		if(transferMode!=null && !"".equals(transferMode)){
			
			Property prop = new Property();
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
	}
	
	private static void readConnettoreRedirectMode(ResultSet rs, Connettore connettore) throws SQLException {
		String redirectMode = rs.getString("redirect_mode");
		if(redirectMode!=null && !"".equals(redirectMode)){
			
			Property prop = new Property();
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
	}
	
	private static void readConnettoreHttpImpl(ResultSet rs, Connettore connettore) throws SQLException {
		String httpImpl = rs.getString("http_impl");
		if(httpImpl!=null && !"".equals(httpImpl)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTP_IMPL);
			prop.setValore(httpImpl.trim());
			connettore.addProperty(prop);
			
		}
	}
	
	private static void readAutenticazioneApiKey(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String apiKey = rs.getString("api_key");
		if(apiKey!=null && !"".equals(apiKey)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_APIKEY);
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(apiKey));
			}
			else {
				prop.setValore(apiKey);
			}
			connettore.addProperty(prop);
			
			String apiKeyHeader = rs.getString("api_key_header");
			if(apiKeyHeader!=null && !"".equals(apiKeyHeader)){
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_APIKEY_HEADER);
				prop.setValore(apiKeyHeader.trim());
				connettore.addProperty(prop);
			}
			
			
			String appId = rs.getString("app_id");
			if(appId!=null && !"".equals(appId)){
				
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_APIKEY_APPID);
				prop.setValore(appId);
				connettore.addProperty(prop);
				
				String appIdHeader = rs.getString("app_id_header");
				if(appIdHeader!=null && !"".equals(appIdHeader)){
					prop = new Property();
					prop.setNome(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER);
					prop.setValore(appIdHeader.trim());
					connettore.addProperty(prop);
				}
			}
		}
	}
	
	private static void readConnettoreHttp(ResultSet rs, Connettore connettore) throws DriverConfigurazioneException, SQLException {
		//	url
		String value = rs.getString("url");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverConfigurazioneException("Connettore di tipo http possiede una url non definita");
		}
		Property prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
		prop.setValore(value);
		connettore.addProperty(prop);
	}
	
	private static void readConnettoreJms(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws DriverConfigurazioneException, SQLException, UtilsException {
		// nome coda/topic
		String value = rs.getString("nome");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverConfigurazioneException("Connettore di tipo jms possiede il nome della coda/topic non definito");
		}
		Property prop = new Property();
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

		readConnettoreJmsCredentials(rs, connettore, driverBYOK);
		
		readConnettoreJmsContext(rs, connettore);
	}
	private static void readConnettoreJmsCredentials(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		// user
		String usr = rs.getString("utente");
		if (usr != null && !usr.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_USER);
			prop.setValore(usr);
			connettore.addProperty(prop);
		}
		// password
		String pwd = rs.getString("password");
		String encValue = rs.getString("enc_password");
		if (pwd != null && !pwd.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PWD);
			
			if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
				if(driverBYOK!=null) {
					prop.setValore(driverBYOK.unwrapAsString(encValue));
				}
				else {
					prop.setValore(encValue);
				}
			}
			else {
				prop.setValore(pwd);
			}
			
			connettore.addProperty(prop);
		}
	}
	private static void readConnettoreJmsContext(ResultSet rs, Connettore connettore) throws SQLException {
		// context-java.naming.factory.initial
		String initcont = rs.getString("initcont");
		if (initcont != null && !initcont.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
			prop.setValore(initcont);
			connettore.addProperty(prop);
		}
		// context-java.naming.factory.url.pkgs
		String urlpkg = rs.getString("urlpkg");
		if (urlpkg != null && !urlpkg.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
			prop.setValore(urlpkg);
			connettore.addProperty(prop);
		}
		// context-java.naming.provider.url
		String provurl = rs.getString("provurl");
		if (provurl != null && !provurl.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
			prop.setValore(provurl);
			connettore.addProperty(prop);
		}
	}
	
	protected static void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection,
			IDriverBYOK driverBYOK) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDBLib.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();
			
			while (rs.next()) {
				processPropertiesConnettoreCustom(rs, connettore, driverBYOK);
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
	private static void processPropertiesConnettoreCustom(ResultSet rs, Connettore connettore, 
			IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String nome = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME);
		String valore = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
		String encValue = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE);
		
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
				return; // è gia stato aggiunto.
			}
		}
		
		Property prop = new Property();
		prop.setNome(nome);
		if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(encValue));
			}
			else {
				prop.setValore(encValue);
			}
		}
		else {
			prop.setValore(valore);
		}
		connettore.addProperty(prop);
	}
	
	private static void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection,
			IDriverBYOK driverBYOK) throws DriverConfigurazioneException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverConfigurazioneDBLib.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				processPropertiesConnettoreExtendedInfo(rs, connettore, 
						driverBYOK);
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
	private static void processPropertiesConnettoreExtendedInfo(ResultSet rs, Connettore connettore, 
			IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String nome = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME);
		String valore = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
		String encValue = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE);
		
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
			return; // è gia stato aggiunto.
		}
		
		Property prop = new Property();
		prop.setNome(nome);
		if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(encValue));
			}
			else {
				prop.setValore(encValue);
			}
		}
		else {
			prop.setValore(valore);
		}
		connettore.addProperty(prop);
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
		return !TipiConnettore.DISABILITATO.getNome().equals(tipo);
	}

	protected static long getIdConnettoreSARISP(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
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

		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

		}
	}

	protected static long getIdConnettoreSAINV(long idServizioApplicativo,Connection con) throws DriverConfigurazioneException
	{
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idConnettore=-1;
		try
		{
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
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

		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs,stm);

		}
	}

	
}
