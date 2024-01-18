/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config;

import java.io.Serializable;

import org.openspcoop2.core.plugins.ConfigurazioneServizio;
import org.openspcoop2.core.plugins.ConfigurazioneServizioAzione;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;

/**
 * BasicServiceLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicServiceLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private IDAccordo idAccordoServizioParteComune;
	private String portType;
	private String azione;
	
	private IDServizio idServizio;
	private org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica accordoServizioParteSpecifica;	
	
	private ConfigurazioneServizio serviceLibrary;
	private ConfigurazioneServizioAzione serviceActionLibrary;
	private ConfigurazioneServizioAzione serviceActionAllLibrary; // per azione '*'
	
	public String getPortType() {
		return this.portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public void setIdAccordoServizioParteComune(
			IDAccordo idAccordoServizioParteComune) {
		this.idAccordoServizioParteComune = idAccordoServizioParteComune;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica() {
		return this.accordoServizioParteSpecifica;
	}
	public void setAccordoServizioParteSpecifica(
			org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
		this.accordoServizioParteSpecifica = accordoServizioParteSpecifica;
	}
	public ConfigurazioneServizio getServiceLibrary() {
		return this.serviceLibrary;
	}
	public void setServiceLibrary(ConfigurazioneServizio serviceLibrary) {
		this.serviceLibrary = serviceLibrary;
	}
	public ConfigurazioneServizioAzione getServiceActionLibrary() {
		return this.serviceActionLibrary;
	}
	public void setServiceActionLibrary(
			ConfigurazioneServizioAzione serviceActionLibrary) {
		this.serviceActionLibrary = serviceActionLibrary;
	}
	public ConfigurazioneServizioAzione getServiceActionAllLibrary() {
		return this.serviceActionAllLibrary;
	}
	public void setServiceActionAllLibrary(
			ConfigurazioneServizioAzione serviceActionAllLibrary) {
		this.serviceActionAllLibrary = serviceActionAllLibrary;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();

		bf.append("IdAccordo: ");
		if(this.getIdAccordoServizioParteComune()!=null){
			bf.append(this.getIdAccordoServizioParteComune().toString());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("PortType: ");
		if(this.getPortType()!=null){
			bf.append(this.getPortType());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("Azione: ");
		if(this.getAzione()!=null){
			bf.append(this.getAzione());
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("IdServizio: ");
		if(this.getIdServizio()!=null){
			bf.append(this.getIdServizio().toString());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("AccordoServizioParteSpecifica: ");
		if(this.getAccordoServizioParteSpecifica()!=null){
			bf.append("presente");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ServiceLibrary: ");
		if(this.getServiceLibrary()!=null){
			bf.append("presente");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ActionLibrary: ");
		if(this.getServiceActionLibrary()!=null){
			bf.append("azione["+this.getServiceActionLibrary().getAzione()+"]");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ActionAllLibrary: ");
		if(this.getServiceActionAllLibrary()!=null){
			bf.append("azione["+this.getServiceActionAllLibrary().getAzione()+"]");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		return bf.toString();
	}
}
