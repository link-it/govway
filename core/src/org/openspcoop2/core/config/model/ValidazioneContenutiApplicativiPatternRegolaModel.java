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

import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPatternRegola;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneContenutiApplicativiPatternRegola 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneContenutiApplicativiPatternRegolaModel extends AbstractModel<ValidazioneContenutiApplicativiPatternRegola> {

	public ValidazioneContenutiApplicativiPatternRegolaModel(){
	
		super();
	
		this.REGOLA = new Field("regola",java.lang.String.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.NOME = new Field("nome",java.lang.String.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.AND = new Field("and",boolean.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.NOT = new Field("not",boolean.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
	
	}
	
	public ValidazioneContenutiApplicativiPatternRegolaModel(IField father){
	
		super(father);
	
		this.REGOLA = new ComplexField(father,"regola",java.lang.String.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.AND = new ComplexField(father,"and",boolean.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"validazione-contenuti-applicativi-pattern-regola",ValidazioneContenutiApplicativiPatternRegola.class);
	
	}
	
	

	public IField REGOLA = null;
	 
	public IField NOME = null;
	 
	public IField AND = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<ValidazioneContenutiApplicativiPatternRegola> getModeledClass(){
		return ValidazioneContenutiApplicativiPatternRegola.class;
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