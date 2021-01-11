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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.Erogazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Erogazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioneModel extends AbstractModel<Erogazione> {

	public ErogazioneModel(){
	
		super();
	
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteComuneModel(new Field("accordo-servizio-parte-comune",org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune.class,"erogazione",Erogazione.class));
		this.SOGGETTO_EROGATORE = new org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel(new Field("soggetto-erogatore",org.openspcoop2.protocol.abstraction.RiferimentoSoggetto.class,"erogazione",Erogazione.class));
		this.SERVIZIO = new org.openspcoop2.protocol.abstraction.model.DatiServizioModel(new Field("servizio",org.openspcoop2.protocol.abstraction.DatiServizio.class,"erogazione",Erogazione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoErogatoreModel(new Field("servizio-applicativo",org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore.class,"erogazione",Erogazione.class));
		this.TIPOLOGIA = new Field("tipologia",java.lang.String.class,"erogazione",Erogazione.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"erogazione",Erogazione.class);
	
	}
	
	public ErogazioneModel(IField father){
	
		super(father);
	
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteComuneModel(new ComplexField(father,"accordo-servizio-parte-comune",org.openspcoop2.protocol.abstraction.RiferimentoAccordoServizioParteComune.class,"erogazione",Erogazione.class));
		this.SOGGETTO_EROGATORE = new org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel(new ComplexField(father,"soggetto-erogatore",org.openspcoop2.protocol.abstraction.RiferimentoSoggetto.class,"erogazione",Erogazione.class));
		this.SERVIZIO = new org.openspcoop2.protocol.abstraction.model.DatiServizioModel(new ComplexField(father,"servizio",org.openspcoop2.protocol.abstraction.DatiServizio.class,"erogazione",Erogazione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoErogatoreModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.protocol.abstraction.RiferimentoServizioApplicativoErogatore.class,"erogazione",Erogazione.class));
		this.TIPOLOGIA = new ComplexField(father,"tipologia",java.lang.String.class,"erogazione",Erogazione.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"erogazione",Erogazione.class);
	
	}
	
	

	public org.openspcoop2.protocol.abstraction.model.RiferimentoAccordoServizioParteComuneModel ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 
	public org.openspcoop2.protocol.abstraction.model.RiferimentoSoggettoModel SOGGETTO_EROGATORE = null;
	 
	public org.openspcoop2.protocol.abstraction.model.DatiServizioModel SERVIZIO = null;
	 
	public org.openspcoop2.protocol.abstraction.model.RiferimentoServizioApplicativoErogatoreModel SERVIZIO_APPLICATIVO = null;
	 
	public IField TIPOLOGIA = null;
	 
	public IField DESCRIZIONE = null;
	 

	@Override
	public Class<Erogazione> getModeledClass(){
		return Erogazione.class;
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