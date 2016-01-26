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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.RegistroServizi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RegistroServizi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RegistroServiziModel extends AbstractModel<RegistroServizi> {

	public RegistroServiziModel(){
	
		super();
	
		this.SOGGETTI = new org.openspcoop2.protocol.manifest.model.SoggettiModel(new Field("soggetti",org.openspcoop2.protocol.manifest.Soggetti.class,"registroServizi",RegistroServizi.class));
		this.SERVIZI = new org.openspcoop2.protocol.manifest.model.ServiziModel(new Field("servizi",org.openspcoop2.protocol.manifest.Servizi.class,"registroServizi",RegistroServizi.class));
		this.VERSIONI = new org.openspcoop2.protocol.manifest.model.VersioniModel(new Field("versioni",org.openspcoop2.protocol.manifest.Versioni.class,"registroServizi",RegistroServizi.class));
	
	}
	
	public RegistroServiziModel(IField father){
	
		super(father);
	
		this.SOGGETTI = new org.openspcoop2.protocol.manifest.model.SoggettiModel(new ComplexField(father,"soggetti",org.openspcoop2.protocol.manifest.Soggetti.class,"registroServizi",RegistroServizi.class));
		this.SERVIZI = new org.openspcoop2.protocol.manifest.model.ServiziModel(new ComplexField(father,"servizi",org.openspcoop2.protocol.manifest.Servizi.class,"registroServizi",RegistroServizi.class));
		this.VERSIONI = new org.openspcoop2.protocol.manifest.model.VersioniModel(new ComplexField(father,"versioni",org.openspcoop2.protocol.manifest.Versioni.class,"registroServizi",RegistroServizi.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.SoggettiModel SOGGETTI = null;
	 
	public org.openspcoop2.protocol.manifest.model.ServiziModel SERVIZI = null;
	 
	public org.openspcoop2.protocol.manifest.model.VersioniModel VERSIONI = null;
	 

	@Override
	public Class<RegistroServizi> getModeledClass(){
		return RegistroServizi.class;
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