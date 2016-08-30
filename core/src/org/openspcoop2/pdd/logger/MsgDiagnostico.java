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




package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.pdd.services.skeleton.IntegrationManager;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.DiagnosticoBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.diagnostica.IMsgDiagnosticoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneApplicativa;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneServizioApplicativo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.date.DateManager;



/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop2
 * per la registrazione di messaggi diagnostici.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MsgDiagnostico {

	
	/** Indicazione di un gestore di msg diagnostici funzionante */
	public static boolean gestoreDiagnosticaDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel sistema di diagnostica */
	public static Exception motivoMalfunzionamentoDiagnostici = null;
	

	/**  Logger log4j utilizzato per scrivere i msgDiagnostici */
	private org.apache.logging.log4j.Logger loggerMsgDiagnostico = null;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici HumanReadable */
	private org.apache.logging.log4j.Logger loggerOpenSPCoop2 = null;
	/**  Logger log4j utilizzato per scrivere i msgDiagnostici HumanReadable del servìzio di IntegrationManager */
	private org.apache.logging.log4j.Logger loggerIntegrationManager = null;
	/**  Logger log4j utilizzato per segnalare a video errori gravi (FATAL) */
	private Logger loggerOpenSPCoop2Fatal = null;
	/**  Logger log4j utilizzato per scrivere lo stack trace degli errore nel core logger di openspcoop */
	private Logger loggerOpenSPCoop2Core = null;
	/** Appender personalizzati per i messaggi diagnostici di OpenSPCoop2 */
	private Vector<IMsgDiagnosticoOpenSPCoopAppender> loggerMsgDiagnosticoOpenSPCoopAppender = null; 
	private Vector<String> tipoMsgDiagnosticoOpenSPCoopAppender = null;

	/** Soggetto che richiede il logger */
	private IDSoggetto idSoggettoDominio;
	/** Modulo Funzionale */
	private String idModulo;
	/** Identificativo della richiesta */
	private String idMessaggioRichiesta;
	/** Identificativo della risposta */
	private String idMessaggioRisposta;
	/** PdDContext */
	private PdDContext pddContext;
	/** XMLBuilder */
	private DiagnosticoBuilder diagnosticoBuilder;
	
	/** Protocol Factory */
	private ProtocolFactoryManager protocolFactoryManager = null;
	private IProtocolFactory protocolFactory;
	private ITraduttore traduttore;
	/** ConfigurazionePdDReader */
	private ConfigurazionePdDManager configurazionePdDReader;
	/** MsgDiagnosticiProperties reader */
	private MsgDiagnosticiProperties msgDiagPropertiesReader;
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** Generatore di date casuali*/
	private GeneratoreCasualeDate generatoreDateCasuali = null;
	
	/** Properties da aggiungere ai diagnostici (utili nelle implementazioni handler) */
	private Hashtable<String,String> properties = new Hashtable<String, String>();		
	public Hashtable<String, String> getProperties() {
		return this.properties;
	}

	/** Keyword per i log personalizzati */
	private Hashtable<String,String> keywordLogPersonalizzati = new Hashtable<String, String>();	
	public Hashtable<String, String> getKeywordLogPersonalizzati() {
		return this.keywordLogPersonalizzati;
	}

	/** Stati */
	private List<IState> states = new ArrayList<IState>();
	
	/**
	 * Costruttore. 
	 *
	 * @param idSoggettoDominio Soggetto che richiede il logger
	 * @param modulo Funzione che richiede il logger
	 * @throws ProtocolException 
	 * 
	 */
	public MsgDiagnostico(IDSoggetto idSoggettoDominio,String modulo,IState ... state) {
		this.idSoggettoDominio = idSoggettoDominio;
		this.idModulo = modulo;
		this.loggerMsgDiagnostico = OpenSPCoop2Logger.loggerMsgDiagnostico;
		this.loggerOpenSPCoop2 = OpenSPCoop2Logger.loggerOpenSPCoop2;
		this.loggerIntegrationManager = OpenSPCoop2Logger.loggerIntegrationManager;
		this.loggerOpenSPCoop2Fatal = OpenSPCoop2Logger.loggerOpenSPCoopConsole;
		this.loggerMsgDiagnosticoOpenSPCoopAppender = OpenSPCoop2Logger.loggerMsgDiagnosticoOpenSPCoopAppender;
		this.tipoMsgDiagnosticoOpenSPCoopAppender = OpenSPCoop2Logger.tipoMsgDiagnosticoOpenSPCoopAppender;
		this.loggerOpenSPCoop2Core = OpenSPCoop2Logger.loggerOpenSPCoopCore;

		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				this.states.add(state[i]);
			}
		}
		this.configurazionePdDReader = ConfigurazionePdDManager.getInstance(state);
		this.msgDiagPropertiesReader = MsgDiagnosticiProperties.getInstance();
		
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato()){
			this.generatoreDateCasuali = GeneratoreCasualeDate.getGeneratoreCasualeDate();
		}
		try{
			this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
			this.protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			this.traduttore = this.protocolFactory.createTraduttore();
		} catch(Throwable e){
			// Succede quando non appartiene a nessun protocollo, ad esempio i diagnostici di startup
			this.protocolFactory = new BasicProtocolFactory(this.loggerOpenSPCoop2Core);
			try{
				this.traduttore = this.protocolFactory.createTraduttore();
			} catch(Throwable eClose){}
			//this.protocolFactory = null; 
		}
		this.diagnosticoBuilder = new DiagnosticoBuilder(this.protocolFactory); 
		// Impostazione filtri
		// Il filtro viene effettuato a livello di programma.
			
	}
	
	/**
	 * Costruttore. 
	 *
	 * @param modulo Funzione che richiede il logger
	 * @throws ProtocolException 
	 * 
	 */
	public MsgDiagnostico(String modulo,IState  ...  state) {
		this(OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(null),modulo,state);
	}
	public MsgDiagnostico(){
		this.msgDiagPropertiesReader = MsgDiagnosticiProperties.getInstance();
	}
	

	public void updateState(IState ... state){
		this.states.clear();
		if(state!=null){
			for (int i = 0; i < state.length; i++) {
				this.states.add(state[i]);
			}
		}
	}
	
	private Connection getConnectionFromState(){
		for (IState state : this.states) {
			if(state!=null && state instanceof StateMessage){
				boolean validConnection = false;
				try{
					validConnection = !((StateMessage)state).getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return ((StateMessage)state).getConnectionDB();
			}
		}
		return null;
	}
	
	/**
	 * Il Metodo si occupa di impostare il dominio del Soggetto che utilizza il logger. 
	 *
	 * @param idSoggettoDominio Soggetto che richiede il logger
	 * 
	 */
	public void setDominio(IDSoggetto idSoggettoDominio){
		this.idSoggettoDominio = idSoggettoDominio;
	}
	public IDSoggetto getDominio() {
		return this.idSoggettoDominio;
	}
	
	/**
	 * Il Metodo si occupa di impostare il contesto della PdD
	 *
	 * @param pddContext Contesto della PdD
	 * 
	 */
	public void setPddContext(PdDContext pddContext, IProtocolFactory protocolFactory) {
		this.pddContext = pddContext;
		this.protocolFactory = protocolFactory;
		try{
			this.traduttore = this.protocolFactory.createTraduttore();
		}catch(Exception e){}
		this.diagnosticoBuilder = new DiagnosticoBuilder(protocolFactory); 
		this.addKeywords(protocolFactory);
	}


	/**
	 * Il Metodo si occupa di impostare il modulo funzionale che che utilizza il logger. 
	 *
	 * @param modulo Funzione che richiede il logger
	 * 
	 */
	public void setFunzione(String modulo){
		this.idModulo = modulo;
	}
	public String getFunzione() {
		return this.idModulo;
	}

	/**
	 * Il Metodo si occupa di impostare l'identificativo della richiesta in gestione. 
	 *
	 * @param id Identificativo
	 */
	public void setIdMessaggioRichiesta(String id){
		this.idMessaggioRichiesta = id;
	}
	/**
	 * Il Metodo si occupa di impostare l'identificativo della risposta in gestione. 
	 *
	 * @param id Identificativo
	 */
	public void setIdMessaggioRisposta(String id){
		this.idMessaggioRisposta = id;
	}

	/**
	 * Il Metodo si occupa di impostare i filtri sui logger. 
	 *
	 * 
	 */
	public void aggiornaFiltri(){
		
		// Il filtro viene effettuato a livello di programma.

	}

	/**
	 * Filter. 
	 *
	 * 
	 * @deprecated  utility che elimina i caratteri XML codificati
	 */
	@Deprecated  public String filter(String msg){
		String xml = msg.replaceAll("&lt;","<");
		xml = xml.replaceAll("&quot;","\"");
		xml = xml.replaceAll("&gt;",">");
		return xml;
	}




	
	
	/** -----------------Impostazione Identificatori nei messaggi ---------------- */
	private IDSoggetto fruitore;
	private IDServizio servizio;
	private String porta;
	private boolean delegata;
	private String servizioApplicativo;
	private String idCorrelazioneApplicativa;
	private String idCorrelazioneRisposta;
	public void setFruitore(IDSoggetto fruitore) {
		this.fruitore = fruitore;
	}

	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}

	public void setDelegata(boolean delegata) {
		this.delegata = delegata;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public void setIdCorrelazioneApplicativa(String idCorrelazioneApplicativa) {
		this.idCorrelazioneApplicativa = idCorrelazioneApplicativa;
	}
	public void setIdCorrelazioneRisposta(String idCorrelazioneRisposta) {
		this.idCorrelazioneRisposta = idCorrelazioneRisposta;
	}
	
	
	
	
	/*---------- Livelli/Messaggi personalizzati -------------*/
	
	/** Keyword replace per msg personalizzati */
	public void addKeyword(String key,String value){
		if(key!=null){
			String tmpValue = value;
			if(tmpValue == null)
				tmpValue = "";
			if(this.keywordLogPersonalizzati.containsKey(key))
				this.keywordLogPersonalizzati.remove(key);
			this.keywordLogPersonalizzati.put(key, tmpValue);
		}
	}
	public void addKeywords(IProtocolFactory protocolFactory){
		this.addKeyword(CostantiPdD.KEY_PROTOCOLLO, protocolFactory.getProtocol());
		try{
			this.addKeyword(CostantiPdD.KEY_PROTOCOLLO_TIPI_SOGGETTI, protocolFactory.createProtocolConfiguration().getTipiSoggetti().toString());
			this.addKeyword(CostantiPdD.KEY_PROTOCOLLO_TIPI_SERVIZI, protocolFactory.createProtocolConfiguration().getTipiServizi().toString());
		}catch(Exception e){}
	}
	public void addKeywords(Busta busta,boolean richiesta){
		if(busta!=null){
			if(richiesta){
				if(busta.getID()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, busta.getID());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, "N.D.");
				if(busta.getProfiloDiCollaborazione()!=null){
					String profilo = null;
					if(this.traduttore!=null){
						profilo = this.traduttore.toString(busta.getProfiloDiCollaborazione());
					}
					else if(busta.getProtocollo()!=null){
						try{
							IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(busta.getProtocollo());
							ITraduttore traduttore = protocolFactory.createTraduttore();
							profilo = traduttore.toString(busta.getProfiloDiCollaborazione());
						}catch(Exception e){
							profilo = busta.getProfiloDiCollaborazione().name();
						}
					}else{
						profilo = busta.getProfiloDiCollaborazione().name();
					}
					if(profilo==null){
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profilo);
				}
				if(busta.getTipoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, busta.getTipoMittente());
				if(busta.getMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, busta.getMittente());
				if(busta.getTipoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RICHIESTA, busta.getTipoDestinatario());
				if(busta.getDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RICHIESTA, busta.getDestinatario());
				if(busta.getTipoServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RICHIESTA, busta.getTipoServizio());
				if(busta.getServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RICHIESTA, busta.getServizio());
				if(busta.getAzione()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, busta.getAzione());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, "");
				if(busta.getRiferimentoMessaggio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RICHIESTA, busta.getRiferimentoMessaggio());
				if(busta.getSequenza()>0){
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SEQUENZA, busta.getSequenza()+"");
				}
				if(busta.getIndirizzoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_MITTENTE_RICHIESTA, busta.getIndirizzoMittente());
				if(busta.getIndirizzoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RICHIESTA, busta.getIndirizzoDestinatario());
				if(busta.getScadenza()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SCADENZA_BUSTA_RICHIESTA, busta.getScadenza().toString());
			}
			else{
				if(busta.getID()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, busta.getID());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RISPOSTA, "N.D.");
				if(busta.getProfiloDiCollaborazione()!=null){
					String profilo = null;
					if(this.traduttore!=null){
						profilo = this.traduttore.toString(busta.getProfiloDiCollaborazione());
					}
					else if(busta.getProtocollo()!=null){
						try{
							IProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(busta.getProtocollo());
							ITraduttore traduttore = protocolFactory.createTraduttore();
							profilo = traduttore.toString(busta.getProfiloDiCollaborazione());
						}catch(Exception e){
							profilo = busta.getProfiloDiCollaborazione().getEngineValue();
						}
					}else{
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					if(profilo==null){
						profilo = busta.getProfiloDiCollaborazione().getEngineValue();
					}
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_PROFILO_COLLABORAZIONE, profilo);
				}
				if(busta.getTipoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RISPOSTA, busta.getTipoMittente());
				if(busta.getMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RISPOSTA, busta.getMittente());
				if(busta.getTipoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RISPOSTA, busta.getTipoDestinatario());
				if(busta.getDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RISPOSTA, busta.getDestinatario());
				if(busta.getTipoServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RISPOSTA, busta.getTipoServizio());
				if(busta.getServizio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RISPOSTA, busta.getServizio());
				if(busta.getAzione()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RISPOSTA, busta.getAzione());
				else
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RISPOSTA, "");
				if(busta.getRiferimentoMessaggio()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_RIFERIMENTO_MESSAGGIO_RISPOSTA, busta.getRiferimentoMessaggio());
				if(busta.getSequenza()>0){
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SEQUENZA, busta.getSequenza()+"");
				}
				if(busta.getIndirizzoMittente()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_MITTENTE_RISPOSTA, busta.getIndirizzoMittente());
				if(busta.getIndirizzoDestinatario()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_INDIRIZZO_TELEMATICO_DESTINATARIO_RISPOSTA, busta.getIndirizzoDestinatario());
				if(busta.getScadenza()!=null)
					this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SCADENZA_BUSTA_RISPOSTA, busta.getScadenza().toString());
			}	
		}
	}
	public void addKeywords(IDSoggetto soggettoFruitore){
		this.addKeywords(soggettoFruitore,null,null);
	}
	public void addKeywords(IDServizio idServizio){
		this.addKeywords(null,idServizio,null);
	}
	public void addKeywords(IDSoggetto soggettoFruitore,IDServizio idServizio,String idMessaggio){
		if(idMessaggio!=null){
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_ID_MESSAGGIO_RICHIESTA, idMessaggio);
		}
		if(soggettoFruitore!=null){
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_MITTENTE_BUSTA_RICHIESTA, soggettoFruitore.getTipo());
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_MITTENTE_BUSTA_RICHIESTA, soggettoFruitore.getNome());
		}
		if(idServizio!=null){
			if(idServizio.getSoggettoErogatore()!=null){
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_DESTINATARIO_BUSTA_RICHIESTA, idServizio.getSoggettoErogatore().getTipo());
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_DESTINATARIO_BUSTA_RICHIESTA, idServizio.getSoggettoErogatore().getNome());
			}
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_TIPO_SERVIZIO_BUSTA_RICHIESTA, idServizio.getTipoServizio());
			this.keywordLogPersonalizzati.put(CostantiPdD.KEY_SERVIZIO_BUSTA_RICHIESTA, idServizio.getServizio());
			if(idServizio.getAzione()!=null)
				this.keywordLogPersonalizzati.put(CostantiPdD.KEY_AZIONE_BUSTA_RICHIESTA, idServizio.getAzione());
		}
	}
	public String replaceKeywords(String msg){
				
		/* Potenzialmente N^2	
		String tmp = msg;
		Enumeration<String> keys = this.keywordLogPersonalizzati.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			tmp = tmp.replace(key, this.keywordLogPersonalizzati.get(key));
		}
		return tmp;
		*/
		
		// Check di esistenza di almeno 2 '@'
		if(msg!=null && msg.length()>2){
			int index1 = msg.indexOf("@");
			int index2 = msg.indexOf("@",index1+1);
			if(index1<0 || index2<0){
				return msg; // non serve il replace
			}
		}
		
		StringBuffer bf = new StringBuffer();
		StringBuffer keyword = new StringBuffer();
		boolean separator = false;
		char separatorChar = '@';
		for(int i=0; i<msg.length(); i++){
			char ch = msg.charAt(i);
			if(separatorChar == ch){
				if(separator==false){
					// inizio keyword
					keyword.append(separatorChar);
					separator = true;
				}
				else{
					// fine keyword
					keyword.append(separatorChar);
					String valoreRimpiazzato = this.keywordLogPersonalizzati.get(keyword.toString());
					if(valoreRimpiazzato==null){
						// keyword non esistente, devo utilizzare l'originale
						bf.append(keyword.toString());
					}else{
						bf.append(valoreRimpiazzato);
					}
					keyword.delete(0, keyword.length());
					separator=false;
				}
			}else{
				if(separator){
					// sto scrivendo la keyword
					keyword.append(ch);
				}else{
					bf.append(ch);
				}
			}
		}
		return bf.toString();
	}
	
	private String prefixMsgPersonalizzati = null;
	public String getPrefixMsgPersonalizzati() {
		return this.prefixMsgPersonalizzati;
	}
	public void setPrefixMsgPersonalizzati(String v){
		this.prefixMsgPersonalizzati = v;
	}
	
	public int getLivello(String keyLivelloPersonalizzato){
		return getLivello(this.prefixMsgPersonalizzati, keyLivelloPersonalizzato);
	}
	public int getLivello(String prefix,String keyLivelloPersonalizzato){
		if(this.msgDiagPropertiesReader==null){
			return -1;
		}
		Integer livello = this.msgDiagPropertiesReader.getLivello(prefix,keyLivelloPersonalizzato);
		if(livello!=null){
			return livello;
		} else{
			return -1;
		}
	}
	
	public String getCodice(String keyCodicePersonalizzato){
		return getCodice(this.prefixMsgPersonalizzati, keyCodicePersonalizzato);
	}
	public String getCodice(String prefix,String keyCodicePersonalizzato){
		if(this.msgDiagPropertiesReader==null){
			return "PropertiesReader dei Messaggi Diagnostici non inizializzato";
		}
		return this.msgDiagPropertiesReader.getCodice(prefix,keyCodicePersonalizzato);
	}
	
	public String getMessaggio(String keyMsgPersonalizzato){
		return this.getMessaggio(this.prefixMsgPersonalizzati, keyMsgPersonalizzato, false);
	}
	public String getMessaggio(String prefix,String keyMsgPersonalizzato){
		return this.getMessaggio(prefix, keyMsgPersonalizzato, false);
	}
	public String getMessaggio_replaceKeywords(String keyMsgPersonalizzato){
		return this.getMessaggio(this.prefixMsgPersonalizzati, keyMsgPersonalizzato, true);
	}
	public String getMessaggio_replaceKeywords(String prefix,String keyMsgPersonalizzato){
		return this.getMessaggio(prefix, keyMsgPersonalizzato, true);
	}
	private String getMessaggio(String prefix,String keyMsgPersonalizzato,boolean replaceKeywords){
		if(this.msgDiagPropertiesReader==null){
			return "PropertiesReader dei Messaggi Diagnostici non inizializzato";
		}
		String msgTmp = this.msgDiagPropertiesReader.getMessaggio(prefix,keyMsgPersonalizzato);
		if(msgTmp==null){
			msgTmp = "Messaggio diagnostico ["+prefix+keyMsgPersonalizzato+"] non definito nella configurazione della porta di dominio??";
		}
		if(replaceKeywords){
			String msgReplaceKey = this.replaceKeywords(msgTmp);
			return msgReplaceKey;
		}else{
			return msgTmp;
		}
	}
	
	
	
	
	

	
	
	/** ----------------- METODI DI LOGGING (Messaggi Diagnostici per correlazione applicativa) ---------------- */
	
	/**
	 * Creazione di un entry che permette di effettuare una correlazione con i msg diagnostici
	 * 
	 */
	public void logCorrelazione() {
		if( LogLevels.SEVERITA_OFF != this.configurazionePdDReader.getSeverita_msgDiagnostici() ){
			try{
				// Data
				Date gdo = DateManager.getDate();
				if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
					gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						
						// Preparo oggetto MsgDiagnosticoCorrelazione
						MsgDiagnosticoCorrelazione msgDiagCorrelazione = new MsgDiagnosticoCorrelazione();
						msgDiagCorrelazione.setProtocollo(this.protocolFactory.getProtocol());
						msgDiagCorrelazione.setIdBusta(this.idMessaggioRichiesta);
						msgDiagCorrelazione.setIdSoggetto(this.idSoggettoDominio);
						msgDiagCorrelazione.setGdo(gdo);
						msgDiagCorrelazione.setNomePorta(this.porta);
						msgDiagCorrelazione.setDelegata(this.delegata);
						msgDiagCorrelazione.setProtocollo(this.protocolFactory.getProtocol());
						if(this.fruitore!=null || this.servizio!=null){
							org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo informazioniBusta = new org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo();
							informazioniBusta.setFruitore(this.fruitore);
							if(this.servizio!=null){
								informazioniBusta.setErogatore(this.servizio.getSoggettoErogatore());
								informazioniBusta.setTipoServizio(this.servizio.getTipoServizio());
								informazioniBusta.setServizio(this.servizio.getServizio());
								informazioniBusta.setVersioneServizio(Integer.parseInt(this.servizio.getVersioneServizio()));
								informazioniBusta.setAzione(this.servizio.getAzione());
							}
							msgDiagCorrelazione.setInformazioniProtocollo(informazioniBusta);
						}
						msgDiagCorrelazione.setCorrelazioneApplicativa(this.idCorrelazioneApplicativa);
						if(this.pddContext!=null){
							String [] key = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
							if(key!=null){
								for (int j = 0; j < key.length; j++) {
									Object o = this.pddContext.getObject(key[j]);
									if(o instanceof String){
										msgDiagCorrelazione.addProperty(key[j], (String)o);
									}
								}
							}
						}
						
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).logCorrelazione(this.getConnectionFromState(),msgDiagCorrelazione);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico di correlazione ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}catch(Exception e){
				logError("MsgDiagnostico.logCorrelazione error "+e.getMessage(),e);
				gestioneErroreDiagnostica(e);
			}
		}
	}
	
	
	/**
	 * Creazione di una correlazione applicativa tra messaggi diagnostici e servizi applicativi.
	 * 
	 */
	public void logCorrelazioneServizioApplicativo(){
		if( LogLevels.SEVERITA_OFF != this.configurazionePdDReader.getSeverita_msgDiagnostici() ){
			try{
				MsgDiagnosticoCorrelazioneServizioApplicativo msg = new MsgDiagnosticoCorrelazioneServizioApplicativo();
				msg.setDelegata(this.delegata);
				msg.setIdBusta(this.idMessaggioRichiesta);
				msg.setServizioApplicativo(this.servizioApplicativo);
				msg.setProtocollo(this.protocolFactory.getProtocol());
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).logCorrelazioneServizioApplicativo(this.getConnectionFromState(),msg);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico di correlazione (servizi applicativi) ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}catch(Exception e){
				logError("MsgDiagnostico.logCorrelazione error "+e.getMessage(),e);
				gestioneErroreDiagnostica(e);
			}
		}
	}
	
	/**
	 * Registrazione dell'identificativo di correlazione applicativa della risposta
	 * 
	 * @throws MsgDiagnosticoException
	 */
	public void logCorrelazioneApplicativaRisposta() throws MsgDiagnosticoException{
		if( LogLevels.SEVERITA_OFF != this.configurazionePdDReader.getSeverita_msgDiagnostici() ){
			try{
				MsgDiagnosticoCorrelazioneApplicativa msg = new MsgDiagnosticoCorrelazioneApplicativa();
				msg.setIdBusta(this.idMessaggioRichiesta);
				msg.setProtocollo(this.protocolFactory.getProtocol());
				msg.setDelegata(this.delegata);
				msg.setCorrelazione(this.idCorrelazioneRisposta);
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).logCorrelazioneApplicativaRisposta(this.getConnectionFromState(),msg);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico di correlazione (id correlazione risposta) ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}catch(Exception e){
				logError("MsgDiagnostico.logCorrelazioneApplicativaRisposta error "+e.getMessage(),e);
				gestioneErroreDiagnostica(e);
			}
		}
	}
	
	
	
	
	
	
	
	




	/** ----------------- METODI DI LOGGING (Messaggi Diagnostici) ---------------- */

	private void setEmitErrorConditionInContext(int livelloLog){
		if(this.pddContext!=null){
			if(livelloLog<=LogLevels.SEVERITA_ERROR_INTEGRATION){
				if(this.pddContext.containsKey(Costanti.EMESSI_DIAGNOSTICI_ERRORE)==false){
					this.pddContext.addObject(Costanti.EMESSI_DIAGNOSTICI_ERRORE, "true");
				}
			}
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'Personalizzato'. 
	 *
	 * 
	 */
	public void logPersonalizzato(String idModuloFunzionale,String idDiagnostico){
		this.logPersonalizzato(this.getMessaggio(idModuloFunzionale,idDiagnostico), 
				this.getLivello(idModuloFunzionale,idDiagnostico),
				this.getCodice(idModuloFunzionale,idDiagnostico)
				);
	}
	public void logPersonalizzato(String idDiagnostico){
		this.logPersonalizzato(this.getMessaggio(idDiagnostico), 
				this.getLivello(idDiagnostico),
				this.getCodice(idDiagnostico)
				);
	}
	public void logPersonalizzato(String messaggio, int livelloLog, String codiceDiagnostico) {

		if(this.msgDiagPropertiesReader==null){
			logError("MsgDiagnostico.logPersonalizzato [Risorsa non inizializzata], messaggio originale: "+messaggio);
			return;
		}
		
		if(messaggio==null){
			logError("MsgDiagnostico.logPersonalizzato error, messaggio non definito.");
			return;
		}		
		
		int severitaLogEmessoPerFiltro = livelloLog;
		int severitaLivelloOpenSPCoop2 = LogLevels.toIntervalloOpenSPCoop2(livelloLog);
		if(severitaLivelloOpenSPCoop2<0 || severitaLivelloOpenSPCoop2>7){
			logError("MsgDiagnostico.logPersonalizzato error, conversione a livello OpenSPCoop non riuscita ["+severitaLivelloOpenSPCoop2+"]");
			return;
		}
		this.setEmitErrorConditionInContext(severitaLivelloOpenSPCoop2);
		Level logLevelseveritaLivelloLog4J = LogLevels.toLog4J(severitaLivelloOpenSPCoop2);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			String msgReplaceKey = null;
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
			
			
			// Messaggio diagnostici
			//System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD ){

				// Replace keyword
				msgReplaceKey = this.replaceKeywords(messaggio);	
				msgDiag = this.getMsgDiagnostico(gdo, severitaLivelloOpenSPCoop2, msgReplaceKey, codiceDiagnostico);
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(logLevelseveritaLivelloLog4J,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}
			
			// Messaggio diagnostici
			//System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD ){

				// Replace keyword
				if(msgReplaceKey==null){
					msgReplaceKey = this.replaceKeywords(messaggio);
					msgDiag = this.getMsgDiagnostico(gdo, severitaLivelloOpenSPCoop2, msgReplaceKey, codiceDiagnostico);
				}
									
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgReplaceKey==null){
					// Replace keyword
					msgReplaceKey = this.replaceKeywords(messaggio);
					msgDiag = this.getMsgDiagnostico(gdo, severitaLivelloOpenSPCoop2, msgReplaceKey, codiceDiagnostico);
				}			
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(logLevelseveritaLivelloLog4J,message);
					}else{
						this.loggerOpenSPCoop2.log(logLevelseveritaLivelloLog4J,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
			
			// Log fatal emesso su console jboss
			if(msgReplaceKey!=null && severitaLivelloOpenSPCoop2==0){
				// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
				//this.loggerOpenSPCoop2Fatal.log(LogLevels.LOG_LEVEL_FATAL,msgReplaceKey);
				this.loggerOpenSPCoop2Fatal.error(msgReplaceKey);
			}

		}catch(Exception e){
			logError("MsgDiagnostico.logPersonalizzato error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}
	
	
	public void logErroreGenerico(Throwable e, String posizioneErrore) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logErroreGenerico(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null)
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
	}
	public void logErroreGenerico(Exception e, String posizioneErrore) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logErroreGenerico(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null)
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
	}
	public void logErroreGenerico(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"erroreGenerico");
	}
	
	
	public void logFatalError(Exception e, String posizioneErrore) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logFatalError(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null){
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Core.fatal(posizioneErrore+": "+msg,e);
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
		}
	}
	public void logFatalError(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"erroreGenericoMalfunzionamentoPdD");
	}
	
	
	public void logStartupError(Exception e, String posizioneErrore) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logStartupError(msg,posizioneErrore);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null){
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Core.fatal(posizioneErrore+": "+msg,e);
			this.loggerOpenSPCoop2Core.error(posizioneErrore+": "+msg,e);
		}
	}
	public void logStartupError(String message, String posizioneErrore) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, posizioneErrore);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_OPENSPCOOP_STARTUP,"erroreGenerico");
	}
	
	public void logDisconnectError(Exception e, String url) {
		String msg = null;
		if(e instanceof NullPointerException)
			 msg = "NullPointerException";
		else
			msg = e.getMessage();
		this.logDisconnectError(msg,url);
		// inoltre registro l'errore nel logger_core di openspcoop
		if(this.loggerOpenSPCoop2Core!=null)
			this.loggerOpenSPCoop2Core.error(url+": "+msg,e);
	}
	public void logDisconnectError(String message, String url) {
		this.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, message);
		this.addKeyword(CostantiPdD.KEY_POSIZIONE_ERRORE, url);
		this.logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_ALL,"connessioneUscita.disconnectError");
	}
	
	

	
	
	
	
	
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'FATAL'. 
	 * @deprecated in funzione del metodo logPersonalizzato
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void fatal(String msg) {

		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_FATAL);
				
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_FATAL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoFatal();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD ){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_FATAL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD ){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_FATAL, msg, codiceDiagnostico);
				}
				
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo, this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_FATAL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_FATAL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
			
			// Log emesso su console jboss
			// dalla versione 2.3, essendo utilizzato slf4j, si converte fatal in error.
			//this.loggerOpenSPCoop2Fatal.log(LogLevels.LOG_LEVEL_FATAL,msg);
			this.loggerOpenSPCoop2Fatal.error(msg);

		}catch(Exception e){
			logError("MsgDiagnostico.fatalOpenSPCoop error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'ERROR-PROTOCOL'.
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void errorProtocol(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_ERROR_PROTOCOL);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_PROTOCOL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoErrorProtocol();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_PROTOCOL, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_ERROR_PROTOCOL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}
		}catch(Exception e){
			logError("MsgDiagnostico.errorProtocol error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'ERROR-INTEGRATION'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void errorIntegration(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_ERROR_INTEGRATION);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_ERROR_INTEGRATION);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoErrorIntegration();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}	
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}	

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_ERROR_INTEGRATION, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_ERROR_INTEGRATION,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.errorIntegration error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'INFO-PROTOCOL'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void infoProtocol(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_INFO_PROTOCOL);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_PROTOCOL);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoInfoProtocol();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato	
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_PROTOCOL, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_INFO_PROTOCOL,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.infoProtocol error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'INFO-INTEGRATION'. 
	 * @deprecated in funzione del metodo logPersonalizzato 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void infoIntegration(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_INFO_INTEGRATION);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_INFO_INTEGRATION);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoInfoIntegration();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
				
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_INFO_INTEGRATION, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_INFO_INTEGRATION,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.infoIntegration error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-LOW'. 
	 * @deprecated in funzione del metodo logPersonalizzato
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void lowDebug(String msg){
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_LOW);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_LOW);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugLow();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_LOW,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}

			}

			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}
			
			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_LOW, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_LOW,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_LOW,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.lowDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-MEDIUM'. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	public void mediumDebug(String msg) {
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_MEDIUM);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_MEDIUM);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugMedium();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}

			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
					
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				//	Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_MEDIUM, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_MEDIUM,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.mediumDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}

	}

	/**
	 * Il Metodo si occupa di creare un messaggio di livello 'DEBUG-HIGH'. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	public void highDebug(String msg){
		
		String codiceDiagnostico = null; // con questi metodi viene fornito un unico codice diagnostico associato ad ogni messaggio di questo livello
		
		this.setEmitErrorConditionInContext(LogLevels.SEVERITA_DEBUG_HIGH);
		
		int severitaLogEmessoPerFiltro = LogLevels.toIntervalloLog4J(LogLevels.SEVERITA_DEBUG_HIGH);
		int severitaRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		int severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getFiltroMsgDiagnostico_OpenSPCoop2_7();
		if(this.configurazionePdDReader!=null && this.configurazionePdDReader.isInitializedConfigurazionePdDReader()){
			severitaRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeverita_msgDiagnostici());
			severitaLog4JRichiestaPdD = this.msgDiagPropertiesReader.getValoreFiltroFromValoreOpenSPCoop2(this.configurazionePdDReader.getSeveritaLog4J_msgDiagnostici());
		}
		
		try{
			
			codiceDiagnostico = this.msgDiagPropertiesReader.getCodiceDiagnosticoDebugHigh();
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
			}
			
			org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiag = null;
						
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD){

				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				if(OpenSPCoop2Logger.loggerMsgDiagnosticoAbilitato){
					try{
						String xml = this.diagnosticoBuilder.toString(msgDiag);
						this.loggerMsgDiagnostico.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,xml);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico su log4j (struttura xml): "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
					
			}
			
			// Messaggio diagnostici
			// System.out.println("1. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaRichiestaPdD+"]");
			if( severitaLogEmessoPerFiltro <= severitaRichiestaPdD){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				// Msg Diagnostico personalizzato
				for(int i=0; i<this.loggerMsgDiagnosticoOpenSPCoopAppender.size();i++){
					try{
						this.loggerMsgDiagnosticoOpenSPCoopAppender.get(i).log(this.getConnectionFromState(),msgDiag);
					}catch(Exception e){
						logError("Errore durante l'emissione del msg diagnostico personalizzato ["+this.tipoMsgDiagnosticoOpenSPCoopAppender.get(i)+"]: "+e.getMessage(),e);
						gestioneErroreDiagnostica(e);
					}
				}
			}

			// Human Readable
			// System.out.println("2. ["+severitaLogEmessoPerFiltro+"] <= ["+severitaOpenSPCoopRichiestaPdD+"]");
			boolean humanReadableAbilitato = false;
			if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
				humanReadableAbilitato = OpenSPCoop2Logger.loggerIntegrationManagerAbilitato;
			}else{
				humanReadableAbilitato = OpenSPCoop2Logger.loggerMsgDiagnosticoReadableAbilitato;
			}
			if( humanReadableAbilitato &&  (severitaLogEmessoPerFiltro <= severitaLog4JRichiestaPdD) ){
				
				if(msgDiag==null){
					msgDiag = this.getMsgDiagnostico(gdo, LogLevels.SEVERITA_DEBUG_HIGH, msg, codiceDiagnostico);
				}
				
				//ProtocolFactory protocolFactory = ProtocolFactoryManager.getInstance((String) this.pddContext.getObject(CostantiPdD.PROTOCOLLO));
				String message = OpenSPCoop2Logger.humanReadable(msgDiag,this.idCorrelazioneApplicativa,this.idCorrelazioneRisposta,
						this.porta,this.delegata,
						this.fruitore,this.servizio,this.servizioApplicativo,this.protocolFactory);
				try{
					if( IntegrationManager.ID_MODULO.equals(this.idModulo)){ 
						this.loggerIntegrationManager.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,message);
					}else{
						this.loggerOpenSPCoop2.log(LogLevels.LOG_LEVEL_DEBUG_HIGH,message);
					}
				}catch(Exception e){
					logError("Errore durante l'emissione del msg diagnostico 'human readable': "+e.getMessage(),e);
					gestioneErroreDiagnostica(e);
				}
			}

		}catch(Exception e){
			logError("MsgDiagnostico.highDebug error "+e.getMessage(),e);
			gestioneErroreDiagnostica(e);
		}
	}

	
	
	
	
	
	private org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico getMsgDiagnostico(Date gdo,int severitaLivelloOpenSPCoop2,String msg,String codiceDiagnostico){
		
		org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiagnostico = new org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico();
		
		msgDiagnostico.setGdo(gdo);
		
		msgDiagnostico.setIdSoggetto(this.idSoggettoDominio);
		msgDiagnostico.setIdFunzione(this.idModulo);
		msgDiagnostico.setSeverita(severitaLivelloOpenSPCoop2);
		msgDiagnostico.setMessaggio(msg);
		
		msgDiagnostico.setIdBusta(this.idMessaggioRichiesta);
		msgDiagnostico.setIdBustaRisposta(this.idMessaggioRisposta);
		msgDiagnostico.setCodice(codiceDiagnostico);
		
		msgDiagnostico.setProtocollo(this.protocolFactory.getProtocol());
		
		if(this.pddContext!=null){
			String [] key = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
			if(key!=null){
				for (int j = 0; j < key.length; j++) {
					Object o = this.pddContext.getObject(key[j]);
					if(o!=null && o instanceof String){
						msgDiagnostico.addProperty(key[j], (String)o);
					}
				}
			}
		}
		
		if(this.properties!=null){
			Enumeration<String> keys = this.properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				msgDiagnostico.addProperty(key, this.properties.get(key));
			}
		}
		
		return msgDiagnostico;
	}
	
	private void gestioneErroreDiagnostica(Exception e) {
		
		if(this.openspcoopProperties.isRegistrazioneDiagnosticaFallita_BloccoServiziPdD()){
			MsgDiagnostico.gestoreDiagnosticaDisponibile=false;
			MsgDiagnostico.motivoMalfunzionamentoDiagnostici=e;
			logError("Il Sistema di gestione della diagnostica ha rilevato un errore durante la registrazione di un messaggio diagnostico,"+
					" tutti i servizi/moduli della porta di dominio sono sospesi. Si richiede un intervento sistemistico per la risoluzione del problema "+
					"e il riavvio della Porta di Dominio. Errore rilevato: ",e);
		}
		
	}
	
	private void logError(String msgErrore,Exception e){
		if(this.loggerOpenSPCoop2Core!=null){
			this.loggerOpenSPCoop2Core.error(msgErrore,e);
		}
		else if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore,e);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore,e);
		}
		
	}
	private void logError(String msgErrore){
		if(this.loggerOpenSPCoop2Core!=null){
			this.loggerOpenSPCoop2Core.error(msgErrore);
		}
		else if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore);
		}
		
	}
}

