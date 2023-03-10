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
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.constants.StatoAllarme;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGestioneErrore;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaServizioApplicativo;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaSoggetto;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import org.openspcoop2.core.config.constants.RicercaTipologiaErogazione;
import org.openspcoop2.core.config.constants.RicercaTipologiaFruizione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.TipologiaServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
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
	private DataSource datasource = null;

	// Connection passata al momento della creazione dell'oggetto
	protected Connection globalConnection = null;
	// Variabile di controllo del tipo di operazione da effettuare
	// l'autoCommit viene gestito internamente a questa classe
	protected boolean atomica = true;
	public boolean isAtomica() {
		return this.atomica;
	}

	/** Logger utilizzato per debug. */
	protected org.slf4j.Logger log = null;

	// Tipo database passato al momento della creazione dell'oggetto
	protected String tipoDB = null;
	public String getTipoDB() {
		return this.tipoDB;
	}

	/** Tabella soggetti */
	protected String tabellaSoggetti = CostantiDB.SOGGETTI;
	
	protected boolean useSuperUser = true;
	public boolean isUseSuperUser() {
		return this.useSuperUser;
	}
	public void setUseSuperUser(boolean useSuperUser) {
		this.useSuperUser = useSuperUser;
	}

	// Driver
	
	private DriverConfigurazioneDB_soggettiDriver soggettiDriver = null;
	private DriverConfigurazioneDB_soggettiSearchDriver soggettiSearchDriver = null;
	
	private DriverConfigurazioneDB_connettoriDriver connettoriDriver = null;
	
	private DriverConfigurazioneDB_porteDelegateDriver porteDelegateDriver = null;
	private DriverConfigurazioneDB_porteDelegateSearchDriver porteDelegateSearchDriver = null;
	private DriverConfigurazioneDB_porteApplicativeDriver porteApplicativeDriver = null;
	private DriverConfigurazioneDB_porteApplicativeSearchDriver porteApplicativeSearchDriver = null;
	private DriverConfigurazioneDB_porteTrasformazioniDriver porteTrasformazioniDriver = null;
	private DriverConfigurazioneDB_porteDriver porteDriver = null;
	
	private DriverConfigurazioneDB_serviziApplicativiDriver serviziApplicativiDriver = null;
	private DriverConfigurazioneDB_serviziApplicativiSearchDriver serviziApplicativiSearchDriver = null;
	
	private DriverConfigurazioneDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	private DriverConfigurazioneDB_routingTableDriver routingTableDriver = null;
	private DriverConfigurazioneDB_gestioneErroreDriver gestioneErroreDriver = null;
	private DriverConfigurazioneDB_genericPropertiesDriver genericPropertiesDriver = null;
	private DriverConfigurazioneDB_configDriver configDriver = null;
	private DriverConfigurazioneDB_configSearchDriver configSearchDriver = null;
	
	private DriverConfigurazioneDB_pluginsDriver pluginsDriver = null;
	
	private DriverConfigurazioneDB_allarmiDriver allarmiDriver = null;
	
	private DriverConfigurazioneDB_utilsDriver utilsDriver = null;
	
	
	
	
	
	/* ******** COSTRUTTORI e METODI DI RELOAD ******** */

	public DriverConfigurazioneDB() {
		
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(this);
		this.soggettiSearchDriver = new DriverConfigurazioneDB_soggettiSearchDriver(this);
		
		this.connettoriDriver = new DriverConfigurazioneDB_connettoriDriver(this);
		
		this.porteDelegateDriver = new DriverConfigurazioneDB_porteDelegateDriver(this);
		this.porteDelegateSearchDriver = new DriverConfigurazioneDB_porteDelegateSearchDriver(this);
		this.porteApplicativeDriver = new DriverConfigurazioneDB_porteApplicativeDriver(this);
		this.porteApplicativeSearchDriver = new DriverConfigurazioneDB_porteApplicativeSearchDriver(this);
		this.porteTrasformazioniDriver = new DriverConfigurazioneDB_porteTrasformazioniDriver(this);
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(this);
		
		this.serviziApplicativiDriver = new DriverConfigurazioneDB_serviziApplicativiDriver(this);
		this.serviziApplicativiSearchDriver = new DriverConfigurazioneDB_serviziApplicativiSearchDriver(this);
		
		this.protocolPropertiesDriver = new DriverConfigurazioneDB_protocolPropertiesDriver(this);
		
		this.routingTableDriver = new DriverConfigurazioneDB_routingTableDriver(this);
		this.gestioneErroreDriver = new DriverConfigurazioneDB_gestioneErroreDriver(this);
		this.genericPropertiesDriver = new DriverConfigurazioneDB_genericPropertiesDriver(this);
		this.configDriver = new DriverConfigurazioneDB_configDriver(this);
		this.configSearchDriver = new DriverConfigurazioneDB_configSearchDriver(this);
		
		this.pluginsDriver = new DriverConfigurazioneDB_pluginsDriver(this);
		
		this.allarmiDriver = new DriverConfigurazioneDB_allarmiDriver(this);
		
		this.utilsDriver = new DriverConfigurazioneDB_utilsDriver(this);
		
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
		
		// driver
		
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(this);
		this.soggettiSearchDriver = new DriverConfigurazioneDB_soggettiSearchDriver(this);
		
		this.connettoriDriver = new DriverConfigurazioneDB_connettoriDriver(this);
		
		this.porteDelegateDriver = new DriverConfigurazioneDB_porteDelegateDriver(this);
		this.porteDelegateSearchDriver = new DriverConfigurazioneDB_porteDelegateSearchDriver(this);
		this.porteApplicativeDriver = new DriverConfigurazioneDB_porteApplicativeDriver(this);
		this.porteApplicativeSearchDriver = new DriverConfigurazioneDB_porteApplicativeSearchDriver(this);
		this.porteTrasformazioniDriver = new DriverConfigurazioneDB_porteTrasformazioniDriver(this);
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(this);
		
		this.serviziApplicativiDriver = new DriverConfigurazioneDB_serviziApplicativiDriver(this);
		this.serviziApplicativiSearchDriver = new DriverConfigurazioneDB_serviziApplicativiSearchDriver(this);
		
		this.protocolPropertiesDriver = new DriverConfigurazioneDB_protocolPropertiesDriver(this);
		
		this.routingTableDriver = new DriverConfigurazioneDB_routingTableDriver(this);
		this.gestioneErroreDriver = new DriverConfigurazioneDB_gestioneErroreDriver(this);
		this.genericPropertiesDriver = new DriverConfigurazioneDB_genericPropertiesDriver(this);
		this.configDriver = new DriverConfigurazioneDB_configDriver(this);
		this.configSearchDriver = new DriverConfigurazioneDB_configSearchDriver(this);
		
		this.pluginsDriver = new DriverConfigurazioneDB_pluginsDriver(this);
		
		this.allarmiDriver = new DriverConfigurazioneDB_allarmiDriver(this);
		
		this.utilsDriver = new DriverConfigurazioneDB_utilsDriver(this);
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

		// driver
		
		this.soggettiDriver = new DriverConfigurazioneDB_soggettiDriver(this);
		this.soggettiSearchDriver = new DriverConfigurazioneDB_soggettiSearchDriver(this);
		
		this.connettoriDriver = new DriverConfigurazioneDB_connettoriDriver(this);
		
		this.porteDelegateDriver = new DriverConfigurazioneDB_porteDelegateDriver(this);
		this.porteDelegateSearchDriver = new DriverConfigurazioneDB_porteDelegateSearchDriver(this);
		this.porteApplicativeDriver = new DriverConfigurazioneDB_porteApplicativeDriver(this);
		this.porteApplicativeSearchDriver = new DriverConfigurazioneDB_porteApplicativeSearchDriver(this);
		this.porteTrasformazioniDriver = new DriverConfigurazioneDB_porteTrasformazioniDriver(this);
		this.porteDriver = new DriverConfigurazioneDB_porteDriver(this);
		
		this.serviziApplicativiDriver = new DriverConfigurazioneDB_serviziApplicativiDriver(this);
		this.serviziApplicativiSearchDriver = new DriverConfigurazioneDB_serviziApplicativiSearchDriver(this);
		
		this.protocolPropertiesDriver = new DriverConfigurazioneDB_protocolPropertiesDriver(this);
		
		this.routingTableDriver = new DriverConfigurazioneDB_routingTableDriver(this);
		this.gestioneErroreDriver = new DriverConfigurazioneDB_gestioneErroreDriver(this);
		this.genericPropertiesDriver = new DriverConfigurazioneDB_genericPropertiesDriver(this);
		this.configDriver = new DriverConfigurazioneDB_configDriver(this);
		this.configSearchDriver = new DriverConfigurazioneDB_configSearchDriver(this);
		
		this.pluginsDriver = new DriverConfigurazioneDB_pluginsDriver(this);
		
		this.allarmiDriver = new DriverConfigurazioneDB_allarmiDriver(this);
		
		this.utilsDriver = new DriverConfigurazioneDB_utilsDriver(this);
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
	
	protected Connection getConnectionFromDatasource(String methodName) throws Exception{
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

			this.closeConnection(con);

		}
	}
	
	public void closeConnection(Connection conParam, Connection con) {
		try {
			if (conParam==null && this.atomica) {
				this.log.debug("rilascio connessione al db...");
				if(con!=null) {
					con.close();
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void closeConnection(Connection con) {
		try {
			if (this.atomica) {
				this.log.debug("rilascio connessioni al db...");
				if(con!=null) {
					con.close();
				}
			}
		} catch (Exception e) {
			// ignore exception
		}
	}
	
	public void closeConnection(boolean error, Connection con) {
		try {
			if (error && this.atomica) {
				this.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
				if(con!=null) {
					con.rollback();
					con.setAutoCommit(true);
					con.close();
				}

			} else if (!error && this.atomica) {
				this.log.debug("eseguo commit e rilascio connessioni...");
				if(con!=null) {
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}
			}

		} catch (Exception e) {
			// ignore exception
		}
	}
	
	
	
	
	// *** Soggetti ***
	
	public IDSoggetto getIdSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getIdSoggetto(idSoggetto);
	}
	public IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getIdSoggetto(idSoggetto, conParam);
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
		return this.soggettiDriver.getSoggetto(aSoggetto);
	}

	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.soggettiDriver.createSoggetto(soggetto);
	}

	/**
	 * Aggiorna un Soggetto e il Connettore con i nuovi dati passati
	 * 
	 * @param soggetto
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.soggettiDriver.updateSoggetto(soggetto);
	}
	
	/**
	 * Cancella un Soggetto e il Connettore
	 * 
	 * @param soggetto
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		this.soggettiDriver.deleteSoggetto(soggetto);
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
		return this.soggettiDriver.getRouter();
	}

	public List<IDSoggetto> getSoggettiWithSuperuser(String user) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getSoggettiWithSuperuser(user);
	}

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public List<IDSoggetto> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getSoggettiVirtuali();
	}

	/**
	 * Ritorna il {@linkplain Soggetto} utilizzando il {@link DriverConfigurazioneDB} che ha l'id passato come parametro 
	 */
	public Soggetto getSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getSoggetto(idSoggetto);
	}
	public Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getSoggetto(idSoggetto, conParam);
	}
	
	public List<Soggetto> soggettiList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		return this.soggettiSearchDriver.soggettiList(superuser, ricerca);
	}

	public List<Soggetto> soggettiWithServiziList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.soggettiSearchDriver.soggettiWithServiziList(ricerca);
	}
	public List<Soggetto> soggettiWithServiziList(String superuser,ISearch ricerca) throws DriverConfigurazioneException {
		return this.soggettiSearchDriver.soggettiWithServiziList(superuser, ricerca);
	}
	
	@Override
	public List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.soggettiDriver.getServizi_SoggettiVirtuali();
	}
	
	@Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverConfigurazioneException {
		return this.soggettiDriver.existsSoggetto(idSoggetto);
	}
	
	public List<Soggetto> getAllSoggetti() throws DriverConfigurazioneException {
		return this.soggettiDriver.getAllSoggetti();
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
	public List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.soggettiDriver.getAllIdSoggetti(filtroRicerca);
	}
	
	
	
	
	// *** Connettori ***
	
	/**
	 * Crea un nuovo Connettore
	 * 
	 * @param connettore
	 * @throws DriverConfigurazioneException
	 */
	public void createConnettore(Connettore connettore) throws DriverConfigurazioneException {
		this.connettoriDriver.createConnettore(connettore);
	}

	/**
	 * Aggiorna un Connettore
	 * 
	 * @param connettore
	 * @throws DriverConfigurazioneException
	 */
	public void updateConnettore(Connettore connettore) throws DriverConfigurazioneException {
		this.connettoriDriver.updateConnettore(connettore);
	}

	/**
	 * Elimina un Connettore
	 * 
	 * @param connettore
	 * @throws DriverConfigurazioneException
	 */
	public void deleteConnettore(Connettore connettore) throws DriverConfigurazioneException {
		this.connettoriDriver.deleteConnettore(connettore);
	}
	
	public List<String> connettoriList() throws DriverConfigurazioneException {
		return this.connettoriDriver.connettoriList();
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
		return this.connettoriDriver.getPropertiesConnettore(nomeConnettore);
	}
	public Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverConfigurazioneException {
		return this.connettoriDriver.getPropertiesConnettore(nomeConnettore, connection);
	}

	public boolean isPolicyNegoziazioneTokenUsedInConnettore(String nome) throws DriverConfigurazioneException{
		return this.connettoriDriver.isPolicyNegoziazioneTokenUsedInConnettore(nome);
	}
	
	public Connettore getConnettore(long idConnettore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.connettoriDriver.getConnettore(idConnettore);
	}
	public Connettore getConnettore(String nomeConnettore) throws DriverConfigurazioneException {
		return this.connettoriDriver.getConnettore(nomeConnettore);
	}



	
	// *** Porte Delegate ***
	
	@Override
	public IDPortaDelegata getIDPortaDelegata(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.porteDelegateDriver.getIDPortaDelegata(nome);
	}
	
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteDelegateDriver.getPortaDelegata(idPD);
	}

	public PortaDelegata getPortaDelegata(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteDelegateDriver.getPortaDelegata(id);
	}
	public PortaDelegata getPortaDelegata(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteDelegateDriver.getPortaDelegata(id, conParam);
	}
	
	@Override
	public void createPortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		this.porteDelegateDriver.createPortaDelegata(aPD);
	}
	
	@Override
	public void updatePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		this.porteDelegateDriver.updatePortaDelegata(aPD);
	}

	@Override
	public void deletePortaDelegata(PortaDelegata aPD) throws DriverConfigurazioneException {
		this.porteDelegateDriver.deletePortaDelegata(aPD);
	}
	
	public List<String> portaDelegataRuoliList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.portaDelegataRuoliList(idPD, ricerca);
	}
	public List<String> portaDelegataRuoliTokenList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.portaDelegataRuoliTokenList(idPD, ricerca);
	}	
	
	public List<String> portaDelegataScopeList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.portaDelegataScopeList(idPD, ricerca);
	}
	
	public List<TrasformazioneRegola> porteDelegateTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniList(idPA, ricerca);
	}
	
	public boolean azioneUsataInTrasformazioniPortaDelegata(String azione) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.azioneUsataInTrasformazioniPortaDelegata(azione);
	}

	public TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazione(idPorta, azioni, pattern, contentType, connettori,
				applicativi);
	}
	
	public TrasformazioneRegola getPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazione(idPorta, nome);
	}
	
	public boolean existsPortaDelegataTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazione(idPorta, azioni, pattern, contentType, connettori);
	}
	
	public boolean existsPortaDelegataTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazione(idPorta, nome);
	}
	
	public List<TrasformazioneRegolaRisposta> porteDelegateTrasformazioniRispostaList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniRispostaList(idPD, idTrasformazione, ricerca);
	}
	
	public TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
	}
	
	public TrasformazioneRegolaRisposta getPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, nome);
	}
	
	public boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
	}
	
	public boolean existsPortaDelegataTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazioneRisposta(idPorta, idTrasformazione, nome);
	}
	
	public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteDelegateTrasformazioniServiziApplicativiList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniServiziApplicativiList(idPD, idTrasformazione, ricerca);
	}
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRispostaHeaderList(long idPD, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniRispostaHeaderList(idPD, idTrasformazione, idTrasformazioneRisposta, ricerca);
	}

	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaHeaderList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniRichiestaHeaderList(idPD, idTrasformazione, ricerca);
	}
	
	public boolean existsPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public List<TrasformazioneRegolaParametro> porteDelegateTrasformazioniRichiestaUrlParameterList(long idPD, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteDelegateTrasformazioniRichiestaUrlParameterList(idPD, idTrasformazione, ricerca);
	}
	
	public boolean existsPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public List<ResponseCachingConfigurazioneRegola> portaDelegataResponseCachingConfigurazioneRegolaList(long idPD, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDriver.portaDelegataResponseCachingConfigurazioneRegolaList(idPD, ricerca);
	}
	
	public boolean existsPortaDelegataResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		return this.porteDriver.existsPortaDelegataResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault);
	}
	
	public List<String> porteDelegateRateLimitingValoriUnivoci(String pName) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.porteDelegateRateLimitingValoriUnivoci(pName);
	}
	
	public List<PortaDelegata> porteDelegateList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateList(idSoggetto, ricerca);
	}
	
	public List<Proprieta> porteDelegatePropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegatePropList(idPortaDelegata, ricerca);
	}
	
	/**
	 * Ritorna la lista di nomi delle proprieta registrate
	 */
	public List<String> nomiProprietaPD(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoServiziProtocollo) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.nomiProprietaPD(filterSoggettoTipo, filterSoggettoNome, tipoServiziProtocollo);
	}
	
	public List<Proprieta> porteDelegateAutenticazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateAutenticazioneCustomPropList(idPortaDelegata, ricerca);
	}
	
	public List<Proprieta> porteDelegateAutorizzazioneCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateAutorizzazioneCustomPropList(idPortaDelegata, ricerca);
	}
	
	public List<Proprieta> porteDelegateAutorizzazioneContenutoCustomPropList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateAutorizzazioneContenutoCustomPropList(idPortaDelegata, ricerca);
	}

	public List<PortaDelegata> porteDelegateList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateList(superuser, ricerca);
	}

	public List<PortaDelegata> porteDelegateWithSoggettoErogatoreList(long idSoggettoErogatore) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.porteDelegateWithSoggettoErogatoreList(idSoggettoErogatore);
	}

	public List<PortaDelegata> porteDelegateWithTipoNomeErogatoreList(String tipoSoggettoErogatore, String nomeSoggettoErogatore) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.porteDelegateWithTipoNomeErogatoreList(tipoSoggettoErogatore, nomeSoggettoErogatore);
	}
	
	public List<ServizioApplicativo> porteDelegateServizioApplicativoList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateServizioApplicativoList(idPortaDelegata, ricerca);
	}
	public List<ServizioApplicativo> porteDelegateServizioApplicativoTokenList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateServizioApplicativoTokenList(idPortaDelegata, ricerca);
	}
	
	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateMessageSecurityRequestList(idPortaDelegata, ricerca);
	}

	public List<MessageSecurityFlowParameter> porteDelegateMessageSecurityResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateMessageSecurityResponseList(idPortaDelegata, ricerca);
	}

	public List<CorrelazioneApplicativaElemento> porteDelegateCorrelazioneApplicativaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateCorrelazioneApplicativaList(idPortaDelegata, ricerca);
	}

	public List<CorrelazioneApplicativaRispostaElemento> porteDelegateCorrelazioneApplicativaRispostaList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateCorrelazioneApplicativaRispostaList(idPortaDelegata, ricerca);
	}
	
	@Override
	public boolean existsPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.existsPortaDelegata(idPD);
	}

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio, String tiposervizio, String nomeservizio,
			Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.getPorteDelegateWithServizio(idServizio, tiposervizio, nomeservizio,
				versioneServizio,
				idSoggetto, tiposoggetto, nomesoggetto);
	}

	public List<PortaDelegata> getPorteDelegateWithServizio(Long idServizio) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.getPorteDelegateWithServizio(idServizio);
	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public List<IDPortaDelegata> getPortaDelegataAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.porteDelegateDriver.getPortaDelegataAzione(nome);
	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public boolean existsPortaDelegataAzione(String nome) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.existsPortaDelegataAzione(nome);
	}
	
	public List<PortaDelegata> serviziFruitoriPorteDelegateList(long idSoggetto, String tipoServizio,String nomeServizio,Long idServizio, 
			String tipoSoggettoErogatore, String nomeSoggettoErogatore, Long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.serviziFruitoriPorteDelegateList(idSoggetto, tipoServizio, nomeServizio, idServizio, 
				tipoSoggettoErogatore, nomeSoggettoErogatore, idSoggettoErogatore, ricerca);
	}
	
	/**
	 * Recupera tutte le porte delegate appartenenti al soggetto 'fruitore'
	 * @param fruitore
	 * @return Lista di {@link PortaDelegata}
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaDelegata> getPorteDelegateByFruitore(IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		return this.porteDelegateSearchDriver.getPorteDelegateByFruitore(fruitore, filters);
	}

	/**
	 * Recupera tutte le porte delegate del soggetto fruitore, relative al servizio idServizio (se diverso da null), erogato dal soggetto erogatore del servizio.
	 * @param idSE L'id del servizio, puo essere null in tal caso si comporta come il metodo getPorteDelegateByFruitore
	 * @return List<PortaDelegata>
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaDelegata> getPorteDelegate(IDServizio idSE,IDSoggetto fruitore,ISearch filters) throws DriverConfigurazioneException{
		return this.porteDelegateSearchDriver.getPorteDelegate(idSE, fruitore, filters);
	}
	
	public List<PortaDelegata> getPorteDelegateBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		return this.porteDelegateDriver.getPorteDelegateBySoggetto(idSoggetto);
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
	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.porteDelegateDriver.getAllIdPorteDelegate(filtroRicerca);
	}
	
	public List<MtomProcessorFlowParameter> porteDelegateMTOMRequestList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateMTOMRequestList(idPortaDelegata, ricerca);
	}

	public List<MtomProcessorFlowParameter> porteDelegateMTOMResponseList(long idPortaDelegata, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDelegateSearchDriver.porteDelegateMTOMResponseList(idPortaDelegata, ricerca);
	}
	
	public List<PortaDelegata> getPorteDelegateByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		return this.porteDelegateDriver.getPorteDelegateByPolicyGestioneToken(nome);
	}
	
	public MappingFruizionePortaDelegata getMappingFruizione(IDServizio idServizio, IDSoggetto idSoggetto, IDPortaDelegata idPortaDelegata) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteDelegateDriver.getMappingFruizione(idServizio, idSoggetto, idPortaDelegata);		
	}

	

	
	// *** Porte Applicative ***
	
	@Override
	public IDPortaApplicativa getIDPortaApplicativa(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.porteApplicativeDriver.getIDPortaApplicativa(nome);
	}
	
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getPortaApplicativa(idPA);
	}
	
	public PortaApplicativa getPortaApplicativa(long id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getPortaApplicativa(id);
	}
	public PortaApplicativa getPortaApplicativa(long id,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getPortaApplicativa(id, conParam);
	}
	
	@Override
	public List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.porteApplicativeDriver.getPorteApplicative(idServizio, ricercaPuntuale);
	}
	
	@Override
	public List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.porteApplicativeDriver.getPorteApplicativeVirtuali(soggettoVirtuale, idServizio, ricercaPuntuale);
	}
	
	@Override
	public void createPortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		this.porteApplicativeDriver.createPortaApplicativa(aPA);
	}
	
	@Override
	public void updatePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		this.porteApplicativeDriver.updatePortaApplicativa(aPA);
	}

	@Override
	public void deletePortaApplicativa(PortaApplicativa aPA) throws DriverConfigurazioneException {
		this.porteApplicativeDriver.deletePortaApplicativa(aPA);
	}

	@Override
	public Map<IDSoggetto, PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getPorteApplicative_SoggettiVirtuali(idServizio);
	}
	
	public List<String> portaApplicativaRuoliList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.portaApplicativaRuoliList(idPA, ricerca);
	}
	public List<String> portaApplicativaRuoliTokenList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.portaApplicativaRuoliTokenList(idPA, ricerca);
	}
	
	public List<String> portaApplicativaScopeList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.portaApplicativaScopeList(idPA, ricerca);
	}
	
	public List<IDPortaApplicativa> porteApplicativeWithApplicativoErogatore(IDServizioApplicativo idSA) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.porteApplicativeWithApplicativoErogatore(idSA);
	}
	
	public List<TrasformazioneRegola> porteApplicativeTrasformazioniList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniList(idPA, ricerca);
	}
	
	public boolean azioneUsataInTrasformazioniPortaApplicativa(String azione) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.azioneUsataInTrasformazioniPortaApplicativa(azione);
	}
	
	public TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori,
			List<TrasformazioneRegolaApplicabilitaSoggetto> soggetti,
			List<TrasformazioneRegolaApplicabilitaServizioApplicativo> applicativi,
			boolean interpretaNullList) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazione(idPorta, azioni, pattern, contentType, connettori,
			soggetti,
			applicativi,
			interpretaNullList);
	}
	
	public TrasformazioneRegola getPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazione(idPorta, nome);
	}
	
	public boolean existsPortaApplicativaTrasformazione(long idPorta, String azioni, String pattern, String contentType, String connettori) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazione(idPorta, azioni, pattern, contentType, connettori);
	}
	
	public boolean existsPortaApplicativaTrasformazione(long idPorta, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazione(idPorta, nome);
	}
	
	public List<TrasformazioneRegolaRisposta> porteApplicativeTrasformazioniRispostaList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniRispostaList(idPA, idTrasformazione, ricerca);
	}
	
	public TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
	}

	public TrasformazioneRegolaRisposta getPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, nome);
	}
		
	public boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, Integer statusMin, Integer statusMax, String pattern, String contentType) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, statusMin, statusMax, pattern, contentType);
	}
	
	public boolean existsPortaApplicativaTrasformazioneRisposta(long idPorta, long idTrasformazione, String nome) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazioneRisposta(idPorta, idTrasformazione, nome);
	}
	
	public List<TrasformazioneRegolaApplicabilitaServizioApplicativo> porteApplicativeTrasformazioniServiziApplicativiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniServiziApplicativiList(idPA, idTrasformazione, ricerca);
	}
	
	public List<TrasformazioneRegolaApplicabilitaSoggetto> porteApplicativeTrasformazioniSoggettiList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniSoggettiList(idPA, idTrasformazione, ricerca);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRispostaHeaderList(long idPA, long idTrasformazione, long idTrasformazioneRisposta, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniRispostaHeaderList(idPA, idTrasformazione, idTrasformazioneRisposta, ricerca);
	}
	
	public boolean existsPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta,  nome, tipo, checkTipo);
	}
	
	public boolean existsPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaDelegataTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta,  nome, tipo, checkTipo);
	}
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
	}
	
	public TrasformazioneRegolaParametro getPortaDelegataTrasformazioneRispostaHeader(long idPorta, long idTrasformazione, long idTrasformazioneRisposta,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaDelegataTrasformazioneRispostaHeader(idPorta, idTrasformazione, idTrasformazioneRisposta, nome, tipo, checkTipo);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaHeaderList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniRichiestaHeaderList(idPA, idTrasformazione, ricerca);
	}
	
	public boolean existsPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}	
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaHeader(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazioneRichiestaHeader(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public List<TrasformazioneRegolaParametro> porteApplicativeTrasformazioniRichiestaUrlParameterList(long idPA, long idTrasformazione, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.porteApplicativeTrasformazioniRichiestaUrlParameterList(idPA, idTrasformazione, ricerca);
	}
	
	public boolean existsPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione, String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.existsPortaApplicativaTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public TrasformazioneRegolaParametro getPortaApplicativaTrasformazioneRichiestaUrlParameter(long idPorta, long idTrasformazione,  String nome, String tipo, boolean checkTipo) throws DriverConfigurazioneException {
		return this.porteTrasformazioniDriver.getPortaApplicativaTrasformazioneRichiestaUrlParameter(idPorta, idTrasformazione, nome, tipo, checkTipo);
	}
	
	public List<ResponseCachingConfigurazioneRegola> portaApplicativaResponseCachingConfigurazioneRegolaList(long idPA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteDriver.portaApplicativaResponseCachingConfigurazioneRegolaList(idPA, ricerca);
	}
	
	public boolean existsPortaApplicativaResponseCachingConfigurazioneRegola(long idPA, Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		return this.porteDriver.existsPortaApplicativaResponseCachingConfigurazioneRegola(idPA, statusMin, statusMax, fault);
	}
	
	/**
	 * Ritorna la lista di porte applicative con settati i campi id, nome,
	 * descrizione e se il security e' abilitato allora crea un oggetto
	 * MessageSecurity vuoto altrimenti null.
	 */
	public List<PortaApplicativa> porteAppList(long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppList(idSoggetto, ricerca);
	}
	/**
	 * Ritorna la lista di tutte le Porte Applicative
	 * @param ricerca
	 * @return Una lista di Porte Applicative
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaApplicativa> porteAppList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppList(superuser, ricerca);
	}

	/**
	 * Ritorna la lista di proprieta di una Porta Applicativa
	 */
	public List<Proprieta> porteAppPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppPropList(idPortaApplicativa, ricerca);
	}
	
	/**
	 * Ritorna la lista di nomi delle proprieta registrate
	 */
	public List<String> nomiProprietaPA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoServiziProtocollo) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.nomiProprietaPA(filterSoggettoTipo, filterSoggettoNome, tipoServiziProtocollo);
	}
	
	/**
	 * Ritorna la lista di proprieta per l'autenticazione custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutenticazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeAutenticazioneCustomPropList(idPortaApplicativa, ricerca);
	}
	
	/**
	 * Ritorna la lista di proprieta per l'autorizzazione custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutorizzazioneCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeAutorizzazioneCustomPropList(idPortaApplicativa, ricerca);
	}
	
	public List<String> porteApplicativeRateLimitingValoriUnivoci(String pName) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.porteApplicativeRateLimitingValoriUnivoci(pName);
	}
	
	/**
	 * Ritorna la lista di proprieta per l'autorizzazione contenuti custom di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeAutorizzazioneContenutoCustomPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeAutorizzazioneContenutoCustomPropList(idPortaApplicativa, ricerca);
	}
	
	/**
	 * Ritorna la lista di Azioni di una  Porta Applicativa
	 */
	public List<PortaApplicativaAzione> porteAppAzioneList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppAzioneList(idPortaApplicativa, ricerca);
	}

	public List<ServizioApplicativo> porteAppServizioApplicativoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppServizioApplicativoList(idPortaApplicativa, ricerca);
	}
	
	public List<PortaApplicativaAutorizzazioneSoggetto> porteAppSoggettoList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppSoggettoList(idPortaApplicativa, ricerca);
	}
	
	public List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppServiziApplicativiAutorizzatiList(idPortaApplicativa, ricerca);
	}
	
	public List<PortaApplicativaAutorizzazioneServizioApplicativo> porteAppServiziApplicativiAutorizzatiTokenList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppServiziApplicativiAutorizzatiTokenList(idPortaApplicativa, ricerca);
	}
	
	public List<MessageSecurityFlowParameter> porteAppMessageSecurityRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppMessageSecurityRequestList(idPortaApplicativa, ricerca);
	}

	public List<MessageSecurityFlowParameter> porteAppMessageSecurityResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteAppMessageSecurityResponseList(idPortaApplicativa, ricerca);
	}
	
	public List<PortaApplicativa> porteAppWithServizio(long idSoggettoErogatore, String tipoServizio,String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.porteAppWithServizio(idSoggettoErogatore, tipoServizio, nomeServizio, versioneServizio);
	}

	public List<PortaApplicativa> porteAppWithIdServizio(long idServizio) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.porteAppWithIdServizio(idServizio);
	}
	
	public List<CorrelazioneApplicativaElemento> porteApplicativeCorrelazioneApplicativaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeCorrelazioneApplicativaList(idPortaApplicativa, ricerca);
	}

	public List<CorrelazioneApplicativaRispostaElemento> porteApplicativeCorrelazioneApplicativaRispostaList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeCorrelazioneApplicativaRispostaList(idPortaApplicativa, ricerca);
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
		return this.porteApplicativeDriver.existsPortaApplicativa(idPA);
	}

	public List<PortaApplicativa> getPorteApplicativeWithServizio(Long idServizio, String tiposervizio, String nomeservizio, Integer versioneServizio,
			Long idSoggetto, String tiposoggetto, String nomesoggetto) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.getPorteApplicativeWithServizio(idServizio, tiposervizio, nomeservizio, versioneServizio,
				idSoggetto, tiposoggetto, nomesoggetto);
	}

	public PortaApplicativa getPortaApplicativaWithSoggettoAndServizio(String nome, Long idSoggetto, Long idServizio, 
			String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.getPortaApplicativaWithSoggettoAndServizio(nome, idSoggetto, idServizio, 
				tipoServizio, nomeServizio, versioneServizio);
	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public List<IDPortaApplicativa> getPortaApplicativaAzione(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getPortaApplicativaAzione(nome);
	}

	// NOTA: Metodo non sicuro!!! Possono esistere piu' azioni di port type diversi o accordi diversi !!!!!
	public boolean existsPortaApplicativaAzione(String nome) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.existsPortaApplicativaAzione(nome);
	}
	
	public List<PortaApplicativa> serviziPorteAppList(String tipoServizio,String nomeServizio, Integer versioneServizio,
			long idServizio, long idSoggettoErogatore, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.serviziPorteAppList(tipoServizio, nomeServizio, versioneServizio,
				idServizio, idSoggettoErogatore, ricerca);
	}
	
	public List<PortaApplicativa> getPorteApplicativaByIdProprietario(long idProprietario) throws DriverConfigurazioneException{
		return this.porteApplicativeDriver.getPorteApplicativaByIdProprietario(idProprietario);
	}

	/**
	 * Recupera tutte le porte applicative relative al servizio idServizio, erogato dal soggetto erogatore del servizio.
	 * @param idSE
	 * @return List<PortaApplicativa>
	 * @throws DriverConfigurazioneException
	 */
	public List<PortaApplicativa> getPorteApplicative(IDServizio idSE) throws DriverConfigurazioneException{
		return this.porteApplicativeDriver.getPorteApplicative(idSE);
	}

	public List<PortaApplicativa> getPorteApplicativeBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.getPorteApplicativeBySoggetto(idSoggetto);
	}
	
	public List<PortaApplicativa> getPorteApplicativeBySoggettoVirtuale(IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.getPorteApplicativeBySoggettoVirtuale(soggettoVirtuale);
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
	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.porteApplicativeDriver.getAllIdPorteApplicative(filtroRicerca);
	}

	@Override
	public List<IDConnettore> getConnettoriConsegnaNotifichePrioritarie(String queueName) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.porteApplicativeDriver.getConnettoriConsegnaNotifichePrioritarie(queueName);
	}
	
	public int resetConnettoriConsegnaNotifichePrioritarie(String queueName) throws DriverConfigurazioneException {
		return this.porteApplicativeDriver.resetConnettoriConsegnaNotifichePrioritarie(queueName);
	}
	
	public List<MtomProcessorFlowParameter> porteApplicativeMTOMRequestList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeMTOMRequestList(idPortaApplicativa, ricerca);
	}

	public List<MtomProcessorFlowParameter> porteApplicativeMTOMResponseList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeMTOMResponseList(idPortaApplicativa, ricerca);
	}
	
	public List<PortaApplicativa> getPorteApplicativeByPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		return this.porteApplicativeDriver.getPorteApplicativeByPolicyGestioneToken(nome);
	}
	
	/**
	 * Ritorna la lista di proprieta per la configurazione custom dei connettori multipli di una Porta Applicativa
	 */
	public List<Proprieta> porteApplicativeConnettoriMultipliConfigPropList(long idPortaApplicativa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeConnettoriMultipliConfigPropList(idPortaApplicativa, ricerca);
	}
	
	/**
	 * Ritorna la lista di proprieta di un connettore multiplo di una Porta Applicativa 
	 */
	public List<Proprieta> porteApplicativeConnettoriMultipliPropList(long idPaSa, ISearch ricerca) throws DriverConfigurazioneException {
		return this.porteApplicativeSearchDriver.porteApplicativeConnettoriMultipliPropList(idPaSa, ricerca);
	}

	public MappingErogazionePortaApplicativa getMappingErogazione(IDServizio idServizio, IDPortaApplicativa idPortaApplicativa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.porteApplicativeDriver.getMappingErogazione(idServizio, idPortaApplicativa);
	}
	
	public static List<String> normalizeConnettoriMultpliById(List<String> sa, PortaApplicativa pa){
		return DriverConfigurazioneDB_porteApplicativeDriver.normalizeConnettoriMultpliById(sa, pa);
	}
	
	
	
	
	
	// *** Servizi Applicativi ***

    @Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativo(idServizioApplicativo);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiBasic(aUser, aPassword, config);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiApiKey(aUser, aPassword, appId, config);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiToken(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiToken(tokenPolicy, tokenClientId, tokenWithHttpsEnabled);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String aSubject, String aIssuer) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiSsl(aSubject, aIssuer);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiSsl(certificate, strictVerifier);
    }
    @Override
    public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String principal) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	return this.serviziApplicativiDriver.getServizioApplicativoByCredenzialiPrincipal(principal);
    }
    public ServizioApplicativo getServizioApplicativo(long idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
    	return this.serviziApplicativiDriver.getServizioApplicativo(idServizioApplicativo);
    }
    
	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
		this.serviziApplicativiDriver.createServizioApplicativo(aSA);
	}

	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
		this.serviziApplicativiDriver.updateServizioApplicativo(aSA);
	}

	/**
	 * 
	 * @param aSA
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteServizioApplicativo(ServizioApplicativo aSA) throws DriverConfigurazioneException {
		this.serviziApplicativiDriver.deleteServizioApplicativo(aSA);
	}
	
	/**
	 * Recupera tutti i servizi applicativi in base ai parametri di ricerca passati
	 */
	public List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.servizioApplicativoList(ricerca);
	}

	public List<ServizioApplicativo> servizioApplicativoList(IDSoggetto idSO,ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.servizioApplicativoList(idSO, ricerca);
	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiBasicList(String utente, String password, boolean checkPassword) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiBasicList(utente, password, checkPassword);
	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiApiKeyList(String utente, boolean appId) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiApiKeyList(utente, appId);
	}

	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(String subject, String issuer) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiSslList(subject, issuer);
	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiSslList(certificate, strictVerifier);
	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiPrincipalList(String principal) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiPrincipalList(principal);
	}
	
	public List<ServizioApplicativo> servizioApplicativoWithCredenzialiTokenList(String tokenPolicy, String tokenClientId, boolean tokenWithHttpsEnabled) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.servizioApplicativoWithCredenzialiTokenList(tokenPolicy, tokenClientId, tokenWithHttpsEnabled);
	}
	
	public String[] soggettiServizioApplicativoList(long idSoggetto) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.soggettiServizioApplicativoList(idSoggetto);
	}

	public List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipoSA, 
			boolean bothSslAndToken, String tokenPolicy) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.soggettiServizioApplicativoList(idSoggetto, superuser, credenziale, appId, tipoSA, 
				bothSslAndToken, tokenPolicy);
	}
	public List<IDServizioApplicativoDB> soggettiServizioApplicativoList(IDSoggetto idSoggetto,String superuser,CredenzialeTipo credenziale, Boolean appId, String tipoSA, 
			boolean bothSslAndToken, String tokenPolicy, boolean tokenPolicyOR) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.soggettiServizioApplicativoList(idSoggetto, superuser, credenziale, appId, tipoSA, 
				bothSslAndToken, tokenPolicy, tokenPolicyOR);
	}
	
	public List<ServizioApplicativo> soggettiServizioApplicativoList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.soggettiServizioApplicativoList(superuser, ricerca);
	}

	public List<ServizioApplicativo> soggettiServizioApplicativoList(Long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.soggettiServizioApplicativoList(idSoggetto, ricerca);
	}
	
	public List<String> servizioApplicativoRuoliList(long idSA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.servizioApplicativoRuoliList(idSA, ricerca);
	}
	
	public List<Proprieta> serviziApplicativiProprietaList(int idSA, ISearch ricerca) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.serviziApplicativiProprietaList(idSA, ricerca);
	}
	
	/**
	 * Ritorna la lista di nomi delle proprieta registrate
	 */
	public List<String> nomiProprietaSA(String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.nomiProprietaSA(filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo);
	}
	
	public List<IDServizioApplicativoDB> getIdServiziApplicativiWithIdErogatore(Long idErogatore, String tipo, 
			boolean checkIM, boolean checkConnettoreAbilitato) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.getIdServiziApplicativiWithIdErogatore(idErogatore, tipo, 
				checkIM, checkConnettoreAbilitato);
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
		return this.serviziApplicativiDriver.existsServizioApplicativo(idServizioApplicativo);
	}

	public long getIdServizioApplicativo(IDSoggetto idSoggetto, String nomeServizioApplicativo) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.getIdServizioApplicativo(idSoggetto, nomeServizioApplicativo);
	}

	public boolean existsServizioApplicativoSoggetto(Long idSoggetto) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.existsServizioApplicativoSoggetto(idSoggetto);
	}
	
	public boolean isServizioApplicativoInUsoComeErogatore(ServizioApplicativo sa, Map<ErrorsHandlerCostant, String> whereIsInUso) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.isServizioApplicativoInUsoComeErogatore(sa, whereIsInUso);
	}
	
	// all
	public int countTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieServiziApplicativi(filters);
	}
	public int countTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieServiziApplicativi(proprietario, filters);
	}

	// fruizione

	public int countTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(filters);
	}
	public int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(filters, checkAssociazionePorta);
	}
	public int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(filters, checkAssociazionePorta, isBound);
	}

	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(fruizione, filters);
	}
	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(fruizione, filters, checkAssociazionePorta);
	}
	public int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(fruizione, filters, checkAssociazionePorta, isBound);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, filters);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, filters, checkAssociazionePorta);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, filters, checkAssociazionePorta, isBound);
	}

	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters, checkAssociazionePorta);
	}
	public int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters, checkAssociazionePorta, isBound);
	}

	// erogazione

	public int countTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(filters);
	}
	public int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(filters, checkAssociazionePorta);
	}
	public int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(filters, checkAssociazionePorta, isBound);
	}
	
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(erogazione, filters);
	}
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(erogazione, filters, checkAssociazionePorta);
	}
	public int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(erogazione, filters, checkAssociazionePorta, isBound);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters, checkAssociazionePorta);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters, checkAssociazionePorta, isBound);
	}

	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, filters);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, filters, checkAssociazionePorta);
	}
	public int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.countTipologieErogazioneServiziApplicativi(proprietario, filters, checkAssociazionePorta, isBound);
	}

	// all
	public List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieServiziApplicativi(filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieServiziApplicativi(proprietario, filters);
	}

	// fruizione

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(fruizione, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(fruizione, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(fruizione, filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieFruizioneServiziApplicativi(proprietario, fruizione, filters, checkAssociazionePorta, isBound);
	}

	// erogazione

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(erogazione, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(erogazione, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(erogazione, filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, erogazione, filters, checkAssociazionePorta, isBound);
	}

	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, filters);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, filters, checkAssociazionePorta);
	}
	public List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return this.serviziApplicativiSearchDriver.getTipologieErogazioneServiziApplicativi(proprietario, filters, checkAssociazionePorta, isBound);
	}

	/**
	 * Recupera i servizi applicativi che hanno come tipologia di erogazione una tra quelle passate come parametro
	 * @param filters
	 * @param proprietario
	 * @return List<IDServizioApplicativo>
	 * @throws DriverConfigurazioneException
	 */
	public List<IDServizioApplicativo> serviziApplicativiList(ISearch filters, IDSoggetto proprietario, TipologiaErogazione... erogazione) throws DriverConfigurazioneException {
		return this.serviziApplicativiSearchDriver.serviziApplicativiList(filters, proprietario, erogazione);
	}
	
	public List<ServizioApplicativo> getServiziApplicativiBySoggetto(long idSoggetto) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.getServiziApplicativiBySoggetto(idSoggetto);
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
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.serviziApplicativiDriver.getAllIdServiziApplicativi(filtroRicerca);
	}
	
	public long getIdServizioApplicativoByConnettore(long idConnettore) throws DriverConfigurazioneException {
		return this.serviziApplicativiDriver.getIdServizioApplicativoByConnettore(idConnettore);
	}
	
	public IDServizio getLabelNomeServizioApplicativo(String nomeServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.serviziApplicativiDriver.getLabelNomeServizioApplicativo(nomeServizioApplicativo);
	}
	
	

	// *** Protocol Properties ***

	public boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {
		return this.protocolPropertiesDriver.existsProtocolProperty(proprietarioProtocolProperty, idProprietario, nome);
	}

	public ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverConfigurazioneException {
		return this.protocolPropertiesDriver.getProtocolProperty(proprietarioProtocolProperty, idProprietario, nome);
	}

	public ProtocolProperty getProtocolProperty(long idProtocolProperty) throws DriverConfigurazioneException {
		return this.protocolPropertiesDriver.getProtocolProperty(idProtocolProperty);
	}
	
	

	
	// *** Routing Table ***
	
	/**
	 * Restituisce la RoutingTable definita nella Porta di Dominio
	 * 
	 * @return RoutingTable
	 * 
	 */
	@Override
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException {
		return this.routingTableDriver.getRoutingTable();
	}

	@Override
	public void createRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		this.routingTableDriver.createRoutingTable(routingTable);
	}

	@Override
	public void updateRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		this.routingTableDriver.updateRoutingTable(routingTable);
	}

	@Override
	public void deleteRoutingTable(RoutingTable routingTable) throws DriverConfigurazioneException {
		this.routingTableDriver.deleteRoutingTable(routingTable);
	}

	public List<RoutingTableDestinazione> routingList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.routingTableDriver.routingList(ricerca); 
	}

	
	// *** Configurazione (Accesso Registro) ***
	
	/**
	 * Restituisce l'accesso al registro definito nella Porta di Dominio
	 * 
	 * @return AccessoRegistro
	 * 
	 */
	@Override
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoRegistro();
	}

	/**
	 * Crea le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */

	@Override
	public void createAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.createAccessoRegistro(registro);
	}

	/**
	 * Aggiorna le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.updateAccessoRegistro(registro);
	}

	/**
	 * Elimina le informazioni per l'accesso ai registri
	 * 
	 * @param registro
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteAccessoRegistro(AccessoRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.deleteAccessoRegistro(registro);
	}
	
	public void createAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.createAccessoRegistro(registro);
	}

	public void updateAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.updateAccessoRegistro(registro);
	}

	public void deleteAccessoRegistro(AccessoRegistroRegistro registro) throws DriverConfigurazioneException {
		this.configDriver.deleteAccessoRegistro(registro);
	}

	public List<AccessoRegistroRegistro> registriList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.registriList(ricerca);
	}
	
	
	// *** Configurazione (Accesso Configurazione) ***
	
	@Override
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoConfigurazione();
	}

	public void createAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		this.configDriver.createAccessoConfigurazione(accessoConfigurazione);
	}

	public void updateAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		this.configDriver.updateAccessoConfigurazione(accessoConfigurazione);
	}

	public void deleteAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) throws DriverConfigurazioneException {
		this.configDriver.deleteAccessoConfigurazione(accessoConfigurazione);
	}

	
	// *** Configurazione (Accesso Dati Autorizzazione) ***	
	
	@Override
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiAutorizzazione();
	}
	
	public void createAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		this.configDriver.createAccessoDatiAutorizzazione(accessoDatiAutorizzazione);
	}

	public void updateAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		this.configDriver.updateAccessoDatiAutorizzazione(accessoDatiAutorizzazione);
	}

	public void deleteAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) throws DriverConfigurazioneException {
		this.configDriver.deleteAccessoDatiAutorizzazione(accessoDatiAutorizzazione);
	}

	
	// *** Configurazione (Accesso Dati Autenticazione) ***	
		
	@Override
	public AccessoDatiAutenticazione getAccessoDatiAutenticazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiAutenticazione();
	}

	
	// *** Configurazione (Accesso Dati GestioneToke) ***	
		
	@Override
	public AccessoDatiGestioneToken getAccessoDatiGestioneToken() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiGestioneToken();
	}

	
	// *** Configurazione (Accesso Dati Keystore) ***	
	
	@Override
	public AccessoDatiKeystore getAccessoDatiKeystore() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiKeystore();
	}

	
	// *** Configurazione (Accesso Dati Load Balancer) ***	
		
	@Override
	public AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiConsegnaApplicativi();
	}

	
	// *** Configurazione (Accesso Dati Richieste) ***	
			
	@Override
	public AccessoDatiRichieste getAccessoDatiRichieste() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getAccessoDatiRichieste();
	}

	
	// *** Configurazione (Gestione Errore) ***	

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.gestioneErroreDriver.getGestioneErroreComponenteCooperazione();
	}

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.gestioneErroreDriver.getGestioneErroreComponenteIntegrazione();
	}
	
	/**
	 * Crea le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.createGestioneErroreComponenteCooperazione(gestione);
	}

	/**
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.updateGestioneErroreComponenteCooperazione(gestione);
	}

	/**
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.deleteGestioneErroreComponenteCooperazione(gestione);
	}

	/**
	 * Crea le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.createGestioneErroreComponenteIntegrazione(gestione);
	}

	/**
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.updateGestioneErroreComponenteIntegrazione(gestione);
	}

	/**
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		this.gestioneErroreDriver.deleteGestioneErroreComponenteIntegrazione(gestione);
	}

	
	// *** Configurazione (Stato Servizi) ***	

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configDriver.getStatoServiziPdD();
	}

	/**
	 * Crea le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configDriver.createStatoServiziPdD(servizi);
	}

	/**
	 * Aggiorna le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configDriver.updateStatoServiziPdD(servizi);
	}


	/**
	 * Elimina le informazioni sui servizi attivi della PdD
	 * 
	 * @param servizi
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configDriver.deleteStatoServiziPdD(servizi);
	}

	
	// *** Configurazione (Proprieta' Sistema) ***	

	public List<Property> systemPropertyList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.systemPropertyList(ricerca);
	}

	/**
	 * Restituisce le proprieta' di sistema utilizzate dalla PdD
	 *
	 * @return proprieta' di sistema
	 * 
	 */
	@Override
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configDriver.getSystemPropertiesPdD();
	}
	
	/**
	 * Crea le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configDriver.createSystemPropertiesPdD(systemProperties);
	}

	/**
	 * Aggiorna le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configDriver.updateSystemPropertiesPdD(systemProperties);
	}

	/**
	 * Elimina le informazioni sulle proprieta' di sistema utilizzate dalla PdD
	 * 
	 * @param systemProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configDriver.deleteSystemPropertiesPdD(systemProperties);
	}

	
	// *** Configurazione (URL Invocazione) ***
	
	@Override
	public ConfigurazioneUrlInvocazioneRegola getUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configDriver.getUrlInvocazioneRegola(nome);
	}
	
	public boolean existsUrlInvocazioneRegola(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configDriver.existsUrlInvocazioneRegola(nome);
	}

	@Override
	public void createUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		this.configDriver.createUrlInvocazioneRegola(regola);
	}

	@Override
	public void updateUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		this.configDriver.updateUrlInvocazioneRegola(regola);
	}

	@Override
	public void deleteUrlInvocazioneRegola(ConfigurazioneUrlInvocazioneRegola regola) throws DriverConfigurazioneException{
		this.configDriver.deleteUrlInvocazioneRegola(regola);
	}
		
	public List<ConfigurazioneUrlInvocazioneRegola> proxyPassConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.proxyPassConfigurazioneRegolaList(ricerca);
	}
	
	public boolean existsProxyPassConfigurazioneRegola(String nome) throws DriverConfigurazioneException {
		return this.configDriver.existsProxyPassConfigurazioneRegola(nome);
	}

	
	// *** Configurazione (Response Cache) ***
	
	public List<ResponseCachingConfigurazioneRegola> responseCachingConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.responseCachingConfigurazioneRegolaList(ricerca);
	}
	
	public boolean existsResponseCachingConfigurazioneRegola(Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException {
		return this.configDriver.existsResponseCachingConfigurazioneRegola(statusMin, statusMax, fault);
	}
	
	
	
	// *** Configurazione (Canali) ***
	
	public List<CanaleConfigurazione> canaleConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.canaleConfigurazioneList(ricerca);
	}
	
	public boolean existsCanale(String nome) throws DriverConfigurazioneException {
		return this.configDriver.existsCanale(nome);
	}
	public List<CanaleConfigurazioneNodo> canaleNodoConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.configSearchDriver.canaleNodoConfigurazioneList(ricerca);
	}
	public boolean existsCanaleNodo(String nome) throws DriverConfigurazioneException {
		return this.configDriver.existsCanaleNodo(nome);
	}
	
	/**
	 * Restituisce la configurazione dei canali
	 * 
	 * @return Configurazione
	 * 
	 */
	@Override
	public CanaliConfigurazione getCanaliConfigurazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configDriver.getCanaliConfigurazione();
	}
	public CanaliConfigurazione getCanaliConfigurazione(boolean readNodi) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configDriver.getCanaliConfigurazione(readNodi);
	}
	
	
	
	// *** Configurazione ***
	
	/**
	 * Restituisce la configurazione generale della Porta di Dominio
	 * 
	 * @return Configurazione
	 * 
	 */
	@Override
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configDriver.getConfigurazioneGenerale();
	}

	public Object getConfigurazioneExtended(Configurazione config, String idExtendedConfiguration) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.configDriver.getConfigurazioneExtended(config, idExtendedConfiguration);
	}
	
	@Override
	public void createConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		this.configDriver.createConfigurazione(configurazione);
	}

	@Override
	public void updateConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		this.configDriver.updateConfigurazione(configurazione);
	}

	@Override
	public void deleteConfigurazione(Configurazione configurazione) throws DriverConfigurazioneException {
		this.configDriver.deleteConfigurazione(configurazione);
	}
	
	
	
	// *** Generic Properties ***	
		
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.genericPropertiesDriver.getGenericProperties();
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.genericPropertiesDriver.getGenericProperties(tipologia);
	}
	
	@Override
	public GenericProperties getGenericProperties(String tipologia, String name) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.genericPropertiesDriver.getGenericProperties(tipologia, name);
	}
	
	/**
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	public List<GenericProperties> getGenericProperties(List<String> tipologia, Integer idLista, ISearch ricerca, boolean throwNotFoundException) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.genericPropertiesDriver.getGenericProperties(tipologia, idLista, ricerca, throwNotFoundException);
	}

	public GenericProperties getGenericProperties(long idGenericProperties) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.genericPropertiesDriver.getGenericProperties(idGenericProperties);
	}

	/**
	 * Crea una proprieta' generica della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void createGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		this.genericPropertiesDriver.createGenericProperties(genericProperties);
	}

	/**
	 * Aggiorna le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void updateGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		this.genericPropertiesDriver.updateGenericProperties(genericProperties);
	}


	/**
	 * Elimina le informazioni sulle proprieta' generiche della PdD
	 * 
	 * @param genericProperties
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public void deleteGenericProperties(GenericProperties genericProperties) throws DriverConfigurazioneException{
		this.genericPropertiesDriver.deleteGenericProperties(genericProperties);
	}

	
	
	
	// *** Plugins ***	
	
	@Override
	public RegistroPlugins getRegistroPlugins() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getRegistroPlugins();
	}
	
	@Override
	public RegistroPlugin getRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getRegistroPlugin(nome);
	}
	@Override
	public RegistroPlugin getDatiRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getDatiRegistroPlugin(nome);
	}
	
	public RegistroPlugin getRegistroPlugin(Connection conParam, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getRegistroPlugin(conParam, nome);
	}
	public RegistroPlugin getDatiRegistroPlugin(Connection conParam, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getDatiRegistroPlugin(conParam, nome);
	}
		
	public RegistroPlugin getRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getRegistroPluginFromPosizione(posizione);
	}
	public RegistroPlugin getDatiRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getDatiRegistroPluginFromPosizione(posizione);
	}
		
	public boolean existsRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.existsRegistroPlugin(nome);
	}
	
	public int getMaxPosizioneRegistroPlugin() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getMaxPosizioneRegistroPlugin();
	}
	
	public int getNumeroArchiviJarRegistroPlugin(String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getNumeroArchiviJarRegistroPlugin(nome);
	}
	
	@Override
	public void createRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.createRegistroPlugin(plugin);
	}
	
	@Override
	public void updateRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.updateRegistroPlugin(plugin);
	}
	
	@Override
	public void deleteRegistroPlugin(RegistroPlugin plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.deleteRegistroPlugin(plugin);
	}
	
	@Override
	public void updateDatiRegistroPlugin(String nomePlugin, RegistroPlugin plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.updateDatiRegistroPlugin(nomePlugin, plugin);
	}
	
	@Override
	public RegistroPluginArchivio getRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.getRegistroPluginArchivio(nomePlugin, nome);
	}
	
	public boolean existsRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.existsRegistroPluginArchivio(nomePlugin, nome);
	}
	
	public boolean existsRegistroPluginArchivio(String nomePlugin, PluginSorgenteArchivio tipoSorgente, String sorgente) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.pluginsDriver.existsRegistroPluginArchivio(nomePlugin, tipoSorgente, sorgente);
	}
	
	@Override
	public void createRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.createRegistroPluginArchivio(nomePlugin, plugin);
	}
	
	@Override
	public void updateRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.updateRegistroPluginArchivio(nomePlugin, plugin);
	}
	
	@Override
	public void deleteRegistroPluginArchivio(String nomePlugin, RegistroPluginArchivio plugin) throws DriverConfigurazioneException{
		this.pluginsDriver.deleteRegistroPluginArchivio(nomePlugin, plugin);
	}
		
	public List<RegistroPlugin> pluginsArchiviList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.pluginsDriver.pluginsArchiviList(ricerca);
	}
	public List<RegistroPluginArchivio> pluginsArchiviJarList(String nome, ISearch ricerca) throws DriverConfigurazioneException {
		return this.pluginsDriver.pluginsArchiviJarList(nome, ricerca);
	}

	
	
	// *** Allarmi ***	
	
	public long countAllarmi(String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.allarmiDriver.countAllarmi(tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
				listSoggettiProprietariAbilitati, listIDServizioAbilitati,
				tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
				idSoggettoProprietario, listIDServizio);
	}
	
	public List<Allarme> findAllAllarmi(String tipologiaRicerca, Boolean enabled, StatoAllarme stato, Boolean acknowledged, String nomeAllarme,
			List<IDSoggetto> listSoggettiProprietariAbilitati, List<IDServizio> listIDServizioAbilitati,
			List<String> tipoSoggettiByProtocollo, List<String> tipoServiziByProtocollo, 
			IDSoggetto idSoggettoProprietario, List<IDServizio> listIDServizio,
			Integer offset, Integer limit) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.allarmiDriver.findAllAllarmi(tipologiaRicerca, enabled, stato, acknowledged, nomeAllarme,
				listSoggettiProprietariAbilitati, listIDServizioAbilitati,
				tipoSoggettiByProtocollo, tipoServiziByProtocollo, 
				idSoggettoProprietario, listIDServizio,
				offset, limit);
	}
	
	
	
	
	// *** Reset ***	
	
	/**
	 * Reset delle tabelle del db gestito da questo driver
	 */
	@Override
	public void reset() throws DriverConfigurazioneException {
		this.utilsDriver.reset();
	}
	@Override
	public void reset(boolean resetConfigurazione) throws DriverConfigurazioneException{
		this.utilsDriver.reset(resetConfigurazione);
	}

	/**
	 * Reset delle tabelle del db govwayConsole gestito da questo driver
	 */
	public void resetCtrlstat() throws DriverConfigurazioneException {
		this.utilsDriver.resetCtrlstat();
	}

	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverConfigurazioneException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		this.utilsDriver.isAlive();
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


}
