/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.protocol.sdk.constants.ArchiveStatoImport;

/**
 *  ArchiveEsitoImportDetailConfigurazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveEsitoImportDetailConfigurazione<T> {

	private T archiveObject;
	private ArchiveStatoImport state;
	private String stateDetail;
	private Throwable exception;
	
	public ArchiveEsitoImportDetailConfigurazione(T archiveObject){
		this.archiveObject = archiveObject;
	}
	
	public T getArchiveObject() {
		return this.archiveObject;
	}
	public void setArchiveObject(T archiveObject) {
		this.archiveObject = archiveObject;
	}
	public ArchiveStatoImport getState() {
		return this.state;
	}
	public void setState(ArchiveStatoImport state) {
		this.state = state;
	}
	public Throwable getException() {
		return this.exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	public String getStateDetail() {
		return this.stateDetail;
	}
	public void setStateDetail(String stateDetail) {
		this.stateDetail = stateDetail;
	}
	
}
