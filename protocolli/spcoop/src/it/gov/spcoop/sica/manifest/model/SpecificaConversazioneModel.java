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

import it.gov.spcoop.sica.manifest.SpecificaConversazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SpecificaConversazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SpecificaConversazioneModel extends AbstractModel<SpecificaConversazione> {

	public SpecificaConversazioneModel(){
	
		super();
	
		this.CONVERSAZIONE_CONCETTUALE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new Field("conversazioneConcettuale",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
		this.CONVERSAZIONE_LOGICA_LATO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new Field("conversazioneLogicaLatoFruitore",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
		this.CONVERSAZIONE_LOGICA_LATO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new Field("conversazioneLogicaLatoErogatore",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
	
	}
	
	public SpecificaConversazioneModel(IField father){
	
		super(father);
	
		this.CONVERSAZIONE_CONCETTUALE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new ComplexField(father,"conversazioneConcettuale",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
		this.CONVERSAZIONE_LOGICA_LATO_FRUITORE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new ComplexField(father,"conversazioneLogicaLatoFruitore",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
		this.CONVERSAZIONE_LOGICA_LATO_EROGATORE = new it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel(new ComplexField(father,"conversazioneLogicaLatoErogatore",it.gov.spcoop.sica.manifest.DocumentoConversazione.class,"SpecificaConversazione",SpecificaConversazione.class));
	
	}
	
	

	public it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel CONVERSAZIONE_CONCETTUALE = null;
	 
	public it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel CONVERSAZIONE_LOGICA_LATO_FRUITORE = null;
	 
	public it.gov.spcoop.sica.manifest.model.DocumentoConversazioneModel CONVERSAZIONE_LOGICA_LATO_EROGATORE = null;
	 

	@Override
	public Class<SpecificaConversazione> getModeledClass(){
		return SpecificaConversazione.class;
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