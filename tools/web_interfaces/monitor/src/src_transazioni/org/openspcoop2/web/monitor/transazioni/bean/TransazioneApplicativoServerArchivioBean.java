package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;

public class TransazioneApplicativoServerArchivioBean {

	private String nomeSA;
	private TransazioneApplicativoServerBean transazioneApplicativoServerBean;
	
	private byte[] diagnosticiRaw;
	private List<MsgDiagnostico> diagnostici;
	private Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti;
	
	public TransazioneApplicativoServerArchivioBean(String nomeSA) {
		this.nomeSA = nomeSA;
		this.contenuti = new HashMap<>();
		this.diagnostici = new ArrayList<>();
	}

	public String getNomeSA() {
		return this.nomeSA;
	}
	public TransazioneApplicativoServerBean getTransazioneApplicativoServerBean() {
		return this.transazioneApplicativoServerBean;
	}
	public void setTransazioneApplicativoServerBean(TransazioneApplicativoServerBean transazioneApplicativoServerBean) {
		this.transazioneApplicativoServerBean = transazioneApplicativoServerBean;
	}
	public List<MsgDiagnostico> getDiagnostici() {
		return this.diagnostici;
	}
	public Map<TipoMessaggio, ContenutiTransazioneArchivioBean> getContenuti() {
		return this.contenuti;
	}
	public void setContenuti(Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti) {
		this.contenuti = contenuti;
	}
	public byte[] getDiagnosticiRaw() {
		return this.diagnosticiRaw;
	}
	public void setDiagnosticiRaw(byte[] diagnosticiRaw) {
		this.diagnosticiRaw = diagnosticiRaw;
	}
}
