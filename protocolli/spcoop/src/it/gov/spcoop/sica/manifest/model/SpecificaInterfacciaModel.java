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

import it.gov.spcoop.sica.manifest.SpecificaInterfaccia;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SpecificaInterfaccia 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SpecificaInterfacciaModel extends AbstractModel<SpecificaInterfaccia> {

	public SpecificaInterfacciaModel(){
	
		super();
	
		this.INTERFACCIA_CONCETTUALE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new Field("interfacciaConcettuale",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
		this.INTERFACCIA_LOGICA_LATO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new Field("interfacciaLogicaLatoErogatore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
		this.INTERFACCIA_LOGICA_LATO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new Field("interfacciaLogicaLatoFruitore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
	
	}
	
	public SpecificaInterfacciaModel(IField father){
	
		super(father);
	
		this.INTERFACCIA_CONCETTUALE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new ComplexField(father,"interfacciaConcettuale",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
		this.INTERFACCIA_LOGICA_LATO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new ComplexField(father,"interfacciaLogicaLatoErogatore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
		this.INTERFACCIA_LOGICA_LATO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new ComplexField(father,"interfacciaLogicaLatoFruitore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaInterfaccia",SpecificaInterfaccia.class));
	
	}
	
	

	public it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel INTERFACCIA_CONCETTUALE = null;
	 
	public it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel INTERFACCIA_LOGICA_LATO_EROGATORE = null;
	 
	public it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel INTERFACCIA_LOGICA_LATO_FRUITORE = null;
	 

	@Override
	public Class<SpecificaInterfaccia> getModeledClass(){
		return SpecificaInterfaccia.class;
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