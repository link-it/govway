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
package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.connettori.ConnettoreBase;
import org.openspcoop2.pdd.core.credenziali.engine.GestoreCredenzialiEngine;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.PdDBaseBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.core.utils.MimeTypeUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
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
public class DettagliBean extends
PdDBaseBean<Transazione, String, IService<TransazioneBean, Long>> {

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

	private transient ITracciaDriver driver;
	private transient ITransazioniService transazioniService;
	
	private boolean visualizzaIdCluster = false;
	
	private String selectedTab = null;
	private DiagnosticiBean diagnosticiBean;

	private TracciaBean tracciaRichiesta;
	private TracciaBean tracciaRisposta;
	private TransazioneBean dettaglio;
	private TransazioneBean originale;

	private static Boolean enableHeaderInfo = null;
	private static boolean headersAsProperties = true;
	private static boolean contenutiAsProperties = false;

	private transient IProtocolFactory<?> protocolFactory;
	
	private boolean showFaultCooperazione = false;
	private boolean showFaultIntegrazione = false;

	private boolean visualizzaDataAccettazione = false;
	private Boolean hasDumpRichiestaIngresso = null;
	private Boolean hasDumpRichiestaUscita = null;
	private Boolean hasDumpRispostaIngresso = null;
	private Boolean hasDumpRispostaUscita = null;
	private Boolean hasDumpBinarioRichiestaIngresso = null;
	private Boolean hasDumpBinarioRichiestaUscita = null;
	private Boolean hasDumpBinarioRispostaIngresso = null;
	private Boolean hasDumpBinarioRispostaUscita = null;
	private Boolean hasHeaderTrasportoRichiestaIngresso = null;
	private Boolean hasHeaderTrasportoRichiestaUscita = null;
	private Boolean hasHeaderTrasportoRispostaIngresso = null;
	private Boolean hasHeaderTrasportoRispostaUscita = null;
	private Boolean hasHeaderTrasportoBinarioRichiestaIngresso = null;
	private Boolean hasHeaderTrasportoBinarioRichiestaUscita = null;
	private Boolean hasHeaderTrasportoBinarioRispostaIngresso = null;
	private Boolean hasHeaderTrasportoBinarioRispostaUscita = null;
	
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

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DettagliBean.log);

			DettagliBean.enableHeaderInfo = govwayMonitorProperties.isAttivoTransazioniExportHeader();
			DettagliBean.headersAsProperties = govwayMonitorProperties.isAttivoTransazioniExportHeaderAsProperties();
			DettagliBean.contenutiAsProperties = govwayMonitorProperties.isAttivoTransazioniExportContenutiAsProperties();
			
		} catch (Exception e) {
			DettagliBean.log
			.error("Inizializzazione servlet fallita, setto enableHeaderInfo=false",
					e);
		}
	}


	public DettagliBean(){
		try {

			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(DettagliBean.log);
			List<String> govwayMonitorare = govwayMonitorProperties.getListaPdDMonitorate_StatusPdD();
			this.setVisualizzaIdCluster(govwayMonitorare!=null && govwayMonitorare.size()>1);

			this.driver  = govwayMonitorProperties.getDriverTracciamento();
			
			this.visualizzaDataAccettazione = govwayMonitorProperties.isAttivoTransazioniDataAccettazione();
			
			

		} catch (Exception e) {
			DettagliBean.log
			.error("Inizializzazione driverTracciamento fallita.....",
					e);
		}
	}

	public void setTransazioniService(ITransazioniService transazioniService) {
		this.transazioniService = transazioniService;
	}

	/**
	 * Ritorna la transazione originale della richiesta di questa (dettaglio),
	 * se questa e' una transazione duplicata
	 * 
	 * @return Transazione Originale Richiesta se questa e' duplicata
	 */
	public Transazione getTransazioneOriginaleRichiesta() {

		if (this.originale != null)
			return this.originale;

		if (this.dettaglio != null
				&& this.dettaglio.getDuplicatiRichiesta() == -1) {

			this.originale = this.transazioniService.findTransazioneOriginale(
					this.idTransazione,
					this.dettaglio.getIdMessaggioRichiesta(), false);
		}

		return this.originale;
	}

	public Transazione getTransazioneOriginaleRisposta() {

		if (this.originale != null)
			return this.originale;

		if (this.dettaglio != null
				&& this.dettaglio.getDuplicatiRisposta() == -1) {

			this.originale = this.transazioniService.findTransazioneOriginale(
					this.idTransazione,
					this.dettaglio.getIdMessaggioRisposta(), true);
		}

		return this.originale;
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
			DettagliBean.log.error(e.getMessage(), e);
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

			Traccia tracciaRichiesta = null;
			Traccia tracciaRisposta = null;
			ArrayList<Traccia> tracce = new ArrayList<Traccia>();
			if (!this.isRisposta) {
				try {
					tracciaRichiesta = this.driver.getTraccia(RuoloMessaggio.RICHIESTA, properties);
					tracce.add(tracciaRichiesta);
				} catch (DriverTracciamentoException e) {
					// ignore
				} catch (DriverTracciamentoNotFoundException e) {
					// ignore
				} 
			} else {
				try {
					tracciaRisposta = this.driver.getTraccia(RuoloMessaggio.RISPOSTA, properties);
					tracce.add(tracciaRisposta);
				} catch (DriverTracciamentoException e) {
					// ignore
				} catch (DriverTracciamentoNotFoundException e) {
					// ignore
				}
			}

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
				if (DettagliBean.enableHeaderInfo) {
					zip.write(UtilityTransazioni.getHeaderTransazione(
							this.dettaglio).getBytes());
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
			DettagliBean.log.error(e.getMessage(), e);
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

	public String getTipologiaConProtocollo() throws Exception{ 
		if(this.dettaglio==null){
			return "n.d.";
		}
		if(this.dettaglio.getPddRuolo()==null){
			return "n.d.";
		}
		String ruolo = null;
		switch (this.dettaglio.getPddRuolo()) {
		case DELEGATA:
			ruolo = "Fruizione";
			break;
		case APPLICATIVA:
			ruolo = "Erogazione";
			break;
		case INTEGRATION_MANAGER:
			ruolo = "MessageBox";
			break;
		case ROUTER:
			ruolo = "Router";
			break;
		}
		if(this.dettaglio.getProtocollo()!=null){
			return ruolo + " ("+this.dettaglio.getProtocolloLabel()+")";
		}
		else{
			return ruolo;
		}
	}
	
	public TransazioneBean getDettaglio() {
		if (this.dettaglio != null)
			return this.dettaglio;
		try {

			this.dettaglio = this.transazioniService
					.findByIdTransazione(this.idTransazione);

			if (this.dettaglio != null && this.protocolFactory == null && this.getTraccia() != null) {
				try {
					this.protocolFactory = ProtocolFactoryManager.getInstance()
							.getProtocolFactoryByName(
									this.getTraccia().getProtocollo());
				} catch (ProtocolException e) {
					DettagliBean.log.error("Errore durante la creazione della Factory", e);
				}
			}
		} catch (Exception e) {
			DettagliBean.log.error(e.getMessage(), e);
		}

		return this.dettaglio;
	}

	public boolean isExistsTracciaRichiesta(){
		if(this.getDettaglio()!=null && this.getDettaglio().getIdMessaggioRichiesta()!=null &&
				!"".equals(this.getDettaglio().getIdMessaggioRichiesta()) &&
				!PddRuolo.INTEGRATION_MANAGER.equals(this.getDettaglio().getPddRuolo())){
			this.isRisposta = false;
			return this.getTraccia()!=null;
		}
		return false;
	}
	
	public boolean isExistsTracciaRisposta(){
		if(this.getDettaglio()!=null && this.getDettaglio().getIdMessaggioRisposta()!=null &&
				!"".equals(this.getDettaglio().getIdMessaggioRisposta()) &&
				!PddRuolo.INTEGRATION_MANAGER.equals(this.getDettaglio().getPddRuolo())){
			this.isRisposta = true;
			return this.getTraccia()!=null;
		}
		return false;
	}
	
	public Traccia getTraccia() {
		
		if (!this.isRisposta) {
			if (this.tracciaRichiesta != null){
				return this.tracciaRichiesta;
			}
		}
		else{
			if (this.tracciaRisposta != null){
				return this.tracciaRisposta;
			}
		}
			
		try {
			Hashtable<String, String> properties = new Hashtable<String, String>();
			properties.put("id_transazione", this.idTransazione);
			boolean notNull = false;
			
			ServiceBinding tipoApi = null;
			if(this.getDettaglio()!=null) {
				if(TipoAPI.REST.getValoreAsInt() == this.getDettaglio().getTipoApi()) {
					tipoApi = ServiceBinding.REST;
				}
				else if(TipoAPI.SOAP.getValoreAsInt() == this.getDettaglio().getTipoApi()) {
					tipoApi = ServiceBinding.SOAP;
				}
			}
			
			if (!this.isRisposta) {
				this.tracciaRichiesta = new TracciaBean(this.driver.getTraccia(RuoloMessaggio.RICHIESTA, properties),tipoApi);
				notNull = this.tracciaRichiesta!=null;
			} else {
				this.tracciaRisposta = new TracciaBean(this.driver.getTraccia(RuoloMessaggio.RISPOSTA, properties),tipoApi);
				notNull = this.tracciaRisposta!=null;
			}

			if (notNull && this.protocolFactory == null) {
				try {
					this.protocolFactory = ProtocolFactoryManager.getInstance()
							.getProtocolFactoryByName(
									this.getTraccia().getProtocollo());
				} catch (ProtocolException e) {
					DettagliBean.log.error("Errore durante la creazione della Factory", e);
				}
			}
		} catch (DriverTracciamentoNotFoundException nfe) {
//			DettagliBean.log.debug(nfe.getMessage() + " IdTransazione ["+this.idTransazione+"]");
			// ignore
		} catch (Exception e) {
			DettagliBean.log.error(e.getMessage(), e);
		}

		if (!this.isRisposta) {
			return this.tracciaRichiesta;
		}
		else{
			return this.tracciaRisposta;
		}
	}
	
 	public boolean getHasDumpRichiestaIngresso() {
		if(this.hasDumpRichiestaIngresso == null)
			this.hasDumpRichiestaIngresso  = this.getHasDump(TipoMessaggio.RICHIESTA_INGRESSO); 
		
		return this.hasDumpRichiestaIngresso;
	}
 	
 	public boolean getHasDumpRichiestaUscita() {
		if(this.hasDumpRichiestaUscita == null)
			this.hasDumpRichiestaUscita  = this.getHasDump(TipoMessaggio.RICHIESTA_USCITA); 
		
		return this.hasDumpRichiestaUscita;
	}

	public boolean getHasDumpRispostaIngresso() {
		if(this.hasDumpRispostaIngresso == null)
			this.hasDumpRispostaIngresso  = this.getHasDump(TipoMessaggio.RISPOSTA_INGRESSO); 
		
		return this.hasDumpRispostaIngresso;
	}

	public boolean getHasDumpRispostaUscita() {
		if(this.hasDumpRispostaUscita == null)
			this.hasDumpRispostaUscita  = this.getHasDump(TipoMessaggio.RISPOSTA_USCITA);  
		
		return this.hasDumpRispostaUscita;
	}
	
	public boolean getHasDumpBinarioRichiestaIngresso() {
		if(this.hasDumpBinarioRichiestaIngresso == null)
			this.hasDumpBinarioRichiestaIngresso  = this.getHasDump(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO); 
		
		return this.hasDumpBinarioRichiestaIngresso;
	}
 	
 	public boolean getHasDumpBinarioRichiestaUscita() {
		if(this.hasDumpBinarioRichiestaUscita == null)
			this.hasDumpBinarioRichiestaUscita  = this.getHasDump(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO); 
		
		return this.hasDumpBinarioRichiestaUscita;
	}

	public boolean getHasDumpBinarioRispostaIngresso() {
		if(this.hasDumpBinarioRispostaIngresso == null)
			this.hasDumpBinarioRispostaIngresso  = this.getHasDump(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO); 
		
		return this.hasDumpBinarioRispostaIngresso;
	}

	public boolean getHasDumpBinarioRispostaUscita() {
		if(this.hasDumpBinarioRispostaUscita == null)
			this.hasDumpBinarioRispostaUscita  = this.getHasDump(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);  
		
		return this.hasDumpBinarioRispostaUscita;
	}

	private boolean getHasDump(TipoMessaggio tipo) {
		return this.transazioniService.hasInfoDumpAvailable(this.idTransazione, null, null, tipo);
	}

	public boolean getHasHeaderTrasportoRichiestaIngresso() {
		if(this.hasHeaderTrasportoRichiestaIngresso == null)
			this.hasHeaderTrasportoRichiestaIngresso = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_INGRESSO);  
		
		return this.hasHeaderTrasportoRichiestaIngresso;
	}
	
	public boolean getHasHeaderTrasportoRichiestaUscita() {
		if(this.hasHeaderTrasportoRichiestaUscita == null)
			this.hasHeaderTrasportoRichiestaUscita = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_USCITA);  
		
		return this.hasHeaderTrasportoRichiestaUscita;
	}

	public boolean getHasHeaderTrasportoRispostaIngresso() {
		if(this.hasHeaderTrasportoRispostaIngresso == null)
			this.hasHeaderTrasportoRispostaIngresso = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_INGRESSO);  
		
		return this.hasHeaderTrasportoRispostaIngresso;
	}

	public boolean getHasHeaderTrasportoRispostaUscita() {
		if(this.hasHeaderTrasportoRispostaUscita == null)
			this.hasHeaderTrasportoRispostaUscita = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_USCITA);  
		
		return this.hasHeaderTrasportoRispostaUscita;
	}
	
	public boolean getHasHeaderTrasportoBinarioRichiestaIngresso() {
		if(this.hasHeaderTrasportoBinarioRichiestaIngresso == null)
			this.hasHeaderTrasportoBinarioRichiestaIngresso = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioRichiestaIngresso;
	}
	
	public boolean getHasHeaderTrasportoBinarioRichiestaUscita() {
		if(this.hasHeaderTrasportoBinarioRichiestaUscita == null)
			this.hasHeaderTrasportoBinarioRichiestaUscita = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioRichiestaUscita;
	}

	public boolean getHasHeaderTrasportoBinarioRispostaIngresso() {
		if(this.hasHeaderTrasportoBinarioRispostaIngresso == null)
			this.hasHeaderTrasportoBinarioRispostaIngresso = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioRispostaIngresso;
	}

	public boolean getHasHeaderTrasportoBinarioRispostaUscita() {
		if(this.hasHeaderTrasportoBinarioRispostaUscita == null)
			this.hasHeaderTrasportoBinarioRispostaUscita = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO);  
		
		return this.hasHeaderTrasportoBinarioRispostaUscita;
	}

	private boolean getHasHeaderTrasporto(TipoMessaggio tipo) {
		return this.transazioniService.hasInfoHeaderTrasportoAvailable(this.idTransazione, null, null, tipo);
	}

	public void visualizzaRichiestaListener(ActionEvent ae) {
		this.dettaglio = null;
	}

	public void visualizzaRispostaListener(ActionEvent ae) {
		this.dettaglio = null;
	}

	public void tracciaListener(ActionEvent ae) {
		this.tracciaRichiesta = null;
		this.tracciaRisposta = null;
	}

	public void setTraccia(Traccia traccia) {
		
		ServiceBinding tipoApi = null;
		if(this.getDettaglio()!=null) {
			if(TipoAPI.REST.getValoreAsInt() == this.getDettaglio().getTipoApi()) {
				tipoApi = ServiceBinding.REST;
			}
			else if(TipoAPI.SOAP.getValoreAsInt() == this.getDettaglio().getTipoApi()) {
				tipoApi = ServiceBinding.SOAP;
			}
		}
		
		TracciaBean tr = new TracciaBean(traccia, tipoApi);
		if (!this.isRisposta) {
			this.tracciaRichiesta = tr;
		}
		else{
			this.tracciaRisposta = tr;
		}

		if (tr != null && this.protocolFactory == null) {
			try {
				this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.getTraccia().getProtocollo());
			} catch (ProtocolException e) {
				DettagliBean.log.error("Errore durante la creazione della Factory", e);
			}
		}

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

	public boolean getShowFaultCooperazione() {
		return this.showFaultCooperazione;
	}

	public void setShowFaultCooperazione(boolean showFaultCooperazione) {
		this.showFaultCooperazione = showFaultCooperazione;
		this.showFaultIntegrazione = false;
	}

	public boolean getShowFaultIntegrazione() {
		return this.showFaultIntegrazione;
	}

	public void setShowFaultIntegrazione(boolean showFaultIntegrazione) {
		
		this.showFaultIntegrazione = showFaultIntegrazione;
		this.showFaultCooperazione = false;
	}
	
	public void setDettaglio(TransazioneBean dettaglio) {
		this.dettaglio = dettaglio;

		if (this.dettaglio != null && this.protocolFactory == null) {
			try {
				this.protocolFactory = ProtocolFactoryManager.getInstance()
						.getProtocolFactoryByName(
								this.getTraccia().getProtocollo());
			} catch (ProtocolException e) {
				DettagliBean.log.error("Errore durante la creazione della Factory", e);
			}
		}
	}

	public String getPrettyEnvelop() {
		String toRet = null;
		
		TracciaBean tr = null;
		if (!this.isRisposta) {
			tr = this.tracciaRichiesta;
		}
		else{
			tr = this.tracciaRisposta;
		}
		
		if(tr!=null && tr.getBustaAsString()!=null)
			toRet = Utils.prettifyXml(tr.getBustaAsString());
		
		if(toRet == null)
			toRet = tr.getBustaAsString() != null ? tr.getBustaAsString() : "";
			
		return toRet;
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
	
	public String getTextCredenziali() {
		if(StringUtils.isNotEmpty(this.dettaglio.getCredenziali())) {
			String cr = this.dettaglio.getCredenziali();
			if(GestoreCredenzialiEngine.containsPrefixGatewayCredenziali(cr)) {
				return GestoreCredenzialiEngine.erasePrefixGatewayCredenziali(cr);
			}
			else {
				return cr;
			}
		}
		return null;
	}
	
	public boolean isVisualizzaTextAreaCredenziali() {
		if(StringUtils.isNotEmpty(this.dettaglio.getCredenziali())) {
			if(this.dettaglio.getCredenziali().length() > 150) {
				return true;
			}
			else if(this.dettaglio.getCredenziali().contains("\n")) {
				return true;
			}
		} 
		return false;
	}

	public void setVisualizzaTextAreaCredenziali(boolean visualizzaTextAreaCredenziali) {
	}
	
	
	public boolean isVisualizzaTextAreaUrlInvocazione () {
		if(StringUtils.isNotEmpty(this.dettaglio.getUrlInvocazione())) {
			if(this.dettaglio.getUrlInvocazione().length() > 150)
				return true;
		} 
		return false;
	}

	public void setVisualizzaTextAreaUrlInvocazione(boolean visualizzaTextAreaUrlInvocazione) {
	}
	
	public boolean isVisualizzaTextAreaTrasportoMittente() {
		if(StringUtils.isNotEmpty(this.dettaglio.getTrasportoMittenteLabel())) {
			if(this.dettaglio.getTrasportoMittenteLabel().length() > 150)
				return true;
		} 
		return false;
	}

	public void setVisualizzaTextAreaTrasportoMittente(boolean visualizzaTextAreaTrasportoMittente) {
	}
	
	public boolean isVisualizzaTextAreaLocationConnettore () {
		if(StringUtils.isNotEmpty(this.dettaglio.getLocationConnettore())) {
			if(this.dettaglio.getLocationConnettore().length() > 150 || this.dettaglio.getLocationConnettore().startsWith(ConnettoreBase.LOCATION_CACHED))
				return true;
		} 
		return false;
	}

	public void setVisualizzaTextAreaLocationConnettore(boolean visualizzaTextAreaLocationConnettore) {
	}
	
	public boolean isVisualizzaTokenInfo(){
		boolean visualizzaMessaggio = true;
		String f = this.dettaglio.getTokenInfo();
		
		if(f == null)
			return false;
		
		StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
		String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuilder, false);
		if(errore!= null)
			return false;

		return visualizzaMessaggio;
	}
	
	public String getPrettyTokenInfo(){
		String f = this.dettaglio.getTokenInfo();
		String toRet = null;
		if(f !=null) {
			StringBuilder contenutoDocumentoStringBuilder = new StringBuilder();
			String errore = Utils.getTestoVisualizzabile(f.getBytes(),contenutoDocumentoStringBuilder, true);
			if(errore!= null)
				return "";
		 
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
			try {
				toRet = jsonUtils.toString(jsonUtils.getAsNode(f));
			} catch (UtilsException e) {
			}
			 
		}

		if(toRet == null)
			toRet = f != null ? f : "";

		return toRet;
	}
	
	public String downloadTokenInfo(){
		DettagliBean.log.debug("downloading TokenInfo: "+this.dettaglio.getId());
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
			
			String toRet = this.dettaglio.getTokenInfo();
			JSONUtils jsonUtils = JSONUtils.getInstance(true);
			try {
				toRet = jsonUtils.toString(jsonUtils.getAsNode(this.dettaglio.getTokenInfo()));
			} catch (UtilsException e) {
			}
			
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
			DettagliBean.log.error(e.getMessage(), e);
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
			
			SingleFileExporter.exportContenuti(log, this.dettaglio, zip, dirPath, this.transazioniService, this.exportContenuto,
					DettagliBean.headersAsProperties,DettagliBean.contenutiAsProperties);
			
			zip.flush();
			zip.close();

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			DettagliBean.log.error(e.getMessage(), e);
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
		this.diagnosticiBean  = new DiagnosticiBean();
		this.diagnosticiBean.setIdEgov(this.idEgov);
		this.diagnosticiBean.setIdentificativoPorta(this.identificativoPorta);
		this.diagnosticiBean.setIdTransazione(this.idTransazione);
		
		TransazioneBean trBean = this.getDettaglio();
		if(trBean!=null) {
			this.diagnosticiBean.setProtocollo(trBean.getProtocollo()); 
			this.diagnosticiBean.setNomeServizioApplicativo(null);
			
			try {
				EsitiProperties esitiProperties = EsitiProperties.getInstance(log, trBean.getProtocollo());
				EsitoTransazioneName esitoTransactionName = esitiProperties.getEsitoTransazioneName(trBean.getEsito());
				if(EsitoTransazioneName.isConsegnaMultipla(esitoTransactionName)) {
					this.diagnosticiBean.setForceNomeServizioApplicativoNull(true);
				}
				else {
					this.diagnosticiBean.setForceNomeServizioApplicativoNull(false);
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
				
		return this.diagnosticiBean;
	}
	public void setDiagnosticiBean(DiagnosticiBean diagnosticiBean) {
		this.diagnosticiBean = diagnosticiBean;
	}

	public boolean isVisualizzaIdCluster() {
		return this.visualizzaIdCluster;
	}

	public void setVisualizzaIdCluster(boolean visualizzaIdCluster) {
		this.visualizzaIdCluster = visualizzaIdCluster;
	}
}
