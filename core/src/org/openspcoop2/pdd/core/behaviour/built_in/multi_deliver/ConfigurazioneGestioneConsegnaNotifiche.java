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
package org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver;

import java.util.ArrayList;
import java.util.List;

/**
 * ConfigurazioneGestoneErrori
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneGestioneConsegnaNotifiche  {
	
	private Integer cadenzaRispedizione;
	
	private TipoGestioneNotificaTrasporto gestioneTrasporto2xx;
	private List<Integer> gestioneTrasporto2xx_codes = new ArrayList<Integer>();
	private Integer gestioneTrasporto2xx_leftInterval;
	private Integer gestioneTrasporto2xx_rightInterval;
	
	private TipoGestioneNotificaTrasporto gestioneTrasporto3xx;
	private List<Integer> gestioneTrasporto3xx_codes = new ArrayList<Integer>();
	private Integer gestioneTrasporto3xx_leftInterval;
	private Integer gestioneTrasporto3xx_rightInterval;
	
	private TipoGestioneNotificaTrasporto gestioneTrasporto4xx;
	private List<Integer> gestioneTrasporto4xx_codes = new ArrayList<Integer>();
	private Integer gestioneTrasporto4xx_leftInterval;
	private Integer gestioneTrasporto4xx_rightInterval;
	
	private TipoGestioneNotificaTrasporto gestioneTrasporto5xx;
	private List<Integer> gestioneTrasporto5xx_codes = new ArrayList<Integer>();
	private Integer gestioneTrasporto5xx_leftInterval;
	private Integer gestioneTrasporto5xx_rightInterval;
	
	private TipoGestioneNotificaFault fault;
	private String faultCode;
	private String faultActor;
	private String faultMessage;
	

	public Integer getCadenzaRispedizione() {
		return this.cadenzaRispedizione;
	}
	public void setCadenzaRispedizione(Integer cadenzaRispedizione) {
		this.cadenzaRispedizione = cadenzaRispedizione;
	}
	public TipoGestioneNotificaTrasporto getGestioneTrasporto2xx() {
		return this.gestioneTrasporto2xx;
	}
	public void setGestioneTrasporto2xx(TipoGestioneNotificaTrasporto gestioneTrasporto2xx) {
		this.gestioneTrasporto2xx = gestioneTrasporto2xx;
	}
	public List<Integer> getGestioneTrasporto2xx_codes() {
		return this.gestioneTrasporto2xx_codes;
	}
	public void setGestioneTrasporto2xx_codes(List<Integer> gestioneTrasporto2xx_codes) {
		this.gestioneTrasporto2xx_codes = gestioneTrasporto2xx_codes;
	}
	public Integer getGestioneTrasporto2xx_leftInterval() {
		return this.gestioneTrasporto2xx_leftInterval;
	}
	public void setGestioneTrasporto2xx_leftInterval(Integer gestioneTrasporto2xx_leftInterval) {
		this.gestioneTrasporto2xx_leftInterval = gestioneTrasporto2xx_leftInterval;
	}
	public Integer getGestioneTrasporto2xx_rightInterval() {
		return this.gestioneTrasporto2xx_rightInterval;
	}
	public void setGestioneTrasporto2xx_rightInterval(Integer gestioneTrasporto2xx_rightInterval) {
		this.gestioneTrasporto2xx_rightInterval = gestioneTrasporto2xx_rightInterval;
	}
	public TipoGestioneNotificaTrasporto getGestioneTrasporto3xx() {
		return this.gestioneTrasporto3xx;
	}
	public void setGestioneTrasporto3xx(TipoGestioneNotificaTrasporto gestioneTrasporto3xx) {
		this.gestioneTrasporto3xx = gestioneTrasporto3xx;
	}
	public List<Integer> getGestioneTrasporto3xx_codes() {
		return this.gestioneTrasporto3xx_codes;
	}
	public void setGestioneTrasporto3xx_codes(List<Integer> gestioneTrasporto3xx_codes) {
		this.gestioneTrasporto3xx_codes = gestioneTrasporto3xx_codes;
	}
	public Integer getGestioneTrasporto3xx_leftInterval() {
		return this.gestioneTrasporto3xx_leftInterval;
	}
	public void setGestioneTrasporto3xx_leftInterval(Integer gestioneTrasporto3xx_leftInterval) {
		this.gestioneTrasporto3xx_leftInterval = gestioneTrasporto3xx_leftInterval;
	}
	public Integer getGestioneTrasporto3xx_rightInterval() {
		return this.gestioneTrasporto3xx_rightInterval;
	}
	public void setGestioneTrasporto3xx_rightInterval(Integer gestioneTrasporto3xx_rightInterval) {
		this.gestioneTrasporto3xx_rightInterval = gestioneTrasporto3xx_rightInterval;
	}
	public TipoGestioneNotificaTrasporto getGestioneTrasporto4xx() {
		return this.gestioneTrasporto4xx;
	}
	public void setGestioneTrasporto4xx(TipoGestioneNotificaTrasporto gestioneTrasporto4xx) {
		this.gestioneTrasporto4xx = gestioneTrasporto4xx;
	}
	public List<Integer> getGestioneTrasporto4xx_codes() {
		return this.gestioneTrasporto4xx_codes;
	}
	public void setGestioneTrasporto4xx_codes(List<Integer> gestioneTrasporto4xx_codes) {
		this.gestioneTrasporto4xx_codes = gestioneTrasporto4xx_codes;
	}
	public Integer getGestioneTrasporto4xx_leftInterval() {
		return this.gestioneTrasporto4xx_leftInterval;
	}
	public void setGestioneTrasporto4xx_leftInterval(Integer gestioneTrasporto4xx_leftInterval) {
		this.gestioneTrasporto4xx_leftInterval = gestioneTrasporto4xx_leftInterval;
	}
	public Integer getGestioneTrasporto4xx_rightInterval() {
		return this.gestioneTrasporto4xx_rightInterval;
	}
	public void setGestioneTrasporto4xx_rightInterval(Integer gestioneTrasporto4xx_rightInterval) {
		this.gestioneTrasporto4xx_rightInterval = gestioneTrasporto4xx_rightInterval;
	}
	public TipoGestioneNotificaTrasporto getGestioneTrasporto5xx() {
		return this.gestioneTrasporto5xx;
	}
	public void setGestioneTrasporto5xx(TipoGestioneNotificaTrasporto gestioneTrasporto5xx) {
		this.gestioneTrasporto5xx = gestioneTrasporto5xx;
	}
	public List<Integer> getGestioneTrasporto5xx_codes() {
		return this.gestioneTrasporto5xx_codes;
	}
	public void setGestioneTrasporto5xx_codes(List<Integer> gestioneTrasporto5xx_codes) {
		this.gestioneTrasporto5xx_codes = gestioneTrasporto5xx_codes;
	}
	public Integer getGestioneTrasporto5xx_leftInterval() {
		return this.gestioneTrasporto5xx_leftInterval;
	}
	public void setGestioneTrasporto5xx_leftInterval(Integer gestioneTrasporto5xx_leftInterval) {
		this.gestioneTrasporto5xx_leftInterval = gestioneTrasporto5xx_leftInterval;
	}
	public Integer getGestioneTrasporto5xx_rightInterval() {
		return this.gestioneTrasporto5xx_rightInterval;
	}
	public void setGestioneTrasporto5xx_rightInterval(Integer gestioneTrasporto5xx_rightInterval) {
		this.gestioneTrasporto5xx_rightInterval = gestioneTrasporto5xx_rightInterval;
	}
	public TipoGestioneNotificaFault getFault() {
		return this.fault;
	}
	public void setFault(TipoGestioneNotificaFault fault) {
		this.fault = fault;
	}
	public String getFaultCode() {
		return this.faultCode;
	}
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	public String getFaultActor() {
		return this.faultActor;
	}
	public void setFaultActor(String faultActor) {
		this.faultActor = faultActor;
	}
	public String getFaultMessage() {
		return this.faultMessage;
	}
	public void setFaultMessage(String faultMessage) {
		this.faultMessage = faultMessage;
	}
}
