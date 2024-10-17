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
package org.openspcoop2.web.monitor.core.bean;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.beans.BlackListElement;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.constants.CostantiGrafici;
import org.openspcoop2.web.monitor.core.constants.ModalitaRicercaTransazioni;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.ricerche.ModuloRicerca;
import org.openspcoop2.web.monitor.core.utils.BeanUtils;
import org.openspcoop2.web.monitor.core.utils.MessageManager;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * RicercaUtenteBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicercaUtenteBean extends RicercaUtente {

	private static final long serialVersionUID = 1L;

	public RicercaUtenteBean() {
		super();
	}

	public RicercaUtenteBean(RicercaUtente ricercaUtente) {
		List<BlackListElement> metodiEsclusi = new ArrayList<>(0);
		BeanUtils.copy(this, ricercaUtente, metodiEsclusi);
	}

	@JsonIgnore
	public String getModuloLabel() {
		String modulo = this.getModulo();

		if(modulo == null || modulo.equals("")) {
			return Costanti.NON_SELEZIONATO;
		}
		ModuloRicerca moduloRicerca = ModuloRicerca.valueOf(modulo);

		switch (moduloRicerca) {
		case ModuloRicerca.ALLARMI:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_ALLARMI_LABEL_KEY);
		case ModuloRicerca.CONFIGURAZIONI:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_CONFIGURAZIONI_LABEL_KEY);
		case ModuloRicerca.EVENTI:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_EVENTI_LABEL_KEY);
		case ModuloRicerca.STATISTICHE:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_STATISTICHE_LABEL_KEY);
		case ModuloRicerca.STATISTICHE_PERSONALIZZATE:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_STATISTICHE_PERSONALIZZATE_LABEL_KEY);
		case ModuloRicerca.TRANSAZIONI:
			return MessageManager.getInstance().getMessageFromResourceBundle(Costanti.RICERCHE_UTENTE_MODULO_TRANSAZIONI_LABEL_KEY);
		default:
			return Costanti.NON_SELEZIONATO;
		}
	}

	@JsonIgnore
	public String getModalitaRicercaLabel() {
		String modalitaRicerca = this.getModalitaRicerca();

		if(modalitaRicerca == null || modalitaRicerca.equals("")) {
			return Costanti.NON_SELEZIONATO;
		}

		String modulo = this.getModulo();

		if(modulo.equals(ModuloRicerca.STATISTICHE.toString())) {
			switch (modalitaRicerca) {
			case CostantiGrafici.TIPO_DISTRIBUZIONE_TEMPORALE: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_TEMPORALE_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_ESITI: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ESITI_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_ERRORI: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_ERRORI_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_REMOTO_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SOGGETTO_LOCALE_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_SERVIZIO_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_AZIONE: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_AZIONE_LABEL_KEY);
			case CostantiGrafici.TIPO_DISTRIBUZIONE_SERVIZIO_APPLICATIVO: 
				return MessageManager.getInstance().getMessage(Costanti.STATS_ANALISI_STATISTICA_TIPO_DISTRIBUZIONE_APPLICATIVO_LABEL_KEY);
			default:
				return Costanti.NON_SELEZIONATO;
			}

		}

		if(modulo.equals(ModuloRicerca.TRANSAZIONI.toString())) {
			ModalitaRicercaTransazioni modalitaRicercaTransazioni = ModalitaRicercaTransazioni.getFromString(modalitaRicerca);

			switch (modalitaRicercaTransazioni) {
			case ModalitaRicercaTransazioni.ANDAMENTO_TEMPORALE:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_BASE_LABEL_KEY);
			case ModalitaRicercaTransazioni.RICERCA_LIBERA:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_TEMPORALE_RICERCA_LIBERA_LABEL_KEY);
			case ModalitaRicercaTransazioni.MITTENTE_TOKEN_INFO:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_TOKEN_INFO_LABEL_KEY);
			case ModalitaRicercaTransazioni.MITTENTE_SOGGETTO:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_SOGGETTO_LABEL_KEY);
			case ModalitaRicercaTransazioni.MITTENTE_APPLICATIVO:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_APPLICATIVO_LABEL_KEY);
			case ModalitaRicercaTransazioni.MITTENTE_IDENTIFICATIVO_AUTENTICATO:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_IDENTIFICATIVO_AUTENTICATO_LABEL_KEY);
			case ModalitaRicercaTransazioni.MITTENTE_INDIRIZZO_IP:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_MITENTE_RICERCA_INDIRIZZO_IP_LABEL_KEY);
			case ModalitaRicercaTransazioni.ID_APPLICATIVO_AVANZATA:
				return MessageManager.getInstance().getMessage(Costanti.TRANSAZIONI_SEARCH_TIPO_RICERCA_ID_RICERCA_ID_APPLICATIVO_LVL2_RICERCA_AVANZATA_LABEL_KEY);
			default:
				return Costanti.NON_SELEZIONATO;
			}
		}

		return Costanti.NON_SELEZIONATO;
	}
	
	@JsonIgnore
	public String getVisibilitaLabel() {
		String visibilita = this.getVisibilita();
		
		if(visibilita.equals(Costanti.VALUE_VISIBILITA_RICERCA_UTENTE_PRIVATA)) {
			return MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_VISIBILITA_PRIVATA_LABEL_KEY);
		} else {
			return MessageManager.getInstance().getMessage(Costanti.RICERCHE_UTENTE_VISIBILITA_PUBBLICA_LABEL_KEY);
		}
	}
	
	@JsonIgnore
	public String getDescrizioneTooltip() {
		String descrizione = this.getDescrizione();
		
		if(descrizione == null) {
			return "";
		}
		
		if(descrizione.length() > 1000) {
			return descrizione.substring(0, 997) + "...";
		}
		
		return descrizione;
	}
	
	@JsonIgnore
	public String getProtocolloLabel() {
		String protocollo = super.getProtocollo();
		
		if(protocollo == null || protocollo.equals("")) {
			return Costanti.LABEL_PARAMETRO_MODALITA_ALL;
		}
		
		// traduzione della label
		try {
			return NamingUtils.getLabelProtocollo(protocollo);
		} catch (ProtocolException e) {
			return protocollo;
		}
	}
	
	@JsonIgnore
	public String getSoggettoLabel() {
		String soggetto = super.getSoggetto();
		
		if(soggetto == null || soggetto.equals("")) {
			return Costanti.LABEL_PARAMETRO_MODALITA_ALL;
		}
		
		String tipoSoggettoOperativoSelezionato = Utility.parseTipoSoggetto(soggetto);
		String nomeSoggettoOperativoSelezionato = Utility.parseNomeSoggetto(soggetto);
		IDSoggetto idSoggetto = new IDSoggetto(tipoSoggettoOperativoSelezionato, nomeSoggettoOperativoSelezionato);
		try {
			return NamingUtils.getLabelSoggetto(idSoggetto);
		} catch (ProtocolException e) {
			return soggetto;
		}
	}
}
