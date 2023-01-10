/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
	
		this.CODE = new Field("code",java.lang.String.class,"eccezione",Eccezione.class);
		this.DESCRIPTION = new Field("description",java.lang.String.class,"eccezione",Eccezione.class);
		this.SEVERITY = new Field("severity",java.lang.String.class,"eccezione",Eccezione.class);
		this.CONTEXT = new Field("context",java.lang.String.class,"eccezione",Eccezione.class);
		this.TYPE = new Field("type",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	public EccezioneModel(IField father){
	
		super(father);
	
		this.CODE = new ComplexField(father,"code",java.lang.String.class,"eccezione",Eccezione.class);
		this.DESCRIPTION = new ComplexField(father,"description",java.lang.String.class,"eccezione",Eccezione.class);
		this.SEVERITY = new ComplexField(father,"severity",java.lang.String.class,"eccezione",Eccezione.class);
		this.CONTEXT = new ComplexField(father,"context",java.lang.String.class,"eccezione",Eccezione.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"eccezione",Eccezione.class);
	
	}
	
	

	public IField CODE = null;
	 
	public IField DESCRIPTION = null;
	 
	public IField SEVERITY = null;
	 
	public IField CONTEXT = null;
	 
	public IField TYPE = null;
	 

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