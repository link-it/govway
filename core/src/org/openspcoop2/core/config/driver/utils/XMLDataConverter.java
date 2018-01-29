/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.core.config.driver.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneCRUD;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.mapping.Implementation;
import org.openspcoop2.core.mapping.ImplementationUtils;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mapping.Subscription;
import org.openspcoop2.core.mapping.SubscriptionUtils;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.utils.RegistroServiziUtils;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
 * Classe utilizzata per convertire dati presenti in un file XML in un altra risorsa che implementa l'interfaccia CRUD.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLDataConverter {

	/** GestoreCRUD */
	private IDriverConfigurazioneCRUD gestoreCRUD = null;
	/** 'Root' della configurazione di  OpenSPCoop. */
	private org.openspcoop2.core.config.Openspcoop2 sorgenteConfigurazione;
	/** Logger */
	private Logger log = null;
	/** Logger alternativo per i driver */
	private Logger logDriver = null;
	/** SuperUser */
	private String superUser = null;
	/** Indicazione se deve essere convertita anche la configurazione */
	private boolean gestioneConfigurazione;
	/** Tipo del backend di destinazione */
	private String tipoBEDestinazione;
	
	private AbstractXMLUtils xmlUtils = null;
	
	public XMLDataConverter(String sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(String sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(String sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(byte[] sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(byte[] sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(byte[] sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(InputStream sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(InputStream sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(InputStream sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(File sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(File sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(File sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,destinazione,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	private void XMLDataConverterSetup(Object sorgente,AccessoConfigurazionePdD destinazione,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
	
		if(log == null)
			this.log = LoggerWrapperFactory.getLogger(XMLDataConverter.class);
		else
			this.log = log;
		this.logDriver = logDriver;
		
		this.gestioneConfigurazione = configurazione;
		if(destinazione==null)
			throw new DriverConfigurazioneException("GestoreCRUD non definito");
		this.tipoBEDestinazione = destinazione.getTipo();
		this.superUser = superUser;
		
		// Istanziazione sorgente
		try{
			if(sorgente instanceof String){
				createSorgente((String)sorgente);
			}
			else if (sorgente instanceof byte[]){
				createSorgente((byte[])sorgente);
			}else if (sorgente instanceof InputStream){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				InputStream is = (InputStream) sorgente;
				int letti = 0;
				byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
				while( (letti=is.read(reads)) != -1 ){
					bout.write(reads,0,letti);	
				}
				bout.flush();
				bout.close();
				createSorgente(bout.toByteArray());
			}else if (sorgente instanceof File){
				createSorgente(((File)sorgente).getAbsolutePath());
			}
		}catch(DriverConfigurazioneException d){
			throw d;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Creazione sorgente ["+sorgente.getClass().getName()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Istanziazione CRUD
		try{
			if(CostantiConfigurazione.REGISTRO_DB.equals(destinazione.getTipo())){
				if(destinazione.getLocation()==null)
					throw new Exception("Location (DataSource) non definita");
				if(destinazione.getContext()==null)
					throw new Exception("Contesto di localizzazione del datasource non definito in GenericProperties");
				if(destinazione.getTipoDatabase()==null)
					throw new Exception("TipoDatabase (DataSource) non definita");
				
				this.gestoreCRUD = new DriverConfigurazioneDB(destinazione.getLocation(),destinazione.getContext(),this.logDriver,
						destinazione.getTipoDatabase(),tabellaSoggettiCondivisaPddRegserv);
				if(((DriverConfigurazioneDB)this.gestoreCRUD).create)
					this.log.info("Inizializzato Configurazione DB");
				else
					throw new Exception("Configurazione DB non inizializzato");
			}
			else{
				throw new Exception("Tipo di configurazione CRUD non gestita: "+destinazione.getTipo());
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		this.xmlUtils = XMLUtils.getInstance();
		
		// Protocol initialize
		initializeProtocolManager(protocolloDefault);
	}
	
	
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				null,null);
	}
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,null);
	}
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,
				configurazione,tabellaSoggettiCondivisaPddRegserv,superUser,protocolloDefault,
				log,logDriver);
	}
	
	private void XMLDataConverterSetup(Object sorgente,Connection connection,String tipoDatabase,
			boolean configurazione,boolean tabellaSoggettiCondivisaPddRegserv,String superUser,String protocolloDefault,
			Logger log,Logger logDriver) throws DriverConfigurazioneException{
	
		if(log == null)
			this.log = LoggerWrapperFactory.getLogger(XMLDataConverter.class);
		else
			this.log = log;
		this.logDriver = logDriver;
		
		this.gestioneConfigurazione = configurazione;
		this.tipoBEDestinazione = CostantiConfigurazione.REGISTRO_DB.toString();
		this.superUser = superUser;
		
		// Istanziazione sorgente
		try{
			if(sorgente instanceof String){
				createSorgente((String)sorgente);
			}
			else if (sorgente instanceof byte[]){
				createSorgente((byte[])sorgente);
			}else if (sorgente instanceof InputStream){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				InputStream is = (InputStream) sorgente;
				int letti = 0;
				byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
				while( (letti=is.read(reads)) != -1 ){
					bout.write(reads,0,letti);	
				}
				bout.flush();
				bout.close();
				createSorgente(bout.toByteArray());
			}else if (sorgente instanceof File){
				createSorgente(((File)sorgente).getAbsolutePath());
			}
		}catch(DriverConfigurazioneException d){
			throw d;
		}catch(Exception e){
			throw new DriverConfigurazioneException("Creazione sorgente ["+sorgente.getClass().getName()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Istanziazione CRUD in version DB
		try{
			this.gestoreCRUD = new DriverConfigurazioneDB(connection,this.logDriver,tipoDatabase,tabellaSoggettiCondivisaPddRegserv);
			if(((DriverConfigurazioneDB)this.gestoreCRUD).create)
				this.log.info("Inizializzato Configurazione DB");
			else
				throw new Exception("Configurazione DB non inizializzato");
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		this.xmlUtils = XMLUtils.getInstance();
		
		// Protocol initialize
		initializeProtocolManager(protocolloDefault);
	}
	
	private void initializeProtocolManager(String protocolloDefault) throws DriverConfigurazioneException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = null;
			try{
				protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			}catch(Exception pe){}
			if(protocolFactoryManager==null){
			
				Class<?> cConfigurazionePdD = Class.forName("org.openspcoop2.protocol.sdk.ConfigurazionePdD");
				Object configurazionePdD = cConfigurazionePdD.newInstance();
				String confDir = null;
				cConfigurazionePdD.getMethod("setConfigurationDir", String.class).invoke(configurazionePdD, confDir);
				cConfigurazionePdD.getMethod("setAttesaAttivaJDBC", long.class).invoke(configurazionePdD, 60);
				cConfigurazionePdD.getMethod("setCheckIntervalJDBC", int.class).invoke(configurazionePdD, 100);
				cConfigurazionePdD.getMethod("setLoader", Loader.class).invoke(configurazionePdD, new Loader());
				cConfigurazionePdD.getMethod("setLog", Logger.class).invoke(configurazionePdD, this.log);
				
				cProtocolFactoryManager.getMethod("initialize", Logger.class, cConfigurazionePdD, String.class).
					invoke(null, this.log, configurazionePdD, protocolloDefault);
				
			}
			
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
	}
	
	private static Implementation getImplementationDefault(ServiceBinding serviceBinding, IDServizio idServizio) throws DriverConfigurazioneException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			String protocollo = (String) cProtocolFactoryManager.getMethod("getProtocolByOrganizationType",String.class).invoke(protocolFactoryManager,idServizio.getSoggettoErogatore().getTipo());
			
			Class<?> cProtocolFactory = Class.forName("org.openspcoop2.protocol.sdk.IProtocolFactory");
			Object protocolFactory = cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager, protocollo);
			
			Class<?> cProtocolIntegrationConfiguration = Class.forName("org.openspcoop2.protocol.sdk.config.IProtocolIntegrationConfiguration");
			Object protocolIntegrationConfiguration = cProtocolFactory.getMethod("createProtocolIntegrationConfiguration").invoke(protocolFactory);
			
			org.openspcoop2.message.constants.ServiceBinding serviceBindingMessage = RegistroServiziUtils.convertToMessage(serviceBinding);
			
			return (Implementation) cProtocolIntegrationConfiguration.getMethod("createDefaultImplementation", org.openspcoop2.message.constants.ServiceBinding.class, IDServizio.class).
					invoke(protocolIntegrationConfiguration, serviceBindingMessage, idServizio);
						
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'utilizzo della protocolFactory (getImplementationDefault): "+e.getMessage(),e);
		}
	}
	private static Subscription getSubscriptionDefault(ServiceBinding serviceBinding, IDSoggetto idFruitore, IDServizio idServizio) throws DriverConfigurazioneException{
		// Protocol initialize
		try{
			Class<?> cProtocolFactoryManager = Class.forName("org.openspcoop2.protocol.engine.ProtocolFactoryManager");
			Object protocolFactoryManager = cProtocolFactoryManager.getMethod("getInstance").invoke(null);
			
			String protocollo = (String) cProtocolFactoryManager.getMethod("getProtocolByOrganizationType",String.class).invoke(protocolFactoryManager,idServizio.getSoggettoErogatore().getTipo());
			
			Class<?> cProtocolFactory = Class.forName("org.openspcoop2.protocol.sdk.IProtocolFactory");
			Object protocolFactory = cProtocolFactoryManager.getMethod("getProtocolFactoryByName",String.class).invoke(protocolFactoryManager, protocollo);
			
			Class<?> cProtocolIntegrationConfiguration = Class.forName("org.openspcoop2.protocol.sdk.config.IProtocolIntegrationConfiguration");
			Object protocolIntegrationConfiguration = cProtocolFactory.getMethod("createProtocolIntegrationConfiguration").invoke(protocolFactory);
			
			org.openspcoop2.message.constants.ServiceBinding serviceBindingMessage = RegistroServiziUtils.convertToMessage(serviceBinding);
			
			return (Subscription) cProtocolIntegrationConfiguration.getMethod("createDefaultSubscription", org.openspcoop2.message.constants.ServiceBinding.class, IDSoggetto.class, IDServizio.class).
					invoke(protocolIntegrationConfiguration, serviceBindingMessage, idFruitore, idServizio);
						
		}catch(Exception e){
			throw new DriverConfigurazioneException("Errore durante l'utilizzo della protocolFactory (getSubscriptionDefault): "+e.getMessage(),e);
		}
	}
	
	private void createSorgente(String sorgente)throws DriverConfigurazioneException{
		// Istanziazione Sorgente
		if(sorgente==null)
			throw new DriverConfigurazioneException("Sorgente non definita");
		
		// ValidatoreXSD
		ValidatoreXSD validatoreRegistro = null;
		try{
			validatoreRegistro = new ValidatoreXSD(this.log,DriverConfigurazioneXML.class.getResourceAsStream("/config.xsd"));
		}catch (Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione dello schema della Configurazione XML di OpenSPCoop: "+e.getMessage());
		}
		FileInputStream fXML = null;
		try{
			if(sorgente.startsWith("http://") || sorgente.startsWith("file://")){
				validatoreRegistro.valida(sorgente);  
			}else{
				fXML = new FileInputStream(sorgente);
				validatoreRegistro.valida(fXML);
			}
		}catch (Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la validazione XSD del Configurazione XML di OpenSPCoop: "+e.getMessage());
		}finally{
			if(fXML!=null){
				try{
					fXML.close();
				}catch(Exception e){}
			}
		}
		
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.config.Openspcoop2.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			InputStream iStream = null;
			HttpURLConnection httpConn = null;
			if(sorgente.startsWith("http://") || sorgente.startsWith("file://")){
				try{ 
					URL url = new URL(sorgente);
					URLConnection connection = url.openConnection();
					httpConn = (HttpURLConnection) connection;
					httpConn.setRequestMethod("GET");
					httpConn.setDoOutput(true);
					httpConn.setDoInput(true);
					iStream = httpConn.getInputStream();
				}catch(Exception e) {
					try{  
						if(iStream!=null)
							iStream.close();
						if(httpConn !=null)
							httpConn.disconnect();
					} catch(Exception ef) {}
					throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStream del Configurazione (HTTP) : \n\n"+e.getMessage());
				}
			}else{
				try{  
					iStream = new FileInputStream(sorgente);
				}catch(java.io.FileNotFoundException e) {
					throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStream del Configurazione (FILE) : \n\n"+e.getMessage());
				}
			}

			
			/* ---- Unmarshall del file di configurazione ---- */
			try{  
				this.sorgenteConfigurazione = (org.openspcoop2.core.config.Openspcoop2) uctx.unmarshalDocument(readBytes(iStream), null);
			} catch(org.jibx.runtime.JiBXException e) {
				try{  
					if(iStream!=null)
						iStream.close();
					if(httpConn !=null)
						httpConn.disconnect();
				} catch(Exception ef) {}
				throw new DriverConfigurazioneException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
			}

			try{  
				// Chiusura dello Stream
				if(iStream!=null)
					iStream.close();
				// Chiusura dell'eventuale connessione HTTP
				if(httpConn !=null)
					httpConn.disconnect();
			} catch(Exception e) {
				throw new DriverConfigurazioneException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
			}
			
		} catch(Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante l'istanziazione del registro: "+e.getMessage(),e);
		}
	}
	
	private InputStream readBytes(InputStream is) throws Exception{
		// Leggo il data[] in modo da correggere gli eventuali entity imports
		this.xmlUtils = XMLUtils.getInstance();
		byte[] b = new byte[0];
		try {
			Document d = this.xmlUtils.newDocument(is);
			String xml = this.xmlUtils.toString(d, true);
			xml = org.openspcoop2.utils.Utilities.eraserXmlAttribute(xml, "xml:base=");
			b = xml.getBytes();
			return new ByteArrayInputStream(b);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createSorgente(byte[] sorgente)throws DriverConfigurazioneException{
		// Istanziazione Sorgente
		if(sorgente==null)
			throw new DriverConfigurazioneException("Sorgente non definita");
		
		// ValidatoreXSD
		ValidatoreXSD validatoreRegistro = null;
		try{
			validatoreRegistro = new ValidatoreXSD(this.log,DriverConfigurazioneXML.class.getResourceAsStream("/config.xsd"));
		}catch (Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante l'inizializzazione dello schema della Configurazione XML di OpenSPCoop: "+e.getMessage());
		}
		try{
			validatoreRegistro.valida(new ByteArrayInputStream(sorgente));
		}catch (Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la validazione XSD del Configurazione XML di OpenSPCoop: "+e.getMessage());
		}
		
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.config.Openspcoop2.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			InputStreamReader iStream = null;			
			try{  
				iStream = new InputStreamReader(new ByteArrayInputStream(sorgente));
			}catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStreamReader della Configurazione : \n\n"+e.getMessage());
			}
			



			/* ---- Unmarshall del file di configurazione ---- */
			try{  
				this.sorgenteConfigurazione = (org.openspcoop2.core.config.Openspcoop2) uctx.unmarshalDocument(iStream, null);
			} catch(org.jibx.runtime.JiBXException e) {
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(Exception ef) {}
				throw new DriverConfigurazioneException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
			}

			try{  
				// Chiusura dello Stream
				if(iStream!=null)
					iStream.close();
				// Chiusura dell'eventuale connessione HTTP
			} catch(Exception e) {
				throw new DriverConfigurazioneException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
			}
			
		} catch(Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante l'istanziazione del registro: "+e.getMessage(),e);
		}
	}
	
	// Per motivi di dipendenze di compilazione non e' possibile usare  IDServizioFactory
	@SuppressWarnings("deprecation")
	private IDServizio toIdServizio(PortaDelegata pd) {
		IDServizio idServizio = new IDServizio();
		idServizio.setTipo(pd.getServizio().getTipo());
		idServizio.setNome(pd.getServizio().getNome());
		idServizio.setVersione(pd.getServizio().getVersione());
		idServizio.setSoggettoErogatore(new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome()));
		return idServizio;
	}
	@SuppressWarnings("deprecation")
	private IDServizio toIdServizio(PortaApplicativa pa) {
		IDServizio idServizio = new IDServizio();
		idServizio.setTipo(pa.getServizio().getTipo());
		idServizio.setNome(pa.getServizio().getNome());
		idServizio.setVersione(pa.getServizio().getVersione());
		idServizio.setSoggettoErogatore(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
		return idServizio;
	}
	
	public void convertXML(boolean reset,boolean aggiornamentoSoggetti, boolean createMappingErogazioneFruizione) throws DriverConfigurazioneException{
		
		// Reset
		if(reset){
			try{
				this.log.info("Configurazione, reset in corso (Reset configurazione:"+this.gestioneConfigurazione+")...");
				this.gestoreCRUD.reset(this.gestioneConfigurazione);
				this.log.info("Configurazione, reset effettuato.");
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Reset del Configurazione non riuscita: "+e.getMessage(),e);
			}
		}
		
		
		// Soggetti
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				soggetto.setSuperUser(this.superUser);
				
				// TODO GESTIRE TRAMITE FACTORY?		
				// Non e' possibile farlo in questo punto, non avendo i protocolli
				// Gestirlo prima o dopo quando si e' in possesso della protocol factory.
//				if(CostantiConfigurazione.CONFIGURAZIONE_DB.equals(this.tipoBEDestinazione)){
//					if(soggetto.getIdentificativoPorta()==null)
//						soggetto.setIdentificativoPorta(soggetto.getNome()+"SPCoopIT");
//				}
				
				IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
				if( (reset==false) && this.gestoreCRUD.existsSoggetto(idSoggetto)){
					if(aggiornamentoSoggetti){
						this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updateSoggetto(soggetto);
						this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
					}
				}else{
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
					this.gestoreCRUD.createSoggetto(soggetto);
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante la conversione XML dei soggetti: "+e.getMessage(),e);
		}
		
		// Servizi Applicativi
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				for(int j=0;j<soggetto.sizeServizioApplicativoList();j++){
					ServizioApplicativo servizioApplicativo = soggetto.getServizioApplicativo(j);
					this.addServizioApplicativo(servizioApplicativo,soggetto,null,reset);				
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante la conversione XML dei servizi applicativi: "+e.getMessage(),e);
		}
		
		// Porte Delegate
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				HashMap<IDPortaDelegata, PortaDelegata> listPDModificare = new  HashMap<>(); 
				for(int j=0;j<soggetto.sizePortaDelegataList();j++){
					PortaDelegata pd = soggetto.getPortaDelegata(j);
					
					pd.setTipoSoggettoProprietario(soggetto.getTipo());
					pd.setNomeSoggettoProprietario(soggetto.getNome());
					
					impostaInformazioniConfigurazione_PortaDelegata(pd);
					
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(pd.getNome());			
					if( (reset==false) && this.gestoreCRUD.existsPortaDelegata(idPD)){
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updatePortaDelegata(pd);
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
					}else{
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
						this.gestoreCRUD.createPortaDelegata(pd);
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
					}
					
					if(createMappingErogazioneFruizione && this.gestoreCRUD instanceof DriverConfigurazioneDB) {
						DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
						Connection con = null;
						try {
							con = driver.getConnection("XMLDataConverter.mappingFruizione");
							IDSoggetto idFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
							IDServizio idServizio = toIdServizio(pd);
							boolean existsMapping = DBMappingUtils.existsMappingFruizione(idServizio, idFruitore, idPD, con, driver.getTipoDB());
							if(!existsMapping) {
								boolean isDefault = false;
								String nomeMapping = null;
								boolean create = false;
								if(SubscriptionUtils.isPortaDelegataUtilizzabileComeDefault(pd)) {
									nomeMapping = SubscriptionUtils.getDefaultMappingName();
									isDefault = true;
									if(DBMappingUtils.existsIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, driver.getTipoDB())==false) {
										create = true;
									}
								}
								else {
									if(PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(pd.getAzione().getIdentificazione())) {
										// gia' nel nuovo formato devo solo creare il mapping che per qualche motivo si è perso
										if(pd.getAzione().sizeAzioneDelegataList()<=0) {
											this.log.error("Trovata porta delegata "+pd.getNome()+"] con un'identificazione dell'azione delegata senza pero' possedere azioni delegate");
											create=false;
										}
										else {
											String nomeAzione = pd.getAzione().getAzioneDelegata(0); // utilizzo un'azione a caso.
											nomeMapping = nomeAzione;
											idServizio.setAzione(nomeAzione);
											if(DBMappingUtils.existsIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, driver.getTipoDB())==false) {
												create = true;
											}
										}
									}
									else {
										nomeMapping = pd.getAzione().getNome();
										if(nomeMapping!=null && !"".equals(nomeMapping)) {
											idServizio.setAzione(pd.getAzione().getNome());
											if(DBMappingUtils.existsIDPortaDelegataAssociataAzione(idServizio, idFruitore, con, driver.getTipoDB())==false) {
												create = true;
												
												// modifico porta applicativa adeguandola alla nuova specifica
												SubscriptionUtils.setAzioneDelegate(pd, 
														null, // nome porta delegante lo imposto dopo aver trovato la porta di default. 
														pd.getAzione().getNome());
												
												listPDModificare.put(idPD, pd);
											}
										}
									}
								}
								if(create) {
									this.log.info("Creazione mapping di fruizione (nome:"+nomeMapping+" default:"+isDefault+") tra Porta delegata ["+pd.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] creazione in corso...");
									DBMappingUtils.createMappingFruizione(nomeMapping, isDefault, idServizio, idFruitore, idPD, con, driver.getTipoDB());
									this.log.info("Creazione mapping di fruizione (nome:"+nomeMapping+" default:"+isDefault+") tra Porta delegata ["+pd.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] creato.");
								}
								
							}
						}finally {
							try {
								if(driver.isAtomica()) {
									con.commit();
								}
							}catch(Throwable t) {}
							driver.releaseConnection(con);
						}
					}
				}
				if(listPDModificare!=null && listPDModificare.size()>0) {
					DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
					Connection con = null;
					try {
						con = driver.getConnection("XMLDataConverter.mappingFruizione");
						for (IDPortaDelegata idPD : listPDModificare.keySet()) {
							PortaDelegata pd = listPDModificare.get(idPD);
							
							IDSoggetto idFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
							IDServizio idServizio = this.toIdServizio(pd);
							IDPortaDelegata idPDDefault = DBMappingUtils.getIDPortaDelegataAssociataDefault(idServizio, idFruitore, con, driver.getTipoDB());
							
							if(idPDDefault==null) {
								ServiceBinding serviceBinding = readServiceBinding(idServizio, con);
								Subscription subcription = XMLDataConverter.getSubscriptionDefault(serviceBinding, idFruitore, idServizio);
								PortaDelegata pdDefault = subcription.getPortaDelegata();
								MappingFruizionePortaDelegata mapping = subcription.getMapping();
								idPDDefault = mapping.getIdPortaDelegata();
								
								if(this.gestoreCRUD.existsPortaDelegata(idPDDefault) == false) {
									// creo porta delegata standard
									this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione delegante ["+pdDefault.getNome()+"] in corso...");
									this.gestoreCRUD.createPortaDelegata(pdDefault);
									this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione delegante ["+pdDefault.getNome()+"].");
								}
								
								this.log.info("Creazione mapping di fruizione di default (nome:"+mapping.getNome()+" default:"+mapping.isDefault()+") tra Porta delegata ["+pdDefault.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] creazione delegante in corso...");
								DBMappingUtils.createMappingFruizione(mapping.getNome(), mapping.isDefault(), idServizio, idFruitore, idPDDefault, con, driver.getTipoDB());
								this.log.info("Creazione mapping di fruizione di default (nome:"+mapping.getNome()+" default:"+mapping.isDefault()+") tra Porta delegata ["+pdDefault.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] creato delegante.");
							}
							
							pd.getAzione().setNomePortaDelegante(idPDDefault.getNome());
							
							// comunque devo aggiornare i dati per delegatedBy
							this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento per nomeDelegante in corso...");
							this.gestoreCRUD.updatePortaDelegata(pd);
							this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato per nomeDelegante.");
						}
					}finally {
						try {
							if(driver.isAtomica()) {
								con.commit();
							}
						}catch(Throwable t) {}
						driver.releaseConnection(con);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante la conversione XML delle porte delegate: "+e.getMessage(),e);
		}
		
		// Porte Applicative
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				HashMap<IDPortaApplicativa, PortaApplicativa> listPAModificare = new  HashMap<>(); 
				for(int j=0;j<soggetto.sizePortaApplicativaList();j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					
					pa.setTipoSoggettoProprietario(soggetto.getTipo());
					pa.setNomeSoggettoProprietario(soggetto.getNome());
					
					impostaInformazioniConfigurazione_PortaApplicativa(pa);
					
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(pa.getNome());
					if( (reset==false) && this.gestoreCRUD.existsPortaApplicativa(idPA)){
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updatePortaApplicativa(pa);
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
					}else{
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
						this.gestoreCRUD.createPortaApplicativa(pa);
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
					}
					
					if(createMappingErogazioneFruizione && this.gestoreCRUD instanceof DriverConfigurazioneDB) {
						DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
						Connection con = null;
						try {
							con = driver.getConnection("XMLDataConverter.mappingErogazione");
							IDServizio idServizio = this.toIdServizio(pa);
							boolean existsMapping = DBMappingUtils.existsMappingErogazione(idServizio, idPA, con, driver.getTipoDB());
							if(!existsMapping) {
								boolean isDefault = false;
								String nomeMapping = null;
								boolean create = false;
								if(ImplementationUtils.isPortaApplicativaUtilizzabileComeDefault(pa)) {
									nomeMapping = ImplementationUtils.getDefaultMappingName();
									isDefault = true;
									if(DBMappingUtils.existsIDPortaApplicativaAssociataDefault(idServizio, con, driver.getTipoDB())==false) {
										create = true;
									}
								}
								else {
									if(PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(pa.getAzione().getIdentificazione())) {
										// gia' nel nuovo formato devo solo creare il mapping che per qualche motivo si è perso
										if(pa.getAzione().sizeAzioneDelegataList()<=0) {
											this.log.error("Trovata porta applicativa "+pa.getNome()+"] con un'identificazione dell'azione delegata senza pero' possedere azioni delegate");
											create=false;
										}
										else {
											String nomeAzione = pa.getAzione().getAzioneDelegata(0); // utilizzo un'azione a caso.
											nomeMapping = nomeAzione;
											idServizio.setAzione(nomeAzione);
											if(DBMappingUtils.existsIDPortaApplicativaAssociataAzione(idServizio, con, driver.getTipoDB())==false) {
												create = true;
											}
										}
									}
									else {
										nomeMapping = pa.getAzione().getNome();
										if(nomeMapping!=null && !"".equals(nomeMapping)) {
											idServizio.setAzione(pa.getAzione().getNome());
											if(DBMappingUtils.existsIDPortaApplicativaAssociataAzione(idServizio, con, driver.getTipoDB())==false) {
												create = true;
												
												// modifico porta applicativa adeguandola alla nuova specifica
												ImplementationUtils.setAzioneDelegate(pa, 
														null, // nome porta delegante lo imposto dopo aver trovato la porta di default. 
														pa.getAzione().getNome());

												listPAModificare.put(idPA, pa);
											}
										}
									}
								}
								if(create) {
									this.log.info("Creazione mapping di erogazione (nome:"+nomeMapping+" default:"+isDefault+") tra Porta Applicativa ["+pa.getNome()+"] e servizio ["+idServizio+"] creazione in corso...");
									DBMappingUtils.createMappingErogazione(nomeMapping, isDefault, idServizio, idPA, con, driver.getTipoDB());
									this.log.info("Creazione mapping di erogazione (nome:"+nomeMapping+" default:"+isDefault+") tra Porta Applicativa ["+pa.getNome()+"] e servizio ["+idServizio+"] creato.");
								}
								
							}
						}finally {
							try {
								if(driver.isAtomica()) {
									con.commit();
								}
							}catch(Throwable t) {}
							driver.releaseConnection(con);
						}
					}
				}
				if(listPAModificare!=null && listPAModificare.size()>0) {
					DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
					Connection con = null;
					try {
						con = driver.getConnection("XMLDataConverter.mappingErogazione");
						for (IDPortaApplicativa idPA : listPAModificare.keySet()) {
							PortaApplicativa pa = listPAModificare.get(idPA);
							
							IDServizio idServizio = this.toIdServizio(pa);
							IDPortaApplicativa idPADefault = DBMappingUtils.getIDPortaApplicativaAssociataDefault(idServizio, con, driver.getTipoDB());
							
							if(idPADefault==null) {
								// Creo una porta applicativa automaticamente simile a quella delegatedBy
								
								ServiceBinding serviceBinding = readServiceBinding(idServizio, con);
								Implementation implementation = XMLDataConverter.getImplementationDefault(serviceBinding, idServizio);
								PortaApplicativa paDefault = implementation.getPortaApplicativa();
								MappingErogazionePortaApplicativa mapping = implementation.getMapping();
								idPADefault = mapping.getIdPortaApplicativa();
								if(this.gestoreCRUD.existsPortaApplicativa(idPADefault) == false) {
									// creo porta applicativa standard
									this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione delegante ["+paDefault.getNome()+"] in corso...");
									this.gestoreCRUD.createPortaApplicativa(paDefault);
									this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione delegante ["+paDefault.getNome()+"].");
								}
								
								this.log.info("Creazione mapping di erogazione di default (nome:"+mapping.getNome()+" default:"+mapping.isDefault()+") tra Porta Applicativa ["+paDefault.getNome()+"] e servizio ["+idServizio+"] creazione delegante in corso...");
								DBMappingUtils.createMappingErogazione(mapping.getNome(), mapping.isDefault(), idServizio, idPADefault, con, driver.getTipoDB());
								this.log.info("Creazione mapping di erogazione di default (nome:"+mapping.getNome()+" default:"+mapping.isDefault()+") tra Porta Applicativa ["+paDefault.getNome()+"] e servizio ["+idServizio+"] creato delegante.");
						
							}
							
							pa.getAzione().setNomePortaDelegante(idPADefault.getNome());
							
							// comunque devo aggiornare i dati per delegatedBy
							this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento per nomeDelegante in corso...");
							this.gestoreCRUD.updatePortaApplicativa(pa);
							this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato per nomeDelegante.");
						}
					}finally {
						try {
							if(driver.isAtomica()) {
								con.commit();
							}
						}catch(Throwable t) {}
						driver.releaseConnection(con);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante la conversione XML delle porte applicative: "+e.getMessage(),e);
		}
		
		
		if(this.gestioneConfigurazione){
			// RoutingTable
			try{
				this.log.info("Routing Table, creazione/aggiornamento in corso...");
				if(this.sorgenteConfigurazione.getConfigurazione()!=null &&
						this.sorgenteConfigurazione.getConfigurazione().getRoutingTable()!=null ){
					if(reset){
						this.gestoreCRUD.createRoutingTable(this.sorgenteConfigurazione.getConfigurazione().getRoutingTable());
						this.log.info("Routing Table, creazione effettuata.");
					}
					else{
						this.gestoreCRUD.updateRoutingTable(this.sorgenteConfigurazione.getConfigurazione().getRoutingTable());
						this.log.info("Routing Table, aggiornamento effettuato.");
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante la conversione XML della routing table: "+e.getMessage(),e);
			}
		
			// AccessoRegistro
			try{
				this.log.info("Accesso registro, creazione in corso...");
				if(this.sorgenteConfigurazione.getConfigurazione()!=null && 
						this.sorgenteConfigurazione.getConfigurazione().getAccessoRegistro()!=null){
					if(reset){
						this.gestoreCRUD.createAccessoRegistro(this.sorgenteConfigurazione.getConfigurazione().getAccessoRegistro());
						this.log.info("Accesso registro, creazione effettuata.");
					}
					else{
						this.gestoreCRUD.updateAccessoRegistro(this.sorgenteConfigurazione.getConfigurazione().getAccessoRegistro());
						this.log.info("Accesso registro, aggiornamento effettuato.");
					}						
				}
			
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante la conversione XML dei dati di accesso al registro: "+e.getMessage(),e);
			}
			
			// GestioneErrore di Cooperazione
			try{
				this.log.info("GestioneErrore, creazione in corso...");
				if(this.sorgenteConfigurazione.getConfigurazione()!=null && 
						this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore()!=null &&
						this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteCooperazione()!=null){
					
					if(reset){
						this.gestoreCRUD.createGestioneErroreComponenteCooperazione(this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteCooperazione());
						this.log.info("GestioneErrore, creazione effettuata.");
					}
					else{
						this.gestoreCRUD.updateGestioneErroreComponenteCooperazione(this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteCooperazione());
						this.log.info("GestioneErrore, aggiornamento effettuato.");
					}
								
				}
				
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante la conversione XML dei dati di gestione dell'errore del connettore per il componente di cooperazione: "+
						e.getMessage(),e);
			}
			
			// GestioneErrore di Integrazione
			try{
				this.log.info("GestioneErrore, creazione in corso...");
				if(this.sorgenteConfigurazione.getConfigurazione()!=null && 
						this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore()!=null &&
						this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteIntegrazione()!=null){
					
					if(reset){
						this.gestoreCRUD.createGestioneErroreComponenteIntegrazione(this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteIntegrazione());
						this.log.info("GestioneErrore, creazione effettuata.");
					}
					else{
						this.gestoreCRUD.updateGestioneErroreComponenteIntegrazione(this.sorgenteConfigurazione.getConfigurazione().getGestioneErrore().getComponenteIntegrazione());
						this.log.info("GestioneErrore, aggiornamento effettuato.");
					}
				}

			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante la conversione XML dei dati di gestione dell'errore del connettore per il componente di integrazione: "
						+e.getMessage(),e);
			}
			
			// Configurazione
			try{
				this.log.info("Configurazione, creazione in corso...");
				if(this.sorgenteConfigurazione.getConfigurazione()!=null){
					
					if(reset){
						this.gestoreCRUD.createConfigurazione(this.sorgenteConfigurazione.getConfigurazione());
						this.log.info("Configurazione, creazione effettuata.");
					}else{
						this.gestoreCRUD.updateConfigurazione(this.sorgenteConfigurazione.getConfigurazione());
						this.log.info("Configurazione, aggiornamento effettuato.");
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante la conversione XML della Configurazione: "+e.getMessage(),e);
			}
		}
	}
	
	private ServiceBinding readServiceBinding(IDServizio idServizio, Connection con) throws DriverConfigurazioneException{
		DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
		long idServizioAsLong = -1;
		try {
			idServizioAsLong = DBUtils.getIdAccordoServizioParteSpecifica(idServizio, con, driver.getTipoDB());
			if(idServizioAsLong<=0) {
				throw new Exception("Non trovato");
			}
		}catch(Exception e) {
			throw new DriverConfigurazioneException("Accordo Parte Specifica non trovato ["+idServizio+"]");
		}
		
		PreparedStatement stm = null;
		ResultSet rs = null;
		try
		{
			
			// NOTA: nell'APS, il soggetto e la versione sono obbligatori
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(driver.getTipoDB());
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObject.addSelectField("message_type");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = "+CostantiDB.ACCORDI+".id");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+"id = ?");
			String query = sqlQueryObject.createSQLQuery();
			stm=con.prepareStatement(query);
			stm.setLong(1, idServizioAsLong);
			
			rs=stm.executeQuery();

			if(rs.next()){
				return ServiceBinding.toEnumConstant(rs.getString("message_type"),true);
			}

			throw new DriverConfigurazioneException("Accordo Parte Specifica non trovato ["+idServizio+"] (Lettura ServiceBinding)");

		}catch (SQLException e) {
			throw new DriverConfigurazioneException(e);
		}catch (Exception e) {
			throw new DriverConfigurazioneException(e);
		}finally
		{
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}

		}
	}
	
	
	
	public void delete(boolean deleteSoggetti, boolean deleteMappingErogazioneFruizione) throws DriverConfigurazioneException{
		
		
		// Porte Delegate
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				for(int j=0;j<soggetto.sizePortaDelegataList();j++){
					PortaDelegata pd = soggetto.getPortaDelegata(j);
					pd.setTipoSoggettoProprietario(soggetto.getTipo());
					pd.setNomeSoggettoProprietario(soggetto.getNome());	
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(pd.getNome());		
					
					if(deleteMappingErogazioneFruizione && this.gestoreCRUD instanceof DriverConfigurazioneDB) {
						DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
						Connection con = null;
						try {
							con = driver.getConnection("XMLDataConverter.mappingFruizione");
							IDSoggetto idFruitore = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
							IDServizio idServizio = toIdServizio(pd);
							boolean existsMapping = DBMappingUtils.existsMappingFruizione(idServizio, idFruitore, idPD, con, driver.getTipoDB());
							if(existsMapping) {
								this.log.info("Eliminazione mapping di fruizione tra Porta delegata ["+pd.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] eliminazione in corso...");
								DBMappingUtils.deleteMappingFruizione(idServizio, idFruitore, idPD, con, driver.getTipoDB());
								this.log.info("Eliminazione mapping di fruizione tra Porta delegata ["+pd.getNome()+"], fruitore ["+idFruitore+"] e servizio ["+idServizio+"] eliminato.");
							}
						}finally {
							try {
								if(driver.isAtomica()) {
									con.commit();
								}
							}catch(Throwable t) {}
							driver.releaseConnection(con);
						}
					}
					
					this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if(this.gestoreCRUD.existsPortaDelegata(idPD)){
						this.gestoreCRUD.deletePortaDelegata(((IDriverConfigurazioneGet)this.gestoreCRUD).getPortaDelegata(idPD));
					}
					this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminata.");
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante l'eliminazione delle porte delegate: "+e.getMessage(),e);
		}
		
		// Porte Applicative
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				for(int j=0;j<soggetto.sizePortaApplicativaList();j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);					
					pa.setTipoSoggettoProprietario(soggetto.getTipo());
					pa.setNomeSoggettoProprietario(soggetto.getNome());				
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(pa.getNome());
					
					if(deleteMappingErogazioneFruizione && this.gestoreCRUD instanceof DriverConfigurazioneDB) {
						DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.gestoreCRUD;
						Connection con = null;
						try {
							con = driver.getConnection("XMLDataConverter.mappingErogazione");
							IDServizio idServizio = this.toIdServizio(pa);
							boolean existsMapping = DBMappingUtils.existsMappingErogazione(idServizio, idPA, con, driver.getTipoDB());
							if(existsMapping) {
								this.log.info("Eliminazione mapping di erogazione tra Porta Applicativa ["+pa.getNome()+"] e servizio ["+idServizio+"] eliminazione in corso...");
								DBMappingUtils.deleteMappingErogazione(idServizio, idPA, con, driver.getTipoDB());
								this.log.info("Eliminazione mapping di erogazione tra Porta Applicativa ["+pa.getNome()+"] e servizio ["+idServizio+"] eliminato.");
							}
						}finally {
							try {
								if(driver.isAtomica()) {
									con.commit();
								}
							}catch(Throwable t) {}
							driver.releaseConnection(con);
						}
					}
					
					this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsPortaApplicativa(idPA)){
						this.gestoreCRUD.deletePortaApplicativa(((IDriverConfigurazioneGet)this.gestoreCRUD).getPortaApplicativa(idPA));
					}
					this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminata.");
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante l'eliminazione delle porte applicative: "+e.getMessage(),e);
		}
		
		
		// Servizi Applicativi
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				for(int j=0;j<soggetto.sizeServizioApplicativoList();j++){
					ServizioApplicativo servizioApplicativo = soggetto.getServizioApplicativo(j);
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(idSoggetto);
					idSA.setNome(servizioApplicativo.getNome());
					this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsServizioApplicativo(idSA)){
						this.gestoreCRUD.deleteServizioApplicativo(((IDriverConfigurazioneGet)this.gestoreCRUD).getServizioApplicativo(idSA));
					}
					this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminato.");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverConfigurazioneException("Errore durante l'eliminazione dei servizi applicativi: "+e.getMessage(),e);
		}
		
		
		if(deleteSoggetti){
			// Soggetti
			try{
				for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
					Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsSoggetto(idSoggetto)){
						this.gestoreCRUD.deleteSoggetto(((IDriverConfigurazioneGet)this.gestoreCRUD).getSoggetto(idSoggetto));
					}
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminato.");
				}
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverConfigurazioneException("Errore durante l'eliminazione dei soggetti: "+e.getMessage(),e);
			}
		}
		
	}

	
	private static void gestioneDefaultConnettoreHTTP(Connettore connettore){
		if(connettore.getProperties()!=null){
			if(connettore.getProperties().containsKey(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE)==false){
				Property cp = new Property();
				cp.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
				cp.setValore("jks"); // default
				connettore.addProperty(cp);
			}
			if(connettore.getProperties().containsKey(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM)==false){
				Property cp = new Property();
				cp.setNome(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
				cp.setValore(TrustManagerFactory.getDefaultAlgorithm()); // default
				connettore.addProperty(cp);
			}
			if(connettore.getProperties().containsKey(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION)){
				if(connettore.getProperties().containsKey(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE)==false){
					Property cp = new Property();
					cp.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
					cp.setValore("jks"); // default
					connettore.addProperty(cp);
				}
				if(connettore.getProperties().containsKey(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM)==false){
					Property cp = new Property();
					cp.setNome(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
					cp.setValore(KeyManagerFactory.getDefaultAlgorithm()); // default
					connettore.addProperty(cp);
				}
			}	
		}
	}
	
	public static void impostaInformazioniConfigurazione_PortaDelegata(PortaDelegata pd){
		// messageSecurity
		if(pd.getMessageSecurity()!=null && 
				( 
					(pd.getMessageSecurity().getRequestFlow()!=null && pd.getMessageSecurity().getRequestFlow().sizeParameterList()>0) 
					  || 
					(pd.getMessageSecurity().getResponseFlow()!=null && pd.getMessageSecurity().getResponseFlow().sizeParameterList()>0)
				)
		){
			pd.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
		}
	}
	
	public static void impostaInformazioniConfigurazione_PortaApplicativa(PortaApplicativa pa){
		// messageSecurity
		if(pa.getMessageSecurity()!=null && 
				( 
					(pa.getMessageSecurity().getRequestFlow()!=null && pa.getMessageSecurity().getRequestFlow().sizeParameterList()>0) 
					  || 
					(pa.getMessageSecurity().getResponseFlow()!=null && pa.getMessageSecurity().getResponseFlow().sizeParameterList()>0)
				)
		){
			pa.setStatoMessageSecurity(CostantiConfigurazione.ABILITATO.toString());
		}
	}
	
	public static void impostaInformazioniConfigurazione_ServizioApplicativo(ServizioApplicativo servizioApplicativo,Soggetto soggettoProprietario,
			String posizione,Logger log,String tipoBEDestinazione){
		
		// **** InvocazionePorta ****
		if(servizioApplicativo.getInvocazionePorta()==null){
			InvocazionePorta saInv = new InvocazionePorta();
			Credenziali credenziali = new Credenziali();
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.NONE));
			saInv.addCredenziali(credenziali);
			InvocazionePortaGestioneErrore ge = new InvocazionePortaGestioneErrore();
			ge.setFault(CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP);
			saInv.setGestioneErrore(ge);
			servizioApplicativo.setInvocazionePorta(saInv);
		}else{
			if(servizioApplicativo.getInvocazionePorta().getGestioneErrore()==null){
				InvocazionePortaGestioneErrore ge = new InvocazionePortaGestioneErrore();
				ge.setFault(CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP);
				servizioApplicativo.getInvocazionePorta().setGestioneErrore(ge);
			}else if(servizioApplicativo.getInvocazionePorta().getGestioneErrore().getFault()==null){
				servizioApplicativo.getInvocazionePorta().getGestioneErrore().setFault(CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP);	
			}
			if(servizioApplicativo.getInvocazionePorta().sizeCredenzialiList()==0){
				Credenziali credenziali = new Credenziali();
				credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.NONE));
				servizioApplicativo.getInvocazionePorta().addCredenziali(credenziali);
			}
		}		
		
		// **** InvocazioneServizio ****
		// Check connettori non definit all'interno del servizio
		if(servizioApplicativo.getInvocazioneServizio()!=null && servizioApplicativo.getInvocazioneServizio().getConnettore()!=null){
			if(servizioApplicativo.getInvocazioneServizio().getConnettore().getTipo()==null){
				// ricavo il connettore dal soggetto
				for(int con=0;con<soggettoProprietario.sizeConnettoreList();con++){
					if(soggettoProprietario.getConnettore(con).getNome().equals(servizioApplicativo.getInvocazioneServizio().getConnettore().getNome())){
						servizioApplicativo.getInvocazioneServizio().setConnettore(soggettoProprietario.getConnettore(con));
						break;
					}
				}
				log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+posizione+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" connettore inv servizio di tipo, dopo ricerca ["+servizioApplicativo.getInvocazioneServizio().getConnettore().getTipo()+"]");
			}else{
				log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+posizione+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" connettore inv servizio di tipo ["+servizioApplicativo.getInvocazioneServizio().getConnettore().getTipo()+"]");
			}
			if(CostantiConfigurazione.CONFIGURAZIONE_DB.equals(tipoBEDestinazione)){
				// I nomi vengono autogenerati dal driver
				servizioApplicativo.getInvocazioneServizio().getConnettore().setNome(null);
				// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
				String tipoConnettore = servizioApplicativo.getInvocazioneServizio().getConnettore().getTipo();
				if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
					servizioApplicativo.getInvocazioneServizio().getConnettore().setCustom(true);
				}
				// Gestione default per connettore https
				if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
					gestioneDefaultConnettoreHTTP(servizioApplicativo.getInvocazioneServizio().getConnettore());
				}
			}
		}else if(servizioApplicativo.getInvocazioneServizio()==null){
			Credenziali credenziali = new Credenziali();
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.NONE));
			InvocazioneServizio invServizio = new InvocazioneServizio();
			invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.toEnumConstant(CostantiConfigurazione.NONE));
			invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
			servizioApplicativo.setInvocazioneServizio(invServizio);
		}
		// GetMessage
		if(servizioApplicativo.getInvocazioneServizio()!=null){
			if(servizioApplicativo.getInvocazioneServizio().getGetMessage()==null){
				servizioApplicativo.getInvocazioneServizio().setGetMessage(CostantiConfigurazione.DISABILITATO);
			}
		}
		
		// **** RispostaAsincrona ****
		// Check connettori non definit all'interno del servizio
		if(servizioApplicativo.getRispostaAsincrona()!=null && servizioApplicativo.getRispostaAsincrona().getConnettore()!=null){
			if(servizioApplicativo.getRispostaAsincrona().getConnettore().getTipo()==null){
				// ricavo il connettore dal soggetto
				for(int con=0;con<soggettoProprietario.sizeConnettoreList();con++){
					if(soggettoProprietario.getConnettore(con).getNome().equals(servizioApplicativo.getRispostaAsincrona().getConnettore().getNome())){
						servizioApplicativo.getRispostaAsincrona().setConnettore(soggettoProprietario.getConnettore(con));
						break;
					}
				}
				log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+posizione+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" connettore risposta asincrona di tipo, dopo ricerca ["+servizioApplicativo.getRispostaAsincrona().getConnettore().getTipo()+"]");
			}else{
				log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+posizione+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" connettore risposta asincrona di tipo ["+servizioApplicativo.getRispostaAsincrona().getConnettore().getTipo()+"]");
			}
			if(CostantiConfigurazione.CONFIGURAZIONE_DB.equals(tipoBEDestinazione)){
				// I nomi vengono autogenerati dal driver
				servizioApplicativo.getRispostaAsincrona().getConnettore().setNome(null);
				// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
				String tipoConnettore = servizioApplicativo.getRispostaAsincrona().getConnettore().getTipo();
				if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
					servizioApplicativo.getRispostaAsincrona().getConnettore().setCustom(true);
				}
				// Gestione default per connettore https
				if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
					gestioneDefaultConnettoreHTTP(servizioApplicativo.getRispostaAsincrona().getConnettore());
				}
			}
		}else if(servizioApplicativo.getRispostaAsincrona()==null){
			Credenziali credenziali = new Credenziali();
			credenziali.setTipo(CredenzialeTipo.toEnumConstant(CostantiConfigurazione.NONE));
			RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
			rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.toEnumConstant(CostantiConfigurazione.NONE));
			rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
			servizioApplicativo.setRispostaAsincrona(rispostaAsinc);
		}
		// GetMessage
		if(servizioApplicativo.getRispostaAsincrona()!=null){
			if(servizioApplicativo.getRispostaAsincrona().getGetMessage()==null){
				servizioApplicativo.getRispostaAsincrona().setGetMessage(CostantiConfigurazione.DISABILITATO);
			}
		}
		
		
		// **** altre info per driver DB ***
		if(CostantiConfigurazione.REGISTRO_DB.equals(tipoBEDestinazione)){
			if(servizioApplicativo.getInvocazioneServizio()!=null && servizioApplicativo.getInvocazioneServizio().getConnettore()!=null){
				// I nomi dei connettorivengono autogenerati dal driver
				servizioApplicativo.getInvocazioneServizio().getConnettore().setNome(null);
				// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
				String tipoConnettore = servizioApplicativo.getInvocazioneServizio().getConnettore().getTipo();
				if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
					servizioApplicativo.getInvocazioneServizio().getConnettore().setCustom(true);
				}
				// Gestione default per connettore https
				if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
					gestioneDefaultConnettoreHTTP(servizioApplicativo.getInvocazioneServizio().getConnettore());
				}
			}
			if(servizioApplicativo.getRispostaAsincrona()!=null && servizioApplicativo.getRispostaAsincrona().getConnettore()!=null){
				// I nomi dei connettorivengono autogenerati dal driver
				servizioApplicativo.getRispostaAsincrona().getConnettore().setNome(null);
				// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
				String tipoConnettore = servizioApplicativo.getRispostaAsincrona().getConnettore().getTipo();
				if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
					 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
					servizioApplicativo.getRispostaAsincrona().getConnettore().setCustom(true);
				}
				// Gestione default per connettore https
				if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
					gestioneDefaultConnettoreHTTP(servizioApplicativo.getRispostaAsincrona().getConnettore());
				}
			}
		}
	}
	
	public static void impostaInformazioniConfigurazione_ServizioApplicativo_update(ServizioApplicativo servizioApplicativo,ServizioApplicativo old){
		servizioApplicativo.setId(old.getId());
		if(servizioApplicativo.getInvocazioneServizio()!=null){
			if(servizioApplicativo.getInvocazioneServizio().getConnettore()==null){
				if(old.getInvocazioneServizio()!=null)
					servizioApplicativo.getInvocazioneServizio().setConnettore(old.getInvocazioneServizio().getConnettore());
			}else{
				if(old.getInvocazioneServizio()!=null && old.getInvocazioneServizio().getConnettore()!=null){
					servizioApplicativo.getInvocazioneServizio().getConnettore().setId(old.getInvocazioneServizio().getConnettore().getId());
					servizioApplicativo.getInvocazioneServizio().getConnettore().setNome(old.getInvocazioneServizio().getConnettore().getNome());
				}
			}
		}
		if(servizioApplicativo.getRispostaAsincrona()!=null){
			if(servizioApplicativo.getRispostaAsincrona().getConnettore()==null){
				if(old.getRispostaAsincrona()!=null)
					servizioApplicativo.getRispostaAsincrona().setConnettore(old.getRispostaAsincrona().getConnettore());
			}else{
				if(old.getRispostaAsincrona()!=null && old.getRispostaAsincrona().getConnettore()!=null){
					servizioApplicativo.getRispostaAsincrona().getConnettore().setId(old.getRispostaAsincrona().getConnettore().getId());
					servizioApplicativo.getRispostaAsincrona().getConnettore().setNome(old.getRispostaAsincrona().getConnettore().getNome());
				}
			}
		}
	}
	
	private void addServizioApplicativo(ServizioApplicativo servizioApplicativo,Soggetto soggettoProprietario,String posizione,boolean reset) throws Exception{
		servizioApplicativo.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
		servizioApplicativo.setNomeSoggettoProprietario(soggettoProprietario.getNome());
		String pos = "";
		if(posizione!=null){
			pos = "["+posizione+"]";
		}
		
		impostaInformazioniConfigurazione_ServizioApplicativo(servizioApplicativo, soggettoProprietario, 
				pos, this.log, this.tipoBEDestinazione);		
		
		// *** effettua create/update/delete
		IDSoggetto idSoggetto = new IDSoggetto(soggettoProprietario.getTipo(),soggettoProprietario.getNome());
		IDServizioApplicativo idSA = new IDServizioApplicativo();
		idSA.setIdSoggettoProprietario(idSoggetto);
		idSA.setNome(servizioApplicativo.getNome());
		if( (reset==false) && this.gestoreCRUD.existsServizioApplicativo(idSA)){
			this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+pos+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" aggiornamento in corso...");
			
			ServizioApplicativo old = ((IDriverConfigurazioneGet) this.gestoreCRUD).getServizioApplicativo(idSA);
			
			impostaInformazioniConfigurazione_ServizioApplicativo_update(servizioApplicativo, old);
			
			this.gestoreCRUD.updateServizioApplicativo(servizioApplicativo);
			this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+pos+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" aggiornato.");
		}else{
			this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+pos+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" creazione in corso...");
			this.gestoreCRUD.createServizioApplicativo(servizioApplicativo);
			this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+pos+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" creato.");
		}
		
		
		
	}
}
