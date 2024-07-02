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
package org.openspcoop2.pdd.config.vault.cli;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.byok.DriverBYOK;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
* VaultUpdateConfigUtilities
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultUpdateConfigUtilities {
	
	private VaultUpdateConfig config;
	
	private DriverBYOK inDriverBYOK = null;
	private DriverBYOK outDriverBYOK = null;
	
	private String tipoDatabase;
	private String driver;
	private String username;
	private String password;
	private String connectionURL;
	
	private static final String LOG_ORIG_SKIPPED = "\t!skyp! wrapped with other policy:";
	private static final String LOG_ORIG_SKIPPED_NO_WRAPPED = "\t!skyp! no wrapped value:";
	private static final String LOG_ORIG_SKIPPED_ALREADY_WRAPPED = "\t!skyp! already wrapped value:";
	private static final String LOG_ORIG_SKIPPED_WITH_POLICY = "\t!skyp! wrapped with policy:";
	private static final String LOG_ORIG = "\torig:";
	private static final String LOG_NEW = "\tnew:";
	private static final String LOG_ROW_UPDATE = "\trow-update:";
	
	private static final String CONDITION_WHERE_ID = "id = ?";
	private static final String CONDITION_JOIN_ID = ".id = ";
	
	private static final String ALIAS_PLAIN_VALUE = "plainValue";
	private static final String ALIAS_ENC_VALUE = "encValue";
	
	public VaultUpdateConfigUtilities(VaultUpdateConfig config) {
		this.config = config;
	}

	public void process() throws CoreException {

		try {
		
			VaultTools.logCoreDebug("Inizializzazione connessione database in corso...");
			
			VaultDatabaseProperties databaseProperties = VaultDatabaseProperties.getInstance();
			this.tipoDatabase = databaseProperties.getTipoDatabase();
			this.driver = databaseProperties.getDriver();
			this.username = databaseProperties.getUsername();
			this.password = databaseProperties.getPassword();
			this.connectionURL = databaseProperties.getConnectionUrl();
	
			VaultTools.logCoreDebug("Inizializzazione connessione database terminata");
			
			
			
			VaultTools.logCoreDebug("Inizializzazione driver ...");
			
			if(this.config.isInSecurityMode()) {
				this.inDriverBYOK = new DriverBYOK(VaultTools.getLogCore(), this.config.getInId(), this.config.getInId());
			}
			if(this.config.isOutSecurityMode()) {
				this.outDriverBYOK = new DriverBYOK(VaultTools.getLogCore(), this.config.getOutId(), this.config.getOutId());
			}

			VaultTools.logCoreDebug("Inizializzazione driver terminata");
			
			StringBuilder output = null;
			if(this.config.getReportPath()!=null) {
				output = new StringBuilder();
			}
			
			processConnettori(output);
			
			processServiziApplicativi(output);
			
			processSecurity(output);
			
			processGenericProperties(output);
			
			processProtocolProperties(output);
			
			processProperties(output); // lasciare in fondo in modo da non gestire nuovamente le proprietà di sicurezza
			
			if(this.config.getReportPath()!=null && output!=null) {
				FileSystemUtilities.writeFile(this.config.getReportPath(), output.toString().getBytes());
			}
			
		}
		catch(Exception t) {
			VaultTools.logCoreError(t.getMessage(),t);
			throw new CoreException(t.getMessage(),t);
		}

	}
	private void processConnettori(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione connettori ...");
		
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== Connettori ===\n\n");
		}
				
		updateConnettori("password", "enc_password", output);
		updateConnettori("proxy_password", "enc_proxy_password", output);
		updateConnettori("api_key", null, output);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Connettori Custom ===\n\n");
		}
		
		for (String nomeProprieta : CostantiConnettori.getConfidentials()) {
			updateConnettoriCustom(nomeProprieta, output);
		}
		
		VaultTools.logCoreDebug("Conversione connettori terminata");
	}
	private void processServiziApplicativi(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione applicativi ...");
		
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== Applicativi ===\n\n");
		}
				
		updateServiziApplicativi("passwordrisp", "enc_passwordrisp", output);
		updateServiziApplicativi("passwordinv", "enc_passwordinv", output);
		
		VaultTools.logCoreDebug("Conversione applicativi terminata");
	}
	private void processSecurity(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione message security ...");
		
		List<String> messageSecurityIds = CostantiProprieta.getMessageSecurityIds();
		
		processSecurityFruizioni(messageSecurityIds, output);
		processSecurityErogazioni(messageSecurityIds, output);
		
		VaultTools.logCoreDebug("Conversione message security terminata");
	}
	private void processSecurityFruizioni(List<String> messageSecurityIds, StringBuilder output) throws CoreException {
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== Message Security (Fruizioni request-flow) ===\n\n");
		}
		
		processSecurity(
				messageSecurityIds, output,
				CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, 
				CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE);
				
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Message Security (Fruizioni response-flow) ===\n\n");
		}
		
		processSecurity(
				messageSecurityIds, output,
				CostantiDB.PORTE_DELEGATE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, 
				CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE);

	}
	private void processSecurityErogazioni(List<String> messageSecurityIds, StringBuilder output) throws CoreException {
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Message Security (Erogazioni request-flow) ===\n\n");
		}
		
		processSecurity(
				messageSecurityIds, output,
				CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, 
				CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Message Security (Erogazioni response-flow) ===\n\n");
		}
		
		processSecurity(
				messageSecurityIds, output,
				CostantiDB.PORTE_APPLICATIVE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, 
				CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE);
		
	}
	private void processSecurity(List<String> messageSecurityIds, StringBuilder output,
			String tabellaPadre, String tabella, 
			String nomeColonnaNomeProprieta, String nomeColonnaPlain, String nomeColonnaCodificata) throws CoreException {
		if(messageSecurityIds!=null && !messageSecurityIds.isEmpty()) {
			List<String> lnomiProp = new ArrayList<>();  
			for (String id : messageSecurityIds) {
				List<String> l =  CostantiProprieta.getMessageSecurityProperties(id);
				for (String nomeProprieta : l) {
					if(!lnomiProp.contains(nomeProprieta)) {
						lnomiProp.add(nomeProprieta);
					}
				}
			}
			
			for (String nomeProprieta : lnomiProp) {
				updateMessageSecurity(tabellaPadre, tabella, 
						nomeColonnaNomeProprieta, nomeColonnaPlain, nomeColonnaCodificata,
						nomeProprieta, output);
			}
		}
	}
	private void processGenericProperties(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione generic properties ...");
		
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== TokenPolicy Validazione ===\n\n");
		}
		
		for (String nomeProprieta : CostantiProprieta.getTokenValidationProperties()) {
			updateGenericProperties(CostantiProprieta.TOKEN_VALIDATION_ID, nomeProprieta, output);
		}
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== TokenPolicy Negoziazione ===\n\n");
		}
		
		for (String nomeProprieta : CostantiProprieta.getTokenRetrieveProperties()) {
			updateGenericProperties(CostantiProprieta.TOKEN_NEGOZIAZIONE_ID, nomeProprieta, output);
		}
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Attribute Authority ===\n\n");
		}
		
		for (String nomeProprieta : CostantiProprieta.getAttributeAuthorityProperties()) {
			updateGenericProperties(CostantiProprieta.ATTRIBUTE_AUTHORITY_ID, nomeProprieta, output);
		}
		
		VaultTools.logCoreDebug("Conversione generic properties terminata");
	}
	private void processProtocolProperties(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione protocol properties ...");
		
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== ProtocolProperties ===\n\n");
		}
		
		// DBProtocolPropertiesUtils.getProtocolPropertiesConfidentials()
		// Non uso questo metodo poichè viene inizializzato dalla ModI Factory e nel vault non viene usato alcun protocollo
		
		updateProtocolProperties(CostantiDB.MODIPA_KEYSTORE_PASSWORD, false, output);
		updateProtocolProperties(CostantiDB.MODIPA_KEY_PASSWORD, false, output);
		updateProtocolProperties(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, false, output);
		updateProtocolProperties(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD, false, output);
		
		updateProtocolProperties(CostantiDB.MODIPA_KEYSTORE_ARCHIVE, true, output);
		
		VaultTools.logCoreDebug("Conversione protocol properties terminata");
	}
	private void processProperties(StringBuilder output) throws CoreException {
		VaultTools.logCoreDebug("Conversione properties ...");
		
		if(output!=null) {
			if(output.length()>0) {
				output.append("\n\n");
			}
			output.append("=== Soggetti properties ===\n\n");
		}
				
		updateProperties(CostantiDB.SOGGETTI_PROPS, CostantiDB.SOGGETTI_PROPS_COLUMN_NAME, CostantiDB.SOGGETTI_PROPS_COLUMN_VALUE, CostantiDB.SOGGETTI_PROPS_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Applicativi properties ===\n\n");
		}
		
		updateProperties(CostantiDB.SERVIZI_APPLICATIVI_PROPS, CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME, CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_VALUE, CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Configurazione properties ===\n\n");
		}
		
		updateProperties(CostantiDB.SYSTEM_PROPERTIES_PDD, CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_NOME, CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_VALUE, CostantiDB.SYSTEM_PROPERTIES_PDD_COLUMN_ENC_VALUE, output,
				false, false);
		
		
		processErogazioniProperties(output);
		
		processFruizioniProperties(output);

		VaultTools.logCoreDebug("Conversione properties terminata");
	}
	private void processErogazioniProperties(StringBuilder output) throws CoreException {
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni message security request properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE, output,
				true, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni message security response properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE, output,
				false, true);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni autenticazione properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni autorizzazione properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni autorizzazione contenuti properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Erogazioni properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_APPLICATIVE_PROP, CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_VALORE, CostantiDB.PORTE_APPLICATIVE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
	}
	private void processFruizioniProperties(StringBuilder output) throws CoreException {
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni message security request properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_COLUMN_ENC_VALUE, output,
				true, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni message security response properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_COLUMN_ENC_VALUE, output,
				false, true);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni autenticazione properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni autorizzazione properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni autorizzazione contenuti properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP_COLUMN_ENC_VALUE, output,
				false, false);
		
		if(output!=null) {
			output.append("\n\n");
			output.append("=== Fruizioni properties ===\n\n");
		}
		
		updateProperties(CostantiDB.PORTE_DELEGATE_PROP, CostantiDB.PORTE_DELEGATE_PROP_COLUMN_NOME, 
				CostantiDB.PORTE_DELEGATE_PROP_COLUMN_VALORE, CostantiDB.PORTE_DELEGATE_PROP_COLUMN_ENC_VALUE, output,
				false, false);
	}
	
	
	
	
	// UTILS
	
	private Connection getConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
		Connection connectionSQL = null;
		org.openspcoop2.utils.resources.Loader.getInstance().newInstance(this.driver);
		if(this.username!=null && this.password!=null){
			connectionSQL = DriverManager.getConnection(this.connectionURL,this.username,this.password);
		}else{
			connectionSQL = DriverManager.getConnection(this.connectionURL);
		}
		return connectionSQL;
	}
	private void closeConnection(Connection connectionSQL) {
		if(connectionSQL!=null) {
			try {
				connectionSQL.close();
			}catch(Exception e) {
				// ignore
			}
		}
	}
	
	private void skip(StringBuilder output, String prefix, String value, boolean wrapped) {
		if(output!=null) {
			output.append(prefix).append("\n");
			output.append(wrapped ? LOG_ORIG_SKIPPED : LOG_ORIG_SKIPPED_NO_WRAPPED).append(value).append("\n");
		}
	}
	private void skipWithPolicy(StringBuilder output, String prefix, String value) {
		if(output!=null) {
			output.append(prefix).append("\n");
			output.append(LOG_ORIG_SKIPPED_WITH_POLICY).append(value).append("\n");
		}
	}
	private void skipAlreadyWrapped(StringBuilder output, String prefix, String value) {
		if(output!=null) {
			output.append(prefix).append("\n");
			output.append(LOG_ORIG_SKIPPED_ALREADY_WRAPPED).append(value).append("\n");
		}
	}
	
	private void updateValue(Connection connectionSQL, ResultSet rs, String nomeColonna, 
			String prefix, StringBuilder output,
			VaultUpdateConfigValue c) throws UtilsException, SQLException, SQLQueryObjectException {
		
		String plainStringValue = rs.getString(nomeColonna);
		String updatedValue = null;
		if(this.inDriverBYOK!=null && 
				plainStringValue!=null && StringUtils.isNotEmpty(plainStringValue)) {
			if(BYOKUtilities.isWrappedValue(plainStringValue)) {
				if(this.inDriverBYOK.isWrappedWithInternalPolicy(plainStringValue)) {
					updatedValue = this.inDriverBYOK.unwrapAsString(plainStringValue);
				}
				else {
					// trovato un valore cifrato con una security policy differente ?
					// non effettua il wrap anche se richiesto
					skip(output, prefix, plainStringValue, true);
					return;
				}
			}
			else {
				// trovato un valore non cifrato con una security policy
				// non effettua il wrap anche se richiesto
				skip(output, prefix, plainStringValue, false);
				return;
			}
		}
		else {
			if(plainStringValue!=null && StringUtils.isNotEmpty(plainStringValue) && BYOKUtilities.isWrappedValue(plainStringValue)) {
				// trovato un valore cifrato con una security policy, non devo quindi considerarlo
				skipWithPolicy(output, prefix, plainStringValue);
				return;
			}
		}
		
		if(this.outDriverBYOK!=null || updatedValue!=null) {
			updateValue(connectionSQL, output,
					plainStringValue, updatedValue, 
					c, prefix);
		}
		
	}
	private void updateValue(Connection connectionSQL, StringBuilder output,
			String plainStringValue, String updatedValue, 
			VaultUpdateConfigValue c, String prefix) throws SQLQueryObjectException, SQLException, UtilsException {
		
		if(this.outDriverBYOK!=null) {
			String v = updatedValue!=null ? updatedValue : plainStringValue;
			if(this.outDriverBYOK.isWrappedWithInternalPolicy(v)) {
				// Il valore di destinazione risulta già cifrato con la policy richiesta
				skipAlreadyWrapped(output, prefix, v);
				return;
			}
		}
		
		if(output!=null) {
			output.append(prefix).append("\n");
			output.append(LOG_ORIG).append(plainStringValue).append("\n");
		}
		
		updateValue(connectionSQL, output,
				plainStringValue, updatedValue, 
				c);
		
		if(output!=null) {
			output.append("\n\n");
		}
	}
	private void updateValue(Connection connectionSQL, StringBuilder output,
			String plainStringValue, String updatedValue, 
			VaultUpdateConfigValue updateConfig) throws SQLQueryObjectException, SQLException, UtilsException {
		
		PreparedStatement stmUpdate = null;
		try {
			BYOKWrappedValue wrappedValue =null;
			if(this.outDriverBYOK!=null) {
				wrappedValue = this.outDriverBYOK.wrap(updatedValue!=null ? updatedValue : plainStringValue);
			}	
			
			ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObjectUpdate.addUpdateTable(updateConfig.tabella);
			sqlQueryObjectUpdate.addUpdateField(updateConfig.nomeColonna, "?");
			sqlQueryObjectUpdate.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObjectUpdate.setANDLogicOperator(true);
			String sqlUpdate = sqlQueryObjectUpdate.createSQLUpdate();
			stmUpdate = connectionSQL.prepareStatement(sqlUpdate);
			int index = 1;
			if(wrappedValue!=null) {
				stmUpdate.setString(index++, wrappedValue.getWrappedValue());
				if(output!=null) {
					output.append(LOG_NEW).append(wrappedValue.getWrappedValue()).append("\n");
				}
			}
			else {
				stmUpdate.setString(index++, updatedValue);
				if(output!=null) {
					output.append(LOG_NEW).append(updatedValue).append("\n");
				}
			}
			stmUpdate.setLong(index++, updateConfig.id);
			int row = stmUpdate.executeUpdate();
			if(output!=null) {
				output.append(LOG_ROW_UPDATE).append(row).append("\n");
			}
			stmUpdate.close();
		} finally {
			JDBCUtilities.closeResources(stmUpdate);
		}
	}
	
	
	private void updatePlainEncValue(Connection connectionSQL, ResultSet rs, String nomeColonnaPlain, String nomeColonnaCodificata, 
			String prefix, StringBuilder output,
			VaultUpdateConfigPlainEnc c) throws UtilsException, SQLException, SQLQueryObjectException {
		
		String plainStringValue = rs.getString(nomeColonnaPlain);
		String encStringValue = rs.getString(nomeColonnaCodificata);
		String updatedValue = null;
		if(this.inDriverBYOK!=null) {
			if(encStringValue!=null && StringUtils.isNotEmpty(encStringValue)) {
				if(this.inDriverBYOK.isWrappedWithInternalPolicy(encStringValue)) {
					updatedValue = this.inDriverBYOK.unwrapAsString(encStringValue);
				}
				else {
					// trovato un valore cifrato con una security policy differente ?
					// non effettua il wrap anche se richiesto
					skip(output, prefix, encStringValue, true);
					return;
				}
			}
			else {
				// trovato un valore non cifrato con una security policy
				// non effettua il wrap anche se richiesto
				skip(output, prefix, plainStringValue, false);
				return;
			}
		}
		else {
			if(encStringValue!=null && StringUtils.isNotEmpty(encStringValue)) {
				// trovato un valore cifrato con una security policy, non devo quindi considerarlo
				skipWithPolicy(output, prefix, encStringValue);
				return;
			}
		}
		
		if(this.outDriverBYOK!=null || updatedValue!=null) {
			updatePlainEncValue(connectionSQL, output,
					plainStringValue, encStringValue, updatedValue, 
					c, prefix);
		}
		
	}
	private void updatePlainEncValue(Connection connectionSQL, StringBuilder output,
			String plainStringValue, String encStringValue, String updatedValue, 
			VaultUpdateConfigPlainEnc c, String prefix) throws SQLQueryObjectException, SQLException, UtilsException {
		
		if(this.outDriverBYOK!=null) {
			String v = updatedValue!=null ? updatedValue : plainStringValue;
			if(this.outDriverBYOK.isWrappedWithInternalPolicy(v)) {
				// Il valore di destinazione risulta già cifrato con la policy richiesta
				skipAlreadyWrapped(output, prefix, v);
				return;
			}
		}
		
		if(output!=null) {
			output.append(prefix).append("\n");
			output.append(LOG_ORIG).append(encStringValue!=null && StringUtils.isNotEmpty(encStringValue) ? encStringValue : plainStringValue).append("\n");
		}
		
		updatePlainEncValue(connectionSQL, output,
				plainStringValue, updatedValue, 
				c);
		
		if(output!=null) {
			output.append("\n\n");
		}
	}
	private void updatePlainEncValue(Connection connectionSQL, StringBuilder output,
			String plainStringValue, String updatedValue, 
			VaultUpdateConfigPlainEnc plainEncConfig) throws SQLQueryObjectException, SQLException, UtilsException {
		
		PreparedStatement stmUpdate = null;
		try {
			BYOKWrappedValue wrappedValue =null;
			if(this.outDriverBYOK!=null) {
				wrappedValue = this.outDriverBYOK.wrap(updatedValue!=null ? updatedValue : plainStringValue);
			}	
			
			ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObjectUpdate.addUpdateTable(plainEncConfig.tabella);
			sqlQueryObjectUpdate.addUpdateField(plainEncConfig.nomeColonnaPlain, "?");
			sqlQueryObjectUpdate.addUpdateField(plainEncConfig.nomeColonnaCodificata, "?");
			sqlQueryObjectUpdate.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObjectUpdate.setANDLogicOperator(true);
			String sqlUpdate = sqlQueryObjectUpdate.createSQLUpdate();
			stmUpdate = connectionSQL.prepareStatement(sqlUpdate);
			int index = 1;
			if(wrappedValue!=null) {
				stmUpdate.setString(index++, wrappedValue.getWrappedPlainValue());
				stmUpdate.setString(index++, wrappedValue.getWrappedValue());
				if(output!=null) {
					output.append(LOG_NEW).append(wrappedValue.getWrappedValue()).append("\n");
				}
			}
			else {
				stmUpdate.setString(index++, updatedValue);
				stmUpdate.setString(index++, null);
				if(output!=null) {
					output.append(LOG_NEW).append(updatedValue).append("\n");
				}
			}
			stmUpdate.setLong(index++, plainEncConfig.id);
			int row = stmUpdate.executeUpdate();
			if(output!=null) {
				output.append(LOG_ROW_UPDATE).append(row).append("\n");
			}
			stmUpdate.close();
		} finally {
			JDBCUtilities.closeResources(stmUpdate);
		}
	}

	
	private void updateBinaryProtocolProperty(IJDBCAdapter jdbcAdapter, Connection connectionSQL, ResultSet rs, String nomeColonna, 
			String prefix, StringBuilder output,
			VaultUpdateConfigValue c) throws UtilsException, SQLException, SQLQueryObjectException {

		byte[]binaryValue = jdbcAdapter.getBinaryData(rs,nomeColonna);
		byte[]updateValue = null;
		if(this.inDriverBYOK!=null &&
			binaryValue!=null && binaryValue.length>0) {
			if(BYOKUtilities.isWrappedValue(binaryValue)) {
				if(this.inDriverBYOK.isWrappedWithInternalPolicy(binaryValue)) {
					updateValue = this.inDriverBYOK.unwrap(binaryValue);
				}
				else {
					// trovato un valore cifrato con una security policy differente ?
					// non effettua il wrap anche se richiesto
					skip(output, prefix, BYOKUtilities.extractPrefixWrappedValue(binaryValue), true);
					return;
				}
			}
			else {
				// trovato un valore non cifrato con una security policy
				// non effettua il wrap anche se richiesto
				skip(output, prefix, "--binary-value--", false);
				return;
			}
		}
		else {
			if(binaryValue!=null && binaryValue.length>0 && BYOKUtilities.isWrappedValue(binaryValue)) {
				// trovato un valore cifrato con una security policy, non devo quindi considerarlo
				skipWithPolicy(output, prefix, BYOKUtilities.extractPrefixWrappedValue(binaryValue));
				return;
			}
		}
		
		if(this.outDriverBYOK!=null || updateValue!=null) {
			updateBinaryValue(jdbcAdapter, connectionSQL, output, 
					binaryValue, updateValue, 
					c, prefix);			
		}
		
	}
	private void updateBinaryValue(IJDBCAdapter jdbcAdapter, Connection connectionSQL, StringBuilder output, 
			byte[]binaryValue, byte[]updateValue, 
			VaultUpdateConfigValue c, String prefix) throws UtilsException, SQLQueryObjectException, SQLException {
		
		if(this.outDriverBYOK!=null) {
			byte[] v = updateValue!=null ? updateValue : binaryValue;
			if(this.outDriverBYOK.isWrappedWithInternalPolicy(v)) {
				// Il valore di destinazione risulta già cifrato con la policy richiesta
				skipAlreadyWrapped(output, prefix, BYOKUtilities.extractPrefixWrappedValue(v));
				return;
			}
		}
		
		if(output!=null) {
			output.append(prefix).append("\n");
			String base64 = Base64Utilities.encodeAsString(binaryValue);
			output.append(LOG_ORIG).append(base64).append("\n");
		}
		
		updateBinaryValue(jdbcAdapter, connectionSQL, output, 
				binaryValue, updateValue, 
				c);
		
		if(output!=null) {
			output.append("\n\n");
		}
	}
	private void updateBinaryValue(IJDBCAdapter jdbcAdapter, Connection connectionSQL, StringBuilder output, 
			byte[]binaryValue, byte[]updateValue, 
			VaultUpdateConfigValue binaryConfig) throws UtilsException, SQLQueryObjectException, SQLException {
		PreparedStatement stmUpdate = null;
		try {
			BYOKWrappedValue wrappedValue =null;
			if(this.outDriverBYOK!=null) {
				wrappedValue = this.outDriverBYOK.wrap(updateValue!=null ? updateValue : binaryValue);
			}
			
			ISQLQueryObject sqlQueryObjectUpdate = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObjectUpdate.addUpdateTable(binaryConfig.tabella);
			sqlQueryObjectUpdate.addUpdateField(binaryConfig.nomeColonna, "?");
			sqlQueryObjectUpdate.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObjectUpdate.setANDLogicOperator(true);
			String sqlUpdate = sqlQueryObjectUpdate.createSQLUpdate();
			stmUpdate = connectionSQL.prepareStatement(sqlUpdate);
			int index = 1;
			if(wrappedValue!=null) {
				jdbcAdapter.setBinaryData(stmUpdate,index++,wrappedValue.getWrappedValue().getBytes());
				if(output!=null) {
					String base64 = Base64Utilities.encodeAsString(wrappedValue.getWrappedValue().getBytes());
					output.append(LOG_NEW).append(base64).append("\n");
				}
			}
			else {
				jdbcAdapter.setBinaryData(stmUpdate,index++,updateValue);
				if(output!=null) {
					String base64 = Base64Utilities.encodeAsString(updateValue);
					output.append(LOG_NEW).append(base64).append("\n");
				}
			}
			stmUpdate.setLong(index++, binaryConfig.id);
			int row = stmUpdate.executeUpdate();
			if(output!=null) {
				output.append(LOG_ROW_UPDATE).append(row).append("\n");
			}
			stmUpdate.close();
		} finally {
			JDBCUtilities.closeResources(stmUpdate);
		}
	}
	
	
	
	
	
	// CONNETTORI
	
	private void updateConnettori(String nomeColonnaPlain, String nomeColonnaCodificata, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addWhereIsNotNullCondition(nomeColonnaPlain);
			sqlQueryObject.addWhereIsNotEmptyCondition(nomeColonnaPlain);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.CONNETTORI_COLUMN_NOME);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			rs = stmRead.executeQuery();
			
			while(rs.next()){
				
				long id = rs.getLong("id");
				String nomeConnettore = rs.getString(CostantiDB.CONNETTORI_COLUMN_NOME);
				String endpointtype = rs.getString(CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE);
				String prefix = "["+nomeConnettore+"][tipoConnettore:"+endpointtype+"][idConnettore:"+id+"] '"+nomeColonnaPlain+"' ";
				
				if(nomeColonnaCodificata!=null) {
					VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
					c.tabella = CostantiDB.CONNETTORI;
					c.nomeColonnaPlain = nomeColonnaPlain;
					c.nomeColonnaCodificata = nomeColonnaCodificata;
					c.id = id;
					updatePlainEncValue(connectionSQL, rs, nomeColonnaPlain, nomeColonnaCodificata,
							prefix, output,
							c);
				}
				else {
					VaultUpdateConfigValue c = new VaultUpdateConfigValue();
					c.tabella = CostantiDB.CONNETTORI;
					c.nomeColonna = nomeColonnaPlain;
					c.id = id;
					updateValue(connectionSQL, rs, nomeColonnaPlain, 
							prefix, output,
							c);
				}
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateConnettori] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	
	private void updateConnettoriCustom(String nomeProprieta, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			String aliasIdConnettore = "idCon";
			String aliasNomeConnettore = "nomeCon";
			String aliasTipoConnettore = "tipoCon";
			String aliasIdConnettoreCustsom = "idConCustom";
			String aliasNomeConnettoreCustsom = "nomeConCustom";
			String aliasPlanValueConnettoreCustsom = "plainConCustom";
			String aliasEncValueConnettoreCustsom = "encConCustom";
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, aliasNomeConnettore);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE, aliasTipoConnettore);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI, "id", aliasIdConnettore);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI_CUSTOM, "id", aliasIdConnettoreCustsom);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, aliasNomeConnettoreCustsom);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE, aliasPlanValueConnettoreCustsom);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE, aliasEncValueConnettoreCustsom);
			sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+CONDITION_JOIN_ID+CostantiDB.CONNETTORI_CUSTOM+"."+CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE);
			sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI_CUSTOM+"."+CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME+"=?");
			sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.CONNETTORI_CUSTOM+"."+CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
			sqlQueryObject.addWhereIsNotEmptyCondition(CostantiDB.CONNETTORI_CUSTOM+"."+CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
			sqlQueryObject.addWhereCondition(true, true, CostantiDB.CONNETTORI_CUSTOM+"."+CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE+"='"+CostantiDB.MODIPA_VALUE_UNDEFINED+"'"); // vale anche per security properties
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.CONNETTORI_COLUMN_NOME);
			sqlQueryObject.setSelectDistinct(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			stmRead.setString(1, nomeProprieta);
			rs = stmRead.executeQuery();
			
			while(rs.next()){
				
				long idCustom = rs.getLong(aliasIdConnettoreCustsom);
				long idConnettore = rs.getLong(aliasIdConnettore);
				String nomeConnettore = rs.getString(aliasNomeConnettore);
				String endpointtype = rs.getString(aliasTipoConnettore);
				String prefix = "["+nomeConnettore+"][tipoConnettore:"+endpointtype+"][idConnettore:"+idConnettore+"][idCustomProp:"+idCustom+"] '"+nomeProprieta+"' ";
				
				VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
				c.tabella = CostantiDB.CONNETTORI_CUSTOM;
				c.nomeColonnaPlain = CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE;
				c.nomeColonnaCodificata = CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE;
				c.id = idCustom;
				updatePlainEncValue(connectionSQL, rs, aliasPlanValueConnettoreCustsom, aliasEncValueConnettoreCustsom,
						prefix, output,
						c);
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateConnettoriCustom] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}

	
	
	
	
	
	
	
	
	// SERVIZI APPLICATIVI
	
	private void updateServiziApplicativi(String nomeColonnaPlain, String nomeColonnaCodificata, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			String aliasNomeSA = "nomeSA";
			String aliasTipoSoggetto = "tipoSogg";
			String aliasNomeSoggetto = "nomeSogg";
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "idSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "nome", aliasNomeSA);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipo", "tipoSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "tipo_soggetto", aliasTipoSoggetto);
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "nome_soggetto", aliasNomeSoggetto);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, nomeColonnaPlain, ALIAS_PLAIN_VALUE);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, nomeColonnaCodificata, ALIAS_ENC_VALUE);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereIsNotNullCondition(nomeColonnaPlain);
			sqlQueryObject.addWhereIsNotEmptyCondition(nomeColonnaPlain);
			sqlQueryObject.addOrderBy(aliasTipoSoggetto);
			sqlQueryObject.addOrderBy(aliasNomeSoggetto);
			sqlQueryObject.addOrderBy(aliasNomeSA);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			rs = stmRead.executeQuery();
			
			while(rs.next()){
				
				long idSA = rs.getLong("idSA");
				String nome = rs.getString(aliasNomeSA);
				String tipo = rs.getString("tipoSA");
				String tipoSoggetto = rs.getString(aliasTipoSoggetto);
				String nomeSoggetto = rs.getString(aliasNomeSoggetto);
				String prefix = "["+nome+"][soggetto:"+tipoSoggetto+"/"+nomeSoggetto+"][tipoSA:"+tipo+"][idSA:"+idSA+"] '"+nomeColonnaPlain+"' ";
				
				VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
				c.tabella = CostantiDB.SERVIZI_APPLICATIVI;
				c.nomeColonnaPlain = nomeColonnaPlain;
				c.nomeColonnaCodificata = nomeColonnaCodificata;
				c.id = idSA;
				updatePlainEncValue(connectionSQL, rs, ALIAS_PLAIN_VALUE, ALIAS_ENC_VALUE, 
						prefix, output,
						c);
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateServiziApplicativi] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	
	
	
	
	
	
	
	
	
	
	// MESSAGE SECURITY
	
	private void updateMessageSecurity(String tabellaPadre, String tabella, 
			String nomeColonnaNomeProprieta, String nomeColonnaPlain, String nomeColonnaCodificata,
			String nomeProprieta, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			String aliasIdSecProp = "idSecProp";
			String aliasIdPorta = "idP";
			String aliasNomePorta = "nomeP";
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(tabellaPadre);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addSelectAliasField(tabellaPadre, "id", aliasIdPorta);
			sqlQueryObject.addSelectAliasField(tabellaPadre, "nome_porta", aliasNomePorta);
			sqlQueryObject.addSelectAliasField(tabella, "id", aliasIdSecProp);
			sqlQueryObject.addSelectAliasField(tabella, nomeColonnaPlain, ALIAS_PLAIN_VALUE);
			sqlQueryObject.addSelectAliasField(tabella, nomeColonnaCodificata, ALIAS_ENC_VALUE);
			sqlQueryObject.addWhereCondition(tabellaPadre+CONDITION_JOIN_ID+tabella+".id_porta");
			
			ISQLQueryObject sqlQueryObjectOrName = sqlQueryObject.newSQLQueryObject();
			sqlQueryObjectOrName.addFromTable(tabella);
			sqlQueryObjectOrName.addWhereCondition(tabella+"."+nomeColonnaNomeProprieta+" = ?");
			sqlQueryObjectOrName.addWhereLikeCondition(tabella+"."+nomeColonnaNomeProprieta, CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+nomeProprieta, LikeConfig.endsWith(false)); // puo' contenere l'id del properties
			sqlQueryObjectOrName.addWhereLikeCondition(tabella+"."+nomeColonnaNomeProprieta, CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR+nomeProprieta, LikeConfig.endsWith(false)); // puo' contenere l'id del properties
			sqlQueryObjectOrName.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition(sqlQueryObjectOrName.createSQLConditions());

			sqlQueryObject.addWhereIsNotNullCondition(nomeColonnaPlain);
			sqlQueryObject.addWhereIsNotEmptyCondition(nomeColonnaPlain);
			sqlQueryObject.addWhereCondition(true, true, tabella+"."+nomeColonnaPlain+"='"+CostantiDB.MODIPA_VALUE_UNDEFINED+"'"); // vale anche per security properties
			
			sqlQueryObject.addOrderBy(aliasNomePorta);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			stmRead.setString(1, nomeProprieta);
			rs = stmRead.executeQuery();

			while(rs.next()){
				
				long idSecProp = rs.getLong(aliasIdSecProp);
				String nome = rs.getString(aliasNomePorta);
				long idPorta = rs.getLong(aliasIdPorta);
				String prefix = "["+nome+"][idPorta:"+idPorta+"][idSecProp:"+idSecProp+"] '"+nomeProprieta+"' ";
				
				VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
				c.tabella = tabella;
				c.nomeColonnaPlain = nomeColonnaPlain;
				c.nomeColonnaCodificata = nomeColonnaCodificata;
				c.id = idSecProp;
				updatePlainEncValue(connectionSQL, rs, ALIAS_PLAIN_VALUE, ALIAS_ENC_VALUE, 
						prefix, output,
						c);
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateMessageSecurity] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	
	
	
	
	
	
	
	
	
	// GENERIC PROPERTIES
	
	private void updateGenericProperties(String tipologia, String nomeProprieta, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			String aliasNomeConfig = "nomeConfig";
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONFIG_GENERIC_PROPERTY, "id", "idProp");
			sqlQueryObject.addSelectAliasField(CostantiDB.CONFIG_GENERIC_PROPERTIES, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME, aliasNomeConfig);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, ALIAS_PLAIN_VALUE);
			sqlQueryObject.addSelectAliasField(CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ENC_VALUE, ALIAS_ENC_VALUE);
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTIES+CONDITION_JOIN_ID+CostantiDB.CONFIG_GENERIC_PROPERTY+".id_props");
			sqlQueryObject.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTIES+".tipo = ?");
			
			ISQLQueryObject sqlQueryObjectOrName = sqlQueryObject.newSQLQueryObject();
			sqlQueryObjectOrName.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
			sqlQueryObjectOrName.addWhereCondition(CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME+" = ?");
			sqlQueryObjectOrName.addWhereLikeCondition(CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME, CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+nomeProprieta, LikeConfig.endsWith(false)); // puo' contenere l'id del properties
			sqlQueryObjectOrName.addWhereLikeCondition(CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME, CostantiProprieta.KEY_PROPERTIES_DEFAULT_SEPARATOR+nomeProprieta, LikeConfig.endsWith(false)); // puo' contenere l'id del properties
			sqlQueryObjectOrName.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition(sqlQueryObjectOrName.createSQLConditions());
			
			sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE);
			sqlQueryObject.addWhereIsNotEmptyCondition(CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE);
			sqlQueryObject.addWhereCondition(true, true, CostantiDB.CONFIG_GENERIC_PROPERTY+"."+CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE+"='"+CostantiDB.MODIPA_VALUE_UNDEFINED+"'"); // vale anche per generic properties
			
			sqlQueryObject.addOrderBy(aliasNomeConfig);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setSelectDistinct(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			stmRead.setString(1, tipologia);
			stmRead.setString(2, nomeProprieta);
			rs = stmRead.executeQuery();

			while(rs.next()){
				
				long id = rs.getLong("idProp");
				String nomeConfig = rs.getString(aliasNomeConfig);
				String prefix = "[config:"+nomeConfig+"][tipo:"+tipologia+"][idGP:"+id+"] '"+nomeProprieta+"' ";
				
				VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
				c.tabella = CostantiDB.CONFIG_GENERIC_PROPERTY;
				c.nomeColonnaPlain = CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE;
				c.nomeColonnaCodificata = CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ENC_VALUE;
				c.id = id;
				updatePlainEncValue(connectionSQL, rs, ALIAS_PLAIN_VALUE, ALIAS_ENC_VALUE, 
						prefix, output,
						c);
							
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateGenericProperties] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	
	
	
	
	// PROTOCOL PROPERTIES
	
	private void updateProtocolProperties(String nomeProprieta, boolean binary, StringBuilder output) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PROTOCOL_PROPERTIES);
			sqlQueryObject.addWhereCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME+" = ?");
			if(binary) {
				sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY);
			}
			else {
				sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING);
				sqlQueryObject.addWhereIsNotEmptyCondition(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING);
				sqlQueryObject.addWhereCondition(true, true, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING+"='"+CostantiDB.MODIPA_VALUE_UNDEFINED+"'");
			}
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO);
			sqlQueryObject.addOrderBy(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO);
			sqlQueryObject.addOrderBy(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_NAME);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			stmRead.setString(1, nomeProprieta);
			rs = stmRead.executeQuery();
			
			IJDBCAdapter jdbcAdapter = null;
			if(binary) {
				jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDatabase);
			}
			
			
			while(rs.next()){
				
				long id = rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID);
				long idProprietario = rs.getLong(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_ID_PROPRIETARIO);
				String proprietario = rs.getString(CostantiDB.PROTOCOL_PROPERTIES_COLUMN_TIPO_PROPRIETARIO);
				
				String identificativoOggetto = getIdentificativoOggettoProtocolProperties(proprietario,idProprietario, id);
				
				String prefix = "[tipoProprietario:"+proprietario+"][idProprietario:"+idProprietario+"]["+identificativoOggetto+"][idProp:"+id+"] '"+nomeProprieta+"' ";
				
				if(binary) {
					VaultUpdateConfigValue c = new VaultUpdateConfigValue();
					c.tabella = CostantiDB.PROTOCOL_PROPERTIES;
					c.nomeColonna = CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY;
					c.id = id;
					updateBinaryProtocolProperty(jdbcAdapter, connectionSQL, rs, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_BINARY, 
							prefix, output,
							c);
				}
				else {
					VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
					c.tabella = CostantiDB.PROTOCOL_PROPERTIES;
					c.nomeColonnaPlain = CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING;
					c.nomeColonnaCodificata = CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING;
					c.id = id;
					updatePlainEncValue(connectionSQL, rs, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_STRING, CostantiDB.PROTOCOL_PROPERTIES_COLUMN_VALUE_ENCODING_STRING, 
							prefix, output,
							c);
				}
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateProtocolProperties] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	private String getIdentificativoOggettoProtocolProperties(String proprietario, long id, long idProp) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();		
		
			ProprietariProtocolProperty proprietarioProtocolProperty = ProprietariProtocolProperty.valueOf(proprietario);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			addTableProtocolProperties(sqlQueryObject, proprietarioProtocolProperty);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			stmRead.setLong(1, id);
			rs = stmRead.executeQuery();
			if(rs.next()){
				return readInfoProtocolProperties(connectionSQL, rs, proprietarioProtocolProperty);
			}
			throw new CoreException("Entry not found for (idProp:"+idProp+") '"+proprietarioProtocolProperty+"' ("+sqlQuery+" id:"+id+")");
		
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
		
	}
	private void addTableProtocolProperties(ISQLQueryObject sqlQueryObject, ProprietariProtocolProperty proprietarioProtocolProperty) throws SQLQueryObjectException {
		switch(proprietarioProtocolProperty) {
		case ACCORDO_COOPERAZIONE:
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			break;
		case ACCORDO_SERVIZIO_PARTE_COMUNE:
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			break;
		case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			break;
		case AZIONE_ACCORDO:
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			break;
		case OPERATION:
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			break;
		case PORT_TYPE:
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			break;
		case RESOURCE:
			sqlQueryObject.addFromTable(CostantiDB.API_RESOURCES);
			break;
		case FRUITORE:
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			break;
		case SERVIZIO_APPLICATIVO:
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			break;
		case SOGGETTO:
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			break;
		default:
			break;
	}
	}
	private String readInfoProtocolProperties(Connection connectionSQL, ResultSet rs, ProprietariProtocolProperty proprietarioProtocolProperty) throws SQLException, CoreException, DriverRegistroServiziException {
		switch(proprietarioProtocolProperty) {
		case ACCORDO_COOPERAZIONE:{
			return rs.getString("nome");
		}
		case ACCORDO_SERVIZIO_PARTE_COMUNE:{
			String nome = rs.getString("nome");
			long idSoggetto = rs.getLong(CostantiDB.ACCORDI_COLUMN_ID_REFERENTE_REF);
			int versione = rs.getInt("versione");
			IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
			return IDAccordoFactory.getInstance().getUriFromValues(nome, idS, versione);
		}
		case ACCORDO_SERVIZIO_PARTE_SPECIFICA:{
			String tipoServizio = rs.getString("tipo_servizio");
			String nomeServizio = rs.getString("nome_servizio");
			int versioneServizio = rs.getInt("versione_servizio");
			long idSoggetto = rs.getLong(CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF);
			IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
			return IDServizioFactory.getInstance().getUriFromValues(tipoServizio, nomeServizio, idS, versioneServizio);
		}
		case AZIONE_ACCORDO:{
			String nome = rs.getString("nome");
			long idAccordo = rs.getLong(CostantiDB.ACCORDI_COLUMN_ID_ACCORDO_REF);
			return "api:"+readDatiAccordo(connectionSQL, idAccordo) + " azione:"+nome;
		}
		case OPERATION:{
			String nome = rs.getString("nome");
			long idPortType = rs.getLong("id_port_type");
			return "api:"+readDatiPT(connectionSQL, idPortType) + " operation:"+nome;
		}
		case PORT_TYPE:{
			String nome = rs.getString("nome");
			long idAccordo = rs.getLong(CostantiDB.ACCORDI_COLUMN_ID_ACCORDO_REF);
			return "api:"+readDatiAccordo(connectionSQL, idAccordo) + " portType:"+nome;
		}
		case RESOURCE:{
			String nome = rs.getString("nome");
			long idAccordo = rs.getLong(CostantiDB.ACCORDI_COLUMN_ID_ACCORDO_REF);
			return "api:"+readDatiAccordo(connectionSQL, idAccordo) + " resource:"+nome;
		}
		case FRUITORE:{
			long idServizio = rs.getLong("id_servizio");
			long idSoggetto = rs.getLong(CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF);
			IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
			return idS.toString() +" -> " + readDatiServizio(connectionSQL, idServizio);
		}
		case SERVIZIO_APPLICATIVO:{
			String nome = rs.getString("nome");
			long idSoggetto = rs.getLong("id_soggetto");
			IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
			return idS.toString()+" sa:"+nome;
		}
		case SOGGETTO:{
			String tipoSoggetto = rs.getString("tipo_soggetto");
			String nomeSoggetto = rs.getString("nome_soggetto");
			return new IDSoggetto(tipoSoggetto, nomeSoggetto).toString();
		}
		default:
			break;
		}
		return null;
	}
	
	
	
	
	// PROPERTIES
	
	private void updateProperties(String tabella, String nomeColonna, String nomeColonnaPlain, String nomeColonnaCodificata, StringBuilder output,
			boolean checkRequestSec, boolean checkResponseCheck) throws CoreException {
		PreparedStatement stmRead = null;
		ResultSet rs=null;
		
		Connection connectionSQL = null;
		try {
			connectionSQL = getConnection();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addWhereIsNotNullCondition(nomeColonnaCodificata);
			sqlQueryObject.addWhereIsNotEmptyCondition(nomeColonnaCodificata);
			sqlQueryObject.addOrderBy(nomeColonna);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmRead = connectionSQL.prepareStatement(sqlQuery);
			rs = stmRead.executeQuery();
			
			while(rs.next()){
				
				long idP = rs.getLong("id");
				String nomeProprieta = rs.getString(nomeColonna);
				
				if(checkRequestSec || checkResponseCheck) {
					long idPorta = rs.getLong(CostantiDB.PORTA_COLUMN_ID_REF);
					String tabellaPorta = CostantiDB.PORTE_DELEGATE;
					if(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST.equals(tabella) ||
							CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE.equals(tabella)) {
						tabellaPorta = CostantiDB.PORTE_APPLICATIVE;
					}
					String sec = readMessageSecurityPorta(connectionSQL, idPorta, tabellaPorta, checkRequestSec);
					if(sec!=null && StringUtils.isNotEmpty(sec) && !"-".equals(sec) && !CostantiDB.SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT.equals(sec)) {
						// viene gestita con il processamento specifico delle proprietà fatto in precedenza
						continue;
					}
				}
				
				String identificativoOggetto = getIdentificativoOggettoProperties(tabella,connectionSQL,rs);
				
				String prefix = "[proprietario:"+tabella+"]["+identificativoOggetto+"][idProp:"+idP+"] '"+nomeProprieta+"' ";
				
				VaultUpdateConfigPlainEnc c = new VaultUpdateConfigPlainEnc();
				c.tabella = tabella;
				c.nomeColonnaPlain = nomeColonnaPlain;
				c.nomeColonnaCodificata = nomeColonnaCodificata;
				c.id = idP;
				
				updatePlainEncValue(connectionSQL, rs, nomeColonnaPlain, nomeColonnaCodificata, 
						prefix, output,
						c);
			
			}
			
		} catch (Exception se) {
			throw new CoreException("[updateProperties-"+tabella+"] failed: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stmRead);
			closeConnection(connectionSQL);
		}
	}
	private String getIdentificativoOggettoProperties(String tabella, Connection connectionSQL, ResultSet rs) throws SQLException, CoreException {
		if(CostantiDB.SOGGETTI_PROPS.equals(tabella)) {
			long idSoggetto = rs.getLong("id_soggetto");
			IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
			return idS.toString();
		}
		else if(CostantiDB.SERVIZI_APPLICATIVI_PROPS.equals(tabella)) {
			long idSA = rs.getLong("id_servizio_applicativo");
			return readDatiSA(connectionSQL, idSA);
		}
		else if(CostantiDB.SYSTEM_PROPERTIES_PDD.equals(tabella)) {
			return "configurazione-generale";
		}
		else if(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST.equals(tabella) ||
				CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE.equals(tabella) ||
				CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP.equals(tabella) ||
				CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP.equals(tabella) ||
				CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP.equals(tabella) ||
				CostantiDB.PORTE_APPLICATIVE_PROP.equals(tabella)) {
			long idPorta = rs.getLong(CostantiDB.PORTA_COLUMN_ID_REF);
			return readDatiPortaApplicativa(connectionSQL, idPorta);
		}
		else if(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST.equals(tabella) ||
				CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE.equals(tabella) ||
				CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP.equals(tabella) ||
				CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP.equals(tabella) ||
				CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP.equals(tabella) ||
				CostantiDB.PORTE_DELEGATE_PROP.equals(tabella)) {
			long idPorta = rs.getLong(CostantiDB.PORTA_COLUMN_ID_REF);
			return readDatiPortaDelegata(connectionSQL, idPorta);
		}
		return null;
		
	}
	
	
	
	// READ UTILS
		
	private String readDatiAccordo(Connection connectionSQL, long idAccordo) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idAccordo);
			rsInternal = stmReadInternal.executeQuery();
			String nome = rsInternal.getString("nome");
			if(rsInternal.next()) {
				long idSoggetto = rsInternal.getLong(CostantiDB.ACCORDI_COLUMN_ID_REFERENTE_REF);
				int versione = rsInternal.getInt("versione");
				IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
				return IDAccordoFactory.getInstance().getUriFromValues(nome, idS, versione);
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
	private String readDatiPT(Connection connectionSQL, long idPortType) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idPortType);
			rsInternal = stmReadInternal.executeQuery();
			if(rsInternal.next()) {
				String nome = rsInternal.getString("nome");
				long idAccordo = rsInternal.getLong(CostantiDB.ACCORDI_COLUMN_ID_ACCORDO_REF);
				return readDatiAccordo(connectionSQL, idAccordo) + " portType:"+nome;
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
	private String readDatiServizio(Connection connectionSQL, long idServizio) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idServizio);
			rsInternal = stmReadInternal.executeQuery();
			if(rsInternal.next()) {
				String tipoServizio = rsInternal.getString("tipo_servizio");
				String nomeServizio = rsInternal.getString("nome_servizio");
				int versioneServizio = rsInternal.getInt("versione_servizio");
				long idSoggetto = rsInternal.getLong(CostantiDB.SERVIZI_COLUMN_ID_SOGGETTO_REF);
				IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
				return IDServizioFactory.getInstance().getUriFromValues(tipoServizio, nomeServizio, idS, versioneServizio);
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
	private String readDatiSA(Connection connectionSQL, long idServizioApplicativo) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idServizioApplicativo);
			rsInternal = stmReadInternal.executeQuery();
			if(rsInternal.next()) {
				String nome = rsInternal.getString("nome");
				long idSoggetto = rsInternal.getLong(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				IDSoggetto idS = DBUtils.getIdSoggetto(idSoggetto, connectionSQL, this.tipoDatabase);
				return idS.toString()+" sa:"+nome;
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
	private String readDatiPortaDelegata(Connection connectionSQL, long idPorta) throws CoreException {
		return readDatiPorta(connectionSQL, idPorta, CostantiDB.PORTE_DELEGATE) ;
	}
	private String readDatiPortaApplicativa(Connection connectionSQL, long idPorta) throws CoreException {
		return readDatiPorta(connectionSQL, idPorta, CostantiDB.PORTE_APPLICATIVE) ;
	}
	private String readDatiPorta(Connection connectionSQL, long idPorta, String tabella) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idPorta);
			rsInternal = stmReadInternal.executeQuery();
			if(rsInternal.next()) {
				return rsInternal.getString("nome_porta");
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
	private String readMessageSecurityPorta(Connection connectionSQL, long idPorta, String tabella, boolean request) throws CoreException {
		PreparedStatement stmReadInternal = null;
		ResultSet rsInternal=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(tabella);
			sqlQueryObject.addWhereCondition(CONDITION_WHERE_ID);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmReadInternal = connectionSQL.prepareStatement(sqlQuery);
			stmReadInternal.setLong(1, idPorta);
			rsInternal = stmReadInternal.executeQuery();
			if(rsInternal.next()) {
				return rsInternal.getString(request ? "security_request_mode" : "security_response_mode");
			}
			return null;
		} catch (Exception se) {
			throw new CoreException(se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rsInternal, stmReadInternal);
		}
	}
}

class VaultUpdateConfigPlainEnc{
	
	String tabella;
	String nomeColonnaPlain;
	String nomeColonnaCodificata;
	long id;
	
}

class VaultUpdateConfigValue{
	
	String tabella;
	String nomeColonna;
	long id;
	
}
