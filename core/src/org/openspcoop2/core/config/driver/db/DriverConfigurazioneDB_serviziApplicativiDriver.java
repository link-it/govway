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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolPropertyConfig;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * DriverConfigurazioneDB_serviziApplicativiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_serviziApplicativiDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDB_soggettiDriver soggettiDriver = null;
	private DriverConfigurazioneDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	protected DriverConfigurazioneDB_serviziApplicativiDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(driver);
		this.protocolPropertiesDriver = new DriverConfigurazioneDB_protocolPropertiesDriver(driver);
	}
	

    protected ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this.getEngineServizioApplicativo(idServizioApplicativo, null, null, 
            		null, null, null, false,
            		null, 
            		null,
            		null,
            		false, false,
            		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this.getEngineServizioApplicativo(null, aUser, aPassword, 
            		null, null, null, false,
            		null, 
            		null,
            		config,
            		false, false,
            		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this.getEngineServizioApplicativo(null, aUser, aPassword, 
            		null, null, null, false,
            		null, 
            		null,
            		config,
            		true, appId,
            		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this.getEngineServizioApplicativo(null, null, null, 
            		null, null, null, false,
            		null, 
            		null,
            		null,
            		false, false,
            		tokenPolicy, tokenClientId, tokenWithHttpsEnabled);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String aSubject, String aIssuer) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this.getEngineServizioApplicativo(null, null, null, 
            		aSubject, aIssuer, null, false,
            		null, 
            		null,
            		null,
            		false, false,
            		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	 return this.getEngineServizioApplicativo(null, null, null, 
         		null, null, certificate, strictVerifier,
         		null, 
         		null,
         		null,
         		false, false,
        		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.getEngineServizioApplicativo(null, null, null, 
    			null, null, null, false,
    			principal, 
    			null,
    			null,
    			false, false,
        		null, null, false);
    }
    protected ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
            return this.getEngineServizioApplicativo(null, null, null, 
            		null, null, null, false,
            		null, 
            		idServizioApplicativo,
            		null,
            		false, false,
            		null, null, false);
    }

    private ServizioApplicativo getEngineServizioApplicativo(IDServizioApplicativo idServizioApplicativoObject, 
    		String aUser, String aPassord, 
    		String aSubject, String aIssuer, CertificateInfo aCertificate, boolean aStrictVerifier, 
    		String principal,
    		Long idServizioApplicativo, 
    		CryptConfig config,
    		boolean apiKey, boolean appId,
    		String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String nome_sa = null;
		String sqlQuery = null;
		ServizioApplicativo sa = null;

		int type = 0;
		final int TYPE_ID_OBJECT = 1;
		final int TYPE_BASIC = 2;
		final int TYPE_SSL_SUBJECT_ISSUER = 31;
		final int TYPE_SSL_CERTIFICATE = 32;
		final int TYPE_PRINCIPAL = 4;
		final int TYPE_ID_LONG = 5;
		final int TYPE_API_KEY = 6;
		final int TYPE_TOKEN = 7;
		if(idServizioApplicativoObject!=null){
			IDSoggetto idSO = idServizioApplicativoObject.getIdSoggettoProprietario();
			if(idSO==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Soggetto Proprietario non definito.");
			if(idSO.getNome()==null || "".equals(idSO.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Proprietario non definito.");
			if(idSO.getTipo()==null || "".equals(idSO.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Proprietario non definito.");

			nome_sa = idServizioApplicativoObject.getNome();
			if(nome_sa==null){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome non definito.");
			}
			type = TYPE_ID_OBJECT;
		}
		else if(apiKey){
			type = TYPE_API_KEY;
		}
		else if(aUser!=null){
			type = TYPE_BASIC;
		}
		else if(aSubject!=null){
			type = TYPE_SSL_SUBJECT_ISSUER;
		}
		else if(aCertificate!=null){
			type = TYPE_SSL_CERTIFICATE;
		}
		else if(principal!=null){
			type = TYPE_PRINCIPAL;
		}
		else if(tokenPolicy!=null){
			type = TYPE_TOKEN;
		}
		else{
			type = TYPE_ID_LONG;
			if(idServizioApplicativo==null || idServizioApplicativo.longValue()<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Id DB non definito.");
			}
		}

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServizioApplicativo");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			switch (type) {
			case TYPE_ID_OBJECT:{
				String tipoSog=idServizioApplicativoObject.getIdSoggettoProprietario().getTipo();
				String nomeSog=idServizioApplicativoObject.getIdSoggettoProprietario().getNome();

				long idSog=DBUtils.getIdSoggetto(nomeSog, tipoSog, con, this.driver.tipoDB,this.driver.tabellaSoggetti);

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nome_sa);
				stm.setLong(2, idSog);
				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, nome_sa));

				break;
			}
			case TYPE_BASIC:
			case TYPE_API_KEY:{

				boolean testInChiaro = false;
				ICrypt crypt = null;
				String tipoCredenziale = null;
				if(type==TYPE_BASIC) {
					tipoCredenziale = CostantiConfigurazione.CREDENZIALE_BASIC.toString();
					if(config==null || config.isBackwardCompatibility()) {
						testInChiaro = true;
					}
					if(config!=null) {
						crypt = CryptFactory.getCrypt(this.driver.log, config);
					}
				}
				else {
					tipoCredenziale = CostantiConfigurazione.CREDENZIALE_APIKEY.toString();
					if(config!=null) {
						crypt = CryptFactory.getCrypt(this.driver.log, config);
					}
					else {
						testInChiaro = true;
					}
				}
				
				//cerco un servizio applicativo che contenga utente e password con autenticazione basi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				sqlQueryObject.addWhereCondition("utente = ?");
				//sqlQueryObject.addWhereCondition("password = ?");
				if(apiKey) {
					sqlQueryObject.addWhereCondition("issuer = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				//stm.setString(1, nome_sa);
				stm.setString(1, tipoCredenziale);
				stm.setString(2, aUser);
				//stm.setString(3, aPassord);
				if(apiKey) {
					stm.setString(3, CostantiDB.getIssuerApiKey(appId));
				}

				if(apiKey) {
					this.driver.logDebug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, tipoCredenziale, aUser, CostantiDB.getIssuerApiKey(appId)));
				}
				else {
					this.driver.logDebug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, tipoCredenziale, aUser));
				}

				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					
					String passwordDB =  rs.getString("password");
					
					boolean found = false;
					if(testInChiaro) {
						found = aPassord.equals(passwordDB);
					}
					if(!found && crypt!=null) {
						found = crypt.check(aPassord, passwordDB);
					}
					
					if( found ) {
						idSA = rs.getLong("id");
						break;
					}

				}
				rs.close();
				stm.close();
				//System.out.println("TROVATO["+idSA+"]");
				if(idSA<=0){
					throw new DriverConfigurazioneNotFound("Nessun Servizio Applicativo trovato.");
				}
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));
				
				break;
			}	
			case TYPE_SSL_SUBJECT_ISSUER:{

				// Autenticazione SSL deve essere LIKE
				Map<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoMap(aSubject, PrincipalType.SUBJECT);
				Map<String, List<String>> hashIssuer = null;
				if(StringUtils.isNotEmpty(aIssuer)) {
					hashIssuer = CertificateUtils.getPrincipalIntoMap(aIssuer, PrincipalType.ISSUER);
				}

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "subject", "saSubject");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "issuer", "saIssuer");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipoauth", "saTipoAuth");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
				// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
				if(!tokenWithHttpsEnabled){
					sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
				}

				for (String key : hashSubject.keySet()) {
					List<String> listValues = hashSubject.get(key);
					for (String value : listValues) {
						sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
					}
				}
				
				if(hashIssuer!=null) {
					for (String key : hashIssuer.keySet()) {			
						List<String> listValues = hashIssuer.get(key);
						for (String value : listValues) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
						}
					}
				}
				else {
					sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer");
				}

				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_SSL.toString());

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
					
					String subjectPotenziale =  rs.getString("saSubject");
					boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, aSubject, PrincipalType.SUBJECT, this.driver.log);
					
					boolean issuerValid = true;
					if(hashIssuer!=null) {
						String issuerPotenziale =  rs.getString("saIssuer");
						if(StringUtils.isNotEmpty(issuerPotenziale)) {
							issuerValid = CertificateUtils.sslVerify(issuerPotenziale, aIssuer, PrincipalType.ISSUER, this.driver.log);
						}
						else {
							issuerValid = false;
						}
					}
					
					
					if( subjectValid && issuerValid ) {
						idSA = rs.getLong("saIdentificativo");
						break;
					}
				}
				rs.close();
				stm.close();
				//System.out.println("TROVATO["+idSA+"]");
				
				if(idSA<=0) {
					// provo a vedere all'interno delle credenziali ulteriori
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "subject", "saSubject");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "issuer", "saIssuer");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipoauth", "saTipoAuth");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
					// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
					if(!tokenWithHttpsEnabled){
						sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
					}
					
					for (String key : hashSubject.keySet()) {
						List<String> listValues = hashSubject.get(key);
						for (String value : listValues) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
						}
					}
					
					if(hashIssuer!=null) {
						for (String key : hashIssuer.keySet()) {
							List<String> listValues = hashIssuer.get(key);
							for (String value : listValues) {
								sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
							}
						}
					}
					else {
						sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".issuer");
					}

					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					
					//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

					stm = con.prepareStatement(sqlQuery);
					stm.setString(1, CostantiConfigurazione.CREDENZIALE_SSL.toString());

					this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

					rs = stm.executeQuery();
					while(rs.next()){
						// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
						
						String subjectPotenziale =  rs.getString("saSubject");
						boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, aSubject, PrincipalType.SUBJECT, this.driver.log);
						
						boolean issuerValid = true;
						if(hashIssuer!=null) {
							String issuerPotenziale =  rs.getString("saIssuer");
							if(StringUtils.isNotEmpty(issuerPotenziale)) {
								issuerValid = CertificateUtils.sslVerify(issuerPotenziale, aIssuer, PrincipalType.ISSUER, this.driver.log);
							}
							else {
								issuerValid = false;
							}
						}
						
						
						if( subjectValid && issuerValid ) {
							idSA = rs.getLong("saIdentificativo");
							break;
						}
					}
					rs.close();
					stm.close();
					//System.out.println("TROVATO["+idSA+"]");
				}
				
				if(idSA<=0){
					throw new DriverConfigurazioneNotFound("Nessun Servizio Applicativo trovato.");
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));

				break;
			}
			case TYPE_SSL_CERTIFICATE:{

				String cnSubject = aCertificate.getSubject().getCN();
				String cnIssuer = aCertificate.getIssuer().getCN();

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "cn_subject", "saCNSubject");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "cn_issuer", "saCNIssuer");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "cert_strict_verification", "saCertStrictVerificationr");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "certificate", "saCertificate");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipoauth", "saTipoAuth");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".cert_strict_verification = ?");
				// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
				// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
				if(!tokenWithHttpsEnabled){
					sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
				}

				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

				stm = con.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, CostantiConfigurazione.CREDENZIALE_SSL.toString());
				stm.setString(index++, cnSubject);
				stm.setString(index++, cnIssuer);
				if(aStrictVerifier) {
					stm.setInt(index++, CostantiDB.TRUE);
				}
				else {
					stm.setInt(index++, CostantiDB.FALSE);
				}

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				
				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					// Possono esistere piu' sil che hanno un CN con subject e issuer diverso.
					
					byte[] certificatoBytes = jdbcAdapter.getBinaryData(rs, "saCertificate");
					Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
					//int tmpStrict = rs.getInt("cert_strict_verification");
					//boolean strict = tmpStrict == CostantiDB.TRUE;
					
					if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
						idSA = rs.getLong("saIdentificativo");
						break;
					}

				}
				rs.close();
				stm.close();
				//System.out.println("TROVATO["+idSA+"]");
				
				if(idSA<=0) {
					// provo a vedere all'interno delle credenziali ulteriori
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "cn_subject", "saCNSubject");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "cn_issuer", "saCNIssuer");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "cert_strict_verification", "saCertStrictVerificationr");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI, "certificate", "saCertificate");
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipoauth", "saTipoAuth");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".cn_subject = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".cn_issuer = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".cert_strict_verification = ?");
					// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
					// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
					if(!tokenWithHttpsEnabled){
						sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
					}
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

					stm = con.prepareStatement(sqlQuery);
					index = 1;
					stm.setString(index++, CostantiConfigurazione.CREDENZIALE_SSL.toString());
					stm.setString(index++, cnSubject);
					stm.setString(index++, cnIssuer);
					if(aStrictVerifier) {
						stm.setInt(index++, CostantiDB.TRUE);
					}
					else {
						stm.setInt(index++, CostantiDB.FALSE);
					}

					this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

					jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
					
					rs = stm.executeQuery();
					while(rs.next()){
						// Possono esistere piu' sil che hanno un CN con subject e issuer diverso.
						
						byte[] certificatoBytes = jdbcAdapter.getBinaryData(rs, "saCertificate");
						Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
						//int tmpStrict = rs.getInt("cert_strict_verification");
						//boolean strict = tmpStrict == CostantiDB.TRUE;
						
						if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
							idSA = rs.getLong("saIdentificativo");
							break;
						}

					}
					rs.close();
					stm.close();
					//System.out.println("TROVATO["+idSA+"]");
				}
				
				if(idSA<=0){
					throw new DriverConfigurazioneNotFound("Nessun Servizio Applicativo trovato.");
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));

				break;
			}	
			case TYPE_PRINCIPAL:{

				//cerco un servizio applicativo che contenga utente e password con autenticazione basi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				sqlQueryObject.addWhereCondition("utente = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				//stm.setString(1, nome_sa);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString());
				stm.setString(2, principal);
				this.driver.logDebug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString(), principal));

				break;
			}
			case TYPE_TOKEN:{

				//cerco un servizio applicativo che contenga l'autenticazione token
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				
				List<FiltroRicercaProtocolPropertyConfig> modiFilter = null; 
				if(tokenWithHttpsEnabled){
				
					modiFilter = buildListModiTokenCredentials(tokenPolicy, tokenClientId);
					
					ISQLQueryObject sqlQueryObjectBuiltIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectBuiltIn.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObjectBuiltIn.addWhereCondition(false, "tipoauth = ?", "tipoauth = ?");
					sqlQueryObjectBuiltIn.addWhereCondition("token_policy = ?");
					sqlQueryObjectBuiltIn.addWhereCondition("utente = ?");
					sqlQueryObjectBuiltIn.setANDLogicOperator(true);
					
					ISQLQueryObject sqlQueryObjectModi = buildSQLQueryObjectModiTokenCredentials(modiFilter);
					
					sqlQueryObject.addWhereCondition(sqlQueryObjectBuiltIn.createSQLConditions());
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectModi);
					sqlQueryObject.setANDLogicOperator(false);
				}
				else {
					sqlQueryObject.addWhereCondition("tipoauth = ?");
					sqlQueryObject.addWhereCondition("token_policy = ?");
					sqlQueryObject.addWhereCondition("utente = ?");
					sqlQueryObject.setANDLogicOperator(true);
				}

				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				
				int index = 1;
				if(tokenWithHttpsEnabled){
					stm.setString(index++, CostantiConfigurazione.CREDENZIALE_TOKEN.toString());
					stm.setString(index++, CostantiConfigurazione.CREDENZIALE_SSL.toString());
					stm.setString(index++, tokenPolicy);
					stm.setString(index++, tokenClientId);
					setSQLQueryObjectModiTokenCredentials (stm, index, modiFilter);
				}
				else {
					stm.setString(index++, CostantiConfigurazione.CREDENZIALE_TOKEN.toString());
					stm.setString(index++, tokenPolicy);
					stm.setString(index++, tokenClientId);
				}
				
				this.driver.logDebug("eseguo query (tokenPolicy:"+tokenPolicy+" tokenClientId:"+tokenClientId+"):" +sqlQuery);

				break;
			}
			case TYPE_ID_LONG:{
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);

				this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
				break;
			}
			
			}

			if(stm==null) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Type '"+type+"' sconosciuto?");
			}
			
			rs = stm.executeQuery();

			if (rs.next()) {
				sa = new ServizioApplicativo();

				sa.setId(rs.getLong("id"));
				sa.setTipo(rs.getString("tipo"));
				sa.setNome(rs.getString("nome"));
				sa.setIdSoggetto(rs.getLong("id_soggetto"));
				//tipo e nome soggetto
				//tipo e nome soggetto sono necessari per la propagazione...
				sa.setTipoSoggettoProprietario(this.soggettiDriver.getSoggetto(sa.getIdSoggetto(),con).getTipo());
				sa.setNomeSoggettoProprietario(this.soggettiDriver.getSoggetto(sa.getIdSoggetto(),con).getNome());
				// descrizione
				sa.setDescrizione(rs.getString("descrizione"));

				int as_client = rs.getInt("as_client");
				sa.setUseAsClient(CostantiDB.TRUE == as_client);
				
				//tipologia fruizione
				String tipoFruizione = rs.getString("tipologia_fruizione")!=null && !"".equals(rs.getString("tipologia_fruizione")) ? rs.getString("tipologia_fruizione") : TipologiaFruizione.DISABILITATO.toString();
				String tipoErogazione = rs.getString("tipologia_erogazione")!=null && !"".equals(rs.getString("tipologia_erogazione"))? rs.getString("tipologia_erogazione") : TipologiaErogazione.DISABILITATO.toString();
				sa.setTipologiaFruizione(TipologiaFruizione.valueOf(tipoFruizione.toUpperCase()).toString());
				sa.setTipologiaErogazione(TipologiaErogazione.valueOf(tipoErogazione.toUpperCase()).toString());

				//se le credenziali sono nulle allora non nn esiste invocazione porta
				InvocazionePorta invPorta = null;
				String tipoAuth = rs.getString("tipoauth");
				String fault = rs.getString("fault");
				String faultActor = rs.getString("fault_actor");
				String prefixFault = rs.getString("prefix_fault_code");
				String genericFault = rs.getString("generic_fault_code");
				if ((tipoAuth != null && !tipoAuth.equals("")) || 
						(fault != null && !fault.equals("")) ||
						(faultActor!=null && !faultActor.equals("")) ||
						(genericFault!=null && !genericFault.equals("")) ||
						(prefixFault!=null && !prefixFault.equals(""))
						) {
					invPorta = new InvocazionePorta();
					Credenziali credenziali = new Credenziali();
					
					credenziali.setTipo(DriverConfigurazioneDB_LIB.getEnumCredenzialeTipo(tipoAuth));
					
					credenziali.setUser(rs.getString("utente"));
					credenziali.setPassword(rs.getString("password"));
					
					if(org.openspcoop2.core.config.constants.CredenzialeTipo.APIKEY.equals(credenziali.getTipo())) {
						credenziali.setAppId(CostantiDB.isAPPID(rs.getString("issuer")));
					}
					else {
						credenziali.setIssuer(rs.getString("issuer"));
					}
					
					credenziali.setSubject(rs.getString("subject"));
					credenziali.setCnSubject(rs.getString("cn_subject"));
					credenziali.setCnIssuer(rs.getString("cn_issuer"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
					credenziali.setCertificate(jdbcAdapter.getBinaryData(rs, "certificate"));
					int strict = rs.getInt("cert_strict_verification");
					if(strict == CostantiDB.TRUE) {
						credenziali.setCertificateStrictVerification(true);
					}
					else if(strict == CostantiDB.FALSE) {
						credenziali.setCertificateStrictVerification(false);
					}
					
					credenziali.setTokenPolicy(rs.getString("token_policy"));
					
					if(tipoAuth != null && !tipoAuth.equals("")){
						invPorta.addCredenziali( credenziali );
					}

					InvocazionePortaGestioneErrore gestioneErrore = new InvocazionePortaGestioneErrore();
					gestioneErrore.setFault(DriverConfigurazioneDB_LIB.getEnumFaultIntegrazioneTipo(fault));
					gestioneErrore.setFaultActor(faultActor);
					gestioneErrore.setGenericFaultCode(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(genericFault));
					gestioneErrore.setPrefixFaultCode(prefixFault);
					//setto gestione errore solo se i valori sono diversi da null
					invPorta.setGestioneErrore((fault != null || faultActor!=null || genericFault!=null || prefixFault!=null ) ? gestioneErrore : null);

					invPorta.setInvioPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("invio_x_rif")));

					int sbustamentoInfoProtocollo = rs.getInt("sbustamento_protocol_info");
					if(CostantiDB.TRUE == sbustamentoInfoProtocollo)
						invPorta.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
					else if(CostantiDB.FALSE == sbustamentoInfoProtocollo)
						invPorta.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.DISABILITATO);
					else
						invPorta.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
					
				}

				// RispostaAsincrona
				//se non esiste il connnettore e getmsgrisp e' disabilitato allora nn esiste risposta asincrona
				Long idConnettore = rs.getLong("id_connettore_risp");
				String getMsgRisp = rs.getString("getmsgrisp");
				RispostaAsincrona rispAsinc = null;				
				if (idConnettore > 0 || (getMsgRisp != null && !getMsgRisp.equals("")) ) {
					rispAsinc = new RispostaAsincrona();

					rispAsinc.setAutenticazione(DriverConfigurazioneDB_LIB.getEnumInvocazioneServizioTipoAutenticazione(rs.getString("tipoauthrisp")));
					rispAsinc.setInvioPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("invio_x_rif_risp")));
					rispAsinc.setRispostaPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("risposta_x_rif_risp")));

					if(rispAsinc.getAutenticazione()!=null && InvocazioneServizioTipoAutenticazione.BASIC.equals(rispAsinc.getAutenticazione())){
						InvocazioneCredenziali credenzialiRispA = new InvocazioneCredenziali();
						credenzialiRispA.setPassword(rs.getString("passwordrisp"));
						credenzialiRispA.setUser(rs.getString("utenterisp"));
						rispAsinc.setCredenziali(credenzialiRispA);
					}
					
					Connettore connettore = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettore, con);
					rispAsinc.setConnettore(connettore);
					rispAsinc.setGetMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(getMsgRisp));

					int sbustamentoInfoProtocollo = rs.getInt("sbustamento_protocol_info_risp");
					if(CostantiDB.TRUE == sbustamentoInfoProtocollo)
						rispAsinc.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
					else if(CostantiDB.FALSE == sbustamentoInfoProtocollo)
						rispAsinc.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.DISABILITATO);
					else
						rispAsinc.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);

					if(rs.getInt("sbustamentorisp")==1)
						rispAsinc.setSbustamentoSoap(CostantiConfigurazione.ABILITATO);
					else
						rispAsinc.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);

					// Gestione errore
					Long idGestioneErroreRispostaAsincrona = rs.getLong("id_gestione_errore_risp");
					GestioneErrore gestioneErroreRispostaAsincrona = null;
					if(idGestioneErroreRispostaAsincrona>0){
						gestioneErroreRispostaAsincrona = DriverConfigurazioneDB_gestioneErroreLIB.getGestioneErrore(idGestioneErroreRispostaAsincrona, con);
						rispAsinc.setGestioneErrore(gestioneErroreRispostaAsincrona);
					}
				}

				// InvocazioneServizio
				//se non esiste il connettore e getmsginv e' disabilitato allora non esiste invocazione servizio 
				idConnettore = rs.getLong("id_connettore_inv");
				String getMsgInv = rs.getString("getmsginv");
				InvocazioneServizio invServizio = null;				
				if (idConnettore > 0 || (getMsgInv != null && !getMsgInv.equals("")) ) {
					invServizio = new InvocazioneServizio();
					Connettore connserv = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettore, con);
					invServizio.setConnettore(connserv);
					invServizio.setGetMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(getMsgInv));

					int sbustamentoInfoProtocollo = rs.getInt("sbustamento_protocol_info_inv");
					if(CostantiDB.TRUE == sbustamentoInfoProtocollo)
						invServizio.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
					else if(CostantiDB.FALSE == sbustamentoInfoProtocollo)
						invServizio.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.DISABILITATO);
					else
						invServizio.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);

					if(rs.getInt("sbustamentoinv")==1)
						invServizio.setSbustamentoSoap(CostantiConfigurazione.ABILITATO);
					else
						invServizio.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);

					invServizio.setAutenticazione(DriverConfigurazioneDB_LIB.getEnumInvocazioneServizioTipoAutenticazione(rs.getString("tipoauthinv")));
					invServizio.setInvioPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("invio_x_rif_inv")));
					invServizio.setRispostaPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("risposta_x_rif_inv")));
					
					if(invServizio.getAutenticazione()!=null && InvocazioneServizioTipoAutenticazione.BASIC.equals(invServizio.getAutenticazione())){
						InvocazioneCredenziali credInvServ = new InvocazioneCredenziali();
						credInvServ.setPassword(rs.getString("passwordinv"));
						credInvServ.setUser(rs.getString("utenteinv"));
						invServizio.setCredenziali(credInvServ);
					}

					// Gestione errore
					Long idGestioneErroreInvocazioneServizio = rs.getLong("id_gestione_errore_inv");
					GestioneErrore gestioneErroreInvocazioneServizio = null;
					if(idGestioneErroreInvocazioneServizio>0){
						gestioneErroreInvocazioneServizio = DriverConfigurazioneDB_gestioneErroreLIB.getGestioneErrore(idGestioneErroreInvocazioneServizio, con);
						invServizio.setGestioneErrore(gestioneErroreInvocazioneServizio);
					}
				}

				sa.setInvocazionePorta(invPorta);
				sa.setRispostaAsincrona(rispAsinc);
				sa.setInvocazioneServizio(invServizio);

				// retrocompatibilita tipologie
				if(sa.getTipologiaErogazione()==null){
					sa.setTipologiaErogazione(TipologiaErogazione.DISABILITATO.getValue());
				}
				if(sa.getTipologiaFruizione()==null){
					sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
				}
				if(TipologiaErogazione.DISABILITATO.equals(sa.getTipologiaErogazione()) &&
						TipologiaFruizione.DISABILITATO.equals(sa.getTipologiaFruizione())){
					
					// TipologiaFruizione: cerco di comprenderlo dalla configurazione del sa
					if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0){
						sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
					}

					// TipologiaErogazione: cerco di comprenderlo dalla configurazione del sa
					if(sa.getInvocazioneServizio()!=null){
						if(StatoFunzionalita.ABILITATO.equals(sa.getInvocazioneServizio().getGetMessage())){
							sa.setTipologiaErogazione(TipologiaErogazione.MESSAGE_BOX.getValue());
						}
						else if(sa.getInvocazioneServizio().getConnettore()!=null && 
								!TipiConnettore.DISABILITATO.getNome().equals(sa.getInvocazioneServizio().getConnettore().getTipo())){
							sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
						}
					}
						
				}	
				
				rs.close();
				stm.close();
				
				
				
				// ruoli
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, sa.getId());
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(sa.getInvocazionePorta()==null){
						sa.setInvocazionePorta(new InvocazionePorta());
					}
					if(sa.getInvocazionePorta().getRuoli()==null){
						sa.getInvocazionePorta().setRuoli(new ServizioApplicativoRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					sa.getInvocazionePorta().getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				// credenziali
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQueryObject.addOrderBy("id", true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, sa.getId());
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(sa.getInvocazionePorta()==null){
						sa.setInvocazionePorta(new InvocazionePorta());
					}
					
					Credenziali credenziali = new Credenziali();
					
					credenziali.setTipo(DriverConfigurazioneDB_LIB.getEnumCredenzialeTipo(tipoAuth));
										
					credenziali.setIssuer(rs.getString("issuer"));
					credenziali.setSubject(rs.getString("subject"));
					credenziali.setCnSubject(rs.getString("cn_subject"));
					credenziali.setCnIssuer(rs.getString("cn_issuer"));
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
					credenziali.setCertificate(jdbcAdapter.getBinaryData(rs, "certificate"));
					int strict = rs.getInt("cert_strict_verification");
					if(strict == CostantiDB.TRUE) {
						credenziali.setCertificateStrictVerification(true);
					}
					else if(strict == CostantiDB.FALSE) {
						credenziali.setCertificateStrictVerification(false);
					}
				
					sa.getInvocazionePorta().addCredenziali(credenziali);	
				}
				rs.close();
				stm.close();
				
				
				// proprieta'
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, sa.getId());
				rs = stm.executeQuery();

				while (rs.next()) {
					
					Proprieta proprieta = new Proprieta();
					proprieta.setNome(rs.getString("nome"));
					proprieta.setValore(rs.getString("valore"));
					sa.addProprieta(proprieta);
				
				}
				rs.close();
				stm.close();
				
				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverConfigurazioneDB_LIB.getListaProtocolProperty(sa.getId(), ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							sa.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverConfigurazioneNotFound dNotFound){}
				
				
				return sa;

			} else {
				throw new DriverConfigurazioneNotFound("Nessun Servizio Applicativo trovato.");
			}
		} catch (SQLException e) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] SQLException :" + e.getMessage(),e);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Exception :" + e.getMessage(),e);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}

    private List<FiltroRicercaProtocolPropertyConfig> buildListModiTokenCredentials (String tokenPolicy, String tokenClientId) throws SQLQueryObjectException {
    	List<FiltroRicercaProtocolPropertyConfig> list = new ArrayList<FiltroRicercaProtocolPropertyConfig>();
		FiltroRicercaProtocolPropertyConfig pTokenPolicy = new FiltroRicercaProtocolPropertyConfig();
		pTokenPolicy.setName(CostantiDB.MODIPA_SICUREZZA_TOKEN_POLICY);
		pTokenPolicy.setValueAsString(tokenPolicy);
		list.add(pTokenPolicy);
		FiltroRicercaProtocolPropertyConfig pTokenClientId = new FiltroRicercaProtocolPropertyConfig();
		pTokenClientId.setName(CostantiDB.MODIPA_SICUREZZA_TOKEN_CLIENT_ID);
		pTokenClientId.setValueAsString(tokenClientId);
		list.add(pTokenClientId);
		return list;
    }
    private ISQLQueryObject buildSQLQueryObjectModiTokenCredentials (List<FiltroRicercaProtocolPropertyConfig> list) throws SQLQueryObjectException {
    	ISQLQueryObject sqlQueryObjectProtocolProperties = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
    	this.protocolPropertiesDriver._setProtocolPropertiesForSearch(sqlQueryObjectProtocolProperties, list, CostantiDB.SERVIZI_APPLICATIVI);
		return sqlQueryObjectProtocolProperties;
    }
    private void setSQLQueryObjectModiTokenCredentials (PreparedStatement stmt, int index,
    		List<FiltroRicercaProtocolPropertyConfig> list) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException {
    	this.protocolPropertiesDriver._setProtocolPropertiesForSearch(stmt, index, 
				list, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO);
    }
    
	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	protected void createServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {

		if (aSA == null)
			throw new DriverConfigurazioneException("Servizio Applicativo non valido");
		if (aSA.getNome() == null || aSA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Servizio Applicativo non valido");
		if (aSA.getNomeSoggettoProprietario() == null || aSA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Servizio Applicativo non valido");
		if (aSA.getTipoSoggettoProprietario() == null || aSA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Servizio Applicativo non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDServizioApplicativo type = 1");
			// creo soggetto
			DriverConfigurazioneDB_serviziApplicativiLIB.CRUDServizioApplicativo(1, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServizioApplicativo] Errore durante la creazione del servizioApplicativo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	protected void updateServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
		if (aSA == null)
			throw new DriverConfigurazioneException("Servizio Applicativo non valida");
		if (aSA.getNome() == null || aSA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Servizio Applicativo non valido");
		if (aSA.getNomeSoggettoProprietario() == null || aSA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Servizio Applicativo non valido");
		if (aSA.getTipoSoggettoProprietario() == null || aSA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Servizio Applicativo non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDServizioApplicativo type = 2");
			// creo soggetto
			DriverConfigurazioneDB_serviziApplicativiLIB.CRUDServizioApplicativo(2, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("Errore durante l'aggiornamento del servizioApplicativo : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
		if (aSA == null)
			throw new DriverConfigurazioneException("Servizio Applicativo non valida");
		if (aSA.getNome() == null || aSA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Servizio Applicativo non valido");
		if (aSA.getNomeSoggettoProprietario() == null || aSA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Servizio Applicativo non valido");
		if (aSA.getTipoSoggettoProprietario() == null || aSA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Servizio Applicativo non valido");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDServizioApplicativo type = 3");
			// creo soggetto
			DriverConfigurazioneDB_serviziApplicativiLIB.CRUDServizioApplicativo(3, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServizioApplicativo] Errore durante la delete del servizioApplicativo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	

	

	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiBasicList(String utente, String password, boolean checkPassword) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiBasicList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			sqlQueryObject.addWhereCondition("utente = ?");
			if(checkPassword) {
				sqlQueryObject.addWhereCondition("password = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, CredenzialeTipo.BASIC.getValue());
			stmt.setString(index++, utente);
			if(checkPassword) {
				stmt.setString(index++, password);
			}
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.getServizioApplicativo(risultato.getLong("id"));

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiApiKeyList(String utente, boolean appId) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiApiKeyList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			sqlQueryObject.addWhereCondition("utente = ?");
			sqlQueryObject.addWhereCondition("issuer = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, CredenzialeTipo.APIKEY.getValue());
			stmt.setString(index++, utente);
			stmt.setString(index++, CostantiDB.getIssuerApiKey(appId));
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.getServizioApplicativo(risultato.getLong("id"));

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}

	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(String subject, String issuer) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			for (int i = 0; i < 2; i++) {
							
				String tabella = null;
				if(i==0) {
					tabella = CostantiDB.SERVIZI_APPLICATIVI;
				}
				else {
					tabella = CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI;
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				if(i>0) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
				}
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
				//sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "nome", "saNome");
				//sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto", "saIdentificativoSoggetto");
				sqlQueryObject.addSelectAliasField(tabella, "subject", "saSubject");
				sqlQueryObject.addSelectAliasField(tabella, "issuer", "saIssuer");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "tipoauth", "saTipoAuth");
				
				if(i>0) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
				// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
				boolean tokenWithHttpsEnabled = false;
				if(!tokenWithHttpsEnabled){
					sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
				}
				
				Map<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoMap(subject, PrincipalType.SUBJECT);
				Map<String, List<String>> hashIssuer = null;
				if(StringUtils.isNotEmpty(issuer)) {
					hashIssuer = CertificateUtils.getPrincipalIntoMap(issuer, PrincipalType.ISSUER);
				}
				
				for (String key : hashSubject.keySet()) {					
					List<String> listValues = hashSubject.get(key);
					for (String value : listValues) {
						sqlQueryObject.addWhereLikeCondition(tabella+".subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
					}
					
				}
				
				if(hashIssuer!=null) {
					for (String key : hashIssuer.keySet()) {
						List<String> listValues = hashIssuer.get(key);
						for (String value : listValues) {
							sqlQueryObject.addWhereLikeCondition(tabella+".issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
						}
					}
					
				}
				else {
					sqlQueryObject.addWhereIsNullCondition(tabella+".issuer");
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setString(1, CredenzialeTipo.SSL.getValue());
				
				risultato = stmt.executeQuery();
	
				ServizioApplicativo sa;
				while (risultato.next()) {
	
					// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
					
					String subjectPotenziale =  risultato.getString("saSubject");
					boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, subject, PrincipalType.SUBJECT, this.driver.log);
					
					boolean issuerValid = true;
					if(hashIssuer!=null) {
						String issuerPotenziale =  risultato.getString("saIssuer");
						if(StringUtils.isNotEmpty(issuerPotenziale)) {
							issuerValid = CertificateUtils.sslVerify(issuerPotenziale, issuer, PrincipalType.ISSUER, this.driver.log);
						}
						else {
							issuerValid = false;
						}
					}
					
					if( !subjectValid || !issuerValid ) {
						continue;
					}
					
					sa=this.getServizioApplicativo(risultato.getLong("saIdentificativo"));
					lista.add(sa);
				}
				risultato.close(); risultato = null;
				stmt.close(); stmt = null;
				
			}
			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			
			
			for (int i = 0; i < 2; i++) {
				
				String tabella = null;
				if(i==0) {
					tabella = CostantiDB.SERVIZI_APPLICATIVI;
				}
				else {
					tabella = CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI;
				}

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				if(i>0) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI);
				}
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "saIdentificativo");
				//sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "nome", "saNome");
				//sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto", "saIdentificativoSoggetto");
				sqlQueryObject.addSelectAliasField(tabella, "cn_subject", "saCNSubject");
				sqlQueryObject.addSelectAliasField(tabella, "cn_issuer", "saCNIssuer");
				sqlQueryObject.addSelectAliasField(tabella, "cert_strict_verification", "saCertStrictVerificationr");
				sqlQueryObject.addSelectAliasField(tabella, "certificate", "saCertificate");
				
				if(i>0) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_CREDENZIALI+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
				// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
				boolean tokenWithHttpsEnabled = false;
				if(!tokenWithHttpsEnabled){
					sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
				}
				sqlQueryObject.addWhereCondition(tabella+".cn_subject = ?");
				sqlQueryObject.addWhereCondition(tabella+".cn_issuer = ?");
				//sqlQueryObject.addWhereCondition(tabella+".cert_strict_verification = ?");
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				String cnSubject = certificate.getSubject().getCN();
				String cnIssuer = certificate.getIssuer().getCN();
				stmt = con.prepareStatement(queryString);
				int indexStmt = 1;
				stmt.setString(indexStmt++, CredenzialeTipo.SSL.getValue());
				stmt.setString(indexStmt++, cnSubject);
				stmt.setString(indexStmt++, cnIssuer);
				// Il controllo serve ad evitare di caricare piu' applicativi con stesso certificato medesimo (indipendentemente dallo strict)
				// Se quindi sto creando un entita con strict abilitato, verifichero sotto tra i vari certificati la corrispondenza esatta, altrimenti una corrispondenza non esatta
	//			if(strictVerifier) {
	//				stmt.setInt(indexStmt++, CostantiDB.TRUE);
	//			}
	//			else {
	//				stmt.setInt(indexStmt++, CostantiDB.FALSE);
	//			}
				risultato = stmt.executeQuery();
	
				ServizioApplicativo sa;
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				while (risultato.next()) {
	
					// Possono esistere piu' servizi applicativi che hanno un CN con subject e issuer diverso.
					
					byte[] certificatoBytes = jdbcAdapter.getBinaryData(risultato, "saCertificate");
					Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
					//int tmpStrict = rs.getInt("cert_strict_verification");
					//boolean strict = tmpStrict == CostantiDB.TRUE;
					
					if(!certificate.equals(certificato.getCertificate(),strictVerifier)) {
						continue;
					}
					
					sa=this.getServizioApplicativo(risultato.getLong("saIdentificativo"));
					lista.add(sa);
				}
				risultato.close(); risultato=null;
				stmt.close(); stmt=null;
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiPrincipalList(String principal) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiPrincipalList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			sqlQueryObject.addWhereCondition("utente = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, CredenzialeTipo.PRINCIPAL.getValue());
			stmt.setString(2, principal);		
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.getServizioApplicativo(risultato.getLong("id"));

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<ServizioApplicativo> servizioApplicativoWithCredenzialiTokenList(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiTokenList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			
			List<FiltroRicercaProtocolPropertyConfig> modiFilter = null; 
			if(tokenWithHttpsEnabled){
			
				modiFilter = buildListModiTokenCredentials(tokenPolicy, tokenClientId);
				
				ISQLQueryObject sqlQueryObjectBuiltIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectBuiltIn.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObjectBuiltIn.addWhereCondition(false, "tipoauth = ?", "tipoauth = ?");
				sqlQueryObjectBuiltIn.addWhereCondition("token_policy = ?");
				sqlQueryObjectBuiltIn.addWhereCondition("utente = ?");
				sqlQueryObjectBuiltIn.setANDLogicOperator(true);
				
				ISQLQueryObject sqlQueryObjectModi = buildSQLQueryObjectModiTokenCredentials(modiFilter);
				
				sqlQueryObject.addWhereCondition(sqlQueryObjectBuiltIn.createSQLConditions());
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectModi);
				sqlQueryObject.setANDLogicOperator(false);
			}
			else {
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				sqlQueryObject.addWhereCondition("token_policy = ?");
				sqlQueryObject.addWhereCondition("utente = ?");
				sqlQueryObject.setANDLogicOperator(true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			
			int index = 1;
			if(tokenWithHttpsEnabled){
				stmt.setString(index++, CostantiConfigurazione.CREDENZIALE_TOKEN.toString());
				stmt.setString(index++, CostantiConfigurazione.CREDENZIALE_SSL.toString());
				stmt.setString(index++, tokenPolicy);
				stmt.setString(index++, tokenClientId);
				setSQLQueryObjectModiTokenCredentials (stmt, index, modiFilter);
			}
			else {
				stmt.setString(index++, CostantiConfigurazione.CREDENZIALE_TOKEN.toString());
				stmt.setString(index++, tokenPolicy);
				stmt.setString(index++, tokenClientId);
			}
			
			this.driver.logDebug("eseguo query (tokenPolicy:"+tokenPolicy+" tokenClientId:"+tokenClientId+"):" +queryString);
			
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.getServizioApplicativo(risultato.getLong("id"));

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}

	protected String[] soggettiServizioApplicativoList(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		String[] silList = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiServizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				silList = new String[risultato.getInt(1)];
			}
			risultato.close();
			stmt.close();

			if(silList!=null) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idSoggetto);
				risultato = stmt.executeQuery();
	
				int i = 0;
				while (risultato.next()) {
					silList[i] = risultato.getString("nome");
					i++;
				}
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}

	protected List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipoSA, 
			boolean bothSslAndToken, String tokenPolicy) throws DriverConfigurazioneException {
		return soggettiServizioApplicativoList(idSoggetto,superuser,credenziale, appId, tipoSA, bothSslAndToken, tokenPolicy, false);
	}
	protected List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipoSA, 
			boolean bothSslAndToken, String tokenPolicy, boolean tokenPolicyOR) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<IDServizioApplicativoDB> silList = new ArrayList<IDServizioApplicativoDB>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "idServAppl");
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "nome", "nomeServAppl");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			if(tipoSA!=null) {
				if(CostantiConfigurazione.CLIENT.equals(tipoSA)) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
				}
			}
			if(credenziale!=null) {
				if(tokenPolicyOR) {
					ISQLQueryObject sqlQueryObjectTrasporto = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectTrasporto.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObjectTrasporto.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
						sqlQueryObjectTrasporto.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer = ?");
					}
					sqlQueryObjectTrasporto.setANDLogicOperator(true);
					String trasporto = sqlQueryObjectTrasporto.createSQLConditions();
					
					ISQLQueryObject sqlQueryObjectToken = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectToken.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObjectToken.addWhereCondition(false, 
							CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
							CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
					if(tokenPolicy!=null) {
						sqlQueryObjectToken.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
					sqlQueryObjectToken.setANDLogicOperator(true);
					String token = sqlQueryObjectToken.createSQLConditions();
					
					sqlQueryObject.addWhereCondition(false, trasporto, token);
				}
				else {
					if(CredenzialeTipo.TOKEN.equals(credenziale)){
						if(bothSslAndToken) {
							sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
						}
						else {
							sqlQueryObject.addWhereCondition(false, 
									CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
									CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
						}
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
						if(CredenzialeTipo.SSL.equals(credenziale)){
							// I certificati caricati con una token policy potrebbero non dover essere considerati per l'autenticazione https
							// in futuro se serve gestire il boolean tokenWithHttpsEnabled anche per l'autenticazione https
							boolean tokenWithHttpsEnabled = false;
							if(!tokenWithHttpsEnabled){
								sqlQueryObject.addWhereIsNullCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy");
							}
						}
					}
					if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer = ?");
					}
					if(tokenPolicy!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
				}
			}
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, idSoggetto.getTipo());
			stmt.setString(index++, idSoggetto.getNome());
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(index++, superuser);
			if(tipoSA!=null) {
				stmt.setString(index++, tipoSA);
				if(CostantiConfigurazione.CLIENT.equals(tipoSA)) {
					stmt.setInt(index++, CostantiDB.TRUE);
				}
			}
			if(credenziale!=null) {
				if(tokenPolicyOR) {
					// trasporto
					stmt.setString(index++, credenziale.getValue());
					if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
						stmt.setString(index++, CostantiDB.getIssuerApiKey(appId));
					}
					
					// token
					stmt.setString(index++, CredenzialeTipo.TOKEN.getValue());
					stmt.setString(index++, CredenzialeTipo.SSL.getValue());
					if(tokenPolicy!=null) {
						stmt.setString(index++, tokenPolicy);
					}
				}
				else {
					if(CredenzialeTipo.TOKEN.equals(credenziale)){
						if(bothSslAndToken) {
							stmt.setString(index++, CredenzialeTipo.SSL.getValue());
						}
						else {
							stmt.setString(index++, credenziale.getValue());
							stmt.setString(index++, CredenzialeTipo.SSL.getValue());
						}
					}
					else {
						stmt.setString(index++, credenziale.getValue());
					}

					if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
						stmt.setString(index++, CostantiDB.getIssuerApiKey(appId));
					}
					if(tokenPolicy!=null) {
						stmt.setString(index++, tokenPolicy);
					}
				}
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				
				IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
				idSA.setIdSoggettoProprietario(new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto")));
				idSA.setNome(risultato.getString("nomeServAppl"));
				idSA.setId(risultato.getLong("idServAppl"));
				silList.add(idSA);
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<String> nomiProprietaSA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo) throws DriverConfigurazioneException {
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("nomiProprietaSA");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaSA] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS + ".nome");
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) || (tipoSoggettiProtocollo != null && tipoSoggettiProtocollo.size() > 0)) {
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI + ".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
			}
			
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome))) {
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			}
			
			if((tipoSoggettiProtocollo != null && tipoSoggettiProtocollo.size() > 0)) {
				String [] tipiServiziProtocolloS = tipoSoggettiProtocollo.toArray(new String[tipoSoggettiProtocollo.size()]); 
				sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipiServiziProtocolloS);
			}
			
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_PROPS + ".nome"); 
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if((filterSoggettoNome!=null && !"".equals(filterSoggettoNome))) {
				stmt.setString(1, filterSoggettoTipo);
				stmt.setString(2, filterSoggettoNome);
			}
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				lista.add(risultato.getString("nome"));
			}
			return lista;
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::nomiProprietaSA] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore, String tipo, 
			boolean checkIM, boolean checkConnettoreAbilitato) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<IDServizioApplicativoDB> lista = new ArrayList<IDServizioApplicativoDB>();

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getServiziApplicativiWithIdErogatore");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServiziApplicativiWithIdErogatore] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id", "idServAppl");
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "nome", "nomeServAppl");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			if(tipo != null) {
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
			}
			if(checkConnettoreAbilitato) {
				sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".id="+CostantiDB.SERVIZI_APPLICATIVI+".id_connettore_inv");
				if(checkIM) {
					sqlQueryObject.addWhereCondition(false,
							CostantiDB.SERVIZI_APPLICATIVI+".getmsginv = ? ",
							CostantiDB.CONNETTORI+".endpointtype <> ? " );
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.CONNETTORI+".endpointtype <> ? " );
				}
			}
			else if(checkIM) {
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".getmsginv = ? ");
			}
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			stm.setLong(index++, idErogatore);
			if(tipo != null) {
				stm.setString(index++, tipo);
			}
			if(checkConnettoreAbilitato) {
				if(checkIM) {
					stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(StatoFunzionalita.ABILITATO));
					stm.setString(index++, TipiConnettore.DISABILITATO.getNome());
				}
				else {
					stm.setString(index++, TipiConnettore.DISABILITATO.getNome());
				}
			}
			else if(checkIM) {
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(StatoFunzionalita.ABILITATO));
			}

			String debugQuery = DBUtils.formatSQLString(sqlQuery, idErogatore);
			if(tipo != null) {
				debugQuery = DBUtils.formatSQLString(debugQuery, tipo);
			}
			if(checkConnettoreAbilitato) {
				if(checkIM) {
					debugQuery = DBUtils.formatSQLString(debugQuery,  DriverConfigurazioneDB_LIB.getValue(StatoFunzionalita.ABILITATO));
					debugQuery = DBUtils.formatSQLString(debugQuery,  TipiConnettore.DISABILITATO.getNome());
				}
				else {
					debugQuery = DBUtils.formatSQLString(debugQuery,  TipiConnettore.DISABILITATO.getNome());
				}
			}
			else if(checkIM) {
				debugQuery = DBUtils.formatSQLString(debugQuery, DriverConfigurazioneDB_LIB.getValue(StatoFunzionalita.ABILITATO));
			}
			this.driver.logDebug("eseguo query : " + debugQuery);
			
			rs = stm.executeQuery();

			while (rs.next()) {
				
				IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
				idSA.setIdSoggettoProprietario(new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto")));
				idSA.setNome(rs.getString("nomeServAppl"));
				idSA.setId(rs.getLong("idServAppl"));
				lista.add(idSA);
			}

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException{

		IDSoggetto idSoggetto = idServizioApplicativo.getIdSoggettoProprietario();
		if(idSoggetto==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Soggetto Fruitore non Impostato.");
		if(idServizioApplicativo.getNome()==null || "".equals(idServizioApplicativo.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Servizio Applicativo non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Soggetto Fruitore non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Soggetto Fruitore non Impostato.");

		Connection con = null;

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existsServizioApplicativo");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			return DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(idServizioApplicativo.getNome(), idSoggetto.getTipo(), idSoggetto.getNome(), con, this.driver.tipoDB,this.driver.tabellaSoggetti)>0;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			this.driver.closeConnection(con);

		}

	}

	protected long getIdServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {

		if(idSoggetto==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Soggetto Fruitore non Impostato.");
		if(nomeServizioApplicativo==null || "".equals(nomeServizioApplicativo))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Servizio Applicativo non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Fruitore non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Tipo Soggetto Fruitore non Impostato.");

		Connection con = null;

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("getIdServizioApplicativo");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			return DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeServizioApplicativo, idSoggetto.getTipo(), idSoggetto.getNome(), con, this.driver.tipoDB,this.driver.tabellaSoggetti);
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			this.driver.closeConnection(con);

		}

	}

	protected boolean existsServizioApplicativoSoggetto(Long idSoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existsServizioApplicativoSoggetto");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativoSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);
			
			this.driver.closeConnection(con);

		}

	}
	
	protected boolean isServizioApplicativoInUsoComeErogatore(ServizioApplicativo sa, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;

		try {

			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("isServizioApplicativoInUsoComeErogatore");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::isServizioApplicativoInUsoComeErogatore] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			ArrayList<String> nomiPorteApplicative = new ArrayList<>();
			boolean isInUso=false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				isInUso=true;
				nomiPorteApplicative.add(risultato.getString("nome_porta"));
			}
			risultato.close();
			stmt.close();


			if(isInUso){
				if(whereIsInUso==null) whereIsInUso=new HashMap<ErrorsHandlerCostant, String>();

				whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, nomiPorteApplicative.toString());
			}

			return isInUso;

		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);

		}

	}
	
	protected List<ServizioApplicativo> getServiziApplicativiBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteApplicativeBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");

				ServizioApplicativo sa = this.getServizioApplicativo(id);

				// Check per validazioneSemantica

				if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()==0){
					// Una invocazione porta senza credenziali equivale a non avere i dati di invocazione porta per la PdD. 
					// ma l'interfaccia permette di farlo poiche' la configurazione avviene per step.
					sa.setInvocazionePorta(null);
				}

				if(sa.getInvocazioneServizio()!=null && 
						(sa.getInvocazioneServizio().getConnettore()==null || CostantiConfigurazione.DISABILITATO.equals(sa.getInvocazioneServizio().getConnettore().getTipo())) &&
						CostantiConfigurazione.DISABILITATO.equals(sa.getInvocazioneServizio().getGetMessage()) ){
					// Un' invocazione servizio senza connettore e senza getMessage equivale a non averlo per la PdD.
					// ma l'interfaccia permette di farlo poiche' la configurazione avviene per step.
					sa.setInvocazioneServizio(null);
				}

				if(sa.getRispostaAsincrona()!=null && 
						(sa.getRispostaAsincrona().getConnettore()==null || CostantiConfigurazione.DISABILITATO.equals(sa.getRispostaAsincrona().getConnettore().getTipo())) &&
						CostantiConfigurazione.DISABILITATO.equals(sa.getRispostaAsincrona().getGetMessage()) ){
					// Un' invocazione servizio senza connettore e senza getMessage equivale a non averlo per la PdD.
					// ma l'interfaccia permette di farlo poiche' la configurazione avviene per step.
					sa.setRispostaAsincrona(null);
				}

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<IDServizioApplicativo> getAllIdServiziApplicativi(
			FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug("getAllIdServiziApplicativi...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdServiziApplicativi");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				}
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".ora_registrazione < ?");
				if(filtroRicerca.getTipoSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome = ?");
				if(filtroRicerca.getIdRuolo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo = ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
				DBUtils.setPropertiesForSearch(sqlQueryObject, filtroRicerca.getProprieta(), CostantiDB.SERVIZI_APPLICATIVI, CostantiDB.SERVIZI_APPLICATIVI_PROPS, "nome", "valore", "id_servizio_applicativo");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca, CostantiDB.SERVIZI_APPLICATIVI);
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.logDebug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.logDebug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.driver.logDebug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.driver.logDebug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
					indexStmt++;
				}		
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getIdRuolo()!=null){
					this.driver.logDebug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getTipo()!=null){
					this.driver.logDebug("tipo stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				DBUtils.setPropertiesForSearch(stm, indexStmt, filtroRicerca.getProprieta(), this.driver.tipoDB, this.driver.log);
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroRicerca, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO);
			}
			rs = stm.executeQuery();
			List<IDServizioApplicativo> idsSA = new ArrayList<IDServizioApplicativo>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idS);
				idSA.setNome(rs.getString("nome"));
				idsSA.add(idSA);
			}
			if(idsSA.size()==0){
				if(filtroRicerca!=null)
					throw new DriverConfigurazioneNotFound("ServiziApplicativi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverConfigurazioneNotFound("ServiziApplicativi non trovati");
			}else{
				return idsSA;
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdServiziApplicativi error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}

	}
	
	protected long getIdServizioApplicativoByConnettore(long idConnettore) throws DriverConfigurazioneException {
		String nomeMetodo = "getIdServizioApplicativoByConnettore";

		Connection con = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_connettore_inv=?");
			sqlQueryObject.addWhereCondition("id_connettore_risp=?");
			sqlQueryObject.setANDLogicOperator(false);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idConnettore);
			stmt.setLong(2, idConnettore);
			risultato = stmt.executeQuery();
			long idSA = -1;
			if (risultato.next()) {
				idSA = risultato.getLong("id");
			}			
			return idSA;

		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
	protected IDServizio getLabelNomeServizioApplicativo(String nomeServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		// viene inserito come azione dell'IDServizio
		
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stm = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("findAllAllarmi");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::findAllAllarmi] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI + ".id");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI + ".tipo");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome=?");

			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nomeServizioApplicativo);

			this.driver.logDebug("eseguo query: " + sqlQuery);
			
			long idSA = -1;
			String tipo = null;
			rs = stm.executeQuery();
			if(rs.next()) {
				idSA = rs.getLong("id");
				tipo = rs.getString("tipo");
			}
			rs.close(); rs=null;
			stm.close(); stm = null;

			if(idSA>0) {

				if(CostantiConfigurazione.SERVER.equals(tipo)){
					return null; // non serve normalizzazione, si puo' usare il nome stesso
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE + ".behaviour");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SA + ".connettore_nome");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE + ".tipo_servizio");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE + ".servizio");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE + ".versione_servizio");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE + ".id="+CostantiDB.PORTE_APPLICATIVE_SA + ".id_porta");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id="+CostantiDB.PORTE_APPLICATIVE + ".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA + ".id_servizio_applicativo=?");

				sqlQuery = sqlQueryObject.createSQLQuery();
				
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idSA);

				this.driver.logDebug("eseguo query: " + sqlQuery);
				
				List<IDServizio> nomiConnettore = new ArrayList<IDServizio>();
				rs = stm.executeQuery();
				while(rs.next()) {
					
					String behaviour = rs.getString("behaviour");
					String nomeConnettore = rs.getString("connettore_nome");
					
					String tipo_soggetto = rs.getString("tipo_soggetto");
					String nome_soggetto = rs.getString("nome_soggetto");
					String tipo_servizio = rs.getString("tipo_servizio");
					String servizio = rs.getString("servizio");
					int versione_servizio = rs.getInt("versione_servizio");
					
					IDServizio idServizio = IDServizioUtils.buildIDServizio(tipo_servizio, servizio,
							new IDSoggetto(tipo_soggetto, nome_soggetto),
							versione_servizio);
					
					if(nomeConnettore!=null && !"".equals(nomeConnettore)) {
						idServizio.setAzione(nomeConnettore);
					}
					else {
						if(behaviour!=null && !"".equals(behaviour)) {
							idServizio.setAzione(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
						}
					}

					nomiConnettore.add(idServizio);
				}
				rs.close(); rs=null;
				stm.close(); stm = null;
				
				if(!nomiConnettore.isEmpty() && nomiConnettore.size()==1) {
					return nomiConnettore.get(0);
				}
				// else esistono più associazione e non e' di tipo server ???
			}
			
			return null; // normalizzazione non riuscita, si puo' usare il nome stesso
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::findAllAllarmi] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
	}
}
