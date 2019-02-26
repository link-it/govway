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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.ElencoServiziComposti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ElencoServiziComposti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ElencoServiziCompostiModel extends AbstractModel<ElencoServiziComposti> {

	public ElencoServiziCompostiModel(){
	
		super();
	
		this.SERVIZIO_COMPOSTO = new Field("servizioComposto",java.net.URI.class,"ElencoServiziComposti",ElencoServiziComposti.class);
	
	}
	
	public ElencoServiziCompostiModel(IField father){
	
		super(father);
	
		this.SERVIZIO_COMPOSTO = new ComplexField(father,"servizioComposto",java.net.URI.class,"ElencoServiziComposti",ElencoServiziComposti.class);
	
	}
	
	

	public IField SERVIZIO_COMPOSTO = null;
	 

	@Override
	public Class<ElencoServiziComposti> getModeledClass(){
		return ElencoServiziComposti.class;
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