/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.core.config.RoutingTable;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RoutingTable 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RoutingTableModel extends AbstractModel<RoutingTable> {

	public RoutingTableModel(){
	
		super();
	
		this.DESTINAZIONE = new org.openspcoop2.core.config.model.RoutingTableDestinazioneModel(new Field("destinazione",org.openspcoop2.core.config.RoutingTableDestinazione.class,"routing-table",RoutingTable.class));
		this.DEFAULT = new org.openspcoop2.core.config.model.RoutingTableDefaultModel(new Field("default",org.openspcoop2.core.config.RoutingTableDefault.class,"routing-table",RoutingTable.class));
		this.ABILITATA = new Field("abilitata",Boolean.class,"routing-table",RoutingTable.class);
	
	}
	
	public RoutingTableModel(IField father){
	
		super(father);
	
		this.DESTINAZIONE = new org.openspcoop2.core.config.model.RoutingTableDestinazioneModel(new ComplexField(father,"destinazione",org.openspcoop2.core.config.RoutingTableDestinazione.class,"routing-table",RoutingTable.class));
		this.DEFAULT = new org.openspcoop2.core.config.model.RoutingTableDefaultModel(new ComplexField(father,"default",org.openspcoop2.core.config.RoutingTableDefault.class,"routing-table",RoutingTable.class));
		this.ABILITATA = new ComplexField(father,"abilitata",Boolean.class,"routing-table",RoutingTable.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.RoutingTableDestinazioneModel DESTINAZIONE = null;
	 
	public org.openspcoop2.core.config.model.RoutingTableDefaultModel DEFAULT = null;
	 
	public IField ABILITATA = null;
	 

	@Override
	public Class<RoutingTable> getModeledClass(){
		return RoutingTable.class;
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