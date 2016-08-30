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




package org.openspcoop2.core.registry.driver.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDAccordoCooperazioneWithSoggetto;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazioneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordocooperazione.search.SearchFilterAccordoCooperazione;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComuneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecificaSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.SearchFilterAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.Servizio;
import org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominioSoap11Service;
import org.openspcoop2.core.registry.ws.client.portadominio.search.SearchFilterPortaDominio;
import org.openspcoop2.core.registry.ws.client.soggetto.search.SearchFilterSoggetto;
import org.openspcoop2.core.registry.ws.client.soggetto.search.SoggettoSoap11Service;
import org.openspcoop2.utils.LoggerWrapperFactory;



/**
 * Classe utilizzata per effettuare query ad un registro WS, riguardanti specifiche
 * proprieta' di servizi presenti all'interno del registro.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverRegistroServiziWS extends BeanUtilities
  implements IDriverRegistroServiziGet,IMonitoraggioRisorsa {



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** Stub per invocazione del WS */
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazioneSoap11Service accordoCooperazioneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComuneSoap11Service accordoServizioParteComuneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecificaSoap11Service accordoServizioParteSpecificaService;
	private org.openspcoop2.core.registry.ws.client.soggetto.search.SoggettoSoap11Service soggettoService;
	private org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominioSoap11Service pddService;
	
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazione accordoCooperazionePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComune accordoServizioParteComunePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecifica accordoServizioParteSpecificaPort;
	private org.openspcoop2.core.registry.ws.client.soggetto.search.Soggetto soggettoPort;
	private org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominio pddPort;

	
	/** Logger utilizzato per info. */
	private org.slf4j.Logger log = null;

	private  DatatypeFactory dataTypeFactory = null;



	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public DriverRegistroServiziWS(String prefixLocation,Logger alog){	
		this(prefixLocation,null,null,alog);
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public DriverRegistroServiziWS(String prefixLocation,String username,String password,Logger alog){	
		try{
			if(alog==null)
				this.log = LoggerWrapperFactory.getLogger(DriverRegistroServiziWS.class);
			else
				this.log = alog;
			
			// es. http://127.0.0.1:8080/openspcoop2Registry/
			if(prefixLocation==null)
				throw new Exception("Location is null");
			if(prefixLocation.endsWith("/")==false){
				prefixLocation = prefixLocation + "/";
			}
			
			this.dataTypeFactory = DatatypeFactory.newInstance();
			
			this.accordoCooperazioneService = new AccordoCooperazioneSoap11Service();
			this.accordoServizioParteComuneService = new AccordoServizioParteComuneSoap11Service();
			this.accordoServizioParteSpecificaService = new AccordoServizioParteSpecificaSoap11Service();
			this.soggettoService = new SoggettoSoap11Service();
			this.pddService = new PortaDominioSoap11Service();
			
			this.accordoCooperazionePort = this.accordoCooperazioneService.getAccordoCooperazionePortSoap11();
			this.accordoServizioParteComunePort = this.accordoServizioParteComuneService.getAccordoServizioParteComunePortSoap11();
			this.accordoServizioParteSpecificaPort = this.accordoServizioParteSpecificaService.getAccordoServizioParteSpecificaPortSoap11();
			this.soggettoPort = this.soggettoService.getSoggettoPortSoap11();
			this.pddPort = this.pddService.getPortaDominioPortSoap11();
			
			((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"AccordoCooperazione/Soap11");
			((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"AccordoServizioParteComune/Soap11");
			((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"AccordoServizioParteSpecifica/Soap11");
			((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"Soggetto/Soap11");
			((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"PortaDominio/Soap11");
			
			((BindingProvider)this.accordoCooperazionePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.soggettoPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.pddPort).getRequestContext().put("schema-validation-enabled", true);
			
			if(username !=null && password!=null){
				// to use Basic HTTP Authentication: 
				
				((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoCooperazionePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.soggettoPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.pddPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			}
			
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoCooperazione: " + this.accordoCooperazioneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteComune: " + this.accordoServizioParteComuneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteSpecifica: " + this.accordoServizioParteSpecificaService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Soggetto: " + this.soggettoService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. PortaDominio: " + this.pddService.getClass().getSimpleName());
			
			this.create = true;
		} catch(Exception e){
			this.log.error("Inizializzazione fallita: "+e.getMessage());
			this.create = false;
		}
	}
	
	
	
	// IS ALIVE
	
	@Override
	public void isAlive() throws CoreException {
		SearchFilterSoggetto filter = new SearchFilterSoggetto();
		filter.setNome("ISALIVE");
		try{
			this.soggettoPort.count(new SearchFilterSoggetto());
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	
	
	// PORTE DOMINIO
	
	@Override
	public PortaDominio getPortaDominio(String nomePdD)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			return this.pddPort.get(new IdPortaDominio(nomePdD));
		}catch(org.openspcoop2.core.registry.ws.client.portadominio.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterPortaDominio filter = new SearchFilterPortaDominio();
			if(filtroRicerca!=null){
				if(filtroRicerca.getTipo()!=null){
					// NOP
				}
				if(filtroRicerca.getNome()!=null){
					filter.setNome(filtroRicerca.getNome());
				}
				if(filtroRicerca.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicerca.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}				
			}
			List<IdPortaDominio> ids = this.pddPort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.portadominio.search.RegistryNotFoundException_Exception("La ricerca non ha trovato pdd");
			}
			List<String> idsOpenSPCoop = new ArrayList<String>();
			for (IdPortaDominio idPortaDominio : ids) {
				String idPortaDominioOpenSPCoop = idPortaDominio.getNome();
				idsOpenSPCoop.add(idPortaDominioOpenSPCoop);
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.portadominio.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// SOGGETTI
	
	@Override
	public Soggetto getSoggetto(IDSoggetto idSoggetto)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			return this.soggettoPort.get(new IdSoggetto(idSoggetto));
		}catch(org.openspcoop2.core.registry.ws.client.soggetto.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterSoggetto filter = new SearchFilterSoggetto();
			if(filtroRicerca!=null){
				if(filtroRicerca.getTipo()!=null){
					filter.setTipo(filtroRicerca.getTipo());
				}
				if(filtroRicerca.getNome()!=null){
					filter.setNome(filtroRicerca.getNome());
				}
				if(filtroRicerca.getNomePdd()!=null){
					filter.setPortaDominio(filtroRicerca.getNomePdd());
				}
				if(filtroRicerca.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicerca.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}				
			}
			List<IdSoggetto> ids = this.soggettoPort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.soggetto.search.RegistryNotFoundException_Exception("La ricerca non ha trovato soggetti");
			}
			List<IDSoggetto> idsOpenSPCoop = new ArrayList<IDSoggetto>();
			for (IdSoggetto idSoggetto : ids) {
				IDSoggetto idSoggettoOpenSPCoop = new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome());
				idsOpenSPCoop.add(idSoggettoOpenSPCoop);
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.soggetto.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// ACCORDI COOPERAZIONE
	
	@Override
	public AccordoCooperazione getAccordoCooperazione(
			IDAccordoCooperazione idAccordo)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			return this.accordoCooperazionePort.get(new IdAccordoCooperazione(idAccordo));
		}catch(org.openspcoop2.core.registry.ws.client.accordocooperazione.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<IDAccordoCooperazione> getAllIdAccordiCooperazione(
			FiltroRicercaAccordi filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterAccordoCooperazione filter = new SearchFilterAccordoCooperazione();
			if(filtroRicerca!=null){
				if(filtroRicerca.getVersione()!=null){
					filter.setVersione(filtroRicerca.getVersione());
				}
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					filter.setSoggettoReferente(new org.openspcoop2.core.registry.ws.client.accordocooperazione.search.IdSoggetto());
					if(filtroRicerca.getTipoSoggettoReferente()!=null){
						filter.getSoggettoReferente().setTipo(filtroRicerca.getTipoSoggettoReferente());
					}
					if(filtroRicerca.getNomeSoggettoReferente()!=null){
						filter.getSoggettoReferente().setNome(filtroRicerca.getNomeSoggettoReferente());
					}
				}
				if(filtroRicerca.getNomeAccordo()!=null){
					filter.setNome(filtroRicerca.getNomeAccordo());
				}
				if(filtroRicerca.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicerca.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}				
			}
			List<IdAccordoCooperazione> ids = this.accordoCooperazionePort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordocooperazione.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			List<IDAccordoCooperazione> idsOpenSPCoop = new ArrayList<IDAccordoCooperazione>();
			for (IdAccordoCooperazione idAccordoCooperazione : ids) {
				IDAccordoCooperazione idAccordoCooperazioneOpenSPCoop = 
						IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(idAccordoCooperazione.getNome(), idAccordoCooperazione.getVersione());
				IDAccordoCooperazioneWithSoggetto idAccordoCooperazioneOpenSPCoopWithSoggetto = new IDAccordoCooperazioneWithSoggetto(idAccordoCooperazioneOpenSPCoop);
				idAccordoCooperazioneOpenSPCoopWithSoggetto.
					setSoggettoReferente(new IDSoggetto(idAccordoCooperazione.getSoggettoReferente().getTipo(), idAccordoCooperazione.getSoggettoReferente().getNome()));
				idsOpenSPCoop.add(idAccordoCooperazioneOpenSPCoopWithSoggetto);
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordocooperazione.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	// ACCORDI SERVIZIO PARTE COMUNE
	
	@Override
	public AccordoServizioParteComune getAccordoServizioParteComune(
			IDAccordo idAccordo) throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			return this.accordoServizioParteComunePort.get(new IdAccordoServizioParteComune(idAccordo));
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteComune(
			FiltroRicercaAccordi filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterAccordoServizioParteComune filter = new SearchFilterAccordoServizioParteComune();
			if(filtroRicerca!=null){
				if(filtroRicerca.getVersione()!=null){
					filter.setVersione(filtroRicerca.getVersione());
				}
				if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
					filter.setSoggettoReferente(new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.IdSoggetto());
					if(filtroRicerca.getTipoSoggettoReferente()!=null){
						filter.getSoggettoReferente().setTipo(filtroRicerca.getTipoSoggettoReferente());
					}
					if(filtroRicerca.getNomeSoggettoReferente()!=null){
						filter.getSoggettoReferente().setNome(filtroRicerca.getNomeSoggettoReferente());
					}
				}
				if(filtroRicerca.getNomeAccordo()!=null){
					filter.setNome(filtroRicerca.getNomeAccordo());
				}
				if(filtroRicerca.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicerca.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}			
				if(filtroRicerca.getIdAccordoCooperazione()!=null){
					// NOP
				}
			}
			List<IdAccordoServizioParteComune> ids = this.accordoServizioParteComunePort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			List<IDAccordo> idsOpenSPCoop = new ArrayList<IDAccordo>();
			for (IdAccordoServizioParteComune idAccordoServizioParteComune : ids) {
				IDSoggetto idSoggetto = null;
				if(idAccordoServizioParteComune.getSoggettoReferente()!=null){
					idSoggetto = new IDSoggetto(idAccordoServizioParteComune.getSoggettoReferente().getTipo(), idAccordoServizioParteComune.getSoggettoReferente().getNome());
				}
				IDAccordo idAccordoServizioParteComuneOpenSPCoop = 
						IDAccordoFactory.getInstance().getIDAccordoFromValues(idAccordoServizioParteComune.getNome(), idSoggetto, idAccordoServizioParteComune.getVersione());
				idsOpenSPCoop.add(idAccordoServizioParteComuneOpenSPCoop);
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	// ACCORDI SERVIZIO PARTE SPECIFICA
	
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDServizio idServizio) throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterAccordoServizioParteSpecifica filter = new SearchFilterAccordoServizioParteSpecifica();
			filter.setServizio(new Servizio());
			filter.getServizio().setTipo(idServizio.getTipoServizio());
			filter.getServizio().setNome(idServizio.getServizio());
			if(idServizio.getSoggettoErogatore()!=null){
				filter.getServizio().setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
				filter.getServizio().setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
			}
			return this.accordoServizioParteSpecificaPort.find(filter);
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(
			IDAccordo idAccordo) throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			return this.accordoServizioParteSpecificaPort.get(new IdAccordoServizioParteSpecifica(idAccordo));
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(
			IDSoggetto idSoggetto, IDAccordo idAccordoServizioParteComune)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterAccordoServizioParteSpecifica filter = new SearchFilterAccordoServizioParteSpecifica();
			filter.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteComune));
			if(idSoggetto!=null){
				filter.setServizio(new Servizio());
				filter.getServizio().setTipoSoggettoErogatore(idSoggetto.getTipo());
				filter.getServizio().setNomeSoggettoErogatore(idSoggetto.getNome());
				filter.getServizio().setTipologiaServizio(TipologiaServizio.CORRELATO);
			}
			return this.accordoServizioParteSpecificaPort.find(filter);
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(
			FiltroRicercaServizi filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			SearchFilterAccordoServizioParteSpecifica filter = new SearchFilterAccordoServizioParteSpecifica();
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdAccordo()!=null){
					filter.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(filtroRicerca.getIdAccordo()));
				}
				if(filtroRicerca.getNome()!=null || filtroRicerca.getTipo()!=null ||
						filtroRicerca.getNomeSoggettoErogatore()!=null ||
						filtroRicerca.getTipoSoggettoErogatore()!=null ||
						filtroRicerca.getTipologiaServizio()!=null){
					filter.setServizio(new Servizio());
					filter.getServizio().setTipo(filtroRicerca.getTipo());
					filter.getServizio().setNome(filtroRicerca.getNome());
					filter.getServizio().setTipoSoggettoErogatore(filtroRicerca.getTipoSoggettoErogatore());
					filter.getServizio().setNomeSoggettoErogatore(filtroRicerca.getNomeSoggettoErogatore());
					filter.getServizio().setTipologiaServizio(TipologiaServizio.toEnumConstant(filtroRicerca.getTipologiaServizio()));
				}
				if(filtroRicerca.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicerca.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicerca.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}	
			}
			List<IdAccordoServizioParteSpecifica> ids = this.accordoServizioParteSpecificaPort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			List<IDAccordo> idsOpenSPCoop = new ArrayList<IDAccordo>();
			for (IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica : ids) {
				IDSoggetto idSoggetto = null;
				if(idAccordoServizioParteSpecifica.getSoggettoErogatore()!=null){
					idSoggetto = new IDSoggetto(idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo(), idAccordoServizioParteSpecifica.getSoggettoErogatore().getNome());
				}
				IDAccordo idAccordoServizioParteSpecificaOpenSPCoop = 
						IDAccordoFactory.getInstance().getIDAccordoFromValues(idAccordoServizioParteSpecifica.getNome(), idSoggetto, idAccordoServizioParteSpecifica.getVersione());
				idsOpenSPCoop.add(idAccordoServizioParteSpecificaOpenSPCoop);
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	@Override
	public List<IDServizio> getAllIdServizi(FiltroRicercaServizi filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		List<IDAccordo> listIdsServizi = this.getAllIdAccordiServizioParteSpecifica(filtroRicerca);
		List<IDServizio> listIdsTradotti = new ArrayList<IDServizio>();
		for (IDAccordo idAccordo : listIdsServizi) {
			try{
				AccordoServizioParteSpecifica asps = this.accordoServizioParteSpecificaPort.get(new IdAccordoServizioParteSpecifica(idAccordo));
				IDServizio idServizio = new IDServizio(asps.getServizio().getTipoSoggettoErogatore(), asps.getServizio().getNomeSoggettoErogatore(), 
						asps.getServizio().getTipo(), asps.getServizio().getNome());
				listIdsTradotti.add(idServizio);
			}catch(Exception e){
				throw new DriverRegistroServiziException("Accordo ["+idAccordo.toString()+"] non presente dopo aver estratto l'id?");
			}
		}
		return listIdsTradotti;
	}


}

