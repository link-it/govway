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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
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
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_soggettiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_soggettiCredenzialiDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_soggettiDriver soggettiDriver = null;
	
	protected DriverRegistroServiziDB_soggettiCredenzialiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.soggettiDriver = new DriverRegistroServiziDB_soggettiDriver(driver);
	}
	
	
	protected Soggetto soggettoWithCredenzialiBasic(String utente, String password, boolean checkPassword) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettoWithCredenzialiBasic";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		Soggetto sogg = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("id");
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

			if (risultato.next()) {

				sogg=this.soggettiDriver.getSoggetto(risultato.getLong("id"));

			}

			return sogg;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected Soggetto soggettoWithCredenzialiApiKey(String utente, boolean appId) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettoWithCredenzialiApiKey";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		Soggetto sogg = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			sqlQueryObject.addWhereCondition("utente = ?");
			sqlQueryObject.addWhereCondition("issuer = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, CredenzialeTipo.APIKEY.getValue());
			stmt.setString(index++, utente);
			stmt.setString(index++, CostantiDB.getISSUER_APIKEY(appId));
			risultato = stmt.executeQuery();

			if (risultato.next()) {

				sogg=this.soggettiDriver.getSoggetto(risultato.getLong("id"));

			}

			return sogg;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected Soggetto getSoggettoByCredenzialiBasic(
			String user,String password, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.getEngineSoggettoAutenticato(CredenzialeTipo.BASIC, user, password, 
				null, null, null, false, 
				null,
				config,
				false,
				false);
	}
	
	protected Soggetto getSoggettoByCredenzialiApiKey(
			String user,String password, boolean appId, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.getEngineSoggettoAutenticato(CredenzialeTipo.APIKEY, user, password, 
				null, null, null, false, 
				null,
				config,
				appId,
				false);
	}
	
	protected Soggetto getSoggettoByCredenzialiSsl(
			String subject, String issuer) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try {
			return this.getEngineSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
					subject, issuer, null, false,
					null,
					null,
					false,
					false);
		}catch(DriverRegistroServiziNotFound notFound) {
			// provo a cercare in credenziali ulteriori
			return this.getEngineSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
					subject, issuer, null, false,
					null,
					null,
					false,
					true);
		}
	}
	
	protected Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try {
			return this.getEngineSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
					null, null, certificate, strictVerifier,
					null,
					null,
					false,
					false);
		}catch(DriverRegistroServiziNotFound notFound) {
			// provo a cercare in credenziali ulteriori
			return this.getEngineSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
					null, null, certificate, strictVerifier,
					null,
					null,
					false,
					true);
		}
	}
	
	protected Soggetto getSoggettoByCredenzialiPrincipal(
			String principal) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.getEngineSoggettoAutenticato(CredenzialeTipo.PRINCIPAL, null, null, 
				null, null, null, false,
				principal,
				null,
				false,
				false);
	}
	
	private Soggetto getEngineSoggettoAutenticato(
			CredenzialeTipo tipoCredenziale, String user,String password, 
			String aSubject, String aIssuer, CertificateInfo aCertificate, boolean aStrictVerifier, 
			String principal,
			CryptConfig config,
			boolean appId,
			boolean searchInCredenzialiUlteriori) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		// conrollo consistenza
		if (tipoCredenziale == null)
			throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro tipoCredenziale is null");

		switch (tipoCredenziale) {
		case BASIC:
			if (user == null || "".equalsIgnoreCase(user))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro user is null (required for basic auth)");
			if (password == null || "".equalsIgnoreCase(password))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro password is null (required for basic auth)");
			break;
		case APIKEY:
			if (user == null || "".equalsIgnoreCase(user))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro user is null (required for apikey auth)");
			if (password == null || "".equalsIgnoreCase(password))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro password is null (required for apikey auth)");
			break;
		case SSL:
			if ( (aSubject == null || "".equalsIgnoreCase(aSubject)) && (aCertificate==null))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro subject/certificate is null (required for ssl auth)");
			break;
		case PRINCIPAL:
			if (principal == null || "".equalsIgnoreCase(principal))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro principal is null (required for principal auth)");
			break;
		}

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettoAutenticato");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggettoAutenticato] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		IDSoggetto idSoggetto = null;
		try {
			String tabella = CostantiDB.SOGGETTI;
			if(searchInCredenzialiUlteriori) {
				tabella = CostantiDB.SOGGETTI_CREDENZIALI;
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			if(searchInCredenzialiUlteriori) {
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_CREDENZIALI);
			}
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			if(searchInCredenzialiUlteriori) {
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_CREDENZIALI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			}
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			switch (tipoCredenziale) {
			case BASIC:
				sqlQueryObject.addSelectField("password");
				
				sqlQueryObject.addWhereCondition("utente = ?");
				//sqlQueryObject.addWhereCondition("password = ?");
				break;
			case APIKEY:
				sqlQueryObject.addSelectField("password");
				
				sqlQueryObject.addWhereCondition("utente = ?");
				//sqlQueryObject.addWhereCondition("password = ?");
				sqlQueryObject.addWhereCondition("issuer = ?");
				break;
			case SSL:
				if(aSubject!=null && !"".equals(aSubject)) {
					
					sqlQueryObject.addSelectAliasField(tabella, "subject", "soggettoSubject");
					sqlQueryObject.addSelectAliasField(tabella, "issuer", "soggettoIssuer");				
					
					// Autenticazione SSL deve essere LIKE
					Map<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoMap(aSubject, PrincipalType.SUBJECT);
					Map<String, List<String>> hashIssuer = null;
					if(StringUtils.isNotEmpty(aIssuer)) {
						hashIssuer = CertificateUtils.getPrincipalIntoMap(aIssuer, PrincipalType.ISSUER);
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
				}
				else {
				
					sqlQueryObject.addSelectAliasField(tabella, "cn_subject", "soggettoCNSubject");
					sqlQueryObject.addSelectAliasField(tabella, "cn_issuer", "soggettoCNIssuer");
					sqlQueryObject.addSelectAliasField(tabella, "cert_strict_verification", "soggettoCertStrictVerification");
					sqlQueryObject.addSelectAliasField(tabella, "certificate", "soggettoCertificate");
					
					// ricerca per certificato
					sqlQueryObject.addWhereCondition(tabella+".cn_subject = ?");
					sqlQueryObject.addWhereCondition(tabella+".cn_issuer = ?");
					sqlQueryObject.addWhereCondition(tabella+".cert_strict_verification = ?");
					
				}
				break;
			case PRINCIPAL:
				sqlQueryObject.addWhereCondition("utente = ?");
				break;
			}
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, tipoCredenziale.toString());
			switch (tipoCredenziale) {
			case BASIC:
				stm.setString(index++, user);
				//stm.setString(index++, password);
				this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipoCredenziale.toString(), user));
				break;
			case APIKEY:
				stm.setString(index++, user);
				//stm.setString(index++, password);
				stm.setString(index++, CostantiDB.getISSUER_APIKEY(appId));
				this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipoCredenziale.toString(), user, CostantiDB.getISSUER_APIKEY(appId)));
				break;
			case SSL:
				if(aSubject!=null && !"".equals(aSubject)) {
					this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipoCredenziale.toString()));
				}
				else {
					String cnSubject = aCertificate.getSubject().getCN();
					String cnIssuer = aCertificate.getIssuer().getCN();
					
					stm.setString(index++, cnSubject);
					stm.setString(index++, cnIssuer);
					if(aStrictVerifier) {
						stm.setInt(index++, CostantiDB.TRUE);
					}
					else {
						stm.setInt(index++, CostantiDB.FALSE);
					}
					this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipoCredenziale.toString(), cnSubject, cnIssuer,
							(aStrictVerifier? CostantiDB.TRUE : CostantiDB.FALSE)));
				}
				break;
			case PRINCIPAL:
				stm.setString(index++, principal);
				this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipoCredenziale.toString(), principal));
				break;
			}

			rs = stm.executeQuery();

			if(CredenzialeTipo.BASIC.equals(tipoCredenziale) || CredenzialeTipo.APIKEY.equals(tipoCredenziale)) {
				
				boolean testInChiaro = false;
				ICrypt crypt = null;
				if(CredenzialeTipo.BASIC.equals(tipoCredenziale)) {
					if(config==null || config.isBackwardCompatibility()) {
						testInChiaro = true;
					}
					if(config!=null) {
						crypt = CryptFactory.getCrypt(this.driver.log, config);
					}
				}
				else {
					if(config!=null) {
						crypt = CryptFactory.getCrypt(this.driver.log, config);
					}
					else {
						testInChiaro = true;
					}
				}
				
				while(rs.next()){
					String passwordDB =  rs.getString("password");
					
					boolean found = false;
					if(testInChiaro) {
						found = password.equals(passwordDB);
					}
					if(!found && crypt!=null) {
						found = crypt.check(password, passwordDB);
					}
					
					if( found ) {
						idSoggetto = new IDSoggetto();
						idSoggetto.setTipo(rs.getString("tipo_soggetto"));
						idSoggetto.setNome(rs.getString("nome_soggetto"));
						break;
					}
				}
				
			}
			else if(CredenzialeTipo.SSL.equals(tipoCredenziale)) {
				
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				
				while(rs.next()){
					
					if(aSubject!=null && !"".equals(aSubject)) {
					
						// Possono esistere piu' soggetti che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
											
						String subjectPotenziale =  rs.getString("soggettoSubject");
						boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, aSubject, PrincipalType.SUBJECT, this.driver.log);
						
						boolean issuerValid = true;
						if(StringUtils.isNotEmpty(aIssuer)) {
							String issuerPotenziale =  rs.getString("soggettoIssuer");
							if(StringUtils.isNotEmpty(issuerPotenziale)) {
								issuerValid = CertificateUtils.sslVerify(issuerPotenziale, aIssuer, PrincipalType.ISSUER, this.driver.log);
							}
							else {
								issuerValid = false;
							}
						}
						
						
						if( subjectValid && issuerValid ) {
							idSoggetto = new IDSoggetto();
							idSoggetto.setTipo(rs.getString("tipo_soggetto"));
							idSoggetto.setNome(rs.getString("nome_soggetto"));
							break;
						}
					}
					else {
						
						// ricerca per certificato
						// Possono esistere piu' soggetti che hanno un CN con subject e issuer diverso.
						
						byte[] certificatoBytes = jdbcAdapter.getBinaryData(rs, "soggettoCertificate");
						Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
						//int tmpStrict = rs.getInt("cert_strict_verification");
						//boolean strict = tmpStrict == CostantiDB.TRUE;
						
						if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
							idSoggetto = new IDSoggetto();
							idSoggetto.setTipo(rs.getString("tipo_soggetto"));
							idSoggetto.setNome(rs.getString("nome_soggetto"));
							break;
						}
					}
				}
				
			}
			else {
				if (rs.next()) {
					idSoggetto = new IDSoggetto();
					idSoggetto.setTipo(rs.getString("tipo_soggetto"));
					idSoggetto.setNome(rs.getString("nome_soggetto"));
				}
			}

		}catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggettoAutenticato] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggettoAutenticato] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
		if(idSoggetto!=null){
			return this.soggettiDriver.getSoggetto(idSoggetto);
		}
		else{
			throw new DriverRegistroServiziNotFound("Nessun soggetto trovato che possiede le credenziali '"+tipoCredenziale.toString()+"' fornite");
		}
	}
	
	protected List<Soggetto> soggettoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettoWithCredenzialiSslList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			for (int i = 0; i < 2; i++) {
				
				String tabella = null;
				if(i==0) {
					tabella = CostantiDB.SOGGETTI;
				}
				else {
					tabella = CostantiDB.SOGGETTI_CREDENZIALI;
				}
			
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				if(i>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_CREDENZIALI);
				}
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "id", "soggettoIdentificativo");
				sqlQueryObject.addSelectAliasField(tabella, "cn_subject", "soggettoCNSubject");
				sqlQueryObject.addSelectAliasField(tabella, "cn_issuer", "soggettoCNIssuer");
				sqlQueryObject.addSelectAliasField(tabella, "cert_strict_verification", "soggettoCertStrictVerification");
				sqlQueryObject.addSelectAliasField(tabella, "certificate", "soggettoCertificate");
				
				if(i>0) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_CREDENZIALI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
				
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
	
				Soggetto soggetto;
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
				while (risultato.next()) {
	
					// Possono esistere piu' servizi applicativi che hanno un CN con subject e issuer diverso.
					
					byte[] certificatoBytes = jdbcAdapter.getBinaryData(risultato, "soggettoCertificate");
					Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
					//int tmpStrict = rs.getInt("cert_strict_verification");
					//boolean strict = tmpStrict == CostantiDB.TRUE;
					
					if(!certificate.equals(certificato.getCertificate(),strictVerifier)) {
						continue;
					}
					
					soggetto=this.soggettiDriver.getSoggetto(risultato.getLong("soggettoIdentificativo"));
					lista.add(soggetto);
				}
				risultato.close(); risultato=null;
				stmt.close(); stmt=null;
				
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			
			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<IDSoggettoDB> getSoggettiFromTipoAutenticazione(List<String> tipiSoggetto, String superuser, CredenzialeTipo credenziale, Boolean appId, PddTipologia pddTipologia) throws DriverRegistroServiziException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;
		int offset = 0;
		int limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
		
		this.driver.log.debug("getSoggettiFromTipoAutenticazione...");
		
		List<IDSoggettoDB> soggetti= null;
		
		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getSoggettiFromTipoAutenticazione");
			else
				con = this.driver.globalConnection;
		
			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"id", "idTableSoggetto");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			if(tipiSoggetto!=null && tipiSoggetto.size()>0) {
				sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipiSoggetto.toArray(new String[1]));
			}
			if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			if(credenziale != null) {
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
					sqlQueryObject.addWhereCondition("issuer = ?");
				}
			}
			if(pddTipologia!=null) {
				if(PddTipologia.ESTERNO.equals(pddTipologia)) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());									
				}
				else {
					sqlQueryObject.addFromTable(CostantiDB.PDD);
					sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
				}
			}
			
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && !superuser.equals(""))
				stmt.setString(index++, superuser);
			if(credenziale != null) {
				stmt.setString(index++, credenziale.toString());
				if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
					stmt.setString(index++, CostantiDB.getISSUER_APIKEY(appId));
				}
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			
			risultato = stmt.executeQuery();
			
			soggetti = new ArrayList<IDSoggettoDB>();
			while (risultato.next()) {

				IDSoggettoDB sog = new IDSoggettoDB();
				sog.setId(risultato.getLong("idTableSoggetto"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				soggetti.add(sog);
			}

			return soggetti;
		} catch (Exception qe) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggettiFromTipoAutenticazione] Exception: " + qe.getMessage(),qe);
		} finally {
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
	
}
