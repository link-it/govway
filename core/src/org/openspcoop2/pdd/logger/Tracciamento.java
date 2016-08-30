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
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.handlers.GeneratoreCasualeDate;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.TracciaBuilder;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciamentoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;


/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop
 * per il tracciamento di buste.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Tracciamento {

	/** Indicazione di un tracciamento funzionante */
	public static boolean tracciamentoDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel tracciamento */
	public static Exception motivoMalfunzionamentoTracciamento = null;
	
	/**  Logger log4j utilizzato per scrivere i tracciamenti */
	protected Logger loggerTracciamento = null;

	/** Tipo Porta di Dominio */
	private TipoPdD tipoPdD;
	
	/** Soggetto che richiede il logger */
	private IDSoggetto idSoggettoDominio;
	
	/** PdDContext */
	private PdDContext pddContext;
	
	/** Protocol Factory */
	private ProtocolFactoryManager protocolFactoryManager = null;
	private IProtocolFactory protocolFactory;
	
	/** Appender personalizzati per i tracciamenti di OpenSPCoop */
	private Vector<ITracciamentoOpenSPCoopAppender> loggerTracciamentoOpenSPCoopAppender = null; 
	private Vector<String> tipoTracciamentoOpenSPCoopAppender = null; 

	/** Reader della configurazione di OpenSPCoop */
	private ConfigurazionePdDManager configurazionePdDManager;
	
	/** Stati */
	private IState statoRichiesta;
	private IState statoRisposta;
	
	/** XMLBuilder */
	private TracciaBuilder xmlBuilder;

	/** MsgDiagnostico per eventuali errori di tracciamento */
	private MsgDiagnostico msgDiagErroreTracciamento = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	
	/** Generatore di date casuali*/
	private GeneratoreCasualeDate generatoreDateCasuali = null;
	
	public static String createLocationString(boolean bustaRicevuta,String location){
		if(bustaRicevuta)
			return ConnettoreUtils.limitLocation255Character(CostantiPdD.TRACCIAMENTO_IN+CostantiPdD.TRACCIAMENTO_SEPARATOR_LOCATION+location);
		else
			return ConnettoreUtils.limitLocation255Character(CostantiPdD.TRACCIAMENTO_OUT+CostantiPdD.TRACCIAMENTO_SEPARATOR_LOCATION+location);
	}
	
	
	/**
	 * Costruttore. 
	 *
	 * @param idSoggettoDominio Soggetto che richiede il logger
	 * @throws TracciamentoException 
	 * 
	 */
	public Tracciamento(IDSoggetto idSoggettoDominio,String idFunzione,PdDContext pddContext,TipoPdD tipoPdD,IState statoRichiesta,IState statoRisposta) throws TracciamentoException {
		this.idSoggettoDominio = idSoggettoDominio;
		this.pddContext=pddContext;
		this.tipoPdD = tipoPdD;
		
		this.loggerTracciamento = OpenSPCoop2Logger.loggerTracciamento;
		this.loggerTracciamentoOpenSPCoopAppender = OpenSPCoop2Logger.loggerTracciamentoOpenSPCoopAppender;
		this.tipoTracciamentoOpenSPCoopAppender = OpenSPCoop2Logger.tipoTracciamentoOpenSPCoopAppender;
		this.statoRichiesta = statoRichiesta;
		this.statoRisposta = statoRisposta;
		this.configurazionePdDManager = ConfigurazionePdDManager.getInstance(this.statoRichiesta,this.statoRisposta);
		this.msgDiagErroreTracciamento = new MsgDiagnostico(idSoggettoDominio,idFunzione,this.statoRichiesta,this.statoRisposta);
		this.msgDiagErroreTracciamento.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO);
		try{
			this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
			this.protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName((String) this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
		} catch(Throwable e){
			throw new TracciamentoException(e.getMessage(),e);
		}
		this.xmlBuilder = new TracciaBuilder(this.protocolFactory);
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato()){
			this.generatoreDateCasuali = GeneratoreCasualeDate.getGeneratoreCasualeDate();
		}
	}

	public void updateState(IState statoRichiesta,IState statoRisposta){
		this.statoRichiesta = statoRichiesta;
		this.statoRisposta = statoRisposta;
	}

	/**
	 * Il Metodo si occupa di impostare il dominio del Soggetto che utilizza il logger. 
	 *
	 * @param dominio Soggetto che richiede il logger
	 * 
	 */
	public void setDominio(IDSoggetto dominio){
		this.idSoggettoDominio = dominio;
	}


	/**
	 * Filter. 
	 *
	 * @deprecated  utility che elimina i caratteri XML codificati
	 */
	@Deprecated  public String filter(String msg){
		String xml = msg.replaceAll("&lt;","<");
		xml = xml.replaceAll("&quot;","\"");
		xml = xml.replaceAll("&gt;",">");
		return xml;
	}


	private Connection getConnectionFromState(boolean richiesta){
		if(richiesta){
			if(this.statoRichiesta!=null && this.statoRichiesta instanceof StateMessage){
				boolean validConnection = false;
				try{
					validConnection = !((StateMessage)this.statoRichiesta).getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return ((StateMessage)this.statoRichiesta).getConnectionDB();
			}
		}
		else{
			if(this.statoRisposta!=null && this.statoRisposta instanceof StateMessage){
				boolean validConnection = false;
				try{
					validConnection = !((StateMessage)this.statoRisposta).getConnectionDB().isClosed();
				}catch(Exception e){}
				if(validConnection)
					return ((StateMessage)this.statoRisposta).getConnectionDB();
			}
		}
		return null;
	}
	
	




	/** ----------------- METODI DI LOGGING (Tracciamento buste) ---------------- */

	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare
	 * 
	 */
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito,String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,esito,location,null);
	}
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito,String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		if(this.configurazionePdDManager.tracciamentoBuste()){
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				busta.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(busta, msg,securityInfo, esito, gdo, TipoTraccia.RICHIESTA, location, idCorrelazioneApplicativa);
			
			try{
				
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}

				//	Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
					}catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare in byte
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito,String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,bustaObject,esito,location,null);
	}
	@Deprecated
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito,String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		if(this.configurazionePdDManager.tracciamentoBuste()){
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				bustaObject.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, TipoTraccia.RICHIESTA, location, idCorrelazioneApplicativa);
			traccia.setBustaAsByteArray(busta);
			
			try{
		
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}
					
				// Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
					}catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di richiesta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			SOAPElement busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRichiesta(msg,securityInfo,busta,bustaObject,esito,location,null);
	}
	public void registraRichiesta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			SOAPElement busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa) throws TracciamentoException {
		if(this.configurazionePdDManager.tracciamentoBuste()) {
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				bustaObject.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, TipoTraccia.RICHIESTA, location, idCorrelazioneApplicativa);
			traccia.setBustaAsElement(busta);
			
			try{
				
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}
					
				// Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(true), traccia);
					} catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della richiesta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RICHIESTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	



	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * 
	 */
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,esito,location,null,null);
	}
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			Busta busta,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException {
		if(this.configurazionePdDManager.tracciamentoBuste()){
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				busta.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(busta, msg, securityInfo, esito, gdo, TipoTraccia.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
			
			try{
							
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}
					
				// Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
					}catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,bustaObject,esito, location,null,null);
	}
	@Deprecated
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			byte[] busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException{
		if(this.configurazionePdDManager.tracciamentoBuste()){
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				bustaObject.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, TipoTraccia.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
			traccia.setBustaAsByteArray(busta);
			
			try{
								
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}
					
				// Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
					}catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	/**
	 * Il Metodo si occupa di tracciare una busta di risposta.
	 *
	 * @param busta Busta da registrare
	 * @param bustaObject Busta da registrare (per appender personalizzati)
	 * 
	 */
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			SOAPElement busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location) throws TracciamentoException {
		registraRisposta(msg,securityInfo,busta,bustaObject,esito, location,null,null);
	}
	public void registraRisposta(OpenSPCoop2Message msg,SecurityInfo securityInfo,
			SOAPElement busta,Busta bustaObject,EsitoElaborazioneMessaggioTracciato esito, String location, 
			String idCorrelazioneApplicativa, String idCorrelazioneApplicativaRisposta) throws TracciamentoException {
		if(this.configurazionePdDManager.tracciamentoBuste()){
			String xml = null;
			boolean erroreAppender = false;
			
			// Data
			Date gdo = DateManager.getDate();
			if(this.openspcoopProperties.generazioneDateCasualiLogAbilitato() && this.pddContext!=null && this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID)!=null){
				gdo = this.generatoreDateCasuali.getProssimaData((String)this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID));
				bustaObject.setOraRegistrazione(gdo);
			}
			
			// Traccia
			Traccia traccia = this.getTraccia(bustaObject, msg, securityInfo, esito, gdo, TipoTraccia.RISPOSTA, location, idCorrelazioneApplicativa, idCorrelazioneApplicativaRisposta);
			traccia.setBustaAsElement(busta);
			
			try{
			
				// Miglioramento performance
				if(OpenSPCoop2Logger.loggerTracciamentoAbilitato){
					xml = this.xmlBuilder.toString(traccia);
					if(xml==null)
						throw new Exception("Traccia non costruita");
					this.loggerTracciamento.info(xml);
				}
					
				// Tracciamento personalizzato
				for(int i=0; i<this.loggerTracciamentoOpenSPCoopAppender.size();i++){
					try{
						this.loggerTracciamentoOpenSPCoopAppender.get(i).log(getConnectionFromState(false), traccia);
					}catch(Exception e){
						logError("Errore durante il tracciamento personalizzato ["+this.tipoTracciamentoOpenSPCoopAppender.get(i)+"] della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoTracciamentoOpenSPCoopAppender.get(i));
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita.openspcoopAppender");
						}catch(Exception eMsg){}
						if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
							erroreAppender = true;
							throw e; // Rilancio
						}
					}
				}
			}catch(Exception e){
				// check eventuale costruzione dell'xml non fatto per log4j disabilitato.
				if( (xml==null) && (OpenSPCoop2Logger.loggerTracciamentoAbilitato==false) ){
					try{
						xml = this.xmlBuilder.toString(traccia);
					}catch(Exception eBuild){}
				}
				if(xml==null){
					logError("Errore durante la costruzione della traccia: "+e.getMessage(),e);
				}else{
					logError("Errore durante il tracciamento della risposta: "+e.getMessage()+". Traccia non registrata:\n"+xml,e);
					if(!erroreAppender){
						try{
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, TipoTraccia.RISPOSTA.toString());
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIA, xml);
							this.msgDiagErroreTracciamento.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
							this.msgDiagErroreTracciamento.logPersonalizzato("registrazioneNonRiuscita");
						}catch(Exception eMsg){}
					}
				}
				gestioneErroreTracciamento(e);
			}
		}
	}
	

	
	private Traccia getTraccia(Busta busta,OpenSPCoop2Message msg,SecurityInfo securityInfo,EsitoElaborazioneMessaggioTracciato esito,Date gdo,TipoTraccia tipoTraccia,
			String location,String idCorrelazioneApplicativa){
		return getTraccia(busta, msg, securityInfo, esito, gdo, tipoTraccia, location, idCorrelazioneApplicativa, null);
	}
	private Traccia getTraccia(Busta busta,OpenSPCoop2Message msg,SecurityInfo securityInfo,EsitoElaborazioneMessaggioTracciato esito,Date gdo,TipoTraccia tipoTraccia,
			String location,String idCorrelazioneApplicativa,String idCorrelazioneApplicativaRisposta){
		
		Traccia traccia = new Traccia();
		
		// Esito
		traccia.setEsitoElaborazioneMessaggioTracciato(esito);
		try{
			if(TipoTraccia.RISPOSTA.equals(tipoTraccia)){
				SOAPBody body = msg.getSOAPBody();
				if(body!=null && body.hasFault()){
					StringBuffer bf = new StringBuffer();
					if(esito.getDettaglio()!=null){
						bf.append(esito.getDettaglio());
						bf.append("\n");
					}
					bf.append(SoapUtils.toString(body.getFault()));
					traccia.getEsitoElaborazioneMessaggioTracciato().setDettaglio(bf.toString());
				}
			}
		}catch(Exception e){
			this.logError("errore durante la registrazione del SOAPFault nelle tracce", e);
		}
		
		traccia.setGdo(gdo);
		traccia.setIdSoggetto(this.idSoggettoDominio);
		traccia.setTipoMessaggio(tipoTraccia);
		traccia.setTipoPdD(this.tipoPdD);
				
		traccia.setCorrelazioneApplicativa(idCorrelazioneApplicativa);
		traccia.setCorrelazioneApplicativaRisposta(idCorrelazioneApplicativaRisposta);
		traccia.setLocation(location);
		traccia.setProtocollo(this.protocolFactory.getProtocol());
		
		if(securityInfo!=null){
			busta.setDigest(securityInfo.getDigestHeader());
			Hashtable<String, String> properties = securityInfo.getProperties();
			Enumeration<String> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				busta.addProperty(key, properties.get(key)); // aggiungo le proprieta' di sicurezza riscontrate
			}
		}
		traccia.setBusta(busta);
		
		if(msg!=null){
			java.util.Iterator<?> it = msg.getAttachments();
		    while(it.hasNext()){
		    	AttachmentPart ap = 
		    		(AttachmentPart) it.next();
		    	Allegato allegato = new Allegato();
		    	allegato.setContentId(ap.getContentId());
		    	allegato.setContentLocation(ap.getContentLocation());
		    	allegato.setContentType(ap.getContentType());
		    	
		    	if(securityInfo!=null && ap.getContentId()!=null){
			    	for (int i = 0; i < securityInfo.sizeListaAllegati(); i++) {
						Allegato a = securityInfo.getAllegato(i);
						if(a.getContentId()!=null && a.getContentId().equals(ap.getContentId())){
							allegato.setDigest(a.getDigest());
						}
					}
		    	}
		    	
		    	traccia.addAllegato(allegato);
		    }
		}
		
		if(this.pddContext!=null){
			String [] key = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
			if(key!=null){
				for (int j = 0; j < key.length; j++) {
					Object o = this.pddContext.getObject(key[j]);
					if(o!=null && o instanceof String){
						traccia.addProperty(key[j], (String)o);
					}
				}
			}
		}
		
		return traccia;
	}
	
	private void gestioneErroreTracciamento(Exception e) throws TracciamentoException{
		
		if(this.openspcoopProperties.isTracciaturaFallita_BloccoServiziPdD()){
			Tracciamento.tracciamentoDisponibile=false;
			Tracciamento.motivoMalfunzionamentoTracciamento=e;
			try{
				this.msgDiagErroreTracciamento.logPersonalizzato("errore.bloccoServizi");
			}catch(Exception eMsg){}
			logError("Il Sistema di tracciamento ha rilevato un errore durante la registrazione di una traccia legale,"+
					" tutti i servizi/moduli della porta di dominio sono sospesi. Si richiede un intervento sistemistico per la risoluzione del problema "+
					"e il riavvio della Porta di Dominio. Errore rilevato: ",e);
		}
		
		if(this.openspcoopProperties.isTracciaturaFallita_BloccaCooperazioneInCorso()){
			throw new TracciamentoException(e);
		}
	}
	
	
	private void logError(String msgErrore,Exception e){
		if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore,e);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore,e);
		}
		
	}
	@SuppressWarnings("unused")
	private void logError(String msgErrore){
		if(OpenSPCoop2Logger.loggerOpenSPCoopCore!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopCore.error(msgErrore);
		}
		
		// Registro l'errore anche in questo logger.
		if(OpenSPCoop2Logger.loggerOpenSPCoopResources!=null){
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error(msgErrore);
		}
		
	}
}

