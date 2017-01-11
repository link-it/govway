/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.errore_applicativo.model;

import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErroreApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroreApplicativoModel extends AbstractModel<ErroreApplicativo> {

	public ErroreApplicativoModel(){
	
		super();
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel(new Field("dominio",org.openspcoop2.core.eccezione.errore_applicativo.Dominio.class,"errore-applicativo",ErroreApplicativo.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"errore-applicativo",ErroreApplicativo.class);
		this.DATI_COOPERAZIONE = new org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel(new Field("dati-cooperazione",org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione.class,"errore-applicativo",ErroreApplicativo.class));
		this.ECCEZIONE = new org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel(new Field("eccezione",org.openspcoop2.core.eccezione.errore_applicativo.Eccezione.class,"errore-applicativo",ErroreApplicativo.class));
	
	}
	
	public ErroreApplicativoModel(IField father){
	
		super(father);
	
		this.DOMINIO = new org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel(new ComplexField(father,"dominio",org.openspcoop2.core.eccezione.errore_applicativo.Dominio.class,"errore-applicativo",ErroreApplicativo.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"errore-applicativo",ErroreApplicativo.class);
		this.DATI_COOPERAZIONE = new org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel(new ComplexField(father,"dati-cooperazione",org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione.class,"errore-applicativo",ErroreApplicativo.class));
		this.ECCEZIONE = new org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel(new ComplexField(father,"eccezione",org.openspcoop2.core.eccezione.errore_applicativo.Eccezione.class,"errore-applicativo",ErroreApplicativo.class));
	
	}
	
	

	public org.openspcoop2.core.eccezione.errore_applicativo.model.DominioModel DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.DatiCooperazioneModel DATI_COOPERAZIONE = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.EccezioneModel ECCEZIONE = null;
	 

	@Override
	public Class<ErroreApplicativo> getModeledClass(){
		return ErroreApplicativo.class;
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