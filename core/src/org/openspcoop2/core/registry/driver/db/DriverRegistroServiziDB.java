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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.byok.IDriverBYOKConfig;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.ProprietariProtocolProperty;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoAzione;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaAzioni;
import org.openspcoop2.core.registry.driver.FiltroRicercaFruizioniServizio;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.FiltroRicercaOperations;
import org.openspcoop2.core.registry.driver.FiltroRicercaPortTypes;
import org.openspcoop2.core.registry.driver.FiltroRicercaResources;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziCRUD;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCObject;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

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
IDriverWS ,IMonitoraggioRisorsa, IDriverBYOKConfig{

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

	/** Tabella soggetti */
	protected String tabellaSoggetti = CostantiDB.SOGGETTI;

	/** Logger utilizzato per info. */
	protected Logger log = null;
	void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}
	void logDebug(String msg, Exception e) {
		if(this.log!=null) {
			this.log.debug(msg,e);
		}
	}

	// Tipo database passato al momento della creazione dell'oggetto
	protected String tipoDB = null;

	public String getTipoDB() {
		return this.tipoDB;
	}

	protected boolean useSuperUser = true;
	public boolean isUseSuperUser() {
		return this.useSuperUser;
	}
	public void setUseSuperUser(boolean useSuperUser) {
		this.useSuperUser = useSuperUser;
	}
	

	// Factory
	protected IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	protected IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();

	
	// Driver
	
	private DriverRegistroServiziDB_accordiCooperazioneDriver accordiCooperazioneDriver = null;
	
	private DriverRegistroServiziDB_accordiDriver accordiDriver = null;
	private DriverRegistroServiziDB_accordiSinteticiDriver accordiSinteticiDriver = null;
	private DriverRegistroServiziDB_accordiSoapDriver accordiSoapDriver = null;
	private DriverRegistroServiziDB_accordiRestDriver accordiRestDriver = null;
	private DriverRegistroServiziDB_accordiFindAllDriver accordiFindAllDriver = null;
	private DriverRegistroServiziDB_accordiExistsDriver accordiExistsDriver = null;
	private DriverRegistroServiziDB_accordiServiziCompostiDriver accordiServiziCompostiDriver = null;
	
	private DriverRegistroServiziDB_pddDriver pddDriver = null;
	
	private DriverRegistroServiziDB_gruppiDriver gruppiDriver = null;
	
	private DriverRegistroServiziDB_ruoliDriver ruoliDriver = null;
	
	private DriverRegistroServiziDB_scopeDriver scopeDriver = null;
	
	private DriverRegistroServiziDB_soggettiDriver soggettiDriver = null;
	private DriverRegistroServiziDB_soggettiCredenzialiDriver soggettiCredenzialiDriver = null;
	private DriverRegistroServiziDB_soggettiSearchDriver soggettiSearchDriver = null;
	
	private DriverRegistroServiziDB_accordiParteSpecificaDriver accordiParteSpecificaDriver = null;
	private DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver accordiParteSpecificaFruitoreDriver = null;
	private DriverRegistroServiziDB_accordiParteSpecificaSearchDriver accordiParteSpecificaSearchDriver = null;
	
	private DriverRegistroServiziDB_connettoriDriver connettoriDriver = null;

	private DriverRegistroServiziDB_documentiDriver documentiDriver = null;
	
	private DriverRegistroServiziDB_protocolPropertiesDriver protocolPropertiesDriver = null;
	
	private DriverRegistroServiziDB_utilsDriver utilsDriver = null;



	

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
		
		// Driver
		
		this.accordiCooperazioneDriver = new DriverRegistroServiziDB_accordiCooperazioneDriver(this);
		
		this.accordiDriver = new DriverRegistroServiziDB_accordiDriver(this);
		this.accordiSinteticiDriver = new DriverRegistroServiziDB_accordiSinteticiDriver(this);
		this.accordiSoapDriver = new DriverRegistroServiziDB_accordiSoapDriver(this);
		this.accordiRestDriver = new DriverRegistroServiziDB_accordiRestDriver(this);
		this.accordiFindAllDriver = new DriverRegistroServiziDB_accordiFindAllDriver(this);
		this.accordiExistsDriver = new DriverRegistroServiziDB_accordiExistsDriver(this);
		this.accordiServiziCompostiDriver = new DriverRegistroServiziDB_accordiServiziCompostiDriver(this);
		
		this.pddDriver = new DriverRegistroServiziDB_pddDriver(this);
		
		this.gruppiDriver = new DriverRegistroServiziDB_gruppiDriver(this);
		
		this.ruoliDriver = new DriverRegistroServiziDB_ruoliDriver(this);
		
		this.scopeDriver = new DriverRegistroServiziDB_scopeDriver(this);
		
		this.soggettiDriver = new DriverRegistroServiziDB_soggettiDriver(this);
		this.soggettiCredenzialiDriver = new DriverRegistroServiziDB_soggettiCredenzialiDriver(this);
		this.soggettiSearchDriver = new DriverRegistroServiziDB_soggettiSearchDriver(this);
		
		this.accordiParteSpecificaDriver = new DriverRegistroServiziDB_accordiParteSpecificaDriver(this);
		this.accordiParteSpecificaFruitoreDriver = new DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver(this);
		this.accordiParteSpecificaSearchDriver = new DriverRegistroServiziDB_accordiParteSpecificaSearchDriver(this);
		
		this.connettoriDriver = new DriverRegistroServiziDB_connettoriDriver(this);
		
		this.documentiDriver = new DriverRegistroServiziDB_documentiDriver(this);
		
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(this);
		
		this.utilsDriver = new DriverRegistroServiziDB_utilsDriver(this);

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

		// Driver
		
		this.accordiCooperazioneDriver = new DriverRegistroServiziDB_accordiCooperazioneDriver(this);
		
		this.accordiDriver = new DriverRegistroServiziDB_accordiDriver(this);
		this.accordiSinteticiDriver = new DriverRegistroServiziDB_accordiSinteticiDriver(this);
		this.accordiSoapDriver = new DriverRegistroServiziDB_accordiSoapDriver(this);
		this.accordiRestDriver = new DriverRegistroServiziDB_accordiRestDriver(this);
		this.accordiFindAllDriver = new DriverRegistroServiziDB_accordiFindAllDriver(this);
		this.accordiExistsDriver = new DriverRegistroServiziDB_accordiExistsDriver(this);
		this.accordiServiziCompostiDriver = new DriverRegistroServiziDB_accordiServiziCompostiDriver(this);
		
		this.pddDriver = new DriverRegistroServiziDB_pddDriver(this);
		
		this.gruppiDriver = new DriverRegistroServiziDB_gruppiDriver(this);
		
		this.ruoliDriver = new DriverRegistroServiziDB_ruoliDriver(this);
		
		this.scopeDriver = new DriverRegistroServiziDB_scopeDriver(this);
		
		this.soggettiDriver = new DriverRegistroServiziDB_soggettiDriver(this);
		this.soggettiCredenzialiDriver = new DriverRegistroServiziDB_soggettiCredenzialiDriver(this);
		this.soggettiSearchDriver = new DriverRegistroServiziDB_soggettiSearchDriver(this);
		
		this.accordiParteSpecificaDriver = new DriverRegistroServiziDB_accordiParteSpecificaDriver(this);
		this.accordiParteSpecificaFruitoreDriver = new DriverRegistroServiziDB_accordiParteSpecificaFruitoreDriver(this);
		this.accordiParteSpecificaSearchDriver = new DriverRegistroServiziDB_accordiParteSpecificaSearchDriver(this);
		
		this.connettoriDriver = new DriverRegistroServiziDB_connettoriDriver(this);
		
		this.documentiDriver = new DriverRegistroServiziDB_documentiDriver(this);
		
		this.protocolPropertiesDriver = new DriverRegistroServiziDB_protocolPropertiesDriver(this);
		
		this.utilsDriver = new DriverRegistroServiziDB_utilsDriver(this);
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


	Connection getConnectionFromDatasource(String methodName) throws Exception{
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
					if(con!=null) {
						con.close();
					}
				}
			} catch (Exception e) {
				// ignore
			}

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
	
	
	
	
	// *** BYOK ***
	
	private IDriverBYOK driverBYOK = null;
	private boolean wrapBYOK;
	private boolean unwrapBYOK;
	
	@Override
	public void initialize(IDriverBYOK driver, 
			boolean wrap, boolean unwrap) throws UtilsException{
		this.driverBYOK = driver;
		this.wrapBYOK = wrap;
		this.unwrapBYOK = unwrap;
	}

	public IDriverBYOK getDriverWrapBYOK() {
		return this.wrapBYOK ? this.driverBYOK : null;
	}
	public IDriverBYOK getDriverUnwrapBYOK() {
		return this.unwrapBYOK ? this.driverBYOK : null;
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
		return this.accordiCooperazioneDriver.getAccordoCooperazione(idAccordo);
	}
	public org.openspcoop2.core.registry.AccordoCooperazione getAccordoCooperazione(IDAccordoCooperazione idAccordo,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiCooperazioneDriver.getAccordoCooperazione(idAccordo,readContenutoAllegati);
	}

	public IDAccordoCooperazione getIdAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiCooperazioneDriver.getIdAccordoCooperazione(id);
	}
	public IDAccordoCooperazione getIdAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiCooperazioneDriver.getIdAccordoCooperazione(id, conParam);
	}

	public AccordoCooperazione getAccordoCooperazione(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiCooperazioneDriver.getAccordoCooperazione(id);
	}
	public AccordoCooperazione getAccordoCooperazione(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiCooperazioneDriver.getAccordoCooperazione(id,conParam);
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
		return this.accordiCooperazioneDriver.getAllIdAccordiCooperazione(filtroRicerca);
	}

	/**
	 * Crea un nuovo AccordoCooperazione
	 * 
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		this.accordiCooperazioneDriver.createAccordoCooperazione(accordoCooperazione);
	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 *
	 * @param idAccordo dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 * @throws DriverRegistroServiziException
	 */    
	@Override
	public boolean existsAccordoCooperazione(IDAccordoCooperazione idAccordo) throws DriverRegistroServiziException{
		return this.accordiCooperazioneDriver.existsAccordoCooperazione(idAccordo);
	}

	/**
	 * Aggiorna l'AccordoCooperazione con i nuovi valori.
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		this.accordiCooperazioneDriver.updateAccordoCooperazione(accordoCooperazione);
	}

	/**
	 * Elimina un AccordoCooperazione 
	 *  
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		this.accordiCooperazioneDriver.deleteAccordoCooperazione(accordoCooperazione);
	}
	
	public List<AccordoCooperazione> accordiCooperazioneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiCooperazioneDriver.accordiCooperazioneList(superuser, ricerca);
	}

	public List<IDSoggetto> accordiCoopPartecipantiList(long idAccordo,ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiCooperazioneDriver.accordiCoopPartecipantiList(idAccordo, ricerca);
	}

	public List<AccordoCooperazione> accordiCoopWithSoggettoPartecipante(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.accordiCooperazioneDriver.accordiCoopWithSoggettoPartecipante(idSoggetto);
	}

	public void validaStatoAccordoCooperazione(AccordoCooperazione ac) throws ValidazioneStatoPackageException{
		this.accordiCooperazioneDriver.validaStatoAccordoCooperazione(ac);
	}
	

	/* Accordi di Servizio Parte Comune Sintetici */
	
	public org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico getAccordoServizioParteComuneSintetico(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiSinteticiDriver.getAccordoServizioParteComuneSintetico(idAccordo);
	}	
	
	public org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico getAccordoServizioParteComuneSintetico(long idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiSinteticiDriver.getAccordoServizioParteComuneSintetico(idAccordo);
	}
	
	public List<AccordoServizioParteComuneSintetico> accordiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSinteticiDriver.accordiList(superuser, ricerca);
	}
	public List<AccordoServizioParteComuneSintetico> accordiServizioParteComuneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSinteticiDriver.accordiServizioParteComuneList(superuser, ricerca);
	}
	public List<AccordoServizioParteComuneSintetico> accordiServizioCompostiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSinteticiDriver.accordiServizioCompostiList(superuser, ricerca);
	}
	
	
	/* Accordi di Servizio Parte Comune */
	
	public int getAccordoServizioParteComuneNextVersion(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComuneNextVersion(idAccordo);
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
		return this.accordiDriver.getAccordoServizioParteComune(idAccordo);
	}
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo,boolean readContenutoAllegati,boolean readDatiRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComune(idAccordo, readContenutoAllegati, readDatiRegistro);
	}

	public IDAccordo getIdAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getIdAccordoServizioParteComune(id);
	}
	public IDAccordo getIdAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getIdAccordoServizioParteComune(id, conParam);
	}

	public AccordoServizioParteComune getAccordoServizioParteComune(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComune(id);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(long id, boolean readContenutoAllegati,boolean readDatiRegistro) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComune(id, readContenutoAllegati, readDatiRegistro);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(long id,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComune(id, conParam);
	}
	public AccordoServizioParteComune getAccordoServizioParteComune(long id, boolean readContenutoAllegati,boolean readDatiRegistro,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getAccordoServizioParteComune(id, readContenutoAllegati, readDatiRegistro, conParam);
	}

	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiFindAllDriver.getAllIdAccordiServizioParteComune(filtroRicerca);
	}
	
	@Override
	public List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiFindAllDriver.getAllIdPortType(filtroRicerca);
	}
	
	@Override
	public List<IDPortTypeAzione> getAllIdAzionePortType(FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiFindAllDriver.getAllIdAzionePortType(filtroRicerca);
	}
	
	@Override
	public List<IDAccordoAzione> getAllIdAzioneAccordo(FiltroRicercaAzioni filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiFindAllDriver.getAllIdAzioneAccordo(filtroRicerca);
	}
	
	@Override
	public List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiFindAllDriver.getAllIdResource(filtroRicerca);
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
		this.accordiDriver.createAccordoServizioParteComune(accordoServizio);
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
		return this.accordiExistsDriver.existsAccordoServizioParteComune(idAccordo);
	}

	/**
	 * Verifica l'esistenza di un accordo registrato.
	 * 
	 * @param idAccordo
	 *                dell'accordo da verificare
	 * @return true se l'accordo esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComune(long idAccordo) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComune(idAccordo);
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
		return this.accordiExistsDriver.existsAccordoServizioParteComuneAzione(nome, idAccordo);
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
		return this.accordiExistsDriver.existsAccordoServizioParteComuneAzione(nome, idAccordo);
	}

	/**
	 * Verifica l'esistenza di un'azione in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneAzione(long idAzione) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComuneAzione(idAzione);
	}

	public boolean existsAccordoServizioParteComunePorttype(String nome, IDAccordo idAccordo) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComunePorttype(nome, idAccordo);
	}

	/**
	 * Verifica l'esistenza di un'operation in un accordo
	 * 
	 * @param idAzione dell'azione
	 * @return true se l'azione esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneOperation(long idAzione) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComuneOperation(idAzione);
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
		return this.accordiExistsDriver.existsAccordoServizioParteComunePorttype(nome, idAccordo);
	}

	/**
	 * 
	 * @param nome Nome dell'operation
	 * @param idPortType L'identificativo del PortType
	 * @return true se esiste, false altrimenti.
	 * @throws DriverRegistroServiziException
	 */
	public boolean existsAccordoServizioParteComunePorttypeOperation(String nome, IDPortType idPortType) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComunePorttypeOperation(nome, idPortType);
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
		return this.accordiExistsDriver.existsAccordoServizioParteComunePorttypeOperation(nome, idPortType);
	}
	
	/**
	 * Verifica l'esistenza di un risorsa con determinato nome in un accordo
	 * 
	 * @param nome
	 *                del port-type da verificare
	 * @param idAccordo
	 *                dell'accordo
	 * @return true se il port-type esiste, false altrimenti
	 */
	public boolean existsAccordoServizioParteComuneResource(String nome, long idAccordo) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComuneResource(nome, idAccordo);
	}
	
	public boolean existsAccordoServizioParteComuneResource(String httpMethod, String path, long idAccordo, String excludeResourceWithName) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioParteComuneResource(httpMethod, path, idAccordo, excludeResourceWithName);
	}
	
	/**
	 * Verifica l'esistenza di un response con Status in una Resource
	 * 
	 * @param httpStatus
	 *                 della response da verificare
	 * @param idRisorsa
	 *                della resource
	 * @return true se la risposta esiste, false altrimenti
	 */
	public boolean existsAccordoServizioResourceResponse(long idRisorsa, int httpStatus) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioResourceResponse(idRisorsa, httpStatus);
	}
	
	public boolean existsAccordoServizioResourceRepresentation(Long idRisorsa, boolean isRequest, Long idResponse, String mediaType) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioResourceRepresentation(idRisorsa, isRequest, idResponse, mediaType);
	}
	
	public boolean existsAccordoServizioResourceParameter(Long idRisorsa, boolean isRequest, Long idResponse, ParameterType tipo, String nome) throws DriverRegistroServiziException {
		return this.accordiExistsDriver.existsAccordoServizioResourceParameter(idRisorsa, isRequest, idResponse, tipo, nome);
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
		this.accordiDriver.updateAccordoServizioParteComune(accordoServizio);
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
	public void updatePortType(org.openspcoop2.core.registry.PortType portType, String user) throws DriverRegistroServiziException {
		this.accordiSoapDriver.updatePortType(portType, user);
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
		this.accordiDriver.deleteAccordoServizioParteComune(accordoServizio);
	}

	public List<Azione> accordiAzioniList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException{
		return this.accordiSoapDriver.accordiAzioniList(idAccordo, ricerca);
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
	public List<Azione> accordiAzioniList(long idAccordo,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiAzioniList(idAccordo, profiloCollaborazione, ricerca);
	}
	
	public List<IDAccordoDB> idAccordiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return this.accordiDriver.idAccordiList(superuser, ricerca, 
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	public List<IDAccordoDB> idAccordiServizioParteComuneList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return this.accordiDriver.idAccordiServizioParteComuneList(superuser, ricerca, 
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	public List<IDAccordoDB> idAccordiServizioCompostiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		return this.accordiDriver.idAccordiServizioCompostiList(superuser, ricerca, 
				soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
	}
	
	public List<Documento> accordiAllegatiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiDriver.accordiAllegatiList(idAccordo, ricerca);
	}
	
	public List<IDServizio> getIdServiziWithAccordo(IDAccordo idAccordo,boolean checkPTisNull) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiDriver.getIdServiziWithAccordo(idAccordo, checkPTisNull);
	}
	
	public void controlloUnicitaImplementazioneAccordoPerSoggetto(String portType,
			IDSoggetto idSoggettoErogatore, long idSoggettoErogatoreLong, 
			IDAccordo idAccordoServizioParteComune, long idAccordoServizioParteComuneLong,
			IDServizio idAccordoServizioParteSpecifica, long idAccordoServizioParteSpecificaLong,
			boolean isUpdate,boolean isServizioCorrelato,
			boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
			boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto) throws DriverRegistroServiziException{
		this.accordiDriver.controlloUnicitaImplementazioneAccordoPerSoggetto(portType,
				idSoggettoErogatore, idSoggettoErogatoreLong, 
				idAccordoServizioParteComune, idAccordoServizioParteComuneLong,
				idAccordoServizioParteSpecifica, idAccordoServizioParteSpecificaLong,
				isUpdate,isServizioCorrelato,
				isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto,
				isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto);
	}

	/**
	 * Restituisce gli accordi compatibili
	 * @param ricerca
	 * @return accordiCompatibiliList
	 * @throws DriverRegistroServiziException
	 */
	public List<AccordoServizioParteComune> accordiCompatibiliList(ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiDriver.accordiCompatibiliList(ricerca);
	}
	
	public void validaStatoAccordoServizio(AccordoServizioParteComune as,boolean utilizzoAzioniDiretteInAccordoAbilitato) throws ValidazioneStatoPackageException{
		this.accordiDriver.validaStatoAccordoServizio(as, utilizzoAzioniDiretteInAccordoAbilitato);
	}
	
	/* Accordi di Servizio Composti */
	
	public List<AccordoServizioParteComune> accordiServizio_serviziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.accordiServizio_serviziComponentiConSoggettoErogatore(idSoggetto);
	}

	public List<AccordoServizioParteComune> accordiServizio_serviziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.accordiServizio_serviziComponenti(idServizio);
	}

	public List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.accordiServizioWithAccordoCooperazione(idAccordoCooperazione);
	}
	
	public AccordoServizioParteComune[] getAllIdAccordiWithSoggettoReferente(IDSoggetto idsoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.accordiDriver.getAllIdAccordiWithSoggettoReferente(idsoggetto);
	}
	
	public long getIdServizioCorrelato(String nomeServizio, String tipoServizio, String nomeProprietario, String tipoProprietario, Connection con) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.getIdServizioCorrelato(nomeServizio, tipoServizio, nomeProprietario, tipoProprietario, con);
	}
	
	public List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.accordiComponentiList(idAccordo, ricerca);
	}

	public List<Documento> accordiCoopAllegatiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiServiziCompostiDriver.accordiCoopAllegatiList(idAccordo, ricerca);
	}
	
	/* Accordi di Servizio SOAP */
	
	public void deleteAzione(long idAccordo, String nomeAzione) throws DriverRegistroServiziException {
		this.accordiSoapDriver.deleteAzione(idAccordo, nomeAzione);
	}	
	
	public List<PortType> accordiPorttypeList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeList(idAccordo, ricerca);
	}

	public List<PortType> accordiPorttypeList(long idAccordo, String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeList(idAccordo, profiloCollaborazione, ricerca);
	}
	
	public List<Operation> accordiPorttypeOperationList(long idPortType,String profiloCollaborazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeOperationList(idPortType, profiloCollaborazione, ricerca);
	}

	public List<Operation> accordiPorttypeOperationList(long idPortType, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeOperationList(idPortType, ricerca);
	}
	
	public List<MessagePart> accordiPorttypeOperationMessagePartList(long idOperation, boolean isInput, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeOperationMessagePartList(idOperation, isInput, ricerca);
	}
	
	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idAccordo
	 * @param nomeAzione
	 * @return true se se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	public boolean isCorrelata(long idAccordo,String nomeAzione,String owner) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isCorrelata(idAccordo, nomeAzione, owner);
	}

	/**
	 * Controlla se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @param idPortType
	 * @param nomeCorrelata
	 * @return true se l'azione e' usata come Azione correlata in qualche azione dell'accordo con id idAccordo 
	 * @throws DriverRegistroServiziException
	 */
	public boolean isOperationCorrelata(long idPortType,String nomeCorrelata,String owner) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isOperationCorrelata(idPortType, nomeCorrelata, owner);
	}

	public boolean isOperationCorrelataDaAltraAzione(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isOperationCorrelataDaAltraAzione(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
	}
	public boolean isOperationCorrelataRichiesta(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isOperationCorrelataRichiesta(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
	}
	public boolean isOperationCorrelata(String nomePortType,long idPortType,String azioneDaVerificare,long idAzioneDaVerificare) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isOperationCorrelata(nomePortType, idPortType, azioneDaVerificare, idAzioneDaVerificare);
	}
	
	public PortType getPortType(IDPortType idPT) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.getPortType(idPT);
		
	}
	public PortType getPortType(long id) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.getPortType(id);
	}

	public List<IDServizio> getIdServiziWithPortType(IDPortType idPT) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiSoapDriver.getIdServiziWithPortType(idPT);
	}
	
	// Controlla che esista solo un'azione, direttamente negli accordi e tra i port type con tale nome
	public boolean isUnicaAzioneInAccordi(String azione) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.isUnicaAzioneInAccordi(azione);
	}
	
	public List<PortType> accordiPorttypeCompatibiliList(long idAccordo,boolean isErogazione, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiSoapDriver.accordiPorttypeCompatibiliList(idAccordo,isErogazione, ricerca);
	}

	/* Accordi di Servizio REST */
	
	public List<Resource> accordiResourceList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiRestDriver.accordiResourceList(idAccordo, ricerca);
	}
	
	public List<ResourceResponse> accordiResourceResponseList(long idRisorsa, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiRestDriver.accordiResourceResponseList(idRisorsa, ricerca);
	}
	
	public List<ResourceRepresentation> accordiResourceRepresentationsList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca)  throws DriverRegistroServiziException {
		return this.accordiRestDriver.accordiResourceRepresentationsList(idRisorsa, isRequest, idRisposta, ricerca);
	}
	
	public List<ResourceParameter> accordiResourceParametersList(Long idRisorsa, boolean isRequest, Long idRisposta, ISearch ricerca)  throws DriverRegistroServiziException {
		return this.accordiRestDriver.accordiResourceParametersList(idRisorsa, isRequest, idRisposta, ricerca);
	}

	
	/* Documenti*/
	
	public boolean existsDocumento(String nome, String tipo, String ruolo, long idProprietario, ProprietariDocumento proprietarioDocumento) throws DriverRegistroServiziException {
		return this.documentiDriver.existsDocumento(nome, tipo, ruolo, idProprietario, proprietarioDocumento);
	}

	public Documento getDocumento(String nome, String tipo, String ruolo, long idProprietario,boolean readBytes,ProprietariDocumento tipoProprietario) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.documentiDriver.getDocumento(nome, tipo, ruolo, idProprietario, readBytes, tipoProprietario);
	}

	public Documento getDocumento(long idDocumento,boolean readBytes) throws DriverRegistroServiziException {
		return this.documentiDriver.getDocumento(idDocumento,readBytes);
	}

	public List<Documento> serviziAllegatiList(long idServizio, ISearch ricerca) throws DriverRegistroServiziException {
		return this.documentiDriver.serviziAllegatiList(idServizio,ricerca);
	}

	@Override
	public Documento getAllegato(IDServizio idASPS, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getAllegato(idASPS,nome);
	}
	@Override
	public Documento getSpecificaSemiformale(IDServizio idASPS, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getSpecificaSemiformale(idASPS,tipo,nome);
	}
	@Override
	public Documento getSpecificaSicurezza(IDServizio idASPS, TipiDocumentoSicurezza tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getSpecificaSicurezza(idASPS,tipo,nome);
	}
	@Override
	public Documento getSpecificaLivelloServizio(IDServizio idASPS, TipiDocumentoLivelloServizio tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getSpecificaLivelloServizio(idASPS,tipo,nome);
	}
	
	@Override
	public Documento getAllegato(IDAccordo idAccordo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getAllegato(idAccordo,nome);
	}
	@Override
	public Documento getSpecificaSemiformale(IDAccordo idAccordo, TipiDocumentoSemiformale tipo, String nome)throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.documentiDriver.getSpecificaSemiformale(idAccordo,tipo,nome);
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
		return this.pddDriver.getPortaDominio(nomePdD);
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
		return this.pddDriver.getAllIdPorteDominio(filtroRicerca);
	}

	/**
	 * Crea una nuova Porta di Dominio 
	 * 
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		this.pddDriver.createPortaDominio(pdd);
	}

	/**
	 * Verifica l'esistenza di una Porta di Dominio.
	 *
	 * @param nome della porta di dominio da verificare
	 * @return true se la porta di dominio esiste, false altrimenti
	 * @throws DriverRegistroServiziException
	 */    
	@Override
	public boolean existsPortaDominio(String nome) throws DriverRegistroServiziException{
		return this.pddDriver.existsPortaDominio(nome);
	}

	/**
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		this.pddDriver.updatePortaDominio(pdd);
	}	

	/**
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param nomePdd
	 * @param tipo
	 * @throws DriverRegistroServiziException
	 */
	public void updateTipoPortaDominio(String nomePdd,String tipo) throws DriverRegistroServiziException{
		this.pddDriver.updateTipoPortaDominio(nomePdd, tipo);
	}	
	
	public String getTipoPortaDominio(String nome) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.pddDriver.getTipoPortaDominio(nome);
	}

	/**
	 * Elimina una Porta di Dominio 
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		this.pddDriver.deletePortaDominio(pdd);
	}
	
	/**
	 * Ritorna la lista di Porte di Dominio
	 * Tiene conto dei criteri di ricerca Settati
	 * @param ricerca
	 * @return la lista degli accordi di servizio che soddisfa la ricerca
	 */
	public List<PortaDominio> porteDominioList(String superuser, ISearch ricerca) throws DriverRegistroServiziException{
		return this.pddDriver.porteDominioList(superuser, ricerca);
	}
	public List<PortaDominio> porteDominioList(String superuser,String tipo, ISearch ricerca) throws DriverRegistroServiziException{
		return this.pddDriver.porteDominioList(superuser, tipo, ricerca);
	}

	public boolean isPddInUso(PortaDominio pdd, List<String> whereIsInUso)
			throws DriverRegistroServiziException {
		return this.pddDriver.isPddInUso(pdd, whereIsInUso);
	}

	public List<Soggetto> pddSoggettiList(long idPdd, ISearch ricerca) throws DriverRegistroServiziException {
		return this.pddDriver.pddSoggettiList(idPdd, ricerca);
	}
	
	public List<PortaDominio> porteDominioWithSubject(String subject)throws DriverRegistroServiziException {
		return this.pddDriver.porteDominioWithSubject(subject);
	}
	
	
	
	/* Gruppi */

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Gruppo}, 
	 * identificato grazie al parametro 
	 * <var>nome</var> 
	 *
	 * @param idGruppo Identificativo del gruppo
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.Gruppo}.
	 * 
	 */
	@Override
	public Gruppo getGruppo(IDGruppo idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.gruppiDriver.getGruppo(idGruppo);
	}

	public Gruppo getGruppo(long idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.gruppiDriver.getGruppo(idGruppo);
	}

	/**
	 * Ritorna gli identificatori dei Gruppi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei gruppi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDGruppo> getAllIdGruppi(FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.gruppiDriver.getAllIdGruppi(filtroRicerca);
	}

	/**
	 * Crea una nuovo Gruppo
	 * 
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		this.gruppiDriver.createGruppo(gruppo);
	}

	/**
     * Verifica l'esistenza di un Gruppo
     *
     * @param idGruppo idGruppo del gruppo da verificare
     * @return true se il gruppo esiste, false altrimenti
	 * @throws DriverRegistroServiziException
     */    
    @Override
	public boolean existsGruppo(IDGruppo idGruppo) throws DriverRegistroServiziException{
    	return this.gruppiDriver.existsGruppo(idGruppo);
	}

    /**
	 * Aggiorna il Gruppo con i nuovi valori.
	 *  
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		this.gruppiDriver.updateGruppo(gruppo);
	}	

	/**
	 * Elimina un Gruppo
	 *  
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		this.gruppiDriver.deleteGruppo(gruppo);
	}
	
	public List<Gruppo> gruppiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.gruppiDriver.gruppiList(superuser, ricerca);
	}
	
	
	/* Ruoli */

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Ruolo}, 
	 * identificato grazie al parametro 
	 * <var>nome</var> 
	 *
	 * @param idRuolo Identificativo del ruolo
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.Ruolo}.
	 * 
	 */
	@Override
	public Ruolo getRuolo(IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.ruoliDriver.getRuolo(idRuolo);
	}
	public Ruolo getRuolo(Connection conParam, IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.ruoliDriver.getRuolo(conParam, idRuolo);
	}

	public Ruolo getRuolo(long idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.ruoliDriver.getRuolo(idRuolo);
	}
	public Ruolo getRuolo(Connection conParam, long idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.ruoliDriver.getRuolo(conParam, idRuolo);
	}

	/**
	 * Ritorna gli identificatori dei Ruoli che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei ruoli trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDRuolo> getAllIdRuoli(FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.ruoliDriver.getAllIdRuoli(filtroRicerca);
	}

	/**
	 * Crea una nuovo Ruolo
	 * 
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		this.ruoliDriver.createRuolo(ruolo);
	}

	/**
     * Verifica l'esistenza di un Ruolo
     *
     * @param idRuolo idRuolo del ruolo da verificare
     * @return true se il ruolo esiste, false altrimenti
	 * @throws DriverRegistroServiziException
     */    
    @Override
	public boolean existsRuolo(IDRuolo idRuolo) throws DriverRegistroServiziException{
    	return this.ruoliDriver.existsRuolo(idRuolo);
	}

    /**
	 * Aggiorna il Ruolo con i nuovi valori.
	 *  
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		this.ruoliDriver.updateRuolo(ruolo);
	}	

	/**
	 * Elimina un Ruolo
	 *  
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		this.ruoliDriver.deleteRuolo(ruolo);
	}
	
	public List<Ruolo> ruoliList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.ruoliDriver.ruoliList(superuser, ricerca);
	}
	
	
	
	/* Scope */

	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Scope}, 
	 * identificato grazie al parametro 
	 * <var>nome</var> 
	 *
	 * @param idScope Identificativo del scope
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.Scope}.
	 * 
	 */
	@Override
	public Scope getScope(IDScope idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.scopeDriver.getScope(idScope);
	}

	public Scope getScope(long idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.scopeDriver.getScope(idScope);
	}

	/**
	 * Ritorna gli identificatori dei Scope che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei scope trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDScope> getAllIdScope(FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.scopeDriver.getAllIdScope(filtroRicerca);
	}

	/**
	 * Crea una nuovo Scope
	 * 
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createScope(Scope scope) throws DriverRegistroServiziException{
		this.scopeDriver.createScope(scope);
	}

	/**
     * Verifica l'esistenza di un Scope
     *
     * @param idScope idScope del scope da verificare
     * @return true se il scope esiste, false altrimenti
	 * @throws DriverRegistroServiziException
     */    
    @Override
	public boolean existsScope(IDScope idScope) throws DriverRegistroServiziException{
    	return this.scopeDriver.existsScope(idScope);
	}

    /**
	 * Aggiorna il Scope con i nuovi valori.
	 *  
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateScope(Scope scope) throws DriverRegistroServiziException{
		this.scopeDriver.updateScope(scope);
	}	

	/**
	 * Elimina un Scope
	 *  
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteScope(Scope scope) throws DriverRegistroServiziException{
		this.scopeDriver.deleteScope(scope);
	}
	
	public List<Scope> scopeList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.scopeDriver.scopeList(superuser, ricerca);
	}
	
	
	/* Soggetti */

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
		return this.soggettiDriver.getSoggetto(idSoggetto);
	}

	public Soggetto getSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getSoggetto(idSoggetto);
	}
	public Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getSoggetto(idSoggetto, conParam);
	}
	
	public Soggetto soggettoWithCredenzialiBasic(String utente, String password, boolean checkPassword) throws DriverRegistroServiziException {
		return this.soggettiCredenzialiDriver.soggettoWithCredenzialiBasic(utente, password, checkPassword);
	}
	
	public Soggetto soggettoWithCredenzialiApiKey(String utente, boolean appId) throws DriverRegistroServiziException {
		return this.soggettiCredenzialiDriver.soggettoWithCredenzialiApiKey(utente, appId);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiBasic(String user,String password, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.soggettiCredenzialiDriver.getSoggettoByCredenzialiBasic(user, password, config);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiApiKey(String user,String password, boolean appId, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.soggettiCredenzialiDriver.getSoggettoByCredenzialiApiKey(user, password, appId, config);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(String subject, String issuer) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.soggettiCredenzialiDriver.getSoggettoByCredenzialiSsl(subject, issuer);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this.soggettiCredenzialiDriver.getSoggettoByCredenzialiSsl(certificate, strictVerifier);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiPrincipal(String principal) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.soggettiCredenzialiDriver.getSoggettoByCredenzialiPrincipal(principal);
	}
		
	public List<Soggetto> soggettoWithCredenzialiSslList(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException {
		return this.soggettiCredenzialiDriver.soggettoWithCredenzialiSslList(certificate, strictVerifier);
	}
	
	/***
	 * Restituisce tutti i soggetti con i tipi indicati che utilizzano le credenziali indicate
	 * 
	 * @param tipiSoggetto
	 * @param superuser
	 * @param credenziale
	 * @return tutti i soggetti con i tipi indicati che utilizzano le credenziali indicate
	 */
	public List<IDSoggettoDB> getSoggettiFromTipoAutenticazione(List<String> tipiSoggetto, String superuser, CredenzialeTipo credenziale, Boolean appId, PddTipologia pddTipologia) throws DriverRegistroServiziException {
		return this.soggettiCredenzialiDriver.getSoggettiFromTipoAutenticazione(tipiSoggetto, superuser, credenziale, appId, pddTipologia);
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
		return this.soggettiDriver.getAllIdSoggetti(filtroRicerca);
	}

	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.soggettiDriver.createSoggetto(soggetto);
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
		return this.soggettiDriver.existsSoggetto(idSoggetto);
	}
	public boolean existsSoggetto(Connection conParam, IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.soggettiDriver.existsSoggetto(conParam, idSoggetto);
	}

	public boolean existsSoggetto(long idSoggetto) throws DriverRegistroServiziException {
		return this.soggettiDriver.existsSoggetto(idSoggetto);
	}

	public boolean existsSoggetto(String codiceIPA) throws DriverRegistroServiziException {
		return this.soggettiDriver.existsSoggetto(codiceIPA);
	}

	public Soggetto getSoggetto(String codiceIPA) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getSoggetto(codiceIPA);
	}

	public String getCodiceIPA(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getCodiceIPA(idSoggetto);
	}

	/**
	 * Aggiorna un Soggetto e il Connettore con i nuovi dati passati
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.soggettiDriver.updateSoggetto(soggetto);
	}

	/**
	 * Cancella un Soggetto e il Connettore
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteSoggetto(org.openspcoop2.core.registry.Soggetto soggetto) throws DriverRegistroServiziException {
		this.soggettiDriver.deleteSoggetto(soggetto);
	}

	public IDSoggetto[] getSoggettiWithSuperuser(String user) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getSoggettiWithSuperuser(user);
	}

	public List<String> soggettiRuoliList(long idSoggetto, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soggettiSearchDriver.soggettiRuoliList(idSoggetto, ricerca);
	}
	
	public List<Proprieta> soggettiProprietaList(long idSoggetto, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soggettiSearchDriver.soggettiProprietaList(idSoggetto, ricerca);
	}
	
	/**
	 * Ritorna la lista di nomi delle proprieta registrate
	 */
	public List<String> nomiProprietaSoggetti(List<String> tipoSoggettiProtocollo) throws DriverConfigurazioneException {
		return this.soggettiDriver.nomiProprietaSoggetti(tipoSoggettiProtocollo);
	}
	
	/**
	 * Controlla se il soggetto e' in uso
	 */
	public boolean isSoggettoInUsoInPackageFinali(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return this.soggettiDriver.isSoggettoInUsoInPackageFinali(ss,whereIsInUso);
	}
	public boolean isSoggettoInUsoInPackagePubblici(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return this.soggettiDriver.isSoggettoInUsoInPackagePubblici(ss,whereIsInUso);
	}
	public boolean isSoggettoInUso(Soggetto ss, Map<ErrorsHandlerCostant,String> whereIsInUso) throws DriverRegistroServiziException {
		return this.soggettiDriver.isSoggettoInUso(ss,whereIsInUso);
	}
	
	public List<Soggetto> soggettiRegistroListByTipo(String tipoSoggetto,ISearch ricerca) throws DriverRegistroServiziException{
		return this.soggettiSearchDriver.soggettiRegistroListByTipo(tipoSoggetto, ricerca);
	}

	public List<Soggetto> soggettiRegistroList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return this.soggettiSearchDriver.soggettiRegistroList(superuser, ricerca);
	}
	
	public IDSoggetto getIdSoggetto(long idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getIdSoggetto(idSoggetto);
	}
	public IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.soggettiDriver.getIdSoggetto(idSoggetto, conParam);
	}
	
	public List<IDSoggetto> getSoggettiDefault() throws DriverRegistroServiziException {
		return this.soggettiDriver.getSoggettiDefault();
	}
	public List<IDSoggetto> getSoggettiDefault(Connection conParam) throws DriverRegistroServiziException {
		return this.soggettiDriver.getSoggettiDefault(conParam);
	}
	
	
	/* Accordi Servizio Parte Specifica */
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio, con);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio, readContenutoAllegati);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio, readContenutoAllegati, con);
	}

	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordoServizioParteComune);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordoServizioParteComune, con);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordoServizioParteComune, readContenutoAllegati);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto,IDAccordo idAccordoServizioParteComune,boolean readContenutoAllegati,Connection con) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica_ServizioCorrelato(idSoggetto, idAccordoServizioParteComune, readContenutoAllegati, con);
	}

	@Override
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.accordiParteSpecificaDriver.getAllIdServizi(filtroRicerca);
	}
	
	@Override
	public List<IDFruizione> getAllIdFruizioniServizio(FiltroRicercaFruizioniServizio filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this.accordiParteSpecificaDriver.getAllIdFruizioniServizio(filtroRicerca);
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
		return this.accordiParteSpecificaDriver.getAllIdServiziWithSoggettoErogatore(idSoggetto);
	}

	@Override
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.createAccordoServizioParteSpecifica(accordoServizioParteSpecifica);
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
		return this.accordiParteSpecificaDriver.existsAccordoServizioParteSpecifica(idServizio);
	}

	public boolean existsAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaDriver.existsAccordoServizioParteSpecifica(idServizio);
	}

	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto
	 * accordo e servizio correlato
	 */
	public long getServizioWithSoggettoAccordoServCorr(long idSoggetto, long idAccordo, String servizioCorrelato) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaDriver.getServizioWithSoggettoAccordoServCorr(idSoggetto, idAccordo, servizioCorrelato);
	}

	/**
	 * Verifica l'esistenza di un servizio con un certo soggetto,
	 * accordo e servizio correlato
	 */
	public long getServizioWithSoggettoAccordoServCorrPt(long idSoggetto, long idAccordo, String servizioCorrelato,String portType) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaDriver.getServizioWithSoggettoAccordoServCorrPt(idSoggetto, idAccordo, servizioCorrelato, portType);
	}

	/**
	 * Verifica l'esistenza di un fruitore con un certo soggetto,
	 * ed un certo servizio
	 */
	public long getServizioFruitore(IDServizio idServizio, long idSogg) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServizioFruitore(idServizio, idSogg);
	}

	/**
	 * Ritorna l'id del soggetto fruitore di un servizio fruitore
	 * 
	 * @param idServizioFruitore
	 *                del servizio fruitore
	 * @return l'id del soggetto fruitore
	 */
	public long getServizioFruitoreSoggettoFruitoreID(long idServizioFruitore) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServizioFruitoreSoggettoFruitoreID(idServizioFruitore);
	}

	/**
	 * Ritorna l'id del servizio di un servizio fruitore
	 * 
	 * @param idServizioFruitore
	 *                del servizio fruitore
	 * @return l'id del servizio
	 */
	public long getServizioFruitoreServizioID(long idServizioFruitore) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServizioFruitoreServizioID(idServizioFruitore);
	}

	@Override
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.updateAccordoServizioParteSpecifica(servizio);
	}

	@Override
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.deleteAccordoServizioParteSpecifica(servizio);
	}

	/**
	 * Dato un soggetto, verifica che i suoi servizi non abbiano
	 * il connettore disabilitato
	 * 
	 * @param idSoggetto
	 *                Identificativo del soggetto
	 * @return true se esistono servizi senza connettore, false altrimenti
	 */
	public boolean existsSoggettoServiziWithoutConnettore(long idSoggetto) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaDriver.existsSoggettoServiziWithoutConnettore(idSoggetto);
	}
	
	public boolean existFruizioniServiziSoggettoWithoutConnettore(long idSoggetto, boolean escludiSoggettiEsterni) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.existFruizioniServiziSoggettoWithoutConnettore(idSoggetto, escludiSoggettiEsterni);
	}

	public boolean existFruizioniServizioWithoutConnettore(long idServizio, boolean escludiSoggettiEsterni) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.existFruizioniServizioWithoutConnettore(idServizio, escludiSoggettiEsterni);
	}
	
	public List<AccordoServizioParteSpecifica> serviziList(String superuser,ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaSearchDriver.serviziList(superuser, ricerca);
	}

	public List<AccordoServizioParteSpecifica> servizioWithSoggettoFruitore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.servizioWithSoggettoFruitore(idSoggetto);
	}
	
	public List<Soggetto> accordiErogatoriList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaSearchDriver.accordiErogatoriList(idAccordo, ricerca);
	}

	public Fruitore getAccordoErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaFruitoreDriver.getAccordoErogatoreFruitore(id);
	}

	public Fruitore getErogatoreFruitore(long id) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaFruitoreDriver.getErogatoreFruitore(id);
	}
	
	public Fruitore getServizioFruitore(long idServFru) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServizioFruitore(idServFru);
	}
	
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio,readContenutoAllegati);
	}

	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio,conParam);
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(long idServizio,Connection conParam,boolean readContenutoAllegati) throws DriverRegistroServiziException,DriverRegistroServiziNotFound {
		return this.accordiParteSpecificaDriver.getAccordoServizioParteSpecifica(idServizio,conParam,readContenutoAllegati);
	}
	
	public List<Fruitore> serviziFruitoriList(long idServizi, ISearch ricerca) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.serviziFruitoriList(idServizi, ricerca);
	}

	public List<AccordoServizioParteSpecifica> serviziWithIdAccordoList(long idAccordo) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaDriver.serviziWithIdAccordoList(idAccordo);
	}

	/**
	 * Recupero l'id del servizio fruitore
	 * @param idServizio
	 * @param idSoggetto
	 * @return L'id del servizio fruitore
	 * @throws DriverRegistroServiziException
	 */
	public long getIdServizioFruitore(long idServizio, long idSoggetto) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getIdServizioFruitore(idServizio, idSoggetto);
	}

	public List<Fruitore> getServiziFruitoriWithServizio(long idServizio) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServiziFruitoriWithServizio(idServizio);
	}

	public List<AccordoServizioParteSpecifica> getServiziByIdErogatore(long idErogatore,ISearch filters) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaSearchDriver.getServiziByIdErogatore(idErogatore, filters);
	}

	public List<AccordoServizioParteSpecifica> getServiziByFruitore(Fruitore fruitore) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getServiziByFruitore(fruitore);
	}

	public List<Fruitore> getSoggettiWithServizioNotFruitori(long idServizio, boolean escludiSoggettiEsterni, CredenzialeTipo credenzialeTipo) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaFruitoreDriver.getSoggettiWithServizioNotFruitori(idServizio, escludiSoggettiEsterni, credenzialeTipo);
	}
	
	/***
	 * 
	 * @param superuser
	 * @param ricerca
	 * @param permessiUtente se permessiUtente[0] = true l'utente ha permessi per la categoria Servizi 'S', se permessiUtente[1] = true l'utente ha permessi per la categoria Accordi Cooperazione 'P'
	 * @return lista di accordi di servizio parte specifica
	 * @throws DriverRegistroServiziException
	 */
	public List<AccordoServizioParteSpecifica> soggettiServizioList(String superuser, ISearch ricerca,boolean [] permessiUtente, 
			boolean gestioneFruitori, boolean gestioneErogatori) throws DriverRegistroServiziException {
		return this.accordiParteSpecificaSearchDriver.soggettiServizioList(superuser, ricerca, permessiUtente, 
				gestioneFruitori, gestioneErogatori);
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
		return this.accordiParteSpecificaDriver.getAllIdSoggettiErogatori(tipoServizio, nomeServizio,uriAccordo, nomePortType, tipoSoggetto);
	}
	
	public void validaStatoAccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio, boolean gestioneWsdlImplementativo, boolean checkConnettore) throws ValidazioneStatoPackageException{
		this.accordiParteSpecificaDriver.validaStatoAccordoServizioParteSpecifica(servizio, gestioneWsdlImplementativo, checkConnettore);
	}

	public void validaStatoFruitoreServizio(Fruitore fruitore,AccordoServizioParteSpecifica serv) throws ValidazioneStatoPackageException{
		this.accordiParteSpecificaFruitoreDriver.validaStatoFruitoreServizio(fruitore, serv);
	}
	
	public void updateProprietaOggettoErogazione(IDServizio idServizio, String user) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.updateProprietaOggettoErogazione(idServizio, user);
	}
	public void updateProprietaOggettoErogazione(long idServizio, String user) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.updateProprietaOggettoErogazione(idServizio, user);
	}
	public void updateProprietaOggettoFruizione(IDServizio idServizio, IDSoggetto idFruitore, String user) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.updateProprietaOggettoFruizione(idServizio, idFruitore, user);
	}
	public void updateProprietaOggettoFruizione(long idFruizione, String user) throws DriverRegistroServiziException {
		this.accordiParteSpecificaDriver.updateProprietaOggettoFruizione(idFruizione, user);
	}
	


	/* Connettori */

	public Connettore getConnettore(long idConnettore) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		return this.connettoriDriver.getConnettore(idConnettore);
	}
	public Connettore getConnettore(String nomeConnettore) throws DriverRegistroServiziException {
		return this.connettoriDriver.getConnettore(nomeConnettore);
	}
	
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
		return this.connettoriDriver.getConnettore(idConnettore, connection);
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
		return this.connettoriDriver.getPropertiesConnettore(nomeConnettore);
	}
	public Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverRegistroServiziException {
		return this.connettoriDriver.getPropertiesConnettore(nomeConnettore, connection);
	}
	
	/**
	 * Crea un nuovo Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void createConnettore(Connettore connettore) throws DriverRegistroServiziException {
		this.connettoriDriver.createConnettore(connettore);
	}

	/**
	 * Aggiorna un Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void updateConnettore(Connettore connettore) throws DriverRegistroServiziException {
		this.connettoriDriver.updateConnettore(connettore);
	}

	/**
	 * Elimina un Connettore
	 * 
	 * @param connettore
	 * @throws DriverRegistroServiziException
	 */
	public void deleteConnettore(Connettore connettore) throws DriverRegistroServiziException {
		this.connettoriDriver.deleteConnettore(connettore);
	}

	
	/* Protocol Properties */
	
	public boolean existsProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverRegistroServiziException {
		return this.protocolPropertiesDriver.existsProtocolProperty(proprietarioProtocolProperty, idProprietario, nome);
	}

	public ProtocolProperty getProtocolProperty(ProprietariProtocolProperty proprietarioProtocolProperty, long idProprietario, String nome) throws DriverRegistroServiziException {
		return this.protocolPropertiesDriver.getProtocolProperty(proprietarioProtocolProperty, idProprietario, nome);
	}

	public ProtocolProperty getProtocolProperty(long idProtocolProperty) throws DriverRegistroServiziException {
		return this.protocolPropertiesDriver.getProtocolProperty(idProtocolProperty);
	}
	
	

	/* RESET */
	
	@Override
	public void reset() throws DriverRegistroServiziException {
		this.utilsDriver.reset();
	}

	/**
	 * Reset delle tabelle del db govwayConsole gestito da questo driver
	 */
	public void resetCtrlstat() throws DriverRegistroServiziException {
		this.utilsDriver.resetCtrlstat();
	}

	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws CoreException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		this.utilsDriver.isAlive();
	}
	

}

