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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.RuoloSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
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
public class DriverRegistroServiziDB_soggettiDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	protected DriverRegistroServiziDB_soggettiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(driver);
	}
	
	protected org.openspcoop2.core.registry.Soggetto getSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idSoggetto == null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametro idSoggetto is null");

		String nomeSogg = idSoggetto.getNome();
		String tipoSogg = idSoggetto.getTipo();

		if (nomeSogg == null || nomeSogg.trim().equals("") || tipoSogg == null || tipoSogg.trim().equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] : soggetto non specificato.");

		long idSoggettoLong;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggetto(idSoggetto)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nomeSogg);
			stm.setString(2, tipoSogg);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSogg, tipoSogg));
			rs = stm.executeQuery();

			if (rs.next()) {
				idSoggettoLong = rs.getLong("id");
				
			}else{
				throw new DriverRegistroServiziNotFound("Nessun soggetto trovato eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSogg, tipoSogg));
			}
			
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
		return this.getSoggetto(idSoggettoLong);
	}

	
	protected Soggetto getSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getSoggetto(idSoggetto,null);
	}
	protected Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		// conrollo consistenza
		if (idSoggetto <= 0)
			return null;

		Soggetto soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				soggetto = new Soggetto();

				soggetto.setId(rs.getLong("id"));

				String nomeSogg = rs.getString("nome_soggetto");
				soggetto.setNome(nomeSogg);

				String tipoSogg = rs.getString("tipo_soggetto");
				soggetto.setTipo(tipoSogg);

				String tmp = rs.getString("descrizione");
				soggetto.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("identificativo_porta");
				soggetto.setIdentificativoPorta(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("server");
				soggetto.setPortaDominio(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("superuser");
				soggetto.setSuperUser(((tmp == null || tmp.equals("")) ? null : tmp));

				if(rs.getInt("privato")==1)
					soggetto.setPrivato(true);
				else
					soggetto.setPrivato(false);

				tmp = rs.getString("codice_ipa");
				soggetto.setCodiceIpa(((tmp == null || tmp.equals("")) ? null : tmp));

				//	Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					soggetto.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				long idConnettore = rs.getLong("id_connettore");
				Connettore connettore = this.driver.getConnettore(idConnettore, con);

				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					soggetto.setVersioneProtocollo(profilo);
				}

				String tipoAuth = rs.getString("tipoauth");
				if(tipoAuth != null && !tipoAuth.equals("")){
					CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
					credenziali.setTipo(DriverRegistroServiziDB_LIB.getEnumCredenzialeTipo(tipoAuth));
					
					credenziali.setUser(rs.getString("utente"));		
					credenziali.setPassword(rs.getString("password"));
					
					if(org.openspcoop2.core.registry.constants.CredenzialeTipo.APIKEY.equals(credenziali.getTipo())) {
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
								
					soggetto.addCredenziali( credenziali );
				}
				
				// aggiungo connettore
				soggetto.setConnettore(connettore);

				// Proprieta Oggetto
				soggetto.setProprietaOggetto(DriverRegistroServiziDB_utilsDriver.readProprietaOggetto(rs,false));
				
				rs.close();
				stm.close();
				
				
				// RUOLI
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, soggetto.getId());
				this.driver.logDebug("eseguo query ruoli: " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, soggetto.getId()));
				rs = stm.executeQuery();
				while (rs.next()) {
					if(soggetto.getRuoli()==null){
						soggetto.setRuoli(new RuoliSoggetto());
					}	
					RuoloSoggetto ruolo = new RuoloSoggetto();
					ruolo.setId(rs.getLong("id_ruolo"));
					soggetto.getRuoli().addRuolo(ruolo);
				}
				rs.close();
				stm.close();
				
				if(soggetto.getRuoli()!=null && soggetto.getRuoli().sizeRuoloList()>0){
					for (int i = 0; i < soggetto.getRuoli().sizeRuoloList(); i++) {
						Ruolo ruolo = this.driver.getRuolo(con,soggetto.getRuoli().getRuolo(i).getId());
						soggetto.getRuoli().getRuolo(i).setNome(ruolo.getNome());
					}
				}
				
				
				// CREDENZIALI
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_CREDENZIALI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id", true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, soggetto.getId());
				this.driver.logDebug("eseguo query credenziali: " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, soggetto.getId()));
				rs = stm.executeQuery();
				while (rs.next()) {
					CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
					credenziali.setTipo(DriverRegistroServiziDB_LIB.getEnumCredenzialeTipo(tipoAuth));
					
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
								
					soggetto.addCredenziali( credenziali );
				}
				rs.close();
				stm.close();
				
				
				// PROPERTIES
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, soggetto.getId());
				this.driver.logDebug("eseguo query ruoli: " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, soggetto.getId()));
				rs = stm.executeQuery();
				while (rs.next()) {
					
					Proprieta proprieta = new Proprieta();
					proprieta.setNome(rs.getString("nome"));
					proprieta.setValore(rs.getString("valore"));
					soggetto.addProprieta(proprieta);

				}
				rs.close();
				stm.close();
				
				
				
			}else{
				throw new DriverRegistroServiziNotFound("Nessun risultato trovato eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			}

			// Protocol Properties
			try{
				List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(soggetto.getId(), ProprietariProtocolProperty.SOGGETTO, con, this.driver.tipoDB);
				if(listPP!=null && listPP.size()>0){
					for (ProtocolProperty protocolProperty : listPP) {
						soggetto.addProtocolProperty(protocolProperty);
					}
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			
			return soggetto;
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}
	}
	
	
	
	protected List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug("getAllIdSoggettiRegistro...");

		Certificate certificatoFiltro = null;
		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdSoggetti");
			else
				con = this.driver.globalConnection;

			boolean testInChiaro = false;
			ICrypt crypt = null;
			if(filtroRicerca!=null && filtroRicerca.getCredenzialiSoggetto()!=null && filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
				CredenzialeTipo cTipo = filtroRicerca.getCredenzialiSoggetto().getTipo();
				if(CredenzialeTipo.BASIC.equals(cTipo)){
					CryptConfig config = filtroRicerca.getCryptConfig();
					if(config==null || config.isBackwardCompatibility()) {
						testInChiaro = true;
					}
					if(config!=null) {
						try {
							crypt = CryptFactory.getCrypt(this.driver.log, config);
						}catch(Exception e) {
							throw new DriverRegistroServiziException(e.getMessage(),e);
						}
					}
				}
				else if(CredenzialeTipo.APIKEY.equals(cTipo)){
					CryptConfig config = filtroRicerca.getCryptConfig();
					if(config!=null) {
						try {
							crypt = CryptFactory.getCrypt(this.driver.log, config);
						}catch(Exception e) {
							throw new DriverRegistroServiziException(e.getMessage(),e);
						}
					}
					else {
						testInChiaro = true;
					}
				}
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
					sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				}
			}
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.setSelectDistinct(true);
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".ora_registrazione < ?");
				// Filtro By Tipo e Nome
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				// Filtro By Pdd
				if(filtroRicerca.getNomePdd()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".server = ?");
				// Filtro By Ruoli
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_RUOLI+".id_ruolo="+CostantiDB.RUOLI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".nome = ?");
				}
				// Filtro By Credenziali (per ssl vengono cercate solo nelle credenziali principali)
				if(filtroRicerca.getCredenzialiSoggetto()!=null){
					if(filtroRicerca.getCredenzialiSoggetto().getTipo()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
						if(CredenzialeTipo.APIKEY.equals(filtroRicerca.getCredenzialiSoggetto().getTipo())){
							sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".cert_strict_verification = ?");
						}
					}
					if(filtroRicerca.getCredenzialiSoggetto().getUser()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".utente = ?");
					}
					if(filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
						//sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".password = ?");
						sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".password");
					}
					if(filtroRicerca.getCredenzialiSoggetto().getSubject()!=null){
						
						// Autenticazione SSL deve essere LIKE
						Map<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoMap(filtroRicerca.getCredenzialiSoggetto().getSubject(), PrincipalType.SUBJECT);
						Map<String, List<String>> hashIssuer = null;
						if(filtroRicerca.getCredenzialiSoggetto().getIssuer()!=null) {
							hashIssuer = CertificateUtils.getPrincipalIntoMap(filtroRicerca.getCredenzialiSoggetto().getIssuer(), PrincipalType.ISSUER);
						}
						
						for (String key : hashSubject.keySet()) {
							List<String> listValues = hashSubject.get(key);
							for (String value : listValues) {
								sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
							}
							
							sqlQueryObject.addSelectField("subject");
						}
						
						if(hashIssuer!=null) {
							for (String key : hashIssuer.keySet()) {
								List<String> listValues = hashIssuer.get(key);
								for (String value : listValues) {
									sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
								}
							}
							
							sqlQueryObject.addSelectField("issuer");
						}
						else {
							sqlQueryObject.addWhereIsNullCondition("issuer");
						}
						
					}
					if(filtroRicerca.getCredenzialiSoggetto().getCertificate()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".cn_subject = ?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".cn_issuer = ?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".cert_strict_verification = ?");
						
						sqlQueryObject.addSelectField("certificate");
					}
				}
				DBUtils.setPropertiesForSearch(sqlQueryObject, filtroRicerca.getProprieta(), CostantiDB.SOGGETTI, CostantiDB.SOGGETTI_PROPS, "nome", "valore", "id_soggetto");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca, CostantiDB.SOGGETTI);
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
				if(filtroRicerca.getTipo()!=null){
					this.driver.logDebug("tipoSoggetto stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nomeSoggetto stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getNomePdd()!=null){
					this.driver.logDebug("nomePdD stmt.setString("+filtroRicerca.getNomePdd()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePdd());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuolo()!=null){
					this.driver.logDebug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getCredenzialiSoggetto()!=null){
					if(filtroRicerca.getCredenzialiSoggetto().getTipo()!=null){
						this.driver.logDebug("credenziali.tipo stmt.setString("+filtroRicerca.getCredenzialiSoggetto().getTipo().getValue()+")");
						stm.setString(indexStmt, filtroRicerca.getCredenzialiSoggetto().getTipo().getValue());
						indexStmt++;
						if(CredenzialeTipo.APIKEY.equals(filtroRicerca.getCredenzialiSoggetto().getTipo())){
							int v = filtroRicerca.getCredenzialiSoggetto().isCertificateStrictVerification() ?  CostantiDB.TRUE :  CostantiDB.FALSE;
							this.driver.logDebug("credenziali.certificateStrictVerification stmt.setInt("+v+")");
							stm.setInt(indexStmt, v);
							indexStmt++;
						}
					}
					if(filtroRicerca.getCredenzialiSoggetto().getUser()!=null){
						this.driver.logDebug("credenziali.user stmt.setString("+filtroRicerca.getCredenzialiSoggetto().getUser()+")");
						stm.setString(indexStmt, filtroRicerca.getCredenzialiSoggetto().getUser());
						indexStmt++;
					}
//					if(filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
//						this.driver.logDebug("credenziali.password stmt.setString("+filtroRicerca.getCredenzialiSoggetto().getPassword()+")");
//						stm.setString(indexStmt, filtroRicerca.getCredenzialiSoggetto().getPassword());
//						indexStmt++;
//					}
					if(filtroRicerca.getCredenzialiSoggetto().getSubject()!=null){
						// nop;
					}
					if(filtroRicerca.getCredenzialiSoggetto().getCertificate()!=null){
						
						certificatoFiltro = ArchiveLoader.load(ArchiveType.CER, filtroRicerca.getCredenzialiSoggetto().getCertificate(), 0, null);	
						String cnSubject = certificatoFiltro.getCertificate().getSubject().getCN();
						String cnIssuer = certificatoFiltro.getCertificate().getIssuer().getCN();
						
						stm.setString(indexStmt++, cnSubject);
						this.driver.logDebug("credenziali.cnSubject stmt.setString(" + cnSubject +")");
						stm.setString(indexStmt++, cnIssuer);
						this.driver.logDebug("credenziali.cnIssuer stmt.setString(" + cnIssuer +")");
						if(filtroRicerca.getCredenzialiSoggetto().isCertificateStrictVerification()) {
							this.driver.logDebug("credenziali.strict stmt.setInt(" + CostantiDB.TRUE +")");
							stm.setInt(indexStmt++, CostantiDB.TRUE);
						}
						else {
							this.driver.logDebug("credenziali.strict stmt.setInt(" + CostantiDB.FALSE +")");
							stm.setInt(indexStmt++, CostantiDB.FALSE);
						}
					}
				}
				DBUtils.setPropertiesForSearch(stm, indexStmt, filtroRicerca.getProprieta(), this.driver.tipoDB, this.driver.log);
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroRicerca, ProprietariProtocolProperty.SOGGETTO);
			}
			rs = stm.executeQuery();
			List<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.driver.tipoDB);
			while (rs.next()) {
				
				if(filtroRicerca!=null &&
						filtroRicerca.getCredenzialiSoggetto()!=null && 
						filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
					String passwordDB =  rs.getString("password");
					
					boolean found = false;
					if(testInChiaro) {
						found = filtroRicerca.getCredenzialiSoggetto().getPassword().equals(passwordDB);
					}
					if(!found && crypt!=null) {
						found = crypt.check(filtroRicerca.getCredenzialiSoggetto().getPassword(), passwordDB);
					}
					if( !found ) {
						continue;
					}
				}
				
				if(filtroRicerca!=null &&
						filtroRicerca.getCredenzialiSoggetto()!=null && 
						filtroRicerca.getCredenzialiSoggetto().getSubject()!=null){
				
					// Possono esistere piu' soggetti che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
										
					String subjectPotenziale =  rs.getString("subject");
					boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, filtroRicerca.getCredenzialiSoggetto().getSubject(), PrincipalType.SUBJECT, this.driver.log);
					
					boolean issuerValid = true;
					if(filtroRicerca.getCredenzialiSoggetto().getIssuer()!=null) {
						String issuerPotenziale =  rs.getString("issuer");
						if(StringUtils.isNotEmpty(issuerPotenziale)) {
							issuerValid = CertificateUtils.sslVerify(issuerPotenziale, filtroRicerca.getCredenzialiSoggetto().getIssuer(), PrincipalType.ISSUER, this.driver.log);
						}
						else {
							issuerValid = false;
						}
					}
					
					if( !subjectValid || !issuerValid ) {
						continue;
					}
				}
				
				if(certificatoFiltro!=null) {

					// ricerca per certificato
					// Possono esistere piu' soggetti che hanno un CN con subject e issuer diverso.
					
					byte[] certificatoBytes = jdbcAdapter.getBinaryData(rs, "certificate");
					Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
					//int tmpStrict = rs.getInt("cert_strict_verification");
					//boolean strict = tmpStrict == CostantiDB.TRUE;
					
					if(!certificatoFiltro.getCertificate().equals(certificato.getCertificate(),filtroRicerca.getCredenzialiSoggetto().isCertificateStrictVerification())) {
						continue;
					}
				}
								
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				idSoggetti.add(idS);
			}
			if(idSoggetti.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Soggetti non trovati");
			}else{
				return idSoggetti;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdSoggettiRegistro error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}

	protected void createSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 1");
			// creo soggetto
			DriverRegistroServiziDB_soggettiLIB.CRUDSoggetto(1, soggetto, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsSoggetto(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			exist = existsSoggetto(con, idSoggetto);

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			this.driver.closeConnection(con);
		}

		return exist;
	}
	protected boolean existsSoggetto(Connection conParam, IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		boolean exist = false;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (idSoggetto == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		String nome_soggetto = idSoggetto.getNome();
		String tipo_soggetto = idSoggetto.getTipo();
		if (nome_soggetto == null || nome_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Nome non valido");
		if (tipo_soggetto == null || tipo_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Tipo non valido");

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(sqlQuery);
			stm.setString(1, nome_soggetto);
			stm.setString(2, tipo_soggetto);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

		}

		return exist;
	}

	protected boolean existsSoggetto(long idSoggetto) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsSoggetto(longId)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected boolean existsSoggetto(String codiceIPA) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (codiceIPA == null|| codiceIPA.equals(""))
			throw new DriverRegistroServiziException("Parametro non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsSoggetto(codiceIPA)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("codice_ipa = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, codiceIPA);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected Soggetto getSoggetto(String codiceIPA) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto = -1;

		if (codiceIPA == null|| codiceIPA.equals(""))
			throw new DriverRegistroServiziException("Parametro non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggetto(codiceIPA)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("codice_ipa = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, codiceIPA);
			rs = stm.executeQuery();
			if (rs.next()){
				idSoggetto = rs.getLong("id");
			}
			rs.close();
			stm.close();

		} catch (Exception e) {
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		if(idSoggetto<=0){
			throw new DriverRegistroServiziNotFound("Soggetto con Codice IPA ["+codiceIPA+"] non trovato");
		}
		else{
			return this.getSoggetto(idSoggetto);
		}

	}

	protected String getCodiceIPA(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String codiceIPA = null;
		if (idSoggetto == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		String nome_soggetto = idSoggetto.getNome();
		String tipo_soggetto = idSoggetto.getTipo();
		if (nome_soggetto == null || nome_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Nome non valido");
		if (tipo_soggetto == null || tipo_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Tipo non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getCodiceIPA(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, tipo_soggetto);
			stm.setString(2, nome_soggetto);
			rs = stm.executeQuery();
			if (rs.next()){
				codiceIPA = rs.getString("codice_ipa");
			}
			rs.close();
			stm.close();

		} catch (Exception e) {
			this.driver.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		if(codiceIPA==null){
			throw new DriverRegistroServiziNotFound("Soggetto ["+tipo_soggetto+"/"+nome_soggetto+"] non trovato");
		}
		else{
			return codiceIPA;
		}

	}

	protected void updateSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Parametro non valido.");

		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			/**
			 * L'aggiornamente del tipo/nome soggetto scatena anche l'aggiornamento del nome
			 * dei connettori oltre che del soggetto stesso anche dei fruitori di servizi in cui il soggetto puo essere
			 * o l'erogatore o il fruitore
			 */
			String oldNomeSoggetto=null;
			String oldTipoSoggetto=null;
			if(soggetto.getOldIDSoggettoForUpdate()!=null){
				oldNomeSoggetto = soggetto.getOldIDSoggettoForUpdate().getNome();
				oldTipoSoggetto = soggetto.getOldIDSoggettoForUpdate().getTipo();
			}
			String nomeSoggetto=soggetto.getNome();
			String tipoSoggetto=soggetto.getTipo();

			if(tipoSoggetto==null || tipoSoggetto.equals("")) throw new DriverRegistroServiziException("Parametro Tipo Soggetto non valido.");
			if(nomeSoggetto==null || nomeSoggetto.equals("")) throw new DriverRegistroServiziException("Parametro Nome Soggetto non valido.");

			if(oldNomeSoggetto==null || oldNomeSoggetto.equals("")) oldNomeSoggetto=nomeSoggetto;
			if(oldTipoSoggetto==null || oldTipoSoggetto.equals("")) oldTipoSoggetto=tipoSoggetto;

			//se tipo o nome sono cambiati effettuo modifiche su connettore
			if(!tipoSoggetto.equals(oldTipoSoggetto) || !nomeSoggetto.equals(oldNomeSoggetto)){

				//pattern nome connettore servizio fruitore
				// CNT_SF_tipo/nome(fruitore del servizio)+tipo/nome(erogatore del servizio)+tipo/nome(servizio)
				// "CNT_SF_"  + tipoFruitore  + "/" + nomeFruitore+"_"+  tipoErogatore+"/"+nomeErogatore +"_"+ tiposervizio + "/" + nomeservizio
				//devo modificare la prima e la seconda parte del pattern
				String regex = "CNT_SF_(.*)\\/(.*)_(.*)\\/(.*)_(.*)\\/(.*)";

				//recupero i connettori da modificare dove il soggetto e' erogatore
				//0 soggetti
				//1 servizi
				//2 servizi_fruitori
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_connettore");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = "+CostantiDB.SERVIZI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stm=con.prepareStatement(sqlQuery);
				stm.setString(1, oldTipoSoggetto);
				stm.setString(2, oldNomeSoggetto);
				rs=stm.executeQuery();
				while(rs.next()){
					long idConnettore=rs.getLong("id_connettore");
					Connettore connettore = this.driver.getConnettore(idConnettore, con);
					String oldNomeConnettore=connettore.getNome();
					//controllo se il nome connettore matcha la regex
					if(oldNomeConnettore.matches(regex)){
						this.driver.logDebug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+oldNomeConnettore+"]...");
						//splitto la stringa in modo da prendermi i valori separatamente
						//l'array sara composto da [CNT,SF,tipo/nomeFruitore,tipo/nomeErogatore,tipo/nomeServizio]
						String[] val=oldNomeConnettore.split("_");
						//i=2 contiene il tipo/nome del soggetto fruitore
						//i=3 contiene il tipo/nome del soggetto erogatore (che bisogna cambiare)
						//i=4 contiene il tipo/nome del servizio
						String newNomeConnettore = "CNT_SF_"+val[2]+ tipoSoggetto+"/"+nomeSoggetto +val[4];
						this.driver.logDebug("nuovo nome connettore ["+newNomeConnettore+"]");
						connettore.setNome(newNomeConnettore);
						//aggiorno il connettore
						DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
					}
				}
				rs.close();
				stm.close();
				//recupero i connettori da modificare dove il soggetto e' fruitore
				//0 soggetti
				//1 servizi_fruitori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_connettore");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm=con.prepareStatement(sqlQuery);
				stm.setString(1, oldTipoSoggetto);
				stm.setString(2, oldNomeSoggetto);
				rs=stm.executeQuery();
				while(rs.next()){
					long idConnettore=rs.getLong("id_connettore");
					Connettore connettore = this.driver.getConnettore(idConnettore, con);
					String oldNomeConnettore=connettore.getNome();
					//controllo se il nome connettore matcha la regex
					if(oldNomeConnettore.matches(regex)){
						this.driver.logDebug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+oldNomeConnettore+"]...");
						//splitto la stringa in modo da prendermi i valori separatamente
						//l'array sara composto da [CNT,SF,tipo/nomeFruitore,tipo/nomeErogatore,tipo/nomeServizio]
						String[] val=oldNomeConnettore.split("_");
						//i=2 contiene il tipo/nome del soggetto fruitore (che bisogna cambiare)
						//i=3 contiene il tipo/nome del soggetto erogatore 
						//i=4 contiene il tipo/nome del servizio
						String newNomeConnettore = "CNT_SF_"+tipoSoggetto+"/"+nomeSoggetto+val[3]+val[4];
						this.driver.logDebug("nuovo nome connettore ["+newNomeConnettore+"]");
						connettore.setNome(newNomeConnettore);
						//aggiorno il connettore
						DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
					}
				}
				rs.close();
				stm.close();
			}

			// UPDATE soggetto
			DriverRegistroServiziDB_soggettiLIB.CRUDSoggetto(2, soggetto, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Errore durante l'aggiornamento del soggetto : " + qe.getMessage(),qe);
		} finally {

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 3");
			// DELETE soggetto
			DriverRegistroServiziDB_soggettiLIB.CRUDSoggetto(3, soggetto, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Errore durante l'eliminazione del soggetto : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected IDSoggetto[] getSoggettiWithSuperuser(String user) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		IDSoggetto [] idSoggetti = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettiWithSuperuser");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziException::getSoggettiWithSuperuser] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			List<IDSoggetto> idTrovati = new ArrayList<IDSoggetto>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("superuser = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, user);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiRegistroServizi.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			while (rs.next()) {
				IDSoggetto id = new IDSoggetto();
				id.setTipo(rs.getString("tipo_soggetto"));
				id.setNome(rs.getString("nome_soggetto"));
				idTrovati.add(id);
			}

			if(idTrovati.size()>0){
				idSoggetti =  new IDSoggetto[1];
				idSoggetti = idTrovati.toArray(idSoggetti);
			}

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::getSoggettiWithSuperuser] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::getSoggettiWithSuperuser] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		return idSoggetti;
	}
	
	protected List<String> nomiProprietaSoggetti(List<String> tipoSoggettiProtocollo) throws DriverConfigurazioneException {
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("nomiProprietaSoggetti");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverRegistroServiziDB::nomiProprietaSoggetti] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS + ".nome");
			
			if((tipoSoggettiProtocollo != null && tipoSoggettiProtocollo.size() > 0)) {
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI_PROPS + ".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				String [] tipiServiziProtocolloS = tipoSoggettiProtocollo.toArray(new String[tipoSoggettiProtocollo.size()]); 
				sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipiServiziProtocolloS);
			}
			
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_PROPS + ".nome"); 
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setANDLogicOperator(true);
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				lista.add(risultato.getString("nome"));
			}
			return lista;
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverRegistroServiziDB::nomiProprietaSoggetti] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	protected boolean isSoggettoInUsoInPackageFinali(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,true,false);
	}
	protected boolean isSoggettoInUsoInPackagePubblici(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,false,true);
	}
	protected boolean isSoggettoInUso(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,false,false);
	}
	private boolean isSoggettoInUso(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso,boolean checkOnlyStatiFinali,boolean checkOnlyStatiPubblici) throws DriverRegistroServiziException {
		String nomeMetodo = "isSoggettoInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isSoggettoInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);

		try {
			boolean isInUso = false;
			List<String> nomiServizi = new ArrayList<>();
			List<String> serviziFruitori = new ArrayList<>();
			List<String> accordi = new ArrayList<>();
			List<String> accordi_cooperazione = new ArrayList<>();
			List<String> partecipanti = new ArrayList<>();

			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			if(checkOnlyStatiFinali){
				sqlQueryObject.addWhereCondition("stato = ?");
			}
			if(checkOnlyStatiPubblici){
				sqlQueryObject.addWhereCondition("privato = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			int index = 2;
			if(checkOnlyStatiFinali){
				stmt.setString(index, StatiAccordo.finale.toString());
				index++;
			}
			if(checkOnlyStatiPubblici){
				stmt.setInt(index, 0);
				index++;
			}
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso=true;
				String nomeServizio = risultato.getString("tipo_servizio")+"/"+risultato.getString("nome_servizio");
				nomiServizi.add(nomeServizio);
			}
			risultato.close();
			stmt.close();

			if(nomiServizi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, nomiServizi.toString());

			// controllo se in uso in fruitori
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			if(checkOnlyStatiFinali){
				sqlQueryObject.addWhereCondition("stato = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			if(checkOnlyStatiFinali){
				stmt.setString(2, StatiAccordo.finale.toString());
			}
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso=true;
				AccordoServizioParteSpecifica servizio = this.driver.getAccordoServizioParteSpecifica(risultato.getLong("id_servizio"));
				String uriServizio = this.driver.idServizioFactory.getUriFromAccordo(servizio);
				if(checkOnlyStatiPubblici){
					if(servizio.getPrivato()==null || servizio.getPrivato()==false){
						serviziFruitori.add(uriServizio);
					}
				}else{
					serviziFruitori.add(uriServizio);
				}		
			}
			risultato.close();
			stmt.close();

			if(serviziFruitori.size()>0) whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_FRUITORI, serviziFruitori.toString());


			//controllo se referente
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_referente = ?");
			if(checkOnlyStatiFinali){
				sqlQueryObject.addWhereCondition("stato = ?");
			}
			if(checkOnlyStatiPubblici){
				sqlQueryObject.addWhereCondition("privato = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			index = 2;
			if(checkOnlyStatiFinali){
				stmt.setString(index, StatiAccordo.finale.toString());
				index++;
			}
			if(checkOnlyStatiPubblici){
				stmt.setInt(index, 0);
				index++;
			}
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso=true;
				AccordoServizioParteComune accordo = this.driver.getAccordoServizioParteComune(risultato.getLong("id"));
				accordi.add(this.driver.idAccordoFactory.getUriFromAccordo(accordo));
			}
			risultato.close();
			stmt.close();

			if(accordi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE, accordi.toString());


			//controllo se referente in accordi cooperazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_referente = ?");
			if(checkOnlyStatiFinali){
				sqlQueryObject.addWhereCondition("stato = ?");
			}
			if(checkOnlyStatiPubblici){
				sqlQueryObject.addWhereCondition("privato = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			index = 2;
			if(checkOnlyStatiFinali){
				stmt.setString(index, StatiAccordo.finale.toString());
				index++;
			}
			if(checkOnlyStatiPubblici){
				stmt.setInt(index, 0);
				index++;
			}
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso=true;
				AccordoCooperazione accordo = this.driver.getAccordoCooperazione(risultato.getLong("id"));
				accordi_cooperazione.add(this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
			}
			risultato.close();
			stmt.close();

			if(accordi_cooperazione.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE, accordi_cooperazione.toString());


			//controllo se partecipante in cooperazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()){
				AccordoCooperazione accordo = this.driver.getAccordoCooperazione(risultato.getLong("id_accordo_cooperazione"));
				if(checkOnlyStatiFinali){
					if(StatiAccordo.finale.toString().equals(accordo.getStatoPackage())){
						isInUso=true;
						partecipanti.add(this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
					}
				}else if(checkOnlyStatiPubblici){
					if(accordo.getPrivato()==null || accordo.getPrivato()==false){
						isInUso=true;
						partecipanti.add(this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
					}
				}else{
					isInUso=true;
					partecipanti.add(this.driver.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
				}
			}
			risultato.close();
			stmt.close();

			if(partecipanti.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_PARTECIPANTE_COOPERAZIONE, partecipanti.toString());


			return isInUso;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	
	protected IDSoggetto getIdSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdSoggetto(idSoggetto,null);
	}
	protected IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		// conrollo consistenza
		if (idSoggetto <= 0)
			return null;

		IDSoggetto idSoggettoObject = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getIdSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getIdSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				idSoggettoObject = new IDSoggetto();

				// String tmp = rs.getString("nomeprov");
				// soggetto.setNome(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				idSoggettoObject.setNome(rs.getString("nome_soggetto"));
				// tmp = rs.getString("tipoprov");
				// soggetto.setTipo(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				idSoggettoObject.setTipo(rs.getString("tipo_soggetto"));

			}else{
				throw new DriverRegistroServiziNotFound("Nessun risultato trovato eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			}

			return idSoggettoObject;
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdSoggetto] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}
	}
	
	
	
	protected List<IDSoggetto> getSoggettiDefault() throws DriverRegistroServiziException {
		return getSoggettiDefault(null);
	}
	protected List<IDSoggetto> getSoggettiDefault(Connection conParam) throws DriverRegistroServiziException {

		List<IDSoggetto> soggettiDefault = new ArrayList<IDSoggetto>();

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettiDefault()");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggettiDefault] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("is_default = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setInt(1, CostantiDB.TRUE);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, CostantiDB.TRUE));
			rs = stm.executeQuery();

			while (rs.next()) {
				
				IDSoggetto idSoggetto = new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"));
				soggettiDefault.add(idSoggetto);

			}
			
			if(soggettiDefault.isEmpty()) {
				throw new DriverRegistroServiziNotFound("Nessun risultato trovato eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, CostantiDB.TRUE));
			}

			return soggettiDefault;
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggettiDefault] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggettiDefault] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}
	}
}
