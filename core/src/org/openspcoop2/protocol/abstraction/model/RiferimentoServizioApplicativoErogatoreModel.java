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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoServizioApplicativoErogatore 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoServizioApplicativoErogatoreModel extends AbstractModel<RiferimentoServizioApplicativoErogatore> {

	public RiferimentoServizioApplicativoErogatoreModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class);
		this.NOME_PORTA_APPLICATIVA = new Field("nome-porta-applicativa",java.lang.String.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class);
		this.DATI_APPLICATIVI = new org.openspcoop2.protocol.abstraction.model.DatiApplicativiErogazioneModel(new Field("dati-applicativi",org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class));
	
	}
	
	public RiferimentoServizioApplicativoErogatoreModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class);
		this.NOME_PORTA_APPLICATIVA = new ComplexField(father,"nome-porta-applicativa",java.lang.String.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class);
		this.DATI_APPLICATIVI = new org.openspcoop2.protocol.abstraction.model.DatiApplicativiErogazioneModel(new ComplexField(father,"dati-applicativi",org.openspcoop2.protocol.abstraction.DatiApplicativiErogazione.class,"RiferimentoServizioApplicativoErogatore",RiferimentoServizioApplicativoErogatore.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField NOME_PORTA_APPLICATIVA = null;
	 
	public org.openspcoop2.protocol.abstraction.model.DatiApplicativiErogazioneModel DATI_APPLICATIVI = null;
	 

	@Override
	public Class<RiferimentoServizioApplicativoErogatore> getModeledClass(){
		return RiferimentoServizioApplicativoErogatore.class;
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