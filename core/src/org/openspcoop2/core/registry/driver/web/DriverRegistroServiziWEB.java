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




package org.openspcoop2.core.registry.driver.web;

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
import org.openspcoop2.core.registry.constants.TipologiaServizio;
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
 * Classe utilizzata per effettuare query ad un registro WEB, riguardanti specifiche
 * proprieta' di servizi presenti all'interno del registro.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverRegistroServiziWEB extends BeanUtilities
implements IDriverRegistroServiziGet,IDriverRegistroServiziCRUD, IDriverWS,IMonitoraggioRisorsa{



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** --- URL Prefix utilizzato come prefisso 
     per il repository WEB XML ------*/
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
	 * Costruttore per interfaccia di query. 
	 *
	 * 
	 */
	public DriverRegistroServiziWEB(String urlPrefix,Logger alog){	
		this(urlPrefix,null,alog);
	}
	/**
	 * Costruttore per interfaccia CRUD. 
	 *
	 * 
	 */
	public DriverRegistroServiziWEB(String urlPrefix,String pathPrefix,Logger alog){	
		try{
			if(alog==null)
				this.log = LoggerWrapperFactory.getLogger(DriverRegistroServiziWEB.class);
			else
				this.log = alog;

			if (!urlPrefix.endsWith(CostantiRegistroServizi.URL_SEPARATOR))
				this.urlPrefix= urlPrefix + CostantiRegistroServizi.URL_SEPARATOR;
			else
				this.urlPrefix = urlPrefix;

			/* --- Costruzione Validatore XSD -- */
			try{
				this.validatoreRegistro = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverRegistroServiziWEB.class.getResourceAsStream("/registroServizi.xsd"));
			}catch (Exception e) {
				throw new Exception("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage(),e);
			}

			if(pathPrefix!=null)
				this.generatoreXML = new XMLLib(pathPrefix,urlPrefix);

			this.create = true;
		}catch(Exception e){
			this.log.error("Inizializzazione fallita: "+e.getMessage());
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
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro Non Valido");
		if( idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Nome accordo cooperazione non fornito");


		org.openspcoop2.core.registry.AccordoCooperazione accRichiesto = null;

		// Ottengo URL XML associato all'accordo
		String fileName = this.generatoreXML.mappingIDAccordoCooperazioneToFileName(idAccordo);
		String urlXMLAccordoCooperazione = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiRegistroServizi.URL_SEPARATOR + fileName + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordoCooperazione);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Accordo richiesto non esiste: "+fileName);
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLAccordoCooperazione);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoCooperazione] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeAccordoCooperazioneList()>0)
				accRichiesto = rs.getAccordoCooperazione(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getAccordoCooperazione] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(accRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Accordo di Cooperazione non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoCooperazione[] acList = this.generatoreXML.getAccordiCooperazione();
			if(acList==null)
				throw new DriverRegistroServiziNotFound("Accordi non esistenti nel repository WEB");

			// Esamina degli accordi
			List<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();
			for(int i=0; i<acList.length; i++){

				String fileName = this.generatoreXML.mappingUriToFileName_accordoCooperazione(this.idAccordoCooperazioneFactory.getUriFromAccordo(acList[i]));
				String acUrlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_COOPERAZIONE + CostantiRegistroServizi.URL_SEPARATOR+ fileName + ".xml";	
				String acURI = this.idAccordoCooperazioneFactory.getUriFromAccordo(acList[i]);


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(acUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiCooperazione] Riscontrato errore durante la validazione XSD ("+acUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(acList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMinDate) Accordo di servizio ["+acURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(acList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(acList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMaxDate) Accordo di servizio ["+acURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(acList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNomeAccordo()!=null){
						if(acList[i].getNome().equals(filtroRicerca.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(acList[i].getVersione().equals(filtroRicerca.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
						if(acList[i].getSoggettoReferente()==null)
							continue;
						if(filtroRicerca.getTipoSoggettoReferente()!=null){
							if(acList[i].getSoggettoReferente().getTipo().equals(filtroRicerca.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicerca.getNomeSoggettoReferente()!=null){
							if(acList[i].getSoggettoReferente().getNome().equals(filtroRicerca.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(acList[i], filtroRicerca.getProtocolPropertiesAccordo())==false){
						continue;
					}
				}
				idAccordi.add(this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(acList[i]));
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
			throw new DriverRegistroServiziException("[AccordoServizioParteComune] Parametro Non Valido");
		if( idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[AccordoServizioParteComune] Nome accordo servizio non fornito");


		org.openspcoop2.core.registry.AccordoServizioParteComune accRichiesto = null;

		// Ottengo URL XML associato all'accordo
		String fileName = this.generatoreXML.mappingIDAccordoToFileName(idAccordo);
		String urlXMLAccordoServizio = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiRegistroServizi.URL_SEPARATOR + fileName + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordoServizio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[AccordoServizioParteComune] Accordo richiesto non esiste: "+fileName);
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLAccordoServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[AccordoServizioParteComune] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeAccordoServizioParteComuneList()>0)
				accRichiesto = rs.getAccordoServizioParteComune(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[AccordoServizioParteComune] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(accRichiesto==null)
			throw new DriverRegistroServiziNotFound("[AccordoServizioParteComune] Accordo di Servizio non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("["+nomeMetodo+"] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoServizioParteComune[] asList = this.generatoreXML.getAccordiServizioParteComune(); 
			if(asList==null)
				throw new DriverRegistroServiziNotFound("Accordi non esistenti nel repository WEB");

			// Esamina degli accordi
			for(int i=0; i<asList.length; i++){

				String fileName = this.generatoreXML.mappingUriToFileName(this.idAccordoFactory.getUriFromAccordo(asList[i]));
				String asUrlXML = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiRegistroServizi.URL_SEPARATOR+ fileName + ".xml";	
				String asURI = this.idAccordoFactory.getUriFromAccordo(asList[i]);


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(asUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteComune] Riscontrato errore durante la validazione XSD ("+asUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicercaBase!=null){
					// Filtro By Data
					if(filtroRicercaBase.getMinDate()!=null){
						if(asList[i].getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMinDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(asList[i].getOraRegistrazione().before(filtroRicercaBase.getMinDate())){
							continue;
						}
					}
					if(filtroRicercaBase.getMaxDate()!=null){
						if(asList[i].getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMaxDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(asList[i].getOraRegistrazione().after(filtroRicercaBase.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicercaBase.getNomeAccordo()!=null){
						if(asList[i].getNome().equals(filtroRicercaBase.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getVersione()!=null){
						if(asList[i].getVersione().equals(filtroRicercaBase.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
						if(asList[i].getSoggettoReferente()==null)
							continue;
						if(filtroRicercaBase.getTipoSoggettoReferente()!=null){
							if(asList[i].getSoggettoReferente().getTipo().equals(filtroRicercaBase.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicercaBase.getNomeSoggettoReferente()!=null){
							if(asList[i].getSoggettoReferente().getNome().equals(filtroRicercaBase.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					if(filtroRicercaBase.getServiceBinding()!=null){
						if(asList[i].getServiceBinding().equals(filtroRicercaBase.getServiceBinding()) == false){
							continue;
						}
					}
					if(filtroRicercaBase.getIdGruppo()!=null && filtroRicercaBase.getIdGruppo().getNome()!=null){
						boolean found = false;
						if(asList[i].getGruppi()!=null && asList[i].getGruppi().sizeGruppoList()>0) {
							for (GruppoAccordo gruppo : asList[i].getGruppi().getGruppoList()) {
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
						if(asList[i].getServizioComposto()==null){
							continue;
						}
						IDAccordoCooperazione idAC = this.idAccordoCooperazioneFactory.getIDAccordoFromUri(asList[i].getServizioComposto().getAccordoCooperazione());
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
							if(asList[i].getServizioComposto()==null){
								continue;
							}
						}
						else {
							if(asList[i].getServizioComposto()!=null){
								continue;
							}
						}
					}
					
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(asList[i], filtroRicercaBase.getProtocolPropertiesAccordo())==false){
						continue;
					}
					
				}
				
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromValues(asList[i].getNome(),BeanUtilities.getSoggettoReferenteID(asList[i].getSoggettoReferente()),asList[i].getVersione());
				
				if(filtroPT!=null){
					for (PortType pt : asList[i].getPortTypeList()) {
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
					for (PortType pt : asList[i].getPortTypeList()) {
						
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
					for (Azione az : asList[i].getAzioneList()) {
						
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
					for (Resource resource : asList[i].getResourceList()) {
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
			if(listReturn.size()<=0){
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

		org.openspcoop2.core.registry.PortaDominio pddRichiesta = null;

		// Ottengo URL XML associata alla porta di dominio
		String urlXMLPortaDominio = this.urlPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + CostantiRegistroServizi.URL_SEPARATOR + nomePdD + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLPortaDominio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di dominio richiesta non esiste: "+nomePdD);
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLPortaDominio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getPortaDominio] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizePortaDominioList()>0)
				pddRichiesta = rs.getPortaDominio(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getPortaDominio] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(pddRichiesta==null)
			throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di Dominio non trovata.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdPorteDominio] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.PortaDominio[] pddList = this.generatoreXML.getPorteDominio();
			if(pddList==null)
				throw new DriverRegistroServiziNotFound("Porte di dominio non esistenti nel repository WEB");

			// Esamina delle pdd
			List<String> nomiPdd = new ArrayList<String>();
			for(int i=0; i<pddList.length; i++){

				String pdUrlXML = this.urlPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + CostantiRegistroServizi.URL_SEPARATOR 
				+ pddList[i].getNome() + ".xml";	  


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(pdUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdPorteDominio] Riscontrato errore durante la validazione XSD ("+pdUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(pddList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMinDate) Porta di Dominio ["+pddList[i].getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pddList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(pddList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMaxDate) Porta di Dominio ["+pddList[i].getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pddList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(pddList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
				}
				nomiPdd.add(pddList[i].getNome());
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

		org.openspcoop2.core.registry.Gruppo gruppoRichiesto = null;

		// Ottengo URL XML associata alla porta di dominio
		String urlXMLPortaDominio = this.urlPrefix + CostantiXMLRepository.GRUPPI + CostantiRegistroServizi.URL_SEPARATOR + idGruppo.getNome() + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLPortaDominio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo richiesto non esiste: "+idGruppo.getNome());
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLPortaDominio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getGruppo] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeGruppoList()>0)
				gruppoRichiesto = rs.getGruppo(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getGruppo] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(gruppoRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdGruppi] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.Gruppo[] gruppoList = this.generatoreXML.getGruppi();
			if(gruppoList==null)
				throw new DriverRegistroServiziNotFound("Gruppi non esistenti nel repository WEB");

			// Esamina dei gruppi
			List<IDGruppo> idGruppi = new ArrayList<IDGruppo>();
			for(int i=0; i<gruppoList.length; i++){

				String gruppoUrlXML = this.urlPrefix + CostantiXMLRepository.GRUPPI + CostantiRegistroServizi.URL_SEPARATOR 
						+ gruppoList[i].getNome() + ".xml";	  


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(gruppoUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdGruppi] Riscontrato errore durante la validazione XSD ("+gruppoUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(gruppoList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdGruppi](FiltroByMinDate) Gruppo ["+gruppoList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(gruppoList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(gruppoList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdGruppi](FiltroByMaxDate) Gruppo ["+gruppoList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(gruppoList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(gruppoList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By ServiceBinding
					if(filtroRicerca.getServiceBinding()!=null){
						if(gruppoList[i].getServiceBinding()!=null){ // se e' uguale a null significa che va bene per qualsiasi service binding
							if(gruppoList[i].getServiceBinding().equals(filtroRicerca.getServiceBinding()) == false) {
								continue;
							}
						}
					}
				}
				IDGruppo id = new IDGruppo(gruppoList[i].getNome());
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

		org.openspcoop2.core.registry.Ruolo ruoloRichiesto = null;

		// Ottengo URL XML associata alla porta di dominio
		String urlXMLPortaDominio = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR + idRuolo.getNome() + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLPortaDominio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo richiesto non esiste: "+idRuolo.getNome());
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLPortaDominio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getRuolo] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeRuoloList()>0)
				ruoloRichiesto = rs.getRuolo(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getRuolo] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(ruoloRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdRuoli] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.Ruolo[] ruoloList = this.generatoreXML.getRuoli();
			if(ruoloList==null)
				throw new DriverRegistroServiziNotFound("Ruoli non esistenti nel repository WEB");

			// Esamina dei ruoli
			List<IDRuolo> idRuoli = new ArrayList<IDRuolo>();
			for(int i=0; i<ruoloList.length; i++){

				String ruoloUrlXML = this.urlPrefix + CostantiXMLRepository.RUOLI + CostantiRegistroServizi.URL_SEPARATOR 
						+ ruoloList[i].getNome() + ".xml";	  


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(ruoloUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdRuoli] Riscontrato errore durante la validazione XSD ("+ruoloUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ruoloList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdRuoli](FiltroByMinDate) Ruolo ["+ruoloList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ruoloList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ruoloList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdRuoli](FiltroByMaxDate) Ruolo ["+ruoloList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ruoloList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(ruoloList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Tipologia
					if(filtroRicerca.getTipologia()!=null && !RuoloTipologia.QUALSIASI.equals(filtroRicerca.getTipologia())){
						if(ruoloList[i].getTipologia()==null){
							continue;
						}
						if(!RuoloTipologia.QUALSIASI.equals(ruoloList[i].getTipologia())){
							if(ruoloList[i].getTipologia().equals(filtroRicerca.getTipologia()) == false){
								continue;
							}
						}
					}
					// Filtro By Contesto
					if(filtroRicerca.getContesto()!=null && !RuoloContesto.QUALSIASI.equals(filtroRicerca.getContesto())){
						if(ruoloList[i].getContestoUtilizzo()==null){
							continue;
						}
						if(!RuoloContesto.QUALSIASI.equals(ruoloList[i].getContestoUtilizzo())){
							if(ruoloList[i].getContestoUtilizzo().equals(filtroRicerca.getContesto()) == false){
								continue;
							}
						}
					}
				}
				IDRuolo id = new IDRuolo(ruoloList[i].getNome());
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

		org.openspcoop2.core.registry.Scope scopeRichiesto = null;

		// Ottengo URL XML associata alla porta di dominio
		String urlXMLPortaDominio = this.urlPrefix + CostantiXMLRepository.SCOPE + CostantiRegistroServizi.URL_SEPARATOR + idScope.getNome() + ".xml";	  

		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLPortaDominio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getScope] Scope richiesto non esiste: "+idScope.getNome());
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLPortaDominio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getScope] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeScopeList()>0)
				scopeRichiesto = rs.getScope(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getScope] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(scopeRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getScope] Scope non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdScope] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.Scope[] scopeList = this.generatoreXML.getScope();
			if(scopeList==null)
				throw new DriverRegistroServiziNotFound("Scope non esistenti nel repository WEB");

			// Esamina dei scope
			List<IDScope> idScope = new ArrayList<IDScope>();
			for(int i=0; i<scopeList.length; i++){

				String scopeUrlXML = this.urlPrefix + CostantiXMLRepository.SCOPE + CostantiRegistroServizi.URL_SEPARATOR 
						+ scopeList[i].getNome() + ".xml";	  


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(scopeUrlXML);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdScope] Riscontrato errore durante la validazione XSD ("+scopeUrlXML+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(scopeList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdScope](FiltroByMinDate) Scope ["+scopeList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(scopeList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(scopeList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdScope](FiltroByMaxDate) Scope ["+scopeList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(scopeList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNome()!=null){
						if(scopeList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Tipologia
					if(filtroRicerca.getTipologia()!=null){
						if(scopeList[i].getTipologia()==null){
							continue;
						}
						if(scopeList[i].getTipologia().equals(filtroRicerca.getTipologia()) == false){
							continue;
						}
					}
					// Filtro By Contesto
					if(filtroRicerca.getContesto()!=null && !ScopeContesto.QUALSIASI.equals(filtroRicerca.getContesto())){
						if(scopeList[i].getContestoUtilizzo()==null){
							continue;
						}
						if(!ScopeContesto.QUALSIASI.equals(scopeList[i].getContestoUtilizzo())){
							if(scopeList[i].getContestoUtilizzo().equals(filtroRicerca.getContesto()) == false){
								continue;
							}
						}
					}
				}
				IDScope id = new IDScope(scopeList[i].getNome());
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
		String tipo = idSoggetto.getTipo();
		String nome = idSoggetto.getNome();
		if(tipo == null || nome == null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametri Non Validi");

		org.openspcoop2.core.registry.Soggetto soggRichiesto = null;

		// Ottengo URL XML associato al Soggetto
		String idSoggettoXML = tipo + nome;
		String urlXMLSoggetto = this.urlPrefix + idSoggettoXML +CostantiRegistroServizi.URL_SEPARATOR  + idSoggettoXML + ".xml";



		// Ottengo oggetto Soggetto
		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLSoggetto);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getSoggetto] Il soggetto ["+idSoggetto.getTipo()+"/"+idSoggetto.getNome()+
					"] non risulta gia' inserito nel registro dei servizi.");
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLSoggetto);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getSoggetto] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeSoggettoList()>0)
				soggRichiesto = rs.getSoggetto(0);
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getSoggetto] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(soggRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non trovato.");

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
			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.Soggetto[] ssList = this.generatoreXML.getSoggetti(); 
			if(ssList==null)
				throw new DriverRegistroServiziNotFound("Soggetti non esistenti nel repository WEB");

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
			for(int i=0; i<ssList.length; i++){

				String idSoggettoXML = ssList[i].getTipo() + ssList[i].getNome();
				String urlXMLSoggetto = this.urlPrefix + idSoggettoXML +CostantiRegistroServizi.URL_SEPARATOR  + idSoggettoXML + ".xml";

				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLSoggetto);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] Riscontrato errore durante la validazione XSD ("+urlXMLSoggetto+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ssList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggettiRegistro](FiltroByMinDate) Soggetto ["+ssList[i].getTipo()+"/"+ssList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ssList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ssList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggettiRegistro](FiltroByMaxDate) Soggetto ["+ssList[i].getTipo()+"/"+ssList[i].getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ssList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(ssList[i].getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(ssList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro By Pdd
					if(filtroRicerca.getNomePdd()!=null){
						if(ssList[i].getPortaDominio().equals(filtroRicerca.getNomePdd()) == false){
							continue;
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(ssList[i], filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					// Filtro By Ruoli
					if(filtroRicerca.getIdRuolo()!=null && filtroRicerca.getIdRuolo().getNome()!=null){
						if(ssList[i].getRuoli()==null){
							continue;
						}
						boolean contains = false;
						for (int j = 0; j < ssList[i].getRuoli().sizeRuoloList(); j++) {
							if(filtroRicerca.getIdRuolo().getNome().equals(ssList[i].getRuoli().getRuolo(j).getNome())){
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
						CredenzialiSoggetto credenziali = ssList[i].getCredenziali();
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
				IDSoggetto idS = new IDSoggetto(ssList[i].getTipo(),ssList[i].getNome());
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
	 * @param idService Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDServizio}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * 
	 */
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDServizio idService) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		if(idService == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		String uri = this.idServizioFactory.getUriFromIDServizio(idService);
		if(servizio == null || tipoServizio == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Soggetto) Non Validi");


		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servRichiesto = null;
		// get URL XML Servizio
		String idSoggettoXML = tipoSogg + nomeSogg;
		String idServizioXML = tipoServizio + servizio + versioneServizio;
		String urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
		CostantiRegistroServizi.URL_SEPARATOR + idServizioXML  + ".xml";

		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLServizio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getServizio] Il servizio ["+uri+"] non risulta gia' registrato nel registro");
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.setNomeSoggettoErogatore(idService.getSoggettoErogatore().getNome());
					servRichiesto.setTipoSoggettoErogatore(idService.getSoggettoErogatore().getTipo());
				}
			}
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getServizio] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(servRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getServizio] Servizio non trovato.");

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

		if(idSoggetto == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametro Non Valido");
		String nomeSoggetto = idSoggetto.getNome();
		String tipoSoggetto = idSoggetto.getTipo();
		if(nomeSoggetto == null || tipoSoggetto == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametri (Soggetto) Non Validi");
		if(idAccordo == null || idAccordo.getNome()==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametri (Accordo) Non Valido");

		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servRichiesto = null;

		// get URL XML Index
		String idSoggettoXML = tipoSoggetto + nomeSoggetto;
		String urlXMLIndexServizi = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
		CostantiRegistroServizi.URL_SEPARATOR + CostantiXMLRepository.INDEX_SERVIZI;

		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);

		IDServizio [] lista = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLIndexServizi);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Il soggetto ["+idSoggetto+"] non possiede un index dei servizi erogati");
				} else
					throw e;
			}
			lista = XMLLib.mappingIndexServizi(new String(fileXML));
			if(lista==null){
				throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Il soggetto ["+idSoggetto+"] non possiede una lista di servizi erogati");
			}

		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Errore durante la get Servizi List Index: "+e.getMessage(),e);
		}

		// leggo index file
		String urlXMLServizio = null;
		String tipoServizio = null;
		String nomeServizio = null;
		Integer versioneServizio = null;
		try{
			for(int i=0;i<lista.length;i++){
				if(uriAccordo.equals(lista[i].getUriAccordoServizioParteComune()) &&
						TipologiaServizio.CORRELATO.getValue().equals(lista[i].getTipologia().getValue()) &&
						idSoggetto.getTipo().equals(lista[i].getSoggettoErogatore().getTipo()) &&
						idSoggetto.getNome().equals(lista[i].getSoggettoErogatore().getNome())
				){
					urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
					CostantiRegistroServizi.URL_SEPARATOR + lista[i].getTipo()+lista[i].getNome()+lista[i].getVersione()+ ".xml";
					tipoServizio = lista[i].getTipo();
					nomeServizio = lista[i].getNome();
					versioneServizio = lista[i].getVersione();
					break;
				}
			}
			if(urlXMLServizio==null){
				throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Il soggetto ["+idSoggetto+"] non possiede un servizio correlato che implementa l'accordo con uri ["+uriAccordo+"]");
			}

		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(Exception e){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Errore durante la ricerca: "+e.getMessage(),e);	
		}

		ByteArrayInputStream bin = null;
		try{
			byte[] fileXML = null;
			try{
				fileXML = HttpUtilities.requestHTTPFile(urlXMLServizio);
			}catch(UtilsException e){
				// Controllo pre-esistenza dell'accordo
				if( "404".equals(e.getMessage()) ){
					throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Il servizio ["+tipoServizio+"/"+nomeServizio+":"+versioneServizio
							+"] erogato dal soggetto ["+idSoggetto+"] non risulta gia' registrato nel registro");
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// parsing
			bin = new ByteArrayInputStream(fileXML);
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) deserializer.readRegistroServizi(bin);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.setNomeSoggettoErogatore(idSoggetto.getNome());
					servRichiesto.setTipoSoggettoErogatore(idSoggetto.getTipo());
				}
			}
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
		}catch(Exception e){
			try{
				if(bin!=null)
					bin.close();
			} catch(Exception eis) {}
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("[getServizioCorrelatoByAccordo] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(servRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getServizioCorrelatoByAccordo] Servizio non trovato.");

		return servRichiesto;

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
			
			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("["+nomeMetodo+"] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoServizioParteSpecifica[] servList = this.generatoreXML.getAccordiServiziParteSpecifica();
			if(servList==null)
				throw new DriverRegistroServiziNotFound("Servizi non esistenti nel repository WEB");

			// Esamina dei servizi
			for(int i=0; i<servList.length; i++){

				String idSoggettoXML = servList[i].getTipoSoggettoErogatore() + servList[i].getNomeSoggettoErogatore();
				String idServizioXML = servList[i].getTipo() + servList[i].getNome() + servList[i].getVersione();
				String urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
				CostantiRegistroServizi.URL_SEPARATOR + idServizioXML  + ".xml";


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizio);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("["+nomeMetodo+"] Riscontrato errore durante la validazione XSD URL("+urlXMLServizio+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(servList[i].getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(servList[i].getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMinDate) Servizio["+this.idServizioFactory.getUriFromAccordo(servList[i])+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMaxDate) Servizio["+this.idServizioFactory.getUriFromAccordo(servList[i])+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo, Nome e Versione
					if(filtroRicerca.getTipo()!=null){
						if(servList[i].getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(servList[i].getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(servList[i].getVersione().intValue() != filtroRicerca.getVersione().intValue()){
							continue;
						}
					}
					if(filtroRicerca.getPortType()!=null){
						if(servList[i].getPortType().equals(filtroRicerca.getPortType()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordoServizioParteComune());
						if(servList[i].getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(servList[i], filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					// Filtro By Tipo e/o Nome Soggetto Fruitore
					if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
						if(servList[i].sizeFruitoreList()<=0){
							continue;
						}
						boolean found = false;
						for (int k = 0; k < servList[i].sizeFruitoreList(); k++) {
							Fruitore fruitore = servList[i].getFruitore(k);
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
				
				IDServizio idServ = this.idServizioFactory.getIDServizioFromAccordo(servList[i]);
				
				if(filtroFruizioni!=null){
					
					for (Fruitore fruitore : servList[i].getFruitoreList()) {
						
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
				throw new DriverRegistroServiziException("["+nomeMetodo+"] error",e);
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

			// Controllo elementi obbligatori
			if( accordoCooperazione.getNome() == null  ){
				throw new DriverRegistroServiziException("Accordo Cooperazione non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo
			if( this.generatoreXML.existsAccordoCooperazione(idAccordo) == true){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordo.toString()
						+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createAccordoCooperazione(idAccordo,accordoCooperazione);

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
			return this.generatoreXML.existsAccordoCooperazione(idAccordo);
		}catch(Exception e){
			this.log.error("[existsAccordoCooperazione] Accordo non trovato: "+e.getMessage());
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

			// Controllo elementi obbligatori dell'accordo modificato
			if( accordoCooperazione.getNome() == null ){
				throw new DriverRegistroServiziException("Accordo Cooperazione modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo da modificare
			if( this.generatoreXML.existsAccordoCooperazione(idAccordoOLD) == false){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordoOLD
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita dell'accordo (se da modificare)
			IDAccordoCooperazione idAccordoNEW = this.idAccordoCooperazioneFactory.getIDAccordoFromAccordo(accordoCooperazione);
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				if( this.generatoreXML.existsAccordoCooperazione(idAccordoNEW) == true){
					throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordoNEW
							+"] risulta gia' inserito nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createAccordoCooperazione(idAccordoOLD,accordoCooperazione);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateAccordoCooperazione] Errore generatosi durante la modifica dell'accordo di cooperazione ["+idAccordoOLD+"]: "+e,e);
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

			// Controllo id dell'accordo da eliminare
			if(accordoCooperazione.getNome()==null){
				throw new DriverRegistroServiziException("Accordo Cooperazione da eliminare non definito");
			}

			// Controllo pre-esistenza dell'accordo da eliminare
			if( this.generatoreXML.existsAccordoCooperazione(idAccordo) == false){
				throw new DriverRegistroServiziException("L'accordo di cooperazione ["+idAccordo
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteAccordoCooperazione(idAccordo);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoCooperazione] Errore generatosi durante l'eliminazione dell'accordo di cooperazione ["+idAccordo+"]: "+e.getMessage(),e);
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

			// Controllo elementi obbligatori
			if( (accordoServizio.getNome() == null) ||
					(accordoServizio.getProfiloCollaborazione() == null) ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo
			if( this.generatoreXML.existsAccordoServizioParteComune(idAccordo) == true){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordo.toString()
						+"] risulta gia' inserito nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteComune(idAccordo,accordoServizio);

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
			return this.generatoreXML.existsAccordoServizioParteComune(idAccordo);
		}catch(Exception e){
			this.log.error("[existsAccordoServizioParteComune] Accordo non trovato: "+e.getMessage());
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

			// Controllo elementi obbligatori dell'accordo modificato
			if( (accordoServizio.getNome() == null) ||
					(accordoServizio.getProfiloCollaborazione() == null) ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza dell'accordo da modificare
			if( this.generatoreXML.existsAccordoServizioParteComune(idAccordoOLD) == false){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordoOLD
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita dell'accordo (se da modificare)
			IDAccordo idAccordoNEW = this.idAccordoFactory.getIDAccordoFromAccordo(accordoServizio);
			if(idAccordoOLD.equals(idAccordoNEW) == false){
				if( this.generatoreXML.existsAccordoServizioParteComune(idAccordoNEW) == true){
					throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordoNEW
							+"] risulta gia' inserito nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createAccordoServizioParteComune(idAccordoOLD,accordoServizio);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateAccordoServizioParteComune] Errore generatosi durante la modifica dell'accordo di servizio ["+idAccordoOLD+"]: "+e,e);
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

			// Controllo id dell'accordo da eliminare
			if(accordoServizio.getNome()==null){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Comune da eliminare non definito");
			}

			// Controllo pre-esistenza dell'accordo da eliminare
			if( this.generatoreXML.existsAccordoServizioParteComune(idAccordo) == false){
				throw new DriverRegistroServiziException("L'accordo di servizio ["+idAccordo
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteAccordoServizioParteComune(idAccordo);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteComune] Errore generatosi durante l'eliminazione dell'accordo di servizio ["+idAccordo+"]: "+e.getMessage(),e);
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

			// Controllo elementi obbligatori
			if( (pdd.getNome() == null) ){
				throw new DriverRegistroServiziException("Porta di dominio non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza della pdd
			if( this.generatoreXML.existsPortaDominio(pdd.getNome()) == true){
				throw new DriverRegistroServiziException("La porta di dominio ["+pdd.getNome()
						+"] risulta gia' inserita nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createPortaDominio(pdd.getNome(),pdd);

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
			return this.generatoreXML.existsPortaDominio(nome);
		}catch(Exception e){
			this.log.error("[existsPortaDominio] Porta di dominio non trovata: "+e.getMessage());
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
		//if(pddOLD==null)
		pddOLD = pdd.getNome();

		try {

			// Controllo  dell'accordo da Modificare
			if(pddOLD==null){
				throw new DriverRegistroServiziException("Porta di dominio da modificare non definita");
			}

			// Controllo elementi obbligatori dell'accordo modificato
			if( (pdd.getNome() == null) ){
				throw new DriverRegistroServiziException("Porta di dominio modificata non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza della pdd da modificare
			if( this.generatoreXML.existsPortaDominio(pddOLD) == false){
				throw new DriverRegistroServiziException("La porta di dominio ["+pddOLD
						+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita della pdd (se da modificare)
			String pddNEW = pdd.getNome();
			if(pddOLD.equals(pddNEW) == false){
				if( this.generatoreXML.existsPortaDominio(pddNEW) == true){
					throw new DriverRegistroServiziException("La porta di dominio ["+pddNEW
							+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createPortaDominio(pddOLD,pdd);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updatePortaDominio] Errore generatosi durante la modifica della porta di dominio ["+pddOLD+"]: "+e,e);
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
			// Controllo id dell'accordo da eliminare
			if(pdd.getNome()==null){
				throw new DriverRegistroServiziException("Porta di dominio da eliminare non definita");
			}

			// Controllo pre-esistenza della porta di dominio da eliminare
			if( this.generatoreXML.existsPortaDominio(pdd.getNome()) == false){
				throw new DriverRegistroServiziException("La porta di dominio ["+pdd.getNome()
						+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deletePortaDominio(pdd.getNome());

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deletePortaDominio] Errore generatosi durante l'eliminazione della porta di dominio ["+pdd.getNome()+"]: "+e.getMessage(),e);
		}
	}





	
	
	
	
	
	
	
	/**
	 * Crea un nuovo Gruppo
	 * 
	 * @param gruppo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if( gruppo == null)
			throw new DriverRegistroServiziException("[createGruppo] Parametro Non Valido");

		try {

			// Controllo elementi obbligatori
			if( (gruppo.getNome() == null) ){
				throw new DriverRegistroServiziException("Gruppo non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del gruppo
			if( this.generatoreXML.existsGruppo(gruppo.getNome()) == true){
				throw new DriverRegistroServiziException("Il Gruppo ["+gruppo.getNome()
						+"] risulta gia' inserita nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createGruppo(gruppo.getNome(),gruppo);

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
			return this.generatoreXML.existsGruppo(idGruppo.getNome());
		}catch(Exception e){
			this.log.error("[existsGruppo] Gruppo non trovato: "+e.getMessage());
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

			// Controllo  dell'accordo da Modificare
			if(idGruppoOLD==null || idGruppoOLD.getNome()==null){
				throw new DriverRegistroServiziException("Gruppo da modificare non definito");
			}

			// Controllo elementi obbligatori del gruppo modificato
			if( (gruppo.getNome() == null) ){
				throw new DriverRegistroServiziException("Gruppo modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del gruppo da modificare
			if( this.generatoreXML.existsGruppo(idGruppoOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Gruppo ["+idGruppoOLD
						+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del gruppo (se da modificare)
			IDGruppo idGruppoNEW = new IDGruppo(gruppo.getNome());
			if(idGruppoOLD.equals(idGruppoNEW) == false){
				if( this.generatoreXML.existsGruppo(idGruppoNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Gruppo ["+idGruppoNEW
							+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createGruppo(idGruppoOLD.getNome(),gruppo);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateGruppo] Errore generatosi durante la modifica della porta di dominio ["+idGruppoOLD+"]: "+e,e);
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
			// Controllo id del gruppo da eliminare
			if(gruppo.getNome()==null){
				throw new DriverRegistroServiziException("Gruppo da eliminare non definito");
			}

			// Controllo pre-esistenza del gruppo da eliminare
			if( this.generatoreXML.existsGruppo(gruppo.getNome()) == false){
				throw new DriverRegistroServiziException("Il Gruppo ["+gruppo.getNome()
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteGruppo(gruppo.getNome());

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteGruppo] Errore generatosi durante l'eliminazione del gruppo ["+gruppo.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	

	
	
	
	
	/**
	 * Crea un nuovo Ruolo
	 * 
	 * @param ruolo
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if( ruolo == null)
			throw new DriverRegistroServiziException("[createRuolo] Parametro Non Valido");

		try {

			// Controllo elementi obbligatori
			if( (ruolo.getNome() == null) ){
				throw new DriverRegistroServiziException("Ruolo non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del ruolo
			if( this.generatoreXML.existsRuolo(ruolo.getNome()) == true){
				throw new DriverRegistroServiziException("Il Ruolo ["+ruolo.getNome()
						+"] risulta gia' inserita nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createRuolo(ruolo.getNome(),ruolo);

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
			return this.generatoreXML.existsRuolo(idRuolo.getNome());
		}catch(Exception e){
			this.log.error("[existsRuolo] Ruolo non trovato: "+e.getMessage());
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

			// Controllo  dell'accordo da Modificare
			if(idRuoloOLD==null || idRuoloOLD.getNome()==null){
				throw new DriverRegistroServiziException("Ruolo da modificare non definito");
			}

			// Controllo elementi obbligatori del ruolo modificato
			if( (ruolo.getNome() == null) ){
				throw new DriverRegistroServiziException("Ruolo modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del ruolo da modificare
			if( this.generatoreXML.existsRuolo(idRuoloOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Ruolo ["+idRuoloOLD
						+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del ruolo (se da modificare)
			IDRuolo idRuoloNEW = new IDRuolo(ruolo.getNome());
			if(idRuoloOLD.equals(idRuoloNEW) == false){
				if( this.generatoreXML.existsRuolo(idRuoloNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Ruolo ["+idRuoloNEW
							+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createRuolo(idRuoloOLD.getNome(),ruolo);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateRuolo] Errore generatosi durante la modifica della porta di dominio ["+idRuoloOLD+"]: "+e,e);
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
			// Controllo id del ruolo da eliminare
			if(ruolo.getNome()==null){
				throw new DriverRegistroServiziException("Ruolo da eliminare non definito");
			}

			// Controllo pre-esistenza del ruolo da eliminare
			if( this.generatoreXML.existsRuolo(ruolo.getNome()) == false){
				throw new DriverRegistroServiziException("Il Ruolo ["+ruolo.getNome()
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteRuolo(ruolo.getNome());

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteRuolo] Errore generatosi durante l'eliminazione del ruolo ["+ruolo.getNome()+"]: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Crea un nuovo Scope
	 * 
	 * @param scope
	 * @throws DriverRegistroServiziException
	 */
	@Override
	public void createScope(Scope scope) throws DriverRegistroServiziException{
		if( scope == null)
			throw new DriverRegistroServiziException("[createScope] Parametro Non Valido");

		try {

			// Controllo elementi obbligatori
			if( (scope.getNome() == null) ){
				throw new DriverRegistroServiziException("Scope non completamente definita nei parametri obbligatori");
			}

			// Controllo pre-esistenza del scope
			if( this.generatoreXML.existsScope(scope.getNome()) == true){
				throw new DriverRegistroServiziException("Il Scope ["+scope.getNome()
						+"] risulta gia' inserita nel registro dei servizi.");
			} 

			// Generazione XML
			this.generatoreXML.createScope(scope.getNome(),scope);

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
			return this.generatoreXML.existsScope(idScope.getNome());
		}catch(Exception e){
			this.log.error("[existsScope] Scope non trovato: "+e.getMessage());
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

			// Controllo  dell'accordo da Modificare
			if(idScopeOLD==null || idScopeOLD.getNome()==null){
				throw new DriverRegistroServiziException("Scope da modificare non definito");
			}

			// Controllo elementi obbligatori del scope modificato
			if( (scope.getNome() == null) ){
				throw new DriverRegistroServiziException("Scope modificato non completamente definito nei parametri obbligatori");
			}

			// Controllo pre-esistenza del scope da modificare
			if( this.generatoreXML.existsScope(idScopeOLD.getNome()) == false){
				throw new DriverRegistroServiziException("Il Scope ["+idScopeOLD
						+"] non risulta gia' inserita nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del scope (se da modificare)
			IDScope idScopeNEW = new IDScope(scope.getNome());
			if(idScopeOLD.equals(idScopeNEW) == false){
				if( this.generatoreXML.existsScope(idScopeNEW.getNome()) == true){
					throw new DriverRegistroServiziException("Il Scope ["+idScopeNEW
							+"] risulta gia' inserita nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createScope(idScopeOLD.getNome(),scope);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[updateScope] Errore generatosi durante la modifica della porta di dominio ["+idScopeOLD+"]: "+e,e);
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
			// Controllo id del scope da eliminare
			if(scope.getNome()==null){
				throw new DriverRegistroServiziException("Scope da eliminare non definito");
			}

			// Controllo pre-esistenza del scope da eliminare
			if( this.generatoreXML.existsScope(scope.getNome()) == false){
				throw new DriverRegistroServiziException("Il Scope ["+scope.getNome()
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteScope(scope.getNome());

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
			if(this.generatoreXML.existsSoggetto(idSoggetto) == true){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
						+"] risulta gia' inserito nel registro dei servizi.");
			} 


			// Generazione XML
			this.generatoreXML.createSoggetto(idSoggetto,soggetto);

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
			return this.generatoreXML.existsSoggetto(idSoggetto);
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
			if( this.generatoreXML.existsSoggetto(idSoggettoOLD) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoOLD_string
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo non esistenza della nuova identita del soggetto (se da modificare)
			String idSoggettoNEW_string = soggetto.getTipo() + soggetto.getNome();
			if(idSoggettoOLD_string.equals(idSoggettoNEW_string) == false){
				if( this.generatoreXML.existsSoggetto(idSoggettoNEW) == true){
					throw new DriverRegistroServiziException("La nuova identita da associare al soggetto ["+idSoggettoNEW_string
							+"] risulta gia' utilizzata nel registro dei servizi.");
				} 
			}

			// Ri-Generazione XML
			this.generatoreXML.createSoggetto(idSoggettoOLD,soggetto);

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
			// Controllo id del soggetto da eliminare
			if(soggetto.getTipo()==null || soggetto.getNome()==null){
				throw new DriverRegistroServiziException("Soggetto da eliminare non definito");
			}

			// Controllo pre-esistenza del soggetto da modificare
			IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
			if( this.generatoreXML.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Delete from Repository
			this.generatoreXML.deleteSoggetto(idSoggetto);

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
			// Controllo elementi obbligatori
			if( (asps.getNomeSoggettoErogatore() == null) || 
					(asps.getTipoSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			if( (asps.getNome() == null) || 
					(asps.getTipo() == null)  || 
					(asps.getVersione() == null)  ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Specifica, non definito");
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
					ConfigurazioneServizioAzione checkAz = checkFr.getConfigurazioneAzione(i);
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
							throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getAzioneList()+" della fruizione del servizio non corretta");
						}
					}
				}
				// Controllo pre-esistenza del soggetto fruitore
				String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.generatoreXML.existsSoggetto(idSoggettoFruitore) == false){
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
							throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getAzioneList()+" del servizio non corretta");
						}
					}
				}
			}

			// Controllo pre-esistenza del soggetto erogatore
			IDSoggetto idSoggettoErogatore = new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			String idSoggettoErogatore_string = asps.getTipoSoggettoErogatore()+asps.getNomeSoggettoErogatore();
			if( this.generatoreXML.existsSoggetto(idSoggettoErogatore) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoErogatore_string
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del servizio
			String idServizio_string = asps.getTipo() + asps.getNome() + asps.getVersione();
			IDServizio idServizio = this.idServizioFactory.getIDServizioFromAccordo(asps);
			if (this.generatoreXML.existsAccordoServizioParteSpecifica(idServizio)==true){
				throw new DriverRegistroServiziException("Il servizio ["+idServizio_string+"] erogato dal soggetto ["+idSoggettoErogatore_string+"] risulta gia' registrato nel registro");
			}

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteSpecifica(idServizio,asps);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createAccordoServizioParteSpecifica] Errore generatosi durante la creazione del nuovo Servizio ["+
					this.idServizioFactory.getIDServizioFromAccordo(asps)+"]: "+e.getMessage(),e);
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
		}catch(DriverRegistroServiziNotFound notFound){
			return false;
		}
		catch(Exception e){
			this.log.error("[existsAccordoServizioParteSpecifica] Servizio non trovato: "+e.getMessage());
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
							throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getAzioneList()+" della fruizione del servizio non corretta");
						}
					}
				}
				// Controllo pre-esistenza del soggetto fruitore
				/*String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.generatoreXML.existsSoggetto(idSoggettoFruitore) == false){
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
			if( this.generatoreXML.existsSoggetto(idSoggettoOLD) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoOLD_string
						+"] da modificare, non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del nuovo soggetto erogatore
			String idSoggettoNEW_string = asps.getTipoSoggettoErogatore() + asps.getNomeSoggettoErogatore();
			IDSoggetto idSoggettoNEW = new IDSoggetto(asps.getTipoSoggettoErogatore(),asps.getNomeSoggettoErogatore());
			if( idSoggettoOLD_string.equals(idSoggettoNEW_string) == false ){
				if( this.generatoreXML.existsSoggetto(idSoggettoNEW) == false){
					throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoNEW_string
							+"] a cui re-assegnare il servizio, non risulta gia' inserito nel registro dei servizi.");
				} 
			}

			// Controllo pre-esistenza del servizio da modificare
			if (this.generatoreXML.existsAccordoServizioParteSpecifica(idServizioOLD)==false){
				throw new DriverRegistroServiziException("Il servizio ["+idServizioOLD_string+"] erogato dal soggetto ["+idSoggettoOLD_string+"] non risulta gia' registrato nel registro");
			}

			// check esistenza nuovo servizio
			IDServizio idServizioNEW = this.idServizioFactory.getIDServizioFromValues(asps.getTipo(),asps.getNome(), idSoggettoNEW, asps.getVersione());
			String idServizioNEW_string = asps.getTipo() + asps.getNome() + asps.getVersione();
			if((idServizioOLD_string.equals(idServizioNEW_string) == false) || (idSoggettoOLD_string.equals(idSoggettoNEW_string) == false)){
				// Controllo non esistenza del servizio da creare
				if (this.generatoreXML.existsAccordoServizioParteSpecifica(idServizioNEW)==true){
					throw new DriverRegistroServiziException("Il servizio ["+idServizioNEW_string+"] erogato dal soggetto ["+idSoggettoNEW_string+"] risulta gia' registrato nel registro");
				}
			}


			if( idSoggettoOLD_string.equals(idSoggettoNEW_string) == true ){
				// NON e' CAMBIATO IL SOGGETTO EROGATORE.

				// Ri-Generazione XML
				this.generatoreXML.createAccordoServizioParteSpecifica(idServizioOLD,asps);

			}else{
				// E' CAMBIATO IL SOGGETTO EROGATORE

				// Delete from Repository
				this.generatoreXML.deleteAccordoServizioParteSpecifica(idServizioOLD);

				// Generazione XML
				this.generatoreXML.createAccordoServizioParteSpecifica(idServizioNEW,asps);

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
		String idServizio_string = asps.getTipo() + asps.getNome()+ asps.getVersione();

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
			if( this.generatoreXML.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 
			// Controllo pre-esistenza del servizio
			IDServizio idServizio = this.idServizioFactory.getIDServizioFromValues(asps.getTipo(),asps.getNome(), idSoggetto, asps.getVersione());
			if (this.generatoreXML.existsAccordoServizioParteSpecifica(idServizio)==false){
				throw new DriverRegistroServiziException("Il servizio ["+idServizio_string+"] erogato dal soggetto ["+idSoggetto_string+"] non risulta gia' registrato nel registro");
			}

			// Delete from Repository
			this.generatoreXML.deleteAccordoServizioParteSpecifica(idServizio);

		}catch (Exception e) {
			throw new DriverRegistroServiziException("[deleteAccordoServizioParteSpecifica] Errore generatosi durante l'eliminazione del Servizio ["+idServizio_string
					+"] erogato dal soggetto ["+idSoggetto_string+"]: "+e.getMessage(),e);
		}
	}

	
	

	//RESET
	@Override
	public void reset() throws DriverRegistroServiziException{
		try{
			AccordoServizioParteComune[] accordiRegistrati = this.generatoreXML.getAccordiServizioParteComune();
			if(accordiRegistrati!=null){
				for(int i=0; i<accordiRegistrati.length;i++){
					this.log.info("eliminazione accordo di servizio ["+this.idAccordoFactory.getUriFromAccordo(accordiRegistrati[i])+"] in corso...");
					this.deleteAccordoServizioParteComune(accordiRegistrati[i]);
					this.log.info("eliminazione accordo di servizio ["+this.idAccordoFactory.getUriFromAccordo(accordiRegistrati[i])+"] effettuata.");
				}
			}
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Errore durante la cancellazione degli accordi registrati: "+e.getMessage(),e);
		}
		try{
			AccordoCooperazione[] accordiCooperazioneRegistrati = this.generatoreXML.getAccordiCooperazione();
			if(accordiCooperazioneRegistrati!=null){
				for(int i=0; i<accordiCooperazioneRegistrati.length;i++){
					this.log.info("eliminazione accordo di cooperazione ["+this.idAccordoCooperazioneFactory.getUriFromAccordo(accordiCooperazioneRegistrati[i])+"] in corso...");
					this.deleteAccordoCooperazione(accordiCooperazioneRegistrati[i]);
					this.log.info("eliminazione accordo di cooperazione ["+this.idAccordoCooperazioneFactory.getUriFromAccordo(accordiCooperazioneRegistrati[i])+"] effettuata.");
				}
			}
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Errore durante la cancellazione degli accordi di cooperazione registrati: "+e.getMessage(),e);
		}
		try{
			AccordoServizioParteSpecifica[] serviziRegistrati = this.generatoreXML.getAccordiServiziParteSpecifica();
			if(serviziRegistrati!=null){
				for(int i=0; i<serviziRegistrati.length;i++){
					this.log.info("eliminazione servizio ["+this.idServizioFactory.getUriFromAccordo(serviziRegistrati[i])+"] in corso...");
					this.deleteAccordoServizioParteSpecifica(serviziRegistrati[i]);
					this.log.info("eliminazione servizio ["+this.idServizioFactory.getUriFromAccordo(serviziRegistrati[i])+"] effettuata.");
				}
			}
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Errore durante la cancellazione dei servizi registrati: "+e.getMessage(),e);
		}
		try{
			Soggetto[] soggettiRegistrati = this.generatoreXML.getSoggetti();
			if(soggettiRegistrati!=null){
				for(int i=0; i<soggettiRegistrati.length;i++){
					this.log.info("eliminazione soggetto ["+soggettiRegistrati[i].getTipo()+"/"+soggettiRegistrati[i].getNome()+"] in corso...");
					this.deleteSoggetto(soggettiRegistrati[i]);
					this.log.info("eliminazione soggetto ["+soggettiRegistrati[i].getTipo()+"/"+soggettiRegistrati[i].getNome()+"] effettuata.");
				}
			}
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Errore durante la cancellazione dei soggetti registrati: "+e.getMessage(),e);
		}
		try{
			PortaDominio[] pddRegistrate = this.generatoreXML.getPorteDominio();
			if(pddRegistrate!=null){
				for(int i=0; i<pddRegistrate.length;i++){
					this.log.info("eliminazione porta di dominio ["+pddRegistrate[i].getNome()+"] in corso...");
					this.deletePortaDominio(pddRegistrate[i]);
					this.log.info("eliminazione porta di dominio ["+pddRegistrate[i].getNome()+"] effettuata.");
				}
			}
		}catch (Exception e) {
			throw new DriverRegistroServiziException("Errore durante la cancellazione delle porte di dominio registrate: "+e.getMessage(),e);
		}
	}
	
	//RESET PDD CONSOLE
	public void resetCtrlstat() throws DriverRegistroServiziException{
	    return;
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

		String urlVerifica = this.urlPrefix + CostantiRegistroServizi.URL_SEPARATOR;
		try{
			byte[] dir = HttpUtilities.requestHTTPFile(urlVerifica);
			if(dir==null)
				throw new Exception("Directory accordi richiesta non ritornata");
		}catch(Exception e){
			throw new CoreException("Connessione al registro non disponibile ["+urlVerifica+"]: "+e.getMessage(),e);
		}
	}

}

