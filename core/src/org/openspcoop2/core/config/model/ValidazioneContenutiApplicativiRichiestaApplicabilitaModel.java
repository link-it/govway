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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiestaApplicabilita;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativiRichiestaApplicabilita 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiRichiestaApplicabilitaModel extends AbstractModel<ValidazioneContenutiApplicativiRichiestaApplicabilita> {

	public ValidazioneContenutiApplicativiRichiestaApplicabilitaModel(){
	
		super();
	
		this.AZIONE = new Field("azione",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
		this.MATCH = new Field("match",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
	
	}
	
	public ValidazioneContenutiApplicativiRichiestaApplicabilitaModel(IField father){
	
		super(father);
	
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
		this.MATCH = new ComplexField(father,"match",java.lang.String.class,"validazione-contenuti-applicativi-richiesta-applicabilita",ValidazioneContenutiApplicativiRichiestaApplicabilita.class);
	
	}
	
	

	public IField AZIONE = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField MATCH = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativiRichiestaApplicabilita> getModeledClass(){
		return ValidazioneContenutiApplicativiRichiestaApplicabilita.class;
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