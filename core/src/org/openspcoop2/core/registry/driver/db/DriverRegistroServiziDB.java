/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziCRUD;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Classe utilizzata per effettuare query ad un registro dei servizi openspcoop
 * formato da un db.
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverRegistroServiziDB extends BeanUtilities implements IDriverRegistroServiziGet,
IDriverRegistroServiziCRUD, 
IDriverWS ,IMonitoraggioRisorsa{

	/* ******** F I E L D S P R I V A T I ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	// Datasource per la connessione al DB
	public DataSource datasource = null;
	// Connection passata al momento della creazione dell'oggetto
	private Connection globalConnection = null;
	// Variabile di controllo del tipo di operazione da effettuare
	// l'autoCommit viene gestito internamente a questa classe
	private boolean atomica = true;

	/** Tabella soggetti */
	String tabellaSoggetti = CostantiDB.SOGGETTI;

	/** Logger utilizzato per info. */
	private Logger log = null;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;

	public String getTipoDB() {
		return this.tipoDB;
	}



	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();




	

	/* ******** COSTRUTTORI e METODI DI RELOAD ******** */

	/**
	 * Costruttore.
	 * 
	 * @param nomeDataSource
	 *                Nome del Datasource da utilizzare per prelevare le
	 *                connessioni
	 * 
	 */
	public DriverRegistroServiziDB(String nomeDataSource, Properties prop,String tipoDB) {
		this(nomeDataSource,prop,null,tipoDB,false,false);
	}
	public DriverRegistroServiziDB(String nomeDataSource, Properties prop,Logger alog,String tipoDB){
		this(nomeDataSource,prop,alog,tipoDB,false,false);
	}
	public DriverRegistroServiziDB(String nomeDataSource, Properties prop,Logger alog,String tipoDB, 
			boolean useOp2UtilsDatasource, boolean bindJMX){

		try {
			if(alog==null)
				this.log = LoggerWrapperFactory.getLogger(CostantiRegistroServizi.REGISTRO_DRIVER_DB_LOGGER);
			else{
				this.log = alog;
				DriverRegistroServiziDB_LIB.initStaticLogger(this.log);
			}

			this.log.info("Inizializzo DriverRegistroServiziDB..");
			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDB);
				try{
					this.datasource = DataSourceFactory.newInstance(nomeDataSource, prop, dsParams);
				}catch(UtilsAlreadyExistsException exists){
					this.datasource = DataSourceFactory.getInstance(nomeDataSource); 
					if(this.datasource==null){
						throw new Exception("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
					}
				}
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(prop);
				this.datasource = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
			if (this.datasource != null)
				this.create = true;
		} catch (Exception ne) {
			this.log.error("Eccezione acquisendo il datasource: "+ne.getMessage(),ne);
			this.create = false;
		}

		if (tipoDB == null) {
			this.log.error("Il tipoDatabase non puo essere null.");
			this.create = false;
		}

		this.atomica = true;
		this.tipoDB = tipoDB;
		// Setto il tipoDB anche in DriverRegistroServiziDB_LIB
		DriverRegistroServiziDB_LIB.setTipoDB(tipoDB);

	}

	/**
	 * Costruttore Utilizzato dalle classi estese che gestiscono direttamente la
	 * connessione con il DB e si occupano di getstire il commit delle
	 * transazioni complesse
	 * 
	 * @param connection
	 * @throws DriverRegistroServiziException
	 */
	public DriverRegistroServiziDB(Connection connection,String tipoDB) throws DriverRegistroServiziException {
		this(connection,null,tipoDB);
	}
	public DriverRegistroServiziDB(Connection connection,Logger alog,String tipoDB) throws DriverRegistroServiziException {

		if(alog==null)
			this.log = LoggerWrapperFactory.getLogger(CostantiRegistroServizi.REGISTRO_DRIVER_DB_LOGGER);
		else{
			this.log = alog;
			DriverRegistroServiziDB_LIB.initStaticLogger(this.log);
		}

		//	this.log.info("Inizializzo DriverRegistroServiziDB...");
		if (connection == null) {
			this.create = false;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::DriverRegistroServiziDB(Connection ) La connection non puo essere null.");
		}
		if (tipoDB == null) {
			this.create = false;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::DriverRegistroServiziDB(Connection ) Il tipoDatabase non puo essere null.");
		}
		this.globalConnection = connection;
		this.create = true;
		this.atomica = false;		
		this.tipoDB = tipoDB;
		// Setto il tipoDB anche in DriverRegistroServiziDB_LIB
		DriverRegistroServiziDB_LIB.setTipoDB(tipoDB);

	}
	
	
	
	public Connection getConnection(String methodName) throws DriverRegistroServiziException{
		Connection con = null;
		
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(methodName);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnection] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		
		return con;
	}
	
	public void releaseConnection(Connection con){
		if (this.atomica) {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}


	private Connection getConnectionFromDatasource(String methodName) throws Exception{
		if(this.datasource instanceof org.openspcoop2.utils.datasource.DataSource){
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasource).getWrappedConnection(null, "DriverRegistroServizi."+methodName);
		}
		else{
			return this.datasource.getConnection();
		}
	}

	
	public List<List<Object>> readCustom(ISQLQueryObject sqlQueryObject, List<Class<?>> returnTypes, List<JDBCObject> paramTypes) throws DriverRegistroServiziException
	{
		Connection con = null;
		try {

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("readCustom");
			else
				con = this.globalConnection;
		
			return DBUtils.readCustom(this.log, con, this.tipoDB, sqlQueryObject, returnTypes, paramTypes);
			
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readCustom]: " + se.getMessage(),se);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}
	
	


	/* Accordi di Cooperazione */

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoCooperazione}, 
	 * identificato grazie al parametro 
	 * <var>nomeAccordo</var> 
	 *
	 * @param idAccordo Identificativo dell'accordo di Cooperazione
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.AccordoCooperazione}.
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return getAccordoCooperazione(idAccordo,false);
	}
	public org.openspcoop2.core.registry.AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro idAccordo.getNome non e' definito");

		this.log.debug("richiesto getAccordoCooperazione: " + idAccordo.toString());

		org.openspcoop2.core.registry.AccordoCooperazione accordoCooperazione = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;

		try {

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAccordoCooperazione(idAccordo)");
			else
				con = this.globalConnection;

			long idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordo, con, this.tipoDB);
			if(idAccordoLong<=0){
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoCooperazione] Accordo non trovato (id:"+idAccordoLong+")");
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();



			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordoLong);


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
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
				if(rs.getInt("id_referente")>0) {
					Soggetto soggRef = getSoggetto(new Long(rs.getInt("id_referente")),con);
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggRef.getTipo());
					assr.setNome(soggRef.getNome());
					accordoCooperazione.setSoggettoReferente(assr);
				}

				//Versione
				if(rs.getString("versione")!=null && !"".equals(rs.getString("versione")))
					accordoCooperazione.setVersione(rs.getString("versione"));

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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				while (rs.next()) {

					long idAccServizioComposto = rs.getLong("id_accordo");

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
						if(rs2.getLong("id_referente")>0){
							Soggetto s = this.getSoggetto(rs2.getLong("id_referente"));
							soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
						}
						String uriAccordo = this.idAccordoFactory.getUriFromValues(rs2.getString("nome"), soggettoReferente, rs2.getString("versione"));
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_accordo_cooperazione = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				IdSoggetto accCompPartecipante = null;
				AccordoCooperazionePartecipanti accCopPartecipanti = null;
				while (rs.next()) {

					accCompPartecipante = new IdSoggetto();
					accCompPartecipante.setIdSoggetto(rs.getLong("id_soggetto"));

					Soggetto s = this.getSoggetto(rs.getLong("id_soggetto"),con);
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
					List<?> allegati = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.allegato.toString(), idAccordoLong, 
							ProprietariDocumento.accordoCooperazione,readContenutoAllegati, con, this.tipoDB);
					for(int i=0; i<allegati.size();i++){
						accordoCooperazione.addAllegato((Documento) allegati.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				try{
					List<?> specificheSemiformali = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
							idAccordoLong, ProprietariDocumento.accordoCooperazione,readContenutoAllegati, con, this.tipoDB);
					for(int i=0; i<specificheSemiformali.size();i++){
						accordoCooperazione.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
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
				if(rs2!=null) rs2.close();
				if(stm2!=null) stm2.close();
			}catch (Exception e) {
				//ignore
			}

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}


	public IDAccordoCooperazioneWithSoggetto getIdAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdAccordoCooperazione(id,null);
	}
	public IDAccordoCooperazioneWithSoggetto getIdAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getIdAccordoCooperazione: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordoCooperazioneWithSoggetto idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.atomica)
				con = this.getConnectionFromDatasource("getIdAccordoCooperazione(longId)");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					referente = this.getIdSoggetto(idReferente,con);
					if(referente==null){
						throw new Exception("Soggetto referente non presente?");
					}
				}

				IDAccordoCooperazione idAccordoCoop = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),rs.getString("versione"));
				idAccordo = new IDAccordoCooperazioneWithSoggetto(idAccordoCoop);
				idAccordo.setSoggettoReferente(referente);

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
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return idAccordo;
	}


	public AccordoCooperazione getAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoCooperazione(id,null);
	}
	public AccordoCooperazione getAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getAccordoCooperazione: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordoCooperazione idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.atomica)
				con = this.getConnectionFromDatasource("getAccordoCooperazione(longId)");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),rs.getString("versione"));

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
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return this.getAccordoCooperazione(idAccordo);
	}

	/**
	 * Ritorna gli identificatori degli accordi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdAccordiCooperazione...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdAccordiCooperazione");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
			if(filtroRicerca!=null){
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
				}
			}
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_COOPERAZIONE,"versione");
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
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente="+this.tabellaSoggetti+".id");
					if(filtroRicerca.getTipoSoggettoReferente()!=null)
						sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".tipo_soggetto=?");
					if(filtroRicerca.getNomeSoggettoReferente()!=null)
						sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".nome_soggetto=?");
				}
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getNomeAccordo()!=null){
					this.log.debug("nomeAccordo stmt.setString("+filtroRicerca.getNomeAccordo()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeAccordo());
					indexStmt++;
				}	
				if(filtroRicerca.getVersione()!=null){
					this.log.debug("versioneAccordo stmt.setString("+filtroRicerca.getVersione()+")");
					stm.setString(indexStmt, filtroRicerca.getVersione());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					if(filtroRicerca.getTipoSoggettoReferente()!=null){
						this.log.debug("tipoSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getTipoSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicerca.getTipoSoggettoReferente());
						indexStmt++;
					}
					if(filtroRicerca.getNomeSoggettoReferente()!=null){
						this.log.debug("nomeSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getNomeSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicerca.getNomeSoggettoReferente());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			Vector<IDAccordoCooperazione> idAccordi = new Vector<IDAccordoCooperazione>();
			while (rs.next()) {
				IDAccordoCooperazione idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(rs.getString("nome"),rs.getString("versione"));
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
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
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
	@Override
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("createAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizio tupe=1");
			// CREATE
			DriverRegistroServiziDB_LIB.CRUDAccordoCooperazione(CostantiDB.CREATE, accordoCooperazione, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoCooperazione] Errore durante la creazione dell'accordo : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * @throws DriverRegistroServiziException TODO
	 */    
	@Override
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoCooperazione");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			long idAccordoLong = DBUtils.getIdAccordoCooperazione(idAccordo, connection, this.tipoDB);
			if (idAccordoLong <= 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
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
	@Override
	public void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Parametro non valido.");
		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updateAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			// UPDATE
			DriverRegistroServiziDB_LIB.CRUDAccordoCooperazione(CostantiDB.UPDATE, accordoCooperazione, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoCooperazione] Errore durante l'update dell'accordo : " + qe.getMessage(),qe);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	@Override
	public void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if (accordoCooperazione == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deleteAccordoCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizio type = 3");
			// creo soggetto
			DriverRegistroServiziDB_LIB.CRUDAccordoCooperazione(CostantiDB.DELETE, accordoCooperazione, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoCooperazione] Errore durante la delete dell'accordo : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Si occupa di ritornare l'oggetto
	 * {@link org.openspcoop2.core.registry.AccordoServizioParteComune}, identificato grazie
	 * al parametro <var>nomeAccordo</var>
	 * 
	 * @param idAccordo
	 *                Nome dell'accordo di Servizio
	 * @return l'oggetto di tipo
	 *         {@link org.openspcoop2.core.registry.AccordoServizioParteComune}
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(idAccordo,false);
	}
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo is null");
		if (idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo.getNome is null");
		if (idAccordo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro idAccordo.getNome non e' definito");

		this.log.debug("richiesto getAccordoServizioParteComune: " + idAccordo.toString());

		org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAccordoServizioParteComune(idAccordo)");
			else
				con = this.globalConnection;

			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
			if(idAccordoLong<=0){
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComune] Accordo non trovato (id:"+idAccordo+")");
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();



			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idAccordoLong);


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			rs = stm.executeQuery();

			if (rs.next()) {
				accordoServizio = new org.openspcoop2.core.registry.AccordoServizioParteComune();

				accordoServizio.setId(rs.getLong("id"));

				String tmp = rs.getString("nome");
				// se tmp==null oppure tmp=="" then setNome(null) else
				// setNome(tmp)
				accordoServizio.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				accordoServizio.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				// controllare i vari casi di profcoll (one-way....)
				tmp = rs.getString("profilo_collaborazione");
				accordoServizio.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione((tmp == null || tmp.equals("")) ? null : tmp));
				if(accordoServizio.getProfiloCollaborazione()==null){
					// puo' essere null se e' stato ridefinito nei port type e nelle operation
					// inserisco comunque un default (usato anche nelle interfacce)
					accordoServizio.setProfiloCollaborazione(ProfiloCollaborazione.ONEWAY);
				}

				//		if (tmp == null || tmp.equals(""))
				//		    accordoServizio.setProfiloCollaborazione(null);
				//		if (tmp.equals("oneway"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
				//		if (tmp.equals("sincrono"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
				//		if (tmp.equals("asincrono-simmetrico"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
				//		if (tmp.equals("asincrono-asimmetrico"))
				//		    accordoServizio
				//			    .setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);

				tmp = rs.getString("wsdl_definitorio");
				accordoServizio.setByteWsdlDefinitorio(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_concettuale");
				accordoServizio.setByteWsdlConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_erogatore");
				accordoServizio.setByteWsdlLogicoErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("wsdl_logico_fruitore");
				accordoServizio.setByteWsdlLogicoFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_concettuale");
				accordoServizio.setByteSpecificaConversazioneConcettuale(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_erogatore");
				accordoServizio.setByteSpecificaConversazioneErogatore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				tmp = rs.getString("spec_conv_fruitore");
				accordoServizio.setByteSpecificaConversazioneFruitore(((tmp == null || tmp.trim().equals("")) ? null : tmp.getBytes()));

				accordoServizio.setUtilizzoSenzaAzione(rs.getInt("utilizzo_senza_azione") == CostantiDB.TRUE ? true : false);

				tmp = rs.getString("filtro_duplicati");
				accordoServizio.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				accordoServizio.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				accordoServizio.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = rs.getString("consegna_in_ordine");
				accordoServizio.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = rs.getString("scadenza");
				accordoServizio.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("superuser");
				accordoServizio.setSuperUser(((tmp == null || tmp.equals("")) ? null : tmp));

				if(rs.getInt("privato")==1)
					accordoServizio.setPrivato(true);
				else
					accordoServizio.setPrivato(false);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoServizio.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Soggetto referente
				if(rs.getInt("id_referente")>0) {
					Soggetto soggRef = getSoggetto(new Long(rs.getInt("id_referente")),con);
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggRef.getTipo());
					assr.setNome(soggRef.getNome());
					assr.setId(soggRef.getId());
					accordoServizio.setSoggettoReferente(assr);
				}

				//Versione
				if(rs.getString("versione")!=null && !"".equals(rs.getString("versione")))
					accordoServizio.setVersione(rs.getString("versione"));

				// Stato
				tmp = rs.getString("stato");
				accordoServizio.setStatoPackage(((tmp == null || tmp.equals("")) ? null : tmp));

				rs.close();
				stm.close();


				// Aggiungo azione

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idAccordoLong);

				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));

				rs = stm.executeQuery();

				org.openspcoop2.core.registry.Azione azione = null;
				while (rs.next()) {

					azione = new org.openspcoop2.core.registry.Azione();

					tmp = rs.getString("conferma_ricezione");
					azione.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("consegna_in_ordine");
					azione.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("filtro_duplicati");
					azione.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("identificativo_collaborazione");
					azione.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("nome");
					azione.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("scadenza");
					azione.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

					tmp = rs.getString("profilo_collaborazione");
					azione.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

					tmp = rs.getString("correlata");
					azione.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

					//		    if (tmp == null || tmp.equals("")){
					//			azione.setProfiloCollaborazione(null);
					//		    }
					//		    else if (tmp.equals("oneway")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
					//		    }else if (tmp.equals("sincrono")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
					//		    }else if (tmp.equals("asincrono-simmetrico")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
					//		    }else if (tmp.equals("asincrono-asimmetrico")){
					//			azione.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
					//		    }

					tmp = rs.getString("profilo_azione");
					if (tmp == null || tmp.equals(""))
						azione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					else
						azione.setProfAzione(tmp);

					long idAzione = rs.getLong("id");
					azione.setId(idAzione);

					accordoServizio.addAzione(azione);

				}
				rs.close();
				stm.close();


				// read port type
				this.readPortTypes(accordoServizio,con);


				// read AccordoServizioComposto
				this.readAccordoServizioComposto(accordoServizio , con);


				// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
				//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
				try{
					List<?> allegati = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.allegato.toString(), idAccordoLong, 
							ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.tipoDB);
					for(int i=0; i<allegati.size();i++){
						accordoServizio.addAllegato((Documento) allegati.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				try{
					List<?> specificheSemiformali = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
							idAccordoLong, ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.tipoDB);
					for(int i=0; i<specificheSemiformali.size();i++){	
						accordoServizio.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
					}
				}catch(DriverRegistroServiziNotFound dNotFound){}
				if(accordoServizio.getServizioComposto()!=null){
					// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
					//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
					try{
						List<?> specificheCoordinamento = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaCoordinamento.toString(), 
								idAccordoLong, ProprietariDocumento.accordoServizio,readContenutoAllegati, con, this.tipoDB);
						for(int i=0; i<specificheCoordinamento.size();i++){	
							accordoServizio.getServizioComposto().addSpecificaCoordinamento((Documento) specificheCoordinamento.get(i));
						}
					}catch(DriverRegistroServiziNotFound dNotFound){}
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizioParteComune] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			}


//			if(accordoServizio!=null){
//				// nomiAzione setting 
//				accordoServizio.setNomiAzione(accordoServizio.readNomiAzione());
//			}
			return accordoServizio;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteComune] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	private void readPortTypes(AccordoServizioParteComune as,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.atomica)
				con = this.getConnectionFromDatasource("readPortTypes");
			else
				con = this.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			while (rs.next()) {

				PortType pt = new PortType();

				String tmp = rs.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);

				tmp = rs.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				pt.setIdAccordo(as.getId());

				long idPortType = rs.getLong("id");
				pt.setId(idPortType);

				// Aggiungo azioni a port type
				this.readAzioniPortTypes(pt,con);

				as.addPortType(pt);

			}
			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	private void readAzioniPortTypes(PortType pt,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.atomica)
				con = this.getConnectionFromDatasource("readAzioniPortTypes");
			else
				con = this.globalConnection;

			if(pt.getId()==null || pt.getId()<=0)
				throw new Exception("Port Type id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, pt.getId());

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, pt.getId()));

			rs = stm.executeQuery();

			org.openspcoop2.core.registry.Operation azionePT = null;
			while (rs.next()) {

				azionePT = new org.openspcoop2.core.registry.Operation();

				String tmp = rs.getString("conferma_ricezione");
				azionePT.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				azionePT.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				azionePT.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				azionePT.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("nome");
				azionePT.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("scadenza");
				azionePT.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				azionePT.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("correlata_servizio");
				azionePT.setCorrelataServizio(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("correlata");
				azionePT.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					azionePT.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					azionePT.setProfAzione(tmp);

				tmp = rs.getString("soap_style");
				azionePT.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("soap_action");
				azionePT.setSoapAction(((tmp == null || tmp.equals("")) ? null : tmp));

				azionePT.setIdPortType(pt.getId());

				long idAzionePortType = rs.getLong("id");
				azionePT.setId(idAzionePortType);

				// Aggiungo messages nell'azione del port type
				this.readMessagesAzioniPortTypes(azionePT,con);

				String msgInput = rs.getString("soap_use_msg_input");
				if(azionePT.getMessageInput()!=null || msgInput!=null){
					if(azionePT.getMessageInput()==null){
						azionePT.setMessageInput(new Message());
					}
					azionePT.getMessageInput().setUse(DriverRegistroServiziDB_LIB.getEnumBindingUse(((msgInput == null || msgInput.equals("")) ? null : msgInput)));
					tmp = rs.getString("soap_namespace_msg_input");
					azionePT.getMessageInput().setSoapNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				}

				String msgOutput = rs.getString("soap_use_msg_output");
				if(azionePT.getMessageOutput()!=null || msgOutput!=null){
					if(azionePT.getMessageOutput()==null){
						azionePT.setMessageOutput(new Message());
					}
					 
					azionePT.getMessageOutput().setUse(DriverRegistroServiziDB_LIB.getEnumBindingUse(((msgOutput == null || msgOutput.equals("")) ? null : msgOutput)));
					tmp = rs.getString("soap_namespace_msg_output");
					azionePT.getMessageOutput().setSoapNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				}

				pt.addAzione(azionePT);

			}
			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readAzioniPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	private void readMessagesAzioniPortTypes(Operation azionePT,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Aggiungo port type

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.atomica)
				con = this.getConnectionFromDatasource("readMessagesAzioniPortTypes");
			else
				con = this.globalConnection;

			if(azionePT.getId()==null || azionePT.getId()<=0)
				throw new Exception("Port Type azione id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, azionePT.getId());

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, azionePT.getId()));

			rs = stm.executeQuery();

			org.openspcoop2.core.registry.Message messageInputPart = null;
			org.openspcoop2.core.registry.Message messageOutputPart = null;
			while (rs.next()) {

				boolean isInputMessage = false;
				if(rs.getInt("input_message")==1)
					isInputMessage = true;

				MessagePart part = new MessagePart();

				String name = rs.getString("name");
				name = ((name == null || name.equals("")) ? null : name);
				part.setName(name);

				String elementName = rs.getString("element_name");
				elementName = ((elementName == null || elementName.equals("")) ? null : elementName);
				part.setElementName(elementName);

				String elementNamespace = rs.getString("element_namespace");
				elementNamespace = ((elementNamespace == null || elementNamespace.equals("")) ? null : elementNamespace);
				part.setElementNamespace(elementNamespace);

				String typeName = rs.getString("type_name");
				typeName = ((typeName == null || typeName.equals("")) ? null : typeName);
				part.setTypeName(typeName);

				String typeNamespace = rs.getString("type_namespace");
				typeNamespace = ((typeNamespace == null || typeNamespace.equals("")) ? null : typeNamespace);
				part.setTypeNamespace(typeNamespace);

				long idMessage = rs.getLong("id");
				part.setId(idMessage);

				if(isInputMessage){

					if(messageInputPart==null)
						messageInputPart = new org.openspcoop2.core.registry.Message();
					messageInputPart.addPart(part);

				}else{

					if(messageOutputPart==null)
						messageOutputPart = new org.openspcoop2.core.registry.Message();
					messageOutputPart.addPart(part);

				}

			}

			if(messageInputPart!=null){
				azionePT.setMessageInput(messageInputPart);
			}
			if(messageOutputPart!=null){
				azionePT.setMessageOutput(messageOutputPart);
			}

			rs.close();
			stm.close();

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readMessagesAzioniPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	private void readAccordoServizioComposto(AccordoServizioParteComune as,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		String sqlQuery = null;

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.atomica)
				con = this.getConnectionFromDatasource("readAccordoServizioComposto");
			else
				con = this.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			AccordoServizioParteComuneServizioComposto asComposto = null;
			if (rs.next()) {

				asComposto = new AccordoServizioParteComuneServizioComposto();
				asComposto.setId(rs.getLong("id"));
				asComposto.setIdAccordoCooperazione(rs.getLong("id_accordo_cooperazione"));

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("versione");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm2 = con.prepareStatement(sqlQuery);
				stm2.setLong(1, rs.getLong("id_accordo_cooperazione"));
				rs2 = stm2.executeQuery();
				if(rs2.next()){
					String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromValues(rs2.getString("nome"), rs2.getString("versione"));
					asComposto.setAccordoCooperazione(uriAccordo);
				}else{
					throw new DriverRegistroServiziException("IDAccordoCooperazione con id ["+rs.getLong("id_accordo_cooperazione")+"] non presente");
				}
				rs2.close();
				stm2.close();
			}
			rs.close();
			stm.close();

			if(asComposto!=null){

				// read servizi componenti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_servizio_composto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, asComposto.getId());
				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, asComposto.getId()));
				rs = stm.executeQuery();

				while (rs.next()) {

					AccordoServizioParteComuneServizioCompostoServizioComponente asComponente = new AccordoServizioParteComuneServizioCompostoServizioComponente();
					asComponente.setIdServizioComponente(rs.getLong("id_servizio_componente"));
					asComponente.setAzione(rs.getString("azione"));

					AccordoServizioParteSpecifica aspsServizioComponente = this.getAccordoServizioParteSpecifica(asComponente.getIdServizioComponente(),con);
					Servizio servizioComponente = aspsServizioComponente.getServizio();
					asComponente.setTipo(servizioComponente.getTipo());
					asComponente.setNome(servizioComponente.getNome());
					asComponente.setTipoSoggetto(servizioComponente.getTipoSoggettoErogatore());
					asComponente.setNomeSoggetto(servizioComponente.getNomeSoggettoErogatore());

					asComposto.addServizioComponente(asComponente);
				}
				rs.close();
				stm.close();


				// setto all'interno dell'accordo
				as.setServizioComposto(asComposto);
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			try{
				if(rs2!=null) rs2.close();
				if(stm2!=null) stm2.close();
			}catch (Exception e) {
				//ignore
			}

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}










	/**
	 * Ritorna gli identificatori degli accordi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdAccordiServizioParteComune...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdAccordiServizioParteComune");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			if(filtroRicerca!=null){
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
				}
			}
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"versione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI,"id_referente");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".ora_registrazione < ?");
				if(filtroRicerca.getNomeAccordo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
				if(filtroRicerca.getVersione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+this.tabellaSoggetti+".id");
					if(filtroRicerca.getTipoSoggettoReferente()!=null)
						sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".tipo_soggetto=?");
					if(filtroRicerca.getNomeSoggettoReferente()!=null)
						sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".nome_soggetto=?");
				}

				if( (filtroRicerca.getIdAccordoCooperazione()!=null &&
						(filtroRicerca.getIdAccordoCooperazione().getNome()!=null || 
						filtroRicerca.getIdAccordoCooperazione().getVersione()!=null)) ){
					ISQLQueryObject sqlQueryObjectASComposti = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
					sqlQueryObjectASComposti.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
					sqlQueryObjectASComposti.setANDLogicOperator(true);
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo_cooperazione");
					if(filtroRicerca.getIdAccordoCooperazione().getNome()!=null){
						sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome=?");
					}
					if(filtroRicerca.getIdAccordoCooperazione().getVersione()!=null){
						sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione=?");
					}
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectASComposti);
				}
				else if(filtroRicerca.isServizioComposto()!=null){
					ISQLQueryObject sqlQueryObjectASComposti = sqlQueryObject.newSQLQueryObject();
					sqlQueryObjectASComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
					sqlQueryObjectASComposti.addSelectField("id");
					sqlQueryObjectASComposti.setANDLogicOperator(true);
					sqlQueryObjectASComposti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
					sqlQueryObject.addWhereExistsCondition(!filtroRicerca.isServizioComposto(), sqlQueryObjectASComposti);
				}

			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getNomeAccordo()!=null){
					this.log.debug("nomeAccordo stmt.setString("+filtroRicerca.getNomeAccordo()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeAccordo());
					indexStmt++;
				}	
				if(filtroRicerca.getVersione()!=null){
					this.log.debug("versioneAccordo stmt.setString("+filtroRicerca.getVersione()+")");
					stm.setString(indexStmt, filtroRicerca.getVersione());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					if(filtroRicerca.getTipoSoggettoReferente()!=null){
						this.log.debug("tipoSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getTipoSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicerca.getTipoSoggettoReferente());
						indexStmt++;
					}
					if(filtroRicerca.getNomeSoggettoReferente()!=null){
						this.log.debug("nomeSoggettoReferenteAccordo stmt.setString("+filtroRicerca.getNomeSoggettoReferente()+")");
						stm.setString(indexStmt, filtroRicerca.getNomeSoggettoReferente());
						indexStmt++;
					}
				}
				if(filtroRicerca.getIdAccordoCooperazione()!=null &&
						(filtroRicerca.getIdAccordoCooperazione().getNome()!=null || 
						filtroRicerca.getIdAccordoCooperazione().getVersione()!=null) ){
					if(filtroRicerca.getIdAccordoCooperazione().getNome()!=null){
						this.log.debug("nomeAccordoCooperazione stmt.setString("+filtroRicerca.getIdAccordoCooperazione().getNome()+")");
						stm.setString(indexStmt, filtroRicerca.getIdAccordoCooperazione().getNome());
						indexStmt++;
					}
					if(filtroRicerca.getIdAccordoCooperazione().getVersione()!=null){
						this.log.debug("versioneAccordoCooperazione stmt.setString("+filtroRicerca.getIdAccordoCooperazione().getVersione()+")");
						stm.setString(indexStmt, filtroRicerca.getIdAccordoCooperazione().getVersione());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			Vector<IDAccordo> idAccordi = new Vector<IDAccordo>();
			while (rs.next()) {
				long idReferente = rs.getLong("id_referente");
				IDSoggetto idSoggettoReferente = null;
				if(idReferente>0){
					Soggetto soggettoReferente = this.getSoggetto(idReferente,con);
					if(soggettoReferente==null){
						try{
							if(rs!=null){
								rs.close();
								rs = null;
							}
						}catch (Exception e) {}
						try{
							if(stm!=null){
								stm.close();
								stm=null;
							}
						}catch (Exception e) {}
						throw new Exception("Soggetto referente ["+idReferente+"] presente nell'accordo ["+rs.getString("nome")+"] (versione ["+rs.getString("versione")+"]) non presente?");
					}
					idSoggettoReferente = new IDSoggetto(soggettoReferente.getTipo(),soggettoReferente.getNome());
				}

				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),idSoggettoReferente,rs.getString("versione"));
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
			throw new DriverRegistroServiziException("getAllIdAccordiServizioParteComune error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	/**
	 * Crea un nuovo AccordoServizio inserendolo nel DB Se il parametro
	 * Connection e' null vuol dire che e' un'operazione atomica, altrimenti
	 * vuol dire che l'operazione e' stata richiesta dalla parte 'estesa' (GUI)
	 * e che non bisogna fare il commit a questo livello
	 * 
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		StatoFunzionalita confermaRicezione = accordoServizio.getConfermaRicezione();
		StatoFunzionalita conegnaInOrdine = accordoServizio.getConsegnaInOrdine();
		String descrizione = accordoServizio.getDescrizione();
		StatoFunzionalita filtroDuplicati = accordoServizio.getFiltroDuplicati();
		StatoFunzionalita identificativoCollaborazione = accordoServizio.getIdCollaborazione();

		ProfiloCollaborazione profiloCollaborazione = accordoServizio.getProfiloCollaborazione();
		String scadenza = accordoServizio.getScadenza();
		boolean utilizzioSenzaAzione = accordoServizio.getUtilizzoSenzaAzione();
		String wsdlConcettuale = (accordoServizio.getByteWsdlConcettuale()!=null ? new String(accordoServizio.getByteWsdlConcettuale()) : null);
		String wsdlDefinitorio = (accordoServizio.getByteWsdlDefinitorio()!=null ? new String(accordoServizio.getByteWsdlDefinitorio()) : null);
		String wsdlLogicoErogatore = (accordoServizio.getByteWsdlLogicoErogatore()!=null ? new String(accordoServizio.getByteWsdlLogicoErogatore()) : null);
		String wsdlLogicoFruitore = (accordoServizio.getByteWsdlLogicoFruitore()!=null ? new String(accordoServizio.getByteWsdlLogicoFruitore()) : null);
		String conversazioneConcettuale = (accordoServizio.getByteSpecificaConversazioneConcettuale()!=null ? new String(accordoServizio.getByteSpecificaConversazioneConcettuale()) : null);
		String conversazioneErogatore = (accordoServizio.getByteSpecificaConversazioneErogatore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneErogatore()) : null);
		String conversazioneFruitore = (accordoServizio.getByteSpecificaConversazioneFruitore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneFruitore()) : null);
		String superUser = accordoServizio.getSuperUser();

		wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
		wsdlDefinitorio = wsdlDefinitorio!=null && !"".equals(wsdlDefinitorio.trim().replaceAll("\n", "")) ? wsdlDefinitorio : null;
		wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;
		wsdlLogicoFruitore = wsdlLogicoFruitore!=null && !"".equals(wsdlLogicoFruitore.trim().replaceAll("\n", "")) ? wsdlLogicoFruitore : null;
		conversazioneConcettuale = conversazioneConcettuale!=null && !"".equals(conversazioneConcettuale.trim().replaceAll("\n", "")) ? conversazioneConcettuale : null;
		conversazioneErogatore = conversazioneErogatore!=null && !"".equals(conversazioneErogatore.trim().replaceAll("\n", "")) ? conversazioneErogatore : null;
		conversazioneFruitore = conversazioneFruitore!=null && !"".equals(conversazioneFruitore.trim().replaceAll("\n", "")) ? conversazioneFruitore : null;

		String sqlQuery = "";

		Connection connection = null;
		PreparedStatement stm = null;
		boolean error = false;

		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("createAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.ACCORDI);
			sqlQueryObject.addInsertField("conferma_ricezione", "?");
			sqlQueryObject.addInsertField("consegna_in_ordine", "?");
			sqlQueryObject.addInsertField("descrizione", "?");
			sqlQueryObject.addInsertField("filtro_duplicati", "?");
			sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
			sqlQueryObject.addInsertField("nome", "?");
			sqlQueryObject.addInsertField("profilo_collaborazione", "?");
			sqlQueryObject.addInsertField("scadenza", "?");
			sqlQueryObject.addInsertField("wsdl_concettuale", "?");
			sqlQueryObject.addInsertField("wsdl_definitorio", "?");
			sqlQueryObject.addInsertField("wsdl_logico_erogatore", "?");
			sqlQueryObject.addInsertField("wsdl_logico_fruitore", "?");
			sqlQueryObject.addInsertField("spec_conv_concettuale", "?");
			sqlQueryObject.addInsertField("spec_conv_erogatore", "?");
			sqlQueryObject.addInsertField("spec_conv_fruitore", "?");
			sqlQueryObject.addInsertField("superuser", "?");
			sqlQueryObject.addInsertField("utilizzo_senza_azione", "?");
			sqlQueryObject.addInsertField("privato", "?");
			if(accordoServizio.getStatoPackage()!=null)
				sqlQueryObject.addInsertField("stato", "?");
			if(accordoServizio.getSoggettoReferente()!=null)
				sqlQueryObject.addInsertField("id_referente", "?");
			sqlQueryObject.addInsertField("versione", "?");
			if(accordoServizio.getOraRegistrazione()!=null)
				sqlQueryObject.addInsertField("ora_registrazione", "?");
			sqlQuery = sqlQueryObject.createSQLInsert();
			stm = connection.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(confermaRicezione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(conegnaInOrdine));
			stm.setString(index++, descrizione);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(filtroDuplicati));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoCollaborazione));
			stm.setString(index++, nome);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(profiloCollaborazione));
			stm.setString(index++, scadenza);
			stm.setString(index++, wsdlConcettuale!=null && !wsdlConcettuale.trim().equals("") ? wsdlConcettuale : null);
			stm.setString(index++, wsdlDefinitorio!=null && !wsdlDefinitorio.trim().equals("") ? wsdlDefinitorio : null );
			stm.setString(index++ ,wsdlLogicoErogatore!=null && !wsdlLogicoErogatore.trim().equals("") ? wsdlLogicoErogatore : null );
			stm.setString(index++, wsdlLogicoFruitore!=null && !wsdlLogicoFruitore.trim().equals("") ? wsdlLogicoFruitore : null );
			stm.setString(index++, conversazioneConcettuale!=null && !conversazioneConcettuale.trim().equals("") ? conversazioneConcettuale : null);
			stm.setString(index++ ,conversazioneErogatore!=null && !conversazioneErogatore.trim().equals("") ? conversazioneErogatore : null );
			stm.setString(index++, conversazioneFruitore!=null && !conversazioneFruitore.trim().equals("") ? conversazioneFruitore : null );
			stm.setString(index++, superUser);
			stm.setInt(index++, utilizzioSenzaAzione ? CostantiDB.TRUE : CostantiDB.FALSE);
			if (accordoServizio.getPrivato()!=null && accordoServizio.getPrivato())
				stm.setInt(index++, 1);
			else
				stm.setInt(index++, 0);
			if(accordoServizio.getStatoPackage()!=null){
				stm.setString(index, accordoServizio.getStatoPackage());
				index++;
			}
			if(accordoServizio.getSoggettoReferente()!=null){
				long idReferente = DBUtils.getIdSoggetto(accordoServizio.getSoggettoReferente().getNome(), accordoServizio.getSoggettoReferente().getTipo(), connection, this.tipoDB, this.tabellaSoggetti);
				if(idReferente<=0){
					throw new DriverRegistroServiziException("Soggetto Referente ["+accordoServizio.getSoggettoReferente().getTipo()+"/"+accordoServizio.getSoggettoReferente().getNome()+"] non trovato");
				}
				stm.setLong(index, idReferente);
				index++;
			}

			if(accordoServizio.getVersione()!=null && !"".equals(accordoServizio.getVersione())){
				stm.setString(index, accordoServizio.getVersione());
			}else{
				stm.setString(index, CostantiRegistroServizi.VERSIONE_DEFAULT);
			}
			index++;

			if(accordoServizio.getOraRegistrazione()!=null){
				stm.setTimestamp(index, new Timestamp(accordoServizio.getOraRegistrazione().getTime()));
				index++;
			}

			this.log.debug("inserisco accordoServizio : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, confermaRicezione, conegnaInOrdine, descrizione, 
					filtroDuplicati, identificativoCollaborazione, nome, profiloCollaborazione, scadenza, 
					wsdlConcettuale, wsdlDefinitorio, wsdlLogicoErogatore, wsdlLogicoFruitore, 
					conversazioneConcettuale, conversazioneErogatore, conversazioneFruitore,
					superUser, accordoServizio.getUtilizzoSenzaAzione(), (accordoServizio.getPrivato()!=null && accordoServizio.getPrivato())));

			// eseguo la query
			stm.executeUpdate();
			stm.close();
			// recupero l-id dell'accordo appena inserito
			IDSoggetto soggettoReferente = null;
			if(accordoServizio.getSoggettoReferente()!=null){
				soggettoReferente = new IDSoggetto(accordoServizio.getSoggettoReferente().getTipo(),accordoServizio.getSoggettoReferente().getNome());
			}
			IDAccordo idAccordoObject = this.idAccordoFactory.getIDAccordoFromValues(accordoServizio.getNome(),soggettoReferente,accordoServizio.getVersione());
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idAccordoObject, connection, this.tipoDB);
			if (idAccordo<=0) {
				error = true;
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizio] non riesco a trovare l'id del'Accordo inserito");
			}
			accordoServizio.setId(idAccordo);
			// aggiungo le eventuali azioni che c-erano
			//sqlQuery = "INSERT INTO " + CostantiDB.ACCORDI_AZIONI + " (id_accordo, nome, filtro_duplicati, conferma_ricezione, identificativo_collaborazione, consegna_in_ordine, scadenza) " + "VALUES (?,?,?,?,?,?,?)";

			Azione azione = null;
			for (int i = 0; i < accordoServizio.sizeAzioneList(); i++) {
				azione = accordoServizio.getAzione(i);
				DriverRegistroServiziDB_LIB.CRUDAzione(CostantiDB.CREATE,accordoServizio, azione, connection, idAccordo);

				//				stm = connection.prepareStatement(sqlQuery);
				//				stm.setLong(1, idAccordo);
				//				stm.setString(2, azione.getNome());
				//				stm.setString(3, azione.getFiltroDuplicati());
				//				stm.setString(4, azione.getConfermaRicezione());
				//				stm.setString(5, azione.getIdCollaborazione());
				//				stm.setString(6, azione.getConsegnaInOrdine());
				//				stm.setString(7, azione.getScadenza());

				//				stm.executeUpdate();

			}
			this.log.debug("inserite " + accordoServizio.sizeAzioneList() + " azioni relative all'accordo :" + nome + " id :" + idAccordo);

			PortType pt = null;
			for (int i = 0; i < accordoServizio.sizePortTypeList(); i++) {
				pt = accordoServizio.getPortType(i);
				DriverRegistroServiziDB_LIB.CRUDPortType(CostantiDB.CREATE,accordoServizio,pt, connection, idAccordo);
			}
			this.log.debug("inserite " + accordoServizio.sizePortTypeList() + " porttype relative all'accordo :" + nome + " id :" + idAccordo);

			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.CREATE, 
						accordoServizio.getServizioComposto(), connection, idAccordo);
			}


			// Documenti generici accordo di servizio
			List<Documento> documenti = new ArrayList<Documento>();
			// Allegati
			for(int i=0; i<accordoServizio.sizeAllegatoList(); i++){
				Documento doc = accordoServizio.getAllegato(i);
				doc.setRuolo(RuoliDocumento.allegato.toString());
				documenti.add(doc);
			}
			// Specifiche Semiformali
			for(int i=0; i<accordoServizio.sizeSpecificaSemiformaleList(); i++){
				Documento doc = accordoServizio.getSpecificaSemiformale(i);
				doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
				documenti.add(doc);
			}
			// Specifiche Coordinamento
			if(accordoServizio.getServizioComposto()!=null){
				for(int i=0; i<accordoServizio.getServizioComposto().sizeSpecificaCoordinamentoList(); i++){
					Documento doc = accordoServizio.getServizioComposto().getSpecificaCoordinamento(i);
					doc.setRuolo(RuoliDocumento.specificaCoordinamento.toString());
					documenti.add(doc);
				}
			}
			// CRUD
			DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.CREATE, documenti, idAccordo, ProprietariDocumento.accordoServizio, connection, this.tipoDB);


		} catch (SQLException se) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteComune] SQLException [" + se.getMessage() + "].",se);
		} catch (DriverRegistroServiziException e) {
			error = true;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}catch (Exception e) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteComune] Exception [" + e.getMessage() + "].",e);
		}finally {

			try{
				stm.close();
			}catch(Exception e){}

			try {
				// se ci sono errori eseguo il rollback altrimenti il commit
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					connection.rollback();
					connection.setAutoCommit(true);
					connection.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					connection.commit();
					connection.setAutoCommit(true);
					connection.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 * 
	 * @param idAccordo id 
	 *                dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 */
	@Override
	public boolean existsAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComune(idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {

			// Check soggetto referente se esiste.
			if(idAccordo.getSoggettoReferente()!=null){
				if(this.existsSoggetto(idAccordo.getSoggettoReferente())==false){
					return false;
				}
			}

			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.tipoDB);
			if (idAccordoLong <= 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 * 
	 * @param idAccordo
	 *                dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComune(long idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComune(longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		PreparedStatement stm=null;
		ResultSet rs=null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			rs=stm.executeQuery();
			if (rs.next())
				return true;
			else
				return false;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					if(rs!=null) rs.close();
					if(stm!=null) stm.close();
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param nome
	 *                dell'azione da verificare
	 * @param idAccordo Identificativo dell'accordi di servizio
	 *               
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneAzione(String nome, IDAccordo idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(nome,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		try {
			return existsAccordoServizioParteComuneAzione(nome, DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.tipoDB));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param nome
	 *                dell'azione da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneAzione(String nome, long idAccordo) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneAzione(long idAzione) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComuneAzione(longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneAzione] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAzione);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	public boolean existsAccordoServizioParteComunePorttype(String nome, IDAccordo idAccordo) throws DriverRegistroServiziException {
		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComunePorttype(nome,idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttype] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		try {
			return existsAccordoServizioParteComunePorttype(nome, DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.tipoDB));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}



	/**
	 * Verifica l'esistenza di un'operation in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneOperation(long idAzione) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComuneOperation(longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComuneOperation] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAzione);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	/**
	 * Verifica l'esistenza di un port-type in un accordo
	 * 
	 * @param nome
	 *                del port-type da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se il port-type esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComunePorttype(String nome, long idAccordo) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComunePorttype(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttype] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordo);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	/**
	 * 
	 * @param nome Nome dell'operation
	 * @param idPortType L'identificativo del PortType
	 * @return true se esiste, false altrimenti.
	 * @throws DriverRegistroServiziException
	 */
	public boolean existsAccordoServizioParteComunePorttypeOperation(String nome, IDPortType idPortType) throws DriverRegistroServiziException {
		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComunePorttypeOperation(nome,idPortType)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttypeOperation] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		try {
			long idAccordo = DBUtils.getIdAccordoServizioParteComune(idPortType.getIdAccordo(), connection, this.tipoDB);
			return existsAccordoServizioParteComunePorttypeOperation(nome, DBUtils.getIdPortType(idAccordo,idPortType.getNome(), connection));
		}catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Verifica l'esistenza di un operation in un port-type
	 * 
	 * @param nome
	 *                dell'operation da verificare
	 * @param idPortType
	 *                del port-type
	 * @return true se l'operation esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComunePorttypeOperation(String nome, long idPortType) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteComunePorttypeOperation(nome,longId)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteComunePorttypeOperation] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idPortType);
			stm.setString(2, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	/**
	 * Aggiorna l'elemento nel database con i nuovi valori. Se il parametro
	 * Connection e' null vuol dire che e' un'operazione atomica, altrimenti
	 * vuol dire che l'operazione e' stata richiesta dalla parte 'estesa' (GUI)
	 * e che non bisogna fare il commit a questo livello
	 * 
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		Connection connection = null;
		PreparedStatement stm = null;
		ResultSet rs =null;
		String sqlQuery = "";

		StatoFunzionalita confermaRicezione = accordoServizio.getConfermaRicezione();
		StatoFunzionalita conegnaInOrdine = accordoServizio.getConsegnaInOrdine();
		String descrizione = accordoServizio.getDescrizione();
		StatoFunzionalita filtroDuplicati = accordoServizio.getFiltroDuplicati();
		StatoFunzionalita identificativoCollaborazione = accordoServizio.getIdCollaborazione();

		ProfiloCollaborazione profiloCollaborazione = accordoServizio.getProfiloCollaborazione();
		String scadenza = accordoServizio.getScadenza();
		boolean utilizzioSenzaAzione = accordoServizio.getUtilizzoSenzaAzione();

		String wsdlConcettuale = (accordoServizio.getByteWsdlConcettuale()!=null ? new String(accordoServizio.getByteWsdlConcettuale()) : null);
		String wsdlDefinitorio = (accordoServizio.getByteWsdlDefinitorio()!=null ? new String(accordoServizio.getByteWsdlDefinitorio()) : null);
		String wsdlLogicoErogatore = (accordoServizio.getByteWsdlLogicoErogatore()!=null ? new String(accordoServizio.getByteWsdlLogicoErogatore()) : null);
		String wsdlLogicoFruitore = (accordoServizio.getByteWsdlLogicoFruitore()!=null ? new String(accordoServizio.getByteWsdlLogicoFruitore()) : null);
		String conversazioneConcettuale = (accordoServizio.getByteSpecificaConversazioneConcettuale()!=null ? new String(accordoServizio.getByteSpecificaConversazioneConcettuale()) : null);
		String conversazioneErogatore = (accordoServizio.getByteSpecificaConversazioneErogatore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneErogatore()) : null);
		String conversazioneFruitore = (accordoServizio.getByteSpecificaConversazioneFruitore()!=null ? new String(accordoServizio.getByteSpecificaConversazioneFruitore()) : null);
		String superUser = accordoServizio.getSuperUser();

		wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
		wsdlDefinitorio = wsdlDefinitorio!=null && !"".equals(wsdlDefinitorio.trim().replaceAll("\n", "")) ? wsdlDefinitorio : null;
		wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;
		wsdlLogicoFruitore = wsdlLogicoFruitore!=null && !"".equals(wsdlLogicoFruitore.trim().replaceAll("\n", "")) ? wsdlLogicoFruitore : null;
		conversazioneConcettuale = conversazioneConcettuale!=null && !"".equals(conversazioneConcettuale.trim().replaceAll("\n", "")) ? conversazioneConcettuale : null;
		conversazioneErogatore = conversazioneErogatore!=null && !"".equals(conversazioneErogatore.trim().replaceAll("\n", "")) ? conversazioneErogatore : null;
		conversazioneFruitore = conversazioneFruitore!=null && !"".equals(conversazioneFruitore.trim().replaceAll("\n", "")) ? conversazioneFruitore : null;

		boolean error = false;

		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("updateAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::createAccordoServizio] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			IDAccordo idAccordoAttualeInseritoDB = null;
			if(accordoServizio.getOldIDAccordoForUpdate()!=null){
				idAccordoAttualeInseritoDB = accordoServizio.getOldIDAccordoForUpdate();
			}else{
				idAccordoAttualeInseritoDB = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
			}

			long idAccordoLong = -1;
			try{
				idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordoAttualeInseritoDB, connection,this.tipoDB);
			}catch(Exception e){
				if(accordoServizio.getOldIDAccordoForUpdate()!=null){
					// Provo con soggetto attuale
					if(accordoServizio.getSoggettoReferente()!=null){
						idAccordoAttualeInseritoDB = this.idAccordoFactory.getIDAccordoFromValues(idAccordoAttualeInseritoDB.getNome(),
								new IDSoggetto(accordoServizio.getSoggettoReferente().getTipo(),
										accordoServizio.getSoggettoReferente().getNome()),
										idAccordoAttualeInseritoDB.getVersione());
					}else{
						idAccordoAttualeInseritoDB = this.idAccordoFactory.getIDAccordoFromValues(idAccordoAttualeInseritoDB.getNome(),null,
								idAccordoAttualeInseritoDB.getVersione());
					}
					idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordoAttualeInseritoDB, connection,this.tipoDB);
				}else{
					throw e;
				}
			}
			if (idAccordoLong <= 0)
				throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Servizio : " + nome);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.ACCORDI);
			sqlQueryObject.addUpdateField("conferma_ricezione", "?");
			sqlQueryObject.addUpdateField("consegna_in_ordine", "?");
			sqlQueryObject.addUpdateField("descrizione", "?");
			sqlQueryObject.addUpdateField("filtro_duplicati", "?");
			sqlQueryObject.addUpdateField("identificativo_collaborazione", "?");
			sqlQueryObject.addUpdateField("nome", "?");
			sqlQueryObject.addUpdateField("profilo_collaborazione", "?");
			sqlQueryObject.addUpdateField("scadenza", "?");
			sqlQueryObject.addUpdateField("wsdl_concettuale", "?");
			sqlQueryObject.addUpdateField("wsdl_definitorio", "?");
			sqlQueryObject.addUpdateField("wsdl_logico_erogatore", "?");
			sqlQueryObject.addUpdateField("wsdl_logico_fruitore", "?");
			sqlQueryObject.addUpdateField("spec_conv_concettuale", "?");
			sqlQueryObject.addUpdateField("spec_conv_erogatore", "?");
			sqlQueryObject.addUpdateField("spec_conv_fruitore", "?");
			sqlQueryObject.addUpdateField("superuser", "?");
			sqlQueryObject.addUpdateField("utilizzo_senza_azione", "?");
			sqlQueryObject.addUpdateField("privato", "?");

			if(accordoServizio.getStatoPackage()!=null){
				sqlQueryObject.addUpdateField("stato", "?");
			}

			if(accordoServizio.getOraRegistrazione()!=null)
				sqlQueryObject.addUpdateField("ora_registrazione", "?");

			sqlQueryObject.addUpdateField("id_referente", "?");
			sqlQueryObject.addUpdateField("versione", "?");

			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLUpdate();

			stm = connection.prepareStatement(sqlQuery);
			int index = 1;
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(confermaRicezione));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(conegnaInOrdine));
			stm.setString(index++, descrizione);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(filtroDuplicati));
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(identificativoCollaborazione));
			stm.setString(index++, nome);
			stm.setString(index++, DriverRegistroServiziDB_LIB.getValue(profiloCollaborazione));
			stm.setString(index++, scadenza);
			stm.setString(index++, wsdlConcettuale);
			stm.setString(index++, wsdlDefinitorio);
			stm.setString(index++, wsdlLogicoErogatore);
			stm.setString(index++, wsdlLogicoFruitore);
			stm.setString(index++, conversazioneConcettuale);
			stm.setString(index++, conversazioneErogatore);
			stm.setString(index++, conversazioneFruitore);
			stm.setString(index++, superUser);
			stm.setInt(index++, utilizzioSenzaAzione ? CostantiDB.TRUE : CostantiDB.FALSE);
			if(accordoServizio.getPrivato()!=null && accordoServizio.getPrivato())
				stm.setInt(index++, 1);
			else
				stm.setInt(index++, 0);

			if(accordoServizio.getStatoPackage()!=null){
				stm.setString(index++, accordoServizio.getStatoPackage());
			}

			if(accordoServizio.getOraRegistrazione()!=null){
				stm.setTimestamp(index++, new Timestamp(accordoServizio.getOraRegistrazione().getTime()));
			}

			if(accordoServizio.getSoggettoReferente()!=null) {
				long idSRef = DBUtils.getIdSoggetto(accordoServizio.getSoggettoReferente().getNome(), 
						accordoServizio.getSoggettoReferente().getTipo(), connection, this.tipoDB,this.tabellaSoggetti);
				stm.setLong(index++, idSRef);
			}else{
				stm.setLong(index++, CostantiRegistroServizi.SOGGETTO_REFERENTE_DEFAULT);
			}
			if(accordoServizio.getVersione()!=null && !"".equals(accordoServizio.getVersione()) ) {
				stm.setString(index++, accordoServizio.getVersione());
			}else{
				stm.setString(index++, CostantiRegistroServizi.VERSIONE_DEFAULT);
			}

			stm.setLong(index++, idAccordoLong);

			this.log.debug("update accordoServizio : " + 
					DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, confermaRicezione, conegnaInOrdine, descrizione, 
							filtroDuplicati, identificativoCollaborazione, nome, profiloCollaborazione, scadenza, 
							wsdlConcettuale, wsdlDefinitorio, wsdlLogicoErogatore, wsdlLogicoFruitore, 
							conversazioneConcettuale, conversazioneErogatore, conversazioneFruitore,
							superUser,utilizzioSenzaAzione, idAccordoLong));

			stm.executeUpdate();
			stm.close();

			//aggiorno le azioni
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le azioni necessarie
			//prima cancello le azioni e poi reinserisco quelle nuove
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = connection.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			int n=stm.executeUpdate();
			stm.close();
			this.log.debug("Cancellate "+n+" azioni associate all'accordo "+idAccordoLong);

			for (int i = 0; i < accordoServizio.sizeAzioneList(); i++) {
				Azione azione = accordoServizio.getAzione(i);
				String profiloAzione = azione.getProfAzione();
				//se profilo azione = default allora utilizzo il profilo collaborazione dell'accordo
				if(profiloAzione!=null && profiloAzione.equals(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT))
				{
					azione.setProfiloCollaborazione(profiloCollaborazione);
				}
				DriverRegistroServiziDB_LIB.CRUDAzione(CostantiDB.CREATE, accordoServizio,azione, connection, idAccordoLong);
			}
			this.log.debug("Inserite "+accordoServizio.sizeAzioneList()+" azioni associate all'accordo "+idAccordoLong);

			//aggiorno i port type
			//TODO possibile ottimizzazione
			//la lista contiene tutte e soli i port type necessari
			//prima cancello i port type e poi reinserisco quelle nuove

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			Vector<Long> idPT = new Vector<Long>();
			while(rs.next()){
				idPT.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
			this.log.debug("Trovati "+idPT.size()+" port type...");

			while(idPT.size()>0){
				Long idPortType = idPT.remove(0);

				// Seleziono id port type azione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm=connection.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				rs=stm.executeQuery();
				Vector<Long> idPTAzione = new Vector<Long>();
				while(rs.next()){
					idPTAzione.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();

				this.log.debug("Trovati "+idPTAzione.size()+" port type azioni...");

				// Elimino i messages
				while(idPTAzione.size()>0){
					Long idPortTypeAzione = idPTAzione.remove(0);
					this.log.debug("Eliminazione message con id["+idPortTypeAzione+"]...");
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObject.addWhereCondition("id_port_type_azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm=connection.prepareStatement(sqlQuery);
					stm.setLong(1, idPortTypeAzione);
					n=stm.executeUpdate();
					stm.close();
					this.log.debug("Cancellate "+n+" messages di un'azione con id["+idPortTypeAzione+"] del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
				}

				this.log.debug("Elimino port type azione del port types ["+idPortType+"]...");

				// Elimino port types azioni
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm=connection.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				n=stm.executeUpdate();
				stm.close();
				this.log.debug("Cancellate "+n+" azioni del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
			}

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			n=stm.executeUpdate();
			stm.close();
			this.log.debug("Cancellate "+n+" port type associate all'accordo "+idAccordoLong);

			PortType pt = null;
			for (int i = 0; i < accordoServizio.sizePortTypeList(); i++) {
				pt = accordoServizio.getPortType(i);
				DriverRegistroServiziDB_LIB.CRUDPortType(CostantiDB.CREATE,accordoServizio,pt, connection, idAccordoLong);
			}
			this.log.debug("inserite " + accordoServizio.sizePortTypeList() + " porttype relative all'accordo :" + nome + " id :" + idAccordoLong);


			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				// Elimino eventualmente se prima era presente
				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, connection, idAccordoLong);

				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.CREATE, 
						accordoServizio.getServizioComposto(), connection, idAccordoLong);
			}else{
				// Elimino eventualmente se prima era presente
				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, connection, idAccordoLong);
			}


			// Documenti generici accordo di servizio
			List<Documento> documenti = new ArrayList<Documento>();
			// Allegati
			for(int i=0; i<accordoServizio.sizeAllegatoList(); i++){
				Documento doc = accordoServizio.getAllegato(i);
				doc.setRuolo(RuoliDocumento.allegato.toString());
				documenti.add(doc);
			}
			// Specifiche Semiformali
			for(int i=0; i<accordoServizio.sizeSpecificaSemiformaleList(); i++){
				Documento doc = accordoServizio.getSpecificaSemiformale(i);
				doc.setRuolo(RuoliDocumento.specificaSemiformale.toString());
				documenti.add(doc);
			}
			// Specifiche Coordinamento
			if(accordoServizio.getServizioComposto()!=null){
				for(int i=0; i<accordoServizio.getServizioComposto().sizeSpecificaCoordinamentoList(); i++){
					Documento doc = accordoServizio.getServizioComposto().getSpecificaCoordinamento(i);
					doc.setRuolo(RuoliDocumento.specificaCoordinamento.toString());
					documenti.add(doc);
				}
			}

			// CRUD
			DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.UPDATE, documenti, idAccordoLong, ProprietariDocumento.accordoServizio, connection, this.tipoDB);

		}catch (SQLException se) {
			this.log.error(se.getMessage(),se);
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizio] SQLException [" + se.getMessage() + "].",se);
		} 
		catch (Exception se) {
			this.log.error(se.getMessage(),se);
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(stm!=null)
					stm.close();
			}catch(Exception e){}
			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					connection.rollback();
					connection.setAutoCommit(true);
					connection.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					connection.commit();
					connection.setAutoCommit(true);
					connection.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	/**
	 * Aggiorna l'elemento nel database con i nuovi valori. Se il parametro
	 * Connection e' null vuol dire che e' un'operazione atomica, altrimenti
	 * vuol dire che l'operazione e' stata richiesta dalla parte 'estesa' (GUI)
	 * e che non bisogna fare il commit a questo livello
	 * 
	 * @param portType
	 * @throws DriverRegistroServiziException
	 */
	public void updatePortType(org.openspcoop2.core.registry.PortType portType) throws DriverRegistroServiziException {

		if (portType == null)
			throw new DriverRegistroServiziException("Il port-type non puo' essere null.");

		String nome = portType.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome del port-type non e' valido.");

		Connection connection = null;
		PreparedStatement stm = null, updateStmt = null;
		ResultSet rs =null;
		//String sqlQuery = ""; // updateQuery = "";

		boolean error = false;

		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("updatePortType");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::updatePortType] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			// NOTA: questo metodo viene chiamato solo quando si vogliono aggiornare le azioni

			//Prendo l'accordo per gestire il caso di "usa profilo accordo"
			AccordoServizioParteComune as = getAccordoServizioParteComune(portType.getIdAccordo(), connection);

			if(portType.getId()==null || portType.getId()<=0){
				for (PortType ptCheck : as.getPortTypeList()) {
					if(ptCheck.getNome().equals(portType.getNome())){
						portType.setId(ptCheck.getId());
					}
				}
			}
			if(portType.getId()==null || portType.getId()<=0){
				throw new Exception("Id PortType undefined");
			}
			
			//TODO possibile ottimizzazione
			//la lista contiene tutte e sole le azioni necessarie
			//prima cancello le azioni e poi reinserisco quelle nuove
//			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
//			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
//			sqlQueryObject.addWhereCondition("id_port_type=?");
//			sqlQuery = sqlQueryObject.createSQLDelete();
//			stm=connection.prepareStatement(sqlQuery);
//			stm.setLong(1, portType.getId());
//			int n=stm.executeUpdate();
//			stm.close();
			
			List<Long> idsOperation = new ArrayList<Long>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addWhereCondition("id_port_type=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(queryString);
			stm.setLong(1, portType.getId());
			rs=stm.executeQuery();
			while(rs.next()){
				idsOperation.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();
			
			if(idsOperation!=null && idsOperation.size()>0){
				for (Long id : idsOperation) {
					ISQLQueryObject sqlQueryObjectMessages = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObjectMessages.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObjectMessages.addWhereCondition("id_port_type_azione=?");
					String updateString = sqlQueryObjectMessages.createSQLDelete();
					stm = connection.prepareStatement(updateString);
					stm.setLong(1, id);
					int n=stm.executeUpdate();
					stm.close();
					this.log.debug("Cancellate "+n+" operation messages associate all'azione con id "+id+" del port type "+portType.getNome()+ " dell'accordo: "+as.getNome());
				}
			}
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addWhereCondition("id_port_type=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = connection.prepareStatement(updateString);
			stm.setLong(1, portType.getId());
			int n=stm.executeUpdate();
			stm.close();
			this.log.debug("Cancellate "+n+" azioni associate al portType "+portType.getNome()+ " dell'accordo: "+as.getNome());
			
//			Operation azione = null;
			for (int i = 0; i < portType.sizeAzioneList(); i++) {
				Operation azione = portType.getAzione(i);
				
				
				DriverRegistroServiziDB_LIB.CRUDAzionePortType(CostantiDB.CREATE,as,portType,azione, connection, portType.getId());

//				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
//				sqlQueryObject.addInsertTable(CostantiDB.PORT_TYPE_AZIONI);
//				sqlQueryObject.addInsertField("id_port_type", "?");
//				sqlQueryObject.addInsertField("nome", "?");
//				sqlQueryObject.addInsertField("profilo_pt_azione", "?");
//				sqlQueryObject.addInsertField("filtro_duplicati", "?");
//				sqlQueryObject.addInsertField("conferma_ricezione", "?");
//				sqlQueryObject.addInsertField("identificativo_collaborazione", "?");
//				sqlQueryObject.addInsertField("consegna_in_ordine", "?");
//				sqlQueryObject.addInsertField("scadenza", "?");
//				sqlQueryObject.addInsertField("profilo_collaborazione", "?");
//				sqlQueryObject.addInsertField("correlata", "?");
//				sqlQueryObject.addInsertField("correlata_servizio", "?");
//
//
//				updateQuery = sqlQueryObject.createSQLInsert();
//				updateStmt = connection.prepareStatement(updateQuery);
//				updateStmt.setLong(1, portType.getId());
//				updateStmt.setString(2, azione.getNome());
//				updateStmt.setString(3, azione.getProfAzione());
//
//				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(azione.getProfAzione())){
//					updateStmt.setString(4, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
//					updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
//					updateStmt.setString(6, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
//					updateStmt.setString(7, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
//					updateStmt.setString(8, azione.getScadenza());
//					updateStmt.setString(9, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
//					updateStmt.setString(10, azione.getCorrelata());
//					updateStmt.setString(11, azione.getCorrelataServizio());
//				}else{
//					if(azione.getFiltroDuplicati()!=null)
//						updateStmt.setString(4, DriverRegistroServiziDB_LIB.getValue(azione.getFiltroDuplicati()));
//					else if(portType.getFiltroDuplicati()!=null)
//						updateStmt.setString(4, DriverRegistroServiziDB_LIB.getValue(portType.getFiltroDuplicati()));
//					else
//						updateStmt.setString(4, DriverRegistroServiziDB_LIB.getValue(as.getFiltroDuplicati()));
//
//					if(azione.getConfermaRicezione()!=null)
//						updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(azione.getConfermaRicezione()));
//					else if(portType.getConfermaRicezione()!=null)
//						updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(portType.getConfermaRicezione()));
//					else
//						updateStmt.setString(5, DriverRegistroServiziDB_LIB.getValue(as.getConfermaRicezione()));
//
//					if(azione.getIdCollaborazione()!=null)
//						updateStmt.setString(6, DriverRegistroServiziDB_LIB.getValue(azione.getIdCollaborazione()));
//					else if(portType.getIdCollaborazione()!=null)
//						updateStmt.setString(6, DriverRegistroServiziDB_LIB.getValue(portType.getIdCollaborazione()));
//					else
//						updateStmt.setString(6, DriverRegistroServiziDB_LIB.getValue(as.getIdCollaborazione()));
//
//					if(azione.getConsegnaInOrdine()!=null)
//						updateStmt.setString(7, DriverRegistroServiziDB_LIB.getValue(azione.getConsegnaInOrdine()));
//					else if(portType.getConsegnaInOrdine()!=null)
//						updateStmt.setString(7, DriverRegistroServiziDB_LIB.getValue(portType.getConsegnaInOrdine()));
//					else
//						updateStmt.setString(7, DriverRegistroServiziDB_LIB.getValue(as.getConsegnaInOrdine()));
//
//					if(azione.getScadenza()!=null)
//						updateStmt.setString(8, azione.getScadenza());
//					else if(portType.getScadenza()!=null)
//						updateStmt.setString(8, portType.getScadenza());
//					else
//						updateStmt.setString(8, as.getScadenza());
//
//					if(azione.getProfiloCollaborazione()!=null)
//						updateStmt.setString(9, DriverRegistroServiziDB_LIB.getValue(azione.getProfiloCollaborazione()));
//					else if(portType.getProfiloCollaborazione()!=null)
//						updateStmt.setString(9, DriverRegistroServiziDB_LIB.getValue(portType.getProfiloCollaborazione()));
//					else
//						updateStmt.setString(9, DriverRegistroServiziDB_LIB.getValue(as.getProfiloCollaborazione()));
//
//					updateStmt.setString(10, azione.getCorrelata());
//					updateStmt.setString(11, azione.getCorrelataServizio());
//
//				}
//				n = updateStmt.executeUpdate();
//				updateStmt.close();
			}
			DriverRegistroServiziDB_LIB.log.debug("inserite " + portType.sizeAzioneList() + " azioni relative al port type["+portType.getNome()+"] id-porttype["+portType.getId()+"]");
		} 
//		catch (SQLException se) {
//			this.log.error(se);
//			error = true;
//			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortType] SQLException [" + se.getMessage() + "].",se);
//		} 
		catch (Exception se) {
			this.log.error(se.getMessage(),se);
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortType] Exception [" + se.getMessage() + "].",se);
		}finally {
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{

				if(stm!=null) stm.close();
				if(updateStmt!=null) updateStmt.close();
			}catch(Exception e){}
			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					connection.rollback();
					connection.setAutoCommit(true);
					connection.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					connection.commit();
					connection.setAutoCommit(true);
					connection.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	/**
	 * Elimina un AccordoServizio nel database e le eventuali azioni collegate
	 * Se il parametro Connection e' null vuol dire che e' un'operazione
	 * atomica, altrimenti vuol dire che l'operazione e' stata richiesta dalla
	 * parte 'estesa' (GUI) e che non bisogna fare il commit a questo livello
	 * 
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteAccordoServizioParteComune(org.openspcoop2.core.registry.AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException {

		if (accordoServizio == null)
			throw new DriverRegistroServiziException("L'AccordoServizio non puo' essere null.");

		String nome = accordoServizio.getNome();
		if (nome == null || nome.equals(""))
			throw new DriverRegistroServiziException("Il nome dell'AccordoServizio non e' valido.");

		Connection connection = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		boolean error = false;

		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("deleteAccordoServizioParteComune");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::deleteAccordoServizioParteComune] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(nome,BeanUtilities.getSoggettoReferenteID(accordoServizio.getSoggettoReferente()),accordoServizio.getVersione());
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, connection, this.tipoDB);
			if (idAccordoLong <= 0)
				throw new DriverRegistroServiziException("Impossibile recuperare l'id dell'Accordo di Servizio : " + nome);

			// elimino tutte le azioni correlate con questo accordo
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			String updateString = sqlQueryObject.createSQLDelete();
			stm = connection.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			this.log.debug("delete azioni :" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			int n=stm.executeUpdate();
			stm.close();
			this.log.debug("cancellate " + n + " azioni.");

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			rs=stm.executeQuery();
			Vector<Long> idPT = new Vector<Long>();
			while(rs.next()){
				idPT.add(rs.getLong("id"));
			}
			rs.close();
			stm.close();

			while(idPT.size()>0){
				Long idPortType = idPT.remove(0);

				// gestione operation_messages
				Vector<Long> idPortTypeAzioni = new Vector<Long>();
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm=connection.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				rs=stm.executeQuery();
				while(rs.next()){
					idPortTypeAzioni.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();

				while(idPortTypeAzioni.size()>0){
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
					sqlQueryObject.addWhereCondition("id_port_type_azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm=connection.prepareStatement(sqlQuery);
					stm.setLong(1, idPortTypeAzioni.remove(0));
					n=stm.executeUpdate();
					stm.close();
				}

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addWhereCondition("id_port_type=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm=connection.prepareStatement(sqlQuery);
				stm.setLong(1, idPortType);
				n=stm.executeUpdate();
				stm.close();
				this.log.debug("Cancellate "+n+" azioni del port type ["+idPortType+"] associate all'accordo "+idAccordoLong);
			}

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLDelete();
			stm=connection.prepareStatement(sqlQuery);
			stm.setLong(1, idAccordoLong);
			n=stm.executeUpdate();
			stm.close();
			this.log.debug("Cancellate "+n+" port type associate all'accordo "+idAccordoLong);

			// Documenti generici accordo di servizio
			// Allegati
			// Specifiche Semiformali
			// Speficiche Coordinamento
			DriverRegistroServiziDB_LIB.CRUDDocumento(CostantiDB.DELETE, null, idAccordoLong, ProprietariDocumento.accordoServizio, connection, this.tipoDB);


			// Accordo servizio composto
			if(accordoServizio.getServizioComposto()!=null){
				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						accordoServizio.getServizioComposto(), connection, idAccordoLong);
			}else{
				DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteComuneServizioComposto(CostantiDB.DELETE, 
						null, connection, idAccordoLong);
			}


			// elimino accordoservizio
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI);
			sqlQueryObject.addWhereCondition("id=?");
			updateString = sqlQueryObject.createSQLDelete();
			stm = connection.prepareStatement(updateString);
			stm.setLong(1, idAccordoLong);
			this.log.debug("delete accordoServizio :" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idAccordoLong));
			stm.executeUpdate();
			stm.close();


		} catch (SQLException se) {

			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizio] SQLException [" + se.getMessage() + "].",se);
		} catch (Exception se) {

			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizio] Exception [" + se.getMessage() + "].",se);
		}finally {

			try {
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			} catch (Exception e) {

			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					connection.rollback();
					connection.setAutoCommit(true);
					connection.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					connection.commit();
					connection.setAutoCommit(true);
					connection.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	/* Porte di Dominio */

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.PortaDominio}, 
	 * identificato grazie al parametro 
	 * <var>nomePdD</var> 
	 *
	 * @param nomePdD Nome della Porta di Dominio
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.PortaDominio}.
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.PortaDominio getPortaDominio(String nomePdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		this.log.debug("richiesto getPortaDominio: " + nomePdD);
		// conrollo consistenza
		if (nomePdD == null)
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro nomePdD is null");
		if (nomePdD.trim().equals(""))
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro nomePdD non e' definito");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getPortaDominio(nome)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			String queryString = sqlQueryObject
					.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setString(1, nomePdD);
			rs = stm.executeQuery();
			PortaDominio pdd = null;
			if (rs.next()) {
				pdd = new PortaDominio();
				pdd.setId(rs.getLong("id"));
				pdd.setNome(rs.getString("nome"));
				pdd.setDescrizione(rs.getString("descrizione"));
				pdd.setImplementazione(rs.getString("implementazione"));
				pdd.setSubject(rs.getString("subject"));
				pdd.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(rs.getString("client_auth")));
				pdd.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					pdd.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getPortaDominio] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(queryString, nomePdD));
			}

			return pdd;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPortaDominio] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPortaDominio] Exception: " + se.getMessage(),se);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	/**
	 * Ritorna gli identificatori delle PdD che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID degli accordi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdPorteDominio...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdPorteDominio");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("nome");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione < ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition("nome = ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition("tipo = ?");
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.log.debug("tipoPdd stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
			}
			rs = stm.executeQuery();
			Vector<String> nomiPdd = new Vector<String>();
			while (rs.next()) {
				nomiPdd.add(rs.getString("nome"));
			}
			if(nomiPdd.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate");
			}else{
				return nomiPdd;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdPorteDominio error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}



	/**
	 * Crea una nuova Porta di Dominio 
	 * 
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("createPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaDominio type = 1");
			// creo soggetto
			DriverRegistroServiziDB_LIB.CRUDPortaDominio(CostantiDB.CREATE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Errore durante la creazione del soggetto : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Verifica l'esistenza di una Porta di Dominio.
	 *
	 * @param nome della porta di dominio da verificare
	 * @return true se la porta di dominio esiste, false altrimenti
	 * @throws DriverRegistroServiziException TODO
	 */    
	@Override
	public boolean existsPortaDominio(String nome) throws DriverRegistroServiziException{
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (nome == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (nome.equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.log.error("Errore durante verifica esistenza porta di dominio :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		return exist;
	}

	/**
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Parametro non valido.");

		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updatePortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			// UPDATE porta di dominio
			DriverRegistroServiziDB_LIB.CRUDPortaDominio(CostantiDB.UPDATE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Errore durante l'aggiornamento della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {

			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param nomePdd
	 * @param tipo
	 * @throws DriverRegistroServiziException
	 */
	public void updateTipoPortaDominio(String nomePdd,String tipo) throws DriverRegistroServiziException{
		if (nomePdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateTipoPortaDominio] Parametro non valido.");
		if (tipo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateTipoPortaDominio] Parametro.tipo non valido.");

		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updateTipoPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.PDD);
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, tipo);
			stm.setString(2, nomePdd);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipo, nomePdd));
			stm.executeUpdate();

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Errore durante l'aggiornamento della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {

			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Elimina una Porta di Dominio 
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deletePortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 3");
			// DELETE porta dominio
			DriverRegistroServiziDB_LIB.CRUDPortaDominio(CostantiDB.DELETE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Errore durante l'eliminazione della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Si occupa di ritornare l'oggetto
	 * {@link org.openspcoop2.core.registry.Soggetto}, identificato grazie
	 * al parametro <var>idSoggetto</var> di tipo
	 * {@link org.openspcoop2.core.id.IDSoggetto}.
	 * 
	 * @param idSoggetto
	 *                Identificatore del Soggetto di tipo
	 *                {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @return l'oggetto di tipo
	 *         {@link org.openspcoop2.core.registry.Soggetto} se la ricerca
	 *         nel registro ha successo, null altrimenti.
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.Soggetto getSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// conrollo consistenza
		if (idSoggetto == null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametro idSoggetto is null");

		String nomeSogg = idSoggetto.getNome();
		String tipoSogg = idSoggetto.getTipo();

		if (nomeSogg == null || nomeSogg.trim().equals("") || tipoSogg == null || tipoSogg.trim().equals(""))
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] : soggetto non specificato.");

		Soggetto soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getSoggetto(idSoggetto)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nomeSogg);
			stm.setString(2, tipoSogg);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSogg, tipoSogg));
			rs = stm.executeQuery();

			if (rs.next()) {
				soggetto = new Soggetto();

				soggetto.setId(rs.getLong("id"));
				// String tmp = rs.getString("nomeprov");
				// soggetto.setNome(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				soggetto.setNome(nomeSogg);
				// tmp = rs.getString("tipoprov");
				// soggetto.setTipo(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
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
				Connettore connettore = getConnettore(idConnettore, con);

				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					soggetto.setVersioneProtocollo(profilo);
				}

				// aggiungo connettore
				soggetto.setConnettore(connettore);

				// Questi non li implemento per adesso
				// soggetto.addServizio()
				// soggetto.addServizioCorrelato()

			}else{
				throw new DriverRegistroServiziNotFound("Nessun soggetto trovato eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSogg, tipoSogg));
			}

			return soggetto;
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 *  Ritorna gli identificatori dei soggetti che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei soggetti trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdSoggettiRegistro...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdSoggetti");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione < ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				if(filtroRicerca.getNomePdd()!=null)
					sqlQueryObject.addWhereCondition("server = ?");
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.log.debug("tipoSoggetto stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nomeSoggetto stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getNomePdd()!=null){
					this.log.debug("nomePdD stmt.setString("+filtroRicerca.getNomePdd()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePdd());
					indexStmt++;
				}	
			}
			rs = stm.executeQuery();
			Vector<IDSoggetto> idSoggetti = new Vector<IDSoggetto>();
			while (rs.next()) {
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
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("createSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 1");
			// creo soggetto
			DriverRegistroServiziDB_LIB.CRUDSoggetto(1, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Verifica l'esistenza di un soggetto registrato.
	 * 
	 * @param idSoggetto
	 *                Identificativo del soggetto
	 * @return true se il soggetto esiste, false altrimenti
	 */
	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
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
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsSoggetto(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome_soggetto);
			stm.setString(2, tipo_soggetto);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		return exist;
	}

	public boolean existsSoggetto(long idSoggetto) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsSoggetto(longId)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		return exist;
	}

	public boolean existsSoggetto(String codiceIPA) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (codiceIPA == null|| codiceIPA.equals(""))
			throw new DriverRegistroServiziException("Parametro non valido");

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsSoggetto(codiceIPA)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		return exist;
	}

	public Soggetto getSoggetto(String codiceIPA) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto = -1;

		if (codiceIPA == null|| codiceIPA.equals(""))
			throw new DriverRegistroServiziException("Parametro non valido");

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getSoggetto(codiceIPA)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		if(idSoggetto<=0){
			throw new DriverRegistroServiziNotFound("Soggetto con Codice IPA ["+codiceIPA+"] non trovato");
		}
		else{
			return this.getSoggetto(idSoggetto);
		}

	}

	public String getCodiceIPA(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getCodiceIPA(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		if(codiceIPA==null){
			throw new DriverRegistroServiziNotFound("Soggetto ["+tipo_soggetto+"/"+nome_soggetto+"] non trovato");
		}
		else{
			return codiceIPA;
		}

	}

	/**
	 * Aggiorna un Soggetto e il Connettore con i nuovi dati passati
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Parametro non valido.");

		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updateSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			/**
			 * L'aggiornamente del tipo/nome soggetto scatena anche l'aggiornamento del nome
			 * dei connettori oltre che del soggetto stesso anche dei fruitori di servizi in cui il soggetto puo essere
			 * o l'erogatore o il fruitore
			 */
			String oldNomeSoggetto=soggetto.getOldNomeForUpdate();
			String oldTipoSoggetto=soggetto.getOldTipoForUpdate();
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
					Connettore connettore = this.getConnettore(idConnettore, con);
					String oldNomeConnettore=connettore.getNome();
					//controllo se il nome connettore matcha la regex
					if(oldNomeConnettore.matches(regex)){
						this.log.debug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+oldNomeConnettore+"]...");
						//splitto la stringa in modo da prendermi i valori separatamente
						//l'array sara composto da [CNT,SF,tipo/nomeFruitore,tipo/nomeErogatore,tipo/nomeServizio]
						String[] val=oldNomeConnettore.split("_");
						//i=2 contiene il tipo/nome del soggetto fruitore
						//i=3 contiene il tipo/nome del soggetto erogatore (che bisogna cambiare)
						//i=4 contiene il tipo/nome del servizio
						String newNomeConnettore = "CNT_SF_"+val[2]+ tipoSoggetto+"/"+nomeSoggetto +val[4];
						this.log.debug("nuovo nome connettore ["+newNomeConnettore+"]");
						connettore.setNome(newNomeConnettore);
						//aggiorno il connettore
						DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
					}
				}
				rs.close();
				stm.close();
				//recupero i connettori da modificare dove il soggetto e' fruitore
				//0 soggetti
				//1 servizi_fruitori
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
					Connettore connettore = this.getConnettore(idConnettore, con);
					String oldNomeConnettore=connettore.getNome();
					//controllo se il nome connettore matcha la regex
					if(oldNomeConnettore.matches(regex)){
						this.log.debug("Tento aggiornamento connettore id: ["+idConnettore+"] oldNome: ["+oldNomeConnettore+"]...");
						//splitto la stringa in modo da prendermi i valori separatamente
						//l'array sara composto da [CNT,SF,tipo/nomeFruitore,tipo/nomeErogatore,tipo/nomeServizio]
						String[] val=oldNomeConnettore.split("_");
						//i=2 contiene il tipo/nome del soggetto fruitore (che bisogna cambiare)
						//i=3 contiene il tipo/nome del soggetto erogatore 
						//i=4 contiene il tipo/nome del servizio
						String newNomeConnettore = "CNT_SF_"+tipoSoggetto+"/"+nomeSoggetto+val[3]+val[4];
						this.log.debug("nuovo nome connettore ["+newNomeConnettore+"]");
						connettore.setNome(newNomeConnettore);
						//aggiorno il connettore
						DriverRegistroServiziDB_LIB.CRUDConnettore(CostantiDB.UPDATE, connettore, con);
					}
				}
				rs.close();
				stm.close();
			}

			// UPDATE soggetto
			DriverRegistroServiziDB_LIB.CRUDSoggetto(2, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateSoggetto] Errore durante l'aggiornamento del soggetto : " + qe.getMessage(),qe);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {

			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Cancella un Soggetto e il Connettore
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		if (soggetto == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deleteSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 3");
			// DELETE soggetto
			DriverRegistroServiziDB_LIB.CRUDSoggetto(3, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteSoggetto] Errore durante l'eliminazione del soggetto : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public IDSoggetto[] getSoggettiWithSuperuser(String user) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		IDSoggetto [] idSoggetti = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getSoggettiWithSuperuser");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziException::getSoggettiWithSuperuser] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			Vector<IDSoggetto> idTrovati = new Vector<IDSoggetto>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("superuser = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, user);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiRegistroServizi.ABILITATO));
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
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
		return idSoggetti;
	}

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio,false,null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio,false,con);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idServizio, readContenutoAllegati, null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// questo e' il tipo 1 cioe Servizio con parametro IDService
		AccordoServizioParteSpecifica servizio = getAccordoServizioParteSpecifica(1, idServizio,null, null, null,readContenutoAllegati,con);
		return servizio;
	}

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica,false,null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica,false,con);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica, readContenutoAllegati, null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// questo e' il tipo 2 cioe IDAccordo con parametro idAccordoServizioParteSpecifica
		AccordoServizioParteSpecifica servizio = getAccordoServizioParteSpecifica(2, null,idAccordoServizioParteSpecifica, null, null,readContenutoAllegati,con);
		return servizio;
	}

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune,false,null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune,false,con);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto,idAccordoServizioParteComune, readContenutoAllegati, null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		// questo e' il tipo 3 
		AccordoServizioParteSpecifica servizio = getAccordoServizioParteSpecifica(3, null,null, idSoggetto, idAccordoServizioParteComune,readContenutoAllegati,con);
		return servizio;
	}




	/**
	 *  Ritorna gli identificatori dei servizi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return lista di IDServizio
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdServizi ...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdServizi");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);

			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);

			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addSelectField("servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = "+CostantiDB.ACCORDI+".id");

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione < ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio = ?");
				if(filtroRicerca.getTipologiaServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".servizio_correlato = ?");
				if(filtroRicerca.getTipoSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getIdAccordo()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordo();
					if(idAccordo.getNome()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					}
					if(idAccordo.getSoggettoReferente()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente = ?");
					}
					if(idAccordo.getVersione()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					}
				}	
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.log.debug("tipoServizio stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nomeServizio stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getTipologiaServizio()!=null){
					this.log.debug("tipologiaServizio stmt.setString("+filtroRicerca.getTipologiaServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipologiaServizio());
					indexStmt++;
				}
				if(filtroRicerca.getTipoSoggettoErogatore()!=null){
					this.log.debug("tipoSoggettoErogatore stmt.setString("+filtroRicerca.getTipoSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoErogatore()!=null){
					this.log.debug("nomeSoggettoErogatore stmt.setString("+filtroRicerca.getNomeSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getIdAccordo()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordo();
					if(idAccordo.getNome()!=null){
						this.log.debug("nomeAccordo stmt.setString("+idAccordo.getNome()+")");
						stm.setString(indexStmt, idAccordo.getNome());
						indexStmt++;
					}
					if(idAccordo.getSoggettoReferente()!=null){
						long idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, this.tipoDB,this.tabellaSoggetti);
						if(idSoggettoReferente<=0){
							throw new Exception("Soggetto referente ["+idAccordo.getSoggettoReferente().toString()+"] non trovato");
						}
						this.log.debug("idReferenteAccordi stmt.setLong("+idSoggettoReferente+")");
						stm.setLong(indexStmt, idSoggettoReferente);
						indexStmt++;
					}
					if(idAccordo.getVersione()!=null){
						this.log.debug("versioneAccordo stmt.setString("+idAccordo.getVersione()+")");
						stm.setString(indexStmt, idAccordo.getVersione());
						indexStmt++;
					}
				}	
			}
			rs = stm.executeQuery();
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
			while (rs.next()) {
				IDServizio idServ = new IDServizio(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"),
						rs.getString("tipo_servizio"),rs.getString("nome_servizio"));

				// uriAccordoServizio
				IDSoggetto soggettoReferente = null;
				long idSoggettoReferente = rs.getLong("id_referente");
				if(idSoggettoReferente>0){
					Soggetto s = this.getSoggetto(idSoggettoReferente,con);
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),soggettoReferente,rs.getString("versione"));
				idServ.setUriAccordo(this.idAccordoFactory.getUriFromIDAccordo(idAccordo));

				String servizioCorrelato = rs.getString("servizio_correlato");
				if(CostantiRegistroServizi.ABILITATO.toString().equals(servizioCorrelato) || TipologiaServizio.CORRELATO.toString().equals(servizioCorrelato))
					idServ.setTipologiaServizio(TipologiaServizio.CORRELATO.toString());
				else
					idServ.setTipologiaServizio(TipologiaServizio.NORMALE.toString());
				idServizi.add(idServ);
			}
			if(idServizi.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Servizi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Servizi non trovati");
			}else{
				return idServizi;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdServizi error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(
			FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdServizi ...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdAccordiServizioParteSpecifica");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);

			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);

			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("aps_nome");
			sqlQueryObject.addSelectField("aps_versione");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = "+CostantiDB.ACCORDI+".id");

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".ora_registrazione < ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio = ?");
				if(filtroRicerca.getTipologiaServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".servizio_correlato = ?");
				if(filtroRicerca.getTipoSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getIdAccordo()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordo();
					if(idAccordo.getNome()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					}
					if(idAccordo.getSoggettoReferente()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente = ?");
					}
					if(idAccordo.getVersione()!=null){
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					}
				}	
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.log.debug("tipoServizio stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nomeServizio stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getTipologiaServizio()!=null){
					this.log.debug("tipologiaServizio stmt.setString("+filtroRicerca.getTipologiaServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipologiaServizio());
					indexStmt++;
				}
				if(filtroRicerca.getTipoSoggettoErogatore()!=null){
					this.log.debug("tipoSoggettoErogatore stmt.setString("+filtroRicerca.getTipoSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoErogatore()!=null){
					this.log.debug("nomeSoggettoErogatore stmt.setString("+filtroRicerca.getNomeSoggettoErogatore()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoErogatore());
					indexStmt++;
				}
				if(filtroRicerca.getIdAccordo()!=null){
					IDAccordo idAccordo = filtroRicerca.getIdAccordo();
					if(idAccordo.getNome()!=null){
						this.log.debug("nomeAccordo stmt.setString("+idAccordo.getNome()+")");
						stm.setString(indexStmt, idAccordo.getNome());
						indexStmt++;
					}
					if(idAccordo.getSoggettoReferente()!=null){
						long idSoggettoReferente = DBUtils.getIdSoggetto(idAccordo.getSoggettoReferente().getNome(), idAccordo.getSoggettoReferente().getTipo(), con, this.tipoDB,this.tabellaSoggetti);
						if(idSoggettoReferente<=0){
							throw new Exception("Soggetto referente ["+idAccordo.getSoggettoReferente().toString()+"] non trovato");
						}
						this.log.debug("idReferenteAccordi stmt.setLong("+idSoggettoReferente+")");
						stm.setLong(indexStmt, idSoggettoReferente);
						indexStmt++;
					}
					if(idAccordo.getVersione()!=null){
						this.log.debug("versioneAccordo stmt.setString("+idAccordo.getVersione()+")");
						stm.setString(indexStmt, idAccordo.getVersione());
						indexStmt++;
					}
				}	
			}
			rs = stm.executeQuery();
			Vector<IDAccordo> idAccordi = new Vector<IDAccordo>();
			while (rs.next()) {
				long idReferente = rs.getLong("id_soggetto");
				IDSoggetto idSoggettoReferente = null;
				if(idReferente>0){
					Soggetto soggettoReferente = this.getSoggetto(idReferente,con);
					if(soggettoReferente==null){
						try{
							if(rs!=null){
								rs.close();
								rs = null;
							}
						}catch (Exception e) {}
						try{
							if(stm!=null){
								stm.close();
								stm=null;
							}
						}catch (Exception e) {}
						throw new Exception("Soggetto referente ["+idReferente+"] presente nell'accordo ["+rs.getString("aps_nome")+"] (versione ["+rs.getString("aps_versione")+"]) non presente?");
					}
					idSoggettoReferente = new IDSoggetto(soggettoReferente.getTipo(),soggettoReferente.getNome());
				}

				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("aps_nome"),idSoggettoReferente,rs.getString("aps_versione"));
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
			throw new DriverRegistroServiziException("getAllIdServizi error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null){
					rs.close();
					rs = null;
				}
			}catch (Exception e) {}
			try{
				if(stm!=null){
					stm.close();
					stm=null;
				}
			}catch (Exception e) {}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}

	}


	/**
	 *  Ritorna gli identificatori dei servizi del soggetto dato
	 * 
	 * @param idSoggetto
	 * @return identificatori dei servizi del soggetto dato
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public IDServizio[] getAllIdServiziWithSoggettoErogatore(Long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		String operazione = "getAllIdServiziWithSoggettoErogatore";

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug(operazione+"...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdServiziWithSoggettoErogatore");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);

			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = ?");

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
			while (rs.next()) {
				IDServizio idServ = new IDServizio(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"),
						rs.getString("tipo_servizio"),rs.getString("nome_servizio"));
				idServizi.add(idServ);
			}
			if(idServizi.size()==0){
				throw new DriverRegistroServiziNotFound("Servizi non trovati per il soggetto con id: "+idSoggetto);
			}else{
				IDServizio[] res = new IDServizio[1];
				return idServizi.toArray(res);
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException(operazione+" error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}


	@Override
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		if (accordoServizioParteSpecifica == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("createAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizio tupe=1");
			// CREATE
			DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecifica(1, accordoServizioParteSpecifica, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createAccordoServizioParteSpecifica] Errore durante la creazione del servizio : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Verifica l'esistenza di un servizio registrato.
	 * 
	 * @param idServizio
	 *                Identificativo del servizio
	 * @return true se il servizio esiste, false altrimenti
	 */
	@Override
	public boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException {

		if (idServizio == null)
			throw new DriverRegistroServiziException("IDServizio non valido.");

		IDSoggetto erogatore = idServizio.getSoggettoErogatore();
		if (erogatore == null)
			throw new DriverRegistroServiziException("Soggetto Erogatore non valido.");

		String nomeServizio = idServizio.getServizio();
		String tipoServizio = idServizio.getTipoServizio();
		String nomeProprietario = erogatore.getNome();
		String tipoProprietario = erogatore.getTipo();

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			long idS = DBUtils.getIdServizio(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con, false,this.tipoDB);
			return idS > 0;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existsAccordoServizioParteSpecifica : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Verifica l'esistenza di un servizio con tipo e nome dati
	 * 
	 * @param tipoServizio
	 *                Tipo del servizio
	 * @param nomeServizio
	 *                Nome del servizio
	 * @return true se il servizio esiste, false altrimenti
	 */
	public boolean existsServizioWithTipoAndNome(String tipoServizio, String nomeServizio) throws DriverRegistroServiziException {

		if (tipoServizio == null)
			throw new DriverRegistroServiziException("TipoServizio non valido.");
		if (nomeServizio == null)
			throw new DriverRegistroServiziException("NomeServizio non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsServizioWithTipoAndNome");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			int idServ = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			PreparedStatement stm = con.prepareStatement(sqlQuery);
			stm.setString(1, tipoServizio);
			stm.setString(2, nomeServizio);
			ResultSet rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getInt("id");
			rs.close();
			stm.close();
			return idServ > 0;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existsServizioWithTipoAndNome : " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean existsAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException {
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsAccordoServizioParteSpecifica(longId)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idServizio);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.log.error("Errore durante verifica esistenza servizio :", e);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		return exist;
	}


	/**
	 * Verifica l'esistenza di un accordo registrato.
	 * 
	 * @param idAccordo id 
	 *                dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 */
	@Override
	public boolean existsAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException {

		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsAccordoServizioParteSpecifica(idAccordo)");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {

			// Check soggetto referente se esiste.
			if(this.existsSoggetto(idAccordo.getSoggettoReferente())==false){
				return false;
			}

			long idAccordoLong = DBUtils.getIdAccordoServizioParteSpecifica(idAccordo, connection, this.tipoDB);
			if (idAccordoLong <= 0)
				return false;
			else
				return true;
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public IDAccordo getIDAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		IDAccordo idAccordo = null;
		if (idServizio == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (idServizio.getSoggettoErogatore() == null)
			throw new DriverRegistroServiziException("Parametro non valido (soggetto erogatore)");
		String nome_soggetto = idServizio.getSoggettoErogatore().getNome();
		String tipo_soggetto = idServizio.getSoggettoErogatore().getTipo();
		if (nome_soggetto == null || nome_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Nome soggetto non valido");
		if (tipo_soggetto == null || tipo_soggetto.equals(""))
			throw new DriverRegistroServiziException("Parametro Tipo soggetto non valido");

		String nome_servizio = idServizio.getServizio();
		String tipo_servizio = idServizio.getTipoServizio();
		if (nome_servizio == null || nome_servizio.equals(""))
			throw new DriverRegistroServiziException("Parametro Nome servizio non valido");
		if (tipo_servizio == null || tipo_servizio.equals(""))
			throw new DriverRegistroServiziException("Parametro Tipo servizio non valido");

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getIDAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("aps_nome");
			sqlQueryObject.addSelectField("aps_versione");
			sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, tipo_soggetto);
			stm.setString(2, nome_soggetto);
			stm.setString(3, tipo_servizio);
			stm.setString(4, nome_servizio);
			rs = stm.executeQuery();
			if (rs.next()){
				idAccordo = 
						this.idAccordoFactory.getIDAccordoFromValues(rs.getString("aps_nome"), 
								idServizio.getSoggettoErogatore(), 
								rs.getString("aps_versione"));
			}
			rs.close();
			stm.close();

		} catch (Exception e) {
			this.log.error("Errore durante verifica esistenza soggetto :", e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					con.close();
				} catch (Exception e) {
				}
			}
		}

		if(idAccordo==null){
			throw new DriverRegistroServiziNotFound("Servizio ["+idServizio.toString()+"] non trovato");
		}
		else{
			return idAccordo;
		}
	}


	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto
	 * accordo e servizio correlato
	 * 
	 * @param idSoggetto
	 *                del soggetto
	 * @param idAccordo
	 *                dell'accordo
	 * @param idServizio
	 *                del servizio
	 * @param servizioCorrelato
	 *                abilitato o disabilitato
	 * 
	 * @return l'id del servizio trovato
	 */
	public int getServizioWithSoggettoAccordoServCorr(long idSoggetto, long idAccordo, IDServizio idServizio, String servizioCorrelato) throws DriverRegistroServiziException {
		return _getServizioWithSoggettoAccordoServCorrPT(idSoggetto, idAccordo, idServizio, servizioCorrelato, null);
	}

	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto e port-type
	 * accordo e servizio correlato
	 * 
	 * @param idSoggetto
	 *                del soggetto
	 * @param idAccordo
	 *                dell'accordo
	 * @param idServizio
	 *                del servizio
	 * @param servizioCorrelato
	 *                abilitato o disabilitato
	 * @param portType Il port-type
	 * 
	 * @return l'id del servizio trovato
	 */
	private int _getServizioWithSoggettoAccordoServCorrPT(long idSoggetto, long idAccordo, IDServizio idServizio, String servizioCorrelato,String portType) throws DriverRegistroServiziException {

		int idServ = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("_getServizioWithSoggettoAccordoServCorrPT");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioWithSoggettoAccordoServCorr] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("servizio_correlato = ?");
			sqlQueryObject.addWhereCondition(false, "nome_servizio != ?", "tipo_servizio != ?");

			if(portType!=null) sqlQueryObject.addWhereCondition("port_type = ?");
			else sqlQueryObject.addWhereCondition("port_type is null");

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idSoggetto);
			stm.setLong(2, idAccordo);
			stm.setString(3, servizioCorrelato);
			stm.setString(4, idServizio.getServizio());
			stm.setString(5, idServizio.getTipoServizio());
			if(portType!=null) stm.setString(6, portType);
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getInt("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idServ;
	}

	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto,
	 * accordo e servizio correlato
	 * 
	 * @param idSoggetto
	 *                del soggetto
	 * @param idAccordo
	 *                dell'accordo
	 * @param idServizio
	 *                del servizio
	 * @param servizioCorrelato
	 *                abilitato o disabilitato
	 * @param portType Il port-type
	 * 
	 * @return l'id del servizio trovato
	 */
	public int getServizioWithSoggettoAccordoServCorrPt(long idSoggetto, long idAccordo, IDServizio idServizio, String servizioCorrelato,String portType) throws DriverRegistroServiziException {
		return _getServizioWithSoggettoAccordoServCorrPT(idSoggetto, idAccordo, idServizio, servizioCorrelato, portType);
	}

	/**
	 * Verifica l'esistenza di un fruitore con un certo soggetto,
	 * ed un certo servizio
	 * 
	 * @param idServizio
	 *                del servizio
	 * @param idSogg
	 *                del soggetto
	 * @return l'id del fruitore trovato
	 */
	public int getServizioFruitore(IDServizio idServizio, long idSogg) throws DriverRegistroServiziException {

		int idFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("getServizioFruitore");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioWithSoggettoAccordoServCorr] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			int idServ = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setString(1, idServizio.getServizio());
			stm.setString(2, idServizio.getTipoServizio());
			stm.setString(3, idServizio.getSoggettoErogatore().getNome());
			stm.setString(4, idServizio.getSoggettoErogatore().getTipo());
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getInt("id");
			rs.close();
			stm.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idServ);
			stm.setLong(2, idSogg);
			rs = stm.executeQuery();
			if (rs.next())
				idFru = rs.getInt("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idFru;
	}

	/**
	 * Ritorna l'id del soggetto fruitore di un servizio fruitore
	 * 
	 * @param idServizioFruitore
	 *                del servizio fruitore
	 * @return l'id del soggetto fruitore
	 */
	public int getServizioFruitoreSoggettoFruitoreID(int idServizioFruitore) throws DriverRegistroServiziException {

		int idSoggFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("getServizioFruitoreSoggettoFruitoreID");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioFruitoreSoggettoFruitoreID] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setInt(1, idServizioFruitore);
			rs = stm.executeQuery();
			if (rs.next())
				idSoggFru = rs.getInt("id_soggetto");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idSoggFru;
	}

	/**
	 * Ritorna l'id del servizio di un servizio fruitore
	 * 
	 * @param idServizioFruitore
	 *                del servizio fruitore
	 * @return l'id del servizio
	 */
	public int getServizioFruitoreServizioID(int idServizioFruitore) throws DriverRegistroServiziException {

		int idServ = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("getServizioFruitoreServizioID");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizioFruitoreServizioID] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("id_servizio");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setInt(1, idServizioFruitore);
			rs = stm.executeQuery();
			if (rs.next())
				idServ = rs.getInt("id_servizio");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idServ;
	}

	@Override
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		if (servizio == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Parametro non valido.");
		PreparedStatement stm=null;
		ResultSet rs=null;
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updateAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			// UPDATE
			DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecifica(2, servizio, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateAccordoServizioParteSpecifica] Errore durante l'update del servizio : " + qe.getMessage(),qe);
		} finally {

			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	@Override
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		if (servizio == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deleteAccordoServizioParteSpecifica");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizio type = 3");
			// creo soggetto
			DriverRegistroServiziDB_LIB.CRUDAccordoServizioParteSpecifica(3, servizio, con, this.tipoDB);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteAccordoServizioParteSpecifica] Errore durante la delete del servizio : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Dato un soggetto, verifica che i suoi servizi non abbiano
	 * il connettore disabilitato
	 * 
	 * @param idSoggetto
	 *                Identificativo del soggetto
	 * @return true se esistono servizi senza connettore, false altrimenti
	 */
	public boolean existsSoggettoServiziWithoutConnettore(int idSoggetto) throws DriverRegistroServiziException {
		if (idSoggetto == 0)
			throw new DriverRegistroServiziException("idSoggetto non valido.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		boolean trovatoServ = false;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("existsSoggettoServiziWithoutConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_connettore = " + CostantiDB.CONNETTORI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("endpointtype = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setInt(1, idSoggetto);
			stm.setString(2, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.DISABILITATO));
			rs = stm.executeQuery();
			if (rs.next())
				trovatoServ = true;
			rs.close();
			stm.close();

			return trovatoServ;
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("Errore durante existsSoggettoServiziWithoutConnettore: " + qe.getMessage(), qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}





	/*
	 * Metodi di utilita'
	 */

	/**
	 * Accede alla tabella connettori, recupera il connettore con idConnettore
	 * in caso di errore lancia un'eccezione.
	 * 
	 * @param idConnettore -
	 *                id da prelevare
	 * @param connection -
	 *                una connesione con il db
	 * @return Connettore
	 * 
	 */
	public Connettore getConnettore(long idConnettore, Connection connection) throws DriverRegistroServiziException {

		Connettore connettore = null;

		// accedo alla tab regserv_connettori

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idConnettore));

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
					//l'id del connettore e' quello passato come parametro
					connettore.setId(idConnettore);

					// Debug
					if(rs.getInt("debug")==1){
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_DEBUG);
						prop.setValore("true");
						connettore.addProperty(prop);
					}

					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {

						// url
						String value = rs.getString("url");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverRegistroServiziException("Connettore di tipo http possiede una url non definita");
						}
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
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

					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){

						// nome coda/topic
						String value = rs.getString("nome");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverRegistroServiziException("Connettore di tipo jms possiede il nome della coda/topic non definito");
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
							throw new DriverRegistroServiziException("Connettore di tipo jms possiede il tipo della coda non definito");
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
							throw new DriverRegistroServiziException("Connettore di tipo jms non possiede la definizione di una Connection Factory");
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
							throw new DriverRegistroServiziException("Connettore di tipo jms possiede il tipo dell'oggetto JMS non definito");
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
							Property[] ps = ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,this.tipoDB);
							List<Property> listCP = new ArrayList<Property>();
							if(ps!=null){
								for (int i = 0; i < ps.length; i++) {
									listCP.add(ps[i]);
								}
							}
							connettore.setPropertyList(listCP);
						}
					}

				}
			}
			
			// Extended Info
			this.readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection);
			
			return connettore;
		} catch (SQLException sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] SQLException : " + sqle.getMessage(),sqle);
		} catch (CoreException e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] CoreException : " + e.getMessage(),e);
		}catch (Exception sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] Exception : " + sqle.getMessage(),sqle);
		}finally {
			// chiudo lo statement e resultset
			try {
				rs.close();
				stm.close();
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	protected void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection) throws DriverRegistroServiziException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverRegistroServiziDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

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

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	protected void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection) throws DriverRegistroServiziException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition("name", CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			DriverRegistroServiziDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

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

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}


	/**
	 * Accede alla tabella connettori_properties, recupera le properties del connettore con nomeConnettore
	 * in caso di errore lancia un'eccezione.
	 * 
	 * @param nomeConnettore -
	 *                nome da prelevare
	 * @return Property[]
	 * 
	 */
	public Property[] getPropertiesConnettore(String nomeConnettore) throws DriverRegistroServiziException {
		Connection con = null;
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getPropertiesConnettore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPropertiesConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		return getPropertiesConnettore(nomeConnettore,con);
	}
	public Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverRegistroServiziException {
		try {
			return ConnettorePropertiesUtilities.getPropertiesConnettore(nomeConnettore, connection,this.tipoDB);
		} catch (CoreException e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPropertiesConnettore] CoreException : " + e.getMessage(),e);
		}
	}

	/**
	 * Recupera un Servizio in base al tipo.
	 * 
	 * @param type
	 *                indica il tipo di operazione: 
	 *                - 1 indica getAccordoServizioParteSpecifica(IDServizio idServizio) 
	 *                - 2 indica getAccordoServizioParteSpecifica(IDAccordo idAccordoServizioParteSpecifica) 
	 *                - 3 indica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto soggetto, IDAccordo idAccordoServizioParteComune)
	 * 
	 * 
	 * @param idServizio
	 *                Utilizzato in caso di type 1 o null altrimenti
	 * @param idAccordoServizioParteSpecifica
	 * 				  Utilizzato in caso di type 2 o null altrimenti
	 * @param idSoggetto
	 *                Utilizzato in caso di type 3 null altrimenti
	 * @param idAccordoServizioParteComune
	 *                Utilizzato in caso di type 3 null altrimenti
	 * 
	 * @return Servizio
	 * @throws DriverRegistroServiziException
	 */
	private AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(int type, IDServizio idServizio, 
			IDAccordo idAccordoServizioParteSpecifica, 
			IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune,
			boolean readContenutoAllegati,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		AccordoServizioParteSpecifica accordoServizioParteSpecifica = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;

		this.log.debug("chiamato getAccordoServizioParteSpecifica type = " + type);

		switch (type) {
		case 1:
			// in questi casi idService non deve essere null
			// faccio i controlli vari
			if (idServizio == null || idServizio.getServizio() == null || idServizio.getServizio().trim().equals("") || idServizio.getTipoServizio() == null || idServizio.getTipoServizio().trim().equals("") || idServizio.getSoggettoErogatore() == null || idServizio.getSoggettoErogatore().getNome() == null || idServizio.getSoggettoErogatore().getNome().trim().equals("") || idServizio.getSoggettoErogatore().getTipo() == null || idServizio.getSoggettoErogatore().getTipo().trim().equals(""))
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] : errore nei parametri d'ingresso, type :" + type);

			break;

		case 2:
			if(idAccordoServizioParteSpecifica==null || idAccordoServizioParteSpecifica.getNome()==null || idAccordoServizioParteSpecifica.getSoggettoReferente()==null || idAccordoServizioParteSpecifica.getSoggettoReferente().getTipo()==null || idAccordoServizioParteSpecifica.getSoggettoReferente().getNome()==null){
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] : errore nei parametri d'ingresso, type :" + type);

			}
			break;

		case 3:
			// in questo caso idSoggetto non deve essere null e anche
			// nomeAccordo
			if (idAccordoServizioParteComune == null || idAccordoServizioParteComune.getNome()==null || idAccordoServizioParteComune.getNome().trim().equals("") || idSoggetto == null || idSoggetto.getNome() == null || idSoggetto.getNome().trim().equals("") || idSoggetto.getTipo() == null || idSoggetto.getTipo().trim().equals(""))
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] : errore nei parametri d'ingresso, type :" + type);
			break;

		default:
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] tipo operazione incorretto. parametro type passato: " + type);

		}

		if(conParam!=null){
			con = conParam;
		}
		else if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getAccordoServizioParteSpecifica");
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nomeservizio = ?");
			sqlQueryObject.addWhereCondition("tiposervizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			accordoServizioParteSpecifica = new AccordoServizioParteSpecifica();
			Servizio servizio = new Servizio();
			accordoServizioParteSpecifica.setServizio(servizio);

			String nomeSoggEr = null;
			String tipoSoggEr = null;
			long longIdAccordoServizioParteComune = 0;
			String nomeServizio = null;
			String tipoServizio = null;
			String superUser = null;
			long idSoggErogatore = 0;
			long longIdAccordoServizioParteSpecifica = 0;


			// se tipo 1 utilizzo idServio per recuperare qualche parametro
			switch (type) {
			case 1:
				nomeServizio = idServizio.getServizio();
				tipoServizio = idServizio.getTipoServizio();

				// nome e tipo soggetto erogatore
				nomeSoggEr = idServizio.getSoggettoErogatore().getNome();
				tipoSoggEr = idServizio.getSoggettoErogatore().getTipo();

				// Prendo l'id del soggetto

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nomeSoggEr);
				stm.setString(2, tipoSoggEr);

				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSoggEr, tipoSoggEr));

				rs = stm.executeQuery();
				if (rs.next()) {
					idSoggErogatore = rs.getLong("id");
				}
				rs.close();
				stm.close();
				break;

			case 2:

				// Check soggetto referente se esiste.
				if(this.existsSoggetto(idAccordoServizioParteSpecifica.getSoggettoReferente())==false){
					throw new DriverRegistroServiziNotFound("Soggetto erogatore ["+idAccordoServizioParteSpecifica.getSoggettoReferente()+"] non esistente");
				}
				Soggetto sogErogatore = this.getSoggetto(idAccordoServizioParteSpecifica.getSoggettoReferente());
				nomeSoggEr = sogErogatore.getNome();
				tipoSoggEr = sogErogatore.getTipo();

				longIdAccordoServizioParteSpecifica = DBUtils.getIdAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica, con, this.tipoDB);
				if (longIdAccordoServizioParteSpecifica <= 0)
					throw new DriverRegistroServiziNotFound("Accordo di Servizio Parte Specifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
				break;

			case 3:
				// In questo caso non ho IDService ma IDSoggetto
				// quindi prendo il nome del soggetto erogatore e il tipo da
				// idSoggetto
				nomeSoggEr = idSoggetto.getNome();
				tipoSoggEr = idSoggetto.getTipo();

				// per settare il nome del servizio devo accedere al db
				// tramite id del soggetto erogatore e id dell-accordo (che lo
				// recupero tramite il nomeAccordo passato come parametro)
				longIdAccordoServizioParteComune = DBUtils.getIdAccordoServizioParteComune(idAccordoServizioParteComune, con, this.tipoDB);

				// Prendo l'id del soggetto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto = ?");
				sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nomeSoggEr);
				stm.setString(2, tipoSoggEr);

				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeSoggEr, tipoSoggEr));
				rs = stm.executeQuery();
				if (rs.next()) {
					idSoggErogatore = rs.getLong("id");
				}
				rs.close();
				stm.close();

				// ora che ho l'id recupero nome-servizio e tipo-servizio dalla
				// tabella
				// regserv_servizi acceduta tramite id-soggetto e id-accordo
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereCondition("servizio_correlato = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idSoggErogatore);
				stm.setLong(2, longIdAccordoServizioParteComune);
				stm.setString(3, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.ABILITATO));
				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggErogatore, longIdAccordoServizioParteComune, CostantiRegistroServizi.ABILITATO));
				rs = stm.executeQuery();
				if (rs.next()) {
					nomeServizio = rs.getString("nome_servizio");
					tipoServizio = rs.getString("tipo_servizio");
					superUser = rs.getString("superuser");

				}
				rs.close();
				stm.close();
				break;
			}

			// Prendo l'id del servizio
			long idConnettore = 0;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			switch (type) {
			case 1:
				sqlQueryObject.addWhereCondition("nome_servizio = ?");
				sqlQueryObject.addWhereCondition("tipo_servizio = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				break;
			case 2:
				sqlQueryObject.addWhereCondition("id = ?");
				break;
			case 3:
				sqlQueryObject.addWhereCondition("nome_servizio = ?");
				sqlQueryObject.addWhereCondition("tipo_servizio = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				break;
			}
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			switch (type) {
			case 1:
				stm.setString(1, nomeServizio);
				stm.setString(2, tipoServizio);
				stm.setLong(3, idSoggErogatore);
				break;
			case 2:
				stm.setLong(1, longIdAccordoServizioParteSpecifica);
				break;
			case 3:
				stm.setString(1, nomeServizio);
				stm.setString(2, tipoServizio);
				stm.setLong(3, idSoggErogatore);
				break;
			}

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, nomeServizio, tipoServizio, idSoggErogatore));
			rs = stm.executeQuery();
			if (rs.next()) {
				longIdAccordoServizioParteSpecifica = rs.getLong("id");
				idConnettore = rs.getLong("id_connettore");
				longIdAccordoServizioParteComune = rs.getLong("id_accordo");
				superUser = rs.getString("superuser");
				accordoServizioParteSpecifica.setId(longIdAccordoServizioParteSpecifica);
				servizio.setId(longIdAccordoServizioParteSpecifica); // imposto anche qua per comodita
				accordoServizioParteSpecifica.setIdAccordo(longIdAccordoServizioParteComune);

				// setNome servizio
				servizio.setNome(rs.getString("nome_servizio"));
				// setTipo servizio
				servizio.setTipo(rs.getString("tipo_servizio"));

				//setto connettore
				servizio.setConnettore(getConnettore(idConnettore, con));
				// setWsdlImplementativoErogatore
				String wsdlimpler = rs.getString("wsdl_implementativo_erogatore");
				accordoServizioParteSpecifica.setByteWsdlImplementativoErogatore((wsdlimpler != null && !wsdlimpler.trim().equals("")) ? wsdlimpler.trim().getBytes() : null);
				// setWddlImplementativoFruitore
				String wsdlimplfru = rs.getString("wsdl_implementativo_fruitore");
				accordoServizioParteSpecifica.setByteWsdlImplementativoFruitore((wsdlimplfru != null && !wsdlimplfru.trim().equals("")) ? wsdlimplfru.trim().getBytes() : null);

				// Setto informazione sul servizio correlato
				String servizioCorrelato = rs.getString("servizio_correlato");
				if(CostantiRegistroServizi.ABILITATO.toString().equals(servizioCorrelato) || TipologiaServizio.CORRELATO.toString().equals(servizioCorrelato))
					servizio.setTipologiaServizio(TipologiaServizio.CORRELATO);
				else
					servizio.setTipologiaServizio(TipologiaServizio.NORMALE);

				//setto erogatore servizio
				servizio.setTipoSoggettoErogatore(tipoSoggEr);
				servizio.setNomeSoggettoErogatore(nomeSoggEr);
				accordoServizioParteSpecifica.setIdSoggetto(idSoggErogatore);

				if(rs.getInt("privato")==1)
					accordoServizioParteSpecifica.setPrivato(true);
				else
					accordoServizioParteSpecifica.setPrivato(false);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					accordoServizioParteSpecifica.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// porttype
				String tmp = rs.getString("port_type");
				if(tmp!=null && ("".equals(tmp)==false))
					accordoServizioParteSpecifica.setPortType(tmp);

				// Profilo 
				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					accordoServizioParteSpecifica.setVersioneProtocollo(profilo);
				}

				accordoServizioParteSpecifica.setSuperUser(superUser);

				// Descrizione
				accordoServizioParteSpecifica.setDescrizione(rs.getString("descrizione"));

				// Stato Documento
				accordoServizioParteSpecifica.setStatoPackage(rs.getString("stato"));

				// APS
				accordoServizioParteSpecifica.setNome(rs.getString("aps_nome"));
				accordoServizioParteSpecifica.setVersione(rs.getString("aps_versione"));

			}else{
				switch (type) {
				case 1:
					throw new DriverRegistroServiziNotFound("Servizio ["+tipoServizio+"/"+nomeServizio+"] erogato dal soggetto ["+tipoSoggEr+"/"+nomeSoggEr+"] non esiste");
				case 2:
					throw new DriverRegistroServiziNotFound("Accordo di Servizio Parte Specifica ["+idAccordoServizioParteSpecifica.toString()+"] non esiste");
				case 3:
					throw new DriverRegistroServiziNotFound("Servizio ["+tipoServizio+"/"+nomeServizio+"] erogato dal soggetto ["+tipoSoggEr+"/"+nomeSoggEr+"] non esiste");
				}

			}
			rs.close();
			stm.close();
			long idSoggFruitore = 0;
			Fruitore fruitore = null;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteSpecifica);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteSpecifica));
			rs = stm.executeQuery();

			while (rs.next()) {
				fruitore = new Fruitore();

				idSoggFruitore = rs.getLong("id_soggetto"); // recupero id del
				// soggetto fruitore
				// del servizio
				idConnettore = rs.getLong("id_connettore"); // recuper id del
				// connettore

				fruitore.setConnettore(getConnettore(idConnettore, con));

				String wsdlimpler = rs.getString("wsdl_implementativo_erogatore");
				fruitore.setByteWsdlImplementativoErogatore(wsdlimpler!=null && !wsdlimpler.trim().equals("") ? wsdlimpler.getBytes() : null );
				String wsdlimplfru = rs.getString("wsdl_implementativo_fruitore");
				fruitore.setByteWsdlImplementativoFruitore(wsdlimplfru!=null && !wsdlimplfru.trim().equals("") ? wsdlimplfru.getBytes() : null);

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					fruitore.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Profilo 
				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					fruitore.setVersioneProtocollo(profilo);
				}

				// Stato Documento
				fruitore.setStatoPackage(rs.getString("stato"));

				// Client Auth
				fruitore.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(rs.getString("client_auth")));

				// recupero informazioni del soggetto fruitore
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idSoggFruitore);
				this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggFruitore));
				rs1 = stm1.executeQuery();

				if (rs1.next()) {
					fruitore.setNome(rs1.getString("nome_soggetto"));
					fruitore.setTipo(rs1.getString("tipo_soggetto"));
				} else {
					throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Non ho trovato i dati del soggetto fruitore necessario eseguendo: \n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggFruitore));
				}
				rs1.close();
				stm1.close();

				// aggiungo il fruitore al servizio da restituire
				fruitore.setId(rs.getLong("id"));
				accordoServizioParteSpecifica.addFruitore(fruitore);

			}
			rs.close();
			stm.close();


			ServizioAzione azione = null;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_AZIONI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteSpecifica);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteSpecifica));
			rs = stm.executeQuery();

			while (rs.next()) {
				azione = new ServizioAzione();

				azione.setNome(rs.getString("nome_azione"));

				idConnettore = rs.getLong("id_connettore"); // recuper id del
				// connettore
				azione.setConnettore(getConnettore(idConnettore, con));

				// aggiungo il fruitore al servizio da restituire
				azione.setId(rs.getLong("id"));
				servizio.addParametriAzione(azione);

			}
			rs.close();
			stm.close();


			// imposto uri accordo di servizio parte comune
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, longIdAccordoServizioParteComune);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, longIdAccordoServizioParteComune));
			rs = stm.executeQuery();

			if (rs.next()) {
				// setAccordoServizio
				accordoServizioParteSpecifica.setIdAccordo(longIdAccordoServizioParteComune);

				String tmp = rs.getString("nome");
				String tmpVersione = rs.getString("versione");
				long id_referente = rs.getLong("id_referente");
				IDSoggetto soggettoReferente = null;
				if(id_referente>0){
					Soggetto s = this.getSoggetto(id_referente,con);
					if(s==null){
						throw new Exception ("Soggetto referente ["+id_referente+"] dell'accordo non esiste");
					}
					soggettoReferente = new IDSoggetto(s.getTipo(),s.getNome());
				}
				accordoServizioParteSpecifica.setAccordoServizioParteComune(this.idAccordoFactory.getUriFromValues(tmp, soggettoReferente, tmpVersione));


				/** Non servono poiche' non presenti nel db dei servizi */
				/*
				// setFiltroduplicati
				tmp = rs.getString("filtro_duplicati");
				servizio.setFiltroDuplicati((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setConfermaRicezione
				tmp = rs.getString("conferma_ricezione");
				servizio.setConfermaRicezione((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setIdCollaborazione
				tmp = rs.getString("identificativo_collaborazione");
				servizio.setIdCollaborazione((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setConsegnaInOrdine
				tmp = rs.getString("consegna_in_ordine");
				servizio.setConsegnaInOrdine((tmp != null && !(tmp.trim().equals(""))) ? tmp : CostantiRegistroServizi.DISABILITATO);
				// setScadenza
				tmp = rs.getString("scadenza");
				servizio.setScadenza((tmp != null && !(tmp.trim().equals(""))) ? tmp : null);
				 */
			}
			rs.close();
			stm.close();




			// read Documenti generici: i bytes non vengono ritornati se readContenutoAllegati==false, utilizzare il metodo apposta per averli: 
			//                                               DriverRegistroServiziDB_LIB.getDocumento(id, readBytes, connection);
			try{
				List<?> allegati = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.allegato.toString(), longIdAccordoServizioParteSpecifica, 
						ProprietariDocumento.servizio,readContenutoAllegati, con, this.tipoDB);
				for(int i=0; i<allegati.size();i++){
					accordoServizioParteSpecifica.addAllegato((Documento) allegati.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheSemiformali = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaSemiformale.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.tipoDB);
				for(int i=0; i<specificheSemiformali.size();i++){
					accordoServizioParteSpecifica.addSpecificaSemiformale((Documento) specificheSemiformali.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheLivelloServizio = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaLivelloServizio.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.tipoDB);
				for(int i=0; i<specificheLivelloServizio.size();i++){
					accordoServizioParteSpecifica.addSpecificaLivelloServizio((Documento) specificheLivelloServizio.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}
			try{
				List<?> specificheSicurezza = DriverRegistroServiziDB_LIB.getListaDocumenti(RuoliDocumento.specificaSicurezza.toString(), 
						longIdAccordoServizioParteSpecifica, ProprietariDocumento.servizio,readContenutoAllegati, con, this.tipoDB);
				for(int i=0; i<specificheSicurezza.size();i++){
					accordoServizioParteSpecifica.addSpecificaSicurezza((Documento) specificheSicurezza.get(i));
				}
			}catch(DriverRegistroServiziNotFound dNotFound){}

			return accordoServizioParteSpecifica;

		} catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			this.log.debug("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception:"+se.getMessage());
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizioParteSpecifica] Exception:" + se.getMessage(),se);

		} finally {
			try{
				if(rs!=null) rs.close();
				if(rs1!=null) rs1.close();
				if(stm!=null) stm.close();
				if(stm1!=null) stm1.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio le connessioni al db...");
					con.close();
				}				
			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	public List<Azione> accordiAzioniList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException{
		String nomeMetodo = "accordiAzioniList";
		int idLista = Liste.ACCORDI_AZIONI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Azione> lista = new ArrayList<Azione>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiAzioniList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			// ricavo il numero di entries totale
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectAliasField("id","idAzione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
				sqlQueryObject.addSelectAliasField("id","idAzione");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("profilo_azione");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();

			Azione az;
			while (risultato.next()) {

				az = new Azione();

				//az.setId(risultato.getLong("id_accordo"));  nn c'e' un id per questo oggetto

				//fix by stefano: Aggiunto campo idAzione alla query in modo da leggere l'id dell'oggetto azione
				az.setId(risultato.getLong("idAzione"));
				az.setNome(risultato.getString("nome"));
				az.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("conferma_ricezione")));
				az.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("consegna_in_ordine")));
				az.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("filtro_duplicati")));
				az.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("identificativo_collaborazione")));
				az.setScadenza(risultato.getString("scadenza"));
				az.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(risultato.getString("profilo_collaborazione")));
				az.setProfAzione(risultato.getString("profilo_azione"));
				lista.add(az);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Recupera la lista di azioni dell'Accordo con id idAccordo, con il profilo di collaborazione specificato
	 * Il profilo di collaborazione viene ricercato nell'accordo di servizio se l'azione ha profilo di defaul
	 * altrimenti viene controllato il profilo di collaborazione dell'azione 
	 * @param idAccordo
	 * @param profiloCollaborazione Opzionale
	 * @param ricerca
	 * @return Lista di {@link Azione} dell'accordo idAccordo con profilo di collaborazione 'profiloCollaborazione' se specificato altrimenti tutte.
	 * @throws DriverRegistroServiziException
	 */
	public List<Azione> accordiAzioniList(int idAccordo,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiAzioniList";
		int idLista = Liste.ACCORDI_AZIONI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Azione> lista = new ArrayList<Azione>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiAzioniList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			/**
			 * SELECT a.id as idAccordo,aa.id,aa.nome 
			 * from accordi as a, accordi_azioni as aa 
			 * where a.id=aa.id_accordo 
			 * and( (aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico') 
			 * and aa.id_accordo=12;
			 */
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_AZIONI+".id", "cont");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = ?");
			//(aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico')
			sqlQueryObject.addWhereCondition(false, 
					CostantiDB.ACCORDI_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.ACCORDI_AZIONI+".profilo_azione= ?",
					CostantiDB.ACCORDI+".profilo_collaborazione=?");
			sqlQueryObject.setANDLogicOperator(true);

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			stmt.setString(2, profiloCollaborazione);
			stmt.setString(3, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(4, profiloCollaborazione);

			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);

			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"nome");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"conferma_ricezione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"consegna_in_ordine");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"filtro_duplicati");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"identificativo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"scadenza");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"profilo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"profilo_azione");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"id_accordo");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_AZIONI,"correlata");
			//where
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".id_accordo = ?");
			//(aa.profilo_azione='ridefinito' and aa.profilo_collaborazione='asincronoAsimmetrico') or a.profilo_collaborazione='asincronoAsimmetrico')
			sqlQueryObject.addWhereCondition(false, 
					CostantiDB.ACCORDI_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.ACCORDI_AZIONI+".profilo_azione= ?",
					CostantiDB.ACCORDI+".profilo_collaborazione=?");

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			sqlQueryObject.addOrderBy(CostantiDB.ACCORDI_AZIONI+".nome");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();


			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			stmt.setString(2, profiloCollaborazione);
			stmt.setString(3, CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(4, profiloCollaborazione);

			risultato = stmt.executeQuery();

			Azione az;
			while (risultato.next()) {

				az = new Azione();

				//az.setId(risultato.getLong("id_accordo"));  nn c'e' un id per questo oggetto

				az.setNome(risultato.getString("nome"));
				az.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("conferma_ricezione")));
				az.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("consegna_in_ordine")));
				az.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("filtro_duplicati")));
				az.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("identificativo_collaborazione")));
				az.setScadenza(risultato.getString("scadenza"));
				az.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(risultato.getString("profilo_collaborazione")));
				az.setProfAzione(risultato.getString("profilo_azione"));
				az.setCorrelata(risultato.getString("correlata"));
				lista.add(az);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<AccordoServizioParteSpecifica> serviziList(String superuser,ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziList";
		int idLista = Liste.SERVIZI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.log.debug("search : " + search);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<AccordoServizioParteSpecifica> lista = new ArrayList<AccordoServizioParteSpecifica>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");

				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.setANDLogicOperator(true);
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("tipo_servizio");
				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.setANDLogicOperator(true);
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("tipo_servizio");
				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			this.log.debug("query : " + queryString);

			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			AccordoServizioParteSpecifica asps;
			while (risultato.next()) {

				asps = new AccordoServizioParteSpecifica();
				Servizio serv = new Servizio();
				asps.setServizio(serv);

				asps.setId(risultato.getLong("id"));
				serv.setNome(risultato.getString("nome_servizio"));
				serv.setTipo(risultato.getString("tipo_servizio"));
				asps.setIdSoggetto(risultato.getLong("id_soggetto"));
				asps.setIdAccordo(risultato.getLong("id_accordo"));
				String servizio_correlato = risultato.getString("servizio_correlato");
				if ( (servizio_correlato != null) && 
						(servizio_correlato.equalsIgnoreCase(CostantiRegistroServizi.ABILITATO.toString()) || TipologiaServizio.CORRELATO.toString().equals(servizio_correlato)) 
						){
					serv.setTipologiaServizio(TipologiaServizio.CORRELATO);
				}else{
					serv.setTipologiaServizio(TipologiaServizio.NORMALE);
				}

				// informazioni su soggetto
				Soggetto sog = this.getSoggetto(asps.getIdSoggetto(),con);
				String nomeErogatore = sog.getNome();
				String tipoErogatore = sog.getTipo();

				serv.setNomeSoggettoErogatore(nomeErogatore);
				serv.setTipoSoggettoErogatore(tipoErogatore);

				// informazioni su accordo
				AccordoServizioParteComune as = this.getAccordoServizioParteComune(asps.getIdAccordo(),con);
				asps.setAccordoServizioParteComune(this.idAccordoFactory.getUriFromAccordo(as));

				// Stato
				asps.setStatoPackage(risultato.getString("stato"));

				lista.add(asps);
			}

			this.log.debug("size lista :" + ((lista == null) ? null : lista.size()));

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<AccordoServizioParteSpecifica> servizioWithSoggettoFruitore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "servizioWithSoggettoFruitore";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteSpecifica> idServizi = new ArrayList<AccordoServizioParteSpecifica>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("servizioWithSoggettoFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObjectSoggetti.setSelectDistinct(true);
			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.SERVIZI, "id","idServizio");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idSoggetto.getTipo());
			stmt.setString(2,idSoggetto.getNome());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idServizioLong = risultato.getLong("idServizio");
				AccordoServizioParteSpecifica serv = this.getAccordoServizioParteSpecifica(idServizioLong);
				Soggetto s = this.getSoggetto(serv.getIdSoggetto());
				serv.getServizio().setTipoSoggettoErogatore(s.getTipo());
				serv.getServizio().setNomeSoggettoErogatore(s.getNome());

				idServizi.add(serv);

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idServizi;
	}

	public List<AccordoServizioParteComune> accordiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,false,false);
	}
	public List<AccordoServizioParteComune> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,false,true);
	}
	public List<AccordoServizioParteComune> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return accordiListEngine(superuser,ricerca,true,false);
	}
	private List<AccordoServizioParteComune> accordiListEngine(String superuser, ISearch ricerca,boolean excludeASParteComune,boolean excludeASComposti) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiList";
		int idLista = Liste.ACCORDI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.log.debug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> lista = new ArrayList<AccordoServizioParteComune>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiListEngine");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if(excludeASComposti && excludeASParteComune){
				throw new Exception("Non e' possibile invocare il metodo accordiListEngine con entrambi i parametri excludeASParteComune,excludeASComposti impostati al valore true");
			}

			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
				sqlQueryObjectSoggetti.addWhereCondition(false,
						sqlQueryObjectSoggetti.getWhereLikeCondition("tipo_soggetto", search, true, true),
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true));
			}


			ISQLQueryObject sqlQueryObjectExclude = null;
			if(excludeASComposti || excludeASParteComune){
				sqlQueryObjectExclude = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectExclude.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObjectExclude.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id_accordo");
				sqlQueryObjectExclude.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
			}

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				if(excludeASParteComune){
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
				}
				if(excludeASComposti){
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(excludeASParteComune){
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
				}
				if(excludeASComposti){
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "id");
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "nome");
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "versione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
				//sqlQueryObject.addSelectField("id_referente");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione", search, true, true), // la versione e' troppo, tutte hanno 1 ....
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				if(excludeASParteComune){
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
				}
				if(excludeASComposti){
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "id");
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "nome");
				sqlQueryObject.addSelectField(CostantiDB.ACCORDI, "versione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
				//sqlQueryObject.addSelectField("id_referente");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".superuser = ?");
				if(excludeASParteComune){
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectExclude);
				}
				if(excludeASComposti){
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExclude);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			AccordoServizioParteComune accordo = null;

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				accordo = this.getAccordoServizioParteComune(id, con);
				lista.add(accordo);

			}

			this.log.debug("size lista :" + ((lista == null) ? null : lista.size()));

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<AccordoCooperazione> accordiCooperazioneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
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

		this.log.debug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiCooperazioneList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition("versione", search, true, true));
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("versione");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						sqlQueryObject.getWhereLikeCondition("nome", search, true, true),
						sqlQueryObject.getWhereLikeCondition("versione", search, true, true));
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("versione");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				String nomeAcc = risultato.getString("nome"); 
				String versioneAcc = risultato.getString("versione");

				IDAccordoCooperazione id = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(nomeAcc, versioneAcc);

				idAccordi.add(id);

			}

			this.log.debug("size lista :" + ((idAccordi == null) ? null : idAccordi.size()));


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
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

	public List<IDSoggetto> accordiCoopPartecipantiList(long idAccordo,ISearch ricerca) throws DriverRegistroServiziException {
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

		this.log.debug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiCoopPartecipantiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

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
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idSoggetti;
	}

	public List<AccordoCooperazione> accordiCoopWithSoggettoPartecipante(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCoopWithSoggettoPartecipante";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoCooperazione> idAccordoCooperazione = new ArrayList<AccordoCooperazione>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiCoopWithSoggettoPartecipante");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

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
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idAccordoCooperazione;
	}

	public List<AccordoServizioParteComune> accordiServizio_serviziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizio_serviziComponentiConSoggettoErogatore";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiServizio_serviziComponentiConSoggettoErogatore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id="+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idSoggetto.getTipo());
			stmt.setString(2,idSoggetto.getNome());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idAccordoServizio;
	}

	public List<AccordoServizioParteComune> accordiServizio_serviziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizio_serviziComponenti";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiServizio_serviziComponenti");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id="+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idServizio.getSoggettoErogatore().getTipo());
			stmt.setString(2,idServizio.getSoggettoErogatore().getNome());
			stmt.setString(3, idServizio.getTipoServizio());
			stmt.setString(4, idServizio.getServizio());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idAccordoServizio;
	}


	public List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizioWithAccordoCooperazione";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiServizioWithAccordoCooperazione");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);

			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo_cooperazione="+CostantiDB.ACCORDI_COOPERAZIONE+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idAccordoCooperazione.getNome());
			stmt.setString(2,idAccordoCooperazione.getVersione());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		return idAccordoServizio;
	}


	public List<Soggetto> accordiErogatoriList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiErogatoriList";
		int idLista = Liste.ACCORDI_EROGATORI;
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

		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiErogatoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".descrizione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".identificativo_porta");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".codice_ipa");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id", "servid");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".servizio_correlato");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".descrizione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".identificativo_porta");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".codice_ipa");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id", "servid");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".servizio_correlato");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();

			Soggetto sog = null;
			String oldNome = "";
			String oldTipo = "";
			while (risultato.next()) {
				String newNome = risultato.getString("nome_soggetto");
				String newTipo = risultato.getString("tipo_soggetto");

				// se il nome o il tipo sono diversi allora e' un soggetto nuovo
				// altrimenti e' sempre lo stesso soggetto
				// il controllo va fatto prima sul tipo in quanto il risultato
				// e' ordinato come tipo/nome
				if (sog == null || !oldTipo.equals(newTipo) || !oldNome.equals(newNome)) {

					oldTipo=newTipo;
					oldNome=newNome;
					// se sog e' null e' la prima volta che visito il result set
					// e non devo aggiunger nulla alla lista
					// altrimenti ho finito di aggiungere i servizi ad un
					// soggetto e devo metterlo nella lista
					// e iniziare ad aggiungere i servizi al nuovo soggetto
					// trovato
					if (sog != null)
						lista.add(sog);

					// creo il nuovo soggetto
					sog = new Soggetto();

					sog.setId(risultato.getLong("id"));
					sog.setNome(risultato.getString("nome_soggetto"));
					sog.setTipo(risultato.getString("tipo_soggetto"));
					sog.setDescrizione(risultato.getString("descrizione"));
					sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
					sog.setCodiceIpa(risultato.getString("codice_ipa"));
				}

				AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
				serv.setId(risultato.getLong("servid"));
				serv.setServizio(new Servizio());
				serv.getServizio().setNome(risultato.getString("nome_servizio"));
				serv.getServizio().setTipo(risultato.getString("tipo_servizio"));

				if ( risultato.getString("servizio_correlato").equals(CostantiRegistroServizi.ABILITATO.toString()) ||
						TipologiaServizio.CORRELATO.toString().equals(risultato.getString("servizio_correlato"))) {
					serv.getServizio().setTipologiaServizio(TipologiaServizio.CORRELATO);
				}
				else{
					serv.getServizio().setTipologiaServizio(TipologiaServizio.NORMALE);
				}
				sog.addAccordoServizioParteSpecifica(serv);
			}

			//aggiungo l'ultimo soggetto alla lista
			if(sog!=null) lista.add(sog);

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public Fruitore getAccordoErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getAccordoErogatoreFruitore: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		org.openspcoop2.core.registry.Fruitore fruitore = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAccordoErogatoreFruitore");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();

			if (rs.next()) {
				fruitore = new org.openspcoop2.core.registry.Fruitore();

				fruitore.setId(id);
				fruitore.setTipo(rs.getString(CostantiDB.SOGGETTI + ".tipo_soggetto"));
				fruitore.setNome(rs.getString(CostantiDB.SOGGETTI + ".nome_soggetto"));
				long idConnettore = rs.getLong(CostantiDB.SOGGETTI + "id_connettore");
				fruitore.setConnettore(getConnettore(idConnettore, con));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			return fruitore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoErogatoreFruitore] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}
	}

	public Fruitore getErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getErogatoreFruitore: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		org.openspcoop2.core.registry.Fruitore fruitore = null;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id_connettore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".stato");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".client_auth");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".profilo");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getErogatoreFruitore");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();

			if (rs.next()) {
				fruitore = new org.openspcoop2.core.registry.Fruitore();

				fruitore.setId(id);
				fruitore.setTipo(rs.getString("tipo_soggetto"));
				fruitore.setNome(rs.getString("nome_soggetto"));
				fruitore.setStatoPackage(rs.getString("stato"));
				fruitore.setVersioneProtocollo(rs.getString("profilo"));
				fruitore.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(rs.getString("client_auth")));
				long idConnettore = rs.getLong("id_connettore");
				fruitore.setConnettore(getConnettore(idConnettore, con));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getErogatoreFruitore] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			return fruitore;

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getErogatoreFruitore] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getErogatoreFruitore] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}
	}

	public AccordoServizioParteComune[] getAllIdAccordiWithSoggettoReferente(IDSoggetto idsoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdAccordiWithSoggettoReferente...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdAccordiWithSoggettoReferente");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectAliasField(CostantiDB.ACCORDI,"id","idAccordo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, idsoggetto.getTipo());
			stm.setString(2, idsoggetto.getNome());
			rs = stm.executeQuery();
			Vector<AccordoServizioParteComune> accordi = new Vector<AccordoServizioParteComune>();
			while (rs.next()) {
				accordi.add(this.getAccordoServizioParteComune(rs.getLong("idAccordo")));
			}
			if(accordi.size()==0){
				throw new DriverRegistroServiziNotFound("Accordi non trovati con soggetto referente ["+idsoggetto+"]");
			}else{
				AccordoServizioParteComune [] res = new AccordoServizioParteComune[1];
				return accordi.toArray(res);
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdAccordiWithSoggettoReferente error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}


	public List<Fruitore> accordiErogatoriFruitoriList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiErogatoriFruitoriList";
		int idLista = Liste.ACCORDI_EROGATORI_FRUITORI;
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

		ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiErogatoriFruitoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI, "id", "idFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".stato");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI, "id", "idFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".stato");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f = null;
			while (risultato.next()) {

				f = new Fruitore();

				f.setId(risultato.getLong("idFruitore"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				f.setStatoPackage(risultato.getString("stato"));

				lista.add(f);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Fruitore> serviziFruitoriList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziFruitoriList";
		int idLista = Liste.SERVIZI_FRUITORI;
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

		ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziFruitoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f = null;
			while (risultato.next()) {

				f = new Fruitore();

				f.setId(risultato.getLong("id"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				f.setIdSoggetto(risultato.getLong("id_soggetto"));
				f.setIdServizio(risultato.getLong("id_servizio"));

				lista.add(f);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public Fruitore getServizioFruitore(int idServFru) throws DriverRegistroServiziException {
		String nomeMetodo = "getServizioFruitore";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		//ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getServizioFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			// ricavo le entries
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".id_connettore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".wsdl_implementativo_erogatore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".wsdl_implementativo_fruitore");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".profilo");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".client_auth");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI+".stato");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI_FRUITORI+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServFru);
			risultato = stmt.executeQuery();

			Fruitore f = null;
			if (risultato.next()) {

				f = new Fruitore();

				f.setId(risultato.getLong("id"));
				f.setIdSoggetto(risultato.getLong("id_soggetto"));
				f.setIdServizio(risultato.getLong("id_servizio"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				long idConnettore = risultato.getLong("id_connettore");
				f.setConnettore(getConnettore(idConnettore, con));
				f.setByteWsdlImplementativoErogatore(risultato.getString("wsdl_implementativo_erogatore")!=null && !risultato.getString("wsdl_implementativo_erogatore").trim().equals("") ? risultato.getString("wsdl_implementativo_erogatore").trim().getBytes() : null);
				f.setByteWsdlImplementativoFruitore(risultato.getString("wsdl_implementativo_fruitore")!=null && !risultato.getString("wsdl_implementativo_fruitore").trim().equals("") ? risultato.getString("wsdl_implementativo_fruitore").trim().getBytes() : null);
				f.setVersioneProtocollo(risultato.getString("profilo"));
				f.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(risultato.getString("client_auth")));
				f.setStatoPackage(risultato.getString("stato"));
			}

			return f;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Controlla se l'accordo e' in uso
	 */
	public boolean isAccordoInUso(AccordoServizioParteComune as,Map<ErrorsHandlerCostant,List<String>> whereIsInUso) throws DriverRegistroServiziException {
		String nomeMetodo = "isAccordoInUso";
		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isAccordoInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			return DBOggettiInUsoUtils.isAccordoServizioParteComuneInUso_soloOggettiRegistro(con, this.tipoDB, 
					this.idAccordoFactory.getIDAccordoFromAccordo(as), whereIsInUso);

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isInUso(AccordoServizioParteComune as,Map<ErrorsHandlerCostant,ArrayList<?>> whereIsInUso) throws DriverRegistroServiziException {
		String nomeMetodo = "isAccordoInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isInUso(accordoParteComune)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			boolean isInUso = false;
			ArrayList<IDServizio> idServizi = new ArrayList<IDServizio>();
			ArrayList<IDPortaDelegata> idPorteDelegate = new ArrayList<IDPortaDelegata>();

			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, as.getId());
			risultato = stmt.executeQuery();
			String uri = this.idAccordoFactory.getUriFromAccordo(as);
			while (risultato.next()){
				isInUso=true;
				AccordoServizioParteSpecifica ss = this.getAccordoServizioParteSpecifica(risultato.getLong("id"));
				IDServizio idSE = new IDServizio();
				idSE.setServizio(ss.getServizio().getNome());
				idSE.setTipoServizio(ss.getServizio().getTipo());
				idSE.setSoggettoErogatore(ss.getServizio().getTipoSoggettoErogatore(),ss.getServizio().getNomeSoggettoErogatore());
				idSE.setTipologiaServizio(ss.getServizio().getTipologiaServizio().toString());
				idSE.setUriAccordo(uri);
				idServizi.add(idSE);
			}
			risultato.close();
			stmt.close();

			if(idServizi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, idServizi);


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, as.getId());
			risultato = stmt.executeQuery();
			uri = this.idAccordoFactory.getUriFromAccordo(as);
			while (risultato.next()){
				isInUso=true;
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setLocationPD(risultato.getString("nome_porta"));
				Soggetto fruitore = this.getSoggetto(risultato.getLong("id_soggetto"));
				IDSoggetto idSO = new IDSoggetto(fruitore.getTipo(),fruitore.getNome());
				idPD.setSoggettoFruitore(idSO);
				idPorteDelegate.add(idPD);
			}
			risultato.close();
			stmt.close();

			if(idPorteDelegate.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, idPorteDelegate);

			return isInUso;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isServizioInUso(AccordoServizioParteSpecifica ss,Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		String nomeMetodo = "isServizioInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isServizioInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			boolean isInUso = false;
			Vector<String> nomiAccordi = new Vector<String>();
			//Vector<String> nomiServiziApplicativi = new Vector<String>();
			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO,"id_accordo");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto = "+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()){
				isInUso=true;
				AccordoServizioParteComune as = getAccordoServizioParteComune(risultato.getLong("id_accordo"), con);
				nomiAccordi.add(this.idAccordoFactory.getUriFromAccordo(as));
			}
			risultato.close();
			stmt.close();

			if(nomiAccordi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_SERVIZIO_COMPONENTE_IN_ACCORDI, nomiAccordi.toString());

			//if(nomiServiziApplicativi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI_APPLICATIVI, nomiServiziApplicativi.toString());

			return isInUso;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isAccordoCooperazioneInUso(AccordoCooperazione ac,Map<ErrorsHandlerCostant,List<String>> whereIsInUso) throws DriverRegistroServiziException {
		String nomeMetodo = "isAccordoCooperazioneInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isAccordoCooperazioneInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			return DBOggettiInUsoUtils.isAccordoCooperazioneInUso(con, this.tipoDB, this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac), whereIsInUso);

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Controlla se il soggetto e' in uso
	 */
	public boolean isSoggettoInUsoInPackageFinali(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,true,false);
	}
	public boolean isSoggettoInUsoInPackagePubblici(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,false,true);
	}
	public boolean isSoggettoInUso(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return isSoggettoInUso(ss,whereIsInUso,false,false);
	}
	private boolean isSoggettoInUso(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso,boolean checkOnlyStatiFinali,boolean checkOnlyStatiPubblici) throws DriverRegistroServiziException {
		String nomeMetodo = "isSoggettoInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isSoggettoInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			boolean isInUso = false;
			Vector<String> nomiServizi = new Vector<String>();
			Vector<String> serviziFruitori = new Vector<String>();
			Vector<String> accordi = new Vector<String>();
			Vector<String> accordi_cooperazione = new Vector<String>();
			Vector<String> partecipanti = new Vector<String>();

			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoServizioParteSpecifica servizio = this.getAccordoServizioParteSpecifica(risultato.getLong("id_servizio"));
				if(checkOnlyStatiPubblici){
					if(servizio.getPrivato()==null || servizio.getPrivato()==false){
						serviziFruitori.add(servizio.getServizio().getTipo() + "/" + servizio.getServizio().getNome());
					}
				}else{
					serviziFruitori.add(servizio.getServizio().getTipo() + "/" + servizio.getServizio().getNome());
				}		
			}
			risultato.close();
			stmt.close();

			if(serviziFruitori.size()>0) whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_FRUITORI, serviziFruitori.toString());


			//controllo se referente
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoServizioParteComune accordo = this.getAccordoServizioParteComune(risultato.getLong("id"));
				accordi.add(this.idAccordoFactory.getUriFromAccordo(accordo));
			}
			risultato.close();
			stmt.close();

			if(accordi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE, accordi.toString());


			//controllo se referente in accordi cooperazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoCooperazione accordo = this.getAccordoCooperazione(risultato.getLong("id"));
				accordi_cooperazione.add(this.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
			}
			risultato.close();
			stmt.close();

			if(accordi_cooperazione.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE_COOPERAZIONE, accordi_cooperazione.toString());


			//controllo se partecipante in cooperazione
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, ss.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()){
				AccordoCooperazione accordo = this.getAccordoCooperazione(risultato.getLong("id_accordo_cooperazione"));
				if(checkOnlyStatiFinali){
					if(StatiAccordo.finale.toString().equals(accordo.getStatoPackage())){
						isInUso=true;
						partecipanti.add(this.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
					}
				}else if(checkOnlyStatiPubblici){
					if(accordo.getPrivato()==null || accordo.getPrivato()==false){
						isInUso=true;
						partecipanti.add(this.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
					}
				}else{
					isInUso=true;
					partecipanti.add(this.idAccordoCooperazioneFactory.getUriFromAccordo(accordo));
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
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isInUso(Soggetto ss, Map<ErrorsHandlerCostant,ArrayList<?>> whereIsInUso,boolean checkOnlyStatiFinali,boolean checkOnlyStatiPubblici) throws DriverRegistroServiziException {
		String nomeMetodo = "isSoggettoInUso";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isInUso(soggetto)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			boolean isInUso = false;
			ArrayList<IDServizio> idServizi = new ArrayList<IDServizio>();
			ArrayList<IDServizio> idServiziFruitori = new ArrayList<IDServizio>();
			ArrayList<IDAccordo> idAccordi = new ArrayList<IDAccordo>();
			//Vector<String> accordi_cooperazione = new Vector<String>();
			//Vector<String> partecipanti = new Vector<String>();

			//controllo se in uso in servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoServizioParteSpecifica servizio = this.getAccordoServizioParteSpecifica(risultato.getLong("id"));
				IDServizio idSE = new IDServizio();
				idSE.setServizio(servizio.getServizio().getNome());
				idSE.setTipoServizio(servizio.getServizio().getTipo());
				idSE.setSoggettoErogatore(servizio.getServizio().getTipoSoggettoErogatore(),servizio.getServizio().getNomeSoggettoErogatore());
				idSE.setTipologiaServizio(servizio.getServizio().getTipologiaServizio().toString());
				idSE.setUriAccordo(servizio.getAccordoServizioParteComune());
				idServizi.add(idSE);
			}
			risultato.close();
			stmt.close();

			if(idServizi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_SERVIZI, idServizi);

			// controllo se in uso in fruitori
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoServizioParteSpecifica servizio = this.getAccordoServizioParteSpecifica(risultato.getLong("id_servizio"));
				if(checkOnlyStatiPubblici){
					if(servizio.getPrivato()==null || servizio.getPrivato()==false){
						IDServizio idSE = new IDServizio();
						idSE.setServizio(servizio.getServizio().getNome());
						idSE.setTipoServizio(servizio.getServizio().getTipo());
						idSE.setSoggettoErogatore(servizio.getServizio().getTipoSoggettoErogatore(),servizio.getServizio().getNomeSoggettoErogatore());
						idSE.setTipologiaServizio(servizio.getServizio().getTipologiaServizio().toString());
						idSE.setUriAccordo(servizio.getAccordoServizioParteComune());
						idServiziFruitori.add(idSE);
					}
				}else{
					IDServizio idSE = new IDServizio();
					idSE.setServizio(servizio.getServizio().getNome());
					idSE.setTipoServizio(servizio.getServizio().getTipo());
					idSE.setSoggettoErogatore(servizio.getServizio().getTipoSoggettoErogatore(),servizio.getServizio().getNomeSoggettoErogatore());
					idSE.setTipologiaServizio(servizio.getServizio().getTipologiaServizio().toString());
					idSE.setUriAccordo(servizio.getAccordoServizioParteComune());
					idServiziFruitori.add(idSE);
				}		
			}
			risultato.close();
			stmt.close();

			if(idServiziFruitori.size()>0) whereIsInUso.put(ErrorsHandlerCostant.POSSIEDE_FRUITORI, idServiziFruitori);


			//controllo se referente
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				AccordoServizioParteComune accordo = this.getAccordoServizioParteComune(risultato.getLong("id"));
				idAccordi.add(this.idAccordoFactory.getIDAccordoFromAccordo(accordo));
			}
			risultato.close();
			stmt.close();

			if(idAccordi.size()>0) whereIsInUso.put(ErrorsHandlerCostant.IS_REFERENTE, idAccordi);

			return isInUso;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isAzioneInUso(String nomeAzione) throws DriverRegistroServiziException {
		String nomeMetodo = "isAzioneInUso";
		Connection con = null;
		PreparedStatement stmtPD = null;
		PreparedStatement stmtPA = null;
		ResultSet risultatoPD = null;
		ResultSet risultatoPA = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isAzioneInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("azione = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmtPA = con.prepareStatement(queryString);
			stmtPA.setString(1, nomeAzione);
			risultatoPA = stmtPA.executeQuery();
			if (risultatoPA.next()){
				return true;
			}

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_azione = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmtPD = con.prepareStatement(queryString);
			stmtPD.setString(1, nomeAzione);
			risultatoPD = stmtPD.executeQuery();
			if (risultatoPD.next())
				return true;

			return false;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultatoPA!=null) risultatoPA.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(risultatoPD!=null) risultatoPD.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmtPA!=null) stmtPA.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmtPD!=null) stmtPD.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public void deleteAzione(int idAccordo, String nomeAzione) throws DriverRegistroServiziException {
		String nomeMetodo = "deleteAzione";
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deleteAzione");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addWhereCondition("id_accordo=?");
			sqlQueryObject.addWhereCondition("nome=?");
			sqlQueryObject.setANDLogicOperator(true);
			String updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, nomeAzione);
			stmt.executeUpdate();
			stmt.close();

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}



	public IDAccordo getIdAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdAccordoServizioParteComune(id,null);
	}
	public IDAccordo getIdAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getIdAccordoServizioParteComune: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordo idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_referente");
			sqlQueryObject.addSelectField("versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.atomica)
				con = this.getConnectionFromDatasource("getIdAccordoServizioParteComune(longId)");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					referente = this.getIdSoggetto(idReferente,con);
					if(referente==null){
						throw new Exception("Soggetto referente non presente?");
					}
				}

				idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getString("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteComune] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return idAccordo;
	}




	public AccordoServizioParteComune getAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteComune(id,null);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getAccordoServizio: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordo idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.atomica)
				con = this.getConnectionFromDatasource("getAccordoServizioParteComune(longId)");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto referente = null;
				long idReferente = rs.getLong("id_referente");
				if(idReferente>0){
					Soggetto s = this.getSoggetto(idReferente,con);
					if(s==null){
						throw new Exception("Soggetto referente non presente?");
					}
					referente = new IDSoggetto(s.getTipo(),s.getNome());
				}

				idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("nome"),referente,rs.getString("versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getAccordoServizio] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizio] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getAccordoServizio] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return this.getAccordoServizioParteComune(idAccordo);
	}

	public List<Soggetto> soggettiRegistroListByTipo(String tipoSoggetto,ISearch ricerca) throws DriverRegistroServiziException{
		return soggettiRegistroList("", tipoSoggetto, ricerca);
	}

	public List<Soggetto> soggettiRegistroList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return soggettiRegistroList(superuser, null, ricerca);
	}

	private List<Soggetto> soggettiRegistroList(String superuser, String tipoSoggetto,ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiRegistroList";
		int idLista = Liste.SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("soggettiRegistroList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null){
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
					sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);	
				}
				else{
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true),
							sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if (superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if (!search.equals("")) {
				if(tipoSoggetto!=null){
					stmt.setString(index++, tipoSoggetto);
				}
			}else{
				if(tipoSoggetto!=null) 
					stmt.setString(index++, tipoSoggetto);
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id_connettore");
				sqlQueryObject.addSelectField("codice_ipa");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null){
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
					sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);	
				}
				else{
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true),
							sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id_connettore");
				sqlQueryObject.addSelectField("codice_ipa");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if (superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if (!search.equals("")) {
				if(tipoSoggetto!=null){
					stmt.setString(index++, tipoSoggetto);
				}
			}else{
				if(tipoSoggetto!=null) 
					stmt.setString(index++, tipoSoggetto);
			}
			risultato = stmt.executeQuery();

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				sog.setDescrizione(risultato.getString("descrizione"));
				sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
				sog.setPortaDominio(risultato.getString("server"));
				sog.setCodiceIpa(risultato.getString("codice_ipa"));

				long idConnettore = risultato.getLong("id_connettore");
				sog.setConnettore(getConnettore(idConnettore, con));

				lista.add(sog);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public IDSoggetto getIdSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdSoggetto(idSoggetto,null);
	}
	public IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

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
		else if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getIdSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getIdSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
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
				throw new DriverRegistroServiziNotFound("Nessun risultato trovat eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
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
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public Soggetto getSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getSoggetto(idSoggetto,null);
	}
	public Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

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
		else if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				soggetto = new Soggetto();

				soggetto.setId(rs.getLong("id"));
				// String tmp = rs.getString("nomeprov");
				// soggetto.setNome(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				soggetto.setNome(rs.getString("nome_soggetto"));
				// tmp = rs.getString("tipoprov");
				// soggetto.setTipo(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				soggetto.setTipo(rs.getString("tipo_soggetto"));

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

				long idConnettore = rs.getLong("id_connettore");
				Connettore connettore = getConnettore(idConnettore, con);

				String profilo = rs.getString("profilo");
				if(profilo!=null){
					profilo = profilo.trim();
					soggetto.setVersioneProtocollo(profilo);
				}

				tmp = rs.getString("codice_ipa");
				soggetto.setCodiceIpa(((tmp == null || tmp.equals("")) ? null : tmp));

				// aggiungo connettore
				soggetto.setConnettore(connettore);

				// Questi non li implemento per adesso
				// soggetto.addServizio()
				// soggetto.addServizioCorrelato()

			}else{
				throw new DriverRegistroServiziNotFound("Nessun risultato trovat eseguendo: "+DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			}

			return soggetto;
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public IDAccordo getIdAccordoServizioParteSpecifica(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getIdAccordoServizioParteSpecifica(id,null);
	}
	public IDAccordo getIdAccordoServizioParteSpecifica(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		this.log.debug("richiesto getIdAccordoServizioParteSpecifica: " + id);
		// conrollo consistenza
		if (id <= 0)
			return null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		IDAccordo idAccordo = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("aps_nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("aps_versione");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if(conParam!=null){
				con = conParam;
			}
			else if (this.atomica)
				con = this.getConnectionFromDatasource("getIdAccordoServizioParteSpecifica(longId)");
			else
				con = this.globalConnection;

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, id);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			rs = stm.executeQuery();


			if (rs.next()) {

				IDSoggetto erogatore = null;
				long idErogatore = rs.getLong("id_soggetto");
				if(idErogatore>0){
					erogatore = this.getIdSoggetto(idErogatore,con);
					if(erogatore==null){
						throw new Exception("Soggetto erogatore non presente?");
					}
				}

				idAccordo = this.idAccordoFactory.getIDAccordoFromValues(rs.getString("aps_nome"),erogatore,rs.getString("aps_versione"));

			} else {
				rs.close();
				stm.close();
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getIdAccordoServizioParteSpecifica] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, id));
			}

			rs.close();
			stm.close();

		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteSpecifica] SQLException :" + se.getMessage(),se);
		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getIdAccordoServizioParteSpecifica] Exception :" + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {

				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore
			}

		}

		return idAccordo;
	}

	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio,null);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio,null,readContenutoAllegati);
	}

	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return getAccordoServizioParteSpecifica(idServizio, conParam,false);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {

		// conrollo consistenza
		if (idServizio <= 0)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] L'id del servizio deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getAccordoServizioParteSpecifica(longId)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		IDServizio idServizioObject = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("servizio_correlato");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);

			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idServizio));

			rs = stm.executeQuery();

			if (rs.next()) {
				idServizioObject = new IDServizio(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"),rs.getString("tipo_servizio"),rs.getString("nome_servizio"));
				if(CostantiRegistroServizi.ABILITATO.toString().equals(rs.getString("servizio_correlato")) || 
						TipologiaServizio.CORRELATO.toString().equals(rs.getString("servizio_correlato"))){
					idServizioObject.setTipologiaServizio(TipologiaServizio.CORRELATO.toString());
				}
				else{
					idServizioObject.setTipologiaServizio(TipologiaServizio.NORMALE.toString());
				}

			}else{
				throw new DriverRegistroServiziNotFound("Nessun Servizio trovato con id="+idServizio);
			}
			rs.close();
			stm.close();
		} catch (SQLException se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] SqlException: " + se.getMessage(),se);
		} catch (DriverRegistroServiziNotFound nf) {
			throw nf;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getServizio] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (conParam==null && this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

		return this.getAccordoServizioParteSpecifica(idServizioObject,readContenutoAllegati,conParam);

	}

	//RESET
	@Override
	public void reset() throws DriverRegistroServiziException {

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("reset");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		//this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//Svuoto il db del registro servizi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORT_TYPE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE_PARTECIPANTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.ACCORDI_COOPERAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SOGGETTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI_CUSTOM);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.CONNETTORI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.DOCUMENTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PDD);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

		} catch (SQLException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Errore durante il reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::reset] Errore durante il reset : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Reset delle tabelle del db pddConsole gestito da questo driver
	 */
	public void resetCtrlstat() throws DriverRegistroServiziException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("resetCtrlstat");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		//this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.RUOLI_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.POLITICHE_SICUREZZA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
		}finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Recupero l'id del servizio correlato
	 * @param nomeServizio
	 * @param tipoServizio
	 * @param nomeProprietario
	 * @param tipoProprietario
	 * @param con
	 * @return L'id del servizio correlato se esiste, -1 altrimenti
	 * @throws DriverRegistroServiziException
	 */
	public long getIdServizioCorrelato(String nomeServizio, String tipoServizio, String nomeProprietario, String tipoProprietario, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizio = 0;
		try {
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, this.tipoDB, this.tabellaSoggetti);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("servizio_correlato = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setString(1, tipoServizio);
			stm.setString(2, nomeServizio);
			stm.setLong(3, idSoggetto);
			stm.setString(4, CostantiRegistroServizi.ABILITATO.toString());
			rs = stm.executeQuery();

			if (rs.next()) {
				idServizio = rs.getLong("id");
			}

			return idServizio;

		} catch (CoreException e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}

	public List<String> getTipi() throws DriverRegistroServiziException{
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getTipi");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ArrayList<String> lista=new ArrayList<String>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.TIPI);
			sqlQueryObject.addSelectField("*");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmt=con.prepareStatement(sqlQuery);
			risultato = stmt.executeQuery();
			while(risultato.next()){
				String tipo = risultato.getString("nome");
				lista.add(tipo);
			}
			risultato.close();
			stmt.close();

			return lista;
		}catch (Exception se) {
			throw new DriverRegistroServiziException(se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public String getTipoById(long id) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getTipoById");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.TIPI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stmt=con.prepareStatement(sqlQuery);
			stmt.setLong(1, id);
			risultato = stmt.executeQuery();
			String tipo="";
			if(risultato.next()){
				tipo = risultato.getString("nome");
			}else{
				throw new DriverRegistroServiziNotFound("Impossibile trovare un tipo Soggetto con id="+id);
			}
			risultato.close();
			stmt.close();

			return tipo;
		}catch (Exception se) {
			throw new DriverRegistroServiziNotFound(se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}




	public List<Fruitore> serviziFruitoriList(int idServizi, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziFruitoriList";
		int idLista = Liste.SERVIZI_FRUITORI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziFruitoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} 
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idServizi);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"id","idFruitore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"stato","statoFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"id","idFruitore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI,"stato","statoFruitore");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idServizi);
			risultato = stmt.executeQuery();

			Fruitore f;
			lista = new ArrayList<Fruitore>();
			while (risultato.next()) {
				f = new Fruitore();

				f.setId(risultato.getLong("idFruitore"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));
				f.setIdSoggetto(risultato.getLong("id_soggetto"));
				f.setIdServizio(risultato.getLong("id_servizio"));
				f.setStatoPackage(risultato.getString("statoFruitore"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<AccordoServizioParteSpecifica> serviziSoggettoList(long idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziSoggettoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> lista = new ArrayList<AccordoServizioParteSpecifica>();
		AccordoServizioParteSpecifica serv;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziSoggettoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".tipo_servizio");
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI+".nome_servizio");
			sqlQueryObject.setSortType(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				serv = new AccordoServizioParteSpecifica();
				serv.setServizio(new Servizio());
				serv.setId(risultato.getLong("id"));
				serv.getServizio().setNome(risultato.getString("nome_servizio"));
				serv.getServizio().setTipo(risultato.getString("tipo_servizio"));
				lista.add(serv);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<AccordoServizioParteSpecifica> serviziWithIdAccordoList(long idAccordo) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziWithIdAccordoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> lista = new ArrayList<AccordoServizioParteSpecifica>();
		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziWithIdAccordoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				AccordoServizioParteSpecifica serv = this.getAccordoServizioParteSpecifica(risultato.getLong("id"));
				lista.add(serv);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Recupero l'id del servizio fruitore
	 * @param idServizio
	 * @param idSoggetto
	 * @return L'id del servizio fruitore
	 * @throws DriverRegistroServiziException
	 */
	public int getIdServizioFruitore(int idServizio, int idSoggetto) throws DriverRegistroServiziException {
		int idFru = 0;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("getIdServizioFruitore");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getIdServizioFruitore] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_FRUITORI + ".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setInt(1, idServizio);
			stm.setInt(2, idSoggetto);
			rs = stm.executeQuery();
			if (rs.next())
				idFru = rs.getInt("id");
			rs.close();
			stm.close();
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return idFru;
	}

	public List<Fruitore> getServiziFruitoriWithServizio(int idServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "getServiziFruitoriWithServizio";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getServiziFruitoriWithServizio");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI + ".id = " + CostantiDB.SERVIZI_FRUITORI + ".id_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f;
			lista = new ArrayList<Fruitore>();
			while (risultato.next()) {

				f = new Fruitore();

				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<AccordoServizioParteSpecifica> getServiziByIdErogatore(long idErogatore) throws DriverRegistroServiziException {
		return getServiziByIdErogatoreAndFilters(idErogatore, null);		
	}

	public List<AccordoServizioParteSpecifica> getServiziByIdErogatore(long idErogatore,ISearch filters) throws DriverRegistroServiziException {
		return getServiziByIdErogatoreAndFilters(idErogatore, filters);		
	}

	private List<AccordoServizioParteSpecifica> getServiziByIdErogatoreAndFilters(long idErogatore,ISearch filters) throws DriverRegistroServiziException {

		String nomeMetodo = "getServiziByIdErogatoreAndFilters";
		String queryString;
		int idLista = Liste.SERVIZI;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		ArrayList<AccordoServizioParteSpecifica> lista = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getServiziByIdErogatoreAndFilters");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//count
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
			} 
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idErogatore);
			risultato = stmt.executeQuery();
			if (risultato.next())
				filters.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();



			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("id_soggetto");	
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
			} 

			sqlQueryObject.addOrderBy("tipo_servizio");
			sqlQueryObject.addOrderBy("nome_servizio");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idErogatore);
			risultato = stmt.executeQuery();

			lista = new ArrayList<AccordoServizioParteSpecifica>();
			while (risultato.next()) {
				long id=risultato.getLong("id");
				AccordoServizioParteSpecifica se=this.getAccordoServizioParteSpecifica(id, con);
				lista.add(se);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<AccordoServizioParteSpecifica> getServiziByFruitore(Fruitore fruitore) throws DriverRegistroServiziException {
		String nomeMetodo = "getServiziByFruitore";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteSpecifica> lista = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getServiziByFruitore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			long idFruitore = DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, this.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idFruitore);
			risultato = stmt.executeQuery();

			lista = new ArrayList<AccordoServizioParteSpecifica>();
			while (risultato.next()) {
				long id=risultato.getLong("id");
				AccordoServizioParteSpecifica se=this.getAccordoServizioParteSpecifica(id, con);
				lista.add(se);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<Fruitore> getSoggettiWithServizioNotFruitori(int idServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "getSoggettiWithServizioNotFruitori";
		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<Fruitore> lista = new ArrayList<Fruitore>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getSoggettiWithServizioNotFruitori");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			// Condizione where not
			ISQLQueryObject sqlQueryObjectWhere = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectWhere.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObjectWhere.addSelectField("*");
			sqlQueryObjectWhere.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
			sqlQueryObjectWhere.addWhereCondition(CostantiDB.SERVIZI_FRUITORI + ".id_servizio = ?");
			sqlQueryObjectWhere.setANDLogicOperator(true);

			// Query
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".id");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI + ".nome_soggetto");
			sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectWhere);
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
			sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idServizio);
			risultato = stmt.executeQuery();

			Fruitore f;
			while (risultato.next()) {
				f = new Fruitore();

				f.setId(risultato.getLong("id"));
				f.setNome(risultato.getString("nome_soggetto"));
				f.setTipo(risultato.getString("tipo_soggetto"));

				lista.add(f);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	public List<PortType> accordiPorttypeList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt");
				sqlQueryObject.addSelectField("soap_style");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt");
				sqlQueryObject.addSelectField("soap_style");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_accordo = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();

			PortType pt;
			while (risultato.next()) {
				pt = new PortType();

				String tmp = risultato.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);
				tmp = risultato.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));
				pt.setIdAccordo(risultato.getLong("id_accordo"));
				long idPortType = risultato.getLong("id");
				pt.setId(idPortType);
				lista.add(pt);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<PortType> accordiPorttypeList(int idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = ?");
			if(profiloCollaborazione!=null){
				sqlQueryObject.addWhereCondition(false,
						CostantiDB.ACCORDI+".profilo_collaborazione=?"
						,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			if(profiloCollaborazione!=null){
				stmt.setString(2,profiloCollaborazione);
				stmt.setString(3,profiloCollaborazione);
				stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "id_accordo");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "nome");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "descrizione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "profilo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "filtro_duplicati");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "conferma_ricezione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "identificativo_collaborazione");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "consegna_in_ordine");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "scadenza");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "profilo_pt");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "soap_style");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE, "id");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = ?");
			if(profiloCollaborazione!=null){
				sqlQueryObject.addWhereCondition(false,
						CostantiDB.ACCORDI+".profilo_collaborazione=?"
						,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?");
			}
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy(CostantiDB.PORT_TYPE+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			if(profiloCollaborazione!=null){
				stmt.setString(2,profiloCollaborazione);
				stmt.setString(3,profiloCollaborazione);
				stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}
			risultato = stmt.executeQuery();

			PortType pt;
			while (risultato.next()) {
				pt = new PortType();

				String tmp = risultato.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);
				tmp = risultato.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));
				pt.setIdAccordo(risultato.getLong("id_accordo"));
				long idPortType = risultato.getLong("id");
				pt.setId(idPortType);
				lista.add(pt);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Documento> accordiAllegatiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiAllegatiList";
		int idLista = Liste.ACCORDI_ALLEGATI;
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

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoServizio.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoServizio.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_LIB.getDocumento(risultato.getLong("id"),false, con, this.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiComponentiList";
		int idLista = Liste.ACCORDI_COMPONENTI;
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

		ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponente> lista = new ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponente>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiComponentiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			/*
			 *  SELECT * from acc_serv_composti composti, acc_serv_componenti componenti 
			 *  where composti.id_accordo=5 and composti.id=componenti.id_servizio_composto;

			 */
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_servizio",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_servizio",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI,"id_servizio_componente");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI,"azione");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI,"tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI,"nome_servizio");

			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");


			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_servizio",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_servizio",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.addOrderBy("tipo_servizio");
			sqlQueryObject.addOrderBy("nome_servizio");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();

			while(risultato.next()){

				long idServizioComponente = risultato.getLong("id_servizio_componente");
				String azione = risultato.getString("azione");
				AccordoServizioParteComuneServizioCompostoServizioComponente asComponente = new AccordoServizioParteComuneServizioCompostoServizioComponente();
				asComponente.setAzione(azione);
				asComponente.setIdServizioComponente(idServizioComponente);
				asComponente.setTipo(risultato.getString("tipo_servizio"));
				asComponente.setNome(risultato.getString("nome_servizio"));
				asComponente.setTipoSoggetto(risultato.getString("tipo_soggetto"));
				asComponente.setNomeSoggetto(risultato.getString("nome_soggetto"));

				lista.add(asComponente);
			}


			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Documento> accordiCoopAllegatiList(int idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCoopAllegatiList";
		int idLista = Liste.ACCORDI_COOP_ALLEGATI;
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

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiCoopAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoCooperazione.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoCooperazione.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_LIB.getDocumento(risultato.getLong("id"),false, con, this.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Operation> accordiPorttypeOperationList(int idPortType,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationsList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
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

		ArrayList<Operation> lista = new ArrayList<Operation>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeOperationList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			/*SELECT a.id,pt.nome,pta.nome 
			 * from accordi a,port_type pt,port_type_azioni pta
				where a.id=pt.id_accordo and pt.id=pta.id_port_type
				and 
				(
					a.profilo_collaborazione='asincronoAsimmetrico'
					OR (pt.profilo_pt='ridefinito' AND pt.profilo_collaborazione='asincronoAsimmetrico')
					OR (pta.profilo_pt_azione='ridefinito' AND pta.profilo_collaborazione='asincronoAsimmetrico')
				));
			 */	
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			//count
			sqlQueryObject.addSelectCountField(CostantiDB.PORT_TYPE_AZIONI+".id", "cont");
			//condizioni di join
			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = "+CostantiDB.PORT_TYPE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id = ?");

			sqlQueryObject.addWhereCondition(false,
					CostantiDB.ACCORDI+".profilo_collaborazione=?"
					,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?"
					,CostantiDB.PORT_TYPE_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE_AZIONI+".profilo_pt_azione= ?");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idPortType);
			stmt.setString(2,profiloCollaborazione);
			stmt.setString(3,profiloCollaborazione);
			stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(5,profiloCollaborazione);
			stmt.setString(6,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);

			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			//select
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE_AZIONI,"id_port_type");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"nome","nomePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"profilo_collaborazione","profCollPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"filtro_duplicati","filtro_duplicatiPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"conferma_ricezione","conferma_ricezionePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"identificativo_collaborazione","idCollPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"consegna_in_ordine","consegna_in_ordinePTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"scadenza","scadenzaPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"correlata","correlataPTAz");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"correlata_servizio","correlataServizioPTAz");
			sqlQueryObject.addSelectField(CostantiDB.PORT_TYPE_AZIONI,"profilo_pt_azione");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORT_TYPE_AZIONI,"id","idPTAz");
			//condizioni di join
			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = "+CostantiDB.PORT_TYPE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id = ?");

			sqlQueryObject.addWhereCondition(false,
					CostantiDB.ACCORDI+".profilo_collaborazione=?"
					,CostantiDB.PORT_TYPE+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE+".profilo_pt= ?"
					,CostantiDB.PORT_TYPE_AZIONI+".profilo_collaborazione = ? AND "+CostantiDB.PORT_TYPE_AZIONI+".profilo_pt_azione= ?");

			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}

			sqlQueryObject.addOrderBy(CostantiDB.PORT_TYPE_AZIONI+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			queryString = sqlQueryObject.createSQLQuery();

			this.log.debug("Query: "+queryString);

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idPortType);
			stmt.setString(2,profiloCollaborazione);
			stmt.setString(3,profiloCollaborazione);
			stmt.setString(4,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			stmt.setString(5,profiloCollaborazione);
			stmt.setString(6,CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);


			risultato = stmt.executeQuery();
			Operation op;
			while (risultato.next()) {
				op = new Operation();

				String tmp = risultato.getString("nomePTAz");
				op.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profCollPTAz");
				op.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicatiPTAz");
				op.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezionePTAz");
				op.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("idCollPTAz");
				op.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordinePTAz");
				op.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenzaPTAz");
				op.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("correlataPTAz");
				op.setCorrelata(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("correlataServizioPTAz");
				op.setCorrelataServizio(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					op.setProfAzione(tmp);
				op.setIdPortType(risultato.getLong("id_port_type"));
				long idOperation = risultato.getLong("idPTAz");
				op.setId(idOperation);
				lista.add(op);
			}

			return lista;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	public List<Operation> accordiPorttypeOperationList(int idPortType, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationsList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
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

		ArrayList<Operation> lista = new ArrayList<Operation>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeOperationList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idPortType);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id_port_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectField("id_port_type");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("profilo_collaborazione");
				sqlQueryObject.addSelectField("filtro_duplicati");
				sqlQueryObject.addSelectField("conferma_ricezione");
				sqlQueryObject.addSelectField("identificativo_collaborazione");
				sqlQueryObject.addSelectField("consegna_in_ordine");
				sqlQueryObject.addSelectField("scadenza");
				sqlQueryObject.addSelectField("profilo_pt_azione");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idPortType);
			risultato = stmt.executeQuery();

			Operation op;
			while (risultato.next()) {
				op = new Operation();

				String tmp = risultato.getString("nome");
				op.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				op.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				op.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				op.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				op.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				op.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				op.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt_azione");
				if (tmp == null || tmp.equals(""))
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					op.setProfAzione(tmp);
				op.setIdPortType(risultato.getLong("id_port_type"));
				long idOperation = risultato.getLong("id");
				op.setId(idOperation);
				lista.add(op);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public List<MessagePart> accordiPorttypeOperationMessagePartList(int idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeOperationMessagePartList";
		int idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;
		if(!isInput)
			idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
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

		ArrayList<MessagePart> lista = new ArrayList<MessagePart>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeOperationMessagePartList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.addWhereLikeCondition("name", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idOperation);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectField("name");
				sqlQueryObject.addSelectField("element_name");
				sqlQueryObject.addSelectField("element_namespace");
				sqlQueryObject.addSelectField("type_name");
				sqlQueryObject.addSelectField("type_namespace");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("name");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI_OPERATION_MESSAGES);
				sqlQueryObject.addSelectField("name");
				sqlQueryObject.addSelectField("element_name");
				sqlQueryObject.addSelectField("element_namespace");
				sqlQueryObject.addSelectField("type_name");
				sqlQueryObject.addSelectField("type_namespace");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_port_type_azione = ?");
				if(isInput)
					sqlQueryObject.addWhereCondition("input_message = 1");
				else
					sqlQueryObject.addWhereCondition("input_message != 1");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("name");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idOperation);
			risultato = stmt.executeQuery();

			
			MessagePart mp;
			
			while (risultato.next()) {
				mp = new MessagePart();

				String tmp = risultato.getString("name");
				mp.setName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("element_name");
				mp.setElementName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("element_namespace");
				mp.setElementNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("type_name");
				mp.setTypeName(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("type_namespace");
				mp.setTypeNamespace(((tmp == null || tmp.equals("")) ? null : tmp));
				long idMessage = risultato.getLong("id");
				mp.setId(idMessage);
				lista.add(mp);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idAccordo
	 * @param nomeAzione
	 * @return true se se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	public boolean isCorrelata(int idAccordo,String nomeAzione,String owner) throws DriverRegistroServiziException {
		String nomeMetodo = "isCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isCorrelata");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {			

			// ricavo le entries

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField("id", "tot");

			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.addWhereCondition("correlata = ?");
			sqlQueryObject.addWhereCondition("nome <> ?");

			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			stmt.setString(2, nomeAzione);
			stmt.setString(3, owner);

			risultato = stmt.executeQuery();

			int tot=0;
			if (risultato.next()) {
				tot=risultato.getInt("tot");
			}
			risultato.close();

			return tot>0;


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idPortType
	 * @param nomeCorrelata
	 * @return true se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	public boolean isOperationCorrelata(int idPortType,String nomeCorrelata,String owner) throws DriverRegistroServiziException {
		String nomeMetodo = "isOperationCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isOperationCorrelata");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {			

			// ricavo le entries

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectCountField("id", "tot");

			sqlQueryObject.addWhereCondition("id_port_type = ?");
			sqlQueryObject.addWhereCondition("correlata = ?");
			sqlQueryObject.addWhereCondition("nome <> ?");

			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idPortType);
			stmt.setString(2, nomeCorrelata);
			stmt.setString(3, owner);

			risultato = stmt.executeQuery();

			int tot=0;
			if (risultato.next()) {
				tot=risultato.getInt("tot");
			}
			risultato.close();

			return tot>0;


		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isOperationCorrelataDaAltraAzione(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, false, true);
	}
	public boolean isOperationCorrelataRichiesta(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, true, false);
	}
	public boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare, true, true);
	}
	private boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare,
			boolean checkCorrelazioneARichiesta,boolean checkCorrelazioneDaAltraAzione) throws DriverRegistroServiziException {
		String nomeMetodo = "isOperationCorrelata";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isOperationCorrelata(nomePortType,id,azione,idAzione,check)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {			

			// VERIFICO CORRELAZIONE AD UNA RICHIESTA
			boolean correlataAdUnaRichiesta = false;
			if(checkCorrelazioneARichiesta){
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("id", "tot");
				// seleziono il port type
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id_port_type = ?");
				// seleziono l'azione
				sqlQueryObject.addWhereCondition("nome = ?");
				// verifico che l'azione non sia correlata ad una richiesta
				sqlQueryObject.addWhereCondition(false,CostantiDB.PORT_TYPE_AZIONI+".correlata is not null",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is not null");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				this.log.debug("VERIFICO CORRELAZIONE AD UNA RICHIESTA ["+idPortType+"] ["+azioneDaVerificare+"]: "+queryString);
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idPortType);
				stmt.setString(2, azioneDaVerificare);
				risultato = stmt.executeQuery();
				int tot=0;
				if (risultato.next()) {
					tot=risultato.getInt("tot");
				}
				risultato.close();
				stmt.close();
				correlataAdUnaRichiesta = tot>0;
				this.log.debug("VERIFICO CORRELAZIONE AD UNA RICHIESTA, risultato ["+tot+"]: "+correlataAdUnaRichiesta);
			}

			// VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE
			boolean correlataDaUnAltraAzione = false;
			if(checkCorrelazioneDaAltraAzione){

				// Correlazione precisa: per asincrono simmetrico e asincrono asimmetrico con port type diversi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
				sqlQueryObject.addSelectCountField("id", "tot");
				sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id<>?");
				sqlQueryObject.addWhereCondition(true,CostantiDB.PORT_TYPE_AZIONI+".correlata =?",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio =?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				this.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AS e AA con PT diverso) ["+idAzioneDaVerificare+"] ["+azioneDaVerificare+"] ["+nomePortType+"]: "+queryString);
				stmt.setLong(1, idAzioneDaVerificare);
				stmt.setString(2, azioneDaVerificare);
				stmt.setString(3, nomePortType);
				risultato = stmt.executeQuery();
				int tot=0;
				if (risultato.next()) {
					tot=risultato.getInt("tot");
				}
				risultato.close();
				stmt.close();
				correlataDaUnAltraAzione = tot>0;
				this.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AS e AA con PT diverso), risultato ["+tot+"]: "+correlataDaUnAltraAzione);

				if(correlataDaUnAltraAzione==false){
					// Correlazione sullo stesso port type
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
					sqlQueryObject.addSelectCountField("id", "tot");
					sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".id<>?");
					sqlQueryObject.addWhereCondition(true,CostantiDB.PORT_TYPE_AZIONI+".correlata =?",CostantiDB.PORT_TYPE_AZIONI+".correlata_servizio is null",CostantiDB.PORT_TYPE_AZIONI+".id_port_type = ?");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					this.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AA con stesso port type) ["+idAzioneDaVerificare+"] ["+azioneDaVerificare+"] ["+nomePortType+"] ["+azioneDaVerificare+"] ["+idPortType+"]: "+queryString);
					stmt.setLong(1, idAzioneDaVerificare);
					stmt.setString(2, azioneDaVerificare);
					stmt.setLong(3, idPortType);
					risultato = stmt.executeQuery();
					tot=0;
					if (risultato.next()) {
						tot=risultato.getInt("tot");
					}
					risultato.close();
					stmt.close();
					correlataDaUnAltraAzione = tot>0;
					this.log.debug("VERIFICO CHE NON SIA CORRELATA DA UN'ALTRA AZIONE (AA con stesso port type), risultato ["+tot+"]: "+correlataDaUnAltraAzione);
				}
			}

			if(correlataAdUnaRichiesta){
				return true;
			}
			else if(correlataDaUnAltraAzione){
				return true;
			}
			else{
				return false;
			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws CoreException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{

		if(this.create==false)
			throw new CoreException("Driver non inizializzato");

		if(this.atomica){
			// Verifico la connessione
			Connection con = null;
			Statement stmtTest = null;
			try {
				con = this.getConnectionFromDatasource("isAlive");
				if(con == null)
					throw new Exception("Connessione is null");
				// test:
				stmtTest = con.createStatement();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable("db_info");
				sqlQueryObject.addSelectField("*");
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmtTest.execute(sqlQuery);
			} catch (Exception e) {
				throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);

			}finally{
				try{
					if(stmtTest!=null)
						stmtTest.close();
				}catch(Exception e){}
				try{
					if(con!=null)
						con.close();
				}catch(Exception e){}
			}
		}else{
			Statement stmtTest = null;
			try {
				if(this.globalConnection == null)
					throw new Exception("Connessione is null");
				// test:
				stmtTest = this.globalConnection.createStatement();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable("db_info");
				sqlQueryObject.addSelectField("*");
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmtTest.execute(sqlQuery);
			} catch (Exception e) {
				throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);

			}finally{
				try{
					if(stmtTest!=null)
						stmtTest.close();
				}catch(Exception e){}
			}
		}
	}

	public List<AccordoServizioParteSpecifica> soggettiServizioList(long idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiServizioList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> serviziList = new ArrayList<AccordoServizioParteSpecifica>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("soggettiServizioList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addOrderBy("tipo_servizio");
			sqlQueryObject.addOrderBy("nome_servizio");
			sqlQueryObject.setSortType(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				AccordoServizioParteSpecifica asps = new AccordoServizioParteSpecifica();
				asps.setServizio(new Servizio());
				asps.setId(risultato.getLong("id"));
				asps.getServizio().setTipo(risultato.getString("tipo_servizio"));
				asps.getServizio().setNome(risultato.getString("nome_servizio"));
				serviziList.add(asps);
			}

			return serviziList;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	/***
	 * 
	 * @param superuser
	 * @param ricerca
	 * @param permessiUtente se permessiUtente[0] = true l'utente ha permessi per la categoria Servizi 'S', se permessiUtente[1] = true l'utente ha permessi per la categoria Accordi Cooperazione 'P'
	 * @return lista di accordi di servizio parte specifica
	 * @throws DriverRegistroServiziException
	 */
	public List<AccordoServizioParteSpecifica> soggettiServizioList(String superuser, ISearch ricerca,boolean [] permessiUtente) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiServizioList";
		int idLista = Liste.SERVIZI;
		int offset;
		int limit;
		String search;
		String queryString;
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> serviziList = new ArrayList<AccordoServizioParteSpecifica>();

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("soggettiServizioList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObjectAccordiComposti = null;

			if (permessiUtente != null) {
				sqlQueryObjectAccordiComposti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectAccordiComposti.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObjectAccordiComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObjectAccordiComposti.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id");
				sqlQueryObjectAccordiComposti.setANDLogicOperator(true);
				sqlQueryObjectAccordiComposti.addWhereCondition(CostantiDB.SERVIZI+".id_accordo="+CostantiDB.ACCORDI+".id");
				sqlQueryObjectAccordiComposti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
			}

			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.setANDLogicOperator(true);
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				sqlQueryObjectSoggetti.addWhereCondition(false,
						sqlQueryObjectSoggetti.getWhereLikeCondition("tipo_soggetto", search, true, true),
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true));
			}

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						// - ricerca su tipo/nome servizio
						sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition("nome_servizio", search, true, true),
						// - ricerca su dati accordo
						sqlQueryObject.getWhereLikeCondition("aps_nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("aps_versione", search, true, true), la versione e' troppo, di solito e' sempre 1...
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));

				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}

				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");

				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}

				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"descrizione","descrizioneServizio");
				sqlQueryObject.addSelectField("aps_versione");
				sqlQueryObject.addSelectField("aps_nome");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereCondition(false, 
						// - ricerca su tipo/nome servizio
						sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition("nome_servizio", search, true, true),
						// - ricerca su dati accordo
						sqlQueryObject.getWhereLikeCondition("aps_nome", search, true, true),
						//sqlQueryObject.getWhereLikeCondition("aps_versione", search, true, true), la versione e' troppo, di solito e' sempre 1...
						sqlQueryObject.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				
				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}
				
				
				sqlQueryObject.setANDLogicOperator(true);
				//				sqlQueryObject.addOrderBy("tipo_servizio");
				//				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("aps_nome");
				sqlQueryObject.addOrderBy("aps_versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"descrizione","descrizioneServizio");
				sqlQueryObject.addSelectField("aps_versione");
				sqlQueryObject.addSelectField("aps_nome");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				
				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				//				sqlQueryObject.addOrderBy("tipo_servizio");
				//				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("aps_nome");
				sqlQueryObject.addOrderBy("aps_versione");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			this.log.debug("query : " + queryString);

			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
				serv.setServizio(new Servizio());

				serv.setId(risultato.getLong("id"));
				serv.getServizio().setId(risultato.getLong("id"));
				serv.getServizio().setNome(risultato.getString("nome_servizio"));
				serv.getServizio().setTipo(risultato.getString("tipo_servizio"));
				serv.setIdSoggetto(risultato.getLong("id_soggetto"));
				serv.setIdAccordo(risultato.getLong("id_accordo"));
				String servizio_correlato = risultato.getString("servizio_correlato");
				if( (servizio_correlato != null) && 
						(servizio_correlato.equalsIgnoreCase(CostantiRegistroServizi.ABILITATO.toString()) ||
								TipologiaServizio.CORRELATO.toString().equals(servizio_correlato))){
					serv.getServizio().setTipologiaServizio(TipologiaServizio.CORRELATO);
				}else{
					serv.getServizio().setTipologiaServizio(TipologiaServizio.NORMALE);
				}
				serv.setDescrizione(risultato.getString("descrizioneServizio"));

				// informazioni su soggetto
				Soggetto sog = this.getSoggetto(serv.getIdSoggetto(),con);
				String nomeErogatore = sog.getNome();
				String tipoErogatore = sog.getTipo();

				serv.getServizio().setNomeSoggettoErogatore(nomeErogatore);
				serv.getServizio().setTipoSoggettoErogatore(tipoErogatore);

				// informazioni su accordo
				AccordoServizioParteComune as = this.getAccordoServizioParteComune(serv.getIdAccordo(),con);
				serv.setAccordoServizioParteComune(this.idAccordoFactory.getUriFromAccordo(as));

				// Stato
				serv.setStatoPackage(risultato.getString("stato"));

				// informazioni su accordo servizio parte specifica
				serv.setNome(risultato.getString("aps_nome"));
				serv.setVersione(risultato.getString("aps_versione"));

				serviziList.add(serv);
			}

			return serviziList;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Crea un nuovo Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void createConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("createConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 1");
			// creo connettore
			DriverRegistroServiziDB_LIB.CRUDConnettore(1, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Aggiorna un Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void updateConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("updateConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 2");
			// update connettore
			DriverRegistroServiziDB_LIB.CRUDConnettore(2, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Elimina un Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void deleteConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("deleteConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 3");
			// delete connettore
			DriverRegistroServiziDB_LIB.CRUDConnettore(3, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
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
	 * Ritorna la lista di Porte di Dominio
	 * Tiene conto dei criteri di ricerca Settati
	 * @param ricerca
	 * @return la lista degli accordi di servizio che soddisfa la ricerca
	 */
	public List<PortaDominio> porteDominioList(String superuser, ISearch ricerca) throws DriverRegistroServiziException{
		return this.porteDominioList(superuser, null, ricerca);
	}
	public List<PortaDominio> porteDominioList(String superuser,String tipo, ISearch ricerca) throws DriverRegistroServiziException{
		String nomeMetodo = "porteDominioList";
		int idLista = Liste.PDD;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<PortaDominio> lista = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("porteDominioList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				// query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.addWhereLikeCondition(
						"nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if (tipo!=null && (!tipo.equals("")))
				stmt.setString(index++, tipo);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("nome");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.addWhereLikeCondition(
						"nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("nome");
				if (superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if (superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if (tipo!=null && (!tipo.equals("")))
				stmt.setString(index++, tipo);
			risultato = stmt.executeQuery();

			PortaDominio pdd;
			lista = new ArrayList<PortaDominio>();
			while (risultato.next()) {
				String nome = risultato.getString("nome");
				pdd = this.getPortaDominio(nome);
				lista.add(pdd);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) {
					risultato.close();
				}
				if(stmt!=null) {
					stmt.close();
				}
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public boolean isPddInUso(PortaDominio pdd, List<String> whereIsInUso)
			throws DriverRegistroServiziException {
		String nomeMetodo = "pddInUso";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isPddInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(
						"[DriverRegistroServiziDB::" + nomeMetodo
						+ "] Exception accedendo al datasource :"
						+ e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject
			.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject
			.addWhereCondition("server = ?");
			queryString = sqlQueryObject
					.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, pdd.getNome());
			risultato = stmt.executeQuery();
			boolean isInUso = false;
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				whereIsInUso.add(tipo_soggetto + "/" + nome_soggetto);
				isInUso = true;
			}

			return isInUso;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::"
					+ nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Soggetto> pddSoggettiList(int idPdd, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "pddSoggettiList";
		int idLista = Liste.PDD_SOGGETTI;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("pddSoggettiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(
						"[DriverRegistroServiziDB::" + nomeMetodo
						+ "] Exception accedendo al datasource :"
						+ e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		ArrayList<Soggetto> lista = null;

		try {

			// Prendo il nome del pdd
			String nomePdd = "";
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			queryString = sqlQueryObject
					.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idPdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				nomePdd = risultato.getString("nome");
			}
			risultato.close();
			stmt.close();

			if (!search.equals("")) {
				// query con search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false,
						sqlQueryObject
						.getWhereLikeCondition("nome_soggetto", search,
								true, true),
								sqlQueryObject
								.getWhereLikeCondition("tipo_soggetto", search,
										true, true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				sqlQueryObject
				.addWhereCondition("server = ?");
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false,
						sqlQueryObject
						.getWhereLikeCondition("nome_soggetto", search,
								true, true),
								sqlQueryObject
								.getWhereLikeCondition("tipo_soggetto", search,
										true, true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject
				.addOrderBy("tipo_soggetto");
				sqlQueryObject
				.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				// senza search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject
				.addOrderBy("tipo_soggetto");
				sqlQueryObject
				.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();

			lista = new ArrayList<Soggetto>();
			Soggetto sog = null;
			while (risultato.next()) {
				long ids = risultato.getLong("id");
				sog = this.getSoggetto(ids,con);
				lista.add(sog);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::"
					+ nomeMetodo + "] Exception: " + se.getMessage(), se);
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	/**
	 * Recupera gli id dei soggetti che erogano un determinato servizio
	 * Si puo passare il tipo di soggetti che si desidera controllare, oppure null per tutti i soggetti. 
	 * @param tipoServizio
	 * @param nomeServizio
	 * @param uriAccordo
	 * @param nomePortType
	 * @param tipoSoggetto Il tipo del soggetto erogatore
	 * @return List<IDSoggetto>
	 * @throws DriverRegistroServiziException
	 */
	public List<IDSoggetto> getAllIdSoggettiErogatori(String tipoServizio,String nomeServizio,String uriAccordo,String nomePortType,String tipoSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "getAllIdSoggettiErogatori";
		ArrayList<IDSoggetto> lista = new ArrayList<IDSoggetto>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getAllIdSoggettiErogatori");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//recupero idAccordo
			IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromUri(uriAccordo);
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "tipo_soggetto", "tipoSoggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "nome_soggetto", "nomeSoggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio= ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio= ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type = ?");
			if(tipoSoggetto!=null) sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoServizio);
			stmt.setString(2, nomeServizio);
			stmt.setLong(3, idAccordoLong);
			stmt.setString(4, nomePortType);
			if(tipoSoggetto!=null) stmt.setString(5, tipoSoggetto);

			risultato = stmt.executeQuery();

			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipoSoggetto");
				String nome_soggetto = risultato.getString("nomeSoggetto");
				lista.add(new IDSoggetto(tipo_soggetto,nome_soggetto));
			}

			return lista;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (risultato != null) {
					risultato.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public PortType getPortType(IDPortType idPT) throws DriverRegistroServiziException {
		String nomeMetodo = "getPortType";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getPortType");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//recupero idAccordo
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idPT.getIdAccordo(), con, this.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE+".nome=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);
			stmt.setString(2, idPT.getNome());

			rs = stmt.executeQuery();
			PortType pt = null;
			if (rs.next()) {
				pt = new PortType();
				String tmp = rs.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));

				tmp = rs.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);

				tmp = rs.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));

				pt.setIdAccordo(idAccordoLong);

				long idPortType = rs.getLong("id");
				pt.setId(idPortType);

				// Aggiungo azioni a port type
				this.readAzioniPortTypes(pt,con);
			}

			return pt;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public Vector<IDServizio> getIdServiziWithPortType(IDPortType idPT) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		String nomeMetodo = "getIdServiziWithPortType";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getIdServiziWithPortType");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		Vector<IDServizio> idServizi = new Vector<IDServizio>(); 
		try {

			//recupero idAccordo
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idPT.getIdAccordo(), con, this.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);
			stmt.setString(2, idPT.getNome());

			rs = stmt.executeQuery();
			while (rs.next()) {
				IDServizio idServizio = 
						new IDServizio(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"), 
								rs.getString("tipo_servizio"), rs.getString("nome_servizio"));
				idServizi.add(idServizio);
			}

			if(idServizi.size()<=0){
				throw new DriverRegistroServiziNotFound("Servizi non trovato che implementano il servizio "+idPT.getNome()+" dell'accordo di servizio "+idPT.getIdAccordo().toString());
			}
			else{
				return idServizi;
			}

		}catch(DriverRegistroServiziNotFound dNot){
			throw dNot;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	public Vector<IDServizio> getIdServiziWithAccordo(IDAccordo idAccordo,boolean checkPTisNull) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		String nomeMetodo = "getIdServiziWithPortType";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getIdServiziWithAccordo");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		Vector<IDServizio> idServizi = new Vector<IDServizio>(); 
		try {

			//recupero idAccordo
			long idAccordoLong = DBUtils.getIdAccordoServizioParteComune(idAccordo, con, this.tipoDB);
			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo=?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			if(checkPTisNull){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".port_type is null");
			}
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordoLong);

			rs = stmt.executeQuery();
			while (rs.next()) {
				IDServizio idServizio = 
						new IDServizio(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto"), 
								rs.getString("tipo_servizio"), rs.getString("nome_servizio"));
				idServizi.add(idServizio);
			}

			if(idServizi.size()<=0){
				throw new DriverRegistroServiziNotFound("Servizi non trovato che implementano l'accordo di servizio "+idAccordo.toString());
			}
			else{
				return idServizi;
			}

		}catch(DriverRegistroServiziNotFound dNot){
			throw dNot;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	// Controlla che esista solo un'azione, direttamente negli accordi e tra i port type con tale nome
	public boolean isUnicaAzioneInAccordi(String azione) throws DriverRegistroServiziException {
		String nomeMetodo = "isUnicaAzioneInAccordi";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String queryString;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("isUnicaAzioneInAccordi");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			// AZIONI direttamente negli accordi
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_AZIONI);
			sqlQueryObject.addSelectCountField("nome", "countAzioni");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_AZIONI+".nome=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, azione);
			rs = stmt.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt("countAzioni");
			}
			else{
				throw new Exception("Azione ["+azione+"] non trovata (rs.next fallita)");
			}
			rs.close();
			stmt.close();

			// AZIONI dei port types
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORT_TYPE_AZIONI);
			sqlQueryObject.addSelectCountField("nome", "countAzioni");
			sqlQueryObject.addWhereCondition(CostantiDB.PORT_TYPE_AZIONI+".nome=?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, azione);
			rs = stmt.executeQuery();
			if(rs.next()){
				count = count + rs.getInt("countAzioni");
			}
			else{
				throw new Exception("Azione ["+azione+"] non trovata (rs.next fallita pt)");
			}

			if(count<=0){
				throw new Exception("Azione ["+azione+"] non trovata");
			}else{
				return count==1;
			}

		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			// Chiudo statement and resultset
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				// ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}




	public boolean existsDocumento(String nome, String tipo, String ruolo, long idProprietario, ProprietariDocumento proprietarioDocumento) throws DriverRegistroServiziException {

		boolean exist = false;
		Connection connection;
		PreparedStatement stm = null;
		ResultSet rs = null;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsDocumento");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::existsDocumento] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.addWhereCondition("tipo = ?");
			sqlQueryObject.addWhereCondition("ruolo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idProprietario);
			stm.setString(2, proprietarioDocumento.toString());
			stm.setString(3, nome);
			stm.setString(4, tipo);
			stm.setString(5, ruolo);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();
		} catch (Exception e) {
			exist = false;
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			if (this.atomica) {
				try {
					connection.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}

		return exist;
	}

	public Documento getDocumento(String nome, String tipo, String ruolo, long idProprietario,boolean readBytes,ProprietariDocumento tipoProprietario) throws DriverRegistroServiziException {
		String nomeMetodo = "getDocumento";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getDocumento");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		try {
			String tipoProprietarioAsString = null;
			if(tipoProprietario!=null){
				tipoProprietarioAsString = tipoProprietario.toString();
			}
			long idDoc = DBUtils.getIdDocumento(nome, tipo, ruolo, idProprietario,con,this.tipoDB,tipoProprietarioAsString);
			return DriverRegistroServiziDB_LIB.getDocumento(idDoc, readBytes, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public Documento getDocumento(long idDocumento,boolean readBytes) throws DriverRegistroServiziException {
		String nomeMetodo = "getDocumento";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getDocumento");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		try {

			return DriverRegistroServiziDB_LIB.getDocumento(idDocumento, readBytes, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::" + nomeMetodo + "] Exception: " + se.getMessage());
		} finally {
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public List<Documento> serviziAllegatiList(int idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziAllegatiList";
		int idLista = Liste.SERVIZI_ALLEGATI;
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

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("serviziAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idServizio);
			stmt.setString(2,ProprietariDocumento.servizio.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("roulo",search,true,true),sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idServizio);
			stmt.setString(2,ProprietariDocumento.servizio.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_LIB.getDocumento(risultato.getLong("id"),false, con, this.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}




	public void validaStatoAccordoServizio(AccordoServizioParteComune as,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("AccordoServizio",as.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(as.getPrivato()==null || as.getPrivato()==false){
				if(as.getSoggettoReferente()!=null){
					IDSoggetto idS = new IDSoggetto(as.getSoggettoReferente().getTipo(),as.getSoggettoReferente().getNome());
					try{
						Soggetto s = this.getSoggetto(idS);
						if(s.getPrivato()!=null && s.getPrivato()){
							erroreValidazione.addErroreValidazione("soggetto referente ["+idS+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
						}
					}catch(DriverRegistroServiziNotFound dNot){}
				}
			}
			if(as.getServizioComposto()!=null){
				if(as.getServizioComposto().getIdAccordoCooperazione()>0){
					try{
						AccordoCooperazione ac = this.getAccordoCooperazione(as.getServizioComposto().getIdAccordoCooperazione());
						if(as.getPrivato()==null || as.getPrivato()==false){
							if(ac.getPrivato()!=null && ac.getPrivato()){
								erroreValidazione.addErroreValidazione("accordo di cooperazione ["+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
							}
						}
					}catch(DriverRegistroServiziNotFound dNot){}
				}
				if(as.getServizioComposto().sizeServizioComponenteList()>=2){
					for(int i=0; i<as.getServizioComposto().sizeServizioComponenteList(); i++){
						if(as.getServizioComposto().getServizioComponente(i).getId()>0){
							try{
								AccordoServizioParteSpecifica sc = this.getAccordoServizioParteSpecifica(as.getServizioComposto().getServizioComponente(i).getId());
								if(as.getPrivato()==null || as.getPrivato()==false){
									if(sc.getPrivato()!=null && sc.getPrivato()){
										erroreValidazione.addErroreValidazione("servizio componente ["+sc.getServizio().getTipo()+"/"+sc.getServizio().getNome()+"_"+
												sc.getServizio().getTipoSoggettoErogatore()+"/"+
												sc.getServizio().getNomeSoggettoErogatore()+"] con visibilita' privata, in un accordo di servizio con visibilita' pubblica");
									}
								}
							}catch(DriverRegistroServiziNotFound dNot){}
						}
					}
				}
			}


			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(as.getStatoPackage()) == false){

				// Validazione necessaria sia ad uno stato operativo che finale
				if(utilizzoAzioniDiretteInAccordoAbilitato==false){
					if(as.sizePortTypeList()==0){
						erroreValidazione.addErroreValidazione("non sono definiti Servizi");
					}
					for(int j=0; j<as.sizePortTypeList(); j++){
						if(as.getPortType(j).sizeAzioneList()==0){
							erroreValidazione.addErroreValidazione("servizio["+as.getPortType(j).getNome()+"] non possiede azioni");
						}
					}
				}else{
					if(as.sizePortTypeList()==0 && as.sizeAzioneList()==0 && as.getUtilizzoSenzaAzione()==false){
						erroreValidazione.addErroreValidazione("non sono definite ne Azioni (utilizzoSenzaAzione=false) ne Servizi");
					}
					if(as.sizePortTypeList()!=0){
						for(int j=0; j<as.sizePortTypeList(); j++){
							if(as.getPortType(j).sizeAzioneList()==0){
								erroreValidazione.addErroreValidazione("servizio["+as.getPortType(j).getNome()+"] non possiede azioni");
							}
						}
					}
					if(as.sizePortTypeList()!=0){
						for(int j=0; j<as.sizePortTypeList(); j++){
							PortType pt = as.getPortType(j);
							for(int k=0;k<pt.sizeAzioneList();k++){
								Operation op = pt.getAzione(k);
								ProfiloCollaborazione profiloCollaborazioneOP = op.getProfiloCollaborazione();
								if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(op.getProfAzione())){
									profiloCollaborazioneOP = pt.getProfiloCollaborazione();
									if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(pt.getProfiloPT())){
										profiloCollaborazioneOP = as.getProfiloCollaborazione();
									}
								}
								if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazioneOP) ||
										CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profiloCollaborazioneOP)	){
									if(op.getCorrelata()==null){
										// Verifico che esista un altra azione correlata a questa.
										boolean trovataCorrelazione = false;
										for(int verificaPTIndex=0; verificaPTIndex<as.sizePortTypeList(); verificaPTIndex++){
											PortType ptVerifica = as.getPortType(verificaPTIndex);
											for(int verificaOPIndex=0; verificaOPIndex<ptVerifica.sizeAzioneList(); verificaOPIndex++){
												Operation opVerifica = ptVerifica.getAzione(verificaOPIndex);
												if(opVerifica.getCorrelata()!=null && opVerifica.getCorrelata().equals(op.getNome())){
													// potenziale correlazione, verifico servizio
													if(opVerifica.getCorrelataServizio()==null){
														if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazioneOP) && ptVerifica.getNome().equals(pt.getNome())){
															// la correlazione per l'asincrono asimmetrico puo' avvenire sullo stesso port type
															trovataCorrelazione = true;
															break;
														}
													}
													else if(opVerifica.getCorrelataServizio().equals(pt.getNome())){
														trovataCorrelazione = true;
														break;
													}
												}
											}
										}
										if(trovataCorrelazione==false){
											erroreValidazione.addErroreValidazione("L'azione ["+op.getNome()+"] del servizio["+as.getPortType(j).getNome()+"] non risulta correlata da altre azioni");
										}
									}
								}
							}
						}
					}
				}

				if(StatiAccordo.finale.toString().equals(as.getStatoPackage())){

					String wsdlConcettuale = (as.getByteWsdlConcettuale()!=null ? new String(as.getByteWsdlConcettuale()) : null);
					String wsdlLogicoErogatore = (as.getByteWsdlLogicoErogatore()!=null ? new String(as.getByteWsdlLogicoErogatore()) : null);
					wsdlConcettuale = wsdlConcettuale!=null && !"".equals(wsdlConcettuale.trim().replaceAll("\n", "")) ? wsdlConcettuale : null;
					wsdlLogicoErogatore = wsdlLogicoErogatore!=null && !"".equals(wsdlLogicoErogatore.trim().replaceAll("\n", "")) ? wsdlLogicoErogatore : null;

					if(	wsdlConcettuale == null){
						erroreValidazione.addErroreValidazione("interfaccia WSDL Concettuale non definita");
					}
					if(	wsdlLogicoErogatore == null){
						erroreValidazione.addErroreValidazione("interfaccia WSDL LogicoErogatore non definita");
					}

					if(as.getServizioComposto()!=null){
						if(as.getServizioComposto().getIdAccordoCooperazione()<=0){
							erroreValidazione.addErroreValidazione("accordo di cooperazione (id) non definito");
						}else{
							try{
								AccordoCooperazione ac = this.getAccordoCooperazione(as.getServizioComposto().getIdAccordoCooperazione());
								if(StatiAccordo.finale.toString().equals(ac.getStatoPackage())==false){
									erroreValidazione.addErroreValidazione("accordo di cooperazione ["+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+"] in uno stato non finale ["+ac.getStatoPackage()+"]");
								}
							}catch(DriverRegistroServiziNotFound dNot){
								erroreValidazione.addErroreValidazione("accordo di cooperazione non definito");
							}
						}

						if(as.getServizioComposto().sizeSpecificaCoordinamentoList()<=0){
							erroreValidazione.addErroreValidazione("specifica di coordinamento non definita");
						}


						if(as.getServizioComposto().sizeServizioComponenteList()<=0){
							erroreValidazione.addErroreValidazione("servizi componenti non definiti");	
						}else{
							if(as.getServizioComposto().sizeServizioComponenteList()<2){
								erroreValidazione.addErroreValidazione("almeno 2 servizi componenti sono necessari per realizzare un servizio composto");	
							}else{
								for(int i=0; i<as.getServizioComposto().sizeServizioComponenteList(); i++){
									if(as.getServizioComposto().getServizioComponente(i).getIdServizioComponente()<=0){
										erroreValidazione.addErroreValidazione("servizio componente ["+i+"] (id) non definito");
									}else{
										try{
											AccordoServizioParteSpecifica sc = this.getAccordoServizioParteSpecifica(as.getServizioComposto().getServizioComponente(i).getIdServizioComponente());
											if(StatiAccordo.finale.toString().equals(sc.getStatoPackage())==false){
												erroreValidazione.addErroreValidazione("servizio componente ["+sc.getServizio().getTipo()+"/"+sc.getServizio().getNome()
														+"_"+sc.getServizio().getTipoSoggettoErogatore()+"/"+
														sc.getServizio().getNomeSoggettoErogatore()+"] in uno stato non finale ["+sc.getStatoPackage()+"]");
											}
										}catch(DriverRegistroServiziNotFound dNot){
											erroreValidazione.addErroreValidazione("servizio componente ["+i+"] non definito");
										}
									}
								}
							}
						}
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



	public void validaStatoAccordoCooperazione(AccordoCooperazione ac) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("AccordoCooperazione",ac.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(ac.getPrivato()==null || ac.getPrivato()==false){
				if(ac.getSoggettoReferente()!=null){
					IDSoggetto idS = new IDSoggetto(ac.getSoggettoReferente().getTipo(),ac.getSoggettoReferente().getNome());
					try{
						Soggetto s = this.getSoggetto(idS);
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
								Soggetto s = this.getSoggetto(partecipanti.getSoggettoPartecipante(i).getIdSoggetto());
								if(s.getPrivato()!=null && s.getPrivato()){
									erroreValidazione.addErroreValidazione("soggetto partecipante ["+s.getTipo()+"/"+s.getNome()+"] con visibilita' privata, in un accordo di cooperazione con visibilita' pubblica");
								}
							}catch(DriverRegistroServiziNotFound dNot){}
						}
						else if(idSoggettoPartecipante.getTipo()!=null && idSoggettoPartecipante.getNome()!=null){
							try{
								Soggetto s = this.getSoggetto(new IDSoggetto(idSoggettoPartecipante.getTipo(), idSoggettoPartecipante.getNome()));
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


	public void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("Servizio",servizio.getStatoPackage(),null);

		try{

			// Controlli di visibilita
			if(servizio.getPrivato()==null || servizio.getPrivato()==false){
				IDSoggetto idS = new IDSoggetto(servizio.getServizio().getTipoSoggettoErogatore(),servizio.getServizio().getNomeSoggettoErogatore());
				try{
					Soggetto s = this.getSoggetto(idS);
					if(s.getPrivato()!=null && s.getPrivato()){
						erroreValidazione.addErroreValidazione("soggetto erogatore ["+idS+"] con visibilita' privata, in un servizio con visibilita' pubblica");
					}
				}catch(DriverRegistroServiziNotFound dNot){}
				try{
					AccordoServizioParteComune as = this.getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
					if(as.getPrivato()!=null && as.getPrivato()){
						erroreValidazione.addErroreValidazione("accordo di servizio ["+servizio.getAccordoServizioParteComune()+"] con visibilita' privata, in un servizio con visibilita' pubblica");
					}
				}catch(DriverRegistroServiziNotFound dNot){}
			}	

			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(servizio.getStatoPackage()) == false){

				if(StatiAccordo.operativo.toString().equals(servizio.getStatoPackage())){
					try{
						AccordoServizioParteComune as = this.getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
						if(StatiAccordo.finale.toString().equals(as.getStatoPackage())==false && StatiAccordo.operativo.toString().equals(as.getStatoPackage())==false){
							erroreValidazione.addErroreValidazione("accordo di servizio riferito ["+this.idAccordoFactory.getUriFromAccordo(as)+"] possiede lo stato ["+as.getStatoPackage()+"]");
						}
					}catch(DriverRegistroServiziNotFound dNot){
						erroreValidazione.addErroreValidazione("accordo di servizio non definito");
					}
				}
				
				else if(StatiAccordo.finale.toString().equals(servizio.getStatoPackage())){
					try{
						AccordoServizioParteComune as = this.getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromUri(servizio.getAccordoServizioParteComune()));
						if(StatiAccordo.finale.toString().equals(as.getStatoPackage())==false){
							erroreValidazione.addErroreValidazione("accordo di servizio ["+this.idAccordoFactory.getUriFromAccordo(as)+"] in uno stato non finale ["+as.getStatoPackage()+"]");
						}
					}catch(DriverRegistroServiziNotFound dNot){
						erroreValidazione.addErroreValidazione("accordo di servizio non definito");
					}

					if(TipologiaServizio.CORRELATO.equals(servizio.getServizio().getTipologiaServizio())){
						String wsdlImplementativoFruitore = (servizio.getByteWsdlImplementativoFruitore()!=null ? new String(servizio.getByteWsdlImplementativoFruitore()) : null);
						wsdlImplementativoFruitore = wsdlImplementativoFruitore!=null && !"".equals(wsdlImplementativoFruitore.trim().replaceAll("\n", "")) ? wsdlImplementativoFruitore : null;
						if(	wsdlImplementativoFruitore == null){
							erroreValidazione.addErroreValidazione("WSDL Implementativo fruitore non definito");
						}
					}else{
						String wsdlImplementativoErogatore = (servizio.getByteWsdlImplementativoErogatore()!=null ? new String(servizio.getByteWsdlImplementativoErogatore()) : null);
						wsdlImplementativoErogatore = wsdlImplementativoErogatore!=null && !"".equals(wsdlImplementativoErogatore.trim().replaceAll("\n", "")) ? wsdlImplementativoErogatore : null;
						if(	wsdlImplementativoErogatore == null){
							erroreValidazione.addErroreValidazione("WSDL Implementativo erogatore non definito");
						}
					}


					// check connettore: un servizio puo' essere finale con jms: il check sara' poi al momento dell'esportazione nei package cnipa
					/*if(servizio.getConnettore()!=null && !CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(servizio.getConnettore().getTipo()) && 
							!CostantiDB.CONNETTORE_TIPO_HTTP.equals(servizio.getConnettore().getTipo()) && !CostantiDB.CONNETTORE_TIPO_HTTPS.equals(servizio.getConnettore().getTipo())){
						erroreValidazione.addErroreValidazione("Accordo di servizio parte specifica possiede un connettore ("+servizio.getConnettore().getTipo()+") non utilizzabile nella rete SPC");
					}
					else */
					if(servizio.getServizio().getConnettore()==null || CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(servizio.getServizio().getConnettore().getTipo())){
						// check connettore del soggetto erogatore: un servizio puo' essere finale con jms: il check sara' poi al momento dell'esportazione nei package cnipa
						Soggetto soggettoErogatore = this.getSoggetto(new IDSoggetto(servizio.getServizio().getTipoSoggettoErogatore(),servizio.getServizio().getNomeSoggettoErogatore()));
						/*if(soggettoErogatore.getConnettore()!=null && !CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(soggettoErogatore.getConnettore().getTipo()) && 
								!CostantiDB.CONNETTORE_TIPO_HTTP.equals(soggettoErogatore.getConnettore().getTipo()) && !CostantiDB.CONNETTORE_TIPO_HTTPS.equals(soggettoErogatore.getConnettore().getTipo())){
							erroreValidazione.addErroreValidazione("Accordo di servizio parte specifica non possiede un connettore e soggetto erogatore "+servizio.getTipoSoggettoErogatore()+"/"+servizio.getNomeSoggettoErogatore()+" possiede un connettore ("+soggettoErogatore.getConnettore().getTipo()+") non utilizzabile nella rete SPC");
						}
						else */
						if(soggettoErogatore.getConnettore()==null || CostantiDB.CONNETTORE_TIPO_DISABILITATO.equals(soggettoErogatore.getConnettore().getTipo()) ){
							erroreValidazione.addErroreValidazione("Sia l'Accordo di servizio parte specifica che il soggetto erogatore non possiedono un connettore");
						}
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


	public void validaStatoFruitoreServizio(Fruitore fruitore,AccordoServizioParteSpecifica serv) throws ValidazioneStatoPackageException{

		ValidazioneStatoPackageException erroreValidazione =
				new ValidazioneStatoPackageException("FruitoreServizio",fruitore.getStatoPackage(),null);

		try{

			// Controlli di stato
			if(StatiAccordo.bozza.toString().equals(fruitore.getStatoPackage()) == false){

				if(StatiAccordo.operativo.toString().equals(fruitore.getStatoPackage())){
					if(StatiAccordo.finale.toString().equals(serv.getStatoPackage())==false && StatiAccordo.operativo.toString().equals(serv.getStatoPackage())==false){
						erroreValidazione.addErroreValidazione("servizio riferito ["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] erogato dal soggetto ["+
							serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] possiede lo stato ["+serv.getStatoPackage()+"]");
					}
				}
				
				else if(StatiAccordo.finale.toString().equals(fruitore.getStatoPackage())){

					if(StatiAccordo.finale.toString().equals(serv.getStatoPackage())==false){
						erroreValidazione.addErroreValidazione("servizio ["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] erogato dal soggetto ["+
								serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] in uno stato non finale ["+serv.getStatoPackage()+"]");
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







	public void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			boolean isUpdate,boolean isServizioCorrelato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws DriverRegistroServiziException{
		/*
		 * Controllo che non esistano 2 servizi con stesso soggetto erogatore e accordo di servizio 
		 * che siano entrambi correlati o non correlati. 
		 * Al massimo possono esistere 2 servizi di uno stesso accordo erogati da uno stesso soggetto, 
		 * purche' siano uno correlato e uno no. 
		 * Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato che sto
		 * cambiando 
		 */

		String tmpServCorr = CostantiRegistroServizi.DISABILITATO.toString();
		if(isServizioCorrelato){
			tmpServCorr = CostantiRegistroServizi.ABILITATO.toString();
		}
		String s = "servizio";
		if (isServizioCorrelato) {
			s = "servizio correlato";
		}

		// se il servizio non definisce port type effettuo controllo che non
		// esistano 2 servizi con stesso soggetto,
		// accordo e servizio correlato
		if (portType == null || "-".equals(portType)) {

			if(isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto){

				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato
				// Se tipoOp = change, devo fare attenzione a non escludere il servizio selezionato

				int idAccordoServizioParteSpecificaAlreadyExists = 
						this.getServizioWithSoggettoAccordoServCorr(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								idAccordoServizioParteSpecifica, tmpServCorr);

				boolean addError = ((!isUpdate) && (idAccordoServizioParteSpecificaAlreadyExists > 0));

				boolean changeError = false;
				if (isUpdate && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}

				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa l'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}

			}

		} else {

			if(isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto){

				// Controllo che non esistano 2 servizi con stesso soggetto,
				// accordo e servizio correlato e port-type

				int idAccordoServizioParteSpecificaAlreadyExists =  
						this.getServizioWithSoggettoAccordoServCorrPt(idSoggettoErogatoreLong, idAccordoServizioParteComuneLong, 
								idAccordoServizioParteSpecifica, tmpServCorr, portType);

				boolean addError = ((!isUpdate) && (idAccordoServizioParteSpecificaAlreadyExists > 0));

				boolean changeError = false;
				if (isUpdate && (idAccordoServizioParteSpecificaAlreadyExists > 0)) {
					changeError = (idAccordoServizioParteSpecificaLong != idAccordoServizioParteSpecificaAlreadyExists);
				}

				if (addError || changeError) {
					throw new DriverRegistroServiziException("Esiste gi&agrave; un " + s + " del Soggetto "+idSoggettoErogatore+
							" che implementa il servizio "+portType+" dell'accordo selezionato ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune)+"]");
				}

			}
		}
	}






	/**
	 * Restituisce gli accordi compatibili
	 * @param ricerca
	 * @return accordiCompatibiliList
	 * @throws DriverRegistroServiziException
	 */
	public List<AccordoServizioParteComune> accordiCompatibiliList(ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCompatibiliList";
		int idLista = Liste.ACCORDI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.log.debug("search : " + search);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> lista = new ArrayList<AccordoServizioParteComune>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiCompatibiliList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlquery = DriverRegistroServiziDB_LIB.getSQLRicercaAccordiValidi();
			sqlquery.setANDLogicOperator(true);

			ISQLQueryObject sqlQueryObjectSoggetti = null;
			if (!search.equals("")) {
				sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggetti.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggetti.addWhereCondition(true,
						sqlQueryObjectSoggetti.getWhereLikeCondition("nome_soggetto", search, true, true),
						CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
			}


			if (!search.equals("")) {
				//query con search
				sqlquery.addSelectCountField("*", "cont");
				sqlquery.addWhereCondition(false, 
						sqlquery.getWhereLikeCondition("nome", search, true, true),
						sqlquery.getWhereLikeCondition("versione", search, true, true),
						sqlquery.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				queryString = sqlquery.createSQLQuery();
			} else {
				sqlquery.addSelectCountField("*", "cont");
				queryString = sqlquery.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			//resetto query
			sqlquery = DriverRegistroServiziDB_LIB.getSQLRicercaAccordiValidi();
			sqlquery.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search

				sqlquery.addSelectField("id");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("id_referente");
				sqlquery.addSelectField("versione");
				sqlquery.addSelectField("stato");
				sqlquery.addWhereCondition(false, 
						sqlquery.getWhereLikeCondition("nome", search, true, true),
						sqlquery.getWhereLikeCondition("versione", search, true, true),
						sqlquery.getWhereExistsCondition(false, sqlQueryObjectSoggetti));
				sqlquery.addOrderBy("nome");
				sqlquery.addOrderBy("versione");
				sqlquery.addOrderBy("id_referente");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			} else {
				// senza search
				sqlquery.addSelectField("id");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("id_referente");
				sqlquery.addSelectField("versione");
				sqlquery.addSelectField("stato");
				sqlquery.addOrderBy("nome");
				sqlquery.addOrderBy("versione");
				sqlquery.addOrderBy("id_referente");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			}


			this.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			AccordoServizioParteComune accordo = null;

			while (risultato.next()) {

				accordo = new AccordoServizioParteComune();

				accordo.setId(risultato.getLong("id"));
				accordo.setNome(risultato.getString("nome"));
				accordo.setDescrizione(risultato.getString("descrizione"));
				accordo.setStatoPackage(risultato.getString("stato"));
				accordo.setVersione(risultato.getString("versione"));

				// Soggetto referente
				if(risultato.getInt("id_referente")>0) {
					Soggetto soggRef = getSoggetto(new Long(risultato.getInt("id_referente")),con);
					IdSoggetto assr = new IdSoggetto();
					assr.setTipo(soggRef.getTipo());
					assr.setNome(soggRef.getNome());
					accordo.setSoggettoReferente(assr);
				}
				lista.add(accordo);

				this.readAccordoServizioComposto(accordo, con);

			}


		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
		return lista;
	}

	public List<PortType> accordiPorttypeCompatibiliList(int idAccordo,boolean isErogazione, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiPorttypeCompatibiliList";
		int idLista = Liste.ACCORDI_PORTTYPE;
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

		ArrayList<PortType> lista = new ArrayList<PortType>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("accordiPorttypeCompatibiliList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlquery = DriverRegistroServiziDB_LIB.getSQLRicercaServiziValidiByIdAccordo(isErogazione);
			sqlquery.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlquery.addSelectCountField("*", "cont");
				sqlquery.addWhereLikeCondition("nome", search, true, true);
				sqlquery.setANDLogicOperator(true);
				queryString = sqlquery.createSQLQuery();
			} else {
				sqlquery.addSelectCountField("*", "cont");
				queryString = sqlquery.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();


			//resetto query
			sqlquery = DriverRegistroServiziDB_LIB.getSQLRicercaServiziValidiByIdAccordo(isErogazione);
			sqlquery.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search

				sqlquery.addSelectField("id_accordo");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("profilo_collaborazione");
				sqlquery.addSelectField("filtro_duplicati");
				sqlquery.addSelectField("conferma_ricezione");
				sqlquery.addSelectField("identificativo_collaborazione");
				sqlquery.addSelectField("consegna_in_ordine");
				sqlquery.addSelectField("scadenza");
				sqlquery.addSelectField("profilo_pt");
				sqlquery.addSelectField("soap_style");
				sqlquery.addSelectField("id");

				sqlquery.addWhereLikeCondition("nome", search, true, true);
				sqlquery.setANDLogicOperator(true);
				sqlquery.addOrderBy("nome");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			} else {
				// senza search
				sqlquery.addSelectField("id_accordo");
				sqlquery.addSelectField("nome");
				sqlquery.addSelectField("descrizione");
				sqlquery.addSelectField("profilo_collaborazione");
				sqlquery.addSelectField("filtro_duplicati");
				sqlquery.addSelectField("conferma_ricezione");
				sqlquery.addSelectField("identificativo_collaborazione");
				sqlquery.addSelectField("consegna_in_ordine");
				sqlquery.addSelectField("scadenza");
				sqlquery.addSelectField("profilo_pt");
				sqlquery.addSelectField("soap_style");
				sqlquery.addSelectField("id");

				sqlquery.addOrderBy("nome");
				sqlquery.setSortType(true);
				sqlquery.setLimit(limit);
				sqlquery.setOffset(offset);
				queryString = sqlquery.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idAccordo);
			risultato = stmt.executeQuery();

			PortType pt;
			while (risultato.next()) {
				pt = new PortType();

				String tmp = risultato.getString("nome");
				pt.setNome(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("descrizione");
				pt.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_collaborazione");
				pt.setProfiloCollaborazione(DriverRegistroServiziDB_LIB.getEnumProfiloCollaborazione(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("filtro_duplicati");
				pt.setFiltroDuplicati(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("conferma_ricezione");
				pt.setConfermaRicezione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("identificativo_collaborazione");
				pt.setIdCollaborazione(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("consegna_in_ordine");
				pt.setConsegnaInOrdine(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(((tmp == null || tmp.equals("")) ? null : tmp)));
				tmp = risultato.getString("scadenza");
				pt.setScadenza(((tmp == null || tmp.equals("")) ? null : tmp));
				tmp = risultato.getString("profilo_pt");
				if (tmp == null || tmp.equals(""))
					pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				else
					pt.setProfiloPT(tmp);
				tmp = risultato.getString("soap_style");
				pt.setStyle(DriverRegistroServiziDB_LIB.getEnumBindingStyle(((tmp == null || tmp.equals("")) ? null : tmp)));
				pt.setIdAccordo(risultato.getLong("id_accordo"));
				long idPortType = risultato.getLong("id");
				pt.setId(idPortType);

				this.readAzioniPortTypes(pt, con);

				lista.add(pt);				
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}
			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}



	public List<PortaDominio> porteDominioWithSubject(String subject)throws DriverRegistroServiziException {
		String nomeMetodo = "porteDominioWithSubject";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDominio> lista = new ArrayList<PortaDominio>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("porteDominioWithSubject");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(subject);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("subject");
			Enumeration<String> keys = hashSubject.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				String value = hashSubject.get(key);
				sqlQueryObject.addWhereLikeCondition("subject", "/"+Utilities.formatKeySubject(key)+"="+Utilities.formatValueSubject(value)+"/", true, false);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			risultato = stmt.executeQuery();

			PortaDominio pdd;
			while (risultato.next()) {

				// Possono esistere piu' pdd che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
				String subjectPotenziale =  risultato.getString("subject");
				if(Utilities.sslVerify(subjectPotenziale, subject)){
					pdd=this.getPortaDominio(risultato.getString("nome"));
					lista.add(pdd);
				}
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	public List<String> fruzioniWithClientAuthAbilitato(String nomePdD)throws DriverRegistroServiziException {
		String nomeMetodo = "fruzioniWithClientAuthAbilitato";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<String> lista = new ArrayList<String>();

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("fruzioniWithClientAuthAbilitato");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObjectInner = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectInner.addFromTable(this.tabellaSoggetti);
			sqlQueryObjectInner.addSelectField("id");
			sqlQueryObjectInner.addWhereCondition("server=?");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
			sqlQueryObject.addWhereCondition("client_auth=?");
			sqlQueryObject.addWhereINSelectSQLCondition(false, "id_soggetto", sqlQueryObjectInner);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			this.log.debug("QUERY ["+queryString+"] 1["+CostantiRegistroServizi.ABILITATO+"] 2["+nomePdD+"]");
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, DriverRegistroServiziDB_LIB.getValue(CostantiRegistroServizi.ABILITATO));
			stmt.setString(2, nomePdD);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Soggetto fruitore = this.getSoggetto(risultato.getLong("id_soggetto"));
				AccordoServizioParteSpecifica servizio = this.getAccordoServizioParteSpecifica(risultato.getLong("id_servizio"));
				lista.add("Fruizione da parte del soggetto "+fruitore.getTipo()+"/"+fruitore.getNome()+" verso l'accordo di servizio parte specifica "+this.idAccordoFactory.getUriFromAccordo(servizio)+
						", servizio "+servizio.getServizio().getTipo()+"/"+servizio.getServizio().getNome());
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (error && this.atomica) {
					this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.atomica) {
					this.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}


	public void createServizioApplicativoAutorizzato(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore, String nomeServizioApplicativo) throws DriverRegistroServiziException{
		Connection con = null;
		PreparedStatement stmt = null;

		this.log.debug("createServizioApplicativoAutorizzato...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("createServizioApplicativoAutorizzato");
			else
				con = this.globalConnection;

			long idSoggettoLong = DBUtils.getIdSoggetto(idFruitore.getNome(), idFruitore.getTipo(), con, this.tipoDB);
			if(idSoggettoLong<=0){
				throw new Exception("Soggetto Fruitore ["+idFruitore.toString()+"] non esistente");
			}
			long idAccordoServizioParteSpecificaLong = this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica,false).getId();
			if(idAccordoServizioParteSpecificaLong<=0){
				throw new Exception("AccordoServizioParteSpecifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
			}
			long idServizioApplicativo = DBUtils.getIdServizioApplicativo(nomeServizioApplicativo, idFruitore.getTipo(), idFruitore.getNome(), con, this.tipoDB);
			if(idServizioApplicativo<=0){
				throw new Exception("ServizioApplicativo ["+nomeServizioApplicativo+"] del soggetto ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addInsertTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addInsertField("id_fruitore", "?");
			sqlQueryObject.addInsertField("id_servizio", "?");
			sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
			String queryString = sqlQueryObject.createSQLInsert();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoLong);
			stmt.setLong(2, idAccordoServizioParteSpecificaLong);
			stmt.setLong(3, idServizioApplicativo);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new DriverRegistroServiziException("createServizioApplicativoAutorizzato error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}
	
	
	public void deleteServizioApplicativoAutorizzato(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore, String nomeServizioApplicativo) throws DriverRegistroServiziException{
		Connection con = null;
		PreparedStatement stmt = null;

		this.log.debug("deleteServizioApplicativoAutorizzato...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("deleteServizioApplicativoAutorizzato");
			else
				con = this.globalConnection;

			long idSoggettoLong = DBUtils.getIdSoggetto(idFruitore.getNome(), idFruitore.getTipo(), con, this.tipoDB);
			if(idSoggettoLong<=0){
				throw new Exception("Soggetto Fruitore ["+idFruitore.toString()+"] non esistente");
			}
			long idAccordoServizioParteSpecificaLong = this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica,false).getId();
			if(idAccordoServizioParteSpecificaLong<=0){
				throw new Exception("AccordoServizioParteSpecifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
			}
			long idServizioApplicativo = DBUtils.getIdServizioApplicativo(nomeServizioApplicativo, idFruitore.getTipo(), idFruitore.getNome(), con, this.tipoDB);
			if(idServizioApplicativo<=0){
				throw new Exception("ServizioApplicativo ["+nomeServizioApplicativo+"] del soggetto ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addWhereCondition("id_fruitore=?");
			sqlQueryObject.addWhereCondition("id_servizio=?");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoLong);
			stmt.setLong(2, idAccordoServizioParteSpecificaLong);
			stmt.setLong(3, idServizioApplicativo);
			stmt.executeUpdate();
			stmt.close();
			
		}catch(Exception e){
			throw new DriverRegistroServiziException("deleteServizioApplicativoAutorizzato error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}
		

	/**
	 *  Ritorna gli identificatori dei soggetti che rispettano il parametro di ricerca
	 * 
	 * @param idAccordoServizioParteSpecifica idAccordoServizioParteSpecifica
	 * @return Una lista di ID dei soggetti trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	public List<IDServizioApplicativo> getAllIdServiziApplicativiAutorizzati(IDAccordo idAccordoServizioParteSpecifica, IDSoggetto idFruitore) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdServiziApplicativiAutorizzati...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = this.getConnectionFromDatasource("getAllIdServiziApplicativiAutorizzati");
			else
				con = this.globalConnection;

			long idSoggettoLong = DBUtils.getIdSoggetto(idFruitore.getNome(), idFruitore.getTipo(), con, this.tipoDB);
			if(idSoggettoLong<=0){
				throw new Exception("Soggetto Fruitore ["+idFruitore.toString()+"] non esistente");
			}
			long idAccordoServizioParteSpecificaLong = this.getAccordoServizioParteSpecifica(idAccordoServizioParteSpecifica,false).getId();
			if(idAccordoServizioParteSpecificaLong<=0){
				throw new Exception("AccordoServizioParteSpecifica ["+idAccordoServizioParteSpecifica.toString()+"] non esistente");
			}

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.POLITICHE_SICUREZZA);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition("id_fruitore = ?");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.POLITICHE_SICUREZZA+".id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			stm.setLong(indexStmt,idSoggettoLong);
			indexStmt++;
			stm.setLong(indexStmt,idAccordoServizioParteSpecificaLong);
			indexStmt++;

			rs = stm.executeQuery();
			Vector<IDServizioApplicativo> idServiziApplicativi = new Vector<IDServizioApplicativo>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(idS);
				idSA.setNome(rs.getString("nome"));
				idServiziApplicativi.add(idSA);
			}
			if(idServiziApplicativi.size()==0){
				throw new DriverRegistroServiziNotFound("ServiziApplicativi non trovati per il fruitore ["+idFruitore+"] dell'accordo di servizio parte specifica ["+idAccordoServizioParteSpecifica+"]");
			}else{
				return idServiziApplicativi;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdServiziApplicativiAutorizzati error",e);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessione al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore
			}

		}
	}

}

