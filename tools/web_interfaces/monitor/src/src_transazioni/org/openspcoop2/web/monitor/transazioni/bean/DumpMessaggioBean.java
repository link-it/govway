/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;

/**     
 * DumpMessaggioBean 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpMessaggioBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date dataConsegnaErogatore = null;
	private String idTransazione;
	private String servizioApplicativoErogatore;
	private String protocollo;	
	private List<TipoMessaggio> tipiMessaggio = new ArrayList<>();
	
	public Long getId() {
		if(this.dataConsegnaErogatore != null)
			return this.dataConsegnaErogatore.getTime();
		
		return 0L;
	}
	public void setId(Long id) {}
	
	
	public String getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
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
