/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.TrasformazioneRegolaParametro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaParametro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaParametroModel extends AbstractModel<TrasformazioneRegolaParametro> {

	public TrasformazioneRegolaParametroModel(){
	
		super();
	
		this.CONVERSIONE_TIPO = new Field("conversione-tipo",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.NOME = new Field("nome",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.VALORE = new Field("valore",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.IDENTIFICAZIONE_FALLITA = new Field("identificazione-fallita",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
	
	}
	
	public TrasformazioneRegolaParametroModel(IField father){
	
		super(father);
	
		this.CONVERSIONE_TIPO = new ComplexField(father,"conversione-tipo",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
		this.IDENTIFICAZIONE_FALLITA = new ComplexField(father,"identificazione-fallita",java.lang.String.class,"trasformazione-regola-parametro",TrasformazioneRegolaParametro.class);
	
	}
	
	

	public IField CONVERSIONE_TIPO = null;
	 
	public IField NOME = null;
	 
	public IField VALORE = null;
	 
	public IField IDENTIFICAZIONE_FALLITA = null;
	 

	@Override
	public Class<TrasformazioneRegolaParametro> getModeledClass(){
		return TrasformazioneRegolaParametro.class;
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