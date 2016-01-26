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

import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CorrelazioneApplicativaElemento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CorrelazioneApplicativaElementoModel extends AbstractModel<CorrelazioneApplicativaElemento> {

	public CorrelazioneApplicativaElementoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.IDENTIFICAZIONE = new Field("identificazione",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.IDENTIFICAZIONE_FALLITA = new Field("identificazione-fallita",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.RIUSO_IDENTIFICATIVO = new Field("riuso-identificativo",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
	
	}
	
	public CorrelazioneApplicativaElementoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.IDENTIFICAZIONE = new ComplexField(father,"identificazione",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.IDENTIFICAZIONE_FALLITA = new ComplexField(father,"identificazione-fallita",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
		this.RIUSO_IDENTIFICATIVO = new ComplexField(father,"riuso-identificativo",java.lang.String.class,"correlazione-applicativa-elemento",CorrelazioneApplicativaElemento.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField IDENTIFICAZIONE = null;
	 
	public IField PATTERN = null;
	 
	public IField IDENTIFICAZIONE_FALLITA = null;
	 
	public IField RIUSO_IDENTIFICATIVO = null;
	 

	@Override
	public Class<CorrelazioneApplicativaElemento> getModeledClass(){
		return CorrelazioneApplicativaElemento.class;
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