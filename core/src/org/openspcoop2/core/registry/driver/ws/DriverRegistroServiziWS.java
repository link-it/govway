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




package org.openspcoop2.core.registry.driver.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;

import org.openspcoop2.core.commons.CoreException;
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
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.IdAccordoCooperazione;
import org.openspcoop2.core.registry.IdAccordoServizioParteComune;
import org.openspcoop2.core.registry.IdAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.IdGruppo;
import org.openspcoop2.core.registry.IdPortaDominio;
import org.openspcoop2.core.registry.IdRuolo;
import org.openspcoop2.core.registry.IdScope;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
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
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ProtocolPropertiesUtilities;
import org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazioneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordocooperazione.search.SearchFilterAccordoCooperazione;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComuneSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.SearchFilterAccordoServizioParteComune;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecificaSoap11Service;
import org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.SearchFilterAccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominioSoap11Service;
import org.openspcoop2.core.registry.ws.client.portadominio.search.SearchFilterPortaDominio;
import org.openspcoop2.core.registry.ws.client.gruppo.search.GruppoSoap11Service;
import org.openspcoop2.core.registry.ws.client.gruppo.search.SearchFilterGruppo;
import org.openspcoop2.core.registry.ws.client.ruolo.search.RuoloSoap11Service;
import org.openspcoop2.core.registry.ws.client.ruolo.search.SearchFilterRuolo;
import org.openspcoop2.core.registry.ws.client.scope.search.ScopeSoap11Service;
import org.openspcoop2.core.registry.ws.client.scope.search.SearchFilterScope;
import org.openspcoop2.core.registry.ws.client.soggetto.search.SearchFilterSoggetto;
import org.openspcoop2.core.registry.ws.client.soggetto.search.SoggettoSoap11Service;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.slf4j.Logger;



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
	public boolean isCreate() {
		return this.create;
	}
	
	/** Stub per invocazione del WS */
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazioneSoap11Service accordoCooperazioneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComuneSoap11Service accordoServizioParteComuneService;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecificaSoap11Service accordoServizioParteSpecificaService;
	private org.openspcoop2.core.registry.ws.client.soggetto.search.SoggettoSoap11Service soggettoService;
	private org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominioSoap11Service pddService;
	private org.openspcoop2.core.registry.ws.client.gruppo.search.GruppoSoap11Service gruppoService;
	private org.openspcoop2.core.registry.ws.client.ruolo.search.RuoloSoap11Service ruoloService;
	private org.openspcoop2.core.registry.ws.client.scope.search.ScopeSoap11Service scopeService;
	
	private org.openspcoop2.core.registry.ws.client.accordocooperazione.search.AccordoCooperazione accordoCooperazionePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.AccordoServizioParteComune accordoServizioParteComunePort;
	private org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.AccordoServizioParteSpecifica accordoServizioParteSpecificaPort;
	private org.openspcoop2.core.registry.ws.client.soggetto.search.Soggetto soggettoPort;
	private org.openspcoop2.core.registry.ws.client.portadominio.search.PortaDominio pddPort;
	private org.openspcoop2.core.registry.ws.client.gruppo.search.Gruppo gruppoPort;
	private org.openspcoop2.core.registry.ws.client.ruolo.search.Ruolo ruoloPort;
	private org.openspcoop2.core.registry.ws.client.scope.search.Scope scopePort;

	
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
			this.gruppoService = new GruppoSoap11Service();
			this.ruoloService = new RuoloSoap11Service();
			this.scopeService = new ScopeSoap11Service();
			
			this.accordoCooperazionePort = this.accordoCooperazioneService.getAccordoCooperazionePortSoap11();
			this.accordoServizioParteComunePort = this.accordoServizioParteComuneService.getAccordoServizioParteComunePortSoap11();
			this.accordoServizioParteSpecificaPort = this.accordoServizioParteSpecificaService.getAccordoServizioParteSpecificaPortSoap11();
			this.soggettoPort = this.soggettoService.getSoggettoPortSoap11();
			this.pddPort = this.pddService.getPortaDominioPortSoap11();
			this.gruppoPort = this.gruppoService.getGruppoPortSoap11();
			this.ruoloPort = this.ruoloService.getRuoloPortSoap11();
			this.scopePort = this.scopeService.getScopePortSoap11();
			
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
			((BindingProvider)this.gruppoPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"Gruppo/Soap11");
			((BindingProvider)this.ruoloPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"Ruolo/Soap11");
			((BindingProvider)this.scopePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
					prefixLocation+"Scope/Soap11");
			
			((BindingProvider)this.accordoCooperazionePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteComunePort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.accordoServizioParteSpecificaPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.soggettoPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.pddPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.gruppoPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.ruoloPort).getRequestContext().put("schema-validation-enabled", true);
			((BindingProvider)this.scopePort).getRequestContext().put("schema-validation-enabled", true);
			
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
				
				((BindingProvider)this.gruppoPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.gruppoPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.ruoloPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.ruoloPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
				
				((BindingProvider)this.scopePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
				((BindingProvider)this.scopePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
			}
			
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoCooperazione: " + this.accordoCooperazioneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteComune: " + this.accordoServizioParteComuneService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. AccordoServizioParteSpecifica: " + this.accordoServizioParteSpecificaService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Soggetto: " + this.soggettoService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. PortaDominio: " + this.pddService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Gruppo: " + this.gruppoService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Ruolo: " + this.ruoloService.getClass().getSimpleName());
			this.log.debug("GestoreRegistro: Inizializzato WebService. Scope: " + this.scopeService.getClass().getSimpleName());
			
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
	
	
	
	// GRUPPI

	@Override
	public Gruppo getGruppo(
			IDGruppo idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			return this.gruppoPort.get(new IdGruppo(idGruppo));
		}catch(org.openspcoop2.core.registry.ws.client.gruppo.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}


	@Override
	public List<IDGruppo> getAllIdGruppi(
			FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			SearchFilterGruppo filter = new SearchFilterGruppo();
			if(filtroRicerca!=null){
				if(filtroRicerca.getNome()!=null){
					filter.setNome(filtroRicerca.getNome());
				}
				if(filtroRicerca.getServiceBinding()!=null){
					filter.setServiceBinding(filtroRicerca.getServiceBinding());
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
			List<IdGruppo> ids = this.gruppoPort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.gruppo.search.RegistryNotFoundException_Exception("La ricerca non ha trovato gruppi");
			}
			List<IDGruppo> idsOpenSPCoop = new ArrayList<IDGruppo>();
			for (IdGruppo idGruppo : ids) {
				String idGruppoOpenSPCoop = idGruppo.getNome();
				idsOpenSPCoop.add(new IDGruppo(idGruppoOpenSPCoop));
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.gruppo.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	// RUOLI

	@Override
	public Ruolo getRuolo(
			IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			return this.ruoloPort.get(new IdRuolo(idRuolo));
		}catch(org.openspcoop2.core.registry.ws.client.ruolo.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}


	@Override
	public List<IDRuolo> getAllIdRuoli(
			FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			SearchFilterRuolo filter = new SearchFilterRuolo();
			if(filtroRicerca!=null){
				if(filtroRicerca.getNome()!=null){
					filter.setNome(filtroRicerca.getNome());
				}
				if(filtroRicerca.getTipologia()!=null){
					filter.setTipologia(filtroRicerca.getTipologia());
				}
				if(filtroRicerca.getContesto()!=null){
					filter.setContestoUtilizzo(filtroRicerca.getContesto());
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
			List<IdRuolo> ids = this.ruoloPort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.ruolo.search.RegistryNotFoundException_Exception("La ricerca non ha trovato ruoli");
			}
			List<IDRuolo> idsOpenSPCoop = new ArrayList<IDRuolo>();
			for (IdRuolo idRuolo : ids) {
				String idRuoloOpenSPCoop = idRuolo.getNome();
				idsOpenSPCoop.add(new IDRuolo(idRuoloOpenSPCoop));
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.ruolo.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}
	
	
	
	
	// SCOPE

	@Override
	public Scope getScope(
			IDScope idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			return this.scopePort.get(new IdScope(idScope));
		}catch(org.openspcoop2.core.registry.ws.client.scope.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}


	@Override
	public List<IDScope> getAllIdScope(
			FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			SearchFilterScope filter = new SearchFilterScope();
			if(filtroRicerca!=null){
				if(filtroRicerca.getNome()!=null){
					filter.setNome(filtroRicerca.getNome());
				}
				if(filtroRicerca.getTipologia()!=null){
					filter.setTipologia(filtroRicerca.getTipologia());
				}
				if(filtroRicerca.getContesto()!=null){
					filter.setContestoUtilizzo(filtroRicerca.getContesto());
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
			List<IdScope> ids = this.scopePort.findAllIds(filter);
			if(ids==null || ids.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.scope.search.RegistryNotFoundException_Exception("La ricerca non ha trovato scope");
			}
			List<IDScope> idsOpenSPCoop = new ArrayList<IDScope>();
			for (IdScope idScope : ids) {
				String idScopeOpenSPCoop = idScope.getNome();
				idsOpenSPCoop.add(new IDScope(idScopeOpenSPCoop));
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.scope.search.RegistryNotFoundException_Exception e){
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
	
	@Override
	public List<IDSoggetto> getAllIdSoggetti(FiltroRicercaSoggetti filtroRicerca)
			throws DriverRegistroServiziException,
			DriverRegistroServiziNotFound {
		try{
			@SuppressWarnings("unused")
			boolean testInChiaro = false;
			@SuppressWarnings("unused")
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
				// Filtro By Ruolo
				if(filtroRicerca.getIdRuolo()!=null){
					throw new DriverRegistroServiziException("Not Implemented");
				}
				// Filtro By Credenziali
				if(filtroRicerca.getCredenzialiSoggetto()!=null){
					org.openspcoop2.core.registry.ws.client.soggetto.search.CredenzialiSoggetto credenziali = new 
							org.openspcoop2.core.registry.ws.client.soggetto.search.CredenzialiSoggetto();
					if(filtroRicerca.getCredenzialiSoggetto().getTipo()!=null){
						credenziali.setTipo(filtroRicerca.getCredenzialiSoggetto().getTipo());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getUser()!=null){
						credenziali.setUser(filtroRicerca.getCredenzialiSoggetto().getUser());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getPassword()!=null){
						credenziali.setPassword(filtroRicerca.getCredenzialiSoggetto().getPassword());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getSubject()!=null){
						credenziali.setSubject(filtroRicerca.getCredenzialiSoggetto().getSubject());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getIssuer()!=null){
						credenziali.setSubject(filtroRicerca.getCredenzialiSoggetto().getIssuer());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getCertificate()!=null){
						credenziali.setCertificate(filtroRicerca.getCredenzialiSoggetto().getCertificate());
						credenziali.setCertificateStrictVerification(filtroRicerca.getCredenzialiSoggetto().isCertificateStrictVerification());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getCnSubject()!=null){
						credenziali.setCnSubject(filtroRicerca.getCredenzialiSoggetto().getCnSubject());
					}
					if(filtroRicerca.getCredenzialiSoggetto().getCnIssuer()!=null){
						credenziali.setCnIssuer(filtroRicerca.getCredenzialiSoggetto().getCnIssuer());
					}
					filter.setCredenziali(credenziali);
				}
			}
			List<IDSoggetto> idsOpenSPCoop = new ArrayList<IDSoggetto>();
			if(filtroRicerca.getProtocolProperties()==null || filtroRicerca.getProtocolProperties().size()<=0){
				List<IdSoggetto> ids = this.soggettoPort.findAllIds(filter);
				if(ids==null || ids.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.soggetto.search.RegistryNotFoundException_Exception("La ricerca non ha trovato soggetti");
				}
				for (IdSoggetto idSoggetto : ids) {
					IDSoggetto idSoggettoOpenSPCoop = new IDSoggetto(idSoggetto.getTipo(), idSoggetto.getNome());
					idsOpenSPCoop.add(idSoggettoOpenSPCoop);
				}
			}
			else{
				List<Soggetto> ss = this.soggettoPort.findAll(filter);
				if(ss==null || ss.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.soggetto.search.RegistryNotFoundException_Exception("La ricerca non ha trovato soggetti");
				}
				for (Soggetto soggetto : ss) {
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(soggetto, filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					IDSoggetto idSoggettoOpenSPCoop = new IDSoggetto(soggetto.getTipo(), soggetto.getNome());
					idsOpenSPCoop.add(idSoggettoOpenSPCoop);
				}
				if(idsOpenSPCoop==null || idsOpenSPCoop.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
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
					filter.setVersione(Long.valueOf(filtroRicerca.getVersione()));
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
			List<IDAccordoCooperazione> idsOpenSPCoop = new ArrayList<IDAccordoCooperazione>();
			if(filtroRicerca.getProtocolPropertiesAccordo()==null || filtroRicerca.getProtocolPropertiesAccordo().size()<=0){
				List<IdAccordoCooperazione> ids = this.accordoCooperazionePort.findAllIds(filter);
				if(ids==null || ids.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordocooperazione.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (IdAccordoCooperazione idAccordoCooperazione : ids) {
					IDAccordoCooperazione idAccordoCooperazioneOpenSPCoop = 
							IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(idAccordoCooperazione.getNome(), 
									new IDSoggetto(idAccordoCooperazione.getSoggettoReferente().getTipo(), idAccordoCooperazione.getSoggettoReferente().getNome()),
									idAccordoCooperazione.getVersione());
					idsOpenSPCoop.add(idAccordoCooperazioneOpenSPCoop);
				}
			}
			else{
				List<AccordoCooperazione> accordi = this.accordoCooperazionePort.findAll(filter);
				if(accordi==null || accordi.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordocooperazione.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (AccordoCooperazione accordoCooperazione : accordi) {
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(accordoCooperazione, filtroRicerca.getProtocolPropertiesAccordo())==false){
						continue;
					}
					IDAccordoCooperazione idAccordoCooperazioneOpenSPCoop = 
							IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromValues(accordoCooperazione.getNome(), 
									new IDSoggetto(accordoCooperazione.getSoggettoReferente().getTipo(), accordoCooperazione.getSoggettoReferente().getNome()),
									accordoCooperazione.getVersione());
					idsOpenSPCoop.add(idAccordoCooperazioneOpenSPCoop);
				}
				if(idsOpenSPCoop==null || idsOpenSPCoop.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
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
					filter.setVersione(Long.valueOf(filtroRicerca.getVersione()));
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
				if(filtroRicerca.getServiceBinding()!=null){
					filter.setServiceBinding(filtroRicerca.getServiceBinding());
				}
			}
			List<IDAccordo> idsOpenSPCoop = new ArrayList<IDAccordo>();
			if(filtroRicerca.getProtocolPropertiesAccordo()==null || filtroRicerca.getProtocolPropertiesAccordo().size()<=0){
				List<IdAccordoServizioParteComune> ids = this.accordoServizioParteComunePort.findAllIds(filter);
				if(ids==null || ids.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (IdAccordoServizioParteComune idAccordoServizioParteComune : ids) {
					IDSoggetto idSoggetto = null;
					if(idAccordoServizioParteComune.getSoggettoReferente()!=null){
						idSoggetto = new IDSoggetto(idAccordoServizioParteComune.getSoggettoReferente().getTipo(), idAccordoServizioParteComune.getSoggettoReferente().getNome());
					}
					IDAccordo idAccordoServizioParteComuneOpenSPCoop = 
							IDAccordoFactory.getInstance().getIDAccordoFromValues(idAccordoServizioParteComune.getNome(), idSoggetto, idAccordoServizioParteComune.getVersione());
					idsOpenSPCoop.add(idAccordoServizioParteComuneOpenSPCoop);
				}
			}
			else{
				List<AccordoServizioParteComune> accordi = this.accordoServizioParteComunePort.findAll(filter);
				if(accordi==null || accordi.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (AccordoServizioParteComune accordoServizioParteComune : accordi) {
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(accordoServizioParteComune, filtroRicerca.getProtocolPropertiesAccordo())==false){
						continue;
					}
					IDSoggetto idSoggetto = null;
					if(accordoServizioParteComune.getSoggettoReferente()!=null){
						idSoggetto = new IDSoggetto(accordoServizioParteComune.getSoggettoReferente().getTipo(), accordoServizioParteComune.getSoggettoReferente().getNome());
					}
					IDAccordo idAccordoServizioParteComuneOpenSPCoop = 
							IDAccordoFactory.getInstance().getIDAccordoFromValues(accordoServizioParteComune.getNome(), idSoggetto, accordoServizioParteComune.getVersione());
					idsOpenSPCoop.add(idAccordoServizioParteComuneOpenSPCoop);
				}
				if(idsOpenSPCoop==null || idsOpenSPCoop.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
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
			SearchFilterAccordoServizioParteComune filter = new SearchFilterAccordoServizioParteComune();
			if(filtroRicercaBase!=null){
				if(filtroRicercaBase.getVersione()!=null){
					filter.setVersione(Long.valueOf(filtroRicercaBase.getVersione()));
				}
				if(filtroRicercaBase.getTipoSoggettoReferente()!=null || filtroRicercaBase.getNomeSoggettoReferente()!=null){
					filter.setSoggettoReferente(new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.IdSoggetto());
					if(filtroRicercaBase.getTipoSoggettoReferente()!=null){
						filter.getSoggettoReferente().setTipo(filtroRicercaBase.getTipoSoggettoReferente());
					}
					if(filtroRicercaBase.getNomeSoggettoReferente()!=null){
						filter.getSoggettoReferente().setNome(filtroRicercaBase.getNomeSoggettoReferente());
					}
				}
				if(filtroRicercaBase.getNomeAccordo()!=null){
					filter.setNome(filtroRicercaBase.getNomeAccordo());
				}
				if(filtroRicercaBase.getMaxDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicercaBase.getMaxDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}
				if(filtroRicercaBase.getMinDate()!=null){
					GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
					cal.setTime(filtroRicercaBase.getMinDate());
					filter.setOraRegistrazioneMax(this.dataTypeFactory.newXMLGregorianCalendar(cal));
				}			
				if(filtroRicercaBase.getIdAccordoCooperazione()!=null){
					// NOP
				}
				if(filtroRicercaBase.getServiceBinding()!=null){
					filter.setServiceBinding(filtroRicercaBase.getServiceBinding());
				}
			}
			
			List<AccordoServizioParteComune> accordi = this.accordoServizioParteComunePort.findAll(filter);
			if(accordi==null || accordi.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			for (AccordoServizioParteComune as : accordi) {
				
				// ProtocolProperties
				if(ProtocolPropertiesUtilities.isMatch(as, filtroRicercaBase.getProtocolPropertiesAccordo())==false){
					continue;
				}
				
				IDSoggetto idSoggetto = null;
				if(as.getSoggettoReferente()!=null){
					idSoggetto = new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome());
				}
				IDAccordo idAccordo = 
						IDAccordoFactory.getInstance().getIDAccordoFromValues(as.getNome(), idSoggetto, as.getVersione());
				
				
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
				
			}
			
			if(listReturn==null || listReturn.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartecomune.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			
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
			filter.setTipo(idServizio.getTipo());
			filter.setNome(idServizio.getNome());
			filter.setVersione(Long.valueOf(idServizio.getVersione()));
			if(idServizio.getSoggettoErogatore()!=null){
				filter.setTipoSoggettoErogatore(idServizio.getSoggettoErogatore().getTipo());
				filter.setNomeSoggettoErogatore(idServizio.getSoggettoErogatore().getNome());
			}
			return this.accordoServizioParteSpecificaPort.find(filter);
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
				filter.setTipoSoggettoErogatore(idSoggetto.getTipo());
				filter.setNomeSoggettoErogatore(idSoggetto.getNome());
				filter.setTipologiaServizio(TipologiaServizio.CORRELATO);
			}
			return this.accordoServizioParteSpecificaPort.find(filter);
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
		try{
			SearchFilterAccordoServizioParteSpecifica filter = new SearchFilterAccordoServizioParteSpecifica();
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
					filter.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(filtroRicerca.getIdAccordoServizioParteComune()));
				}
				if(filtroRicerca.getNome()!=null || filtroRicerca.getTipo()!=null || filtroRicerca.getVersione()!=null ||
						filtroRicerca.getPortType()!=null ||
						filtroRicerca.getNomeSoggettoErogatore()!=null ||
						filtroRicerca.getTipoSoggettoErogatore()!=null ||
						filtroRicerca.getTipologia()!=null){
					filter.setTipo(filtroRicerca.getTipo());
					filter.setNome(filtroRicerca.getNome());
					filter.setVersione(Long.valueOf(filtroRicerca.getVersione()));
					filter.setPortType(filtroRicerca.getPortType());
					filter.setTipoSoggettoErogatore(filtroRicerca.getTipoSoggettoErogatore());
					filter.setNomeSoggettoErogatore(filtroRicerca.getNomeSoggettoErogatore());
					if(filtroRicerca.getTipologia()!=null){
						filter.setTipologiaServizio(TipologiaServizio.toEnumConstant(filtroRicerca.getTipologia().getValue()));
					}
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
				// Filtro By Tipo e/o Nome Soggetto Fruitore
				if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
					throw new DriverRegistroServiziException("Not Implemented");
				}
			}
			List<IDServizio> idsOpenSPCoop = new ArrayList<IDServizio>();
			if(filtroRicerca.getProtocolProperties()==null || filtroRicerca.getProtocolProperties().size()<=0){
				List<IdAccordoServizioParteSpecifica> ids = this.accordoServizioParteSpecificaPort.findAllIds(filter);
				if(ids==null || ids.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (IdAccordoServizioParteSpecifica idAccordoServizioParteSpecifica : ids) {
					AccordoServizioParteSpecifica asps = this.accordoServizioParteSpecificaPort.get(idAccordoServizioParteSpecifica);
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
							new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), 
							asps.getVersione()); 
					idsOpenSPCoop.add(idServizio);
				}
			}
			else{
				List<AccordoServizioParteSpecifica> accordi = this.accordoServizioParteSpecificaPort.findAll(filter);
				if(accordi==null || accordi.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
				for (AccordoServizioParteSpecifica asps : accordi) {
					
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(asps, filtroRicerca.getProtocolProperties())==false){
						continue;
					}
					
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
							new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), 
							asps.getVersione()); 
					idsOpenSPCoop.add(idServizio);
				}
				if(idsOpenSPCoop==null || idsOpenSPCoop.size()<=0){
					throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
				}
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
	}

	@Override
	public List<IDFruizione> getAllIdFruizioniServizio(
			FiltroRicercaFruizioniServizio filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		try{
			SearchFilterAccordoServizioParteSpecifica filter = new SearchFilterAccordoServizioParteSpecifica();
			if(filtroRicerca!=null){
				if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
					filter.setAccordoServizioParteComune(IDAccordoFactory.getInstance().getUriFromIDAccordo(filtroRicerca.getIdAccordoServizioParteComune()));
				}
				if(filtroRicerca.getNome()!=null || filtroRicerca.getTipo()!=null || filtroRicerca.getVersione()!=null ||
						filtroRicerca.getPortType()!=null ||
						filtroRicerca.getNomeSoggettoErogatore()!=null ||
						filtroRicerca.getTipoSoggettoErogatore()!=null ||
						filtroRicerca.getTipologia()!=null){
					filter.setTipo(filtroRicerca.getTipo());
					filter.setNome(filtroRicerca.getNome());
					filter.setVersione(Long.valueOf(filtroRicerca.getVersione()));
					filter.setPortType(filtroRicerca.getPortType());
					filter.setTipoSoggettoErogatore(filtroRicerca.getTipoSoggettoErogatore());
					filter.setNomeSoggettoErogatore(filtroRicerca.getNomeSoggettoErogatore());
					if(filtroRicerca.getTipologia()!=null){
						filter.setTipologiaServizio(TipologiaServizio.toEnumConstant(filtroRicerca.getTipologia().getValue()));
					}
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
			List<IDFruizione> idsOpenSPCoop = new ArrayList<IDFruizione>();
			
			List<AccordoServizioParteSpecifica> accordi = this.accordoServizioParteSpecificaPort.findAll(filter);
			if(accordi==null || accordi.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			for (AccordoServizioParteSpecifica asps : accordi) {
				
				// ProtocolProperties
				if(ProtocolPropertiesUtilities.isMatch(asps, filtroRicerca.getProtocolProperties())==false){
					continue;
				}
				
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(asps.getTipo(), asps.getNome(), 
						new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), 
						asps.getVersione()); 
				
				for (Fruitore fruitore : asps.getFruitoreList()) {
					
					// Tipo
					if(filtroRicerca.getTipoSoggettoFruitore()!=null){
						if(fruitore.getTipo().equals(filtroRicerca.getTipoSoggettoFruitore()) == false){
							continue;
						}
					}
					// Nome
					if(filtroRicerca.getNomeSoggettoFruitore()!=null){
						if(fruitore.getNome().equals(filtroRicerca.getNomeSoggettoFruitore()) == false){
							continue;
						}
					}
					// ProtocolProperties
					if(ProtocolPropertiesUtilities.isMatch(fruitore, filtroRicerca.getProtocolPropertiesFruizione())==false){
						continue;
					}
					
					IDFruizione idFruizione = new IDFruizione();
					idFruizione.setIdServizio(idServizio);
					idFruizione.setIdFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
					idsOpenSPCoop.add(idFruizione);
				}

			}
			if(idsOpenSPCoop==null || idsOpenSPCoop.size()<=0){
				throw new org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception("La ricerca non ha trovato accordi");
			}
			
			return idsOpenSPCoop;
			
		}catch(org.openspcoop2.core.registry.ws.client.accordoserviziopartespecifica.search.RegistryNotFoundException_Exception e){
			throw new DriverRegistroServiziNotFound(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		
	}


}

