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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
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
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
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
	
	
	
	public void convertXML(boolean reset,boolean aggiornamentoSoggetti) throws DriverConfigurazioneException{
		
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
				for(int j=0;j<soggetto.sizePortaDelegataList();j++){
					PortaDelegata pd = soggetto.getPortaDelegata(j);
					
					String nomePD = pd.getNome();
					if(pd.getLocation()!=null){
						nomePD =pd.getLocation();
					}
					nomePD = soggetto.getTipo()+soggetto.getNome()+"_"+nomePD;
					
					// creazione servizi applicativi interni alla porta delegata
					for(int k=0; k<pd.sizeServizioApplicativoList();k++){
						ServizioApplicativo saPD = pd.getServizioApplicativo(k);
						if(saPD.getInvocazionePorta()!=null || saPD.getInvocazioneServizio()!=null || saPD.getRispostaAsincrona()!=null){
							saPD.setNome(nomePD+"_"+saPD.getNome());
							this.addServizioApplicativo(saPD,soggetto,"Definizione Interna alla PortaDelegata",reset);			
						}
					}
					
					pd.setTipoSoggettoProprietario(soggetto.getTipo());
					pd.setNomeSoggettoProprietario(soggetto.getNome());
					
					impostaInformazioniConfigurazione_PortaDelegata(pd);
					
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setSoggettoFruitore(idSoggetto);
					idPD.setLocationPD(pd.getNome());
					if(pd.getLocation()!=null){
						idPD.setLocationPD(pd.getLocation());
					}					
					if( (reset==false) && this.gestoreCRUD.existsPortaDelegata(idPD)){
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updatePortaDelegata(pd);
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
					}else{
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
						this.gestoreCRUD.createPortaDelegata(pd);
						this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
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
				for(int j=0;j<soggetto.sizePortaApplicativaList();j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					
					String nomePA = pa.getNome();
					nomePA = soggetto.getTipo()+soggetto.getNome()+"_"+nomePA;
					
					// creazione servizi applicativi interni alla porta applicativa
					for(int k=0; k<pa.sizeServizioApplicativoList();k++){
						ServizioApplicativo saPA = pa.getServizioApplicativo(k);
						if(saPA.getInvocazionePorta()!=null || saPA.getInvocazioneServizio()!=null || saPA.getRispostaAsincrona()!=null){
							saPA.setNome(nomePA+"_"+saPA.getNome());
							this.addServizioApplicativo(saPA,soggetto,"Definizione Interna alla PortaApplicativa",reset);		
						}
					}
					
					pa.setTipoSoggettoProprietario(soggetto.getTipo());
					pa.setNomeSoggettoProprietario(soggetto.getNome());
					
					impostaInformazioniConfigurazione_PortaApplicativa(pa);
					
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					//IDPortaApplicativa idPA = new IDPortaApplicativa();
					//String azione = null;
					//if(pa.getAzione()!=null)
					//	azione = pa.getAzione().getNome();
					//idPA.setIDServizio(new IDServizio(idSoggetto,pa.getServizio().getTipo(),pa.getServizio().getNome(),azione));
					if( (reset==false) && this.gestoreCRUD.existsPortaApplicativa(pa.getNome(), idSoggetto)){
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updatePortaApplicativa(pa);
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
					}else{
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
						this.gestoreCRUD.createPortaApplicativa(pa);
						this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
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
	
	
	
	
	public void delete(boolean deleteSoggetti) throws DriverConfigurazioneException{
		
		
		// Porte Delegate
		try{
			for(int i=0; i<this.sorgenteConfigurazione.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteConfigurazione.getSoggetto(i);
				for(int j=0;j<soggetto.sizePortaDelegataList();j++){
					PortaDelegata pd = soggetto.getPortaDelegata(j);
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setSoggettoFruitore(idSoggetto);
					idPD.setLocationPD(pd.getNome());
					if(pd.getLocation()!=null){
						idPD.setLocationPD(pd.getLocation());
					}			
					this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if(this.gestoreCRUD.existsPortaDelegata(idPD)){
						this.gestoreCRUD.deletePortaDelegata(((IDriverConfigurazioneGet)this.gestoreCRUD).getPortaDelegata(idPD));
					}
					this.log.info("Porta delegata ["+pd.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminata.");
					
					
					// eliminazione servizi applicativi interni alla porta delegata
					String nomePD = pd.getNome();
					if(pd.getLocation()!=null){
						nomePD =pd.getLocation();
					}
					nomePD = soggetto.getTipo()+soggetto.getNome()+"_"+nomePD;
					for(int k=0; k<pd.sizeServizioApplicativoList();k++){
						ServizioApplicativo saPD = pd.getServizioApplicativo(k);
						if(saPD.getInvocazionePorta()!=null || saPD.getInvocazioneServizio()!=null || saPD.getRispostaAsincrona()!=null){
							saPD.setNome(nomePD+"_"+saPD.getNome());
							this.log.info("Servizio Applicativo ["+saPD.getNome()+"] [Definizione Interna alla PortaDelegata] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminazione in corso...");
							if( this.gestoreCRUD.existsServizioApplicativo(idSoggetto, saPD.getNome())){
								this.gestoreCRUD.deleteServizioApplicativo(((IDriverConfigurazioneGet)this.gestoreCRUD).getServizioApplicativo(idSoggetto, saPD.getNome()));
							}
							this.log.info("Servizio Applicativo ["+saPD.getNome()+"] [Definizione Interna alla PortaDelegata] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminato.");				
						}
					}
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
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					String azione = null;
					if(pa.getAzione()!=null)
						azione = pa.getAzione().getNome();
					idPA.setIDServizio(new IDServizio(idSoggetto,pa.getServizio().getTipo(),pa.getServizio().getNome(),azione));
					this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsPortaApplicativa(idPA,true)){
						this.gestoreCRUD.deletePortaApplicativa(((IDriverConfigurazioneGet)this.gestoreCRUD).getPortaApplicativa(idPA));
					}
					this.log.info("Porta applicativa ["+pa.getNome()+"] del Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminata.");
					
					
					// eliminazione servizi applicativi interni alla porta applicativa
					String nomePA = pa.getNome();
					nomePA = soggetto.getTipo()+soggetto.getNome()+"_"+nomePA;
					for(int k=0; k<pa.sizeServizioApplicativoList();k++){
						ServizioApplicativo saPA = pa.getServizioApplicativo(k);
						if(saPA.getInvocazionePorta()!=null || saPA.getInvocazioneServizio()!=null || saPA.getRispostaAsincrona()!=null){
							saPA.setNome(nomePA+"_"+saPA.getNome());
							this.log.info("Servizio Applicativo ["+saPA.getNome()+"] [Definizione Interna alla PortaApplicativa] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminazione in corso...");
							if( this.gestoreCRUD.existsServizioApplicativo(idSoggetto, saPA.getNome())){
								this.gestoreCRUD.deleteServizioApplicativo(((IDriverConfigurazioneGet)this.gestoreCRUD).getServizioApplicativo(idSoggetto, saPA.getNome()));
							}
							this.log.info("Servizio Applicativo ["+saPA.getNome()+"] [Definizione Interna alla PortaApplicativa] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminato.");		
						}
					}
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
					this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"] del Soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsServizioApplicativo(idSoggetto, servizioApplicativo.getNome())){
						this.gestoreCRUD.deleteServizioApplicativo(((IDriverConfigurazioneGet)this.gestoreCRUD).getServizioApplicativo(idSoggetto, servizioApplicativo.getNome()));
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
			invServizio.setCredenziali(credenziali);
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
			rispostaAsinc.setCredenziali(credenziali);
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
		if( (reset==false) && this.gestoreCRUD.existsServizioApplicativo(idSoggetto, servizioApplicativo.getNome())){
			this.log.info("Servizio Applicativo ["+servizioApplicativo.getNome()+"]"+pos+" del Soggetto "+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+" aggiornamento in corso...");
			
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setSoggettoFruitore(new IDSoggetto(soggettoProprietario.getTipo(),soggettoProprietario.getNome()));
			ServizioApplicativo old = ((IDriverConfigurazioneGet) this.gestoreCRUD).getServizioApplicativo(idPD, servizioApplicativo.getNome());
			
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
