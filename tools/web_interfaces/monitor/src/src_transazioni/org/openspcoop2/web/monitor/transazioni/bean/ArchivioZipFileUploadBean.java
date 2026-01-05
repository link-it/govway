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
package org.openspcoop2.web.monitor.transazioni.bean;

import javax.faces.event.ActionEvent;

import org.openspcoop2.web.monitor.core.bean.BaseFileUploadBean;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;
import org.springframework.http.MediaType;

/****
 * 
 * ArchivioZipFileUploadBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ArchivioZipFileUploadBean extends BaseFileUploadBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	private TransazioniSearchForm search = null; 
	
	public ArchivioZipFileUploadBean() throws Exception {
		super();
	}
	
	@Override
	public void clear() {
		super.clear();
		this.search.clearIdFiles();
	}
	
	public TransazioniSearchForm getSearch() {
		return this.search;
	}

	public void setSearch(TransazioniSearchForm search) {
		this.search = search;
	}

	@Override
	public final void startUploadsListener(final ActionEvent e) {
		log.debug("Start Upload");
		
		// i file che vengono caricati sostituiscono quelli esistenti
		this.clear();
	}
	
	@Override
	public final void stopUploadsListener(final ActionEvent evt) {
		log.debug("Stop Upload Ids: {}", this.getMapChiaviElementi().keySet());
	}
	
	@Override
	public final void uploadErrorListener(final ActionEvent e) {
		//donothing
	}

	@Override
	public final void deleteCompleteOkListener(final ActionEvent e) {
		//donothing
	}
	
	@Override
	public final void deleteCompleteFailListener(final ActionEvent e) {
		//donothing
	}
	
	@Override
	public MediaType[] getAcceptedTypes() {
		return new MediaType[]{MediaType.APPLICATION_OCTET_STREAM, MediaType.parseMediaType("application/zip") };
	}
}