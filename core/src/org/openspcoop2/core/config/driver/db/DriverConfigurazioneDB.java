/*
 * OpenSPCoop - Customizable API Gateway 
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ProprietaProtocollo;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RouteGateway;
import org.openspcoop2.core.config.RouteRegistro;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDefault;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.StatoServiziPddIntegrationManager;
import org.openspcoop2.core.config.StatoServiziPddPortaApplicativa;
import org.openspcoop2.core.config.StatoServiziPddPortaDelegata;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataServizioIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettoErogatoreIdentificazione;
import org.openspcoop2.core.config.constants.RegistroTipo;
import org.openspcoop2.core.config.constants.RicercaTipologiaErogazione;
import org.openspcoop2.core.config.constants.RicercaTipologiaFruizione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneSearch;
import org.openspcoop2.core.config.driver.TipologiaServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
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
 * </ul>
 * 
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverConfigurazioneDB extends BeanUtilities
implements IDriverConfigurazioneGet, IDriverConfigurazioneCRUD, IDriverConfigurazioneSearch, IDriverWS, IMonitoraggioRisorsa {

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

	/** Logger utilizzato per debug. */
	protected org.slf4j.Logger log = null;

	// Tipo database passato al momento della creazione dell'oggetto
	private String tipoDB = null;
	public String getTipoDB() {
		return this.tipoDB;
	}

	/** Tabella soggetti */
	String tabellaSoggetti = CostantiDB.SOGGETTI;


	
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
			this.log.error("Impossibile recuperare il context: " + ne.getMessage());
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
	 * Restituisce Il soggetto che include la porta delegata identificata da
	 * <var>location</var>
	 * 
	 * @param location
	 *                Location che identifica una porta delegata
	 * @return Il Soggetto che include la porta delegata fornita come
	 *         parametro.
	 * 
	 */
	@Override
	public Soggetto getSoggetto(String location) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (location == null)
			throw new DriverConfigurazioneException("[getSoggetto] Parametro Non Valido");

		Soggetto Soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getSoggetto(location)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("location = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, location);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, location));
			rs = stm.executeQuery();

			// se nn ho risultati allora devo ricercare per nome e filtrare su
			// location
			if (rs.next()) {
				// la location era settata
				Soggetto = new Soggetto();

				long idSoggProprietario = rs.getLong("id_soggetto");

				Soggetto = this.getSoggetto(idSoggProprietario,con);

			} else {
				rs.close();
				stm.close();
				boolean trovato = false;
				// ricerco per nome porta e restituisco il primo record che ha
				// come nome_porta la location
				// e come location null
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_porta = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setString(1, location);
				rs=stm.executeQuery();
				while (rs.next() && trovato == false) {
					// se location e' null allora e' questo il mio record
					if (rs.getString("location") == null || "".equals(rs.getString("location")) ) {
						trovato = true;

						long idSoggProprietario = rs.getLong("id_soggetto");
						if(idSoggProprietario>0){
							Soggetto=this.getSoggetto(idSoggProprietario,con);
							trovato = true;
						}
					}
				}
			}

			if (Soggetto == null)
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getSoggetto] Soggetto associato alla porta delegata ["+location+"] non esistente.");

			return Soggetto;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneException se) {
			throw se;
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception: " + se.getMessage(),se);
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
			Vector<IDSoggetto> idTrovati = new Vector<IDSoggetto>();
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
	public HashSet<String> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		HashSet<String> soggettiVirtuali = new HashSet<String>();
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
				String soggettoVirtuale = rs.getString("tipo_soggetto_virtuale") + rs.getString("nome_soggetto_virtuale");
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

	/**
	 * Restituisce la porta delegata identificato da <var>idPD</var>
	 * 
	 * @param idPD
	 *                Identificatore di una Porta Delegata
	 * @return La porta delegata.
	 * 
	 */
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idPD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametro idPD Non Valido");

		IDSoggetto aSoggetto = idPD.getSoggettoFruitore();
		String location = idPD.getLocationPD();

		if ((location == null) || (aSoggetto == null)){
			String a1 = "";
			String a2 = "";
			if(location==null){
				a1=" [Location is null]";
			}else{
				a1=" ["+location+"]";
			}
			if(aSoggetto==null){
				a2=" [soggetto is null]";
			}else{
				a2=" [soggetto tipo("+aSoggetto.getTipo()+") nome("+aSoggetto.getNome()+")]";
			}
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] Parametri non Validi"+a1+a2);
		}

		// Devi cercare una porta delegata che appartiene al soggetto fruitore
		// (aSoggetto occhio al tipo+nome)
		// e che soddisfa la location
		// Nota: la location puo' essere definita come attributo a se stante
		// (url di invocazione), oppure puo' essere assunta nel nome della porta
		// delegata
		// Quindi per ogni PD devi guardare se e' definita una url di
		// invocazione, se lo e' quella e' la location da controllare,
		// altrimenti e' il nome
		//
		// 1. deve ritornare le proprieta' della PD.
		// 2. Le proprieta' di Message-Security.
		// 3. Solo i NOMI dei servizi applicativi associati alla PD.
		/*
		 * PortaDelegata pd = new PortaDelegata();
		 * pd.setAutenticazione(autenticazione);
		 * pd.setAutorizzazione(autorizzazione); pd.setDescrizione(descrizione);
		 * pd.setLocation(location); // e' l'url di invocazione.
		 * pd.setNome(nome);
		 * pd.setSoggettoErogatore(SoggettoErogatore);
		 * pd.setServizio(servizio); pd.setAzione(azione);
		 * pd.setMessageSecurity(messageSecurity); ....
		 * pd.addServizioApplicativo(servizioApplicativo); // servizi
		 * applicativi associati alla Porta Delegata: NOTA BAsta impostare il
		 * nome del servizio applicativo!!
		 */
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery;
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
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_soggetto=?");
			sqlQueryObject.addWhereCondition("tipo_soggetto=?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			String nomeSoggFruit = aSoggetto.getNome();
			String tipoSoggFruit = aSoggetto.getTipo();
			long idSoggettoFruitore = 0;
			if (nomeSoggFruit == null || nomeSoggFruit.trim().equals("") || tipoSoggFruit == null || tipoSoggFruit.trim().equals(""))
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPortaDelegata] nome o tipo Soggetto fruitore non settati correttamente");

			stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stm.setString(1, nomeSoggFruit);
			stm.setString(2, tipoSoggFruit);
			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nomeSoggFruit, tipoSoggFruit));

			rs = stm.executeQuery();

			if (rs.next()) {
				// il soggetto fruitore e' il proprietario della porta delegeata
				// che sto cercando
				idSoggettoFruitore = rs.getLong("id");

			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getPortaDelegata] Non ho trovato nessuno soggetto fruitore [" + nomeSoggFruit + "," + tipoSoggFruit + "] non esistente");
			}
			rs.close();
			stm.close();
			// cerco porta delegata del soggetto fruitore che soddisfa la
			// location
			// la location se nn settata va controllata nel nome_porta
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			//sqlQueryObject.addSelectField("*");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_DELEGATE+".id", "idPD");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+this.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".location = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stm.setLong(1, idSoggettoFruitore);
			stm.setString(2, location);

			rs = stm.executeQuery();

			if (rs.next()) {// ho trovato la porta delegata che cercavo
				idPortaDelegata = rs.getLong("idPD");
				trovato = true;
				rs.close();
				stm.close();
			} else {// in questo caso devo cercare il parametro location sul
				// nome_porta

				// ricerco per nome porta e restituisco il primo record che ha
				// come nome_porta la location
				// e come location null
				rs.close();
				stm.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				stm.setString(1, location);
				stm.setLong(2, idSoggettoFruitore);
				rs=stm.executeQuery();
				while (rs.next() && trovato == false) {
					idPortaDelegata = rs.getLong("id");
					trovato = true;
				}
				rs.close();
				stm.close();
			}
			
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

		if (trovato) {
			pd=this.getPortaDelegata(idPortaDelegata);

		} else {
			throw new DriverConfigurazioneNotFound("Nessuna PortaDelegata trovata.");
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

	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, viene ricercata
	 * una Porta Applicativa che non possegga l'azione
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return getPortaApplicativa(idPA, false,null);
	}

	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return getPortaApplicativa(idPA, ricercaPuntuale,null);
	}

	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * @param idPA
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return getPortaApplicativa(idPA, false,soggettoVirtuale);
	}

	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale,boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return getPortaApplicativa(idPA, ricercaPuntuale,soggettoVirtuale);
	}

	private PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale, IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		if (idPA == null || idPA.getIDServizio()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idPA Non Validi");

		IDSoggetto soggettoErogatore = idPA.getIDServizio().getSoggettoErogatore();
		IDServizio service = idPA.getIDServizio();
		if ((soggettoErogatore == null) || (service == null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri Non Validi");
		String servizio = service.getServizio();
		String tipoServizio = service.getTipoServizio();
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

		// 1. deve ritornare le proprieta' della PA.
		// 2. Le proprieta' di Message-Security.
		// 3. Le proprieta' di Properties.
		// 4. Solo i NOMI dei servizi applicativi associati alla PA.
		// 5. Il soggetto virtuale
		/*
		 * PortaApplicativa pa = new PortaApplicativa();
		 * pa.setDescrizione(descrizione); pa.setNome(nome);
		 * pa.setServizio(servizio); pa.setAzione(azione);
		 * pa.setSoggettoVirtuale(soggettoVirtuale);
		 * pa.setMessageSecurity(messageSecurity);
		 * pa.addServizioApplicativo(servizioApplicativo); .... solo i NOMI!!!
		 * pa.addSetProperty(setProperty); .....
		 */

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

		long idPortaApplicativa = -1;
		boolean trovato = false;
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
					sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					stm.setLong(1, idSoggErog);
					stm.setString(2, tipoServizio);
					stm.setString(3, servizio);
					stm.setString(4, "");
					stm.setString(5, "-");
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio,"","-"));
				}else{
					stm.setLong(1, idSoggErog);
					stm.setLong(2, idSoggVirtuale);
					stm.setString(3, soggettoVirtuale.getTipo());
					stm.setString(4, soggettoVirtuale.getNome());
					stm.setString(5, tipoServizio);
					stm.setString(6, servizio);
					stm.setString(7, "");
					stm.setString(8, "-");
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, soggettoErogatore.getTipo(),soggettoErogatore.getNome(),tipoServizio, servizio,"","-"));
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
					sqlQueryObject.addWhereCondition("azione = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
				}

				stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

				if(soggettoVirtuale==null){
					stm.setLong(1, idSoggErog);
					stm.setString(2, tipoServizio);
					stm.setString(3, servizio);
					stm.setString(4, azione);
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, azione));
				}else
				{
					stm.setLong(1, idSoggErog);
					stm.setLong(2, idSoggVirtuale);
					stm.setString(3, soggettoVirtuale.getTipo());
					stm.setString(4, soggettoVirtuale.getNome());
					stm.setString(5, tipoServizio);
					stm.setString(6, servizio);
					stm.setString(7, azione);
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio, azione));
				}
			}


			rs = stm.executeQuery();

			trovato = rs.next();
			this.log.debug("ricerca puntuale="+ricercaPuntuale);
			if(!ricercaPuntuale){
				if (!trovato && azione != null) {
					this.log.debug("ricerca PA con azione != null ma con solo il servizio, soggettoVirtuale="+soggettoVirtuale);

					rs.close();
					stm.close();				
					// in questo caso provo a cercaredelle porte applicative solo
					// con id_soggetto tipo_servizio e servizio
					if(soggettoVirtuale==null) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
						sqlQueryObject.addSelectField("*");
						sqlQueryObject.addWhereCondition("id_soggetto = ?");
						sqlQueryObject.addWhereCondition("tipo_servizio = ?");
						sqlQueryObject.addWhereCondition("servizio = ?");
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
						sqlQueryObject.addWhereCondition(false, "azione IS NULL", "azione = ?", "azione = ?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
					}

					stm = con.prepareStatement(sqlQuery,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					if(soggettoVirtuale==null){
						stm.setLong(1, idSoggErog);
						stm.setString(2, tipoServizio);
						stm.setString(3, servizio);
						stm.setString(4, "");
						stm.setString(5, "-");
					}else{
						stm.setLong(1, idSoggErog);
						stm.setLong(2, idSoggVirtuale);
						stm.setString(3, soggettoVirtuale.getTipo());
						stm.setString(4, soggettoVirtuale.getNome());
						stm.setString(5, tipoServizio);
						stm.setString(6, servizio);
						stm.setString(7, "");
						stm.setString(8, "-");
					}


					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, idSoggErog, tipoServizio, servizio));


					rs = stm.executeQuery();

					trovato = rs.next();
				}
			}

			if (trovato) {
				// ripristino il cursore del rs sulla prima riga
				rs.first();
				idPortaApplicativa = rs.getLong("id");
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

		PortaApplicativa pa = null;
		if (trovato) {
			pa = this.getPortaApplicativa(idPortaApplicativa);
		} else {
			throw new DriverConfigurazioneNotFound("Porta Applicativa non esistente");
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
	
	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa)
	 * che possiedono il soggetto SoggettoVirtuale identificato da <var>idPA</var>
	 * 
	 * @param idPA
	 *                Identificatore di una Porta Applicativa con soggetto
	 *                Virtuale
	 * @return una porta applicativa
	 * 
	 */
	@Override
	public Hashtable<IDSoggetto, PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		this.log.debug("metodo getPorteApplicative_SoggettiVirtuali in esecuzione...");

		if (idPA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idPA Non Valido");
		if (idPA.getIDServizio() == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idServizio Non Valido");
		if (idPA.getIDServizio().getSoggettoErogatore() == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro Soggetto Erogatore Non Valido");
		Hashtable<IDSoggetto, PortaApplicativa> paConSoggetti = new Hashtable<IDSoggetto, PortaApplicativa>();
		IDSoggetto soggettoVirtuale = idPA.getIDServizio().getSoggettoErogatore();
		String servizio = idPA.getIDServizio().getServizio();
		String tipoServizio = idPA.getIDServizio().getTipoServizio();
		String azione = idPA.getIDServizio().getAzione();
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
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

					stm.setString(1, nomeSoggVirt);
					stm.setString(2, tipoSoggVirt);
					stm.setString(3, tipoServizio);
					stm.setString(4, servizio);
					stm.setString(5, "");
					stm.setString(6, "-");
					stm.setLong(7, idSoggetto);

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
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					stm.setString(1, nomeSoggVirt);
					stm.setString(2, tipoSoggVirt);
					stm.setString(3, tipoServizio);
					stm.setString(4, servizio);
					stm.setString(5, azione);
					stm.setLong(6, idSoggetto);

					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio, azione));
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
					sqlQueryObject.addWhereCondition(false, CostantiDB.PORTE_APPLICATIVE+".azione IS NULL", CostantiDB.PORTE_APPLICATIVE+".azione = ?", CostantiDB.PORTE_APPLICATIVE+".azione = ?");
					sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".id = ?");
					sqlQueryObject.setANDLogicOperator(true);
					sqlQuery = sqlQueryObject.createSQLQuery();
					this.log.debug("eseguo query " + DBUtils.formatSQLString(sqlQuery, nomeSoggVirt, tipoSoggVirt, tipoServizio, servizio));
					this.log.debug("QUERY RAW: "+sqlQuery);

					stm = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					stm.setString(1, nomeSoggVirt);
					stm.setString(2, tipoSoggVirt);
					stm.setString(3, tipoServizio);
					stm.setString(4, servizio);
					stm.setString(5, "");
					stm.setString(6, "-");
					stm.setLong(7, idSoggetto);



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

	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta
	 * delegata <var>location</var>. Se nella porta delegata non vi e' viene
	 * cercato poi in un specifico soggetto se specificato con <var>aSoggetto</var>,
	 * altrimenti in ogni soggetto.
	 * 
	 * @param idPD
	 *                Identificatore della porta delegata.
	 * @param servizioApplicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativo(IDPortaDelegata idPD, String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		return getServizioApplicativo(1, idPD, null, servizioApplicativo, null, null, null, 0);

	}

	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta
	 * applicativa <var>location</var> e poi nel soggetto <var>aSoggetto</var>.
	 * 
	 * @param idPA
	 *                Identificatore della porta applicativa.
	 * @param servizioApplicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativo(IDPortaApplicativa idPA, String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		return getServizioApplicativo(4, null, idPA, servizioApplicativo, null, null, null, 0);
	}

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate
	 * come parametro.
	 * 
	 * @param idPD
	 *                Identificatore della porta delegata.
	 * @param aUser
	 *                User utilizzato nell'header HTTP Authentication.
	 * @param aPassword
	 *                Password utilizzato nell'header HTTP Authentication.
	 * @return Il servizio applicativo che include le credenziali passate come
	 *         parametro.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aUser, String aPassword) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		return getServizioApplicativo(2, idPD, null, null, aUser, aPassword, null, 0);
	}

	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(String aUser,String aPassword)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativo(22, null, null, null, aUser, aPassword, null, 0); // tipo 22 sta ad indicare che e' un particolare caso del 2
	}

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate
	 * come parametro.
	 * 
	 * @param idPD
	 *                Identificatore della porta delegata.
	 * @param aSubject
	 *                Subject utilizzato nella connessione HTTPS.
	 * @return Il servizio applicativo che include le credenziali passate come
	 *         parametro.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getServizioApplicativo(3, idPD, null, null, null, null, aSubject, 0);
	}

	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getServizioApplicativo(33, null, null, null, null, null, aSubject, 0); // tipo 33 sta ad indicare che e' un particolare caso del 3
	}

	/**
	 * Verifica l'esistenza di un servizio applicativo.
	 *
	 * @param idServizioApplicativo id del servizio applicativo
	 * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
	 */    
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setSoggettoFruitore(idServizioApplicativo.getIdSoggettoProprietario());
		return getServizioApplicativo(1, idPD, null, idServizioApplicativo.getNome(), null, null, null, 0);
	}


	/**
	 * Verifica l'esistenza di un servizio applicativo.
	 *
	 * @param idSoggetto id del soggetto proprietario
	 * @param nomeServizioApplicativo nome del servizio applicativo
	 * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
	 */    
	@Override
	public ServizioApplicativo getServizioApplicativo(IDSoggetto idSoggetto,String nomeServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(idSoggetto);
		idServizioApplicativo.setNome(nomeServizioApplicativo);
		return this.getServizioApplicativo(idServizioApplicativo);
	}

	private ServizioApplicativo getServizioApplicativo(int type, IDPortaDelegata idPD, IDPortaApplicativa idPA, String servizioApplicativo, String aUser, String aPassord, String aSubject, long idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		// 1: getServizioApplicativo(RichiestaDelegata idPD)
		// 2: getServizioApplicativo(RichiestaDelegata idPD, String aUser,String
		// aPassword)
		// 3: getServizioApplicativo(RichiestaDelegata idPD, String aSubject)
		// 4: getServizioApplicativo(RichiestaApplicativa idPA)
		// 5: getServizioApplicativo(IDServizioApplicativo idServizioApplicativo)
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String nome_sa = null;
		String sqlQuery = null;
		ServizioApplicativo sa = null;
		long idSoggettoFruitore = -1;
		IDSoggetto idSF = null;

		switch (type) {
		case 1:
			//controllo soggetto fruitore
			if (idPD == null)
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD non valido.");

			IDSoggetto idSO = idPD.getSoggettoFruitore();
			if(idSO==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Soggetto Fruitore non Impostato.");
			if(idSO.getNome()==null || "".equals(idSO.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Fruitore non Impostato.");
			if(idSO.getTipo()==null || "".equals(idSO.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Fruitore non Impostato.");

			nome_sa = servizioApplicativo;
			break;

		case 2:
		case 22:
			if(type == 2){
				if (idPD == null)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD non valido.");
				if (idPD.getSoggettoFruitore() == null)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD.getSoggettoFruitore() non valido.");
				idSF = idPD.getSoggettoFruitore();
				if ( (idSF.getNome() == null) || (idSF.getTipo() == null) )
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD.getSoggettoFruitore(valori) non valido.");
			}

			if (aUser == null || aUser.trim().equals("") || aPassord == null || aPassord.trim().equals(""))
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametri di autenticazione user["+aUser+"] password["+aPassord+"] non validi.");
			nome_sa = servizioApplicativo;
			break;

		case 3:
		case 33:
			if(type == 3){
				if (idPD == null)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD non valido.");
				if (idPD.getSoggettoFruitore() == null)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD.getSoggettoFruitore() non valido.");
				idSF = idPD.getSoggettoFruitore();
				if ( (idSF.getNome() == null) || (idSF.getTipo() == null) )
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPD.getSoggettoFruitore(valori) non valido.");
			}

			if (aSubject == null || aSubject.trim().equals(""))
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametri di autenticazione subject["+aSubject+"] non validi.");

			nome_sa = servizioApplicativo;
			break;

		case 4:
			//controllo soggetto
			if (idPA == null)
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametro idPA non valido.");

			IDServizio idSE=idPA.getIDServizio();
			if(idSE==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] IDServizio non impostato.");

			idSO = idSE.getSoggettoErogatore();
			if(idSO==null)throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Soggetto Erogatore non Impostato.");
			if(idSO.getNome()==null || "".equals(idSO.getNome()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Erogatore non Impostato.");
			if(idSO.getTipo()==null || "".equals(idSO.getTipo()))throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Nome Soggetto Erogatore non Impostato.");

			nome_sa = servizioApplicativo;
			break;

		case 5:
			if (idServizioApplicativo <= 0)
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] L'id del Servizio applicativo deve essere > 0.");
			break;
		default:
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServizioApplicativo] Parametri non validi.");
		}

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getServizioApplicativo");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			switch (type) {
			case 1:
				String tipoSog=idPD.getSoggettoFruitore().getTipo();
				String nomeSog=idPD.getSoggettoFruitore().getNome();

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

			case 2:
			case 22:

				// id soggetto fruitore
				if(type==2){
					idSoggettoFruitore = DBUtils.getIdSoggetto(idSF.getNome(), idSF.getTipo(), con, this.tipoDB,this.tabellaSoggetti);
				}

				//cerco un servizio applicativo che contenga utente e password con autenticazione basi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");
				sqlQueryObject.addWhereCondition("utente = ?");
				sqlQueryObject.addWhereCondition("password = ?");
				if(type==2){
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				//stm.setString(1, nome_sa);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_BASIC.toString());
				stm.setString(2, aUser);
				stm.setString(3, aPassord);
				if(type==2){
					stm.setLong(4, idSoggettoFruitore);
				}

				this.log.debug("eseguo query :" + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_BASIC.toString(), aUser, aPassord));

				break;
			case 3:
			case 33:

				// id soggetto fruitore
				if(type==3){
					idSoggettoFruitore = DBUtils.getIdSoggetto(idSF.getNome(), idSF.getTipo(), con, this.tipoDB,this.tabellaSoggetti);
				}

				// Autenticazione SSL deve essere LIKE
				Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(aSubject);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("tipoauth = ?");

				Enumeration<String> keys = hashSubject.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					String value = hashSubject.get(key);
					sqlQueryObject.addWhereLikeCondition("subject", "/"+Utilities.formatKeySubject(key)+"="+Utilities.formatValueSubject(value)+"/", true, false);
				}

				if(type==3){
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();

				//System.out.println("QUERY["+sqlQuery+"]["+type+"]["+idSoggettoFruitore+"]");

				stm = con.prepareStatement(sqlQuery);
				//stm.setString(1, nome_sa);
				stm.setString(1, CostantiConfigurazione.CREDENZIALE_SSL.toString());
				if(type==3){
					stm.setLong(2, idSoggettoFruitore);
				}

				if(type==3){
					this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString(), idSoggettoFruitore));
				}else{
					this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.CREDENZIALE_SSL.toString()));
				}

				rs = stm.executeQuery();
				long idSA = -1;
				while(rs.next()){
					// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
					String subjectPotenziale =  rs.getString("subject");
					if(Utilities.sslVerify(subjectPotenziale, aSubject)){
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
			case 4:
				tipoSog=idPA.getIDServizio().getSoggettoErogatore().getTipo();
				nomeSog=idPA.getIDServizio().getSoggettoErogatore().getNome();

				idSog=DBUtils.getIdSoggetto(nomeSog, tipoSog, con, this.tipoDB,this.tabellaSoggetti);
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
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

			case 5:
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id = ?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idServizioApplicativo);

				this.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, idServizioApplicativo));
				break;

			}

			rs = stm.executeQuery();

			if (rs.next()) {
				sa = new ServizioApplicativo();

				sa.setId(rs.getLong("id"));
				sa.setNome(rs.getString("nome"));
				sa.setIdSoggetto(rs.getLong("id_soggetto"));
				//tipo e nome soggetto
				//tipo e nome soggetto sono necessari per la propagazione...
				sa.setTipoSoggettoProprietario(getSoggetto(sa.getIdSoggetto(),con).getTipo());
				sa.setNomeSoggettoProprietario(getSoggetto(sa.getIdSoggetto(),con).getNome());
				// descrizione
				sa.setDescrizione(rs.getString("descrizione"));

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
					credenziali.setPassword(rs.getString("password"));
					credenziali.setSubject(rs.getString("subject"));
					credenziali.setTipo(DriverConfigurazioneDB_LIB.getEnumCredenzialeTipo(tipoAuth));
					credenziali.setUser(rs.getString("utente"));
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
					Credenziali credenzialiRispA = new Credenziali();
					credenzialiRispA.setPassword(rs.getString("passwordrisp"));
					credenzialiRispA.setSubject(rs.getString("subjectrisp"));
					credenzialiRispA.setTipo(DriverConfigurazioneDB_LIB.getEnumCredenzialeTipo(rs.getString("tipoauthrisp")));
					credenzialiRispA.setUser(rs.getString("utenterisp"));
					//se il tipo di autenticazione non e' settato allora nn esistono credenziali
					rispAsinc.setCredenziali(credenzialiRispA.getTipo() != null ? credenzialiRispA : null);

					rispAsinc.setAutenticazione(DriverConfigurazioneDB_LIB.getEnumInvocazioneServizioTipoAutenticazione(rs.getString("tipoauthrisp")));
					rispAsinc.setInvioPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("invio_x_rif_risp")));
					rispAsinc.setRispostaPerRiferimento(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("risposta_x_rif_risp")));

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
					Credenziali credInvServ = new Credenziali();
					credInvServ.setPassword(rs.getString("passwordinv"));
					credInvServ.setSubject(rs.getString("subjectinv"));
					credInvServ.setTipo(DriverConfigurazioneDB_LIB.getEnumCredenzialeTipo(rs.getString("tipoauthinv")));
					credInvServ.setUser(rs.getString("utenteinv"));
					invServizio.setCredenziali(credInvServ.getTipo() != null ? credInvServ : null);
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage());
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createServiziAttiviPdD] Exception accedendo al datasource :" + e.getMessage(),e);

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
				String tracc_dump_applicativo = rs.getString("tracciamento_dump");
				String tracc_dump_pd = rs.getString("tracciamento_dump_bin_pd");
				String tracc_dump_pa = rs.getString("tracciamento_dump_bin_pa");
				Tracciamento tracciamento = new Tracciamento();
				tracciamento.setBuste(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tracc_buste));
				tracciamento.setDump(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tracc_dump_applicativo));
				tracciamento.setDumpBinarioPortaDelegata(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tracc_dump_pd));
				if(tracciamento.getDumpBinarioPortaDelegata()==null){
					tracciamento.setDumpBinarioPortaDelegata(StatoFunzionalita.DISABILITATO); // default
				}
				tracciamento.setDumpBinarioPortaApplicativa(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(tracc_dump_pa));
				if(tracciamento.getDumpBinarioPortaApplicativa()==null){
					tracciamento.setDumpBinarioPortaApplicativa(StatoFunzionalita.DISABILITATO); // default
				}
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

		return config;
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
	 * descrizione e se il ws_security e' abilitato allora crea un oggetto
	 * MessageSecurity vuoto altrimenti null.
	 */
	@Override
	public List<PortaApplicativa> porteAppList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
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
			stm.setInt(1, idSoggetto);
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
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("ws_security");
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
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("ws_security");
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
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta",search,true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stm = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
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
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stm = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
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
	@Override
	public List<ProprietaProtocollo> porteAppPropList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
		ArrayList<ProprietaProtocollo> lista = new ArrayList<ProprietaProtocollo>();

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
			stmt.setInt(1, idPortaApplicativa);
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

			ProprietaProtocollo prop = null;
			while (risultato.next()) {

				prop = new ProprietaProtocollo();

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

	@Override
	public List<ServizioApplicativo> porteAppServizioApplicativoList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			stmt.setInt(1, idPortaApplicativa);
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

	@Override
	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			stmt.setInt(1, idPortaApplicativa);
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

	@Override
	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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

	@Override
	public List<PortaDelegata> porteDelegateList(int idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
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
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setInt(1, idSoggetto);
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
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("autenticazione");
				sqlQueryObject.addSelectField("autorizzazione");
				sqlQueryObject.addSelectField("ws_security");
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
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("autenticazione");
				sqlQueryObject.addSelectField("autorizzazione");
				sqlQueryObject.addSelectField("ws_security");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
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
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
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
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".id");
				sqlQueryObject.addSelectField("nome_porta");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
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

	public List<PortaDelegata> porteDelegateWithLocationList(String location) throws DriverConfigurazioneException {
		String nomeMetodo = "porteDelegateWithLocationList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("porteDelegateWithLocationList");
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
			sqlQueryObject.addWhereCondition("location = ?");
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, location);
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

	public List<PortaApplicativa> porteAppWithTipoNomeServizio(long idSoggettoErogatore, String tipoServizio,String nomeServizio) throws DriverConfigurazioneException {
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
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, tipoServizio);
			stmt.setString(2, nomeServizio);
			stmt.setLong(3, idSoggettoErogatore);
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

	@Override
	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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

	@Override
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
			stmt.setInt(1, idPortaDelegata);
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

	@Override
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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

	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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

	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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


	public List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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



	public List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
	 * Restituisce la lista delle porte applicative con il nome fornito da parametro.
	 * Possono esistere piu' porte applicative con medesimo nome, che appartengono a soggetti differenti.
	 * Se indicati i parametri sui soggetti vengono utilizzati come filtro per localizzare in maniera piu' precisa la PA
	 * 
	 * @param nomePA Nome di una Porta Applicativa
	 * @param tipoSoggettoProprietario Tipo del Soggetto Proprietario di una Porta Applicativa
	 * @param nomeSoggettoProprietario Nome del Soggetto Proprietario di una Porta Applicativa
	 * @return La lista di porte applicative
	 * 
	 */
	@Override
	public List<PortaApplicativa> getPorteApplicative(
			String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(nomePA==null)
			throw new DriverConfigurazioneException("[getPorteApplicative] Parametro nomePA Non Valido");

		List<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		String nomeMetodo = "getPorteApplicative";
		boolean error = false;

		if (this.atomica) {
			try {
				con = (Connection) getConnectionFromDatasource("getPorteApplicative");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			boolean tipoSoggettoDefined = tipoSoggettoProprietario!=null && !"".equals(tipoSoggettoProprietario);
			boolean nomeSoggettoDefined = nomeSoggettoProprietario!=null && !"".equals(nomeSoggettoProprietario);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE,"pa");
			if(tipoSoggettoDefined || nomeSoggettoDefined)
				sqlQueryObject.addFromTable(this.tabellaSoggetti,"s");
			sqlQueryObject.addSelectAliasField("pa","id","idPortaApplicativa");
			if(tipoSoggettoDefined || nomeSoggettoDefined)
				sqlQueryObject.addWhereCondition("pa.id_soggetto = s.id");
			sqlQueryObject.addWhereCondition("pa.nome_porta = ?");
			if(tipoSoggettoDefined){
				sqlQueryObject.addWhereCondition("s.tipo_soggetto = ?");
			}
			if(nomeSoggettoDefined){
				sqlQueryObject.addWhereCondition("s.nome_soggetto = ?");
			}
			String queryString = sqlQueryObject.createSQLQuery();
			this.log.debug("Query ["+queryString+"] NOMEPA["+nomePA+"] TIPOSOG["+tipoSoggettoProprietario+"] NOMESOG["+nomeSoggettoProprietario+"]");
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, nomePA);
			if(tipoSoggettoDefined){
				stmt.setString(index++, tipoSoggettoProprietario);
			}
			if(nomeSoggettoDefined){
				stmt.setString(index++, nomeSoggettoProprietario);
			}
			risultato = stmt.executeQuery();

			//long idServizioApplicativo = 0;
			List<Long> listaId = new ArrayList<Long>();
			while (risultato.next()) {
				listaId.add(risultato.getLong("idPortaApplicativa"));
			}
			risultato.close();
			stmt.close();

			for (int i = 0; i < listaId.size(); i++) {
				lista.add(this.getPortaApplicativa(listaId.get(i), con));
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

		if(lista.size() == 0)
			throw new DriverConfigurazioneNotFound("[getPorteApplicative] Porte applicative non esistenti.");

		return lista;
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
	@Override
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

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiList(String utente, String password) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("servizioApplicativoWithCredenzialiList");
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
			sqlQueryObject.addWhereCondition("utente = ?");
			sqlQueryObject.addWhereCondition("password = ?");
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, utente);
			stmt.setString(2, password);			
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

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiList(String subject) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoWithCredenzialiList";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("servizioApplicativoWithCredenzialiList(subject)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(subject);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("subject");
			sqlQueryObject.addSelectField("id_soggetto");
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

			ServizioApplicativo sa;
			while (risultato.next()) {

				// Possono esistere piu' sil che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
				String subjectPotenziale =  risultato.getString("subject");
				if(Utilities.sslVerify(subjectPotenziale, subject)){
					sa=this.getServizioApplicativo(risultato.getLong("id"));
					lista.add(sa);
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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

	@Override
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
				if (superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if (superuser!=null && !superuser.equals(""))
					sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && !superuser.equals(""))
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
				if (superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);
				if (superuser!=null && !superuser.equals(""))
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
				sqlQueryObject.addFromTable(this.tabellaSoggetti);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("descrizione");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("is_router");
				if (superuser!=null && !superuser.equals(""))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && !superuser.equals(""))
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
			if(superuser!=null && (!"".equals(superuser))){
				sqlQueryObject.addWhereCondition(this.tabellaSoggetti+".superuser=?");
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser))){
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
			if(superuser!=null && (!"".equals(superuser))){
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
			if(superuser!=null && (!"".equals(superuser))){
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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

	public List<ServizioApplicativo> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		this.log.debug("operazione this.atomica = " + this.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
			if(superuser!=null && (!"".equals(superuser)))
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
			if(credenziale!=null)
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			int index = 1;
			stmt.setString(index++, idSoggetto.getTipo());
			stmt.setString(index++, idSoggetto.getNome());
			if(superuser!=null && (!"".equals(superuser)))
				stmt.setString(index++, superuser);
			if(credenziale!=null)
				stmt.setString(index++, credenziale.getValue());
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (superuser!=null && !superuser.equals(""))
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
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
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(superuser!=null && (!"".equals(superuser)))
				stmt.setString(1, superuser);
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (idSoggetto!=null)
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
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
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if (idSoggetto!=null)
				stmt.setLong(1, idSoggetto);
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
	public HashSet<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		HashSet<IDServizio> servizi = new HashSet<IDServizio>();
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
			sqlQueryObject.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition("id_soggetto_virtuale<>-1");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_virtuale is not null", "nome_soggetto_virtuale is not null");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			while (rs.next()) {
				IDServizio servizio = new IDServizio();
				servizio.setSoggettoErogatore(new IDSoggetto(rs.getString("tipo_soggetto_virtuale"), rs.getString("nome_soggetto_virtuale")));
				servizio.setServizio(rs.getString("servizio"));
				servizio.setTipoServizio(rs.getString("tipo_servizio"));
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

				int router = rs.getInt("is_router");
				boolean isrouter = false;
				if (router == CostantiDB.TRUE)
					isrouter = true;
				Soggetto.setRouter(isrouter);

				tmp = rs.getString("pd_url_prefix_rewriter");
				Soggetto.setPdUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("pa_url_prefix_rewriter");
				Soggetto.setPaUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				// Aggiungo i servizi applicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm1 = con.prepareStatement(sqlQuery);
				stm1.setLong(1, rs.getLong("id"));
				rs1 = stm1.executeQuery();

				ServizioApplicativo servizioApplicativo = null;
				while (rs1.next()) {
					// setto solo il nome come da specifica
					servizioApplicativo = new ServizioApplicativo();
					servizioApplicativo.setId(rs1.getLong("id"));
					servizioApplicativo.setNome(rs1.getString("nome"));
					Soggetto.addServizioApplicativo(servizioApplicativo);
				}
				rs1.close();
				stm1.close();

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
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectField("azione");
			sqlQueryObject.addSelectField("validazione_contenuti_stato");
			sqlQueryObject.addSelectField("validazione_contenuti_tipo");
			sqlQueryObject.addSelectField("validazione_contenuti_mtom");
			sqlQueryObject.addSelectField("mtom_request_mode");
			sqlQueryObject.addSelectField("mtom_response_mode");
			sqlQueryObject.addSelectField("ws_security");
			sqlQueryObject.addSelectField("ws_security_mtom_req");
			sqlQueryObject.addSelectField("ws_security_mtom_res");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".id", "idPA");
			sqlQueryObject.addSelectAliasField(this.tabellaSoggetti+".id", "idSoggetto");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_APPLICATIVE+".descrizione", "descrizionePorta");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("behaviour");
			sqlQueryObject.addSelectField("autorizzazione_contenuto");
			sqlQueryObject.addSelectField("id_accordo");
			sqlQueryObject.addSelectField("id_port_type");
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
				pa.setIdSoggetto(rs.getLong("idSoggetto"));
				pa.setDescrizione(rs.getString("descrizionePorta"));
				pa.setNome(rs.getString("nome_porta"));
				pa.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pa.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

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
					idServizioPA = DBUtils.getIdServizio(nomeServizio, tipoServizioPA, nomeProprietarioServizio, tipoProprietarioServizio, con, this.tipoDB,this.tabellaSoggetti);
				} catch (Exception e) {
					// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
					// in un database che non ' quello della controlstation ma quello pdd.
					//this.log.info(e);
				}
				if( (idServizioPA>0) || (nomeServizio!=null && !nomeServizio.equals("") && tipoServizioPA!=null && !tipoServizioPA.equals("")) ){
					paServizio = new PortaApplicativaServizio();
					paServizio.setNome(nomeServizio);
					paServizio.setTipo(tipoServizioPA);
					paServizio.setId(idServizioPA);
				}
				pa.setServizio(paServizio);

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
					if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
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
					if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
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
				pa.setBehaviour(rs.getString("behaviour"));

				// Autorizzazione per contenuto
				pa.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				String azione = rs.getString("azione");
				//aggiunto controllo per coerenza su "-" e "",
				//i valori "-" e "" vengono interpretati come null
				if (azione != null && !"-".equals(azione) && !"".equals(azione)) {
					PortaApplicativaAzione paAzione = new PortaApplicativaAzione();
					paAzione.setNome(azione);
					pa.setAzione(paAzione);
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
				
				String ws_security = rs.getString("ws_security");
				String ws_security_mtom_req = rs.getString("ws_security_mtom_req");
				String ws_security_mtom_res = rs.getString("ws_security_mtom_res");
				MessageSecurity messageSecurity = null;
				if(  (ws_security_mtom_req!=null && !ws_security_mtom_req.equals(""))  
						||
						(ws_security_mtom_res!=null && !ws_security_mtom_res.equals(""))  	)
				{
					messageSecurity = new MessageSecurity();
					if((ws_security_mtom_req!=null && !ws_security_mtom_req.equals(""))  ){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
						messageSecurity.getRequestFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(ws_security_mtom_req));
					}
					if((ws_security_mtom_res!=null && !ws_security_mtom_res.equals(""))  ){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
						messageSecurity.getResponseFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(ws_security_mtom_res));
					}
				}
				
				rs.close();
				stm.close();

				// stato wss
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(ws_security)) {
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

						ServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new ServizioApplicativo();
							servizioApplicativo.setId(idSA);
							servizioApplicativo.setNome(rs1.getString("nome"));
							pa.addServizioApplicativo(servizioApplicativo);
						}
						rs1.close();
						stm1.close();
					}
				}
				rs.close();
				stm.close();
				// pa.addSetProperty(setProperty); .....
				ProprietaProtocollo prop = null;
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs=stm.executeQuery();
				while (rs.next()) {
					prop = new ProprietaProtocollo();
					prop.setId(idPortaApplicativa);
					prop.setNome(rs.getString("nome"));
					prop.setValore(rs.getString("valore"));
					pa.addProprietaProtocollo(prop);
				}
				rs.close();
				stm.close();
				
				
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
			sqlQueryObject.addSelectField("autorizzazione");
			sqlQueryObject.addSelectField("autorizzazione_contenuto");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("location");
			sqlQueryObject.addSelectField("nome_porta");
			sqlQueryObject.addSelectField("id_soggetto_erogatore");
			sqlQueryObject.addSelectField("mode_soggetto_erogatore");
			sqlQueryObject.addSelectField("nome_soggetto_erogatore");
			sqlQueryObject.addSelectField("tipo_soggetto_erogatore");
			sqlQueryObject.addSelectField("pattern_soggetto_erogatore");
			sqlQueryObject.addSelectField("id_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("mode_servizio");
			sqlQueryObject.addSelectField("pattern_servizio");
			sqlQueryObject.addSelectField("mode_azione");
			sqlQueryObject.addSelectField("id_azione");
			sqlQueryObject.addSelectField("nome_azione");
			sqlQueryObject.addSelectField("pattern_azione");
			sqlQueryObject.addSelectField("force_wsdl_based_azione");
			sqlQueryObject.addSelectField("ricevuta_asincrona_asim");
			sqlQueryObject.addSelectField("ricevuta_asincrona_sim");
			sqlQueryObject.addSelectField("integrazione");
			sqlQueryObject.addSelectField("scadenza_correlazione_appl");
			sqlQueryObject.addSelectField("validazione_contenuti_stato");
			sqlQueryObject.addSelectField("validazione_contenuti_tipo");
			sqlQueryObject.addSelectField("validazione_contenuti_mtom");
			sqlQueryObject.addSelectField("mtom_request_mode");
			sqlQueryObject.addSelectField("mtom_response_mode");
			sqlQueryObject.addSelectField("ws_security");
			sqlQueryObject.addSelectField("ws_security_mtom_req");
			sqlQueryObject.addSelectField("ws_security_mtom_res");
			sqlQueryObject.addSelectField("allega_body");
			sqlQueryObject.addSelectField("scarta_body");
			sqlQueryObject.addSelectField("gestione_manifest");
			sqlQueryObject.addSelectAliasField(CostantiDB.PORTE_DELEGATE+".descrizione", "descrizionePD");
			sqlQueryObject.addSelectField("stateless");
			sqlQueryObject.addSelectField("local_forward");
			sqlQueryObject.addSelectField("id_accordo");
			sqlQueryObject.addSelectField("id_port_type");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+this.tabellaSoggetti+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, idPortaDelegata);

			rs = stm.executeQuery();

			if (rs.next()) {// ho trovato la porta delegata che cercavo

				pd = new PortaDelegata();

				pd.setTipoSoggettoProprietario(rs.getString("tipo_soggetto"));
				pd.setNomeSoggettoProprietario(rs.getString("nome_soggetto"));

				pd.setId(idPortaDelegata);
				pd.setAutenticazione(rs.getString("autenticazione"));
				pd.setAutorizzazione(rs.getString("autorizzazione"));
				pd.setAutorizzazioneContenuto(rs.getString("autorizzazione_contenuto"));

				pd.setDescrizione(rs.getString("descrizionePD"));
				pd.setIdSoggetto(rs.getLong("id_soggetto"));
				pd.setLocation(rs.getString("location"));
				pd.setNome(rs.getString("nome_porta"));

				//idaccordo
				pd.setIdAccordo(rs.getLong("id_accordo"));
				//id port type
				pd.setIdPortType(rs.getLong("id_port_type"));

				//se mode e' settato allora creo oggetto
				String modeSoggErogatore = rs.getString("mode_soggetto_erogatore");
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
				if(idSoggErogatore>0 || (modeSoggErogatore!=null && !modeSoggErogatore.equals(""))){
					SoggettoErogatore = new PortaDelegataSoggettoErogatore();
					SoggettoErogatore.setId(idSoggErogatore);
					SoggettoErogatore.setIdentificazione(PortaDelegataSoggettoErogatoreIdentificazione.toEnumConstant(modeSoggErogatore));
					SoggettoErogatore.setNome(nomeSoggettoErogatore);
					SoggettoErogatore.setTipo(tipoSoggettoErogatore);
					SoggettoErogatore.setPattern(rs.getString("pattern_soggetto_erogatore"));
					pd.setSoggettoErogatore(SoggettoErogatore);
				}else{
					throw new DriverConfigurazioneException("Soggetto Erogatore della Porta Delegata ["+pd.getNome()+"] non presente.");
				}

				//se mode e' settato allora creo oggetto
				String tipoServizio = rs.getString("tipo_servizio");
				String nomeServizio = rs.getString("nome_servizio");
				String mode_servizio = rs.getString("mode_servizio");
				long idServizioDB = rs.getLong("id_servizio");
				long idServizio=-1;
				if( (idServizioDB==-2) || (idServizioDB>0) ){
					idServizio = idServizioDB;
				}
				else{
					try {
						idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, nomeSoggettoErogatore, tipoSoggettoErogatore, con, this.tipoDB,this.tabellaSoggetti);
					} catch (Exception e) {
						// NON Abilitare il log, poiche' la tabella servizi puo' non esistere per il driver di configurazione 
						// in un database che non ' quello della controlstation ma quello pdd.
						//this.log.debug(e);
					}
				}
				PortaDelegataServizio pdServizio = null;
				if(idServizio>0 || (mode_servizio!=null && !mode_servizio.equals(""))){
					pdServizio=new PortaDelegataServizio();
					pdServizio.setId(idServizio);
					pdServizio.setNome(nomeServizio);
					pdServizio.setTipo(tipoServizio);
					pdServizio.setIdentificazione(PortaDelegataServizioIdentificazione.toEnumConstant(mode_servizio));
					pdServizio.setPattern(rs.getString("pattern_servizio"));
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
					pdAzione.setForceWsdlBased(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("force_wsdl_based_azione")));
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
					if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
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
					if (modeCA.equals("urlBased") || modeCA.equals("contentBased"))
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
				pd.setLocalForward(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(rs.getString("local_forward")));

				// messageSecurity			
				String ws_security = rs.getString("ws_security");
				String ws_security_mtom_req = rs.getString("ws_security_mtom_req");
				String ws_security_mtom_res = rs.getString("ws_security_mtom_res");
				MessageSecurity messageSecurity = null;
				if(  (ws_security_mtom_req!=null && !ws_security_mtom_req.equals(""))  
						||
						(ws_security_mtom_res!=null && !ws_security_mtom_res.equals(""))  	)
				{
					messageSecurity = new MessageSecurity();
					if((ws_security_mtom_req!=null && !ws_security_mtom_req.equals(""))  ){
						messageSecurity.setRequestFlow(new MessageSecurityFlow());
						messageSecurity.getRequestFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(ws_security_mtom_req));
					}
					if((ws_security_mtom_res!=null && !ws_security_mtom_res.equals(""))  ){
						messageSecurity.setResponseFlow(new MessageSecurityFlow());
						messageSecurity.getResponseFlow().setApplyToMtom(DriverConfigurazioneDB_LIB.getEnumStatoFunzionalita(ws_security_mtom_res));
					}
				}

				rs.close();
				stm.close();

				// stato wss
				if (CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(ws_security)) {
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

						ServizioApplicativo servizioApplicativo = null;
						if (rs1.next()) {
							// setto solo il nome come da specifica
							servizioApplicativo = new ServizioApplicativo();
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

	public ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		return getServizioApplicativo(5, null, null, null, null, null, null, idServizioApplicativo);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
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
			sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
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
	 * Reset delle tabelle del db pddConsole gestito da questo driver
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
	 * Verifica l'esistenza di una porta applicativa in cui il soggetto erogatore e' un soggetto virtuale
	 * @param idPA
	 * @return true se la porta applicativa esiste, false altrimenti
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public boolean existsPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException {

		try{
			return getPortaApplicativaVirtuale(idPA,soggettoVirtuale)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}
	}
	/**
	 * Verifica l'esistenza di una porta applicativa in cui il soggetto erogatore e' un soggetto virtuale
	 * @param idPA
	 * @param ricercaPuntuale ricercaPuntuale
	 * @return true se la porta applicativa esiste, false altrimenti
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public boolean existsPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale,boolean ricercaPuntuale) throws DriverConfigurazioneException {

		try{
			return getPortaApplicativaVirtuale(idPA,soggettoVirtuale,ricercaPuntuale)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}
	}
	/**
	 * Verifica l'esistenza di una porta applicativa.
	 * E' possibile specificare se si vuole effettuare una ricerca puntuale in tal caso
	 * se l'azione e' specificata ma non vi sono porte applicative che mathcano i criteri verra restituito false
	 * @param idPA id della porta applicativa da verificare
	 * @param ricercaPuntuale ricercaPuntuale
	 * @return true se la porta applicativa esiste, false altrimenti
	 * @throws DriverRegistroServiziException 
	 */
	@Override
	public boolean existsPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale) throws DriverConfigurazioneException {

		try{
			return getPortaApplicativa(idPA,ricercaPuntuale)!=null;
		}catch (DriverConfigurazioneNotFound e) {
			return false;
		}
	}
	/**
	 * Verifica l'esistenza di una porta applicativa.
	 * se l'azione e' specificata ma non vi sono porte applicative che mathcano i criteri allora
	 * effettua la ricerca senza tener conto dell'azione. Per una ricerca piu precisa utilizzare 
	 * existsPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale) con il parametro ricercaPuntuale a true
	 * @param idPA id della porta applicativa da verificare
	 * @return true se la porta applicativa esiste, false altrimenti
	 * @throws DriverRegistroServiziException 
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaServizioApplicativo] Exception accedendo al datasource :" + e.getMessage());

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaSoggetto] Exception accedendo al datasource :" + e.getMessage());

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

	public List<PortaApplicativa> getPorteApplicativeWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteApplicativeWithServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithServizio] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ?", "id_soggetto_virtuale = ?", "tipo_soggetto_virtuale = ? AND nome_soggetto_virtuale = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);
			stm.setString(2, tiposervizio);
			stm.setString(3, nomeservizio);
			stm.setLong(4, idSoggetto);
			stm.setLong(5, idSoggetto);
			stm.setString(6, tiposoggetto);
			stm.setString(7, nomesoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, idSoggetto, idSoggetto, tiposoggetto, nomesoggetto));
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

	public PortaApplicativa getPortaApplicativaWithSoggettoAndServizio(String nome, Long idSoggetto, Long idServizio, String tipoServizio, String nomeServizio) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		PortaApplicativa pa = new PortaApplicativa();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativaWithSoggettoAndServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteApplicativeWithSoggettoAndServizio] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome_porta = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND id_soggetto_virtuale <= ?",
					"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
					"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?",
					"id_soggetto_virtuale = ? AND id_servizio = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			stm.setLong(2, idSoggetto);
			stm.setString(3, tipoServizio);
			stm.setString(4, nomeServizio);
			stm.setLong(5, 0);

			stm.setLong(6, idSoggetto);
			stm.setLong(7, idServizio);
			stm.setLong(8, 0);

			stm.setLong(9, idSoggetto);
			stm.setString(10, tipoServizio);
			stm.setString(11, nomeServizio);

			stm.setLong(12, idSoggetto);
			stm.setLong(13, idServizio);

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
	public Vector<IDPortaApplicativaByNome> getPortaApplicativaAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativaAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		Vector<IDPortaApplicativaByNome> id = new Vector<IDPortaApplicativaByNome>();
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("azione=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto="+this.tabellaSoggetti+".id");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaApplicativaByNome idPA = new IDPortaApplicativaByNome();
				idPA.setSoggetto(new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto")));
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage());

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataServizioApplicativo] Exception accedendo al datasource :" + e.getMessage());

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataSoggetto] Exception accedendo al datasource :" + e.getMessage());

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

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<PortaDelegata> lista = new ArrayList<PortaDelegata>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPorteDelegateWithServizio");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition(false, "id_servizio = ?", "tipo_servizio = ? AND nome_servizio = ?");
			sqlQueryObject.addWhereCondition(false, "id_soggetto_erogatore = ?", "tipo_soggetto_erogatore = ? AND nome_soggetto_erogatore = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idServizio);
			stm.setString(2, tiposervizio);
			stm.setString(3, nomeservizio);
			stm.setLong(4, idSoggetto);
			stm.setString(5, tiposoggetto);
			stm.setString(6, nomesoggetto);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idServizio, tiposervizio, nomeservizio, idSoggetto, tiposoggetto, nomesoggetto));
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getPorteDelegateWithServizio] Exception accedendo al datasource :" + e.getMessage());

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

	public List<ServizioApplicativo> getServiziApplicativiWithIdErogatore(Long idErogatore) throws DriverConfigurazioneException {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getServiziApplicativiWithIdErogatore");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getServiziApplicativiWithIdErogatore] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idErogatore);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idErogatore));
			rs = stm.executeQuery();

			while (rs.next()) {
				ServizioApplicativo sa = this.getServizioApplicativo(rs.getLong("id"));
				lista.add(sa);
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
	public Vector<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaDelegataAzione");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaApplicativaAzione] Exception accedendo al datasource :" + e.getMessage());

			}

		} else
			con = this.globalConnection;

		Vector<IDPortaDelegata> id = new Vector<IDPortaDelegata>();
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
			sqlQueryObject.addFromTable(this.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition("nome_azione=?");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto="+this.tabellaSoggetti+".id");
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, nome);

			this.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, nome));
			rs = stm.executeQuery();

			while (rs.next()){
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setLocationPD(rs.getString("nome_porta"));
				idPD.setSoggettoFruitore(new IDSoggetto(rs.getString("tipo_soggetto"), rs.getString("nome_soggetto")));
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegataAzione] Exception accedendo al datasource :" + e.getMessage());

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


	@Override
	public boolean existsServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(idSoggetto);
		idServizioApplicativo.setNome(nomeServizioApplicativo);
		return this.existsServizioApplicativo(idServizioApplicativo);
	}

	/**
	 * Verifica l'esistenza di un servizio applicativo.
	 *
	 * @param idServizioApplicativo id del servizio applicativo
	 * @return true se il servizio applicativo esiste, false altrimenti
	 * @throws DriverRegistroServiziException
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsServizioApplicativoSoggetto] Exception accedendo al datasource :" + e.getMessage());

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
				IDServizio idSE = new IDServizio();
				idSE.setAzione(risultato.getString("azione"));
				idSE.setServizio(risultato.getString("servizio"));
				idSE.setTipoServizio(risultato.getString("tipo_servizio"));
				idSE.setSoggettoErogatore(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto"));
				idPA.setIDServizio(idSE);
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

				idPD.setSoggettoFruitore(new IDSoggetto(risultato.getString("tipo_soggetto"), risultato.getString("nome_soggetto")));
				String location = risultato.getString("location");
				if(location==null || "".equals(location))
					location = risultato.getString("nome_porta");

				idPD.setLocationPD(location);
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
				stmtTest = con.createStatement();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable("db_info");
				sqlQueryObject.addSelectField("*");
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmtTest.execute(sqlQuery);
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
				stmtTest = this.globalConnection.createStatement();
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable("db_info");
				sqlQueryObject.addSelectField("*");
				String sqlQuery = sqlQueryObject.createSQLQuery();
				stmtTest.execute(sqlQuery);
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

	@Override
	public PortaApplicativa getPortaApplicativa(
			IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(idPA==null) 
			throw new DriverConfigurazioneException("Identificativo non fornito");
		return this.getPortaApplicativa(idPA.getNome(), idPA.getSoggetto());
	}

	@Override
	public PortaApplicativa getPortaApplicativa(String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {		
		if(nomePorta==null || "".equals(nomePorta.trim())) throw new DriverConfigurazioneException("Nome PA non valido");

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaApplicativa");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		long idPorta= -1;
		try{
			if(!existsPortaApplicativa(nomePorta,soggettoProprietario)) 
				throw new DriverConfigurazioneNotFound("Porta Applicativa ["+nomePorta+"] del soggetto ["+soggettoProprietario.toString()+"] non trovata.");

			idPorta=DriverConfigurazioneDB_LIB.getIdPortaApplicativa(nomePorta, soggettoProprietario.getTipo(),soggettoProprietario.getNome(), con, this.tipoDB,this.tabellaSoggetti);
		}catch (CoreException e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}catch (DriverConfigurazioneNotFound e) {
			throw e;
		}catch (DriverConfigurazioneException e) {
			throw e;
		}finally{
			try{
				if (this.atomica) {
					this.log.debug("rilascio connessioni al db...");
					con.close();
				}
			}catch (Exception e) {

			}
		}

		return getPortaApplicativa(idPorta);

	}

	@Override
	public boolean existsPortaApplicativa(
			IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException{
		if(idPA==null) 
			throw new DriverConfigurazioneException("Identificativo non fornito");
		return this.existsPortaApplicativa(idPA.getNome(), idPA.getSoggetto());
	}
	
	@Override
	public boolean existsPortaApplicativa(String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException {

		if(nomePorta==null || "".equals(nomePorta.trim())) throw new DriverConfigurazioneException("Nome PA non valido");

		// check Soggetto Proprietario
		if(!existsSoggetto(soggettoProprietario)) return false;

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaApplicativa");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			return DBUtils.getIdPortaApplicativa(nomePorta, soggettoProprietario.getTipo(),soggettoProprietario.getNome(), con, this.tipoDB,this.tabellaSoggetti)>0;
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

	public List<PortaApplicativa> serviziPorteAppList(String tipoServizio,String nomeServizio,long idServizio, long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziPorteAppList";
		int idLista = Liste.SERVIZI_PORTE_APPLICATIVE;
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.addWhereLikeCondition("nome_porta", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idSoggettoErogatore);
			stmt.setString(2, tipoServizio);
			stmt.setString(3, nomeServizio);
			stmt.setLong(4, 0);

			stmt.setLong(5, idSoggettoErogatore);
			stmt.setLong(6, idServizio);
			stmt.setLong(7, 0);

			stmt.setLong(8, idSoggettoErogatore);
			stmt.setString(9, tipoServizio);
			stmt.setString(10, nomeServizio);

			stmt.setLong(11, idSoggettoErogatore);
			stmt.setLong(12, idServizio);

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
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?",
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
				sqlQueryObject.addWhereCondition(false, "id_soggetto = ? AND tipo_servizio = ? AND servizio=? AND id_soggetto_virtuale <= ?",
						"id_soggetto = ? AND id_servizio = ? AND id_soggetto_virtuale <= ?", 
						"id_soggetto_virtuale = ? AND tipo_servizio = ? AND servizio=?",
						"id_soggetto_virtuale = ? AND id_servizio = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_porta");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);

			stmt.setLong(1, idSoggettoErogatore);
			stmt.setString(2, tipoServizio);
			stmt.setString(3, nomeServizio);
			stmt.setLong(4, 0);

			stmt.setLong(5, idSoggettoErogatore);
			stmt.setLong(6, idServizio);
			stmt.setLong(7, 0);

			stmt.setLong(8, idSoggettoErogatore);
			stmt.setString(9, tipoServizio);
			stmt.setString(10, nomeServizio);

			stmt.setLong(11, idSoggettoErogatore);
			stmt.setLong(12, idServizio);

			risultato = stmt.executeQuery();

			PortaApplicativa pa = null;

			while (risultato.next()) {

				pa = this.getPortaApplicativa(risultato.getLong("id"));

				lista.add(pa);

			}

			return lista;

		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception: " + se.getMessage());
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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
								+ e.getMessage());

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
								+ e.getMessage());

			}

		} else
			con = this.globalConnection;
		ArrayList<PortaApplicativa> lista = null;
		try {
			IDSoggetto erogatore = idSE.getSoggettoErogatore();
			String tipoServizio = idSE.getTipoServizio();
			String nomeServizio = idSE.getServizio();

			long idSoggetto = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
			long idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);

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
								+ e.getMessage());

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

			if(idSE!=null){
				erogatore = idSE.getSoggettoErogatore();
				nomeErogatore = erogatore.getNome();
				tipoErogatore = erogatore.getTipo();
				tipoServizio = idSE.getTipoServizio();
				nomeServizio = idSE.getServizio();
			}

			long idSoggettoFruitore = DBUtils.getIdSoggetto(fruitore.getNome(), fruitore.getTipo(), con, this.tipoDB);

			long idSoggettoErogatore =-1;
			long idServizio = -1;
			if(idSE!=null){
				idSoggettoErogatore = DBUtils.getIdSoggetto(erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
				idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, erogatore.getNome(), erogatore.getTipo(), con, this.tipoDB);
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

	public boolean existsPortaDelegata(String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException {

		if(nomePorta==null || "".equals(nomePorta.trim())) throw new DriverConfigurazioneException("Nome PD non valido");

		// check Soggetto Proprietario
		if(!existsSoggetto(soggettoProprietario)) return false;

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("existsPortaDelegata");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			return DriverConfigurazioneDB_LIB.getIdPortaDelegata(nomePorta, soggettoProprietario.getTipo(),soggettoProprietario.getNome(), con, this.tipoDB,this.tabellaSoggetti)>0;
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

	public PortaDelegata getPortaDelegata(String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if(nomePorta==null || "".equals(nomePorta.trim())) throw new DriverConfigurazioneException("Nome PD non valido");

		// check Soggetto Proprietario
		if(!existsSoggetto(soggettoProprietario)) throw new DriverConfigurazioneNotFound("Il soggetto indicato come proprietario della Porta Delegata non esiste");

		Connection con = null;

		if (this.atomica) {
			try {
				con = getConnectionFromDatasource("getPortaDelegata");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsPortaDelegata] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.globalConnection;

		try {
			long idPD = DriverConfigurazioneDB_LIB.getIdPortaDelegata(nomePorta, soggettoProprietario.getTipo(),soggettoProprietario.getNome(), con, this.tipoDB,this.tabellaSoggetti);
			return this.getPortaDelegata(idPD,con);
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








	// all
	public int countTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, false, filters, false);
	}
	public int countTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, false, filters, false);
	}

	// fruizione

	public int countTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, false);
	}
	public int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, checkAssociazionePorta);
	}

	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, false);
	}
	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, false);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, false);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta);
	}

	// erogazione

	public int countTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, false);
	}
	public int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, checkAssociazionePorta);
	}

	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false, filters, false);
	}
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true,  null, false, filters, false);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, false);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta);
	}

	// engine

	private int serviziApplicativiList_engineCount(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException {

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
					filters, checkAssociazionePorta, search, true);
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
		return serviziApplicativiList_engine(null, null, false,  null, false, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  null, false, filters, false);
	}

	// fruizione

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, null, true, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, true, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, fruizione, true, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  fruizione, true, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, fruizione, true, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta);
	}

	// erogazione

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, false);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, checkAssociazionePorta);
	}

	// engine

	private List<TipologiaServizioApplicativo> serviziApplicativiList_engine(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException {

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
					filters, checkAssociazionePorta));



			// *** select ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, serchSoloFruizione,  
					filters, checkAssociazionePorta, search, false);
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
			ISearch filters,boolean checkAssociazionePorta,String search, boolean count) throws Exception{

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
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage());

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
				throw new DriverConfigurazioneException("[DriverRegistroServiziDB::getPropertiesConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

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
			throw new DriverConfigurazioneException("[DriverRegistroServiziDB::getPropertiesConnettore] DriverConfigurazioneException : " + e.getMessage(),e);
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
			Vector<IDSoggetto> idSoggetti = new Vector<IDSoggetto>();
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
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".nome_porta");
			sqlQueryObject.addSelectField(CostantiDB.PORTE_DELEGATE+".location");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");

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
				if(filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_DELEGATE+".nome_porta = ?");
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
				if(filtroRicerca.getAzione()!=null){
					this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
			}
			rs = stm.executeQuery();
			Vector<IDPortaDelegata> idsPD = new Vector<IDPortaDelegata>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setSoggettoFruitore(idS);
				String location = rs.getString("location");
				if(location!=null && !"".equals(location)){
					idPD.setLocationPD(location);
				}else{
					idPD.setLocationPD(rs.getString("nome_porta"));
				}
				idsPD.add(idPD);
			}
			if(idsPD.size()==0){
				if(filtroRicerca!=null)
					throw new DriverConfigurazioneNotFound("PorteDelegate non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverConfigurazioneNotFound("PorteDelegate non trovate");
			}else{
				return idsPD;
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
	public List<IDPortaApplicativaByNome> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

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
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.PORTE_APPLICATIVE+".nome_porta");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
			sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".id_soggetto = "+CostantiDB.SOGGETTI+".id");

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
				if(filtroRicerca.getAzione()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".azione = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.PORTE_APPLICATIVE+".nome_porta = ?");
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
				if(filtroRicerca.getAzione()!=null){
					this.log.debug("azione stmt.setString("+filtroRicerca.getAzione()+")");
					stm.setString(indexStmt, filtroRicerca.getAzione());
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}
			}
			rs = stm.executeQuery();
			Vector<IDPortaApplicativaByNome> idsPA = new Vector<IDPortaApplicativaByNome>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				IDPortaApplicativaByNome idPA = new IDPortaApplicativaByNome();
				idPA.setSoggetto(idS);
				idPA.setNome(rs.getString("nome_porta"));
				idsPA.add(idPA);
			}
			if(idsPA.size()==0){
				if(filtroRicerca!=null)
					throw new DriverConfigurazioneNotFound("PorteApplicative non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverConfigurazioneNotFound("PorteApplicative non trovate");
			}else{
				return idsPA;
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
			}
			rs = stm.executeQuery();
			Vector<IDServizioApplicativo> idsSA = new Vector<IDServizioApplicativo>();
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
	
	
	@Override
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
			stmt.setInt(1, idPortaDelegata);
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

	@Override
	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(int idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
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
	
	@Override
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
			stmt.setInt(1, idPortaApplicativa);
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

	@Override
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(int idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
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
}
