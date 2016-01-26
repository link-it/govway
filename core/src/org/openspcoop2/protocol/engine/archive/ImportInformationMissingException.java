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
import java.util.List;

import org.openspcoop2.protocol.information_missing.Input;

/**
 *  ImportInformationMissingException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImportInformationMissingException extends Exception implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String idObject;
	private String idObjectDescription;

	private Object object;
	private Class<?> classObject;
	
	private boolean missingInfoSoggetto;
	private String missingInfoSoggetto_protocollo;
	private String missingInfoSoggetto_tipoPdD;
	
	private boolean missingInfoVersione;
	
	private boolean missingInfoProfiliServizi;
	
	private boolean mismatchPortTypeRifServiziParteComune;
	private String mismatchPortTypeRifServiziParteComune_nome;
	private List<String> mismatchPortTypeRifServiziParteComune_serviziParteComune;
	
	private boolean missingInfoAccordoServizioParteComune;
		
	private boolean missingInfoAccordoCooperazione;
	
	private boolean missingInfoInvocazioneServizio;
	
	private boolean missingInfoCredenziali;
	
	private boolean missingInfoConnettore;
	
	private boolean missingInfoInput;
	private Input missingInfoInputObject;
	
	public ImportInformationMissingException(String objectId,String idObjectDescription){
		this.idObject = objectId;
		this.idObjectDescription = idObjectDescription;
	}
	public ImportInformationMissingException(String objectId,String idObjectDescription,String message){
		super(message);
		this.idObject = objectId;
		this.idObjectDescription = idObjectDescription;
	}
	public ImportInformationMissingException(String objectId,String idObjectDescription,String message,Throwable e){
		super(message,e);
		this.idObject = objectId;
		this.idObjectDescription = idObjectDescription;
	}
	public ImportInformationMissingException(String objectId,String idObjectDescription,Throwable e){
		super(e);
		this.idObject = objectId;
		this.idObjectDescription = idObjectDescription;
	}
	
	public String getIdObject() {
		return this.idObject;
	}
	public String getIdObjectDescription() {
		return this.idObjectDescription;
	}
	
	public Object getObject() {
		return this.object;
	}
	public Class<?> getClassObject() {
		return this.classObject;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public void setClassObject(Class<?> classObject) {
		this.classObject = classObject;
	}
	
	public boolean isMissingInfoSoggetto() {
		return this.missingInfoSoggetto;
	}
	public void setMissingInfoSoggetto(boolean missingInfoSoggetto) {
		this.missingInfoSoggetto = missingInfoSoggetto;
	}
	
	public String getMissingInfoSoggetto_protocollo() {
		return this.missingInfoSoggetto_protocollo;
	}
	public void setMissingInfoSoggetto_protocollo(
			String missingInfoSoggetto_protocollo) {
		this.missingInfoSoggetto_protocollo = missingInfoSoggetto_protocollo;
	}
	
	public String getMissingInfoSoggetto_tipoPdD() {
		return this.missingInfoSoggetto_tipoPdD;
	}
	public void setMissingInfoSoggetto_tipoPdD(String missingInfoSoggetto_tipoPdD) {
		this.missingInfoSoggetto_tipoPdD = missingInfoSoggetto_tipoPdD;
	}
	
	public boolean isMissingInfoVersione() {
		return this.missingInfoVersione;
	}
	public void setMissingInfoVersione(boolean missingInfoVersione) {
		this.missingInfoVersione = missingInfoVersione;
	}
	
	public boolean isMissingInfoProfiliServizi() {
		return this.missingInfoProfiliServizi;
	}
	public void setMissingInfoProfiliServizi(boolean missingInfoProfiliServizi) {
		this.missingInfoProfiliServizi = missingInfoProfiliServizi;
	}
	
	public void setMismatchPortTypeRifServiziParteComune(
			boolean mismatchPortTypeRifServiziParteComune,
			String mismatchPortTypeRifServiziParteComune_nome,
			List<String> mismatchPortTypeRifServiziParteComune_serviziParteComune) {
		this.mismatchPortTypeRifServiziParteComune = mismatchPortTypeRifServiziParteComune;
		this.mismatchPortTypeRifServiziParteComune_nome = mismatchPortTypeRifServiziParteComune_nome;
		this.mismatchPortTypeRifServiziParteComune_serviziParteComune = mismatchPortTypeRifServiziParteComune_serviziParteComune;
	}
	public boolean isMismatchPortTypeRifServiziParteComune() {
		return this.mismatchPortTypeRifServiziParteComune;
	}
	public String getMismatchPortTypeRifServiziParteComune_nome() {
		return this.mismatchPortTypeRifServiziParteComune_nome;
	}
	public List<String> getMismatchPortTypeRifServiziParteComune_serviziParteComune() {
		return this.mismatchPortTypeRifServiziParteComune_serviziParteComune;
	}
	
	public boolean isMissingInfoAccordoServizioParteComune() {
		return this.missingInfoAccordoServizioParteComune;
	}
	public void setMissingInfoAccordoServizioParteComune(
			boolean missingInfoAccordoServizioParteComune) {
		this.missingInfoAccordoServizioParteComune = missingInfoAccordoServizioParteComune;
	}
	public boolean isMissingInfoAccordoCooperazione() {
		return this.missingInfoAccordoCooperazione;
	}
	public void setMissingInfoAccordoCooperazione(
			boolean missingInfoAccordoCooperazione) {
		this.missingInfoAccordoCooperazione = missingInfoAccordoCooperazione;
	}
	public boolean isMissingInfoInvocazioneServizio() {
		return this.missingInfoInvocazioneServizio;
	}
	public void setMissingInfoInvocazioneServizio(
			boolean missingInfoInvocazioneServizio) {
		this.missingInfoInvocazioneServizio = missingInfoInvocazioneServizio;
	}
	public boolean isMissingInfoConnettore() {
		return this.missingInfoConnettore;
	}
	public void setMissingInfoConnettore(boolean missingInfoConnettore) {
		this.missingInfoConnettore = missingInfoConnettore;
	}
	public boolean isMissingInfoCredenziali() {
		return this.missingInfoCredenziali;
	}
	public void setMissingInfoCredenziali(boolean missingInfoCredenziali) {
		this.missingInfoCredenziali = missingInfoCredenziali;
	}
	public boolean isMissingInfoInput() {
		return this.missingInfoInput;
	}
	public void setMissingInfoInput(boolean missingInfoInput) {
		this.missingInfoInput = missingInfoInput;
	}
	public Input getMissingInfoInputObject() {
		return this.missingInfoInputObject;
	}
	public void setMissingInfoInputObject(Input missingInfoInputObject) {
		this.missingInfoInputObject = missingInfoInputObject;
	}
}
