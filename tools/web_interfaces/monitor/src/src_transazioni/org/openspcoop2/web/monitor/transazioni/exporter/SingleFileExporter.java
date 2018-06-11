package org.openspcoop2.web.monitor.transazioni.exporter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.DumpAllegato;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpHeaderTrasporto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExport;
import org.openspcoop2.core.transazioni.constants.DeleteState;
import org.openspcoop2.core.transazioni.constants.ExportState;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniExportService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;

public class SingleFileExporter implements IExporter{

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();
	
	private boolean exportTracce = false;
	private boolean exportDiagnostici = false;
	private boolean exportContenuti = false;
	private boolean enableHeaderInfo = false;
	private boolean mimeThrowExceptionIfNotFound = false;
	private boolean abilitaMarcamentoTemporale = false;
	
	private ITransazioniService transazioniService;
	private ITracciaDriver tracciamentoService;
	private IDiagnosticDriver diagnosticiService;
	private ITransazioniExportService transazioniExporterService;
	
	
	private ZipOutputStream zip;
	private String fileName=null;
	
	private SingleFileExporter(ExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) {
		this.enableHeaderInfo = properties.isEnableHeaderInfo();
		
		this.exportTracce = properties.isExportTracce();
		this.exportDiagnostici = properties.isExportDiagnostici();
		this.exportContenuti = properties.isExportContenuti();
		this.mimeThrowExceptionIfNotFound = properties.isMimeThrowExceptionIfNotFound();
		this.abilitaMarcamentoTemporale = properties.isAbilitaMarcamentoTemporaleEsportazione();
		
		this.tracciamentoService = tracciamentoService;
		this.transazioniService = transazioniService;
		this.diagnosticiService = diagnosticiService;
		this.transazioniExporterService = transazioniExport;
		
		SingleFileExporter.log.info("Single File Exporter inizializzato:");
		SingleFileExporter.log.info("\t -esportazione Tracce      abilitata: "+this.exportTracce);
		SingleFileExporter.log.info("\t -esportazione Contenuti   abilitata: "+this.exportContenuti);
		SingleFileExporter.log.info("\t -esportazione Diagnostici abilitata: "+this.exportDiagnostici);
		
		SingleFileExporter.log.info("\t -MimeType handling (mime.throwExceptionIfMappingNotFound):"+this.mimeThrowExceptionIfNotFound);
	}
	
	public SingleFileExporter(OutputStream outstream,ExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) throws Exception{
		
		this(properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);

		this.zip = new ZipOutputStream(outstream);
		
	}

	public SingleFileExporter(File destFile,ExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService,ITransazioniExportService transazioniExport) throws Exception{
		this(properties,transazioniService,tracciamentoService,diagnosticiService,transazioniExport);
		
		FileOutputStream fos = new FileOutputStream(destFile);
		
		this.zip = new ZipOutputStream(fos);
		this.fileName = destFile.getName();
		SingleFileExporter.log.info("\n\t -Esportazione su file:"+destFile.getAbsolutePath());
		
	}

	public SingleFileExporter(String pathToFile,ExporterProperties properties, ITransazioniService transazioniService, ITracciaDriver tracciamentoService,IDiagnosticDriver diagnosticiService, ITransazioniExportService transazioniExport) throws Exception{
		this(new File(pathToFile), properties, transazioniService, tracciamentoService, diagnosticiService,transazioniExport);
	}
	
	private void export(String rootDir, List<TransazioneBean> transazioni) throws ExportException{
		
		byte[] buf = new byte[1024];
		
		InputStream in = null;
		
		for(TransazioneBean t: transazioni){
			
			String transazioneDir = rootDir+t.getIdTransazione()+File.separatorChar;
			
			//manifest
			if(this.enableHeaderInfo){
				//scrivo il manifest
				try{
					this.zip.putNextEntry(new ZipEntry(transazioneDir+"manifest.xml"));
					UtilityTransazioni.writeManifestTransazione(t,this.zip);
					this.zip.flush();
					this.zip.closeEntry();
				} catch (Exception e) {
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file manifest.xml ("+e.getMessage()+")";
					SingleFileExporter.log.error(msg,e);
					throw new ExportException(msg, e);
				}
				
			}
			
			//tracce
			if(this.exportTracce){
				//devo impostare solo l'idtransazione
				//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
				Hashtable<String, String> properties = new Hashtable<String, String>();
				properties.put("id_transazione", t.getIdTransazione());

				Traccia tracciaRichiesta = null;
				Traccia tracciaRisposta  = null;
				ArrayList<Traccia> tracce = new ArrayList<Traccia>();
				try{
					tracciaRichiesta=this.tracciamentoService.getTraccia(RuoloMessaggio.RICHIESTA,properties);
					tracce.add(tracciaRichiesta);
				}catch(DriverTracciamentoException e){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare la traccia di richiesta ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}catch(DriverTracciamentoNotFoundException e){
					//ignore
				}
				try{
					tracciaRisposta = this.tracciamentoService.getTraccia(RuoloMessaggio.RISPOSTA,properties);
					tracce.add(tracciaRisposta);
				}catch(DriverTracciamentoException e){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a recuperare la traccia di risposta ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}catch(DriverTracciamentoNotFoundException e){
					//ignore
				}
				
				// gestione degli errori.
				ArrayList<String> errori = new ArrayList<String>(0);
				
				if(tracce.size()>0){
					// Add ZIP entry to output stream.
					try{
						this.zip.putNextEntry(new ZipEntry(transazioneDir+"tracce.xml"));
						
						String tail = null;			
						for (int j = 0; j < tracce.size(); j++) {
							Traccia tr = tracce.get(j);
							String newLine = j > 0 ? "\n\n" : "";
							
							IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
							ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();
							
							try {
								if(j==0){
									XMLRootElement xmlRootElement = tracciaBuilder.getXMLRootElement();
									if(xmlRootElement!=null){
										String head = xmlRootElement.getAsStringStartTag();
										if(head!=null && !"".equals(head)){
											head = head +"\n\n";
											this.zip.write(head.getBytes(), 0, head.length());
											tail = xmlRootElement.getAsStringEndTag();
			    							if(tail!=null && !"".equals(tail)){
			    								tail = "\n\n" + tail;
			    							}
										}
									}
								}
								
								String traccia = tracciaBuilder.toString(tr,TipoSerializzazione.DEFAULT);
							
								in = new ByteArrayInputStream((newLine + traccia).getBytes());

								// Transfer bytes from the input stream to the ZIP file
								int len;
								while ((len = in.read(buf)) > 0) {
									this.zip.write(buf, 0, len);
								}
							} catch (ProtocolException e) {
								String idTransazione = t.getIdTransazione();
								String tipoTraccia = tr.getTipoMessaggio().getTipo();
								String bustaAsString = tr.getBustaAsString();
								String messaggioErrore = e.getMessage();

								StringBuilder sb = new StringBuilder(0);
								sb.append("ID Transazione: ").append(idTransazione)
										 .append("\n");
								sb.append("Tipo Traccia: ").append(tipoTraccia)
										 .append("\n");
								sb.append("Busta: \n").append(bustaAsString)
										.append("\n\n");
								sb.append("Errore: \n").append(messaggioErrore)
										.append("\n");
								
								errori.add(sb.toString());

							}
						}
						if(tail!=null && !"".equals(tail)){
							this.zip.write(tail.getBytes(), 0, tail.length());
						}

						// Complete the entry
						this.zip.flush();
						this.zip.closeEntry();
						if(in!=null)
							in.close();
						
						
						// se si sono riscontrati degli errori nella produzione delle tracce
						// creo un file che contiene la descrizione di tali errori.
						if (errori.size() > 0) {
							// Add ZIP entry to output stream.
							this.zip.putNextEntry(new ZipEntry(transazioneDir+"tracce.xml.error"));

							for (int i = 0; i < errori.size(); i++) {
								String errore = errori.get(i);

								String newLine = i > 0 ? "\n\n" : "";

								in = new ByteArrayInputStream((newLine + errore).getBytes());

								// Transfer bytes from the input stream to the ZIP file
								int len;
								while ((len = in.read(buf)) > 0) {
									this.zip.write(buf, 0, len);
								}
							}

							// Complete the entry
							this.zip.closeEntry();
							this.zip.flush();
							if (in != null) {
								in.close();
							}
						}
					}catch(Exception e){
						String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file tracce.xml ("+e.getMessage()+")";
						throw new ExportException(msg, e);
					}
					
					
					
				}
			}
			
			//diagnostici
			if(this.exportDiagnostici){
				try{
					FiltroRicercaDiagnosticiConPaginazione filter = new FiltroRicercaDiagnosticiConPaginazione();
					
					
					//devo impostare solo l'idtransazione
					//filter.setIdEgov(this.diagnosticiBean.getIdEgov());	
					Hashtable<String, String> properties = new Hashtable<String, String>();
					properties.put("id_transazione", t.getIdTransazione());
					filter.setProperties(properties);
					
					//non necessario, id_transazione e' sufficiente
					//filter.setIdentificativoPorta(search.getIdentificativoPorta());
					
					
					List<MsgDiagnostico> list = this.diagnosticiService.getMessaggiDiagnostici(filter);
					// Add ZIP entry to output stream.
					this.zip.putNextEntry(new ZipEntry(transazioneDir+"diagnostici.xml"));
					
					String tail = null;
					for (int j = 0; j < list.size(); j++) {
						MsgDiagnostico msg = list.get(j);
						String newLine = j > 0 ? "\n\n" : "";
						
						IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(msg.getProtocollo());
						IDiagnosticSerializer diagnosticoBuilder = pf.createDiagnosticSerializer();
						
						if(j==0){
							XMLRootElement xmlRootElement = diagnosticoBuilder.getXMLRootElement();
							if(xmlRootElement!=null){
								String head = xmlRootElement.getAsStringStartTag();
								if(head!=null && !"".equals(head)){
									head = head +"\n\n";
									this.zip.write(head.getBytes(), 0, head.length());
									tail = xmlRootElement.getAsStringEndTag();
	    							if(tail!=null && !"".equals(tail)){
	    								tail = "\n\n" + tail;
	    							}
								}
							}
						}
						
						String msgDiagnostico = diagnosticoBuilder.toString(msg,TipoSerializzazione.DEFAULT);
						in = new ByteArrayInputStream((newLine + msgDiagnostico).getBytes());
						// Transfer bytes from the input stream to the ZIP file
						int len;
						while ((len = in.read(buf)) > 0) {
							this.zip.write(buf, 0, len);
						}

					}
					if(tail!=null && !"".equals(tail)){
						this.zip.write(tail.getBytes(), 0, tail.length());
					}
					
					// Complete the entry
					this.zip.flush();
					this.zip.closeEntry();
					if(in!=null)
						in.close();
				}catch(Exception e){
					String msg = "Si e' verificato un errore durante l'esportazione della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file diagnostici.xml ("+e.getMessage()+")";
					throw new ExportException(msg, e);
				}
			}
			//contenuti e fault
			if(this.exportContenuti){
				
				//fault - integrazione
				String fault = t.getFaultIntegrazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						this.zip.putNextEntry(new ZipEntry(transazioneDir+"faultIntegrazione.xml"));
						this.zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						this.zip.flush();
						this.zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultIntegrazione.xml ("+ioe.getMessage()+")";
						SingleFileExporter.log.error(msg,ioe);
						throw new ExportException(msg, ioe);
					}				
				}
				
				//fault - cooperazione
				fault = t.getFaultCooperazione();
				if(StringUtils.isNotBlank(fault)){
					try{
						this.zip.putNextEntry(new ZipEntry(transazioneDir+"faultCooperazione.xml"));
						this.zip.write((fault != null ? fault.getBytes() : "".getBytes()));
						this.zip.flush();
						this.zip.closeEntry();
					}catch(Exception ioe){
						String msg = "Si e' verificato un errore durante l'esportazione dei contenuti della transazione con id:"+t.getIdTransazione();
						msg+=" Non sono riuscito a creare il file faultCooperazione.xml ("+ioe.getMessage()+")";
						SingleFileExporter.log.error(msg,ioe);
						throw new ExportException(msg, ioe);
					}				
				}
				
				//contenuti
				
				exportContenuti(t, this.zip, transazioneDir, this.transazioniService, TipoMessaggio.RICHIESTA_INGRESSO);
				exportContenuti(t, this.zip, transazioneDir, this.transazioniService, TipoMessaggio.RICHIESTA_USCITA);
				exportContenuti(t, this.zip, transazioneDir, this.transazioniService, TipoMessaggio.RISPOSTA_INGRESSO);
				exportContenuti(t, this.zip, transazioneDir, this.transazioniService, TipoMessaggio.RISPOSTA_USCITA);
			}

		}//chiudo for transazioni
	}
	
	public void export(List<String> idtransazioni) throws ExportException{
		Date startTime = Calendar.getInstance().getTime();
		TransazioneExport te = null;
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
//			int start = 0;
//			int limit = 100;
			
			
			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SingleFileExporter.log.debug("Avvio esportazione ...");
			SingleFileExporter.log.debug("Inizio esportazione alle:"+time.format(startTime));
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();
			
			for (String id : idtransazioni) {
				transazioni.add(this.transazioniService.findByIdTransazione(id));
			}
			
			String rootDir = "TransazioniPdD"+File.separatorChar;

			//search filter
			try{
				this.zip.putNextEntry(new ZipEntry(rootDir+"SearchFilter.xml"));
				UtilityTransazioni.writeSearchFilterXml(this.transazioniService.getSearch(),idtransazioni,this.transazioniService.totalCount(),this.zip);
				this.zip.flush();
				this.zip.closeEntry();
			}catch(Exception ioe){
				String msg = "Si e' verificato un errore durante l'esportazione delle transazioni";
				msg+=" Non sono riuscito a creare il file SearchFilter.xml ("+ioe.getMessage()+")";
				SingleFileExporter.log.error(msg,ioe);
				throw new ExportException(msg, ioe);
			}
			
			
			if(this.abilitaMarcamentoTemporale){
				te = this.transazioniExporterService.getByIntervallo(this.transazioniService.getSearch().getDataInizio(), this.transazioniService.getSearch().getDataFine());
				te.setExportState(ExportState.EXECUTING);
				te.setExportTimeStart(startTime);
				te.setDeleteState(DeleteState.UNDEFINED);
				
				if(this.fileName!=null)
					te.setNome(this.fileName);
				
				this.transazioniExporterService.store(te);
			}
			
			export(rootDir,transazioni);

			Date dataFine = Calendar.getInstance().getTime();
			
			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}
			
			this.zip.flush();
			this.zip.close();
		
			SingleFileExporter.log.debug("Fine esportazione alle:"+formatter.format(Calendar.getInstance().getTime()));
			SingleFileExporter.log.debug("Esportazione completata.");
			
		}catch(ExportException e){
			SingleFileExporter.log.error("Errore durante esportazione su file",e);
			
			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());
					
					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					SingleFileExporter.log.error("Errore durante il marcamento temporale.",ex);
				}
			}
			
			throw e;
		}catch(Exception e){
			SingleFileExporter.log.error("Errore durante esportazione su file",e);
			throw new ExportException("Errore durante esportazione su file", e);
		}
	}
	
	/**
	 * Effettua l'esportazione utilizzando il searchform presente nel {@link ITransazioniService} transazioniService
	 * @throws Exception
	 */
	public void export() throws ExportException{
		
		Date startTime = Calendar.getInstance().getTime();
		TransazioneExport te = null;
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
			int start = 0;
			int limit = 100;
			
			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SingleFileExporter.log.debug("Avvio esportazione ...");
			SingleFileExporter.log.debug("Inizio esportazione alle:"+time.format(startTime));
			
			if(this.abilitaMarcamentoTemporale){
				te = this.transazioniExporterService.getByIntervallo(this.transazioniService.getSearch().getDataInizio(), this.transazioniService.getSearch().getDataFine());
				te.setExportState(ExportState.EXECUTING);
				te.setExportTimeStart(startTime);
				te.setDeleteState(DeleteState.UNDEFINED);
				
				if(this.fileName!=null)
					te.setNome(this.fileName);
				
				this.transazioniExporterService.store(te);
			}
			
			List<TransazioneBean> transazioni = new ArrayList<TransazioneBean>();
			
			transazioni = this.transazioniService.findAll(start, limit);
						
			//creo sempre il file anche se non ci sono transazioni
			//il file conterra' solo il SearchFilter.xml
//			if(transazioni.size()>0){
				//int i = 0;// progressivo per evitare entry duplicate nel file zip
				// Create a buffer for reading the files
				String rootDir = "TransazioniPdD"+File.separatorChar;
				
				//search filter
				try{
					this.zip.putNextEntry(new ZipEntry(rootDir+"SearchFilter.xml"));
					 UtilityTransazioni.writeSearchFilterXml(this.transazioniService.getSearch(),this.zip);
					this.zip.flush();
					this.zip.closeEntry();

					while(transazioni.size()>0){

						export(rootDir,transazioni);

						start+=limit;

						transazioni = this.transazioniService.findAll(start, limit);
					}

					this.zip.flush();
					this.zip.close();
				}catch(IOException ioe){
					String msg = "Si e' verificato un errore durante l'esportazione delle transazioni";
					msg+=" Non sono riuscito a creare il file SearchFilter.xml ("+ioe.getMessage()+")";
					SingleFileExporter.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
//			}
				
			Date dataFine = Calendar.getInstance().getTime();
			
			if(this.abilitaMarcamentoTemporale){
				te.setExportState(ExportState.COMPLETED);
				te.setExportTimeEnd(dataFine);
				this.transazioniExporterService.store(te);
			}
			
			SingleFileExporter.log.debug("Fine esportazione alle:"+formatter.format(dataFine));
			SingleFileExporter.log.debug("Esportazione completata.");
			
		}catch(ExportException e){
			SingleFileExporter.log.error("Errore durante esportazione su file",e);
			
			if(this.abilitaMarcamentoTemporale){
				try{
					te.setExportState(ExportState.ERROR);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					te.setExportError(sw.toString());
					
					this.transazioniExporterService.store(te);
				}catch(Exception ex){
					SingleFileExporter.log.error("Errore durante il marcamento temporale.",ex);
				}
			}
			
			throw e;
		}catch(Exception e){
			SingleFileExporter.log.error("Errore durante esportazione su file",e);
			throw new ExportException("Errore durante esportazione su file", e);
		}
	}
	
	private void exportContenuti(Transazione t, ZipOutputStream zip,String dirPath,ITransazioniService service,TipoMessaggio tipo) throws ExportException{

		String contenutiDir = dirPath+"contenuti"+File.separator;

		DumpMessaggio dump=null;
		try {
			dump = service.getDumpMessaggio(t.getIdTransazione(), tipo);
		} catch (Exception e) {
			String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
			msg+=" Non sono riuscito a recuperare il messaggio di dump ("+e.getMessage()+")";
			SingleFileExporter.log.error(msg,e);
			throw new ExportException(msg, e);
		}
		if(dump!=null){
			String dir = contenutiDir+tipo.toString().toLowerCase()+File.separator;

			//envelope
			if(dump.getBody()!=null){
				try{
					zip.putNextEntry(new ZipEntry(dir+"envelope.xml"));
					zip.write(dump.getBody());
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file envelope.xml ("+ioe.getMessage()+")";
					SingleFileExporter.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}
			//header trasporto
			List<DumpHeaderTrasporto> headers = service.getHeaderTrasporto(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(headers.size()>0){
				try{
					zip.putNextEntry(new ZipEntry(dir+"header.xml"));
					 UtilityTransazioni.writeHeadersTrasportoXml(headers,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file header.xml ("+ioe.getMessage()+")";
					SingleFileExporter.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}

			}
			//contenuti
			List<DumpContenuto> contenuti = service.getContenutiSpecifici(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(contenuti.size()>0){
				try{
					zip.putNextEntry(new ZipEntry(dir+"contenuti.xml"));
					
					UtilityTransazioni.writeContenutiXml(contenuti,zip);
					zip.flush();
					zip.closeEntry();
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Non sono riuscito a creare il file contenuti.xml ("+ioe.getMessage()+")";
					SingleFileExporter.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}


			}
			//allegati
			List<DumpAllegato> allegati = service.getAllegatiMessaggio(dump.getIdTransazione(), dump.getTipoMessaggio(), dump.getId());
			if(allegati.size()>0){
				try{
					for (int i = 0; i < allegati.size(); i++) {
						DumpAllegato allegato = allegati.get(i);
						String iEsimoAllegato=dir+"allegati"+File.separator+"allegato_"+(i+1)+File.separator;
						zip.putNextEntry(new ZipEntry(iEsimoAllegato+"manifest.xml"));
						
						// scrivo il contenuto dell'allegato
						
						UtilityTransazioni.writeManifestAllegatoXml(allegato, zip);
						zip.flush();
						zip.closeEntry();

						//salvo il file
						String fileName = "allegato";

						String ext = MimeTypeUtils.fileExtensionForMIMEType(allegato.getContentType());

						fileName+="."+ext;

						zip.putNextEntry(new ZipEntry(iEsimoAllegato+fileName));
						zip.write(allegato.getAllegato());	
						zip.flush();
						zip.closeEntry();
					}
				}catch(Exception ioe){
					String msg = "Si e' verificato un errore durante l'esportazione dei contenuti ("+tipo.toString()+") della transazione con id:"+t.getIdTransazione();
					msg+=" Errore durante la gestione degli allegati ("+ioe.getMessage()+")";
					SingleFileExporter.log.error(msg,ioe);
					throw new ExportException(msg, ioe);
				}
			}
		}

	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public boolean isAbilitaMarcamentoTemporale() {
		return this.abilitaMarcamentoTemporale;
	}
}
