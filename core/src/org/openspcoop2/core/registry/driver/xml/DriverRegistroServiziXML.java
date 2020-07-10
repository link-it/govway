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




package org.openspcoop2.core.registry.driver.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Azione;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.Soggetto;
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
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ProtocolPropertiesUtilities;
import org.openspcoop2.core.registry.driver.ValidazioneSemantica;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;


/**
 * Classe utilizzata per effettuare query ad un registro dei servizi openspcoop formato da un file xml.
 * Le query riguardano specifiche
 * proprieta' di servizi presenti all'interno del registro.
 *
 *
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverRegistroServiziXML extends BeanUtilities 
	implements IDriverRegistroServiziGet,IMonitoraggioRisorsa {


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** Path dove si trova il file xml che realizza il Registro dei servizi OpenSPCoop. */
	private String registry_path;
	/** 'Root' del servizio dei registri OpenSPCoop. */
	private org.openspcoop2.core.registry.RegistroServizi registro;
	/** Validatore della configurazione */
	private ValidatoreXSD validatoreRegistro = null;

	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
	private IDServizioFactory idServizioFactory = IDServizioFactory.getInstance();


	/** Variabile che mantiene uno stato di ultima modifica del registro dei servizi xml
        od una variabile temporale che servira' per fare il refresh della configurazione 
	una volta scaduto un timeout, nel caso il file sia posto in remoto (HTTP) .*/
	private long lastModified = 0;
	/** Logger utilizzato per info. */
	private Logger log = null;
	/** Indica ogni quanto deve essere effettuato il refresh del file dei registri dei servizi, 
	nel caso sia posto in remoto (secondi)*/
	private static final int timeoutRefresh = 30;

	public org.openspcoop2.core.registry.RegistroServizi getRegistroXML() {
		return this.registro;
	}


	/** ************* parsing XML ************** */
	public void parsingXMLRegistroServizi() throws DriverRegistroServiziException{

		/* --- Validazione XSD -- */
		FileInputStream fXML = null;
		try{
			if(this.registry_path.startsWith("http://") || this.registry_path.startsWith("file://")){
				this.validatoreRegistro.valida(this.registry_path);  
			}else{
				fXML = new FileInputStream(this.registry_path);
				this.validatoreRegistro.valida(fXML);
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

		/* ---- InputStream ---- */
		InputStream iStream = null;
		HttpURLConnection httpConn = null;
		if(this.registry_path.startsWith("http://") || this.registry_path.startsWith("file://")){
			try{ 
				URL url = new URL(this.registry_path);
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
			this.lastModified = DateManager.getTimeMillis();
		}else{
			try{  
				iStream = new FileInputStream(this.registry_path);
			}catch(java.io.FileNotFoundException e) {
				throw new DriverRegistroServiziException("Riscontrato errore durante la creazione dell'inputStream del registro dei servizi (FILE) : \n\n"+e.getMessage());
			}
			try{
				this.lastModified = (new File(this.registry_path)).lastModified();
			}catch(Exception e){
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				throw new DriverRegistroServiziException("Riscontrato errore durante la lettura del file dove e' allocato il registro dei servizi: "+e.getMessage());
			}
		}



		/* ---- Unmarshall del file di configurazione ---- */
		try{  
			org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.registry.utils.serializer.JaxbDeserializer();
			this.registro = deserializer.readRegistroServizi(iStream);
		} catch(Exception e) {
			try{  
				if(iStream!=null)
					iStream.close();
			} catch(Exception ef) {}
			try{ 
				if(httpConn !=null)
					httpConn.disconnect();
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
		try{
			// Chiusura dell'eventuale connessione HTTP
			if(httpConn !=null)
				httpConn.disconnect();
		} catch(Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la chiusura dell'Input Stream (http): "+e.getMessage());
		}
	}




	/* ********  COSTRUTTORI e METODI DI RELOAD  ******** */

	/**
	 * Costruttore.  Viene effettuato l'unmarshall del file di configurazione.
	 *
	 * @param path path dove si trova il file xml che realizza il Registro dei servizi OpenSPCoop.
	 * 
	 */
	public DriverRegistroServiziXML(String path,Logger alog){

		if(alog==null)
			this.log = LoggerWrapperFactory.getLogger(DriverRegistroServiziXML.class);
		else
			this.log = alog;

		if(path == null){
			this.log.error("DriverRegistroServizi: Riscontrato errore durante la creazione: url/path is null");
			this.create=false;
			return;
		}
		this.registry_path = path;

		/* --- Costruzione Validatore XSD -- */
		try{
			this.validatoreRegistro = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverRegistroServiziXML.class.getResourceAsStream("/registroServizi.xsd"));
		}catch (Exception e) {
			this.log.error("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage());
			return;
		}

		try{
			this.parsingXMLRegistroServizi();
		}catch(Exception e){
			this.log.error("DriverRegistroServizi: "+e.getMessage());
			this.create=false;
			return;
		}
		this.create = true;
	}

	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	private void forzaRefreshRegistroServiziXML() throws DriverRegistroServiziException{
		refreshRegistroServiziXML(true);
	}
	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	public void refreshRegistroServiziXML() throws DriverRegistroServiziException{
		refreshRegistroServiziXML(false);
	}
	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	private synchronized void refreshRegistroServiziXML(boolean refreshForzato) throws DriverRegistroServiziException{

		File fTest = null;
		boolean refresh = refreshForzato;
		if(refreshForzato==false){
			if(this.registry_path.startsWith("http://") || this.registry_path.startsWith("file://")){
				long now = DateManager.getTimeMillis();
				if( (now-this.lastModified) > (DriverRegistroServiziXML.timeoutRefresh*1000) ){
					refresh=true;
				}
			}else{
				fTest = new File(this.registry_path);
				if(this.lastModified != fTest.lastModified()){
					refresh = true;
				}
			}
		}
		if(refresh){

			try{
				this.parsingXMLRegistroServizi();
			}catch(Exception e){
				this.log.error("DriverRegistroServizi refreshError: "+e.getMessage());
				throw new DriverRegistroServiziException("DriverRegistroServizi refreshError: "+e.getMessage());
			}
			if(this.registry_path.startsWith("http://")==false && this.registry_path.startsWith("file://")==false){
				this.log.warn("Reloaded registry context.");
			}

			if(this.validazioneSemanticaDuranteModificaXML){
				ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(this.registro,this.verificaURI,this.tipiConnettori,
						this.tipiSoggetti,this.tipiServiziSoap,this.tipiServiziRest,
						this.log);
				try{
					validazioneSemantica.validazioneSemantica(false);
				}catch(Exception e){
					this.log.error("DriverRegistroServizi refreshError(ValidazioneSemantica): "+e.getMessage());
					throw new DriverRegistroServiziException("DriverRegistroServizi refreshError(ValidazioneSemantica): "+e.getMessage());
				}				
			}
			
		}

		if(this.registro == null){
			this.log.error("DriverRegistroServizi refreshError: istanza del registro is null dopo il refresh");
			throw new DriverRegistroServiziException("DriverRegistroServizi refreshError: istanza del registro is null dopo il refresh");
		}

	}

	private boolean validazioneSemanticaDuranteModificaXML = false;
	private boolean verificaURI = false;
	private String[] tipiConnettori = null;
	private String[] tipiSoggetti = null;
	private String[] tipiServiziSoap = null;
	private String[] tipiServiziRest = null;
	public void abilitazioneValidazioneSemanticaDuranteModificaXML(boolean verificaURI, String[] tipiConnettori,
			String[] tipiSoggetti,String [] tipiServiziSoapValidi, String [] tipiServiziRestValidi){
		this.validazioneSemanticaDuranteModificaXML = true;
		this.verificaURI = verificaURI;
		this.tipiConnettori = tipiConnettori;
		this.tipiSoggetti = tipiSoggetti;
		this.tipiServiziSoap = tipiServiziSoapValidi;
		this.tipiServiziRest = tipiServiziRestValidi;
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

		if(idAccordo==null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Parametro Non Valido");
		if(idAccordo.getNome()==null)
			throw new DriverRegistroServiziException("[getAccordoCooperazione] Nome accordo non presente");
		
		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeAccordoCooperazioneList(); i++){
			org.openspcoop2.core.registry.AccordoCooperazione ac = this.registro.getAccordoCooperazione(i);
			if (ac.getNome() != null) {
				if (ac.getNome().equals(idAccordo.getNome())) {
					
					// potenziale as, verifico versione
					if(idAccordo.getVersione()==null){
						if(ac.getVersione()!=null)
							continue;
					}else{
						if(idAccordo.getVersione().equals(ac.getVersione())==false)
							continue;
					}
					
					//read uriServiziComposti
					String uriAccordoCooperazione = this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
					for(int j=0; j<this.registro.sizeAccordoServizioParteComuneList(); j++){
						AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(j);
						if(as.getServizioComposto()!=null && uriAccordoCooperazione.equals(as.getServizioComposto().getAccordoCooperazione()) ){
							// Trovato as composto collegato all'accordo di cooperazione
							ac.addUriServiziComposti(this.idAccordoFactory.getUriFromAccordo(as));
						} 
					}
					
					return ac;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Accordo di Cooperazione non trovato");
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

			List<IDAccordoCooperazione> idAccordi = new ArrayList<IDAccordoCooperazione>();
			for(int i=0; i<this.registro.sizeAccordoCooperazioneList(); i++){
				org.openspcoop2.core.registry.AccordoCooperazione ac = this.registro.getAccordoCooperazione(i);
				String uriAC = this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ac.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMinDate) Accordo di cooperazione ["+uriAC+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ac.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ac.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiCooperazione](FiltroByMaxDate) Accordo di cooperazione ["+uriAC+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
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
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Accordi di cooperazione non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Accordi di cooperazione non trovati");
				}
			}else{
				return idAccordi;
			}
		}catch(Exception e){
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

		if(idAccordo==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Parametro Non Valido");
		if(idAccordo.getNome()==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Nome accordo non presente");
		IDSoggetto soggettoReferenteCheck = idAccordo.getSoggettoReferente();
		if(soggettoReferenteCheck!=null){
			if(soggettoReferenteCheck.getTipo()==null){
				throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Tipo soggetto referente non presente?");
			}
			if(soggettoReferenteCheck.getNome()==null){
				throw new DriverRegistroServiziException("[getAccordoServizioParteComune] Nome soggetto referente non presente?");
			}
		}

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeAccordoServizioParteComuneList(); i++){
			org.openspcoop2.core.registry.AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(i);
			if (as.getNome() != null) {
				if (as.getNome().equals(idAccordo.getNome())) {
					
					// potenziale as, verifico soggetto referente e versione
					if(soggettoReferenteCheck==null){
						if(as.getSoggettoReferente()!=null)
							continue;
					}else{
						if(as.getSoggettoReferente()==null)
							continue;
						if(soggettoReferenteCheck.getTipo().equals(as.getSoggettoReferente().getTipo())==false)
							continue;
						if(soggettoReferenteCheck.getNome().equals(as.getSoggettoReferente().getNome())==false)
							continue;
					}
					if(idAccordo.getVersione()==null){
						if(as.getVersione()!=null)
							continue;
					}else{
						if(idAccordo.getVersione().equals(as.getVersione())==false)
							continue;
					}
					
					// nomiAzione setting 
//					as.setNomiAzione(as.readNomiAzione());
					return as;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteComune] Accordo di Servizio non trovato");
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

			for(int i=0; i<this.registro.sizeAccordoServizioParteComuneList(); i++){
				org.openspcoop2.core.registry.AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(i);
				String uriAS = this.idAccordoFactory.getUriFromAccordo(as);
				
				if(filtroRicercaBase!=null){
					// Filtro By Data
					if(filtroRicercaBase.getMinDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMinDate) Accordo di servizio ["+uriAS+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().before(filtroRicercaBase.getMinDate())){
							continue;
						}
					}
					if(filtroRicercaBase.getMaxDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("["+nomeMetodo+"](FiltroByMaxDate) Accordo di servizio ["+uriAS+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
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
				
				IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(as);
				
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
		
		if(nomePdD==null)
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro Non Valido");

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizePortaDominioList(); i++){
			org.openspcoop2.core.registry.PortaDominio pd = this.registro.getPortaDominio(i);
			if (pd.getNome() != null) {
				if (pd.getNome().equals(nomePdD)) {
					return pd;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getPortaDominio] Porta di Dominio non trovata");
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

			List<String> nomiPorteDiDominio = new ArrayList<String>();
			for(int i=0; i<this.registro.sizePortaDominioList(); i++){
				org.openspcoop2.core.registry.PortaDominio pd = this.registro.getPortaDominio(i);
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(pd.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMinDate) Porta di Dominio ["+pd.getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pd.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(pd.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDominio](FiltroByMaxDate) Porta di Dominio ["+pd.getNome()+"] non valorizzata nell'ora-registrazione. Non inserito nella lista ritornata.");
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
				nomiPorteDiDominio.add(pd.getNome());
			}
			if(nomiPorteDiDominio.size()==0){
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate");
				}
			}else{
				return nomiPorteDiDominio;
			}
		}catch(Exception e){
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

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeGruppoList(); i++){
			org.openspcoop2.core.registry.Gruppo gruppo = this.registro.getGruppo(i);
			if (gruppo.getNome() != null) {
				if (gruppo.getNome().equals(idGruppo.getNome())) {
					return gruppo;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getGruppo] Gruppo non trovato");
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

			List<IDGruppo> idGruppi = new ArrayList<IDGruppo>();
			for(int i=0; i<this.registro.sizeGruppoList(); i++){
				org.openspcoop2.core.registry.Gruppo gruppo = this.registro.getGruppo(i);
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
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Gruppi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Gruppi non trovati");
				}
			}else{
				return idGruppi;
			}
		}catch(Exception e){
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

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeRuoloList(); i++){
			org.openspcoop2.core.registry.Ruolo ruolo = this.registro.getRuolo(i);
			if (ruolo.getNome() != null) {
				if (ruolo.getNome().equals(idRuolo.getNome())) {
					return ruolo;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getRuolo] Ruolo non trovato");
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

			List<IDRuolo> idRuoli = new ArrayList<IDRuolo>();
			for(int i=0; i<this.registro.sizeRuoloList(); i++){
				org.openspcoop2.core.registry.Ruolo ruolo = this.registro.getRuolo(i);
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
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Ruoli non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Ruoli non trovati");
				}
			}else{
				return idRuoli;
			}
		}catch(Exception e){
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

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeScopeList(); i++){
			org.openspcoop2.core.registry.Scope scope = this.registro.getScope(i);
			if (scope.getNome() != null) {
				if (scope.getNome().equals(idScope.getNome())) {
					return scope;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getScope] Scope non trovato");
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

			List<IDScope> idScope = new ArrayList<IDScope>();
			for(int i=0; i<this.registro.sizeScopeList(); i++){
				org.openspcoop2.core.registry.Scope scope = this.registro.getScope(i);
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
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Scope non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Scope non trovati");
				}
			}else{
				return idScope;
			}
		}catch(Exception e){
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
		String tipoSP = idSoggetto.getTipo();
		String codiceSP = idSoggetto.getNome();
		if(tipoSP == null || codiceSP == null)
			throw new DriverRegistroServiziException("[getSoggetto] Parametri Non Validi");

		refreshRegistroServiziXML();

		for(int i=0; i<this.registro.sizeSoggettoList(); i++){
			org.openspcoop2.core.registry.Soggetto ss = this.registro.getSoggetto(i);
			
			if( (ss.getTipo() != null) && 
					(ss.getNome() != null) ){
				if( (ss.getTipo().equals(tipoSP)) && 
						(ss.getNome().equals(codiceSP)) ){
					return ss;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non trovato");
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiBasic(
			String user,String password, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.BASIC, user, password, 
				null, null, null, false,
				null,
				config,
				false);
	}
	
	@Override
	public Soggetto getSoggettoByCredenzialiApiKey(
			String user,String password, boolean appId, CryptConfig config) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return this._getSoggettoAutenticato(CredenzialeTipo.APIKEY, user, password, 
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
	public Soggetto getSoggettoByCredenzialiSsl(
			CertificateInfo certificate, boolean strictVerifier) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
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

		refreshRegistroServiziXML();

		boolean testInChiaro = false;
		ICrypt crypt = null;
		if(CredenzialeTipo.BASIC.equals(tipoCredenziale)) {
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
		else if(CredenzialeTipo.APIKEY.equals(tipoCredenziale)) {
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
		
		for(int i=0; i<this.registro.sizeSoggettoList(); i++){
			org.openspcoop2.core.registry.Soggetto ss = this.registro.getSoggetto(i);
			CredenzialiSoggetto credenziali = ss.getCredenziali();
			if(credenziali==null){
				continue;
			}
			if(credenziali.getTipo()==null){
				if(tipoCredenziale.equals(CredenzialeTipo.SSL) == false){ // ssl è il default
					continue;
				}	
			}
			if(tipoCredenziale.equals(credenziali.getTipo())==false){
				continue;
			}
			
			switch (tipoCredenziale) {
			case BASIC:
			case APIKEY:
				if( (user.equals(credenziali.getUser()))){
					
					if(CredenzialeTipo.APIKEY.equals(tipoCredenziale)) {
						if(appId) {
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
					
					String passwordSaved =  credenziali.getPassword();
					
					boolean found = false;
					if(testInChiaro) {
						found = password.equals(passwordSaved);
					}
					if(!found && crypt!=null) {
						found = crypt.check(password, passwordSaved);
					}
					
					if( found ) {
						return ss;
					}
					
				}
				break;
			case SSL:
				try{
					if(aSubject!=null && !"".equals(aSubject)) {
					
						boolean subjectValid = false;
						if(credenziali.getSubject()!=null) {
							subjectValid = CertificateUtils.sslVerify(credenziali.getSubject(), aSubject, PrincipalType.subject, this.log);
						}
						
						boolean issuerValid = true;
						if(aIssuer!=null && !"".equals(aIssuer)) {
							if(credenziali.getIssuer()==null) {
								issuerValid = false;
							}
							else {
								issuerValid = CertificateUtils.sslVerify(credenziali.getIssuer(), aIssuer, PrincipalType.issuer, this.log);
							}
						}
						else {
							issuerValid = (credenziali.getIssuer() == null);
						}
						
						if(subjectValid && issuerValid){
							return ss;
						}
	
					}
					else {
						
						// ricerca per certificato
						String cnSubject = aCertificate.getSubject().getCN();
						if(cnSubject.equals(credenziali.getCnSubject())) {
							Certificate certificato = ArchiveLoader.load(ArchiveType.CER, credenziali.getCertificate(), 0, null);
							if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
								return ss;
							}
						}
						
					}
				}catch(Exception e){
					throw new DriverRegistroServiziException(e.getMessage(),e);
				}
				break;
			case PRINCIPAL:
				if( (principal.equals(credenziali.getUser()))){
					return ss;
				}
				break;
			}
		}

		throw new DriverRegistroServiziNotFound("[getSoggetto] Soggetto non Trovato");
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
			
			List<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();
			for(int i=0; i<this.registro.sizeSoggettoList(); i++){
				org.openspcoop2.core.registry.Soggetto ss = this.registro.getSoggetto(i);
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(ss.getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggetti](FiltroByMinDate) Soggetto ["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(ss.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(ss.getOraRegistrazione()==null){
							this.log.debug("[getAllIdSoggetti](FiltroByMaxDate) Soggetto ["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
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
									if(credenziali.getIssuer()==null) {
										issuerValid = false;
									}
									else {
										issuerValid = CertificateUtils.sslVerify(credenziali.getIssuer(), filtroRicerca.getCredenzialiSoggetto().getIssuer(), PrincipalType.issuer, this.log);
									}
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
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Soggetti non trovati");
				}
			}else{
				return idSoggetti;
			}
		}catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdSoggetti error",e);
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

		// Il refresh viene effettuato con questa ricerca.
		org.openspcoop2.core.registry.Soggetto soggetto = getSoggetto(idService.getSoggettoErogatore());

		if(soggetto == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica(IDServizio)] Parametro Non Valido");
		String servizio = idService.getNome();
		String tipoServizio = idService.getTipo();
		Integer versioneServizio = idService.getVersione();
		if((servizio==null)||(tipoServizio==null)||(versioneServizio==null))
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica(IDServizio)] Parametri Non Validi");

		for(int i=0; i<soggetto.sizeAccordoServizioParteSpecificaList(); i++){
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = soggetto.getAccordoServizioParteSpecifica(i);
			if( (asps.getTipo().equals(tipoServizio)) && 
					(asps.getNome().equals(servizio)) &&
					asps.getVersione().intValue() == versioneServizio.intValue()){
				asps.setTipoSoggettoErogatore(soggetto.getTipo());
				asps.setNomeSoggettoErogatore(soggetto.getNome());
				return asps;
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica(IDServizio)] Servizio non trovato");
	}


	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  correlato identificato grazie ai fields Soggetto
	 * e nomeAccordo
	 * 
	 * @param idSoggetto Identificatore del Soggetto di tipo {@link org.openspcoop2.core.id.IDSoggetto}.
	 * @param idAccordoServizioParteComune ID dell'accordo che deve implementare il servizio correlato
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}.
	 * 
	 */
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica_ServizioCorrelato(
			IDSoggetto idSoggetto, 
			IDAccordo idAccordoServizioParteComune) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		// Il refresh viene effettuato con questa ricerca.
		org.openspcoop2.core.registry.Soggetto soggetto = getSoggetto(idSoggetto);
		if(soggetto == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica_ServizioCorrelato] Parametro Non Valido");

		String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordoServizioParteComune);
		
		for(int i=0; i<soggetto.sizeAccordoServizioParteSpecificaList(); i++){
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = soggetto.getAccordoServizioParteSpecifica(i);
			if( asps.getAccordoServizioParteComune() != null) {
				if( asps.getAccordoServizioParteComune().equals(uriAccordo) && TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ) {
					asps.setTipoSoggettoErogatore(soggetto.getTipo());
					asps.setNomeSoggettoErogatore(soggetto.getNome());
					return asps;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Servizio non trovato");
		
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
			
			for(int i=0; i<this.registro.sizeSoggettoList(); i++){
				org.openspcoop2.core.registry.Soggetto ss = this.registro.getSoggetto(i);
				if(filtroRicerca!=null){
					// Filtro By Tipo e Nome Soggetto Erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(ss.getTipo().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(ss.getNome().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
				}
				for(int j=0; j<ss.sizeAccordoServizioParteSpecificaList(); j++){
					org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = ss.getAccordoServizioParteSpecifica(j);

					if(filtroRicerca!=null){
						// Filtro By Data
						if(filtroRicerca.getMinDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("["+nomeMetodo+"](FiltroByMinDate) Servizio["+this.idServizioFactory.getUriFromAccordo(asps)+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().before(filtroRicerca.getMinDate())){
								continue;
							}
						}
						if(filtroRicerca.getMaxDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("["+nomeMetodo+"](FiltroByMaxDate) Servizio["+this.idServizioFactory.getUriFromAccordo(asps)+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
								continue;
							}
						}
						// Filtro By Tipo, Nome e Versione
						if(filtroRicerca.getTipo()!=null){
							if(asps.getTipo().equals(filtroRicerca.getTipo()) == false){
								continue;
							}
						}
						if(filtroRicerca.getNome()!=null){
							if(asps.getNome().equals(filtroRicerca.getNome()) == false){
								continue;
							}
						}
						if(filtroRicerca.getVersione()!=null){
							if(asps.getVersione().intValue() != filtroRicerca.getVersione().intValue()){
								continue;
							}
						}
						if(filtroRicerca.getPortType()!=null){
							if(asps.getPortType().equals(filtroRicerca.getPortType()) == false){
								continue;
							}
						}
						// Filtro by Accordo
						if(filtroRicerca.getIdAccordoServizioParteComune()!=null){
							String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordoServizioParteComune());
							if(asps.getAccordoServizioParteComune().equals(uriAccordo) == false){
								continue;
							}
						}
						// ProtocolProperties
						if(ProtocolPropertiesUtilities.isMatch(asps, filtroRicerca.getProtocolProperties())==false){
							continue;
						}
						// Filtro By Tipo e/o Nome Soggetto Fruitore
						if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
							if(asps.sizeFruitoreList()<=0){
								continue;
							}
							boolean found = false;
							for (int k = 0; k < asps.sizeFruitoreList(); k++) {
								Fruitore fruitore = asps.getFruitore(k);
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
					IDServizio idServ = this.idServizioFactory.getIDServizioFromAccordo(asps);
					
					if(filtroFruizioni!=null){
						
						for (Fruitore fruitore : asps.getFruitoreList()) {
							
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
						
						// Filtro By Tipo e/o Nome Soggetto Fruitore
						if(filtroRicerca.getTipoSoggettoFruitore()!=null || filtroRicerca.getNomeSoggettoFruitore()!=null){
							if(asps.sizeFruitoreList()<=0){
								continue;
							}
							boolean found = false;
							for (int k = 0; k < asps.sizeFruitoreList(); k++) {
								Fruitore fruitore = asps.getFruitore(k);
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
					else{
						listReturn.add((T)idServ);
					}
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
			throw new DriverRegistroServiziException(nomeMetodo+" error",e);
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
			this.forzaRefreshRegistroServiziXML();
		}catch(Exception e){
			throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);
		}
	}



	@Override
	public RegistroServizi getImmagineCompletaRegistroServizi() throws DriverRegistroServiziException{
		
		refreshRegistroServiziXML();		
		return this.registro;
	}
}
