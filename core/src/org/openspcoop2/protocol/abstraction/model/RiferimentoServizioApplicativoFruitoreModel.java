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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoFruitore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoServizioApplicativoFruitore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoServizioApplicativoFruitoreModel extends AbstractModel<RiferimentoServizioApplicativoFruitore> {

	public RiferimentoServizioApplicativoFruitoreModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class);
		this.NOME_PORTA_DELEGATA = new Field("nome-porta-delegata",java.lang.String.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class);
		this.DATI_APPLICATIVI = new org.openspcoop2.protocol.abstraction.model.DatiApplicativiFruizioneModel(new Field("dati-applicativi",org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class));
	
	}
	
	public RiferimentoServizioApplicativoFruitoreModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class);
		this.NOME_PORTA_DELEGATA = new ComplexField(father,"nome-porta-delegata",java.lang.String.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class);
		this.DATI_APPLICATIVI = new org.openspcoop2.protocol.abstraction.model.DatiApplicativiFruizioneModel(new ComplexField(father,"dati-applicativi",org.openspcoop2.protocol.abstraction.DatiApplicativiFruizione.class,"RiferimentoServizioApplicativoFruitore",RiferimentoServizioApplicativoFruitore.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField NOME_PORTA_DELEGATA = null;
	 
	public org.openspcoop2.protocol.abstraction.model.DatiApplicativiFruizioneModel DATI_APPLICATIVI = null;
	 

	@Override
	public Class<RiferimentoServizioApplicativoFruitore> getModeledClass(){
		return RiferimentoServizioApplicativoFruitore.class;
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