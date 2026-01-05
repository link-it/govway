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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.web.monitor.transazioni.mbean.TracciaBean;

/**
 * TransazioneArchivioBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class TransazioneArchivioBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String idTransazione;
	private TransazioneBean transazioneBean;
	
	private List<TracciaBean> tracce;
	private List<MsgDiagnostico> diagnostici;
	
	private byte[] tracceRaw;
	private byte[] diagnosticiRaw;
	
	private transient Map<String,TransazioneApplicativoServerArchivioBean> consegne;
	private transient Map<TipoMessaggio, ContenutiTransazioneArchivioBean> contenuti;
	
	public TransazioneArchivioBean(String idTransazione) {
		this.idTransazione = idTransazione;
		this.contenuti = new HashMap<>();
		this.tracce = new ArrayList<>();
		this.consegne = new HashMap<>();
		this.diagnostici = new ArrayList<>();
	}
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public TransazioneBean getTransazioneBean() {
		return this.transazioneBean;
	}
	public void setTransazioneBean(TransazioneBean transazioneBean) {
		this.transazioneBean = transazioneBean;
	}
	public List<TracciaBean> getTracce() {
		return this.tracce;
	}
	public TracciaBean getTraccia(RuoloMessaggio ruolo) {
		if(this.tracce == null || this.tracce.isEmpty()) {
			return null;
		}
		
		for (TracciaBean traccia : this.tracce) {
			if(ruolo.equals(traccia.getTipoMessaggio())){
				return traccia;
			}
		}
		
		return null;
	}
	
	public List<MsgDiagnostico> getDiagnostici() {
		return this.diagnostici;
	}
	public Map<String,TransazioneApplicativoServerArchivioBean> getConsegne() {
		return this.consegne;
	}
	public Map<TipoMessaggio, ContenutiTransazioneArchivioBean> getContenuti() {
		return this.contenuti;
	}
	public byte[] getTracceRaw() {
		return this.tracceRaw;
	}
	public void setTracceRaw(byte[] tracceRaw) {
		this.tracceRaw = tracceRaw;
	}
	public byte[] getDiagnosticiRaw() {
		return this.diagnosticiRaw;
	}
	public void setDiagnosticiRaw(byte[] diagnosticiRaw) {
		this.diagnosticiRaw = diagnosticiRaw;
	}
	
}