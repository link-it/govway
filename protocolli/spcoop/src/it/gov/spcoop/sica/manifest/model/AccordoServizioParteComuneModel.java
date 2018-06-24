/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import it.gov.spcoop.sica.manifest.AccordoServizioParteComune;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComune 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneModel extends AbstractModel<AccordoServizioParteComune> {

	public AccordoServizioParteComuneModel(){
	
		super();
	
		this.SPECIFICA_INTERFACCIA = new it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel(new Field("specificaInterfaccia",it.gov.spcoop.sica.manifest.SpecificaInterfaccia.class,"accordoServizioParteComune",AccordoServizioParteComune.class));
		this.SPECIFICA_CONVERSAZIONE = new it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel(new Field("specificaConversazione",it.gov.spcoop.sica.manifest.SpecificaConversazione.class,"accordoServizioParteComune",AccordoServizioParteComune.class));
		this.PUBBLICATORE = new Field("pubblicatore",java.net.URI.class,"accordoServizioParteComune",AccordoServizioParteComune.class);
	
	}
	
	public AccordoServizioParteComuneModel(IField father){
	
		super(father);
	
		this.SPECIFICA_INTERFACCIA = new it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel(new ComplexField(father,"specificaInterfaccia",it.gov.spcoop.sica.manifest.SpecificaInterfaccia.class,"accordoServizioParteComune",AccordoServizioParteComune.class));
		this.SPECIFICA_CONVERSAZIONE = new it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel(new ComplexField(father,"specificaConversazione",it.gov.spcoop.sica.manifest.SpecificaConversazione.class,"accordoServizioParteComune",AccordoServizioParteComune.class));
		this.PUBBLICATORE = new ComplexField(father,"pubblicatore",java.net.URI.class,"accordoServizioParteComune",AccordoServizioParteComune.class);
	
	}
	
	

	public it.gov.spcoop.sica.manifest.model.SpecificaInterfacciaModel SPECIFICA_INTERFACCIA = null;
	 
	public it.gov.spcoop.sica.manifest.model.SpecificaConversazioneModel SPECIFICA_CONVERSAZIONE = null;
	 
	public IField PUBBLICATORE = null;
	 

	@Override
	public Class<AccordoServizioParteComune> getModeledClass(){
		return AccordoServizioParteComune.class;
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