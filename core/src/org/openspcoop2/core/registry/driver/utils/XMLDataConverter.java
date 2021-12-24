/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.core.commons.ProtocolFactoryReflectionUtils;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.DBMappingUtils;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziCRUD;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.uddi.DriverRegistroServiziUDDI;
import org.openspcoop2.core.registry.driver.web.DriverRegistroServiziWEB;
import org.openspcoop2.core.registry.driver.xml.DriverRegistroServiziXML;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * Classe utilizzata per convertire dati presenti in un file XML in un altra risorsa che implementa l'interfaccia CRUD.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLDataConverter {

	/** GestoreCRUD */
	private IDriverRegistroServiziCRUD gestoreCRUD = null;
	/** 'Root' del servizio dei registri OpenSPCoop. */
	private org.openspcoop2.core.registry.RegistroServizi sorgenteRegistro;
	/** Logger */
	private Logger log = null;
	/** Logger alternativo per i driver */
	private Logger logDriver = null;
	/** SuperUser */
	private String superUser = null;
	/** Tipo del backend di destinazione */
	private String tipoBEDestinazione;
	
	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	private IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();

	
	public XMLDataConverter(String sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(String sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(String sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(byte[] sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(byte[] sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(byte[] sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(InputStream sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(InputStream sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(InputStream sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(File sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(File sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(File sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(RegistroServizi sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,destinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	private void XMLDataConverterSetup(Object sorgente,AccessoRegistroRegistro destinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
	
		if(log == null)
			this.log = LoggerWrapperFactory.getLogger(XMLDataConverter.class);
		else
			this.log = log;
		this.logDriver = logDriver;
		
		if(destinazione==null)
			throw new DriverRegistroServiziException("GestoreCRUD non definito");
		
		this.tipoBEDestinazione = destinazione.getTipo().toString();
		this.superUser = superUser;
		
		// Istanziazione sorgente
		try{
			if(sorgente instanceof RegistroServizi) {
				this.sorgenteRegistro = (RegistroServizi) sorgente;
			}
			else if(sorgente instanceof String){
				createSorgente((String)sorgente);
				// Calcolo directory padre
				try{
					File f = new File((String)sorgente);
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
			else if (sorgente instanceof byte[]){
				createSorgente((byte[])sorgente);
			}else if (sorgente instanceof InputStream){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				InputStream is = (InputStream) sorgente;
//				int letti = 0;
//				byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//				while( (letti=is.read(reads)) != -1 ){
//					bout.write(reads,0,letti);	
//				}
				CopyStream.copy(is, bout);
				bout.flush();
				bout.close();
				createSorgente(bout.toByteArray());
			}else if (sorgente instanceof File){
				createSorgente(((File)sorgente).getAbsolutePath());
				// Calcolo directory padre
				try{
					File f = (File)sorgente;
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
		}catch(DriverRegistroServiziException d){
			throw d;
		}catch(Exception e){
			throw new DriverRegistroServiziException("Creazione sorgente ["+sorgente.getClass().getName()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Istanziazione CRUD
		try{
			if(CostantiConfigurazione.REGISTRO_DB.equals(destinazione.getTipo())){
				if(destinazione.getLocation()==null)
					throw new Exception("Location (DataSource) non definita");
				if(destinazione.getTipoDatabase()==null)
					throw new Exception("TipoDatabase (DataSource) non definita");
				if(destinazione.getGenericPropertiesMap()==null)
					throw new Exception("Contesto di localizzazione del datasource non definito in GenericProperties");
				java.util.Properties properties = new Properties();
				properties.putAll(destinazione.getGenericPropertiesMap());
				this.gestoreCRUD = new DriverRegistroServiziDB(destinazione.getLocation(),properties,this.logDriver,destinazione.getTipoDatabase());
				if(((DriverRegistroServiziDB)this.gestoreCRUD).create)
					this.log.info("Inizializzato Registro dei Servizi DB");
				else
					throw new Exception("RegistroServizi DB non inizializzato");
			}
			else if(CostantiConfigurazione.REGISTRO_UDDI.equals(destinazione.getTipo())){
				if(destinazione.getLocation()==null)
					throw new Exception("Location (InquiryURL) non definita");
				if(destinazione.getGenericPropertiesMap()==null)
					throw new Exception("PublishURL/UrlPrefix/PathPrefix non definita in GenericProperties");
				if(destinazione.getGenericPropertiesMap().size()!=3)
					throw new Exception("PublishURL/UrlPrefix/PathPrefix non definita in GenericProperties (size:"+destinazione.getGenericPropertiesMap().size()+")");
				if((destinazione.getGenericPropertiesMap().get("publishUrl") instanceof String) == false)
					throw new Exception("PublishURL definita in GenericProperties non e' un oggetto String");
				if((destinazione.getGenericPropertiesMap().get("urlPrefix") instanceof String) == false)
					throw new Exception("UrlPrefix definita in GenericProperties non e' un oggetto String");
				if((destinazione.getGenericPropertiesMap().get("pathPrefix") instanceof String) == false)
					throw new Exception("PathPrefix definita in GenericProperties non e' un oggetto String");
				this.gestoreCRUD = new DriverRegistroServiziUDDI(destinazione.getLocation(),
						destinazione.getGenericPropertiesMap().get("publishUrl"),
						destinazione.getUser(),destinazione.getPassword(),
						destinazione.getGenericPropertiesMap().get("urlPrefix"),
						destinazione.getGenericPropertiesMap().get("pathPrefix"),
						this.logDriver);
				if(((DriverRegistroServiziUDDI)this.gestoreCRUD).create)
					this.log.info("Inizializzato Registro dei Servizi UDDI");
				else
					throw new Exception("RegistroServizi UDDI non inizializzato");
			}
			else if(CostantiConfigurazione.REGISTRO_WEB.equals(destinazione.getTipo())){
				if(destinazione.getLocation()==null)
					throw new Exception("Location (UrlPrefix) non definita");
				if(destinazione.getGenericPropertiesMap()==null)
					throw new Exception("PathPrefix non definita in GenericProperties");
				if(destinazione.getGenericPropertiesMap().size()!=1)
					throw new Exception("PathPrefix non definita in GenericProperties (size:"+destinazione.getGenericPropertiesMap().size()+")");
				if((destinazione.getGenericPropertiesMap().get("pathPrefix") instanceof String) == false)
					throw new Exception("PathPrefix definita in GenericProperties non e' un oggetto String");
				this.gestoreCRUD = new DriverRegistroServiziWEB(destinazione.getLocation(),
						destinazione.getGenericPropertiesMap().get("pathPrefix"),
						this.logDriver);
				this.log.info("Inizializzato Registro dei Servizi WEB");
				if(((DriverRegistroServiziWEB)this.gestoreCRUD).create)
					this.log.info("Inizializzato Registro dei Servizi WEB");
				else
					throw new Exception("RegistroServizi WEB non inizializzato");
		
			}else{
				throw new Exception("Tipo di registro CRUD non gestito: "+destinazione.getTipo());
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		
		// Protocol initialize
		try {
			ProtocolFactoryReflectionUtils.initializeProtocolManager(protocolloDefault, this.log);
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		
		
	}
	
	
	public XMLDataConverter(String sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(String sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(String sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(byte[] sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(byte[] sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(byte[] sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(InputStream sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(InputStream sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(InputStream sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(File sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(File sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(File sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(RegistroServizi sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,gestoreCRUD, tipoDestinazione,superUser,protocolloDefault,log,logDriver);
	}
	
	private void XMLDataConverterSetup(Object sorgente, IDriverRegistroServiziCRUD gestoreCRUD, String tipoDestinazione,
			String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		
		if(log == null)
			this.log = LoggerWrapperFactory.getLogger(XMLDataConverter.class);
		else
			this.log = log;
		this.logDriver = logDriver;
		
		if(gestoreCRUD==null)
			throw new DriverRegistroServiziException("GestoreCRUD non definito");
		
		this.tipoBEDestinazione = tipoDestinazione;
		this.superUser = superUser;
		
		// Istanziazione sorgente
		try{
			if(sorgente instanceof RegistroServizi) {
				this.sorgenteRegistro = (RegistroServizi) sorgente;
			}
			else if(sorgente instanceof String){
				createSorgente((String)sorgente);
				// Calcolo directory padre
				try{
					File f = new File((String)sorgente);
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
			else if (sorgente instanceof byte[]){
				createSorgente((byte[])sorgente);
			}else if (sorgente instanceof InputStream){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				InputStream is = (InputStream) sorgente;
//				int letti = 0;
//				byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//				while( (letti=is.read(reads)) != -1 ){
//					bout.write(reads,0,letti);	
//				}
				CopyStream.copy(is, bout);
				bout.flush();
				bout.close();
				createSorgente(bout.toByteArray());
			}else if (sorgente instanceof File){
				createSorgente(((File)sorgente).getAbsolutePath());
				// Calcolo directory padre
				try{
					File f = (File)sorgente;
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
		}catch(DriverRegistroServiziException d){
			throw d;
		}catch(Exception e){
			throw new DriverRegistroServiziException("Creazione sorgente ["+sorgente.getClass().getName()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Istanziazione CRUD
		this.gestoreCRUD = gestoreCRUD;
		
		// Protocol initialize
		try {
			ProtocolFactoryReflectionUtils.initializeProtocolManager(protocolloDefault, this.log);
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		
		
	}
	
	
	
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(String sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(byte[] sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(InputStream sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(File sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,logDriver);
	}
	
	public XMLDataConverter(RegistroServizi sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,null,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,null);
	}
	public XMLDataConverter(RegistroServizi sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
		XMLDataConverterSetup(sorgente,connection,tipoDatabase,superUser,protocolloDefault,log,logDriver);
	}
	
	private File parentFile = null;

	private void XMLDataConverterSetup(Object sorgente,Connection connection,String tipoDatabase,String superUser,String protocolloDefault,Logger log,Logger logDriver) throws DriverRegistroServiziException{
	
		if(log == null)
			this.log = LoggerWrapperFactory.getLogger(XMLDataConverter.class);
		else
			this.log = log;
		this.logDriver = logDriver;
		
		this.superUser = superUser;
		this.tipoBEDestinazione = CostantiConfigurazione.REGISTRO_DB.toString();
		
		// Istanziazione sorgente
		try{
			if(sorgente instanceof RegistroServizi) {
				this.sorgenteRegistro = (RegistroServizi) sorgente;
			}
			else if(sorgente instanceof String){
				createSorgente((String)sorgente);
				// Calcolo directory padre
				try{
					File f = new File((String)sorgente);
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
			else if (sorgente instanceof byte[]){
				createSorgente((byte[])sorgente);
			}else if (sorgente instanceof InputStream){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				InputStream is = (InputStream) sorgente;
//				int letti = 0;
//				byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//				while( (letti=is.read(reads)) != -1 ){
//					bout.write(reads,0,letti);	
//				}
				CopyStream.copy(is, bout);
				bout.flush();
				bout.close();
				createSorgente(bout.toByteArray());
			}else if (sorgente instanceof File){
				createSorgente(((File)sorgente).getAbsolutePath());
				// Calcolo directory padre
				try{
					File f = (File)sorgente;
					if(f.getParentFile()!=null){
						this.parentFile = f.getParentFile();
					}
				}catch(Exception e){}
			}
		}catch(DriverRegistroServiziException d){
			throw d;
		}catch(Exception e){
			throw new DriverRegistroServiziException("Creazione sorgente ["+sorgente.getClass().getName()+"] non riuscita: "+e.getMessage(),e);
		}
		
		// Istanziazione CRUD in version DB
		try{
			this.gestoreCRUD = new DriverRegistroServiziDB(connection,this.logDriver,tipoDatabase);
			if(((DriverRegistroServiziDB)this.gestoreCRUD).create)
				this.log.info("Inizializzato Configurazione DB");
			else
				throw new Exception("Configurazione DB non inizializzato");
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
		
		// Protocol initialize
		try {
			ProtocolFactoryReflectionUtils.initializeProtocolManager(protocolloDefault, this.log);
		}catch(Exception e){
			throw new DriverRegistroServiziException("Errore durante l'istanziazione del driver di CRUD: "+e.getMessage(),e);
		}
	}
	
	
	
	
	private void createSorgente(String sorgente)throws DriverRegistroServiziException{
		// Istanziazione Sorgente
		if(sorgente==null)
			throw new DriverRegistroServiziException("Sorgente non definita");
		
		// ValidatoreXSD
		ValidatoreXSD validatoreRegistro = null;
		try{
			validatoreRegistro = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverRegistroServiziXML.class.getResourceAsStream("/registroServizi.xsd"));
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage());
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
			throw new DriverRegistroServiziException("Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage());
		}finally{
			if(fXML!=null){
				try{
					fXML.close();
				}catch(Exception e){}
			}
		}
		
		try{
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
					throw new DriverRegistroServiziException("Riscontrato errore durante la creazione dell'inputStream del registro dei servizi (HTTP) : \n\n"+e.getMessage());
				}
			}else{
				try{  
					iStream = new FileInputStream(sorgente);
				}catch(java.io.FileNotFoundException e) {
					throw new DriverRegistroServiziException("Riscontrato errore durante la creazione dell'inputStream del registro dei servizi (FILE) : \n\n"+e.getMessage());
				}
			}



			/* ---- Unmarshall del file di configurazione ---- */
			try{  
				org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
				this.sorgenteRegistro = deserializer.readRegistroServizi(iStream);
			} catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
					if(httpConn !=null)
						httpConn.disconnect();
				} catch(Exception ef) {}
				throw new DriverRegistroServiziException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
			}

			try{  
				// Chiusura dello Stream
				if(iStream!=null)
					iStream.close();
				// Chiusura dell'eventuale connessione HTTP
				if(httpConn !=null)
					httpConn.disconnect();
			} catch(Exception e) {
				throw new DriverRegistroServiziException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
			}
			
		} catch(Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante l'istanziazione del registro: "+e.getMessage(),e);
		}
	}
	
	
	private void createSorgente(byte[] sorgente)throws DriverRegistroServiziException{
		// Istanziazione Sorgente
		if(sorgente==null)
			throw new DriverRegistroServiziException("Sorgente non definita");
		
		// ValidatoreXSD
		ValidatoreXSD validatoreRegistro = null;
		try{
			validatoreRegistro = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverRegistroServiziXML.class.getResourceAsStream("/registroServizi.xsd"));
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage());
		}
		try{
			validatoreRegistro.valida(new ByteArrayInputStream(sorgente));
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage());
		}
		
		try{
			InputStream iStream = null;
			try{  
				iStream = new ByteArrayInputStream(sorgente);
			}catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				throw new DriverRegistroServiziException("Riscontrato errore durante la creazione dell'inputStreamReader del registro dei servizi : \n\n"+e.getMessage());
			}


			/* ---- Unmarshall del file di configurazione ---- */
			try{  
				org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
				this.sorgenteRegistro = deserializer.readRegistroServizi(iStream);
			} catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(Exception ef) {}
				throw new DriverRegistroServiziException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
			}

			try{  
				// Chiusura dello Stream
				if(iStream!=null)
					iStream.close();
			} catch(Exception e) {
				throw new DriverRegistroServiziException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
			}
			
		} catch(Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante l'istanziazione del registro: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	public void convertXML(boolean reset, boolean mantieniFruitoriEsistenti,boolean aggiornamentoSoggetti,StatiAccordo statoAccordo) throws DriverRegistroServiziException{
		convertXML(reset,new PdDConfig(),mantieniFruitoriEsistenti,aggiornamentoSoggetti,statoAccordo);
	}
	public void convertXML(boolean reset, boolean mantieniFruitoriEsistenti,boolean aggiornamentoSoggetti,StatiAccordo statoAccordo, boolean updateEnabled) throws DriverRegistroServiziException{
		convertXML(reset,new PdDConfig(),mantieniFruitoriEsistenti,aggiornamentoSoggetti,statoAccordo,updateEnabled);
	}
	
	// pddEsterne da impostare solo nel caso di conversione per govwayConsole
	public void convertXML(boolean reset, PdDConfig pddConfig, boolean mantieniFruitoriEsistenti,boolean aggiornamentoSoggetti,StatiAccordo statoAccordo) throws DriverRegistroServiziException{
		this.convertXML(reset, pddConfig, mantieniFruitoriEsistenti, aggiornamentoSoggetti, statoAccordo, true);
	}
	public void convertXML(boolean reset, PdDConfig pddConfig, boolean mantieniFruitoriEsistenti,boolean aggiornamentoSoggetti,StatiAccordo statoAccordo, boolean updateEnabled) throws DriverRegistroServiziException{
		
		// Reset
		if(reset){
			try{
				this.log.info("RegistroServizi, reset in corso...");
				this.gestoreCRUD.reset();
				this.log.info("RegistroServizi, reset effettuato.");
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverRegistroServiziException("Reset del RegistroServizi non riuscita: "+e.getMessage(),e);
			}
		}
		
			
		// Porte di Dominio
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizePortaDominioList(); i++){
				PortaDominio pdd = this.sorgenteRegistro.getPortaDominio(i);
				pdd.setSuperUser(this.superUser);
				impostaInformazioniRegistroDB_PortaDominio(pdd);
				
				if( (reset==false) && this.gestoreCRUD.existsPortaDominio(pdd.getNome())){
					if(updateEnabled) {
						this.log.info("Porta di Dominio "+pdd.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updatePortaDominio(pdd);
						this.log.info("Porta di Dominio "+pdd.getNome()+" aggiornata.");
					}
					else {
						this.log.info("Porta di Dominio "+pdd.getNome()+" non aggiornata (aggiornamento disabilitato).");
					}
				}else{
					this.log.info("Porta di Dominio "+pdd.getNome()+" creazione in corso...");
					this.gestoreCRUD.createPortaDominio(pdd);
					this.log.info("Porta di Dominio "+pdd.getNome()+" creata.");				
				}
				
				if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione) && pddConfig.getPddOperativaCtrlstatSinglePdd()!=null){
					impostaInformazioniRegistroDB_PortaDominio_update(pdd, pddConfig.getPddOperativaCtrlstatSinglePdd(), 
							this.log, ((DriverRegistroServiziDB)this.gestoreCRUD), pddConfig.getTipoPdd());
				}	

			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML delle porte di dominio: "+e.getMessage(),e);
		}
		
		
		
		
		
		
		// Gruppi
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeGruppoList(); i++){
				Gruppo gruppo = this.sorgenteRegistro.getGruppo(i);
				gruppo.setSuperUser(this.superUser);
				impostaInformazioniRegistroDB_Gruppo(gruppo);
				
				IDGruppo idGruppo = new IDGruppo(gruppo.getNome());
				if( (reset==false) && this.gestoreCRUD.existsGruppo(idGruppo)){
					if(updateEnabled) {
						this.log.info("Gruppo "+gruppo.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updateGruppo(gruppo);
						this.log.info("Gruppo "+gruppo.getNome()+" aggiornato.");
					}
					else {
						this.log.info("Gruppo "+gruppo.getNome()+" non aggiornato (aggiornamento disabilitato).");
					}
				}else{
					this.log.info("Gruppo "+gruppo.getNome()+" creazione in corso...");
					this.gestoreCRUD.createGruppo(gruppo);
					this.log.info("Gruppo "+gruppo.getNome()+" creato.");				
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML dei gruppi: "+e.getMessage(),e);
		}
		
		
		
		
		
		// Ruoli
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeRuoloList(); i++){
				Ruolo ruolo = this.sorgenteRegistro.getRuolo(i);
				ruolo.setSuperUser(this.superUser);
				impostaInformazioniRegistroDB_Ruolo(ruolo);
				
				IDRuolo idRuolo = new IDRuolo(ruolo.getNome());
				if( (reset==false) && this.gestoreCRUD.existsRuolo(idRuolo)){
					if(updateEnabled) {
						this.log.info("Ruolo "+ruolo.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updateRuolo(ruolo);
						this.log.info("Ruolo "+ruolo.getNome()+" aggiornato.");
					}
					else {
						this.log.info("Ruolo "+ruolo.getNome()+" non aggiornato (aggiornamento disabilitato).");
					}
				}else{
					this.log.info("Ruolo "+ruolo.getNome()+" creazione in corso...");
					this.gestoreCRUD.createRuolo(ruolo);
					this.log.info("Ruolo "+ruolo.getNome()+" creato.");				
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML dei ruoli: "+e.getMessage(),e);
		}
		
		
		
		
		
		
		// Scope
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeScopeList(); i++){
				Scope scope = this.sorgenteRegistro.getScope(i);
				scope.setSuperUser(this.superUser);
				impostaInformazioniRegistroDB_Scope(scope);
				
				IDScope idScope = new IDScope(scope.getNome());
				if( (reset==false) && this.gestoreCRUD.existsScope(idScope)){
					if(updateEnabled) {
						this.log.info("Scope "+scope.getNome()+" aggiornamento in corso...");
						this.gestoreCRUD.updateScope(scope);
						this.log.info("Scope "+scope.getNome()+" aggiornato.");
					}
					else {
						this.log.info("Scope "+scope.getNome()+" non aggiornato (aggiornamento disabilitato).");
					}
				}else{
					this.log.info("Scope "+scope.getNome()+" creazione in corso...");
					this.gestoreCRUD.createScope(scope);
					this.log.info("Scope "+scope.getNome()+" creato.");				
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML degli scope: "+e.getMessage(),e);
		}
				
		
		
		

		// Soggetto
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteRegistro.getSoggetto(i);
				this.gestioneSoggetto(soggetto, reset, aggiornamentoSoggetti,pddConfig.getPddOperativaCtrlstatSinglePdd(), updateEnabled);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML dei soggetti: "+e.getMessage(),e);
		}
		
		
		// Accordi Cooperazione
		try{
			for(int i=0; i<this.sorgenteRegistro.sizeAccordoCooperazioneList(); i++){
				AccordoCooperazione ac = this.sorgenteRegistro.getAccordoCooperazione(i);
				
				// stato package
				if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
					ac.setStatoPackage(statoAccordo.toString());
				}
				
				this.gestioneAccordoCooperazione(ac, reset, updateEnabled);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML degli accordi di cooperazione: "+e.getMessage(),e);
		}
		
		
		// Accordi Servizio Parte Comune
		List<AccordoServizioParteComune> accordiServizioComposti = new ArrayList<AccordoServizioParteComune>();
		List<String> accordiServizioCompostiID = new ArrayList<String>();
		try{
			for(int i=0; i<this.sorgenteRegistro.sizeAccordoServizioParteComuneList(); i++){
				AccordoServizioParteComune as = this.sorgenteRegistro.getAccordoServizioParteComune(i);
				
				// stato package
				if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
					as.setStatoPackage(statoAccordo.toString());
				}
				
				if(as.getServizioComposto()==null){
					this.gestioneAccordoServizioParteComune(as, reset, updateEnabled);
				}else{
					String idAccordoServizioComposto = this.idAccordoFactory.getUriFromAccordo(as);
					this.log.debug("AccordoServizio ["+idAccordoServizioComposto+"] e' un accordo di servizio composto, verra posticipata la gestione dopo la creazione dei servizi");
					accordiServizioComposti.add(as);
					accordiServizioCompostiID.add(idAccordoServizioComposto);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML degli accordi: "+e.getMessage(),e);
		}
		
		
		// Accordo Servizio Parte Specifica
		List<AccordoServizioParteSpecifica> servizioComposti = new ArrayList<AccordoServizioParteSpecifica>();
		List<Soggetto> servizioComposti_Soggetti = new ArrayList<Soggetto>();
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteRegistro.getSoggetto(i);
				for(int j=0; j<soggetto.sizeAccordoServizioParteSpecificaList(); j++){
					AccordoServizioParteSpecifica servizio = soggetto.getAccordoServizioParteSpecifica(j);
					
					// stato package
					if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
						servizio.setStatoPackage(statoAccordo.toString());
					}
					// accordo parte specifica
					if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
						if(servizio.getVersione()==null){
							servizio.setVersione(1);
						}
					}
					
					boolean servizioComposto = accordiServizioCompostiID.contains(servizio.getAccordoServizioParteComune());
					if(!servizioComposto){
						this.gestioneServizio(servizio, soggetto, reset, mantieniFruitoriEsistenti,statoAccordo, updateEnabled);
					}else{
						String uri = this.idServizioFactory.getUriFromAccordo(servizio);
						this.log.debug("Servizio ["+uri+"] e' un servizio composto, verra posticipata la gestione dopo la creazione degli accordi di servizio composti");
						servizioComposti.add(servizio);
						servizioComposti_Soggetti.add(soggetto);
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML dei servizi: "+e.getMessage(),e);
		}
		
		// Accordi Servizio Composti
		try{
			while(accordiServizioComposti.size()>0){
				AccordoServizioParteComune as = accordiServizioComposti.remove(0);
				this.gestioneAccordoServizioParteComune(as, reset, updateEnabled);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML degli accordi di servizio composti: "+e.getMessage(),e);
		}
		
		// Servizi di  Accordi Servizio Composti
		try{
			while(servizioComposti.size()>0){
				AccordoServizioParteSpecifica servizio = servizioComposti.remove(0);
				Soggetto soggetto = servizioComposti_Soggetti.remove(0);
				this.gestioneServizio(servizio, soggetto, reset, mantieniFruitoriEsistenti,statoAccordo, updateEnabled);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante la conversione XML dei servizi degli accordi di servizio composti: "+e.getMessage(),e);
		}
		
	}
	
	public static void impostaInformazioniRegistroDB_PortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if(pdd.getImplementazione()==null)
			pdd.setImplementazione("standard");
	}
	
	public static void impostaInformazioniRegistroDB_PortaDominio_update(PortaDominio pdd,
			String nomePddOperativaCtrlstatSinglePdD,Logger log,DriverRegistroServiziDB driverRegistroServiziDB,String tipoPdd) throws DriverRegistroServiziException{
		if(pdd.getNome().equals(nomePddOperativaCtrlstatSinglePdD)){
			log.info("Porta di Dominio "+pdd.getNome()+" aggiornamento tipo["+PddTipologia.OPERATIVO.toString()+"] in corso...");
			driverRegistroServiziDB.updateTipoPortaDominio(pdd.getNome(), PddTipologia.OPERATIVO.toString());
			log.info("Porta di Dominio "+pdd.getNome()+" aggiornata con tipo "+PddTipologia.OPERATIVO.toString()+".");
		}else{
			log.info("Porta di Dominio "+pdd.getNome()+" aggiornamento tipo["+PddTipologia.ESTERNO.toString()+"] in corso...");
			driverRegistroServiziDB.updateTipoPortaDominio(pdd.getNome(), tipoPdd);
			log.info("Porta di Dominio "+pdd.getNome()+" aggiornata con tipo "+PddTipologia.ESTERNO.toString()+".");
		}
	}
	
	public static void impostaInformazioniRegistroDB_Gruppo(Gruppo gruppo) throws DriverRegistroServiziException{

	}
	
	public static void impostaInformazioniRegistroDB_Ruolo(Ruolo ruolo) throws DriverRegistroServiziException{

	}
	
	public static void impostaInformazioniRegistroDB_Scope(Scope scope) throws DriverRegistroServiziException{

	}
	
	
	
	private static void mantieniFruitori(AccordoServizioParteSpecifica old,AccordoServizioParteSpecifica servizio){
		for(int fr=0; fr<old.sizeFruitoreList(); fr++){
			boolean find = false;
			for(int frNew =0; frNew < servizio.sizeFruitoreList(); frNew++){
				if(servizio.getFruitore(frNew).getTipo().equals(old.getFruitore(fr).getTipo()) &&
						servizio.getFruitore(frNew).getNome().equals(old.getFruitore(fr).getNome())	){
					find = true;
					break;
				}
			}
			if(!find){
				servizio.addFruitore(old.getFruitore(fr));
			}
		}
	}
	
	public static void aggiornatoStatoFruitori(AccordoServizioParteSpecifica servizio,StatiAccordo statoAccordo){
		for(int i =0; i < servizio.sizeFruitoreList(); i++){
			servizio.getFruitore(i).setStatoPackage(statoAccordo.toString());
		}
	}
	
	public void delete(boolean deleteSoggetti, boolean deleteMappingErogazioneFruizione) throws DriverRegistroServiziException{
		
		// Servizi
		try{
			
			for(int i=0; i<this.sorgenteRegistro.sizeSoggettoList(); i++){
				Soggetto soggetto = this.sorgenteRegistro.getSoggetto(i);
				for(int j=0; j<soggetto.sizeAccordoServizioParteSpecificaList(); j++){
					AccordoServizioParteSpecifica servizio = soggetto.getAccordoServizioParteSpecifica(j);
					servizio.setTipoSoggettoErogatore(soggetto.getTipo());
					servizio.setNomeSoggettoErogatore(soggetto.getNome());
					IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(servizio);
					String uri = this.idServizioFactory.getUriFromAccordo(servizio);
					
					if(deleteMappingErogazioneFruizione && this.gestoreCRUD instanceof DriverRegistroServiziDB) {
						DriverRegistroServiziDB driver = (DriverRegistroServiziDB) this.gestoreCRUD;
						Connection con = null;
						try {
							con = driver.getConnection("XMLDataConverter.mappingErogazioneFruizione");
							this.log.info("Servizio "+uri+" (mappingErogazione) eliminazione in corso...");							
							if(this.gestoreCRUD.existsAccordoServizioParteSpecifica(idServizio)){
								// elimino anche tutti i mapping fruitori
								AccordoServizioParteSpecifica dbImage = driver.getAccordoServizioParteSpecifica(idServizio);
								for (Fruitore fruitore : dbImage.getFruitoreList()) {
									IDSoggetto idFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
									if(DBMappingUtils.existsIDPorteDelegateAssociate(idServizio, idFruitore, con, driver.getTipoDB())) {
										DBMappingUtils.deleteMappingFruizione(idServizio, idFruitore, true, con, driver.getTipoDB()); // elimina anche le porte delegate
									}
								}
							}
							if(DBMappingUtils.existsIDPorteApplicativeAssociate(idServizio, con, driver.getTipoDB())) {
								DBMappingUtils.deleteMappingErogazione(idServizio, true, con, driver.getTipoDB()); // elimina anche le porte applicative
							}
							this.log.info("Servizio "+uri+" (mappingErogazione) eliminazione completata");
						}finally {
							try {
								if(driver.isAtomica()) {
									con.commit();
								}
							}catch(Throwable t) {}
							driver.releaseConnection(con);
						}
					}
					
					this.log.info("Servizio "+uri+" eliminazione in corso...");
					if(this.gestoreCRUD.existsAccordoServizioParteSpecifica(idServizio)){
						this.gestoreCRUD.deleteAccordoServizioParteSpecifica(((IDriverRegistroServiziGet)this.gestoreCRUD).getAccordoServizioParteSpecifica(idServizio));
					}
					this.log.info("Servizio "+uri+" eliminato.");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante l'eliminazione dei servizi: "+e.getMessage(),e);
		}
		
			
		
		
		// Accordi Servizio
		List<AccordoServizioParteComune> asServiziNonComposti = new ArrayList<AccordoServizioParteComune>();
		try{
			for(int i=0; i<this.sorgenteRegistro.sizeAccordoServizioParteComuneList(); i++){
				AccordoServizioParteComune as = this.sorgenteRegistro.getAccordoServizioParteComune(i);
				if(as.getServizioComposto()==null){
					asServiziNonComposti.add(as);
				}
				else{
					this.log.info("Accordo di Servizio Composto "+this.idAccordoFactory.getUriFromAccordo(as)+" eliminazione in corso...");
					if( this.gestoreCRUD.existsAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromAccordo(as))){
						this.gestoreCRUD.deleteAccordoServizioParteComune(((IDriverRegistroServiziGet)this.gestoreCRUD).getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromAccordo(as)));
					}
					this.log.info("Accordo di Servizio Composto "+this.idAccordoFactory.getUriFromAccordo(as)+" eliminato");
				}
			}
			
			while(asServiziNonComposti.size()>0){
				AccordoServizioParteComune as =  asServiziNonComposti.remove(0);
				this.log.info("Accordo di Servizio "+this.idAccordoFactory.getUriFromAccordo(as)+" eliminazione in corso...");
				if( this.gestoreCRUD.existsAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromAccordo(as))){
					this.gestoreCRUD.deleteAccordoServizioParteComune(((IDriverRegistroServiziGet)this.gestoreCRUD).getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromAccordo(as)));
				}
				this.log.info("Accordo di Servizio "+this.idAccordoFactory.getUriFromAccordo(as)+" eliminato");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante l'eliminazione degli accordi: "+e.getMessage(),e);
		}
		
		
		
		
		// Accordi Cooperazione
		try{
			for(int i=0; i<this.sorgenteRegistro.sizeAccordoCooperazioneList(); i++){
				AccordoCooperazione ac = this.sorgenteRegistro.getAccordoCooperazione(i);
				this.log.info("Accordo di Cooperazione "+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+" eliminazione in corso...");
				if( this.gestoreCRUD.existsAccordoCooperazione(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac))){
					this.gestoreCRUD.deleteAccordoCooperazione(((IDriverRegistroServiziGet)this.gestoreCRUD).getAccordoCooperazione(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac)));
				}
				this.log.info("Accordo di Cooperazione "+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+" eliminato");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new DriverRegistroServiziException("Errore durante l'eliminazione degli accordi di cooperazione: "+e.getMessage(),e);
		}
		
		
		if(deleteSoggetti){
			
			// Soggetto
			try{
				
				for(int i=0; i<this.sorgenteRegistro.sizeSoggettoList(); i++){
					Soggetto soggetto = this.sorgenteRegistro.getSoggetto(i);
					IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsSoggetto(idSoggetto)){
						this.gestoreCRUD.deleteSoggetto(((IDriverRegistroServiziGet)this.gestoreCRUD).getSoggetto(idSoggetto));
					}
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" eliminato.");
				}
				
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverRegistroServiziException("Errore durante l'eliminazione dei soggetti: "+e.getMessage(),e);
			}
			
			
			// Porte di Dominio
			try{
				
				for(int i=0; i<this.sorgenteRegistro.sizePortaDominioList(); i++){
					PortaDominio pdd = this.sorgenteRegistro.getPortaDominio(i);
					this.log.info("Porta di Dominio "+pdd.getNome()+" eliminazione in corso...");
					if( this.gestoreCRUD.existsPortaDominio(pdd.getNome())){
						this.gestoreCRUD.deletePortaDominio(((IDriverRegistroServiziGet)this.gestoreCRUD).getPortaDominio(pdd.getNome()));
					}
					this.log.info("Porta di Dominio "+pdd.getNome()+" eliminata.");
				}
				
			}catch(Exception e){
				e.printStackTrace();
				throw new DriverRegistroServiziException("Errore durante l'eliminazione delle porte di dominio: "+e.getMessage(),e);
			}
			
		}
			
	}
	
	
	private byte [] gestioneBytesDocumenti_Wsdl_Wsbl(String documento) throws Exception{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		if(documento!=null && (documento.equals("http://undefined")==false) ){
			if(documento.startsWith("http://") || documento.startsWith("file://")){
				byte[] file = HttpUtilities.requestHTTPFile(documento);
				if(file==null)
					throw new Exception("byte[] is null");
				else
					bout.write(file);
			}else{
				File f = new File(documento);
				if(f.exists()){
					FileInputStream file = null;
					try{
						file = new FileInputStream(f);
//						byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//						int letti = 0;
//						while( (letti=file.read(reads)) >=0 ){
//							bout.write(reads,0,letti);
//						}
						CopyStream.copy(file, bout);
					}finally{
						try{
							if(file!=null){
								file.close();
							}
						}catch(Exception eClose){}
					}
				}
				else if(this.parentFile!=null){
					File fWithParent = new File(this.parentFile,documento);
					if(fWithParent.exists()){
						FileInputStream file = null;
						try{
							file = new FileInputStream(fWithParent);
//							byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//							int letti = 0;
//							while( (letti=file.read(reads)) >=0 ){
//								bout.write(reads,0,letti);
//							}
							CopyStream.copy(file, bout);
						}finally{
							try{
								if(file!=null){
									file.close();
								}
							}catch(Exception eClose){}
						}	
					}
				}
				else{
					throw new Exception("File ["+documento+"] non esistente");
				}
			}
			bout.flush();
			bout.close();
			if(bout.size()>0){
				return bout.toByteArray();
			}	
		}
		return null;
	}
	
	
	private void initializeWSDLBytes(AccordoServizioParteComune as) throws DriverRegistroServiziException{
		
		String uriAS = this.idAccordoFactory.getUriFromAccordo(as);
	
		if(as.getWsdlDefinitorio()!=null){
			try{
				as.setByteWsdlDefinitorio(gestioneBytesDocumenti_Wsdl_Wsbl(as.getWsdlDefinitorio()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura wsdl definitorio ["+as.getWsdlDefinitorio()+"] non riuscita:",e);
			}	
		}
		if(as.getWsdlConcettuale()!=null){
			try{
				as.setByteWsdlConcettuale(gestioneBytesDocumenti_Wsdl_Wsbl(as.getWsdlConcettuale()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura wsdl concettuale ["+as.getWsdlConcettuale()+"] non riuscita:",e);
			}	
		}
		if(as.getWsdlLogicoErogatore()!=null){
			try{
				as.setByteWsdlLogicoErogatore(gestioneBytesDocumenti_Wsdl_Wsbl(as.getWsdlLogicoErogatore()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura wsdl logico erogatore ["+as.getWsdlLogicoErogatore()+"] non riuscita:",e);
			}	
		}
		if(as.getWsdlLogicoFruitore()!=null){
			try{
				as.setByteWsdlLogicoFruitore(gestioneBytesDocumenti_Wsdl_Wsbl(as.getWsdlLogicoFruitore()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura wsdl logico fruitore ["+as.getWsdlLogicoFruitore()+"] non riuscita:",e);
			}	
		}
		
		if(as.getSpecificaConversazioneConcettuale()!=null){
			try{
				as.setByteSpecificaConversazioneConcettuale(gestioneBytesDocumenti_Wsdl_Wsbl(as.getSpecificaConversazioneConcettuale()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura specifica conversazione concettuale ["+as.getSpecificaConversazioneConcettuale()+"] non riuscita:",e);
			}	
		}
		if(as.getSpecificaConversazioneErogatore()!=null){
			try{
				as.setByteSpecificaConversazioneErogatore(gestioneBytesDocumenti_Wsdl_Wsbl(as.getSpecificaConversazioneErogatore()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura specifica conversazione erogatore ["+as.getSpecificaConversazioneErogatore()+"] non riuscita:",e);
			}	
		}
		if(as.getSpecificaConversazioneFruitore()!=null){
			try{
				as.setByteSpecificaConversazioneFruitore(gestioneBytesDocumenti_Wsdl_Wsbl(as.getSpecificaConversazioneFruitore()));
			}catch(Exception e){
				this.log.warn("Accordo di Servizio "+uriAS+", lettura specifica conversazione fruitore ["+as.getSpecificaConversazioneFruitore()+"] non riuscita:",e);
			}	
		}
	}
	
	
	private void initializeWSDLBytes(AccordoServizioParteSpecifica s){
		if(s.getWsdlImplementativoErogatore()!=null){
			try{
				s.setByteWsdlImplementativoErogatore(gestioneBytesDocumenti_Wsdl_Wsbl(s.getWsdlImplementativoErogatore()));
			}catch(Exception e){
				String uri = null;
				try{
					uri = this.idServizioFactory.getUriFromAccordo(s);
				}catch(Exception eInternal){
					throw new RuntimeException(eInternal);
				}
				this.log.warn("Servizio "+uri+", lettura wsdl implementativo erogatore ["+s.getWsdlImplementativoErogatore()+"] non riuscita:",e);
			}
		}
		if(s.getWsdlImplementativoFruitore()!=null){
			try{
				s.setByteWsdlImplementativoFruitore(gestioneBytesDocumenti_Wsdl_Wsbl(s.getWsdlImplementativoFruitore()));
			}catch(Exception e){
				String uri = null;
				try{
					uri = this.idServizioFactory.getUriFromAccordo(s);
				}catch(Exception eInternal){
					throw new RuntimeException(eInternal);
				}
				this.log.warn("Servizio "+uri+", lettura wsdl implementativo fruitore ["+s.getWsdlImplementativoFruitore()+"] non riuscita:",e);
			}	
		}
	}

	
	private byte[] readDocumento(String fileName) throws DriverRegistroServiziException{
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try{
			if(fileName.startsWith("http://") || fileName.startsWith("file://")){
				byte[] file = HttpUtilities.requestHTTPFile(fileName);
				if(file==null)
					throw new Exception("byte[] is null");
				else
					bout.write(file);
			}else{
				File f = new File(fileName);
				if(f.exists()){
					FileInputStream file = null;
					try{
						file = new FileInputStream(f);
//						byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//						int letti = 0;
//						while( (letti=file.read(reads)) >=0 ){
//							bout.write(reads,0,letti);
//						}
						CopyStream.copy(file, bout);
					}finally{
						try{
							if(file!=null){
								file.close();
							}
						}catch(Exception eClose){}
					}		
				}
				else if(this.parentFile!=null){
					File fWithParent = new File(this.parentFile,fileName);
					if(fWithParent.exists()){
						FileInputStream file = null;
						try{
							file = new FileInputStream(fWithParent);
//							byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//							int letti = 0;
//							while( (letti=file.read(reads)) >=0 ){
//								bout.write(reads,0,letti);
//							}
							CopyStream.copy(file, bout);
						}finally{
							try{
								if(file!=null){
									file.close();
								}
							}catch(Exception eClose){}
						}	
					}
				}
				else{
					throw new Exception("File ["+fileName+"] non esistente");
				}
			}
			bout.flush();
			bout.close();
			if(bout.size()>0){
				return bout.toByteArray();
			}
			
		}catch(Exception e){
			this.log.warn("File "+fileName+", lettura documento non riuscita:",e);
		}	
		
		throw new DriverRegistroServiziException("Contenuto non letto?? File ["+fileName+"]");
	}
	
	private String getNomeFile(String fileName){
		String name = null;
		if(fileName.startsWith("http://")){
			int index = fileName.lastIndexOf("/");
			name = fileName.substring(index+1, fileName.length());
		}
		else {
			File f = new File(fileName);
			name = f.getName();
		}
		return name;
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
	
	public static void impostaInformazioniRegistroDB_Soggetto(Soggetto soggetto,String pddOperativaCtrlstatSinglePdd) throws DriverRegistroServiziException{
		if(soggetto.getConnettore()!=null){
			// I nomi vengono autogenerati dal driver
			soggetto.getConnettore().setNome(null);
			// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
			String tipoConnettore = soggetto.getConnettore().getTipo();
			if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
				soggetto.getConnettore().setCustom(true);
			}
			// Gestione default per connettore https
			if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
				gestioneDefaultConnettoreHTTP(soggetto.getConnettore());
			}
		}
		IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
		if( (soggetto.getIdentificativoPorta()==null) || ("".equals(soggetto.getIdentificativoPorta())) ) {
			try {
				soggetto.setIdentificativoPorta(ProtocolFactoryReflectionUtils.getIdentificativoPortaDefault(idSoggetto));
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
		if( (soggetto.getCodiceIpa() == null) || ("".equals(soggetto.getCodiceIpa())) ){
			try {
				soggetto.setCodiceIpa(ProtocolFactoryReflectionUtils.getCodiceIPADefault(idSoggetto));
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
			
		if(pddOperativaCtrlstatSinglePdd!=null && soggetto.getPortaDominio()==null){
			soggetto.setPortaDominio(pddOperativaCtrlstatSinglePdd);
		}
	}
	public static void impostaInformazioniRegistroDB_Soggetto_update(Soggetto soggetto,Soggetto old){
		soggetto.setId(old.getId());
		if(soggetto.getConnettore()==null){
			soggetto.setConnettore(old.getConnettore());
		}else{
			soggetto.getConnettore().setId(old.getConnettore().getId());
			soggetto.getConnettore().setNome(old.getConnettore().getNome());
		}
	}
	
	private void gestioneSoggetto(Soggetto soggetto,boolean reset,boolean aggiornamentoSoggetti,String pddOperativaCtrlstatSinglePdd, boolean updateEnabled) throws Exception{
		soggetto.setSuperUser(this.superUser);
		IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
		
		if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
			impostaInformazioniRegistroDB_Soggetto(soggetto,pddOperativaCtrlstatSinglePdd);
		}
		
		// protocol properties: bytes
		if(soggetto.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : soggetto.getProtocolPropertyList()) {
				if(pp.getFile()!=null) {
					pp.setByteFile(this.readDocumento(pp.getFile()));
				}
			}
		}
		
		if( (reset==false) && this.gestoreCRUD.existsSoggetto(idSoggetto)){
			if(aggiornamentoSoggetti){
				if(updateEnabled) {
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornamento in corso...");
					Soggetto old = ((IDriverRegistroServiziGet)this.gestoreCRUD).getSoggetto(idSoggetto);
					impostaInformazioniRegistroDB_Soggetto_update(soggetto, old);
					this.gestoreCRUD.updateSoggetto(soggetto);
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" aggiornato.");
				}
				else {
					this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" non aggiornato (aggiornamento disabilitato).");
				}
			}
		}else{
			this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creazione in corso...");
			this.gestoreCRUD.createSoggetto(soggetto);
			this.log.info("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome()+" creato.");
		}
	}
	
	private void gestioneAccordoCooperazione(AccordoCooperazione ac,boolean reset, boolean updateEnabled) throws Exception {
		ac.setSuperUser(this.superUser);
		// bytes
		for(int k=0; k<ac.sizeAllegatoList();k++){
			String fileName = ac.getAllegato(k).getFile();
			String name = getNomeFile(fileName);
			
			ac.getAllegato(k).setByteContenuto(this.readDocumento(fileName));
			ac.getAllegato(k).setFile(name);
			ac.getAllegato(k).setRuolo(RuoliDocumento.allegato.toString());
			ac.getAllegato(k).setTipoProprietarioDocumento(ProprietariDocumento.accordoCooperazione.toString());
		}
		for(int k=0; k<ac.sizeSpecificaSemiformaleList();k++){
			String fileName = ac.getSpecificaSemiformale(k).getFile();
			String name = getNomeFile(fileName);
			
			ac.getSpecificaSemiformale(k).setByteContenuto(this.readDocumento(fileName));
			ac.getSpecificaSemiformale(k).setFile(name);
			ac.getSpecificaSemiformale(k).setRuolo(RuoliDocumento.specificaSemiformale.toString());
			ac.getSpecificaSemiformale(k).setTipoProprietarioDocumento(ProprietariDocumento.accordoCooperazione.toString());
		}
		// protocol properties: bytes
		if(ac.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : ac.getProtocolPropertyList()) {
				if(pp.getFile()!=null) {
					pp.setByteFile(this.readDocumento(pp.getFile()));
				}
			}
		}
		IDAccordoCooperazione idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac);
		String uriAC = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordo);
		if( (reset==false) && this.gestoreCRUD.existsAccordoCooperazione(idAccordo)){
			if(updateEnabled) {
				this.log.info("Accordo di Cooperazione "+uriAC+" aggiornamento in corso...");
				this.gestoreCRUD.updateAccordoCooperazione(ac);
				this.log.info("Accordo di Cooperazione "+uriAC+" aggiornato.");
			}
			else {
				this.log.info("Accordo di Cooperazione "+uriAC+" non aggiornato (aggiornamento disabilitato).");
			}
		}else{
			this.log.info("Accordo di Cooperazione "+uriAC+" creazione in corso...");
			this.gestoreCRUD.createAccordoCooperazione(ac);
			this.log.info("Accordo di Cooperazione "+uriAC+" creato.");
		}
	}
	
	public static boolean equals(StatoFunzionalita arg1, StatoFunzionalita arg2) {
		if(arg1==null) {
			return arg2==null;
		}
		else {
			return arg1.equals(arg2);
		}
	}
	
	public static void impostaInformazioniRegistroDB_AccordoServizioParteComune(AccordoServizioParteComune as){
		
		// Devono essere impostati i comportamenti per la ridefinizione delle azioni
		for(int k=0; k<as.sizeAzioneList();k++){
			if(
					(as.getAzione(k).getProfiloCollaborazione()!=null && !as.getAzione(k).getProfiloCollaborazione().equals(as.getProfiloCollaborazione())) ||
					!equals(as.getAzione(k).getConfermaRicezione(),as.getConfermaRicezione()) ||
					!equals(as.getAzione(k).getConsegnaInOrdine(),as.getConsegnaInOrdine()) ||
					!equals(as.getAzione(k).getFiltroDuplicati(),as.getFiltroDuplicati()) ||
					!equals(as.getAzione(k).getIdCollaborazione(),as.getIdCollaborazione()) ||
					!equals(as.getAzione(k).getIdRiferimentoRichiesta(),as.getIdRiferimentoRichiesta()) ||
					(as.getAzione(k).getScadenza()!=null && !as.getAzione(k).getScadenza().equals(as.getScadenza()))
					) {
				as.getAzione(k).setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}else{
				as.getAzione(k).setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				as.getAzione(k).setProfiloCollaborazione(as.getProfiloCollaborazione());
				as.getAzione(k).setConfermaRicezione(as.getConfermaRicezione());
				as.getAzione(k).setConsegnaInOrdine(as.getConsegnaInOrdine());
				as.getAzione(k).setFiltroDuplicati(as.getFiltroDuplicati());
				as.getAzione(k).setIdCollaborazione(as.getIdCollaborazione());
				as.getAzione(k).setIdRiferimentoRichiesta(as.getIdRiferimentoRichiesta());
				as.getAzione(k).setScadenza(as.getScadenza());
			}
		}
		// Devono essere impostati i comportamenti per la ridefinizione dei port-types e delle azioni dei port-types
		for(int k=0; k<as.sizePortTypeList();k++){
			PortType pt = as.getPortType(k);
			if(
					(pt.getProfiloCollaborazione()!=null && !pt.getProfiloCollaborazione().equals(as.getProfiloCollaborazione())) ||
					!equals(pt.getConfermaRicezione(),as.getConfermaRicezione()) ||
					!equals(pt.getConsegnaInOrdine(),as.getConsegnaInOrdine()) ||
					!equals(pt.getFiltroDuplicati(),as.getFiltroDuplicati()) ||
					!equals(pt.getIdCollaborazione(),as.getIdCollaborazione()) ||
					!equals(pt.getIdRiferimentoRichiesta(),as.getIdRiferimentoRichiesta()) ||
					(pt.getScadenza()!=null && !pt.getScadenza().equals(as.getScadenza()))
					) {
				pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}else{
				pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				pt.setProfiloCollaborazione(as.getProfiloCollaborazione());
				pt.setConfermaRicezione(as.getConfermaRicezione());
				pt.setConsegnaInOrdine(as.getConsegnaInOrdine());
				pt.setFiltroDuplicati(as.getFiltroDuplicati());
				pt.setIdCollaborazione(as.getIdCollaborazione());
				pt.setIdRiferimentoRichiesta(as.getIdRiferimentoRichiesta());
				pt.setScadenza(as.getScadenza());
			}
			
			for(int l=0; l<pt.sizeAzioneList();l++){
				Operation op = pt.getAzione(l);
				
				boolean ridefinitoProfiloAzioneParametri = false;
				ProfiloCollaborazione profiloDefault = null;
				String scadenzaDefault = null;
				if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())) {
					ridefinitoProfiloAzioneParametri = 
							!equals(op.getConfermaRicezione(),pt.getConfermaRicezione()) ||
							!equals(op.getConsegnaInOrdine(),pt.getConsegnaInOrdine()) ||
							!equals(op.getFiltroDuplicati(),pt.getFiltroDuplicati()) ||
							!equals(op.getIdCollaborazione(),pt.getIdCollaborazione()) ||
							!equals(op.getIdRiferimentoRichiesta(),pt.getIdRiferimentoRichiesta());
					profiloDefault = pt.getProfiloCollaborazione();
					scadenzaDefault = pt.getScadenza();
				}
				else {
					ridefinitoProfiloAzioneParametri = 
							!equals(op.getConfermaRicezione(),as.getConfermaRicezione()) ||
							!equals(op.getConsegnaInOrdine(),as.getConsegnaInOrdine()) ||
							!equals(op.getFiltroDuplicati(),as.getFiltroDuplicati()) ||
							!equals(op.getIdCollaborazione(),as.getIdCollaborazione()) ||
							!equals(op.getIdRiferimentoRichiesta(),as.getIdRiferimentoRichiesta());
					profiloDefault = as.getProfiloCollaborazione();
					scadenzaDefault = as.getScadenza();
				}
				
				if(	(op.getProfiloCollaborazione()!=null && !op.getProfiloCollaborazione().equals(profiloDefault)) ||
						ridefinitoProfiloAzioneParametri ||
						(op.getScadenza()!=null && !op.getScadenza().equals(scadenzaDefault))
						) {
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
				}else{
					op.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
					
					if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(pt.getProfiloPT())) {
						op.setProfiloCollaborazione(pt.getProfiloCollaborazione());
						op.setConfermaRicezione(pt.getConfermaRicezione());
						op.setConsegnaInOrdine(pt.getConsegnaInOrdine());
						op.setFiltroDuplicati(pt.getFiltroDuplicati());
						op.setIdCollaborazione(pt.getIdCollaborazione());
						op.setIdRiferimentoRichiesta(pt.getIdRiferimentoRichiesta());
						op.setScadenza(pt.getScadenza());
					}
					else {
						op.setProfiloCollaborazione(as.getProfiloCollaborazione());
						op.setConfermaRicezione(as.getConfermaRicezione());
						op.setConsegnaInOrdine(as.getConsegnaInOrdine());
						op.setFiltroDuplicati(as.getFiltroDuplicati());
						op.setIdCollaborazione(as.getIdCollaborazione());
						op.setIdRiferimentoRichiesta(as.getIdRiferimentoRichiesta());
						op.setScadenza(as.getScadenza());
					}
				}
				

			}
		}
		// Devono essere impostati i comportamenti per la ridefinizione delle azioni
		for(int k=0; k<as.sizeResourceList();k++){
			if(
					//(as.getResource(k).getProfiloCollaborazione()!=null && !as.getResource(k).getProfiloCollaborazione().equals(as.getProfiloCollaborazione())) ||
					!equals(as.getResource(k).getConfermaRicezione(),as.getConfermaRicezione()) ||
					!equals(as.getResource(k).getConsegnaInOrdine(),as.getConsegnaInOrdine()) ||
					!equals(as.getResource(k).getFiltroDuplicati(),as.getFiltroDuplicati()) ||
					!equals(as.getResource(k).getIdCollaborazione(),as.getIdCollaborazione()) ||
					!equals(as.getResource(k).getIdRiferimentoRichiesta(),as.getIdRiferimentoRichiesta()) ||
					(as.getResource(k).getScadenza()!=null && !as.getResource(k).getScadenza().equals(as.getScadenza()))
					) {
				as.getResource(k).setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			}else{
				as.getResource(k).setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
				//as.getResource(k).setProfiloCollaborazione(as.getProfiloCollaborazione());
				as.getResource(k).setConfermaRicezione(as.getConfermaRicezione());
				as.getResource(k).setConsegnaInOrdine(as.getConsegnaInOrdine());
				as.getResource(k).setFiltroDuplicati(as.getFiltroDuplicati());
				as.getResource(k).setIdCollaborazione(as.getIdCollaborazione());
				as.getResource(k).setIdRiferimentoRichiesta(as.getIdRiferimentoRichiesta());
				as.getResource(k).setScadenza(as.getScadenza());
			}			
		}
	}
	
	private void gestioneAccordoServizioParteComune(AccordoServizioParteComune as,boolean reset, boolean updateEnabled) throws Exception{
		as.setSuperUser(this.superUser);
		if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
			impostaInformazioniRegistroDB_AccordoServizioParteComune(as);
		}
		// bytes
		this.initializeWSDLBytes(as);
		for(int k=0; k<as.sizeAllegatoList();k++){
			String fileName = as.getAllegato(k).getFile();
			String name = getNomeFile(fileName);
			
			as.getAllegato(k).setByteContenuto(this.readDocumento(fileName));
			as.getAllegato(k).setFile(name);
			as.getAllegato(k).setRuolo(RuoliDocumento.allegato.toString());
			as.getAllegato(k).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
		}
		for(int k=0; k<as.sizeSpecificaSemiformaleList();k++){
			String fileName = as.getSpecificaSemiformale(k).getFile();
			String name = getNomeFile(fileName);
			
			as.getSpecificaSemiformale(k).setByteContenuto(this.readDocumento(fileName));
			as.getSpecificaSemiformale(k).setFile(name);
			as.getSpecificaSemiformale(k).setRuolo(RuoliDocumento.specificaSemiformale.toString());
			as.getSpecificaSemiformale(k).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
		}
		if(as.getServizioComposto()!=null){
			for(int k=0; k<as.getServizioComposto().sizeSpecificaCoordinamentoList();k++){
				String fileName = as.getServizioComposto().getSpecificaCoordinamento(k).getFile();
				String name = getNomeFile(fileName);
				
				as.getServizioComposto().getSpecificaCoordinamento(k).setByteContenuto(this.readDocumento(fileName));
				as.getServizioComposto().getSpecificaCoordinamento(k).setFile(name);
				as.getServizioComposto().getSpecificaCoordinamento(k).setRuolo(RuoliDocumento.specificaCoordinamento.toString());
				as.getServizioComposto().getSpecificaCoordinamento(k).setTipoProprietarioDocumento(ProprietariDocumento.accordoServizio.toString());
			}
		}
		// protocol properties: bytes
		if(as.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : as.getProtocolPropertyList()) {
				if(pp.getFile()!=null) {
					pp.setByteFile(this.readDocumento(pp.getFile()));
				}
			}
			if(as.sizeAzioneList()>0) {
				for (Azione azione : as.getAzioneList()) {
					for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
						if(pp.getFile()!=null) {
							pp.setByteFile(this.readDocumento(pp.getFile()));
						}
					}
				}
			}
			if(as.sizePortTypeList()>0) {
				for (PortType pt : as.getPortTypeList()) {
					for (ProtocolProperty pp : pt.getProtocolPropertyList()) {
						if(pp.getFile()!=null) {
							pp.setByteFile(this.readDocumento(pp.getFile()));
						}
					}
					if(pt.sizeAzioneList()>0) {
						for (Operation azione : pt.getAzioneList()) {
							for (ProtocolProperty pp : azione.getProtocolPropertyList()) {
								if(pp.getFile()!=null) {
									pp.setByteFile(this.readDocumento(pp.getFile()));
								}
							}
						}
					}
				}
			}
			if(as.sizeResourceList()>0) {
				for (Resource resource : as.getResourceList()) {
					if(resource.getPath()!=null && !"".equals(resource.getPath())) {
						String pathNormalizzato = null;
						pathNormalizzato = resource.getPath().trim();
						if(!pathNormalizzato.startsWith("/"))
							pathNormalizzato = "/" + pathNormalizzato;
						resource.setPath(pathNormalizzato);
					}
					for (ProtocolProperty pp : resource.getProtocolPropertyList()) {
						if(pp.getFile()!=null) {
							pp.setByteFile(this.readDocumento(pp.getFile()));
						}
					}
				}
			}
		}
		if(ServiceBinding.SOAP.equals(as.getServiceBinding())) {
			if(as.getFormatoSpecifica()==null) {
				as.setFormatoSpecifica(CostantiRegistroServizi.DEFAULT_VALUE_INTERFACE_TYPE_SOAP);
			}
		}else {
			if(as.getFormatoSpecifica()==null) {
				as.setFormatoSpecifica(CostantiRegistroServizi.DEFAULT_VALUE_INTERFACE_TYPE_REST);
			}
		}
	
		IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
		String uriAS = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
		if( (reset==false) && this.gestoreCRUD.existsAccordoServizioParteComune(idAccordo)){
			if(updateEnabled) {
				this.log.info("Accordo di Servizio "+uriAS+" aggiornamento in corso...");
				this.gestoreCRUD.updateAccordoServizioParteComune(as);
				this.log.info("Accordo di Servizio "+uriAS+" aggiornato.");
			}
			else {
				this.log.info("Accordo di Servizio "+uriAS+" non aggiornato (aggiornamento disabilitato).");
			}
		}else{
			this.log.info("Accordo di Servizio "+uriAS+" creazione in corso...");
			this.gestoreCRUD.createAccordoServizioParteComune(as);
			this.log.info("Accordo di Servizio "+uriAS+" creato.");
		}
	}
	
	public static void impostaInformazioniRegistroDB_AccordoServizioParteSpecifica_Fruitore(Fruitore fruitore){
		if(fruitore.getConnettore()!=null){
			// I nomi vengono autogenerati dal driver
			fruitore.getConnettore().setNome(null);
			// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
			String tipoConnettore = fruitore.getConnettore().getTipo();
			if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
				fruitore.getConnettore().setCustom(true);
			}
			// Gestione default per connettore https
			if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
				gestioneDefaultConnettoreHTTP(fruitore.getConnettore());
			}
		}
		if(fruitore.getConfigurazioneAzioneList()!=null && fruitore.sizeConfigurazioneAzioneList()>0) {
			for (int i = 0; i < fruitore.sizeConfigurazioneAzioneList(); i++) {
				if(fruitore.getConfigurazioneAzione(i).getConnettore()!=null) {
					// I nomi vengono autogenerati dal driver
					fruitore.getConfigurazioneAzione(i).getConnettore().setNome(null);
					// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
					String tipoConnettore = fruitore.getConfigurazioneAzione(i).getConnettore().getTipo();
					if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
						 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
						 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
						fruitore.getConfigurazioneAzione(i).getConnettore().setCustom(true);
					}
					// Gestione default per connettore https
					if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
						gestioneDefaultConnettoreHTTP(fruitore.getConfigurazioneAzione(i).getConnettore());
					}
				}
			}
		}
	}
	public static void impostaInformazioniRegistroDB_AccordoServizioParteSpecifica(AccordoServizioParteSpecifica servizio){
		if(servizio.getConfigurazioneServizio()!=null && servizio.getConfigurazioneServizio().getConnettore()!=null){
			// I nomi vengono autogenerati dal driver
			servizio.getConfigurazioneServizio().getConnettore().setNome(null);
			// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
			String tipoConnettore = servizio.getConfigurazioneServizio().getConnettore().getTipo();
			if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
				 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
				servizio.getConfigurazioneServizio().getConnettore().setCustom(true);
			}
			// Gestione default per connettore https
			if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
				gestioneDefaultConnettoreHTTP(servizio.getConfigurazioneServizio().getConnettore());
			}
		}
		for(int k=0; k< servizio.sizeFruitoreList();k++){
			if(servizio.getFruitore(k).getConnettore()!=null){
				impostaInformazioniRegistroDB_AccordoServizioParteSpecifica_Fruitore(servizio.getFruitore(k));
			}
		}
		if(servizio.getConfigurazioneServizio()!=null){
			for(int k=0; k< servizio.getConfigurazioneServizio().sizeConfigurazioneAzioneList();k++){
				if(servizio.getConfigurazioneServizio().getConfigurazioneAzione(k).getConnettore()!=null){
					// I nomi vengono autogenerati dal driver
					servizio.getConfigurazioneServizio().getConfigurazioneAzione(k).getConnettore().setNome(null);
					// I tipi diversi da disabilitato,http,jms,null,nullEcho sono custom
					String tipoConnettore = servizio.getConfigurazioneServizio().getConfigurazioneAzione(k).getConnettore().getTipo();
					if ( !TipiConnettore.JMS.getNome().equals(tipoConnettore) && !TipiConnettore.HTTP.getNome().equals(tipoConnettore) &&
						 !TipiConnettore.DISABILITATO.getNome().equals(tipoConnettore) && !TipiConnettore.NULL.getNome().equals(tipoConnettore) &&
						 !TipiConnettore.NULLECHO.getNome().equals(tipoConnettore) ){
						servizio.getConfigurazioneServizio().getConfigurazioneAzione(k).getConnettore().setCustom(true);
					}
					// Gestione default per connettore https
					if(TipiConnettore.HTTPS.getNome().equals(tipoConnettore)){
						gestioneDefaultConnettoreHTTP(servizio.getConfigurazioneServizio().getConfigurazioneAzione(k).getConnettore());
					}
				}
			}
		}
	}
	
	public static void impostaInformazioniRegistro_AccordoServizioParteSpecifica_update(AccordoServizioParteSpecifica servizio,AccordoServizioParteSpecifica old,
			boolean mantieniFruitoriEsistenti){
		if(old.getConfigurazioneServizio()!=null && old.getConfigurazioneServizio().getConnettore()!=null){
			if(servizio.getConfigurazioneServizio()==null || servizio.getConfigurazioneServizio().getConnettore()==null){
				if(servizio.getConfigurazioneServizio()==null){
					servizio.setConfigurazioneServizio(new ConfigurazioneServizio());
				}
				servizio.getConfigurazioneServizio().setConnettore(old.getConfigurazioneServizio().getConnettore());
			}else{
				servizio.getConfigurazioneServizio().getConnettore().setId(old.getConfigurazioneServizio().getConnettore().getId());
				servizio.getConfigurazioneServizio().getConnettore().setNome(old.getConfigurazioneServizio().getConnettore().getNome());
			}
		}
		if(mantieniFruitoriEsistenti){
			mantieniFruitori(old, servizio);
		}
		if(servizio.sizeFruitoreList()>0){
			for (int i = 0; i < servizio.sizeFruitoreList(); i++) {
				Fruitore fruitore = servizio.getFruitore(i);
				// cerco fruitore nella vecchia immagine
				for (int j = 0; j < old.sizeFruitoreList(); j++) {
					Fruitore oldFruitore = old.getFruitore(j);
					if(oldFruitore.getTipo().equals(fruitore.getTipo()) &&
							oldFruitore.getNome().equals(fruitore.getNome())){
						if(fruitore.getConnettore()==null){
							fruitore.setConnettore(oldFruitore.getConnettore());
						}else{
							fruitore.getConnettore().setId(oldFruitore.getConnettore().getId());
							fruitore.getConnettore().setNome(oldFruitore.getConnettore().getNome());
						}		
					}
				}
			}
		}
	}
	
	private void gestioneServizio(AccordoServizioParteSpecifica servizio,Soggetto soggetto,boolean reset,
			boolean mantieniFruitoriEsistenti,StatiAccordo statoAccordo, boolean updateEnabled) throws Exception{
		servizio.setSuperUser(this.superUser);
		servizio.setTipoSoggettoErogatore(soggetto.getTipo());
		servizio.setNomeSoggettoErogatore(soggetto.getNome());
		if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
			impostaInformazioniRegistroDB_AccordoServizioParteSpecifica(servizio);
		}
		// bytes
		this.initializeWSDLBytes(servizio);
		for(int k=0; k<servizio.sizeAllegatoList();k++){
			String fileName = servizio.getAllegato(k).getFile();
			String name = getNomeFile(fileName);
			
			servizio.getAllegato(k).setByteContenuto(this.readDocumento(fileName));
			servizio.getAllegato(k).setFile(name);
			servizio.getAllegato(k).setRuolo(RuoliDocumento.allegato.toString());
			servizio.getAllegato(k).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
		}
		for(int k=0; k<servizio.sizeSpecificaSemiformaleList();k++){
			String fileName = servizio.getSpecificaSemiformale(k).getFile();
			String name = getNomeFile(fileName);
			
			servizio.getSpecificaSemiformale(k).setByteContenuto(this.readDocumento(fileName));
			servizio.getSpecificaSemiformale(k).setFile(name);
			servizio.getSpecificaSemiformale(k).setRuolo(RuoliDocumento.specificaSemiformale.toString());
			servizio.getSpecificaSemiformale(k).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
		}
		for(int k=0; k<servizio.sizeSpecificaLivelloServizioList();k++){
			String fileName = servizio.getSpecificaLivelloServizio(k).getFile();
			String name = getNomeFile(fileName);
			
			servizio.getSpecificaLivelloServizio(k).setByteContenuto(this.readDocumento(fileName));
			servizio.getSpecificaLivelloServizio(k).setFile(name);
			servizio.getSpecificaLivelloServizio(k).setRuolo(RuoliDocumento.specificaLivelloServizio.toString());
			servizio.getSpecificaLivelloServizio(k).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
		}
		for(int k=0; k<servizio.sizeSpecificaSicurezzaList();k++){
			String fileName = servizio.getSpecificaSicurezza(k).getFile();
			String name = getNomeFile(fileName);
			
			servizio.getSpecificaSicurezza(k).setByteContenuto(this.readDocumento(fileName));
			servizio.getSpecificaSicurezza(k).setFile(name);
			servizio.getSpecificaSicurezza(k).setRuolo(RuoliDocumento.specificaSicurezza.toString());
			servizio.getSpecificaSicurezza(k).setTipoProprietarioDocumento(ProprietariDocumento.servizio.toString());
		}
		// protocol properties: bytes
		if(servizio.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : servizio.getProtocolPropertyList()) {
				if(pp.getFile()!=null) {
					pp.setByteFile(this.readDocumento(pp.getFile()));
				}
			}
			if(servizio.sizeFruitoreList()>0) {
				for (Fruitore fruitore : servizio.getFruitoreList()) {
					if(fruitore.sizeProtocolPropertyList()>0) {
						for (ProtocolProperty pp : fruitore.getProtocolPropertyList()) {
							if(pp.getFile()!=null) {
								pp.setByteFile(this.readDocumento(pp.getFile()));
							}
						}
					}
				}
			}
		}
		
		IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
		IDServizio idServizio = this.idServizioFactory.getIDServizioFromValues(servizio.getTipo(), servizio.getNome(), idSoggetto, servizio.getVersione());
		String uri = this.idServizioFactory.getUriFromIDServizio(idServizio);
		if( (reset==false) && (this.gestoreCRUD.existsAccordoServizioParteSpecifica(idServizio)) ){
			if(updateEnabled) {
				this.log.info("Servizio "+uri+" aggiornamento in corso...");
				
				AccordoServizioParteSpecifica old = ((IDriverRegistroServiziGet)this.gestoreCRUD).getAccordoServizioParteSpecifica(idServizio);
				impostaInformazioniRegistro_AccordoServizioParteSpecifica_update(servizio, old, mantieniFruitoriEsistenti);
				
				// stato package
				if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
					aggiornatoStatoFruitori(servizio, statoAccordo);
				}
				this.gestoreCRUD.updateAccordoServizioParteSpecifica(servizio);
				
				this.log.info("Servizio "+uri+" aggiornato.");
			}
			else {
				this.log.info("Servizio "+uri+" non aggiornato (aggiornamento disabilitato).");
			}
		}else{
			this.log.info("Servizio "+uri+" creazione in corso...");
			// stato package
			if(CostantiConfigurazione.REGISTRO_DB.equals(this.tipoBEDestinazione)){
				aggiornatoStatoFruitori(servizio, statoAccordo);
			}
			this.gestoreCRUD.createAccordoServizioParteSpecifica(servizio);
			this.log.info("Servizio "+uri+" creato.");
		}
	}
	
	

}
