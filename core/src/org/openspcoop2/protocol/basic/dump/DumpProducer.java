/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderAllegato;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.DumpMultipartHeader;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
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
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.transport.TransportUtils;




/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione del dump su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DumpProducer extends BasicProducer implements IDumpProducer{

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
		this.dump(conOpenSPCoopPdD, messaggio, false);
	}
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio,boolean headersCompact) throws DumpException{

		if(messaggio==null)
			throw new DumpException("Errore durante il dump: messaggio is null");

		if(messaggio.getIdTransazione()==null)
			throw new DumpException("Errore durante il dump: id transazione is null");

		Date gdo = messaggio.getGdo();
		
		String protocollo = messaggio.getProtocollo();
		
		String idTransazione = messaggio.getIdTransazione();
		
		TipoMessaggio tipoMessaggio = messaggio.getTipoMessaggio();
		
		MessageType formatoMessaggio = messaggio.getFormatoMessaggio();
		
		String servizioApplicativoErogatore = messaggio.getServizioApplicativoErogatore();
		Date dataConsegnaErogatore = messaggio.getDataConsegna();
		
		String identificativoDump = "["+idTransazione+"]["+tipoMessaggio+"]";
		if(servizioApplicativoErogatore!=null) {
			identificativoDump=identificativoDump+"["+servizioApplicativoErogatore+"]";
		}
		if(dataConsegnaErogatore!=null) {
			identificativoDump=identificativoDump+"["+DateUtils.getSimpleDateFormatMs().format(dataConsegnaErogatore)+"]";
		}
		
		if(this.debug){
			this.log.debug("@@ log"+identificativoDump+" ....");
		}
		
		Connection con = null;
		BasicConnectionResult cr = null;
		try{
			//	Connessione al DB
			cr = this.getConnection(conOpenSPCoopPdD,"dump.log");
			con = cr.getConnection();

			if(this.debug){
				this.log.debug("@@ log"+identificativoDump+" (getConnection finished) ....");
			}
			
			ServiceManagerProperties smProperties = new ServiceManagerProperties();
			smProperties.setDatabaseType(this.tipoDatabase);
			smProperties.setShowSql(this.debug);
			JDBCServiceManager jdbcServiceManager = new JDBCServiceManager(con, smProperties);
			
			org.openspcoop2.core.transazioni.dao.IDumpMessaggioService dumpMessageService = jdbcServiceManager.getDumpMessaggioService();
			
			SimpleDateFormat dateformat = null;
			if(this.debug) {
				dateformat = DateUtils.getSimpleDateFormatMs();
			}
						
			// MESSAGGIO BASE
			
			File fDump = null;
			
			DumpMessaggio dumpMessaggio = new DumpMessaggio();
			dumpMessaggio.setProtocollo(protocollo);
			dumpMessaggio.setIdTransazione(idTransazione);
			dumpMessaggio.setServizioApplicativoErogatore(servizioApplicativoErogatore);
			dumpMessaggio.setDataConsegnaErogatore(dataConsegnaErogatore);
			dumpMessaggio.setTipoMessaggio(tipoMessaggio);
						
			if(this.debug){
				this.log.debug("formato-messaggio: "+formatoMessaggio);
				this.log.debug("gdo: "+dateformat.format(gdo));
				this.log.debug("content-type["+messaggio.getContentType()+"]");
				if(messaggio.getBody()==null) {
					this.log.debug("body undefined");
				}
				else {
					this.log.debug("body: "+Utilities.convertBytesToFormatString(messaggio.getBody().size()));
				}
			}
			if(formatoMessaggio!=null) {
				dumpMessaggio.setFormatoMessaggio(formatoMessaggio.name());
			}
			dumpMessaggio.setDumpTimestamp(gdo);
			dumpMessaggio.setContentType(messaggio.getContentType());
			if(messaggio.getBody()!=null && messaggio.getBody().size()>0) {
				if(!messaggio.getBody().isSerializedOnFileSystem()) {			
					dumpMessaggio.setBody(messaggio.getBody().toByteArray());
					dumpMessaggio.setContentLength(Long.valueOf(messaggio.getBody().size()));
				}
				else {
					fDump = messaggio.getBody().getSerializedFile();
					dumpMessaggio.setContentLength(fDump.length());
				}
			}
			
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
					
					Map<String, List<String>> propertiesHdr = new HashMap<String, List<String>>();
					List<DumpMultipartHeader> backupFailed = new ArrayList<DumpMultipartHeader>();
					
					Iterator<String> keys = messaggio.getBodyMultipartInfo().getHeaders().keySet().iterator();
					while (keys.hasNext()) {
						String key = (String) keys.next();
						
						List<String> values = messaggio.getBodyMultipartInfo().getHeaders().get(key);
						if(values!=null && !values.isEmpty()) {
							for (String value : values) {
								if(value==null){
									value = ""; // puo' succedere in alcuni casi.
								}
								if(this.debug){
									this.log.debug("\t\t"+key+"="+value);
								}
								
								DumpMultipartHeader headerMultipart = new DumpMultipartHeader();
								headerMultipart.setNome(key);
								headerMultipart.setValore(value.toString());
								headerMultipart.setDumpTimestamp(gdo);
								
								if(headersCompact) {
									TransportUtils.addHeader(propertiesHdr, key, value);
									backupFailed.add(headerMultipart);
								}
								else {
									dumpMessaggio.addMultipartHeader(headerMultipart);
								}		
							}
						}
						
					}
					
					if(headersCompact) {
						PropertiesSerializator ps = new PropertiesSerializator(propertiesHdr);
						try{
							dumpMessaggio.setMultipartHeaderExt(ps.convertToDBColumnValue());
							backupFailed.clear();
							backupFailed = null;
						}catch(Throwable e){
							// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
							this.log.error("Errore durante la conversione degli header multipart: "+e.getMessage(),e);
							for (DumpMultipartHeader dumpMultipartHeader : backupFailed) {
								dumpMessaggio.addMultipartHeader(dumpMultipartHeader);
							}
						}
					}
				}
			}
			
			dumpMessaggio.setPostProcessed(1);
			
			
			
			// HEADER TRASPORTO
			if(messaggio.getHeaders()!=null && messaggio.getHeaders().size()>0){
				if(this.debug){
					this.log.debug("Dump "+messaggio.getHeaders().size()+" headers");
				}

				Map<String, List<String>> propertiesHdr = new HashMap<String, List<String>>();
				List<DumpHeaderTrasporto> backupFailed = new ArrayList<DumpHeaderTrasporto>();
				
				Iterator<String> keys = messaggio.getHeaders().keySet().iterator();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					
					List<String> values = messaggio.getHeaders().get(key);
					if(values!=null && !values.isEmpty()) {
						for (String value : values) {				
							if(value==null){
								value = ""; // puo' succedere in alcuni casi.
							}
							if(this.debug){
								this.log.debug("\t\t"+key+"="+value);
							}
		
							DumpHeaderTrasporto headerTrasporto = new DumpHeaderTrasporto();
							headerTrasporto.setNome(key);
							headerTrasporto.setValore(value.toString());
							headerTrasporto.setDumpTimestamp(gdo);
							
							if(headersCompact) {
								TransportUtils.addHeader(propertiesHdr,key, value);
								backupFailed.add(headerTrasporto);
							}
							else {
								dumpMessaggio.addHeaderTrasporto(headerTrasporto);
							}
						}
					}
				}
				
				if(headersCompact) {
					PropertiesSerializator ps = new PropertiesSerializator(propertiesHdr);
					try{
						dumpMessaggio.setHeaderExt(ps.convertToDBColumnValue());
						backupFailed.clear();
						backupFailed = null;
					}catch(Throwable e){
						// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
						this.log.error("Errore durante la conversione degli header: "+e.getMessage(),e);
						for (DumpHeaderTrasporto dumpHeader : backupFailed) {
							dumpMessaggio.addHeaderTrasporto(dumpHeader);
						}
					}
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

						Map<String, List<String>> propertiesHdr = new HashMap<String, List<String>>();
						List<DumpHeaderAllegato> backupFailed = new ArrayList<DumpHeaderAllegato>();
												
						Iterator<String> keys = attach.getHeaders().keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();
							
							List<String> values = attach.getHeaders().get(key);
							if(values!=null && !values.isEmpty()) {
								for (String value : values) {
									if(value==null){
										value = ""; // puo' succedere in alcuni casi.
									}
									if(this.debug){
										this.log.debug("\t\t"+key+"="+value);
									}
		
									DumpHeaderAllegato headerAllegato = new DumpHeaderAllegato();
									headerAllegato.setNome(key);
									headerAllegato.setValore(value.toString());
									headerAllegato.setDumpTimestamp(gdo);
									
									if(headersCompact) {
										TransportUtils.addHeader(propertiesHdr,key, value);
										backupFailed.add(headerAllegato);
									}
									else {
										dumpAllegato.addHeader(headerAllegato);
									}
								}
							}
						}
						
						if(headersCompact) {
							PropertiesSerializator ps = new PropertiesSerializator(propertiesHdr);
							try{
								dumpAllegato.setHeaderExt(ps.convertToDBColumnValue());
								backupFailed.clear();
								backupFailed = null;
							}catch(Throwable e){
								// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
								this.log.error("Errore durante la conversione degli header dell'allegato '"+attach.getContentId()+"': "+e.getMessage(),e);
								for (DumpHeaderAllegato dumpHeader : backupFailed) {
									dumpAllegato.addHeader(dumpHeader);
								}
							}
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
					
					String value = messaggio.getContenuti().get(key);
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
				this.log.debug("@@ log"+identificativoDump+" registrazione in corso ...");
			}
			
			dumpMessageService.create(dumpMessaggio);
			
			if(fDump!=null) {
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
				sqlQueryObject.addUpdateTable(CostantiDB.DUMP_MESSAGGI);
				sqlQueryObject.addUpdateField("body", "?");
				sqlQueryObject.addWhereCondition(CostantiDB.DUMP_MESSAGGI+".id=?");
				
				IJDBCAdapter jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDatabase);
				
				String sqlQueryUpdate =sqlQueryObject.createSQLUpdate();
				PreparedStatement pstmt = null;
				try {
					try(FileInputStream fin = new FileInputStream(fDump)){
						int index = 1;
						pstmt = con.prepareStatement(sqlQueryUpdate);
						jdbcAdapter.setBinaryData(pstmt, index++, fin, true);
						pstmt.setLong(index++, dumpMessaggio.getId());
						pstmt.execute();
					}
				}finally {
					try {
						if(pstmt!=null) {
							pstmt.close();
						}
					}catch(Exception eClose) {}
				}
			}
			
			if(this.debug){
				this.log.debug("@@ log"+identificativoDump+" registrazione completata");
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
