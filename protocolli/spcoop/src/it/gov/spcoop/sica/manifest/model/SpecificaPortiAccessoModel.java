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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.SpecificaPortiAccesso;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SpecificaPortiAccesso 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SpecificaPortiAccessoModel extends AbstractModel<SpecificaPortiAccesso> {

	public SpecificaPortiAccessoModel(){
	
		super();
	
		this.PORTI_ACCESSO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new Field("portiAccessoFruitore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaPortiAccesso",SpecificaPortiAccesso.class));
		this.PORTI_ACCESSO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new Field("portiAccessoErogatore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaPortiAccesso",SpecificaPortiAccesso.class));
	
	}
	
	public SpecificaPortiAccessoModel(IField father){
	
		super(father);
	
		this.PORTI_ACCESSO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new ComplexField(father,"portiAccessoFruitore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaPortiAccesso",SpecificaPortiAccesso.class));
		this.PORTI_ACCESSO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel(new ComplexField(father,"portiAccessoErogatore",it.gov.spcoop.sica.manifest.DocumentoInterfaccia.class,"SpecificaPortiAccesso",SpecificaPortiAccesso.class));
	
	}
	
	

	public it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel PORTI_ACCESSO_FRUITORE = null;
	 
	public it.gov.spcoop.sica.manifest.model.DocumentoInterfacciaModel PORTI_ACCESSO_EROGATORE = null;
	 

	@Override
	public Class<SpecificaPortiAccesso> getModeledClass(){
		return SpecificaPortiAccesso.class;
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