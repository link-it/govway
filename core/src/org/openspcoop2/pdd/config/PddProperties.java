/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.config;


import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;


/**
 * Questo file permette ridefinire alcune configurazioni della porta di dominio, 
 * definite normalmente in govway.properties o nella configurazione xml/db della porta,
 * con lo scopo di far interoperare correttamente la porta di dominio openspcoop con altre implementazioni.
 * L'implementazione di una pdd associata ad un soggetto viene indicata nella definizione della porta di dominio presente
 * nel registro dei servizi di OpenSPCoop. Tale informazione verra' utilizzata dalla PdD a runtime, per accedere a questo file di properties,
 * e verificare se vi sono qualche opzioni ridefinite per l'implementazione della porta di dominio del soggetto erogatore/fruitore del servizio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class PddProperties {	

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'govway.pdd.properties' */
	private PddInstanceProperties reader;

	/** Copia Statica */
	private static PddProperties pddProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private PddProperties(String location,String confDir) throws UtilsException {

		if(OpenSPCoop2Startup.initialize)
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		else
			this.log = LoggerWrapperFactory.getLogger(PddProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			if(location!=null){
				properties = new FileInputStream(location);
			}else{
				properties = PddProperties.class.getResourceAsStream("/govway.pdd.properties");
			}
			if(properties==null){
				throw new UtilsException("File '/govway.pdd.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			doError(e);
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){
		    	// close
		    }
		}

		this.reader = new PddInstanceProperties(propertiesReader, this.log, confDir);
	}
	private void doError(Exception e) throws UtilsException {
		this.log.error("Riscontrato errore durante la lettura del file 'govway.pdd.properties': "+e.getMessage(),e);
	    throw new UtilsException("PddProperties initialize error: "+e.getMessage(),e);
	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static boolean initialize(String location,String confDir){

		try {
		    PddProperties.pddProperties = new PddProperties(location,confDir);	
		    return true;
		}
		catch(Exception e) {
		    return false;
		}
	}
    
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di ClassNameProperties
	 * 
	 */
	public static PddProperties getInstance() throws OpenSPCoop2ConfigurationException{
	    if(PddProperties.pddProperties==null) {
	    	// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
	    	synchronized (PddProperties.class) {
	    		throw new OpenSPCoop2ConfigurationException("PddProperties non inizializzato");
	    	}
	    }
	    return PddProperties.pddProperties;
	}
    
	public static void updateLocalImplementation(Properties prop){
		PddProperties.pddProperties.reader.setLocalObjectImplementation(prop);
	}








	/* ********  M E T O D I  ******** */

	/**
	 * Ritorna la la proprieta' ridefinita per la porta di dominio passata come parametro.
	 *
	 * 
	 */
	public String getValidazioneBuste_Stato(String nomePdd){
		return this.get(nomePdd, ".validazioneBuste.stato");
	}
	public String getValidazioneBuste_Controllo(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.controllo");
	}
	public String getValidazioneBuste_ProfiloCollaborazione(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.profiloCollaborazione");
	}
	public String getValidazioneBuste_ManifestAttachments(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.manifestAttachments");
	}
	public String getValidazione_FiltroDuplicatiLetturaRegistro(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.filtroDuplicati.letturaRegistro");
	}
	public String getValidazione_ConfermaRicezioneLetturaRegistro(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.confermaRicezione.letturaRegistro");
	}
	public String getValidazione_ConsegnaInOrdineLetturaRegistro(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.consegnaInOrdine.letturaRegistro");
	}
	public String getValidazione_readQualifiedAttribute(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.readQualifiedAttribute");
	}
	public String getValidazione_ValidazioneIDBustaCompleta(String nomePdd){
		return this.get(nomePdd,".validazioneBuste.validazioneIDBustaCompleta");
	}
	
	
	public String getBusta_TempoTipo(String nomePdd){
		return this.get(nomePdd,".busta.tempo.tipo");
	}
	public String getBusta_AsincroniAttributiCorrelatiEnable(String nomePdd){
		return this.get(nomePdd,".busta.asincroni.attributiCorrelati.enable");
	}
	public String getBusta_CollaborazioneEnable(String nomePdd){
		return this.get(nomePdd,".busta.collaborazione.enable");
	}
	public String getBusta_IdRiferimentoRichiestaEnable(String nomePdd){
		return this.get(nomePdd,".busta.idRiferimentoRichiesta.enable");
	}
	public String getBusta_ConsegnaInOrdineEnable(String nomePdd){
		return this.get(nomePdd,".busta.consegnaInOrdine.enable");
	}
	public String getBusta_TrasmissioneEnable(String nomePdd){
		return this.get(nomePdd,".busta.trasmissione.enable");
	}
	public String getBusta_RiscontriEnable(String nomePdd){
		return this.get(nomePdd,".busta.riscontri.enable");
	}
	public String getBusta_FiltroduplicatiGenerazioneBustaErrore(String nomePdd){
		return this.get(nomePdd,".busta.filtroduplicati.generazioneBustaErrore");
	}
	
	
	
	public String getValidazioneContenutiApplicativi_Stato(String nomePdd){
		return this.get(nomePdd,".validazioneContenutiApplicativi.stato");
	}
	public String getValidazioneContenutiApplicativi_Tipo(String nomePdd){
		return this.get(nomePdd,".validazioneContenutiApplicativi.tipo");
	}
	public String getValidazioneContenutiApplicativi_AcceptMtomMessage(String nomePdd){
		return this.get(nomePdd,".validazioneContenutiApplicativi.acceptMtomMessage");
	}
	
	
	
	public String getMessageSecurity_ActorDefaultEnable(String nomePdd){
		return this.get(nomePdd,".messageSecurity.actorDefault.enable");
	}
	public String getMessageSecurity_ActorDefaultValue(String nomePdd){
		return this.get(nomePdd,".messageSecurity.actorDefault.valore");
	}

	
	
	
	private String get(String nomePdd,String proprieta){
		try{
			return this.reader.getValue(nomePdd+proprieta);
		}catch(Exception e){
			this.log.error("Errore durante la lettura della proprieta' '"+nomePdd+proprieta+"': "+e.getMessage(),e);
			return null;
		}
	}
}
