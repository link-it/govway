/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.dump;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.transazioni.utils.TransactionContentUtils;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.basic.BasicConnectionResult;
import org.openspcoop2.protocol.basic.BasicProducer;
import org.openspcoop2.protocol.basic.BasicProducerType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.dump.Attachment;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.Utilities;




/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione del dump su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DumpProducer extends BasicProducer implements IDumpProducer{

	private static final String format = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public DumpProducer(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory, BasicProducerType.DUMP);
	}

	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un tracciamento emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws TracciamentoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws DumpException{
		try{
			this.initializeAppender(appenderProperties, true);
		}catch(Exception e){
			throw new DumpException("Errore durante l'inizializzazione dell'appender: "+e.getMessage(),e);
		}
	}



	/**
	 * Dump di un messaggio
	 * 
	 * @param conOpenSPCoopPdD Connessione verso il database
	 * @param messaggio
	 * @throws DumpException
	 */
	@Override
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio) throws DumpException{

		if(messaggio==null)
			throw new DumpException("Errore durante il dump: messaggio is null");

		if(messaggio.getIdTransazione()==null)
			throw new DumpException("Errore durante il dump: id transazione is null");

		Date gdo = messaggio.getGdo();
		
		String protocollo = messaggio.getProtocollo();
		
		String idTransazione = messaggio.getIdTransazione();
		
		TipoMessaggio tipoMessaggio = messaggio.getTipoMessaggio();
		
		MessageType formatoMessaggio = messaggio.getFormatoMessaggio();
		
		if(this.debug){
			this.log.debug("@@ log["+idTransazione+"]["+tipoMessaggio+"] ....");
		}
		
		Connection con = null;
		BasicConnectionResult cr = null;
		try{
			//	Connessione al DB
			cr = this.getConnection(conOpenSPCoopPdD,"dump.log");
			con = cr.getConnection();

			if(this.debug){
				this.log.debug("@@ log["+idTransazione+"]["+tipoMessaggio+"] (getConnection finished) ....");
			}
			
			ServiceManagerProperties smProperties = new ServiceManagerProperties();
			smProperties.setDatabaseType(this.tipoDatabase);
			smProperties.setShowSql(this.debug);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, smProperties);
			
			org.openspcoop2.core.transazioni.dao.IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
			
			SimpleDateFormat dateformat = null;
			if(this.debug) {
				dateformat = new SimpleDateFormat (format); // SimpleDateFormat non e' thread-safe
			}
						
			// MESSAGGIO BASE
			DumpMessaggio dumpMessaggio = new DumpMessaggio();
			dumpMessaggio.setProtocollo(protocollo);
			dumpMessaggio.setIdTransazione(idTransazione);
			dumpMessaggio.setTipoMessaggio(tipoMessaggio);
						
			if(this.debug){
				this.log.debug("formato-messaggio: "+formatoMessaggio);
				this.log.debug("gdo: "+dateformat.format(gdo));
				this.log.debug("content-type["+messaggio.getContentType()+"]");
				if(messaggio.getBody()==null) {
					this.log.debug("body undefined");
				}
				else {
					this.log.debug("body: "+Utilities.convertBytesToFormatString(messaggio.getBody().length));
				}
			}
			if(formatoMessaggio!=null) {
				dumpMessaggio.setFormatoMessaggio(formatoMessaggio.name());
			}
			dumpMessaggio.setDumpTimestamp(gdo);
			dumpMessaggio.setContentType(messaggio.getContentType());
			dumpMessaggio.setBody(messaggio.getBody());
			
			if(messaggio.getBodyMultipartInfo()!=null) {
				if(this.debug){
					this.log.debug("multipart-body-content-id["+messaggio.getBodyMultipartInfo().getContentId()+"]");
					this.log.debug("multipart-body-content-location["+messaggio.getBodyMultipartInfo().getContentLocation()+"]");
					this.log.debug("multipart-body-content-type["+messaggio.getBodyMultipartInfo().getContentType()+"]");
				}
				dumpMessaggio.setMultipartContentId(messaggio.getBodyMultipartInfo().getContentId());
				dumpMessaggio.setMultipartContentLocation(messaggio.getBodyMultipartInfo().getContentLocation());
				dumpMessaggio.setMultipartContentType(messaggio.getBodyMultipartInfo().getContentType());
				if(messaggio.getBodyMultipartInfo().getHeaders()!=null && 
						messaggio.getBodyMultipartInfo().getHeaders().size()>0) {
					if(this.debug){
						this.log.debug("Dump "+messaggio.getBodyMultipartInfo().getHeaders().size()+" multipart-body headers");
					}
					Iterator<String> keys = messaggio.getBodyMultipartInfo().getHeaders().keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						
						String value = messaggio.getBodyMultipartInfo().getHeaders().get(key);
						if(value==null){
							value = ""; // puo' succedere in alcuni casi.
						}
						
						DumpMultipartHeader headerMultipart = new DumpMultipartHeader();
						headerMultipart.setNome(key);
						if(this.debug){
							this.log.debug("\t\t"+key+"="+value);
						}
						headerMultipart.setValore(value.toString());
						headerMultipart.setDumpTimestamp(gdo);
						dumpMessaggio.addMultipartHeader(headerMultipart);
					}
				}
			}
			
			dumpMessaggio.setPostProcessed(1);
			
			
			
			// HEADER TRASPORTO
			if(messaggio.getHeaders()!=null && messaggio.getHeaders().size()>0){
				if(this.debug){
					this.log.debug("Dump "+messaggio.getHeaders().size()+" headers");
				}

				Iterator<String> keys = messaggio.getHeaders().keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					
					String value = messaggio.getHeaders().get(key);
					if(value==null){
						value = ""; // puo' succedere in alcuni casi.
					}

					DumpHeaderTrasporto headerTrasporto = new DumpHeaderTrasporto();
					headerTrasporto.setNome(key);
					if(this.debug){
						this.log.debug("\t\t"+key+"="+value);
					}
					headerTrasporto.setValore(value.toString());
					headerTrasporto.setDumpTimestamp(gdo);
					dumpMessaggio.addHeaderTrasporto(headerTrasporto);
				}
			}
			
			// ALLEGATI
			if(messaggio.getAttachments()!=null && messaggio.getAttachments().size()>0) {
				
				if(this.debug){
					this.log.debug("Dump "+messaggio.getAttachments().size()+" attachments");
				}
				
				for (Attachment attach : messaggio.getAttachments()) {
					if(this.debug){
						this.log.debug("Attachment:");
						this.log.debug("\t\tId["+attach.getContentId()+"]");
						this.log.debug("\t\tlocation["+attach.getContentLocation()+"]");
						this.log.debug("\t\ttype["+attach.getContentType()+"]");
						if(attach.getContent()==null) {
							this.log.debug("\t\tcontent undefined");
						}
						else {
							this.log.debug("\t\tcontent: "+Utilities.convertBytesToFormatString(attach.getContent().length));
						}
					}

					DumpAllegato dumpAllegato = new DumpAllegato();
					dumpAllegato.setContentId(attach.getContentId());
					dumpAllegato.setContentLocation(attach.getContentLocation());
					dumpAllegato.setContentType(attach.getContentType());
					dumpAllegato.setAllegato(attach.getContent());
					dumpAllegato.setDumpTimestamp(gdo);
					dumpMessaggio.addAllegato(dumpAllegato);
					
					
					if(attach.getHeaders()!=null && attach.getHeaders().size()>0){
						if(this.debug){
							this.log.debug("Dump "+attach.getHeaders().size()+" headers dell'allegato con id ["+attach.getContentId()+"]");
						}

						Iterator<String> keys = attach.getHeaders().keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							
							String value = attach.getHeaders().get(key);
							if(value==null){
								value = ""; // puo' succedere in alcuni casi.
							}

							DumpHeaderAllegato headerAllegato = new DumpHeaderAllegato();
							headerAllegato.setNome(key);
							if(this.debug){
								this.log.debug("\t\t"+key+"="+value);
							}
							headerAllegato.setValore(value.toString());
							headerAllegato.setDumpTimestamp(gdo);
							dumpAllegato.addHeader(headerAllegato);
						}
					}
				}
					
			}
			
			// CONTENUTI
			if(messaggio.getContenuti()!=null && messaggio.getContenuti().size()>0){
				if(this.debug){
					this.log.debug("Dump "+messaggio.getContenuti().size()+" contenuti");
				}

				Iterator<String> keys = messaggio.getContenuti().keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					
					String value = messaggio.getHeaders().get(key);
					if(value==null){
						value = ""; // puo' succedere in alcuni casi.
					}

					DumpContenuto contenuto = TransactionContentUtils.createDumpContenuto(key, value, gdo); 
					
					if(this.debug){
						this.log.debug("\t\t"+key+"="+value);
					}
					
					dumpMessaggio.addContenuto(contenuto);
				}
			}
			
			if(this.debug){
				this.log.debug("@@ log["+idTransazione+"]["+tipoMessaggio+"] registrazione in corso ...");
			}
			
			dumpMessageService.create(dumpMessaggio);
			
			if(this.debug){
				this.log.debug("@@ log["+idTransazione+"]["+tipoMessaggio+"] registrazione completata");
			}

		}catch(Exception e){
			throw new DumpException("Errore durante il dump del messaggio idTransazione["+idTransazione+"] tipoMessaggio["+tipoMessaggio+"]: "+e.getMessage(),e);
		}finally{
			try{
				this.releaseConnection(cr, "dump.log");
			}catch(Exception e){}
		}
	}


}
