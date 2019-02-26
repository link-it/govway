/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiApplicativiErogazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiApplicativiErogazioneModel extends AbstractModel<DatiApplicativiErogazione> {

	public DatiApplicativiErogazioneModel(){
	
		super();
	
		this.ENDPOINT = new Field("endpoint",java.lang.String.class,"DatiApplicativiErogazione",DatiApplicativiErogazione.class);
		this.CREDENZIALI_BASIC = new org.openspcoop2.protocol.abstraction.model.CredenzialiInvocazioneBasicModel(new Field("credenziali-basic",org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic.class,"DatiApplicativiErogazione",DatiApplicativiErogazione.class));
	
	}
	
	public DatiApplicativiErogazioneModel(IField father){
	
		super(father);
	
		this.ENDPOINT = new ComplexField(father,"endpoint",java.lang.String.class,"DatiApplicativiErogazione",DatiApplicativiErogazione.class);
		this.CREDENZIALI_BASIC = new org.openspcoop2.protocol.abstraction.model.CredenzialiInvocazioneBasicModel(new ComplexField(father,"credenziali-basic",org.openspcoop2.protocol.abstraction.CredenzialiInvocazioneBasic.class,"DatiApplicativiErogazione",DatiApplicativiErogazione.class));
	
	}
	
	

	public IField ENDPOINT = null;
	 
	public org.openspcoop2.protocol.abstraction.model.CredenzialiInvocazioneBasicModel CREDENZIALI_BASIC = null;
	 

	@Override
	public Class<DatiApplicativiErogazione> getModeledClass(){
		return DatiApplicativiErogazione.class;
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