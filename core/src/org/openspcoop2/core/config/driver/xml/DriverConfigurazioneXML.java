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


package org.openspcoop2.core.config.driver.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
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
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.ValidazioneSemantica;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiErogazione;
import org.openspcoop2.core.id.IdentificativiFruizione;
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
		InputStream iStream = null;
		HttpURLConnection httpConn = null;
		if(this.configuration_path.startsWith("http://") || this.configuration_path.startsWith("file://")){
			try{ 
				URL url = new URL(this.configuration_path);
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
				throw new DriverConfigurazioneException("Riscontrato errore durante la creazione dell'inputStream della configurazione (HTTP) : \n\n"+e.getMessage());
			}
			this.lastModified = DateManager.getTimeMillis();
		}else{
			try{  
				iStream = new FileInputStream(this.configuration_path);
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
				throw new DriverConfigurazioneException("Riscontrato errore durante la lettura del file dove e' allocato la configurazione: "+e.getMessage());
			}
		}


		/* ---- Unmarshall del file di configurazione ---- */
		try{  
			org.openspcoop2.core.config.utils.serializer.JaxbDeserializer deserializer = new org.openspcoop2.core.config.utils.serializer.JaxbDeserializer();
			this.openspcoop = deserializer.readOpenspcoop2(iStream);
		} catch(Exception e) {
			try{  
				if(iStream!=null)
					iStream.close();
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
			this.validatoreConfigurazione = new ValidatoreXSD(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.log,DriverConfigurazioneXML.class.getResourceAsStream("/config.xsd"));
		}catch (Exception e) {
			this.log.info("Riscontrato errore durante l'inizializzazione dello schema della configurazione di OpenSPCoop: "+e.getMessage(),e);
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
				ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(this.openspcoop,this.tipiConnettori,
						this.tipiSoggetti,this.tipiServiziSoap,this.tipiServiziRest,
						this.tipoMsgDiagnosticiAppender,this.tipoTracciamentoAppender,this.tipoDumpAppender,
						this.tipoAutenticazionePortaDelegata,this.tipoAutenticazionePortaApplicativa,
						this.tipoAutorizzazionePortaDelegata,this.tipoAutorizzazionePortaApplicativa,
						this.tipoAutorizzazioneContenutoPortaDelegata,this.tipoAutorizzazioneContenutoPortaApplicativa,
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
	private String[] tipiServiziSoap = null;
	private String[] tipiServiziRest = null;
	private String[] tipoMsgDiagnosticiAppender = null;
	private String[] tipoTracciamentoAppender = null;
	private String[] tipoDumpAppender = null;
	private String[] tipoAutenticazionePortaDelegata = null;
	private String[] tipoAutenticazionePortaApplicativa = null;
	private String[] tipoAutorizzazionePortaDelegata = null;
	private String[] tipoAutorizzazionePortaApplicativa = null;
	private String[] tipoAutorizzazioneContenutoPortaDelegata = null;
	private String[] tipoAutorizzazioneContenutoPortaApplicativa = null;
	private String[] tipoIntegrazionePD = null;
	private String[] tipoIntegrazionePA = null;
	public void abilitazioneValidazioneSemanticaDuranteModificaXML(String[] tipiConnettori,
			String[] tipiSoggetti,String[] tipiServiziSoap,String[] tipiServiziRest,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,String[]tipoDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA){
		this.validazioneSemanticaDuranteModificaXML = true;
		
		this.tipiConnettori = tipiConnettori;
		this.tipiSoggetti = tipiSoggetti;
		this.tipiServiziSoap = tipiServiziSoap;
		this.tipiServiziRest = tipiServiziRest;
		this.tipoMsgDiagnosticiAppender= tipoMsgDiagnosticiAppender;
		this.tipoTracciamentoAppender=tipoTracciamentoAppender;
		this.tipoDumpAppender=tipoDumpAppender;
		this.tipoAutenticazionePortaDelegata=tipoAutenticazionePortaDelegata;
		this.tipoAutenticazionePortaApplicativa=tipoAutenticazionePortaApplicativa;
		this.tipoAutorizzazionePortaDelegata=tipoAutorizzazionePortaDelegata;
		this.tipoAutorizzazionePortaApplicativa=tipoAutorizzazionePortaApplicativa;
		this.tipoAutorizzazioneContenutoPortaDelegata=tipoAutorizzazioneContenutoPortaDelegata;
		this.tipoAutorizzazioneContenutoPortaApplicativa=tipoAutorizzazioneContenutoPortaApplicativa;
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
	public List<IDSoggetto> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{	

		refreshConfigurazioneXML();

		List<IDSoggetto> lista = new ArrayList<IDSoggetto>();
		try{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggetto = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggetto.sizePortaApplicativaList(); j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					if(pa.getSoggettoVirtuale()!=null && 
							pa.getSoggettoVirtuale().getTipo()!=null &&
							pa.getSoggettoVirtuale().getNome()!=null){
						IDSoggetto soggettoVirtuale = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome());
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
	public List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		List<IDServizio> lista = new ArrayList<IDServizio>();
		HashSet<String> unique = new HashSet<String>();
		try{
			for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
				Soggetto soggetto = this.openspcoop.getSoggetto(i);
				for(int j=0; j<soggetto.sizePortaApplicativaList(); j++){
					PortaApplicativa pa = soggetto.getPortaApplicativa(j);
					if(pa.getSoggettoVirtuale()!=null && 
							pa.getSoggettoVirtuale().getTipo()!=null &&
							pa.getSoggettoVirtuale().getNome()!=null){
						IDServizio s = IDServizioUtils.buildIDServizio(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
								new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome()), 
								pa.getServizio().getVersione());
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

	private IDPortaDelegata convertToIDPortaDelegata(PortaDelegata pd) throws DriverConfigurazioneException{
		
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(pd.getNome());
		
		IdentificativiFruizione idFruizione = new IdentificativiFruizione();
		
		IDSoggetto soggettoFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
		idFruizione.setSoggettoFruitore(soggettoFruitore);
		
		try{
			IDServizio idServizio = IDServizioUtils.buildIDServizio(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
					new IDSoggetto(pd.getSoggettoErogatore().getTipo(),pd.getSoggettoErogatore().getNome()), 
					pd.getServizio().getVersione()); 
			if(pd.getAzione()!=null && pd.getAzione().getNome()!=null && !"".equals(pd.getAzione().getNome())){
				idServizio.setAzione(pd.getAzione().getNome());	
			}
			idFruizione.setIdServizio(idServizio);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		idPD.setIdentificativiFruizione(idFruizione);
		
		return idPD;
	}
	
	@Override
	public IDPortaDelegata getIDPortaDelegata(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(nome == null)
			throw new DriverConfigurazioneException("[getIDPortaDelegata] Parametro Non Validi");

		refreshConfigurazioneXML();

		for(int i=0;i<this.openspcoop.sizeSoggettoList();i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);

			//	ricerca PD
			for(int j=0; j<soggetto.sizePortaDelegataList() ; j++){
				PortaDelegata pd = soggetto.getPortaDelegata(j);
				if(nome.equals(pd.getNome())){
					pd.setTipoSoggettoProprietario(soggetto.getTipo());
					pd.setNomeSoggettoProprietario(soggetto.getNome());
					return convertToIDPortaDelegata(pd);
				}
			}  
		}  

		throw new DriverConfigurazioneNotFound("Porta Delegata ["+nome+"] non esistente");
	}
	
	@Override
	public PortaDelegata getPortaDelegata(IDPortaDelegata idPD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		if(idPD == null)
			throw new DriverConfigurazioneException("[getPortaDelegata] Parametro idPD Non Validi");

		String nome = idPD.getNome();

		if( (nome == null) )
			throw new DriverConfigurazioneException("[getPortaDelegata] Parametri non Validi");

		// Il getIDPortaDelegata effettua il REFRESH XML
		IDPortaDelegata id = this.getIDPortaDelegata(nome);
		IDSoggetto soggettoProprietario = id.getIdentificativiFruizione().getSoggettoFruitore();
		
		// Il getSoggetto effettua il REFRESH XML
		Soggetto soggetto = null;
		try{
			soggetto = getSoggetto(soggettoProprietario);
		}catch(Exception e){}
		if(soggetto == null)
			throw new DriverConfigurazioneException("[getPortaDelegata] Soggetto fruitore ["+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+"] non esistente");

		// ricerca PD
		for(int j=0; j<soggetto.sizePortaDelegataList() ; j++){

			PortaDelegata pd = soggetto.getPortaDelegata(j);
			if(nome.equals(pd.getNome())){
				pd.setTipoSoggettoProprietario(soggetto.getTipo());
				pd.setNomeSoggettoProprietario(soggetto.getNome());
				return pd;
			}
		}  

		throw new DriverConfigurazioneNotFound("PortaDelegata ["+nome+"] non esistente");
	}

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
				
				if(filtroRicerca!=null){
					
					boolean porteDelegatePerAzioni = false;
					if(filtroRicerca.getNomePortaDelegante()!=null) {
						porteDelegatePerAzioni = true;
					}
					
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
					if(filtroRicerca.getVersioneServizio()!=null){
						if(pd.getServizio().getVersione()==null){
							continue;
						}
						if(pd.getServizio().getVersione().intValue() != filtroRicerca.getVersioneServizio().intValue()){
							continue;
						}
					}
					// Filtro By azione
					if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
						if(pd.getAzione().getNome()==null){
							continue;
						}
						if(pd.getAzione().getNome().equals(filtroRicerca.getAzione()) == false){
							continue;
						}
					}
					// Filtro By Ruoli
					if(filtroRicerca.getIdRuolo()!=null && filtroRicerca.getIdRuolo().getNome()!=null){
						if(pd.getRuoli()==null){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pd.getRuoli().sizeRuoloList(); z++) {
							if(filtroRicerca.getIdRuolo().getNome().equals(pd.getRuoli().getRuolo(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By Scope
					if(filtroRicerca.getIdScope()!=null && filtroRicerca.getIdScope().getNome()!=null){
						if(pd.getScope()==null){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pd.getScope().sizeScopeList(); z++) {
							if(filtroRicerca.getIdScope().getNome().equals(pd.getScope().getScope(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By Autorizzazione Servizio Applicativo
					if(filtroRicerca.getNomeServizioApplicativo()!=null) {
						if(pd.sizeServizioApplicativoList()<=0){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pd.sizeServizioApplicativoList(); z++) {
							if(filtroRicerca.getNomeServizioApplicativo().equals(pd.getServizioApplicativo(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}						
					// Filtro By Stato
					if(filtroRicerca.getStato()!=null){
						if(pd.getStato()==null){
							// equivale a abilitato
							if(!StatoFunzionalita.ABILITATO.equals(filtroRicerca.getStato())){
								continue;
							}
						}				
						else if(!pd.getStato().equals(filtroRicerca.getStato())){
							continue;
						}
					}
					// Filtro By NomePortaDelegante
					if(porteDelegatePerAzioni) {
						if(pd.getAzione()==null || pd.getAzione().getNomePortaDelegante()==null){
							continue;
						}				
						else if(!pd.getAzione().getNomePortaDelegante().equals(filtroRicerca.getNomePortaDelegante())){
							continue;
						}
						if(filtroRicerca.getAzione()!=null) {
							if(pd.getAzione().getAzioneDelegataList().size()<=0){
								continue;
							}
							if(pd.getAzione().getAzioneDelegataList().contains(filtroRicerca.getAzione()) == false){
								continue;
							}
						}
					}
				}
				
				pd.setTipoSoggettoProprietario(soggetto.getTipo());
				pd.setNomeSoggettoProprietario(soggetto.getNome());
				listIDPorteDelegate.add(this.convertToIDPortaDelegata(pd));
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
	
	private IDPortaApplicativa convertToIDPortaApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException{
		
		IDPortaApplicativa idPA = new IDPortaApplicativa();
		idPA.setNome(pa.getNome());
		
		IdentificativiErogazione idErogazione = new IdentificativiErogazione();
		
		if(pa.getSoggettoVirtuale()!=null){
			IDSoggetto soggettoVirtuale = new IDSoggetto(pa.getSoggettoVirtuale().getTipo(),pa.getSoggettoVirtuale().getNome());
			idErogazione.setSoggettoVirtuale(soggettoVirtuale);
		}
		
		try{
			IDServizio idServizio = IDServizioUtils.buildIDServizio(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
					new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()), 
					pa.getServizio().getVersione()); 
			if(pa.getAzione()!=null && pa.getAzione().getNome()!=null && !"".equals(pa.getAzione().getNome())){
				idServizio.setAzione(pa.getAzione().getNome());	
			}
			idErogazione.setIdServizio(idServizio);
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		idPA.setIdentificativiErogazione(idErogazione);
		
		return idPA;
	}
	
	@Override
	public IDPortaApplicativa getIDPortaApplicativa(String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(nome == null)
			throw new DriverConfigurazioneException("[getIDPortaApplicativa] Parametro Non Validi");

		refreshConfigurazioneXML();

		for(int i=0;i<this.openspcoop.sizeSoggettoList();i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);

			//	ricerca PA
			for(int j=0; j<soggetto.sizePortaApplicativaList() ; j++){
				PortaApplicativa pa = soggetto.getPortaApplicativa(j);
				if(nome.equals(pa.getNome())){				
					pa.setTipoSoggettoProprietario(soggetto.getTipo());
					pa.setNomeSoggettoProprietario(soggetto.getNome());
					return convertToIDPortaApplicativa(pa);
				}
			}  
		}  

		throw new DriverConfigurazioneNotFound("Porta Applicativa ["+nome+"] non esistente");
	}
	
	@Override
	public PortaApplicativa getPortaApplicativa(IDPortaApplicativa idPA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		if(idPA == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idPA Non Validi");

		String nome = idPA.getNome();

		if( (nome == null) )
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri non Validi");

		// Il getIDPortaApplicativa effettua il REFRESH XML
		IDPortaApplicativa id = this.getIDPortaApplicativa(nome);
		IDSoggetto soggettoProprietario = id.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore();
		
		// Il getSoggetto effettua il REFRESH XML
		Soggetto soggetto = null;
		try{
			soggetto = getSoggetto(soggettoProprietario);
		}catch(Exception e){}
		if(soggetto == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Soggetto fruitore ["+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+"] non esistente");

		// ricerca PA
		for(int j=0; j<soggetto.sizePortaApplicativaList() ; j++){

			PortaApplicativa pa = soggetto.getPortaApplicativa(j);
			if(nome.equals(pa.getNome())){
				pa.setTipoSoggettoProprietario(soggetto.getTipo());
				pa.setNomeSoggettoProprietario(soggetto.getNome());
				return pa;
			}
		}  

		throw new DriverConfigurazioneNotFound("PortaApplicativa ["+nome+"] non esistente");
	}
	
	@Override
	public List<PortaApplicativa> getPorteApplicative(IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getPorteApplicative_engine(idServizio, null, ricercaPuntuale);
	}
	
	@Override
	public List<PortaApplicativa> getPorteApplicativeVirtuali(IDSoggetto soggettoVirtuale,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this._getPorteApplicative_engine(idServizio, soggettoVirtuale, ricercaPuntuale);
	}
	
	private List<PortaApplicativa> _getPorteApplicative_engine(IDServizio service,IDSoggetto soggettoVirtuale,boolean ricercaPuntuale) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(service == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametro idServizio non definito");
		
		IDSoggetto soggettoErogatore = service.getSoggettoErogatore();
		if(soggettoErogatore == null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri non validi (Soggetto Erogatore is null)");
	
		String servizio = service.getNome();
		String tipoServizio = service.getTipo();
		Integer versioneServizio = service.getVersione();
		String azione = service.getAzione();
		if((servizio==null)||(tipoServizio==null)||versioneServizio==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa] Parametri (Servizio) non validi");

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
		List<PortaApplicativa> paSenzaAzioneList = new ArrayList<PortaApplicativa>();
		List<PortaApplicativa> paList = new ArrayList<PortaApplicativa>();
		for(int j=0; j<soggetto.sizePortaApplicativaList() ; j++){

			PortaApplicativa pa = soggetto.getPortaApplicativa(j);

			boolean paMatchaCriteriDiRicerca = false;
			if(soggettoVirtuale==null){
				paMatchaCriteriDiRicerca = servizio.equals(pa.getServizio().getNome()) && 
										   tipoServizio.equals(pa.getServizio().getTipo()) &&
										   versioneServizio.intValue() == pa.getServizio().getVersione().intValue();
			}else{
				paMatchaCriteriDiRicerca = servizio.equals(pa.getServizio().getNome()) && 
				                           tipoServizio.equals(pa.getServizio().getTipo()) &&
				                           versioneServizio.intValue() == pa.getServizio().getVersione().intValue() &&
				                           pa.getSoggettoVirtuale()!=null &&
				                           soggettoVirtuale.getTipo().equals(pa.getSoggettoVirtuale().getTipo()) &&
				                           soggettoVirtuale.getNome().equals(pa.getSoggettoVirtuale().getNome());
			}
			if(paMatchaCriteriDiRicerca) {
				if(pa.getAzione()!=null && PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(pa.getAzione().getIdentificazione())) {
					paMatchaCriteriDiRicerca = false;
				}
			}
				
				
			if(paMatchaCriteriDiRicerca){

				// ricerca di una porta applicativa senza azione
				if(azione==null){
					if(pa.getAzione() == null){
						pa.setTipoSoggettoProprietario(soggetto.getTipo());
						pa.setNomeSoggettoProprietario(soggetto.getNome());
						paList.add(pa);
					}
				}
				// ricerca di una porta applicativa con azione
				else{
					if(pa.getAzione() == null){
						PortaApplicativa paSenzaAzione = pa; // potenziale porta applicativa che mappa solo il servizio (se non esiste)
						paSenzaAzione.setTipoSoggettoProprietario(soggetto.getTipo());
						paSenzaAzione.setNomeSoggettoProprietario(soggetto.getNome());
						paSenzaAzioneList.add(paSenzaAzione);
					}
					else if(azione.equals(pa.getAzione().getNome())){
						pa.setTipoSoggettoProprietario(soggetto.getTipo());
						pa.setNomeSoggettoProprietario(soggetto.getNome());
						paList.add(pa);
					}
				}
			}
		}  
		
		if(paList.size()>0){
			return paList;
		}
		
		if(paSenzaAzioneList.size()>0 && ricercaPuntuale==false)
			return paSenzaAzioneList;

		throw new DriverConfigurazioneNotFound("PorteApplicative non esistenti");
	}
	
	@Override
	public Map<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(IDServizio idServizio)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(idServizio==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro idServizio non valido");
		if(idServizio.getSoggettoErogatore()==null)
			throw new DriverConfigurazioneException("[getPortaApplicativa_SoggettiVirtuali] Parametro Soggetto Erogatore non valido");

		refreshConfigurazioneXML();

		Hashtable<IDSoggetto,PortaApplicativa> paConSoggetti = new Hashtable<IDSoggetto,PortaApplicativa>();
		IDSoggetto soggettoVirtuale = idServizio.getSoggettoErogatore();	
		String servizio = idServizio.getNome();
		String tipoServizio = idServizio.getTipo();
		Integer versioneServizio = idServizio.getVersione();
		String azione = idServizio.getAzione();
		if((servizio==null)||(tipoServizio==null)||versioneServizio==null)
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
								tipoServizio.equals(pa.getServizio().getTipo()) &&
								versioneServizio.intValue() == pa.getServizio().getVersione().intValue()){

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

	@Override
	public List<IDPortaApplicativa> getAllIdPorteApplicative(
			FiltroRicercaPorteApplicative filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		List<IDPortaApplicativa> listIDPorteApplicative = new ArrayList<IDPortaApplicativa>();
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggetto = this.openspcoop.getSoggetto(i);
			for (int j = 0; j < soggetto.sizePortaApplicativaList(); j++) {
				PortaApplicativa pa = soggetto.getPortaApplicativa(j);
				
				String id = pa.getNome();
				
				if(filtroRicerca!=null){
					
					boolean porteDelegatePerAzioni = false;
					if(filtroRicerca.getNomePortaDelegante()!=null) {
						porteDelegatePerAzioni = true;
					}
					
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
					if(filtroRicerca.getVersioneServizio()!=null){
						if(pa.getServizio().getVersione()==null){
							continue;
						}
						if(pa.getServizio().getVersione().intValue() != filtroRicerca.getVersioneServizio().intValue()){
							continue;
						}
					}
					// Filtro By azione
					if(!porteDelegatePerAzioni && filtroRicerca.getAzione()!=null){
						if(pa.getAzione().getNome()==null){
							continue;
						}
						if(pa.getAzione().getNome().equals(filtroRicerca.getAzione()) == false){
							continue;
						}
					}
					// Filtro By Ruoli
					if(filtroRicerca.getIdRuolo()!=null && filtroRicerca.getIdRuolo().getNome()!=null){
						if(pa.getRuoli()==null){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pa.getRuoli().sizeRuoloList(); z++) {
							if(filtroRicerca.getIdRuolo().getNome().equals(pa.getRuoli().getRuolo(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By Scope
					if(filtroRicerca.getIdScope()!=null && filtroRicerca.getIdScope().getNome()!=null){
						if(pa.getScope()==null){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pa.getScope().sizeScopeList(); z++) {
							if(filtroRicerca.getIdScope().getNome().equals(pa.getScope().getScope(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By SoggettoAutorizzato
					if(filtroRicerca.getIdSoggettoAutorizzato()!=null &&
							(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null || 
							filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null)){
						if(pa.getSoggetti()==null || pa.getSoggetti().sizeSoggettoList()<=0) {
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pa.getSoggetti().sizeSoggettoList(); z++) {
							if(filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null &&
									!filtroRicerca.getIdSoggettoAutorizzato().getTipo().equals(pa.getSoggetti().getSoggetto(z).getTipo())){
								continue;
							}
							if(filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null &&
									!filtroRicerca.getIdSoggettoAutorizzato().getNome().equals(pa.getSoggetti().getSoggetto(z).getNome())){
								continue;
							}
							contains = true;
							break;
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By ServizioApplicativoAutorizzato (ricerca puntuale)
					if(filtroRicerca.getIdServizioApplicativoAutorizzato()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getNome()!=null &&
							filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario()!=null &&
							filtroRicerca.getIdSoggettoAutorizzato().getTipo()!=null &&
							filtroRicerca.getIdSoggettoAutorizzato().getNome()!=null
							){
						if(pa.getServiziApplicativiAutorizzati()==null || pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()<=0) {
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); z++) {
							if(!filtroRicerca.getIdServizioApplicativoAutorizzato().getNome().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(z).getNome())){
								continue;
							}
							if(!filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getTipo().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(z).getTipoSoggettoProprietario())){
								continue;
							}	
							if(!filtroRicerca.getIdServizioApplicativoAutorizzato().getIdSoggettoProprietario().getNome().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(z).getNomeSoggettoProprietario())){
								continue;
							}
							contains = true;
							break;
						}
						if(!contains){
							continue;
						}
					}
					// Filtro By Stato
					if(filtroRicerca.getStato()!=null){
						if(pa.getStato()==null){
							// equivale a abilitato
							if(!StatoFunzionalita.ABILITATO.equals(filtroRicerca.getStato())){
								continue;
							}
						}				
						else if(!pa.getStato().equals(filtroRicerca.getStato())){
							continue;
						}
					}
					// Filtro By NomePortaDelegante
					if(porteDelegatePerAzioni) {
						if(pa.getAzione()==null || pa.getAzione().getNomePortaDelegante()==null){
							continue;
						}				
						else if(!pa.getAzione().getNomePortaDelegante().equals(filtroRicerca.getNomePortaDelegante())){
							continue;
						}
						if(filtroRicerca.getAzione()!=null) {
							if(pa.getAzione().getAzioneDelegataList().size()<=0){
								continue;
							}
							if(pa.getAzione().getAzioneDelegataList().contains(filtroRicerca.getAzione()) == false){
								continue;
							}
						}
					}
				}
				
				pa.setTipoSoggettoProprietario(soggetto.getTipo());
				pa.setNomeSoggettoProprietario(soggetto.getNome());
				listIDPorteApplicative.add(this.convertToIDPortaApplicativa(pa));
				
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
	
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiBasic(String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(aUser==null){
			throw new DriverConfigurazioneException("Username non definito");
		}
		if(aPassword==null){
			throw new DriverConfigurazioneException("Password non definita");
		}
		
		boolean testInChiaro = false;
		ICrypt crypt = null;
		if(config==null || config.isBackwardCompatibility()) {
			testInChiaro = true;
		}
		if(config!=null) {
			try {
				crypt = CryptFactory.getCrypt(this.log, config);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
				
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_BASIC.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) ){
								
								String passwordSaved = sa.getInvocazionePorta().getCredenziali(z).getPassword();
								
								boolean found = false;
								if(testInChiaro) {
									found = aPassword.equals(passwordSaved);
								}
								if(!found && crypt!=null) {
									found = crypt.check(aPassword, passwordSaved);
								}
								
								if( found ) {
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
	public ServizioApplicativo getServizioApplicativoByCredenzialiApiKey(String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(aUser==null){
			throw new DriverConfigurazioneException("Username non definito");
		}
		if(aPassword==null){
			throw new DriverConfigurazioneException("Password non definita");
		}
		
		boolean testInChiaro = false;
		ICrypt crypt = null;
		if(config!=null) {
			try {
				crypt = CryptFactory.getCrypt(this.log, config);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		else {
			testInChiaro = true;
		}
				
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_APIKEY.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							if( (aUser.equals(sa.getInvocazionePorta().getCredenziali(z).getUser())) ){
								
								if(appId) {
									if(!sa.getInvocazionePorta().getCredenziali(z).isAppId()) {
										continue;
									}
								}
								else {
									if(sa.getInvocazionePorta().getCredenziali(z).isAppId()) {
										continue;
									}
								}
								
								String passwordSaved = sa.getInvocazionePorta().getCredenziali(z).getPassword();
								
								boolean found = false;
								if(testInChiaro) {
									found = aPassword.equals(passwordSaved);
								}
								if(!found && crypt!=null) {
									found = crypt.check(aPassword, passwordSaved);
								}
								
								if( found ) {
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
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(String aSubject, String aIssuer) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(aSubject==null){
			throw new DriverConfigurazioneException("Subject non definito");
		}
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						//	Default: SSL
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
							try{
								if(sa.getInvocazionePorta().getCredenziali(z).getSubject()==null) {
									continue;
								}
								boolean subjectValid = CertificateUtils.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject, PrincipalType.subject, this.log);
								boolean issuerValid = true;
								if(aIssuer!=null) {
									if(sa.getInvocazionePorta().getCredenziali(z).getIssuer()==null) {
										issuerValid = false;
									}else {
										issuerValid = CertificateUtils.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getIssuer(), aIssuer, PrincipalType.issuer, this.log);
									}
								}
								else {
									issuerValid = (sa.getInvocazionePorta().getCredenziali(z).getIssuer() == null);
								}
								if(subjectValid && issuerValid){
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
								if(sa.getInvocazionePorta().getCredenziali(z).getSubject()==null) {
									continue;
								}
								boolean subjectValid = CertificateUtils.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getSubject(), aSubject, PrincipalType.subject, this.log);
								boolean issuerValid = true;
								if(aIssuer!=null) {
									if(sa.getInvocazionePorta().getCredenziali(z).getIssuer()==null) {
										issuerValid = false;
									}else {
										issuerValid = CertificateUtils.sslVerify(sa.getInvocazionePorta().getCredenziali(z).getIssuer(), aIssuer, PrincipalType.issuer, this.log);
									}
								}
								else {
									issuerValid = (sa.getInvocazionePorta().getCredenziali(z).getIssuer() == null);
								}
								if(subjectValid && issuerValid){
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
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiSsl(CertificateInfo aCertificate, boolean aStrictVerifier) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(aCertificate==null){
			throw new DriverConfigurazioneException("Certificato non definito");
		}
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						//	Default: SSL
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo() == null){
							try{
								if(sa.getInvocazionePorta().getCredenziali(z).getCertificate()==null) {
									continue;
								}
								if(aStrictVerifier != sa.getInvocazionePorta().getCredenziali(z).isCertificateStrictVerification()) {
									continue;
								}
								String cnSubject = aCertificate.getSubject().getCN();
								if(cnSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getCnSubject())) {
									Certificate certificato = ArchiveLoader.load(ArchiveType.CER, sa.getInvocazionePorta().getCredenziali(z).getCertificate(), 0, null);
									if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
										sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
										sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
										return sa;
									}
								}
							}catch(Exception e){
								throw new DriverConfigurazioneException(e.getMessage(),e);
							}
						}
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_SSL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							try{
								if(sa.getInvocazionePorta().getCredenziali(z).getCertificate()==null) {
									continue;
								}
								if(aStrictVerifier != sa.getInvocazionePorta().getCredenziali(z).isCertificateStrictVerification()) {
									continue;
								}
								String cnSubject = aCertificate.getSubject().getCN();
								if(cnSubject.equals(sa.getInvocazionePorta().getCredenziali(z).getCnSubject())) {
									Certificate certificato = ArchiveLoader.load(ArchiveType.CER, sa.getInvocazionePorta().getCredenziali(z).getCertificate(), 0, null);
									if(aCertificate.equals(certificato.getCertificate(),aStrictVerifier)) {
										sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
										sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
										return sa;
									}	
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
	
	@Override
	public ServizioApplicativo getServizioApplicativoByCredenzialiPrincipal(String aUserPrincipal) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(aUserPrincipal==null){
			throw new DriverConfigurazioneException("Principal non definito");
		}
		
		for(int i=0; i<this.openspcoop.sizeSoggettoList(); i++){
			Soggetto soggettoSearch = this.openspcoop.getSoggetto(i);
			for(int j=0; j<soggettoSearch.sizeServizioApplicativoList(); j++){
				ServizioApplicativo sa = soggettoSearch.getServizioApplicativo(j);
				if(sa.getInvocazionePorta()!=null){
					for(int z=0;z<sa.getInvocazionePorta().sizeCredenzialiList();z++){
						if(sa.getInvocazionePorta().getCredenziali(z).getTipo()!=null &&
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.equals(sa.getInvocazionePorta().getCredenziali(z).getTipo())){
							if( aUserPrincipal.equals(sa.getInvocazionePorta().getCredenziali(z).getUser()) ){
								sa.setTipoSoggettoProprietario(soggettoSearch.getTipo());
								sa.setNomeSoggettoProprietario(soggettoSearch.getNome());
								return sa;
							}
						}
					}
				}
			}
		}
		
		throw new DriverConfigurazioneNotFound("Servizio Applicativo cercato con credenziali principal non trovato");
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
					// Filtro By Ruoli
					if(filtroRicerca.getIdRuolo()!=null && filtroRicerca.getIdRuolo().getNome()!=null){
						if(sa.getInvocazionePorta()==null){
							continue;
						}
						if(sa.getInvocazionePorta().getRuoli()==null){
							continue;
						}
						boolean contains = false;
						for (int z = 0; z < sa.getInvocazionePorta().getRuoli().sizeRuoloList(); z++) {
							if(filtroRicerca.getIdRuolo().getNome().equals(sa.getInvocazionePorta().getRuoli().getRuolo(z).getNome())){
								contains = true;
								break;
							}
						}
						if(!contains){
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
	 * Restituisce l'accesso ai dati di autenticazione definiti nella Porta di Dominio 
	 *
	 * @return AccessoDatiAutenticazione
	 * 
	 */
	@Override
	public AccessoDatiAutenticazione getAccessoDatiAutenticazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoDatiAutenticazione()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoDatiAutenticazione] Informazioni di accesso ai dati di autenticazione non trovate");
		
		return this.openspcoop.getConfigurazione().getAccessoDatiAutenticazione();
		
	}

	
	/**
	 * Restituisce l'accesso ai dati di gestione token
	 *
	 * @return AccessoDatiGestioneToken
	 * 
	 */
	@Override
	public AccessoDatiGestioneToken getAccessoDatiGestioneToken() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoDatiGestioneToken()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoDatiGestioneToken] Informazioni di accesso ai dati di gestione dei token non trovati");
		
		return this.openspcoop.getConfigurazione().getAccessoDatiGestioneToken();
		
	}
	
	/**
	 * Restituisce l'accesso ai dati per la gestione dei keystore
	 *
	 * @return AccessoDatiKeystore
	 * 
	 */
	@Override
	public AccessoDatiKeystore getAccessoDatiKeystore() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
	
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoDatiKeystore()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoDatiKeystore] Informazioni di accesso ai dati per la gestione dei keystore non trovati");
		
		return this.openspcoop.getConfigurazione().getAccessoDatiKeystore();
		
	}
	
	/**
	 * Restituisce l'accesso ai dati per la gestione della consegna agli applicativi
	 *
	 * @return AccessoDatiKeystore
	 * 
	 */
	@Override
	public AccessoDatiConsegnaApplicativi getAccessoDatiConsegnaApplicativi() throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
	
		refreshConfigurazioneXML();

		if(this.openspcoop.getConfigurazione().getAccessoDatiConsegnaApplicativi()==null)
			throw new DriverConfigurazioneNotFound("[getAccessoDatiKeystore] Informazioni di accesso ai dati per la gestione della consegna agli applicativi non trovati");
		
		return this.openspcoop.getConfigurazione().getAccessoDatiConsegnaApplicativi();
		
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
	 * Restituisce le proprieta' generiche utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getGenericPropertiesList()==null || this.openspcoop.getConfigurazione().getGenericPropertiesList().size()<=0)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti");
	
		return this.openspcoop.getConfigurazione().getGenericPropertiesList();
		
	}
	
	/**
	 * Restituisce le proprieta' generiche di una tipologia utilizzate dalla PdD
	 *
	 * @return proprieta' generiche
	 * 
	 */
	@Override
	public List<GenericProperties> getGenericProperties(String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getGenericPropertiesList()==null || this.openspcoop.getConfigurazione().getGenericPropertiesList().size()<=0)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti");
		 
		List<GenericProperties> list = new ArrayList<>();
		for (GenericProperties genericProperties : this.openspcoop.getConfigurazione().getGenericPropertiesList()) {
			if(tipologia!=null && tipologia.equals(genericProperties.getTipologia())) {
				list.add(genericProperties);
			}
		}
		
		if(list.size()<=0) {
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti con tipologia '"+tipologia+"'");
		}
		
		return list;
	}
	
	@Override
	public GenericProperties getGenericProperties(String tipologia, String name) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		refreshConfigurazioneXML();
		
		if(this.openspcoop.getConfigurazione()==null)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione non trovata");
		if(this.openspcoop.getConfigurazione().getGenericPropertiesList()==null || this.openspcoop.getConfigurazione().getGenericPropertiesList().size()<=0)
			throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti");
		 
		for (GenericProperties genericProperties : this.openspcoop.getConfigurazione().getGenericPropertiesList()) {
			if(tipologia!=null && tipologia.equals(genericProperties.getTipologia())) {
				if(genericProperties.getNome().equals(name)) {
					return genericProperties;
				}
			}
		}
		
		throw new DriverConfigurazioneNotFound("[getGenericProperties] Configurazione Generic Properties non presenti con tipologia '"+tipologia+"' e nome '"+name+"'");
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
