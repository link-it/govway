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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.PdDBaseBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneApplicativoServerBean;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniApplicativoServerService;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;
import org.openspcoop2.web.monitor.transazioni.exporter.SingleFileExporter;
import org.slf4j.Logger;

/**
 * DettagliBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DettagliTransazioneApplicativoServerBean extends
PdDBaseBean<TransazioneApplicativoServerBean, Long, IService<TransazioneApplicativoServerBean, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	// idEgov in caso di transazioni indica l'id transazione
	private String idTransazione;
	private String idEgov;
	private String identificativoPorta;
	private boolean isRisposta;
	
	private String servizioApplicativoErogatore;
	
	private TransazioneApplicativoServerBean dettaglio;
	private transient ITransazioniApplicativoServerService transazioniSAService;
	private transient ITransazioniService transazioniService;

	private boolean visualizzaIdCluster = false;
	
	private DiagnosticiBean diagnosticiBean;
	
	private String selectedTab = null;
	private String protocollo = null;
	private Date dataUltimaConsegna = null;

	private static Boolean enableHeaderInfo = null;
	@SuppressWarnings("unused")
	private static boolean headersAsProperties = true;
	@SuppressWarnings("unused")
	private static boolean contenutiAsProperties = false;

	private transient IProtocolFactory<?> protocolFactory;
	
	private boolean showFault = false;
	private boolean showFaultUltimoErrore = false;

	private boolean visualizzaDataAccettazione = false;
	
	private Boolean hasDumpBinarioUltimaConsegnaRichiestaUscita = null;
	private Boolean hasDumpBinarioUltimaConsegnaRispostaIngresso = null;
	private Boolean hasDumpUltimaConsegnaRichiestaUscita = null;
	private Boolean hasDumpUltimaConsegnaRispostaIngresso = null;
	private Boolean hasHeaderTrasportoUltimaConsegnaRichiestaUscita = null;
	private Boolean hasHeaderTrasportoUltimaConsegnaRispostaIngresso = null;
	private Boolean hasHeaderTrasportoBinarioUltimaConsegnaRichiestaUscita = null;
	private Boolean hasHeaderTrasportoBinarioUltimaConsegnaRispostaIngresso = null;
	
	private Date dataConsegnaErogatore = null;
	private Date ultimaConsegna = null;
	
	private TipoMessaggio exportContenuto;
		
	public String getExportContenuto() {
		if(this.exportContenuto == null){
			return null;
		}else{
			return this.exportContenuto.toString();
		}
	}
	public void setExportContenuto(String exportContenuto) {
		if(exportContenuto != null )
			this.exportContenuto = (TipoMessaggio) TipoMessaggio.toEnumConstantFromString(exportContenuto);
	}


	static {
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DettagliTransazioneApplicativoServerBean.log);

			DettagliTransazioneApplicativoServerBean.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();
			DettagliTransazioneApplicativoServerBean.headersAsProperties = govwayMonitorProperties.isAttivoTransazioniExportHeaderAsProperties();
			DettagliTransazioneApplicativoServerBean.contenutiAsProperties = govwayMonitorProperties.isAttivoTransazioniExportContenutiAsProperties();
			
		} catch (Exception e) {
			DettagliTransazioneApplicativoServerBean.log
			.warn("Inizializzazione servlet fallita, setto enableHeaderInfo=false",
					e);
		}
	}


	public DettagliTransazioneApplicativoServerBean(){
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DettagliTransazioneApplicativoServerBean.log);
			List<String> govwayMonitorare = govwayMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(govwayMonitorare!=null && govwayMonitorare.size()>1);

			this.visualizzaDataAccettazione = govwayMonitorProperties.isAttivoTransazioniDataAccettazione();
		} catch (Exception e) {
			DettagliTransazioneApplicativoServerBean.log
			.warn("Inizializzazione driverTracciamento fallita.....",
					e);
		}
	}
	
	public void setTransazioniService(ITransazioniService transazioniService) {
		this.transazioniService = transazioniService;
	}

	public void setTransazioniApplicativoServerService(ITransazioniApplicativoServerService transazioniService) {
		this.transazioniSAService = transazioniService;
	}
	
	public Boolean getHasDumpUltimaConsegnaRichiestaUscita() {
		if(this.hasDumpUltimaConsegnaRichiestaUscita == null)
			this.hasDumpUltimaConsegnaRichiestaUscita  = this.getHasDump(TipoMessaggio.RICHIESTA_USCITA); 
		
		return this.hasDumpUltimaConsegnaRichiestaUscita;
	}
	
	public Boolean getHasDumpUltimaConsegnaRispostaIngresso() {
		if(this.hasDumpUltimaConsegnaRispostaIngresso == null)
			this.hasDumpUltimaConsegnaRispostaIngresso  = this.getHasDump(TipoMessaggio.RISPOSTA_INGRESSO); 
		
		return this.hasDumpUltimaConsegnaRispostaIngresso;
	}
	
 	public boolean getHasDumpBinarioUltimaConsegnaRichiestaUscita() {
		if(this.hasDumpBinarioUltimaConsegnaRichiestaUscita == null)
			this.hasDumpBinarioUltimaConsegnaRichiestaUscita  = this.getHasDump(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO); 
		
		return this.hasDumpBinarioUltimaConsegnaRichiestaUscita;
	}

	public boolean getHasDumpBinarioUltimaConsegnaRispostaIngresso() {
		if(this.hasDumpBinarioUltimaConsegnaRispostaIngresso == null)
			this.hasDumpBinarioUltimaConsegnaRispostaIngresso  = this.getHasDump(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO); 
		
		return this.hasDumpBinarioUltimaConsegnaRispostaIngresso;
	}
	
	public Boolean getHasHeaderTrasportoUltimaConsegnaRichiestaUscita() {
		if(this.hasHeaderTrasportoUltimaConsegnaRichiestaUscita == null)
			this.hasHeaderTrasportoUltimaConsegnaRichiestaUscita  = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_USCITA);  
		
		return this.hasHeaderTrasportoUltimaConsegnaRichiestaUscita;
	}
	
	public Boolean getHasHeaderTrasportoUltimaConsegnaRispostaIngresso() {
		if(this.hasHeaderTrasportoUltimaConsegnaRispostaIngresso == null)
			this.hasHeaderTrasportoUltimaConsegnaRispostaIngresso  = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_INGRESSO);  
		
		return this.hasHeaderTrasportoUltimaConsegnaRispostaIngresso;
	}
	
	public boolean getHasHeaderTrasportoBinarioUltimaConsegnaRichiestaUscita() {
		if(this.hasHeaderTrasportoBinarioUltimaConsegnaRichiestaUscita == null)
			this.hasHeaderTrasportoBinarioUltimaConsegnaRichiestaUscita = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioUltimaConsegnaRichiestaUscita;
	}

	public boolean getHasHeaderTrasportoBinarioUltimaConsegnaRispostaIngresso() {
		if(this.hasHeaderTrasportoBinarioUltimaConsegnaRispostaIngresso == null)
			this.hasHeaderTrasportoBinarioUltimaConsegnaRispostaIngresso = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioUltimaConsegnaRispostaIngresso;
	}
	
	private boolean getHasDump(TipoMessaggio tipo) {
		if(this.dettaglio.getNumeroTentativi() > 1) {
			return this.transazioniService.hasInfoDumpAvailable(this.idTransazione, this.dettaglio.getNomeServizioApplicativoErogatore(), this.getDataUltimaConsegna(), tipo);
		}
		
		return this.transazioniService.hasInfoDumpAvailable(this.idTransazione, this.dettaglio.getNomeServizioApplicativoErogatore(), null, tipo);
	}
	
	private boolean getHasHeaderTrasporto(TipoMessaggio tipo) {
		if(this.dettaglio.getNumeroTentativi() > 1) {
			return this.transazioniService.hasInfoHeaderTrasportoAvailable(this.idTransazione, this.dettaglio.getNomeServizioApplicativoErogatore(), this.getDataUltimaConsegna(), tipo);
		}
		
		return this.transazioniService.hasInfoHeaderTrasportoAvailable(this.idTransazione, this.dettaglio.getNomeServizioApplicativoErogatore(), null, tipo);
	}

	public Date getDataUltimaConsegna() {
		if(this.dataUltimaConsegna == null) {
			this.dataUltimaConsegna = this.transazioniService.getDataConsegnaErogatore(this.idTransazione,  this.dettaglio.getNomeServizioApplicativoErogatore(),  this.dettaglio.getDataAccettazioneRichiesta());
		}
		
		return this.dataUltimaConsegna;
	}
	public void setDataUltimaConsegna(Date dataUltimaConsegna) {
		this.dataUltimaConsegna = dataUltimaConsegna;
	}
	public String saveDiagnostici() {
		try {

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();
			
			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);
			
			sessione.setAttribute(CostantiExport.PARAMETER_ID_TRANSAZIONI_ORIGINALI, this.dettaglio.getIdTransazione());
			sessione.setAttribute(CostantiExport.PARAMETER_IS_ALL_ORIGINALE, false);

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/diagnosticiexporter?isAll=false&ids="
					+ this.dettaglio.getIdTransazione());

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			DettagliTransazioneApplicativoServerBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei diagnostici.");
		}

		return null;
	}

	public String saveTracce() {

		try {

			// devo impostare solo l'idtransazione
			// filter.setIdEgov(this.diagnosticiBean.getIdEgov());
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put("id_transazione", this.dettaglio.getIdTransazione());

			@SuppressWarnings("unused")
			Traccia tracciaRichiesta = null;
			@SuppressWarnings("unused")
			Traccia tracciaRisposta = null;
			ArrayList<Traccia> tracce = new ArrayList<Traccia>();
//			if (!this.isRisposta) {
//				try {
//					tracciaRichiesta = this.driver.getTraccia(RuoloMessaggio.RICHIESTA, properties);
//					tracce.add(tracciaRichiesta);
//				} catch (DriverTracciamentoException e) {
//					// ignore
//				} catch (DriverTracciamentoNotFoundException e) {
//					// ignore
//				} 
//			} else {
//				try {
//					tracciaRisposta = this.driver.getTraccia(RuoloMessaggio.RISPOSTA, properties);
//					tracce.add(tracciaRisposta);
//				} catch (DriverTracciamentoException e) {
//					// ignore
//				} catch (DriverTracciamentoNotFoundException e) {
//					// ignore
//				}
//			}

			if (tracce.size() == 0) {
				MessageUtils.addWarnMsg("Nessuna traccia trovata.");
				return null;
			}

			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Be sure to retrieve the absolute path to the file with the
			// required
			// method
			// filePath = pathToTheFile;

			// This is another important attribute for the header of the
			// response
			// Here fileName, is a String with the name that you will suggest as
			// a
			// name to save as
			// I use the same name as it is stored in the file system of the
			// server.

			String fileName = null;
			if (this.isRisposta)
				fileName = "TracciaRisposta.zip";
			else
				fileName = "TracciaRichiesta.zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
			
			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();

			// int i = 0;// progressivo per evitare entry duplicate nel file zip
			// Create a buffer for reading the files
			byte[] buf = new byte[1024];
			ZipOutputStream zip = new ZipOutputStream(
					response.getOutputStream());
			InputStream in = null;

			ArrayList<String> errori = new ArrayList<String>(0);

			if (tracce.size() > 0) {
				// Add ZIP entry to output stream.
				zip.putNextEntry(new ZipEntry(this.dettaglio.getIdTransazione()
						+ " (" + tracce.size() + " entries)" + ".xml"));
				if (DettagliTransazioneApplicativoServerBean.enableHeaderInfo) {
//					zip.write(UtilityTransazioni.getHeaderTransazione(
//							this.dettaglio).getBytes());
				}
				for (int j = 0; j < tracce.size(); j++) {
					Traccia tr = tracce.get(j);
					String newLine = j > 0 ? "\n\n" : "";

					IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tr.getProtocollo());
					ITracciaSerializer tracciaBuilder = pf.createTracciaSerializer();

					try {
						String traccia = tracciaBuilder.toString(tr,TipoSerializzazione.DEFAULT);

						in = new ByteArrayInputStream(
								(newLine + traccia).getBytes());

						// Transfer bytes from the input stream to the ZIP file
						int len;
						while ((len = in.read(buf)) > 0) {
							zip.write(buf, 0, len);
						}
					} catch (ProtocolException e) {
						String idTransazione = this.dettaglio.getIdTransazione();
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

				// Complete the entry
				zip.closeEntry();
				zip.flush();
				if (in != null) {
					in.close();
				}
			}

			// se si sono riscontrati degli errori nella produzione delle tracce
			// creo un file che contiene la descrizione di tali errori.
			if (errori.size() > 0) {
				// Add ZIP entry to output stream.
				zip.putNextEntry(new ZipEntry(this.dettaglio.getIdTransazione()
						+ " (" + errori.size() + " entries)" + ".xml.error"));

				for (int i = 0; i < errori.size(); i++) {
					String errore = errori.get(i);

					String newLine = i > 0 ? "\n\n" : "";

					in = new ByteArrayInputStream((newLine + errore).getBytes());

					// Transfer bytes from the input stream to the ZIP file
					int len;
					while ((len = in.read(buf)) > 0) {
						zip.write(buf, 0, len);
					}
				}

				// Complete the entry
				zip.closeEntry();
				zip.flush();
				if (in != null) {
					in.close();
				}
			}

			zip.flush();
			zip.close();

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			DettagliTransazioneApplicativoServerBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione delle tracce.");
		}

		return null;
	}

	public void showOriginaleSelected(ActionEvent ae) {
		this.dettaglio = null;
	}

	public void showDuplicatoSelected(ActionEvent ae) {
		this.dettaglio = null;
	}

	public TransazioneApplicativoServerBean getDettaglio() {
		if (this.dettaglio != null)
			return this.dettaglio;
		try {

			this.transazioniSAService.setIdTransazione(this.idTransazione);
			this.transazioniSAService.setProtocollo(this.protocollo);
			this.dettaglio = this.transazioniSAService.findByServizioApplicativoErogatore(this.servizioApplicativoErogatore);

			if (this.dettaglio != null && this.protocolFactory == null) { // && this.getTraccia() != null) {
				try {
					this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.getProtocollo());
				} catch (ProtocolException e) {
					DettagliTransazioneApplicativoServerBean.log.error("Errore durante la creazione della Factory", e);
				}
			}
		} catch (Exception e) {
			DettagliTransazioneApplicativoServerBean.log.error(e.getMessage(), e);
		}

		return this.dettaglio;
	}

	public void visualizzaRichiestaListener(ActionEvent ae) {
		this.dettaglio = null;
	}

	public void visualizzaRispostaListener(ActionEvent ae) {
		this.dettaglio = null;
	}

	public String getIdEgov() {
		return this.idEgov;
	}

	public void setIdEgov(String idEgov) {
		this.idEgov = idEgov;
	}

	public boolean getIsRisposta() {
		return this.isRisposta;
	}

	public void setIsRisposta(boolean isRisposta) {
		this.isRisposta = isRisposta;
	}

	public String getIdentificativoPorta() {
		return this.identificativoPorta;
	}

	public void setIdentificativoPorta(String identificativoPorta) {
		this.identificativoPorta = identificativoPorta;
	}

	public String getIdTransazione() {
		return this.idTransazione;
	}

	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}

	public boolean getShowFault() {
		return this.showFault;
	}

	public void setShowFault(boolean showFault) {
		this.showFault = showFault;
		this.showFaultUltimoErrore= false;
	}

	public boolean getShowFaultUltimoErrore() {
		return this.showFaultUltimoErrore;
	}

	public void setShowFaultUltimoErrore(boolean showFaultUltimoErrore) {
		
		this.showFaultUltimoErrore = showFaultUltimoErrore;
		this.showFault = false;
	}
	
	public void setDettaglio(TransazioneApplicativoServerBean dettaglio) {
		this.dettaglio = dettaglio;

		if (this.dettaglio != null && this.protocolFactory == null) {
			try {
				this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.dettaglio.getProtocollo());
			} catch (ProtocolException e) {
				DettagliTransazioneApplicativoServerBean.log.error("Errore durante la creazione della Factory", e);
			}
		}
	}

	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	public boolean isVisualizzaDataAccettazione() {
		return this.visualizzaDataAccettazione;
	}

	public void setVisualizzaDataAccettazione(boolean visualizzaDataAccettazione) {
		this.visualizzaDataAccettazione = visualizzaDataAccettazione;
	}
	
	public String downloadTokenInfo(){
		DettagliTransazioneApplicativoServerBean.log.debug("downloading TokenInfo: "+this.dettaglio.getId());
		try{
			//recupero informazioni sul file


			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

			// Now we create some variables we will use for writting the file to the
			// response
			// String filePath = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			// Be sure to retrieve the absolute path to the file with the required
			// method
			// filePath = pathToTheFile;

			// Now set the content type for our response, be sure to use the best
			// suitable content type depending on your file
			// the content type presented here is ok for, lets say, text files and
			// others (like CSVs, PDFs)
			String contentType = "application/json";
			response.setContentType(contentType);

			// This is another important attribute for the header of the response
			// Here fileName, is a String with the name that you will suggest as a
			// name to save as
			// I use the same name as it is stored in the file system of the server.
			//String fileName = "allegato_"+this.selectedAttachment.getId();
			// NOTA: L'id potrebbe essere -1 nel caso di mascheramento logico.
			String fileName = "tokenInfo";

			String ext = MimeTypeUtils.fileExtensionForMIMEType(contentType);

			fileName+="."+ext;

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName, contentType);

			// Streams we will use to read, write the file bytes to our response
			ByteArrayInputStream bis = null;
			OutputStream os = null;

			// First we load the file in our InputStream 
			
			String toRet = ""; //this.dettaglio.getTokenInfo();
			@SuppressWarnings("unused")
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
//			try {
//				toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dettaglio.getTokenInfo()));
//			} catch (UtilsException e) {
//			}
			
			byte[] contenutoBody = toRet.getBytes();
			//			if(this.base64Decode){
			//				contenutoBody = ((DumpAllegatoBean)this.dumpMessaggio).decodeBase64();
			//			}
			bis = new ByteArrayInputStream(contenutoBody);
			os = response.getOutputStream();

			// While there are still bytes in the file, read them and write them to
			// our OutputStream
			while ((read = bis.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

			// Clean resources
			os.flush();
			os.close();

			FacesContext.getCurrentInstance().responseComplete();

			// End of the method
		}catch (Exception e) {
			DettagliTransazioneApplicativoServerBean.log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg("Si e' verificato un errore durante il download del token info");
		}
		return null;
	}
	
	
	public String exportContenuti() {

		try {
			
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Be sure to retrieve the absolute path to the file with the
			// required
			// method
			// filePath = pathToTheFile;

			// This is another important attribute for the header of the
			// response
			// Here fileName, is a String with the name that you will suggest as
			// a
			// name to save as
			// I use the same name as it is stored in the file system of the
			// server.

			@SuppressWarnings("unused")
			String dirPath = null; // per non far produrre la directory contenuti
			String fileName = this.exportContenuto.name().toLowerCase();
						
			if (this.isRisposta)
				fileName = fileName+".zip";
			else
				fileName = fileName+".zip";

			// Setto Proprietà Export File
			HttpUtilities.setOutputFile(response, true, fileName);
			
			// committing status and headers
			response.setStatus(200);
			response.flushBuffer();


			ZipOutputStream zip = new ZipOutputStream(
					response.getOutputStream());
			
			Date dataConsegna = null;
			if(this.ultimaConsegna == null) {
				dataConsegna = this.dataConsegnaErogatore;
			}
			else {
				dataConsegna = this.ultimaConsegna;
			}
			
			SingleFileExporter.exportContenuti(log, this.dettaglio, dataConsegna, 
					zip, dirPath, this.transazioniService, this.exportContenuto,
					DettagliTransazioneApplicativoServerBean.headersAsProperties,DettagliTransazioneApplicativoServerBean.contenutiAsProperties);
			
			zip.flush();
			zip.close();

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			DettagliTransazioneApplicativoServerBean.log.error(e.getMessage(), e);
			MessageUtils
			.addErrorMsg("Si e' verificato un errore durante l'esportazione dei contenuti.");
		}

		return null;
	}
	
    public String getSelectedTab() {
        return this.selectedTab;
	}
	
	public void setSelectedTab(String selectedTab) {
	        this.selectedTab = selectedTab;
	}
	
	public DiagnosticiBean getDiagnosticiBean() {
//		if(this.diagnosticiBean == null) {
			this.diagnosticiBean  = new DiagnosticiBean();
			this.diagnosticiBean.setIdEgov(this.idEgov);
			this.diagnosticiBean.setIdentificativoPorta(this.identificativoPorta);
			this.diagnosticiBean.setIdTransazione(this.idTransazione);
			if(this.dettaglio != null) {
				this.diagnosticiBean.setProtocollo(this.dettaglio.getProtocollo()); 
				this.diagnosticiBean.setNomeServizioApplicativo(this.dettaglio.getNomeServizioApplicativoErogatore());
			}
			this.diagnosticiBean.setForceNomeServizioApplicativoNull(null);
//		}
		
		return this.diagnosticiBean;
	}
	public void setDiagnosticiBean(DiagnosticiBean diagnosticiBean) {
		this.diagnosticiBean = diagnosticiBean;
	}
	
	@Override
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	
	public boolean isVisualizzaIdCluster() {
		return this.visualizzaIdCluster;
	}

	public void setVisualizzaIdCluster(boolean visualizzaIdCluster) {
		this.visualizzaIdCluster = visualizzaIdCluster;
	}
	
	public boolean isVisualizzaTextAreaLocationConnettore () {
		if(StringUtils.isNotEmpty(this.dettaglio.getLocationConnettore())) {
			if(this.dettaglio.getLocationConnettore().length() > 150)
				return true;
		} 
		return false;
	}

	public void setVisualizzaTextAreaLocationConnettore(boolean visualizzaTextAreaLocationConnettore) {
	}
	
	public boolean isVisualizzaTextAreaLocationUltimoErrore () {
		if(StringUtils.isNotEmpty(this.dettaglio.getLocationUltimoErrore())) {
			if(this.dettaglio.getLocationUltimoErrore().length() > 150)
				return true;
		} 
		return false;
	}
	
	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	
	public boolean isVisualizzaTextAreaUltimoErrore () {
		if(StringUtils.isNotEmpty(this.dettaglio.getUltimoErrore())) {
			if(this.dettaglio.getUltimoErrore().length() > 150)
				return true;
		} 
		return false;
	}
	
	public boolean isVisualizzaUltimoErrore () {
		if(this.dettaglio.getUltimoErrore() != null && (
				this.dettaglio.getDataUltimoErrore().equals(this.dettaglio.getDataUltimaConsegna()))
		)
			return true;
		return false;
	}
	
	public boolean isVisualizzaTabUltimaConsegnaErrore () {
		if(this.dettaglio.isConsegnaTrasparente() && this.dettaglio.getNumeroTentativi() > 1 && this.dettaglio.getUltimoErrore() != null && 
				!this.dettaglio.getDataUltimoErrore().equals(this.dettaglio.getDataUscitaRichiesta()) &&
				!this.dettaglio.getDataUltimoErrore().equals(this.dettaglio.getDataAccettazioneRichiesta())
		)
			return true;
		return false;
	}
	
	public Date getDataConsegnaErogatore() {
		return this.dataConsegnaErogatore;
	}

	public void setDataConsegnaErogatore(Date dataConsegnaErogatore) {
		this.dataConsegnaErogatore = dataConsegnaErogatore;
	}
	
	public Date getUltimaConsegna() {
		return this.ultimaConsegna;
	}

	public void setUltimaConsegna(Date ultimaConsegna) {
		this.ultimaConsegna = ultimaConsegna;
	}
}
