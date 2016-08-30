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

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoMessaggio;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.AttachmentsUtils;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.dump.IDumpOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;



/**
 * Contiene la definizione un Logger utilizzato dai nodi dell'infrastruttura di OpenSPCoop
 * per la registrazione di messaggi diagnostici.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Dump {

	/** Indicazione di un dump funzionante */
	public static boolean sistemaDumpDisponibile = true;
	/** Primo errore avvenuto nel momento in cui è stato rilevato un malfunzionamento nel sistema di dump */
	public static Exception motivoMalfunzionamentoDump = null;


	/**  Logger log4j utilizzato per effettuare un dump dei messaggi applicativi */
	private Logger loggerDump = null;
	
	/** Soggetto che richiede il logger */
	private IDSoggetto dominio;
	/** Modulo Funzionale */
	private String idModulo;
	/** Identificativo del Messaggio */
	private String idMessaggio;
	/** Fruitore */
	private IDSoggetto fruitore;
	/** Servizio */
	private IDServizio servizio;
	/** Signature */
	private String signature;
	/** GDO */
	private Date gdo;
	/** TipoPdD */
	private TipoPdD tipoPdD;
	/** PdDContext */
	private PdDContext pddContext;

	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties properties = null;

	/** MsgDiagnostico per eventuali errori di tracciamento */
	private MsgDiagnostico msgDiagErroreDump = null;
	
	/** Appender personalizzati per i dump applicativi di OpenSPCoop */
	private Vector<IDumpOpenSPCoopAppender> loggerDumpOpenSPCoopAppender = null; 
	private Vector<String> tipoDumpOpenSPCoopAppender = null; 
	
	private IProtocolFactory protocolFactory = null;
	
	/** Stati */
	private IState statoRichiesta;
	private IState statoRisposta;
	
	/**
	 * Costruttore. 
	 *
	 * @param dominio Soggetto che richiede il logger
	 * @param modulo Funzione che richiede il logger
	 * @param pddContext pddContext
	 * 
	 */
	public Dump(IDSoggetto dominio, String modulo, TipoPdD tipoPdD, PdDContext pddContext,IState statoRichiesta,IState statoRisposta) throws TracciamentoException{
		this(dominio, modulo, null, null, null, tipoPdD, pddContext,statoRichiesta,statoRisposta);
	}
	public Dump(IDSoggetto dominio, String modulo, String idMessaggio, IDSoggetto fruitore, IDServizio servizio, TipoPdD tipoPdD, PdDContext pddContext,IState statoRichiesta,IState statoRisposta) throws TracciamentoException{
		this.dominio = dominio;
		this.idModulo = modulo;
		this.idMessaggio = idMessaggio;
		this.fruitore = fruitore;
		this.servizio = servizio;
		this.loggerDump = OpenSPCoop2Logger.loggerDump;
		this.loggerDumpOpenSPCoopAppender = OpenSPCoop2Logger.loggerDumpOpenSPCoopAppender;
		this.tipoDumpOpenSPCoopAppender = OpenSPCoop2Logger.tipoDumpOpenSPCoopAppender;
		this.gdo = DateManager.getDate();
		if(this.dominio!=null)
			this.signature = this.dominio.getCodicePorta()+" <"+this.gdo+"> "+this.idModulo+"\n";
		else
			this.signature = "<"+this.gdo+"> "+this.idModulo+"\n";
		this.tipoPdD = tipoPdD;
		this.pddContext = pddContext;
		this.properties = OpenSPCoop2Properties.getInstance();
		this.statoRichiesta = statoRichiesta;
		this.statoRisposta = statoRisposta;
		this.msgDiagErroreDump = new MsgDiagnostico(dominio,modulo,this.statoRichiesta,this.statoRisposta);
		this.msgDiagErroreDump.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_TRACCIAMENTO);
		// Protocol Factory Manager
		String protocol = null;
		try{
			this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName((String) pddContext.getObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO));
			protocol = this.protocolFactory.getProtocol();
			this.msgDiagErroreDump.setPddContext(pddContext, this.protocolFactory);
		}catch (Exception e) {
			throw new TracciamentoException("Errore durante l'inizializzazione del ProtocolFactoryManager...",e);
		}
		if(this.dominio==null){
			this.dominio=OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocol);
		}
	}








	/** ----------------- METODI DI LOGGING  ---------------- */

	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	@Deprecated
	public void dumpRichiestaMessage(byte[] msg) throws TracciamentoException {

		try{
		    this.loggerDump.info(this.signature+"Messaggio di richiesta con identificativo ["+this.idMessaggio+"]:\n"+new String(msg)); 
		}catch(Exception e){
		    this.loggerDump.error("Riscontrato errore durante il dump del messaggio con identificativo ["+this.idMessaggio+"]:"+e); 
		    gestioneErroreDump(e);
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	@Deprecated
	public void dumpRichiestaMessage(OpenSPCoop2Message msg) throws TracciamentoException {

		try{
		    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    msg.writeTo(stream,false);
		    
		    this.loggerDump.info(this.signature+"Messaggio di richiesta con identificativo ["+this.idMessaggio+"]:\n"+stream.toString()); 
		}catch(Exception e){
		    this.loggerDump.error("Riscontrato errore durante il dump del messaggio con identificativo ["+this.idMessaggio+"]:"+e); 
		    gestioneErroreDump(e);
		}
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRichiestaIngresso(byte[] msg, InfoConnettoreIngresso infoConnettore) throws TracciamentoException {
		dump(TipoMessaggio.RICHIESTA_INGRESSO,null,msg,infoConnettore.getFromLocation(),infoConnettore.getUrlProtocolContext().getParametersTrasporto());
	}

	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRichiestaIngresso(OpenSPCoop2Message msg, InfoConnettoreIngresso infoConnettore) throws TracciamentoException {
		dump(TipoMessaggio.RICHIESTA_INGRESSO,msg,null,infoConnettore.getFromLocation(),infoConnettore.getUrlProtocolContext().getParametersTrasporto());
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRichiestaUscita(byte[] msg, InfoConnettoreUscita infoConnettore) throws TracciamentoException {
		dump(TipoMessaggio.RICHIESTA_USCITA,null,msg,infoConnettore.getLocation(),infoConnettore.getPropertiesTrasporto());
	}

	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRichiestaUscita(OpenSPCoop2Message msg, InfoConnettoreUscita infoConnettore) throws TracciamentoException {
		dump(TipoMessaggio.RICHIESTA_USCITA,msg,null,infoConnettore.getLocation(),infoConnettore.getPropertiesTrasporto());
	}
	
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void dumpRispostaMessage(byte[] msg) {

		try{
		   this.loggerDump.info(this.signature+"Messaggio di risposta relativa alla richiesta con identificativo ["+this.idMessaggio+"]:\n"+new String(msg)); 
		}catch(Exception e){
		    this.loggerDump.error("Riscontrato errore durante il dump del messaggio con identificativo ["+this.idMessaggio+"]:"+e); 
		}
	}
	
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * 
	 */
	@Deprecated
	public void dumpRispostaMessage(OpenSPCoop2Message msg) {

		try{
		    java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
		    msg.writeTo(stream,false);
		    
		    this.loggerDump.info(this.signature+"Messaggio di risposta relativa alla richiesta con identificativo ["+this.idMessaggio+"]:\n"+stream.toString()); 
		}catch(Exception e){
		    this.loggerDump.error("Riscontrato errore durante il dump del messaggio con identificativo ["+this.idMessaggio+"]:"+e); 
		}
	}

	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRispostaIngresso(byte[] msg, InfoConnettoreUscita infoConnettore, java.util.Properties transportHeaderRisposta) throws TracciamentoException {
		dump(TipoMessaggio.RISPOSTA_INGRESSO,null,msg,infoConnettore.getLocation(),transportHeaderRisposta);
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRispostaIngresso(OpenSPCoop2Message msg, InfoConnettoreUscita infoConnettore, java.util.Properties transportHeaderRisposta) throws TracciamentoException {
		dump(TipoMessaggio.RISPOSTA_INGRESSO,msg,null,infoConnettore.getLocation(),transportHeaderRisposta);
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRispostaUscita(byte[] msg, InfoConnettoreIngresso infoConnettore, java.util.Properties transportHeaderRisposta) throws TracciamentoException {
		dump(TipoMessaggio.RISPOSTA_USCITA,null,msg,infoConnettore.getFromLocation(),transportHeaderRisposta);
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpRispostaUscita(OpenSPCoop2Message msg, InfoConnettoreIngresso infoConnettore, java.util.Properties transportHeaderRisposta) throws TracciamentoException {
		dump(TipoMessaggio.RISPOSTA_USCITA,msg,null,infoConnettore.getFromLocation(),transportHeaderRisposta);
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	public void dumpIntegrationManagerGetMessage(OpenSPCoop2Message msg) throws TracciamentoException {
		dump(TipoMessaggio.INTEGRATION_MANAGER,msg,null,null,null);
	}
	
	/**
	 * Il Metodo si occupa di creare un effettuare un dump del messaggio applicativo. 
	 *
	 * @param msg Messaggio da registrare
	 * @throws TracciamentoException 
	 * 
	 */
	private void dump(TipoMessaggio tipoMessaggio,OpenSPCoop2Message msg,byte[] msgBytes, 
			String location,java.util.Properties transportHeader) throws TracciamentoException {

		// Log4J
		if(OpenSPCoop2Logger.loggerDumpAbilitato){
			try{
				StringBuffer out = new StringBuffer();
				out.append(this.signature);
				out.append("TipoMessaggio:"+tipoMessaggio.getTipo());
				
				if(this.idMessaggio!=null){
					out.append(" idMessaggio:");
					out.append(this.idMessaggio);
				}
				
				if(location!=null){
					if(TipoMessaggio.RICHIESTA_INGRESSO.equals(tipoMessaggio) || TipoMessaggio.RISPOSTA_USCITA.equals(tipoMessaggio)){
						out.append(" source:");
					}else{
						out.append(" location:");
					}
					out.append(location);
				}
				
				if( this.fruitore!=null ){
					out.append(" FR:");
					out.append(this.fruitore.toString());
				}
				if( this.fruitore!=null && this.servizio!=null)
					out.append(" -> ");
				if( this.servizio!=null ){
					if(this.servizio.getServizio()!=null && this.servizio.getTipoServizio()!=null && this.servizio.getSoggettoErogatore()!=null){
						out.append(" S:");
						out.append(this.servizio.toString());
					}else if(this.servizio.getSoggettoErogatore()!=null){
						out.append(" ER:");
						out.append(this.servizio.getSoggettoErogatore().toString());
					}
				}
				
				out.append(" \n");
				
				// HEADER
				if(transportHeader!=null && transportHeader.size()>0){
					Enumeration<?> keys = transportHeader.keys();
					out.append("------ Header di trasporto ------\n");
					while(keys.hasMoreElements()){
						String key = (String) keys.nextElement();
						Object value = transportHeader.getProperty(key);
						if(value instanceof String){
							out.append(key+"="+value+"\n");
						}
						else{
							out.append(key+"=ObjectType("+value.getClass().getName()+")\n");
						}
					}
				}
				
				if(msg!=null){
					// NOTA: Non aggiungere ContentType in Axis. Congela il message nella writeTo e non e' possibile modificarlo.
					//out.append("] (ContentType:"+msg.getContentType(new org.apache.axis.soap.SOAP11Constants())+") \n");
					out.append(MessageUtils.dumpMessage(msg, this.properties.isDumpAllAttachments()));
				}else{
					if(AttachmentsUtils.messageWithAttachment(msgBytes)){
						out.append("------ SOAPWithAttachments ------\n");
					}else{
						out.append("------ SOAPEnvelope ------\n");
					}
					out.append(new String(msgBytes));
				}
				
			    this.loggerDump.info(out.toString()); 
			}catch(Exception e){
				try{
					// Registro l'errore sul file dump.log
					this.loggerDump.error("Riscontrato errore durante il dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio.getTipo()+") con identificativo ["+this.idMessaggio+"]:"+e.getMessage());
				}catch(Exception eLog){}
				OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante il dump del contenuto applicativo presente nel messaggio ("+tipoMessaggio.getTipo()+") con identificativo ["+this.idMessaggio+"]: "+e.getMessage(),e);
				try{
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getTipo());
					this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
					this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita");
				}catch(Exception eMsg){}
				gestioneErroreDump(e);
			}
		}
		
		// Il dump via API lo effettuo solamente se ho davvero un messaggio OpenSPCoop
		// Senno ottengo errori all'interno delle implementazioni degli appender dump
		if(msg!=null){
			for(int i=0; i<this.loggerDumpOpenSPCoopAppender.size();i++){
				try{
					Messaggio messaggioDump = new Messaggio();
					messaggioDump.setGdo(this.gdo);
					messaggioDump.setIdBusta(this.idMessaggio);
					messaggioDump.setIdPorta(this.dominio);
					messaggioDump.setIdFunzione(this.idModulo);
					messaggioDump.setFruitore(this.fruitore);
					messaggioDump.setServizio(this.servizio);
					messaggioDump.setMsg(msg);
					messaggioDump.setLocation(location);
					messaggioDump.setTransportHeader(transportHeader);
					messaggioDump.setTipoPdD(this.tipoPdD);
					messaggioDump.setProtocollo(this.protocolFactory.getProtocol());
					messaggioDump.setTipoMessaggio(tipoMessaggio);
					
					if(this.pddContext!=null){
						String [] key = org.openspcoop2.core.constants.Costanti.CONTEXT_OBJECT;
						if(key!=null){
							for (int j = 0; j < key.length; j++) {
								Object o = this.pddContext.getObject(key[j]);
								if(o!=null && o instanceof String){
									messaggioDump.addProperty(key[j], (String)o);
								}
							}
						}
					}
					this.loggerDumpOpenSPCoopAppender.get(i).dump(messaggioDump);
				}catch(Exception e){
					OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Errore durante il dump personalizzato ["+this.tipoDumpOpenSPCoopAppender.get(i)+"] del contenuto applicativo presente nel messaggio ("+tipoMessaggio.getTipo()+") con identificativo ["+this.idMessaggio+"]: "+e.getMessage(),e);
					try{
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIA_TIPO, tipoMessaggio.getTipo());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_ERRORE,e.getMessage());
						this.msgDiagErroreDump.addKeyword(CostantiPdD.KEY_TRACCIAMENTO_PERSONALIZZATO,this.tipoDumpOpenSPCoopAppender.get(i));
						this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.registrazioneNonRiuscita.openspcoopAppender");
					}catch(Exception eMsg){}
					gestioneErroreDump(e);
				}
			}
		}
	}
	

	
	private void gestioneErroreDump(Exception e) throws TracciamentoException{
		
		if(this.properties.isDumpFallito_BloccoServiziPdD()){
			Dump.sistemaDumpDisponibile=false;
			Dump.motivoMalfunzionamentoDump=e;
			try{
				this.msgDiagErroreDump.logPersonalizzato("dumpContenutiApplicativi.errore.bloccoServizi");
			}catch(Exception eMsg){}
			OpenSPCoop2Logger.loggerOpenSPCoopResources.error("Il Sistema di dump dei contenuti applicativi ha rilevato un errore "+
					"durante la registrazione di un contenuto applicativo, tutti i servizi/moduli della porta di dominio sono sospesi."+
					" Si richiede un intervento sistemistico per la risoluzione del problema e il riavvio della Porta di Dominio. "+
					"Errore rilevato: ",e);
		}
		
		if(this.properties.isDumpFallito_BloccaCooperazioneInCorso()){
			throw new TracciamentoException(e);
		}
	}
}

