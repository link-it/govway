/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.richfaces.model.UploadItem;
import org.springframework.http.MediaType;

/****
 * 
 * BaseFileUploadBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class BaseFileUploadBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String, UploadItem> mapElementiRicevuti = null;
	private Map<String, String> mapChiaviElementi = null;
	
	private int numeroFile = 1;
	
	protected BaseFileUploadBean() {
		this.mapChiaviElementi = new HashMap<>();
		this.mapElementiRicevuti = new HashMap<>();
	}
	
	public void setNumeroFile(int numeroFile) {
		this.numeroFile = numeroFile;
	}
	
	public void clear() {
		this.mapChiaviElementi.clear();
		this.mapElementiRicevuti.clear();
	}
	
	public int getNumeroFile(){
		return this.numeroFile - this.mapElementiRicevuti.size();
	}
	
	public boolean checkAcceptedType(String contentType) {
		if(contentType == null) {
			return false;
		}

		return Arrays.asList(this.getAcceptedTypes()).contains(MediaType.parseMediaType(contentType));
	}
	
	public abstract void startUploadsListener(final ActionEvent e);
	
	public abstract void stopUploadsListener(final ActionEvent e);
	
	public abstract void uploadErrorListener(final ActionEvent e);
	
	public abstract void deleteCompleteOkListener(final ActionEvent e);
	
	public abstract void deleteCompleteFailListener(final ActionEvent e); 
	

	public Map<String, UploadItem> getMapElementiRicevuti() {
		return this.mapElementiRicevuti;
	}

	public void setMapElementiRicevuti(Map<String, UploadItem> mapElementiRicevuti) {
		this.mapElementiRicevuti = mapElementiRicevuti;
	}

	public Map<String, String> getMapChiaviElementi() {
		return this.mapChiaviElementi;
	}

	public void setMapChiaviElementi(Map<String, String> mapChiaviElementi) {
		this.mapChiaviElementi = mapChiaviElementi;
	}
	
	public boolean isVisualizzaComandiEliminaFile() {
		return !this.getMapElementiRicevuti().isEmpty();
	}

	public void setVisualizzaComandiEliminaFile(boolean visualizzaComandiEliminaFile) {
	}
	
	public abstract MediaType[] getAcceptedTypes(); 
	
	/** Nome del parametro con cui il client segnala il motivo dell'errore di caricamento */
	public static final String PARAMETRO_MESSAGGIO_ERRORE_UPLOAD = "param1";
	
	/**
	 * Motivo dell'errore di caricamento restituito dalla servlet di upload (es. dimensione massima superata).
	 * Viene letto direttamente dalla richiesta poiché l'actionListener della funzione JavaScript viene eseguito
	 * prima dell'assegnazione dei parametri (a4j:actionparam).
	 */
	protected String leggiUploadErrorMessage() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if(fc == null) {
			return null;
		}
		String v = fc.getExternalContext().getRequestParameterMap().get(PARAMETRO_MESSAGGIO_ERRORE_UPLOAD);
		return (v != null && !v.trim().isEmpty()) ? v.trim() : null;
	}
	
	/** Destinazione dell'a4j:actionparam con il motivo dell'errore; il valore viene letto dalla richiesta tramite leggiUploadErrorMessage() */
	private String uploadErrorMessage = null;
	public String getUploadErrorMessage() {
		return this.uploadErrorMessage;
	}
	public void setUploadErrorMessage(String uploadErrorMessage) {
		this.uploadErrorMessage = uploadErrorMessage;
	}
}