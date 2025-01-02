/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RiferimentoArchivioType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoArchivioType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoArchivioTypeModel extends AbstractModel<RiferimentoArchivioType> {

	public RiferimentoArchivioTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.String.class,"RiferimentoArchivio_Type",RiferimentoArchivioType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"RiferimentoArchivio_Type",RiferimentoArchivioType.class);
	
	}
	
	public RiferimentoArchivioTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.String.class,"RiferimentoArchivio_Type",RiferimentoArchivioType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"RiferimentoArchivio_Type",RiferimentoArchivioType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 

	@Override
	public Class<RiferimentoArchivioType> getModeledClass(){
		return RiferimentoArchivioType.class;
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