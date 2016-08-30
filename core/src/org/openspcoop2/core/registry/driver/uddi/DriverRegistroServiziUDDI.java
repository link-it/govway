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




package org.openspcoop2.core.registry.driver.uddi;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IDriverWS;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CostantiXMLRepository;
import org.openspcoop2.core.registry.driver.BeanUtilities;
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
import org.openspcoop2.core.registry.driver.web.XMLLib;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.HttpUtilities;

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
				this.validatoreRegistro = new ValidatoreXSD(this.log,DriverRegistroServiziUDDI.class.getResourceAsStream("/registroServizi.xsd"));
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
		InputStreamReader istr = null;
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
			
			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeAccordoCooperazioneList()>0)
				accRichiesto = rs.getAccordoCooperazione(0);
			istr.close();
			bin.close();
		}catch(Exception e){
			try{
				if(istr!=null)
					istr.close();
			} catch(Exception eis) {}
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
				idAccordoFiltro = this.idAccordoCooperazioneFactory.getIDAccordoFromValues(filtroRicerca.getNomeAccordo(),filtroRicerca.getVersione());
			}
			
			String [] urlXMLAccordi = this.uddiLib.getUrlXmlAccordiCooperazione(idAccordoFiltro,this.urlPrefix);
			
			// Esamina degli accordi
			Vector<IDAccordoCooperazione> idAccordi = new Vector<IDAccordoCooperazione>();
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
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordi[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizeAccordoCooperazioneList()>0)
						ac = rs.getAccordoCooperazione(0);
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
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
				}
				idAccordi.add(this.idAccordoCooperazioneFactory.getIDAccordoFromValues(ac.getNome(),ac.getVersione()));
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
		InputStreamReader istr = null;
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
			
			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeAccordoServizioParteComuneList()>0)
				accRichiesto = rs.getAccordoServizioParteComune(0);
			istr.close();
			bin.close();
		}catch(Exception e){
			try{
				if(istr!=null)
					istr.close();
			} catch(Exception eis) {}
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
		try{

			// Ricerca UDDI degli accordi
			/*if(this.urlPrefix==null){
				throw new DriverRegistroServiziException("[getAllIdAccordiServizio] Implementazione non eseguibile se il driver non viene inizializzato con urlPrefix.");
			}*/
			
			IDAccordo idAccordoFiltro = null;
			if(filtroRicerca!=null && filtroRicerca.getNomeAccordo()!=null){
				IDSoggetto soggettoReferente = null;
				if(filtroRicerca.getTipoSoggettoReferente()!=null && filtroRicerca.getNomeSoggettoReferente()!=null){
					soggettoReferente = new IDSoggetto(filtroRicerca.getTipoSoggettoReferente(),filtroRicerca.getNomeSoggettoReferente());
				}
				idAccordoFiltro = this.idAccordoFactory.getIDAccordoFromValues(filtroRicerca.getNomeAccordo(),soggettoReferente,filtroRicerca.getVersione());
			}
			
			String [] urlXMLAccordi = this.uddiLib.getUrlXmlAccordiServizio(idAccordoFiltro,this.urlPrefix);
			
			// Esamina degli accordi
			Vector<IDAccordo> idAccordi = new Vector<IDAccordo>();
			for(int i=0; i<urlXMLAccordi.length; i++){
				org.openspcoop2.core.registry.AccordoServizioParteComune as = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLAccordi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteComune] Riscontrato errore durante la validazione XSD ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				
				// Ottengo oggetto Accordo
				ByteArrayInputStream bin = null;
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLAccordi[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizeAccordoServizioParteComuneList()>0)
						as = rs.getAccordoServizioParteComune(0);
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteComune] Errore durante il parsing xml ("+urlXMLAccordi[i]+"): "+e.getMessage(),e);
				}
				if(as==null)
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteComune] accordo non definito per la url ["+urlXMLAccordi[i]+"]");
				String asURI = this.idAccordoFactory.getUriFromAccordo(as);
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteComune](FiltroByMinDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteComune](FiltroByMaxDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNomeAccordo()!=null){
						if(as.getNome().equals(filtroRicerca.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(as.getVersione().equals(filtroRicerca.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
						if(as.getSoggettoReferente()==null)
							continue;
						if(filtroRicerca.getTipoSoggettoReferente()!=null){
							if(as.getSoggettoReferente().getTipo().equals(filtroRicerca.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicerca.getNomeSoggettoReferente()!=null){
							if(as.getSoggettoReferente().getNome().equals(filtroRicerca.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					
					if(filtroRicerca.getIdAccordoCooperazione()!=null &&
							(filtroRicerca.getIdAccordoCooperazione().getNome()!=null || 
							filtroRicerca.getIdAccordoCooperazione().getVersione()!=null) ){
						if(as.getServizioComposto()==null){
							continue;
						}
						IDAccordoCooperazione idAC = this.idAccordoCooperazioneFactory.getIDAccordoFromUri(as.getServizioComposto().getAccordoCooperazione());
						if(filtroRicerca.getIdAccordoCooperazione().getNome()!=null){
							if(idAC.getNome().equals(filtroRicerca.getIdAccordoCooperazione().getNome())== false){
								continue;
							}
						}
						if(filtroRicerca.getIdAccordoCooperazione().getVersione()!=null){
							if(idAC.getVersione().equals(filtroRicerca.getIdAccordoCooperazione().getVersione())== false){
								continue;
							}
						}
					}
					else if(filtroRicerca.isServizioComposto()!=null){
						if(filtroRicerca.isServizioComposto()){
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
					
				}
				idAccordi.add(this.idAccordoFactory.getIDAccordoFromValues(as.getNome(),BeanUtilities.getSoggettoReferenteID(as.getSoggettoReferente()),as.getVersione()));
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
				throw new DriverRegistroServiziException("getAllIdAccordiServizioParteComune error",e);
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
		InputStreamReader istr = null;
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
			
			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizePortaDominioList()>0)
				pddRichiesta = rs.getPortaDominio(0);
			istr.close();
			bin.close();
		}catch(Exception e){
			try{
				if(istr!=null)
					istr.close();
			} catch(Exception eis) {}
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
			Vector<String> nomiPdd = new Vector<String>();
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
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLPdd[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizePortaDominioList()>0)
						pd = rs.getPortaDominio(0);
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
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
		InputStreamReader istr = null;
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
			
			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeSoggettoList()>0)
				soggRichiesto = rs.getSoggetto(0);
			istr.close();
			bin.close();
		}catch(Exception e){
			try{
				if(istr!=null)
					istr.close();
			} catch(Exception eis) {}
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
			
			// Esamina dei soggetti
			Vector<IDSoggetto> idSoggetti = new Vector<IDSoggetto>();
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
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLSoggetti[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizeSoggettoList()>0)
						ss = rs.getSoggetto(0);
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
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
			throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica] Il servizio ["+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+"] erogato dal soggetto ["+idServizio.getSoggettoErogatore()+"] non risulta gia' registrato nel registro");
		}
		
		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servRichiesto = null;
		// get URL XML Servizio
		String urlXMLServizio = this.uddiLib.getUrlXmlServizio(idServizio);
		if( urlXMLServizio == null ){
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Definizione XML non disponibile");
		}
		
		ByteArrayInputStream bin = null;
		InputStreamReader istr = null;
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
			
			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.getServizio().setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
					servRichiesto.getServizio().setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
				}
			}
			istr.close();
			bin.close();
		}catch(Exception e){
			try{
				if(istr!=null)
					istr.close();
			} catch(Exception eis) {}
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
	
	
	@Override
	public org.openspcoop2.core.registry.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		throw new DriverRegistroServiziException("Not Implemented");
		
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
			InputStreamReader istr = null;
			try{
				IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
				IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
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
				
				// Parsing JIBX
				bin = new ByteArrayInputStream(fileXML);
				istr = new InputStreamReader(bin);
				org.openspcoop2.core.registry.RegistroServizi rs = 
					(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
				if(rs.sizeSoggettoList()>0){
					if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
						servCorrelatoRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
						servCorrelatoRichiesto.getServizio().setNomeSoggettoErogatore(idSoggetto.getNome());
						servCorrelatoRichiesto.getServizio().setTipoSoggettoErogatore(idSoggetto.getTipo());
					}
				}
				istr.close();
				bin.close();
				if(servCorrelatoRichiesto!=null)
					break;
			}catch(Exception e){
				try{
					if(istr!=null)
						istr.close();
				} catch(Exception eis) {}
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

	
	/**
	 *  Ritorna gli identificatori dei servizi che rispettano il parametro di ricerca
	 * 
	 * @param filtroRicerca
	 * @return Una lista di ID dei servizi trovati
	 * @throws DriverRegistroServiziException
	 * @throws DriverRegistroServiziNotFound
	 */
	@Override
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
			
		try{
	
			// Ricerca UDDI
			IDAccordo idAccordo = null;
			IDSoggetto soggettoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			if(filtroRicerca!=null){
				idAccordo = filtroRicerca.getIdAccordo();
				if(filtroRicerca.getTipoSoggettoErogatore()!=null && filtroRicerca.getNomeSoggettoErogatore()!=null){
					soggettoErogatore = new IDSoggetto(filtroRicerca.getTipoSoggettoErogatore(),filtroRicerca.getNomeSoggettoErogatore());
				}
				tipoServizio = filtroRicerca.getTipo();
				nomeServizio = filtroRicerca.getNome();
			}
			String [] urlXMLServizi = this.uddiLib.getUrlXMLServiziBySearch(idAccordo, soggettoErogatore, tipoServizio, nomeServizio);
			
			
			
			// Esamina dei servizi
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
			for(int i=0; i<urlXMLServizi.length; i++){
			
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica serv = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdServizi] Riscontrato errore durante la validazione XSD URL("+urlXMLServizi[i]+"): "+e.getMessage(),e);
				}
				
				ByteArrayInputStream bin = null;
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLServizi[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizeSoggettoList()>0){
						if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0 ){
							org.openspcoop2.core.registry.Soggetto s = rs.getSoggetto(0);
							if(s.sizeAccordoServizioParteSpecificaList()>0){
								serv = s.getAccordoServizioParteSpecifica(0);
								serv.getServizio().setNomeSoggettoErogatore(s.getNome());
								serv.getServizio().setTipoSoggettoErogatore(s.getTipo());
							}else{
								continue;
							}
						}
					}
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
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
						if(serv.getServizio().getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(serv.getServizio().getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdServizi](FiltroByMinDate) Servizio["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] SoggettoErogatore["+serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdServizi](FiltroByMaxDate) Servizio["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] SoggettoErogatore["+serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(serv.getServizio().getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
						}
					if(filtroRicerca.getNome()!=null){
						if(serv.getServizio().getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordo()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
						if(serv.getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					IDServizio idServ = new IDServizio(serv.getServizio().getTipoSoggettoErogatore(),serv.getServizio().getNomeSoggettoErogatore(),serv.getServizio().getTipo(),serv.getServizio().getNome());
					idServ.setUriAccordo(serv.getAccordoServizioParteComune());
					idServ.setTipologiaServizio(serv.getServizio().getTipologiaServizio().toString());
					idServizi.add(idServ);
				}
			}
			if(idServizi.size()==0){
				throw new DriverRegistroServiziNotFound("Servizi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idServizi;
			}

		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdServizi error",e);
		}
	}
	
	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		
		try{
	
			// Ricerca UDDI
			IDAccordo idAccordo = null;
			IDSoggetto soggettoErogatore = null;
			String tipoServizio = null;
			String nomeServizio = null;
			if(filtroRicerca!=null){
				idAccordo = filtroRicerca.getIdAccordo();
				if(filtroRicerca.getTipoSoggettoErogatore()!=null && filtroRicerca.getNomeSoggettoErogatore()!=null){
					soggettoErogatore = new IDSoggetto(filtroRicerca.getTipoSoggettoErogatore(),filtroRicerca.getNomeSoggettoErogatore());
				}
				tipoServizio = filtroRicerca.getTipo();
				nomeServizio = filtroRicerca.getNome();
			}
			String [] urlXMLServizi = this.uddiLib.getUrlXMLServiziBySearch(idAccordo, soggettoErogatore, tipoServizio, nomeServizio);
			
			
			
			// Esamina dei servizi
			Vector<IDAccordo> idServizi = new Vector<IDAccordo>();
			for(int i=0; i<urlXMLServizi.length; i++){
			
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica serv = null;
				
				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizi[i]);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteSpecifica] Riscontrato errore durante la validazione XSD URL("+urlXMLServizi[i]+"): "+e.getMessage(),e);
				}
				
				ByteArrayInputStream bin = null;
				InputStreamReader istr = null;
				try{
					IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
					IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
					byte[] fileXML = HttpUtilities.requestHTTPFile(urlXMLServizi[i]);
					bin = new ByteArrayInputStream(fileXML);
					istr = new InputStreamReader(bin);
					org.openspcoop2.core.registry.RegistroServizi rs = 
						(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
					if(rs.sizeSoggettoList()>0){
						if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0 ){
							org.openspcoop2.core.registry.Soggetto s = rs.getSoggetto(0);
							if(s.sizeAccordoServizioParteSpecificaList()>0){
								serv = s.getAccordoServizioParteSpecifica(0);
								serv.getServizio().setNomeSoggettoErogatore(s.getNome());
								serv.getServizio().setTipoSoggettoErogatore(s.getTipo());
							}else{
								continue;
							}
						}
					}
					istr.close();
					bin.close();
				}catch(Exception e){
					try{
						if(istr!=null)
							istr.close();
					} catch(Exception eis) {}
					try{
						if(bin!=null)
							bin.close();
					} catch(Exception eis) {}
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteSpecifica] Errore durante il parsing xml del servizio("+urlXMLServizi[i]+"): "+e.getMessage(),e);
				}
				if(serv==null)
					throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteSpecifica] servizio non definito per la url ["+urlXMLServizi[i]+"]");
				
				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(serv.getServizio().getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(serv.getServizio().getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteSpecifica](FiltroByMinDate) Servizio["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] SoggettoErogatore["+serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(serv.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteSpecifica](FiltroByMaxDate) Servizio["+serv.getServizio().getTipo()+"/"+serv.getServizio().getNome()+"] SoggettoErogatore["+serv.getServizio().getTipoSoggettoErogatore()+"/"+serv.getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(serv.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(serv.getServizio().getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
						}
					if(filtroRicerca.getNome()!=null){
						if(serv.getServizio().getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordo()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
						if(serv.getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					idServizi.add(this.idAccordoFactory.getIDAccordoFromAccordo(serv));
				}
			}
			if(idServizi.size()==0){
				throw new DriverRegistroServiziNotFound("Servizi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			}else{
				return idServizi;
			}

		}catch(Exception e){
			if(e instanceof DriverRegistroServiziNotFound)
				throw (DriverRegistroServiziNotFound)e;
			else
				throw new DriverRegistroServiziException("getAllIdAccordiServizioParteSpecifica error",e);
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
		
		String tipoOLD = soggetto.getOldTipoForUpdate();
		String nomeOLD = soggetto.getOldNomeForUpdate();
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
		
		Servizio servizio = asps.getServizio();
		
	
		try {

			// controllo inizializzazione UDDI
			if(this.uddiLib.create == false){
				throw new DriverRegistroServiziException("Inizializzazione dell'engine UDDI errata");
			}

			// Controllo elementi obbligatori
			if( (servizio.getNomeSoggettoErogatore() == null) || 
					(servizio.getTipoSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			if( (servizio.getNome() == null) || 
					(servizio.getTipo() == null) ){
				throw new DriverRegistroServiziException("Servizio, non definito");
			}
			if(asps.getAccordoServizioParteComune()==null){
				throw new DriverRegistroServiziException("Accordo di Servizio, da associare al servizio, non definito");
			}
			if(servizio.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(servizio.getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( servizio.getConnettore().getTipo() == null ){
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
				// Controllo pre-esistenza del soggetto fruitore
				String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.uddiLib.existsSoggetto(idSoggettoFruitore) == false){
					throw new DriverRegistroServiziException("Il fruitore ["+idSoggettoFruitore_string
					+"] non risulta gia' inserito nel registro dei servizi.");
				} 
			}
			for(int i=0;i<servizio.sizeParametriAzioneList();i++){
				ServizioAzione checkAz = servizio.getParametriAzione(i);
				if( (checkAz.getNome()==null) || (checkAz.getConnettore()==null)){
					throw new DriverRegistroServiziException("Definizione di un azione senza nome o connettore");
				}
				// controllo connettore
				if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
					boolean connettoreNonDefinito = false;
					if( checkAz.getConnettore().getTipo() == null ){
						connettoreNonDefinito = true;
					}
					if(connettoreNonDefinito){
						throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getNome()+" del servizio non corretta");
					}
				}
				for(int j=0;j<checkAz.sizeParametriFruitoreList();j++){
					ServizioAzioneFruitore checkAzFr = checkAz.getParametriFruitore(j);
					if( (checkAzFr.getNome()==null) || (checkAzFr.getTipo()==null) || (checkAzFr.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un fruitore di una azione senza nome o connettore");
					}
					// controllo connettore
					if(checkAzFr.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAzFr.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAzFr.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso del fruitore "+checkAzFr.getTipo()+checkAzFr.getNome()+" dell'azione "+checkAz.getNome()+" del servizio non corretta");
						}
					}
				}
			}

			// Controllo pre-esistenza del soggetto erogatore
			IDSoggetto idSoggettoErogatore = new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore());
			String idSoggettoErogatore_string = servizio.getTipoSoggettoErogatore()+servizio.getNomeSoggettoErogatore();
			if( this.uddiLib.existsSoggetto(idSoggettoErogatore) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoErogatore_string
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del servizio
			String idServizio_string = servizio.getTipo() + servizio.getNome();
			IDServizio idServizio = new IDServizio(idSoggettoErogatore,servizio.getTipo(),servizio.getNome());
			if (this.uddiLib.existsServizio(idServizio)==true){
				throw new DriverRegistroServiziException("Il servizio ["+idServizio_string+"] erogato dal soggetto ["+idSoggettoErogatore_string+"] risulta gia' registrato nel registro");
			}

			String urlXML = this.urlPrefix + idSoggettoErogatore_string + CostantiRegistroServizi.URL_SEPARATOR +CostantiXMLRepository.SERVIZI+ 
			    CostantiRegistroServizi.URL_SEPARATOR + idServizio_string  + ".xml";

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteSpecifica(idServizio,asps);
			
			// Registrazione nel registro UDDI.
			this.uddiLib.createServizio(idServizio,
					urlXML,this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
				
		}catch (Exception e) {
			throw new DriverRegistroServiziException("[createAccordoServizioParteSpecifica] Errore generatosi durante la creazione del nuovo Servizio ["+servizio.getTipo()+servizio.getNome()
			+"] erogato dal soggetto ["+servizio.getTipoSoggettoErogatore()+servizio.getNomeSoggettoErogatore()+"]: "+e.getMessage(),e);
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
	
    @Override
	public boolean existsAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException{
    	if( idAccordo == null)
			return false;
	
		try{
			this.getAccordoServizioParteSpecifica(idAccordo);
			
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
		
		Servizio servizio = asps.getServizio();
		
	
		String tipoServizioOLD = servizio.getOldTipoForUpdate();
		String nomeServizioOLD = servizio.getOldNomeForUpdate();
		if(tipoServizioOLD==null||nomeServizioOLD==null){
			tipoServizioOLD = servizio.getTipo();
			nomeServizioOLD = servizio.getNome();
		}
		String tipoSoggettoOLD = servizio.getOldTipoSoggettoErogatoreForUpdate();
		String nomeSoggettoOLD = servizio.getOldNomeSoggettoErogatoreForUpdate();
		if(tipoSoggettoOLD==null||nomeSoggettoOLD==null){
			tipoSoggettoOLD = servizio.getTipoSoggettoErogatore();
			nomeSoggettoOLD = servizio.getNomeSoggettoErogatore();
		}
		String idSoggettoOLD_string = tipoSoggettoOLD+nomeSoggettoOLD;
		IDSoggetto idSoggettoOLD = new IDSoggetto(tipoSoggettoOLD,nomeSoggettoOLD);
		String idServizioOLD_string = tipoServizioOLD + nomeServizioOLD;
		IDServizio idServizioOLD = new IDServizio(idSoggettoOLD,tipoServizioOLD,nomeServizioOLD);
				
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
			if( (servizio.getTipoSoggettoErogatore() == null) || 
					(servizio.getNomeSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			// vecchio ID Servizio
			if( (tipoServizioOLD == null) || 
					(nomeServizioOLD == null) ){
				throw new DriverRegistroServiziException("Servizio da modificare non definito");
			}
			// nuovo ID Servizio
			if( (servizio.getNome() == null) || 
					(servizio.getTipo() == null) ){
				throw new DriverRegistroServiziException("Dati del nuovo servizio, non definiti");
			}
			// accordo
			if(asps.getAccordoServizioParteComune()==null){
				throw new DriverRegistroServiziException("Accordo di Servizio, da associare al servizio, non definito");
			}
			// connettore
			if(servizio.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(servizio.getConnettore().getTipo())){
				boolean connettoreNonDefinito = false;
				if( servizio.getConnettore().getTipo() == null ){
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
				// Controllo pre-esistenza del soggetto fruitore
				/*String idSoggettoFruitore_string = checkFr.getTipo() + checkFr.getNome();
				IDSoggetto idSoggettoFruitore = new IDSoggetto(checkFr.getTipo(),checkFr.getNome());
				if( this.uddiLib.existsSoggetto(idSoggettoFruitore) == false){
					throw new DriverRegistroServiziException("Il fruitore ["+idSoggettoFruitore_string
					+"] non risulta gia' inserito nel registro dei servizi.");
				} */
			}
			// azioni-fruitori-connettori
			for(int i=0;i<servizio.sizeParametriAzioneList();i++){
				ServizioAzione checkAz = servizio.getParametriAzione(i);
				if( (checkAz.getNome()==null) || (checkAz.getConnettore()==null)){
					throw new DriverRegistroServiziException("Definizione di un fruitore senza nome o connettore");
				}
				// controllo connettore
				if(checkAz.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAz.getConnettore().getTipo())){
					boolean connettoreNonDefinito = false;
					if( checkAz.getConnettore().getTipo() == null ){
						connettoreNonDefinito = true;
					}
					if(connettoreNonDefinito){
						throw new DriverRegistroServiziException("Definizione del punto di accesso dell'azione "+checkAz.getNome()+" del servizio non corretta");
					}
				}
				for(int j=0;j<checkAz.sizeParametriFruitoreList();j++){
					ServizioAzioneFruitore checkAzFr = checkAz.getParametriFruitore(j);
					if( (checkAzFr.getNome()==null) || (checkAzFr.getNome()==null) || (checkAzFr.getConnettore()==null)){
						throw new DriverRegistroServiziException("Definizione di un fruitore di una azione senza nome/tipo o connettore");
					}
					// controllo connettore
					if(checkAzFr.getConnettore() !=null && !CostantiRegistroServizi.DISABILITATO.equals(checkAzFr.getConnettore().getTipo())){
						boolean connettoreNonDefinito = false;
						if( checkAzFr.getConnettore().getTipo() == null ){
							connettoreNonDefinito = true;
						}
						if(connettoreNonDefinito){
							throw new DriverRegistroServiziException("Definizione del punto di accesso del fruitore "+checkAzFr.getTipo()+checkAzFr.getNome()+" dell'azione "+checkAz.getNome()+" del servizio non corretta");
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
			String idSoggettoNEW_string = servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore();
			IDSoggetto idSoggettoNEW = new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore());
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
			IDServizio idServizioNEW = new IDServizio(idSoggettoNEW,servizio.getTipo(),servizio.getNome());
			String idServizioNEW_string = servizio.getTipo() + servizio.getNome();
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
				// Lo faccio cmq per non incasinare la pddConsole
				
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
		
		Servizio servizio = asps.getServizio();
		
		String idSoggetto_string = servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore();
		String idServizio_string = servizio.getTipo() + servizio.getNome();
				
		try {
			// Controllo id del soggetto 
			if(servizio.getTipoSoggettoErogatore()==null || servizio.getNomeSoggettoErogatore()==null){
				throw new DriverRegistroServiziException("Soggetto non definito");
			}
			// Controllo id del servizio da eliminare
			if(servizio.getTipo()==null || servizio.getNome()==null){
				throw new DriverRegistroServiziException("Servizio da eliminare non definito");
			}
			// Controllo pre-esistenza del soggetto
			IDSoggetto idSoggetto = new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore());
			if( this.uddiLib.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
				+"] non risulta gia' inserito nel registro dei servizi.");
			} 
			// Controllo pre-esistenza del servizio
			IDServizio idServizio = new IDServizio(idSoggetto,servizio.getTipo(),servizio.getNome());
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
					inEliminazione=serviziRegistrati[i].getServizio().getTipo()+"/"+serviziRegistrati[i].getServizio().getNome()
					+ " erogato da ["+serviziRegistrati[i].getServizio().getTipoSoggettoErogatore()+"/"+serviziRegistrati[i].getServizio().getNomeSoggettoErogatore();
					this.log.info("eliminazione servizio ["+inEliminazione+"] in corso...");
					IDServizio idS = new IDServizio(serviziRegistrati[i].getServizio().getTipoSoggettoErogatore(),
							serviziRegistrati[i].getServizio().getNomeSoggettoErogatore(),
							serviziRegistrati[i].getServizio().getTipo(),serviziRegistrati[i].getServizio().getNome());
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
