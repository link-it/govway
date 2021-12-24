/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiFatturaRettificataType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiFatturaRettificataTypeModel extends AbstractModel<DatiFatturaRettificataType> {

	public DatiFatturaRettificataTypeModel(){
	
		super();
	
		this.NUMERO_FR = new Field("NumeroFR",java.lang.String.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
		this.DATA_FR = new Field("DataFR",java.util.Date.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
		this.ELEMENTI_RETTIFICATI = new Field("ElementiRettificati",java.lang.String.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
	
	}
	
	public DatiFatturaRettificataTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_FR = new ComplexField(father,"NumeroFR",java.lang.String.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
		this.DATA_FR = new ComplexField(father,"DataFR",java.util.Date.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
		this.ELEMENTI_RETTIFICATI = new ComplexField(father,"ElementiRettificati",java.lang.String.class,"DatiFatturaRettificataType",DatiFatturaRettificataType.class);
	
	}
	
	

	public IField NUMERO_FR = null;
	 
	public IField DATA_FR = null;
	 
	public IField ELEMENTI_RETTIFICATI = null;
	 

	@Override
	public Class<DatiFatturaRettificataType> getModeledClass(){
		return DatiFatturaRettificataType.class;
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