/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.statistic;

import java.util.Date;

/**
 * IStatistic
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticData {

	private Date data;
	private String tipoMittente;
	private String mittente;
	private String tipoDestinatario;
	private String destinatario;
	private String tipoServizio;
	private String servizio;
	private Integer versioneServizio;
	private String azione;
	private String servizioApplicativo;
	private String tipoPorta;
	private String idPorta;
	private int esito;
	private String value;
	private long number;
	private long msg_bytes;
	
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getNumber() {
		return this.number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
	public long getMsg_bytes() {
		return this.msg_bytes;
	}
	public void setMsg_bytes(long msg_bytes) {
		this.msg_bytes = msg_bytes;
	}
	public Date getData() {
		return this.data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getTipoMittente() {
		return this.tipoMittente;
	}
	public void setTipoMittente(String tipoMittente) {
		this.tipoMittente = tipoMittente;
	}
	public String getMittente() {
		return this.mittente;
	}
	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	public String getTipoDestinatario() {
		return this.tipoDestinatario;
	}
	public void setTipoDestinatario(String tipoDestinatario) {
		this.tipoDestinatario = tipoDestinatario;
	}
	public String getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getTipoServizio() {
		return this.tipoServizio;
	}
	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}
	public String getServizio() {
		return this.servizio;
	}
	public void setServizio(String servizio) {
		this.servizio = servizio;
	}
	public Integer getVersioneServizio() {
		return this.versioneServizio;
	}
	public void setVersioneServizio(Integer versioneServizio) {
		this.versioneServizio = versioneServizio;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	public String getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(String tipoPorta) {
		this.tipoPorta = tipoPorta;
	}
	public String getIdPorta() {
		return this.idPorta;
	}
	public void setIdPorta(String idPorta) {
		this.idPorta = idPorta;
	}
	public int getEsito() {
		return this.esito;
	}
	public void setEsito(int esito) {
		this.esito = esito;
	}
}
