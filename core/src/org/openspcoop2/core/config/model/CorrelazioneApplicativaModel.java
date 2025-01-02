/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.CorrelazioneApplicativa;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CorrelazioneApplicativa 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorrelazioneApplicativaModel extends AbstractModel<CorrelazioneApplicativa> {

	public CorrelazioneApplicativaModel(){
	
		super();
	
		this.ELEMENTO = new org.openspcoop2.core.config.model.CorrelazioneApplicativaElementoModel(new Field("elemento",org.openspcoop2.core.config.CorrelazioneApplicativaElemento.class,"correlazione-applicativa",CorrelazioneApplicativa.class));
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"correlazione-applicativa",CorrelazioneApplicativa.class);
	
	}
	
	public CorrelazioneApplicativaModel(IField father){
	
		super(father);
	
		this.ELEMENTO = new org.openspcoop2.core.config.model.CorrelazioneApplicativaElementoModel(new ComplexField(father,"elemento",org.openspcoop2.core.config.CorrelazioneApplicativaElemento.class,"correlazione-applicativa",CorrelazioneApplicativa.class));
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"correlazione-applicativa",CorrelazioneApplicativa.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.CorrelazioneApplicativaElementoModel ELEMENTO = null;
	 
	public IField SCADENZA = null;
	 

	@Override
	public Class<CorrelazioneApplicativa> getModeledClass(){
		return CorrelazioneApplicativa.class;
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