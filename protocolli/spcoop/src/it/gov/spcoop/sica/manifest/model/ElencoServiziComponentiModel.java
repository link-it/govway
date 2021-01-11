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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ElencoServiziComponenti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ElencoServiziComponentiModel extends AbstractModel<ElencoServiziComponenti> {

	public ElencoServiziComponentiModel(){
	
		super();
	
		this.SERVIZIO_COMPONENTE = new Field("servizioComponente",java.net.URI.class,"ElencoServiziComponenti",ElencoServiziComponenti.class);
	
	}
	
	public ElencoServiziComponentiModel(IField father){
	
		super(father);
	
		this.SERVIZIO_COMPONENTE = new ComplexField(father,"servizioComponente",java.net.URI.class,"ElencoServiziComponenti",ElencoServiziComponenti.class);
	
	}
	
	

	public IField SERVIZIO_COMPONENTE = null;
	 

	@Override
	public Class<ElencoServiziComponenti> getModeledClass(){
		return ElencoServiziComponenti.class;
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