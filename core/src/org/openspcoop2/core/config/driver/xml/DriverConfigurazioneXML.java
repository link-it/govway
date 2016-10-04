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


package org.openspcoop2.core.config.driver.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.Openspcoop2;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.ValidazioneSemantica;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;

/**
 * Contiene un 'reader' della configurazione dell'infrastruttura OpenSPCoop.
 * <p>
 * Sono forniti metodi per la lettura della configurazione la quale contiene i seguenti soggetti :
 * <ul>
 * <li>Porte Delegate</li>
 * <li>Porte Applicative</li>
 * <li>SIL-Mittenti</li>
 * <li>Porta di Dominio</li>
 * <li>RoutingTable</li>
 * <li>Altri aspetti di configurazione ...</li>
 * </ul>
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverConfigurazioneXML extends BeanUtilities 
implements IDriverConfigurazioneGet,IMonitoraggioRisorsa{

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Indicazione di una corretta creazione */
	public boolean create = false;

	/** Contesto di Unmarshall. */
	private IUnmarshallingContext uctx;
	/** Path dove si trova il file xml che realizza la configurazione di OpenSPCoop. */
	private String configuration_path;
	/** 'Root' del servizio della configurazione di OpenSPCoop. */
	private org.openspcoop2.core.config.Openspcoop2 openspcoop;

	/** Validatore della configurazione */
	private ValidatoreXSD validatoreConfigurazione = null;

	/** Variabile che mantiene uno stato di ultima modifica della configurazione xml
        od una variabile temporale che servira' per fare il refresh della configurazione 
	una volta scaduto un timeout, nel caso il file sia posto in remoto (HTTP) .*/
	private long lastModified = 0;
	/** Logger utilizzato per info. */
	private Logger log = null;
	/** Indica ogni quanto deve essere effettuato il refresh del file dei registri dei servizi, 
	nel caso sia posto in remoto (secondi)*/
	private static final int timeoutRefresh = 30;




	/** ************* parsing XML ************** */
	private void parsingXMLConfigurazione() throws DriverConfigurazioneException{
		
		/* --- Validazione XSD -- */
		FileInputStream fXML = null;
		try{
			if(this.configuration_path.startsWith("http://") || this.configuration_path.startsWith("file://")){
				this.validatoreConfigurazione.valida(this.configuration_path);  
			}else{
				fXML = new FileInputStream(this.configuration_path);
				this.validatoreConfigurazione.valida(fXML);
			}
		}catch (Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la validazione XSD della configurazione XML di OpenSPCoop: "+e.getMessage());
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
		if(this.configuration_path.startsWith("http://") || this.configuration_path.startsWith("file://")){
			try{ 
				URL url = new URL(this.configuration_path);
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
				throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStream della configurazione (HTTP) : \n\n"+e.getMessage());
			}
			this.lastModified = DateManager.getTimeMillis();
		}else{
			try{  
				fin = new FileInputStream(this.configuration_path);
				iStream = new InputStreamReader(fin);
			}catch(java.io.FileNotFoundException e) {
				throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStream della configurazione (FILE) : \n\n"+e.getMessage());
			}
			try{
				this.lastModified = (new File(this.configuration_path)).lastModified();
			}catch(Exception e){
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				try{  
					if(fin!=null)
						fin.close();
				} catch(java.io.IOException ef) {}
				throw new DriverConfigurazioneException("Riscontrato errore durante la lettura del file dove e' allocato la configurazione: "+e.getMessage());
			}
		}


		/* ---- Unmarshall del file di configurazione ---- */
		try{  
			this.openspcoop = (org.openspcoop2.core.config.Openspcoop2) this.uctx.unmarshalDocument(iStream, null);
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
			throw new DriverConfigurazioneException("Riscontrato errore durante l'unmarshall del file di configurazione: "+e.getMessage());
		}
		
		try{  
			// Chiusura dello Stream
			if(iStream!=null)
				iStream.close();
		} catch(Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la chiusura dell'Input Stream: "+e.getMessage());
		}
		try{
			// Chiusura dell FileInputStream
			if(fin!=null)
				fin.close();
		} catch(Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la chiusura dell'Input Stream (file): "+e.getMessage());
		}
		try{
			// Chiusura dell'eventuale connessione HTTP
			if(httpConn !=null)
				httpConn.disconnect();
		} catch(Exception e) {
			throw new DriverConfigurazioneException("Riscontrato errore durante la chiusura dell'Input Stream (http): "+e.getMessage());
		}
	}




	/* ********  COSTRUTTORI e METODI DI RELOAD  ******** */

	/**
	 * Costruttore.  Viene effettuato l'unmarshall del file di configurazione.
	 *
	 * @param path path dove si trova il file xml che realizza la configurazione di OpenSPCoop.
	 * 
	 */
	public DriverConfigurazioneXML(String path,Logger alog){

		if(alog==null)
			this.log = LoggerWrapperFactory.getLogger(DriverConfigurazioneXML.class);
		else
			this.log = alog;
		
		if(path == null){
			this.log.error("DriverConfigurazione: Riscontrato errore durante la creazione: url/path is null");
			this.create=false;
			return;
		}
		this.configuration_path = path;

		/* --- Costruzione Validatore XSD -- */
		try{
			this.validatoreConfigurazione = new ValidatoreXSD(this.log,DriverConfigurazioneXML.class.getResourceAsStream("/config.xsd"));
		}catch (Exception e) {
			this.log.info("Riscontrato errore durante l'inizializzazione dello schema della configurazione di OpenSPCoop: "+e.getMessage(),e);
			return;
		}

		/* ---- Inizializzazione del contesto di unmarshall ---- */
		try{
			IBindingFactory bfact = BindingDirectory.getFactory(org.openspcoop2.core.config.Openspcoop2.class);
			this.uctx = bfact.createUnmarshallingContext();
		} catch(org.jibx.runtime.JiBXException e) {
			this.log.error("DriverConfigurazione: Riscontrato errore durante la creazione del contesto di unmarshall  : "+e.getMessage(),e);
			this.create=false;
			return;
		}

		try{
			this.parsingXMLConfigurazione();
		}catch(Exception e){
			this.log.error("DriverConfigurazione: "+e.getMessage(),e);
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
	private void forzaRefreshConfigurazioneXML() throws DriverConfigurazioneException{
		refreshConfigurazioneXML(true);
	}
	
	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	public void refreshConfigurazioneXML() throws DriverConfigurazioneException{
		refreshConfigurazioneXML(false);
	}

	/**
	 * Metodo che si occupa di effettuare il refresh dell'unmarshall del file di configurazione
	 * se il file di configurazione e' stato modificato dall'ultimo unmarshall.
	 *
	 * 
	 */
	private synchronized void refreshConfigurazioneXML(boolean refreshForzato) throws DriverConfigurazioneException{

		File fTest = null;
		boolean refresh = refreshForzato;
		if(refreshForzato==false){
			if(this.configuration_path.startsWith("http://") || this.configuration_path.startsWith("file://")){
				long now = DateManager.getTimeMillis();
				if( (now-this.lastModified) > (DriverConfigurazioneXML.timeoutRefresh*1000) ){
					refresh=true;
				}
			}else{
				fTest = new File(this.configuration_path);
				if(this.lastModified != fTest.lastModified()){
					refresh = true;
				}
			}
		}
		if(refresh){
			try{
				this.parsingXMLConfigurazione();
			}catch(Exception e){
				this.log.error("DriverConfigurazione refreshError: "+e.getMessage());
				throw new DriverConfigurazioneException("DriverConfigurazione refreshError: "+e.getMessage());
			}
			if(this.configuration_path.startsWith("http://")==false &&
				this.configuration_path.startsWith("file://")==false){
				this.log.warn("Reloaded configuration context.");
			}

			if(this.validazioneSemanticaDuranteModificaXML){
				ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(this.openspcoop,this.tipiConnettori,this.tipiSoggetti,this.tipiServizi,
						this.tipoMsgDiagnosticiAppender,this.tipoTracciamentoAppender,
						this.tipoAutenticazione,this.tipoAutorizzazione,
						this.tipoAutorizzazioneContenuto,this.tipoAutorizzazioneBusteContenuto,
						this.tipoIntegrazionePD,this.tipoIntegrazionePA,
						true,
						this.log);
				try{
					validazioneSemantica.validazioneSemantica(false);
				}catch(Exception e){
					this.log.error("DriverConfigurazione refreshError(ValidazioneSemantica): "+e.getMessage());
					throw new DriverConfigurazioneException("DriverConfigurazione refreshError(ValidazioneSemantica): "+e.getMessage());
				}				
			}
		}

		if(this.openspcoop == null){
			this.log.error("DriverConfigurazione refreshError: istanza della configurazione is null dopo il refresh");
			throw new DriverConfigurazioneException("DriverConfigurazione refreshError: istanza della configurazione is null dopo il refresh");
		}

	}


	private boolean validazioneSemanticaDuranteModificaXML = false;
	private String[] tipiConnettori = null;
	private String[] tipiSoggetti = null;
	private String[] tipiServizi = null;
	private String[] tipoMsgDiagnosticiAppender = null;
	private String[] tipoTracciamentoAppender = null;
	private String[] tipoAutenticazione = null;
	private String[] tipoAutorizzazione = null;
	private String[] tipoAutorizzazioneContenuto = null;
	private String[] tipoAutorizzazioneBusteContenuto = null;
	private String[] tipoIntegrazionePD = null;
	private String[] tipoIntegrazionePA = null;
	public void abilitazioneValidazioneSemanticaDuranteModificaXML(String[] tipiConnettori,
			String[] tipiSoggetti,String[] tipiServizi,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,
			String[]tipoAutenticazione,String[]tipoAutorizzazione,
			String[] tipoAutorizzazioneContenuto,String[] tipoAutorizzazioneBusteContenuto,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA){
		this.validazioneSemanticaDuranteModificaXML = true;
		
		this.tipiConnettori = tipiConnettori;
		this.tipiSoggetti = tipiSoggetti;
		this.tipiServizi = tipiServizi;
		this.tipoMsgDiagnosticiAppender= tipoMsgDiagnosticiAppender;
		this.tipoTracciamentoAppender=tipoTracciamentoAppender;
		this.tipoAutenticazione=tipoAutenticazione;
		this.tipoAutorizzazione=tipoAutorizzazione;
		this.tipoAutorizzazioneContenuto=tipoAutorizzazioneContenuto;
		this.tipoAutorizzazioneBusteContenuto=tipoAutorizzazioneBusteContenuto;
		this.tipoIntegrazionePD=tipoIntegrazionePD;
		this.tipoIntegrazionePA=tipoIntegrazionePA;
	}
	
	
	






	// SOGGETTO 
	
	/**
	 * Restituisce Il soggetto identificato da <var>idSoggetto</var>
	 *
	 * @param aSoggetto Identificatore di un soggetto
	 * @return Il Soggetto identificato dal parametro.
	 * 
	 */
	@Override
	public Soggetto getSoggetto(IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if( (aSoggetto == null) || (aSoggetto.getNome()==null) || (aSoggetto.getTipo()==null)){
			throw new DriverConfigurazioneException("[getSoggetto] Parametri Non Validi");
		}

		refreshConfigurazioneXML();

		for(int i=0;i<this.openspcoop.sizeSoggettoList();i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);

			if( aSoggetto.getNome().equals(soggetto.getNome()) &&
					aSoggetto.getTipo().equals(soggetto.getTipo())){

				return soggetto;
			}

		}  

		throw new DriverConfigurazioneNotFound("[getSoggetto] Soggetto "+aSoggetto.toString()+" non Esistente");
	}

	/**
	 * Restituisce Il soggetto che include la porta delegata identificata da <var>location</var>
	 *
	 * @param location Location che identifica una porta delegata
	 * @return Il Soggetto che include la porta delegata fornita come parametro.
	 * 
	 */
	@Override
	public Soggetto getSoggetto(String location) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(location == null)
			throw new DriverConfigurazioneException("[getSoggetto] Parametro Non Validi");

		refreshConfigurazioneXML();

		for(int i=0;i<this.openspcoop.sizeSoggettoList();i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);

			//	ricerca PD
			for(int j=0; j<soggetto.sizePortaDelegataList() ; j++){

				PortaDelegata pd = soggetto.getPortaDelegata(j);

				// Prima controllo un eventuale location fornito. Poi controllo il nome della porta delegata.
				if(pd.getLocation()!=null){
					if(pd.getLocation().equals(location)){
						return soggetto;
					}
				}
				else if(location.equals(pd.getNome())){
					return soggetto;
				}
			}  
		}  

		throw new DriverConfigurazioneNotFound("[getSoggetto] Soggetto che possiede la porta delegata ["+location+"] non esistente");
	}

	/**
	 * Restituisce il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 * 
	 * @return il soggetto configurato come router, se esiste nella Porta di Dominio un soggetto registrato come Router
	 * 
	 */
	@Override
	public Soggetto getRouter() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		refreshConfigurazioneXML();

		// Deve esistere un soggetto configurato come Router
		for(int i=0; i<this.openspcoop.sizeSoggettoList();i++){
			if(this.openspcoop.getSoggetto(i).getRouter())
				return this.openspcoop.getSoggetto(i);
		}

		throw new DriverConfigurazioneNotFound("[getRouter] Router non esistente");
	}

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public HashSet<String> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{	

		refreshConfigurazioneXML();

		HashSet<String> lista = new HashSet<String>();
		try{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggetto = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggetto.sizePortaApplicativaList(); j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					if(pa.getSoggettoVirtuale()!=null && 
							pa.getSoggettoVirtuale().getTipo()!=null &&
							pa.getSoggettoVirtuale().getNome()!=null){
						String soggettoVirtuale = 
							pa.getSoggettoVirtuale().getTipo() + pa.getSoggettoVirtuale().getNome();
						if(lista.contains(soggettoVirtuale)==false){
							this.log.info("aggiunto Soggetto "+soggettoVirtuale+" alla lista dei Soggetti Virtuali");
							lista.add(soggettoVirtuale);
						}
					}
				}
			}	
		}catch(Exception e){
			throw new DriverConfigurazioneException("[getSoggettiVirtuali] Inizializzazione lista dei soggetti virtuali non riuscita: "+e.getMessage());
		}

		if(lista.size()==0){
			throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Soggetti virtuali non esistenti");
		}
		
		return lista;
	}

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei servizi associati a soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	@Override
	public HashSet<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		HashSet<IDServizio> lista = new HashSet<IDServizio>();
		HashSet<String> unique = new HashSet<String>();
		try{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggetto = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggetto.sizePortaApplicativaList(); j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					if(pa.getSoggettoVirtuale()!=null && 
							pa.getSoggettoVirtuale().getTipo()!=null &&
							pa.getSoggettoVirtuale().getNome()!=null){
						IDServizio s = new IDServizio();
						s.setSoggettoErogatore(new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome()));
						s.setTipoServizio(pa.getServizio().getTipo());
						s.setServizio(pa.getServizio().getNome());
						if(unique.contains(s.toString())==false){
							//log.info("aggiunto Servizio "+s.toString()+" alla lista dei servizi erogati da Soggetti Virtuali");
							lista.add(s);
							unique.add(s.toString());
						}
					}
				}
			}	
		}catch(Exception e){
			throw new DriverConfigurazioneException("[getServizi_SoggettiVirtuali] Inizializzazione lista servizi dei soggetti virtuali non riuscita: "+e.getMessage());
		}
		
		
		if(lista.size()==0){
			throw new DriverConfigurazioneNotFound("[getServizi_SoggettiVirtuali] Servizi erogati da Soggetti virtuali non esistenti");
		}
	
		return lista;
		
	}
	
	/**
	 * Restituisce la lista degli identificativi dei soggetti
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei soggetti
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDSoggetto> getAllIdSoggetti(
			FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
	
		refreshConfigurazioneXML();
		
		List<IDSoggetto> listIDSoggetti = new ArrayList<IDSoggetto>();
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null){
					if(soggetto.getOraRegistrazione()==null){
						this.log.debug("[getAllIdSoggetti](FiltroByMinDate) Soggetto ["+soggetto.getTipo()+"/"+soggetto.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
						continue;
					}else if(soggetto.getOraRegistrazione().before(filtroRicerca.getMinDate())){
						continue;
					}
				}
				if(filtroRicerca.getMaxDate()!=null){
					if(soggetto.getOraRegistrazione()==null){
						this.log.debug("[getAllIdSoggetti](FiltroByMaxDate) Soggetto ["+soggetto.getTipo()+"/"+soggetto.getNome()+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
						continue;
					}else if(soggetto.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
						continue;
					}
				}
				// Filtro By Tipo e Nome
				if(filtroRicerca.getTipo()!=null){
					if(soggetto.getTipo().equals(filtroRicerca.getTipo()) == false){
						continue;
					}
				}
				if(filtroRicerca.getNome()!=null){
					if(soggetto.getNome().equals(filtroRicerca.getNome()) == false){
						continue;
					}
				}
			}
			listIDSoggetti.add(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
		}
		
		if(listIDSoggetti.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("Soggetti non trovati");
		}
		
		return listIDSoggetti;
	}
	
	
	
	
	
	
	
	
	// PORTA DELEGATA

	/**
	 * Restituisce la porta delegata identificato da <var>idPD</var>
	 *
	 * @param idPD Identificatore di una Porta Delegata
	 * @return La porta delegata.
	 * 
	 */
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		if(idPD == null)
			throw new DriverConfigurazioneException("[getPortaDelegata] Parametro idPD Non Validi");

		IDSoggetto aSoggetto = idPD.getSoggettoFruitore();
		String location = idPD.getLocationPD();

		if( (location == null) || (aSoggetto==null))
			throw new DriverConfigurazioneException("[getPortaDelegata] Parametri non Validi");

		// Il getSoggetto effettua il REFRESH XML
		Soggetto soggetto = null;
		try{
			soggetto = getSoggetto(aSoggetto);
		}catch(Exception e){}
		if(soggetto == null)
			throw new DriverConfigurazioneException("[getPortaDelegata] Soggetto fruitore ["+aSoggetto.getTipo()+"/"+aSoggetto.getNome()+"] non esistente");

		// ricerca PD
		for(int j=0; j<soggetto.sizePortaDelegataList() ; j++){

			PortaDelegata pd = soggetto.getPortaDelegata(j);

			// Prima controllo un eventuale location fornito. Poi controllo il nome della porta delegata.
			if(pd.getLocation()!=null){
				if(pd.getLocation().equals(location)){
					pd.setTipoSoggettoProprietario(soggetto.getTipo());
					pd.setNomeSoggettoProprietario(soggetto.getNome());
					return pd;
				}
			}
			else if(location.equals(pd.getNome())){
				pd.setTipoSoggettoProprietario(soggetto.getTipo());
				pd.setNomeSoggettoProprietario(soggetto.getNome());
				return pd;
			}
		}  

		throw new DriverConfigurazioneNotFound("[getPortaDelegata] PortaDelegata ["+location+"] non esistente");
	}

	/**
	 * Restituisce la lista degli identificativi delle porte delegate
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte delegate
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDPortaDelegata> getAllIdPorteDelegate(
			FiltroRicercaPorteDelegate filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
	
		refreshConfigurazioneXML();
		
		List<IDPortaDelegata> listIDPorteDelegate = new ArrayList<IDPortaDelegata>();
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);
			for (int j = 0; j < soggetto.sizePortaDelegataList(); j++) {
				PortaDelegata pd = soggetto.getPortaDelegata(j);
				
				String id = pd.getNome();
				if(pd.getLocation()!=null){
					id = pd.getLocation();
				}
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(pd.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDelegate](FiltroByMinDate) PortaDelegata ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pd.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(soggetto.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteDelegate](FiltroByMaxDate) PortaDelegata ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(soggetto.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipoSoggetto()!=null){
						if(soggetto.getTipo().equals(filtroRicerca.getTipoSoggetto()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggetto()!=null){
						if(soggetto.getNome().equals(filtroRicerca.getNomeSoggetto()) == false){
							continue;
						}
					}
					// Filtro By Tipo e Nome soggetto erogatore
					if(filtroRicerca.getTipoSoggettoErogatore()!=null){
						if(pd.getSoggettoErogatore().getTipo().equals(filtroRicerca.getTipoSoggettoErogatore()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggettoErogatore()!=null){
						if(pd.getSoggettoErogatore().getNome()==null){
							continue;
						}
						if(pd.getSoggettoErogatore().getNome().equals(filtroRicerca.getNomeSoggettoErogatore()) == false){
							continue;
						}
					}
					// Filtro By Tipo e Nome servizio
					if(filtroRicerca.getTipoServizio()!=null){
						if(pd.getServizio().getTipo().equals(filtroRicerca.getTipoServizio()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeServizio()!=null){
						if(pd.getServizio().getNome()==null){
							continue;
						}
						if(pd.getServizio().getNome().equals(filtroRicerca.getNomeServizio()) == false){
							continue;
						}
					}
					// Filtro By azione
					if(filtroRicerca.getAzione()!=null){
						if(pd.getAzione().getNome()==null){
							continue;
						}
						if(pd.getAzione().getNome().equals(filtroRicerca.getAzione()) == false){
							continue;
						}
					}
				}
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setLocationPD(id);
				idPD.setSoggettoFruitore(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
				listIDPorteDelegate.add(idPD);
			}
		}
		
		if(listIDPorteDelegate.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteDelegate non trovate");
		}
		
		return listIDPorteDelegate;
	}
	
	
	
	
	
	
	
	// PORTA APPLICATIVA
	
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, viene ricercata
	 * una Porta Applicativa che non possegga l'azione
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.getPortaApplicativa_engine(idPA, false,null);	
	}
	
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA,boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.getPortaApplicativa_engine(idPA, ricercaPuntuale,null);
	}
	
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * @param idPA
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
			return getPortaApplicativa_engine(idPA,false,soggettoVirtuale);
	}
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 * dove il soggetto erogatore e' un soggetto virtuale
	 * nel caso in cui e' specificata un'azione ma non viene trovato nessun risultato, non vengono effettuate ricerche ulteriori.
	 * @param idPA
	 * @param ricercaPuntuale
	 * @return La porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public PortaApplicativa getPortaApplicativaVirtuale(IDPortaApplicativa idPA,IDSoggetto soggettoVirtuale,boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
			return getPortaApplicativa_engine(idPA,ricercaPuntuale,soggettoVirtuale);
	}
	
	
	/**
	 * Restituisce la porta applicativa identificata da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return La porta applicativa
	 * 
	 */
	private PortaApplicativa getPortaApplicativa_engine(IDPortaApplicativa idPA,boolean ricercaPuntuale,IDSoggetto soggettoVirtuale) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(idPA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idPA Non Validi");

		if(idPA.getIDServizio() == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idPA non Validi (IDServizio is null)");
		
		IDSoggetto soggettoErogatore = idPA.getIDServizio().getSoggettoErogatore();
		if(soggettoErogatore == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri Non Validi (Soggetto Erogatore is null)");
		
		IDServizio service = idPA.getIDServizio();
		if( service==null )
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri Non Validi (Servizio is null)");
	
		String servizio = service.getServizio();
		String tipoServizio = service.getTipoServizio();
		String azione = service.getAzione();
		if((servizio==null)||(tipoServizio==null))
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Servizio) Non Validi");

		//	Il getSoggetto effettua il REFRESH XML
		Soggetto soggetto = null;
		try{
			soggetto = getSoggetto(soggettoErogatore);
		}catch(DriverConfigurazioneNotFound dn){
			throw new DriverConfigurazioneNotFound("[getPortaApplicativa] "+dn.getMessage(),dn);
		}
		catch(Exception e){}
		if(soggetto == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Soggetto erogatore ["+soggettoErogatore.getTipo()+"/"+soggettoErogatore.getNome()+"] non esistente");

		if(soggettoVirtuale!=null){
			if(soggettoVirtuale.getTipo()==null || soggettoVirtuale.getNome()==null)
				throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Soggetto Virtuale) non validi");
		}
		
		// ricerca PA
		PortaApplicativa paSenzaAzione = null;
		for(int j=0; j<soggetto.sizePortaApplicativaList() ; j++){

			PortaApplicativa pa = soggetto.getPortaApplicativa(j);

			boolean paMatchaCriteriDiRicerca = false;
			if(soggettoVirtuale==null){
				paMatchaCriteriDiRicerca = servizio.equals(pa.getServizio().getNome()) && 
										   tipoServizio.equals(pa.getServizio().getTipo());
			}else{
				paMatchaCriteriDiRicerca = servizio.equals(pa.getServizio().getNome()) && 
				                           tipoServizio.equals(pa.getServizio().getTipo()) &&
				                           pa.getSoggettoVirtuale()!=null &&
				                           soggettoVirtuale.getTipo().equals(pa.getSoggettoVirtuale().getTipo()) &&
				                           soggettoVirtuale.getNome().equals(pa.getSoggettoVirtuale().getNome());
			}
				
				
			if(paMatchaCriteriDiRicerca){

				// ricerca di una porta applicativa senza azione
				if(azione==null){
					if(pa.getAzione() == null){
						pa.setTipoSoggettoProprietario(soggetto.getTipo());
						pa.setNomeSoggettoProprietario(soggetto.getNome());
						return pa;
					}
				}
				// ricerca di una porta applicativa con azione
				else{
					if(pa.getAzione() == null){
						paSenzaAzione = pa; // potenziale porta applicativa che mappa solo il servizio (se non esiste)
						paSenzaAzione.setTipoSoggettoProprietario(soggetto.getTipo());
						paSenzaAzione.setNomeSoggettoProprietario(soggetto.getNome());
						continue;
					}
					if(azione.equals(pa.getAzione().getNome())){
						pa.setTipoSoggettoProprietario(soggetto.getTipo());
						pa.setNomeSoggettoProprietario(soggetto.getNome());
						return pa;
					}
				}
			}
		}  
		if(paSenzaAzione!=null && ricercaPuntuale==false)
			return paSenzaAzione;

		throw new DriverConfigurazioneNotFound("[getPortaApplicativa] PortaApplicativa non esistente");
	}
	
	/** Restituisce una porta applicativa in base al nome della porta e il soggetto proprietario della porta*/
	@Override
	public PortaApplicativa getPortaApplicativa(
			IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(idPA==null) 
			throw new DriverConfigurazioneException("Identificativo non fornito");
		return this.getPortaApplicativa(idPA.getNome(), idPA.getSoggetto());
	}
	@Override
	public PortaApplicativa getPortaApplicativa(String nomePorta, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(nomePorta==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa]  Nome Porta is null");
		if(soggettoProprietario==null){
			throw new DriverConfigurazioneException("[getPortaApplicativa]  SoggettoProprietario is null");
		}
		if(soggettoProprietario.getTipo()==null || soggettoProprietario.getNome()==null ){
			throw new DriverConfigurazioneException("[getPortaApplicativa]  dati del SoggettoProprietario null");
		}
		
		//	Il getSoggetto effettua il REFRESH XML
		Soggetto soggetto = null;
		try{
			soggetto = getSoggetto(soggettoProprietario);
		}catch(Exception e){}
		if(soggetto == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Soggetto proprietario ["+soggettoProprietario.toString()+"] non esistente");
		
		for(int i=0; i<soggetto.sizePortaApplicativaList(); i++){
			if (nomePorta.equals(soggetto.getPortaApplicativa(i).getNome())){
				PortaApplicativa pa = soggetto.getPortaApplicativa(i);
				pa.setTipoSoggettoProprietario(soggetto.getTipo());
				pa.setNomeSoggettoProprietario(soggetto.getNome());
				return pa;
			}
		}
		
		throw new DriverConfigurazioneNotFound("[getPortaApplicativa] PortaApplicativa non esistente");

	}
	
	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa) 
	 * che possiedono il soggetto SoggettoVirtuale identificato da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa con soggetto Virtuale
	 * @return una porta applicativa
	 * 
	 */
	@Override
	public Hashtable<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDPortaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(idPA==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idPA Non Valido");
		if(idPA.getIDServizio()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idServizio Non Valido");
		if(idPA.getIDServizio().getSoggettoErogatore()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro Soggetto Erogatore Non Valido");

		refreshConfigurazioneXML();

		Hashtable<IDSoggetto,PortaApplicativa> paConSoggetti = new Hashtable<IDSoggetto,PortaApplicativa>();
		IDSoggetto soggettoVirtuale = idPA.getIDServizio().getSoggettoErogatore();	
		String servizio = idPA.getIDServizio().getServizio();
		String tipoServizio = idPA.getIDServizio().getTipoServizio();
		String azione = idPA.getIDServizio().getAzione();
		if((servizio==null)||(tipoServizio==null))
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametri (Servizio) Non Validi");


		// Ricerca soggetti che possiedono porte applicative.... che hanno un soggetto virtuale
		for(int i=0; i<this.openspcoop.sizeSoggettoList();i++){

			Soggetto aSoggetto = this.openspcoop.getSoggetto(i);
			PortaApplicativa paSenzaAzione = null;

			// Ricerca tra le PA del soggetto...
			for(int j=0; j<aSoggetto.sizePortaApplicativaList() ; j++){

				PortaApplicativa pa = aSoggetto.getPortaApplicativa(j);
				if( pa.getSoggettoVirtuale()!= null  ){
					if( (pa.getSoggettoVirtuale().getTipo().equals(soggettoVirtuale.getTipo())) &&
							(pa.getSoggettoVirtuale().getNome().equals(soggettoVirtuale.getNome())) ){

						// Porta applicativa con tale soggetto virtuale... guardo se possiede il servizio e l'azione della busta
						if(servizio.equals(pa.getServizio().getNome()) &&
								tipoServizio.equals(pa.getServizio().getTipo())){

							// ricerca di una porta applicativa senza azione
							if(azione==null){
								if(pa.getAzione() == null){
									for(int k=0; k<pa.sizeServizioApplicativoList(); k++){
										paConSoggetti.put(new IDSoggetto(aSoggetto.getTipo(),aSoggetto.getNome()),
												pa);
									}
									break; // ricerca in questo soggetto terminata.
								}
							}
							// ricerca di una porta applicativa con azione
							else{
								if(pa.getAzione() == null){
									paSenzaAzione = pa; // potenziale porta applicativa che mappa solo il servizio (se non esiste)
									continue;
								}
								if(azione.equals(pa.getAzione().getNome())){
									for(int k=0; k<pa.sizeServizioApplicativoList(); k++){
										paConSoggetti.put(new IDSoggetto(aSoggetto.getTipo(),aSoggetto.getNome()),
												pa);
										paSenzaAzione = null;
									}
									break; // ricerca in questo soggetto terminata.
								}
							}
						}
					}
				}
			}// Fine ricerca tra le PA del soggetto
			if(paSenzaAzione!=null){
				// Trovata PA con solo match per azione
				paConSoggetti.put(new IDSoggetto(aSoggetto.getTipo(),aSoggetto.getNome()),
						paSenzaAzione);
			}

		}  // fine Ricerca

		if(paConSoggetti.size() == 0)
			throw new DriverConfigurazioneNotFound("[getPortaApplicativa_SoggettiVirtuali] Porte applicative di soggetti virtuali non esistenti.");
		
		return paConSoggetti;
	}

	/**
	 * Restituisce la lista delle porte applicative con il nome fornito da parametro.
	 * Possono esistere piu' porte applicative con medesimo nome, che appartengono a soggetti differenti.
	 * Se indicati i parametri sui soggetti vengono utilizzati come filtro per localizzare in maniera piu' precisa la PA
	 * 
	 * @param nomePA Nome di una Porta Applicativa
	 * @param tipoSoggettoProprietario Tipo del Soggetto Proprietario di una Porta Applicativa
	 * @param nomeSoggettoProprietario Nome del Soggetto Proprietario di una Porta Applicativa
	 * @return La lista di porte applicative
	 * 
	 */
	@Override
	public List<PortaApplicativa> getPorteApplicative(
			String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
	
		if(nomePA==null)
			throw new DriverConfigurazioneException("[getPorteApplicative] Parametro nomePA Non Valido");
	
		
		
		refreshConfigurazioneXML();
	
		List<PortaApplicativa> lista = new ArrayList<PortaApplicativa>();
		
		// Ricerca soggetti che possiedono porte applicative
		for(int i=0; i<this.openspcoop.sizeSoggettoList();i++){

			Soggetto aSoggetto = this.openspcoop.getSoggetto(i);

			if(tipoSoggettoProprietario!=null && !"".equals(tipoSoggettoProprietario)){
				if(aSoggetto.getTipo().equals(tipoSoggettoProprietario)==false){
					continue;
				}
			}
			if(nomeSoggettoProprietario!=null && !"".equals(nomeSoggettoProprietario)){
				if(aSoggetto.getNome().equals(nomeSoggettoProprietario)==false){
					continue;
				}
			}
			
			// Ricerca tra le PA del soggetto...
			for(int j=0; j<aSoggetto.sizePortaApplicativaList() ; j++){

				PortaApplicativa pa = aSoggetto.getPortaApplicativa(j);
				pa.setTipoSoggettoProprietario(aSoggetto.getTipo());
				pa.setNomeSoggettoProprietario(aSoggetto.getNome());
				
				
				if(pa.getNome().equals(nomePA)){
					lista.add(pa);
				}
			}
			
		}
		
		if(lista.size() == 0)
			throw new DriverConfigurazioneNotFound("[getPorteApplicative] Porte applicative non esistenti.");
		
		return lista;
	}
	
	/**
	 * Restituisce la lista degli identificativi delle porte applicative
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi delle porte applicative
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDPortaApplicativaByNome> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		List<IDPortaApplicativaByNome> listIDPorteApplicative = new ArrayList<IDPortaApplicativaByNome>();
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);
			for (int j = 0; j < soggetto.sizePortaApplicativaList(); j++) {
				PortaApplicativa pa = soggetto.getPortaApplicativa(j);
				
				String id = pa.getNome();
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(pa.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteApplicative](FiltroByMinDate) PortaApplicativa ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(pa.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(soggetto.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteApplicative](FiltroByMaxDate) PortaApplicativa ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(soggetto.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipoSoggetto()!=null){
						if(soggetto.getTipo().equals(filtroRicerca.getTipoSoggetto()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggetto()!=null){
						if(soggetto.getNome().equals(filtroRicerca.getNomeSoggetto()) == false){
							continue;
						}
					}
					// Filtro By Tipo e Nome servizio
					if(filtroRicerca.getTipoServizio()!=null){
						if(pa.getServizio().getTipo().equals(filtroRicerca.getTipoServizio()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeServizio()!=null){
						if(pa.getServizio().getNome()==null){
							continue;
						}
						if(pa.getServizio().getNome().equals(filtroRicerca.getNomeServizio()) == false){
							continue;
						}
					}
					// Filtro By azione
					if(filtroRicerca.getAzione()!=null){
						if(pa.getAzione().getNome()==null){
							continue;
						}
						if(pa.getAzione().getNome().equals(filtroRicerca.getAzione()) == false){
							continue;
						}
					}
				}
				IDPortaApplicativaByNome idPA = new IDPortaApplicativaByNome();
				idPA.setNome(id);
				idPA.setSoggetto(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
				listIDPorteApplicative.add(idPA);
			}
		}
		
		if(listIDPorteApplicative.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("PorteApplicative non trovate");
		}
		
		return listIDPorteApplicative;
	}
	
	
	
	
	
	
	
	// SERVIZIO APPLICATIVO
	
	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta delegata <var>location</var>.
	 * Se nella porta delegata non vi e' viene cercato 
	 * poi in un specifico soggetto se specificato con <var>aSoggetto</var>, altrimenti in ogni soggetto. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativo(IDPortaDelegata idPD,String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		IDSoggetto soggetto = idPD.getSoggettoFruitore();

		if( servizioApplicativo==null )
			throw new DriverConfigurazioneException("[getServizioApplicativo(RichiestaDelegata)] Parametro servizioApplicativo Non Valido");

		// Ricerca nella porta delegata
		// REFRESH verra' effettuato attraverso questa ricerca
		PortaDelegata pd = getPortaDelegata(idPD);
		if(pd!=null){
			for(int j=0; j<pd.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa =  pd.getServizioApplicativo(j);
				if( servizioApplicativo.equals(sa.getNome()) ) {
					if( sa.getInvocazionePorta()!=null ){
						sa.setTipoSoggettoProprietario(pd.getTipoSoggettoProprietario());
						sa.setNomeSoggettoProprietario(pd.getNomeSoggettoProprietario());
						return sa;
					}else{
						break; // nella porta delegata e' presente solo il puntatore.
					}
				}
			}    
		}

		// Ricerca nel soggetto se diverso da null
		if(soggetto!=null){
			Soggetto soggettoProprietario = null;
			try{
				soggettoProprietario = getSoggetto(soggetto);
			}catch(Exception e){}
			if(soggettoProprietario == null)
				throw new DriverConfigurazioneException("[getServizioApplicativo(RichiestaDelegata)] Soggetto fruitore non esistente");
			for(int j=0; j<soggettoProprietario.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoProprietario.getServizioApplicativo(j);
				if( servizioApplicativo.equals(sa.getNome()) ){
					sa.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
					sa.setNomeSoggettoProprietario(soggettoProprietario.getNome());
					return sa;
				}
			}
		}
		// Ricerca in ogni soggetto se uguale a null
		else{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
					if( servizioApplicativo.equals(sa.getNome()) ){
						sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
						sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
						return sa;
					}
				}
			}
		}

		throw new DriverConfigurazioneNotFound("[getServizioApplicativo(RichiestaDelegata)] Servizio Applicativo non esistente");

	}

	/**
	 * Restituisce il servizio applicativo, cercandolo prima nella porta applicativa <var>location</var>
	 * e poi nel soggetto <var>aSoggetto</var>. 
	 *
	 * @param idPA Identificatore della porta applicativa.
	 * @param servizioApplicativo Servizio Applicativo
	 * @return Il Servizio Applicativo.
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativo(IDPortaApplicativa idPA,String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		IDSoggetto soggetto = idPA.getIDServizio().getSoggettoErogatore();

		if( soggetto == null || servizioApplicativo==null )
			throw new DriverConfigurazioneException("[getServizioApplicativo(RichiestaApplicativa)] Parametri Non Validi");

		//	REFRESH verra' effettuato attraverso questa ricerca
		PortaApplicativa pa = getPortaApplicativa(idPA);
		if(pa!=null){
			for(int j=0; j<pa.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa =  pa.getServizioApplicativo(j);
				if( servizioApplicativo.equals(sa.getNome()) ) {
					if( sa.getInvocazioneServizio() !=null ){
						sa.setTipoSoggettoProprietario(pa.getTipoSoggettoProprietario());
						sa.setNomeSoggettoProprietario(pa.getNomeSoggettoProprietario());
						return sa;
					}else{
						break; // nella porta applicativa e' presente solo il puntatore.
					}
				}
			}    
		}

		Soggetto soggettoProprietario = null;
		try{
			soggettoProprietario = getSoggetto(soggetto);
		}catch(Exception e){}
		if(soggettoProprietario == null)
			throw new DriverConfigurazioneException("[getServizioApplicativo(RichiestaApplicativa)] Soggetto Erogatore non esistente");
		for(int j=0; j<soggettoProprietario.sizeServizioApplicativoList(); j++){
			ServizioApplicativo sa = soggettoProprietario.getServizioApplicativo(j);
			if( servizioApplicativo.equals(sa.getNome()) ){
				sa.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
				sa.setNomeSoggettoProprietario(soggettoProprietario.getNome());
				return sa;
			}
		}

		throw new DriverConfigurazioneNotFound("[getServizioApplicativo(RichiestaApplicativa)] Servizio applicativo non esistente");
	}

	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aUser User utilizzato nell'header HTTP Authentication.
	 * @param aPassword Password utilizzato nell'header HTTP Authentication.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aUser,String aPassword)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(idPD!=null && idPD.getSoggettoFruitore()!=null & idPD.getLocationPD()!=null){
			// Cerco Prima come definizione interna ad una porta delegata di un soggetto
			// REFRESH verra' effettuato attraverso questa ricerca
			PortaDelegata pd = null;
			try{
				pd = getPortaDelegata(idPD);
			}catch(Exception e){}
			if(pd!=null){
				for(int j=0; j<pd.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = pd.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
									CostantiConfigurazione.CREDENZIALE_BASIC.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) && 
										(aPassword.equals(sa.getInvocazionePorta().getCredenziali(z).getPassword()))){
									sa.setTipoSoggettoProprietario(pd.getTipoSoggettoProprietario());
									sa.setNomeSoggettoProprietario(pd.getNomeSoggettoProprietario());
									return sa;
								}
							}
						}
					}
				}
			}
		}

		//	Se il soggetto non e' null lo cerco poi come definizione esterna alla porta delegata dentro il soggetto
		if(idPD!=null && idPD.getSoggettoFruitore()!=null){
			// REFRESH verra' effettuato anche attraverso questa ricerca
			Soggetto soggetto = null;
			try{
				soggetto = getSoggetto(idPD.getSoggettoFruitore());
			}catch(Exception e){}
			if(soggetto!=null)
				for(int j=0; j<soggetto.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggetto.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
									CostantiConfigurazione.CREDENZIALE_BASIC.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) && 
										(aPassword.equals(sa.getInvocazionePorta().getCredenziali(z).getPassword()))){
									sa.setTipoSoggettoProprietario(soggetto.getTipo());
									sa.setNomeSoggettoProprietario(soggetto.getNome());
									return sa;
								}
							}
						}
					}
				}
		}	

		// Se il soggetto e' null lo cerca in ogni soggetto...
		else{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
									CostantiConfigurazione.CREDENZIALE_BASIC.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) && 
										(aPassword.equals(sa.getInvocazionePorta().getCredenziali(z).getPassword()))){
									sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
									sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
									return sa;
								}
							}
						}
					}
				}
			}
		}


		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali basic non trovato");
	}

	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(String aUser,String aPassword)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_BASIC.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) && 
									(aPassword.equals(sa.getInvocazionePorta().getCredenziali(z).getPassword()))){
								sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
								sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
								return sa;
							}
						}
					}
				}
			}
		}
		
		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali basic non trovato");
	}
	
	
	/**
	 * Restituisce Il servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param idPD Identificatore della porta delegata.
	 * @param aSubject Subject utilizzato nella connessione HTTPS.
	 * @return Il servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(IDPortaDelegata idPD, String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(idPD !=null && idPD.getSoggettoFruitore()!=null & idPD.getLocationPD()!=null){
			// Cerco Prima come definizione interna ad una porta delegata di un soggetto
			// REFRESH verra' effettuato attraverso questa ricerca
			PortaDelegata pd = null;
			try{
				pd = getPortaDelegata(idPD);
			}catch(Exception e){}
			if(pd!=null){
				for(int j=0; j<pd.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = pd.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							//	Default: SSL
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(pd.getTipoSoggettoProprietario());
										sa.setNomeSoggettoProprietario(pd.getNomeSoggettoProprietario());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null && 
									CostantiConfigurazione.CREDENZIALE_SSL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(pd.getTipoSoggettoProprietario());
										sa.setNomeSoggettoProprietario(pd.getNomeSoggettoProprietario());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
						}
					}
				}
			}
		}

		//	Se il soggetto non e' null lo cerco poi come definizione esterna alla porta delegata dentro il soggetto
		if(idPD !=null && idPD.getSoggettoFruitore()!=null){
			// REFRESH verra' effettuato anche attraverso questa ricerca
			Soggetto soggetto = null;
			try{
				soggetto = getSoggetto(idPD.getSoggettoFruitore());
			}catch(Exception e){}
			if(soggetto!=null)
				for(int j=0; j<soggetto.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggetto.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							//	Default: SSL
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(soggetto.getTipo());
										sa.setNomeSoggettoProprietario(soggetto.getNome());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
									CostantiConfigurazione.CREDENZIALE_SSL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(soggetto.getTipo());
										sa.setNomeSoggettoProprietario(soggetto.getNome());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
						}
					}
				}
		}	

		// Se il soggetto e' null lo cerca in ogni soggetto...
		else{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
					if(sa.getInvocazionePorta()!=null){
						for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
							//	Default: SSL
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
										sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
							if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
									CostantiConfigurazione.CREDENZIALE_SSL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
								try{
									//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
									if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
										sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
										sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
										return sa;
									}	
								}catch(Exception e){
									throw new DriverConfigurazioneException(e.getMessage(),e);
								}
							}
						}
					}
				}
			}
		}

		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali ssl non trovato");
	}

	@Override
	public ServizioApplicativo getServizioApplicativoAutenticato(String aSubject) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						//	Default: SSL
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
							try{
								//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
								if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
									sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
									sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
									return sa;
								}	
							}catch(Exception e){
								throw new DriverConfigurazioneException(e.getMessage(),e);
							}
						}
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_SSL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							try{
								//if( aSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getSubject())){
								if(Utilities.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject)){
									sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
									sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
									return sa;
								}	
							}catch(Exception e){
								throw new DriverConfigurazioneException(e.getMessage(),e);
							}
						}
					}
				}
			}
		}
		
		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali ssl non trovato");
	}

	
	/**
     * Verifica l'esistenza di un servizio applicativo.
     *
     * @param idServizioApplicativo id del servizio applicativo
     * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
     */    
	@Override
	public ServizioApplicativo getServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(idServizioApplicativo==null){
			throw new DriverConfigurazioneException("IDServizioApplicativo non definito");
		}
		if(idServizioApplicativo.getIdSoggettoProprietario()==null){
			throw new DriverConfigurazioneException("IDServizioApplicativo.idSoggettoProprietario non definito");
		}
		if(idServizioApplicativo.getNome()==null){
			throw new DriverConfigurazioneException("IDServizioApplicativo.nome non definito");
		}
		
		Soggetto soggettoProprietario = this.getSoggetto(idServizioApplicativo.getIdSoggettoProprietario());
		for(int j=0; j<soggettoProprietario.sizeServizioApplicativoList(); j++){
			ServizioApplicativo sa = soggettoProprietario.getServizioApplicativo(j);
			if(idServizioApplicativo.getNome().equals(sa.getNome())){
				sa.setTipoSoggettoProprietario(soggettoProprietario.getTipo());
				sa.setNomeSoggettoProprietario(soggettoProprietario.getNome());
				return sa;
			}
		}
		
		throw new DriverConfigurazioneNotFound("Servizio Applicativo non trovato");
	}


	/**
     * Verifica l'esistenza di un servizio applicativo.
     *
     * @param idSoggetto id del soggetto proprietario
     * @param nomeServizioApplicativo nome del servizio applicativo
     * @return ServizioApplicativo
	 * @throws DriverRegistroServiziException
     */    
    @Override
	public ServizioApplicativo getServizioApplicativo(IDSoggetto idSoggetto,String nomeServizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
    	for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			if(idSoggetto.getTipo().equals(soggettoSearch.getTipo()) &&
					idSoggetto.getNome().equals(soggettoSearch.getNome()) ){
				for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
					ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
					if(sa.getNome().equals(nomeServizioApplicativo)){
						sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
						sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
						return sa;
					}
				}
			}
		}
		
		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali ssl non trovato");
    }
	
    
	/**
	 * Restituisce la lista degli identificativi dei servizi applicativi
	 * 
	 * @param filtroRicerca
	 * @return lista degli identificativi dei servizi applicativi
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	@Override
	public List<IDServizioApplicativo> getAllIdServiziApplicativi(
			FiltroRicercaServiziApplicativi filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		List<IDServizioApplicativo> listIDServiziApplicativi = new ArrayList<IDServizioApplicativo>();
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);
			for (int j = 0; j < soggetto.sizeServizioApplicativoList(); j++) {
				ServizioApplicativo sa = soggetto.getServizioApplicativo(j);
				
				String id = sa.getNome();
				
				if(filtroRicerca!=null){
					// Filtro By Data
					if(filtroRicerca.getMinDate()!=null){
						if(sa.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteApplicative](FiltroByMinDate) PortaApplicativa ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(sa.getOraRegistrazione().before(filtroRicerca.getMinDate())){
							continue;
						}
					}
					if(filtroRicerca.getMaxDate()!=null){
						if(soggetto.getOraRegistrazione()==null){
							this.log.debug("[getAllIdPorteApplicative](FiltroByMaxDate) PortaApplicativa ["+id+"] non valorizzato nell'ora-registrazione. Non inserito nella lista ritornata.");
							continue;
						}else if(soggetto.getOraRegistrazione().after(filtroRicerca.getMaxDate())){
							continue;
						}
					}
					// Filtro By Tipo e Nome
					if(filtroRicerca.getTipoSoggetto()!=null){
						if(soggetto.getTipo().equals(filtroRicerca.getTipoSoggetto()) == false){
							continue;
						}
					}
					if(filtroRicerca.getNomeSoggetto()!=null){
						if(soggetto.getNome().equals(filtroRicerca.getNomeSoggetto()) == false){
							continue;
						}
					}
				}
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(id);
				idSA.setIdSoggettoProprietario(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
				listIDServiziApplicativi.add(idSA);
			}
		}
		
		if(listIDServiziApplicativi.size()<=0){
			if(filtroRicerca!=null)
				throw new DriverConfigurazioneNotFound("ServiziApplicativi non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
			else
				throw new DriverConfigurazioneNotFound("ServiziApplicativi non trovate");
		}
		
		return listIDServiziApplicativi;
		
	}
    
    
    
    
    
    
	
	

	// CONFIGURAZIONE

	/**
	 * Restituisce la RoutingTable definita nella Porta di Dominio 
	 *
	 * @return RoutingTable
	 * 
	 */
	@Override
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException{

		refreshConfigurazioneXML();

		RoutingTable r = null;
		if(this.openspcoop.getConfigurazione().getRoutingTable()==null){
			r = new RoutingTable();
			r.setAbilitata(false);
		}else{
			r =  this.openspcoop.getConfigurazione().getRoutingTable();
			r.setAbilitata(true);
		}
		
		return r;
	}

	/**
	 * Restituisce l'accesso al registro definito nella Porta di Dominio 
	 *
	 * @return AccessoRegistro
	 * 
	 */
	@Override
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoRegistro()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoRegistro] Informazioni di accesso al RegistroServizi non trovate");
		
		return this.openspcoop.getConfigurazione().getAccessoRegistro();
	}
	
	/**
	 * Restituisce l'accesso alla configurazione definito nella Porta di Dominio 
	 *
	 * @return AccessoConfigurazione
	 * 
	 */
	@Override
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoConfigurazione] Informazioni di accesso alla configurazione non trovate");
		
		return this.openspcoop.getConfigurazione().getAccessoConfigurazione();
	}
	
	/**
	 * Restituisce l'accesso ai dati di autorizzazione definiti nella Porta di Dominio 
	 *
	 * @return AccessoDatiAutorizzazione
	 * 
	 */
	@Override
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoDatiAutorizzazione()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoDatiAutorizzazione] Informazioni di accesso ai dati di autorizzazione non trovate");
		
		return this.openspcoop.getConfigurazione().getAccessoDatiAutorizzazione();
		
	}

	
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteCooperazione] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getGestioneErrore()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteCooperazione] Informazioni per la gestione dell'errore non trovate");
		if(this.openspcoop.getConfigurazione().getGestioneErrore().getComponenteCooperazione()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteCooperazione] Informazioni per la gestione dell'errore per il componente di cooperazione non trovate");
	
		return this.openspcoop.getConfigurazione().getGestioneErrore().getComponenteCooperazione();
	}
	
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	@Override
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteIntegrazione] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getGestioneErrore()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteIntegrazione] Informazioni per la gestione dell'errore non trovate");
		if(this.openspcoop.getConfigurazione().getGestioneErrore().getComponenteIntegrazione()==null)
			throw new DriverConfigurazioneNotFound("[getGestioneErroreComponenteIntegrazione] Informazioni per la gestione dell'errore per il componente di cooperazione non trovate");
	
		return this.openspcoop.getConfigurazione().getGestioneErrore().getComponenteIntegrazione();
	}

	/**
	 * Restituisce la servizio sui ServiziAttivi definiti nella Porta di Dominio 
	 *
	 * @return ServiziAttivi
	 * 
	 */
	@Override
	public StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getServiziAttiviPdD] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getStatoServiziPdd()==null)
			throw new DriverConfigurazioneNotFound("[getServiziAttiviPdD] Configurazione servizi attivi sulla PdD non presente");
	
		return this.openspcoop.getConfigurazione().getStatoServiziPdd();
	}
	
	/**
	 * Restituisce le proprieta' di sistema utilizzate dalla PdD
	 *
	 * @return proprieta' di sistema
	 * 
	 */
	@Override
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getServiziAttiviPdD] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getSystemProperties()==null)
			throw new DriverConfigurazioneNotFound("[getServiziAttiviPdD] Configurazione System Properties non presenti");
	
		return this.openspcoop.getConfigurazione().getSystemProperties();
		
	}
	

	/**
	 * Restituisce la configurazione generale della Porta di Dominio 
	 *
	 * @return Configurazione
	 * 
	 */
	@Override
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneException{

		refreshConfigurazioneXML();

		return this.openspcoop.getConfigurazione();
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
			this.forzaRefreshConfigurazioneXML();
		}catch(Exception e){
			throw new CoreException("Connessione al registro non disponibile: "+e.getMessage(),e);
		}
	}




	@Override
	public Openspcoop2 getImmagineCompletaConfigurazionePdD() throws DriverConfigurazioneException{
		
		refreshConfigurazioneXML();		
		return this.openspcoop;
	}


}
