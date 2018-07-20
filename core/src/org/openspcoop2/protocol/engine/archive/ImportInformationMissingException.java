/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine.archive;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.protocol.information_missing.Input;
import org.openspcoop2.protocol.information_missing.Description;
import org.openspcoop2.protocol.information_missing.Default;

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

	private String missingInfoProtocollo;
	
	private boolean missingInfoSoggetto;
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
	
	private Description missingInfoHeader;
	private Description missingInfoFooter;
	private Default missingInfoDefault;
	
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
	
	public String getMissingInfoProtocollo() {
		return this.missingInfoProtocollo;
	}
	public void setMissingInfoProtocollo(String missingInfoProtocollo) {
		this.missingInfoProtocollo = missingInfoProtocollo;
	}
	
	public boolean isMissingInfoSoggetto() {
		return this.missingInfoSoggetto;
	}
	public void setMissingInfoSoggetto(boolean missingInfoSoggetto) {
		this.missingInfoSoggetto = missingInfoSoggetto;
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
	public Description getMissingInfoHeader() {
		return this.missingInfoHeader;
	}
	public void setMissingInfoHeader(Description missingInfoHeader) {
		this.missingInfoHeader = missingInfoHeader;
	}
	public Description getMissingInfoFooter() {
		return this.missingInfoFooter;
	}
	public void setMissingInfoFooter(Description missingInfoFooter) {
		this.missingInfoFooter = missingInfoFooter;
	}
	public Default getMissingInfoDefault() {
		return this.missingInfoDefault;
	}
	public void setMissingInfoDefault(Default missingInfoDefault) {
		this.missingInfoDefault = missingInfoDefault;
	}
}
