/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.Date;

/**
 * ExporterProperties
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ExporterProperties {
	private boolean exportTracce;
	private boolean exportDiagnostici;
	private boolean exportContenuti;
	private boolean enableHeaderInfo;
	private boolean enableConsegneInfo;
	private boolean mimeThrowExceptionIfNotFound;
	private boolean abilitaMarcamentoTemporaleEsportazione;
	private boolean headersAsProperties = true;
	private boolean contenutiAsProperties = false;
	private boolean useCount = true;
	
	private Date dataInizio;
	private Date dataFine;
	
	public boolean isExportTracce() {
		return this.exportTracce;
	}
	public boolean isExportDiagnostici() {
		return this.exportDiagnostici;
	}
	public boolean isExportContenuti() {
		return this.exportContenuti;
	}
	public boolean isEnableHeaderInfo() {
		return this.enableHeaderInfo;
	}
	public boolean isEnableConsegneInfo() {
		return this.enableConsegneInfo;
	}
	public boolean isMimeThrowExceptionIfNotFound() {
		return this.mimeThrowExceptionIfNotFound;
	}
	public void setExportTracce(boolean exportTracce) {
		this.exportTracce = exportTracce;
	}
	public void setExportDiagnostici(boolean exportDiagnostici) {
		this.exportDiagnostici = exportDiagnostici;
	}
	public void setExportContenuti(boolean exportContenuti) {
		this.exportContenuti = exportContenuti;
	}
	public void setEnableHeaderInfo(boolean enableHeaderInfo) {
		this.enableHeaderInfo = enableHeaderInfo;
	}
	public void setEnableConsegneInfo(boolean isEnableConsegneInfo) {
		this.enableConsegneInfo = isEnableConsegneInfo;
	}
	public void setMimeThrowExceptionIfNotFound(boolean mimeThrowExceptionIfNotFound) {
		this.mimeThrowExceptionIfNotFound = mimeThrowExceptionIfNotFound;
	}
	public boolean isAbilitaMarcamentoTemporaleEsportazione() {
		return this.abilitaMarcamentoTemporaleEsportazione;
	}
	public void setAbilitaMarcamentoTemporaleEsportazione(
			boolean abilitaMarcamentoTemporaleEsportazione) {
		this.abilitaMarcamentoTemporaleEsportazione = abilitaMarcamentoTemporaleEsportazione;
	}
	public Date getDataInizio() {
		return this.dataInizio;
	}
	public Date getDataFine() {
		return this.dataFine;
	}
	public void setDataInizio(Date dataInizio) {
		this.dataInizio = dataInizio;
	}
	public void setDataFine(Date dataFine) {
		this.dataFine = dataFine;
	}
	public boolean isHeadersAsProperties() {
		return this.headersAsProperties;
	}
	public void setHeadersAsProperties(boolean headersAsProperties) {
		this.headersAsProperties = headersAsProperties;
	}
	public boolean isContenutiAsProperties() {
		return this.contenutiAsProperties;
	}
	public void setContenutiAsProperties(boolean contenutiAsProperties) {
		this.contenutiAsProperties = contenutiAsProperties;
	}
	public boolean isUseCount() {
		return this.useCount;
	}
	public void setUseCount(boolean useCount) {
		this.useCount = useCount;
	}
}
