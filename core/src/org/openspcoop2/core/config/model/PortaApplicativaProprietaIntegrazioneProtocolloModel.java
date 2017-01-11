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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaApplicativaProprietaIntegrazioneProtocollo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaProprietaIntegrazioneProtocollo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaProprietaIntegrazioneProtocolloModel extends AbstractModel<PortaApplicativaProprietaIntegrazioneProtocollo> {

	public PortaApplicativaProprietaIntegrazioneProtocolloModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-proprieta-integrazione-protocollo",PortaApplicativaProprietaIntegrazioneProtocollo.class);
		this.VALORE = new Field("valore",java.lang.String.class,"porta-applicativa-proprieta-integrazione-protocollo",PortaApplicativaProprietaIntegrazioneProtocollo.class);
	
	}
	
	public PortaApplicativaProprietaIntegrazioneProtocolloModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-proprieta-integrazione-protocollo",PortaApplicativaProprietaIntegrazioneProtocollo.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"porta-applicativa-proprieta-integrazione-protocollo",PortaApplicativaProprietaIntegrazioneProtocollo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField VALORE = null;
	 

	@Override
	public Class<PortaApplicativaProprietaIntegrazioneProtocollo> getModeledClass(){
		return PortaApplicativaProprietaIntegrazioneProtocollo.class;
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