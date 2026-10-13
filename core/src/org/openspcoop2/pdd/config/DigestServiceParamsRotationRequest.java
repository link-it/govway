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
package org.openspcoop2.pdd.config;

import java.io.Serializable;
import java.util.Date;

/**
 * Richiesta di rotazione forzata delle informazioni crittografiche (seme) di un servizio.
 * Oggetto utilizzato dalla console per effettuare l'operazione e registrarla nell'audit: non contiene il seme.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DigestServiceParamsRotationRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long idServizioRef;
	private String servizio;
	private long idSeme;
	private Long signalId;
	private Date dataGenerazioneSeme;
	private Date dataRichiesta;
	
	public long getIdServizioRef() {
		return this.idServizioRef;
	}
	public void setIdServizioRef(long idServizioRef) {
		this.idServizioRef = idServizioRef;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public long getIdSeme() {
		return this.idSeme;
	}
	public void setIdSeme(long idSeme) {
		this.idSeme = idSeme;
	}
	public Long getSignalId() {
		return this.signalId;
	}
	public void setSignalId(Long signalId) {
		this.signalId = signalId;
	}
	public Date getDataGenerazioneSeme() {
		return this.dataGenerazioneSeme;
	}
	public void setDataGenerazioneSeme(Date dataGenerazioneSeme) {
		this.dataGenerazioneSeme = dataGenerazioneSeme;
	}
	public Date getDataRichiesta() {
		return this.dataRichiesta;
	}
	public void setDataRichiesta(Date dataRichiesta) {
		this.dataRichiesta = dataRichiesta;
	}
	
}
