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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiCooperazioneDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiCooperazioneDriver {

	private DriverRegistroServiziDB driver = null;
	private DriverRegistroServiziDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	public DriverRegistroServiziDB_accordiCooperazioneDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(driver);
	}
	
	
	/* Accordi di Cooperazione */

	protected org.openspcoop2.core.registry.AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoCooperazione(idAccordo,false);
	}
	protected org.openspcoop2.core.registry.AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo.getNome non e' definito");

		this.driver.log.debug("richiesto getAccordoCooperazione: " + idAccordo.toString());

		org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazione = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;

		try {

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoCooperazione(idAccordo)");
			else
				con = this.driver.globalConnection;

			long idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordo, con, this.driver.tipoDB);
			if(idAccordoLong<=0){
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoCooperazione] Accordo non trovato (id:"+idAccordoLong+")");
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();



			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordoLong);


			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			rs = stm.executeQuery();

			if (rs.next()) {
				accordoCooperazione = new org.openspcoop2.core.registry.AccordoCooperazione();

				accordoCooperazione.setId(rs.getLong("id"));

				String tmp = rs.getString("nome");
				// se tmp==null oppure tmp=="" then setNome(null) else
				// setNome(tmp)
				accordoCooperazione.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				accordoCooperazione.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				// Soggetto referente
				long id_referente = rs.getLong("id_referente");
				if(id_referente>0) {
					IDSoggetto soggettoReferente = null;
					try {
						soggettoReferente = this.driver.getIdSoggetto(id_referente,con);
						if(soggettoReferente==null){
							throw new DriverRegistroServiziNotFound ("non esiste");
						}
					}catch(DriverRegistroServiziNotFound notFound) {
						throw new Exception ("Soggetto referente ["+id_referente+"] dell'accordo non esiste");
					}
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggettoReferente.getTipo());
					assr.setNome(soggettoReferente.getNome());
					accordoCooperazione.setSoggettoReferente(assr);
				}

				//Versione
				if(rs.getString("versione")!=null && !"".equals(rs.getString("versione")))
					accordoCooperazione.setVersione(rs.getInt("versione"));

				// Stato
				tmp = rs.getString("stato");
				accordoCooperazione.setStatoPackage(((tmp == null || tmp.equals("")) ? null : tmp));

				// Privato
				if(rs.getInt("privato")==1)
					accordoCooperazione.setPrivato(true);
				else
					accordoCooperazione.setPrivato(false);

				tmp = rs.getString("superuser");
				accordoCooperazione.setSuperUser(((tmp == null || tmp.equals("")) ? null : tmp));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoCooperazione.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				rs.close();
				stm.close();


				// Aggiungo ServiziComposti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				while (rs.next()) {

					long idAccServizioComposto = rs.getLong("id_accordo");

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addSelectField("nome");
					sqlQueryObject.addSelectField("versione");
					sqlQueryObject.addSelectField("id_referente");
					sqlQueryObject.addWhereCondition("id = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAccServizioComposto);
					rs2 = stm2.executeQuery();
					if(rs2.next()){
						IDSoggetto soggettoReferente = null;
						long id_referenteInternal = rs2.getLong("id_referente");
						if(id_referenteInternal>0){
							try {
								soggettoReferente = this.driver.getIdSoggetto(id_referenteInternal,con);
								if(soggettoReferente==null){
									throw new DriverRegistroServiziNotFound ("non esiste");
								}
							}catch(DriverRegistroServiziNotFound notFound) {
								throw new Exception ("Soggetto referente ["+id_referenteInternal+"] dell'accordo non esiste");
							}
						}
						String uriAccordo = this.driver.idAccordoFactory.getUriFromValues(rs2.getString("nome"), soggettoReferente, rs2.getInt("versione"));
						accordoCooperazione.addUriServiziComposti(uriAccordo);
					}else{
						throw new DriverRegistroServiziException("IDAccordo con id ["+rs.getLong("id_accordo_servizio")+"] non presente");
					}
					rs2.close();
					stm2.close();

				}
				rs.close();
				stm.close();




				// Aggiungo Partecipanti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				IdSoggetto accCompPartecipante = null;
				AccordoCooperazionePartecipanti accCopPartecipanti = null;
				while (rs.next()) {

					accCompPartecipante = new IdSoggetto();
					accCompPartecipante.setIdSoggetto(rs.getLong("id_soggetto"));

					Soggetto s = this.driver.getSoggetto(rs.getLong("id_soggetto"),con);
					accCompPartecipante.setTipo(s.getTipo());
					accCompPartecipante.setNome(s.getNome());

					if(accCopPartecipanti==null){
						accCopPartecipanti = new AccordoCooperazionePartecipanti();
						accordoCooperazione.setElencoPartecipanti(accCopPartecipanti);
					}
					accordoCooperazione.getElencoPartecipanti().addSoggettoPartecipante(accCompPartecipante);

				}
				rs.close();
				stm.close();


				// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
				//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
				try{
					List<?> allegati = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.allegato.toString(), idAccordoLong, 
							ProprietariDocumento.accordoCooperazione,readContenutoAllegati, con, this.driver.tipoDB);
					for(int i=0; i<allegati.size();i++){
						accordoCooperazione.addAllegato((Documento) allegati.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				try{
					List<?> specificheSemiformali = DriverRegistroServiziDB_documentiLIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
							idAccordoLong, ProprietariDocumento.accordoCooperazione,readContenutoAllegati, con, this.driver.tipoDB);
					for(int i=0; i<specificheSemiformali.size();i++){
						accordoCooperazione.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}

				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverRegistroServiziDB_LIB.getListaProtocolProperty(idAccordoLong, ProprietariProtocolProperty.ACCORDO_COOPERAZIONE, con, this.driver.tipoDB);
					if(listPP!=null && listPP.size()>0){
						for (ProtocolProperty protocolProperty : listPP) {
							accordoCooperazione.addProtocolProperty(protocolProperty);
						}
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoCooperazione] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			}


			return accordoCooperazione;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoCooperazione] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs2!=null) 
					rs2.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm2!=null) 
					stm2.close();
			}catch (Exception e) {
				//ignore
			}

			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	protected IDAccordoCooperazione getIdAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdAccordoCooperazione(id,null);
	}
	protected IDAccordoCooperazione getIdAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.log.debug("richiesto getIdAccordoCooperazione: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordoCooperazione idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getIdAccordoCooperazione(longId)");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					referente = this.driver.getIdSoggetto(idReferente,con);
					if(referente==null){
						throw new Exception("Soggetto referente non presente?");
					}
				}

				idAccordo = this.driver.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getInt("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getIdAccordoCooperazione] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoCooperazione] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoCooperazione] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return idAccordo;
	}


	protected AccordoCooperazione getAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoCooperazione(id,null);
	}
	protected AccordoCooperazione getAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.driver.log.debug("richiesto getAccordoCooperazione: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordoCooperazione idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAccordoCooperazione(longId)");
			else
				con = this.driver.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					referente = this.driver.getIdSoggetto(idReferente,con);
					if(referente==null){
						throw new Exception("Soggetto referente non presente?");
					}
				}
				
				idAccordo = this.driver.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getInt("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoCooperazione] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoCooperazione] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoCooperazione] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return this.getAccordoCooperazione(idAccordo, false);
	}

	/**
	 * Ritorna gli identificatori degli accordi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	protected List<IDAccordoCooperazione> getAllIdAccordiCooperazione(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.log.debug("getAllIdAccordiCooperazione...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdAccordiCooperazione");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);

			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE,"versione");
			sqlQueryObject.addSelectField(this.driver.tabellaSoggetti,"tipo_soggetto");
			sqlQueryObject.addSelectField(this.driver.tabellaSoggetti,"nome_soggetto");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente="+this.driver.tabellaSoggetti+".id");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".ora_registrazione < ?");
				if(filtroRicerca.getNomeAccordo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome = ?");
				if(filtroRicerca.getVersione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione = ?");
				if(filtroRicerca.getTipoSoggettoReferente()!=null)
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".tipo_soggetto=?");
				if(filtroRicerca.getNomeSoggettoReferente()!=null)
					sqlQueryObject.addWhereCondition(this.driver.tabellaSoggetti+".nome_soggetto=?");
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca, CostantiDB.ACCORDI_COOPERAZIONE);
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getNomeAccordo()!=null){
					this.driver.log.debug("nomeAccordo stmt.setString("+filtroRicerca.getNomeAccordo()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeAccordo());
					indexStmt++;
				}	
				if(filtroRicerca.getVersione()!=null){
					this.driver.log.debug("versioneAccordo stmt.setString("+filtroRicerca.getVersione()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersione());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoReferente()!=null){
					this.driver.log.debug("tipoSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getTipoSoggettoReferente()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoReferente());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoReferente()!=null){
					this.driver.log.debug("nomeSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getNomeSoggettoReferente()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoReferente());
					indexStmt++;
				}	
				this.protocolPropertiesDriver.setProtocolPropertiesForSearch(stm, indexStmt, filtroRicerca, ProprietariProtocolProperty.ACCORDO_COOPERAZIONE);
			}
			rs = stm.executeQuery();
			List<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();
			while (rs.next()) {
				IDSoggetto idSoggetto = new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"));
				IDAccordoCooperazione idAccordo = this.driver.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),idSoggetto,rs.getInt("versione"));
				idAccordi.add(idAccordo);
			}
			if(idAccordi.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Accordi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Accordi non trovati");
			}else{
				return idAccordi;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdAccordiCooperazione error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}




	/**
	 * Crea un nuovo AccordoCooperazione
	 * 
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	protected void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDServizio tupe=1");
			// CREATE
			DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoCooperazione(CostantiDB.CREATE, accordoCooperazione, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Errore durante la creazione dell'accordo : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 *
	 * @param idAccordo dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 * @throws DriverRegistroServiziException
	 */    
	protected boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		Connection connection;
		if (this.driver.atomica) {
			try {
				connection = this.driver.getConnectionFromDatasource("existsAccordoCooperazione");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);
		try {
			long idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordo, connection, this.driver.tipoDB);
			if (idAccordoLong <= 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.driver.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Aggiorna l'AccordoCooperazione con i nuovi valori.
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	protected void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Parametro non valido.");
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			// UPDATE
			DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoCooperazione(CostantiDB.UPDATE, accordoCooperazione, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Errore durante l'update dell'accordo : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Elimina un AccordoCooperazione 
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	protected void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDServizio type = 3");
			// creo soggetto
			DriverRegistroServiziDB_accordiCooperazioneLIB.CRUDAccordoCooperazione(CostantiDB.DELETE, accordoCooperazione, con, this.driver.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Errore durante la delete dell'accordo : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	protected List<AccordoCooperazione> accordiCooperazioneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCooperazioneList";
		int idLista = Liste.ACCORDI_COOPERAZIONE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}

		String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);

		this.driver.log.debug("search : " + search);
		this.driver.log.debug("filterProtocollo : " + filterProtocollo);
		this.driver.log.debug("filterProtocolli : " + filterProtocolli);
		this.driver.log.debug("filterStatoAccordo : " + filterStatoAccordo);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiCooperazioneList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true)); // e' un intero
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_referente = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_referente = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI_COOPERAZIONE,"id","idAccordoCooperazione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_referente");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI_COOPERAZIONE,"descrizione","descrizioneAccordoCooperazione");
				sqlQueryObject.addSelectField("versione");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true));
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true));  // e' un intero
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_referente = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI_COOPERAZIONE,"id","idAccordoCooperazione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_referente");
				sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI_COOPERAZIONE,"descrizione","descrizioneAccordoCooperazione");
				sqlQueryObject.addSelectField("versione");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_referente = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".stato = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}


			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				String nomeAcc = risultato.getString("nome"); 
				int versioneAcc = risultato.getInt("versione");
				long idReferente = risultato.getLong("id_referente");

				IDSoggetto soggettoReferente = null;
				if(idReferente>0){
					Soggetto s = this.driver.getSoggetto(idReferente);
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				
				IDAccordoCooperazione id = this.driver.idAccordoCooperazioneFactory.getIDAccordoFromValues(nomeAcc, soggettoReferente, versioneAcc);

				idAccordi.add(id);

			}

			this.driver.log.debug("size lista :" + ((idAccordi == null) ? null : idAccordi.size()));


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		ArrayList<AccordoCooperazione> lista = new ArrayList<AccordoCooperazione>();
		for(int i=-0; i<idAccordi.size(); i++){
			try{
				lista.add(this.getAccordoCooperazione(idAccordi.get(i)));
			}catch(DriverRegistroServiziNotFound dNot){
				throw new DriverRegistroServiziException("Accordo non trovato con id?: "+dNot.getMessage(),dNot);
			}
		}
		return lista;
	}

	protected List<IDSoggetto> accordiCoopPartecipantiList(long idAccordo,ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCoopPartecipantiList";
		int idLista = Liste.ACCORDI_COOP_PARTECIPANTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.driver.log.debug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiCoopPartecipantiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObjectSoggetti.addSelectCountField("*", "cont");
				sqlQueryObjectSoggetti.addWhereCondition(true,
						sqlQueryObjectSoggetti.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true),
							CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione=?");
				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				queryString = sqlQueryObjectSoggetti.createSQLQuery();
			}else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione=?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "id");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI, "id_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI, "id_accordo_cooperazione");

				sqlQueryObjectSoggetti.addWhereCondition(true,
						sqlQueryObjectSoggetti.getWhereLikeCondition(CostantiDB.SOGGETTI+".nome_soggetto", search, true, true),
						CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_soggetto="+CostantiDB.SOGGETTI+".id");

				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione=?");

				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				sqlQueryObjectSoggetti.addOrderBy("tipo_soggetto");
				sqlQueryObjectSoggetti.addOrderBy("nome_soggetto");
				sqlQueryObjectSoggetti.setSortType(true);
				sqlQueryObjectSoggetti.setLimit(limit);
				sqlQueryObjectSoggetti.setOffset(offset);
				queryString = sqlQueryObjectSoggetti.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "id");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI, "id_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI, "id_accordo_cooperazione");

				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione=?");
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_soggetto="+CostantiDB.SOGGETTI+".id");

				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				sqlQueryObjectSoggetti.addOrderBy("tipo_soggetto");
				sqlQueryObjectSoggetti.addOrderBy("nome_soggetto");
				sqlQueryObjectSoggetti.setSortType(true);
				sqlQueryObjectSoggetti.setLimit(limit);
				sqlQueryObjectSoggetti.setOffset(offset);
				queryString = sqlQueryObjectSoggetti.createSQLQuery();
			}


			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				String tipo = risultato.getString("tipo_soggetto");
				String nome = risultato.getString("nome_soggetto");
				IDSoggetto idSogg = new IDSoggetto(tipo,nome);
				idSoggetti.add(idSogg);

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idSoggetti;
	}

	protected List<AccordoCooperazione> accordiCoopWithSoggettoPartecipante(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCoopWithSoggettoPartecipante";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoCooperazione> idAccordoCooperazione = new ArrayList<AccordoCooperazione>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiCoopWithSoggettoPartecipante");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI_COOPERAZIONE, "id","idAccordoCooperazione");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_accordo_cooperazione="+CostantiDB.ACCORDI_COOPERAZIONE+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idSoggetto.getTipo());
			stmt.setString(2,idSoggetto.getNome());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoCooperazioneLong = risultato.getLong("idAccordoCooperazione");
				idAccordoCooperazione.add(this.getAccordoCooperazione(idAccordoCooperazioneLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idAccordoCooperazione;
	}
	
	protected void validaStatoAccordoCooperazione(AccordoCooperazione ac) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("AccordoCooperazione",ac.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(ac.getPrivato()==null || ac.getPrivato()==false){
				if(ac.getSoggettoReferente()!=null){
					IDSoggetto idS = new IDSoggetto(ac.getSoggettoReferente().getTipo(),ac.getSoggettoReferente().getNome());
					try{
						Soggetto s = this.driver.getSoggetto(idS);
						if(s.getPrivato()!=null && s.getPrivato()){
							erroreValidazione.addErroreValidazione("soggetto referente ["+idS+"] con visibilita' privata, in un accordo di cooperazione con visibilita' pubblica");
						}
					}catch(DriverRegistroServiziNotFound dNot){}
				}
			}
			if(ac.getElencoPartecipanti()!=null){
				AccordoCooperazionePartecipanti partecipanti = ac.getElencoPartecipanti();
				if(partecipanti.sizeSoggettoPartecipanteList()>=2){
					for(int i=0; i<partecipanti.sizeSoggettoPartecipanteList(); i++){
						IdSoggetto idSoggettoPartecipante = partecipanti.getSoggettoPartecipante(i);
						if(idSoggettoPartecipante.getIdSoggetto()!=null && idSoggettoPartecipante.getIdSoggetto()>0){
							try{
								Soggetto s = this.driver.getSoggetto(partecipanti.getSoggettoPartecipante(i).getIdSoggetto());
								if(s.getPrivato()!=null && s.getPrivato()){
									erroreValidazione.addErroreValidazione("soggetto partecipante ["+s.getTipo()+"/"+s.getNome()+"] con visibilita' privata, in un accordo di cooperazione con visibilita' pubblica");
								}
							}catch(DriverRegistroServiziNotFound dNot){}
						}
						else if(idSoggettoPartecipante.getTipo()!=null && idSoggettoPartecipante.getNome()!=null){
							try{
								Soggetto s = this.driver.getSoggetto(new IDSoggetto(idSoggettoPartecipante.getTipo(), idSoggettoPartecipante.getNome()));
								if(s.getPrivato()!=null && s.getPrivato()){
									erroreValidazione.addErroreValidazione("soggetto partecipante ["+s.getTipo()+"/"+s.getNome()+"] con visibilita' privata, in un accordo di cooperazione con visibilita' pubblica");
								}
							}catch(DriverRegistroServiziNotFound dNot){}
						}
					}
				}
			}

			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(ac.getStatoPackage()) == false){

				// Validazione necessaria sia ad uno stato operativo che finale
				if(ac.getElencoPartecipanti()==null){
					erroreValidazione.addErroreValidazione("soggetti partecipanti non definiti");	
				}
				else if(ac.getElencoPartecipanti().sizeSoggettoPartecipanteList()<=0){
					erroreValidazione.addErroreValidazione("soggetti partecipanti non definiti");	
				}else{
					if(ac.getElencoPartecipanti().sizeSoggettoPartecipanteList()<2){
						erroreValidazione.addErroreValidazione("almeno 2 soggetti partecipanti devono essere definiti");	
					}
				}

			}

		}catch(Exception e){
			throw new ValidazioneStatoPackageException(e);
		}

		if(erroreValidazione.sizeErroriValidazione()>0){
			throw erroreValidazione;
		}
	}
}
