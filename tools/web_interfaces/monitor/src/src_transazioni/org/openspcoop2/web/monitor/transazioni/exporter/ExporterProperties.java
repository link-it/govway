package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.Date;

public class ExporterProperties {
	private boolean exportTracce;
	private boolean exportDiagnostici;
	private boolean exportContenuti;
	private boolean enableHeaderInfo;
	private boolean mimeThrowExceptionIfNotFound;
	private boolean abilitaMarcamentoTemporaleEsportazione;
	
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
}
