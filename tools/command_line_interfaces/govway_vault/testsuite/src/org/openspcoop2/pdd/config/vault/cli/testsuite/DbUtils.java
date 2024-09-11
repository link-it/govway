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

package org.openspcoop2.pdd.config.vault.cli.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
* DbUtils
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private static final String UNCORRECT_TYPE = "Uncorrect return type '";
    private static final String AND_CONDITION=" AND ";
    private static final String SELECT_ID_FROM = "select id from ";
    private static final String WHERE = " WHERE ";
    private static final String SELECT= "select ";
    private static final String FROM= " from ";
    
    public final JdbcTemplate jdbc;
    
    public final TipiDatabase tipoDatabase;

    public DbUtils(Map<String, String> config) {
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");
        String driver = config.get("driverClassName");
        String type = config.get("dbType");
        if(type!=null) {
        	this.tipoDatabase = TipiDatabase.toEnumConstant(type );
        }
        else {
        	this.tipoDatabase = TipiDatabase.DEFAULT;
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbc = new JdbcTemplate(dataSource);
        logger.info("init jdbc template: {}", url);
    }

    public Object readValue(String query) {
        try {
        	return this.jdbc.queryForObject(query, Object.class);
        }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		return this.jdbc.queryForObject(query, Object.class);
    	}
    }
    public <T> T readValue(String query, Class<T> classObject) {
    	 try {
         	return this.jdbc.queryForObject(query, classObject);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject);
     	}
    }
    public Object readValue(String query, Object... args) {
    	 try {
         	 return this.jdbc.queryForObject(query, Object.class, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, Object.class, args);
     	}
    }
    public <T> T readValue(String query, Class<T> classObject, Object... args) {
    	 try {
         	 return this.jdbc.queryForObject(query, classObject, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject, args);
     	}
    }
    
    public <T> T readValueArray(String query, Class<T> classObject, Object[] args) {
    	 try {
         	return this.jdbc.queryForObject(query, classObject, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject, args);
     	}
    }
    
    

    public Map<String, Object> readRow(String query) {
    	Map<String, Object> mapReaded = null;
    	try {
    		mapReaded = this.jdbc.queryForMap(query);
    	}catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		mapReaded = this.jdbc.queryForMap(query);
    	}
    	return this.formatResult(mapReaded);
    }

    public List<Map<String, Object>> readRows(String query, Object... args) {
    	List<Map<String, Object>> listReaded = null;
    	try {
    		listReaded = this.jdbc.queryForList(query, args);
    	}catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		listReaded = this.jdbc.queryForList(query, args);
    	}
    	if(!listReaded.isEmpty()) {
    		List<Map<String, Object>> list = new ArrayList<>();
    		for (Map<String, Object> mapReaded : listReaded) {
				list.add(this.formatResult(mapReaded));
			}
    		return list;
    	}
    	else {
    		listReaded = null;
    	}
    	return listReaded;
    }

    private Map<String, Object> formatResult(Map<String, Object> mapReaded){
    	Map<String, Object> map = null;
    	if(mapReaded!=null && !mapReaded.isEmpty()) {
    		map = new HashMap<>();
    		for (Map.Entry<String,Object> entry : mapReaded.entrySet()) {
    			String colonna= entry.getKey();
				Object value = mapReaded.get(colonna);
				if(value instanceof java.math.BigDecimal) {
					java.math.BigDecimal bd = (java.math.BigDecimal) value;
					map.put(colonna.toLowerCase(), bd.intValue());
				}
				else {
					map.put(colonna.toLowerCase(), value);
				}
			}
    	}
		return map;
    }
    
    public int update(String query) {
        return this.jdbc.update(query);
    }
    
    
    public String getServiziApplicativiNome(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("nome", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getServiziApplicativiPasswordInv(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    public String getServiziApplicativiEncPasswordInv(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getServiziApplicativiNome(String nomePortaDefault, String azione) throws UtilsException {
    	return getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    }
    public String getServiziApplicativiPasswordInv(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	return getColumnValue("passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    }
    public String getServiziApplicativiEncPasswordInv(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	return getColumnValue("enc_passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    }
    
    public String getServiziApplicativiPasswordRisp(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("passwordrisp", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    public String getServiziApplicativiEncPasswordRisp(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_passwordrisp", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getConnettorePassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    public String getConnettoreEncPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreProxyPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("proxy_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    public String getConnettoreEncProxyPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_proxy_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreApiKey(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("api_key", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreCustomValue(String nomeConnettore, String nomeProprieta) throws UtilsException {
    	Long l = getColumnLongValue("id", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, nomeConnettore);
    	long idConnettore = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById("value", CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, idConnettore,
    			"name", nomeProprieta);
    }
    public String getConnettoreCustomEncValue(String nomeConnettore, String nomeProprieta) throws UtilsException {
    	Long l = getColumnLongValue("id", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, nomeConnettore);
    	long idConnettore = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById("enc_value", CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, idConnettore,
    			"name", nomeProprieta);
    }
    
    public String getNomeConnettoreByPortaApplicativa(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	Long l = getColumnLongValue(CostantiDB.CONNETTORI_COLUMN_ID_CONNETTORE_INV, CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    	long idConnettore = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.CONNETTORI_COLUMN_NOME, CostantiDB.CONNETTORI, "id", idConnettore);
    }
    
    public String getConfigPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_VALUE, CostantiDB.SYSTEM_PROPERTIES_PDD, CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_NOME, propertyName);
    }
    public String getConfigPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_ENC_VALUE, CostantiDB.SYSTEM_PROPERTIES_PDD, CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_NOME, propertyName);
    }
    
    public String getSoggettoPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SOGGETTI_PROPS_COLUMN_VALUE, CostantiDB.SOGGETTI_PROPS, CostantiDB.SOGGETTI_PROPS_COLUMN_NAME, propertyName);
    }
    public String getSoggettoPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SOGGETTI_PROPS_COLUMN_ENC_VALUE, CostantiDB.SOGGETTI_PROPS, CostantiDB.SOGGETTI_PROPS_COLUMN_NAME, propertyName);
    }
    
    public String getApplicativoPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_VALUE, CostantiDB.SERVIZI_APPLICATIVI_PROPS, CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME, propertyName);
    }
    public String getApplicativoPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_ENC_VALUE, CostantiDB.SERVIZI_APPLICATIVI_PROPS, CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME, propertyName);
    }
    
    public String getPortaDelegataPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_PROP, CostantiDB.PORTE_DELEGATE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaDelegataPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_PROP, CostantiDB.PORTE_DELEGATE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaDelegataAuthnPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaDelegataAuthnPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaDelegataAuthzPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaDelegataAuthzPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaDelegataAuthcPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaDelegataAuthcPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaApplicativaPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_PROP, CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaApplicativaPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_PROP, CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaApplicativaAuthnPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaApplicativaAuthnPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaApplicativaAuthzPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaApplicativaAuthzPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getPortaApplicativaAuthcPropertyValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, propertyName);
    }
    public String getPortaApplicativaAuthcPropertyEncValue(String propertyName) throws UtilsException {
    	return getColumnValue(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, propertyName);
    }
    
    public String getProtocolPropertyPropertyServizioStringValue(IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdServizio(idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public String getProtocolPropertyPropertyServizioStringEncValue(IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdServizio(idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public byte[] getProtocolPropertyPropertyServizioBinaryValue(IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdServizio(idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnBinaryValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.ACCORDO_SERVIZIO_PARTE_SPECIFICA.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    
    public String getProtocolPropertyPropertyFruitoreStringValue(IDSoggetto fruitore, IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdFruizione(fruitore, idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.FRUITORE.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public String getProtocolPropertyPropertyFruitoreStringEncValue(IDSoggetto fruitore, IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdFruizione(fruitore, idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.FRUITORE.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public byte[] getProtocolPropertyPropertyFruitoreBinaryValue(IDSoggetto fruitore, IDServizio idServizio, String propertyName) throws UtilsException {
    	Long l = getIdFruizione(fruitore, idServizio);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnBinaryValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.FRUITORE.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    
    public String getProtocolPropertyPropertyApplicativoStringValue(IDServizioApplicativo idServizioApplicativo, String propertyName) throws UtilsException {
    	Long l = getIdServizioApplicativo(idServizioApplicativo);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public String getProtocolPropertyPropertyApplicativoStringEncValue(IDServizioApplicativo idServizioApplicativo, String propertyName) throws UtilsException {
    	Long l = getIdServizioApplicativo(idServizioApplicativo);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    public byte[] getProtocolPropertyPropertyApplicativoBinaryValue(IDServizioApplicativo idServizioApplicativo, String propertyName) throws UtilsException {
    	Long l = getIdServizioApplicativo(idServizioApplicativo);
    	long idProprietario = (l!=null) ? l.longValue() : -1;
    	return getColumnBinaryValueById(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, CostantiDB.PROTOCOL_PROPERTIES, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO, idProprietario, 
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO.name(),
    			CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME, propertyName);
    }
    
    public List<String> getTokenPolicyValidazioneValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, nomeProprieta, CostantiProprieta.TOKEN_VALIDATION_ID, nomePolicy);
    }
    public List<String> getTokenPolicyValidazioneEncValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ENC_VALUE, nomeProprieta, CostantiProprieta.TOKEN_VALIDATION_ID, nomePolicy);
    }
    
    public List<String> getTokenPolicyNegoziazioneValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, nomeProprieta, CostantiProprieta.TOKEN_NEGOZIAZIONE_ID, nomePolicy);
    }
    public List<String> getTokenPolicyNegoziazioneEncValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ENC_VALUE, nomeProprieta, CostantiProprieta.TOKEN_NEGOZIAZIONE_ID, nomePolicy);
    }
    
    public List<String> getAttributeAuthorityValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, nomeProprieta, CostantiProprieta.ATTRIBUTE_AUTHORITY_ID, nomePolicy);
    }
    public List<String> getAttributeAuthorityEncValue(String nomeProprieta, String nomePolicy) throws UtilsException {
    	return getGenericPropertiesValue(CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ENC_VALUE, nomeProprieta, CostantiProprieta.ATTRIBUTE_AUTHORITY_ID, nomePolicy);
    }
    
    private List<String> getGenericPropertiesValue(String nomeColonna, String nomeProprieta, String tipo, String nome) throws UtilsException {
    	String query = SELECT+nomeColonna+FROM+CostantiDB.CONFIG_GENERIC_PROPERTY+" where "+
    			"  ("+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME+"='"+nomeProprieta+"' OR "+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME+" LIKE '%"+nomeProprieta+"') "+
    			"AND "+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS+" in (select id from "+CostantiDB.CONFIG_GENERIC_PROPERTIES+" where tipo='"+tipo+"' AND nome='"+nome+"')";
    	logger.info(query);
    	List<Map<String, Object>> list = readRows(query);
    	List<String> result = null;
    	if(list==null || list.isEmpty()) {
    		return result;
    	}
    	result = new ArrayList<>();
    	for (Map<String, Object> map : list) {
			Object oId = map.get(nomeColonna);
			if(oId instanceof String) {
				result.add((String) oId);
	    	}
			else if(oId==null) {
	    		result.add(null);
	    	}
			else {
				throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
			}
		} 
    	return result;
    }
    
    public List<String> getMessageSecurityPortaDelegataRequestValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, 
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    public List<String> getMessageSecurityPortaDelegataRequestEncValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, 
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    
    public List<String> getMessageSecurityPortaDelegataResponseValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, 
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    public List<String> getMessageSecurityPortaDelegataResponseEncValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, 
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    
    public List<String> getMessageSecurityPortaApplicativaRequestValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, 
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    public List<String> getMessageSecurityPortaApplicativaRequestEncValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, 
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    
    public List<String> getMessageSecurityPortaApplicativaResponseValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, 
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    public List<String> getMessageSecurityPortaApplicativaResponseEncValue(String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	return getMessageSecurityValue(CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, 
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
    			nomeProprieta, nomePortaDefault, nomeAzione);
    }
    
    private List<String> getMessageSecurityValue(String tabellaPadre, String tabella, 
    		String colonnaValue, String colonnaNome, String nomeProprieta, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	
    	String nomePorta = nomeAzione==null ? nomePortaDefault : getNomePorta(tabellaPadre, nomePortaDefault, nomeAzione);
    	
    	String query = SELECT+colonnaValue+FROM+tabella+" where "+
    			"  ("+colonnaNome+"='"+nomeProprieta+"' OR "+colonnaNome+" LIKE '%"+nomeProprieta+"') "+
    			"AND id_porta in (select id from "+tabellaPadre+" where nome_porta='"+nomePorta+"')";
    	logger.info(query);
    	List<Map<String, Object>> list = readRows(query);
    	List<String> result = null;
    	if(list==null || list.isEmpty()) {
    		return result;
    	}
    	result = new ArrayList<>();
    	for (Map<String, Object> map : list) {
			Object oId = map.get(colonnaValue);
			if(oId instanceof String) {
				result.add((String) oId);
	    	}
			else if(oId==null) {
	    		result.add(null);
	    	}
			else {
				throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
			}
		} 
    	return result;
    }
    public String getNomePorta(String tabella, String nomePortaDefault, String nomeAzione) throws UtilsException {
    	
    	String tabellaAzioni = CostantiDB.PORTE_DELEGATE.equals(tabella) ? CostantiDB.PORTE_DELEGATE_AZIONI : CostantiDB.PORTE_APPLICATIVE_AZIONI;
    	
    	String query = "select nome_porta from "+tabella+" p, "+tabellaAzioni+" a where a.id_porta=p.id AND "+
    			"a.azione='"+nomeAzione+"' AND p.nome_porta LIKE '__"+nomePortaDefault+"__Specific%'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("nome_porta");
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		throw new UtilsException("Nome porta non individuata per portaDefault '"+nomePortaDefault+"' e azione '"+nomeAzione+"'");
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    
    private String getColumnValue(String colonna, String tabella, String colonnaId, String colonnaIdValue) throws UtilsException {
    	return getColumnValue(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private String getColumnValue(String colonna, String tabella, String colonnaId, String colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    protected String getColumnValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue) throws UtilsException {
    	return getColumnValueById(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private String getColumnValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	return getColumnValueById(colonna, tabella, colonnaId, colonnaIdValue, 
        		colonnaId2, colonnaIdValue2,
        		null, null);
    }
    private String getColumnValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2,
    		String colonnaId3, String colonnaIdValue3) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue, 
        		colonnaId2, colonnaIdValue2,
        		colonnaId3, colonnaIdValue3);
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    private byte[] getColumnBinaryValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2,
    		String colonnaId3, String colonnaIdValue3) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue, 
        		colonnaId2, colonnaIdValue2,
        		colonnaId3, colonnaIdValue3);
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	byte[] res = null;
    	if(oId instanceof byte[]) {
    		return (byte[]) oId;
    	}
    	if(oId==null) {
    		return res;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private Long getColumnLongValue(String colonna, String tabella, String colonnaId, String colonnaIdValue) throws UtilsException {
    	return getColumnLongValue(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private Long getColumnLongValue(String colonna, String tabella, String colonnaId, String colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId instanceof Integer) {
    		// oracle
    		return ((Integer) oId).longValue();
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
        
    private String getServizioApplicativoAssociatoPorta(String nomePortaDefault, String nomeAzione) throws UtilsException {
    	String query = "select nome from "+CostantiDB.SERVIZI_APPLICATIVI+" where id=(select id_servizio_applicativo from porte_applicative_sa where id_porta=(select pa.id from porte_applicative pa,pa_azioni az where az.id_porta=pa.id AND "+
    			"az.azione='"+nomeAzione+"' AND pa.nome_porta LIKE '__"+nomePortaDefault+"__Specific%'))";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("nome");
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private Long getIdSoggetto(IDSoggetto idSoggetto) throws UtilsException {
    	String query = SELECT_ID_FROM+CostantiDB.SOGGETTI+WHERE+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"='"+idSoggetto.getTipo()+"'"+
    			AND_CONDITION+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+"='"+idSoggetto.getNome()+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId instanceof Integer) {
    		// oracle
    		return ((Integer) oId).longValue();
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    private Long getIdServizio(IDServizio idServizio) throws UtilsException {
    	String query = SELECT_ID_FROM+CostantiDB.SERVIZI+WHERE+CostantiDB.SERVIZI_COLUMN_TIPO_SERVIZIO+"='"+idServizio.getTipo()+"'"+
    			AND_CONDITION+CostantiDB.SERVIZI_COLUMN_NOME_SERVIZIO+"='"+idServizio.getNome()+"'"+
    			AND_CONDITION+CostantiDB.SERVIZI_COLUMN_VERSIONE_SERVIZIO+"="+idServizio.getVersione()+""+
    			AND_CONDITION+CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF+"='"+getIdSoggetto(idServizio.getSoggettoErogatore())+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId instanceof Integer) {
    		// oracle
    		return ((Integer) oId).longValue();
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    private Long getIdFruizione(IDSoggetto idSoggetto, IDServizio idServizio) throws UtilsException {
    	String query = SELECT_ID_FROM+CostantiDB.SERVIZI_FRUITORI+WHERE+
    			CostantiDB.SERVIZI_FRUITORI_ID_SERVIZIO_REF+"='"+getIdServizio(idServizio)+"'"+
    			AND_CONDITION+CostantiDB.SERVIZI_FRUITORI_ID_SOGGETTO_REF+"='"+getIdSoggetto(idSoggetto)+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId instanceof Integer) {
    		// oracle
    		return ((Integer) oId).longValue();
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    private Long getIdServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws UtilsException {
    	String query = SELECT_ID_FROM+CostantiDB.SERVIZI_APPLICATIVI+WHERE+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME+"='"+idServizioApplicativo.getNome()+"'"+
    			AND_CONDITION+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"='"+getIdSoggetto(idServizioApplicativo.getIdSoggettoProprietario())+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId instanceof Integer) {
    		// oracle
    		return ((Integer) oId).longValue();
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private String getQueryBase(String colonna, String tabella, String colonnaId, Object colonnaIdValue) {
    	String q = SELECT+colonna+FROM+tabella+WHERE+colonnaId;
    	if(colonnaIdValue instanceof String) {
    		q+="='"+colonnaIdValue+"'";
    	}
    	else {
    		q+="="+colonnaIdValue;
    	}
    	return q;
    }
    private String getQueryBase(String colonna, String tabella, String colonnaId, long colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2,
    		String colonnaId3, String colonnaIdValue3) {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	if(colonnaId3!=null && colonnaIdValue3!=null) {
    		query+=AND_CONDITION+colonnaId3+"='"+colonnaIdValue3+"'";
    	}
    	return query;
    }
     
    public void updateEncSystemProperty(String nome,String plainV,String encV) {
    	deleteEncSystemProperties(nome);
    	updateEncSystemProperties(nome,plainV,encV);
    }
    private void deleteEncSystemProperties(String nome) {
    	String delete = "delete from "+CostantiDB.SYSTEM_PROPERTIES_PDD+WHERE+CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_NOME+"='"+nome+"'";
    	logger.info(delete);
    	int r = this.update(delete);
    	String m = "rows-delete:"+r;
    	logger.info(m);
    }
    private void updateEncSystemProperties(String nome,String plainV,String encV) {
    	String insert = "insert into "+CostantiDB.SYSTEM_PROPERTIES_PDD+" ("+CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_NOME+","+CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_VALUE+","+CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_ENC_VALUE+") VALUES "+
    				" ('"+nome+"','"+plainV+"','"+encV+"')";
    	logger.info(insert);
    	int r = this.update(insert);
    	String m = "rows-insert:"+r;
    	logger.info(m);
    }
    

    public void updateByokPolicyPorteMessageSecurity(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicyPorteDelegateMessageSecurity(oldValue, name, securityPolicy);
    	updateByokPolicyPorteApplicativeMessageSecurity(oldValue, name, securityPolicy);
    }
    public void updateByokPolicyPorteDelegateMessageSecurity(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME);
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME);
    }
    public void updateByokPolicyPorteApplicativeMessageSecurity(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME);
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME);
    }
    public void updateByokPolicyConnettoriCustom(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME);
    }
    public void updateByokPolicyGenericProperties(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME);
    }
    public void updateByokPolicyProtocolProperties(String oldValue, String name,String securityPolicy) throws UtilsException {
    	updateByokPolicy(oldValue, name, securityPolicy,
    			CostantiDB.PROTOCOL_PROPERTIES, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME);
    }
    
    public void updateByokPolicy(String oldValue, String name,String securityPolicy,
    		String table, String columnValue, String columnName) throws UtilsException {
    	String update = "update "+table+" set "+columnValue+" = '"+securityPolicy+"' "+
    			"WHERE "+columnName+"='"+name+"' AND ";
    	/**update = update + columnValue+"='"+oldValue+"'";*/
    	// campo clob, non gestito tramite condizione uguale su oracle
    	try {
    		ISQLQueryObject sqlQuery = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
    		update = update + sqlQuery.getWhereLikeCondition(columnValue, oldValue, true);
    	}catch(Exception e) {
    		throw new UtilsException(e.getMessage(),e);
    	}
    	
    	logger.info(update);
    	int r = this.update(update);
    	String m = "rows-updated:"+r;
    	logger.info(m);
    }
}
