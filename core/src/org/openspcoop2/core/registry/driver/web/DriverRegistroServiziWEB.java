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




package org.openspcoop2.core.registry.driver.web;

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
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.CostantiXMLRepository;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.BeanUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaAccordi;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziCRUD;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.core.registry.ServizioAzione;
import org.openspcoop2.core.registry.ServizioAzioneFruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.HttpUtilities;

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
				this.validatoreRegistro = new ValidatoreXSD(this.log,DriverRegistroServiziWEB.class.getResourceAsStream("/registroServizi.xsd"));
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

			// parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeAccordoCooperazioneList()>0)
				accRichiesto = rs.getAccordoCooperazione(0);
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
			Vector<IDAccordoCooperazione> idAccordi = new Vector<IDAccordoCooperazione>();
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
				}
				idAccordi.add(this.idAccordoCooperazioneFactory.getIDAccordoFromValues(acList[i].getNome(),acList[i].getVersione()));
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
			throw new DriverRegistroServiziException("[AccordoServizioParteComune] Parametro Non Valido");
		if( idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[AccordoServizioParteComune] Nome accordo servizio non fornito");


		org.openspcoop2.core.registry.AccordoServizioParteComune accRichiesto = null;

		// Ottengo URL XML associato all'accordo
		String fileName = this.generatoreXML.mappingIDAccordoToFileName(idAccordo);
		String urlXMLAccordoServizio = this.urlPrefix + CostantiXMLRepository.ACCORDI_DI_SERVIZIO + CostantiRegistroServizi.URL_SEPARATOR + fileName + ".xml";	  

		// Ottengo oggetto Soggetto
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

			// parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeAccordoServizioParteComuneList()>0)
				accRichiesto = rs.getAccordoServizioParteComune(0);
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
				throw new DriverRegistroServiziException("[AccordoServizioParteComune] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(accRichiesto==null)
			throw new DriverRegistroServiziNotFound("[AccordoServizioParteComune] Accordo di Servizio non trovato.");

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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdAccordiServizioParteComune] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoServizioParteComune[] asList = this.generatoreXML.getAccordiServizioParteComune(); 
			if(asList==null)
				throw new DriverRegistroServiziNotFound("Accordi non esistenti nel repository WEB");

			// Esamina degli accordi
			Vector<IDAccordo> idAccordi = new Vector<IDAccordo>();
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

				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(asList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizio](FiltroByMinDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(asList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(asList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizio](FiltroByMaxDate) Accordo di servizio ["+asURI+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(asList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Nome
					if(filtroRicerca.getNomeAccordo()!=null){
						if(asList[i].getNome().equals(filtroRicerca.getNomeAccordo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getVersione()!=null){
						if(asList[i].getVersione().equals(filtroRicerca.getVersione()) == false){
							continue;
						}
					}
					if(filtroRicerca.getTipoSoggettoReferente()!=null || filtroRicerca.getNomeSoggettoReferente()!=null){
						if(asList[i].getSoggettoReferente()==null)
							continue;
						if(filtroRicerca.getTipoSoggettoReferente()!=null){
							if(asList[i].getSoggettoReferente().getTipo().equals(filtroRicerca.getTipoSoggettoReferente()) == false){
								continue;
							}
						}
						if(filtroRicerca.getNomeSoggettoReferente()!=null){
							if(asList[i].getSoggettoReferente().getNome().equals(filtroRicerca.getNomeSoggettoReferente()) == false){
								continue;
							}
						}
					}
					
					if(filtroRicerca.getIdAccordoCooperazione()!=null &&
							(filtroRicerca.getIdAccordoCooperazione().getNome()!=null || 
							filtroRicerca.getIdAccordoCooperazione().getVersione()!=null) ){
						if(asList[i].getServizioComposto()==null){
							continue;
						}
						IDAccordoCooperazione idAC = this.idAccordoCooperazioneFactory.getIDAccordoFromUri(asList[i].getServizioComposto().getAccordoCooperazione());
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
					
				}
				idAccordi.add(this.idAccordoFactory.getIDAccordoFromValues(asList[i].getNome(),BeanUtilities.getSoggettoReferenteID(asList[i].getSoggettoReferente()),asList[i].getVersione()));
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

		org.openspcoop2.core.registry.PortaDominio pddRichiesta = null;

		// Ottengo URL XML associata alla porta di dominio
		String urlXMLPortaDominio = this.urlPrefix + CostantiXMLRepository.PORTE_DI_DOMINIO + CostantiRegistroServizi.URL_SEPARATOR + nomePdD + ".xml";	  

		// Ottengo oggetto Soggetto
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

			// parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizePortaDominioList()>0)
				pddRichiesta = rs.getPortaDominio(0);
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
			Vector<String> nomiPdd = new Vector<String>();
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

			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeSoggettoList()>0)
				soggRichiesto = rs.getSoggetto(0);
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
				throw new DriverRegistroServiziException("[getSoggetto] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(soggRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non trovato.");

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
			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdSoggettiRegistro] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.Soggetto[] ssList = this.generatoreXML.getSoggetti(); 
			if(ssList==null)
				throw new DriverRegistroServiziNotFound("Soggetti non esistenti nel repository WEB");

			// Esamina dei soggetti
			Vector<IDSoggetto> idSoggetti = new Vector<IDSoggetto>();
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
		String servizio = idService.getServizio();
		String tipoServizio = idService.getTipoServizio();
		if(servizio == null || tipoServizio == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Servizio) Non Validi");
		String tipoSogg = idService.getSoggettoErogatore().getTipo();
		String nomeSogg = idService.getSoggettoErogatore().getNome();
		if(tipoSogg == null || nomeSogg == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Parametri (Soggetto) Non Validi");


		org.openspcoop2.core.registry.AccordoServizioParteSpecifica servRichiesto = null;
		// get URL XML Servizio
		String idSoggettoXML = tipoSogg + nomeSogg;
		String idServizioXML = tipoServizio + servizio;
		String urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
		CostantiRegistroServizi.URL_SEPARATOR + idServizioXML  + ".xml";

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
					throw new DriverRegistroServiziNotFound("[getServizio] Il servizio ["+idService.getTipoServizio()+"/"+idService.getServizio()+
							"] erogato dal soggetto ["+idService.getSoggettoErogatore()+"] non risulta gia' registrato nel registro");
				} else
					throw e;
			}

			/* --- Validazione XSD (ora che sono sicuro che non ho un 404) -- */
			try{
				this.validatoreRegistro.valida(urlXMLServizio);  
			}catch (Exception e) {
				throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica] Riscontrato errore durante la validazione XSD del Registro dei Servizi XML di OpenSPCoop: "+e.getMessage(),e);
			}

			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.getServizio().setNomeSoggettoErogatore(idService.getSoggettoErogatore().getNome());
					servRichiesto.getServizio().setTipoSoggettoErogatore(idService.getSoggettoErogatore().getTipo());
				}
			}
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
				throw new DriverRegistroServiziException("[getServizio] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(servRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getServizio] Servizio non trovato.");

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
		try{
			for(int i=0;i<lista.length;i++){
				if(uriAccordo.equals(lista[i].getUriAccordo()) &&
						TipologiaServizio.CORRELATO.toString().equals(lista[i].getTipologiaServizio()) &&
						idSoggetto.getTipo().equals(lista[i].getSoggettoErogatore().getTipo()) &&
						idSoggetto.getNome().equals(lista[i].getSoggettoErogatore().getNome())
				){
					urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
					CostantiRegistroServizi.URL_SEPARATOR + lista[i].getTipoServizio()+lista[i].getServizio()+ ".xml";
					tipoServizio = lista[i].getTipoServizio();
					nomeServizio = lista[i].getServizio();
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
					throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Il servizio ["+tipoServizio+"/"+nomeServizio
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

			// Parsing JIBX
			bin = new ByteArrayInputStream(fileXML);
			istr = new InputStreamReader(bin);
			org.openspcoop2.core.registry.RegistroServizi rs = 
				(org.openspcoop2.core.registry.RegistroServizi) uctx.unmarshalDocument(istr, null);
			if(rs.sizeSoggettoList()>0){
				if(rs.getSoggetto(0).sizeAccordoServizioParteSpecificaList()>0){
					servRichiesto = rs.getSoggetto(0).getAccordoServizioParteSpecifica(0);
					servRichiesto.getServizio().setNomeSoggettoErogatore(idSoggetto.getNome());
					servRichiesto.getServizio().setTipoSoggettoErogatore(idSoggetto.getTipo());
				}
			}
			istr.close();
			bin.close();
		}catch(DriverRegistroServiziNotFound e){
			throw e;
		}catch(DriverRegistroServiziException e){
			throw e;
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
				throw new DriverRegistroServiziException("[getServizioCorrelatoByAccordo] Errore durante il parsing xml: "+e.getMessage(),e);
		}

		if(servRichiesto==null)
			throw new DriverRegistroServiziNotFound("[getServizioCorrelatoByAccordo] Servizio non trovato.");

		return servRichiesto;

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
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		try{

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoServizioParteSpecifica[] servList = this.generatoreXML.getAccordiServiziParteSpecifica();
			if(servList==null)
				throw new DriverRegistroServiziNotFound("Servizi non esistenti nel repository WEB");

			// Esamina dei servizi
			Vector<IDAccordo> idServizi = new Vector<IDAccordo>();
			for(int i=0; i<servList.length; i++){

				String idSoggettoXML = servList[i].getServizio().getTipoSoggettoErogatore() + servList[i].getServizio().getNomeSoggettoErogatore();
				String idServizioXML = servList[i].getServizio().getTipo() + servList[i].getServizio().getNome();
				String urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
				CostantiRegistroServizi.URL_SEPARATOR + idServizioXML  + ".xml";


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizio);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] Riscontrato errore durante la validazione XSD URL("+urlXMLServizio+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(servList[i].getServizio().getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(servList[i].getServizio().getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServiziParteSpecifica](FiltroByMinDate) Servizio["+servList[i].getServizio().getTipo()+"/"+servList[i].getServizio().getNome()+"] SoggettoErogatore["+servList[i].getServizio().getTipoSoggettoErogatore()+"/"+servList[i].getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServiziParteSpecifica](FiltroByMaxDate) Servizio["+servList[i].getServizio().getTipo()+"/"+servList[i].getServizio().getNome()+"] SoggettoErogatore["+servList[i].getServizio().getTipoSoggettoErogatore()+"/"+servList[i].getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(servList[i].getServizio().getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(servList[i].getServizio().getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordo()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
						if(servList[i].getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(servList[i]);
					idServizi.add(idAccordo);
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
				throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] error",e);
		}
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

			if(this.generatoreXML==null)
				throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] Gestore repository XML non istanziato. Necessario per l'implementazione di questo metodo.");

			org.openspcoop2.core.registry.AccordoServizioParteSpecifica[] servList = this.generatoreXML.getAccordiServiziParteSpecifica();
			if(servList==null)
				throw new DriverRegistroServiziNotFound("Servizi non esistenti nel repository WEB");

			// Esamina dei servizi
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
			for(int i=0; i<servList.length; i++){

				String idSoggettoXML = servList[i].getServizio().getTipoSoggettoErogatore() + servList[i].getServizio().getNomeSoggettoErogatore();
				String idServizioXML = servList[i].getServizio().getTipo() + servList[i].getServizio().getNome();
				String urlXMLServizio = this.urlPrefix + idSoggettoXML + CostantiRegistroServizi.URL_SEPARATOR+CostantiXMLRepository.SERVIZI+
				CostantiRegistroServizi.URL_SEPARATOR + idServizioXML  + ".xml";


				/* --- Validazione XSD -- */
				try{
					this.validatoreRegistro.valida(urlXMLServizio);  
				}catch (Exception e) {
					throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] Riscontrato errore durante la validazione XSD URL("+urlXMLServizio+"): "+e.getMessage(),e);
				}

				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(servList[i].getServizio().getTipoSoggettoErogatore().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(servList[i].getServizio().getNomeSoggettoErogatore().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServiziParteSpecifica](FiltroByMinDate) Servizio["+servList[i].getServizio().getTipo()+"/"+servList[i].getServizio().getNome()+"] SoggettoErogatore["+servList[i].getServizio().getTipoSoggettoErogatore()+"/"+servList[i].getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(servList[i].getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServiziParteSpecifica](FiltroByMaxDate) Servizio["+servList[i].getServizio().getTipo()+"/"+servList[i].getServizio().getNome()+"] SoggettoErogatore["+servList[i].getServizio().getTipoSoggettoErogatore()+"/"+servList[i].getServizio().getNomeSoggettoErogatore()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(servList[i].getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipo()!=null){
						if(servList[i].getServizio().getTipo().equals(filtroRicerca.getTipo()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNome()!=null){
						if(servList[i].getServizio().getNome().equals(filtroRicerca.getNome()) == false){
							continue;
						}
					}
					// Filtro by Accordo
					if(filtroRicerca.getIdAccordo()!=null){
						String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
						if(servList[i].getAccordoServizioParteComune().equals(uriAccordo) == false){
							continue;
						}
					}
					IDServizio idServ = new IDServizio(servList[i].getServizio().getTipoSoggettoErogatore(),servList[i].getServizio().getNomeSoggettoErogatore(),
							servList[i].getServizio().getTipo(),servList[i].getServizio().getNome());
					idServ.setUriAccordo(servList[i].getAccordoServizioParteComune());
					idServ.setTipologiaServizio(servList[i].getServizio().getTipologiaServizio().toString());
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
				throw new DriverRegistroServiziException("[getAllIdAccordiServiziParteSpecifica] error",e);
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

		String tipoOLD = soggetto.getOldTipoForUpdate();
		String nomeOLD = soggetto.getOldNomeForUpdate();
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
		
		Servizio servizio = asps.getServizio();
		
		try {
			// Controllo elementi obbligatori
			if( (servizio.getNomeSoggettoErogatore() == null) || 
					(servizio.getTipoSoggettoErogatore() == null) ){
				throw new DriverRegistroServiziException("Soggetto, erogatore del servizio, non definito");
			}
			if( (servizio.getNome() == null) || 
					(servizio.getTipo() == null) ){
				throw new DriverRegistroServiziException("Accordo Servizio Parte Specifica, non definito");
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
				if( this.generatoreXML.existsSoggetto(idSoggettoFruitore) == false){
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
			if( this.generatoreXML.existsSoggetto(idSoggettoErogatore) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoErogatore_string
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del servizio
			String idServizio_string = servizio.getTipo() + servizio.getNome();
			IDServizio idServizio = new IDServizio(idSoggettoErogatore,servizio.getTipo(),servizio.getNome());
			if (this.generatoreXML.existsAccordoServizioParteSpecifica(idServizio)==true){
				throw new DriverRegistroServiziException("Il servizio ["+idServizio_string+"] erogato dal soggetto ["+idSoggettoErogatore_string+"] risulta gia' registrato nel registro");
			}

			// Generazione XML
			this.generatoreXML.createAccordoServizioParteSpecifica(idServizio,asps);

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
		}catch(DriverRegistroServiziNotFound notFound){
			return false;
		}
		catch(Exception e){
			this.log.error("[existsAccordoServizioParteSpecifica] Servizio non trovato: "+e.getMessage());
			return false;
		}
	}
	
	/**
	 * Verifica l'esistenza di un servizio registrato.
	 *
	 * @param idAccordo Identificativo del servizio
	 * @return true se il servizio esiste, false altrimenti
	 */    
	@Override
	public boolean existsAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException{
		
		if( idAccordo == null)
			return false;

		try{
			this.getAccordoServizioParteSpecifica(idAccordo);

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
				if( this.generatoreXML.existsSoggetto(idSoggettoFruitore) == false){
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
			if( this.generatoreXML.existsSoggetto(idSoggettoOLD) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggettoOLD_string
						+"] da modificare, non risulta gia' inserito nel registro dei servizi.");
			} 

			// Controllo pre-esistenza del nuovo soggetto erogatore
			String idSoggettoNEW_string = servizio.getTipoSoggettoErogatore() + servizio.getNomeSoggettoErogatore();
			IDSoggetto idSoggettoNEW = new IDSoggetto(servizio.getTipoSoggettoErogatore(),servizio.getNomeSoggettoErogatore());
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
			IDServizio idServizioNEW = new IDServizio(idSoggettoNEW,servizio.getTipo(),servizio.getNome());
			String idServizioNEW_string = servizio.getTipo() + servizio.getNome();
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
			if( this.generatoreXML.existsSoggetto(idSoggetto) == false){
				throw new DriverRegistroServiziException("Il soggetto ["+idSoggetto_string
						+"] non risulta gia' inserito nel registro dei servizi.");
			} 
			// Controllo pre-esistenza del servizio
			IDServizio idServizio = new IDServizio(idSoggetto,servizio.getTipo(),servizio.getNome());
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
					this.log.info("eliminazione servizio ["+serviziRegistrati[i].getServizio().getTipo()+"/"+serviziRegistrati[i].getServizio().getNome()+"] erogato da ["+serviziRegistrati[i].getServizio().getTipoSoggettoErogatore()+"/"+serviziRegistrati[i].getServizio().getNomeSoggettoErogatore()+"] in corso...");
					this.deleteAccordoServizioParteSpecifica(serviziRegistrati[i]);
					this.log.info("eliminazione servizio ["+serviziRegistrati[i].getServizio().getTipo()+"/"+serviziRegistrati[i].getServizio().getNome()+"] erogato da ["+serviziRegistrati[i].getServizio().getTipoSoggettoErogatore()+"/"+serviziRegistrati[i].getServizio().getNomeSoggettoErogatore()+"] effettuata.");
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

