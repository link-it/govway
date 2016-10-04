/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.core.config.GestioneErroreSoapFault;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GestioneErroreSoapFault 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneErroreSoapFaultModel extends AbstractModel<GestioneErroreSoapFault> {

	public GestioneErroreSoapFaultModel(){
	
		super();
	
		this.FAULT_ACTOR = new Field("fault-actor",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.FAULT_CODE = new Field("fault-code",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.FAULT_STRING = new Field("fault-string",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.COMPORTAMENTO = new Field("comportamento",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.CADENZA_RISPEDIZIONE = new Field("cadenza-rispedizione",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
	
	}
	
	public GestioneErroreSoapFaultModel(IField father){
	
		super(father);
	
		this.FAULT_ACTOR = new ComplexField(father,"fault-actor",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.FAULT_CODE = new ComplexField(father,"fault-code",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.FAULT_STRING = new ComplexField(father,"fault-string",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.COMPORTAMENTO = new ComplexField(father,"comportamento",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
		this.CADENZA_RISPEDIZIONE = new ComplexField(father,"cadenza-rispedizione",java.lang.String.class,"gestione-errore-soap-fault",GestioneErroreSoapFault.class);
	
	}
	
	

	public IField FAULT_ACTOR = null;
	 
	public IField FAULT_CODE = null;
	 
	public IField FAULT_STRING = null;
	 
	public IField COMPORTAMENTO = null;
	 
	public IField CADENZA_RISPEDIZIONE = null;
	 

	@Override
	public Class<GestioneErroreSoapFault> getModeledClass(){
		return GestioneErroreSoapFault.class;
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