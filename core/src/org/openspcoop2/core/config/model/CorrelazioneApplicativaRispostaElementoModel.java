/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CorrelazioneApplicativaRispostaElemento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorrelazioneApplicativaRispostaElementoModel extends AbstractModel<CorrelazioneApplicativaRispostaElemento> {

	public CorrelazioneApplicativaRispostaElementoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.IDENTIFICAZIONE = new Field("identificazione",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.IDENTIFICAZIONE_FALLITA = new Field("identificazione-fallita",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
	
	}
	
	public CorrelazioneApplicativaRispostaElementoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.IDENTIFICAZIONE = new ComplexField(father,"identificazione",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
		this.IDENTIFICAZIONE_FALLITA = new ComplexField(father,"identificazione-fallita",java.lang.String.class,"correlazione-applicativa-risposta-elemento",CorrelazioneApplicativaRispostaElemento.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField IDENTIFICAZIONE = null;
	 
	public IField PATTERN = null;
	 
	public IField IDENTIFICAZIONE_FALLITA = null;
	 

	@Override
	public Class<CorrelazioneApplicativaRispostaElemento> getModeledClass(){
		return CorrelazioneApplicativaRispostaElemento.class;
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