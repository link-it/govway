/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.statistiche.constants.PossibiliStatiRichieste;
import org.openspcoop2.web.monitor.core.bean.BaseSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.openspcoop2.web.monitor.core.utils.MessageManager;
import org.openspcoop2.web.monitor.core.utils.MessageUtils;
import org.openspcoop2.web.monitor.statistiche.constants.ModalitaRicercaStatistichePdnd;
import org.openspcoop2.web.monitor.statistiche.constants.StatisticheCostanti;
import org.slf4j.Logger;

/**
 * StatistichePdndTracingSearchForm
 * 
 * @author Pintori Giuliano (giuliano.pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class StatistichePdndTracingSearchForm extends BaseSearchForm implements Cloneable {
	
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	private String tracingId;
	private Integer tentativiPubblicazione;
	private String stato;
	private String statoPdnd;
	
	private static String defaultModalitaRicerca = ModalitaRicercaStatistichePdnd.ANDAMENTO_TEMPORALE.getValue();
	private String modalitaRicerca = StatistichePdndTracingSearchForm.defaultModalitaRicerca;
	private int livelloRicerca = ModalitaRicercaStatistichePdnd.getLivello(this.modalitaRicerca);
	private boolean backRicerca = false;
	
	private static List<String> elencoFieldsRicercaDaIgnorare = new ArrayList<>();	
	
	static {
		// aggiungo tutti i fields delle super classi
		elencoFieldsRicercaDaIgnorare.addAll(Arrays.asList(Costanti.SEARCH_FORM_FIELDS_DA_NON_SALVARE));
	}
	
	public StatistichePdndTracingSearchForm(){
		super();
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(StatistichePdndTracingSearchForm.log);
			this.setUseCount(govwayMonitorProperties.isAttivoUtilizzaCountStatistichePdndTracingLista()); 
			
			this.modalitaRicerca = StatistichePdndTracingSearchForm.defaultModalitaRicerca;
			this.setLivelloRicerca(ModalitaRicercaStatistichePdnd.getLivello(this.modalitaRicerca));
		}catch (Exception e) {
			StatistichePdndTracingSearchForm.log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public void initSearchListener(ActionEvent ae) {
		super.initSearchListener(ae);
		this.executeQuery = false;
		this.modalitaRicerca = StatistichePdndTracingSearchForm.defaultModalitaRicerca;
		this.stato = Costanti.NON_SELEZIONATO;
		this.statoPdnd = Costanti.NON_SELEZIONATO;
		this.tentativiPubblicazione = null;
		this.tracingId = null;
	}

	@Override
	protected String ripulisciValori(){
		this.initSearchListener(null);
		return null;
	}

	@Override
	protected String eseguiAggiorna() {
		return null;
	}
	
	@Override
	protected String eseguiFiltra() {
		return this.filtraEngine();
	}
	
	private String filtraEngine() {
		try {
			// Tipo di periodo selezionato 'Personalizzato'
			if(this.getPeriodo().equals("Personalizzato")){
				if(this.getDataInizio() == null){
					MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_PERIODO_DATA_INIZIO_LABEL_KEY));
					return null;
				}

				if(this.getDataFine() == null){
					MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(Costanti.SEARCH_MISSING_PARAMETERS_PERIODO_DATA_FINE_LABEL_KEY));
					return null;
				}
			}

			this.setBackRicerca(false);
			
			ModalitaRicercaStatistichePdnd ricerca = ModalitaRicercaStatistichePdnd.getFromString(this.getModalitaRicerca());
			if(ricerca!=null){
				switch (ricerca) {
				case TRACING_ID:
					if(org.apache.commons.lang.StringUtils.isEmpty(this.getTracingId())){
						MessageUtils.addErrorMsg(MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_MISSING_PARAMETERS_TRACING_ID_LABEL_KEY));
						return null;
					}
					break;
				case ANDAMENTO_TEMPORALE:
				default:
					break;
				}
			}
		} finally {
			//donothing
		}
		return null;
	}

	public String getTracingId() {
		return this.tracingId;
	}

	public void setTracingId(String tracingId) {
		this.tracingId = tracingId;
	}

	public Integer getTentativiPubblicazione() {
		return this.tentativiPubblicazione;
	}

	public void setTentativiPubblicazione(Integer numeroTentativi) {
		this.tentativiPubblicazione = numeroTentativi;
	}

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getStatoPdnd() {
		return this.statoPdnd;
	}

	public void setStatoPdnd(String statoPdnd) {
		this.statoPdnd = statoPdnd;
	}
	
	public boolean isShowFiltroSoggetto() {
        // viene visualizzato solo se non e' selezionata una modalita'
		String loggedUtenteModalita = Utility.getLoggedUtenteModalita();
		
		return Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteModalita);
	}
	
	@Override
	public String getProtocollo() {
		// protocollo della sezione consente di gestire solo soggetti modiPA
		return CostantiLabel.MODIPA_PROTOCOL_NAME;
	}
	
	public int getLivelloRicerca() {
		return this.livelloRicerca;
	}

	public void setLivelloRicerca(int livelloRicerca) {
		this.livelloRicerca = livelloRicerca;
	}
	
	public boolean isBackRicerca() {
		return this.backRicerca;
	}

	public void setBackRicerca(boolean backRicerca) {
		this.backRicerca = backRicerca;
	}
	
	@Override
	public String getModalitaRicerca() {
		if(this.modalitaRicerca==null || "".equals(this.modalitaRicerca)){
			this.modalitaRicerca = StatistichePdndTracingSearchForm.defaultModalitaRicerca;
		}
		return this.modalitaRicerca;
	}

	public void setModalitaRicerca(String modalitaRicerca) {
		this.modalitaRicerca = modalitaRicerca;
		this.setLivelloRicerca(ModalitaRicercaStatistichePdnd.getLivello(this.modalitaRicerca));
	}
	
	public boolean isRicercaLiberaTuttiProfili() {
		return ModalitaRicercaTransazioni.RICERCA_LIBERA.getValue().equals(this.modalitaRicerca) && this.isAllProtocol();
	}
	
	public String getTipoRicercaLabel() {
		if(this.getModalitaRicerca() != null) {
			ModalitaRicercaStatistichePdnd t = ModalitaRicercaStatistichePdnd.getFromString(this.getModalitaRicerca());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_BREADCUMP_KEY);
			case TRACING_ID:
			default:
				return MessageManager.getInstance().getMessage(StatisticheCostanti.STATS_PDND_TRACING_SEARCH_TIPO_RICERCA_ID_RICERCA_TRACING_ID_BREADCUMP_KEY);
			}
		}
		
		return "Visualizza Statistiche Tracing PDND";
	}
	
	public boolean isShowRicercaPanel() {
		if(this.getModalitaRicerca() != null) {
			ModalitaRicercaStatistichePdnd t = ModalitaRicercaStatistichePdnd.getFromString(this.getModalitaRicerca());
			switch (t) { 
			case ANDAMENTO_TEMPORALE:
				return true;
			case TRACING_ID:
			default:
				return false;
			}
		}
		return false;
	}
	
	public boolean isShowFiltroStatoPdnd(){
		// visualizzo stato pdnd se e' stato indicato lo stato ed e' published
		return (this.stato != null && !this.stato.equals(Costanti.NON_SELEZIONATO) && this.stato.equals(PossibiliStatiRichieste.PUBLISHED.getValue()));
	}
	
	public void statoListener(ActionEvent ae) {
		if (!PossibiliStatiRichieste.PUBLISHED.equals(this.stato))
			this.statoPdnd = Costanti.NON_SELEZIONATO;
	}

	@Override
	public ModuloRicerca getModulo() {
		return ModuloRicerca.STATISTICHE;
	}
	
	@Override
	public boolean isVisualizzaComandoSalvaRicerca() {
		return true;
	}
	
	@Override
	public List<String> getElencoFieldRicercaDaIgnorare() {
		return elencoFieldsRicercaDaIgnorare;
	}
}
