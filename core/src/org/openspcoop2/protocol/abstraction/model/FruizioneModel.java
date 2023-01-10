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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.Fruizione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Fruizione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FruizioneModel extends AbstractModel<Fruizione> {

	public FruizioneModel(){
	
		super();
	
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteSpecificaModel(new Field("accordo-servizio-parte-specifica",org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica.class,"fruizione",Fruizione.class));
		this.SOGGETTO_FRUITORE = new org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel(new Field("soggetto-fruitore",org.openspcoop2.protocol.abstraction.RiferimentoSoggetto.class,"fruizione",Fruizione.class));
		this.FRUIZIONE = new org.openspcoop2.protocol.abstraction.model.DatiFruizioneModel(new Field("fruizione",org.openspcoop2.protocol.abstraction.DatiFruizione.class,"fruizione",Fruizione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoFruitoreModel(new Field("servizio-applicativo",org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore.class,"fruizione",Fruizione.class));
		this.TIPOLOGIA = new Field("tipologia",java.lang.String.class,"fruizione",Fruizione.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"fruizione",Fruizione.class);
	
	}
	
	public FruizioneModel(IField father){
	
		super(father);
	
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteSpecificaModel(new ComplexField(father,"accordo-servizio-parte-specifica",org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteSpecifica.class,"fruizione",Fruizione.class));
		this.SOGGETTO_FRUITORE = new org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel(new ComplexField(father,"soggetto-fruitore",org.openspcoop2.protocol.abstraction.RiferimentoSoggetto.class,"fruizione",Fruizione.class));
		this.FRUIZIONE = new org.openspcoop2.protocol.abstraction.model.DatiFruizioneModel(new ComplexField(father,"fruizione",org.openspcoop2.protocol.abstraction.DatiFruizione.class,"fruizione",Fruizione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoFruitoreModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore.class,"fruizione",Fruizione.class));
		this.TIPOLOGIA = new ComplexField(father,"tipologia",java.lang.String.class,"fruizione",Fruizione.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"fruizione",Fruizione.class);
	
	}
	
	

	public org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteSpecificaModel ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel SOGGETTO_FRUITORE = null;
	 
	public org.openspcoop2.protocol.abstraction.model.DatiFruizioneModel FRUIZIONE = null;
	 
	public org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoFruitoreModel SERVIZIO_APPLICATIVO = null;
	 
	public IField TIPOLOGIA = null;
	 
	public IField DESCRIZIONE = null;
	 

	@Override
	public Class<Fruizione> getModeledClass(){
		return Fruizione.class;
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