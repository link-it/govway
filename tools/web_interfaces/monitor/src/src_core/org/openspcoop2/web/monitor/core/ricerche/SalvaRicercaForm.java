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
package org.openspcoop2.web.monitor.core.ricerche;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.web.lib.users.dao.RicercaUtente;
import org.openspcoop2.web.monitor.core.bean.AbstractDateSearchForm;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.utils.MessageManager;

/****
 * SalvaRicercaForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SalvaRicercaForm {

	private String label;
	private String visibilita;
	private String descrizione;
	private ModuloRicerca modulo;
	private String modalitaRicerca;
	private AbstractDateSearchForm search;
	private String salvaRicercaErrorMessage = null;
	
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getVisibilita() {
		return this.visibilita;
	}
	public void setVisibilita(String visibilita) {
		this.visibilita = visibilita;
	}
	public AbstractDateSearchForm getSearch() {
		return this.search;
	}
	public void setSearch(AbstractDateSearchForm search) {
		this.search = search;
	}
	public String getSalvaRicercaErrorMessage() {
		return this.salvaRicercaErrorMessage;
	}
	public void setSalvaRicercaErrorMessage(String salvaRicercaErrorMessage) {
		this.salvaRicercaErrorMessage = salvaRicercaErrorMessage;
	}
	public ModuloRicerca getModulo() {
		return this.modulo;
	}
	public void setModulo(ModuloRicerca modulo) {
		this.modulo = modulo;
	}
	public String getModalitaRicerca() {
		return this.modalitaRicerca;
	}
	public void setModalitaRicerca(String modalitaRicerca) {
		this.modalitaRicerca = modalitaRicerca;
	}
	public String getDescrizione() {
		return this.descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public void initFormListener(ActionEvent ae) {
		this.label = "";
		this.descrizione = "";
		this.visibilita = "";
		this.search = null;
		this.salvaRicercaErrorMessage = null;
	}
	
	public boolean eseguiValidazioneForm() {
		
		// validazione label
		this.salvaRicercaErrorMessage = SalvaRicercaForm.validaLabel(this.getLabel());
		
		if(this.salvaRicercaErrorMessage != null) {
			return false;
		}
		
		// validazione descrizione
		this.salvaRicercaErrorMessage = SalvaRicercaForm.validaDescrizione(this.getDescrizione());
		
		if(this.salvaRicercaErrorMessage != null) {
			return false;
		}
		
		// validazione visibilita
		this.salvaRicercaErrorMessage = SalvaRicercaForm.validaVisibilita(this.getVisibilita());
		
		return this.salvaRicercaErrorMessage == null;
	}
	
	public static String validaLabel(String label) {
		if (StringUtils.isEmpty(label)) {
			return MessageManager.getInstance().getMessage(Costanti.SALVA_RICERCA_MISSING_PARAMETER_LABEL_LABEL_KEY);
		}
		
		if (label.length() > 255) {
			return MessageManager.getInstance().getMessage(Costanti.SALVA_RICERCA_INVALID_PARAMETER_LABEL_DIMENSIONE_NON_VALIDA_LABEL_KEY);
		}
		
		return null;
	}
	
	public static String validaDescrizione(String descrizione) {
		if (StringUtils.isEmpty(descrizione)) {
			return MessageManager.getInstance().getMessage(Costanti.SALVA_RICERCA_MISSING_PARAMETER_LABEL_LABEL_KEY);
		}
		
		if (descrizione.length() > 4000) {
			return MessageManager.getInstance().getMessage(Costanti.SALVA_RICERCA_INVALID_PARAMETER_DESCRIZIONE_DIMENSIONE_NON_VALIDA_LABEL_KEY);
		}
		
		return null;
	}
	
	public static String validaVisibilita(String visibilita) {
		if (StringUtils.isEmpty(visibilita)) {
			return MessageManager.getInstance().getMessage(Costanti.SALVA_RICERCA_MISSING_PARAMETER_VISIBILITA_LABEL_KEY);
		}
		
		return null;
	}
	
	public String validaForm() {
		this.eseguiValidazioneForm();
		return null;
	}
	
	public RicercaUtente getRicerca() throws UtilsException {
		RicercaUtente ricercaPersonalizzata = new RicercaUtente();
		
		ricercaPersonalizzata.setLabel(this.label);
		ricercaPersonalizzata.setDescrizione(this.descrizione);
		ricercaPersonalizzata.setDataCreazione(new Date());
		ricercaPersonalizzata.setVisibilita(this.visibilita);
		ricercaPersonalizzata.setModulo(this.modulo.toString());
		ricercaPersonalizzata.setModalitaRicerca(this.modalitaRicerca);
		ricercaPersonalizzata.setProtocollo(this.search.getProtocolloRicerca());
		ricercaPersonalizzata.setSoggetto(this.search.getSoggettoRicerca());
		
		FiltriRicercaPersonalizzata filtriRicercaPersonalizzata = new FiltriRicercaPersonalizzata();
		filtriRicercaPersonalizzata.setFiltri(this.search.getFiltriImpostati());
		
		JSONUtils jsonUtils = JSONUtils.getInstance();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		jsonUtils.writeTo(filtriRicercaPersonalizzata, baos);
		ricercaPersonalizzata.setRicerca(baos.toString());
		
		return ricercaPersonalizzata;
	}
	public void impostaIdRicercaSalvata(long idUtente, long idRicerca) {

		String idRicercaForm = idUtente+ "_" + idRicerca;
		this.search.setRicercaUtente(idRicercaForm);
		
	}
	
}
