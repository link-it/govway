/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.engine.archive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;

/**
 *  ImportInformationMissing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImportInformationMissing implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto soggetto;
	private Integer versione;
	private List<PortType> portTypes = new ArrayList<PortType>();
	private String portTypeImplemented;
	private IDAccordo idAccordoServizioParteComune;
	private IDAccordoCooperazione idAccordoCooperazione;
	private InvocazioneServizio invocazioneServizio;
	private Credenziali credenziali;
	private Connettore connettore;
	private MapPlaceholder inputPlaceholder;
	
	public IDSoggetto getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(IDSoggetto soggetto) {
		this.soggetto = soggetto;
	}
	
	public Integer getVersione() {
		return this.versione;
	}
	public void setVersione(Integer versione) {
		this.versione = versione;
	}
	public String getPortTypeImplemented() {
		return this.portTypeImplemented;
	}
	public void setPortTypeImplemented(String portTypeImplemented) {
		this.portTypeImplemented = portTypeImplemented;
	}
	
	public List<PortType> getPortTypes() {
		return this.portTypes;
	}
	public void setPortTypes(List<PortType> portTypes) {
		this.portTypes = portTypes;
	}
	public void addPortType(PortType pt) throws ProtocolException{
		if(this.getPortType(pt.getNome())!=null){
			throw new ProtocolException("PortType with name ["+pt.getNome()+"] already exists");
		}
		this.portTypes.add(pt);
	}
	public PortType getPortType(String name){
		for (int i = 0; i < this.portTypes.size(); i++) {
			if(this.portTypes.get(i).getNome().equals(name)){
				return this.portTypes.get(i);
			}
		}
		return null;
	}
	public PortType getPortType(int index){
		return this.portTypes.get(index);
	}
	public PortType removePortType(String name){
		for (int i = 0; i < this.portTypes.size(); i++) {
			if(this.portTypes.get(i).getNome().equals(name)){
				return this.portTypes.remove(i);
			}
		}
		return null;
	}
	public PortType removePortType(int index){
		return this.portTypes.remove(index);
	}
	public boolean existsPortType(String name){
		for (int i = 0; i < this.portTypes.size(); i++) {
			if(this.portTypes.get(i).getNome().equals(name)){
				return true;
			}
		}
		return false;
	}
	public int sizePortTypeList(){
		return this.portTypes.size();
	}
	
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public void setIdAccordoServizioParteComune(
			IDAccordo idAccordoServizioParteComune) {
		this.idAccordoServizioParteComune = idAccordoServizioParteComune;
	}
	public IDAccordoCooperazione getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}
	public void setIdAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) {
		this.idAccordoCooperazione = idAccordoCooperazione;
	}
	public InvocazioneServizio getInvocazioneServizio() {
		return this.invocazioneServizio;
	}
	public void setInvocazioneServizio(InvocazioneServizio invocazioneServizio) {
		this.invocazioneServizio = invocazioneServizio;
	}
	public Connettore getConnettore() {
		return this.connettore;
	}
	public void setConnettore(Connettore connettore) {
		this.connettore = connettore;
	}
	public Credenziali getCredenziali() {
		return this.credenziali;
	}
	public void setCredenziali(Credenziali credenziali) {
		this.credenziali = credenziali;
	}
	public MapPlaceholder getInputPlaceholder() {
		return this.inputPlaceholder;
	}
	public void setInputPlaceholder(MapPlaceholder inputPlaceholder) {
		this.inputPlaceholder = inputPlaceholder;
	}
}
