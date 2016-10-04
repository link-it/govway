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




package org.openspcoop2.core.registry.driver.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
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
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.ValidazioneSemantica;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.RegistroServizi;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;


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

	/** Contesto di Unmarshall. */
	private IUnmarshallingContext uctx;
	/** Path dove si trova il file xml che realizza il Registro dei servizi OpenSPCoop. */
	private String registry_path;
	/** 'Root' del servizio dei registri OpenSPCoop. */
	private org.openspcoop2.core.registry.RegistroServizi registro;
	/** Validatore della configurazione */
	private ValidatoreXSD validatoreRegistro = null;

	// Factory
	private IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();


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
		InputStreamReader iStream = null;
		FileInputStream fin = null;
		HttpURLConnection httpConn = null;
		if(this.registry_path.startsWith("http://") || this.registry_path.startsWith("file://")){
			try{ 
				URL url = new URL(this.registry_path);
				URLConnection connection = url.openConnection();
				httpConn = (HttpURLConnection) connection;
				httpConn.setRequestMethod("GET");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				iStream = new InputStreamReader(httpConn.getInputStream());
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
				fin = new FileInputStream(this.registry_path);
				iStream = new InputStreamReader(fin);
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
				try{  
					if(fin!=null)
						fin.close();
				} catch(java.io.IOException ef) {}
				throw new DriverRegistroServiziException("Riscontrato errore durante la lettura del file dove e' allocato il registro dei servizi: "+e.getMessage());
			}
		}



		/* ---- Unmarshall del file di configurazione ---- */
		try{  
			this.registro = (org.openspcoop2.core.registry.RegistroServizi) this.uctx.unmarshalDocument(iStream, null);
		} catch(org.jibx.runtime.JiBXException e) {
			try{  
				if(iStream!=null)
					iStream.close();
			} catch(Exception ef) {}
			try{  
				if(fin!=null)
					fin.close();
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
			// Chiusura dell FileInputStream
			if(fin!=null)
				fin.close();
		} catch(Exception e) {
			throw new DriverRegistroServiziException("Riscontrato errore durante la chiusura dell'Input Stream (file): "+e.getMessage());
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
			this.validatoreRegistro = new ValidatoreXSD(this.log,DriverRegistroServiziXML.class.getResourceAsStream("/registroServizi.xsd"));
		}catch (Exception e) {
			this.log.error("Riscontrato errore durante l'inizializzazione dello schema del Registro dei Servizi di OpenSPCoop: "+e.getMessage());
			return;
		}

		/* ---- Inizializzazione del contesto di unmarshall ---- */
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.registry.RegistroServizi.class);
			this.uctx = bfact.createUnmarshallingContext();
		} catch(org.jibx.runtime.JiBXException e) {
			this.log.error("DriverRegistroServizi: Riscontrato errore durante la creazione del contesto di unmarshall  : \n\n"+e.getMessage());
			this.create=false;
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
				ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(this.registro,this.verificaURI,this.tipiConnettori,this.tipiSoggetti,this.tipiServizi,
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
	private String[] tipiServizi = null;
	public void abilitazioneValidazioneSemanticaDuranteModificaXML(boolean verificaURI, String[] tipiConnettori,
			String[] tipiSoggetti,String[] tipiServizi){
		this.validazioneSemanticaDuranteModificaXML = true;
		this.verificaURI = verificaURI;
		this.tipiConnettori = tipiConnettori;
		this.tipiSoggetti = tipiSoggetti;
		this.tipiServizi = tipiServizi;
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

		throw new DriverRegistroServiziNotFound("[getAccordoCooperazione] Accordo di Cooperazione non Trovato");
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

			Vector<IDAccordoCooperazione> idAccordi = new Vector<IDAccordoCooperazione>();
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

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteComune] Accordo di Servizio non Trovato");
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

			Vector<IDAccordo> idAccordi = new Vector<IDAccordo>();
			for(int i=0; i<this.registro.sizeAccordoServizioParteComuneList(); i++){
				org.openspcoop2.core.registry.AccordoServizioParteComune as = this.registro.getAccordoServizioParteComune(i);
				String uriAS = this.idAccordoFactory.getUriFromAccordo(as);
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteComune](FiltroByMinDate) Accordo di servizio ["+uriAS+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(as.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(as.getOraRegistrazione()==null){
							this.log.debug("[getAllIdAccordiServizioParteComune](FiltroByMaxDate) Accordo di servizio ["+uriAS+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
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
				idAccordi.add(this.idAccordoFactory.getIDAccordoFromAccordo(as));
			}
			if(idAccordi.size()==0){
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Accordi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Accordi non trovati");
				}
			}else{
				return idAccordi;
			}
		}catch(Exception e){
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

			Vector<String> nomiPorteDiDominio = new Vector<String>();
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
			Vector<IDSoggetto> idSoggetti = new Vector<IDSoggetto>();
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
		String servizio = idService.getServizio();
		String tipoServizio = idService.getTipoServizio();
		if((servizio==null)||(tipoServizio==null))
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica(IDServizio)] Parametri Non Validi");

		for(int i=0; i<soggetto.sizeAccordoServizioParteSpecificaList(); i++){
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = soggetto.getAccordoServizioParteSpecifica(i);
			Servizio ss = asps.getServizio();
			if( (ss.getTipo() != null) && 
					(ss.getNome() != null) ){
				if( (ss.getTipo().equals(tipoServizio)) && 
						(ss.getNome().equals(servizio)) ){
					ss.setTipoSoggettoErogatore(soggetto.getTipo());
					ss.setNomeSoggettoErogatore(soggetto.getNome());
					return asps;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica(IDServizio)] Servizio non Trovato");
	}

	
	/**
	 * Si occupa di ritornare l'oggetto {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}
	 * contenente le informazioni sulle funzionalita' associate
	 * al servizio  identificato grazie ai fields Soggetto,
	 * 'Servizio','TipoServizio' e 'Azione' impostati
	 * all'interno del parametro <var>idAccordo</var> di tipo {@link org.openspcoop2.core.id.IDAccordo}. 
	 *
	 * @param idAccordo Identificatore del Servizio di tipo {@link org.openspcoop2.core.id.IDAccordo}.
	 * @return l'oggetto di tipo {@link org.openspcoop2.core.registry.AccordoServizioParteSpecifica}.
	 * 
	 */
	@Override
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica(IDAccordo idAccordo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		if(idAccordo==null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica(idAccordo)] Parametro Non Valido");
		if(idAccordo.getNome() == null)
			throw new DriverRegistroServiziException("[getAccordoServizioParteSpecifica(idAccordo)] Parametri Non Validi");

		refreshRegistroServiziXML();
		
		for (int i = 0; i < this.registro.sizeSoggettoList(); i++) {
			
			org.openspcoop2.core.registry.Soggetto soggetto = this.registro.getSoggetto(i);
			
			for(int j=0; j<soggetto.sizeAccordoServizioParteSpecificaList(); j++){
				
				org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = soggetto.getAccordoServizioParteSpecifica(j);
				IDAccordo idAccordoPS = this.idAccordoFactory.getIDAccordoFromAccordo(asps);
				if(idAccordoPS.equals(idAccordo)){
					return asps;
				}
			}
			
		}
		
		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica(idAccordo)] Servizio non Trovato");
		
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
				if( asps.getAccordoServizioParteComune().equals(uriAccordo) && TipologiaServizio.CORRELATO.equals(asps.getServizio().getTipologiaServizio()) ) {
					asps.getServizio().setTipoSoggettoErogatore(soggetto.getTipo());
					asps.getServizio().setNomeSoggettoErogatore(soggetto.getNome());
					return asps;
				}
			}
		}

		throw new DriverRegistroServiziNotFound("[getAccordoServizioParteSpecifica_ServizioCorrelato] Servizio non Trovato");
		
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
	public List<IDAccordo> getAllIdAccordiServizioParteSpecifica(
			FiltroRicercaServizi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		try{
			Vector<IDAccordo> idAccordiServizi = new Vector<IDAccordo>();
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
					Servizio serv = asps.getServizio();

					if(filtroRicerca!=null){
						// Filtro By Data
						if(filtroRicerca.getMinDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("[getAllIdServizi](FiltroByMinDate) Servizio["+serv.getTipo()+"/"+serv.getNome()+"] SoggettoErogatore["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().before(filtroRicerca.getMinDate())){
								continue;
							}
						}
						if(filtroRicerca.getMaxDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("[getAllIdServizi](FiltroByMaxDate) Servizio["+serv.getTipo()+"/"+serv.getNome()+"] SoggettoErogatore["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
								continue;
							}
						}
						// Filtro By Tipo e Nome
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
						// Filtro by Accordo
						if(filtroRicerca.getIdAccordo()!=null){
							String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
							if(asps.getAccordoServizioParteComune().equals(uriAccordo) == false){
								continue;
							}
						}
					}
					idAccordiServizi.add(this.idAccordoFactory.getIDAccordoFromAccordo(asps));
				}
			}
			if(idAccordiServizi.size()==0){
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Servizi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Servizi non trovati");
				}
			}else{
				return idAccordiServizi;
			}

		}catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdServizi error",e);
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
			Vector<IDServizio> idServizi = new Vector<IDServizio>();
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
				IDSoggetto idSoggetto = new IDSoggetto(ss.getTipo(),ss.getNome());
				for(int j=0; j<ss.sizeAccordoServizioParteSpecificaList(); j++){
					org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps = ss.getAccordoServizioParteSpecifica(j);
					Servizio serv = asps.getServizio();

					if(filtroRicerca!=null){
						// Filtro By Data
						if(filtroRicerca.getMinDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("[getAllIdServizi](FiltroByMinDate) Servizio["+serv.getTipo()+"/"+serv.getNome()+"] SoggettoErogatore["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().before(filtroRicerca.getMinDate())){
								continue;
							}
						}
						if(filtroRicerca.getMaxDate()!=null){
							if(asps.getOraRegistrazione()==null){
								this.log.debug("[getAllIdServizi](FiltroByMaxDate) Servizio["+serv.getTipo()+"/"+serv.getNome()+"] SoggettoErogatore["+ss.getTipo()+"/"+ss.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
								continue;
							}else if(asps.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
								continue;
							}
						}
						// Filtro By Tipo e Nome
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
						// Filtro by Accordo
						if(filtroRicerca.getIdAccordo()!=null){
							String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(filtroRicerca.getIdAccordo());
							if(asps.getAccordoServizioParteComune().equals(uriAccordo) == false){
								continue;
							}
						}
					}
					IDServizio idServ = new IDServizio(idSoggetto,serv.getTipo(),serv.getNome());
					idServ.setUriAccordo(asps.getAccordoServizioParteComune());
					idServ.setTipologiaServizio(serv.getTipologiaServizio().toString());
					idServizi.add(idServ);
				}
			}
			if(idServizi.size()==0){
				if(filtroRicerca!=null){
					throw new DriverRegistroServiziNotFound("Servizi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				}else{
					throw new DriverRegistroServiziNotFound("Servizi non trovati");
				}
			}else{
				return idServizi;
			}

		}catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdServizi error",e);
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
