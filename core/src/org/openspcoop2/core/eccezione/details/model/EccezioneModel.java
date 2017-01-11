/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.details.model;

import org.openspcoop2.core.eccezione.details.Eccezione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Eccezione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneModel extends AbstractModel<Eccezione> {

	public EccezioneModel(){
	
		super();
	
		this.CODICE = new Field("codice",java.lang.String.class,"eccezione",Eccezione.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"eccezione",Eccezione.class);
		this.RILEVANZA = new Field("rilevanza",java.lang.String.class,"eccezione",Eccezione.class);
		this.CONTESTO_CODIFICA = new Field("contesto-codifica",java.lang.String.class,"eccezione",Eccezione.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	public EccezioneModel(IField father){
	
		super(father);
	
		this.CODICE = new ComplexField(father,"codice",java.lang.String.class,"eccezione",Eccezione.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"eccezione",Eccezione.class);
		this.RILEVANZA = new ComplexField(father,"rilevanza",java.lang.String.class,"eccezione",Eccezione.class);
		this.CONTESTO_CODIFICA = new ComplexField(father,"contesto-codifica",java.lang.String.class,"eccezione",Eccezione.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	

	public IField CODICE = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField RILEVANZA = null;
	 
	public IField CONTESTO_CODIFICA = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<Eccezione> getModeledClass(){
		return Eccezione.class;
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