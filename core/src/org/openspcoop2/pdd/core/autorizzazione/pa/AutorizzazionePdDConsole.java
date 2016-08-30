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



package org.openspcoop2.pdd.core.autorizzazione.pa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.GestoreJNDI;

/**
 * Interfaccia che definisce un processo di autorizzazione per i soggetti e servizi applicativi.
 * I dati vengono presi dal database dell'applicazione ControlStation (/tools/web_interfaces/control_station)
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazionePdDConsole extends AbstractCore implements IAutorizzazionePortaApplicativa {

	/** Indicazione se il processo di autorizzazione e' stato inizializzato */
	private static boolean initialized = false;
	
	/** DataSource */
	private static DataSource dataSource=null;
	
	/** Indicazione se deve essere effettuato il controllo delle Pdd mittenti */
	private static String checkPdd = CostantiRegistroServizi.DISABILITATO.toString();
	
	/** Indicazione il tipo di controllo per la fruizione per servizio applicativo */
	private static String checkFruizioneServizioApplicativo = CostantiRegistroServizi.DISABILITATO.toString();
	
	/** Indicazione il tipo di controllo per la fruizione per ruolo */
	private static String checkFruizionePerRuolo = CostantiRegistroServizi.DISABILITATO.toString();
	
	/** Tipo di controllo */
	private final static String ALMOST_ONE = "almostOne";
	
	
	public static synchronized void initRisorse(){
		if(AutorizzazionePdDConsole.initialized == false){
			java.io.InputStream properties = null;
			try{
				
				// Carico file properties.
				properties = AutorizzazionePdDConsole.class.getResourceAsStream("/openspcoop2.autorizzazionePdDConsole.properties");
				if(properties==null)
					throw new Exception("File openspcoop2.autorizzazionePdDConsole.properties non trovato");
				Properties reader = new Properties();
				reader.load(properties);
				
				// Istanzio DataSource
				if(Utilities.readProperties("dataSource.property.", reader)==null)
					throw new Exception("Proprieta' per il contesto JNDI [dataSource.property.*] non correttamente definite");
				if(reader.getProperty("dataSource")==null)
					throw new Exception("Nome JNDI del DataSource non fornito");
				GestoreJNDI jndi = new GestoreJNDI(Utilities.readProperties("dataSource.property.", reader));
				AutorizzazionePdDConsole.dataSource = (DataSource) jndi.lookup(reader.getProperty("dataSource").trim());
				if(AutorizzazionePdDConsole.dataSource==null)
					throw new Exception("DataSource is null");
				
				// Proprieta' di configurazione checkPdd
				if(reader.getProperty("checkPdd")==null)
					throw new Exception("Proprieta' 'checkPdd' non fornita");
				if(		CostantiRegistroServizi.ABILITATO.equals(reader.getProperty("checkPdd").trim())==false && 
						CostantiRegistroServizi.DISABILITATO.equals(reader.getProperty("checkPdd").trim())==false)
					throw new Exception("Proprieta' 'checkPdd' non possiede un valore valido");
				AutorizzazionePdDConsole.checkPdd = reader.getProperty("checkPdd").trim();
				
				// Proprieta' di configurazione checkFruizioneServizioApplicativo
				if(reader.getProperty("checkFruizioneServizioApplicativo")==null)
					throw new Exception("Proprieta' 'checkFruizioneServizioApplicativo' non fornita");
				if(AutorizzazionePdDConsole.ALMOST_ONE.equals(reader.getProperty("checkFruizioneServizioApplicativo").trim())==false &&
						CostantiRegistroServizi.ABILITATO.equals(reader.getProperty("checkFruizioneServizioApplicativo").trim())==false && 
						CostantiRegistroServizi.DISABILITATO.equals(reader.getProperty("checkFruizioneServizioApplicativo").trim())==false)
					throw new Exception("Proprieta' 'checkFruizioneServizioApplicativo' non possiede un valore valido");
				AutorizzazionePdDConsole.checkFruizioneServizioApplicativo = reader.getProperty("checkFruizioneServizioApplicativo").trim();
				
				// Proprieta' di configurazione checkFruizionePerRuolo
				if(reader.getProperty("checkFruizionePerRuolo")==null)
					throw new Exception("Proprieta' 'checkFruizionePerRuolo' non fornita");
				if(AutorizzazionePdDConsole.ALMOST_ONE.equals(reader.getProperty("checkFruizionePerRuolo").trim())==false &&
						CostantiRegistroServizi.ABILITATO.equals(reader.getProperty("checkFruizionePerRuolo").trim())==false && 
						CostantiRegistroServizi.DISABILITATO.equals(reader.getProperty("checkFruizionePerRuolo").trim())==false)
					throw new Exception("Proprieta' 'checkFruizionePerRuolo' non possiede un valore valido");
				AutorizzazionePdDConsole.checkFruizionePerRuolo = reader.getProperty("checkFruizionePerRuolo").trim();
				
				AutorizzazionePdDConsole.initialized = true;
				
			}catch(Exception e){
				Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
				if(log==null)
					log = LoggerWrapperFactory.getLogger(AutorizzazionePdDConsole.class);
				log.error("Errore durante l'istanziazione della classe autorizzazionePdDConsole: "+e.getMessage(),e);
			}finally{
				try{
					properties.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	public static DataSource getDataSource() throws Exception{
		
		if(AutorizzazionePdDConsole.initialized==false)
			AutorizzazionePdDConsole.initRisorse();
		
		if(AutorizzazionePdDConsole.initialized==false)
			throw new Exception("DataSource non disponibile");
		else
			return AutorizzazionePdDConsole.dataSource;
	}
	
	
    @Override
	public EsitoAutorizzazioneCooperazione process(DatiInvocazionePortaApplicativa datiInvocazione){
    	
    	EsitoAutorizzazioneCooperazione esito = new EsitoAutorizzazioneCooperazione();
    	
    	Connection connectionDB = null;
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	String query = null;
    	
    	Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null)
			log = LoggerWrapperFactory.getLogger(AutorizzazionePdDConsole.class);
    	
    	try{
    		
    		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
    		IDServizio idServizio = datiInvocazione.getIdServizio();
    		
			Credenziali credenzialiPdDMittente = datiInvocazione.getCredenzialiPdDMittente();
			
			String identitaServizioApplicativoFruitore = null;
    		if(datiInvocazione.getIdentitaServizioApplicativoFruitore()!=null){
    			identitaServizioApplicativoFruitore = datiInvocazione.getIdentitaServizioApplicativoFruitore().getNome();
    		}
    		
    		String subjectServizioApplicativoFruitoreFromMessageSecurityHeader = datiInvocazione.getSubjectServizioApplicativoFruitoreFromMessageSecurityHeader();
    		
    		// Controllo parametri forniti
    		if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null)
    			throw new Exception("Dati soggetto fruitore non forniti");
    		if(idServizio==null || idServizio.getTipoServizio()==null || idServizio.getServizio()==null)
    			throw new Exception("Dati servizio non forniti");
    		if(idServizio.getSoggettoErogatore()==null || idServizio.getSoggettoErogatore().getTipo()==null || idServizio.getSoggettoErogatore().getNome()==null)
    			throw new Exception("Dati soggetto erogatore non forniti");
    		    		
    		// Ottengo connection DB
    		connectionDB = AutorizzazionePdDConsole.getDataSource().getConnection();
    		if(connectionDB==null){
    			throw new Exception("Connessione al Database non disponibile");
    		}
    		
    		// Tipo di Soggetto fruitore (Esterno o operativo)
    		query = "select "+CostantiDB.PDD+".tipo FROM "+CostantiDB.PDD+","+CostantiDB.SOGGETTI+" where "
    			+CostantiDB.SOGGETTI+".server="+CostantiDB.PDD+".nome AND "+
    			CostantiDB.SOGGETTI+".tipo_soggetto=? AND "+
    			CostantiDB.SOGGETTI+".nome_soggetto=?";
    		pstmt = connectionDB.prepareStatement(query);
			pstmt.setString(1,idSoggetto.getTipo());
			pstmt.setString(2,idSoggetto.getNome());
			rs = pstmt.executeQuery();
			if(rs.next() == false){
				rs.close();
    			pstmt.close();
    			String errore = "Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non appartiene a nessuna Porta di Dominio??? Non e' autorizzato ad invocare il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setServizioAutorizzato(false);
    			return esito;
			}
			String tipoPdd = rs.getString("tipo");
			boolean pddEsterna = false;
			if("esterno".equals(tipoPdd)){
				pddEsterna = true;
			}
			log.debug("Soggetto fruitore "+idSoggetto.toString()+" appartiene ad una pdd di tipo ["+tipoPdd+"]");
			rs.close();
			pstmt.close();
    		
    		// Controllo associazione Pdd con SoggettoFruitore
			// Il controllo viene effettuato solo se abilitato e il subject e' presente (in modo che venga fatto solo dove sono presenti connessioni https)
			// Per funzionare deve essere disabilitato l'accesso via http non ssl.
    		if(CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkPdd) && credenzialiPdDMittente!=null){
    			
        		String pdd = null;
        		if(credenzialiPdDMittente!=null){
        			if(credenzialiPdDMittente.getSubject()!=null){
        				pdd = credenzialiPdDMittente.getSubject();
        			}
        		}
    			
    			// Autenticazione SSL deve essere LIKE
				Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(pdd);
    			
    			query = "select * from "+CostantiDB.PDD+","+CostantiDB.SOGGETTI+" where "+
    			CostantiDB.SOGGETTI+".server="+CostantiDB.PDD+".nome "+
    			"and "+CostantiDB.SOGGETTI+".tipo_soggetto=? "+
    			"and "+CostantiDB.SOGGETTI+".nome_soggetto=?";
    			
    			Enumeration<String> keys = hashSubject.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					String value = hashSubject.get(key);
					String ricerca = "/"+Utilities.formatKeySubject(key)+"="+Utilities.formatValueSubject(value)+"/";
					query = query +" and "+CostantiDB.PDD+".subject like '%"+ricerca+"%'";
				}
    			
    			pstmt = connectionDB.prepareStatement(query);
    			pstmt.setString(1,idSoggetto.getTipo());
    			pstmt.setString(2,idSoggetto.getNome());
    			rs = pstmt.executeQuery();
    			boolean match = false;
				while(rs.next()){
					// Possono esistere piu' pdd che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
					String subjectPotenziale =  rs.getString("subject");
					if(Utilities.sslVerify(subjectPotenziale, pdd)){
						match = true;
						break;
					}
				}
				if(match == false){
    				rs.close();
        			pstmt.close();
    				String errore = "Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non appartiene alla Porta di Dominio con subject ["+pdd+"], quindi non e' autorizzato ad invocare il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();    			
        			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_FALSIFICAZIONE_MITTENTE));
        			esito.setServizioAutorizzato(false);
        			return esito;
    			}
    			rs.close();
    			pstmt.close();
    		}
    		
    		boolean autorizzato = false;
    		
    		// Controllo Fruizione Servizio Applicativo
    		if(CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizioneServizioApplicativo) ||
    				AutorizzazionePdDConsole.ALMOST_ONE.equals(AutorizzazionePdDConsole.checkFruizioneServizioApplicativo)	){
    			
    			query = "select * from "+CostantiDB.POLITICHE_SICUREZZA+","+CostantiDB.SOGGETTI+","+CostantiDB.SERVIZI_APPLICATIVI+" where "
    				// id fruitore
    				+CostantiDB.POLITICHE_SICUREZZA+".id_fruitore="+CostantiDB.SOGGETTI+".id AND "
    				+CostantiDB.SOGGETTI+".tipo_soggetto=? AND "
    				+CostantiDB.SOGGETTI+".nome_soggetto=? AND "
    				// id servizio applicativo
    				+CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto="+CostantiDB.SOGGETTI+".id AND "
    				+CostantiDB.POLITICHE_SICUREZZA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id AND "
    				// id servizio 
    				+CostantiDB.POLITICHE_SICUREZZA+".id_servizio in ( select servizi.id from "+CostantiDB.SERVIZI+","+CostantiDB.SOGGETTI+" where "+
    								CostantiDB.SERVIZI+".tipo_servizio=? AND "+CostantiDB.SERVIZI+".nome_servizio=? AND "+
    								CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id AND "+CostantiDB.SOGGETTI+".tipo_soggetto=? AND "
    								+CostantiDB.SOGGETTI+".nome_soggetto=? )";
    			boolean checkFallitoPerMancanzaIdentitaServizioApplicativo = false;
    			if( (!pddEsterna) && CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizioneServizioApplicativo)){
    				if(identitaServizioApplicativoFruitore!=null && subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null)
    					query = query + " AND ( ("+CostantiDB.SERVIZI_APPLICATIVI+".nome=?) OR ("+CostantiDB.SERVIZI_APPLICATIVI+".subject=?))";
    				else if(identitaServizioApplicativoFruitore!=null)
    					query = query + " AND "+CostantiDB.SERVIZI_APPLICATIVI+".nome=?";
    				else if(subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null)
    					query = query + " AND "+CostantiDB.SERVIZI_APPLICATIVI+".subject=?";
    				else{
    					log.debug("CheckFruizioneServizioApplicativo (sa non fornito) ["+AutorizzazionePdDConsole.checkFruizioneServizioApplicativo+"] QUERY["+query+"] SA("+
    	    						identitaServizioApplicativoFruitore+") MessageSecuritySubject("+subjectServizioApplicativoFruitoreFromMessageSecurityHeader+"). Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare" +
    	    								"il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
    	    								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".");
    					checkFallitoPerMancanzaIdentitaServizioApplicativo = true;
    				}
    			}
    			
    			if(checkFallitoPerMancanzaIdentitaServizioApplicativo==false){
	    			pstmt = connectionDB.prepareStatement(query);
	    			pstmt.setString(1,idSoggetto.getTipo());
	    			pstmt.setString(2,idSoggetto.getNome());
	    			pstmt.setString(3,idServizio.getTipoServizio());
	    			pstmt.setString(4,idServizio.getServizio());
	    			pstmt.setString(5,idServizio.getSoggettoErogatore().getTipo());
	    			pstmt.setString(6,idServizio.getSoggettoErogatore().getNome());
	    			if((!pddEsterna) && CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizioneServizioApplicativo)){
	    				if(identitaServizioApplicativoFruitore!=null && subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
	    					pstmt.setString(7,identitaServizioApplicativoFruitore); 			
	    					pstmt.setString(8,subjectServizioApplicativoFruitoreFromMessageSecurityHeader); 
	    				}else if(identitaServizioApplicativoFruitore!=null){
	    					pstmt.setString(7,identitaServizioApplicativoFruitore); 
	    				}else if(subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
	    					pstmt.setString(8,subjectServizioApplicativoFruitoreFromMessageSecurityHeader); 
	    				}
	    			}
	    			rs = pstmt.executeQuery();
	    			autorizzato = rs.next();
	    			if(autorizzato==false){
	    				log.debug("CheckFruizioneServizioApplicativo["+AutorizzazionePdDConsole.checkFruizioneServizioApplicativo+"] QUERY["+query+"] SA("+
	    						identitaServizioApplicativoFruitore+") MessageSecuritySubject("+subjectServizioApplicativoFruitoreFromMessageSecurityHeader+"). Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare" +
	    								"il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
	    								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".");
	    			}
	    			rs.close();
	    			pstmt.close();
    			}
    		}
    		
    		if(autorizzato==false){
    			//  Controllo Fruizione per Ruolo
        		if(CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizionePerRuolo) ||
        				AutorizzazionePdDConsole.ALMOST_ONE.equals(AutorizzazionePdDConsole.checkFruizionePerRuolo)){
        			
        			// Ottengo informazione se il servizio richiesto e' correlato o meno.
        			boolean isCorrelato = false;
        			query="select servizio_correlato from "+CostantiDB.SERVIZI+","+CostantiDB.SOGGETTI+" where " 
        				+CostantiDB.SERVIZI+".tipo_servizio=? AND "+CostantiDB.SERVIZI+".nome_servizio=? AND "+
        				CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id AND "
        				+CostantiDB.SOGGETTI+".tipo_soggetto=? AND "+CostantiDB.SOGGETTI+".nome_soggetto=?";
        			pstmt = connectionDB.prepareStatement(query);
        			pstmt.setString(1,idServizio.getTipoServizio());
        			pstmt.setString(2,idServizio.getServizio());
        			pstmt.setString(3,idServizio.getSoggettoErogatore().getTipo());
        			pstmt.setString(4,idServizio.getSoggettoErogatore().getNome());
        			rs = pstmt.executeQuery();
        			if(rs.next()){
        				isCorrelato = CostantiRegistroServizi.ABILITATO.equals(rs.getString("servizio_correlato"));
        			}else{
        				log.debug("CheckFruizionePerRuolo["+AutorizzazionePdDConsole.checkFruizionePerRuolo+"] QUERY["+query+"]. " +
        						"Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" cerca di usufruire di un servizio non esistente: " 
								+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".");
        				rs.close();
            			pstmt.close();
        				String errore = "Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" cerca di usufruire di un servizio non esistente: " 
								+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".";    			
            			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
            			esito.setServizioAutorizzato(false);
            			return esito;
        			}
        			rs.close();
        			pstmt.close();
        
        			// Controllo
        			query="select * from "+CostantiDB.ACCORDI+","+CostantiDB.SERVIZI+","+CostantiDB.SOGGETTI+","
        										 +CostantiDB.SERVIZI_APPLICATIVI+","+CostantiDB.RUOLI_SA+" where "
        						// Ruolo
        						+CostantiDB.RUOLI_SA+".id_accordo="+CostantiDB.ACCORDI+".id AND "
        						+CostantiDB.ACCORDI+".id=servizi.id_accordo AND "
        						+CostantiDB.ACCORDI+".nome = "+CostantiDB.SERVIZI+".nome_servizio AND "
        						+CostantiDB.SERVIZI+".tipo_servizio=? AND "
        						+CostantiDB.SERVIZI+".nome_servizio=? AND "
        						+CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id AND "
        						+CostantiDB.SOGGETTI+".tipo_soggetto=? AND "
        						+CostantiDB.SOGGETTI+".nome_soggetto=? AND "
        						// Correlato abilitato/disabilitato
        						+CostantiDB.SERVIZI+".servizio_correlato=? AND "
        						+CostantiDB.RUOLI_SA+".servizio_correlato=? AND "
        						// Servizio Applicativo
        						+CostantiDB.RUOLI_SA+".id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id";
        			
        			boolean checkFallitoPerMancanzaIdentitaServizioApplicativo = false;
        			if( (!pddEsterna) && CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizionePerRuolo)  ) {
        				if(identitaServizioApplicativoFruitore!=null && subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
        					query = query + " AND "+
        					"( ("+CostantiDB.SERVIZI_APPLICATIVI+".nome=?) OR ("+CostantiDB.SERVIZI_APPLICATIVI+".subject=?)) AND"
    						+CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto in ( select id from "
    									+CostantiDB.SOGGETTI+" where tipo_soggetto=? AND nome_soggetto=? )";
        				}else if(identitaServizioApplicativoFruitore!=null){
        					query = query + " AND "
    						+CostantiDB.SERVIZI_APPLICATIVI+".nome=? AND "
    						+CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto in ( select id from "
    									+CostantiDB.SOGGETTI+" where tipo_soggetto=? AND nome_soggetto=? )";
        				}
        				else if(subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
        					query = query + " AND "
    						+CostantiDB.SERVIZI_APPLICATIVI+".subject=? AND "
    						+CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto in ( select id from "
    									+CostantiDB.SOGGETTI+" where tipo_soggetto=? AND nome_soggetto=? )";
        				}
        				else{
        					log.debug("CheckFruizionePerRuolo (sa non fornito) ["+AutorizzazionePdDConsole.checkFruizionePerRuolo+"] QUERY["+query+"] SA("+
        							identitaServizioApplicativoFruitore+") MessageSecuritySubject("+subjectServizioApplicativoFruitoreFromMessageSecurityHeader+"). Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare" +
            								"il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
            								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".");
        					checkFallitoPerMancanzaIdentitaServizioApplicativo = true;
        				}
        			}
        			
        			if(checkFallitoPerMancanzaIdentitaServizioApplicativo==false){
	        			pstmt = connectionDB.prepareStatement(query);
	        			pstmt.setString(1,idServizio.getTipoServizio());
	        			pstmt.setString(2,idServizio.getServizio());
	        			pstmt.setString(3,idServizio.getSoggettoErogatore().getTipo());
	        			pstmt.setString(4,idServizio.getSoggettoErogatore().getNome());
	        			if(isCorrelato)
	        				pstmt.setString(5,CostantiRegistroServizi.ABILITATO.toString());
	        			else
	        				pstmt.setString(5,CostantiRegistroServizi.DISABILITATO.toString());
	        			if(isCorrelato)
	        				pstmt.setInt(6,1);
	        			else
	        				pstmt.setInt(6,0);
	        			
	        			if( (!pddEsterna) && CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizionePerRuolo)  ) {
	        				if(identitaServizioApplicativoFruitore!=null && subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
		    					pstmt.setString(7,identitaServizioApplicativoFruitore); 			
		    					pstmt.setString(8,subjectServizioApplicativoFruitoreFromMessageSecurityHeader); 
		    					pstmt.setString(9,idSoggetto.getTipo());
		        				pstmt.setString(10,idSoggetto.getNome());
		    				}else if(identitaServizioApplicativoFruitore!=null){
		    					pstmt.setString(7,identitaServizioApplicativoFruitore);
		    					pstmt.setString(8,idSoggetto.getTipo());
		        				pstmt.setString(9,idSoggetto.getNome());
		    				}else if(subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
		    					pstmt.setString(7,subjectServizioApplicativoFruitoreFromMessageSecurityHeader);
		    					pstmt.setString(8,idSoggetto.getTipo());
		        				pstmt.setString(9,idSoggetto.getNome());
		    				}
	        			}
	        			
	        			rs = pstmt.executeQuery();
	        			autorizzato = rs.next();
	        			log.debug("CheckFruizionePerRuolo["+AutorizzazionePdDConsole.checkFruizionePerRuolo+"] QUERY["+query+"] SA("+
	    						identitaServizioApplicativoFruitore+") MessageSecuritySubject("+subjectServizioApplicativoFruitoreFromMessageSecurityHeader+"). Il soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare" +
	    								"il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "
	    								+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome() +".");
	        			
	        			rs.close();
	        			pstmt.close();
        			}
        		}
    		}
    		
    		if(autorizzato==false){
    			String servizioApplicativoMsg = "";
    			if( (!pddEsterna) &&
    				(CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizioneServizioApplicativo) ||
    			   CostantiRegistroServizi.ABILITATO.equals(AutorizzazionePdDConsole.checkFruizionePerRuolo)) ){
    				if(identitaServizioApplicativoFruitore!=null && subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
        				servizioApplicativoMsg = "servizio applicativo ["+identitaServizioApplicativoFruitore+"] (subject-messageSecurity ["+subjectServizioApplicativoFruitoreFromMessageSecurityHeader+"]) del ";
    				}else if(identitaServizioApplicativoFruitore!=null){
        				servizioApplicativoMsg = "servizio applicativo ["+identitaServizioApplicativoFruitore+"] del ";
    				}else if(subjectServizioApplicativoFruitoreFromMessageSecurityHeader!=null){
    					servizioApplicativoMsg = "servizio applicativo (subject-messageSecurity ["+identitaServizioApplicativoFruitore+"]) del ";
    				}else{
    					servizioApplicativoMsg = "servizio applicativo ANONIMO (ne identita' ne subject-messageSecurity fornito) del ";
    				}
    			}
    			String errore = "Il "+servizioApplicativoMsg+"soggetto "+idSoggetto.getTipo()+"/"+idSoggetto.getNome() +" non e' autorizzato ad invocare il servizio "+idServizio.getTipoServizio()+"/"+idServizio.getServizio()+" erogato da "+idServizio.getSoggettoErogatore().getTipo()+"/"+idServizio.getSoggettoErogatore().getNome();    			
    			esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setServizioAutorizzato(false);
    		}else{
    			esito.setServizioAutorizzato(true);
    		}
    	}catch(Exception e){
    		log.error("Errore durante il processo di autorizzazione PdDConsole query["+query+"]",e);
    		String errore = "Errore durante il processo di autorizzazione PdDConsole: "+e.getMessage();
    		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
			esito.setServizioAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    		try{
    			if(rs!=null)
    				rs.close();
    		}catch(Exception eClose){}
    		try{
    			if(pstmt!=null)
    				pstmt.close();
    		}catch(Exception eClose){}
    	}finally{
    		try{
    			if(connectionDB!=null)
    				connectionDB.close();
    		}catch(Exception eClose){}
    	}
    	
    	return esito;
    }
	
	@Override
	public boolean saveAuthorizationResultInCache() {
		return true;
	}
}
