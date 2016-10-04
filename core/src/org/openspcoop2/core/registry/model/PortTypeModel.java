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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.PortType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortTypeModel extends AbstractModel<PortType> {

	public PortTypeModel(){
	
		super();
	
		this.AZIONE = new org.openspcoop2.core.registry.model.OperationModel(new Field("azione",org.openspcoop2.core.registry.Operation.class,"port-type",PortType.class));
		this.PROFILO_P_T = new Field("profilo-p-t",java.lang.String.class,"port-type",PortType.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"port-type",PortType.class);
		this.NOME = new Field("nome",java.lang.String.class,"port-type",PortType.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"port-type",PortType.class);
		this.STYLE = new Field("style",java.lang.String.class,"port-type",PortType.class);
		this.PROFILO_COLLABORAZIONE = new Field("profilo-collaborazione",java.lang.String.class,"port-type",PortType.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"port-type",PortType.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"port-type",PortType.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"port-type",PortType.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"port-type",PortType.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"port-type",PortType.class);
	
	}
	
	public PortTypeModel(IField father){
	
		super(father);
	
		this.AZIONE = new org.openspcoop2.core.registry.model.OperationModel(new ComplexField(father,"azione",org.openspcoop2.core.registry.Operation.class,"port-type",PortType.class));
		this.PROFILO_P_T = new ComplexField(father,"profilo-p-t",java.lang.String.class,"port-type",PortType.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"port-type",PortType.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"port-type",PortType.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"port-type",PortType.class);
		this.STYLE = new ComplexField(father,"style",java.lang.String.class,"port-type",PortType.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profilo-collaborazione",java.lang.String.class,"port-type",PortType.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"port-type",PortType.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"port-type",PortType.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"port-type",PortType.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"port-type",PortType.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"port-type",PortType.class);
	
	}
	
	

	public org.openspcoop2.core.registry.model.OperationModel AZIONE = null;
	 
	public IField PROFILO_P_T = null;
	 
	public IField ID_ACCORDO = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField STYLE = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 

	@Override
	public Class<PortType> getModeledClass(){
		return PortType.class;
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