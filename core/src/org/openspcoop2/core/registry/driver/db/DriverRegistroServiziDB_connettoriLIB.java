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
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
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
public class DriverRegistroServiziDB_connettoriLIB {


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
	public static long CRUDConnettore(int type, Connettore connettore, Connection connection, IDriverBYOK driverBYOK) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		String sqlQuery;

		if(connettore == null) throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'oggetto Connettore non puo essere NULL.");
		if (type!=CostantiDB.DELETE &&
			(connettore.getNome() == null || connettore.getNome().trim().equals("")) 
			){
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] il nome connettore non puo essere NULL.");
		}

		String nomeConnettore = null;
		String endpointtype = null;
		boolean debug = false;
		String url = null;
		String nome = null;
		String tipo = null;
		String utente = null;
		String password = null;
		String initcont = null;
		String urlpkg = null;
		String provurl = null;
		String connectionfactory = null;
		String sendas = null;

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
		
		String tokenPolicy = null;
		
		String apiKey = null;
		String apiKeyHeader = null;
		String appId = null;
		String appIdHeader = null;
		
		// setto i dati, se le property non sono presenti il loro valore rimarra
		// a null e verra settato come tale nel DB
		String nomeProperty = null;
		String valoreProperty = null;

		boolean isAbilitato = false;
		
		nomeConnettore = connettore.getNome();
		endpointtype = connettore.getTipo();

		if (endpointtype == null || endpointtype.trim().equals(""))
			endpointtype = TipiConnettore.DISABILITATO.getNome();

		Map<String, String> extendedProperties = new HashMap<>();
		
		List<String> propertiesGestiteAttraversoColonneAdHoc = new ArrayList<>();
		
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
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_USER))
					utente = valoreProperty;
				else if (nomeProperty.equals(CostantiDB.CONNETTORE_PWD))
					password = valoreProperty;
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
			switch (type) {
			case 1:

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
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
				sqlQueryObject.addInsertField("enc_password", "?");
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
				
				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore CREATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, 
								transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
								nome, tipo, utente, plainPassword, encPassword, 
								initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug, 
								proxy, proxyType, proxyHostname, proxyPort, proxyUsername, plainProxyPassword, encProxyPassword,
								tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaAvgResponseTime,
								(connettore.getCustom()!=null && connettore.getCustom()),
								tokenPolicy,
								apiKeyInsert, apiKeyHeader, appId, appIdHeader));
				int n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				ResultSet rs;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_connettore = ?");
				sqlQueryObject.addWhereCondition("endpointtype = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = connection.prepareStatement(sqlQuery);

				stm.setString(1, nomeConnettore);
				stm.setString(2, endpointtype);

				DriverRegistroServiziDB_LIB.logDebug("Recupero idConnettore inserito : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeConnettore, endpointtype));

				rs = stm.executeQuery();

				if (rs.next()) {
					idConnettore = rs.getLong("id");
				} else {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Errore tentanto di effettuare la select dopo una create, non riesco a recuperare l'id!");
				}

				rs.close();
				stm.close();
								
				
				// Custom properties
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("enc_value", "?");
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
							throw new DriverRegistroServiziException("Property ["+nomeProperty+"] without value");
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
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("enc_value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new DriverRegistroServiziException("Property ["+nomeP+"] without value");
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
						stm.setString(1, nome);
						stm.setString(2, plainValue);
						stm.setString(3, encValue);
						stm.setLong(4, idConnettore);
						stm.executeUpdate();
						stm.close();
					}				
				}
				
				break;

			case 2:
				// update

				idConnettore = connettore.getId();

				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di update.");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
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
				sqlQueryObject.addUpdateField("enc_password", "?");
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
				stm.setString(index++, isAbilitato ? nome : null);
				stm.setString(index++, isAbilitato ? tipo : null);
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

				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore UPDATE : \n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, endpointtype, url, 
								transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
								nome, tipo, utente, plainPassword, encPassword, 
								initcont, urlpkg, provurl, connectionfactory, sendas, nomeConnettore, debug,
								proxy, proxyType, proxyHostname, proxyPort, proxyUsername, plainProxyPassword, encProxyPassword,
								tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaAvgResponseTime,
								(connettore.getCustom()!=null && connettore.getCustom()),
								tokenPolicy,
								apiKeyInsert, apiKeyHeader, appId, appIdHeader,
								idConnettore));
				n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				
				// Custom properties
				
				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				// Aggiungo attuali
				if(connettore.getCustom()!=null && connettore.getCustom()){					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("enc_value", "?");
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
							throw new DriverRegistroServiziException("Property ["+nomeProperty+"] without value");
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
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.CONNETTORI_CUSTOM);
					sqlQueryObject.addInsertField("name", "?");
					sqlQueryObject.addInsertField("value", "?");
					sqlQueryObject.addInsertField("enc_value", "?");
					sqlQueryObject.addInsertField("id_connettore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					
					for (String nomeP : extendedProperties.keySet()) {
						valoreProperty = extendedProperties.get(nomeP);
						if (valoreProperty != null && valoreProperty.equals(""))
							valoreProperty = null;
					
						if(valoreProperty==null){
							throw new DriverRegistroServiziException("Property ["+nomeP+"] without value");
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

			case 3:
				// delete
				idConnettore = connettore.getId();

				if (idConnettore <= 0)
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] L'id del connettore non puo essere 0 tentando di fare una operazione di delete.");

				// Delete eventuali vecchie properties
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
				sqlQueryObject.addWhereCondition("id_connettore=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				stm.executeUpdate();
				stm.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverRegistroServiziDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = connection.prepareStatement(sqlQuery);
				stm.setLong(1, idConnettore);
				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore DELETE : \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idConnettore));
				n = stm.executeUpdate();
				DriverRegistroServiziDB_LIB.logDebug("CRUDConnettore type = " + type + " row affected =" + n);
				stm.close();
				
				break;
			}

			// ritorno l id del connettore questo e' utile in caso di create
			connettore.setId(idConnettore);
			return idConnettore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] SQLException : " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB_LIB::CRUDConnettore] Exception : " + se.getMessage(),se);
		}finally {
			JDBCUtilities.closeResources(stm);
		}
	}
	

}
