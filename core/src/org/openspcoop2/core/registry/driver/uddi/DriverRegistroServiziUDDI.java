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




package org.openspcoop2.core.registry.driver.uddi;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
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
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CostantiXMLRepository;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ScopeContesto;
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
import org.openspcoop2.core.registry.driver.ProtocolPropertiesUtilities;
import org.openspcoop2.core.registry.driver.web.XMLLib;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * Classe utilizzata per effettuare query al registro UDDI, riguardanti specifiche
 * proprieta' di servizi presenti all'interno del registro.
 *
 *
 * @author Anedda Valentino (anedda@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverRegistroServiziUDDI extends BeanUtilities
   implements IDriverRegistroServiziGet,IDriverRegistroServiziCRUD, IDriverWS,IMonitoraggioRisorsa{


   
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** UDDI Lib. */
	private UDDILib uddiLib=null;
	/** --- URL Prefix utilizzato come prefisso da associare alle url memorizzate nelle entita create nell'UDDI
    e contenenti le definizioni XML ------*/
	private String urlPrefix;	
	/** --- Utility per la generazione dell'XML ------*/
	private XMLLib generatoreXML;
	
	/** Validatore della configurazione */
	private ValidatoreXSD validatoreRegistro = null;
	
	/** Logger utilizzato per info. */
	private org.slf4j.Logger log = null;

	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	private IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();


	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore per Query senza autenticazione. 
	 *
	 * 
	 */
	public DriverRegistroServiziUDDI(String inquiry,Logger alog){
		this(inquiry,null,null,alog);
	}
	/**
	 * Costruttore per Query con autenticazione. 
	 *
	 * 
	 */
	public DriverRegistroServiziUDDI(String inquiry,String user,String password,Logger alog){	
		this(inquiry,null,user,password,null,null,alog);
	}

	/**
	 * Costruttore per Gestione senza autenticazione. 
	 *
	 * 
	 */
	public DriverRegistroServiziUDDI(String inquiry,String publish, String urlPrefix, String pathPrefix,Logger alog){
		this(inquiry,publish,null,null,urlPrefix,pathPrefix,alog);
	}
	/**
	 * Costruttore per Gestione con autenticazione. 
	 *
	 * 
	 */
	public DriverRegistroServiziUDDI(String inquiry,String publish, String user,String password,String urlPrefix, String pathPrefix,Logger alog){
		try{
			if(alog==null)
				this.log = LoggerWrapperFactory.getLogger(DriverRegistroServiziUDDI.class);
			else
				this.log = alog;
			
			if(publish!=null){
				this.uddiLib=new UDDILib(inquiry,publish,user,password);
			}else{
				if(user!=null && password!=null){
					this.uddiLib=new UDDILib(inquiry,user,password);
				}else{
					this.uddiLib=new UDDILib(inquiry);
				}
			}

			/* --- Costruzione Validatore XSD -- */
			try{
				this.validatoreRegistro = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverRegistroServiziUDDI.class.getResourceAsStream("/registroServizi.xsd"));
			}catch (Exception e) {
				throw new Exception("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage(),e);
			}
			
			if(urlPrefix!=null){
				if (!urlPrefix.endsWith(CostantiRegistroServizi.URL_SEPARATOR))
					this.urlPrefix= urlPrefix + CostantiRegistroServizi.URL_SEPARATOR;
				else
					this.urlPrefix = urlPrefix;
			}
			if(urlPrefix!=null && pathPrefix!=null){
				this.generatoreXML = new XMLLib(pathPrefix,urlPrefix);
			}
			
			this.create = this.uddiLib.create;
		}catch(Exception e){
			this.log.error("Inizializzazione fallita: "+e.getMessage(),e);
			this.create = false;
		}
	}





	
	/* ********  INTERFACCIA IDriverRegistroServiziGet ******** */ 
	
	
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

		if( idAccordo == null)
			throw new DriverRegistroServiziException("[AccordoCooperazione] Parametro Non Valido");
		if( idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[AccordoCooperazione] Nome accordo servizio non fornito");
		
		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[AccordoCooperazione] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza dell'accordo
		if( this.uddiLib.existsAccordoCooperazione(idAccordo) == false){
			throw new DriverRegistroServiziNotFound("[AccordoCooperazione] Accordo richiesto non esiste: "+idAccordo);
		} 
		
		org.openspcoop2.core.registry.AccordoCooperazione accRichiesto = null;

		// Ottengo URL XML associato all'accordo
		String urlXMLAccordoCooperazione = this.uddiLib.getUrlXmlAccordoCooperazione(idAccordo);
		if(urlXMLAccordoCooperazione == null){
			throw new DriverRegistroServiziException("[AccordoCooperazione] definzione XML non disponibile");
		}

		// Ottengo oggetto AccordoCooperazione
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordoCooperazione);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[AccordoCooperazione] Accordo richiesto non esiste nel repository http: "+idAccordo);
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLAccordoCooperazione);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[AccordoCooperazione] Riscontrato errore durante la validazione XSD Accordo("+idAccordo+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeAccordoCooperazioneList()>0)
				accRichiesto = rs.getAccordoCooperazione(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[AccordoCooperazione] Errore durante il parsing xml Accordo("+idAccordo+"): "+e.getMessage(),e);
		}

		if(accRichiesto==null)
			throw new DriverRegistroServiziNotFound("[AccordoCooperazione] Accordo di Cooperazione ["+idAccordo+"] non trovato.");
		
		return accRichiesto;
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
		try{

			// Ricerca UDDI degli accordi
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			IDAccordoCooperazione idAccordoFiltro = null;
			if(filtroRicerca!=null && filtroRicerca.getNomeAccordo()!=null){
				IDSoggetto soggettoReferente = null;
				if(filtroRicerca.getTipoSoggettoReferente()!=null && filtroRicerca.getNomeSoggettoReferente()!=null){
					soggettoReferente = new IDSoggetto(filtroRicerca.getTipoSoggettoReferente(), filtroRicerca.getNomeSoggettoReferente());
				}
				idAccordoFiltro = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(filtroRicerca.getNomeAccordo(),soggettoReferente,filtroRicerca.getVersione());
			}
			
			String [] urlXMLAccordi = this.uddiLib.getUrlXmlAccordiCooperazione(idAccordoFiltro,this.urlPrefix);
			
			// Esamina degli accordi
			List<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();
			for(int i=0; i<urlXMLAccordi.length; i++){
				org.openspcoop2.core.registry.AccordoCooperazione ac = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLAccordi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] Riscontrato errore durante la validazione XSD ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Accordo
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordi[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeAccordoCooperazioneList()>0)
						ac = rs.getAccordoCooperazione(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] Errore durante il parsing xml ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				if(ac==null)
					throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] accordo non definito per la url ["+urlXMLAccordi[i]+"]");
				String acURI = this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ac.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMinDate) Accordo di cooperazione ["+acURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ac.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ac.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMaxDate) Accordo di cooperazione ["+acURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ac.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNomeAccordo()!=null){
						if(ac.getNome().equals(filtroRicerca.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(ac.getVersione().equals(filtroRicerca.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
						if(ac.getSoggettoReferente()==null)
							continue;
						if(filtroRicerca.getTipoSoggettoReferente()!=null){
							if(ac.getSoggettoReferente().getTipo().equals(filtroRicerca.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicerca.getNomeSoggettoReferente()!=null){
							if(ac.getSoggettoReferente().getNome().equals(filtroRicerca.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(ac, filtroRicerca.getProtocolPropertiesAccordo())==false){
						continue;
					}
				}
				idAccordi.add(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(ac));
			}
			if(idAccordi.size()==0){
				throw new DriverRegistroServiziNotFound("Accordi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idAccordi;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdAccordiCooperazione error",e);
		}
	}
	
	
	
	
	
	
	/* Accordi di Servizio Parte Comune */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteComune}, 
	 * identificato grazie al parametro 
	 * <var>nomeAccordo</var> 
	 *
	 * @param idAccordo Identificativo dell'accordo di Servizio
	 * @return un oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteComune}.
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteComune getAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if( idAccordo == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro Non Valido");
		if( idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Nome accordo servizio non fornito");
		
		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza dell'accordo
		if( this.uddiLib.existsAccordoServizio(idAccordo) == false){
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteComune] Accordo richiesto non esiste: "+idAccordo);
		} 
		
		org.openspcoop2.core.registry.AccordoServizioParteComune accRichiesto = null;

		// Ottengo URL XML associato all'accordo
		String urlXMLAccordoServizio = this.uddiLib.getUrlXmlAccordoServizio(idAccordo);
		if(urlXMLAccordoServizio == null){
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] definzione XML non disponibile");
		}

		// Ottengo oggetto AccordoServizio
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordoServizio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getAccordoServizioParteComune] Accordo richiesto non esiste nel repository http: "+idAccordo);
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLAccordoServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Riscontrato errore durante la validazione XSD Accordo("+idAccordo+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeAccordoServizioParteComuneList()>0)
				accRichiesto = rs.getAccordoServizioParteComune(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Errore durante il parsing xml Accordo("+idAccordo+"): "+e.getMessage(),e);
		}

		if(accRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteComune] Accordo di Servizio ["+idAccordo+"] non trovato.");
		
		// nomiAzione setting 
//		accRichiesto.setNomiAzione(accRichiesto.readNomiAzione());
		
		return accRichiesto;
	}

	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteComune(FiltroRicercaAccordi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		List<IDAccordo> list = new ArrayList<IDAccordo>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAccordiServizioParteComune", filtroRicerca, null, null, null, null, list);
		return list;
	
	}
	
	@Override
	public List<IDPortType> getAllIdPortType(FiltroRicercaPortTypes filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDPortType> list = new ArrayList<IDPortType>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdPortType", filtroRicerca, filtroRicerca, null, null, null, list);
		return list;
		
	}
	
	@Override
	public List<IDPortTypeAzione> getAllIdAzionePortType(FiltroRicercaOperations filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
	
		List<IDPortTypeAzione> list = new ArrayList<IDPortTypeAzione>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAzionePortType", filtroRicerca, null, filtroRicerca, null, null, list);
		return list;
		
	}
	
	@Override
	public List<IDAccordoAzione> getAllIdAzioneAccordo(FiltroRicercaAzioni filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDAccordoAzione> list = new ArrayList<IDAccordoAzione>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdAzioneAccordo", filtroRicerca, null, null, filtroRicerca, null, list);
		return list;
		
	}
	
	@Override
	public List<IDResource> getAllIdResource(FiltroRicercaResources filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		List<IDResource> list = new ArrayList<IDResource>();
		_fillAllIdAccordiServizioParteComuneEngine("getAllIdResource", filtroRicerca, null, null, null, filtroRicerca, list);
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> void _fillAllIdAccordiServizioParteComuneEngine(String nomeMetodo, 
			FiltroRicercaAccordi filtroRicercaBase,
			FiltroRicercaPortTypes filtroPT, FiltroRicercaOperations filtroOP, FiltroRicercaAzioni filtroAZ,
			FiltroRicercaResources filtroResource,
			List<T> listReturn) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{

			// Ricerca UDDI degli accordi
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdAccordiServizio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			IDAccordo idAccordoFiltro = null;
			if(filtroRicercaBase!=null && filtroRicercaBase.getNomeAccordo()!=null){
				IDSoggetto soggettoReferente = null;
				if(filtroRicercaBase.getTipoSoggettoReferente()!=null && filtroRicercaBase.getNomeSoggettoReferente()!=null){
					soggettoReferente = new IDSoggetto(filtroRicercaBase.getTipoSoggettoReferente(),filtroRicercaBase.getNomeSoggettoReferente());
				}
				idAccordoFiltro = this.idAccordoFactory.getIDAccordoFromValues(filtroRicercaBase.getNomeAccordo(),soggettoReferente,filtroRicercaBase.getVersione());
			}
			
			String [] urlXMLAccordi = this.uddiLib.getUrlXmlAccordiServizio(idAccordoFiltro,this.urlPrefix);
			
			// Esamina degli accordi
			List<IDAccordo> idAccordi = new ArrayList<IDAccordo>();
			for(int i=0; i<urlXMLAccordi.length; i++){
				org.openspcoop2.core.registry.AccordoServizioParteComune as = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLAccordi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("["+nomeMetodo+"] Riscontrato errore durante la validazione XSD ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Accordo
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordi[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeAccordoServizioParteComuneList()>0)
						as = rs.getAccordoServizioParteComune(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("["+nomeMetodo+"] Errore durante il parsing xml ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				if(as==null)
					throw new DriverRegistroServiziException("["+nomeMetodo+"] accordo non definito per la url ["+urlXMLAccordi[i]+"]");
				String asURI = this.idAccordoFactory.getUriFromAccordo(as);
				
				if(filtroRicercaBase!=null){
					// Filtro By Data
					if(filtroRicercaBase.getMinDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMinDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().before(filtroRicercaBase.getMinDate())){
							continue;
						}
					}
					if(filtroRicercaBase.getMaxDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMaxDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().after(filtroRicercaBase.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicercaBase.getNomeAccordo()!=null){
						if(as.getNome().equals(filtroRicercaBase.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getVersione()!=null){
						if(as.getVersione().equals(filtroRicercaBase.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
						if(as.getSoggettoReferente()==null)
							continue;
						if(filtroRicercaBase.getTipoSoggettoReferente()!=null){
							if(as.getSoggettoReferente().getTipo().equals(filtroRicercaBase.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicercaBase.getNomeSoggettoReferente()!=null){
							if(as.getSoggettoReferente().getNome().equals(filtroRicercaBase.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					if(filtroRicercaBase.getServiceBinding()!=null){
						if(as.getServiceBinding().equals(filtroRicercaBase.getServiceBinding()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getIdGruppo()!=null && filtroRicercaBase.getIdGruppo().getNome()!=null){
						boolean found = false;
						if(as.getGruppi()!=null && as.getGruppi().sizeGruppoList()>0) {
							for (GruppoAccordo gruppo : as.getGruppi().getGruppoList()) {
								if(gruppo.getNome().equals(filtroRicercaBase.getIdGruppo().getNome())) {
									found = true;
									break;
								}
							}
						}
						if(!found) {
							continue;
						}
					}
					
					if(filtroRicercaBase.getIdAccordoCooperazione()!=null &&
							(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null || 
							filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null) ){
						if(as.getServizioComposto()==null){
							continue;
						}
						IDAccordoCooperazione idAC = this.idAccordoCooperazioneFactory.getIDAccordoFromUri(as.getServizioComposto().getAccordoCooperazione());
						if(filtroRicercaBase.getIdAccordoCooperazione().getNome()!=null){
							if(idAC.getNome().equals(filtroRicercaBase.getIdAccordoCooperazione().getNome())== false){
								continue;
							}
						}
						if(filtroRicercaBase.getIdAccordoCooperazione().getVersione()!=null){
							if(idAC.getVersione().equals(filtroRicercaBase.getIdAccordoCooperazione().getVersione())== false){
								continue;
							}
						}
					}
					else if(filtroRicercaBase.isServizioComposto()!=null){
						if(filtroRicercaBase.isServizioComposto()){
							if(as.getServizioComposto()==null){
								continue;
							}
						}
						else {
							if(as.getServizioComposto()!=null){
								continue;
							}
						}
					}
					
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(as, filtroRicercaBase.getProtocolPropertiesAccordo())==false){
						continue;
					}
					
				}
				
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione());
				
				if(filtroPT!=null){
					for (PortType pt : as.getPortTypeList()) {
						// Nome PT
						if(filtroPT.getNomePortType()!=null){
							if(pt.getNome().equals(filtroPT.getNomePortType()) == false){
								continue;
							}
						}
						// ProtocolProperties PT
						if(ProtocolPropertiesUtilities.isMatch(pt, filtroPT.getProtocolPropertiesPortType())==false){
							continue;
						}
						
						IDPortType idPT = new IDPortType();
						idPT.setIdAccordo(idAccordo);
						idPT.setNome(pt.getNome());
						listReturn.add((T)idPT);
					}
				}
				else if(filtroOP!=null){
					for (PortType pt : as.getPortTypeList()) {
						
						// Nome PT
						if(filtroOP.getNomePortType()!=null){
							if(pt.getNome().equals(filtroOP.getNomePortType()) == false){
								continue;
							}
						}
						// ProtocolProperties PT
						if(ProtocolPropertiesUtilities.isMatch(pt, filtroOP.getProtocolPropertiesPortType())==false){
							continue;
						}
						
						for (Operation op : pt.getAzioneList()) {
							
							// Nome OP
							if(filtroOP.getNomeAzione()!=null){
								if(op.getNome().equals(filtroOP.getNomeAzione()) == false){
									continue;
								}
							}
							// ProtocolProperties OP
							if(ProtocolPropertiesUtilities.isMatch(pt, filtroOP.getProtocolPropertiesAzione())==false){
								continue;
							}
						
							IDPortTypeAzione idAzione = new IDPortTypeAzione();
							IDPortType idPT = new IDPortType();
							idPT.setIdAccordo(idAccordo);
							idPT.setNome(pt.getNome());
							idAzione.setIdPortType(idPT);
							idAzione.setNome(op.getNome());
							listReturn.add((T)idAzione);
						}
					}
				}
				else if(filtroAZ!=null){
					for (Azione az : as.getAzioneList()) {
						
						// Nome AZ
						if(filtroAZ.getNomeAzione()!=null){
							if(az.getNome().equals(filtroAZ.getNomeAzione()) == false){
								continue;
							}
						}
						// ProtocolProperties PT
						if(ProtocolPropertiesUtilities.isMatch(az, filtroAZ.getProtocolPropertiesAzione())==false){
							continue;
						}
						
						IDAccordoAzione idAzione = new IDAccordoAzione();
						idAzione.setIdAccordo(idAccordo);
						idAzione.setNome(az.getNome());
						listReturn.add((T)idAzione);
					}
				}
				else if(filtroResource!=null){
					for (Resource resource : as.getResourceList()) {
						// Nome Risorsa
						if(filtroResource.getResourceName()!=null){
							if(resource.getNome().equals(filtroResource.getResourceName()) == false){
								continue;
							}
						}
						// ProtocolProperties PT
						if(ProtocolPropertiesUtilities.isMatch(resource, filtroResource.getProtocolPropertiesResources())==false){
							continue;
						}
						
						IDResource idResource = new IDResource();
						idResource.setIdAccordo(idAccordo);
						idResource.setNome(resource.getNome());
						listReturn.add((T)idResource);
					}
				}
				else{
					listReturn.add((T)idAccordo);
				}
			}
			if(idAccordi.size()<=0){
				String msgFiltro = "Elementi non trovati che rispettano il filtro di ricerca selezionato: ";
				if(filtroPT!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroPT.toString());
				}
				else if(filtroOP!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroOP.toString());
				}
				else if(filtroAZ!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroAZ.toString());
				}
				else if(filtroRicercaBase!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroRicercaBase.toString());
				}
				else{
					throw new DriverRegistroServiziNotFound("Elementi non trovati");
				}
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException(nomeMetodo+" error",e);
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
		if( nomePdD == null)
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro Non Valido");

		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getPortaDominio] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza della porta di dominio
		if( this.uddiLib.existsPortaDominio(nomePdD) == false){
			throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di dominio richiesta non esiste: "+nomePdD);
		} 
		
		org.openspcoop2.core.registry.PortaDominio pddRichiesta = null;

		// Ottengo URL XML associato alla porta di dominio
		String urlXMLPortaDominio = this.uddiLib.getUrlXmlPortaDominio(nomePdD);
		if(urlXMLPortaDominio == null){
			throw new DriverRegistroServiziException("[getPortaDominio] definzione XML non disponibile");
		}

		// Ottengo oggetto PortaDominio
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLPortaDominio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di dominio richiesta non esiste nel repository http: "+nomePdD);
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLPortaDominio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getPortaDominio] Riscontrato errore durante la validazione XSD della Porta di dominio("+nomePdD+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizePortaDominioList()>0)
				pddRichiesta = rs.getPortaDominio(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getPortaDominio] Errore durante il parsing xml della Porta di dominio("+nomePdD+"): "+e.getMessage(),e);
		}

		if(pddRichiesta==null)
			throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di dominio ["+nomePdD+"] non trovata.");
		
		return pddRichiesta;
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
		try{

			// Ricerca UDDI delle porte di dominio
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdPorteDominio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			String nomeFiltro = null;
			if(filtroRicerca!=null)
				nomeFiltro = filtroRicerca.getNome();
			String [] urlXMLPdd = this.uddiLib.getUrlXmlPortaDominio(nomeFiltro,this.urlPrefix);
			
			// Esamina delle porte di dominio
			List<String> nomiPdd = new ArrayList<String>();
			for(int i=0; i<urlXMLPdd.length; i++){
				org.openspcoop2.core.registry.PortaDominio pd = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLPdd[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdPorteDominio] Riscontrato errore durante la validazione XSD ("+urlXMLPdd[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Porta di dominio
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLPdd[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizePortaDominioList()>0)
						pd = rs.getPortaDominio(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdPorteDominio] Errore durante il parsing xml ("+urlXMLPdd[i]+"): "+e.getMessage(),e);
				}
				if(pd==null)
					throw new DriverRegistroServiziException("[getAllIdPorteDominio] porta di dominio non definita per la url ["+urlXMLPdd[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(pd.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMinDate) Porta di dominio ["+pd.getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pd.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(pd.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMaxDate) Porta di dominio ["+pd.getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pd.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(pd.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
				}
				nomiPdd.add(pd.getNome());
			}
			if(nomiPdd.size()==0){
				throw new DriverRegistroServiziNotFound("Porte di dominio non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return nomiPdd;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdPorteDominio error",e);
		}
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
	public Gruppo getGruppo(
			IDGruppo idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		if(idGruppo==null || idGruppo.getNome()==null)
			throw new DriverRegistroServiziException("[getGruppo] Parametro Non Valido");

		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getGruppo] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza del gruppo
		if( this.uddiLib.existsGruppo(idGruppo.getNome()) == false){
			throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo richiesto non esiste: "+idGruppo.getNome());
		} 
		
		org.openspcoop2.core.registry.Gruppo gruppoRichiesto = null;

		// Ottengo URL XML associato al gruppo
		String urlXMLGruppo = this.uddiLib.getUrlXmlGruppo(idGruppo.getNome());
		if(urlXMLGruppo == null){
			throw new DriverRegistroServiziException("[getGruppo] definzione XML non disponibile");
		}

		// Ottengo oggetto PortaDominio
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLGruppo);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo richiesto non esiste nel repository http: "+idGruppo.getNome());
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLGruppo);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getGruppo] Riscontrato errore durante la validazione XSD del Gruppo ("+idGruppo.getNome()+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeGruppoList()>0)
				gruppoRichiesto = rs.getGruppo(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getGruppo] Errore durante il parsing xml del Gruppo ("+idGruppo.getNome()+"): "+e.getMessage(),e);
		}

		if(gruppoRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo ["+idGruppo.getNome()+"] non trovato.");
		
		return gruppoRichiesto;
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
	public List<IDGruppo> getAllIdGruppi(
			FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{

			// Ricerca UDDI delle porte di dominio
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdPorteDominio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			String nomeFiltro = null;
			if(filtroRicerca!=null)
				nomeFiltro = filtroRicerca.getNome();
			String [] urlXMLGruppi = this.uddiLib.getUrlXmlGruppo(nomeFiltro,this.urlPrefix);
			
			// Esamina dei Gruppi
			List<IDGruppo> idGruppi = new ArrayList<IDGruppo>();
			for(int i=0; i<urlXMLGruppi.length; i++){
				org.openspcoop2.core.registry.Gruppo gruppo = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLGruppi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdGruppi] Riscontrato errore durante la validazione XSD ("+urlXMLGruppi[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Porta di dominio
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLGruppi[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeGruppoList()>0)
						gruppo = rs.getGruppo(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdGruppi] Errore durante il parsing xml ("+urlXMLGruppi[i]+"): "+e.getMessage(),e);
				}
				if(gruppo==null)
					throw new DriverRegistroServiziException("[getAllIdGruppi] Gruppo non definito per la url ["+urlXMLGruppi[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(gruppo.getOraRegistrazione()==null){
							this.log.debug("[getAllIdGruppi](FiltroByMinDate) Gruppo ["+gruppo.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(gruppo.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(gruppo.getOraRegistrazione()==null){
							this.log.debug("[getAllIdGruppi](FiltroByMaxDate) Gruppo ["+gruppo.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(gruppo.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(gruppo.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By ServiceBinding
					if(filtroRicerca.getServiceBinding()!=null){
						if(gruppo.getServiceBinding()!=null){ // se e' uguale a null significa che va bene per qualsiasi service binding
							if(gruppo.getServiceBinding().equals(filtroRicerca.getServiceBinding()) == false) {
								continue;
							}
						}
					}
				}
				IDGruppo id = new IDGruppo(gruppo.getNome());
				idGruppi.add(id);
			}
			if(idGruppi.size()==0){
				throw new DriverRegistroServiziNotFound("Gruppi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idGruppi;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdGruppi error",e);
		}
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
	public Ruolo getRuolo(
			IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		if(idRuolo==null || idRuolo.getNome()==null)
			throw new DriverRegistroServiziException("[getRuolo] Parametro Non Valido");

		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getRuolo] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza del ruolo
		if( this.uddiLib.existsRuolo(idRuolo.getNome()) == false){
			throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo richiesto non esiste: "+idRuolo.getNome());
		} 
		
		org.openspcoop2.core.registry.Ruolo ruoloRichiesto = null;

		// Ottengo URL XML associato al ruolo
		String urlXMLRuolo = this.uddiLib.getUrlXmlRuolo(idRuolo.getNome());
		if(urlXMLRuolo == null){
			throw new DriverRegistroServiziException("[getRuolo] definzione XML non disponibile");
		}

		// Ottengo oggetto PortaDominio
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLRuolo);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo richiesto non esiste nel repository http: "+idRuolo.getNome());
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLRuolo);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getRuolo] Riscontrato errore durante la validazione XSD del Ruolo ("+idRuolo.getNome()+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeRuoloList()>0)
				ruoloRichiesto = rs.getRuolo(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getRuolo] Errore durante il parsing xml del Ruolo ("+idRuolo.getNome()+"): "+e.getMessage(),e);
		}

		if(ruoloRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo ["+idRuolo.getNome()+"] non trovato.");
		
		return ruoloRichiesto;
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
	public List<IDRuolo> getAllIdRuoli(
			FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{

			// Ricerca UDDI delle porte di dominio
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdPorteDominio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			String nomeFiltro = null;
			if(filtroRicerca!=null)
				nomeFiltro = filtroRicerca.getNome();
			String [] urlXMLRuoli = this.uddiLib.getUrlXmlRuolo(nomeFiltro,this.urlPrefix);
			
			// Esamina dei Ruoli
			List<IDRuolo> idRuoli = new ArrayList<IDRuolo>();
			for(int i=0; i<urlXMLRuoli.length; i++){
				org.openspcoop2.core.registry.Ruolo ruolo = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLRuoli[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdRuoli] Riscontrato errore durante la validazione XSD ("+urlXMLRuoli[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Porta di dominio
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLRuoli[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeRuoloList()>0)
						ruolo = rs.getRuolo(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdRuoli] Errore durante il parsing xml ("+urlXMLRuoli[i]+"): "+e.getMessage(),e);
				}
				if(ruolo==null)
					throw new DriverRegistroServiziException("[getAllIdRuoli] Ruolo non definito per la url ["+urlXMLRuoli[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ruolo.getOraRegistrazione()==null){
							this.log.debug("[getAllIdRuoli](FiltroByMinDate) Ruolo ["+ruolo.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ruolo.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ruolo.getOraRegistrazione()==null){
							this.log.debug("[getAllIdRuoli](FiltroByMaxDate) Ruolo ["+ruolo.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ruolo.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(ruolo.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Tipologia
					if(filtroRicerca.getTipologia()!=null && !RuoloTipologia.QUALSIASI.equals(filtroRicerca.getTipologia())){
						if(ruolo.getTipologia()==null){
							continue;
						}
						if(!RuoloTipologia.QUALSIASI.equals(ruolo.getTipologia())){
							if(ruolo.getTipologia().equals(filtroRicerca.getTipologia()) == false){
								continue;
							}
						}
					}
					// Filtro By Contesto
					if(filtroRicerca.getContesto()!=null && !RuoloContesto.QUALSIASI.equals(filtroRicerca.getContesto())){
						if(ruolo.getContestoUtilizzo()==null){
							continue;
						}
						if(!RuoloContesto.QUALSIASI.equals(ruolo.getContestoUtilizzo())){
							if(ruolo.getContestoUtilizzo().equals(filtroRicerca.getContesto()) == false){
								continue;
							}
						}
					}
				}
				IDRuolo id = new IDRuolo(ruolo.getNome());
				idRuoli.add(id);
			}
			if(idRuoli.size()==0){
				throw new DriverRegistroServiziNotFound("Ruoli non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idRuoli;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdRuoli error",e);
		}
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
	public Scope getScope(
			IDScope idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		if(idScope==null || idScope.getNome()==null)
			throw new DriverRegistroServiziException("[getScope] Parametro Non Valido");

		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getScope] Inizializzazione dell'engine UDDI errata");
		}
		
		//		 Controllo pre-esistenza del scope
		if( this.uddiLib.existsScope(idScope.getNome()) == false){
			throw new DriverRegistroServiziNotFound("[getScope] Scope richiesto non esiste: "+idScope.getNome());
		} 
		
		org.openspcoop2.core.registry.Scope scopeRichiesto = null;

		// Ottengo URL XML associato al scope
		String urlXMLScope = this.uddiLib.getUrlXmlScope(idScope.getNome());
		if(urlXMLScope == null){
			throw new DriverRegistroServiziException("[getScope] definzione XML non disponibile");
		}

		// Ottengo oggetto PortaDominio
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLScope);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getScope] Scope richiesto non esiste nel repository http: "+idScope.getNome());
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLScope);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getScope] Riscontrato errore durante la validazione XSD del Scope ("+idScope.getNome()+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeScopeList()>0)
				scopeRichiesto = rs.getScope(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getScope] Errore durante il parsing xml del Scope ("+idScope.getNome()+"): "+e.getMessage(),e);
		}

		if(scopeRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getScope] Scope ["+idScope.getNome()+"] non trovato.");
		
		return scopeRichiesto;
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
	public List<IDScope> getAllIdScope(
			FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{

			// Ricerca UDDI delle porte di dominio
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdPorteDominio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			String nomeFiltro = null;
			if(filtroRicerca!=null)
				nomeFiltro = filtroRicerca.getNome();
			String [] urlXMLScope = this.uddiLib.getUrlXmlScope(nomeFiltro,this.urlPrefix);
			
			// Esamina dei Scope
			List<IDScope> idScope = new ArrayList<IDScope>();
			for(int i=0; i<urlXMLScope.length; i++){
				org.openspcoop2.core.registry.Scope scope = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLScope[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdScope] Riscontrato errore durante la validazione XSD ("+urlXMLScope[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Porta di dominio
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLScope[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeScopeList()>0)
						scope = rs.getScope(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdScope] Errore durante il parsing xml ("+urlXMLScope[i]+"): "+e.getMessage(),e);
				}
				if(scope==null)
					throw new DriverRegistroServiziException("[getAllIdScope] Scope non definito per la url ["+urlXMLScope[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(scope.getOraRegistrazione()==null){
							this.log.debug("[getAllIdScope](FiltroByMinDate) Scope ["+scope.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(scope.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(scope.getOraRegistrazione()==null){
							this.log.debug("[getAllIdScope](FiltroByMaxDate) Scope ["+scope.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(scope.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(scope.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Tipologia
					if(filtroRicerca.getTipologia()!=null){
						if(scope.getTipologia()==null){
							continue;
						}
						if(scope.getTipologia().equals(filtroRicerca.getTipologia()) == false){
							continue;
						}
					}
					// Filtro By Contesto
					if(filtroRicerca.getContesto()!=null && !ScopeContesto.QUALSIASI.equals(filtroRicerca.getContesto())){
						if(scope.getContestoUtilizzo()==null){
							continue;
						}
						if(!ScopeContesto.QUALSIASI.equals(scope.getContestoUtilizzo())){
							if(scope.getContestoUtilizzo().equals(filtroRicerca.getContesto()) == false){
								continue;
							}
						}
					}
				}
				IDScope id = new IDScope(scope.getNome());
				idScope.add(id);
			}
			if(idScope.size()==0){
				throw new DriverRegistroServiziNotFound("Scope non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idScope;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdScope error",e);
		}
	}
	
	
	
	
	
	
	/* Soggetti */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.Soggetto}, 
	 * identificato grazie al parametro 
	 * <var>idSoggetto</var> di tipo {@link org.openspcoop2.core.id.IDSoggetto}. 
	 *
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.Soggetto} 
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.Soggetto getSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if(idSoggetto==null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametro Non Valido");
		
		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getSoggetto] Inizializzazione dell'engine UDDI errata");
		}
		
		
		if( this.uddiLib.existsSoggetto(idSoggetto) == false){
			throw new DriverRegistroServiziNotFound("[getSoggetto] Il soggetto ["+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+
			"] non risulta gia' inserito nel registro dei servizi.");
		} 
		
		org.openspcoop2.core.registry.Soggetto soggRichiesto = null;

		// Ottengo URL XML associato al Soggetto
		String urlXMLSoggetto = this.uddiLib.getUrlXmlSoggetto(idSoggetto);
		if(urlXMLSoggetto == null){
			throw new DriverRegistroServiziException("[getSoggetto] Definizione xml non disponibile");
		}

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLSoggetto);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto richiesto non esiste nel repository http: "+idSoggetto);
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLSoggetto);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getSoggetto] Riscontrato errore durante la validazione XSD del soggetto("+idSoggetto+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeSoggettoList()>0)
				soggRichiesto = rs.getSoggetto(0);
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getSoggetto] Errore durante il parsing xml del soggetto("+idSoggetto+"): "+e.getMessage(),e);
		}

		if(soggRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto ["+idSoggetto+"] non trovato.");
		
		return soggRichiesto;
	}

	@Override
	public Soggetto getSoggettoByCredenzialiBasic(
			String user,String password, 
			CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.BASIC, user, password, 
				null, null, null, false,
				null,
				config,
				false);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiApiKey(
			String user,String password, boolean appId,
			CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.BASIC, user, password, 
				null, null, null, false,
				null,
				config,
				appId);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(
			String subject, String issuer) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
				subject, issuer, null, false,
				null,
				null,
				false);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiSsl(CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.SSL, null, null, 
				null, null, certificate, strictVerifier,
				null,
				null,
				false);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiPrincipal(
			String principal) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.PRINCIPAL, null, null, 
				null, null, null, false,
				principal,
				null,
				false);
	}
	private org.openspcoop2.core.registry.Soggetto _getSoggettoAutenticato(CredenzialeTipo tipoCredenziale, String user,String password, 
			String aSubject, String aIssuer, CertificateInfo aCertificate, boolean aStrictVerifier, 
			String principal, 
			CryptConfig config,
			boolean appId) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		// conrollo consistenza
		if (tipoCredenziale == null)
			throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro tipoCredenziale is null");

		switch (tipoCredenziale) {
		case BASIC:
			if (user == null || "".equalsIgnoreCase(user))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro user is null (required for basic auth)");
			if (password == null || "".equalsIgnoreCase(password))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro password is null (required for basic auth)");
			break;
		case APIKEY:
			if (user == null || "".equalsIgnoreCase(user))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro user is null (required for apikey auth)");
			if (password == null || "".equalsIgnoreCase(password))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro password is null (required for apikey auth)");
			break;
		case SSL:
			if ( (aSubject == null || "".equalsIgnoreCase(aSubject)) && (aCertificate==null))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro subject/certificate is null (required for ssl auth)");
			break;
		case PRINCIPAL:
			if (principal == null || "".equalsIgnoreCase(principal))
				throw new DriverRegistroServiziException("[getSoggettoAutenticato] Parametro principal is null (required for principal auth)");
			break;
		}

		IDSoggetto idSoggetto = null;
		try{
			FiltroRicercaSoggetti filtroRicerca = new FiltroRicercaSoggetti();
			CredenzialiSoggetto credenzialiSoggetto = new CredenzialiSoggetto();
			credenzialiSoggetto.setTipo(tipoCredenziale);
			switch (tipoCredenziale) {
			case BASIC:
				credenzialiSoggetto.setUser(user);
				credenzialiSoggetto.setPassword(password);
				break;
			case APIKEY:
				credenzialiSoggetto.setUser(user);
				credenzialiSoggetto.setPassword(password);
				credenzialiSoggetto.setAppId(appId);
				break;
			case SSL:
				credenzialiSoggetto.setSubject(aSubject);
				credenzialiSoggetto.setIssuer(aIssuer);
				if(aCertificate!=null) {
					try {
						credenzialiSoggetto.setCertificate(aCertificate.getCertificate().getEncoded());
					}catch(Exception e) {
						throw new DriverRegistroServiziException(e.getMessage(),e);
					}
				}
				credenzialiSoggetto.setCertificateStrictVerification(aStrictVerifier);
				break;
			case PRINCIPAL:
				credenzialiSoggetto.setUser(principal);
				break;
			}
			filtroRicerca.setCredenzialiSoggetto(credenzialiSoggetto, config);
			List<IDSoggetto> l = this.getAllIdSoggetti(filtroRicerca);
			if(l.size()>1){
				throw new DriverRegistroServiziException("Trovato più di un soggetto che possiede le credenziali '"+tipoCredenziale.toString()+"' fornite");
			}
			else if(l.size()==1){
				idSoggetto = l.get(0);
			}
		}catch(DriverRegistroServiziNotFound notFound){}
		
		if(idSoggetto==null){
			throw new DriverRegistroServiziNotFound("Nessun soggetto trovato che possiede le credenziali '"+tipoCredenziale.toString()+"' fornite");
		}
		else{
			return this.getSoggetto(idSoggetto);
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
		try{
			// Ricerca UDDI dei soggetti
			String [] urlXMLSoggetti = this.uddiLib.getUrlXmlSoggetti();
			
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
							crypt = CryptFactory.getCrypt(this.log, config);
						}catch(Exception e) {
							throw new DriverRegistroServiziException(e.getMessage(),e);
						}
					}
				}
				else if(CredenzialeTipo.APIKEY.equals(cTipo)){
					CryptConfig config = filtroRicerca.getCryptConfig();
					if(config!=null) {
						try {
							crypt = CryptFactory.getCrypt(this.log, config);
						}catch(Exception e) {
							throw new DriverRegistroServiziException(e.getMessage(),e);
						}
					}
					else {
						testInChiaro = true;
					}
				}
			}
			
			// Esamina dei soggetti
			List<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();
			for(int i=0; i<urlXMLSoggetti.length; i++){
				
				org.openspcoop2.core.registry.Soggetto ss = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLSoggetti[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] Riscontrato errore durante la validazione XSD ("+urlXMLSoggetti[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Soggetto
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLSoggetti[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeSoggettoList()>0)
						ss = rs.getSoggetto(0);
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] Errore durante il parsing xml ("+urlXMLSoggetti[i]+"): "+e.getMessage(),e);
				}
				if(ss==null)
					throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] soggetto non definito per la url ["+urlXMLSoggetti[i]+"]");
				
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ss.getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggettiRegistro](FiltroByMinDate) Soggetto ["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ss.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ss.getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggettiRegistro](FiltroByMaxDate) Soggetto ["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ss.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(ss.getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(ss.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Pdd
					if(filtroRicerca.getNomePdd()!=null){
						if(ss.getPortaDominio().equals(filtroRicerca.getNomePdd()) == false){
							continue;
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(ss, filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					// Filtro By Ruoli
					if(filtroRicerca.getIdRuolo()!=null && filtroRicerca.getIdRuolo().getNome()!=null){
						if(ss.getRuoli()==null){
							continue;
						}
						boolean contains = false;
						for (int j = 0; j < ss.getRuoli().sizeRuoloList(); j++) {
							if(filtroRicerca.getIdRuolo().getNome().equals(ss.getRuoli().getRuolo(j).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By Credenziali
					if(filtroRicerca.getCredenzialiSoggetto()!=null){
						CredenzialiSoggetto credenziali = ss.getCredenziali();
						if(credenziali==null){
							continue;
						}
						if(filtroRicerca.getCredenzialiSoggetto().getTipo()!=null){
							if(credenziali.getTipo()==null){
								if(filtroRicerca.getCredenzialiSoggetto().getTipo().equals(CredenzialeTipo.SSL) == false){ // ssl è il default
									continue;
								}	
							}
							else{
								if(filtroRicerca.getCredenzialiSoggetto().getTipo().equals(credenziali.getTipo())==false){
									continue;
								}
								if(CredenzialeTipo.APIKEY.equals(filtroRicerca.getCredenzialiSoggetto().getTipo())){
									if(filtroRicerca.getCredenzialiSoggetto().isAppId()) {
										if(!credenziali.isAppId()) {
											continue;
										}
									}
									else {
										if(credenziali.isAppId()) {
											continue;
										}
									}
								}
							}
						}
						if(filtroRicerca.getCredenzialiSoggetto().getUser()!=null){
							if(filtroRicerca.getCredenzialiSoggetto().getUser().equals(credenziali.getUser())==false){
								continue;
							}
						}
						if(filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
							String passwordSaved =  credenziali.getPassword();
							boolean found = false;
							if(testInChiaro) {
								found = filtroRicerca.getCredenzialiSoggetto().getPassword().equals(passwordSaved);
							}
							if(!found && crypt!=null) {
								found = crypt.check(filtroRicerca.getCredenzialiSoggetto().getPassword(), passwordSaved);
							}
							
							if( !found ) {
								continue;
							}
						}
						if(filtroRicerca.getCredenzialiSoggetto().getSubject()!=null){
							try{
								if(credenziali.getSubject()==null){
									continue;
								}
								boolean subjectValid = CertificateUtils.sslVerify(credenziali.getSubject(), filtroRicerca.getCredenzialiSoggetto().getSubject(), PrincipalType.subject, this.log);
								boolean issuerValid = true;
								if(filtroRicerca.getCredenzialiSoggetto().getIssuer()!=null) {
									issuerValid = CertificateUtils.sslVerify(credenziali.getIssuer(), filtroRicerca.getCredenzialiSoggetto().getIssuer(), PrincipalType.issuer, this.log);
								}
								else {
									issuerValid = (credenziali.getIssuer() == null);
								}
								if(!subjectValid || !issuerValid){
									continue;
								}
							}catch(Exception e){
								throw new DriverRegistroServiziException(e.getMessage(),e);
							}
						}
						if(filtroRicerca.getCredenzialiSoggetto().getCnSubject()!=null && filtroRicerca.getCredenzialiSoggetto().getCertificate()!=null) {
							if(filtroRicerca.getCredenzialiSoggetto().getCnSubject().equals(credenziali.getCnSubject())==false) {
								continue;
							}
							// Possono esistere piu' soggetti che hanno un CN con subject e issuer diverso.
							Certificate certificato = ArchiveLoader.load(ArchiveType.CER, credenziali.getCertificate(), 0, null);
							Certificate certificatoFiltro = ArchiveLoader.load(ArchiveType.CER, filtroRicerca.getCredenzialiSoggetto().getCertificate(), 0, null);							
							if(!certificatoFiltro.getCertificate().equals(certificato.getCertificate(),filtroRicerca.getCredenzialiSoggetto().isCertificateStrictVerification())) {
								continue;
							}
						}
					}
				}
				IDSoggetto idS = new IDSoggetto(ss.getTipo(),ss.getNome());
				idSoggetti.add(idS);
			}
			if(idSoggetti.size()==0){
				throw new DriverRegistroServiziNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idSoggetti;
			}
		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdSoggettiRegistro error",e);
		}
	}
	
	
	
	
	
	
	/* Accordi di Servizio Parte Specifica */
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  identificato grazie ai fields Soggetto,
	 * 'Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idService</var> di tipo {@link org.openspcoop2.core.id.IDServizio}. 
	 *
	 * @param idServizio Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica} 
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		if(idServizio == null || idServizio.getSoggettoErogatore()==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametro Non Valido");
		
		// controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Inizializzazione dell'engine UDDI errata");
		}
		
		//	Controllo pre-esistenza del soggetto
		if( this.uddiLib.existsSoggetto(idServizio.getSoggettoErogatore()) == false){
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Il soggetto ["+idServizio.getSoggettoErogatore()
			+"] non risulta gia' inserito nel registro dei servizi.");
		} 
		// Controllo pre-esistenza del servizio
		if (this.uddiLib.existsServizio(idServizio)==false){
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Il servizio ["+idServizio.toString()+"] non risulta gia' registrato nel registro");
		}
		
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servRichiesto = null;
		// get URL XML Servizio
		String urlXMLServizio = this.uddiLib.getUrlXmlServizio(idServizio);
		if( urlXMLServizio == null ){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Definizione XML non disponibile");
		}
		
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLServizio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Servizio richiesto non esiste nel repository http: "+idServizio);
				} else
					throw e;
			}
			
			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Riscontrato errore durante la validazione XSD del servizio("+idServizio+"): "+e.getMessage(),e);
			}
			
			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
					servRichiesto.setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
				}
			}
			bin.close();
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Errore durante il parsing xml del servizio("+idServizio+"): "+e.getMessage(),e);
		}

		if(servRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Servizio non trovato.");
			
		return servRichiesto;
	}
	
	
	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  correlato identificato grazie ai fields Soggetto
	 * e nomeAccordo
	 * 
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordo ID dell'accordo che deve implementare il servizio correlato
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica} 
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(IDSoggetto idSoggetto, IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		if(idSoggetto == null || idAccordo==null || idAccordo.getNome()==null )
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametro Non Valido");
		
		//		 controllo inizializzazione UDDI
		if(this.uddiLib.create == false){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Inizializzazione dell'engine UDDI errata");
		}
		
		//	Controllo pre-esistenza del soggetto
		if( this.uddiLib.existsSoggetto(idSoggetto) == false){
			throw new DriverRegistroServiziNotFound("Il soggetto ["+idSoggetto
			+"] non risulta gia' inserito nel registro dei servizi.");
		} 
				
		String [] url =  this.uddiLib.getUrlXmlServizi(idSoggetto,idAccordo);
		if(url==null)
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Servizi correlati che rispettano il parametri di ricerca, non esistenti.");
		
		
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servCorrelatoRichiesto = null;
		for(int i=0; i<url.length; i++){
			
			//System.out.println("URL["+(i+1)+"]: "+url[i]);	
			ByteArrayInputStream bin = null;
			try{
				byte[] fileXML = null;
				try{
					fileXML = HttpUtilities.requestHTTPFile(url[i]);
				}catch(UtilsException e){
					// Controllo pre-esistenza dell'accordo
					if( "404".equals(e.getMessage()) ){
						throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Servizio richiesto non esiste nel repository http, accordo:"+idAccordo+ " e soggetto:"+idSoggetto);
					} else
						throw e;
				}
				
				/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
				try{
					this.validatoreRegistro.valida(url[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Riscontrato errore durante la validazione XSD del servizioUrl("+url[i]+"): "+e.getMessage(),e);
				}
				
				// parsing
				bin = new ByteArrayInputStream(fileXML);
				org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
				org.openspcoop2.core.registry.RegistroServizi rs = 
					(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
				if(rs.sizeSoggettoList()>0){
					if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
						servCorrelatoRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
						servCorrelatoRichiesto.setNomeSoggettoErogatore(idSoggetto.getNome());
						servCorrelatoRichiesto.setTipoSoggettoErogatore(idSoggetto.getTipo());
					}
				}
				bin.close();
				if(servCorrelatoRichiesto!=null)
					break;
			}catch(Exception e){
				try{
					if(bin!=null)
						bin.close();
				} catch(Exception eis) {}
				if(e instanceof DriverRegistroServiziNotFound)
					throw (DriverRegistroServiziNotFound)e;
				else
					throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Errore durante il parsing xml del servizioUrl("+url[i]+"): "+e.getMessage(),e);
			}
		}

		if(servCorrelatoRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Servizio non trovato.");
			
		return servCorrelatoRichiesto;
	}

	
	@Override
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		List<IDServizio> list = new ArrayList<IDServizio>();
		_fillAllIdServiziEngine("getAllIdServizi", filtroRicerca, list);
		return list;
		
	}
	
	@Override
	public List<IDFruizione> getAllIdFruizioniServizio(
			FiltroRicercaFruizioniServizio filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
	
		List<IDFruizione> list = new ArrayList<IDFruizione>();
		_fillAllIdServiziEngine("getAllIdFruizioniServizio", filtroRicerca, list);
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> void _fillAllIdServiziEngine(String nomeMetodo, 
			FiltroRicercaServizi filtroRicerca,
			List<T> listReturn) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
			
		try{
	
			FiltroRicercaFruizioniServizio filtroFruizioni = null;
			if(filtroRicerca instanceof FiltroRicercaFruizioniServizio){
				filtroFruizioni = (FiltroRicercaFruizioniServizio) filtroRicerca;
			}
			
			// Ricerca UDDI
			IDAccordo idAccordo = null;
			IDSoggetto soggettoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			if(filtroRicerca!=null){
				idAccordo = filtroRicerca.getIdAccordoServizioParteComune();
				if(filtroRicerca.getTipoSoggettoErogatore()!=null && filtroRicerca.getNomeSoggettoErogatore()!=null){
					soggettoErogatore = new IDSoggetto(filtroRicerca.getTipoSoggettoErogatore(),filtroRicerca.getNomeSoggettoErogatore());
				}
				tipoServizio = filtroRicerca.getTipo();
				nomeServizio = filtroRicerca.getNome();
			}
			String [] urlXMLServizi = this.uddiLib.getUrlXMLServiziBySearch(idAccordo, soggettoErogatore, tipoServizio, nomeServizio);
			
			
			
			// Esamina dei servizi
			for(int i=0; i<urlXMLServizi.length; i++){
			
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica serv = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdServizi] Riscontrato errore durante la validazione XSD URL("+urlXMLServizi[i]+"): "+e.getMessage(),e);
				}
				
				ByteArrayInputStream bin = null;
				try{
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLServizi[i]);
					bin = new ByteArrayInputStream(fileXML);
					org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
					if(rs.sizeSoggettoList()>0){
						if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0 ){
							org.openspcoop2.core.registry.Soggetto s = rs.getSoggetto(0);
							if(s.sizeAccordoServizioParteSpecificaList()>0){
								serv = s.getAccordoServizioParteSpecifica(0);
								serv.setNomeSoggettoErogatore(s.getNome());
								serv.setTipoSoggettoErogatore(s.getTipo());
							}else{
								continue;
							}
						}
					}
					bin.close();
				}catch(Exception e){
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdServizi] Errore durante il parsing xml del servizio("+urlXMLServizi[i]+"): "+e.getMessage(),e);
				}
				if(serv==null)
					throw new DriverRegistroServiziException("[getAllIdServizi] servizio non definito per la url ["+urlXMLServizi[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(serv.getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(serv.getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdServizi](FiltroByMinDate) ["+this.idServizioFactory.getUriFromAccordo(serv)+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdServizi](FiltroByMaxDate) Servizio["+this.idServizioFactory.getUriFromAccordo(serv)+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo, Nome e Versione
					if(filtroRicerca.getTipo()!=null){
						if(serv.getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(serv.getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(serv.getVersione().intValue() != filtroRicerca.getVersione().intValue()){
							continue;
						}
					}
					if(filtroRicerca.getPortType()!=null){
						if(serv.getPortType().equals(filtroRicerca.getPortType()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordoServizioParteComune());
						if(serv.getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(serv, filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					// Filtro By Tipo e/o Nome Soggetto Fruitore
					if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
						if(serv.sizeFruitoreList()<=0){
							continue;
						}
						boolean found = false;
						for (int k = 0; k < serv.sizeFruitoreList(); k++) {
							Fruitore fruitore = serv.getFruitore(k);
							if(filtroRicerca.getTipoSoggettoFruitore()!=null){
								if(fruitore.getTipo().equals(filtroRicerca.getTipoSoggettoFruitore()) == false){
									continue;
								}
							}
							if(filtroRicerca.getNomeSoggettoFruitore()!=null){
								if(fruitore.getNome().equals(filtroRicerca.getNomeSoggettoFruitore()) == false){
									continue;
								}
							}
							found = true;
							break;
						}
						if(!found){
							continue;
						}
					}
				}
				
				IDServizio idServ = this.idServizioFactory.getIDServizioFromAccordo(serv); 
				
				if(filtroFruizioni!=null){
					
					for (Fruitore fruitore : serv.getFruitoreList()) {
						
						// Tipo
						if(filtroFruizioni.getTipoSoggettoFruitore()!=null){
							if(fruitore.getTipo().equals(filtroFruizioni.getTipoSoggettoFruitore()) == false){
								continue;
							}
						}
						// Nome
						if(filtroFruizioni.getNomeSoggettoFruitore()!=null){
							if(fruitore.getNome().equals(filtroFruizioni.getNomeSoggettoFruitore()) == false){
								continue;
							}
						}
						// ProtocolProperties
						if(ProtocolPropertiesUtilities.isMatch(fruitore, filtroFruizioni.getProtocolPropertiesFruizione())==false){
							continue;
						}
						
						IDFruizione idFruizione = new IDFruizione();
						idFruizione.setIdServizio(idServ);
						idFruizione.setIdFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
						listReturn.add((T)idFruizione);
					}
					
				}
				else{
					listReturn.add((T)idServ);
				}
			}
			if(listReturn.size()<=0){
				String msgFiltro = "Elementi non trovati che rispettano il filtro di ricerca selezionato: ";
				if(filtroFruizioni!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroFruizioni.toString());
				}
				else if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound(msgFiltro+filtroRicerca.toString());
				}
				else
					throw new DriverRegistroServiziNotFound("Elementi non trovati");
			}

		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdServizi error",e);
		}
	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********  INTERFACCIA IDriverRegistroServiziCRUD ******** */ 
	
	/**
	 * Crea un nuovo AccordoCooperazione
	 * 
	 * @param accordoCooperazione
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createAccordoCooperazione(AccordoCooperazione accordoCooperazione) throws DriverRegistroServiziException{
		if( accordoCooperazione == null)
			throw new DriverRegistroServiziException("[createAccordoCooperazione] Parametro Non Valido");
		
		IDAccordoCooperazione idAccordo = null;
		try {

			idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione);

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( accordoCooperazione.getNome() == null ){
				throw new DriverRegistroServiziException("Accordo Cooperazione non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo
			if( this.uddiLib.existsAccordoCooperazione(idAccordo) == true){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordo
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createAccordoCooperazione(idAccordo,accordoCooperazione);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiRegistroServizi.URL_SEPARATOR + 
				this.generatoreXML.mappingIDAccordoCooperazioneToFileName(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione))  + ".xml";
			this.uddiLib.createAccordoCooperazione(idAccordo,urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createAccordoCooperazione] Errore generatosi durante la creazione di un nuovo accordo di cooperazione ["+idAccordo+"]: "+e.getMessage(),e);
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
    	if( idAccordo == null)
			return false;
    	if( idAccordo.getNome() == null)
			return false;
    	
    	try{
    		return this.uddiLib.existsAccordoCooperazione(idAccordo);
    	}catch(Exception e){
    		this.log.error("[existsAccordoCooperazione] Accordo non trovato: "+e.getMessage(),e);
    		return false;
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
		if( accordoCooperazione == null)
			throw new DriverRegistroServiziException("[updateAccordoCooperazione] Parametro Non Valido");
		
		IDAccordoCooperazione idAccordoOLD = null;
		if(accordoCooperazione.getOldIDAccordoForUpdate()!=null){
			idAccordoOLD = accordoCooperazione.getOldIDAccordoForUpdate();
		}else{
			idAccordoOLD = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione);
		}

		try {

			// Controllo  dell'accordo da Modificare
			if(idAccordoOLD==null){
				throw new DriverRegistroServiziException("Accordo Cooperazione da modificare non definito");
			}

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori dell'accordo modificato
			if( accordoCooperazione.getNome() == null){
				throw new DriverRegistroServiziException("Accordo Cooperazione modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo da modificare
			if( this.uddiLib.existsAccordoCooperazione(idAccordoOLD) == false){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordoOLD
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita dell'accordo (se da modificare)
			IDAccordoCooperazione idAccordoNEW = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione);
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				if( this.uddiLib.existsAccordoCooperazione(idAccordoNEW) == true){
					throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordoNEW
					+"] risulta gia' inserito nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createAccordoCooperazione(idAccordoOLD,accordoCooperazione);
			
			// Modifica UDDI
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiRegistroServizi.URL_SEPARATOR + 
					this.generatoreXML.mappingIDAccordoCooperazioneToFileName(idAccordoNEW)  + ".xml";
				this.uddiLib.updateAccordoCooperazione(idAccordoOLD,idAccordoNEW,urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateAccordoCooperazione] Errore generatosi durante la modifica dell'accordo di cooperazione ["+idAccordoOLD+"]: "+e);
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
		if( accordoCooperazione == null)
			throw new DriverRegistroServiziException("[deleteAccordoCooperazione] Parametro Non Valido");
				
		IDAccordoCooperazione idAccordo = null;
		try {
			idAccordo = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione);
			
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id dell'accordo da eliminare
			if(accordoCooperazione.getNome()==null){
				throw new DriverRegistroServiziException("Accordo Cooperazione da eliminare non definito");
			}

			// Controllo pre-esistenza dell'accordo da eliminare
			if( this.uddiLib.existsAccordoCooperazione(idAccordo) == false){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordo
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteAccordoCooperazione(idAccordo);
				
			// Delete from UDDI
			this.uddiLib.deleteAccordoCooperazione(idAccordo);
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoCooperazione] Errore generatosi durante l'eliminazione dell'accordo di cooperazione ["+this.idAccordoCooperazioneFactory.getUriFromAccordo(accordoCooperazione)+"]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Crea un nuovo AccordoServizio 
	 * 
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createAccordoServizioParteComune(AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException{
		if( accordoServizio == null)
			throw new DriverRegistroServiziException("[createAccordoServizioParteComune] Parametro Non Valido");
		
		IDAccordo idAccordo = null;
		try {

			idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (accordoServizio.getNome() == null) ||
				(accordoServizio.getProfiloCollaborazione() == null) ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo
			if( this.uddiLib.existsAccordoServizio(idAccordo) == true){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordo
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteComune(idAccordo,accordoServizio);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiRegistroServizi.URL_SEPARATOR + 
				this.generatoreXML.mappingIDAccordoToFileName(this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio))  + ".xml";
			this.uddiLib.createAccordoServizio(idAccordo,urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createAccordoServizioParteComune] Errore generatosi durante la creazione di un nuovo accordo di servizio ["+idAccordo+"]: "+e.getMessage(),e);
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
	public boolean existsAccordoServizioParteComune(IDAccordo idAccordo) throws DriverRegistroServiziException{
    	if( idAccordo == null)
			return false;
    	if( idAccordo.getNome() == null)
			return false;
    	
    	try{
    		return this.uddiLib.existsAccordoServizio(idAccordo);
    	}catch(Exception e){
    		this.log.error("[existsAccordoServizioParteComune] Accordo non trovato: "+e.getMessage(),e);
    		return false;
    	}
    }
	
	/**
	 * Aggiorna l'AccordoServizio con i nuovi valori.
	 *  
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateAccordoServizioParteComune(AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException{
		if( accordoServizio == null)
			throw new DriverRegistroServiziException("[updateAccordoServizioParteComune] Parametro Non Valido");
		
		IDAccordo idAccordoOLD = null;
		if(accordoServizio.getOldIDAccordoForUpdate()!=null){
			idAccordoOLD = accordoServizio.getOldIDAccordoForUpdate();
		}else{
			idAccordoOLD = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
		}

		try {

			// Controllo  dell'accordo da Modificare
			if(idAccordoOLD==null){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune da modificare non definito");
			}

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori dell'accordo modificato
			if( (accordoServizio.getNome() == null) ||
				(accordoServizio.getProfiloCollaborazione() == null) ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo da modificare
			if( this.uddiLib.existsAccordoServizio(idAccordoOLD) == false){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordoOLD
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita dell'accordo (se da modificare)
			IDAccordo idAccordoNEW = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				if( this.uddiLib.existsAccordoServizio(idAccordoNEW) == true){
					throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordoNEW
					+"] risulta gia' inserito nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createAccordoServizioParteComune(idAccordoOLD,accordoServizio);
			
			// Modifica UDDI
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiRegistroServizi.URL_SEPARATOR + 
					this.generatoreXML.mappingIDAccordoToFileName(idAccordoNEW)  + ".xml";
				this.uddiLib.updateAccordoServizio(idAccordoOLD,idAccordoNEW,urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateAccordoServizioParteComune] Errore generatosi durante la modifica dell'accordo di servizio ["+idAccordoOLD+"]: "+e);
		}
	}
	
	/**
	 * Elimina un AccordoServizio 
	 *  
	 * @param accordoServizio
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteAccordoServizioParteComune(AccordoServizioParteComune accordoServizio) throws DriverRegistroServiziException{
		if( accordoServizio == null)
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteComune] Parametro Non Valido");
				
		IDAccordo idAccordo = null;
		try {
			idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
			
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id dell'accordo da eliminare
			if(accordoServizio.getNome()==null){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune da eliminare non definito");
			}

			// Controllo pre-esistenza dell'accordo da eliminare
			if( this.uddiLib.existsAccordoServizio(idAccordo) == false){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordo
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteAccordoServizioParteComune(idAccordo);
				
			// Delete from UDDI
			this.uddiLib.deleteAccordoServizio(idAccordo);
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteComune] Errore generatosi durante l'eliminazione dell'accordo di servizio ["+this.idAccordoFactory.getUriFromAccordo(accordoServizio)+"]: "+e.getMessage(),e);
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
		if( pdd == null)
			throw new DriverRegistroServiziException("[createPortaDominio] Parametro Non Valido");
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (pdd.getNome() == null) ){
				throw new DriverRegistroServiziException("Porta di dominio non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza della porta di dominio
			if( this.uddiLib.existsPortaDominio(pdd.getNome()) == true){
				throw new DriverRegistroServiziException("La porta di dominio ["+pdd.getNome()
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createPortaDominio(pdd.getNome(),pdd);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + CostantiRegistroServizi.URL_SEPARATOR + pdd.getNome()  + ".xml";
			this.uddiLib.createPortaDominio(pdd.getNome(),urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createPortaDominio] Errore generatosi durante la creazione di una nuova porta di dominio ["+pdd.getNome()+"]: "+e.getMessage(),e);
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
    	if( nome == null)
			return false;
    	
    	try{
    		return this.uddiLib.existsPortaDominio(nome);
    	}catch(Exception e){
    		this.log.error("[existsPortaDominio] Porta di dominio non trovata: "+e.getMessage(),e);
    		return false;
    	}
    }
	
	/**
	 * Aggiorna la Porta di Dominio con i nuovi valori.
	 *  
	 * @param pdd
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if( pdd == null)
			throw new DriverRegistroServiziException("[updatePortaDominio] Parametro Non Valido");
		
		String pddOLD = null;
		//pddOLD = pdd.getOldNomeForUpdate();
		if(pddOLD==null)
			pddOLD = pdd.getNome();
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo  della porta di dominio da Modificare
			if(pddOLD==null){
				throw new DriverRegistroServiziException("Porta di dominio da modificare non definito");
			}

			// Controllo elementi obbligatori della pdd modificato
			if( (pdd.getNome() == null) ){
				throw new DriverRegistroServiziException("Porta di dominio modificata non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza della pdd da modificare
			if( this.uddiLib.existsPortaDominio(pddOLD) == false){
				throw new DriverRegistroServiziException("La porta di dominio ["+pddOLD
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita della pdd (se da modificare)
			String pddNEW = pdd.getNome();
			if(pddOLD.equals(pddNEW) == false){
				if( this.uddiLib.existsPortaDominio(pddNEW) == true){
					throw new DriverRegistroServiziException("La porta di dominio ["+pddNEW
					+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createPortaDominio(pddOLD,pdd);
			
			// Modifica UDDI
			if(pddOLD.equals(pddNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + CostantiRegistroServizi.URL_SEPARATOR + pddNEW  + ".xml";
				this.uddiLib.updatePortaDominio(pddOLD,pddNEW,urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updatePortaDominio] Errore generatosi durante la modifica della porta di dominio ["+pddOLD+"]: "+e);
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
		if( pdd == null)
			throw new DriverRegistroServiziException("[deletePortaDominio] Parametro Non Valido");
				
		try {
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id dell'accordo da eliminare
			if(pdd.getNome()==null){
				throw new DriverRegistroServiziException("Porta di dominio da eliminare non definito");
			}

			// Controllo pre-esistenza della pdd da eliminare
			if( this.uddiLib.existsPortaDominio(pdd.getNome()) == false){
				throw new DriverRegistroServiziException("La porta di dominio ["+pdd.getNome()
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deletePortaDominio(pdd.getNome());
				
			// Delete from UDDI
			this.uddiLib.deletePortaDominio(pdd.getNome());
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deletePortaDominio] Errore generatosi durante l'eliminazione della porta di dominio ["+pdd.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Crea una nuovo Gruppo
	 * 
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if( gruppo == null)
			throw new DriverRegistroServiziException("[createGruppo] Parametro Non Valido");
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (gruppo.getNome() == null) ){
				throw new DriverRegistroServiziException("Gruppo non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del gruppo
			if( this.uddiLib.existsGruppo(gruppo.getNome()) == true){
				throw new DriverRegistroServiziException("Il gruppo ["+gruppo.getNome()
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createGruppo(gruppo.getNome(),gruppo);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.GRUPPI + CostantiRegistroServizi.URL_SEPARATOR + gruppo.getNome()  + ".xml";
			this.uddiLib.createGruppo(gruppo.getNome(),urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createGruppo] Errore generatosi durante la creazione di un nuovo gruppo ["+gruppo.getNome()+"]: "+e.getMessage(),e);
		}
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
    	if( idGruppo == null || idGruppo.getNome()==null)
			return false;
    	
    	try{
    		return this.uddiLib.existsGruppo(idGruppo.getNome());
    	}catch(Exception e){
    		this.log.error("[existsGruppo] Gruppo non trovato: "+e.getMessage(),e);
    		return false;
    	}
    }
	
	/**
	 * Aggiorna il Gruppo con i nuovi valori.
	 *  
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if( gruppo == null)
			throw new DriverRegistroServiziException("[updateGruppo] Parametro Non Valido");
		
		IDGruppo idGruppoOLD = null;
		//idGruppoOLD = gruppo.getOldIDGruppoForUpdate();
		//if(idGruppoOLD==null)
		idGruppoOLD = new IDGruppo(gruppo.getNome());
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo  del gruppo da Modificare
			if(idGruppoOLD==null || idGruppoOLD.getNome()==null){
				throw new DriverRegistroServiziException("Gruppo da modificare non definito");
			}

			// Controllo elementi obbligatori del gruppo modificato
			if( (gruppo.getNome() == null) ){
				throw new DriverRegistroServiziException("Gruppo modificato non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del gruppo da modificare
			if( this.uddiLib.existsGruppo(idGruppoOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Gruppo ["+idGruppoOLD
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del gruppo (se da modificare)
			IDGruppo idGruppoNEW = new IDGruppo(gruppo.getNome());
			if(idGruppoOLD.equals(idGruppoNEW) == false){
				if( this.uddiLib.existsGruppo(idGruppoNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Gruppo ["+idGruppoNEW
					+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createGruppo(idGruppoOLD.getNome(),gruppo);
			
			// Modifica UDDI
			if(idGruppoOLD.equals(idGruppoNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.GRUPPI + CostantiRegistroServizi.URL_SEPARATOR + idGruppoNEW  + ".xml";
				this.uddiLib.updateGruppo(idGruppoOLD.getNome(),idGruppoNEW.getNome(),urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateGruppo] Errore generatosi durante la modifica del gruppo ["+idGruppoOLD+"]: "+e);
		}
	}
	
	/**
	 * Elimina un Gruppo
	 *  
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if( gruppo == null)
			throw new DriverRegistroServiziException("[deleteGruppo] Parametro Non Valido");
				
		try {
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id del gruppo
			if(gruppo.getNome()==null){
				throw new DriverRegistroServiziException("Gruppo da eliminare non definito");
			}

			// Controllo pre-esistenza del gruppo da eliminare
			if( this.uddiLib.existsGruppo(gruppo.getNome()) == false){
				throw new DriverRegistroServiziException("Il Gruppo ["+gruppo.getNome()
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteGruppo(gruppo.getNome());
				
			// Delete from UDDI
			this.uddiLib.deleteGruppo(gruppo.getNome());
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteGruppo] Errore generatosi durante l'eliminazione del gruppo ["+gruppo.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Crea una nuovo Ruolo
	 * 
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if( ruolo == null)
			throw new DriverRegistroServiziException("[createRuolo] Parametro Non Valido");
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (ruolo.getNome() == null) ){
				throw new DriverRegistroServiziException("Ruolo non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del ruolo
			if( this.uddiLib.existsRuolo(ruolo.getNome()) == true){
				throw new DriverRegistroServiziException("Il ruolo ["+ruolo.getNome()
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createRuolo(ruolo.getNome(),ruolo);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR + ruolo.getNome()  + ".xml";
			this.uddiLib.createRuolo(ruolo.getNome(),urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createRuolo] Errore generatosi durante la creazione di un nuovo ruolo ["+ruolo.getNome()+"]: "+e.getMessage(),e);
		}
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
    	if( idRuolo == null || idRuolo.getNome()==null)
			return false;
    	
    	try{
    		return this.uddiLib.existsRuolo(idRuolo.getNome());
    	}catch(Exception e){
    		this.log.error("[existsRuolo] Ruolo non trovato: "+e.getMessage(),e);
    		return false;
    	}
    }
	
	/**
	 * Aggiorna il Ruolo con i nuovi valori.
	 *  
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if( ruolo == null)
			throw new DriverRegistroServiziException("[updateRuolo] Parametro Non Valido");
		
		IDRuolo idRuoloOLD = null;
		//idRuoloOLD = ruolo.getOldIDRuoloForUpdate();
		//if(idRuoloOLD==null)
		idRuoloOLD = new IDRuolo(ruolo.getNome());
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo  del ruolo da Modificare
			if(idRuoloOLD==null || idRuoloOLD.getNome()==null){
				throw new DriverRegistroServiziException("Ruolo da modificare non definito");
			}

			// Controllo elementi obbligatori del ruolo modificato
			if( (ruolo.getNome() == null) ){
				throw new DriverRegistroServiziException("Ruolo modificato non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del ruolo da modificare
			if( this.uddiLib.existsRuolo(idRuoloOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Ruolo ["+idRuoloOLD
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del ruolo (se da modificare)
			IDRuolo idRuoloNEW = new IDRuolo(ruolo.getNome());
			if(idRuoloOLD.equals(idRuoloNEW) == false){
				if( this.uddiLib.existsRuolo(idRuoloNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Ruolo ["+idRuoloNEW
					+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createRuolo(idRuoloOLD.getNome(),ruolo);
			
			// Modifica UDDI
			if(idRuoloOLD.equals(idRuoloNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR + idRuoloNEW  + ".xml";
				this.uddiLib.updateRuolo(idRuoloOLD.getNome(),idRuoloNEW.getNome(),urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateRuolo] Errore generatosi durante la modifica del ruolo ["+idRuoloOLD+"]: "+e);
		}
	}
	
	/**
	 * Elimina un Ruolo
	 *  
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if( ruolo == null)
			throw new DriverRegistroServiziException("[deleteRuolo] Parametro Non Valido");
				
		try {
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id del ruolo
			if(ruolo.getNome()==null){
				throw new DriverRegistroServiziException("Ruolo da eliminare non definito");
			}

			// Controllo pre-esistenza del ruolo da eliminare
			if( this.uddiLib.existsRuolo(ruolo.getNome()) == false){
				throw new DriverRegistroServiziException("Il Ruolo ["+ruolo.getNome()
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteRuolo(ruolo.getNome());
				
			// Delete from UDDI
			this.uddiLib.deleteRuolo(ruolo.getNome());
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteRuolo] Errore generatosi durante l'eliminazione del ruolo ["+ruolo.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Crea una nuovo Scope
	 * 
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createScope(Scope scope) throws DriverRegistroServiziException{
		if( scope == null)
			throw new DriverRegistroServiziException("[createScope] Parametro Non Valido");
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (scope.getNome() == null) ){
				throw new DriverRegistroServiziException("Scope non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del scope
			if( this.uddiLib.existsScope(scope.getNome()) == true){
				throw new DriverRegistroServiziException("Il scope ["+scope.getNome()
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createScope(scope.getNome(),scope);
				
			// Registrazione nel registro UDDI.
			String urlXML = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR + scope.getNome()  + ".xml";
			this.uddiLib.createScope(scope.getNome(),urlXML);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createScope] Errore generatosi durante la creazione di un nuovo scope ["+scope.getNome()+"]: "+e.getMessage(),e);
		}
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
    	if( idScope == null || idScope.getNome()==null)
			return false;
    	
    	try{
    		return this.uddiLib.existsScope(idScope.getNome());
    	}catch(Exception e){
    		this.log.error("[existsScope] Scope non trovato: "+e.getMessage(),e);
    		return false;
    	}
    }
	
	/**
	 * Aggiorna il Scope con i nuovi valori.
	 *  
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateScope(Scope scope) throws DriverRegistroServiziException{
		if( scope == null)
			throw new DriverRegistroServiziException("[updateScope] Parametro Non Valido");
		
		IDScope idScopeOLD = null;
		//idScopeOLD = scope.getOldIDScopeForUpdate();
		//if(idScopeOLD==null)
		idScopeOLD = new IDScope(scope.getNome());
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo  del scope da Modificare
			if(idScopeOLD==null || idScopeOLD.getNome()==null){
				throw new DriverRegistroServiziException("Scope da modificare non definito");
			}

			// Controllo elementi obbligatori del scope modificato
			if( (scope.getNome() == null) ){
				throw new DriverRegistroServiziException("Scope modificato non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del scope da modificare
			if( this.uddiLib.existsScope(idScopeOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Scope ["+idScopeOLD
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del scope (se da modificare)
			IDScope idScopeNEW = new IDScope(scope.getNome());
			if(idScopeOLD.equals(idScopeNEW) == false){
				if( this.uddiLib.existsScope(idScopeNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Scope ["+idScopeNEW
					+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createScope(idScopeOLD.getNome(),scope);
			
			// Modifica UDDI
			if(idScopeOLD.equals(idScopeNEW) == false){
				String urlXML = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR + idScopeNEW  + ".xml";
				this.uddiLib.updateScope(idScopeOLD.getNome(),idScopeNEW.getNome(),urlXML);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateScope] Errore generatosi durante la modifica del scope ["+idScopeOLD+"]: "+e);
		}
	}
	
	/**
	 * Elimina un Scope
	 *  
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteScope(Scope scope) throws DriverRegistroServiziException{
		if( scope == null)
			throw new DriverRegistroServiziException("[deleteScope] Parametro Non Valido");
				
		try {
			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
		
			// Controllo id del scope
			if(scope.getNome()==null){
				throw new DriverRegistroServiziException("Scope da eliminare non definito");
			}

			// Controllo pre-esistenza del scope da eliminare
			if( this.uddiLib.existsScope(scope.getNome()) == false){
				throw new DriverRegistroServiziException("Il Scope ["+scope.getNome()
				+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteScope(scope.getNome());
				
			// Delete from UDDI
			this.uddiLib.deleteScope(scope.getNome());
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteScope] Errore generatosi durante l'eliminazione del scope ["+scope.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/**
	 * Crea un nuovo Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createSoggetto(Soggetto soggetto) throws DriverRegistroServiziException{
		if( soggetto == null)
			throw new DriverRegistroServiziException("[createSoggetto] Parametro Non Valido");
		
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
			
			// Controllo elementi obbligatori
			if( (soggetto.getNome() == null) || 
					(soggetto.getTipo() == null) ){
				throw new DriverRegistroServiziException("Soggetto non definito");
			}
			if(soggetto.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(soggetto.getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( soggetto.getConnettore().getTipo() == null ){
					connettoreNonDefinito = true;
				}
				if(connettoreNonDefinito){
					throw new DriverRegistroServiziException("Definizione del punto di accesso del dominio del soggetto non corretta");
				}
			}

			//	Definizione dati opzionali
			if( (soggetto.getIdentificativoPorta() == null) || ("".equals(soggetto.getIdentificativoPorta())) ){
				soggetto.setIdentificativoPorta(soggetto.getNome() + "SPCoopIT");
			}
			if( (soggetto.getDescrizione() == null) ||  ("".equals(soggetto.getDescrizione())) ){
				soggetto.setDescrizione("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome());
			}
			IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome(),soggetto.getIdentificativoPorta());
			if( (soggetto.getCodiceIpa() == null) || ("".equals(soggetto.getCodiceIpa())) ){
				// TODO GESTIRE TRAMITE FACTORY?				
				//soggetto.setCodiceIpa(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica(idSoggetto,false));
			}
			
			
			// Controllo pre-esistenza del soggetto
			String idSoggetto_string = soggetto.getTipo() + soggetto.getNome();
			if(this.uddiLib.existsSoggetto(idSoggetto) == true){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
				+"] risulta gia' inserito nel registro dei servizi.");
			} 

			
			// Generazione XML
			String urlXML = this.urlPrefix + idSoggetto_string + CostantiRegistroServizi.URL_SEPARATOR  + idSoggetto_string + ".xml";
			this.generatoreXML.createSoggetto(idSoggetto,soggetto);
				
			// Registrazione nel registro UDDI.
			this.uddiLib.createSoggetto(idSoggetto,	soggetto.getDescrizione(),urlXML);
				
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createSoggetto] Errore generatosi durante la creazione del nuovo Soggetto ["+soggetto.getTipo()+soggetto.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	/**
     * Verifica l'esistenza di un soggetto registrato.
     *
     * @param idSoggetto Identificativo del soggetto
     * @return true se il soggetto esiste, false altrimenti
     */    
    @Override
	public boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverRegistroServiziException{
    	if( idSoggetto == null)
			return false;
    	try{
    		return this.uddiLib.existsSoggetto(idSoggetto);
    	}catch(Exception e)	{
    		return false;
    	}
    }
	
	/**
	 * Aggiorna un Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateSoggetto(Soggetto soggetto) throws DriverRegistroServiziException{
		if( soggetto == null)
			throw new DriverRegistroServiziException("[updateSoggetto] Parametro Non Valido");
		
		String tipoOLD = null;
		String nomeOLD = null;
		if(soggetto.getOldIDSoggettoForUpdate()!=null){
			tipoOLD = soggetto.getOldIDSoggettoForUpdate().getTipo();
			nomeOLD = soggetto.getOldIDSoggettoForUpdate().getNome();
		}
		if(tipoOLD==null||nomeOLD==null){
			tipoOLD = soggetto.getTipo();
			nomeOLD = soggetto.getNome();
		}
		String idSoggettoOLD_string = tipoOLD + nomeOLD;
		IDSoggetto idSoggettoOLD = new IDSoggetto(tipoOLD,nomeOLD);
			
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
			
			// Controllo id del soggetto da Modificare
			if(tipoOLD==null || nomeOLD==null){
				throw new DriverRegistroServiziException("Soggetto da modificare non definito");
			}

			// Controllo elementi obbligatori del soggetto modificato
			if( (soggetto.getNome() == null) || 
					(soggetto.getTipo() == null) ){
				throw new DriverRegistroServiziException("Soggetto modificato non definito");
			}
			if(soggetto.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(soggetto.getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( soggetto.getConnettore().getTipo() == null ){
					connettoreNonDefinito = true;
				}
				if(connettoreNonDefinito){
					throw new DriverRegistroServiziException("Definizione del punto di accesso del dominio del soggetto non corretta");
				}
			}
			
			// Definizione dati opzionali 
			if( (soggetto.getIdentificativoPorta() == null) || ("".equals(soggetto.getIdentificativoPorta())) ){
				soggetto.setIdentificativoPorta(soggetto.getNome() + "SPCoopIT");
			}
			if( (soggetto.getDescrizione() == null) ||  ("".equals(soggetto.getDescrizione())) ){
				soggetto.setDescrizione("Soggetto "+soggetto.getTipo()+"/"+soggetto.getNome());
			}
			IDSoggetto idSoggettoNEW = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			if( (soggetto.getCodiceIpa() == null) || ("".equals(soggetto.getCodiceIpa())) ){
				// TODO GESTIRE TRAMITE FACTORY?				
				//soggetto.setCodiceIpa(SICAtoOpenSPCoopUtilities.buildIDSoggettoSica(idSoggettoNEW,false));
			}

			// Controllo pre-esistenza del soggetto da modificare
			if( this.uddiLib.existsSoggetto(idSoggettoOLD) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoOLD_string
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del soggetto (se da modificare)
			String idSoggettoNEW_string = soggetto.getTipo() + soggetto.getNome();
			if(idSoggettoOLD_string.equals(idSoggettoNEW_string) == false){
				if( this.uddiLib.existsSoggetto(idSoggettoNEW) == true){
					throw new DriverRegistroServiziException("La nuova identita da associare al soggetto ["+idSoggettoNEW_string
					+"] risulta gia' utilizzata nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			String urlXML = this.urlPrefix + idSoggettoNEW_string + CostantiRegistroServizi.URL_SEPARATOR + idSoggettoNEW_string + ".xml";
			this.generatoreXML.createSoggetto(idSoggettoOLD,soggetto);
			
			// Modifica UDDI
			
			// Refresh dominio
			this.uddiLib.updateIdentificativoPortaSoggetto(idSoggettoOLD,soggetto.getIdentificativoPorta());
			
			// Refresh descrizione
			this.uddiLib.updateDescrizioneSoggetto(idSoggettoOLD,soggetto.getDescrizione());
			
			if(idSoggettoOLD_string.equals(idSoggettoNEW_string) == false){
				// Modifica url che punta al file XML
				this.uddiLib.updateUrlXmlSoggetto(idSoggettoOLD,urlXML);

				// Modifica nome BusinessService (Per ultima per permettere il Rollback!!!!!)
				this.uddiLib.updateIdSoggetto(idSoggettoOLD,idSoggettoNEW);
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateSoggetto] Errore generatosi durante la modifica del Soggetto ["+idSoggettoOLD+"]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Cancella un Soggetto
	 * 
	 * @param soggetto
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteSoggetto(Soggetto soggetto) throws DriverRegistroServiziException{
		if( soggetto == null)
			throw new DriverRegistroServiziException("[deleteSoggetto] Parametro Non Valido");

		try {
			//	controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
			
			// Controllo id del soggetto da eliminare
			if(soggetto.getTipo()==null || soggetto.getNome()==null){
				throw new DriverRegistroServiziException("Soggetto da eliminare non definito");
			}

			// Controllo pre-esistenza del soggetto da modificare
			IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome(),soggetto.getIdentificativoPorta());
			if( this.uddiLib.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteSoggetto(idSoggetto);
				
			// Delete from UDDI
			this.uddiLib.deleteSoggetto(idSoggetto);
			
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteSoggetto] Errore generatosi durante l'eliminazione del Soggetto ["+soggetto.getTipo()+soggetto.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/**
	 * Crea un Accordo Servizio Parte Specifica
	 * 
	 * @param asps
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps) throws DriverRegistroServiziException{
	
		if( asps == null)
			throw new DriverRegistroServiziException("[createAccordoServizioParteSpecifica] Parametro Non Valido");
		
		
	
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (asps.getNomeSoggettoErogatore() == null) || 
					(asps.getTipoSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			if( (asps.getNome() == null) || 
					(asps.getTipo() == null)  || 
					(asps.getVersione() == null)  ){
				throw new DriverRegistroServiziException("Servizio, non definito");
			}
			if(asps.getAccordoServizioParteComune()==null){
				throw new DriverRegistroServiziException("Accordo di Servizio, da associare al servizio, non definito");
			}
			if(asps.getConfigurazioneServizio()!=null && asps.getConfigurazioneServizio().getConnettore() !=null && 
					!CostantiRegistroServizi.DISABILITATO.equals(asps.getConfigurazioneServizio().getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( asps.getConfigurazioneServizio().getConnettore().getTipo() == null ){
					connettoreNonDefinito = true;
				}
				if(connettoreNonDefinito){
					throw new DriverRegistroServiziException("Definizione del punto di accesso del dominio del servizio non corretta");
				}
			}
			for(int i=0;i<asps.sizeFruitoreList();i++){
				Fruitore checkFr = asps.getFruitore(i);
				if( (checkFr.getNome()==null) || (checkFr.getTipo()==null)){
					throw new DriverRegistroServiziException("Definizione di un fruitore senza nome o tipo");
				}
				for(int j=0;j<checkFr.sizeConfigurazioneAzioneList();j++){
					ConfigurazioneServizioAzione checkAz = checkFr.getConfigurazioneAzione(j);
					if( (checkAz.sizeAzioneList()<=0) || (checkAz.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un azione (fruizione) senza nome o connettore");
					}
					// controllo connettore
					if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAz.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso delle azioni "+checkAz.getAzioneList()+" della fruizione di servizio non corretta");
						}
					}
				}
				// Controllo pre-esistenza del soggetto fruitore
				String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.uddiLib.existsSoggetto(idSoggettoFruitore) == false){
					throw new DriverRegistroServiziException("Il fruitore ["+idSoggettoFruitore_string
					+"] non risulta gia' inserito nel registro dei servizi.");
				} 
			}
			if(asps.getConfigurazioneServizio()!=null){
				for(int i=0;i<asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();i++){
					ConfigurazioneServizioAzione checkAz = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
					if( (checkAz.sizeAzioneList()<=0) || (checkAz.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un azione senza nome o connettore");
					}
					// controllo connettore
					if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAz.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso delle azioni "+checkAz.getAzioneList()+" del servizio non corretta");
						}
					}
				}
			}

			// Controllo pre-esistenza del soggetto erogatore
			IDSoggetto idSoggettoErogatore = new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			String idSoggettoErogatore_string = asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore();
			if( this.uddiLib.existsSoggetto(idSoggettoErogatore) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoErogatore_string
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del servizio
			String idServizio_string = asps.getTipo() + asps.getNome() + asps.getVersione();
			String uriServizio = this.idServizioFactory.getUriFromAccordo(asps);
			IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
			if (this.uddiLib.existsServizio(idServizio)==true){
				throw new DriverRegistroServiziException("Il servizio ["+uriServizio+"] risulta gia' registrato nel registro");
			}

			String urlXML = this.urlPrefix + idSoggettoErogatore_string + CostantiRegistroServizi.URL_SEPARATOR +CostantiXMLRepository.SERVIZI+ 
			    CostantiRegistroServizi.URL_SEPARATOR + idServizio_string  + ".xml";

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteSpecifica(idServizio,asps);
			
			// Registrazione nel registro UDDI.
			this.uddiLib.createServizio(idServizio,
					urlXML,this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
				
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createAccordoServizioParteSpecifica] Errore generatosi durante la creazione del nuovo Servizio ["+
					this.idServizioFactory.getUriFromAccordo(asps)+"]: "+e.getMessage(),e);
		}
	}		
	
	/**
     * Verifica l'esistenza di un servizio registrato.
     *
	 * @param idServizio Identificativo del servizio
     * @return true se il servizio esiste, false altrimenti
     */    
    @Override
	public boolean existsAccordoServizioParteSpecifica(IDServizio idServizio) throws DriverRegistroServiziException{
    	if( idServizio == null)
			return false;
	
		try{
			this.getAccordoServizioParteSpecifica(idServizio);
			
			return true;
		}catch(DriverRegistroServiziNotFound de){
			return false;
		}
		catch(Exception e){
			this.log.error("[existsAccordoServizioParteSpecifica] Servizio non trovato: "+e.getMessage(),e);
    		return false;
    	}
    }
	
    
	/**
	 * Aggiorna un Accordo Servizio Parte Specifica
	 * 
	 * @param asps
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void updateAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps) throws DriverRegistroServiziException{
		
		if( asps == null)
			throw new DriverRegistroServiziException("[updateAccordoServizioParteSpecifica] Parametro Non Valido");
		
	
		String tipoServizioOLD = null;
		String nomeServizioOLD = null;
		Integer versioneServizioOLD = null;
		if(asps.getOldIDServizioForUpdate()!=null){
			tipoServizioOLD = asps.getOldIDServizioForUpdate().getTipo();
			nomeServizioOLD = asps.getOldIDServizioForUpdate().getNome();
			versioneServizioOLD = asps.getOldIDServizioForUpdate().getVersione();
		}
		if(tipoServizioOLD==null||nomeServizioOLD==null||versioneServizioOLD==null){
			tipoServizioOLD = asps.getTipo();
			nomeServizioOLD = asps.getNome();
			versioneServizioOLD = asps.getVersione();
		}
		String tipoSoggettoOLD = null;
		String nomeSoggettoOLD = null;
		if(asps.getOldIDServizioForUpdate()!=null && asps.getOldIDServizioForUpdate().getSoggettoErogatore()!=null){
			tipoSoggettoOLD = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo();
			nomeSoggettoOLD = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
		}
		if(tipoSoggettoOLD==null||nomeSoggettoOLD==null){
			tipoSoggettoOLD = asps.getTipoSoggettoErogatore();
			nomeSoggettoOLD = asps.getNomeSoggettoErogatore();
		}
		String idSoggettoOLD_string = tipoSoggettoOLD+nomeSoggettoOLD;
		IDSoggetto idSoggettoOLD = new IDSoggetto(tipoSoggettoOLD,nomeSoggettoOLD);
		String idServizioOLD_string = tipoServizioOLD + nomeServizioOLD+versioneServizioOLD;
		IDServizio idServizioOLD = this.idServizioFactory.getIDServizioFromValues(tipoServizioOLD,nomeServizioOLD, idSoggettoOLD, versioneServizioOLD);
				
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}
			
			// Controllo elementi obbligatori
			// vecchio ID Soggetto
			if( (tipoSoggettoOLD == null) || 
					(nomeSoggettoOLD == null) ){
				throw new DriverRegistroServiziException("Soggetto da modificare, erogatore del servizio, non definito");
			}
			// nuovo ID Soggetto
			if( (asps.getTipoSoggettoErogatore() == null) || 
					(asps.getNomeSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			// vecchio ID Servizio
			if( (tipoServizioOLD == null) || 
					(nomeServizioOLD == null) ){
				throw new DriverRegistroServiziException("Servizio da modificare non definito");
			}
			// nuovo ID Servizio
			if( (asps.getNome() == null) || 
					(asps.getTipo() == null) || 
					(asps.getVersione() == null) ){
				throw new DriverRegistroServiziException("Dati del nuovo servizio, non definiti");
			}
			// accordo
			if(asps.getAccordoServizioParteComune()==null){
				throw new DriverRegistroServiziException("Accordo di Servizio, da associare al servizio, non definito");
			}
			// connettore
			if(asps.getConfigurazioneServizio()!=null && asps.getConfigurazioneServizio().getConnettore() !=null && 
					!CostantiRegistroServizi.DISABILITATO.equals(asps.getConfigurazioneServizio().getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( asps.getConfigurazioneServizio().getConnettore().getTipo() == null ){
					connettoreNonDefinito = true;
				}
				if(connettoreNonDefinito){
					throw new DriverRegistroServiziException("Definizione del punto di accesso del dominio del servizio non corretta");
				}
			}
			// fruitori
			for(int i=0;i<asps.sizeFruitoreList();i++){
				Fruitore checkFr = asps.getFruitore(i);
				if( (checkFr.getNome()==null) || (checkFr.getTipo()==null)){
					throw new DriverRegistroServiziException("Definizione di un fruitore senza nome o tipo");
				}
				for(int j=0;j<checkFr.sizeConfigurazioneAzioneList();j++){
					ConfigurazioneServizioAzione checkAz = checkFr.getConfigurazioneAzione(j);
					if( (checkAz.sizeAzioneList()<=0) || (checkAz.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un fruitore senza nome o connettore");
					}
					// controllo connettore
					if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAz.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getAzioneList()+" della fruizione di servizio non corretta");
						}
					}
				}
				// Controllo pre-esistenza del soggetto fruitore
				/*String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.uddiLib.existsSoggetto(idSoggettoFruitore) == false){
					throw new DriverRegistroServiziException("Il fruitore ["+idSoggettoFruitore_string
					+"] non risulta gia' inserito nel registro dei servizi.");
				} */
			}
			// azioni-fruitori-connettori
			if(asps.getConfigurazioneServizio()!=null){
				for(int i=0;i<asps.getConfigurazioneServizio().sizeConfigurazioneAzioneList();i++){
					ConfigurazioneServizioAzione checkAz = asps.getConfigurazioneServizio().getConfigurazioneAzione(i);
					if( (checkAz.sizeAzioneList()<=0) || (checkAz.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un fruitore senza nome o connettore");
					}
					// controllo connettore
					if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAz.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getAzioneList()+" del servizio non corretta");
						}
					}
				}
			}
			
		

			// Controllo pre-esistenza del soggetto erogatore da modificare
			if( this.uddiLib.existsSoggetto(idSoggettoOLD) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoOLD_string
				+"] da modificare, non risulta gia' inserito nel registro dei servizi.");
			} 

	
			
			// Controllo pre-esistenza del nuovo soggetto erogatore
			String idSoggettoNEW_string = asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore();
			IDSoggetto idSoggettoNEW = new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			if( idSoggettoOLD_string.equals(idSoggettoNEW_string) == false ){
				if( this.uddiLib.existsSoggetto(idSoggettoNEW) == false){
					throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoNEW_string
					+"] a cui re-assegnare il servizio, non risulta gia' inserito nel registro dei servizi.");
				} 
			}
			
				
			
			// Controllo pre-esistenza del servizio da modificare
			if (this.uddiLib.existsServizio(idServizioOLD)==false){
				throw new DriverRegistroServiziException("Il servizio ["+idServizioOLD_string+"] erogato dal soggetto ["+idSoggettoOLD_string+"] non risulta gia' registrato nel registro");
			}
	
			// check esistenza nuovo servizio
			IDServizio idServizioNEW = this.idServizioFactory.getIDServizioFromValues(asps.getTipo(),asps.getNome(), idSoggettoNEW, asps.getVersione());
			String idServizioNEW_string = asps.getTipo() + asps.getNome() + asps.getVersione();
			if(idServizioOLD_string.equals(idServizioNEW_string) == false){
				// Controllo non esistenza del servizio da creare
				if (this.uddiLib.existsServizio(idServizioNEW)==true){
					throw new DriverRegistroServiziException("Il servizio ["+idServizioNEW_string+"] erogato dal soggetto ["+idSoggettoNEW_string+"] risulta gia' registrato nel registro");
				}
			}
				
			String urlXML = this.urlPrefix + idSoggettoNEW_string + CostantiRegistroServizi.URL_SEPARATOR +CostantiXMLRepository.SERVIZI+ 
			    CostantiRegistroServizi.URL_SEPARATOR + idServizioNEW_string  + ".xml";
		
			if( idSoggettoOLD_string.equals(idSoggettoNEW_string) == true ){
				// NON e' CAMBIATO IL SOGGETTO EROGATORE.
				
				// Ri-Generazione XML
				this.generatoreXML.createAccordoServizioParteSpecifica(idServizioOLD,asps);
					
				// Modifica UDDI
				// refresh AccordoServizio
				this.uddiLib.updateAccordoServizio(idServizioOLD,this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
					
				//if(idServizioOLD_string.equals(idServizioNEW_string) == false){
				// Lo faccio cmq per non incasinare la govwayConsole
				
				// refresh url XML
				this.uddiLib.updateUrlXmlServizio(idServizioOLD,urlXML);
					
				// Modifica nome BusinessService (Per ultima per permettere il Rollback!!!!!)
				this.uddiLib.updateIdServizio(idServizioOLD,idServizioNEW);
						
				//}
					
				
			}else{
				// E' CAMBIATO IL SOGGETTO EROGATORE
					
				// Delete from Repository
				this.generatoreXML.deleteAccordoServizioParteSpecifica(idServizioOLD);
				
				// Delete from UDDI
				this.uddiLib.deleteServizio(idServizioOLD);
				
				// Generazione XML
				this.generatoreXML.createAccordoServizioParteSpecifica(idServizioNEW,asps);
					
				// Registrazione nel registro UDDI.
				this.uddiLib.createServizio(idServizioNEW,
						urlXML,this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
			}

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateAccordoServizioParteSpecifica] Errore generatosi durante la modifica del Servizio ["+idServizioOLD_string+"] erogato dal soggetto ["+
			idSoggettoOLD_string+"]: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Cancella un Accordo Servizio Parte Specifica
	 * 
	 * @param asps
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps) throws DriverRegistroServiziException{
	
		if( asps == null)
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteSpecifica] Parametro Non Valido");
		
		
		String idSoggetto_string = asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore();
		String idServizio_string = asps.getTipo() + asps.getNome() + asps.getVersione();
				
		try {
			// Controllo id del soggetto 
			if(asps.getTipoSoggettoErogatore()==null || asps.getNomeSoggettoErogatore()==null){
				throw new DriverRegistroServiziException("Soggetto non definito");
			}
			// Controllo id del servizio da eliminare
			if(asps.getTipo()==null || asps.getNome()==null || asps.getVersione()==null){
				throw new DriverRegistroServiziException("Servizio da eliminare non definito");
			}
			// Controllo pre-esistenza del soggetto
			IDSoggetto idSoggetto = new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			if( this.uddiLib.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 
			// Controllo pre-esistenza del servizio
			IDServizio idServizio = this.idServizioFactory.getIDServizioFromValues(asps.getTipo(),asps.getNome(), idSoggetto, asps.getVersione());
			if (this.uddiLib.existsServizio(idServizio)==false){
				throw new DriverRegistroServiziException("Il servizio ["+idServizio_string+"] erogato dal soggetto ["+idSoggetto_string+"] non risulta gia' registrato nel registro");
			}

			// Delete from Repository
			this.generatoreXML.deleteAccordoServizioParteSpecifica(idServizio);

			// Delete from UDDI
			this.uddiLib.deleteServizio(idServizio);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteSpecifica] Errore generatosi durante l'eliminazione del Servizio ["+idServizio_string
			+"] erogato dal soggetto ["+idSoggetto_string+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	//RESET
	@Override
	public void reset() throws DriverRegistroServiziException{
		String inEliminazione = null;
		try{
			AccordoServizioParteComune[] accordiRegistrati = this.generatoreXML.getAccordiServizioParteComune();
			if(accordiRegistrati!=null){
				for(int i=0; i<accordiRegistrati.length;i++){
					IDAccordo idInEliminazione = this.idAccordoFactory.getIDAccordoFromAccordo(accordiRegistrati[i]);
					this.log.info("eliminazione accordo di servizio ["+idInEliminazione+"] in corso...");
					if(this.existsAccordoServizioParteComune(idInEliminazione)){
						this.deleteAccordoServizioParteComune(accordiRegistrati[i]);
					}else{
						this.generatoreXML.deleteAccordoServizioParteComune(idInEliminazione);
					}
					this.log.info("eliminazione accordo di servizio ["+idInEliminazione+"] effettuata.");
				}
			}
		}catch (Exception e) {
			this.log.info("Errore durante l'eliminazione di ["+inEliminazione+"]: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Errore durante la cancellazione degli accordi registrati ["+inEliminazione+"]: "+e.getMessage(),e);
		}
		try{
			AccordoCooperazione[] accordiCooperazioneRegistrati = this.generatoreXML.getAccordiCooperazione();
			if(accordiCooperazioneRegistrati!=null){
				for(int i=0; i<accordiCooperazioneRegistrati.length;i++){
					IDAccordoCooperazione idInEliminazione = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordiCooperazioneRegistrati[i]);
					this.log.info("eliminazione accordo di cooperazione ["+idInEliminazione+"] in corso...");
					if(this.existsAccordoCooperazione(idInEliminazione)){
						this.deleteAccordoCooperazione(accordiCooperazioneRegistrati[i]);
					}else{
						this.generatoreXML.deleteAccordoCooperazione(idInEliminazione);
					}
					this.log.info("eliminazione accordo di cooperazione ["+idInEliminazione+"] effettuata.");
				}
			}
		}catch (Exception e) {
			this.log.info("Errore durante l'eliminazione di ["+inEliminazione+"]: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Errore durante la cancellazione degli accordi registrati ["+inEliminazione+"]: "+e.getMessage(),e);
		}
		try{
			AccordoServizioParteSpecifica[] serviziRegistrati = this.generatoreXML.getAccordiServiziParteSpecifica();
			if(serviziRegistrati!=null){
				for(int i=0; i<serviziRegistrati.length;i++){
					inEliminazione=this.idServizioFactory.getUriFromAccordo(serviziRegistrati[i]);
					this.log.info("eliminazione servizio ["+inEliminazione+"] in corso...");
					IDServizio idS = this.idServizioFactory.getIDServizioFromAccordo(serviziRegistrati[i]);
					if(this.existsAccordoServizioParteSpecifica(idS)){
						this.deleteAccordoServizioParteSpecifica(serviziRegistrati[i]);
					}else{
						this.generatoreXML.deleteAccordoServizioParteSpecifica(idS);
					}
					this.log.info("eliminazione servizio ["+inEliminazione+"] effettuata.");
				}
			}
		}catch (Exception e) {
			this.log.info("Errore durante l'eliminazione di ["+inEliminazione+"]: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Errore durante la cancellazione dei servizi registrati ["+inEliminazione+"]: "+e.getMessage(),e);
		}
		try{
			Soggetto[] soggettiRegistrati = this.generatoreXML.getSoggetti();
			if(soggettiRegistrati!=null){
				for(int i=0; i<soggettiRegistrati.length;i++){
					inEliminazione=soggettiRegistrati[i].getTipo()+"/"+soggettiRegistrati[i].getNome();
					this.log.info("eliminazione soggetto ["+inEliminazione+"] in corso...");
					IDSoggetto idS = new IDSoggetto(soggettiRegistrati[i].getTipo(),soggettiRegistrati[i].getNome());
					if(this.existsSoggetto(idS)) {
						this.deleteSoggetto(soggettiRegistrati[i]);
					}else{
						this.generatoreXML.deleteSoggetto(idS);
					}
					this.log.info("eliminazione soggetto ["+inEliminazione+"] effettuata.");
				}
			}
		}catch (Exception e) {
			this.log.info("Errore durante l'eliminazione di ["+inEliminazione+"]: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Errore durante la cancellazione dei soggetti registrati ["+inEliminazione+"]: "+e.getMessage(),e);
		}
		try{
			PortaDominio[] pddRegistrate = this.generatoreXML.getPorteDominio();
			if(pddRegistrate!=null){
				for(int i=0; i<pddRegistrate.length;i++){
					inEliminazione = pddRegistrate[i].getNome();
					this.log.info("eliminazione porta di dominio ["+inEliminazione+"] in corso...");
					if(this.existsPortaDominio(inEliminazione)){
						this.deletePortaDominio(pddRegistrate[i]);
					}else{
						this.generatoreXML.deletePortaDominio(inEliminazione);
					}
					this.log.info("eliminazione porta di dominio ["+inEliminazione+"] effettuata.");
				}
			}
		}catch (Exception e) {
			this.log.info("Errore durante l'eliminazione di ["+inEliminazione+"]: "+e.getMessage(),e);
			throw new DriverRegistroServiziException("Errore durante la cancellazione delle porte di dominio registrate ["+inEliminazione+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		
		if(this.create==false)
			throw new CoreException("Driver non inizializzato");
		
		try{
			if(this.uddiLib.getTModel("isAliveTest",UDDILib.TMODEL_OPENSPCOOP)==null)
				throw new Exception("TModel ["+UDDILib.TMODEL_OPENSPCOOP+"] non trovata");
		}catch(Exception e){
			throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);
		}
	}

}
