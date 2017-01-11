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
package org.openspcoop2.core.eccezione.router_details.model;

import org.openspcoop2.core.eccezione.router_details.DettaglioRouting;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DettaglioRouting 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DettaglioRoutingModel extends AbstractModel<DettaglioRouting> {

	public DettaglioRoutingModel(){
	
		super();
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.router_details.model.DominioModel(new Field("dominio",org.openspcoop2.core.eccezione.router_details.Dominio.class,"dettaglio-routing",DettaglioRouting.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"dettaglio-routing",DettaglioRouting.class);
		this.DETTAGLIO = new org.openspcoop2.core.eccezione.router_details.model.DettaglioModel(new Field("dettaglio",org.openspcoop2.core.eccezione.router_details.Dettaglio.class,"dettaglio-routing",DettaglioRouting.class));
	
	}
	
	public DettaglioRoutingModel(IField father){
	
		super(father);
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.router_details.model.DominioModel(new ComplexField(father,"dominio",org.openspcoop2.core.eccezione.router_details.Dominio.class,"dettaglio-routing",DettaglioRouting.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"dettaglio-routing",DettaglioRouting.class);
		this.DETTAGLIO = new org.openspcoop2.core.eccezione.router_details.model.DettaglioModel(new ComplexField(father,"dettaglio",org.openspcoop2.core.eccezione.router_details.Dettaglio.class,"dettaglio-routing",DettaglioRouting.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.router_details.model.DominioModel DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public org.openspcoop2.core.eccezione.router_details.model.DettaglioModel DETTAGLIO = null;
	 

	@Override
	public Class<DettaglioRouting> getModeledClass(){
		return DettaglioRouting.class;
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