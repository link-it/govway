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

import org.openspcoop2.core.config.Route;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Route 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RouteModel extends AbstractModel<Route> {

	public RouteModel(){
	
		super();
	
		this.REGISTRO = new org.openspcoop2.core.config.model.RouteRegistroModel(new Field("registro",org.openspcoop2.core.config.RouteRegistro.class,"route",Route.class));
		this.GATEWAY = new org.openspcoop2.core.config.model.RouteGatewayModel(new Field("gateway",org.openspcoop2.core.config.RouteGateway.class,"route",Route.class));
	
	}
	
	public RouteModel(IField father){
	
		super(father);
	
		this.REGISTRO = new org.openspcoop2.core.config.model.RouteRegistroModel(new ComplexField(father,"registro",org.openspcoop2.core.config.RouteRegistro.class,"route",Route.class));
		this.GATEWAY = new org.openspcoop2.core.config.model.RouteGatewayModel(new ComplexField(father,"gateway",org.openspcoop2.core.config.RouteGateway.class,"route",Route.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.RouteRegistroModel REGISTRO = null;
	 
	public org.openspcoop2.core.config.model.RouteGatewayModel GATEWAY = null;
	 

	@Override
	public Class<Route> getModeledClass(){
		return Route.class;
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