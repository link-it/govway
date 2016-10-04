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

import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiModel extends AbstractModel<ValidazioneContenutiApplicativi> {

	public ValidazioneContenutiApplicativiModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
		this.ACCEPT_MTOM_MESSAGE = new Field("accept-mtom-message",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
	
	}
	
	public ValidazioneContenutiApplicativiModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
		this.ACCEPT_MTOM_MESSAGE = new ComplexField(father,"accept-mtom-message",java.lang.String.class,"validazione-contenuti-applicativi",ValidazioneContenutiApplicativi.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField TIPO = null;
	 
	public IField ACCEPT_MTOM_MESSAGE = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativi> getModeledClass(){
		return ValidazioneContenutiApplicativi.class;
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