/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.RoutingTableDefault;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RoutingTableDefault 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RoutingTableDefaultModel extends AbstractModel<RoutingTableDefault> {

	public RoutingTableDefaultModel(){
	
		super();
	
		this.ROUTE = new org.openspcoop2.core.config.model.RouteModel(new Field("route",org.openspcoop2.core.config.Route.class,"routing-table-default",RoutingTableDefault.class));
	
	}
	
	public RoutingTableDefaultModel(IField father){
	
		super(father);
	
		this.ROUTE = new org.openspcoop2.core.config.model.RouteModel(new ComplexField(father,"route",org.openspcoop2.core.config.Route.class,"routing-table-default",RoutingTableDefault.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.RouteModel ROUTE = null;
	 

	@Override
	public Class<RoutingTableDefault> getModeledClass(){
		return RoutingTableDefault.class;
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