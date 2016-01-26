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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InvocazionePortaGestioneErrore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InvocazionePortaGestioneErroreModel extends AbstractModel<InvocazionePortaGestioneErrore> {

	public InvocazionePortaGestioneErroreModel(){
	
		super();
	
		this.FAULT = new Field("fault",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.FAULT_ACTOR = new Field("fault-actor",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.GENERIC_FAULT_CODE = new Field("generic-fault-code",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.PREFIX_FAULT_CODE = new Field("prefix-fault-code",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
	
	}
	
	public InvocazionePortaGestioneErroreModel(IField father){
	
		super(father);
	
		this.FAULT = new ComplexField(father,"fault",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.FAULT_ACTOR = new ComplexField(father,"fault-actor",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.GENERIC_FAULT_CODE = new ComplexField(father,"generic-fault-code",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
		this.PREFIX_FAULT_CODE = new ComplexField(father,"prefix-fault-code",java.lang.String.class,"invocazione-porta-gestione-errore",InvocazionePortaGestioneErrore.class);
	
	}
	
	

	public IField FAULT = null;
	 
	public IField FAULT_ACTOR = null;
	 
	public IField GENERIC_FAULT_CODE = null;
	 
	public IField PREFIX_FAULT_CODE = null;
	 

	@Override
	public Class<InvocazionePortaGestioneErrore> getModeledClass(){
		return InvocazionePortaGestioneErrore.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}