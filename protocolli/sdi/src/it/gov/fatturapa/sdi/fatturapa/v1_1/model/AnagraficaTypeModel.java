/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.AnagraficaType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AnagraficaType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AnagraficaTypeModel extends AbstractModel<AnagraficaType> {

	public AnagraficaTypeModel(){
	
		super();
	
		this.DENOMINAZIONE = new Field("Denominazione",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.NOME = new Field("Nome",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.COGNOME = new Field("Cognome",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.TITOLO = new Field("Titolo",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.COD_EORI = new Field("CodEORI",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
	
	}
	
	public AnagraficaTypeModel(IField father){
	
		super(father);
	
		this.DENOMINAZIONE = new ComplexField(father,"Denominazione",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.NOME = new ComplexField(father,"Nome",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.COGNOME = new ComplexField(father,"Cognome",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.TITOLO = new ComplexField(father,"Titolo",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
		this.COD_EORI = new ComplexField(father,"CodEORI",java.lang.String.class,"AnagraficaType",AnagraficaType.class);
	
	}
	
	

	public IField DENOMINAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField COGNOME = null;
	 
	public IField TITOLO = null;
	 
	public IField COD_EORI = null;
	 

	@Override
	public Class<AnagraficaType> getModeledClass(){
		return AnagraficaType.class;
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