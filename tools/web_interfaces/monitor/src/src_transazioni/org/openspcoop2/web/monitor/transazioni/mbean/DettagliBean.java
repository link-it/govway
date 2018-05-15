package org.openspcoop2.web.monitor.transazioni.mbean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.PdDBaseBean;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.transazioni.bean.TransazioneBean;
import org.openspcoop2.web.monitor.transazioni.core.UtilityTransazioni;
import org.openspcoop2.web.monitor.transazioni.dao.ITransazioniService;
import org.openspcoop2.web.monitor.transazioni.exporter.CostantiExport;

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

	private TracciaBean tracciaRichiesta;
	private TracciaBean tracciaRisposta;
	private TransazioneBean dettaglio;
	private TransazioneBean originale;

	private static Boolean enableHeaderInfo = null;

	private transient IProtocolFactory<?> protocolFactory;
	
	private boolean showFaultCooperazione = false;
	private boolean showFaultIntegrazione = false;

	private boolean visualizzaDataAccettazione = false;
	private Boolean hasDumpRichiesta = null;
	private Boolean hasDumpRisposta = null;
	private Boolean hasHeaderTrasportoRichiesta = null;
	private Boolean hasHeaderTrasportoRisposta = null;
	
	static {
		try {

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(DettagliBean.log);

			DettagliBean.enableHeaderInfo = pddMonitorProperties.isAttivoTransazioniExportHeader();

		} catch (Exception e) {
			DettagliBean.log
			.warn("Inizializzazione servlet fallita, setto enableHeaderInfo=false",
					e);
		}
	}


	public DettagliBean(){
		try {

			PddMonitorProperties pddMonitorProperties = PddMonitorProperties.getInstance(DettagliBean.log);

			this.driver  = pddMonitorProperties.getDriverTracciamento();
			
			this.visualizzaDataAccettazione = pddMonitorProperties.isAttivoTransazioniDataAccettazione();

		} catch (Exception e) {
			DettagliBean.log
			.warn("Inizializzazione driverTracciamento fallita.....",
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
	 * @return
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
						String traccia = tracciaBuilder.toString(tr,TipoSerializzazione.XML);

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

	public String getTipologiaConProtocollo(){
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
			return ruolo + " ("+this.dettaglio.getProtocollo()+")";
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
			if (!this.isRisposta) {
				this.tracciaRichiesta = new TracciaBean(this.driver.getTraccia(RuoloMessaggio.RICHIESTA, properties));
				notNull = this.tracciaRichiesta!=null;
			} else {
				this.tracciaRisposta = new TracciaBean(this.driver.getTraccia(RuoloMessaggio.RISPOSTA, properties));
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
	
 	public boolean getHasDumpRichiesta() {
		if(this.hasDumpRichiesta == null)
			this.hasDumpRichiesta  = this.getHasDump(TipoMessaggio.RICHIESTA_INGRESSO); //TODO sistemare
		
		return this.hasDumpRichiesta;
	}

	public boolean getHasDumpRisposta() {
		if(this.hasDumpRisposta == null)
			this.hasDumpRisposta  = this.getHasDump(TipoMessaggio.RISPOSTA_INGRESSO); //TODO sistemare
		
		return this.hasDumpRisposta;
	}

	private boolean getHasDump(TipoMessaggio tipo) {
		return this.transazioniService.hasInfoDumpAvailable(this.idTransazione, tipo);
	}

	public boolean getHasHeaderTrasportoRichiesta() {
		if(this.hasHeaderTrasportoRichiesta == null)
			this.hasHeaderTrasportoRichiesta = this.getHasHeaderTrasporto(TipoMessaggio.RICHIESTA_INGRESSO); //TODO sistemare
		
		return this.hasHeaderTrasportoRichiesta;
	}

	public boolean getHasHeaderTrasportoRisposta() {
		if(this.hasHeaderTrasportoRisposta == null)
			this.hasHeaderTrasportoRisposta = this.getHasHeaderTrasporto(TipoMessaggio.RISPOSTA_INGRESSO); //TODO sistemare 
		
		return this.hasHeaderTrasportoRisposta;
	}

	private boolean getHasHeaderTrasporto(TipoMessaggio tipo) {
		return this.transazioniService.hasInfoHeaderTrasportoAvailable(this.idTransazione, tipo);
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
		
		TracciaBean tr = new TracciaBean(traccia);
		if (!this.isRisposta) {
			this.tracciaRichiesta = tr;
		}
		else{
			this.tracciaRisposta = tr;
		}

		if (tr != null && this.protocolFactory == null) {
			try {
				this.protocolFactory = ProtocolFactoryManager.getInstance()
						.getProtocolFactoryByName(
								this.getTraccia().getProtocollo());
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
}
