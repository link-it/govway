/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.*;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.RicercaTipologiaErogazione;
import org.openspcoop2.core.config.constants.RicercaTipologiaFruizione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaProtocolProperty;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.TipologiaServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.DBProtocolPropertiesUtils;
import org.openspcoop2.core.mapping.ProprietariProtocolProperty;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterException;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * Contiene l'implementazione dell'interfaccia {@link CostantiConfigurazione}.
 * Viene definito un 'reader' della configurazione dell'infrastruttura
 * OpenSPCoop.
 * <p>
 * Sono forniti metodi per la lettura della configurazione la quale contiene i
 * seguenti soggetti :
 * <ul>
 * <li>Porte Delegate
 * <li>Porte Applicative
 * <li>SIL-Mittenti
 * <li>Porta di Dominio
 * </ul>get
 * 
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverConfigurazioneDB extends BeanUtilities
implements IDriverConfigurazioneGet, IDriverConfigurazioneCRUD, IDriverWS, IMonitoraggioRisorsa {

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
	public boolean isAtomica() {
		return this.atomica;
	}

	/** Logger utilizzato per debug. */
	protected org.slf4j.Logger log = null;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;
	public String getTipoDB() {
		return this.tipoDB;
	}

	/** Tabella soggetti */
	String tabellaSoggetti = CostantiDB.SOGGETTI;
	
	private boolean useSuperUser = true;
	public boolean isUseSuperUser() {
		return this.useSuperUser;
	}
	public void setUseSuperUser(boolean useSuperUser) {
		this.useSuperUser = useSuperUser;
	}

	
	/* ******** COSTRUTTORI e METODI DI RELOAD ******** */

	public DriverConfigurazioneDB() {
	}
	/**
	 * Viene chiamato in causa per istanziare il contesto di unmarshall
	 * all'istanziazione dell'oggetto, ed inoltre viene effettuato l'unmarshall
	 * del file di configurazione.
	 * 
	 * @param nomeDataSource
	 *                Nome del Datasource da utilizzare per prelevare le
	 *                connessioni
	 * @param context
	 *                Contesto di lookup del Datasource
	 * 
	 */
	public DriverConfigurazioneDB(String nomeDataSource, java.util.Properties context,String tipoDB) {
		this(nomeDataSource,context,null,tipoDB);
	}
	public DriverConfigurazioneDB(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDB) {
		this(nomeDataSource,context,alog,tipoDB,false);
	}
	public DriverConfigurazioneDB(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDB, boolean tabellaSoggettiPDD) {
		initDriverConfigurazioneDB(nomeDataSource, context, alog, tipoDB, tabellaSoggettiPDD, false, false);
	}
	public DriverConfigurazioneDB(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDB, boolean tabellaSoggettiPDD,
			boolean useOp2UtilsDatasource, boolean bindJMX) {
		initDriverConfigurazioneDB(nomeDataSource, context, alog, tipoDB, tabellaSoggettiPDD, useOp2UtilsDatasource, bindJMX);
	}

	public void initDriverConfigurazioneDB(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDB, 
			boolean tabellaSoggettiPDD, boolean useOp2UtilsDatasource, boolean bindJMX) {
		try {
			if(alog==null)
				this.log = LoggerWrapperFactory.getLogger(CostantiConfigurazione.DRIVER_DB_LOGGER);
			else{
				this.log = alog;
				DriverConfigurazioneDB_LIB.initStaticLogger(this.log);
			}

			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDB);
				try{
					this.datasource = DataSourceFactory.newInstance(nomeDataSource, context, dsParams);
				}catch(UtilsAlreadyExistsException exists){
					this.datasource = DataSourceFactory.getInstance(nomeDataSource); 
					if(this.datasource==null){
						throw new Exception("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
					}
				}
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				this.datasource = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
			if (this.datasource != null)
				this.create = true;
		} catch (Exception ne) {
			this.create = false;
			this.log.error("Impossibile recuperare il context: " + ne.getMessage(),ne);
		}

		if (tipoDB == null) {
			this.log.error("Il tipoDatabase non puo essere null.");
			this.create = false;
		}

		this.atomica = true;
		this.tipoDB = tipoDB;
		if(tabellaSoggettiPDD){
			this.tabellaSoggetti = CostantiDB.SOGGETTI_PDD;
		}else{
			this.tabellaSoggetti = CostantiDB.SOGGETTI;
		}
		// Setto il tipoDB anche in DriverConfigurazioneDB_LIB
		DriverConfigurazioneDB_LIB.setTipoDB(tipoDB);
		DriverConfigurazioneDB_LIB.setTabellaSoggetti(this.tabellaSoggetti);
	}

	public DriverConfigurazioneDB(Connection connection,String tipoDB) throws DriverConfigurazioneException {
		this(connection,null,tipoDB);
	}
	public DriverConfigurazioneDB(Connection connection,Logger alog,String tipoDB) throws DriverConfigurazioneException {
		this(connection,alog,tipoDB,false);
	}
	public DriverConfigurazioneDB(Connection connection,Logger alog,String tipoDB,boolean tabellaSoggettiPDD) throws DriverConfigurazioneException {

		if(alog==null)
			this.log = LoggerWrapperFactory.getLogger(CostantiConfigurazione.DRIVER_DB_LOGGER);
		else{
			this.log = alog;
			DriverConfigurazioneDB_LIB.initStaticLogger(this.log);
		}

		if (connection == null) {
			this.create = false;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::DriverConfigurazioneDB(Connection con, Properties context) La connection non puo essere null.");
		}

		if (tipoDB == null) {
			this.create = false;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::DriverConfigurazioneDB(Connection ) Il tipoDatabase non puo essere null.");
		}

		// InitialContext initCtx = new InitialContext(context);
		this.globalConnection = connection;
		this.create = true;
		this.atomica = false;
		this.tipoDB = tipoDB;
		if(tabellaSoggettiPDD){
			this.tabellaSoggetti = CostantiDB.SOGGETTI_PDD;
		}else{
			this.tabellaSoggetti = CostantiDB.SOGGETTI;
		}
		// Setto il tipoDB anche in DriverConfigurazioneDB_LIB
		DriverConfigurazioneDB_LIB.setTipoDB(tipoDB);
		DriverConfigurazioneDB_LIB.setTabellaSoggetti(this.tabellaSoggetti);

	}

	
	
	public Connection getConnection(String methodName) throws DriverConfigurazioneException{
		Connection con = null;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(methodName);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConnection] Exception accedendo al datasource :" + e.getMessage(),e);

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
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasource).getWrappedConnection(null, "DriverConfigurazione."+methodName);
		}
		else{
			return this.datasource.getConnection();
		}
	}

	
	
	public List<List<Object>> readCustom(ISQLQueryObject sqlQueryObject, List<Class<?>> returnTypes, List<JDBCObject> paramTypes) throws DriverConfigurazioneException
	{
		Connection con = null;
		try {

			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = getConnectionFromDatasource("readCustom");
			else
				con = this.globalConnection;
		
			return DBUtils.readCustom(this.log, con, this.tipoDB, sqlQueryObject, returnTypes, paramTypes);
			
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::readCustom]: " + se.getMessage(),se);
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
	
	
	
	
	public IDSoggetto getIdSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getIdSoggetto(idSoggetto,null);
	}
	public IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

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
				con = getConnectionFromDatasource("getIdSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("DriverConfigurazioneDB::getIdSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DriverConfigurazioneDB_LIB.formatSQLString(sqlQuery, idSoggetto));
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
				throw new DriverConfigurazioneNotFound("Nessun risultato trovat eseguendo: "+DriverConfigurazioneDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			}

			return idSoggettoObject;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getIdSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getIdSoggetto] Exception: " + se.getMessage(),se);
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
	
	
	
	
	
	/**
	 * Restituisce Il soggetto identificato da <var>idSoggetto</var>
	 * 
	 * @param aSoggetto
	 *                Identificatore di un soggetto
	 * @return Il Soggetto identificato dal parametro.
	 * 
	 */
	@Override
	public Soggetto getSoggetto(IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if ((aSoggetto == null) || (aSoggetto.getNome() == null) || (aSoggetto.getTipo() == null)) {
			throw new DriverConfigurazioneException("[getSoggetto] Parametri Non Validi");
		}

		// Devi cercare un soggetto con quel tipo e nome (la chiave univoca e'
		// formata da tipo+nome)
		//
		// 1. deve ritornare il soggetto con tipo, nome e dominio
		// registrato e descrizione.
		// 3. deve essere impostato anche l'eventuale abilita' di router.
		/*
		 * Soggetto sog = new Soggetto();
		 * sog.setDescrizione(descrizione);
		 * sog.setIdentificativoPorta(identificativoPorta); sog.setNome(nome);
		 * sog.setTipo(tipo); sog.setRouter(boolean);
		 * 
		 */
		Soggetto Soggetto = null;

		Connection con = null;

		String nomeSogg = aSoggetto.getNome();
		String tipoSogg = aSoggetto.getTipo();
		long idSoggetto= -1;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSoggetto(idSoggetto)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			idSoggetto=DBUtils.getIdSoggetto(nomeSogg, tipoSogg, con, this.tipoDB,this.tabellaSoggetti);
			if(idSoggetto==-1)
				throw new DriverConfigurazioneNotFound("Soggetto ["+aSoggetto.toString()+"] non esistente");

		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (CoreException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] DriverException: " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception: " + se.getMessage(),se);
		}finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}


		Soggetto=this.getSoggetto(idSoggetto);

		if(Soggetto==null){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
		}

		return Soggetto;
	}

	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 1");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDSoggetto(1, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
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
	 * Aggiorna un Soggetto e il Connettore con i nuovi dati passati
	 * 
	 * @param soggetto
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 2");
			// UPDATE soggetto
			DriverConfigurazioneDB_LIB.CRUDSoggetto(2, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}finally {

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
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDSoggetto type = 3");
			// DELETE soggetto
			DriverConfigurazioneDB_LIB.CRUDSoggetto(3, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}  catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}finally {

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
	 * @throws DriverConfigurazioneException
	 */
	public void createConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 1");
			// creo connettore
			DriverConfigurazioneDB_LIB.CRUDConnettore(1, connettore, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
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
	 * @throws DriverConfigurazioneException
	 */
	public void updateConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 2");
			// update connettore
			DriverConfigurazioneDB_LIB.CRUDConnettore(2, connettore, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
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
	 * @throws DriverConfigurazioneException
	 */
	public void deleteConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConnettore type = 3");
			// delete connettore
			DriverConfigurazioneDB_LIB.CRUDConnettore(3, connettore, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
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
	 * Restituisce il soggetto configurato come router, se esiste nella Porta di
	 * Dominio un soggetto registrato come Router
	 * 
	 * @return il soggetto configurato come router, se esiste nella Porta di
	 *         Dominio un soggetto registrato come Router
	 * 
	 */
	@Override
	public Soggetto getRouter() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		Soggetto Soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getRouter");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		long idRouter = -1;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("is_router = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setInt(1, CostantiDB.TRUE);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			if (rs.next()) {
				idRouter = rs.getLong("id");
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRouter] Non esiste un Soggetto Router.");
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] Exception: " + se.getMessage(),se);
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

		Soggetto=this.getSoggetto(idRouter);
		// e' sicuramente un router
		Soggetto.setRouter(true);
		return Soggetto;
	}

	public List<IDSoggetto> getSoggettiWithSuperuser(String user) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDSoggetto> idSoggetti = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSoggettiWithSuperuser");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			List<IDSoggetto> idTrovati = new ArrayList<IDSoggetto>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("superuser = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, user);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			while (rs.next()) {
				IDSoggetto id = new IDSoggetto();
				id.setTipo(rs.getString("tipo_soggetto"));
				id.setNome(rs.getString("nome_soggetto"));
				idTrovati.add(id);
			}

			if(idTrovati.size()>0){
				idSoggetti =  new ArrayList<IDSoggetto>();
				idSoggetti.addAll(idTrovati);
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] Exception: " + se.getMessage(),se);
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

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public List<IDSoggetto> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDSoggetto> soggettiVirtuali = new ArrayList<IDSoggetto>();
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			//stm.setString(1, "");
			//stm.setString(2, "");
			rs = stm.executeQuery();

			while (rs.next()) {
				IDSoggetto soggettoVirtuale = new IDSoggetto(rs.getString("tipo_soggetto_virtuale") , rs.getString("nome_soggetto_virtuale"));
				this.log.info("aggiunto Soggetto " + soggettoVirtuale + " alla lista dei Soggetti Virtuali");
				soggettiVirtuali.add(soggettoVirtuale);
			}
			rs.close();
			stm.close();

			if(soggettiVirtuali.size()==0){
				throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Soggetti virtuali non esistenti");
			}

			this.log.info("aggiunti " + soggettiVirtuali.size() + " soggetti alla lista dei Soggetti Virtuali");
			return soggettiVirtuali;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SqlException: " + se.getMessage(), se);
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("Exception: " + se.getMessage(), se);
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

	}


	@Override
	public IDPortaDelegata getIDPortaDelegata(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
	
		String nomeMetodo = "getIDPortaDelegata";
		
		if (nome == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"] Parametro Non Valido");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			IDPortaDelegata idPD = null;
			if (rs.next()) {
				
				idPD = new IDPortaDelegata();
				
				idPD.setNome(rs.getString("nome_porta"));
				
				IdentificativiFruizione idFruizione = new IdentificativiFruizione();
				
				try{
					long idSoggProprietario = rs.getLong("id_soggetto");
					IDSoggetto idSoggettoProprietario = this.getIdSoggetto(idSoggProprietario,con);
					idFruizione.setSoggettoFruitore(idSoggettoProprietario);
				} catch (Exception se) {
					throw new Exception(se.getMessage()); // mappo NotFound in una eccezione generica. Il Soggetto proprietario deve esistere
				}
				
				String nomeSoggettoErogatore = rs.getString("nome_soggetto_erogatore");
				String tipoSoggettoErogatore = rs.getString("tipo_soggetto_erogatore");
				long idSoggettoErogatoreDB = rs.getLong("id_soggetto_erogatore");
				long idSoggErogatore=-1;
				if( (idSoggettoErogatoreDB==-2) || (idSoggettoErogatoreDB>0) ){
					idSoggErogatore = idSoggettoErogatoreDB;
				}
				else{
					try {
						idSoggErogatore = DBUtils.getIdSoggetto(nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.tipoDB,this.tabellaSoggetti);
					} catch (CoreException e) {
						this.log.debug(e.getMessage(),e);
					}
				}
				IDSoggetto idSoggettoErogatore = null;
				if(idSoggErogatore>0){
					idSoggettoErogatore = new IDSoggetto();
					idSoggettoErogatore.setTipo(tipoSoggettoErogatore);
					idSoggettoErogatore.setNome(nomeSoggettoErogatore);
				}else{
					throw new DriverConfigurazioneException("Soggetto Erogatore della Porta Delegata ["+nome+"] non presente.");
				}

				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("nome_servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");
				long idServizioDB = rs.getLong("id_servizio");
				long idServizio=-1;
				if( (idServizioDB==-2) || (idServizioDB>0) ){
					idServizio = idServizioDB;
				}
				else{
					try {
						idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.tipoDB,this.tabellaSoggetti);
					} catch (Exception e) {
						// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
						// in un database che non ' quello della controlstation ma quello pdd.
						//this.log.debug(e);
					}
				}
				IDServizio idServizioObject = null;
				if(idServizio>0){
					idServizioObject= IDServizioUtils.buildIDServizio(tipoServizio, nomeServizio, idSoggettoErogatore, versioneServizio);
				}

				String azione = rs.getString("nome_azione");
				if(azione!=null && !"".equals(azione)){
					idServizioObject.setAzione(azione);
				}
				idFruizione.setIdServizio(idServizioObject);

				
				idPD.setIdentificativiFruizione(idFruizione);	
			}
			else{
				throw new DriverConfigurazioneNotFound("PortaDelegata ["+nome+"] non esistente");
			}

			return idPD;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneException se) {
			throw se;
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception: " + se.getMessage(),se);
		}
		finally {

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
			}// ignore exception
		}
	}
	

	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idPD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametro idPD Non Valido");

		String nome = idPD.getNome();

		if ((nome == null)){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametri non Validi");
		}
		
		Connection con = null;
		PortaDelegata pd = null;
		long idPortaDelegata = 0;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaDelegata(idPortaDelegata)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		boolean trovato = false;
		try {

			try{
				idPortaDelegata = DBUtils.getIdPortaDelegata(nome, con, this.tipoDB);
			} catch (Exception se) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
			}
			if(idPortaDelegata>0){
				trovato = true;
			}
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
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

		if (trovato) {
			pd=this.getPortaDelegata(idPortaDelegata);

		} else {
			throw new DriverConfigurazioneNotFound("PortaDelegata ["+nome+"] non esistente");
		}

		return pd;
	}

	@Override
	public void createPortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createPortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaDelegata type = 1");

			DriverConfigurazioneDB_LIB.CRUDPortaDelegata(1, aPD, con);

			this.log.debug("Creazione PortaDelegata [" + aPD.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaDelegata] Errore durante la creazione della PortaDelegata : " + qe.getMessage(),qe);
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
	
	@Override
	public void updatePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updatePortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaDelegata type = 2");

			//long id = DriverConfigurazioneDB_LIB.CRUDPortaDelegata(2, aPD, con);
			DriverConfigurazioneDB_LIB.CRUDPortaDelegata(2, aPD, con);

			this.log.debug("Aggiornamento PortaDelegata [" + aPD.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaDelegata] Errore durante l'aggiornamento della PortaDelegata : " + qe.getMessage(),qe);
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

	@Override
	public void deletePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		if (aPD == null)
			throw new DriverConfigurazioneException("Porta Delegata non valida");
		if (aPD.getNome() == null || aPD.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Delegata non valido");
		if (aPD.getNomeSoggettoProprietario() == null || aPD.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Delegata non valido");
		if (aPD.getTipoSoggettoProprietario() == null || aPD.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Delegata non valido");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deletePortaDelegata");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaDelegata type = 3");

			long id = DriverConfigurazioneDB_LIB.CRUDPortaDelegata(3, aPD, con);

			this.log.debug("Cancellazione PortaDelegata [" + id + "] completato.");
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaDelegata] Errore durante la cancellazione della PortaDelegata : " + qe.getMessage(),qe);
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
	
	public List<String> portaDelegataRuoliList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataRuoliList";
		int idLista = Liste.PORTE_DELEGATE_RUOLI;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return listIdRuoli;
	}
	
	
	
	public List<String> portaDelegataScopeList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataScopeList";
		int idLista = Liste.PORTE_DELEGATE_SCOPE;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<String> listIdScope = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id="+CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_DELEGATE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPD);

			risultato = stmt.executeQuery();

			listIdScope = new ArrayList<>();
			while (risultato.next()) {

				listIdScope.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return listIdScope;
	}
	
	

	@Override
	public IDPortaApplicativa getIDPortaApplicativa(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
	
		String nomeMetodo = "getIDPortaApplicativa";
		
		if (nome == null)
			throw new DriverConfigurazioneException("["+nomeMetodo+"] Parametro Non Valido");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			IDPortaApplicativa idPA = null;
			if (rs.next()) {
				
				idPA = new IDPortaApplicativa();
				
				idPA.setNome(rs.getString("nome_porta"));
				
				IdentificativiErogazione idErogazione = new IdentificativiErogazione();
				
				IDSoggetto idSoggettoProprietario = null;
				try{
					long idSoggProprietario = rs.getLong("id_soggetto");
					idSoggettoProprietario = this.getIdSoggetto(idSoggProprietario,con);
				} catch (Exception se) {
					throw new Exception(se.getMessage()); // mappo NotFound in una eccezione generica. Il Soggetto proprietario deve esistere
				}
				

				IDSoggetto idSoggettoVirtuale = null;
				String tipoSoggVirt=rs.getString("tipo_soggetto_virtuale");
				String nomeSoggVirt=rs.getString("nome_soggetto_virtuale");
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					idSoggettoVirtuale = new IDSoggetto(tipoSoggVirt, nomeSoggVirt);
				}
				idErogazione.setSoggettoVirtuale(idSoggettoVirtuale);

				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");

				String nomeProprietarioServizio = null;
				String tipoProprietarioServizio = null;
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					nomeProprietarioServizio=nomeSoggVirt;
					tipoProprietarioServizio=tipoSoggVirt;
				}else{
					nomeProprietarioServizio=idSoggettoProprietario.getNome();
					tipoProprietarioServizio=idSoggettoProprietario.getTipo();
				}

				long idServizioPA=-1;
				try {
					idServizioPA = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeProprietarioServizio, tipoProprietarioServizio, con, this.tipoDB,this.tabellaSoggetti);
				} catch (Exception e) {
					// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
					// in un database che non ' quello della controlstation ma quello pdd.
					//this.log.info(e);
				}
				IDServizio idServizio = null;
				if( (idServizioPA>0) || (nomeServizio!=null && !nomeServizio.equals("") && tipoServizio!=null && !tipoServizio.equals("")) ){
					idServizio = IDServizioUtils.buildIDServizio(tipoServizio, nomeServizio, 
							new IDSoggetto(tipoProprietarioServizio,nomeProprietarioServizio), versioneServizio);
				}
				
				String azione = rs.getString("azione");
				if(azione!=null && !"".equals(azione)){
					idServizio.setAzione(azione);
				}
				idErogazione.setIdServizio(idServizio);

				
				idPA.setIdentificativiErogazione(idErogazione);	
			}
			else{
				throw new DriverConfigurazioneNotFound("PortaApplicativa ["+nome+"] non esistente");
			}

			return idPA;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneException se) {
			throw se;
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::"+nomeMetodo+"] Exception: " + se.getMessage(),se);
		}
		finally {

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
			}// ignore exception
		}
	}
	
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idPA == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Parametro idPA Non Valido");

		String nome = idPA.getNome();

		if ((nome == null)){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Parametri non Validi");
		}
		
		Connection con = null;
		PortaApplicativa pa = null;
		long idPortaApplicativa = 0;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativa(idPortaApplicativa)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		boolean trovato = false;
		try {

			try{
				idPortaApplicativa = DBUtils.getIdPortaApplicativa(nome, con, this.tipoDB);
			} catch (Exception se) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
			}
			if(idPortaApplicativa>0){
				trovato = true;
			}
			
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
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

		if (trovato) {
			pa=this.getPortaApplicativa(idPortaApplicativa);

		} else {
			throw new DriverConfigurazioneNotFound("PortaApplicativa ["+nome+"] non esistente");
		}

		return pa;
	}
	
	
	@Override
	public List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getPortaApplicativa(idServizio, ricercaPuntuale, null);
	}
	
	@Override
	public List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getPortaApplicativa(idServizio, ricercaPuntuale, soggettoVirtuale);
	}
	
		
	private List<PortaApplicativa> _getPortaApplicativa(IDServizio service,boolean ricercaPuntuale, IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		if (service==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idServizio Non Valido");

		IDSoggetto soggettoErogatore = service.getSoggettoErogatore();
		if ((soggettoErogatore == null) || (service == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri Non Validi");
		String servizio = service.getNome();
		String tipoServizio = service.getTipo();
		Integer versioneServizio = service.getVersione();
		String azione = service.getAzione();
		if ((servizio == null) || (tipoServizio == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Servizio) Non Validi");

		if(soggettoVirtuale!=null){
			if(soggettoVirtuale.getTipo()==null || soggettoVirtuale.getNome()==null)
				throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Soggetto Virtuale) non validi");
		}

		// Devi cercare una porta applicativa che appartiene al soggetto
		// erogatore ( tipo+nome)
		// e che possiede il tipoServizio,servizio e azione specificata
		// Nota, siccome l'azione e' opzionale e puo' essere null,
		// Se l'azione e' null, chiaramente la ricerca consiste nel trovare una
		// PA con quel servizio (tipo+nome)
		// Se l'azione non e' null, la ricerca deve prima essere effettuata
		// anche con l'azione, e se questa fallisce,
		// viene effettuata allora senza l'azione controllando solo il servizio.

		// Nel caso la ricerca non sia puntuale, se non si trovano PA con un'azione specifica, si prova a cercarla con azione non definita. 

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativa(idPortaApplicativa)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<Long> idPorteApplicative = new ArrayList<Long>();
		try {
			// Soggetto Proprietario della Porta Applicativa
			long idSoggErog = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto=?");
			sqlQueryObject.addWhereCondition("tipo_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stm.setString(1, soggettoErogatore.getNome());
			stm.setString(2, soggettoErogatore.getTipo());
			rs = stm.executeQuery();

			this.log.debug("eseguo query soggetto" + DBUtils.formatSQLString(sqlQuery, soggettoErogatore.getNome() ,soggettoErogatore.getTipo() ));

			if (rs.next()) {
				idSoggErog = rs.getLong("id");
				rs.close();
				stm.close();
			} else
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaApplicativa] Nessuno soggetto trovato [" + soggettoErogatore.getNome() + "," + soggettoErogatore.getTipo() + "].");

			// Eventuale SoggettoVirtuale
			long idSoggVirtuale = 0;
			if(soggettoVirtuale!=null){
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_soggetto=?");
				sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				stm.setString(1, soggettoVirtuale.getNome());
				stm.setString(2, soggettoVirtuale.getTipo());
				rs = stm.executeQuery();

				this.log.debug("eseguo query soggetto virtuale" + DBUtils.formatSQLString(sqlQuery, soggettoVirtuale.getNome() ,soggettoVirtuale.getTipo() ));

				if (rs.next()) {
					idSoggVirtuale = rs.getLong("id");
				} 
				rs.close();
				stm.close();
				//else
				//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaApplicativa] Nessuno soggetto virtuale trovato [" + soggettoVirtuale.getNome() + "," + soggettoVirtuale.getTipo() + "].");
				// L'ID non c'e' per forza. Nel database pdd, il soggetto virtuale non c'e'.
			}


			this.log.debug("eseguo soggetto:" +idSoggErog);

			if (azione == null || azione.trim().equals("")) {
				this.log.debug("ricerca PA con azione == null, soggettoVirtuale="+soggettoVirtuale);
				if(soggettoVirtuale==null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				} else {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, "");
					stm.setString(index++, "-");
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio,versioneServizio,"","-"));
				}else{
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setLong(index++, idSoggVirtuale);
					stm.setString(index++, soggettoVirtuale.getTipo());
					stm.setString(index++, soggettoVirtuale.getNome());
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, "");
					stm.setString(index++, "-");
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, soggettoErogatore.getTipo(),soggettoErogatore.getNome(),
							tipoServizio, servizio,versioneServizio,"","-"));
				}



			} else {
				this.log.debug("ricerca PA con azione != null, soggettoVirtuale="+soggettoVirtuale);
				if(soggettoVirtuale==null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto=?");
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("servizio=?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition("azione=?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				} else {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
					sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition("tipo_servizio = ?");
					sqlQueryObject.addWhereCondition("servizio = ?");
					sqlQueryObject.addWhereCondition("versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
					sqlQueryObject.addWhereCondition("azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, azione);
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, versioneServizio, azione));
				}else
				{
					int index = 1;
					stm.setLong(index++, idSoggErog);
					stm.setLong(index++, idSoggVirtuale);
					stm.setString(index++, soggettoVirtuale.getTipo());
					stm.setString(index++, soggettoVirtuale.getNome());
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
					stm.setString(index++, azione);
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, versioneServizio, azione));
				}
			}


			rs = stm.executeQuery();

			while(rs.next()){
				long idPortaApplicativa = rs.getLong("id");
				idPorteApplicative.add(idPortaApplicativa);
			}
			rs.close();
			stm.close();
				

			this.log.debug("ricerca puntuale="+ricercaPuntuale);
			if(!ricercaPuntuale){
				if (idPorteApplicative.size()==0 && azione != null) {
					this.log.debug("ricerca PA con azione != null ma con solo il servizio, soggettoVirtuale="+soggettoVirtuale);
			
					// in questo caso provo a cercaredelle porte applicative solo
					// con id_soggetto tipo_servizio e servizio
					if(soggettoVirtuale==null) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_soggetto = ?");
						sqlQueryObject.addWhereCondition("tipo_servizio = ?");
						sqlQueryObject.addWhereCondition("servizio = ?");
						sqlQueryObject.addWhereCondition("versione_servizio = ?");
						sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
						sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
					} else {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_soggetto = ?");
						sqlQueryObject.addWhereCondition(false, "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
						sqlQueryObject.addWhereCondition("tipo_servizio = ?");
						sqlQueryObject.addWhereCondition("servizio = ?");
						sqlQueryObject.addWhereCondition("versione_servizio = ?");
						sqlQueryObject.addWhereCondition(false, "mode_azione IS NULL", "mode_azione<>?");
						sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
					}

					stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					int index = 1;
					if(soggettoVirtuale==null){
						stm.setLong(index++, idSoggErog);
						stm.setString(index++, tipoServizio);
						stm.setString(index++, servizio);
						stm.setInt(index++, versioneServizio);
						stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
						stm.setString(index++, "");
						stm.setString(index++, "-");
					}else{
						stm.setLong(index++, idSoggErog);
						stm.setLong(index++, idSoggVirtuale);
						stm.setString(index++, soggettoVirtuale.getTipo());
						stm.setString(index++, soggettoVirtuale.getNome());
						stm.setString(index++, tipoServizio);
						stm.setString(index++, servizio);
						stm.setInt(index++, versioneServizio);
						stm.setString(index++, PortaApplicativaAzioneIdentificazione.DELEGATED_BY.getValue());
						stm.setString(index++, "");
						stm.setString(index++, "-");
					}


					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio));

					rs = stm.executeQuery();

					while(rs.next()){
						long idPortaApplicativa = rs.getLong("id");
						idPorteApplicative.add(idPortaApplicativa);
					}
					
				}
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
		}finally {
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

		if(idPorteApplicative.size()<=0){
			throw new DriverConfigurazioneNotFound("Porte Applicative non esistenti");
		}
		List<PortaApplicativa> pa = new ArrayList<PortaApplicativa>();
		for (Long idPA : idPorteApplicative) {
			pa.add(this.getPortaApplicativa(idPA));
		}

		return pa;
	}

	@Override
	public void createPortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createPortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaApplicativa type = 1");

			DriverConfigurazioneDB_LIB.CRUDPortaApplicativa(1, aPA, con);

			this.log.debug("Creazione PortaApplicativa [" + aPA.getId() + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createPortaApplicativa] Errore durante la creazione della PortaApplicativa : " + qe.getMessage(),qe);
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
	
	@Override
	public void updatePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updatePortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaApplicativa type = 2");

			long id = DriverConfigurazioneDB_LIB.CRUDPortaApplicativa(2, aPA, con);

			this.log.debug("Aggiornamento PortaApplicativa [" + id + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updatePortaApplicativa] Errore durante l'update della PortaApplicativa : " + qe.getMessage(),qe);
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

	@Override
	public void deletePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida");
		if (aPA.getNome() == null || aPA.getNome().equals(""))
			throw new DriverConfigurazioneException("Nome Porta Applicativa non valido");
		if (aPA.getNomeSoggettoProprietario() == null || aPA.getNomeSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Nome Soggetto Proprietario Porta Applicativa non valido");
		if (aPA.getTipoSoggettoProprietario() == null || aPA.getTipoSoggettoProprietario().equals(""))
			throw new DriverConfigurazioneException("Tipo Soggetto Proprietario Porta Applicativa non valido");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deletePortaApplicativa");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDPortaApplicativa type = 3");

			long id = DriverConfigurazioneDB_LIB.CRUDPortaApplicativa(3, aPA, con);

			this.log.debug("Cancellazione PortaApplicativa [" + id + "] completato.");

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deletePortaApplicativa] Errore durante la cancellazione della PortaApplicativa : " + qe.getMessage(),qe);
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

	@Override
	public Hashtable<IDSoggetto, PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		this.log.debug("metodo getPorteApplicative_SoggettiVirtuali in esecuzione...");

		if (idServizio == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idServizio Non Valido");
		if (idServizio.getSoggettoErogatore() == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro Soggetto Erogatore Non Valido");
		Hashtable<IDSoggetto, PortaApplicativa> paConSoggetti = new Hashtable<IDSoggetto, PortaApplicativa>();
		IDSoggetto soggettoVirtuale = idServizio.getSoggettoErogatore();
		String servizio = idServizio.getNome();
		String tipoServizio = idServizio.getTipo();
		Integer versioneServizio = idServizio.getVersione();
		String azione = idServizio.getAzione();
		if ((servizio == null) || (tipoServizio == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametri (Servizio) Non Validi");

		/*
		 * // Devi cercare tutte le porte applicative che hanno come soggetto
		 * virtuale il soggettoVirtuale // con servizio,tipoServizio e azione
		 * come specificato nel precendente metodo.
		 *  // Per ogni porta applicativa che trovi devi creare un IDSoggetto
		 * del soggetto proprietario della PA trovata IDSoggetto sogg = new
		 * IDSoggetto(tipo,nome); // Riempi i dati della PA trovata come detto
		 * nel precedente metodo. PortaApplicativa pa = new PortaApplicativa();
		 * pa.setDescrizione(descrizione); ........ // aggiungi
		 * paConSoggetti.put(IDSoggetto,pa);
		 */
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = null;
		ResultSet rs1=null;
		PreparedStatement stm1 = null;
		ResultSet rs2=null;
		PreparedStatement stm2 = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteApplicative_SoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			String nomeSoggVirt = soggettoVirtuale.getNome();
			String tipoSoggVirt = soggettoVirtuale.getTipo();

			if (nomeSoggVirt == null || nomeSoggVirt.equals("") || tipoSoggVirt == null || tipoSoggVirt.equals(""))
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Parametri SoggettoVirtuale non corretti.");

			//Prendo la lista dei soggetti presenti
			ArrayList<Long> soggettiList = new ArrayList<Long>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("id");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();
			while (rs.next()) {
				soggettiList.add(rs.getLong(1));
			}
			rs.close();
			stm.close();

			//controllo le porte applicative dei soggetti che soddisfano i criteri di ricerca
			for (Long idSoggetto : soggettiList) {
				//Azione non settata eseguo query con azione NULL
				if (azione == null || azione.trim().equals("")) {
					this.log.debug("azione == null");
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, "");
					stm.setString(index++, "-");
					stm.setLong(index++, idSoggetto);

					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio));
				} else {
					//cerco porta applicativa con soggetto virtuale x con servizio y con azione z
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					
					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, azione);
					stm.setLong(index++, idSoggetto);

					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio, versioneServizio, azione));
				}

				rs = stm.executeQuery();

				boolean trovato = rs.next();

				if (!trovato && azione != null) {

					this.log.debug("Cerco PA generica (Azione non definita)");

					rs.close();
					stm.close();
					// in questo caso provo a cercare delle porte applicative solo
					// con id_soggetto tipo_servizio e servizio
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					//sqlQueryObject.addSelectField("*");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePA");
					sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "id_porta_applicativa");
					sqlQueryObject.addSelectAliasField(this.tabellaSoggetti+".id", "id_proprietario");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = "+CostantiDB.PORTE_APPLICATIVE+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio, versioneServizio));
					this.log.debug("QUERY RAW: "+sqlQuery);

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					
					int index = 1;
					stm.setString(index++, nomeSoggVirt);
					stm.setString(index++, tipoSoggVirt);
					stm.setString(index++, tipoServizio);
					stm.setString(index++, servizio);
					stm.setInt(index++, versioneServizio);
					stm.setString(index++, "");
					stm.setString(index++, "-");
					stm.setLong(index++, idSoggetto);



					rs = stm.executeQuery();

					trovato = rs.next();
				}
				this.log.debug("Ripristino rs");
				// ripristino il cursore del resultset
				rs.beforeFirst();

				// devo iterare su tutte le pa se ho trovato delle pa
				this.log.debug("Itero rs...");
				while (trovato && rs.next()) {

					this.log.debug("PortaApplicativa, raccolta dati");

					long idPortaApplicativa = rs.getLong("id_porta_applicativa");
					this.log.debug("PortaApplicativa, raccolta dati id["+idPortaApplicativa+"] in corso...");
					PortaApplicativa pa = this.getPortaApplicativa(idPortaApplicativa,con);
					this.log.debug("PortaApplicativa, raccolta dati id["+idPortaApplicativa+"] effettuata.");


					long idSoggettoProprietario = rs.getLong("id_proprietario");
					this.log.debug("PortaApplicativa, raccolta dati soggetto id["+idSoggettoProprietario+"] in corso...");

					// recupero dati del soggetto proprietario
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(this.tabellaSoggetti);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm1 = con.prepareStatement(sqlQuery);
					stm1.setLong(1, idSoggettoProprietario);
					rs1 = stm1.executeQuery();

					IDSoggetto soggettoProprietario = null;
					if (rs1.next()) {
						soggettoProprietario = new IDSoggetto(rs1.getString("tipo_soggetto"), rs1.getString("nome_soggetto"));
					} else {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Impossibile trovare le informazioni del soggetto proprietario della PA.");
					}
					rs1.close();
					stm1.close();

					this.log.debug("PortaApplicativa, raccolta dati soggetto id["+idSoggettoProprietario+"] completata.");

					// aggiungo la pa e il soggetto all'hashtable
					paConSoggetti.put(soggettoProprietario, pa);

				}//chiudo while

				rs.close();
				stm.close();

			}//chiudo for

			if(paConSoggetti.size() == 0)
				throw new DriverConfigurazioneNotFound("[getPortaApplicativa_SoggettiVirtuali] Porte applicative di soggetti virtuali non esistenti.");

			return paConSoggetti;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound de) {
			throw de;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicative_SoggettiVirtuali] Exception: " + se.getMessage(),se);
		}finally {
			//Chiudo statement and resultset
			try{
				if(rs2!=null) rs2.close();
				if(stm2!=null) stm2.close();
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
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

	}
	
	public List<String> portaApplicativaRuoliList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaApplicativaRuoliList";
		int idLista = Liste.PORTE_APPLICATIVE_RUOLI;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta=?");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta=?");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return listIdRuoli;
	}
	
	public List<String> portaApplicativaScopeList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaApplicativaScopeList";
		int idLista = Liste.PORTE_APPLICATIVE_SCOPE;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<String> listIdScope = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta=?");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();

			listIdScope = new ArrayList<>();
			while (risultato.next()) {

				listIdScope.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return listIdScope;
	}
	
	public List<TrasformazioneRegola> porteDelegateTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI;
		String nomeMetodo = "porteDelegateTrasformazioniList";
		boolean delegata = true;
		return _getTrasformazioniList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegola> porteApplicativeTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeMetodo = "porteApplicativeTrasformazioniList";
		boolean delegata = false;
		return _getTrasformazioniList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegola> _getTrasformazioniList(long idPA, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm0 = null;
		ResultSet rs0 = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegola> lista = new ArrayList<TrasformazioneRegola>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("posizione");
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabella+".posizione");
			sqlQueryObject.addOrderBy(nomeTabella+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			rs = stmt.executeQuery();

			List<Long> idTrasformazione = new ArrayList<>();			
			while (rs.next()) {
				
				idTrasformazione.add(rs.getLong("id"));
				
			}
			rs.close();
			stmt.close();
			
			if(idTrasformazione!=null && !idTrasformazione.isEmpty()) {
			
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();
				
				for (Long idLongTrasformazione : idTrasformazione) {
					
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasformazione);

					rs = stmt.executeQuery();
					
					if (rs.next()) {
									
						TrasformazioneRegola regola = new TrasformazioneRegola();
						
						String nome = rs.getString("nome");
						regola.setNome(nome);
		
						int posizione = rs.getInt("posizione");
						regola.setPosizione(posizione);
						
						StatoFunzionalita stato = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato"));
						regola.setStato(stato);
						
						String applicabilita_azioni = rs.getString("applicabilita_azioni");
						String applicabilita_ct = rs.getString("applicabilita_ct");
						String applicabilita_pattern = rs.getString("applicabilita_pattern");
						if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ||
								(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
								(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
								) {
							TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
							
							if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ) {
								if(applicabilita_azioni.contains(",")) {
									String [] tmp = applicabilita_azioni.split(",");
									for (int i = 0; i < tmp.length; i++) {
										applicabilita.addAzione(tmp[i].trim());
									}
								}
								else {
									applicabilita.addAzione(applicabilita_azioni);
								}
							}
							
							if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
								if(applicabilita_ct.contains(",")) {
									String [] tmp = applicabilita_ct.split(",");
									for (int i = 0; i < tmp.length; i++) {
										applicabilita.addContentType(tmp[i].trim());
									}
								}
								else {
									applicabilita.addContentType(applicabilita_ct);
								}
							}
							
							if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
								applicabilita.setPattern(applicabilita_pattern);
							}
							
							regola.setApplicabilita(applicabilita);
						}
						
						TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
						
						int req_conversione_enabled = rs.getInt("req_conversione_enabled");
						if(CostantiDB.TRUE == req_conversione_enabled) {
							richiesta.setConversione(true);
						}
						else {
							richiesta.setConversione(false);
						}
						richiesta.setConversioneTipo(rs.getString("req_conversione_tipo"));
						IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
						richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "req_conversione_template"));
						richiesta.setContentType(rs.getString("req_content_type"));
						
						int trasformazione_rest = rs.getInt("rest_transformation");
						if(CostantiDB.TRUE == trasformazione_rest) {
							TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
							trasformazioneRest.setMetodo(rs.getString("rest_method"));
							trasformazioneRest.setPath(rs.getString("rest_path"));
							richiesta.setTrasformazioneRest(trasformazioneRest);
						}
							
						int trasformazione_soap = rs.getInt("soap_transformation");
						if(CostantiDB.TRUE == trasformazione_soap) {
							TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
							
							trasformazioneSoap.setVersione(DriverConfigurazioneDB_LIB.getEnumVersioneSOAP(rs.getString("soap_version")));
							trasformazioneSoap.setSoapAction(rs.getString("soap_action"));
							
							int envelope = rs.getInt("soap_envelope");
							if(CostantiDB.TRUE == envelope) {
								trasformazioneSoap.setEnvelope(true);
							}
							else {
								trasformazioneSoap.setEnvelope(false);
							}
							
							int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
							if(CostantiDB.TRUE == envelope_as_attach) {
								trasformazioneSoap.setEnvelopeAsAttachment(true);
								
								trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
								trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
							}
							else {
								trasformazioneSoap.setEnvelopeAsAttachment(false);
							}
							
							richiesta.setTrasformazioneSoap(trasformazioneSoap);
						}
						
						
						regola.setId(rs.getLong("id"));
						
						regola.setRichiesta(richiesta);
						
						
						// ** SOGGETTI **
						
						if(!portaDelegata) {
							
							nomeTabella = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
							
							sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
							sqlQueryObject.addFromTable(nomeTabella);
							sqlQueryObject.addSelectField("*");
							sqlQueryObject.addWhereCondition("id_trasformazione=?");
							String sqlQuery = sqlQueryObject.createSQLQuery();
							stm0 = con.prepareStatement(sqlQuery);
							stm0.setLong(1, regola.getId());
							rs0 = stm0.executeQuery();
			
							while (rs0.next()) {
								
								if(regola.getApplicabilita()==null) {
									regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
								}
								
								TrasformazioneRegolaApplicabilitaSoggetto soggetto = new TrasformazioneRegolaApplicabilitaSoggetto();
								soggetto.setTipo(rs0.getString("tipo_soggetto"));
								soggetto.setNome(rs0.getString("nome_soggetto"));
								
								regola.getApplicabilita().addSoggetto(soggetto);
							
							}
							rs0.close();
							stm0.close();
						}
						
						
						// ** APPLICATIVI **
						
						nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(nomeTabella);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_trasformazione=?");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stm0 = con.prepareStatement(sqlQuery);
						stm0.setLong(1, regola.getId());
						rs0 = stm0.executeQuery();
		
						// per ogni entry 
						// prendo l'id del servizio associato, recupero il nome e
						// aggiungo
						// il servizio applicativo alla PortaDelegata da ritornare
						while (rs0.next()) {
							long idSA = rs0.getLong("id_servizio_applicativo");
		
							if (idSA != 0) {
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
								sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
								sqlQueryObject.addSelectField("nome");
								sqlQueryObject.addSelectField("tipo_soggetto");
								sqlQueryObject.addSelectField("nome_soggetto");
								sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
								sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
								sqlQueryObject.setANDLogicOperator(true);
								sqlQuery = sqlQueryObject.createSQLQuery();
								stm1 = con.prepareStatement(sqlQuery);
								stm1.setLong(1, idSA);
		
								this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));
		
								rs1 = stm1.executeQuery();
		
								TrasformazioneRegolaApplicabilitaServizioApplicativo servizioApplicativo = null;
								if (rs1.next()) {
									// setto solo il nome come da specifica
									servizioApplicativo = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
									servizioApplicativo.setId(idSA);
									servizioApplicativo.setNome(rs1.getString("nome"));
									servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
									servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
									if(regola.getApplicabilita()==null) {
										regola.setApplicabilita(new TrasformazioneRegolaApplicabilitaRichiesta());
									}
									regola.getApplicabilita().addServizioApplicativo(servizioApplicativo);
								}
								rs1.close();
								stm1.close();
							}
						}
						rs0.close();
						stm0.close();
						
										
						lista.add(regola);

					}
					
					rs.close();
					stmt.close();
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if(rs1!=null)
					rs1.close();
			}catch(Exception eClose) {}
			try {
				if(rs0!=null)
					rs0.close();
			}catch(Exception eClose) {}
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception eClose) {}
			try {
				if(stm1!=null)
					stm1.close();
			}catch(Exception eClose) {}
			try {
				if(stm0!=null)
					stm0.close();
			}catch(Exception eClose) {}
			try {
				if(stmt!=null)
					stmt.close();
			}catch(Exception eClose) {}

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
		
		return lista;
	}
	
	public boolean azioneUsataInTrasformazioniPortaDelegata(String azione) throws DriverConfigurazioneException {
		return this.azioneUsataInTrasformazioni(azione, true);
	}
	public boolean azioneUsataInTrasformazioniPortaApplicativa(String azione) throws DriverConfigurazioneException {
		return this.azioneUsataInTrasformazioni(azione, false);
	}
	private boolean azioneUsataInTrasformazioni(String azione, boolean portaDelegata) throws DriverConfigurazioneException {

		String nomeMetodo = "azioneUsataInTrasformazioni";
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("applicabilita_azioni");
			sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azione, false, true, false);
			sqlQueryObject.setANDLogicOperator(true);
			stmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			rs = stmt.executeQuery();

			while (rs.next()) {
				
				String checkAz = rs.getString("applicabilita_azioni");
				if(checkAz!=null) {
					if(azione.equals(checkAz)) {
						return true;
					}
					if(checkAz.contains(",")) {
						String [] tmp = checkAz.split(",");
						if(tmp!=null && tmp.length>0) {
							for (int i = 0; i < tmp.length; i++) {
								if(azione.equals(tmp[i])) {
									return true;
								}
							}
						}
					}
				}

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try {
				if(rs!=null)
					rs.close();
			}catch(Exception eClose) {}
			try {
				if(stmt!=null)
					stmt.close();
			}catch(Exception eClose) {}

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
		
		return false;
	}
	
	
	public TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType,
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			boolean interpretaNullList) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazione";
		boolean delegata = false;
		
		return _getTrasformazione(idPorta, azioni, pattern, contentType, soggetti, applicativi, nomeMetodo, delegata, interpretaNullList);
		
	}
	
	public TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazione";
		boolean delegata = true;
		return _getTrasformazione(idPorta, azioni, pattern, contentType, null, applicativi, nomeMetodo, delegata, 
				true); // esiste solo una lista
	}
	
	private TrasformazioneRegola _getTrasformazione(long idPorta, String azioni, String pattern, String contentType, 
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			String nomeMetodo, boolean portaDelegata, boolean interpretaNullList) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaSoggetti = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI; //portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SOGGETTI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;
		String nomeTabellaSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegola regola = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if(azioni != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_azioni = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azioni, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_azioni");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_ct");
			}
			
			if(!portaDelegata) {
				if(soggetti==null || soggetti.isEmpty()) {
					if(interpretaNullList) {
						ISQLQueryObject sqlQueryObjectExists = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObjectExists.addFromTable(nomeTabellaSoggetti);
						sqlQueryObjectExists.addSelectField("id_trasformazione");
						sqlQueryObjectExists.setANDLogicOperator(true);
						sqlQueryObjectExists.addWhereCondition("id_trasformazione="+nomeTabella+".id");
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExists);	
					}
				}
				else {
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione="+nomeTabella+".id");
					List<String> conditions = new ArrayList<>();
					for (@SuppressWarnings("unused") TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto : soggetti) {
						StringBuilder bf = new StringBuilder();
						bf.append("( ");
						bf.append(nomeTabellaSoggetti).append(".tipo_soggetto=?");
						bf.append(" AND ");
						bf.append(nomeTabellaSoggetti).append(".nome_soggetto=?");
						bf.append(") ");
						conditions.add(bf.toString());
					}
					sqlQueryObject.addWhereCondition(false, conditions.toArray(new String[1]));
				}
			}
			
			if(applicativi==null || applicativi.isEmpty()) {
				if(interpretaNullList) {
					ISQLQueryObject sqlQueryObjectExists = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObjectExists.addFromTable(nomeTabellaSA);
					sqlQueryObjectExists.addSelectField("id_trasformazione");
					sqlQueryObjectExists.setANDLogicOperator(true);
					sqlQueryObjectExists.addWhereCondition("id_trasformazione="+nomeTabella+".id");
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectExists);	
				}
			}
			else {
				sqlQueryObject.addFromTable(nomeTabellaSA);
				sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_trasformazione="+nomeTabella+".id");
				List<String> conditions = new ArrayList<>();
				for (@SuppressWarnings("unused") TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaSA : applicativi) {
					StringBuilder bf = new StringBuilder();
					bf.append("( ");
					bf.append(nomeTabellaSA).append(".id_servizio_applicativo=?");
					bf.append(") ");
					conditions.add(bf.toString());
				}
				sqlQueryObject.addWhereCondition(false, conditions.toArray(new String[1]));
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
//			if(azioni != null)
//				stmt.setString(parameterIndex ++, azioni);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			if(!portaDelegata) {
				if(soggetti==null || soggetti.isEmpty()) {
					// nop;
				}
				else {
					for (TrasformazioneRegolaApplicabilitaSoggetto trasformazioneRegolaApplicabilitaSoggetto : soggetti) {
						stmt.setString(parameterIndex ++, trasformazioneRegolaApplicabilitaSoggetto.getTipo());
						stmt.setString(parameterIndex ++, trasformazioneRegolaApplicabilitaSoggetto.getNome());
					}
				}
			}
			
			if(applicativi==null || applicativi.isEmpty()) {
				// nop;
			}
			else {
				for (TrasformazioneRegolaApplicabilitaServizioApplicativo trasformazioneRegolaApplicabilitaSA : applicativi) {
					long idSA = DBUtils.getIdServizioApplicativo(trasformazioneRegolaApplicabilitaSA.getNome(), 
							trasformazioneRegolaApplicabilitaSA.getTipoSoggettoProprietario(), trasformazioneRegolaApplicabilitaSA.getNomeSoggettoProprietario(), con, this.tipoDB);
					stmt.setLong(parameterIndex ++, idSA);
				}
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				
				regola = _getTrasformazione(risultato);
			}
			
			risultato.close();
			stmt.close();

			return regola;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazione";
		boolean delegata = false;
		return _getTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazione";
		boolean delegata = true;
		return _getTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegola _getTrasformazione(long idPorta, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegola regola = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setString(parameterIndex ++, nome);
						
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				
				regola = _getTrasformazione(risultato);
			}
			
			risultato.close();
			stmt.close();

			return regola;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	private TrasformazioneRegola _getTrasformazione(ResultSet risultato) throws Exception {
		TrasformazioneRegola regola  = new TrasformazioneRegola();
				
		StatoFunzionalita stato = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(risultato.getString("stato"));
		regola.setStato(stato);
		
		String applicabilita_azioni = risultato.getString("applicabilita_azioni");
		String applicabilita_ct = risultato.getString("applicabilita_ct");
		String applicabilita_pattern = risultato.getString("applicabilita_pattern");
		if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ||
				(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
				(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
				) {
			TrasformazioneRegolaApplicabilitaRichiesta applicabilita = new TrasformazioneRegolaApplicabilitaRichiesta();
			
			if( (applicabilita_azioni!=null && !"".equals(applicabilita_azioni)) ) {
				if(applicabilita_azioni.contains(",")) {
					String [] tmp = applicabilita_azioni.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addAzione(tmp[i].trim());
					}
				}
				else {
					applicabilita.addAzione(applicabilita_azioni);
				}
			}
			
			if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
				if(applicabilita_ct.contains(",")) {
					String [] tmp = applicabilita_ct.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addContentType(tmp[i].trim());
					}
				}
				else {
					applicabilita.addContentType(applicabilita_ct);
				}
			}
			
			if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
				applicabilita.setPattern(applicabilita_pattern);
			}
			
			regola.setApplicabilita(applicabilita);
		}
		
		TrasformazioneRegolaRichiesta richiesta = new TrasformazioneRegolaRichiesta();
		
		int req_conversione_enabled = risultato.getInt("req_conversione_enabled");
		if(CostantiDB.TRUE == req_conversione_enabled) {
			richiesta.setConversione(true);
		}
		else {
			richiesta.setConversione(false);
		}
		richiesta.setConversioneTipo(risultato.getString("req_conversione_tipo"));
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
		richiesta.setConversioneTemplate(jdbcAdapter.getBinaryData(risultato, "req_conversione_template"));
		richiesta.setContentType(risultato.getString("req_content_type"));
		
		int trasformazione_rest = risultato.getInt("rest_transformation");
		if(CostantiDB.TRUE == trasformazione_rest) {
			TrasformazioneRest trasformazioneRest = new TrasformazioneRest();
			trasformazioneRest.setMetodo(risultato.getString("rest_method"));
			trasformazioneRest.setPath(risultato.getString("rest_path"));
			richiesta.setTrasformazioneRest(trasformazioneRest);
		}
			
		int trasformazione_soap = risultato.getInt("soap_transformation");
		if(CostantiDB.TRUE == trasformazione_soap) {
			TrasformazioneSoap trasformazioneSoap = new TrasformazioneSoap();
			
			trasformazioneSoap.setVersione(DriverConfigurazioneDB_LIB.getEnumVersioneSOAP(risultato.getString("soap_version")));
			trasformazioneSoap.setSoapAction(risultato.getString("soap_action"));
			
			int envelope = risultato.getInt("soap_envelope");
			if(CostantiDB.TRUE == envelope) {
				trasformazioneSoap.setEnvelope(true);
			}
			else {
				trasformazioneSoap.setEnvelope(false);
			}
			
			int envelope_as_attach = risultato.getInt("soap_envelope_as_attach");
			if(CostantiDB.TRUE == envelope_as_attach) {
				trasformazioneSoap.setEnvelopeAsAttachment(true);
				
				trasformazioneSoap.setEnvelopeBodyConversioneTipo(risultato.getString("soap_envelope_tipo"));
				trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(risultato, "soap_envelope_template"));
			}
			else {
				trasformazioneSoap.setEnvelopeAsAttachment(false);
			}
			
			richiesta.setTrasformazioneSoap(trasformazioneSoap);
		}
		
		regola.setId(risultato.getLong("id"));
		
		regola.setRichiesta(richiesta);
		
		return regola;

	}
	
	public boolean existsPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazione";
		boolean delegata = false;
		return _existsTrasformazione(idPorta, azioni, pattern, contentType, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazione";
		boolean delegata = true;
		return _existsTrasformazione(idPorta, azioni, pattern, contentType, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazione(long idPorta, String azioni, String pattern, String contentType, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if(azioni != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_azioni = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_azioni", azioni, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_azioni");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition("applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition("applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition("applicabilita_ct");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
//			if(azioni != null)
//				stmt.setString(parameterIndex ++, azioni);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public boolean existsPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazione";
		boolean delegata = false;
		return _existsTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazione";
		boolean delegata = true;
		return _existsTrasformazione(idPorta, nome, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazione(long idPorta, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition("nome = ?");
						
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setString(parameterIndex ++, nome);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	
	public List<TrasformazioneRegolaRisposta> porteDelegateTrasformazioniRispostaList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaList";
		boolean delegata = true;
		return _getTrasformazioniRispostaList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegolaRisposta> porteApplicativeTrasformazioniRispostaList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeMetodo = "porteApplicativeTrasformazioniRispostaList";
		boolean delegata = false;
		return _getTrasformazioniRispostaList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaRisposta> _getTrasformazioniRispostaList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaRisposta> lista = new ArrayList<TrasformazioneRegolaRisposta>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectAliasField(nomeTabellaRisposta, "id", "idTrasRisposta");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addOrderBy(nomeTabellaRisposta+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idTrasformazioneRisposta = new ArrayList<>();
			while (rs.next()) { 
				idTrasformazioneRisposta.add(rs.getLong("idTrasRisposta"));
			}
			rs.close();
			stmt.close();
			
			if(idTrasformazioneRisposta!=null && !idTrasformazioneRisposta.isEmpty()) {
				for (Long idLongTrasformazioneRisposta : idTrasformazioneRisposta) {
						
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRisposta);
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
					sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
					sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasformazioneRisposta);

					rs = stmt.executeQuery();
					
					if (rs.next()) { 
						TrasformazioneRegolaRisposta risposta = _readTrasformazioneRegolaRisposta(rs);
						lista.add(risposta);
					}
					
					rs.close();
					stmt.close();
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	public TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _getTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _getTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaRisposta _getTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegolaRisposta risposta = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setANDLogicOperator(true);
			
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_max");
			}
			
			if(pattern != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_pattern = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_pattern");
			}
			
			if(contentType != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_ct = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_ct");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
			if(pattern != null)
				stmt.setString(parameterIndex ++, pattern);
			if(contentType != null)
				stmt.setString(parameterIndex ++, contentType);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				
				risposta = _readTrasformazioneRegolaRisposta(rs);
			}
			
			rs.close();
			stmt.close();

			return risposta;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
	
	public TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _getTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _getTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaRisposta _getTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegolaRisposta risposta = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".posizione");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_min");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_status_max");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_ct");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".applicabilita_pattern");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_enabled");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".conversione_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".content_type");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".return_code");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_as_attach");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_tipo");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".soap_envelope_template");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id");
			sqlQueryObject.addSelectField(nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addSelectField(nomeTabella+".rest_transformation"); // serve per capire se nella risposta devo abilitare la soap trasformation
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			stmt.setString(parameterIndex ++, nome);
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				
				risposta = _readTrasformazioneRegolaRisposta(rs);
			}
			
			rs.close();
			stmt.close();

			return risposta;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
	
	private TrasformazioneRegolaRisposta _readTrasformazioneRegolaRisposta(ResultSet rs) throws Exception{
		TrasformazioneRegolaRisposta risposta = new TrasformazioneRegolaRisposta();
		
		String nome = rs.getString("nome");
		risposta.setNome(nome);

		int posizione = rs.getInt("posizione");
		risposta.setPosizione(posizione);
		
		int applicabilita_status_min = rs.getInt("applicabilita_status_min");
		int applicabilita_status_max = rs.getInt("applicabilita_status_max");
		String applicabilita_ct = rs.getString("applicabilita_ct");
		String applicabilita_pattern = rs.getString("applicabilita_pattern");
		if( (applicabilita_status_min >0 || applicabilita_status_max>0) ||
				(applicabilita_ct!=null && !"".equals(applicabilita_ct)) ||
				(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)) 
				) {
			TrasformazioneRegolaApplicabilitaRisposta applicabilita = new TrasformazioneRegolaApplicabilitaRisposta();
			
			if(applicabilita_status_min>0) {
				applicabilita.setReturnCodeMin(applicabilita_status_min);
			}
			if(applicabilita_status_max>0) {
				applicabilita.setReturnCodeMax(applicabilita_status_max);
			}

			if( (applicabilita_ct!=null && !"".equals(applicabilita_ct)) ) {
				if(applicabilita_ct.contains(",")) {
					String [] tmp = applicabilita_ct.split(",");
					for (int i = 0; i < tmp.length; i++) {
						applicabilita.addContentType(tmp[i].trim());
					}
				}
				else {
					applicabilita.addContentType(applicabilita_ct);
				}
			}
			
			if(applicabilita_pattern!=null && !"".equals(applicabilita_pattern)){
				applicabilita.setPattern(applicabilita_pattern);
			}
			
			risposta.setApplicabilita(applicabilita);
		}
		
		int conversione_enabled = rs.getInt("conversione_enabled");
		if(CostantiDB.TRUE == conversione_enabled) {
			risposta.setConversione(true);
		}
		else {
			risposta.setConversione(false);
		}
		risposta.setConversioneTipo(rs.getString("conversione_tipo"));
		IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
		risposta.setConversioneTemplate(jdbcAdapter.getBinaryData(rs, "conversione_template"));
		risposta.setContentType(rs.getString("content_type"));
		int return_code = rs.getInt("return_code");
		if(return_code>0) {
			risposta.setReturnCode(return_code);
		}
		
		int trasformazione_rest = rs.getInt("rest_transformation");
		if(CostantiDB.TRUE == trasformazione_rest) {
		
			// la risposta deve essere ritornata in SOAP
			
			TrasformazioneSoapRisposta trasformazioneSoap = new TrasformazioneSoapRisposta();
			
			int envelope = rs.getInt("soap_envelope");
			if(CostantiDB.TRUE == envelope) {
				trasformazioneSoap.setEnvelope(true);
			}
			else {
				trasformazioneSoap.setEnvelope(false);
			}
			
			int envelope_as_attach = rs.getInt("soap_envelope_as_attach");
			if(CostantiDB.TRUE == envelope_as_attach) {
				trasformazioneSoap.setEnvelopeAsAttachment(true);
				
				trasformazioneSoap.setEnvelopeBodyConversioneTipo(rs.getString("soap_envelope_tipo"));
				trasformazioneSoap.setEnvelopeBodyConversioneTemplate(jdbcAdapter.getBinaryData(rs, "soap_envelope_template"));
			}
			else {
				trasformazioneSoap.setEnvelopeAsAttachment(false);
			}
			
			risposta.setTrasformazioneSoap(trasformazioneSoap);
			
		}

		risposta.setId(rs.getLong("id"));
		
		return  risposta;
	}
	
	public boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			
			
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_status_max");
			}
			
			if(pattern != null) {
				//sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_pattern = ?");
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_pattern", pattern, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_pattern");
			}
			
			if(contentType != null) {
				//sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".applicabilita_ct = ?");
				sqlQueryObject.addWhereLikeCondition(nomeTabellaRisposta+".applicabilita_ct", contentType, false, false);
			} else {
				sqlQueryObject.addWhereIsNullCondition(nomeTabellaRisposta+".applicabilita_ct");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
//			if(pattern != null)
//				stmt.setString(parameterIndex ++, pattern);
//			if(contentType != null)
//				stmt.setString(parameterIndex ++, contentType);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRisposta";
		boolean delegata = false;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRisposta";
		boolean delegata = true;
		return _existsTrasformazioneRisposta(idPorta, idTrasformazione, nome, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectCountField(nomeTabellaRisposta+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".nome = ?");
			
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			
			stmt.setString(parameterIndex ++, nome);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteDelegateTrasformazioniServiziApplicativiList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO;
		String nomeMetodo = "porteDelegateTrasformazioniServiziApplicativiList";
		boolean delegata = true;
		return _getTrasformazioniServiziApplicativiList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteApplicativeTrasformazioniServiziApplicativiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SERVIZIO_APPLICATIVO_AUTORIZZATO;
		String nomeMetodo = "porteApplicativeTrasformazioniServiziApplicativiList";
		boolean delegata = false;
		return _getTrasformazioniServiziApplicativiList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaApplicabilitaServizioApplicativo> _getTrasformazioniServiziApplicativiList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaTrasformazioneSA = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA;
		String nomeTabellaSA = CostantiDB.SERVIZI_APPLICATIVI;
		String nomeTabellaSoggetti = CostantiDB.SOGGETTI;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaApplicabilitaServizioApplicativo> lista = new ArrayList<TrasformazioneRegolaApplicabilitaServizioApplicativo>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
			sqlQueryObject.addSelectCountField(nomeTabellaTrasformazioneSA+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
			sqlQueryObject.addFromTable(nomeTabellaSA);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectAliasField(nomeTabellaTrasformazioneSA, "id", "idTrasSA");
			sqlQueryObject.addSelectField(nomeTabellaSA+".nome");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaSA+".id="+nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_soggetto="+nomeTabellaSoggetti+".id");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaSA+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasSA"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaTrasformazioneSA);
					sqlQueryObject.addFromTable(nomeTabellaSA);
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addSelectField(nomeTabellaSA+".nome");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id_trasformazione");
					sqlQueryObject.addSelectField(nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(nomeTabellaTrasformazioneSA+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaTrasformazioneSA+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaSA+".id="+nomeTabellaTrasformazioneSA+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(nomeTabellaSA+".id_soggetto="+nomeTabellaSoggetti+".id");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);
					
					rs = stmt.executeQuery();
					if(rs.next()) {
						TrasformazioneRegolaApplicabilitaServizioApplicativo risposta = new TrasformazioneRegolaApplicabilitaServizioApplicativo();
						risposta.setId(rs.getLong("id"));
						risposta.setNome(rs.getString("nome"));
						risposta.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
						risposta.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));
						lista.add(risposta);		
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	public List<TrasformazioneRegolaApplicabilitaSoggetto> porteApplicativeTrasformazioniSoggettiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTO;
		String nomeMetodo = "porteApplicativeTrasformazioniSoggettiList";
		boolean delegata = false;
		return _getTrasformazioniSoggettiList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaApplicabilitaSoggetto> _getTrasformazioniSoggettiList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaSoggetti = CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaApplicabilitaSoggetto> lista = new ArrayList<TrasformazioneRegolaApplicabilitaSoggetto>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectCountField(nomeTabellaSoggetti+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaSoggetti);
			sqlQueryObject.addSelectAliasField(nomeTabellaSoggetti, "id", "idTrasSoggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id");
			sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addOrderBy(nomeTabellaSoggetti+".nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasSoggetto"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaSoggetti);
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".tipo_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".nome_soggetto");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id");
					sqlQueryObject.addSelectField(nomeTabellaSoggetti+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaSoggetti+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaSoggetti+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();
					
					if(rs.next()) {
						TrasformazioneRegolaApplicabilitaSoggetto risposta = new TrasformazioneRegolaApplicabilitaSoggetto();
						risposta.setId(rs.getLong("id"));
						risposta.setTipo(rs.getString("tipo_soggetto"));
						risposta.setNome(rs.getString("nome_soggetto"));
						lista.add(risposta);
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRispostaHeaderList(long idPD, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER;
		String nomeMetodo = "porteDelegateTrasformazioniRispostaHeaderList";
		boolean delegata = true;
		return _getTrasformazioniRispostaHeaderList(idPD, idTrasformazione, idTrasformazioneRisposta, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRispostaHeaderList(long idPA, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
		String nomeMetodo = "porteApplicativeTrasformazioniRispostaHeaderList";
		boolean delegata = false;
		return _getTrasformazioniRispostaHeaderList(idPA, idTrasformazione, idTrasformazioneRisposta, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> _getTrasformazioniRispostaHeaderList(long idPA, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRispostaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);
			stmt.setLong(3, idTrasformazioneRisposta);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRispostaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);
			stmt.setLong(3, idTrasformazioneRisposta);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRisposta);
					sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
					sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();

					if(rs.next()) {
						
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
						
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	public boolean existsPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRispostaHeader";
		boolean delegata = false;
		return _existsTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRispostaHeader";
		boolean delegata = true;
		return _existsTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addFromTable(nomeTabellaRispostaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRispostaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setLong(parameterIndex ++, idTrasformazioneRisposta);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRispostaHeader";
		boolean delegata = false;
		return _getTrasformazioneRispostaHeader(idPorta, idTrasformazione,  idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRispostaHeader";
		boolean delegata = true;
		return _getTrasformazioneRispostaHeader(idPorta, idTrasformazione,  idTrasformazioneRisposta, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro _getTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRisposta = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
		String nomeTabellaRispostaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRisposta);
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".id_transform_risp=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRisposta+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRisposta+".id="+nomeTabellaRispostaHeader+".id_transform_risp");
			sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRispostaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setLong(parameterIndex ++, idTrasformazioneRisposta);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
	
	public List<ResponseCachingConfigurazioneRegola> portaApplicativaResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		String nomeMetodo = "portaApplicativaResponseCachingConfigurazioneRegolaList";
		boolean delegata = false;
		return _getResponseCachingConfigurazioneRegolaList(idPA, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<ResponseCachingConfigurazioneRegola> portaDelegataResponseCachingConfigurazioneRegolaList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "portaDelegataResponseCachingConfigurazioneRegolaList";
		int idLista = Liste.PORTE_DELEGATE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		boolean delegata = true;
		return _getResponseCachingConfigurazioneRegolaList(idPD, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<ResponseCachingConfigurazioneRegola> _getResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<ResponseCachingConfigurazioneRegola> lista = new ArrayList<ResponseCachingConfigurazioneRegola>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabella+".id");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);

			risultato = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (risultato.next()) { 
				idLong.add(risultato.getLong("id"));
			}
			risultato.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongRegola : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition(nomeTabella+".id=?");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongRegola);

					risultato = stmt.executeQuery();
					
					if(risultato.next()) {
					
						ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
						
						regola.setId(risultato.getLong("id"));
						int status_min = risultato.getInt("status_min");
						int status_max = risultato.getInt("status_max");
						if(status_min>0) {
							regola.setReturnCodeMin(status_min);
						}
						if(status_max>0) {
							regola.setReturnCodeMax(status_max);
						}

						int fault = risultato.getInt("fault");
						if(CostantiDB.TRUE == fault) {
							regola.setFault(true);
						}
						else if(CostantiDB.FALSE == fault) {
							regola.setFault(false);
						}
						
						int cacheSeconds = risultato.getInt("cache_seconds");
						if(cacheSeconds>0) {
							regola.setCacheTimeoutSeconds(cacheSeconds);
						}

						lista.add(regola);
						
					}
					
					risultato.close();
					stmt.close();
				}
				
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return lista;
	}
	
	public boolean existsPortaApplicativaResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaResponseCachingConfigurazioneRegola";
		boolean delegata = false;
		return _existsResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataResponseCachingConfigurazioneRegola";
		boolean delegata = true;
		return _existsResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault, nomeMetodo, delegata);
	}
	
	private boolean _existsResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault,String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectCountField(nomeTabella+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition("status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition("status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_max");
			}
			
			if(fault) {
				sqlQueryObject.addWhereCondition("fault = ?");
			} else {
				sqlQueryObject.addWhereCondition("fault = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPA);
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
			if(fault) {
				stmt.setInt(parameterIndex ++, CostantiDB.TRUE);
			} else {
				stmt.setInt(parameterIndex ++, CostantiDB.FALSE);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaHeaderList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_HEADER;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaHeaderList";
		boolean delegata = true;
		return _getTrasformazioniRichiestaHeaderList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaHeaderList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_HEADER;
		String nomeMetodo = "porteApplicativeTrasformazioniRichiestaHeaderList";
		boolean delegata = false;
		return _getTrasformazioniRichiestaHeaderList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> _getTrasformazioniRichiestaHeaderList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRichiestaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);

					rs = stmt.executeQuery();
					
					if(rs.next()) {
						
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
						
					}
					
					rs.close();
					stmt.close();
					
				}
				
			}
			

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	public boolean existsPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRichiestaHeader";
		boolean delegata = false;
		return _existsTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRichiestaHeader";
		boolean delegata = true;
		return _existsTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRichiestaHeader";
		boolean delegata = false;
		return _getTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRichiestaHeader";
		boolean delegata = true;
		return _getTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro _getTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaUrlParameterList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
		String nomeMetodo = "porteDelegateTrasformazioniRichiestaUrlParameterList";
		boolean delegata = true;
		return _getTrasformazioniRichiestaUrlParameterList(idPD, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaUrlParameterList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RICHIESTA_PARAMETRI;
		String nomeMetodo = "porteApplicativeTrasformazioniRichiestaUrlParameterList";
		boolean delegata = false;
		return _getTrasformazioniRichiestaUrlParameterList(idPA, idTrasformazione, ricerca, idLista, nomeMetodo, delegata);
	}
	
	private List<TrasformazioneRegolaParametro> _getTrasformazioniRichiestaUrlParameterList(long idPA, long idTrasformazione, ISearch ricerca, int idLista, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		int offset; 
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<TrasformazioneRegolaParametro> lista = new ArrayList<TrasformazioneRegolaParametro>();
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectAliasField(nomeTabellaRichiestaHeader, "id", "idTrasParametro");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPA);
			stmt.setLong(2, idTrasformazione);

			rs = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (rs.next()) { 
				idLong.add(rs.getLong("idTrasParametro"));
			}
			rs.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongTrasf : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(nomeTabella);
					sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
					sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id=?");
					sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
					sqlQueryObject.setANDLogicOperator(true);
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongTrasf);
					
					rs = stmt.executeQuery();
					
					if(rs.next()) {
						TrasformazioneRegolaParametro parametro = new TrasformazioneRegolaParametro();
						parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
						parametro.setNome(rs.getString("nome"));
						parametro.setValore(rs.getString("valore"));
						parametro.setId(rs.getLong("id"));
						lista.add(parametro);
					}
					
					rs.close();
					stmt.close();
					
				}
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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
		
		return lista;
	} 
	
	public boolean existsPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaApplicativaTrasformazioneRichiestaUrlParameter";
		boolean delegata = false;
		return _existsTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public boolean existsPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "existsPortaDelegataTrasformazioneRichiestaUrlParameter";
		boolean delegata = true;
		return _existsTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private boolean _existsTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addFromTable(nomeTabellaRichiestaHeader);
			sqlQueryObject.addSelectCountField(nomeTabellaRichiestaHeader+".id", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaApplicativaTrasformazioneRichiestaUrlParameter";
		boolean delegata = false;
		return _getTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		String nomeMetodo = "getPortaDelegataTrasformazioneRichiestaUrlParameter";
		boolean delegata = true;
		return _getTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo, nomeMetodo, delegata);
	}
	
	private TrasformazioneRegolaParametro _getTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo, String nomeMetodo, boolean portaDelegata) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet rs =null;
		String queryString;
		String nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI;
		String nomeTabellaRichiestaHeader = portaDelegata ? CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL : CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		TrasformazioneRegolaParametro parametro = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(nomeTabella);
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".nome");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".valore");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".tipo");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id");
			sqlQueryObject.addSelectField(nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabella+".id_porta=?");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".id_trasformazione=?");
			sqlQueryObject.addWhereCondition(nomeTabella+".id="+nomeTabellaRichiestaHeader+".id_trasformazione");
			sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".nome = ?");
			if(checkTipo) {
				sqlQueryObject.addWhereCondition(nomeTabellaRichiestaHeader+".tipo = ?");
			}
			sqlQueryObject.setANDLogicOperator(true);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setLong(parameterIndex ++, idPorta);
			stmt.setLong(parameterIndex ++, idTrasformazione);
			stmt.setString(parameterIndex ++, nome);
			if(checkTipo) {
				stmt.setString(parameterIndex ++, tipo);
			}
			
			rs = stmt.executeQuery();
			if (rs.next()) {
				parametro = new TrasformazioneRegolaParametro();
				parametro.setConversioneTipo(DriverConfigurazioneDB_LIB.getEnumTrasformazioneRegolaParametroTipoAzione(rs.getString("tipo")));
				parametro.setNome(rs.getString("nome"));
				parametro.setValore(rs.getString("valore"));
				parametro.setId(rs.getLong("id"));
			}
			
			rs.close();
			stmt.close();

			return parametro;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
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

    @Override
    public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this._getServizioApplicativo(idServizioApplicativo, null, null, 
            		null, null, null, false,
            		null, 
            		null,
            		null,
            		false, false);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this._getServizioApplicativo(null, aUser, aPassword, 
            		null, null, null, false,
            		null, 
            		null,
            		config,
            		false, false);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this._getServizioApplicativo(null, aUser, aPassword, 
            		null, null, null, false,
            		null, 
            		null,
            		config,
            		true, appId);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String aSubject, String aIssuer) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
            return this._getServizioApplicativo(null, null, null, 
            		aSubject, aIssuer, null, false,
            		null, 
            		null,
            		null,
            		false, false);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	 return this._getServizioApplicativo(null, null, null, 
         		null, null, certificate, strictVerifier,
         		null, 
         		null,
         		null,
         		false, false);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this._getServizioApplicativo(null, null, null, 
    			null, null, null, false,
    			principal, 
    			null,
    			null,
    			false, false);
    }
    public ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
            return this._getServizioApplicativo(null, null, null, 
            		null, null, null, false,
            		null, 
            		idServizioApplicativo,
            		null,
            		false, false);
    }

    private ServizioApplicativo _getServizioApplicativo(IDServizioApplicativo idServizioApplicativoObject, 
    		String aUser, String aPassord, 
    		String aSubject, String aIssuer, CertificateInfo aCertificate, boolean aStrictVerifier, 
    		String principal,
    		Long idServizioApplicativo, 
    		CryptConfig config,
    		boolean apiKey, boolean appId) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

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
		else{
			type = TYPE_ID_LONG;
			if(idServizioApplicativo==null || idServizioApplicativo.longValue()<=0){
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Id DB non definito.");
			}
		}

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getServizioApplicativo");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			switch (type) {
			case TYPE_ID_OBJECT:{
				String tipoSog=idServizioApplicativoObject.getIdSoggettoProprietario().getTipo();
				String nomeSog=idServizioApplicativoObject.getIdSoggettoProprietario().getNome();

				long idSog=DBUtils.getIdSoggetto(nomeSog, tipoSog, con, this.tipoDB,this.tabellaSoggetti);

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome = ?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, nome_sa);
				stm.setLong(2, idSog);
				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, nome_sa));

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
						crypt = CryptFactory.getCrypt(this.log, config);
					}
				}
				else {
					tipoCredenziale = CostantiConfigurazione.CREDENZIALE_APIKEY.toString();
					if(config!=null) {
						crypt = CryptFactory.getCrypt(this.log, config);
					}
					else {
						testInChiaro = true;
					}
				}
				
				//cerco un servizio applicativo che contenga utente e password con autenticazione basi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
					stm.setString(3, CostantiDB.getISSUER_APIKEY(appId));
				}

				if(apiKey) {
					this.log.debug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, tipoCredenziale, aUser, CostantiDB.getISSUER_APIKEY(appId)));
				}
				else {
					this.log.debug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, tipoCredenziale, aUser));
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));
				
				break;
			}	
			case TYPE_SSL_SUBJECT_ISSUER:{

				// Autenticazione SSL deve essere LIKE
				Hashtable<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoHashtable(aSubject, PrincipalType.subject);
				Hashtable<String, List<String>> hashIssuer = null;
				if(StringUtils.isNotEmpty(aIssuer)) {
					hashIssuer = CertificateUtils.getPrincipalIntoHashtable(aIssuer, PrincipalType.issuer);
				}

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");

				Enumeration<String> keys = hashSubject.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					
					List<String> listValues = hashSubject.get(key);
					for (String value : listValues) {
						sqlQueryObject.addWhereLikeCondition("subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
					}
				}
				
				if(hashIssuer!=null) {
					keys = hashIssuer.keys();
					while(keys.hasMoreElements()){
						String key = keys.nextElement();
						
						List<String> listValues = hashIssuer.get(key);
						for (String value : listValues) {
							sqlQueryObject.addWhereLikeCondition("issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
						}
					}
				}
				else {
					sqlQueryObject.addWhereIsNullCondition("issuer");
				}

				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_SSL.toString());

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
					
					String subjectPotenziale =  rs.getString("subject");
					boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, aSubject, PrincipalType.subject, this.log);
					
					boolean issuerValid = true;
					if(hashIssuer!=null) {
						String issuerPotenziale =  rs.getString("issuer");
						if(StringUtils.isNotEmpty(issuerPotenziale)) {
							issuerValid = CertificateUtils.sslVerify(issuerPotenziale, aIssuer, PrincipalType.issuer, this.log);
						}
						else {
							issuerValid = false;
						}
					}
					
					
					if( subjectValid && issuerValid ) {
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));

				break;
			}
			case TYPE_SSL_CERTIFICATE:{

				String cnSubject = aCertificate.getSubject().getCN();
				String cnIssuer = aCertificate.getIssuer().getCN();

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				sqlQueryObject.addWhereCondition("cn_subject = ?");
				sqlQueryObject.addWhereCondition("cn_issuer = ?");
				sqlQueryObject.addWhereCondition("cert_strict_verification = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]["+CostantiConfigurazione.CREDENZIALE_SSL.toString()+"]");

				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_SSL.toString());
				stm.setString(2, cnSubject);
				stm.setString(3, cnIssuer);
				if(aStrictVerifier) {
					stm.setInt(4, CostantiDB.TRUE);
				}
				else {
					stm.setInt(4, CostantiDB.FALSE);
				}

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));

				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
				
				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					// Possono esistere piu' sil che hanno un CN con subject e issuer diverso.
					
					byte[] certificatoBytes = jdbcAdapter.getBinaryData(rs, "certificate");
					Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
					//int tmpStrict = rs.getInt("cert_strict_verification");
					//boolean strict = tmpStrict == CostantiDB.TRUE;
					
					if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
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
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				stm = con.prepareStatement(sqlQueryObject.toString());
				stm.setLong(1, idSA);

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idSA));

				break;
			}	
			case TYPE_PRINCIPAL:{

				//cerco un servizio applicativo che contenga utente e password con autenticazione basi
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				this.log.debug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString(), principal));

				break;
			}
			case TYPE_ID_LONG:{
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
				break;
			}
			
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
				sa.setTipoSoggettoProprietario(getSoggetto(sa.getIdSoggetto(),con).getTipo());
				sa.setNomeSoggettoProprietario(getSoggetto(sa.getIdSoggetto(),con).getNome());
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
					IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
					credenziali.setCertificate(jdbcAdapter.getBinaryData(rs, "certificate"));
					int strict = rs.getInt("cert_strict_verification");
					if(strict == CostantiDB.TRUE) {
						credenziali.setCertificateStrictVerification(true);
					}
					else if(strict == CostantiDB.FALSE) {
						credenziali.setCertificateStrictVerification(false);
					}
					
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
					
					Connettore connettore = DriverConfigurazioneDB_LIB.getConnettore(idConnettore, con);
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
						gestioneErroreRispostaAsincrona = DriverConfigurazioneDB_LIB.getGestioneErrore(idGestioneErroreRispostaAsincrona, con);
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
					Connettore connserv = DriverConfigurazioneDB_LIB.getConnettore(idConnettore, con);
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
						gestioneErroreInvocazioneServizio = DriverConfigurazioneDB_LIB.getGestioneErrore(idGestioneErroreInvocazioneServizio, con);
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
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				
				
				// Protocol Properties
				try{
					List<ProtocolProperty> listPP = DriverConfigurazioneDB_LIB.getListaProtocolProperty(sa.getId(), ProprietariProtocolProperty.SERVIZIO_APPLICATIVO, con, this.tipoDB);
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

	}

	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {

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

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizioApplicativo type = 1");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDServizioApplicativo(1, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServizioApplicativo] Errore durante la creazione del servizioApplicativo : " + qe.getMessage(),qe);
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
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
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

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizioApplicativo type = 2");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDServizioApplicativo(2, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("Errore durante l'aggiornamento del servizioApplicativo : " + qe.getMessage(), qe);
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
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
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

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteServizioApplicativo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDServizioApplicativo type = 3");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDServizioApplicativo(3, aSA, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServizioApplicativo] Errore durante la delete del servizioApplicativo : " + qe.getMessage(),qe);
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
	 * Restituisce la RoutingTable definita nella Porta di Dominio
	 * 
	 * @return RoutingTable
	 * 
	 */
	@Override
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stmSearch = null;
		ResultSet rsSearch = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getRoutingTable");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			boolean routingEnabled = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query per routing enabled : " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				this.log.debug("ConfigurazionePresente");
				this.log.debug("Risultato query per routing enabled ["+rs.getString("routing_enabled")+"]");
				routingEnabled = CostantiConfigurazione.ABILITATO.equals(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("routing_enabled")));
			}
			rs.close();
			stm.close();
			this.log.debug("RoutingEnabled: "+routingEnabled);

			RoutingTable rt = new RoutingTable();
			rt.setAbilitata(routingEnabled);
			//sia che il routing sia abilitato/disabilitato
			//le rotte possono essere comunque presenti
			//if (routingEnabled) {

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ROUTING);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			this.log.debug("eseguo query per routing table : " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);

			rs = stm.executeQuery();

			String tipo = null;
			String nome = null;
			String tipoRotta = null;
			String tiposoggrotta = null;
			String nomesoggrotta = null;
			long id_registrorotta = 0;
			boolean is_default = false;
			long idR = 0;

			RoutingTableDefault rtdefault = null;
			Route route = null;
			RouteGateway routeGateway = null;
			RouteRegistro routeRegistro = null;
			RoutingTableDestinazione rtd = null;

			int nroute = 0;
			this.log.debug("Check esistenza rotte....");
			while (rs.next()) {

				this.log.debug("Nuova rotta....["+rs.getInt("is_default")+"]");

				nroute++;
				// nuova route
				route = new Route();

				idR = rs.getLong("id");
				tipo = rs.getString("tipo");
				nome = rs.getString("nome");
				tipoRotta = rs.getString("tiporotta");
				nomesoggrotta = rs.getString("nomesoggrotta");
				tiposoggrotta = rs.getString("tiposoggrotta");
				id_registrorotta = rs.getLong("registrorotta");
				if(rs.getInt("is_default")==1)
					is_default = true;
				else
					is_default = false;

				if (tipoRotta.equalsIgnoreCase("registro")) {
					// e' una rotta registro
					routeRegistro = new RouteRegistro();

					// se e' 0 allora significa ke voglio tutte le rotte
					if (id_registrorotta != 0) {
						// mi serve il nome di questa rotta
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id = ?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stmSearch = con.prepareStatement(sqlQuery);
						stmSearch.setLong(1, id_registrorotta);

						this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, id_registrorotta));

						rsSearch = stmSearch.executeQuery();
						if (rsSearch.next()) {
							routeRegistro.setNome(rsSearch.getString("nome"));

						}
						rsSearch.close();
						stmSearch.close();
					}

					route.setRegistro(routeRegistro);

				} else if (tipoRotta.equalsIgnoreCase("gateway")) {
					// e' una rotta gw
					routeGateway = new RouteGateway();

					routeGateway.setNome(nomesoggrotta);
					routeGateway.setTipo(tiposoggrotta);

					route.setGateway(routeGateway);
				}

				route.setId(idR);

				// e' di default
				if (is_default){
					if(rtdefault==null){
						rtdefault = new RoutingTableDefault();
						rt.setDefault(rtdefault);
					}
					rt.getDefault().addRoute(route);
				}
				else {// allora e' di destinazione
					rtd = new RoutingTableDestinazione();
					rtd.setNome(nome);
					rtd.setTipo(tipo);
					rtd.addRoute(route);
					rt.addDestinazione(rtd);
				}
			}

			this.log.debug("Ci sono " + nroute + " rotte configurate.");
			rs.close();
			stm.close();
			//if (nroute == 0)
			//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRoutingTable] Routing Abilitato ma nessuna route trovata.]");



			//}
			//else {
			//	throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRoutingTable] Routing Disabilitato]");
			//}

			return rt;


		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRoutingTable] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			try{
				if(rsSearch!=null) rsSearch.close();
				if(stmSearch!=null) stmSearch.close();
			}catch (Exception e) {
				//ignore
			}try{
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

	}

	@Override
	public void createRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {

		if (routingTable == null ||  routingTable.getDefault() == null || routingTable.getDefault().sizeRouteList() == 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDRoutingTable type = 1");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDRoutingTable(1, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Errore durante la creazione della RoutingTable : " + qe.getMessage(),qe);
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

	@Override
	public void updateRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		if (routingTable == null ||  routingTable.getDefault() == null || routingTable.getDefault().sizeRouteList() == 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDRoutingTable type = 2");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDRoutingTable(2, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateRoutingTable] Errore durante la update della RoutingTable : " + qe.getMessage(),qe);
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

	@Override
	public void deleteRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		if (routingTable == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Parametri non validi.");

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteRoutingTable");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDRoutingTable type = 3");
			// creo soggetto
			DriverConfigurazioneDB_LIB.CRUDRoutingTable(3, routingTable, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteRoutingTable] Errore durante la delete della RoutingTable : " + qe.getMessage(),qe);
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

	public List<RoutingTableDestinazione> routingList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "routingList";
		int idLista = Liste.ROUTING;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<RoutingTableDestinazione> lista = new ArrayList<RoutingTableDestinazione>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("routingList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo",search,true,true), sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("is_default = 0");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
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
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("tiporotta");
				sqlQueryObject.addSelectField("is_default");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo",search,true,true), sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ROUTING);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addSelectField("tiporotta");
				sqlQueryObject.addSelectField("is_default");
				sqlQueryObject.addWhereCondition("is_default = 0");
				sqlQueryObject.addOrderBy("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			RoutingTableDestinazione rtd;
			while (risultato.next()) {

				rtd = new RoutingTableDestinazione();

				rtd.setId(risultato.getLong("id"));
				rtd.setNome(risultato.getString("nome"));
				rtd.setTipo(risultato.getString("tipo"));
				// Non è necessario popolare rg e rr
				// perchè tanto il metodo che prepara la lista
				// deve solo decidere il tiporotta scelto
				Route tmpR = new Route();
				if ("gateway".equals(risultato.getString("tiporotta"))) {
					RouteGateway rg = new RouteGateway();
					tmpR.setGateway(rg);
				} else {
					RouteRegistro rr = new RouteRegistro();
					tmpR.setRegistro(rr);
				}
				rtd.addRoute(tmpR);

				lista.add(rtd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	 * Restituisce l'accesso al registro definito nella Porta di Dominio
	 * 
	 * @return AccessoRegistro
	 * 
	 */
	@Override
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoRegistro");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoRegistro car = new AccessoRegistro();
			Cache cache = null;

			// aggiungo i registri
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			AccessoRegistroRegistro itemcar = null;

			while (rs.next()) {
				itemcar = new AccessoRegistroRegistro();

				// TIPO
				RegistroTipo tipoReg = CostantiConfigurazione.REGISTRO_XML;
				String tmpTipo = rs.getString("tipo");
				if (tmpTipo.equals("uddi"))
					tipoReg = CostantiConfigurazione.REGISTRO_UDDI;
				else if (tmpTipo.equals("web"))
					tipoReg = CostantiConfigurazione.REGISTRO_WEB;
				else if (tmpTipo.equals("db"))
					tipoReg = CostantiConfigurazione.REGISTRO_DB;
				else if (tmpTipo.equals("ws"))
					tipoReg = CostantiConfigurazione.REGISTRO_WS;

				itemcar.setTipo(tipoReg);

				// nome
				itemcar.setNome(rs.getString("nome"));

				// location
				itemcar.setLocation(rs.getString("location"));

				// USER e PASSWORD
				String tmpUser = rs.getString("utente");
				String tmpPw = rs.getString("password");
				if ((tmpUser != null) && (tmpPw != null) && !tmpUser.equals("") && !tmpPw.equals("")) {
					itemcar.setUser(tmpUser);
					itemcar.setPassword(tmpPw);
				}

				car.addRegistro(itemcar);
			}
			rs.close();
			stm.close();

			//fix bug 23/11/07
			//se nn trovo registri non va lanciata l'eccezione in quanto
			//possono esistere variabili di configurazione successive che vanno lette
			//se nn ho trovato registri allora lancio eccezione
			//if(itemcar==null) throw new DriverConfigurazioneNotFound("Nessun registro trovato");

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("idlecache");
					String tmpLife = rs.getString("lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					car.setCache(cache);

				}
				rs.close();
				stm.close();
			}
			//nel caso in cui non esiste nessuna configurazione non lancio eccezioni
			//perche' possono essere presenti dei registri
			/*else{
				rs.close();
				stm.close();
				throw new DriverConfigurazioneNotFound("Nessuna Configurazione trovata.");
			}*/



			return car;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoRegistro] Exception: " + se.getMessage(),se);
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
	}

	public void createAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 1");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(1, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Errore durante la createAccessoRegistro : " + qe.getMessage(),qe);
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

	public void updateAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 2");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(2, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
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

	public void deleteAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 3");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(3, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Errore durante la deleteAccessoRegistro : " + qe.getMessage(),qe);
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
	
	
	
	
	@Override
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoConfigurazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoConfigurazione accessoConfigurazione = new AccessoConfigurazione();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("config_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("config_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("config_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("config_idlecache");
					String tmpLife = rs.getString("config_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoConfigurazione.setCache(cache);

				}
				rs.close();
				stm.close();
			}

			return accessoConfigurazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoConfigurazione] Exception: " + se.getMessage(),se);
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
	}

	public void createAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoConfigurazione type = 1");
			DriverConfigurazioneDB_LIB.CRUDAccessoConfigurazione(1, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoConfigurazione] Errore durante la createAccessoConfigurazione : " + qe.getMessage(),qe);
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

	public void updateAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoConfigurazione type = 2");
			DriverConfigurazioneDB_LIB.CRUDAccessoConfigurazione(2, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoConfigurazione] Errore durante la updateAccessoConfigurazione : " + qe.getMessage(),qe);
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

	public void deleteAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteAccessoConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoConfigurazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoConfigurazione type = 3");
			DriverConfigurazioneDB_LIB.CRUDAccessoConfigurazione(3, accessoConfigurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoConfigurazione] Errore durante la deleteAccessoConfigurazione : " + qe.getMessage(),qe);
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
	
	
	
	
	
	@Override
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoDatiAutorizzazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoDatiAutorizzazione accessoDatiAutorizzazione = new AccessoDatiAutorizzazione();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("auth_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("auth_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("auth_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("auth_idlecache");
					String tmpLife = rs.getString("auth_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoDatiAutorizzazione.setCache(cache);

				}
				rs.close();
				stm.close();
			}

			return accessoDatiAutorizzazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutorizzazione] Exception: " + se.getMessage(),se);
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
	}
	
	
	
	@Override
	public AccessoDatiAutenticazione getAccessoDatiAutenticazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoDatiAutenticazione");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoDatiAutenticazione accessoDatiAutenticazione = new AccessoDatiAutenticazione();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("authn_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("authn_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("authn_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("authn_idlecache");
					String tmpLife = rs.getString("authn_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoDatiAutenticazione.setCache(cache);

				}
				rs.close();
				stm.close();
			}

			return accessoDatiAutenticazione;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiAutenticazione] Exception: " + se.getMessage(),se);
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
	}
	
	
	@Override
	public AccessoDatiGestioneToken getAccessoDatiGestioneToken() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoDatiGestioneToken");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoDatiGestioneToken accessoDatiGestioneToken = new AccessoDatiGestioneToken();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("token_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("token_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("token_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("token_idlecache");
					String tmpLife = rs.getString("token_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoDatiGestioneToken.setCache(cache);

				}
				rs.close();
				stm.close();
			}

			return accessoDatiGestioneToken;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiGestioneToken] Exception: " + se.getMessage(),se);
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
	}
	
	
	
	@Override
	public AccessoDatiKeystore getAccessoDatiKeystore() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoDatiKeystore");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoDatiKeystore accessoDatiKeystore = new AccessoDatiKeystore();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("keystore_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("keystore_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("keystore_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("keystore_idlecache");
					String tmpLife = rs.getString("keystore_lifecache");
					String tmpCrlLife = rs.getString("keystore_crl_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoDatiKeystore.setCache(cache);
					
					if (tmpCrlLife != null && !tmpCrlLife.equals(""))
						accessoDatiKeystore.setCrlItemLifeSecond(tmpCrlLife);

				}
				rs.close();
				stm.close();
			}

			return accessoDatiKeystore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiKeystore] Exception: " + se.getMessage(),se);
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
	}
	
	
	
	@Override
	public AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getAccessoDatiConsegnaApplicativi");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			AccessoDatiConsegnaApplicativi accessoDatiConsegnaApplicativi = new AccessoDatiConsegnaApplicativi();
			Cache cache = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			this.log.debug("eseguo query : " + sqlQuery);

			rs = stm.executeQuery();

			if (rs.next()) {
				String tmpCache = rs.getString("consegna_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					cache = new Cache();

					String tmpDim = rs.getString("consegna_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("consegna_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("consegna_idlecache");
					String tmpLife = rs.getString("consegna_lifecache");
					
					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					accessoDatiConsegnaApplicativi.setCache(cache);
					
				}
				rs.close();
				stm.close();
			}

			return accessoDatiConsegnaApplicativi;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getAccessoDatiConsegnaApplicativi] Exception: " + se.getMessage(),se);
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
	}
	
	

	public void createAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoDatiAutorizzazione type = 1");
			DriverConfigurazioneDB_LIB.CRUDAccessoDatiAutorizzazione(1, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoDatiAutorizzazione] Errore durante la createAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
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

	public void updateAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoDatiAutorizzazione type = 2");
			DriverConfigurazioneDB_LIB.CRUDAccessoDatiAutorizzazione(2, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoDatiAutorizzazione] Errore durante la updateAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
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

	public void deleteAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteAccessoDatiAutorizzazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoDatiAutorizzazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoDatiAutorizzazione type = 3");
			DriverConfigurazioneDB_LIB.CRUDAccessoDatiAutorizzazione(3, accessoDatiAutorizzazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoDatiAutorizzazione] Errore durante la deleteAccessoDatiAutorizzazione : " + qe.getMessage(),qe);
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
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGestioneErrore(true);
	}

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGestioneErrore(false);
	}


	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	private GestioneErrore getGestioneErrore(boolean cooperazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sqlQuery = "";

		String tipoOperazione = "GestioneErrore per componente di Cooperazione";
		if(cooperazione==false)
			tipoOperazione = "GestioneErrore per componente di Integrazione";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getGestioneErrore("+cooperazione+")");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("["+tipoOperazione+"] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			GestioneErrore gestioneErrore = null;
			String nomeColonna = "id_ge_cooperazione";
			if(cooperazione==false)
				nomeColonna = "id_ge_integrazione";
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField(nomeColonna);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			if (rs.next()) {

				Long idGestioneErrore = rs.getLong(nomeColonna);
				if(idGestioneErrore>0){
					gestioneErrore = DriverConfigurazioneDB_LIB.getGestioneErrore(idGestioneErrore, con);
				}

			} 

			rs.close();
			stm.close();

			if(gestioneErrore==null){
				throw new DriverConfigurazioneNotFound(tipoOperazione +" non presente.");
			}
			return gestioneErrore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException(tipoOperazione +" SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException(tipoOperazione +" Exception: " + se.getMessage(),se);
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

	}




	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stmFiltri = null;
		ResultSet rsFiltri = null;

		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getStatoServiziPdD");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			StatoServiziPdd servizi = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_PDD);
			sqlQuery = sqlQueryObject.createSQLQuery();

			ISQLQueryObject sqlQueryObjectFiltri = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectFiltri.addFromTable(CostantiDB.SERVIZI_PDD_FILTRI);
			sqlQueryObjectFiltri.addWhereCondition("id_servizio_pdd=?");
			String sqlQueryFiltro = sqlQueryObjectFiltri.createSQLQuery();

			this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();


			while (rs.next()) {

				if(servizi==null){
					servizi = new StatoServiziPdd();
				}

				long id = rs.getLong("id");

				String tipo = rs.getString("componente");

				int servizio = rs.getInt("stato");
				StatoFunzionalita stato = null;
				if(servizio==0)
					stato = CostantiConfigurazione.DISABILITATO;
				else if(servizio==1)
					stato = CostantiConfigurazione.ABILITATO;
				else
					stato = CostantiConfigurazione.ABILITATO; // default

				if(CostantiDB.COMPONENTE_SERVIZIO_PD.equals(tipo)){

					StatoServiziPddPortaDelegata sPD = new StatoServiziPddPortaDelegata();
					sPD.setStato(stato);

					this.log.debug("eseguo query filtro: " + DBUtils.formatSQLString(sqlQueryFiltro,id));
					stmFiltri = con.prepareStatement(sqlQueryFiltro);
					stmFiltri.setLong(1, id);
					rsFiltri = stmFiltri.executeQuery();
					riempiFiltriServiziPdD(rsFiltri, sPD.getFiltroAbilitazioneList(), sPD.getFiltroDisabilitazioneList());
					rsFiltri.close();
					stmFiltri.close();

					servizi.setPortaDelegata(sPD);
				}
				else if(CostantiDB.COMPONENTE_SERVIZIO_PA.equals(tipo)){

					StatoServiziPddPortaApplicativa sPA = new StatoServiziPddPortaApplicativa();
					sPA.setStato(stato);

					this.log.debug("eseguo query filtro: " + DBUtils.formatSQLString(sqlQueryFiltro,id));
					stmFiltri = con.prepareStatement(sqlQueryFiltro);
					stmFiltri.setLong(1, id);
					rsFiltri = stmFiltri.executeQuery();
					riempiFiltriServiziPdD(rsFiltri, sPA.getFiltroAbilitazioneList(), sPA.getFiltroDisabilitazioneList());
					rsFiltri.close();
					stmFiltri.close();

					servizi.setPortaApplicativa(sPA);
				}
				else if(CostantiDB.COMPONENTE_SERVIZIO_IM.equals(tipo)){

					StatoServiziPddIntegrationManager sIM = new StatoServiziPddIntegrationManager();
					sIM.setStato(stato);

					servizi.setIntegrationManager(sIM);
				}
			} 

			if(servizi==null)
				throw new DriverConfigurazioneNotFound("Configurazione servizi attivi sulla PdD non presente");

			rs.close();
			stm.close();

			return servizi;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getServiziAttiviPdD]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getServiziAttiviPdD]  Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rsFiltri!=null) rsFiltri.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmFiltri!=null) stmFiltri.close();
			}catch (Exception e) {
				//ignore
			}
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
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}

	private void riempiFiltriServiziPdD(ResultSet rsFiltri,List<TipoFiltroAbilitazioneServizi> listaAbilitazioni,List<TipoFiltroAbilitazioneServizi> listaDisabilitazioni) throws SQLException{
		while(rsFiltri.next()){

			String tipo = rsFiltri.getString("tipo_filtro");

			TipoFiltroAbilitazioneServizi tipoFiltro = new TipoFiltroAbilitazioneServizi();

			String tipoSoggettoFruitore = rsFiltri.getString("tipo_soggetto_fruitore");
			tipoFiltro.setTipoSoggettoFruitore(tipoSoggettoFruitore);
			String soggettoFruitore = rsFiltri.getString("soggetto_fruitore");
			tipoFiltro.setSoggettoFruitore(soggettoFruitore);
			String identificativoPortaFruitore = rsFiltri.getString("identificativo_porta_fruitore");
			tipoFiltro.setIdentificativoPortaFruitore(identificativoPortaFruitore);

			String tipoSoggettoErogatore = rsFiltri.getString("tipo_soggetto_erogatore");
			tipoFiltro.setTipoSoggettoErogatore(tipoSoggettoErogatore);
			String soggettoErogatore = rsFiltri.getString("soggetto_erogatore");
			tipoFiltro.setSoggettoErogatore(soggettoErogatore);
			String identificativoPortaErogatore = rsFiltri.getString("identificativo_porta_erogatore");
			tipoFiltro.setIdentificativoPortaErogatore(identificativoPortaErogatore);

			String tipoServizio = rsFiltri.getString("tipo_servizio");
			tipoFiltro.setTipoServizio(tipoServizio);
			String servizio = rsFiltri.getString("servizio");
			tipoFiltro.setServizio(servizio);
			Integer versioneServizio = rsFiltri.getInt("versione_servizio");
			if(rsFiltri.wasNull()==false){
				tipoFiltro.setVersioneServizio(versioneServizio);
			}

			String azione = rsFiltri.getString("azione");
			tipoFiltro.setAzione(azione);

			if(CostantiDB.TIPO_FILTRO_ABILITAZIONE_SERVIZIO_PDD.equals(tipo)){
				listaAbilitazioni.add(tipoFiltro);
			}
			else{
				listaDisabilitazioni.add(tipoFiltro);
			}

		}
	}



	/**
	 * Crea le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("createServiziAttiviPdD type = 1");
			DriverConfigurazioneDB_LIB.CRUDServiziPdD(1, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServiziAttiviPdD] Errore durante la createServiziAttiviPdD : " + qe.getMessage(),qe);
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
	 * Aggiorna le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("createServiziAttiviPdD type = 2");
			DriverConfigurazioneDB_LIB.CRUDServiziPdD(2, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateServiziAttiviPdD] Errore durante la updateServiziAttiviPdD : " + qe.getMessage(),qe);
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
	 * Elimina le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteStatoServiziPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("createServiziAttiviPdD type = 3");
			DriverConfigurazioneDB_LIB.CRUDServiziPdD(3, servizi, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteServiziAttiviPdD] Errore durante la deleteServiziAttiviPdD : " + qe.getMessage(),qe);
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





	public List<Property> systemPropertyList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "systemPropertyList";
		int idLista = Liste.SYSTEM_PROPERTIES;

		int offset;
		int limit;
		String search;
		String queryString;
		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Property> lista = new ArrayList<Property>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("systemPropertyList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectCountField("*", "cont");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
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
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
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
				sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			Property sp;
			while (risultato.next()) {

				sp = new Property();

				sp.setId(risultato.getLong("id"));
				sp.setNome(risultato.getString("nome"));
				sp.setValore(risultato.getString("valore"));

				lista.add(sp);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	 * Restituisce le proprieta' di sistema utilizzate dalla PdD
	 *
	 * @return proprieta' di sistema
	 * 
	 */
	@Override
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSystemPropertiesPdD");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			SystemProperties systemProperties = null;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SYSTEM_PROPERTIES_PDD);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();


			while (rs.next()) {

				if(systemProperties==null){
					systemProperties = new SystemProperties();
				}

				long id = rs.getLong("id");

				String nome = rs.getString("nome");
				String valore = rs.getString("valore");

				Property sp = new Property();
				sp.setNome(nome);
				sp.setValore(valore);
				sp.setId(id);
				systemProperties.addSystemProperty(sp);

			} 

			if(systemProperties==null)
				throw new DriverConfigurazioneNotFound("System Properties non presenti");

			rs.close();
			stm.close();

			return systemProperties;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getSystemPropertiesPdD]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getSystemPropertiesPdD]  Exception: " + se.getMessage(),se);
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
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

	}
	
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGenericProperties(null);
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		List<String> listTipologia = new ArrayList<>();
		if(tipologia!=null) {
			listTipologia.add(tipologia);
		}
		return getGenericProperties(listTipologia, null, null,true, null);
	}
	
	@Override
	public GenericProperties getGenericProperties(String tipologia, String name) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		List<String> listTipologia = new ArrayList<>();
		if(tipologia!=null) {
			listTipologia.add(tipologia);
		}
		List<GenericProperties> l = getGenericProperties(listTipologia, null, null,true, name);
		if(l==null || l.size()<=0) {
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti con tipologia '"+tipologia+"' e nome '"+name+"'");
		}
		else if(l.size()>1) {
			throw new DriverConfigurazioneException("[getGenericProperties] Trovata più di una collezione di proprietà con tipologia '"+tipologia+"' e nome '"+name+"'");
		}
		return l.get(0);
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	public List<GenericProperties> getGenericProperties(List<String> tipologia, Integer idLista, ISearch ricerca, boolean throwNotFoundException) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGenericProperties(tipologia, idLista, ricerca, throwNotFoundException, null);
	}
	private List<GenericProperties> getGenericProperties(List<String> tipologia, Integer idLista, ISearch ricerca, boolean throwNotFoundException, String nomeEsatto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		Integer offset = null;
		Integer limit =null;
		String search = "";
		String filterTipoTokenPolicy = null;
		if(idLista != null && ricerca != null) {
			limit = ricerca.getPageSize(idLista);
			offset = ricerca.getIndexIniziale(idLista);
			search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			
			filterTipoTokenPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_TOKEN_POLICY);
			
			this.log.debug("search : " + search);
			this.log.debug("filterTipoTokenPolicy : " + filterTipoTokenPolicy);
		}
		
		
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getGenericProperties");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<Long> listIdLong = new ArrayList<>();
		
		try {
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectCountField("*", "cont");
			if(tipologia!=null && !tipologia.isEmpty()) {
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologia.toArray(new String[1]));
			}
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				sqlQueryObject.addWhereCondition("tipo=?");
			}
			if(nomeEsatto!=null) {
				sqlQueryObject.addWhereCondition("nome=?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			 
			stm = con.prepareStatement(sqlQuery);
			int index = 1;
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				stm.setString(index++, filterTipoTokenPolicy);
			}
			if(nomeEsatto!=null) {
				stm.setString(index++, nomeEsatto);
			}
			rs = stm.executeQuery();
			if (rs.next() && ricerca != null)
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit!= null && limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			if(tipologia!=null && !tipologia.isEmpty()) {
				sqlQueryObject.addWhereINCondition("tipologia", true, tipologia.toArray(new String[1]));
			}
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				sqlQueryObject.addWhereCondition("tipo=?");
			}
			if(nomeEsatto!=null) {
				sqlQueryObject.addWhereCondition("nome=?");
			}
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			if(limit!= null)
				sqlQueryObject.setLimit(limit);
			if(offset != null)
				sqlQueryObject.setOffset(offset);
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			index = 1;
			if(filterTipoTokenPolicy!=null && !"".equals(filterTipoTokenPolicy)) {
				stm.setString(index++, filterTipoTokenPolicy);
			}
			if(nomeEsatto!=null) {
				stm.setString(index++, nomeEsatto);
			}
			rs = stm.executeQuery();
			
			while(rs.next()){
				
				long idP = rs.getLong("id");
				listIdLong.add(idP);
				
			}
			rs.close();
			stm.close();
			
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  Exception: " + se.getMessage(),se);
		} finally {
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
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}

		
		List<GenericProperties> genericPropertiesList = new ArrayList<>();
		
		if(listIdLong.size()>0) {
			for (Long id : listIdLong) {
				genericPropertiesList.add(this.getGenericProperties(id));
			}
		}

		if((genericPropertiesList==null || genericPropertiesList.size()<=0) && throwNotFoundException)
			throw new DriverConfigurazioneNotFound("Generic Properties non presenti");
		
		return genericPropertiesList;
	}



	/**
	 * Crea le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("createSystemPropertiesPdD type = 1");
			DriverConfigurazioneDB_LIB.CRUDSystemPropertiesPdD(1, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSystemPropertiesPdD] Errore durante la createSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Aggiorna le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("updateSystemPropertiesPdD type = 2");
			DriverConfigurazioneDB_LIB.CRUDSystemPropertiesPdD(2, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSystemPropertiesPdD] Errore durante la updateSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Elimina le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteSystemPropertiesPdD");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSystemPropertiesPdD] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("deleteSystemPropertiesPdD type = 3");
			DriverConfigurazioneDB_LIB.CRUDSystemPropertiesPdD(3, systemProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSystemPropertiesPdD] Errore durante la deleteSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Crea una proprieta' generica della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDGenericPropertiesPdD type = 1");
			DriverConfigurazioneDB_LIB.CRUDGenericProperties(1, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGenericProperties] Errore durante la createSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Aggiorna le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("updateGenericProperties type = 2");
			DriverConfigurazioneDB_LIB.CRUDGenericProperties(2, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGenericProperties] Errore durante la updateSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Elimina le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteGenericProperties");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("deleteGenericProperties type = 3");
			DriverConfigurazioneDB_LIB.CRUDGenericProperties(3, genericProperties, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGenericProperties] Errore durante la deleteSystemPropertiesPdD : " + qe.getMessage(),qe);
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
	 * Restituisce la configurazione generale della Porta di Dominio
	 * 
	 * @return Configurazione
	 * 
	 */
	@Override
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		// ritorna la configurazione generale della PdD

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;

		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getConfigurazioneGenerale");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);
		Configurazione config = new Configurazione();
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField("*");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			if (rs.next()) {

				Attachments attachments = new Attachments();
				attachments.setGestioneManifest(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));
				config.setAttachments(attachments);

				//config.setId(rs.getLong("id"));

				IndirizzoRisposta indirizzoRisposta = new IndirizzoRisposta();
				indirizzoRisposta.setUtilizzo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("indirizzo_telematico")));
				config.setIndirizzoRisposta(indirizzoRisposta);

				String cadenza_inoltro = rs.getString("cadenza_inoltro");
				InoltroBusteNonRiscontrate inoltroBusteNonRiscontrate = new InoltroBusteNonRiscontrate();
				inoltroBusteNonRiscontrate.setCadenza(cadenza_inoltro);
				config.setInoltroBusteNonRiscontrate(inoltroBusteNonRiscontrate);

				String autenticazione = rs.getString("auth_integration_manager");
				IntegrationManager integrationManager = new IntegrationManager();
				integrationManager.setAutenticazione(autenticazione);
				config.setIntegrationManager(integrationManager);

				//String stato_cache = rs.getString("statocache");
				//String dim_cache = rs.getString("dimensionecache");
				//String alog_cache = rs.getString("algoritmocache");
				//String idle_cache = rs.getString("idlecache");
				//String life_cache = rs.getString("lifecache");


				boolean routingEnabled =  false;
				if(CostantiConfigurazione.ABILITATO.equals(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("routing_enabled"))))
					routingEnabled = true;
				RoutingTable rt = new RoutingTable();
				rt.setAbilitata(routingEnabled);
				config.setRoutingTable(rt);


				String validazioneContenuti_stato = rs.getString("validazione_contenuti_stato");
				String validazioneContenuti_tipo = rs.getString("validazione_contenuti_tipo");
				String validazioneContenuti_acceptMtomMessage = rs.getString("validazione_contenuti_mtom");
				if(  (validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  
						||
						(validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  
						||
						(validazioneContenuti_acceptMtomMessage!=null && !validazioneContenuti_acceptMtomMessage.equals("")))
				{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					if((validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  )
						val.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(validazioneContenuti_stato));
					if((validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  )
						val.setTipo(DriverConfigurazioneDB_LIB.getEnumValidazioneContenutiApplicativiTipo(validazioneContenuti_tipo));
					if((validazioneContenuti_acceptMtomMessage!=null && !validazioneContenuti_acceptMtomMessage.equals(""))  )
						val.setAcceptMtomMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(validazioneContenuti_acceptMtomMessage));
					config.setValidazioneContenutiApplicativi(val);
				}


				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_INVOCAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				if(rs1.next()){
					ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = new ConfigurazioneUrlInvocazione();
					configurazioneUrlInvocazione.setBaseUrl(rs1.getString("base_url"));
					configurazioneUrlInvocazione.setBaseUrlFruizione(rs1.getString("base_url_fruizione"));
					config.setUrlInvocazione(configurazioneUrlInvocazione);
				}
				rs1.close();
				stm1.close();
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addOrderBy("posizione");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);	
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				while(rs1.next()){
					
					if(config.getUrlInvocazione()==null) {
						config.setUrlInvocazione(new ConfigurazioneUrlInvocazione());
					}
					
					ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
					regola.setId(rs1.getLong("id"));
					regola.setNome(rs1.getString("nome"));
					regola.setPosizione(rs1.getInt("posizione"));
					regola.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs1.getString("stato")));
					regola.setDescrizione(rs1.getString("descrizione"));
					if(rs1.getInt("regexpr") == CostantiDB.TRUE) {
						regola.setRegexpr(true);
					}else {
						regola.setRegexpr(false);
					}
					regola.setRegola(rs1.getString("regola"));
					regola.setContestoEsterno(rs1.getString("contesto_esterno"));
					regola.setBaseUrl(rs1.getString("base_url"));
					regola.setProtocollo(rs1.getString("protocollo"));
					regola.setRuolo(DriverConfigurazioneDB_LIB.getEnumRuoloContesto(rs1.getString("ruolo")));
					regola.setServiceBinding(DriverConfigurazioneDB_LIB.getEnumServiceBinding(rs1.getString("service_binding")));
					String tipoSoggetto = rs1.getString("tipo_soggetto");
					String nomeSoggetto = rs1.getString("nome_soggetto");
					if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && nomeSoggetto!=null && !"".equals(nomeSoggetto)) {
						regola.setSoggetto(new IdSoggetto(new IDSoggetto(tipoSoggetto, nomeSoggetto)));
					}
					config.getUrlInvocazione().addRegola(regola);
				}
				rs1.close();
				stm1.close();

				
				String multitenantStato = rs.getString("multitenant_stato");
				String multitenantStatoSoggettiFruitori = rs.getString("multitenant_fruizioni");
				String multitenantStatoSoggettiErogatori = rs.getString("multitenant_erogazioni");
				config.setMultitenant(new ConfigurazioneMultitenant());
				config.getMultitenant().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(multitenantStato));
				config.getMultitenant().setFruizioneSceltaSoggettiErogatori(DriverConfigurazioneDB_LIB.getEnumPortaDelegataSoggettiErogatori(multitenantStatoSoggettiFruitori));
				config.getMultitenant().setErogazioneSceltaSoggettiFruitori(DriverConfigurazioneDB_LIB.getEnumPortaApplicativaSoggettiFruitori(multitenantStatoSoggettiErogatori));

				String msg_diag_severita = rs.getString("msg_diag_severita");
				String msg_diag_severita_log4j = rs.getString("msg_diag_severita_log4j");
				MessaggiDiagnostici messaggiDiagnostici = new MessaggiDiagnostici();
				messaggiDiagnostici.setSeveritaLog4j(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita_log4j));
				messaggiDiagnostici.setSeverita(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita));
				//messaggi diagnostici appender
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();

				while(rs1.next()){
					OpenspcoopAppender appender = new OpenspcoopAppender();
					//tipo appender
					appender.setTipo(rs1.getString("tipo"));
					long idAppender = rs1.getLong("id");
					appender.setId(idAppender);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppender);
					rs2 = stm2.executeQuery();
					Property appender_prop = null;
					while(rs2.next())
					{
						appender_prop = new Property();
						//proprieta
						appender_prop.setId(rs2.getLong("id"));
						appender_prop.setNome(rs2.getString("nome"));
						appender_prop.setValore(rs2.getString("valore"));
						appender.addProperty(appender_prop);
					}
					rs2.close();
					stm2.close();
					messaggiDiagnostici.addOpenspcoopAppender(appender);
				}
				rs1.close();
				stm1.close();

				//messaggi diagnostici datasource
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_DS);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();

				while(rs1.next()){
					OpenspcoopSorgenteDati openspcoopDS = new OpenspcoopSorgenteDati();
					openspcoopDS.setNome(rs1.getString("nome"));
					openspcoopDS.setNomeJndi(rs1.getString("nome_jndi"));
					openspcoopDS.setTipoDatabase(rs1.getString("tipo_database"));
					long idDS = rs1.getLong("id");
					openspcoopDS.setId(idDS);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGN_DS_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_prop = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idDS);
					rs2 = stm2.executeQuery();
					Property ds_prop = null;
					while(rs2.next())
					{
						ds_prop = new Property();
						//proprieta
						ds_prop.setId(rs2.getLong("id"));
						ds_prop.setNome(rs2.getString("nome"));
						ds_prop.setValore(rs2.getString("valore"));
						openspcoopDS.addProperty(ds_prop);
					}
					rs2.close();
					stm2.close();
					messaggiDiagnostici.addOpenspcoopSorgenteDati(openspcoopDS);
				}
				rs1.close();
				stm1.close();

				config.setMessaggiDiagnostici(messaggiDiagnostici);

				//Tracciamento
				String tracc_buste = rs.getString("tracciamento_buste");
				String tracc_esiti = rs.getString("tracciamento_esiti");
				Tracciamento tracciamento = new Tracciamento();
				tracciamento.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tracc_buste));
				tracciamento.setEsiti(tracc_esiti);
				
				//appender tracciamento
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti gli appender e le prop di ogni appender
				while(rs1.next()){
					OpenspcoopAppender trac_appender = new OpenspcoopAppender();
					//tipo appender
					trac_appender.setTipo(rs1.getString("tipo"));
					long idAppenderTrac = rs1.getLong("id");
					trac_appender.setId(idAppenderTrac);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppenderTrac);
					rs2 = stm2.executeQuery();
					Property trac_appender_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo appender
						trac_appender_prop = new Property();
						//proprieta
						trac_appender_prop.setId(rs2.getLong("id"));
						trac_appender_prop.setNome(rs2.getString("nome"));
						trac_appender_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop all'appender
						trac_appender.addProperty(trac_appender_prop);
					}
					rs2.close();
					stm2.close();
					tracciamento.addOpenspcoopAppender(trac_appender);
				}
				rs1.close();
				stm1.close();

				//datasource tracciamento
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_DS);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti i datasource e le prop di ogni datasource
				while(rs1.next()){
					OpenspcoopSorgenteDati trac_ds = new OpenspcoopSorgenteDati();
					trac_ds.setNome(rs1.getString("nome"));
					trac_ds.setNomeJndi(rs1.getString("nome_jndi"));
					trac_ds.setTipoDatabase(rs1.getString("tipo_database"));
					long idDsTrac = rs1.getLong("id");
					trac_ds.setId(idDsTrac);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.TRACCIAMENTO_DS_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_prop = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idDsTrac);
					rs2 = stm2.executeQuery();
					Property trac_ds_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo datasource
						trac_ds_prop = new Property();
						//proprieta
						trac_ds_prop.setId(rs2.getLong("id"));
						trac_ds_prop.setNome(rs2.getString("nome"));
						trac_ds_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop al datasource
						trac_ds.addProperty(trac_ds_prop);
					}
					rs2.close();
					stm2.close();
					tracciamento.addOpenspcoopSorgenteDati(trac_ds);
				}
				rs1.close();
				stm1.close();

				config.setTracciamento(tracciamento);

				
				// Transazioni
				String transazioniTempiElaborazione = rs.getString("transazioni_tempi");
				String transazioniToken = rs.getString("transazioni_token");
				config.setTransazioni(new Transazioni());
				
				StatoFunzionalita statoTransazioniTempiElaborazione = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(transazioniTempiElaborazione);
				if(statoTransazioniTempiElaborazione!=null) {
					config.getTransazioni().setTempiElaborazione(statoTransazioniTempiElaborazione);
				}
				// else lascio il default
				
				StatoFunzionalita statoTransazioniToken = DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(transazioniToken);
				if(statoTransazioniToken!=null) {
					config.getTransazioni().setToken(statoTransazioniToken);
				}
				// else lascio il default

				
				
				// Dump
				String dump_stato = rs.getString("dump");
				String dump_pd = rs.getString("dump_bin_pd");
				String dump_pa = rs.getString("dump_bin_pa");
				Dump dump = new Dump();
				dump.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(dump_stato));
				dump.setDumpBinarioPortaDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(dump_pd));
				if(dump.getDumpBinarioPortaDelegata()==null){
					dump.setDumpBinarioPortaDelegata(StatoFunzionalita.DISABILITATO); // default
				}
				dump.setDumpBinarioPortaApplicativa(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(dump_pa));
				if(dump.getDumpBinarioPortaApplicativa()==null){
					dump.setDumpBinarioPortaApplicativa(StatoFunzionalita.DISABILITATO); // default
				}
				//appender dump
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DUMP_APPENDER);
				sqlQueryObject.addSelectField("*");
				sqlQuery = sqlQueryObject.createSQLQuery();

				stm1 = con.prepareStatement(sqlQuery);
				rs1 = stm1.executeQuery();
				//recuper tutti gli appender e le prop di ogni appender
				while(rs1.next()){
					OpenspcoopAppender dump_appender = new OpenspcoopAppender();
					//tipo appender
					dump_appender.setTipo(rs1.getString("tipo"));
					long idAppenderDump = rs1.getLong("id");
					dump_appender.setId(idAppenderDump);
					//prendo le proprieta
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DUMP_APPENDER_PROP);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_appender = ?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm2 = con.prepareStatement(sqlQuery);
					stm2.setLong(1, idAppenderDump);
					rs2 = stm2.executeQuery();
					Property dump_appender_prop = null;
					while(rs2.next())
					{
						//setto le prop di questo appender
						dump_appender_prop = new Property();
						//proprieta
						dump_appender_prop.setId(rs2.getLong("id"));
						dump_appender_prop.setNome(rs2.getString("nome"));
						dump_appender_prop.setValore(rs2.getString("valore"));
						//aggiungo la prop all'appender
						dump_appender.addProperty(dump_appender_prop);
					}
					rs2.close();
					stm2.close();
					dump.addOpenspcoopAppender(dump_appender);
				}
				rs1.close();
				stm1.close();

				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_LIB.readDumpConfigurazione(con, null, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_CONFIG);
				dump.setConfigurazione(dumpConfig);
				
				config.setDump(dump);
				
				
				Risposte risposte = new Risposte();
				risposte.setConnessione(DriverConfigurazioneDB_LIB.getEnumTipoConnessioneRisposte(rs.getString("mod_risposta")));
				config.setRisposte(risposte);

				String val_controllo = rs.getString("validazione_controllo");
				String val_stato = rs.getString("validazione_stato");
				String val_manifest = rs.getString("validazione_manifest");
				String val_profilo = rs.getString("validazione_profilo");
				ValidazioneBuste validazioneBuste = new ValidazioneBuste();
				validazioneBuste.setControllo(DriverConfigurazioneDB_LIB.getEnumValidazioneBusteTipoControllo(val_controllo));
				validazioneBuste.setManifestAttachments(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(val_manifest));
				validazioneBuste.setProfiloCollaborazione(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(val_profilo));
				validazioneBuste.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(val_stato));
				config.setValidazioneBuste(validazioneBuste);

				// Gestione CORS
				config.setGestioneCors(new CorsConfigurazione());
				readConfigurazioneCors(config.getGestioneCors(), rs);
				
				// Gestione CacheResponse
				config.setResponseCaching(new ResponseCachingConfigurazioneGenerale());
				
				config.getResponseCaching().setConfigurazione(new ResponseCachingConfigurazione());
				readResponseCaching(null, true, false, config.getResponseCaching().getConfigurazione(), rs, con);
				
				String tmpCache = rs.getString("response_cache_statocache");
				if (CostantiConfigurazione.ABILITATO.equals(tmpCache)) {
					Cache cache = new Cache();

					String tmpDim = rs.getString("response_cache_dimensionecache");
					if (tmpDim != null && !tmpDim.equals(""))
						cache.setDimensione(tmpDim);

					String tmpAlg = rs.getString("response_cache_algoritmocache");
					if (tmpAlg.equalsIgnoreCase("LRU"))
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_LRU);
					else
						cache.setAlgoritmo(CostantiConfigurazione.CACHE_MRU);

					String tmpIdle = rs.getString("response_cache_idlecache");
					String tmpLife = rs.getString("response_cache_lifecache");

					if (tmpIdle != null && !tmpIdle.equals(""))
						cache.setItemIdleTime(tmpIdle);
					if (tmpLife != null && !tmpLife.equals(""))
						cache.setItemLifeSecond(tmpLife);

					config.getResponseCaching().setCache(cache);

				}
				
				
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> list = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.log, config);
					if(list!=null && list.size()>0){
						config.setExtendedInfoList(list);
					}
				}
				
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getConfigurazioneGenerale] Configurazione non presente.");
			}

		} catch (DriverConfigurazioneNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneGenerale] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
				if(rs2!=null) rs2.close();
				if(stm2!=null) stm2.close();
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


		// Altre parti di configurazione. Le prendo dopo aver rilasciato la connessione, per permettere di far funzionare il driver anche con pool di una sola connessione. 

		// - GestioneErrore
		ConfigurazioneGestioneErrore cge = null;

		try{
			GestioneErrore ge = this.getGestioneErroreComponenteIntegrazione();

			if(ge!=null){
				if(cge==null) cge = new ConfigurazioneGestioneErrore();
				cge.setComponenteIntegrazione(ge);
			}
		}catch (Exception e) {}

		try{
			GestioneErrore ge = this.getGestioneErroreComponenteCooperazione();
			if(ge!=null){
				if(cge==null) cge = new ConfigurazioneGestioneErrore();
				cge.setComponenteCooperazione(ge);
			}
		}catch (Exception e) {}

		if(cge!=null) config.setGestioneErrore(cge);

		// - AccessoRegistro
		try{
			config.setAccessoRegistro(getAccessoRegistro());
		}catch (Exception e) {}
		
		// - AccessoConfigurazione
		try{
			config.setAccessoConfigurazione(getAccessoConfigurazione());
		}catch (Exception e) {}
		
		// - AccessoDatiAutorizzazione
		try{
			config.setAccessoDatiAutorizzazione(getAccessoDatiAutorizzazione());
		}catch (Exception e) {}
		
		// - AccessoDatiAutenticazione
		try{
			config.setAccessoDatiAutenticazione(getAccessoDatiAutenticazione());
		}catch (Exception e) {}
		
		// - AccessoDatiGestioneToken
		try{
			config.setAccessoDatiGestioneToken(getAccessoDatiGestioneToken());
		}catch (Exception e) {}
		
		// - AccessoDatiKeystore
		try{
			config.setAccessoDatiKeystore(getAccessoDatiKeystore());
		}catch (Exception e) {}
		
		// - AccessoDatiConsegnaApplicativi
		try{
			config.setAccessoDatiConsegnaApplicativi(getAccessoDatiConsegnaApplicativi());
		}catch (Exception e) {}


		// - RoutingTable
		try{
			config.setRoutingTable(getRoutingTable());
		}catch (Exception e) {}

		// - ServiziPdD
		try{
			config.setStatoServiziPdd(getStatoServiziPdD());
		}catch (Exception e) {}

		// - SystemProperties
		try{
			config.setSystemProperties(getSystemPropertiesPdD());
		}catch (Exception e) {}

		// - GenericProperties
		try{
			config.getGenericPropertiesList().addAll(this.getGenericProperties());
		}catch (Exception e) {}

		return config;
	}

	private void readConfigurazioneCors(CorsConfigurazione configurazione, ResultSet rs) throws SQLException {
		
		String corsStato = rs.getString("cors_stato");
		if(corsStato!=null && !"".equals(corsStato)) {
			configurazione.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsStato));
		}
		
		if(StatoFunzionalita.ABILITATO.equals(configurazione.getStato())) {
			
			String corsTipo = rs.getString("cors_tipo");
			if(corsTipo!=null && !"".equals(corsTipo)) {
				configurazione.setTipo(DriverConfigurazioneDB_LIB.getEnumTipoGestioneCORS(corsTipo));
			}
			
			if(TipoGestioneCORS.GATEWAY.equals(configurazione.getTipo())) {
				
				String corsAllAllowOrigins = rs.getString("cors_all_allow_origins");
				if(corsAllAllowOrigins!=null && !"".equals(corsAllAllowOrigins)) {
					configurazione.setAccessControlAllAllowOrigins(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllAllowOrigins));
				}
				if(StatoFunzionalita.DISABILITATO.equals(configurazione.getAccessControlAllAllowOrigins())) {
					List<String> l = convertToList(rs.getString("cors_allow_origins"));
					if(!l.isEmpty()) {
						configurazione.setAccessControlAllowOrigins(new CorsConfigurazioneOrigin());
					}
					for (String v : l) {
						configurazione.getAccessControlAllowOrigins().addOrigin(v);
					}
				}
			
				String corsAllowCredentials = rs.getString("cors_allow_credentials");
				if(corsAllowCredentials!=null && !"".equals(corsAllowCredentials)) {
					configurazione.setAccessControlAllowCredentials(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(corsAllowCredentials));
				}
				
				int corsAllowMaxAge = rs.getInt("cors_allow_max_age");
				if(CostantiDB.TRUE == corsAllowMaxAge) {
					int corsAllowMaxAgeSeconds = rs.getInt("cors_allow_max_age_seconds");
					configurazione.setAccessControlMaxAge(corsAllowMaxAgeSeconds);
				}
				
				List<String> l = convertToList(rs.getString("cors_allow_headers"));
				if(!l.isEmpty()) {
					configurazione.setAccessControlAllowHeaders(new CorsConfigurazioneHeaders());
				}
				for (String v : l) {
					configurazione.getAccessControlAllowHeaders().addHeader(v);
				}
				
				l = convertToList(rs.getString("cors_allow_methods"));
				if(!l.isEmpty()) {
					configurazione.setAccessControlAllowMethods(new CorsConfigurazioneMethods());
				}
				for (String v : l) {
					configurazione.getAccessControlAllowMethods().addMethod(v);
				}
				
				l = convertToList(rs.getString("cors_allow_expose_headers"));
				if(!l.isEmpty()) {
					configurazione.setAccessControlExposeHeaders(new CorsConfigurazioneHeaders());
				}
				for (String v : l) {
					configurazione.getAccessControlExposeHeaders().addHeader(v);
				}
			}
			
		}
		
	}
	
	private void readResponseCaching(Long idPorta, boolean config, boolean portaDelegata, ResponseCachingConfigurazione configurazione, ResultSet rs, Connection con) throws Exception {
		
		String responseCacheStato = rs.getString("response_cache_stato");
		if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
			configurazione.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheStato));
		}
		
		if(StatoFunzionalita.ABILITATO.equals(configurazione.getStato())) {
	
			int responseCacheSeconds = rs.getInt("response_cache_seconds");
			if(responseCacheSeconds>0) {
				configurazione.setCacheTimeoutSeconds(responseCacheSeconds);
			}
			
			long responseCacheMaxMsgBytes = rs.getLong("response_cache_max_msg_size");
			if(responseCacheMaxMsgBytes>0) {
				configurazione.setMaxMessageSize(responseCacheMaxMsgBytes);
			}
			
			configurazione.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
			
			String responseCacheHashUrl = rs.getString("response_cache_hash_url");
			if(responseCacheHashUrl!=null && !"".equals(responseCacheHashUrl)) {
				configurazione.getHashGenerator().setRequestUri(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashUrl));
			}
			
			String responseCacheHashQuery = rs.getString("response_cache_hash_query");
			if(responseCacheHashQuery!=null && !"".equals(responseCacheHashQuery)) {
				configurazione.getHashGenerator().setQueryParameters(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaCacheDigestQueryParameter(responseCacheHashQuery));
			}
			
			if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(configurazione.getHashGenerator().getQueryParameters())) {
				List<String> l = convertToList(rs.getString("response_cache_hash_query_list"));
				for (String v : l) {
					configurazione.getHashGenerator().addQueryParameter(v);
				}
			}
			
			String responseCacheHashHeaders = rs.getString("response_cache_hash_headers");
			if(responseCacheHashHeaders!=null && !"".equals(responseCacheHashHeaders)) {
				configurazione.getHashGenerator().setHeaders(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashHeaders));
			}
			
			if(StatoFunzionalita.ABILITATO.equals(configurazione.getHashGenerator().getHeaders())) {
				List<String> l = convertToList(rs.getString("response_cache_hash_hdr_list"));
				for (String v : l) {
					configurazione.getHashGenerator().addHeader(v);
				}
			}
			
			String responseCacheHashPayload = rs.getString("response_cache_hash_payload");
			if(responseCacheHashPayload!=null && !"".equals(responseCacheHashPayload)) {
				configurazione.getHashGenerator().setPayload(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(responseCacheHashPayload));
			}
			
			configurazione.setControl(new ResponseCachingConfigurazioneControl());
			
			int response_cache_control_nocache = rs.getInt("response_cache_control_nocache");
			if(CostantiDB.TRUE == response_cache_control_nocache) {
				configurazione.getControl().setNoCache(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_nocache) {
				configurazione.getControl().setNoCache(false);
			}
			
			int response_cache_control_maxage = rs.getInt("response_cache_control_maxage");
			if(CostantiDB.TRUE == response_cache_control_maxage) {
				configurazione.getControl().setMaxAge(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_maxage) {
				configurazione.getControl().setMaxAge(false);
			}
			
			int response_cache_control_nostore = rs.getInt("response_cache_control_nostore");
			if(CostantiDB.TRUE == response_cache_control_nostore) {
				configurazione.getControl().setNoStore(true);
			}
			else if(CostantiDB.FALSE == response_cache_control_nostore) {
				configurazione.getControl().setNoStore(false);
			}
			
			PreparedStatement stmRegole = null;
			ResultSet rsRegole = null;
			try {
				String nomeTabella = null;
				if(config) {
					nomeTabella = CostantiDB.CONFIGURAZIONE_CACHE_REGOLE;
				}
				else {
					nomeTabella = portaDelegata ? CostantiDB.PORTE_DELEGATE_CACHE_REGOLE : CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE;
				}
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(nomeTabella);
				sqlQueryObject.addSelectField("*");
				if(!config) {
					sqlQueryObject.addWhereCondition("id_porta=?");
				}
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmRegole = con.prepareStatement(sqlQuery);
				if(!config) {
					stmRegole.setLong(1, idPorta);
				}
		
				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPorta));
				rsRegole = stmRegole.executeQuery();
		
				while (rsRegole.next()) {
				
					ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
					
					regola.setId(rsRegole.getLong("id"));
					int status_min = rsRegole.getInt("status_min");
					int status_max = rsRegole.getInt("status_max");
					if(status_min>0) {
						regola.setReturnCodeMin(status_min);
					}
					if(status_max>0) {
						regola.setReturnCodeMax(status_max);
					}

					int fault = rsRegole.getInt("fault");
					if(CostantiDB.TRUE == fault) {
						regola.setFault(true);
					}
					else if(CostantiDB.FALSE == fault) {
						regola.setFault(false);
					}
					
					int cacheSeconds = rsRegole.getInt("cache_seconds");
					if(cacheSeconds>0) {
						regola.setCacheTimeoutSeconds(cacheSeconds);
					}
					
					configurazione.addRegola(regola);
					
				}
				
				
			}finally {
				try {
					rsRegole.close();
				}catch(Exception eClose) {}
				try {
					stmRegole.close();
				}catch(Exception eClose) {}
			}
		}

	}
	
	
			
	private List<String> convertToList(String v){
		List<String> l = new ArrayList<>();
		if(v!=null && !"".equals(v)) {
			if(v.contains(",")) {
				String [] tmp = v.split(",");
				for (int i = 0; i < tmp.length; i++) {
					l.add(tmp[i].trim());
				}
			}else {
				l.add(v.trim());
			}
		}
		return l;
	}

	public Object getConfigurazioneExtended(Configurazione config, String idExtendedConfiguration) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		
		if (this.atomica) {
			try {
				con = this.datasource.getConnection();
			} catch (SQLException e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneExtended] SQLException accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("getConfigurazioneExtended("+idExtendedConfiguration+")");
			
			ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
			IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoConfigurazione();
			if(extInfoConfigurazioneDriver!=null){
				
				Object o = extInfoConfigurazioneDriver.getExtendedInfo(con, this.log, config, idExtendedConfiguration);
				if(o==null){
					throw new DriverConfigurazioneNotFound("Oggetto non esistente");
				}
				return o;
				
			}	
			
			throw new DriverConfigurazioneException("Driver non inizializzato");

		} 
		catch (DriverConfigurazioneNotFound dNot) {
			throw dNot;
		}
		catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getConfigurazioneExtended] Errore: " + qe.getMessage(),qe);
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
	


	@Override
	public void createConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConfigurazioneGenerale type = 1");
			DriverConfigurazioneDB_LIB.CRUDConfigurazioneGenerale(1, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConfigurazioneGenerale] Errore durante la create ConfigurazioneGenerale : " + qe.getMessage(),qe);
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

	
	@Override
	public void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConfigurazioneGenerale type = 2");
			DriverConfigurazioneDB_LIB.CRUDConfigurazioneGenerale(2, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConfigurazioneGenerale] Errore durante la update ConfigurazioneGenerale : " + qe.getMessage(),qe);
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

	
	@Override
	public void deleteConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteConfigurazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConfigurazioneGenerale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDConfigurazioneGenerale type = 3");
			DriverConfigurazioneDB_LIB.CRUDConfigurazioneGenerale(CostantiDB.DELETE, configurazione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConfigurazioneGenerale] Errore durante la delete ConfigurazioneGenerale : " + qe.getMessage(),qe);
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
	

	// ACCESSO REGISTRO

	/**
	 * Crea le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */

	@Override
	public void createAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 1");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(1, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
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
	 * Aggiorna le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 2");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(2, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
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
	 * Elimina le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteAccessoRegistro");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDAccessoRegistro type = 3");
			DriverConfigurazioneDB_LIB.CRUDAccessoRegistro(3, registro, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteAccessoRegistro] Errore durante la updateAccessoRegistro : " + qe.getMessage(),qe);
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

	// GESTIONE ERRORE

	/**
	 * Crea le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		PreparedStatement pstmt = null;
		try {
			this.log.debug("CRUDGestioneErrore type = 1");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.CREATE, gestione, con);

			// Aggiorno configurazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addUpdateField("id_ge_cooperazione", "?");
			String updateQuery = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(updateQuery);
			pstmt.setLong(1, gestione.getId());
			pstmt.execute();
			pstmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
		} finally {

			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){}

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
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDGestioneErrore type = 2");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.UPDATE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
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
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDGestioneErrore type = 3");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.DELETE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
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
	 * Crea le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("createGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		PreparedStatement pstmt = null;
		try {
			this.log.debug("CRUDGestioneErrore type = 1");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.CREATE, gestione, con);

			// Aggiorno configurazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addUpdateField("id_ge_integrazione", "?");
			String updateQuery = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(updateQuery);
			pstmt.setLong(1, gestione.getId());
			pstmt.execute();
			pstmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
		} finally {

			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception eClose){}

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
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("updateGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDGestioneErrore type = 2");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.UPDATE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
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
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("deleteGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			this.log.debug("CRUDGestioneErrore type = 3");
			DriverConfigurazioneDB_LIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.DELETE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
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
	 * Ritorna la lista di porte applicative con settati i campi id, nome,
	 * descrizione e se il security e' abilitato allora crea un oggetto
	 * MessageSecurity vuoto altrimenti null.
	 */
	public List<PortaApplicativa> porteAppList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista=Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stm = con.prepareStatement(queryString);
			stm.setLong(1, idSoggetto);
			rs = stm.executeQuery();

			PortaApplicativa pa = null;
			while (rs.next()) {
				pa = getPortaApplicativa(rs.getLong("id"),con);
				lista.add(pa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
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
	 * Ritorna la lista di tutte le Porte Applicative
	 * @param ricerca
	 * @return Una lista di Porte Applicative
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaApplicativa> porteAppList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista=Liste.PORTE_APPLICATIVE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stm = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stm.setString(1, superuser);
			rs = stm.executeQuery();
			if (rs.next())
				ricerca.setNumEntries(idLista,rs.getInt(1));
			rs.close();
			stm.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stm = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stm.setString(1, superuser);
			rs = stm.executeQuery();

			PortaApplicativa pa = null;
			while (rs.next()) {

				pa = getPortaApplicativa(rs.getLong("id"),con);

				lista.add(pa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppList] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
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
	 * Ritorna la lista di proprieta di una Porta Applicativa
	 */
	public List<Proprieta> porteAppPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROP;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id_porta"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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
	 * Ritorna la lista di proprieta per l'autenticazione custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutenticazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTENTICAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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
	 * Ritorna la lista di proprieta per l'autorizzazione custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutorizzazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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
	 * Ritorna la lista di proprieta per l'autorizzazione contenuti custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutorizzazioneContenutoCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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
	 * Ritorna la lista di Azioni di una  Porta Applicativa
	 */
	public List<PortaApplicativaAzione> porteAppAzioneList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_AZIONI;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAzione> lista = new ArrayList<PortaApplicativaAzione>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("azione", search, true, true);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("azione");
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			// [TODO] Parametri da definire con Andrea
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			} 
			
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("azione");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAzione azione = null;
			while (risultato.next()) {

				azione = new PortaApplicativaAzione();
				
				azione.setId(risultato.getLong("id"));
				azione.setNome(risultato.getString("azione")) ;
				
				// [TODO] Parametri da definire con Andrea
				String nomePortaDelegante = null;
				azione.setNomePortaDelegante(nomePortaDelegante);
				PortaApplicativaAzioneIdentificazione identificazione = null;
				azione.setIdentificazione(identificazione);
				StatoFunzionalita forceInterfaceBased = StatoFunzionalita.ABILITATO; 
				azione.setForceInterfaceBased(forceInterfaceBased);

				lista.add(azione);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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

	public List<ServizioApplicativo> porteAppServizioApplicativoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppServizioApplicativoList";
		int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppServizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id_servizio_applicativo");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id_servizio_applicativo");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa = null;
			ResultSet rs1;
			long idServizioApplicativo = 0;
			while (risultato.next()) {
				idServizioApplicativo = risultato.getLong("id_servizio_applicativo");

				sa = new ServizioApplicativo();

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();

				stmt1 = con.prepareStatement(queryString);
				stmt1.setLong(1, idServizioApplicativo);

				rs1 = stmt1.executeQuery();

				// recupero i dati del servizio applicativo
				if (rs1.next()) {
					sa.setId(idServizioApplicativo);
					sa.setNome(rs1.getString("nome"));
					sa.setDescrizione(rs1.getString("descrizione"));
					sa.setIdSoggetto(rs1.getLong("id_soggetto"));
				} else
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore recuperando i dati del Servizio Applicativo.");

				rs1.close();
				stmt1.close();
				lista.add(sa);

			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();			
				if(stmt!=null) stmt.close();
				if(stmt1!=null) stmt1.close();
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
	
	public List<PortaApplicativaAutorizzazioneSoggetto> porteAppSoggettoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppSoggettoList";
		int idLista = Liste.PORTE_APPLICATIVE_SOGGETTO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAutorizzazioneSoggetto> lista = new ArrayList<PortaApplicativaAutorizzazioneSoggetto>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppSoggettoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//query con search
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			}
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAutorizzazioneSoggetto paAuthSoggetto = null;
			while (risultato.next()) {
				paAuthSoggetto = new PortaApplicativaAutorizzazioneSoggetto();
				paAuthSoggetto.setNome(risultato.getString("nome_soggetto"));
				paAuthSoggetto.setTipo(risultato.getString("tipo_soggetto"));
				
				lista.add(paAuthSoggetto);
			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();			
				if(stmt!=null) stmt.close();
				if(stmt1!=null) stmt1.close();
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
	
	public List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppServiziApplicativiAutorizzatiList";
		int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativaAutorizzazioneServizioApplicativo> lista = new ArrayList<PortaApplicativaAutorizzazioneServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppServiziApplicativiAutorizzatiList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			//query con search
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			if (!search.equals("")) {
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("id_servizio_applicativo");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI, "id_soggetto", "idSoggettoProprietarioSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "tipo_soggetto", "tipoSoggettoProprietarioSA");
			sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI, "nome_soggetto", "nomeSoggettoProprietarioSA");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_servizio_applicativo");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
			}
			sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI+".nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			PortaApplicativaAutorizzazioneServizioApplicativo paAuthSa = null;
			while (risultato.next()) {
				paAuthSa = new PortaApplicativaAutorizzazioneServizioApplicativo();
				paAuthSa.setNome(risultato.getString("nome"));
				paAuthSa.setTipoSoggettoProprietario(risultato.getString("tipoSoggettoProprietarioSA"));
				paAuthSa.setNomeSoggettoProprietario(risultato.getString("nomeSoggettoProprietarioSA"));
				lista.add(paAuthSa);
			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();			
				if(stmt!=null) stmt.close();
				if(stmt1!=null) stmt1.close();
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

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppMessageSecurityRequestList";
		int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST;

		int offset;
		int limit;
		String search;
		String queryString;
		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppMessageSecurityRequestList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsreq;
			while (risultato.next()) {

				wsreq = new MessageSecurityFlowParameter();

				wsreq.setId(risultato.getLong("id_porta"));
				wsreq.setNome(risultato.getString("nome"));
				wsreq.setValore(risultato.getString("valore"));

				lista.add(wsreq);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppMessageSecurityResponseList";
		int idLista = Liste.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppMessageSecurityResponseList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsresp;
			while (risultato.next()) {

				wsresp = new MessageSecurityFlowParameter();

				wsresp.setId(risultato.getLong("id_porta"));
				wsresp.setNome(risultato.getString("nome"));
				wsresp.setValore(risultato.getString("valore"));

				lista.add(wsresp);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaDelegata> porteDelegateList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateList";
		int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	
	public List<Proprieta> porteDelegatePropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROP;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
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
	
	public List<Proprieta> porteDelegateAutenticazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTENTICAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
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
	
	public List<Proprieta> porteDelegateAutorizzazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
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
	
	public List<Proprieta> porteDelegateAutorizzazioneContenutoCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_DELEGATE_PROPRIETA_AUTORIZZAZIONE_CONTENUTO;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegatePropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteDelegatePropList] Errore : " + qe.getMessage(),qe);
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

	public List<PortaDelegata> porteDelegateList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateList";
		int idLista = Liste.PORTE_DELEGATE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {

				pd = getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaDelegata> porteDelegateWithSoggettoErogatoreList(long idSoggettoErogatore) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateWithSoggettoErogatoreList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateWithSoggettoErogatoreList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto_erogatore = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaDelegata> porteDelegateWithTipoNomeErogatoreList(String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateWithTipoNomeErogatoreList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateWithTipoNomeErogatoreList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoSoggettoErogatore);
			stmt.setString(2, nomeSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaDelegata pd;
			while (risultato.next()) {
				pd = getPortaDelegata(risultato.getLong("id"),con);
				lista.add(pd);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaApplicativa> porteAppWithServizio(long idSoggettoErogatore, String tipoServizio,String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppWithTipoNomeServizio";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppWithTipoNomeServizio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("servizio = ?");
			sqlQueryObject.addWhereCondition("versione_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, idSoggettoErogatore);
			risultato = stmt.executeQuery();

			PortaApplicativa pa;
			while (risultato.next()) {
				pa = getPortaApplicativa(risultato.getLong("id"),con);
				lista.add(pa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaApplicativa> porteAppWithIdServizio(long idServizio) throws DriverConfigurazioneException {
		String nomeMetodo = "porteAppWithIdServizio";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteAppWithIdServizio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idServizio);
			risultato = stmt.executeQuery();

			PortaApplicativa pa;
			while (risultato.next()) {
				pa = getPortaApplicativa(risultato.getLong("id"),con);
				lista.add(pa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateServizioApplicativoList";
		int idLista = Liste.PORTE_DELEGATE_SERVIZIO_APPLICATIVO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		PreparedStatement stmt1=null;
		ResultSet risultato=null;
		ResultSet rs1=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateServizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SA,"id_servizio_applicativo");
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE_SA,"id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addSelectField("id_servizio_applicativo");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id_servizio_applicativo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa = null;

			long idServizioApplicativo = 0;
			while (risultato.next()) {
				idServizioApplicativo = risultato.getLong("id_servizio_applicativo");

				sa = new ServizioApplicativo();

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				queryString = sqlQueryObject.createSQLQuery();

				stmt1 = con.prepareStatement(queryString);
				stmt1.setLong(1, idServizioApplicativo);

				rs1 = stmt1.executeQuery();

				// recupero i dati del servizio applicativo
				if (rs1.next()) {
					sa.setId(idServizioApplicativo);
					sa.setNome(rs1.getString("nome"));
					sa.setDescrizione(rs1.getString("descrizione"));
					sa.setIdSoggetto(rs1.getLong("id_soggetto"));
				} else
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore recuperando i dati del Servizio Applicativo.");

				rs1.close();
				stmt1.close();
				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(rs1!=null) rs1.close();
				if(stmt!=null) stmt.close();
				if(stmt1!=null) stmt1.close();
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

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMessageSecurityRequestList";
		int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateMessageSecurityRequestList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsreq;
			while (risultato.next()) {

				wsreq = new MessageSecurityFlowParameter();

				wsreq.setId(risultato.getLong("id_porta"));
				wsreq.setNome(risultato.getString("nome"));
				wsreq.setValore(risultato.getString("valore"));

				lista.add(wsreq);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMessageSecurityResponseList";
		int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MessageSecurityFlowParameter> lista = new ArrayList<MessageSecurityFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateMessageSecurityResponseList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("valore");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			MessageSecurityFlowParameter wsresp;
			while (risultato.next()) {

				wsresp = new MessageSecurityFlowParameter();

				wsresp.setId(risultato.getLong("id_porta"));
				wsresp.setNome(risultato.getString("nome"));
				wsresp.setValore(risultato.getString("valore"));

				lista.add(wsresp);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaList";
		int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CorrelazioneApplicativaElemento> lista = new ArrayList<CorrelazioneApplicativaElemento>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateCorrelazioneApplicativaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addSelectField("riuso_id");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addSelectField("riuso_id");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRichiestaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				cae.setRiusoIdentificativo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(risultato.getString("riuso_id")));
				lista.add(cae);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateCorrelazioneApplicativaRispostaList";
		int idLista = Liste.PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CorrelazioneApplicativaRispostaElemento> lista = new ArrayList<CorrelazioneApplicativaRispostaElemento>();

		if (this.atomica) {
			try {
				con = (Connection) getConnectionFromDatasource("porteDelegateCorrelazioneApplicativaRispostaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaRispostaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaRispostaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRispostaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				lista.add(cae);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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


	public List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaList";
		int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CorrelazioneApplicativaElemento> lista = new ArrayList<CorrelazioneApplicativaElemento>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeCorrelazioneApplicativaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRichiestaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				lista.add(cae);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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



	public List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeCorrelazioneApplicativaRispostaList";
		int idLista = Liste.PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RISPOSTA;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<CorrelazioneApplicativaRispostaElemento> lista = new ArrayList<CorrelazioneApplicativaRispostaElemento>();

		if (this.atomica) {
			try {
				con = (Connection) getConnectionFromDatasource("porteApplicativeCorrelazioneApplicativaRispostaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome_elemento", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_elemento");
				sqlQueryObject.addSelectField("mode_correlazione");
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("identificazione_fallita");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			CorrelazioneApplicativaRispostaElemento cae = null;

			//long idServizioApplicativo = 0;
			while (risultato.next()) {
				cae = new CorrelazioneApplicativaRispostaElemento();
				cae.setId(risultato.getLong("id"));
				cae.setNome(risultato.getString("nome_elemento"));
				cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRispostaIdentificazione(risultato.getString("mode_correlazione")));
				cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(risultato.getString("identificazione_fallita")));
				lista.add(cae);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	 * metodo di utilita per il recupero della lista di servizi applicativi
	 * @param ricerca
	 * @param idProprietario
	 * @return List di ServizioApplicativo
	 * @throws DriverConfigurazioneException
	 */
	private List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca,Long idProprietario) throws DriverConfigurazioneException{
		String nomeMetodo = "servizioApplicativoList";
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("servizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");

				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");

				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(idProprietario!=null) stmt.setLong(1, idProprietario);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(idProprietario!=null) stmt.setLong(1, idProprietario);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.getServizioApplicativo(risultato.getLong("id"));

				//				sa = new ServizioApplicativo();

				//				sa.setId(risultato.getLong("id"));
				//				sa.setNome(risultato.getString("nome"));
				//				sa.setDescrizione(risultato.getString("descrizione"));
				//				sa.setIdSoggetto(risultato.getLong("id_soggetto"));

				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	 * Recupera tutti i servizi applicativi in base ai parametri di ricerca passati
	 */
	public List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.servizioApplicativoList(ricerca, null);
	}

	public List<ServizioApplicativo> servizioApplicativoList(IDSoggetto idSO,ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoList";
		Connection con = null;
		boolean error = false;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("servizioApplicativoList(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			return this.servizioApplicativoList(ricerca, DBUtils.getIdSoggetto(idSO.getNome(), idSO.getTipo(), con, this.tipoDB));

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiBasicList(String utente, String password, boolean checkPassword) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiBasicList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiApiKeyList(String utente, boolean appId) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiApiKeyList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			stmt.setString(index++, CostantiDB.getISSUER_APIKEY(appId));
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

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(String subject, String issuer) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("subject");
			sqlQueryObject.addSelectField("issuer");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			
			Hashtable<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoHashtable(subject, PrincipalType.subject);
			Hashtable<String, List<String>> hashIssuer = null;
			if(StringUtils.isNotEmpty(issuer)) {
				hashIssuer = CertificateUtils.getPrincipalIntoHashtable(issuer, PrincipalType.issuer);
			}
			
			Enumeration<String> keys = hashSubject.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				
				List<String> listValues = hashSubject.get(key);
				for (String value : listValues) {
					sqlQueryObject.addWhereLikeCondition("subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
				}
				
			}
			
			if(hashIssuer!=null) {
				keys = hashIssuer.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					
					List<String> listValues = hashIssuer.get(key);
					for (String value : listValues) {
						sqlQueryObject.addWhereLikeCondition("issuer", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, true, false);
					}
				}
				
			}
			else {
				sqlQueryObject.addWhereIsNullCondition("issuer");
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, CredenzialeTipo.SSL.getValue());
			
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
				
				String subjectPotenziale =  risultato.getString("subject");
				boolean subjectValid = CertificateUtils.sslVerify(subjectPotenziale, subject, PrincipalType.subject, this.log);
				
				boolean issuerValid = true;
				if(hashIssuer!=null) {
					String issuerPotenziale =  risultato.getString("issuer");
					if(StringUtils.isNotEmpty(issuerPotenziale)) {
						issuerValid = CertificateUtils.sslVerify(issuerPotenziale, issuer, PrincipalType.issuer, this.log);
					}
					else {
						issuerValid = false;
					}
				}
				
				if( !subjectValid || !issuerValid ) {
					continue;
				}
				
				sa=this.getServizioApplicativo(risultato.getLong("id"));
				lista.add(sa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiSslList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("certificate");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("tipoauth = ?");
			sqlQueryObject.addWhereCondition("cn_subject = ?");
			sqlQueryObject.addWhereCondition("cn_issuer = ?");
			//sqlQueryObject.addWhereCondition("cert_strict_verification = ?");
			
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
			IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDB);
			while (risultato.next()) {

				// Possono esistere piu' servizi applicativi che hanno un CN con subject e issuer diverso.
				
				byte[] certificatoBytes = jdbcAdapter.getBinaryData(risultato, "certificate");
				Certificate certificato = ArchiveLoader.load(ArchiveType.CER, certificatoBytes, 0, null);
				//int tmpStrict = rs.getInt("cert_strict_verification");
				//boolean strict = tmpStrict == CostantiDB.TRUE;
				
				if(!certificate.equals(certificato.getCertificate(),strictVerifier)) {
					continue;
				}
				
				sa=this.getServizioApplicativo(risultato.getLong("id"));
				lista.add(sa);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiPrincipalList(String principal) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiPrincipalList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
	

	public List<AccessoRegistroRegistro> registriList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "registriList";
		int idLista = Liste.REGISTRI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<AccessoRegistroRegistro> lista = new ArrayList<AccessoRegistroRegistro>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("registriList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectCountField("*", "cont");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
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
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.REGISTRI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("tipo");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			AccessoRegistroRegistro arr;
			while (risultato.next()) {

				arr = new AccessoRegistroRegistro();

				arr.setId(risultato.getLong("id"));
				arr.setNome(risultato.getString("nome"));
				arr.setTipo(RegistroTipo.toEnumConstant(risultato.getString("tipo")));

				lista.add(arr);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<ResponseCachingConfigurazioneRegola> responseCachingConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "responseCachingConfigurazioneRegolaList";
		int idLista = Liste.CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;
		int offset;
		int limit;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ResponseCachingConfigurazioneRegola> lista = new ArrayList<ResponseCachingConfigurazioneRegola>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("responseCachingConfigurazioneRegolaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectCountField("id", "cont");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addOrderBy("id");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			List<Long> idLong = new ArrayList<>();
			while (risultato.next()) { 
				idLong.add(risultato.getLong("id"));
			}
			risultato.close();
			stmt.close();
			
			if(!idLong.isEmpty()) {
				
				for (Long idLongRegola : idLong) {
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
					sqlQueryObject.addSelectField("id");
					sqlQueryObject.addSelectField("status_min");
					sqlQueryObject.addSelectField("status_max");
					sqlQueryObject.addSelectField("fault");
					sqlQueryObject.addSelectField("cache_seconds");
					sqlQueryObject.addWhereCondition("id=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idLongRegola);
					risultato = stmt.executeQuery();
					
					if(risultato.next()) {
						ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
						
						regola.setId(risultato.getLong("id"));
						int status_min = risultato.getInt("status_min");
						int status_max = risultato.getInt("status_max");
						if(status_min>0) {
							regola.setReturnCodeMin(status_min);
						}
						if(status_max>0) {
							regola.setReturnCodeMax(status_max);
						}

						int fault = risultato.getInt("fault");
						if(CostantiDB.TRUE == fault) {
							regola.setFault(true);
						}
						else if(CostantiDB.FALSE == fault) {
							regola.setFault(false);
						}
						
						int cacheSeconds = risultato.getInt("cache_seconds");
						if(cacheSeconds>0) {
							regola.setCacheTimeoutSeconds(cacheSeconds);
						}

						lista.add(regola);
					}
					
					risultato.close();
					stmt.close();
				}
				
			}
			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public boolean existsResponseCachingConfigurazioneRegola(Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsResponseCachingConfigurazioneRegola");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE_CACHE_REGOLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			
			if(statusMin != null) {
				sqlQueryObject.addWhereCondition("status_min = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_min");
			}
			
			if(statusMax != null) {
				sqlQueryObject.addWhereCondition("status_max = ?");
			} else {
				sqlQueryObject.addWhereIsNullCondition("status_max");
			}
			
			if(fault) {
				sqlQueryObject.addWhereCondition("fault = ?");
			} else {
				sqlQueryObject.addWhereCondition("fault = ?");
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			if(statusMin != null)
				stmt.setInt(parameterIndex ++, statusMin);
			if(statusMax != null)
				stmt.setInt(parameterIndex ++, statusMax);
			if(fault) {
				stmt.setInt(parameterIndex ++, CostantiDB.TRUE);
			} else {
				stmt.setInt(parameterIndex ++, CostantiDB.FALSE);
			}
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<ConfigurazioneUrlInvocazioneRegola> proxyPassConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "proxyPassConfigurazioneRegolaList";
		int idLista = Liste.CONFIGURAZIONE_PROXY_PASS_REGOLA;
		int offset;
		int limit;
		String queryString;
		String search;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ConfigurazioneUrlInvocazioneRegola> lista = new ArrayList<ConfigurazioneUrlInvocazioneRegola>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("responseCachingConfigurazioneRegolaList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectCountField("id", "cont");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");            
			sqlQueryObject.addSelectField("posizione");       
			sqlQueryObject.addSelectField("stato");           
			sqlQueryObject.addSelectField("descrizione");     
			sqlQueryObject.addSelectField("regexpr");         
			sqlQueryObject.addSelectField("regola");          
			sqlQueryObject.addSelectField("contesto_esterno");
			sqlQueryObject.addSelectField("base_url");        
			sqlQueryObject.addSelectField("protocollo");      
			sqlQueryObject.addSelectField("ruolo");           
			sqlQueryObject.addSelectField("service_binding"); 
			sqlQueryObject.addSelectField("tipo_soggetto");   
			sqlQueryObject.addSelectField("nome_soggetto");
			
			if (!search.equals("")) {
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
			}
			sqlQueryObject.addOrderBy("posizione");
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			
			while (risultato.next()) { 
				ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
				regola.setId(risultato.getLong("id"));
				regola.setNome(risultato.getString("nome"));
				regola.setPosizione(risultato.getInt("posizione"));
				regola.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(risultato.getString("stato")));
				regola.setDescrizione(risultato.getString("descrizione"));
				if(risultato.getInt("regexpr") == CostantiDB.TRUE) {
					regola.setRegexpr(true);
				}else {
					regola.setRegexpr(false);
				}
				regola.setRegola(risultato.getString("regola"));
				regola.setContestoEsterno(risultato.getString("contesto_esterno"));
				regola.setBaseUrl(risultato.getString("base_url"));
				regola.setProtocollo(risultato.getString("protocollo"));
				regola.setRuolo(DriverConfigurazioneDB_LIB.getEnumRuoloContesto(risultato.getString("ruolo")));
				regola.setServiceBinding(DriverConfigurazioneDB_LIB.getEnumServiceBinding(risultato.getString("service_binding")));
				String tipoSoggetto = risultato.getString("tipo_soggetto");
				String nomeSoggetto = risultato.getString("nome_soggetto");
				if(tipoSoggetto!=null && !"".equals(tipoSoggetto) && nomeSoggetto!=null && !"".equals(nomeSoggetto)) {
					regola.setSoggetto(new IdSoggetto(new IDSoggetto(tipoSoggetto, nomeSoggetto)));
				}
				
				lista.add(regola);
			
			}
			risultato.close();
			stmt.close();
			
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public boolean existsProxyPassConfigurazioneRegola(String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String queryString;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsResponseCachingConfigurazioneRegola");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			int count = 0;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_URL_REGOLE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome = ?");
			
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int parameterIndex = 1;
			stmt.setString(parameterIndex ++, nome);
			
			risultato = stmt.executeQuery();
			if (risultato.next())
				count = risultato.getInt(1);
			risultato.close();
			stmt.close();

			return count > 0;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<Soggetto> soggettiList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiList";
		int idLista = Liste.SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("soggettiList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && !superuser.equals(""))
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
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("is_router");
				sqlQueryObject.addSelectField("is_default");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("is_router");
				sqlQueryObject.addSelectField("is_default");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(this.useSuperUser && superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && !superuser.equals(""))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				sog.setDescrizione(risultato.getString("descrizione"));
				sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
				sog.setRouter(risultato.getInt("is_router") == CostantiDB.TRUE ? true : false);
				sog.setDominioDefault(risultato.getInt("is_default") == CostantiDB.TRUE ? true : false);
				lista.add(sog);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<Soggetto> soggettiWithServiziList(ISearch ricerca) throws DriverConfigurazioneException {
		return soggettiWithServiziList(null,ricerca);
	}
	public List<Soggetto> soggettiWithServiziList(String superuser,ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiWithServiziList";
		int idLista = Liste.SOGGETTI;
		int offset;
		int limit;
		//String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		//search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("soggettiWithServiziList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectCountField(this.tabellaSoggetti+".id", "cont", true);
			sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = "+CostantiDB.SERVIZI+".id_soggetto");
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".superuser=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser))){
				stmt.setString(1, superuser);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(this.tabellaSoggetti+".id");
			sqlQueryObject.addSelectField(this.tabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addSelectField(this.tabellaSoggetti+".nome_soggetto");
			sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = "+CostantiDB.SERVIZI+".id_soggetto");
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".superuser=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy(this.tabellaSoggetti+".tipo_soggetto");
			sqlQueryObject.addOrderBy(this.tabellaSoggetti+".nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser))){
				stmt.setString(1, superuser);
			}
			risultato = stmt.executeQuery();

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("id"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				lista.add(sog);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public String[] soggettiServizioApplicativoList(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		String[] silList = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("soggettiServizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();
			if (risultato.next())
				silList = new String[risultato.getInt(1)];
			risultato.close();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipoSA) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<IDServizioApplicativoDB> silList = new ArrayList<IDServizioApplicativoDB>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			if(credenziale!=null) {
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer = ?");
				}
			}
			if(tipoSA!=null) {
				if(CostantiConfigurazione.CLIENT.equals(tipoSA)) {
					sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
				}
				else {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
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
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(index++, superuser);
			if(credenziale!=null) {
				stmt.setString(index++, credenziale.getValue());
				if(CredenzialeTipo.APIKEY.equals(credenziale) && appId!=null) {
					stmt.setString(index++, CostantiDB.getISSUER_APIKEY(appId));
				}
			}
			if(tipoSA!=null) {
				stmt.setString(index++, tipoSA);
				if(CostantiConfigurazione.CLIENT.equals(tipoSA)) {
					stmt.setInt(index++, CostantiDB.TRUE);
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

	public List<ServizioApplicativo> soggettiServizioApplicativoList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		String filterRuoloServizioApplicativo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
		TipologiaFruizione tipologiaFruizione = null;
		TipologiaErogazione tipologiaErogazione = null;
		if(filterRuoloServizioApplicativo!=null && !"".equals(filterRuoloServizioApplicativo)) {
			if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaErogazione = TipologiaErogazione.DISABILITATO;
			}
			else if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaFruizione = TipologiaFruizione.DISABILITATO;
			}
		}
		
		String filterSoggettoTipoNome = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SOGGETTO);
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(filterSoggettoTipoNome!=null && !"".equals(filterSoggettoTipoNome)) {
			filterSoggettoTipo = filterSoggettoTipoNome.split("/")[0];
			filterSoggettoNome = filterSoggettoTipoNome.split("/")[1];
		}
		
		String filterRuolo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_RUOLO);
		
		String filterTipoServizioApplicativo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO);
		
		String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_CREDENZIALI);
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		this.log.debug("filterSoggettoNome : " + filterSoggettoNome);
		this.log.debug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.log.debug("filterRuoloServizioApplicativo : " + filterRuoloServizioApplicativo);
		this.log.debug("filterRuolo : " + filterRuolo);
		this.log.debug("filterTipoServizioApplicativo : " + filterTipoServizioApplicativo);
		this.log.debug("filterTipoCredenziali : " + filterTipoCredenziali);
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<ServizioApplicativo> silList = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.useSuperUser && superuser!=null && !superuser.equals("")) {
				stmt.setString(index++, superuser);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.useSuperUser && superuser!=null && (!"".equals(superuser))) {
				stmt.setString(index++, superuser);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				ServizioApplicativo sa = this.getServizioApplicativo(risultato.getLong("id"));
				silList.add(sa);
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<ServizioApplicativo> soggettiServizioApplicativoList(Long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		List<String> tipoSoggettiProtocollo = null;
		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		if(idSoggetto==null || idSoggetto <=0) {
			try {
				tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		
		String filterRuoloServizioApplicativo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
		TipologiaFruizione tipologiaFruizione = null;
		TipologiaErogazione tipologiaErogazione = null;
		if(filterRuoloServizioApplicativo!=null && !"".equals(filterRuoloServizioApplicativo)) {
			if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaErogazione = TipologiaErogazione.DISABILITATO;
			}
			else if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaFruizione = TipologiaFruizione.DISABILITATO;
			}
		}
		
		String filterTipoServizioApplicativo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO);
		
		String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_CREDENZIALI);
		
		this.log.debug("search : " + search);
		this.log.debug("filterProtocollo : " + filterProtocollo);
		this.log.debug("filterProtocolli : " + filterProtocolli);
		this.log.debug("filterRuoloServizioApplicativo : " + filterRuoloServizioApplicativo);
		this.log.debug("filterTipoServizioApplicativo : " + filterTipoServizioApplicativo);
		this.log.debug("filterTipoCredenziali : " + filterTipoCredenziali);
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<ServizioApplicativo> silList = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if (idSoggetto!=null) {
				stmt.setLong(index++, idSoggetto);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addSelectField("nome_soggetto");
					sqlQueryObject.addSelectField("tipo_soggetto");
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addOrderBy("nome_soggetto");
					sqlQueryObject.addOrderBy("tipo_soggetto");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addSelectField("nome_soggetto");
					sqlQueryObject.addSelectField("tipo_soggetto");
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addOrderBy("nome_soggetto");
					sqlQueryObject.addOrderBy("tipo_soggetto");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
				}
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if (idSoggetto!=null) {
				stmt.setLong(index++, idSoggetto);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				ServizioApplicativo sa = this.getServizioApplicativo(risultato.getLong("id"));
				silList.add(sa);
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	@Override
	public List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDServizio> servizi = new ArrayList<IDServizio>();
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getServizi_SoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition("id_soggetto_virtuale<>-1");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_virtuale is not null", "nome_soggetto_virtuale is not null");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			while (rs.next()) {
				IDServizio servizio = IDServizioUtils.buildIDServizio(rs.getString("tipo_servizio"), rs.getString("servizio"), 
						new IDSoggetto(rs.getString("tipo_soggetto_virtuale"), rs.getString("nome_soggetto_virtuale")), 
						rs.getInt("versione_servizio"));
				servizi.add(servizio);
				this.log.info("aggiunto Servizio " + servizio.toString() + " alla lista dei servizi erogati da Soggetti Virtuali");
			}

			if(servizi.size()==0){
				throw new DriverConfigurazioneNotFound("[getServizi_SoggettiVirtuali] Servizi erogati da Soggetti virtuali non esistenti");
			}

			this.log.info("aggiunti " + servizi.size() + " servizi alla lista dei servizi erogati da Soggetti Virtuali");
			return servizi;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SqlException: " + se.getMessage(), se);
		}  catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("Exception: " + se.getMessage(), se);
		}finally {
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

	}

	/**
	 * Ritorna il {@linkplain Soggetto} utilizzando il {@link DriverConfigurazioneDB} che ha l'id passato come parametro 
	 */
	public Soggetto getSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getSoggetto(idSoggetto,null);
	}
	public Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idSoggetto <= 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] L'id del soggetto deve essere > 0.");

		Soggetto Soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sqlQuery = "";

		if(conParam!=null){
			con = conParam;
		}
		else if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSoggetto(longId)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);
			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("operazione this.atomica = " + this.atomica);

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				Soggetto = new Soggetto();

				Soggetto.setId(rs.getLong("id"));

				Soggetto.setNome(rs.getString("nome_soggetto"));

				Soggetto.setTipo(rs.getString("tipo_soggetto"));

				Soggetto.setSuperUser(rs.getString("superuser"));

				String tmp = rs.getString("descrizione");
				Soggetto.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("identificativo_porta");
				Soggetto.setIdentificativoPorta(((tmp == null || tmp.equals("")) ? null : tmp));

				int defaultR = rs.getInt("is_default");
				boolean is_default = false;
				if (defaultR == CostantiDB.TRUE)
					is_default = true;
				Soggetto.setDominioDefault(is_default);
				
				int router = rs.getInt("is_router");
				boolean isrouter = false;
				if (router == CostantiDB.TRUE)
					isrouter = true;
				Soggetto.setRouter(isrouter);

				tmp = rs.getString("pd_url_prefix_rewriter");
				Soggetto.setPdUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("pa_url_prefix_rewriter");
				Soggetto.setPaUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				// Creava OutOfMemory, non dovrebbe servire
//				// Aggiungo i servizi applicativi
//				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
//				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
//				sqlQueryObject.addSelectField("id");
//				sqlQueryObject.addSelectField("nome");
//				sqlQueryObject.addWhereCondition("id_soggetto=?");
//				sqlQuery = sqlQueryObject.createSQLQuery();
//				stm1 = con.prepareStatement(sqlQuery);
//				stm1.setLong(1, rs.getLong("id"));
//				rs1 = stm1.executeQuery();
//
//				ServizioApplicativo servizioApplicativo = null;
//				while (rs1.next()) {
//					// setto solo il nome come da specifica
//					servizioApplicativo = new ServizioApplicativo();
//					servizioApplicativo.setId(rs1.getLong("id"));
//					servizioApplicativo.setNome(rs1.getString("nome"));
//					Soggetto.addServizioApplicativo(servizioApplicativo);
//				}
//				rs1.close();
//				stm1.close();

			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
			}

			return Soggetto;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
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
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
			}// ignore exception
		}
	}

	public PortaApplicativa getPortaApplicativa(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getPortaApplicativa(id,null);
	}
	public PortaApplicativa getPortaApplicativa(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (id <= 0)
			throw new DriverConfigurazioneException("[getPortaApplicativa] L'id della Porta Applicativa deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		String sqlQuery = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativa(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			long idPortaApplicativa = id;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectField("azione");
			sqlQueryObject.addSelectField("mode_azione");
			sqlQueryObject.addSelectField("pattern_azione");
			sqlQueryObject.addSelectField("nome_porta_delegante_azione");
			sqlQueryObject.addSelectField("force_interface_based_azione");
			sqlQueryObject.addSelectField("validazione_contenuti_stato");
			sqlQueryObject.addSelectField("validazione_contenuti_tipo");
			sqlQueryObject.addSelectField("validazione_contenuti_mtom");
			sqlQueryObject.addSelectField("mtom_request_mode");
			sqlQueryObject.addSelectField("mtom_response_mode");
			sqlQueryObject.addSelectField("security");
			sqlQueryObject.addSelectField("security_mtom_req");
			sqlQueryObject.addSelectField("security_mtom_res");
			sqlQueryObject.addSelectField("security_request_mode");
			sqlQueryObject.addSelectField("security_response_mode");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "idPA");
			sqlQueryObject.addSelectAliasField(this.tabellaSoggetti+".id", "idSoggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePorta");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("behaviour");
			sqlQueryObject.addSelectField("autenticazione");
			sqlQueryObject.addSelectField("autenticazione_opzionale");
			sqlQueryObject.addSelectField("token_policy");
			sqlQueryObject.addSelectField("token_opzionale");
			sqlQueryObject.addSelectField("token_validazione");
			sqlQueryObject.addSelectField("token_introspection");
			sqlQueryObject.addSelectField("token_user_info");
			sqlQueryObject.addSelectField("token_forward");
			sqlQueryObject.addSelectField("token_options");
			sqlQueryObject.addSelectField("token_authn_issuer");
			sqlQueryObject.addSelectField("token_authn_client_id");
			sqlQueryObject.addSelectField("token_authn_subject");
			sqlQueryObject.addSelectField("token_authn_username");
			sqlQueryObject.addSelectField("token_authn_email");
			sqlQueryObject.addSelectField("autorizzazione");
			sqlQueryObject.addSelectField("autorizzazione_xacml");
			sqlQueryObject.addSelectField("autorizzazione_contenuto");
			sqlQueryObject.addSelectField("ruoli_match");
			sqlQueryObject.addSelectField("scope_stato");
			sqlQueryObject.addSelectField("scope_match");
			sqlQueryObject.addSelectField("ricerca_porta_azione_delegata");
			sqlQueryObject.addSelectField("msg_diag_severita");
			sqlQueryObject.addSelectField("tracciamento_esiti");
			sqlQueryObject.addSelectField("stato");
			sqlQueryObject.addSelectField("cors_stato");
			sqlQueryObject.addSelectField("cors_tipo");
			sqlQueryObject.addSelectField("cors_all_allow_origins");
			sqlQueryObject.addSelectField("cors_allow_credentials");
			sqlQueryObject.addSelectField("cors_allow_max_age");
			sqlQueryObject.addSelectField("cors_allow_max_age_seconds");
			sqlQueryObject.addSelectField("cors_allow_origins");
			sqlQueryObject.addSelectField("cors_allow_headers");
			sqlQueryObject.addSelectField("cors_allow_methods");
			sqlQueryObject.addSelectField("cors_allow_expose_headers");
			sqlQueryObject.addSelectField("response_cache_stato");
			sqlQueryObject.addSelectField("response_cache_seconds");
			sqlQueryObject.addSelectField("response_cache_max_msg_size");
			sqlQueryObject.addSelectField("response_cache_hash_url");
			sqlQueryObject.addSelectField("response_cache_hash_query");
			sqlQueryObject.addSelectField("response_cache_hash_query_list");
			sqlQueryObject.addSelectField("response_cache_hash_headers");
			sqlQueryObject.addSelectField("response_cache_hash_hdr_list");
			sqlQueryObject.addSelectField("response_cache_hash_payload");
			sqlQueryObject.addSelectField("response_cache_control_nocache");
			sqlQueryObject.addSelectField("response_cache_control_maxage");
			sqlQueryObject.addSelectField("response_cache_control_nostore");
			sqlQueryObject.addSelectField("id_accordo");
			sqlQueryObject.addSelectField("id_port_type");
			sqlQueryObject.addSelectField("options");
			sqlQueryObject.addSelectField("id_sa_default");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+this.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPortaApplicativa);

			rs = stm.executeQuery();

			if (rs.next()) {

				PortaApplicativa pa = new PortaApplicativa();
				pa.setId(rs.getLong("idPA"));
				long idSoggetto = rs.getLong("idSoggetto");
				pa.setIdSoggetto(idSoggetto);
				pa.setDescrizione(rs.getString("descrizionePorta"));
				pa.setNome(rs.getString("nome_porta"));
				pa.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pa.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

				pa.setOptions(rs.getString("options"));
				
				pa.setIdAccordo(rs.getLong("id_accordo"));
				pa.setIdPortType(rs.getLong("id_port_type"));

				PortaApplicativaSoggettoVirtuale paSoggVirt = null;
				String nomeSoggVirt=rs.getString("nome_soggetto_virtuale");
				String tipoSoggVirt=rs.getString("tipo_soggetto_virtuale");
				/*
				long idSoggVirt = -1;
				try{
					idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, this.tipoDB,this.tabellaSoggetti);
				}catch (Exception e) {
					log.error(e);
				}
				 */
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					long idSoggVirt = -1;
					try{
						idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, this.tipoDB,this.tabellaSoggetti);
					}catch (Exception e) {
						this.log.error(e.getMessage(),e);
					}
					paSoggVirt=new PortaApplicativaSoggettoVirtuale();
					paSoggVirt.setId(idSoggVirt);
					paSoggVirt.setNome(nomeSoggVirt);
					paSoggVirt.setTipo(tipoSoggVirt);
				}
				pa.setSoggettoVirtuale(paSoggVirt);

				PortaApplicativaServizio paServizio = null;
				String nomeServizio = rs.getString("servizio");
				String tipoServizioPA = rs.getString("tipo_servizio");
				Integer versioneServizioPA = rs.getInt("versione_servizio");

				String nomeProprietarioServizio = null;
				String tipoProprietarioServizio = null;
				if(nomeSoggVirt!=null && !nomeSoggVirt.equals("") && tipoSoggVirt!=null && !tipoSoggVirt.equals("")){
					nomeProprietarioServizio=nomeSoggVirt;
					tipoProprietarioServizio=tipoSoggVirt;
				}else{
					nomeProprietarioServizio=pa.getNomeSoggettoProprietario();
					tipoProprietarioServizio=pa.getTipoSoggettoProprietario();
				}

				long idServizioPA=-1;
				try {
					idServizioPA = DBUtils.getIdServizio(nomeServizio, tipoServizioPA, versioneServizioPA, nomeProprietarioServizio, tipoProprietarioServizio, con, this.tipoDB,this.tabellaSoggetti);
				} catch (Exception e) {
					// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
					// in un database che non ' quello della controlstation ma quello pdd.
					//this.log.info(e);
				}
				if( (idServizioPA>0) || (nomeServizio!=null && !nomeServizio.equals("") && tipoServizioPA!=null && !tipoServizioPA.equals("")) ){
					paServizio = new PortaApplicativaServizio();
					paServizio.setNome(nomeServizio);
					paServizio.setTipo(tipoServizioPA);
					paServizio.setVersione(versioneServizioPA);
					paServizio.setId(idServizioPA);
				}
				pa.setServizio(paServizio);

				
				String azione = rs.getString("azione");
				String modeAzione = rs.getString("mode_azione");
				PortaApplicativaAzione paAzione=null;
				if ((azione != null && !"-".equals(azione) && !"".equals(azione)) || (modeAzione!=null && !"".equals(modeAzione)) ) {
					paAzione=new PortaApplicativaAzione();
					paAzione.setNome(azione);
					paAzione.setIdentificazione(PortaApplicativaAzioneIdentificazione.toEnumConstant(modeAzione));
					paAzione.setPattern(rs.getString("pattern_azione"));
					paAzione.setNomePortaDelegante(rs.getString("nome_porta_delegante_azione"));
					paAzione.setForceInterfaceBased(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("force_interface_based_azione")));
					pa.setAzione(paAzione);
				}
				
				//ricevuta asincrona_(a)simmetrica
				pa.setRicevutaAsincronaAsimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_asim")));
				pa.setRicevutaAsincronaSimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_sim")));

				//integrazione
				pa.setIntegrazione(rs.getString("integrazione"));

				//scadenza correlazione applicativa
				String scadenzaCorrelazione = rs.getString("scadenza_correlazione_appl");
				CorrelazioneApplicativa corr= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				String queryCorrApp = sqlQueryObject.createSQLQuery();
				PreparedStatement stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaApplicativa);
				ResultSet rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corr==null) corr=new CorrelazioneApplicativa();
					CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRichiestaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					corr.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				if(corr!=null && scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals(""))
					corr.setScadenza(scadenzaCorrelazione);
				pa.setCorrelazioneApplicativa(corr);
				/*
				  if(scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals("")) {
				  CorrelazioneApplicativa corr= new CorrelazioneApplicativa();
				  corr.setScadenza(scadenzaCorrelazione);
				  pd.setCorrelazioneApplicativa(corr);
				  }
				 */


				// correlazione applicativa risposta
				CorrelazioneApplicativaRisposta corrApplRisposta= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				queryCorrApp = sqlQueryObject.createSQLQuery();
				stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaApplicativa);
				rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corrApplRisposta==null) corrApplRisposta=new CorrelazioneApplicativaRisposta();
					CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRispostaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					corrApplRisposta.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				pa.setCorrelazioneApplicativaRisposta(corrApplRisposta);



				// Gestione funzionalita' Attachments
				pa.setAllegaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("allega_body")));
				pa.setScartaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("scarta_body")));
				pa.setGestioneManifest(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));

				// Stateless
				pa.setStateless(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stateless")));
				
				// Behaviour
				String behaviour = rs.getString("behaviour");
				if(behaviour!=null && !"".equals(behaviour)) {
					pa.setBehaviour(new PortaApplicativaBehaviour());
					pa.getBehaviour().setNome(behaviour);
				}

				// Autorizzazione
				pa.setAutenticazione(rs.getString("autenticazione"));
				pa.setAutenticazioneOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("autenticazione_opzionale")));
				
				// GestioneToken
				String tokenPolicy = rs.getString("token_policy");
				if(tokenPolicy!=null && !"".equals(tokenPolicy)) {
					GestioneToken gestioneToken = new GestioneToken();
					gestioneToken.setPolicy(tokenPolicy);
					gestioneToken.setTokenOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_opzionale")));
					gestioneToken.setValidazione(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_validazione")));
					gestioneToken.setIntrospection(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_introspection")));
					gestioneToken.setUserInfo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_user_info")));
					gestioneToken.setForward(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_forward")));
					gestioneToken.setOptions(rs.getString("token_options"));
					
					String token_authn_issuer = rs.getString("token_authn_issuer");
					String token_authn_client_id = rs.getString("token_authn_client_id");
					String token_authn_subject = rs.getString("token_authn_subject");
					String token_authn_username = rs.getString("token_authn_username");
					String token_authn_email = rs.getString("token_authn_email");
					if(token_authn_issuer!=null ||
							token_authn_client_id!=null ||
									token_authn_subject!=null ||
											token_authn_username!=null ||
													token_authn_email!=null) {
						gestioneToken.setAutenticazione(new GestioneTokenAutenticazione());
						gestioneToken.getAutenticazione().setIssuer(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_issuer));
						gestioneToken.getAutenticazione().setClientId(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_client_id));
						gestioneToken.getAutenticazione().setSubject(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_subject));
						gestioneToken.getAutenticazione().setUsername(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_username));
						gestioneToken.getAutenticazione().setEmail(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_email));
					}

					pa.setGestioneToken(gestioneToken);
				}
				
				// Autorizzazione
				pa.setAutorizzazione(rs.getString("autorizzazione"));
				pa.setXacmlPolicy(rs.getString("autorizzazione_xacml"));
				pa.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				
				// Ricerca Porta Azione Delegata
				if(rs.getString("ricerca_porta_azione_delegata")!=null){
					pa.setRicercaPortaAzioneDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricerca_porta_azione_delegata")));
				}
				
				// Tracciamento
				String msg_diag_severita = rs.getString("msg_diag_severita");
				String tracciamento_esiti = rs.getString("tracciamento_esiti");
				if(msg_diag_severita!=null || tracciamento_esiti!=null) {
					PortaTracciamento tracciamento = new PortaTracciamento();
					tracciamento.setSeverita(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita));
					tracciamento.setEsiti(tracciamento_esiti);
					pa.setTracciamento(tracciamento);
				}
				
				// Stato
				if(rs.getString("stato")!=null){
					pa.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato")));
				}


				//mtom
				MtomProcessor mtomProcessor = null;
				String mtom_request = rs.getString("mtom_request_mode");
				String mtom_response = rs.getString("mtom_response_mode");
				if( (mtom_request!=null && !mtom_request.equals(""))  ||  (mtom_response!=null && !mtom_response.equals("")) ){
					mtomProcessor = new MtomProcessor();
					if((mtom_request!=null && !mtom_request.equals(""))  ){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
						mtomProcessor.getRequestFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_request));
					}
					if((mtom_response!=null && !mtom_response.equals(""))  ){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
						mtomProcessor.getResponseFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_response));
					}
				}
					

				//validazione xsd
				String validazioneContenuti_stato = rs.getString("validazione_contenuti_stato");
				String validazioneContenuti_tipo = rs.getString("validazione_contenuti_tipo");
				String validazioneContenuti_mtom = rs.getString("validazione_contenuti_mtom");
				if(  (validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  
						||
						(validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  	)
				{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					if((validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  )
						val.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(validazioneContenuti_stato));
					if((validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  )
						val.setTipo(DriverConfigurazioneDB_LIB.getEnumValidazioneContenutiApplicativiTipo(validazioneContenuti_tipo));
					if((validazioneContenuti_mtom!=null && !validazioneContenuti_mtom.equals(""))  )
						val.setAcceptMtomMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(validazioneContenuti_mtom));
					pa.setValidazioneContenutiApplicativi(val);
				}
				
				
				// messageSecurity
				
				String security = rs.getString("security");
				String security_mtom_req = rs.getString("security_mtom_req");
				String security_mtom_res = rs.getString("security_mtom_res");
				String security_request_mode = rs.getString("security_request_mode");
				String security_response_mode = rs.getString("security_response_mode");
				MessageSecurity messageSecurity = null;
				if(  (security_mtom_req!=null && !security_mtom_req.equals(""))  
						||
						(security_request_mode!=null && !security_request_mode.equals(""))  
						||
						(security_mtom_res!=null && !security_mtom_res.equals(""))  	
						||
						(security_response_mode!=null && !security_response_mode.equals("")) 
						)
				{
					messageSecurity = new MessageSecurity();
					if((security_mtom_req!=null && !security_mtom_req.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_req));
					}
					if((security_mtom_res!=null && !security_mtom_res.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_res));
					}
					if((security_request_mode!=null && !security_request_mode.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setMode(security_request_mode);
					}
					if((security_response_mode!=null && !security_response_mode.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setMode(security_response_mode);
					}
				}
				
				// RuoliMatch
				String ruoliMatch = rs.getString("ruoli_match");
				if(ruoliMatch!=null && !"".equals(ruoliMatch)){
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					pa.getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(ruoliMatch));
				}
				
				// ScopeMatch
				String scopeStato = rs.getString("scope_stato");
				String scopeMatch = rs.getString("scope_match");
				if( (scopeStato!=null && !"".equals(scopeStato)) || (scopeMatch!=null && !"".equals(scopeMatch)) ){
					if(pa.getScope()==null){
						pa.setScope(new AutorizzazioneScope());
					}
					pa.getScope().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(scopeStato));
					pa.getScope().setMatch(ScopeTipoMatch.toEnumConstant(scopeMatch));
				}
				
				
				// Gestione CORS
				String corsStato = rs.getString("cors_stato");
				if(corsStato!=null && !"".equals(corsStato)) {
					pa.setGestioneCors(new CorsConfigurazione());
					readConfigurazioneCors(pa.getGestioneCors(), rs);
				}
				
				// Gestione CacheResponse
				String responseCacheStato = rs.getString("response_cache_stato");
				if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
					pa.setResponseCaching(new ResponseCachingConfigurazione());
					readResponseCaching(idPortaApplicativa, false, false, pa.getResponseCaching(), rs, con);
				}
				
				// Servizio Applicativo di default
				long id_sa_default = rs.getLong("id_sa_default");
				
				rs.close();
				stm.close();

				
				// Servizio Applicativo di default
				if(id_sa_default>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("nome");
					sqlQueryObject.addWhereCondition("id=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, id_sa_default);

					this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
					rs = stm.executeQuery();

					// Request Flow Parameter
					if (rs.next()) {
						String nome = rs.getString("nome");
						pa.setServizioApplicativoDefault(nome);
					}
					rs.close();
					stm.close();
				}
				
				
				// Trasformazioni
				Trasformazioni trasformazioni = DriverConfigurazioneDB_LIB.readTrasformazioni(idPortaApplicativa, false, con);
				if(trasformazioni!=null) {
					pa.setTrasformazioni(trasformazioni);
				}
				
				
				if(paAzione!=null) {
					// lista azioni
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);

					this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
					rs = stm.executeQuery();

					// Request Flow Parameter
					while (rs.next()) {
						paAzione.addAzioneDelegata(rs.getString("azione"));
					}
					rs.close();
					stm.close();
				}
				
				
				
				// stato security
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(security)) {
					pa.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
				}else{
					pa.setStatoMessageSecurity(CostantiConfigurazione.DISABILITATO.toString());
				}
				
				// lista wss
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				// Request Flow Parameter
				MessageSecurityFlowParameter secRfp;
				while (rs1.next()) {
					secRfp = new MessageSecurityFlowParameter();
					secRfp.setNome(rs1.getString("nome"));
					secRfp.setValore(rs1.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getRequestFlow()==null){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
					}
					messageSecurity.getRequestFlow().addParameter(secRfp);
				}
				rs1.close();
				stm1.close();

				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				MessageSecurityFlowParameter secResfp;
				while (rs1.next()) {
					secResfp = new MessageSecurityFlowParameter();
					secResfp.setNome(rs1.getString("nome"));
					secResfp.setValore(rs1.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getResponseFlow()==null){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
					}
					messageSecurity.getResponseFlow().addParameter(secResfp);
				}
				rs1.close();
				stm1.close();


				// setto il messageSecurity
				pa.setMessageSecurity(messageSecurity);
				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				// Request Flow Parameter
				MtomProcessorFlowParameter rfp;
				while (rs1.next()) {
					rfp = new MtomProcessorFlowParameter();
					rfp.setNome(rs1.getString("nome"));
					rfp.setPattern(rs1.getString("pattern"));
					rfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rfp.setRequired(isrequired);
					
					if(mtomProcessor.getRequestFlow()==null){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getRequestFlow().addParameter(rfp);
				}
				rs1.close();
				stm1.close();

				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaApplicativa);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaApplicativa));
				rs1 = stm1.executeQuery();

				MtomProcessorFlowParameter rsfp;
				while (rs1.next()) {
					rsfp = new MtomProcessorFlowParameter();
					rsfp.setNome(rs1.getString("nome"));
					rsfp.setPattern(rs1.getString("pattern"));
					rsfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rsfp.setRequired(isrequired);
					
					if(mtomProcessor.getResponseFlow()==null){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getResponseFlow().addParameter(rsfp);
				}
				rs1.close();
				stm1.close();
				
				// set mtom
				pa.setMtomProcessor(mtomProcessor);
				

				// pa.addServizioApplicativo(servizioApplicativo); .... solo i
				// NOMI!!!
				// servizi applicativi
				long idSA = 0;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaApplicativa
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA_PA = rs.getLong("id");
					idSA = rs.getLong("id_servizio_applicativo");

					String nomeConnettore = rs.getString("connettore_nome");
					int notificaConnettore = rs.getInt("connettore_notifica");
					String descrizioneConnettore = rs.getString("connettore_descrizione");
					String statoConnettore = rs.getString("connettore_stato");
					String filtriConnettore = rs.getString("connettore_filtri");
					
					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						PortaApplicativaServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaApplicativaServizioApplicativo();
							servizioApplicativo.setId(idSA_PA);
							servizioApplicativo.setIdServizioApplicativo(idSA); 
							servizioApplicativo.setNome(rs1.getString("nome"));
							
							if(nomeConnettore!=null && !"".equals(nomeConnettore)) {
								servizioApplicativo.setDatiConnettore(new PortaApplicativaServizioApplicativoConnettore());
								servizioApplicativo.getDatiConnettore().setNome(nomeConnettore);
								servizioApplicativo.getDatiConnettore().setNotifica(notificaConnettore == CostantiDB.TRUE);
								servizioApplicativo.getDatiConnettore().setDescrizione(descrizioneConnettore);
								servizioApplicativo.getDatiConnettore().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(statoConnettore));
								
								List<String> l = convertToList(filtriConnettore);
								if(!l.isEmpty()) {
									servizioApplicativo.getDatiConnettore().setFiltroList(l);
								}
								
								Proprieta prop = null;
								sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
								sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
								sqlQueryObject.addSelectField("*");
								sqlQueryObject.addWhereCondition("id_porta=?");
								sqlQuery = sqlQueryObject.createSQLQuery();
								stm2 = con.prepareStatement(sqlQuery);
								stm2.setLong(1, idSA_PA);
								rs2=stm2.executeQuery();
								while (rs2.next()) {
									prop = new Proprieta();
									prop.setId(rs2.getLong("id"));
									prop.setNome(rs2.getString("nome"));
									prop.setValore(rs2.getString("valore"));
									servizioApplicativo.getDatiConnettore().addProprieta(prop);
								}
								rs2.close();
								stm2.close();
							}
							
							pa.addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
						
						
					}
				}
				rs.close();
				stm.close();
				
				
				if(pa.getBehaviour()!=null) {
					// behaviour prop
					Proprieta prop = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					rs=stm.executeQuery();
					while (rs.next()) {
						prop = new Proprieta();
						prop.setId(rs.getLong("id"));
						prop.setNome(rs.getString("nome"));
						prop.setValore(rs.getString("valore"));
						pa.getBehaviour().addProprieta(prop);
					}
					rs.close();
					stm.close();
				}
				
				
				// autenticazione prop
				Proprieta prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutenticazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				// autorizzazione prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutorizzazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// autorizzazione per contenuti prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaAutorizzazioneContenuto(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				
				// pa.addSetProperty(setProperty); .....
				prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprieta(prop);
				}
				rs.close();
				stm.close();
				
				
								
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pa.getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getScope()==null){
						pa.setScope(new AutorizzazioneScope());
					}
					
					Scope scope = new Scope();
					scope.setNome(rs.getString("scope"));
					pa.getScope().addScope(scope);
				
				}
				rs.close();
				stm.close();
				
				
				
				// soggetti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pa.getSoggetti()==null){
						pa.setSoggetti(new PortaApplicativaAutorizzazioneSoggetti());
					}
					
					PortaApplicativaAutorizzazioneSoggetto soggetto = new PortaApplicativaAutorizzazioneSoggetto();
					soggetto.setTipo(rs.getString("tipo_soggetto"));
					soggetto.setNome(rs.getString("nome_soggetto"));
					pa.getSoggetti().addSoggetto(soggetto);
				
				}
				rs.close();
				stm.close();
				
				
				
				// applicativi autorizzti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaApplicativa
				// prendo l'id del servizio associato, recupero il nome e
				// aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					long idSA_autorizzato = rs.getLong("id_servizio_applicativo");

					if (idSA_autorizzato != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addSelectField("tipo_soggetto");
						sqlQueryObject.addSelectField("nome_soggetto");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA_autorizzato);

						this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA_autorizzato));

						rs1 = stm1.executeQuery();

						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaApplicativaAutorizzazioneServizioApplicativo();
							servizioApplicativo.setId(idSA_autorizzato);
							servizioApplicativo.setNome(rs1.getString("nome"));
							servizioApplicativo.setTipoSoggettoProprietario(rs1.getString("tipo_soggetto"));
							servizioApplicativo.setNomeSoggettoProprietario(rs1.getString("nome_soggetto"));
							if(pa.getServiziApplicativiAutorizzati()==null) {
								pa.setServiziApplicativiAutorizzati(new PortaApplicativaAutorizzazioneServiziApplicativi());
							}
							pa.getServiziApplicativiAutorizzati().addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				
				
				
				
				
				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_LIB.readDumpConfigurazione(con, pa.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				pa.setDump(dumpConfig);
				
				
				
				// *** Aggiungo extInfo ***
				
				this.log.debug("ExtendedInfo ... ");
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaApplicativa();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> listExtInfo = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.log, pa);
					if(listExtInfo!=null && listExtInfo.size()>0){
						for (Object object : listExtInfo) {
							pa.addExtendedInfo(object);
						}
					}
				}

				
				return pa;
			} else {
				throw new DriverConfigurazioneNotFound("Porta Applicativa non esistente");
			}
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaApplicativa] Exception: " + se.getMessage(),se);
		}finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
			}catch (Exception e) {
				//ignore
			}
			try {

				if (conParam==null  && this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}

	public PortaDelegata getPortaDelegata(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getPortaDelegata(id,null);
	}
	public PortaDelegata getPortaDelegata(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (id <= 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] L'id della Porta Delegata deve essere > 0.");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm1 = null;
		ResultSet rs1 = null;
		String sqlQuery;
		PortaDelegata pd = null;
		long idPortaDelegata = id;


		if(conParam!=null){
			con = conParam;
		}
		else  if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaDelegata(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addSelectField("autenticazione");
			sqlQueryObject.addSelectField("autenticazione_opzionale");
			sqlQueryObject.addSelectField("token_policy");
			sqlQueryObject.addSelectField("token_opzionale");
			sqlQueryObject.addSelectField("token_validazione");
			sqlQueryObject.addSelectField("token_introspection");
			sqlQueryObject.addSelectField("token_user_info");
			sqlQueryObject.addSelectField("token_forward");
			sqlQueryObject.addSelectField("token_options");
			sqlQueryObject.addSelectField("token_authn_issuer");
			sqlQueryObject.addSelectField("token_authn_client_id");
			sqlQueryObject.addSelectField("token_authn_subject");
			sqlQueryObject.addSelectField("token_authn_username");
			sqlQueryObject.addSelectField("token_authn_email");
			sqlQueryObject.addSelectField("autorizzazione");
			sqlQueryObject.addSelectField("autorizzazione_xacml");
			sqlQueryObject.addSelectField("autorizzazione_contenuto");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addSelectField("id_soggetto_erogatore");
			sqlQueryObject.addSelectField("tipo_soggetto_erogatore");
			sqlQueryObject.addSelectField("nome_soggetto_erogatore");
			sqlQueryObject.addSelectField("id_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("id_azione");
			sqlQueryObject.addSelectField("nome_azione");
			sqlQueryObject.addSelectField("mode_azione");
			sqlQueryObject.addSelectField("pattern_azione");
			sqlQueryObject.addSelectField("nome_porta_delegante_azione");
			sqlQueryObject.addSelectField("force_interface_based_azione");
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("validazione_contenuti_stato");
			sqlQueryObject.addSelectField("validazione_contenuti_tipo");
			sqlQueryObject.addSelectField("validazione_contenuti_mtom");
			sqlQueryObject.addSelectField("mtom_request_mode");
			sqlQueryObject.addSelectField("mtom_response_mode");
			sqlQueryObject.addSelectField("security");
			sqlQueryObject.addSelectField("security_mtom_req");
			sqlQueryObject.addSelectField("security_mtom_res");
			sqlQueryObject.addSelectField("security_request_mode");
			sqlQueryObject.addSelectField("security_response_mode");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_DELEGATE+".descrizione", "descrizionePD");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("local_forward");
			sqlQueryObject.addSelectField("local_forward_pa");
			sqlQueryObject.addSelectField("ruoli_match");
			sqlQueryObject.addSelectField("scope_stato");
			sqlQueryObject.addSelectField("scope_match");
			sqlQueryObject.addSelectField("ricerca_porta_azione_delegata");
			sqlQueryObject.addSelectField("msg_diag_severita");
			sqlQueryObject.addSelectField("tracciamento_esiti");
			sqlQueryObject.addSelectField("stato");		
			sqlQueryObject.addSelectField("cors_stato");
			sqlQueryObject.addSelectField("cors_tipo");
			sqlQueryObject.addSelectField("cors_all_allow_origins");
			sqlQueryObject.addSelectField("cors_allow_credentials");
			sqlQueryObject.addSelectField("cors_allow_max_age");
			sqlQueryObject.addSelectField("cors_allow_max_age_seconds");
			sqlQueryObject.addSelectField("cors_allow_origins");
			sqlQueryObject.addSelectField("cors_allow_headers");
			sqlQueryObject.addSelectField("cors_allow_methods");
			sqlQueryObject.addSelectField("cors_allow_expose_headers");
			sqlQueryObject.addSelectField("response_cache_stato");
			sqlQueryObject.addSelectField("response_cache_seconds");
			sqlQueryObject.addSelectField("response_cache_max_msg_size");
			sqlQueryObject.addSelectField("response_cache_hash_url");
			sqlQueryObject.addSelectField("response_cache_hash_query");
			sqlQueryObject.addSelectField("response_cache_hash_query_list");
			sqlQueryObject.addSelectField("response_cache_hash_headers");
			sqlQueryObject.addSelectField("response_cache_hash_hdr_list");
			sqlQueryObject.addSelectField("response_cache_hash_payload");
			sqlQueryObject.addSelectField("response_cache_control_nocache");
			sqlQueryObject.addSelectField("response_cache_control_maxage");
			sqlQueryObject.addSelectField("response_cache_control_nostore");
			sqlQueryObject.addSelectField("id_accordo");
			sqlQueryObject.addSelectField("id_port_type");
			sqlQueryObject.addSelectField("options");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+this.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPortaDelegata);

			rs = stm.executeQuery();

			if (rs.next()) {// ho trovato la porta delegata che cercavo

				pd = new PortaDelegata();

				pd.setOptions(rs.getString("options"));
				
				pd.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pd.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

				pd.setId(idPortaDelegata);
				
				pd.setAutenticazione(rs.getString("autenticazione"));
				pd.setAutenticazioneOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("autenticazione_opzionale")));
				
				String tokenPolicy = rs.getString("token_policy");
				if(tokenPolicy!=null && !"".equals(tokenPolicy)) {
					GestioneToken gestioneToken = new GestioneToken();
					gestioneToken.setPolicy(tokenPolicy);
					gestioneToken.setTokenOpzionale(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_opzionale")));
					gestioneToken.setValidazione(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_validazione")));
					gestioneToken.setIntrospection(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_introspection")));
					gestioneToken.setUserInfo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(rs.getString("token_user_info")));
					gestioneToken.setForward(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("token_forward")));
					gestioneToken.setOptions(rs.getString("token_options"));
					
					
					String token_authn_issuer = rs.getString("token_authn_issuer");
					String token_authn_client_id = rs.getString("token_authn_client_id");
					String token_authn_subject = rs.getString("token_authn_subject");
					String token_authn_username = rs.getString("token_authn_username");
					String token_authn_email = rs.getString("token_authn_email");
					if(token_authn_issuer!=null ||
							token_authn_client_id!=null ||
									token_authn_subject!=null ||
											token_authn_username!=null ||
													token_authn_email!=null) {
						gestioneToken.setAutenticazione(new GestioneTokenAutenticazione());
						gestioneToken.getAutenticazione().setIssuer(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_issuer));
						gestioneToken.getAutenticazione().setClientId(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_client_id));
						gestioneToken.getAutenticazione().setSubject(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_subject));
						gestioneToken.getAutenticazione().setUsername(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_username));
						gestioneToken.getAutenticazione().setEmail(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(token_authn_email));
					}
					
					pd.setGestioneToken(gestioneToken);
				}
				
				pd.setAutorizzazione(rs.getString("autorizzazione"));
				pd.setXacmlPolicy(rs.getString("autorizzazione_xacml"));
				pd.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				pd.setDescrizione(rs.getString("descrizionePD"));
				pd.setIdSoggetto(rs.getLong("id_soggetto"));
				pd.setNome(rs.getString("nome_porta"));

				//idaccordo
				pd.setIdAccordo(rs.getLong("id_accordo"));
				//id port type
				pd.setIdPortType(rs.getLong("id_port_type"));

				//se mode e' settato allora creo oggetto
				String nomeSoggettoErogatore = rs.getString("nome_soggetto_erogatore");
				String tipoSoggettoErogatore = rs.getString("tipo_soggetto_erogatore");
				long idSoggettoErogatoreDB = rs.getLong("id_soggetto_erogatore");
				long idSoggErogatore=-1;
				if( (idSoggettoErogatoreDB==-2) || (idSoggettoErogatoreDB>0) ){
					idSoggErogatore = idSoggettoErogatoreDB;
				}
				else{
					try {
						idSoggErogatore = DBUtils.getIdSoggetto(nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.tipoDB,this.tabellaSoggetti);
					} catch (CoreException e) {
						this.log.debug(e.getMessage(),e);
					}
				}
				PortaDelegataSoggettoErogatore SoggettoErogatore = null;
				if(idSoggErogatore>0){
					SoggettoErogatore = new PortaDelegataSoggettoErogatore();
					SoggettoErogatore.setId(idSoggErogatore);
					SoggettoErogatore.setNome(nomeSoggettoErogatore);
					SoggettoErogatore.setTipo(tipoSoggettoErogatore);
					pd.setSoggettoErogatore(SoggettoErogatore);
				}else{
					throw new DriverConfigurazioneException("Soggetto Erogatore della Porta Delegata ["+pd.getNome()+"] non presente.");
				}

				//se mode e' settato allora creo oggetto
				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("nome_servizio");
				Integer versioneServizio = rs.getInt("versione_servizio");
				long idServizioDB = rs.getLong("id_servizio");
				long idServizio=-1;
				if( (idServizioDB==-2) || (idServizioDB>0) ){
					idServizio = idServizioDB;
				}
				else{
					try {
						idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.tipoDB,this.tabellaSoggetti);
					} catch (Exception e) {
						// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
						// in un database che non ' quello della controlstation ma quello pdd.
						//this.log.debug(e);
					}
				}
				PortaDelegataServizio pdServizio = null;
				if(idServizio>0){
					pdServizio=new PortaDelegataServizio();
					pdServizio.setId(idServizio);
					pdServizio.setNome(nomeServizio);
					pdServizio.setTipo(tipoServizio);
					pdServizio.setVersione(versioneServizio);
				}
				// fix
				else {
					pdServizio=new PortaDelegataServizio();
					pdServizio.setNome(nomeServizio);
					pdServizio.setTipo(tipoServizio);
					pdServizio.setVersione(versioneServizio);
				}
				pd.setServizio(pdServizio);

				PortaDelegataAzione pdAzione = null;
				String modeAzione = rs.getString("mode_azione");
				long idAzione = rs.getLong("id_azione");
				if (idAzione>0 || (modeAzione!=null && !"".equals(modeAzione)) ) {
					pdAzione=new PortaDelegataAzione();
					pdAzione.setId(rs.getLong("id_azione"));
					pdAzione.setNome(rs.getString("nome_azione"));
					pdAzione.setIdentificazione(PortaDelegataAzioneIdentificazione.toEnumConstant(modeAzione));
					pdAzione.setPattern(rs.getString("pattern_azione"));
					pdAzione.setNomePortaDelegante(rs.getString("nome_porta_delegante_azione"));
					pdAzione.setForceInterfaceBased(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("force_interface_based_azione")));
				}
				pd.setAzione(pdAzione);






				//ricevuta asincrona_(a)simmetrica
				pd.setRicevutaAsincronaAsimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_asim")));
				pd.setRicevutaAsincronaSimmetrica(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricevuta_asincrona_sim")));
				//integrazione
				pd.setIntegrazione(rs.getString("integrazione"));
				//scadenza correlazione applicativa
				String scadenzaCorrelazione = rs.getString("scadenza_correlazione_appl");
				CorrelazioneApplicativa corr= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				String queryCorrApp = sqlQueryObject.createSQLQuery();
				PreparedStatement stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaDelegata);
				ResultSet rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corr==null) corr=new CorrelazioneApplicativa();
					CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRichiestaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					cae.setRiusoIdentificativo(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rsCorrApp.getString("riuso_id")));
					corr.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				if(corr!=null && scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals(""))
					corr.setScadenza(scadenzaCorrelazione);
				pd.setCorrelazioneApplicativa(corr);
				/*
				  if(scadenzaCorrelazione!=null && !scadenzaCorrelazione.equals("")) {
				  CorrelazioneApplicativa corr= new CorrelazioneApplicativa();
				  corr.setScadenza(scadenzaCorrelazione);
				  pd.setCorrelazioneApplicativa(corr);
				  }
				 */


				// correlazione applicativa risposta
				CorrelazioneApplicativaRisposta corrApplRisposta= null;

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.addOrderBy("id");
				sqlQueryObject.setSortType(true);
				queryCorrApp = sqlQueryObject.createSQLQuery();
				stmCorrApp = con.prepareStatement(queryCorrApp);
				stmCorrApp.setLong(1, idPortaDelegata);
				rsCorrApp = stmCorrApp.executeQuery();
				while (rsCorrApp.next()) {
					if(corrApplRisposta==null) corrApplRisposta=new CorrelazioneApplicativaRisposta();
					CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
					cae.setId(rsCorrApp.getLong("id"));
					cae.setNome(rsCorrApp.getString("nome_elemento"));
					String modeCA = rsCorrApp.getString("mode_correlazione");
					cae.setIdentificazione(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaRispostaIdentificazione(modeCA));
					//if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
					cae.setPattern(rsCorrApp.getString("pattern"));
					cae.setIdentificazioneFallita(DriverConfigurazioneDB_LIB.getEnumCorrelazioneApplicativaGestioneIdentificazioneFallita(rsCorrApp.getString("identificazione_fallita")));
					corrApplRisposta.addElemento(cae);
				}
				rsCorrApp.close();
				stmCorrApp.close();
				pd.setCorrelazioneApplicativaRisposta(corrApplRisposta);

				
				//mtom
				MtomProcessor mtomProcessor = null;
				String mtom_request = rs.getString("mtom_request_mode");
				String mtom_response = rs.getString("mtom_response_mode");
				if( (mtom_request!=null && !mtom_request.equals(""))  ||  (mtom_response!=null && !mtom_response.equals("")) ){
					mtomProcessor = new MtomProcessor();
					if((mtom_request!=null && !mtom_request.equals(""))  ){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
						mtomProcessor.getRequestFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_request));
					}
					if((mtom_response!=null && !mtom_response.equals(""))  ){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
						mtomProcessor.getResponseFlow().setMode(DriverConfigurazioneDB_LIB.getEnumMTOMProcessorType(mtom_response));
					}
				}
				

				//validazione xsd
				String validazioneContenuti_stato = rs.getString("validazione_contenuti_stato");
				String validazioneContenuti_tipo = rs.getString("validazione_contenuti_tipo");
				String validazioneContenuti_mtom = rs.getString("validazione_contenuti_mtom");
				if(  (validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  
						||
						(validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  	)
				{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					if((validazioneContenuti_stato!=null && !validazioneContenuti_stato.equals(""))  )
						val.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalitaConWarning(validazioneContenuti_stato));
					if((validazioneContenuti_tipo!=null && !validazioneContenuti_tipo.equals(""))  )
						val.setTipo(DriverConfigurazioneDB_LIB.getEnumValidazioneContenutiApplicativiTipo(validazioneContenuti_tipo));
					if((validazioneContenuti_mtom!=null && !validazioneContenuti_mtom.equals(""))  )
						val.setAcceptMtomMessage(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(validazioneContenuti_mtom));
					pd.setValidazioneContenutiApplicativi(val);
				}

				// Gestione funzionalita' Attachments
				pd.setAllegaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("allega_body")));
				pd.setScartaBody(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("scarta_body")));
				pd.setGestioneManifest(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("gestione_manifest")));

				// Stateless
				pd.setStateless(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stateless")));

				// LocalForward
				String statoLocalForward = rs.getString("local_forward");
				PortaDelegataLocalForward pdLocalForward = new PortaDelegataLocalForward();
				pdLocalForward.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(statoLocalForward));
				pdLocalForward.setPortaApplicativa(rs.getString("local_forward_pa"));
				if(pdLocalForward.getStato()!=null) {
					pd.setLocalForward(pdLocalForward);
				}
				else {
					pd.setLocalForward(null);
				}
				
				// Ricerca Porta Azione Delegata
				if(rs.getString("ricerca_porta_azione_delegata")!=null){
					pd.setRicercaPortaAzioneDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("ricerca_porta_azione_delegata")));
				}
				
				// Tracciamento
				String msg_diag_severita = rs.getString("msg_diag_severita");
				String tracciamento_esiti = rs.getString("tracciamento_esiti");
				if(msg_diag_severita!=null || tracciamento_esiti!=null) {
					PortaTracciamento tracciamento = new PortaTracciamento();
					tracciamento.setSeverita(DriverConfigurazioneDB_LIB.getEnumSeverita(msg_diag_severita));
					tracciamento.setEsiti(tracciamento_esiti);
					pd.setTracciamento(tracciamento);
				}
				
				// Stato
				if(rs.getString("stato")!=null){
					pd.setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("stato")));
				}
				

				// messageSecurity			
				String security = rs.getString("security");
				String security_mtom_req = rs.getString("security_mtom_req");
				String security_mtom_res = rs.getString("security_mtom_res");
				String security_request_mode = rs.getString("security_request_mode");
				String security_response_mode = rs.getString("security_response_mode");
				MessageSecurity messageSecurity = null;
				if(  (security_mtom_req!=null && !security_mtom_req.equals(""))  
						||
						(security_request_mode!=null && !security_request_mode.equals(""))  
						||
						(security_mtom_res!=null && !security_mtom_res.equals(""))  	
						||
						(security_response_mode!=null && !security_response_mode.equals("")) 
						)
				{
					messageSecurity = new MessageSecurity();
					if((security_mtom_req!=null && !security_mtom_req.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_req));
					}
					if((security_mtom_res!=null && !security_mtom_res.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(security_mtom_res));
					}
					if((security_request_mode!=null && !security_request_mode.equals(""))  ){
						if(messageSecurity.getRequestFlow()==null) {
							messageSecurity.setRequestFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getRequestFlow().setMode(security_request_mode);
					}
					if((security_response_mode!=null && !security_response_mode.equals(""))  ){
						if(messageSecurity.getResponseFlow()==null) {
							messageSecurity.setResponseFlow(new MessageSecurityFlow());	
						}
						messageSecurity.getResponseFlow().setMode(security_response_mode);
					}
				}

				
				// RuoliMatch
				String ruoliMatch = rs.getString("ruoli_match");
				if(ruoliMatch!=null && !"".equals(ruoliMatch)){
					if(pd.getRuoli()==null){
						pd.setRuoli(new AutorizzazioneRuoli());
					}
					pd.getRuoli().setMatch(RuoloTipoMatch.toEnumConstant(ruoliMatch));
				}
				
				// ScopeMatch
				String scopeStato = rs.getString("scope_stato");
				String scopeMatch = rs.getString("scope_match");
				if( (scopeStato!=null && !"".equals(scopeStato)) || (scopeMatch!=null && !"".equals(scopeMatch)) ){
					if(pd.getScope()==null){
						pd.setScope(new AutorizzazioneScope());
					}
					pd.getScope().setStato(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(scopeStato));
					pd.getScope().setMatch(ScopeTipoMatch.toEnumConstant(scopeMatch));
				}
				
				// Gestione CORS
				String corsStato = rs.getString("cors_stato");
				if(corsStato!=null && !"".equals(corsStato)) {
					pd.setGestioneCors(new CorsConfigurazione());
					readConfigurazioneCors(pd.getGestioneCors(), rs);
				}
				
				// Gestione CacheResponse
				String responseCacheStato = rs.getString("response_cache_stato");
				if(responseCacheStato!=null && !"".equals(responseCacheStato)) {
					pd.setResponseCaching(new ResponseCachingConfigurazione());
					readResponseCaching(idPortaDelegata, false, true, pd.getResponseCaching(), rs, con);
				}
				
				rs.close();
				stm.close();
						
				
				// Trasformazioni
				Trasformazioni trasformazioni = DriverConfigurazioneDB_LIB.readTrasformazioni(idPortaDelegata, true, con);
				if(trasformazioni!=null) {
					pd.setTrasformazioni(trasformazioni);
				}

				
				if(pdAzione!=null) {
					// lista azioni
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
					sqlQueryObject.addSelectField("*");
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLQuery();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);

					this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
					rs = stm.executeQuery();

					// Request Flow Parameter
					while (rs.next()) {
						pdAzione.addAzioneDelegata(rs.getString("azione"));
					}
					rs.close();
					stm.close();
				}
				
				
				
				// stato security
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(security)) {
					pd.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
				}else{
					pd.setStatoMessageSecurity(CostantiConfigurazione.DISABILITATO.toString());
				}
				
				// lista wss
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs = stm.executeQuery();

				// Request Flow Parameter
				MessageSecurityFlowParameter secRfp;
				while (rs.next()) {
					secRfp = new MessageSecurityFlowParameter();
					secRfp.setNome(rs.getString("nome"));
					secRfp.setValore(rs.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getRequestFlow()==null){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
					}
					messageSecurity.getRequestFlow().addParameter(secRfp);
				}
				rs.close();
				stm.close();
				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs = stm.executeQuery();

				MessageSecurityFlowParameter secResfp;
				while (rs.next()) {
					secResfp = new MessageSecurityFlowParameter();
					secResfp.setNome(rs.getString("nome"));
					secResfp.setValore(rs.getString("valore"));
					if(messageSecurity==null){
						messageSecurity = new MessageSecurity();
					}
					if(messageSecurity.getResponseFlow()==null){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
					}
					messageSecurity.getResponseFlow().addParameter(secResfp);
				}
				rs.close();
				stm.close();


				// setto il messageSecurity
				pd.setMessageSecurity(messageSecurity);

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaDelegata);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs1 = stm1.executeQuery();

				// Request Flow Parameter
				MtomProcessorFlowParameter rfp;
				while (rs1.next()) {
					rfp = new MtomProcessorFlowParameter();
					rfp.setNome(rs1.getString("nome"));
					rfp.setPattern(rs1.getString("pattern"));
					rfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rfp.setRequired(isrequired);
					
					if(mtomProcessor.getRequestFlow()==null){
						mtomProcessor.setRequestFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getRequestFlow().addParameter(rfp);
				}
				rs1.close();
				stm1.close();

				// Response Flow Parameter
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, idPortaDelegata);

				this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				rs1 = stm1.executeQuery();

				MtomProcessorFlowParameter rsfp;
				while (rs1.next()) {
					rsfp = new MtomProcessorFlowParameter();
					rsfp.setNome(rs1.getString("nome"));
					rsfp.setPattern(rs1.getString("pattern"));
					rsfp.setContentType(rs1.getString("content_type"));
					int required = rs1.getInt("required");
					boolean isrequired = false;
					if (required == CostantiDB.TRUE)
						isrequired = true;
					rsfp.setRequired(isrequired);
					
					if(mtomProcessor.getResponseFlow()==null){
						mtomProcessor.setResponseFlow(new MtomProcessorFlow());
					}
					mtomProcessor.getResponseFlow().addParameter(rsfp);
				}
				rs1.close();
				stm1.close();
				
				// set mtom
				pd.setMtomProcessor(mtomProcessor);
				
				
				// servizi applicativi
				long idSA = 0;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				// per ogni entry con id_porta == idPortaDelegata
				// prendo l'id del servizio applicativo associato, recupero il
				// nome e aggiungo
				// il servizio applicativo alla PortaDelegata da ritornare
				while (rs.next()) {
					idSA = rs.getLong("id_servizio_applicativo");

					if (idSA != 0) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
						sqlQueryObject.addSelectField("nome");
						sqlQueryObject.addWhereCondition("id=?");
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm1 = con.prepareStatement(sqlQuery);
						stm1.setLong(1, idSA);

						this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSA));

						rs1 = stm1.executeQuery();

						PortaDelegataServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new PortaDelegataServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							pd.addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}

				}
				rs.close();
				stm.close();
				
				
				// proprieta autenticazione
				Proprieta prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutenticazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// proprieta autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutorizzazione(prop);
				}
				rs.close();
				stm.close();
				
				
				
				// proprieta autorizzazione contenuto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprietaAutorizzazioneContenuto(prop);
				}
				rs.close();
				stm.close();
				
				
				
				
				// pd.addSetProperty(setProperty); .....
				prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new Proprieta();
					prop.setId(rs.getLong("id"));
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pd.addProprieta(prop);
				}
				rs.close();
				stm.close();
				
							
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pd.getRuoli()==null){
						pd.setRuoli(new AutorizzazioneRuoli());
					}
					
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(rs.getString("ruolo"));
					pd.getRuoli().addRuolo(ruolo);
				
				}
				rs.close();
				stm.close();
				
				
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				rs = stm.executeQuery();

				while (rs.next()) {
					
					if(pd.getScope()==null){
						pd.setScope(new AutorizzazioneScope());
					}
					
					Scope scope = new Scope();
					scope.setNome(rs.getString("scope"));
					pd.getScope().addScope(scope);
				
				}
				rs.close();
				stm.close();
				
				
				
				// dump_config
				DumpConfigurazione dumpConfig = DriverConfigurazioneDB_LIB.readDumpConfigurazione(con, pd.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				pd.setDump(dumpConfig);
				
				
				
				
				// *** Aggiungo extInfo ***
				
				this.log.debug("ExtendedInfo ...");
				ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
				IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaDelegata();
				if(extInfoConfigurazioneDriver!=null){
					List<Object> listExtInfo = extInfoConfigurazioneDriver.getAllExtendedInfo(con, this.log, pd);
					if(listExtInfo!=null && listExtInfo.size()>0){
						for (Object object : listExtInfo) {
							pd.addExtendedInfo(object);
						}
					}
				}
				
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaDelegata] Nessuna PortaDelegata trovata.");
			}

			return pd;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound de) {
			throw de;
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
				if(rs1!=null) rs1.close();
				if(stm1!=null) stm1.close();
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



	public List<String> servizioApplicativoRuoliList(long idSA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoRuoliList";
		int idLista = Liste.SERVIZIO_APPLICATIVO_RUOLI;
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

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
		
		return listIdRuoli;
	}
	
	
	
	//RESET


	/**
	 * Reset delle tabelle del db gestito da questo driver
	 */
	@Override
	public void reset() throws DriverConfigurazioneException {
		this.reset(true);
	}
	@Override
	public void reset(boolean resetConfigurazione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("reset");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::reset] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		//this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_URL);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SCOPE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AZIONI);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_HEADER);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_URL);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI);
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

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
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
			sqlQueryObject.addDeleteTable(this.tabellaSoggetti);
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
			sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_SOAP);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE_TRASPORTO);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.GESTIONE_ERRORE);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			if(resetConfigurazione){

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.REGISTRI);
				updateString = sqlQueryObject.createSQLDelete();
				stmt = con.prepareStatement(updateString);
				stmt.executeUpdate();
				stmt.close();

				/* ROUTING DI DEFAULT */
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.ROUTING);
				sqlQueryObject.addWhereCondition(true, true, "tiporotta='registro'", "registrorotta=0", "is_default=1");
				updateString = sqlQueryObject.createSQLDelete();
				stmt = con.prepareStatement(updateString);
				stmt.executeUpdate();
				stmt.close();

				/* NON DEVE ESSERE CANCELLATA!
				updateString = "DELETE FROM " + CostantiDB.CONFIGURAZIONE;
				stmt = con.prepareStatement(updateString);
				stmt.executeUpdate();
				stmt.close();
				 */
			}


		} catch (SQLException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la reset : " + qe.getMessage(),qe);
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
	 * Reset delle tabelle del db govwayConsole gestito da questo driver
	 */
	public void resetCtrlstat() throws DriverConfigurazioneException {
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		String updateString;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("resetCtrlstat");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::resetCtrlstat] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		//this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_EROGAZIONE_PA);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.MAPPING_FRUIZIONE_PD);
			updateString = sqlQueryObject.createSQLDelete();
			stmt = con.prepareStatement(updateString);
			stmt.executeUpdate();
			stmt.close();

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addDeleteTable(CostantiDB.SERVIZI_FRUITORI);
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
		} catch (SQLException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
		}catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::resetCtrlstat] Errore durante la reset : " + qe.getMessage(),qe);
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
	 * Verifica l'esistenza di una porta applicativa.
	 * se l'azione e' specificata ma non vi sono porte applicative che mathcano i criteri allora
	 * effettua la ricerca senza tener conto dell'azione. Per una ricerca piu precisa utilizzare 
	 * existsPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale) con il parametro ricercaPuntuale a true
	 * @param idPA id della porta applicativa da verificare
	 * @return true se la porta applicativa esiste, false altrimenti
	 * @throws DriverConfigurazioneException 
	 */    
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException {

		try{
			return getPortaApplicativa(idPA)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}
	}

	public boolean existsPortaApplicativaServizioApplicativo(Long idServizioApplicativo) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaApplicativaServizioApplicativo");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizioApplicativo);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public boolean existsPortaApplicativaSoggetto(Long idSoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaApplicativaSoggetto");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public List<PortaApplicativa> getPorteApplicativeWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteApplicativeWithServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ?", "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			stm.setLong(index++, idServizio);
			stm.setString(index++, tiposervizio);
			stm.setString(index++, nomeservizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tiposoggetto);
			stm.setString(index++, nomesoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, versioneServizio, idSoggetto, idSoggetto, tiposoggetto, nomesoggetto));
			rs = stm.executeQuery();

			while (rs.next()) {
				PortaApplicativa pa = new PortaApplicativa();
				pa.setNome(rs.getString("nome_porta"));
				lista.add(pa);
			}

			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public PortaApplicativa getPortaApplicativaWithSoggettoAndServizio(String nome, Long idSoggetto, Long idServizio, 
			String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		PortaApplicativa pa = new PortaApplicativa();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativaWithSoggettoAndServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithSoggettoAndServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
					"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
					"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? ",
					"id_soggetto_virtuale = ? AND id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			
			stm.setString(index++, nome);

			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, 0);

			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idServizio);
			stm.setLong(index++, 0);

			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tipoServizio);
			stm.setString(index++, nomeServizio);
			stm.setInt(index++, versioneServizio);

			stm.setLong(index++, idSoggetto);
			stm.setLong(index++, idServizio);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto, idServizio, -1, idSoggetto, idServizio));
			rs = stm.executeQuery();

			if (rs.next()) {
				Soggetto sogg = getSoggetto(rs.getLong("id_soggetto"));
				pa.setId(rs.getLong("id"));
				pa.setNome(rs.getString("nome_porta"));
				pa.setIdSoggetto(rs.getLong("id_soggetto"));
				pa.setNomeSoggettoProprietario(sogg.getNome());
				pa.setTipoSoggettoProprietario(sogg.getTipo());
			}else{
				throw new Exception("PA non trovata ["+DBUtils.formatSQLString(sqlQuery, idSoggetto, idServizio, -1, idSoggetto, idServizio)+"]");
			}

			rs.close();
			stm.close();

			return pa;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public List<IDPortaApplicativa> getPortaApplicativaAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativaAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		List<IDPortaApplicativa> id = new ArrayList<IDPortaApplicativa>();
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(rs.getString("nome_porta"));
				id.add(idPA);
			}

			rs.close();
			stm.close();

		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

		if(id.size()>0){
			return id;
		}else{
			throw new DriverConfigurazioneNotFound("Porte Applicative che possiedono l'azione ["+nome+"] non esistenti");
		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public boolean existsPortaApplicativaAzione(String nome) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaApplicativaAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException {

		try{
			return getPortaDelegata(idPD)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}	
	}

	public boolean existsPortaDelegataServizioApplicativo(Long idServizioApplicativo) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaDelegataServizioApplicativo");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizioApplicativo);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public boolean existsPortaDelegataSoggetto(Long idSoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaDelegataSoggetto");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio,
			Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteDelegateWithServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND nome_servizio = ? AND versione_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore = ?", "tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			int index = 1;
			stm.setLong(index++, idServizio);
			stm.setString(index++, tiposervizio);
			stm.setString(index++, nomeservizio);
			stm.setInt(index++, versioneServizio);
			stm.setLong(index++, idSoggetto);
			stm.setString(index++, tiposoggetto);
			stm.setString(index++, nomesoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, versioneServizio, idSoggetto, tiposoggetto, nomesoggetto));
			rs = stm.executeQuery();

			while (rs.next()) {
				lista.add(this.getPortaDelegata(rs.getLong("id")));
			}

			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteDelegateWithServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_servizio = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio));
			rs = stm.executeQuery();

			while (rs.next()) {
				PortaDelegata pde = this.getPortaDelegata(rs.getLong("id"));
				lista.add(pde);
			}

			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	
	public List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore, String tipo, 
			boolean checkIM, boolean checkConnettoreAbilitato) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<IDServizioApplicativoDB> lista = new ArrayList<IDServizioApplicativoDB>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getServiziApplicativiWithIdErogatore");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServiziApplicativiWithIdErogatore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
			this.log.debug("eseguo query : " + debugQuery);
			
			rs = stm.executeQuery();

			while (rs.next()) {
				
				IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
				idSA.setIdSoggettoProprietario(new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto")));
				idSA.setNome(rs.getString("nomeServAppl"));
				idSA.setId(rs.getLong("idServAppl"));
				lista.add(idSA);
			}

			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public List<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaDelegataAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		List<IDPortaDelegata> id = new ArrayList<IDPortaDelegata>();
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome_azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(rs.getString("nome_porta"));
				id.add(idPD);
			}

			rs.close();
			stm.close();

		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

		if(id.size()>0){
			return id;
		}else{
			throw new DriverConfigurazioneNotFound("Porte Delegate che possiedono l'azione ["+nome+"] non esistenti");
		}

	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public boolean existsPortaDelegataAzione(String nome) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaDelegataAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataAzione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_azione=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}


	/**
	 * Verifica l'esistenza di un servizio applicativo.
	 *
	 * @param idServizioApplicativo id del servizio applicativo
	 * @return true se il servizio applicativo esiste, false altrimenti
	 * @throws DriverConfigurazioneException
	 */    
	@Override
	public boolean existsServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException{

		IDSoggetto idSoggetto = idServizioApplicativo.getIdSoggettoProprietario();
		if(idSoggetto==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Soggetto Fruitore non Impostato.");
		if(idServizioApplicativo.getNome()==null || "".equals(idServizioApplicativo.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Servizio Applicativo non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Soggetto Fruitore non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativo] Nome Soggetto Fruitore non Impostato.");

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsServizioApplicativo");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createAccessoRegistro] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			return DriverConfigurazioneDB_LIB.getIdServizioApplicativo(idServizioApplicativo.getNome(), idSoggetto.getTipo(), idSoggetto.getNome(), con, this.tipoDB,this.tabellaSoggetti)>0;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public long getIdServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {

		if(idSoggetto==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Soggetto Fruitore non Impostato.");
		if(nomeServizioApplicativo==null || "".equals(nomeServizioApplicativo))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Servizio Applicativo non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Fruitore non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Fruitore non Impostato.");

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getIdServizioApplicativo");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			return DriverConfigurazioneDB_LIB.getIdServizioApplicativo(nomeServizioApplicativo, idSoggetto.getTipo(), idSoggetto.getNome(), con, this.tipoDB,this.tabellaSoggetti);
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	public boolean existsServizioApplicativoSoggetto(Long idSoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsServizioApplicativoSoggetto");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativoSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			boolean esiste = false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next())
				esiste = true;

			rs.close();
			stm.close();

			return esiste;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}

	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverConfigurazioneException {

		if(idSoggetto==null)throw new DriverConfigurazioneException("[existsSoggetto::existsSoggetto] Soggetto non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[existsSoggetto::existsServizioApplicativo] Nome Soggetto non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[existsSoggetto::existsServizioApplicativo] Nome Soggetto non Impostato.");

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsSoggetto");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			return DBUtils.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo(), con, this.tipoDB,this.tabellaSoggetti)>0;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}
	}

	/**
	 * Controlla se il Servizio Applicativo e' in uso, in tal caso le informazioni vengono inserite nella mappa per specificare dove il servizio applicativo e' in uso
	 * @param sa
	 * @param whereIsInUso
	 * @return true se il servizio applicativo e' in uso, false altrimenti
	 * @throws DriverConfigurazioneException
	 */
	public boolean isServizioApplicativoInUso(ServizioApplicativo sa, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("isServizioApplicativoInUso");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::isServizioApplicativoInUso] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			ArrayList<String> nomiPorteApplicative = new ArrayList<String>();
			boolean isInUso=false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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

			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				// TODO: handle exception
			}

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}


	/**
	 * Controlla se il Servizio Applicativo e' in uso, in tal caso le informazioni vengono inserite nella mappa per specificare dove il servizio applicativo e' in uso
	 * @param sa
	 * @param whereIsInUso
	 * @return true se il servizio applicativo e' in uso, false altrimenti
	 * @throws DriverConfigurazioneException
	 */
	public boolean isInUso(ServizioApplicativo sa, Map<ErrorsHandlerCostant, ArrayList<?>> whereIsInUso) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("isInUso(servizioApplicativo)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::isServizioApplicativoInUso] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {

			ArrayList<IDPortaApplicativa> idsPA = new ArrayList<IDPortaApplicativa>();
			ArrayList<IDPortaDelegata> idsPD = new ArrayList<IDPortaDelegata>();
			boolean isInUso=false;
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");

			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				isInUso=true;
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(risultato.getString("nome_porta"));
				idsPA.add(idPA);
			}
			risultato.close();
			stmt.close();


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_servizio_applicativo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");

			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, sa.getId());
			risultato = stmt.executeQuery();
			while (risultato.next()) {
				isInUso=true;
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(risultato.getString("nome_porta"));
				idsPD.add(idPD);
			}
			risultato.close();
			stmt.close();


			if(isInUso){
				if(whereIsInUso==null) whereIsInUso=new HashMap<ErrorsHandlerCostant, ArrayList<?>>();

				if(idsPA.size()>0)
					whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_APPLICATIVE, idsPA);

				if(idsPD.size()>0)
					whereIsInUso.put(ErrorsHandlerCostant.IN_USO_IN_PORTE_DELEGATE, idsPD);
			}

			return isInUso;

		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				// TODO: handle exception
			}

			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}

		}

	}


	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverConfigurazioneException eccezione che contiene il motivo della mancata connessione
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
				con = getConnectionFromDatasource("isAlive");
				if(con == null)
					throw new Exception("Connessione is null");
				// test:
				try {
					stmtTest = con.createStatement();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DB_INFO_CONSOLE);
					sqlQueryObject.addSelectField("*");
					String sqlQuery = sqlQueryObject.createSQLQuery();
					stmtTest.execute(sqlQuery);
				}catch(Throwable t) {
					try{
						if(stmtTest!=null)
							stmtTest.close();
					}catch(Exception e){}
					try {
						stmtTest = con.createStatement();
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DB_INFO);
						sqlQueryObject.addSelectField("*");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stmtTest.execute(sqlQuery);
					}catch(Throwable tInternal) {
						throw new UtilsMultiException(t,tInternal);
					}
				}
			} catch (Exception e) {
				throw new CoreException("Connessione alla configurazione non disponibile: "+e.getMessage(),e);

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
				try {
					stmtTest = this.globalConnection.createStatement();
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
					sqlQueryObject.addFromTable(CostantiDB.DB_INFO_CONSOLE);
					sqlQueryObject.addSelectField("*");
					String sqlQuery = sqlQueryObject.createSQLQuery();
					stmtTest.execute(sqlQuery);
				}catch(Throwable t) {
					try{
						if(stmtTest!=null)
							stmtTest.close();
					}catch(Exception e){}
					try {
						stmtTest = this.globalConnection.createStatement();
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.DB_INFO);
						sqlQueryObject.addSelectField("*");
						String sqlQuery = sqlQueryObject.createSQLQuery();
						stmtTest.execute(sqlQuery);
					}catch(Throwable tInternal) {
						throw new UtilsMultiException(t,tInternal);
					}
				}
			} catch (Exception e) {
				throw new CoreException("Connessione alla configurazione registro non disponibile: "+e.getMessage(),e);

			}finally{
				try{
					if(stmtTest!=null)
						stmtTest.close();
				}catch(Exception e){}
			}
		}
	}


	public List<PortaApplicativa> serviziPorteAppList(String tipoServizio,String nomeServizio, Integer versioneServizio,
			long idServizio, long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziPorteAppList";
		int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;
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

		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?  AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			int index = 1;
			
			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_servizio");
				sqlQueryObject.addSelectField("id_soggetto_virtuale");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_servizio");
				sqlQueryObject.addSelectField("id_soggetto_virtuale");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=? AND versione_servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			index = 1;
			
			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);
			stmt.setLong(index++, 0);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);
			stmt.setInt(index++, versioneServizio);

			stmt.setLong(index++, idSoggettoErogatore);
			stmt.setLong(index++, idServizio);

			risultato = stmt.executeQuery();

			PortaApplicativa pa = null;

			while (risultato.next()) {

				pa = this.getPortaApplicativa(risultato.getLong("id"));

				lista.add(pa);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	
	public List<PortaDelegata> serviziFruitoriPorteDelegateList(long idSoggetto, String tipoServizio,String nomeServizio,Long idServizio, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziFruitoriPorteDelegateList";
		int idLista = Liste.SERVIZI_FRUITORI_PORTE_DELEGATE;
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

		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			int index = 1;
			
			stmt.setLong(index++, idSoggetto);
			
			if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
				stmt.setLong(index++, idSoggettoErogatore);
			}
			stmt.setString(index++, tipoSoggettoErogatore);
			stmt.setString(index++, nomeSoggettoErogatore);
			
			if(idServizio!=null && idServizio > 0){
				stmt.setLong(index++, idServizio);
			}
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);

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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
					sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore=?" , "tipo_soggetto_erogatore=? AND nome_soggetto_erogatore=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_soggetto_erogatore=?");
					sqlQueryObject.addWhereCondition("nome_soggetto_erogatore=?");
				}
				if(idServizio!=null && idServizio > 0){
					sqlQueryObject.addWhereCondition(false, "id_servizio=?" , "tipo_servizio=? AND nome_servizio=?");
				}
				else{
					sqlQueryObject.addWhereCondition("tipo_servizio=?");
					sqlQueryObject.addWhereCondition("nome_servizio=?");
				}
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			index = 1;
			
			stmt.setLong(index++, idSoggetto);
			
			if(idSoggettoErogatore!=null && idSoggettoErogatore > 0){
				stmt.setLong(index++, idSoggettoErogatore);
			}
			stmt.setString(index++, tipoSoggettoErogatore);
			stmt.setString(index++, nomeSoggettoErogatore);
			
			if(idServizio!=null && idServizio > 0){
				stmt.setLong(index++, idServizio);
			}
			stmt.setString(index++, tipoServizio);
			stmt.setString(index++, nomeServizio);

			risultato = stmt.executeQuery();

			PortaDelegata pd = null;

			while (risultato.next()) {

				pd = this.getPortaDelegata(risultato.getLong("id"));

				lista.add(pd);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
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
	
	

	public List<String> connettoriList() throws DriverConfigurazioneException {
		String nomeMetodo = "connettoriList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<String> lista = new ArrayList<String>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_PROPERTIES);
			sqlQueryObject.addSelectField("nome_connettore");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			while (risultato.next())
				lista.add(risultato.getString("nome_connettore"));

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaApplicativa> getPorteApplicativaByIdProprietario(long idProprietario) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteApplicativaByIdProprietario");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteApplicativaByIdProprietario] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		ArrayList<PortaApplicativa> lista = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idProprietario);
			rs = stm.executeQuery();
			lista=new ArrayList<PortaApplicativa>();
			while(rs.next()){
				long id=rs.getLong("id");
				PortaApplicativa pd = this.getPortaApplicativa(id);
				lista.add(pd);
			}
			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {

			}

		}

	}

	/**
	 * Recupera tutte le porte applicative relative al servizio idServizio, erogato dal soggetto erogatore del servizio.
	 * @param idSE
	 * @return List<PortaApplicativa>
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaApplicativa> getPorteApplicative(IDServizio idSE) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteApplicative(idServizio)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteApplicative] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		ArrayList<PortaApplicativa> lista = null;
		try {
			IDSoggetto erogatore = idSE.getSoggettoErogatore();
			String tipoServizio = idSE.getTipo();
			String nomeServizio = idSE.getNome();
			Integer versioneServizio = idSE.getVersione();

			long idSoggetto = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
			long idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.addWhereCondition(false,"id_servizio=?","tipo_servizio=? AND servizio=?");

			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);
			stm.setLong(2, idServizio);
			stm.setString(3, tipoServizio);
			stm.setString(4, nomeServizio);
			rs = stm.executeQuery();
			lista=new ArrayList<PortaApplicativa>();
			while(rs.next()){
				long id=rs.getLong("id");
				PortaApplicativa pd = this.getPortaApplicativa(id);
				lista.add(pd);
			}
			rs.close();
			stm.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {

			}

		}

	}



	/**
	 * Recupera tutte le porte delegate appartenenti al soggetto 'fruitore'
	 * @param fruitore
	 * @return Lista di {@link PortaDelegata}
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaDelegata> getPorteDelegateByFruitore(IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		return getPorteDelegate(null, fruitore,filters);
	}

	/**
	 * Recupera tutte le porte delegate del soggetto fruitore, relative al servizio idServizio (se diverso da null), erogato dal soggetto erogatore del servizio.
	 * @param idSE L'id del servizio, puo essere null in tal caso si comporta come il metodo getPorteDelegateByFruitore
	 * @return List<PortaDelegata>
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaDelegata> getPorteDelegate(IDServizio idSE,IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString = "";
		int idLista = Liste.PORTE_DELEGATE;


		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteDelegate(idServizio,fruitore)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException(
						"[DriverConfigurazioneDB::getPorteDelegate] Exception accedendo al datasource :"
								+ e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		ArrayList<PortaDelegata> lista = null;
		try {

			IDSoggetto erogatore = null;
			String nomeErogatore = null;
			String tipoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			Integer versioneServizio = null;

			if(idSE!=null){
				erogatore = idSE.getSoggettoErogatore();
				nomeErogatore = erogatore.getNome();
				tipoErogatore = erogatore.getTipo();
				tipoServizio = idSE.getTipo();
				nomeServizio = idSE.getNome();
				versioneServizio = idSE.getVersione();
			}

			long idSoggettoFruitore = DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, this.tipoDB);

			long idSoggettoErogatore =-1;
			long idServizio = -1;
			if(idSE!=null){
				idSoggettoErogatore = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
				idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
			}


			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
			} 
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();
			if (risultato.next())
				filters.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;


			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);

			if(idSE!=null){
				sqlQueryObject.addWhereCondition(false,"id_servizio=?","tipo_servizio=? AND nome_servizio=?");
				sqlQueryObject.addWhereCondition(false,"id_soggetto_erogatore=?","nome_soggetto_erogatore=? AND tipo_soggetto_erogatore=?");
			}

			if (!search.equals("")) { 
				// con search
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);				
			}
			//limit and order by
			sqlQueryObject.addOrderBy("nome_porta");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idSoggettoFruitore);
			if(idSE!=null){
				stmt.setLong(2, idServizio);
				stmt.setString(3, tipoServizio);
				stmt.setString(4, nomeServizio);
				stmt.setLong(5, idSoggettoErogatore);
				stmt.setString(6, nomeErogatore);
				stmt.setString(7, tipoErogatore);
			}

			risultato = stmt.executeQuery();
			lista=new ArrayList<PortaDelegata>();
			while(risultato.next()){
				long id=risultato.getLong("id");
				PortaDelegata pd = this.getPortaDelegata(id);
				lista.add(pd);
			}
			risultato.close();
			stmt.close();

			return lista;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {

			}

		}

	}

	









	// all
	public int countTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, false, filters, false, null, null);
	}
	public int countTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, false, filters, false, null, null);
	}

	// fruizione

	public int countTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, false, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}

	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, false, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, false, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}

	// erogazione

	public int countTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, false, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false, filters, false, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, isBound);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true,  null, false, filters, false, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, false, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	// engine

	private int serviziApplicativiList_engineCount(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException {

		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		Long idProprietario = null;

		int idLista = Liste.SERVIZIO_APPLICATIVO;
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));


		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.tipoDB);
				if(idProprietario<0) new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");
			}


			// *** count ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, 	serchSoloFruizione,  
					filters, checkAssociazionePorta, search, true,
					fruizioneIsBound, erogazioneIsBound);
			String queryString = sqlQueryObject.createSQLQuery();
			this.log.debug("QueryCount=["+queryString+"]");
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(proprietario!=null){
				stmt.setLong(index++, idProprietario);
			}
			if(serchSoloFruizione){
				//se fruizione non specificata voglio tutti i tipi di fruizione
				if(fruizione==null){
					stmt.setString(index++, TipologiaFruizione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
						stmt.setString(index++,fruizione.toString());
					}
				}
			}else if(searchSoloErogazione){
				//se erogazione non specificato voglio tutti i tipi di erogazione
				if(erogazione==null){
					stmt.setString(index++,TipologiaErogazione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
						stmt.setString(index++,erogazione.toString());
					}
				}
			}
			
			if(fruizioneIsBound!=null){
				stmt.setString(index++,TipoAutenticazione.DISABILITATO.getValue());
			}
			
			if(erogazioneIsBound!=null){
				stmt.setString(index++,TipiConnettore.DISABILITATO.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				// stmt.setString(index++,"");  OP-708
			}
			
			risultato = stmt.executeQuery();
			int count = -1;
			if (risultato.next()){
				count =  risultato.getInt("count");
			}
			risultato.close();
			stmt.close();

			return count;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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





	// all
	public List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, false, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  null, false, filters, false, null, null);
	}

	// fruizione

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, null, true, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, true, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, true, filters, checkAssociazionePorta, isBound, null);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, fruizione, true, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}

	// erogazione

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, false, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}

	// engine

	private List<TipologiaServizioApplicativo> serviziApplicativiList_engine(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException {

		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		Long idProprietario = null;

		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		ArrayList<TipologiaServizioApplicativo> lista = new ArrayList<TipologiaServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.tipoDB);
				if(idProprietario<0) new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");
			}
			filters.setNumEntries(idLista, this.serviziApplicativiList_engineCount(proprietario, 
					erogazione, searchSoloErogazione, 
					fruizione, serchSoloFruizione, 
					filters, checkAssociazionePorta,
					fruizioneIsBound, erogazioneIsBound));



			// *** select ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, serchSoloFruizione,  
					filters, checkAssociazionePorta, search, false,
					fruizioneIsBound, erogazioneIsBound);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
			String queryString = sqlQueryObject.createSQLQuery();
			this.log.debug("Query=["+queryString+"]");
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(proprietario!=null)
				stmt.setLong(index++, idProprietario);
			if(serchSoloFruizione){
				//se fruizione non specificata voglio tutti i tipi di fruizione
				if(fruizione==null){
					stmt.setString(index++, TipologiaFruizione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
						stmt.setString(index++,fruizione.toString());
					}
				}
			}else if(searchSoloErogazione){
				//se erogazione non specificato voglio tutti i tipi di erogazione
				if(erogazione==null){
					stmt.setString(index++,TipologiaErogazione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
						stmt.setString(index++,erogazione.toString());
					}
				}
			}
			
			if(fruizioneIsBound!=null){
				stmt.setString(index++,TipoAutenticazione.DISABILITATO.getValue());
			}
			
			if(erogazioneIsBound!=null){
				stmt.setString(index++,TipiConnettore.DISABILITATO.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				// stmt.setString(index++,""); OP-708
			}
			
			risultato = stmt.executeQuery();
			while (risultato.next()){

				IDServizioApplicativo idSA = new IDServizioApplicativo();
				if(proprietario!=null){
					idSA.setIdSoggettoProprietario(proprietario);
				}else{
					long idProprietarioLetto = risultato.getLong("id_soggetto");
					Soggetto s = this.getSoggetto(idProprietarioLetto);
					idSA.setIdSoggettoProprietario(new IDSoggetto(s.getTipo(), s.getNome()));
				}
				idSA.setNome(risultato.getString("nome"));

				TipologiaServizioApplicativo tipologia = new TipologiaServizioApplicativo();
				tipologia.setIdServizioApplicativo(idSA);

				String tipologiaFruizione = risultato.getString("tipologia_fruizione");
				if(tipologiaFruizione!=null && !"".equals(tipologiaFruizione)){
					tipologia.setTipologiaFruizione(TipologiaFruizione.toEnumConstant(tipologiaFruizione));
				}
				String tipologiaErogazione = risultato.getString("tipologia_erogazione");
				if(tipologiaErogazione!=null && !"".equals(tipologiaErogazione)){
					tipologia.setTipologiaErogazione(TipologiaErogazione.toEnumConstant(tipologiaErogazione));
				}

				lista.add(tipologia);
			}
			risultato.close();
			stmt.close();
			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	private ISQLQueryObject getServiziApplicativiSearchFiltratiTipologia(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,String search, boolean count,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws Exception{

		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
		sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
		if(count){
			sqlQueryObject.addSelectCountField("*","count");
		}
		else{
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("tipologia_fruizione");
			sqlQueryObject.addSelectField("tipologia_erogazione");
		}

		if(proprietario!=null){
			sqlQueryObject.addWhereCondition("id_soggetto=?");
		}
		if(!"".equals(search)){
			sqlQueryObject.addWhereLikeCondition("nome", search,false,true,true); // escape a false, deve essere pilotato da chi lo usa!
		}

		if(serchSoloFruizione){
			//se fruizione non specificata voglio tutti i tipi di fruizione che non siano disabilitati
			if(fruizione==null){
				sqlQueryObject.addWhereCondition(true,"tipologia_fruizione is not null","tipologia_fruizione <> ?");
			}else{
				if(RicercaTipologiaFruizione.DISABILITATO.toString().equals(fruizione.toString())){
					sqlQueryObject.addWhereCondition(false,"tipologia_fruizione is null","tipologia_fruizione = ?");
				}
				else if(RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
					// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
				}
				else{
					sqlQueryObject.addWhereCondition("tipologia_fruizione = ?");
				}
			}
			if(checkAssociazionePorta){
				ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectIn.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObjectIn.addSelectField("id_servizio_applicativo");
				sqlQueryObjectIn.addWhereCondition("id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectIn);
			}
		}

		else if(searchSoloErogazione){
			//se erogazione non specificato voglio tutti i tipi di erogazione
			if(erogazione==null){
				sqlQueryObject.addWhereCondition(true,"tipologia_erogazione is not null","tipologia_erogazione <> ?");
			}else{
				if(RicercaTipologiaErogazione.DISABILITATO.toString().equals(erogazione.toString())){
					sqlQueryObject.addWhereCondition(false,"tipologia_erogazione is null","tipologia_erogazione = ?");
				}
				else if(RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
					// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
				}
				else{
					sqlQueryObject.addWhereCondition("tipologia_erogazione = ?");
				}
			}
			if(checkAssociazionePorta){
				ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObjectIn.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObjectIn.addSelectField("id_servizio_applicativo");
				sqlQueryObjectIn.addWhereCondition("id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectIn);
			}
		}
		
		if(fruizioneIsBound!=null){
			if(fruizioneIsBound){
				sqlQueryObject.addWhereIsNotNullCondition("tipoauth");
				sqlQueryObject.addWhereCondition("tipoauth<>?");
			}
			else{
				sqlQueryObject.addWhereCondition(false,"tipoauth is null","tipoauth=?");
			}
		}
		
		if(erogazioneIsBound!=null){
			
			ISQLQueryObject sqlQueryObjectConnettore = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObjectConnettore.setANDLogicOperator(true);
			sqlQueryObjectConnettore.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObjectConnettore.addSelectField("id");
			sqlQueryObjectConnettore.addWhereCondition("id_connettore_inv="+CostantiDB.CONNETTORI+".id");
			
			// Fix OP-708
			ISQLQueryObject sqlQueryObject_NullCondition = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject_NullCondition.addWhereIsNotNullCondition("url");
			ISQLQueryObject sqlQueryObject_EmptyCondition = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject_EmptyCondition.addWhereIsNotEmptyCondition("url");
			
			sqlQueryObjectConnettore.addWhereCondition(false,
					"endpointtype<>? AND endpointtype<>?",
					//"endpointtype=? AND url is not null AND url <> ?"); OP-708
					"endpointtype=? AND "+sqlQueryObject_NullCondition.createSQLConditions()+" AND "+sqlQueryObject_EmptyCondition.createSQLConditions());
			
			if(erogazioneIsBound){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectConnettore);
			}
			else{
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectConnettore);
			}
		}
		
		sqlQueryObject.setANDLogicOperator(true);

		return sqlQueryObject;
	}

	/**
	 * Recupera i servizi applicativi che hanno come tipologia di erogazione una tra quelle passate come parametro
	 * @param filters
	 * @param proprietario
	 * @return List<IDServizioApplicativo>
	 * @throws DriverConfigurazioneException
	 */
	public List<IDServizioApplicativo> serviziApplicativiList(ISearch filters, IDSoggetto proprietario, TipologiaErogazione... erogazione) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		long idProprietario = -1;

		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		ArrayList<IDServizioApplicativo> lista = new ArrayList<IDServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.tipoDB);
			if(idProprietario<0) new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");

			//count
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectCountField("*","count");
			sqlQueryObject.addWhereCondition("id_soggetto=?");

			if(!"".equals(search)){
				sqlQueryObject.addWhereLikeCondition("nome", search,true,true);
			}

			//se erogazione non specificato voglio tutti i tipi di erogazione
			String[] conditions = new String[erogazione.length];
			for (int i = 0; i < erogazione.length; i++) {
				conditions[i] = "tipologia_erogazione = ?";
			}
			sqlQueryObject.addWhereCondition(false,conditions);

			sqlQueryObject.setANDLogicOperator(true);


			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idProprietario);

			int baseIdx = 1;
			for (int i = 0; i < erogazione.length; i++) {
				stmt.setString(++baseIdx, erogazione[i].toString());
			}

			risultato = stmt.executeQuery();

			if (risultato.next()){
				filters.setNumEntries(idLista, risultato.getInt("count"));
			}
			risultato.close();
			stmt.close();

			//select
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("tipologia_fruizione");
			sqlQueryObject.addSelectField("tipologia_erogazione");

			sqlQueryObject.addWhereCondition("id_soggetto=?");

			if(!"".equals(search)){
				sqlQueryObject.addWhereLikeCondition("nome", search,true,true);
			}

			sqlQueryObject.addWhereCondition(false,conditions);

			sqlQueryObject.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);

			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idProprietario);

			baseIdx = 1;
			for (int i = 0; i < erogazione.length; i++) {
				stmt.setString(++baseIdx, erogazione[i].toString());
			}
			risultato = stmt.executeQuery();

			while (risultato.next()){
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(proprietario);
				idSA.setNome(risultato.getString("nome"));				
				lista.add(idSA);
			}
			risultato.close();
			stmt.close();

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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


	@Override
	public Openspcoop2 getImmagineCompletaConfigurazionePdD() throws DriverConfigurazioneException{

		Openspcoop2 openspcoop = new Openspcoop2();

		Configurazione conf = null;
		try{
			conf = this.getConfigurazioneGenerale();
		}catch(DriverConfigurazioneNotFound d){}
		if(conf!=null){
			try{
				conf.setAccessoRegistro(this.getAccessoRegistro());
			}catch(DriverConfigurazioneNotFound d){}
			try{
				conf.setAccessoConfigurazione(this.getAccessoConfigurazione());
			}catch(DriverConfigurazioneNotFound d){}
			try{
				conf.setAccessoDatiAutorizzazione(this.getAccessoDatiAutorizzazione());
			}catch(DriverConfigurazioneNotFound d){}
			conf.setRoutingTable(this.getRoutingTable());
			GestioneErrore compIntegrazione = null;
			try{
				compIntegrazione = this.getGestioneErroreComponenteIntegrazione();
			}catch(DriverConfigurazioneNotFound d){}
			GestioneErrore compCooperazione = null;
			try{
				compCooperazione = this.getGestioneErroreComponenteCooperazione();
			}catch(DriverConfigurazioneNotFound d){}
			if(compIntegrazione!=null && compCooperazione!=null)
			{
				ConfigurazioneGestioneErrore gee = new ConfigurazioneGestioneErrore();
				if(compIntegrazione!=null){
					gee.setComponenteIntegrazione(compIntegrazione);
				}
				if(compCooperazione!=null){
					gee.setComponenteCooperazione(compCooperazione);
				}
				conf.setGestioneErrore(gee);	
			}		
			// configurazione 
			openspcoop.setConfigurazione(conf);
		}


		// Soggetti
		List<Soggetto> soggetti = this.getAllSoggetti();
		while(soggetti.size()>0){

			Soggetto soggetto = soggetti.remove(0);

			// elimino meta-informazioni
			while(soggetto.sizePortaDelegataList()>0){
				soggetto.removePortaDelegata(0);
			}
			while(soggetto.sizePortaApplicativaList()>0){
				soggetto.removePortaApplicativa(0);
			}
			while(soggetto.sizeServizioApplicativoList()>0){
				soggetto.removeServizioApplicativo(0);
			}

			// read porte delegate
			List<PortaDelegata> pd = this.getPorteDelegateBySoggetto(soggetto.getId());
			while(pd.size()>0){
				soggetto.addPortaDelegata(pd.remove(0));
			}

			// read porte applicative
			List<PortaApplicativa> pa = this.getPorteApplicativeBySoggetto(soggetto.getId());
			while(pa.size()>0){
				soggetto.addPortaApplicativa(pa.remove(0));
			}

			// read servizi applicativi
			List<ServizioApplicativo> sa = this.getServiziApplicativiBySoggetto(soggetto.getId());
			while(sa.size()>0){
				soggetto.addServizioApplicativo(sa.remove(0));
			}

			openspcoop.addSoggetto(soggetto);
		}


		return openspcoop;
	}


	public List<Soggetto> getAllSoggetti() throws DriverConfigurazioneException {
		String nomeMetodo = "getAllSoggetti";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("id");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getSoggetto(id));

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaDelegata> getPorteDelegateBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteDelegateBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaDelegata(id));

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<PortaApplicativa> getPorteApplicativeBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteApplicativeBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaApplicativa(id));

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<ServizioApplicativo> getServiziApplicativiBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		String nomeMetodo = "getPorteApplicativeBySoggetto";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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

	public List<PortaApplicativa> getPorteApplicativeBySoggettoVirtuale(IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException {

		Connection con = null;
		boolean error = false;
		PreparedStatement stm=null;
		ResultSet rs=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = (Connection) getConnectionFromDatasource("getPorteApplicativeBySoggettoVirtuale");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeBySoggettoVirtuale] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale is not null");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale is not null");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale = ?");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String queryString = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(queryString);
			stm.setString(1, soggettoVirtuale.getTipo());
			stm.setString(2, soggettoVirtuale.getNome());

			rs = stm.executeQuery();
			while (rs.next()){
				lista.add(getPortaApplicativa(rs.getLong("id"),con));
			}
			rs.close();
			stm.close();

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeBySoggettoVirtuale] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
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
	 * Accede alla tabella connettori_properties, recupera le properties del connettore con nomeConnettore
	 * in caso di errore lancia un'eccezione.
	 * 
	 * @param nomeConnettore -
	 *                nome da prelevare
	 * @return ConnettoreProperty[]
	 * 
	 */
	public Property[] getPropertiesConnettore(String nomeConnettore) throws DriverConfigurazioneException {
		Connection con = null;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPropertiesConnettore");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverDB::getPropertiesConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;
		return getPropertiesConnettore(nomeConnettore,con);
	}
	public Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverConfigurazioneException {
		try {
			List<Property> cList = ConnettorePropertiesUtilities.getPropertiesConnettore(nomeConnettore, connection,this.tipoDB);
			return cList.toArray(new Property[cList.size()]);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("[DriverDB::getPropertiesConnettore] DriverConfigurazioneException : " + e.getMessage(),e);
		}
	}






	/**
	 * Restituisce la lista degli identificativi dei soggetti
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei soggetti
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDSoggetto> getAllIdSoggetti(
			FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdSoggetti...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = getConnectionFromDatasource("getAllIdSoggetti");
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
			}
			rs = stm.executeQuery();
			List<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				idSoggetti.add(idS);
			}
			if(idSoggetti.size()==0){
				if(filtroRicerca!=null)
					throw new DriverConfigurazioneNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverConfigurazioneNotFound("Soggetti non trovati");
			}else{
				return idSoggetti;
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdSoggetti error",e);
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
	 * Restituisce la lista degli identificativi delle porte delegate
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte delegate
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDPortaDelegata> getAllIdPorteDelegate(
			FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		List<String> nomiPD = null;
		
		this.log.debug("getAllIdPorteDelegate...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = getConnectionFromDatasource("getAllIdPorteDelegate");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			}
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdScope()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getNomeServizioApplicativo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				}
			}
			boolean porteDelegatePerAzioni = false;
			if(filtroRicerca!=null && filtroRicerca.getNomePortaDelegante()!=null) {
				porteDelegatePerAzioni = true;
				if(filtroRicerca.getAzione()!=null) {
					sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_AZIONI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".id_porta = "+CostantiDB.PORTE_DELEGATE+".id");
				}
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".ora_registrazione < ?");
				if(filtroRicerca.getTipoSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getTipoSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_soggetto_erogatore = ?");
				if(filtroRicerca.getNomeSoggettoErogatore()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_soggetto_erogatore = ?");
				if(filtroRicerca.getTipoServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".tipo_servizio = ?");
				if(filtroRicerca.getNomeServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_servizio = ?");
				if(filtroRicerca.getVersioneServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".versione_servizio = ?");
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta = ?");
				if(filtroRicerca.getIdRuolo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_RUOLI+".ruolo = ?");
				if(filtroRicerca.getIdScope()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_SCOPE+".scope = ?");
				if(filtroRicerca.getNomeServizioApplicativo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome = ?");			
				if(filtroRicerca.getStato()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".stato = ?");
				if(porteDelegatePerAzioni) {
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta_delegante_azione = ?");
					if(filtroRicerca.getAzione()!=null)
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE_AZIONI+".azione = ?");
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
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.log.debug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.log.debug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
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
				if(filtroRicerca.getTipoServizio()!=null){
					this.log.debug("tipoServizio stmt.setString("+filtroRicerca.getTipoServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoServizio());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizio()!=null){
					this.log.debug("nomeServizio stmt.setString("+filtroRicerca.getNomeServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizio());
					indexStmt++;
				}	
				if(filtroRicerca.getVersioneServizio()!=null){
					this.log.debug("versioneServizio stmt.setInt("+filtroRicerca.getVersioneServizio()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersioneServizio());
					indexStmt++;
				}	
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
					this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuolo()!=null){
					this.log.debug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdScope()!=null){
					this.log.debug("scope stmt.setString("+filtroRicerca.getIdScope().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdScope().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizioApplicativo()!=null){
					this.log.debug("servizioApplicativo stmt.setString("+filtroRicerca.getNomeServizioApplicativo()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizioApplicativo());
					indexStmt++;
				}
				if(filtroRicerca.getStato()!=null){
					this.log.debug("stato stmt.setString("+filtroRicerca.getStato().getValue()+")");
					stm.setString(indexStmt, filtroRicerca.getStato().getValue());
					indexStmt++;
				}
				if(porteDelegatePerAzioni) {
					this.log.debug("nomePortaDelegata stmt.setString("+filtroRicerca.getNomePortaDelegante()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePortaDelegante());
					indexStmt++;
					if(filtroRicerca.getAzione()!=null) {
						this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
						stm.setString(indexStmt, filtroRicerca.getAzione());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			nomiPD = new ArrayList<String>();
			while (rs.next()) {
				nomiPD.add(rs.getString("nome_porta"));
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdPorteDelegate error",e);
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
		
		if(nomiPD == null || nomiPD.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate");
		}else{
			List<IDPortaDelegata> idsPD = new ArrayList<IDPortaDelegata>();
			for (String nomePortaDelegata : nomiPD) {
				idsPD.add(this.getIDPortaDelegata(nomePortaDelegata));
			}
			return idsPD;
		}
		
	}


	/**
	 * Restituisce la lista degli identificativi delle porte applicative
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte applicative
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDPortaApplicativa> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		List<String> nomiPA = null;
		
		this.log.debug("getAllIdPorteApplicative...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = getConnectionFromDatasource("getAllIdPorteApplicative");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			}
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			if(filtroRicerca!=null && (filtroRicerca.getTipoSoggetto()!=null || filtroRicerca.getNomeSoggetto()!=null) ){
				sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdRuolo()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdScope()!=null){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
						filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
						){
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);					
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}
			boolean porteDelegatePerAzioni = false;
			if(filtroRicerca!=null && filtroRicerca.getNomePortaDelegante()!=null) {
				porteDelegatePerAzioni = true;
				if(filtroRicerca.getAzione()!=null) {
					sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".id_porta = "+CostantiDB.PORTE_APPLICATIVE+".id");
				}
			}

			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".ora_registrazione < ?");
				if(filtroRicerca.getTipoSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
				if(filtroRicerca.getNomeSoggetto()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
				if(filtroRicerca.getTipoSoggettoVirtuale()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_soggetto_virtuale = ?");
				if(filtroRicerca.getNomeSoggettoVirtuale()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_soggetto_virtuale = ?");
				if(filtroRicerca.getTipoServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".tipo_servizio = ?");
				if(filtroRicerca.getNomeServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".servizio = ?");
				if(filtroRicerca.getVersioneServizio()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".versione_servizio = ?");
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta = ?");
				if(filtroRicerca.getIdRuolo()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_RUOLI+".ruolo = ?");
				if(filtroRicerca.getIdScope()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SCOPE+".scope = ?");
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".tipo_soggetto= ?");
					}
					if(filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null) {
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SOGGETTI+".nome_soggetto= ?");
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
							){
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI+".id_servizio_applicativo = ?");
					}
				}
				if(filtroRicerca.getStato()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".stato = ?");
				if(porteDelegatePerAzioni) {
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta_delegante_azione = ?");
					if(filtroRicerca.getAzione()!=null)
						sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE_AZIONI+".azione = ?");
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
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.log.debug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.log.debug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoSoggettoVirtuale()!=null){
					this.log.debug("tipoSoggettoVirtuale stmt.setString("+filtroRicerca.getTipoSoggettoVirtuale()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggettoVirtuale());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggettoVirtuale()!=null){
					this.log.debug("nomeSoggettoVirtuale stmt.setString("+filtroRicerca.getNomeSoggettoVirtuale()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggettoVirtuale());
					indexStmt++;
				}	
				if(filtroRicerca.getTipoServizio()!=null){
					this.log.debug("tipoServizio stmt.setString("+filtroRicerca.getTipoServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoServizio());
					indexStmt++;
				}
				if(filtroRicerca.getNomeServizio()!=null){
					this.log.debug("nomeServizio stmt.setString("+filtroRicerca.getNomeServizio()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeServizio());
					indexStmt++;
				}	
				if(filtroRicerca.getVersioneServizio()!=null){
					this.log.debug("versioneServizio stmt.setInt("+filtroRicerca.getVersioneServizio()+")");
					stm.setInt(indexStmt, filtroRicerca.getVersioneServizio());
					indexStmt++;
				}
				if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
					this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdRuolo()!=null){
					this.log.debug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdScope()!=null){
					this.log.debug("scope stmt.setString("+filtroRicerca.getIdScope().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdScope().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
						(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
						filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
					if(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null) {
						this.log.debug("idSoggettoAutorizzazione.tipo stmt.setString("+filtroRicerca.getIdSoggettoAutorizzato().getTipo()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoAutorizzato().getTipo());
						indexStmt++;
					}
					if(filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null) {
						this.log.debug("idSoggettoAutorizzazione.nome stmt.setString("+filtroRicerca.getIdSoggettoAutorizzato().getNome()+")");
						stm.setString(indexStmt, filtroRicerca.getIdSoggettoAutorizzato().getNome());
						indexStmt++;
					}
				}
				if(filtroRicerca!=null){
					if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()!=null
							){
						long idSA = DBUtils.getIdServizioApplicativo(filtroRicerca.getIdServizioApplicativoAutorizzato().getNome(), 
								filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo(), filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome(), con, this.tipoDB);
						this.log.debug("idServizioApplicativoAutorizzato stmt.setLong("+idSA
								+") (getIdBy Nome["+filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()
								+"] tipoSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo()+
								"] nomeSoggetto["+filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome()+"])");
						stm.setLong(indexStmt, idSA);
						indexStmt++;
					}
				}
				if(filtroRicerca.getStato()!=null){
					this.log.debug("stato stmt.setString("+filtroRicerca.getStato().getValue()+")");
					stm.setString(indexStmt, filtroRicerca.getStato().getValue());
					indexStmt++;
				}
				if(porteDelegatePerAzioni) {
					this.log.debug("nomePortaDelegata stmt.setString("+filtroRicerca.getNomePortaDelegante()+")");
					stm.setString(indexStmt, filtroRicerca.getNomePortaDelegante());
					indexStmt++;
					if(filtroRicerca.getAzione()!=null) {
						this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
						stm.setString(indexStmt, filtroRicerca.getAzione());
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			nomiPA = new ArrayList<String>();
			while (rs.next()) {
				nomiPA.add(rs.getString("nome_porta"));
			}
			
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdPorteApplicative error",e);
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

		if(nomiPA == null || nomiPA.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate");
		}else{
			List<IDPortaApplicativa> idsPA = new ArrayList<IDPortaApplicativa>();
			for (String nomePortaApplicativa : nomiPA) {
				idsPA.add(this.getIDPortaApplicativa(nomePortaApplicativa));
			}
			return idsPA;
		}
	}


	/**
	 * Restituisce la lista degli identificativi dei servizi applicativi
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei servizi applicativi
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(
			FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.log.debug("getAllIdServiziApplicativi...");

		try {
			this.log.debug("operazione atomica = " + this.atomica);
			// prendo la connessione dal pool
			if (this.atomica)
				con = getConnectionFromDatasource("getAllIdServiziApplicativi");
			else
				con = this.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
				setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca, CostantiDB.SERVIZI_APPLICATIVI);
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
				if(filtroRicerca.getTipoSoggetto()!=null){
					this.log.debug("tipoSoggetto stmt.setString("+filtroRicerca.getTipoSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getTipoSoggetto());
					indexStmt++;
				}
				if(filtroRicerca.getNomeSoggetto()!=null){
					this.log.debug("nomeSoggetto stmt.setString("+filtroRicerca.getNomeSoggetto()+")");
					stm.setString(indexStmt, filtroRicerca.getNomeSoggetto());
					indexStmt++;
				}		
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getIdRuolo()!=null){
					this.log.debug("ruolo stmt.setString("+filtroRicerca.getIdRuolo().getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getIdRuolo().getNome());
					indexStmt++;
				}
				if(filtroRicerca.getTipo()!=null){
					this.log.debug("tipo stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				setProtocolPropertiesForSearch(stm, indexStmt, filtroRicerca, ProprietariProtocolProperty.SERVIZIO_APPLICATIVO);
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
	
	
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMTOMRequestList";
		int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter reqPar;
			while (risultato.next()) {

				reqPar = new MtomProcessorFlowParameter();
				
				reqPar.setId(risultato.getLong("id_porta"));
				reqPar.setNome(risultato.getString("nome"));
				reqPar.setPattern(risultato.getString("pattern"));
				reqPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				reqPar.setRequired(isrequired);

				

				lista.add(reqPar);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateMTOMResponseList";
		int idLista = Liste.PORTE_DELEGATE_MTOM_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaDelegata);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter resPar;
			while (risultato.next()) {
				
				resPar = new MtomProcessorFlowParameter();
				
				resPar.setId(risultato.getLong("id_porta"));
				resPar.setNome(risultato.getString("nome"));
				resPar.setPattern(risultato.getString("pattern"));
				resPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				resPar.setRequired(isrequired);

				

				lista.add(resPar);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeMTOMRequestList";
		int idLista = Liste.PORTE_APPLICATIVE_MTOM_REQUEST;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter reqPar;
			while (risultato.next()) {

				reqPar = new MtomProcessorFlowParameter();
				
				reqPar.setId(risultato.getLong("id_porta"));
				reqPar.setNome(risultato.getString("nome"));
				reqPar.setPattern(risultato.getString("pattern"));
				reqPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				reqPar.setRequired(isrequired);

				

				lista.add(reqPar);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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

	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "porteApplicativeMTOMResponseList";
		int idLista = Liste.PORTE_APPLICATIVE_MTOM_RESPONSE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<MtomProcessorFlowParameter> lista = new ArrayList<MtomProcessorFlowParameter>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addSelectField("id_porta");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("pattern");
				sqlQueryObject.addSelectField("content_type");
				sqlQueryObject.addSelectField("required");
				sqlQueryObject.addWhereCondition("id_porta = ?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			MtomProcessorFlowParameter resPar;
			while (risultato.next()) {
				
				resPar = new MtomProcessorFlowParameter();
				
				resPar.setId(risultato.getLong("id_porta"));
				resPar.setNome(risultato.getString("nome"));
				resPar.setPattern(risultato.getString("pattern"));
				resPar.setContentType(risultato.getString("content_type"));
				int required = risultato.getInt("required");
				boolean isrequired = false;
				if (required == CostantiDB.TRUE)
					isrequired = true;
				resPar.setRequired(isrequired);

				

				lista.add(resPar);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	public GenericProperties getGenericProperties(long idGenericProperties) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getGenericProperties");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[getGenericProperties] Exception accedendo al datasource :" + e.getMessage(),e);

			}
		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			GenericProperties genericProperties = null;
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTIES);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idGenericProperties);
			rs = stm.executeQuery();

			if(rs.next()){
				
				genericProperties = new GenericProperties();
				genericProperties.setNome(rs.getString("nome"));
				genericProperties.setDescrizione(rs.getString("descrizione"));
				genericProperties.setTipologia(rs.getString("tipologia"));
				genericProperties.setTipo(rs.getString("tipo"));
				
				long idP = rs.getLong("id");
				genericProperties.setId(idP);
				
				//prendo le proprieta
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.CONFIG_GENERIC_PROPERTY);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_props = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm2 = con.prepareStatement(sqlQuery);
				stm2.setLong(1, idP);
				rs2 = stm2.executeQuery();
				Property genericProperty = null;
				while(rs2.next())
				{
					genericProperty = new Property();
					//proprieta
					genericProperty.setId(rs2.getLong("id"));
					genericProperty.setNome(rs2.getString("nome"));
					genericProperty.setValore(rs2.getString("valore"));
					genericProperties.addProperty(genericProperty);
				}
				rs2.close();
				stm2.close();
				
			}
			rs.close();
			stm.close();

			if(genericProperties==null )
				throw new DriverConfigurazioneNotFound("Generic Properties non presenti");
			
			return genericProperties;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[getGenericProperties]  Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs2!=null) rs2.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm2!=null) stm2.close();
			}catch (Exception e) {
				//ignore
			}
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
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	public List<PortaApplicativa> getPorteApplicativeByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "getPorteApplicativeByPolicyGestioneToken";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaApplicativa(id));

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	public List<PortaDelegata> getPorteDelegateByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "getPorteDelegateByPolicyGestioneToken";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getPortaDelegata(id));

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	public boolean isPolicyNegoziazioneTokenUsedInConnettore(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "isPolicyNegoziazioneTokenUsedInConnettore";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			if (risultato.next()) {

				return true;

			}

			return false;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
	
	
	public Connettore getConnettore(long idConnettore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		String nomeMetodo = "getConnettore(id)";
		
		Connection con = null;
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			Connettore connettore = DriverConfigurazioneDB_LIB.getConnettore(idConnettore, con);
			if(connettore==null) {
				throw new DriverConfigurazioneNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
						
			// Recupero anche eventuale username e password in invocazione servizio.
			readCredenzialiBasicConnettore(connettore, idConnettore, con);
			
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (this.atomica) {
					this.log.debug("rilascio connessioni...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	public Connettore getConnettore(String nomeConnettore) throws DriverConfigurazioneException {
		String nomeMetodo = "getConnettore(nome)";
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		if (this.atomica) {
			try {
				con = getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome_connettore=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomeConnettore);
			risultato = stmt.executeQuery();

			Long idConnettore = null;
			if (risultato.next()) {
				idConnettore = risultato.getLong("id");
			}
			else {
				throw new DriverConfigurazioneNotFound("Connettore con nome '"+nomeConnettore+"' non esistente");
			}
					
			Connettore connettore = DriverConfigurazioneDB_LIB.getConnettore(idConnettore, con);
			if(connettore==null) {
				throw new DriverConfigurazioneNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
			
			risultato.close();
			stmt.close();
			
			// Recupero anche eventuale username e password in invocazione servizio.
			readCredenzialiBasicConnettore(connettore, idConnettore, con);
			
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
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
					this.log.debug("rilascio connessioni...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	private void readCredenzialiBasicConnettore(Connettore connettore, long idConnettore,
			Connection con) throws DriverConfigurazioneException {
		String nomeMetodo = "getConnettore(nome)";
		
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		try {
			
			// Recupero anche eventuale username e password in invocazione servizio.
			if(connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_USERNAME)==false) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("utenteinv");
				sqlQueryObject.addSelectField("passwordinv");
				sqlQueryObject.addWhereCondition("id_connettore_inv=?");
				String queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idConnettore);
				risultato = stmt.executeQuery();
				String user = null;
				String password = null;
				if (risultato.next()) {
					user = risultato.getString("utenteinv");
					password = risultato.getString("passwordinv");
				}
				else {
					// cerco come risposta asincrona
					risultato.close();
					stmt.close();
					
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("utenterisp");
					sqlQueryObject.addSelectField("passwordrisp");
					sqlQueryObject.addWhereCondition("id_connettore_risp=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idConnettore);
					risultato = stmt.executeQuery();
					if (risultato.next()) {
						user = risultato.getString("utenterisp");
						password = risultato.getString("passwordrisp");
					}
				}
				
				if(user!=null) {
					Property property = new Property();
					property.setNome(CostantiConnettori.CONNETTORE_USERNAME);
					property.setValore(user);
					connettore.addProperty(property);
				}
				if(password!=null) {
					Property property = new Property();
					property.setNome(CostantiConnettori.CONNETTORE_PASSWORD);
					property.setValore(password);
					connettore.addProperty(property);
				}

			}
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			try{
				if(risultato!=null) risultato.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	
	
	public boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {

		Connection connection;
		if (this.atomica) {
			try {
				connection = this.getConnectionFromDatasource("existsProtocolProperty");
				connection.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("DriverConfigurazioneDB::existsProtocolProperty] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			connection = this.globalConnection;

		this.log.debug("operazione atomica = " + this.atomica);
		try {
			return DBProtocolPropertiesUtils.existsProtocolProperty(proprietarioProtocolProperty, idProprietario, nome, connection, this.tipoDB);
		} catch (Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
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

	public ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		try {
			return DBProtocolPropertiesUtils.getProtocolPropertyConfig(proprietarioProtocolProperty, idProprietario, nome, con, this.tipoDB);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneException::" + nomeMetodo + "] Exception: " + se.getMessage());
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

	public ProtocolProperty getProtocolProperty(long idProtocolProperty) throws DriverConfigurazioneException {
		String nomeMetodo = "getProtocolProperty";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		try {

			return DriverConfigurazioneDB_LIB.getProtocolProperty(idProtocolProperty, con, this.tipoDB);

		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneException::" + nomeMetodo + "] Exception: " + se.getMessage());
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
	
	
	private void setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, FiltroRicercaServiziApplicativi filtroRicerca, String tabella) throws SQLQueryObjectException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(sqlQueryObject, filtroRicerca.getProtocolProperties(), tabella);
		}
	}
	private void _setProtocolPropertiesForSearch(ISQLQueryObject sqlQueryObject, List<FiltroRicercaProtocolProperty> list, String tabella) throws SQLQueryObjectException{
		if(list!=null && list.size()>0){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(sqlQueryObject, l, tabella);
		}
	}
	
	
	private void setProtocolPropertiesForSearch(PreparedStatement stmt, int index, FiltroRicercaServiziApplicativi filtroRicerca, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(filtroRicerca!=null){
			this._setProtocolPropertiesForSearch(stmt, index, filtroRicerca.getProtocolProperties(), proprietario);
		}
	}
	
	private void _setProtocolPropertiesForSearch(PreparedStatement stmt, int index, 
			List<FiltroRicercaProtocolProperty> list, ProprietariProtocolProperty proprietario) throws SQLQueryObjectException, SQLException, JDBCAdapterException, UtilsException{
		if(list!=null && list.size()>0){
			List<org.openspcoop2.core.mapping.FiltroRicercaProtocolProperty> l = new ArrayList<>();
			l.addAll(list);
			DBProtocolPropertiesUtils.setProtocolPropertiesForSearch(stmt, index, l, proprietario, this.tipoDB, this.log);
		}
	}
	
	public long getIdServizioApplicativoByConnettore(long idConnettore) throws DriverConfigurazioneException {
		String nomeMetodo = "getIdServizioApplicativoByConnettore";

		Connection con = null;

		if (this.atomica) {
			try {
				con = this.getConnectionFromDatasource("getProtocolProperty");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(), e);

			}

		} else {
			con = this.globalConnection;
		}

		PreparedStatement stmt=null;
		ResultSet risultato=null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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
	 * Ritorna la lista di proprieta per la configurazione custom dei connettori multipli di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeConnettoriMultipliConfigPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_PROPRIETA;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeConnettoriMultipliConfigPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeConnettoriMultipliConfigPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("valore");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPortaApplicativa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteApplicativeConnettoriMultipliConfigPropList] Errore : " + qe.getMessage(),qe);
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
	 * Ritorna la lista di proprieta di un connettore multiplo di una Porta Applicativa 
	 */
	public List<Proprieta> porteApplicativeConnettoriMultipliPropList(long idPaSa, ISearch ricerca) throws DriverConfigurazioneException {
		int offset;
		int limit;
		int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_PROPRIETA;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));		


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Proprieta> lista = new ArrayList<Proprieta>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteApplicativeAutorizzazioneCustomPropList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPaSa);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("id_porta");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("valore");
			sqlQueryObject.addWhereCondition("id_porta = ?");
			
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
			} 
			
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();
			
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPaSa);
			risultato = stmt.executeQuery();

			Proprieta prop = null;
			while (risultato.next()) {

				prop = new Proprieta();

				prop.setId(risultato.getLong("id"));
				prop.setNome(risultato.getString("nome"));
				prop.setValore(risultato.getString("valore"));

				lista.add(prop);
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::porteAppPropList] Errore : " + qe.getMessage(),qe);
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
}
