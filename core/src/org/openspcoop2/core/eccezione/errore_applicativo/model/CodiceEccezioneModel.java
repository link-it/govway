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
package org.openspcoop2.core.eccezione.errore_applicativo.model;

import org.openspcoop2.core.eccezione.errore_applicativo.CodiceEccezione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CodiceEccezione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CodiceEccezioneModel extends AbstractModel<CodiceEccezione> {

	public CodiceEccezioneModel(){
	
		super();
	
		this.BASE = new Field("base",java.lang.String.class,"CodiceEccezione",CodiceEccezione.class);
		this.TYPE = new Field("type",java.lang.Integer.class,"CodiceEccezione",CodiceEccezione.class);
		this.SUBTYPE = new Field("subtype",java.lang.Integer.class,"CodiceEccezione",CodiceEccezione.class);
	
	}
	
	public CodiceEccezioneModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",java.lang.String.class,"CodiceEccezione",CodiceEccezione.class);
		this.TYPE = new ComplexField(father,"type",java.lang.Integer.class,"CodiceEccezione",CodiceEccezione.class);
		this.SUBTYPE = new ComplexField(father,"subtype",java.lang.Integer.class,"CodiceEccezione",CodiceEccezione.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField TYPE = null;
	 
	public IField SUBTYPE = null;
	 

	@Override
	public Class<CodiceEccezione> getModeledClass(){
		return CodiceEccezione.class;
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