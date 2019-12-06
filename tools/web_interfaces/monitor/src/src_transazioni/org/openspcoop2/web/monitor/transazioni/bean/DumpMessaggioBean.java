package org.openspcoop2.web.monitor.transazioni.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;

public class DumpMessaggioBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date dataConsegnaErogatore = null;
	private String idTransazione;
	private String servizioApplicativoErogatore;
	private List<TipoMessaggio> tipiMessaggio = new ArrayList<>();
	
	public Long getId() {
		if(this.dataConsegnaErogatore != null)
			return this.dataConsegnaErogatore.getTime();
		
		return 0L;
	}
	public void setId(Long id) {}
	
	public Date getDataConsegnaErogatore() {
		return this.dataConsegnaErogatore;
	}
	public void setDataConsegnaErogatore(Date dataConsegnaErogatore) {
		this.dataConsegnaErogatore = dataConsegnaErogatore;
	}
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	public String getServizioApplicativoErogatore() {
		return this.servizioApplicativoErogatore;
	}
	public void setServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.servizioApplicativoErogatore = servizioApplicativoErogatore;
	}
	public List<TipoMessaggio> getTipiMessaggio() {
		return this.tipiMessaggio;
	}
	
	public Boolean getHasDumpRichiestaUscita() {
		return this.tipiMessaggio.contains(TipoMessaggio.RICHIESTA_USCITA); 
	}
	
	public Boolean getHasDumpRispostaIngresso() {
		return this.tipiMessaggio.contains(TipoMessaggio.RISPOSTA_INGRESSO); 
	}
	
 	public boolean getHasDumpBinarioRichiestaUscita() {
 		return this.tipiMessaggio.contains(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO); 
	}

	public boolean getHasDumpBinarioRispostaIngresso() {
		return this.tipiMessaggio.contains(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO); 
	}
}
