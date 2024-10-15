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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.mbean.RicercheUtenteBean;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.springframework.http.MediaType;

/****
 * 
 * FileUploadBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FileUploadBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	private Map<String, UploadItem> mapElementiRicevuti = null;
	private Map<String, String> mapChiaviElementi = null;
	
	private MediaType [] acceptedTypes = {MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN };
	private int numeroFile = 1;
	
	private RicercheUtenteBean mBean =null; 
	
	public FileUploadBean() throws Exception {
		this.mapChiaviElementi = new HashMap<>();
		this.mapElementiRicevuti = new HashMap<>();
	}
	
	public void setNumeroFile(int numeroFile) {
		this.numeroFile = numeroFile;
	}
	
	public final void clear() {
		this.mapChiaviElementi.clear();
		this.mapElementiRicevuti.clear();
		this.mBean.clearIdFiles();
	}
	
	public int getNumeroFile(){
		return this.numeroFile - this.mapElementiRicevuti.size();
	}
	
	public boolean checkAcceptedType(String contentType) {
		if(contentType == null) {
			return false;
		}
		
		MediaType toCheck = MediaType.parseMediaType(contentType);
		
		if(toCheck != null) {
			return Arrays.asList(this.acceptedTypes).contains(toCheck);
		}
		
		return false;
	}
	
	public RicercheUtenteBean getmBean() {
		return this.mBean;
	}

	public void setmBean(RicercheUtenteBean mBean) {
		this.mBean = mBean;
	}

	public final void startUploadsListener(final ActionEvent e) {
		log.debug("Start Upload");
		
		// i file che vengono caricati sostituiscono quelli esistenti
		this.clear();
		this.mBean.setCaricaRicercheErrorMessage(null); 
	}
	
	public final void stopUploadsListener(final ActionEvent evt) {
		log.debug("Stop Upload Ids: {}", this.mapChiaviElementi.keySet());
		
		this.mBean.setCaricaRicercheErrorMessage(null); 
	}
	
	public final void uploadErrorListener(final ActionEvent e) {
		//this.form.enableButton();
		
		this.mBean.setCaricaRicercheErrorMessage("Import del file ricerche completato con errore."); 
	}
	
	public final void deleteCompleteOkListener(final ActionEvent e) {
		this.mBean.setCaricaRicercheErrorMessage(null); 
	}
	
	public final void deleteCompleteFailListener(final ActionEvent e) {
		this.mBean.setCaricaRicercheErrorMessage(null); 
	}

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
}