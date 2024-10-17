/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.mbean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.monitor.core.bean.FileUploadBean;
import org.openspcoop2.web.monitor.core.bean.RicercaUtenteBean;
import org.openspcoop2.web.monitor.core.bean.RicercheUtenteSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.dao.IRicercheUtenteService;
import org.openspcoop2.web.monitor.core.dao.IService;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.openspcoop2.web.monitor.core.ricerche.RicerchePersonalizzate;
import org.openspcoop2.web.monitor.core.ricerche.SalvaRicercaForm;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * RicercheUtenteBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercheUtenteBean extends PdDBaseBean<RicercaUtenteBean, Long, IService<RicercaUtenteBean, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	private transient RicercheUtenteSearchForm search;

	private FileUploadBean ricercheFile= null;
	private String idFiles= null;
	private String caricaRicercheErrorMessage = null;
	private boolean salvataggioOk = false;

	public RicercheUtenteBean() {
	}

	public RicercheUtenteSearchForm getSearch() {
		return this.search;
	}
	public void setSearch(RicercheUtenteSearchForm search) {
		this.search = search;
	}

	public List<SelectItem> getListaModuli() {
		List<SelectItem> moduli = new ArrayList<>();

		moduli.add(new SelectItem(Costanti.NON_SELEZIONATO, Costanti.NON_SELEZIONATO));
		moduli.add(new SelectItem(ModuloRicerca.ALLARMI.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_ALLARMI_LABEL_KEY)));
		moduli.add(new SelectItem(ModuloRicerca.CONFIGURAZIONI.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_CONFIGURAZIONI_LABEL_KEY)));
		moduli.add(new SelectItem(ModuloRicerca.EVENTI.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_EVENTI_LABEL_KEY)));
		moduli.add(new SelectItem(ModuloRicerca.STATISTICHE.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_STATISTICHE_LABEL_KEY)));
		moduli.add(new SelectItem(ModuloRicerca.STATISTICHE_PERSONALIZZATE.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_STATISTICHE_PERSONALIZZATE_LABEL_KEY)));
		moduli.add(new SelectItem(ModuloRicerca.TRANSAZIONI.toString(), MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_TRANSAZIONI_LABEL_KEY)));

		return moduli;
	}

	public List<SelectItem> getListaModalitaRicerca() {
		List<SelectItem> modalitaRicercaList = new ArrayList<>();

		modalitaRicercaList.add(new SelectItem(Costanti.NON_SELEZIONATO, Costanti.NON_SELEZIONATO));

		String filtroModulo2 = this.search.getFiltroModulo();

		if(filtroModulo2.equals(ModuloRicerca.STATISTICHE.toString())) {
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY))); 
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO, MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY)));
		}

		if(filtroModulo2.equals(ModuloRicerca.TRANSAZIONI.toString())) {
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.RICERCA_LIBERA.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.MITTENTE_TOKEN_INFO.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.MITTENTE_SOGGETTO.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.MITTENTE_APPLICATIVO.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.MITTENTE_IDENTIFICATIVO_AUTENTICATO.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.MITTENTE_INDIRIZZO_IP.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY)));
			modalitaRicercaList.add(new SelectItem(ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA.toString(), MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY)));
		}

		return modalitaRicercaList;
	}

	@Override
	public String delete() {
		List<Long> idReport = new ArrayList<>();

		// se nn sono in select all allore prendo solo quelle selezionate
		if (!this.isSelectedAll()) {
			Iterator<RicercaUtenteBean> it = this.selectedIds.keySet().iterator();
			while (it.hasNext()) {
				RicercaUtenteBean bean = it.next();
				if (this.selectedIds.get(bean).booleanValue()) {
					idReport.add(bean.getId());
					it.remove();
				}
			}

			for (Long idToRemove : idReport) {
				log.debug("Rimozione Ricerca [{}]", idToRemove);
				this.service.deleteById(idToRemove);
			}
		} else {
			try {
				this.service.deleteAll();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_CANCELLA_RICERCHE_MESSAGGIO_ERRORE_LABEL_KEY));
			}
		}

		return null;
	}

	@Override
	public void initExportListener(ActionEvent ae) {
		super.initExportListener(ae);
	}

	public String exportSelected() {
		try {
			// recupero lista identificativi
			List<String> idRicerche = new ArrayList<>();

			// se nn sono in select all allore prendo solo quelle selezionate
			if (!this.isSelectedAll()) {
				Iterator<RicercaUtenteBean> it = this.selectedIds.keySet().iterator();
				while (it.hasNext()) {
					RicercaUtenteBean bean = it.next();
					if (this.selectedIds.get(bean).booleanValue()) {
						idRicerche.add(bean.getId() +"");
						it.remove();
					}
				}
			}

			// We must get first our context
			FacesContext context = FacesContext.getCurrentInstance();

			// Then we have to get the Response where to write our file
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();

			// Salvo i parametri di export in sessione
			HttpSession sessione = (HttpSession) context.getExternalContext().getSession(false);

			sessione.setAttribute(Costanti.PARAMETER_IDS_ORIGINALI, StringUtils.join(idRicerche, ","));
			sessione.setAttribute(Costanti.PARAMETER_IS_ALL_ORIGINALE, this.isSelectedAll());

			response.sendRedirect(context.getExternalContext()
					.getRequestContextPath()
					+ "/" + Costanti.RICERCHE_EXPORTER_SERVLET_NAME + "?" + Costanti.PARAMETER_IS_ALL + "="
					+ this.isSelectedAll()
					+ "&" + Costanti.PARAMETER_IDS + "="
					+ StringUtils.join(idRicerche, ",")
					);

			context.responseComplete();

			// End of the method
		} catch (Exception e) {
			FacesContext.getCurrentInstance().responseComplete();
			log.error(e.getMessage(), e);
			MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_ESPORTA_RICERCHE_MESSAGGIO_ERRORE_LABEL_KEY));
		}
		return null;
	}

	public String salvaRicerche() {
		this.salvataggioOk = false;
		this.caricaRicercheErrorMessage = null;

		// salvataggio ricerche caricate
		if(this.idFiles != null && !this.idFiles.isEmpty()) {
			// validazione identificativo file caricato
			String checkIdentificativiMsg = this.checkIdentificativi();
			if(checkIdentificativiMsg != null) {
				// c'e' un errore di correlazione tra gli id caricati e quelli ricevuti al momento del salvataggio
				log.error("Identificativi ricevuti non coincidono con quelli in sessione il salvataggio non verra' effettuato.");
				this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_LABEL_KEY));
				return null;
			}

			if(this.ricercheFile.getMapElementiRicevuti().isEmpty()) {
				// lista file vuota
				log.error("Identificativi ricevuti non coincidono con quelli in sessione il salvataggio non verra' effettuato.");
				this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_LABEL_KEY));
				return null;
			}

			// eseguo prima tutte le validazioni e dopo i salvataggi
			JSONUtils jsonUtils = JSONUtils.getInstance();
			// 1. controllo del content-type caricato
			for (Entry<String, UploadItem> fileCaricatoEntry : this.ricercheFile.getMapElementiRicevuti().entrySet()) {
				UploadItem fileCaricato = fileCaricatoEntry.getValue();
				String fileName = fileCaricato.getFileName();
				if(fileCaricato.getContentType() != null) {
					boolean checkAcceptedType =  this.ricercheFile.checkAcceptedType(fileCaricato.getContentType());
					if(!checkAcceptedType) {
						log.error("Il ContentType "+fileCaricato.getContentType()+" del file ricerche "+fileName+" non e' valido.");
						this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_NON_VALIDO_LABEL_KEY, fileName));
						return null;
					}

					// 2. controllo del contenuto
					try {
						RicerchePersonalizzate ricerchePersonalizzate = jsonUtils.getAsObject(fileCaricato.getData(), RicerchePersonalizzate.class);

						if(ricerchePersonalizzate == null
								|| ricerchePersonalizzate.getRicerche() == null
								|| ricerchePersonalizzate.getRicerche().isEmpty()) {
							//
							log.error("Il file ricerche "+fileName+" e' vuoto.");
							this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_VUOTO_LABEL_KEY, fileName));
							return null;
						}
					} catch (UtilsException e) {
						log.error("Il contenuto del file ricerche "+fileName+" non e' valido.");
						this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_CONTENUTO_NON_VALIDO_LABEL_KEY, fileName));
						return null;
					}
				} else {
					log.error("Il ContentType "+fileCaricato.getContentType()+" del file ricerche "+fileName+" non e' valido.");
					this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_NON_VALIDO_LABEL_KEY, fileName));
					return null;
				}
			}

			List<String> msgRicercheScartate = new ArrayList<String>();
			// 3. salvataggio, quando arrivo qui i file sono gia' validati 
			for (Entry<String, UploadItem> fileCaricatoEntry : this.ricercheFile.getMapElementiRicevuti().entrySet()) {
				UploadItem fileCaricato = fileCaricatoEntry.getValue();
				String fileName = fileCaricato.getFileName();
				try {
					RicerchePersonalizzate ricerchePersonalizzate = jsonUtils.getAsObject(fileCaricato.getData(), RicerchePersonalizzate.class);

					// 3. salvataggio
					User loggedUtente = Utility.getLoggedUtente();
					String login = loggedUtente.getLogin();
					for (RicercaUtente ricercaPersonalizzata : ricerchePersonalizzate.getRicerche()) {
						RicercaUtenteBean ricercaUtenteBean = new RicercaUtenteBean(ricercaPersonalizzata);
						// verifica duplicati
						RicercaUtenteBean oldRicercaUtente = ((IRicercheUtenteService)this.service).leggiRicercaUtente(login, ricercaUtenteBean.getLabel(), ricercaUtenteBean.getModulo(), ricercaUtenteBean.getModalitaRicerca());
						if(oldRicercaUtente != null) {
							msgRicercheScartate.add(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(
									Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_RICERCA_DUPLICATA_LABEL_KEY, ricercaUtenteBean.getLabel(),
									ricercaUtenteBean.getModuloLabel(), ricercaUtenteBean.getModalitaRicercaLabel()));
							continue;
						}
						
						// verifica compatibilita' protocolli
						String protocolloRicercaImport = ricercaUtenteBean.getProtocollo();
						String soggettoRicercaImport = ricercaUtenteBean.getSoggetto();
						
						// protocollo selezionato
						if(protocolloRicercaImport != null && !protocolloRicercaImport.equals(Costanti.NON_SELEZIONATO)) {
							
							List<String> protocolliUtente = Utility.getProtocolli(loggedUtente);
							
							// protocollo della ricerca non tra quelli dell'utente
							if(!protocolliUtente.contains(protocolloRicercaImport)) {
								msgRicercheScartate.add(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(
										Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_PROTOCOLLO_NON_DISPONIBILE_LABEL_KEY, ricercaUtenteBean.getLabel(),
										ricercaUtenteBean.getProtocolloLabel()));
								continue;
							}
							
							// soggetto selezionato
							if(soggettoRicercaImport != null && !soggettoRicercaImport.equals(Costanti.NON_SELEZIONATO)) {
								String tipoSoggettoOperativoSelezionato = Utility.parseTipoSoggetto(soggettoRicercaImport);
								String nomeSoggettoOperativoSelezionato = Utility.parseNomeSoggetto(soggettoRicercaImport);
								
								List<Soggetto> soggettiOperativiAssociatiAlProfilo = Utility.getSoggettiOperativiAssociatiAlProfilo(Utility.getLoggedUser(), protocolloRicercaImport);
								
								boolean found = false;
								for (Soggetto soggetto : soggettiOperativiAssociatiAlProfilo) {
									if(soggetto.getTipoSoggetto().equals(tipoSoggettoOperativoSelezionato) && soggetto.getNomeSoggetto().equals(nomeSoggettoOperativoSelezionato)) {
										found = true;
										break;
									}
								}
								
								if(!found) {
									msgRicercheScartate.add(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(
											Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_SOGGETTO_NON_DISPONIBILE_LABEL_KEY, ricercaUtenteBean.getLabel(), ricercaUtenteBean.getSoggettoLabel()));
									continue;
								}
							}
						}
						
						((IRicercheUtenteService)this.service).insertRicerca(login, ricercaPersonalizzata);	
					}
				} catch (UtilsException e) {
					log.error("Il contenuto del file ricerche "+fileName+" non e' valido.");
					this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_CONTENUTO_NON_VALIDO_LABEL_KEY, fileName));
					return null;
				} catch (DriverUsersDBException | ProtocolException e) { // errore in fase di salvataggio
					log.error("Si e' verificato un errore in fase di salvataggio: " + e.getMessage(), e);
					this.setCaricaRicercheErrorMessage(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_MESSAGGIO_ERRORE_OPERAZIONE_NON_ESEGUITA));
					return null;
				}
			}

			this.salvataggioOk = true;
			if(!msgRicercheScartate.isEmpty()) {
				// visualizza warning ricerche scartate 
				MessageUtils.addWarnMsg(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_CARICAMENTO_COMPLETATO_CON_ERRORI_LABEL_KEY));
				for (String msgRicercaScartata : msgRicercheScartate) {
					MessageUtils.addWarnMsg(msgRicercaScartata);
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_OK_LABEL_KEY)));
			}
			return null;
		} else {
			log.error("Nessun file selezionato.");
			this.setCaricaRicercheErrorMessage(
					MessageManager.getInstance().getMessageWithParamsFromResourceBundle(Costanti.RICERCHE_UTENTE_IMPORTA_RICERCHE_MESSAGGIO_ERRORE_FILE_NON_INDICATO_LABEL_KEY,
							MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_FORM_FIELD_FILE_LABEL_KEY)));
			return null;
		}
	}

	public String aggiornaRicerca() {

		try {
			User loggedUtente = Utility.getLoggedUtente();
			String login = loggedUtente.getLogin();
			
			// 1. Validazione input
			String aggiornaRicercaErrorMessage = this.eseguiValidazioneForm();

			if(aggiornaRicercaErrorMessage != null) {
				// messaggio di errore per l'utente
				MessageUtils.addErrorMsg(aggiornaRicercaErrorMessage);
				// resto sulla stessa pagina
				return null;
			}

			// 2. leggo la ricerca aggiornata dal db
			RicercaUtenteBean oldRicercaUtente = this.service.findById(this.getSelectedElement().getId()); 
			
			// 3. verifica duplicati
			
			// controllo duplicati se ho cambiato la label
			if(!oldRicercaUtente.getLabel().equals(this.getSelectedElement().getLabel())) {
				if(((IRicercheUtenteService)this.service).leggiRicercaUtente(login, this.getSelectedElement().getLabel(), 
						this.getSelectedElement().getModulo(), this.getSelectedElement().getModalitaRicerca()) != null) {
					MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_AGGIORNA_RICERCA_MESSAGGIO_ERRORE_RICERCA_DUPLICATA_LABEL_KEY));
					return null;
				}
			}
			

			// 4. aggiorno dati
			oldRicercaUtente.setLabel(this.getSelectedElement().getLabel());
			oldRicercaUtente.setDescrizione(this.selectedElement.getDescrizione());
			oldRicercaUtente.setVisibilita(this.selectedElement.getVisibilita());

			// 5.salvataggio
			
			((IRicercheUtenteService)this.service).updateRicerca(login, oldRicercaUtente);

			// 6. Messaggio di info per l'utente
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_AGGIORNA_RICERCA_OK_LABEL_KEY)));

			// salvataggio ok torno alla lista
			return "ricercheUtente";
		} catch (DriverUsersDBException e) {
			log.error("Si e' verificato un errore in fase di salvataggio: " + e.getMessage(), e);
			MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_MESSAGGIO_ERRORE_OPERAZIONE_NON_ESEGUITA));
		}

		return null;
	}

	public String eseguiValidazioneForm() {

		// validazione label
		String aggiornaRicercaErrorMessage = SalvaRicercaForm.validaLabel(this.getSelectedElement().getLabel());

		if(aggiornaRicercaErrorMessage != null) {
			return aggiornaRicercaErrorMessage;
		}

		// validazione descrizione
		aggiornaRicercaErrorMessage = SalvaRicercaForm.validaDescrizione(this.getSelectedElement().getDescrizione());

		if(aggiornaRicercaErrorMessage != null) {
			return aggiornaRicercaErrorMessage;
		}

		// validazione visibilita
		aggiornaRicercaErrorMessage = SalvaRicercaForm.validaVisibilita(this.getSelectedElement().getVisibilita());

		if(aggiornaRicercaErrorMessage != null) {
			return aggiornaRicercaErrorMessage;
		}

		return null;
	}

	public void initFormRicercaListener(ActionEvent ae) {
		this.ricercheFile.clear();
		this.salvataggioOk = false;
		this.caricaRicercheErrorMessage = null;
	}

	public FileUploadBean getRicercheFile() {
		return this.ricercheFile;
	}
	public void setRicercheFile(FileUploadBean ricercheFile) {
		this.ricercheFile = ricercheFile;
		this.ricercheFile.setmBean(this);
	}

	public String getIdFiles() {
		return this.idFiles;
	}

	public void setIdFiles(String idFiles) {
		this.idFiles = idFiles;
	}

	public void clearIdFiles() {
		this.setIdFiles("");
	}

	public String checkIdentificativi() {
		if(StringUtils.isNotEmpty(this.idFiles)) {
			String[] ids = this.idFiles.split(",");

			if(ids!= null && ids.length > 0) {
				if(ids.length != this.ricercheFile.getMapChiaviElementi().size())
					return Costanti.ERRORE_GENERICO;

				for (String idRicerca : ids) {
					if(!this.ricercheFile.getMapChiaviElementi().containsKey(idRicerca)) {
						return Costanti.ERRORE_GENERICO;
					}
				}
				//ok
				return null;
			}
		}

		return Costanti.ERRORE_GENERICO;
	}

	public boolean isVisualizzaComandiEliminaFile() {
		return !this.ricercheFile.getMapElementiRicevuti().isEmpty();
	}

	public void setVisualizzaComandiEliminaFile(boolean visualizzaComandiEliminaFile) {
	}

	public String getCaricaRicercheErrorMessage() {
		return this.caricaRicercheErrorMessage;
	}

	public void setCaricaRicercheErrorMessage(String caricaRicercheErrorMessage) {
		this.caricaRicercheErrorMessage = caricaRicercheErrorMessage;
	}

	public boolean isSalvataggioOk(){
		return this.salvataggioOk;
	}

	public void setSalvataggioOk(boolean salvataggioOk) {
		this.salvataggioOk = salvataggioOk;
	}
}
